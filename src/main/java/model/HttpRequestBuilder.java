package model;

import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONObject;
import utils.ControllerUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class HttpRequestBuilder {
    private boolean isAuthenthicated;
    private String uri;
    //    private TokenStorage tokenStorage;
    HttpClientSingleton httpClientSingleton;
    CloseableHttpClient httpClient;
    JSONObject jsonRequest;
    HttpRequestBase httpRequest;
    String methodName;
    ControllerUtils controllerUtils;
    StringEntity stringEntity;


    public HttpRequestBuilder(String methodName, String uri, boolean isAuthenthicated) {
        this.methodName = methodName;
        this.uri = uri;

        this.isAuthenthicated = isAuthenthicated;
        this.controllerUtils = new ControllerUtils();
        TokenStorage.getIntance();
        this.getHttpMethod();
        this.jsonRequest = new JSONObject();
        this.httpClientSingleton = HttpClientSingleton.getInstance();
        this.httpClient = this.httpClientSingleton.getHttpClient();
        this.setHeader();
    }

    // without isAuthentication field, have default value = false
    public HttpRequestBuilder(String methodName, String uri) {
        this(methodName, uri, false);
    }

    private void getHttpMethod() {
        switch (this.methodName) {
            case "POST":
                this.httpRequest = new HttpPost(this.uri);
                break;
            case "GET":
                this.httpRequest = new HttpGet(this.uri);
                break;
            case "PUT":
                this.httpRequest = new HttpPut(this.uri);
                break;
            case "PATCH":
                this.httpRequest = new HttpPatch(this.uri);
                break;
            case "DELETE":
                this.httpRequest = new HttpDelete(this.uri);
                break;
        }
    }

    private void setHeader() {
        this.httpRequest.addHeader("Accept", "application/json");
        this.httpRequest.addHeader("Content-Type", "application/json");
        if (this.isAuthenthicated) {
            this.httpRequest.addHeader("Authorization", "Bearer " + TokenStorage.getToken());
        }
    }

    public boolean isRequestAuthenticated() {
        return this.isAuthenthicated;
    }

    public void updateJsonRequest(String key, String value) {
        this.jsonRequest.put(key, value);
    }
    public void updateJsonRequest(String key, List<String> values){
        this.jsonRequest.put(key,values);
    }

    public void setRequestBody() throws UnsupportedEncodingException {
        if (!this.methodName.equals("GET") || !this.methodName.equals("DELETE")) {
            this.stringEntity = new StringEntity(this.jsonRequest.toString());

            // set entity only work with HttpEntityEnclosingRequestBase, which is POST,PUT,
            if (this.httpRequest instanceof HttpEntityEnclosingRequestBase) {
                ((HttpEntityEnclosingRequestBase)this.httpRequest).setEntity(this.stringEntity);
            }

        }
    }

    public HttpRequestBase getHttpRequest() {
        return this.httpRequest;
    }

    public CloseableHttpClient getHttpClient() {
        return this.httpClient;
    }

    public HttpClientSingleton getHttpClientSingleton(){
        return this.httpClientSingleton;
    }


}
