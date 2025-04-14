package model;

import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONObject;
import utils.ControllerUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class HttpRequestBuilder {
    private final boolean isAuthenthicated;
    private final String uri;
    private final CloseableHttpClient httpClient;
    private final JSONObject jsonRequest;
    private final HttpRequestBase httpRequest;
    private final String methodName;
    private StringEntity stringEntity;

    public HttpRequestBuilder(String methodName, String uri, boolean isAuthenthicated) {
        this.methodName = methodName;
        this.uri = uri;
        this.isAuthenthicated = isAuthenthicated;

        TokenStorage.getIntance(); // Ensure token is loaded
        this.jsonRequest = new JSONObject();
        this.httpRequest = resolveHttpMethod(methodName, uri);
        this.httpClient = HttpClientSingleton.getInstance().getHttpClient();
        setHeaders();
    }

    public HttpRequestBuilder(String methodName, String uri) {
        this(methodName, uri, false);
    }

    private HttpRequestBase resolveHttpMethod(String method, String uri) {
        return switch (method.toUpperCase()) {
            case "POST" -> new HttpPost(uri);
            case "GET" -> new HttpGet(uri);
            case "PUT" -> new HttpPut(uri);
            case "PATCH" -> new HttpPatch(uri);
            case "DELETE" -> new HttpDelete(uri);
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        };
    }

    private void setHeaders() {
        httpRequest.addHeader("Accept", "application/json");
        httpRequest.addHeader("Content-Type", "application/json");

        if (isAuthenthicated) {
            httpRequest.addHeader("Authorization", "Bearer " + TokenStorage.getToken());
        }
    }

    // Add/Update a string field in JSON request
    public HttpRequestBuilder updateJsonRequest(String key, String value) {
        jsonRequest.put(key, value);
        return this;
    }

    // Add/Update a list field in JSON request
    public HttpRequestBuilder updateJsonRequest(String key, List<String> values) {
        jsonRequest.put(key, values);
        return this;
    }

    public HttpRequestBuilder setJsonRequest(JSONObject jsonObject) {
        jsonRequest.clear();
        jsonObject.keySet().forEach(k -> jsonRequest.put(k, jsonObject.get(k)));
        return this;
    }

    public HttpRequestBuilder addHeader(String name, String value) {
        httpRequest.addHeader(name, value);
        return this;
    }

    public void setRequestBody() throws UnsupportedEncodingException {
        if (!methodName.equalsIgnoreCase("GET") && !methodName.equalsIgnoreCase("DELETE")) {
            stringEntity = new StringEntity(jsonRequest.toString());
            if (httpRequest instanceof HttpEntityEnclosingRequestBase entityRequest) {
                entityRequest.setEntity(stringEntity);
            }
        }
    }

    public HttpRequestBase getHttpRequestBase() {
        return httpRequest;
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }
}
