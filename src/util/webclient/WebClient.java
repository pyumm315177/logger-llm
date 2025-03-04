package util.webclient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class WebClient implements IWebClient {
    private final HttpClient httpClient;
    private WebClient() {
        httpClient = HttpClient.newHttpClient();
    }
    private static WebClient instance;

    public static WebClient getWebClient() {
        if (instance == null) {
            instance = new WebClient();
        }
        return instance;
    }

    @Override
    public String sendRequest(String path, HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    @Override
    public HttpRequest makeRequest(Map<String, String> map) {
        return HttpRequest.newBuilder()
                .uri(URI.create(map.get("url")))
                .method(map.get("method"), HttpRequest.BodyPublishers.ofString(map.get("body")))
                .headers(map.get("headers").split(";"))
                .build();
    }
}

interface IWebClient {
    String sendRequest(String path, HttpRequest request) throws IOException, InterruptedException;

    HttpRequest makeRequest(Map<String, String> map);
}
