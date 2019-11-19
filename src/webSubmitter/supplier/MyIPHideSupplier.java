package webSubmitter.supplier;

import java.util.ArrayList;
import java.util.logging.Level;

import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;

import model.Local;
import model.Proxy;
import util.ConstantesUtil;

public class MyIPHideSupplier implements ISupplier {
	
	public MyIPHideSupplier() {
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.http.client").setLevel(Level.OFF);
	}
	
	public ArrayList<Local> listPlaces(String localOrigem){
		
		WebClient webClient = createWebClient();
		
		try {
		    HtmlPage htmlPage = webClient.getPage("https://free-proxy-list.net/");
		    
		    if(htmlPage.getWebResponse().getStatusCode() != 200)
		    	throw new Exception();
		    
		    waitForBackGroudJs(webClient, htmlPage);
		    
		    String html = htmlPage.asXml();
		    
			Document htmlDocument = Jsoup.parse(html);
			
			Elements options = htmlDocument.select("#proxylisttable > tfoot > tr > th:nth-child(4) > select > option");
			
			if(options != null) {
				ArrayList<Local> locations = new ArrayList<>();
				
				HtmlElement parsedHtml = htmlPage.getDocumentElement();
				for (int i = 0; i < options.size(); i++) {
					try {
						Element optionElement = options.get(i);
						
						String optionText = optionElement.text();
						
						boolean isNotAllItem = !optionText.contains("All");
						
						boolean isEmpty = true;
						
						if(isNotAllItem) {
							HtmlElement option = parsedHtml.getElementsByAttribute("option", "value", optionElement.attr("value")).get(0);
							
							waitForBackGroudJs(webClient, htmlPage);
							
							htmlPage = option.click();
							
							parsedHtml = htmlPage.getDocumentElement();
							
							int size = parsedHtml.querySelector("#proxylisttable > tbody").getChildNodes().size();
							
							isEmpty = size == 0;
							
							if(isEmpty)
								continue;
						}

						Local local = new Local(optionText);
												
						locations.add(local);	
					} catch (Exception e) { }
				}
								
				return locations;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			webClient.close();
		}
		
		return null;
	}
	
	public ArrayList<Proxy> listPoxies(String local, String protocol){
		
		WebClient webClient = createWebClient();
		
		try {			
		    HtmlPage htmlPage = webClient.getPage("https://free-proxy-list.net/");
		    
		    if(htmlPage.getWebResponse().getStatusCode() != 200)
		    	throw new Exception();
		    
		    waitForBackGroudJs(webClient, htmlPage);
		    			
		    HtmlSelect htmlSelect;
		    if(!local.equals(ConstantesUtil.NO_VALUE)) {
				htmlSelect = (HtmlSelect) htmlPage.getFirstByXPath("//*[@id=\"proxylisttable\"]/tfoot/tr/th[4]/select");
				htmlSelect.setSelectedAttribute(local, true);
				
				waitForBackGroudJs(webClient, htmlPage);
				
				htmlPage = (HtmlPage) htmlSelect.click();
			}
		    
		    if(!protocol.equals(ConstantesUtil.NO_VALUE)) {
		    	htmlSelect = (HtmlSelect) htmlPage.getFirstByXPath("//*[@id=\"proxylisttable\"]/tfoot/tr/th[7]/select");
		    	htmlSelect.setSelectedAttribute(protocol.equalsIgnoreCase(ConstantesUtil.HTTPS)? "yes" : "no", true);
		    	
		    	waitForBackGroudJs(webClient, htmlPage);
		    	
		    	htmlPage = (HtmlPage) htmlSelect.click();
		    }
			
			htmlSelect = (HtmlSelect) htmlPage.getFirstByXPath("//*[@id=\"proxylisttable\"]/tfoot/tr/th[5]/select");
			htmlSelect.setSelectedAttribute("elite proxy", true);
			
			waitForBackGroudJs(webClient, htmlPage);
			
			htmlPage = (HtmlPage) htmlSelect.click();
			
		    String html = htmlPage.asXml();
		    
			Document htmlParseado = Jsoup.parse(html);
			
			Elements linhasTabela = htmlParseado.select("#proxylisttable > tbody > tr");
			
			ArrayList<Proxy> proxies = new ArrayList<>(); 
			for (Element linhaTabela : linhasTabela) {
				try {
					String ip = linhaTabela.select("td:nth-child(1)").first().text().trim();
					String portAsText = linhaTabela.select("td:nth-child(2)").first().text().trim();
					
					Integer port = Integer.parseInt(portAsText);
					
					Proxy proxy = new Proxy.ProxyBuilder(ip, port).build(); 
					
					proxies.add(proxy);
				} catch (Exception e) { }
			}
			
			return proxies;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			webClient.close();
		}
		
		return null;
	}
	
	private WebClient createWebClient() {
		WebClient webClient = new WebClient(BrowserVersion.CHROME);
		
		try {
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			webClient.getOptions().setCssEnabled(true);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			webClient.getOptions().setTimeout(5000);
			webClient.setJavaScriptTimeout(5000);	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return webClient;
	}
	
	private void waitForBackGroudJs(WebClient webClient, HtmlPage htmlPage) {

		try {
	    	webClient.waitForBackgroundJavaScript(5000);
	    	webClient.waitForBackgroundJavaScriptStartingBefore(5000);
	    	synchronized (htmlPage) { 
					htmlPage.wait(5000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
    	}
	}
}
