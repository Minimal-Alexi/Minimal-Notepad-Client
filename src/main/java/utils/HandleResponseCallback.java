package utils;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.json.JSONObject;

@FunctionalInterface
public interface HandleResponseCallback {
    void handleResponse(CloseableHttpResponse response, JSONObject jsonResponse);
}
