package util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Map;

public class JsoupHelper {
    public static Document requestGet(final String url, final Map<String, String> headers) throws Exception {
        return Jsoup.connect(url).headers(headers).get();
    }
}