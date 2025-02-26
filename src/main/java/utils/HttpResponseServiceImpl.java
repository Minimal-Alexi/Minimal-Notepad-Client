package utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class HttpResponseServiceImpl implements HttpResponseService {
    @Override
//    public void handleReponse(HttpPost httpPost, CloseableHttpClient httpClient, HandleResponseCallback callback) {
    public void handleReponse(HttpRequestBase request, CloseableHttpClient httpClient, HandleResponseCallback callback) {
        new Thread(() -> {
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity responseEntity = response.getEntity();
                String data = EntityUtils.toString(responseEntity);
                JSONObject jsonResponse = new JSONObject(data);
                EntityUtils.consume(responseEntity);
                // Do more processing here...
                StatusLine statusLine = response.getStatusLine();
//                System.out.println("json " + jsonResponse);
//                System.out.println("response " + responseEntity);
//                System.out.println("status code " + statusLine);
                Platform.runLater(() -> {
                    // the callback response from controller using this method, the callback will extract the response and update the GUI of the controller
                    callback.handleResponse(response, jsonResponse);
                });
            } catch (IOException e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Unable to connect to server. Check your connection or try at a later time. To report this error please contact admin.");
                a.show();
            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }).start();
    }



//    @Override
//    public void handleGetResponse(HttpGet httpGet, CloseableHttpClient httpClient, HandleResponseCallback callback) {
//
//    }
}
