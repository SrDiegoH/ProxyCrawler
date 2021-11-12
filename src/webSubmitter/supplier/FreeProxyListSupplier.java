package webSubmitter.supplier;

import model.Local;
import model.Proxy;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.ConstantUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static util.JsoupHelper.requestGet;
import static util.MethodsUtils.getSubstring;
import static util.MethodsUtils.isEmpty;

public class FreeProxyListSupplier implements ISupplier {

	private Map<String, String> basicHeaders;

	public FreeProxyListSupplier() {
		this.basicHeaders = new HashMap<>();
		this.basicHeaders.put("Host", "www.freeproxylists.net");
		this.basicHeaders.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:64.0) Gecko/20100101 Firefox/64.0");
		this.basicHeaders.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		this.basicHeaders.put("Accept-Language", "pt-BR,pt;q=0.8,en-US;q=0.5,en;q=0.3");
		this.basicHeaders.put("Accept-Encoding", "gzip, deflate, br");
		this.basicHeaders.put("Connection", "close");
		this.basicHeaders.put("Upgrade-Insecure-Requests", "1");
		this.basicHeaders.put("TE", "Trailers");
	}

	public List<Local> listPlaces(final String origin){
		try {
			final Document html = requestGet("http://www.freeproxylists.net/country.html", basicHeaders);

			return Optional.ofNullable(html.select(".DataGrid > tbody > tr"))
				.stream()
				.flatMap(elements -> elements.stream())
				.map(this::getLocal)
				.filter(actualLocalOption -> !actualLocalOption.isEmpty())
				.map(optionalActualLocal -> optionalActualLocal.get())
				.collect(Collectors.toList());
		} catch (final Exception exception){
			return List.of();
		}
	}

	private Optional<Local> getLocal(final Element actualLocal){
		try {
			final boolean isActualLocalNotValid = !actualLocal.toString().contains("<a");

			if(isActualLocalNotValid)
				throw new Exception();

			final String name = actualLocal.getElementsByTag("a").text();

			return Optional.ofNullable(new Local(name));
		} catch (final Exception exception) {
			return Optional.empty();
		}
	}

	public List<Proxy> listProxies(final String origin, final String protocol){
		try {
			final String url = urlBuilder("", protocol);

			final Document html = requestGet(url, basicHeaders);

			if(!origin.equals(ConstantUtils.NO_VALUE)) {
				return Optional
						.ofNullable(html
						.select("#form1 > table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(2) > td:nth-child(1) > select:nth-child(1)"))
						.map(elements -> elements.first().getElementsByTag("option"))
						.filter(option -> origin.contains(option.text()))
						.map(option -> getTableLines(option, protocol))
						.stream()
						.flatMap(proxies -> proxies.stream())
						.sorted((firstProxy, secondProxy) -> secondProxy.getSpeed().compareTo(firstProxy.getSpeed()))
						.collect(Collectors.toList());
			}
		} catch (final Exception exception) { }

		return List.of();
	}

	private String urlBuilder(final String optionValue, final String protocol) {
		final String validProtocol = protocol.equals(ConstantUtils.NO_VALUE)? "" : protocol;

		final StringBuilder getQuery = new StringBuilder("http://www.freeproxylists.net/?")
				.append("c=").append(optionValue).append("&")
				.append("pt=").append("&")
				.append("pr=").append(validProtocol).append("&")
				.append("a[]=2").append("&")
				.append("u=0");

		return getQuery.toString();
	}

	private List<Proxy> getTableLines(final Elements option, final String protocol){
		try {
			final String optionValue = option.attr("value");

			final String optionUrl = urlBuilder(optionValue, protocol);

			final Document html = requestGet(optionUrl, basicHeaders);

			return html
					.select(".DataGrid > tbody > tr")
					.stream()
					.map(tableLine -> getProxyData(tableLine))
					.filter(optionalProxy -> !optionalProxy.isEmpty())
					.map(proxy -> proxy.get())
					.collect(Collectors.toList());
		} catch (final Exception exception) {
			return List.of();
		}
	}

	private Optional<Proxy> getProxyData(final Element tableLine){
		try {
			final String anonymity = tableLine
					.select("td:nth-child(4)")
					.first()
					.text();

			if(!anonymity.equalsIgnoreCase("High Anonymous"))
				throw new Exception();

			final String ip = Optional.of(tableLine
					.select("td:nth-child(1) > script")
					.first()
					.html())
					.map(htmlIp -> getSubstring(htmlIp, "IPDecode(\"", "\")", false))
					.map(htmlIp -> {
						try {
							return URLDecoder.decode(htmlIp, "UTF-8");
						} catch (final UnsupportedEncodingException exception) {
							return htmlIp;
						}
					})
					.map(htmlIp -> getSubstring(htmlIp, ">", "<", false))
					.get();

			final String portAsText = tableLine.select("td:nth-child(2)").first().text();

			final int port = !isEmpty(portAsText)? Integer.parseInt(portAsText) : -1;

			final Double speed = getSpeed(tableLine);

			return Optional.ofNullable(new Proxy.ProxyBuilder(ip, port).setSpeed(speed).build());
		} catch (final Exception exception) {
			return Optional.empty();
		}
	}

	public Double getSpeed(final Element tableLine){
		final Double firstSpeed = getSpeedAsText(tableLine, 9);

		final Double secondSpeed = getSpeedAsText(tableLine, 10);

		final Double finalSpeed = ((firstSpeed * 4) + (secondSpeed * 6)) / 10;

		return finalSpeed;
	}

	private Double getSpeedAsText(final Element tableLine, final int childIndex) {
		final String styleSpeed = tableLine
				.select("td:nth-child(" + childIndex + ") > div > span")
				.first()
				.attr("style");

		final String speedAsText =  getSubstring(styleSpeed, "width:", "%", false);

		return !isEmpty(speedAsText)? Double.parseDouble(speedAsText) : -1;
	}
}