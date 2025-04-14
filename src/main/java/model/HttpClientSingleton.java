package model;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public class HttpClientSingleton {
    //    private static final CloseableHttpClient httpClient;
    private static HttpClientSingleton instance;  // get instance of this class
    private final CloseableHttpClient httpClient; // get the opent client

    private final RequestConfig requestConfig;
//    static {
////
//    }

    // create a singleton object with timeout incase request take too long
    private HttpClientSingleton() {
        this.requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setSocketTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        this.httpClient = HttpClients.custom()
                .setDefaultRequestConfig(this.requestConfig)
                .build();// 5s

    }

    public static HttpClientSingleton getInstance() {
        if (instance == null) {
            synchronized (HttpClientSingleton.class) {
                if (instance == null) {
                    instance = new HttpClientSingleton();
                }
            }
        }
        return instance;
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient; // because we use final, so the httpClient is already loaded when istance in initialized
    }

    public void closeHttpClient() {
        try {
            // chekc if the client is still open
            if (this.httpClient != null) {

                this.httpClient.close();
                System.out.println("httpclient is closed");
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }
}
