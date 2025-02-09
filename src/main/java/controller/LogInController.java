package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.HttpClientSingleton;
import model.TokenStorage;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import utils.ControllerUtils;


import java.io.IOException;
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

    @FXML
    private Text errGeneral;
    @FXML
    private Text errUser;
    @FXML
    private Text errPwd;

//    @FXML
//    private Text registerLabel;

    private Stage stage;
    private ControllerUtils controllerUtil = new ControllerUtils();
//    private TokenStorage tokenStorage;
//    Client client;


    @FXML
    private void backBtnClick() {
        System.out.println("back btn is called " + this.backBtn);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/hello_view.fxml"));
        this.stage = this.getStage();
        controllerUtil.updateStage(stage, fxmlLoader);


    }

    @FXML
    private void loginBtnClick() {
        System.out.println("register button click");
        String username = loginUserInput.getText();
        String password = loginPassInput.getText();
        System.out.println("Name: " + username + " - password: " + password);
        handleInput(username, password);

    }


    @FXML
    private void loginPageBtnPress(KeyEvent ke) {
        if (ke.getCode() == KeyCode.ENTER) {
            System.out.println("page submite login info");
            String username = loginUserInput.getText();
            String password = loginPassInput.getText();
            System.out.println("Name: " + username + " - password: " + password);
            handleInput(username, password);
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
        this.controllerUtil.setHandcursor(this.backBtn);
        this.controllerUtil.setHandcursor(this.loginBtn);
    }

    @FXML
    private void mouseExit() {
        this.controllerUtil.setDefaultCursor(this.backBtn);
        this.controllerUtil.setDefaultCursor(this.loginBtn);
    }


    private Stage getStage() {
        if (this.stage == null) {
            this.stage = (Stage) backBtn.getScene().getWindow();
        }
        return this.stage;
    }

    private void handleInput(String username, String password) {
        errGeneral.setText("");
        if (username.equals("") || password.equals("")) {
            if (username.equals("")) {
                this.errUser.setText("Username is empty");
            } else {
                this.errUser.setText("");
            }
            if (password.equals("")) {
                this.errPwd.setText("Password is empty");

            } else {
                this.errPwd.setText("");
            }
        } else {
            try {
                login(username, password);

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void displayErrGeneral(String errMess) {
        this.errGeneral.setText(errMess);
        this.errUser.setText("");
        this.errPwd.setText("");
    }

    private void login(String username, String password) throws IOException {
        this.errUser.setText("");
        this.errPwd.setText("");
        this.errGeneral.setText("");

        HttpClientSingleton instance = HttpClientSingleton.getInstance();
        CloseableHttpClient httpclient = instance.getHttpClient();

        String URI = "http://localhost:8093/api/users-authentication/login";
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

        new Thread(() -> {

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
                Platform.runLater(() -> {
                    this.handleResponse(response, jsonResponse);

                });

                // 401
                // 404
                // 200
//            controllerUtil.updateStage();

            } catch (IOException e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Login: Unable to connect to server. Check your connection or try at a later time. To report this error please contact m@jhourlad.com.");
                a.show();
            }
//            finally {
//                try {
//
//                    httpclient.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        }

        ).start();

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

    private void handleResponse(CloseableHttpResponse response, JSONObject jsonResponse) {
        String statusCode = response.getStatusLine().toString();
        try {
            String token = (String) jsonResponse.get("token");
            String username = (String) jsonResponse.get("username");
            System.out.println("token: " + token);
            System.out.println("username : " + username);
            TokenStorage.saveToken(username, token);
            String savedUsername = TokenStorage.getUser();
            String savedToken = TokenStorage.getToken();
            System.out.println("username: " + savedUsername + " : " + savedToken);


        } catch (JSONException e) {
            String messsage = (String) jsonResponse.get("message");
            displayErrGeneral(messsage);
        }

    }

    private void loginCick() {
        System.out.println("go to register page");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/register_view.fxml"));
        this.stage = this.getStage();

    }
}
