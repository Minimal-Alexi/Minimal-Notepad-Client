package utils;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;

//@FunctionalInterface
public interface HttpResponseService {
    public void handleReponse(HttpPost httpPost, CloseableHttpClient httpClient,HandleResponseCallback callback);
//    public void handleGetResponse(HttpGet httpGet, CloseableHttpClient httpClient, HandleResponseCallback callback);
}
