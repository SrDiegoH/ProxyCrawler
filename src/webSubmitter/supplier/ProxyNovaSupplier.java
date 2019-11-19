package webSubmitter.supplier;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.Header;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import model.Local;
import model.Proxy;
import util.ConstantesUtil;
import util.MethodsUtil;

public class ProxyNovaSupplier implements ISupplier {
	private Header[] basicHeaders;
	
	public ProxyNovaSupplier() {
		basicHeaders = new Header []{
			new  BasicHeader("Host", "www.proxynova.com"),
			new  BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:64.0) Gecko/20100101 Firefox/64.0"),
			new  BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"),
			new  BasicHeader("Accept-Language", "pt-BR,pt;q=0.8,en-US;q=0.5,en;q=0.3"),
			new  BasicHeader("Accept-Encoding", "gzip, deflate, br"),
			new  BasicHeader("Connection", "keep-alive"),
			new  BasicHeader("Upgrade-Insecure-Requests", "1")
		};
	}
	
	public ArrayList<Local> listPlaces(String localOrigin){
		try {
			HttpClient httpClient = HttpClientBuilder.create().build();
			
			HttpGet getRequest = request("https://www.proxynova.com/proxy-server-list/elite-proxies/", basicHeaders);
	
			HttpResponse httpReponse = httpClient.execute(getRequest);
	
			if (httpReponse.getStatusLine().getStatusCode() != 200)
				throw new Exception();
	
			String html = IOUtils.toString(httpReponse.getEntity().getContent(), StandardCharsets.UTF_8);
			
			Document parsedHtml = Jsoup.parse(html);
			
			Element selectPlaces = parsedHtml.getElementById("proxy_country");
			
			if(selectPlaces != null) {
				Elements options = selectPlaces.getElementsByTag("option");
				
				ArrayList<Local> locations = new ArrayList<>(); 
				for (Element option : options) {
					try {
						String optionValue = option.text();  
						
						boolean haveManyLocal = optionValue.contains("(");
						String localText = optionValue.substring(optionValue.lastIndexOf("(") +1, optionValue.lastIndexOf(")"));
						
						if((haveManyLocal && localText.equals("0")) || optionValue.contains("---"))
							continue;
						
						String name = haveManyLocal? optionValue.substring(0, optionValue.lastIndexOf("(")) : optionValue;
						name = name.replaceAll("<|>", "").trim();
						
						Local local = new Local(name);
						
						locations.add(local);
					} catch (Exception e) { }
				}
				
				return locations;
			}
		} catch (Exception ex) { }
		
		return null;
	}
	
	public ArrayList<Proxy> listPoxies(String local, String protocol){
		try {
			HttpClient httpClient = HttpClientBuilder.create().build();
			
			HttpGet getRequest = request("https://www.proxynova.com/proxy-server-list/", basicHeaders);
			
			HttpResponse httpReponse = httpClient.execute(getRequest);
			
			if (httpReponse.getStatusLine().getStatusCode() != 200)
				throw new Exception();
			
			String html = IOUtils.toString(httpReponse.getEntity().getContent(), StandardCharsets.UTF_8);
			
			Document parsedHtml = Jsoup.parse(html);
						

			if(!local.equals(ConstantesUtil.NO_VALUE)) {
				Element selectLocais = parsedHtml.getElementById("proxy_country");
				
				if(selectLocais != null) {
					Elements options = selectLocais.getElementsByTag("option");
					
					String optionValue = null; 
					for (Element option : options) {
						if(local.contains(option.text())) {
							optionValue = option.attr("value");
							break;
						}
					}
	
					if(optionValue != null) {
						String url = "https://www.proxynova.com/proxy-server-list/country-" +  optionValue;
						
						getRequest = request(url , basicHeaders);
						
						httpReponse = httpClient.execute(getRequest);
						
						if (httpReponse.getStatusLine().getStatusCode() != 200)
							throw new Exception();
						
						html = IOUtils.toString(httpReponse.getEntity().getContent(), StandardCharsets.UTF_8);
						
						parsedHtml = Jsoup.parse(html);
					}
				}
			}

			Elements tableLines = parsedHtml.select("#tbl_proxy_list > tbody > tr");
			
			ArrayList<Proxy> proxies = new ArrayList<>(); 
			for (Element tableLine : tableLines) {
				try {
					String anonymity = tableLine.select("td:nth-child(7)").first().text();
					
					if(!anonymity.equalsIgnoreCase("elite"))
						continue;
					
					String ip = tableLine.select("td:nth-child(1) > abbr > script").first().html();
					ip = MethodsUtil.getSubstring(ip, "('", "'.", false).substring(8) + MethodsUtil.getSubstring(ip, "+ '", "')", false);
							
					String portAsText = tableLine.select("td:nth-child(2)").first().text();
					int port = !MethodsUtil.isEmpty(portAsText)? Integer.parseInt(portAsText) : -1;
					
					String speedAsText = tableLine.select("td:nth-child(4) > small").first().text();
					Double speed = !MethodsUtil.isEmpty(speedAsText)? Double.parseDouble(speedAsText.replace(" ms", "")) : -1; 
					
					Proxy proxy = new Proxy.ProxyBuilder(ip, port).setSpeed(speed).build();
					
					proxies.add(proxy);	
				} catch (Exception e) { }
			}
			
			Collections.sort(proxies, (firstProxy, secondProxy) -> {
	            return secondProxy.getSpeed().compareTo(firstProxy.getSpeed());
		    });
			
			return proxies;
		} catch (Exception ex) { }
		
		return null;
	}
	
	private HttpGet request(String url, Header... headers) {
		HttpGet getRequest = new HttpGet(url);
		
		for (Header header : headers)
			getRequest.addHeader(header);
		
		return getRequest;
	}	
}
