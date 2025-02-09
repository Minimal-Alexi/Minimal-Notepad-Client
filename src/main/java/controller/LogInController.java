package controller;

import jakarta.ws.rs.client.ClientBuilder;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import utils.ControllerUtils;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
//import java.awt.*;

public class LogInController {

    @FXML
    public Button loginBtn;
    @FXML
    private Button backBtn;
    @FXML
    private Button testBtn;
    @FXML
    private TextField loginUserInput;
    @FXML
    private PasswordField loginPassInput;
//    @FXML
//    private Text registerLabel;

    private Stage stage;
    private ControllerUtils controllerUtil = new ControllerUtils();
    Client client;


    @FXML
    private void backBtnClick() {
        System.out.println("back btn is called " + this.backBtn);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/hello_view.fxml"));
        this.stage = this.getStage();
        controllerUtil.updateStage(stage, fxmlLoader);
//        System.out.println(backbtn);

    }

    @FXML
    private void registerBtnClick() {
        System.out.println("register button click");
        String userName = loginUserInput.getText();
        String password = loginPassInput.getText();
        System.out.println("Name: " + userName + " - password: " + password);
        try {
            login(userName, password);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    @FXML
    private void testBtnClick() {
        System.out.println("test Btn click");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main_pages/main_page.fxml"));
        this.stage = this.getStage();
        this.controllerUtil.updateStage(stage, fxmlLoader);
    }

    @FXML
    private void mouseEnter() {
//        System.out.println("Mouse enter");
//        System.out.println(this.stage.getClass());
//        Button btn = new Button()
//        switch ()
//        this.backBtn.setCursor(handCursor);

//        this.backBtn.setStyle("-fx-background-color: ");
        this.controllerUtil.setHandcursor(this.backBtn);
        this.controllerUtil.setHandcursor(this.loginBtn);
    }

    @FXML
    private void mouseExit() {
//        System.out.println("Mouse exit");
//        this.backBtn.setCursor(defaultCursor);
        this.controllerUtil.setDefaultCursor(this.backBtn);
        this.controllerUtil.setDefaultCursor(this.loginBtn);
    }


    private Stage getStage() {
        if (this.stage == null) {
            this.stage = (Stage) backBtn.getScene().getWindow();
        }
        return this.stage;
    }

    private void login(String username, String password) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String URI = "http://localhost:8093/usersAuthentication/login";
        HttpPost httpPost = new HttpPost(URI);
        httpPost.addHeader("accept", "application/json");
        httpPost.addHeader("Content-Type", "application/json");

        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("password", password);

        StringEntity entity = new StringEntity(json.toString());
        httpPost.setEntity(entity);

        System.out.println("httpClient: " + httpclient);
        System.out.println("httpPost: " + httpPost);

        try {
            CloseableHttpResponse response = httpclient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            String data = EntityUtils.toString(responseEntity);
            JSONObject jsonResponse = new JSONObject(data);
            EntityUtils.consume(responseEntity);
            response.close();
            // Do more processing here...
            StatusLine statusLine = response.getStatusLine();
            System.out.println("json " + jsonResponse);
            System.out.println("response " + responseEntity);
            System.out.println("status code " + statusLine);

            System.out.println("is error " + statusLine.toString().contains("404"));
//            controllerUtil.updateStage();

        } catch (IOException e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Login: Unable to connect to server. Check your connection or try at a later time. To report this error please contact m@jhourlad.com.");
            a.show();
        } finally {
            httpclient.close();
        }

    }
//        new Thread(() -> {
//            try {
////                client = ClientBuilder.newClient();
////...
////                URI uri = URI.create(<your rest server uri>);
////
////                Response response = client.target(uri).request().get();
////        <your_java_type> entity = response.readEntity(<your_java_type>.class);
//                client = ClientBuilder.newClient();
//                URI uri = URI.create("localhost:8093/usersAuthentication/login");
//                Response response = client.target(uri).request().get();
//                System.out.println(response);
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//            }
////            Platform.runLater();
//        }).start();
//
//    }

    private void registerClick() {
        System.out.println("go to register page");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/register_view.fxml"));
        this.stage = this.getStage();

//        controllerUtil.
    }
}
