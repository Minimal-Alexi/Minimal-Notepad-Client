package utils;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;

//@FunctionalInterface
public interface HttpResponseService {
    public void handleReponse(HttpRequestBase request, CloseableHttpClient httpClient, HandleResponseCallback callback);
}
