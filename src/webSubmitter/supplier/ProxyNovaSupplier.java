package webSubmitter.supplier;

import model.Local;
import model.Proxy;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.ConstantUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static util.HttpClientHelper.requestGet;
import static util.MethodsUtils.getSubstring;
import static util.MethodsUtils.isEmpty;

public class ProxyNovaSupplier implements ISupplier {
	private final List<Header> basicHeaders;

	public ProxyNovaSupplier() {
		this.basicHeaders = List.of(
			new BasicHeader("Host", "www.proxynova.com"),
			new  BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:64.0) Gecko/20100101 Firefox/64.0"),
			new  BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"),
			new  BasicHeader("Accept-Language", "pt-BR,pt;q=0.8,en-US;q=0.5,en;q=0.3"),
			new  BasicHeader("Accept-Encoding", "gzip, deflate, br"),
			new  BasicHeader("Connection", "keep-alive"),
			new  BasicHeader("Upgrade-Insecure-Requests", "1")
		);
	}

	public List<Local> listPlaces(final String origin){
		try {
			final String htmlResponse = requestGet("https://www.proxynova.com/proxy-server-list/elite-proxies/", basicHeaders);

			final Document html = Jsoup.parse(htmlResponse);

			return Optional.ofNullable(html.getElementById("proxy_country"))
						   .map(selectPlaces -> selectPlaces.getElementsByTag("option"))
						   .stream()
						   .map(option -> getLocal(option))
						   .filter(optionalOption -> !optionalOption.isEmpty())
						   .map(optionalOption -> optionalOption.get())
						   .collect(Collectors.toList());
		} catch (final Exception exception) {
			return List.of();
		}
	}

	private Optional<Local> getLocal(final Elements option){
		try {
			final String optionValue = option.text();

			final boolean haveManyLocal = optionValue.contains("(");
			final boolean isLocalTestIsZero = getLocalTest(optionValue).equals("0");

			if((haveManyLocal && isLocalTestIsZero) || optionValue.contains("---"))
				throw new Exception();

			final String name = getName(haveManyLocal, optionValue).replaceAll("<|>", "").trim();

			return Optional.ofNullable(new Local(name));
		} catch (final Exception exception) {
			return Optional.empty();
		}
	}

	private String getLocalTest(final String optionValue){
		return optionValue.substring(optionValue.lastIndexOf("(") +1, optionValue.lastIndexOf(")"));
	}

	private String getName(final boolean haveManyLocal, final String optionValue){
		return haveManyLocal? optionValue.substring(0, optionValue.lastIndexOf("(")) : optionValue;
	}

	public List<Proxy> listProxies(final String origin, final String protocol) {
		try {
			final String htmlResponse = requestGet("https://www.proxynova.com/proxy-server-list/", basicHeaders);

			final Document html = Jsoup.parse(htmlResponse);

			if (!origin.equals(ConstantUtils.NO_VALUE)) {
				return Optional
						.ofNullable(html.getElementById("proxy_country"))
						.map(elements -> elements.getElementsByTag("option"))
						.filter(option -> option != null && origin.contains(option.text()))
						.map(option -> getTableLines(option))
						.filter(option -> !option.isEmpty())
						.stream()
						.flatMap(proxies -> proxies.stream())
						.sorted((firstProxy, secondProxy) -> secondProxy.getSpeed().compareTo(firstProxy.getSpeed()))
						.collect(Collectors.toList());
			}
		} catch (final Exception exception) { }

		return List.of();
	}

	private List<Proxy> getTableLines(final Elements option){
		try {
			final String url = "https://www.proxynova.com/proxy-server-list/country-" + option;

			final String response = requestGet(url, basicHeaders);

			final Document html = Jsoup.parse(response);

			return html.select("#tbl_proxy_list > tbody > tr")
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
					.select("td:nth-child(7)")
					.first()
					.text();

			if (!anonymity.equalsIgnoreCase("elite"))
				throw new Exception();

			final String ip = Optional.of(tableLine
					.select("td:nth-child(1) > abbr > script")
					.first()
					.html())
					.map(htmlIp -> getSubstring(htmlIp, "('", "'.", false).substring(8) +
							       getSubstring(htmlIp, "+ '", "')", false))
					.get();

			final String portAsText = tableLine.select("td:nth-child(2)").first().text();

			final int port = !isEmpty(portAsText) ? Integer.parseInt(portAsText) : -1;

			final String speedAsText = tableLine.select("td:nth-child(4) > small").first().text();
			final Double speed = !isEmpty(speedAsText)? Double.parseDouble(speedAsText.replace(" ms", "")) : -1;

			return Optional.ofNullable(new Proxy.ProxyBuilder(ip, port).setSpeed(speed).build());
		} catch (final Exception exception) {
			return Optional.empty();
		}
	}
}
