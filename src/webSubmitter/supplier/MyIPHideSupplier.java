package webSubmitter.supplier;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import model.Local;
import model.Proxy;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import util.ConstantUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class MyIPHideSupplier implements ISupplier {

	public MyIPHideSupplier() {
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.http.client").setLevel(Level.OFF);
	}

	public List<Local> listPlaces(final String origin){
		final WebClient client = createWebClient();

		try {
		    final HtmlPage page = client.getPage("https://free-proxy-list.net/");

		    if(page.getWebResponse().getStatusCode() != 200)
		    	throw new Exception();

		    waitForBackGroundJs(client, page);

			final Document html = Jsoup.parse(page.asXml());

			return Optional.ofNullable(html.select("#proxylisttable > tfoot > tr > th:nth-child(4) > select > option"))
					.stream()
					.map(optionElements -> getLocal(client, page, optionElements))
					.filter(localOptional -> !localOptional.isEmpty())
					.map(localOptional -> localOptional.get())
					.collect(Collectors.toList());
		} catch (final Exception exception) {
			exception.printStackTrace();
		} finally {
			client.close();
		}

		return List.of();
	}

	private WebClient createWebClient() {
		final WebClient client = new WebClient(BrowserVersion.CHROME);

		try {
			final WebClientOptions options = client.getOptions();
			options.setUseInsecureSSL(true);
			options.setJavaScriptEnabled(true);
			options.setThrowExceptionOnScriptError(false);
			options.setThrowExceptionOnFailingStatusCode(false);
			options.setCssEnabled(true);
			options.setTimeout(5000);

			client.setAjaxController(new NicelyResynchronizingAjaxController());
			client.setJavaScriptTimeout(5000);
		} catch (final Exception exception) {
			exception.printStackTrace();
		}

		return client;
	}

	private Optional<Local> getLocal(final WebClient client, final HtmlPage page, final Elements optionElements){
		try {
			final HtmlElement htmlElement = page.getDocumentElement();

			final String optionText = optionElements.text();

			final boolean isNotAllItem = !optionText.contains("All");

			if(isNotAllItem) {
				final String optionValue = optionElements.attr("value");
				final HtmlElement option = htmlElement
						.getElementsByAttribute("option", "value", optionValue)
						.get(0);

				waitForBackGroundJs(client, page);

				final HtmlPage newPage = option.click();

				final HtmlElement newHtmlElement = newPage.getDocumentElement();

				final int size = newHtmlElement.querySelector("#proxylisttable > tbody").getChildNodes().size();

				boolean isEmpty = size == 0;

				if(isEmpty)
					throw new Exception();
			}

			return Optional.ofNullable(new Local(optionText));
		} catch (final Exception exception) {
			return Optional.empty();
		}
	}

	public List<Proxy> listProxies(final String local, final String protocol){
		final WebClient client = createWebClient();

		try {			
		    final HtmlPage page = client.getPage("https://free-proxy-list.net/");

		    if(page.getWebResponse().getStatusCode() != 200)
		    	throw new Exception();

		    waitForBackGroundJs(client, page);

			clickOnFilter(client, page, local, 4).get();

			clickOnFilter(client, page, protocol, 7).get();

			final HtmlPage clickedToEliteProxy = clickOnFilter(client, page, "elite proxy", 5).get();

		    final String xmlPage = clickedToEliteProxy.asXml();

			final Document html = Jsoup.parse(xmlPage);

			return html
				.select("#proxylisttable > tbody > tr")
				.stream()
				.map(tableLine -> {
					final String ip = tableLine
							.select("td:nth-child(1)")
							.first()
							.text()
							.trim();

					final Integer port = Integer.parseInt(tableLine.select("td:nth-child(2)")
							.first()
							.text()
							.trim());

					return new Proxy.ProxyBuilder(ip, port).build();
				})
				.collect(Collectors.toList());
		} catch (final Exception exception) {
			exception.printStackTrace();
		} finally {
			client.close();
		}

		return List.of();
	}

	private Optional<HtmlPage> clickOnFilter(
		final WebClient client,
		final HtmlPage page,
		final String value,
		final int index
	) throws IOException {
		if(!value.equals(ConstantUtils.NO_VALUE)) {
			final HtmlSelect htmlSelect = page.getFirstByXPath("//*[@id=\"proxylisttable\"]/tfoot/tr/th[" + index + "]/select");

			if(value.contains(ConstantUtils.HTTP)) {
				final String httpFilter = value.equalsIgnoreCase(ConstantUtils.HTTPS)? "yes" : "no";

				htmlSelect.setSelectedAttribute(httpFilter, true);
			} else
				htmlSelect.setSelectedAttribute(value, true);

			waitForBackGroundJs(client, page);

			return Optional.ofNullable((HtmlPage) htmlSelect.click());
		}

		return Optional.empty();
	}

	private void waitForBackGroundJs(final WebClient webClient, final HtmlPage htmlPage) {
		try {
	    	webClient.waitForBackgroundJavaScript(5000);
	    	webClient.waitForBackgroundJavaScriptStartingBefore(5000);

	    	synchronized (htmlPage) {
				htmlPage.wait(5000);
			}
		} catch (final InterruptedException exception) {
			exception.printStackTrace();
    	}
	}
}
