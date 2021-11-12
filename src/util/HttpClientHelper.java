package util;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpClientHelper {

    private static CloseableHttpClient client;

    static {
        if(client == null) {
            final var requestConfig = RequestConfig
                    .custom()
                    .setConnectTimeout(120000)
                    .setConnectionRequestTimeout(120000)
                    .setSocketTimeout(120000)
                    .build();

            client = HttpClientBuilder
                    .create()
                    .setDefaultRequestConfig(requestConfig)
                    .build();
        }
    }

    public static String requestGet(final String url, final List<Header> headers) throws Exception {
        final var request = new HttpGet(url);

        headers.stream().forEach(request::addHeader);

        final var response = client.execute(request);

        if(response.getStatusLine().getStatusCode() != 200)
            throw new Exception();

        return IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
    }
}
