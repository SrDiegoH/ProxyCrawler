package webSubmitter.supplier;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import model.Local;
import model.Proxy;
import util.ConstantesUtil;
import util.MethodsUtil;

public class FreeProxyListSupplier implements ISupplier {
	
	private Map<String, String> basicHeaders;
	
	public FreeProxyListSupplier() {
		basicHeaders = new HashMap<String, String>();
		basicHeaders.put("Host", "www.freeproxylists.net");
		basicHeaders.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:64.0) Gecko/20100101 Firefox/64.0");
		basicHeaders.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		basicHeaders.put("Accept-Language", "pt-BR,pt;q=0.8,en-US;q=0.5,en;q=0.3");
		basicHeaders.put("Accept-Encoding", "gzip, deflate, br");
		basicHeaders.put("Connection", "close");
		basicHeaders.put("Upgrade-Insecure-Requests", "1");
		basicHeaders.put("TE", "Trailers");
	}
	
	public ArrayList<Local> listPlaces(String localOrigin){
		try {
			Document parsedHtml = request("http://www.freeproxylists.net/country.html");
			
			Elements tableLocations = parsedHtml.select(".DataGrid > tbody > tr");
						
			if(tableLocations != null) {
				ArrayList<Local> locations = new ArrayList<>();
				
				for (Element actualLocal : tableLocations) {
					try {
						if(!actualLocal.toString().contains("<a"))
							continue;
						
						String name = actualLocal.getElementsByTag("a").text();
						
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
			String url = urlBuilder("", protocol);
			
			Document parsedHtml = request(url);

			if(!local.equals(ConstantesUtil.NO_VALUE)) {
				Element selectLocations = parsedHtml.select("#form1 > table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(2) > td:nth-child(1) > select:nth-child(1)").first();
				
				if(selectLocations != null) {
					Elements options = selectLocations.getElementsByTag("option");
					
					String optionValue = null; 
					for (Element option : options) {
						if(local.contains(option.text())) {
							optionValue = option.attr("value");
							break;
						}
					}
	
					if(optionValue != null) {
						url = urlBuilder(optionValue, protocol);
						
						parsedHtml = request(url);
					}
				}
			}
			
			Elements tableLines = parsedHtml.select(".DataGrid > tbody > tr");
			
			ArrayList<Proxy> proxies = new ArrayList<>(); 
			for (Element tableLine : tableLines) {
				try {
					String anonymity = tableLine.select("td:nth-child(4)").first().text();
					
					if(!anonymity.equalsIgnoreCase("High Anonymous"))
						continue;
					
					String ip = tableLine.select("td:nth-child(1) > script").first().html();
					ip = MethodsUtil.getSubstring(ip, "IPDecode(\"", "\")", false);
					ip = URLDecoder.decode(ip, "UTF-8");
					ip = MethodsUtil.getSubstring(ip, ">", "<", false);
					
					String portAsText = tableLine.select("td:nth-child(2)").first().text();
					
					int port = !MethodsUtil.isEmpty(portAsText)? Integer.parseInt(portAsText) : -1;
					
					
					String firstSpeedAsText = tableLine.select("td:nth-child(9) > div > span").first().attr("style");
					firstSpeedAsText = MethodsUtil.getSubstring(firstSpeedAsText, "width:", "%", false);
					
					Double firstSpeed = !MethodsUtil.isEmpty(firstSpeedAsText)? Double.parseDouble(firstSpeedAsText) : -1; 
										
					String secondSpeedAsText = tableLine.select("td:nth-child(10) > div > span").first().attr("style");
					secondSpeedAsText = MethodsUtil.getSubstring(secondSpeedAsText, "width:", "%", false);
					
					Double secondSpeed = !MethodsUtil.isEmpty(secondSpeedAsText)? Double.parseDouble(secondSpeedAsText) : -1;
										
					Double finalSpeed = ((firstSpeed*4) + (secondSpeed*6))/10;
					
					
					Proxy proxy = new Proxy.ProxyBuilder(ip, port).setSpeed(finalSpeed).build();
					
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
	
	private Document request(String url) throws Exception {
		Connection connection = Jsoup.connect(url);
		connection.headers(basicHeaders);
		return connection.get();
	}
	
	private String urlBuilder(String optionValue, String protocol) {
		if(protocol.equals(ConstantesUtil.NO_VALUE))
			protocol = "";
		
		StringBuilder getQuery = new StringBuilder("http://www.freeproxylists.net/?")
			.append("c=").append(optionValue).append("&")
			.append("pt=").append("&")
			.append("pr=").append(protocol).append("&")
			.append("a[]=2").append("&")
			.append("u=0");
		
		return getQuery.toString();
	}
}
