package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.HttpClientSingleton;
import model.TokenStorage;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import utils.ControllerUtils;
import utils.HttpResponseService;
import utils.HttpResponseServiceImpl;
import utils.MainPageServices;

import java.io.IOException;

import static utils.MainPageServices.goToPage;
import static utils.MainPageServices.updateLocalTime;


public class AccountInfoPageController {

    //FXML element
    @FXML
    private Button myFileBtn;
    @FXML
    private Button shareNoteBtn;
    @FXML
    private Button favoritiesBtn;
    @FXML
    private Button recycleBinBtn;
    @FXML
    private Button groupsBtn;
    @FXML
    private Button accountBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private Button changePwdBtn;
    @FXML
    private Button deleteBtn;

    @FXML
    private Label localTime;

    @FXML
    private Label userErrLabel;
    @FXML
    private Label emailErrLabel;
    @FXML
    private Label generalErrLabel;

    @FXML
    private TextField usernameInput;
    @FXML
    private TextField emailInput;


    // properties
    private Stage stage;
    private Scene scene;
    private Parent parent;
//    private

    private MainPageServices mainPageServices;
    private ControllerUtils controllerUtils;
    private HttpResponseService httpResponseService;
    private HttpClientSingleton httpInstance;
    private CloseableHttpClient httpClient;

    //URI API
    private static final String URI = "http://localhost:8093/api/user/";

    // this method must be public so javafx can use it
    public void initialize() {
        System.out.println("start Account User Page");
        this.mainPageServices = new MainPageServices();
        this.controllerUtils = new ControllerUtils();
        this.httpResponseService = new HttpResponseServiceImpl();

        TokenStorage.getIntance();//
        System.out.println("User: " + TokenStorage.getUser() + ", token: " + TokenStorage.getToken());

        updateLocalTime(localTime);
        httpInstance = HttpClientSingleton.getInstance();
        httpClient = httpInstance.getHttpClient();
        getUserInfo();
    }

    @FXML
    public void mouseEnter() {
        this.controllerUtils.setHandCursor(saveBtn);
        this.controllerUtils.setHandCursor(changePwdBtn);
        this.controllerUtils.setHandCursor(deleteBtn);
    }

    @FXML
    public void mouseExit() {
        this.controllerUtils.setDefaultCursor(saveBtn);
        this.controllerUtils.setDefaultCursor(changePwdBtn);
        this.controllerUtils.setDefaultCursor(deleteBtn);
    }

    @FXML
    public void saveBtnClick() {
        String email = emailInput.getText();
        String username = usernameInput.getText();
        handleInput(email, username);
    }

    private void getUserInfo() {
        String username = TokenStorage.getUser();
        String token = TokenStorage.getToken();
//        String URI = "http://localhost:8093/api/user/";
        HttpGet httpGet = new HttpGet(URI);
        httpGet.addHeader("Accept", "application/json");
        httpGet.addHeader("Content-Type", "application/json");
        httpGet.addHeader("Authorization", "Bearer " + token);

//        JSONObject json = new JSONObject();
//        json.put("username",)
//        httpResponseService.handleReponse(httpGet);
        new Thread(() -> {
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity responseEntity = response.getEntity();
                String data = EntityUtils.toString(responseEntity);
                JSONObject jsonResponse = new JSONObject(data);
                EntityUtils.consume(responseEntity);
                // Do more processing here...
                StatusLine statusLine = response.getStatusLine();
                System.out.println("json " + jsonResponse);
                System.out.println("response " + responseEntity);
                System.out.println("status code " + statusLine);
                Platform.runLater(() -> {
                    // the callback response from controller using this method, the callback will extract the response and update the GUI of the controller
//                    callback.handleResponse(response, jsonResponse);
                    handleGetUserInfoResponse(jsonResponse);
                });
            } catch (IOException e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Unable to connect to server. Check your connection or try at a later time. To report this error please contact admin.");
                a.show();
            }
        }).start();
    }

    private void handleGetUserInfoResponse(JSONObject jsonResponse) {
//        JSONObject jsonObject = new JSONObject(response);
        try {
            String email = (String) jsonResponse.get("email");
            String username = (String) jsonResponse.get("username");
            emailInput.setText(email);
            usernameInput.setText(username);
        } catch (JSONException e) {
            String errMessage = (String) jsonResponse.get("message");
            displayGeneralErrMessages(errMessage);
        }
    }

    private void handleInput(String email, String username) {
        generalErrLabel.setText("");
        if (username.equals("") || email.equals("")) {
            displayEmptyErrorMessage(email, username);
        } else if (!controllerUtils.validEmail(email)) {
            displayGeneralErrMessages("Wrong email format. Should follow xyz@mail.com");
        } else {
            try {
                saveUserInfo(email, username);

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void resetAllErrMessages() {
        userErrLabel.setText("");
        emailErrLabel.setText("");
        generalErrLabel.setText("");
    }

    private void displayGeneralErrMessages(String errMessage) {
        resetAllErrMessages();
        this.generalErrLabel.setText(errMessage);
    }

    private void displayEmptyErrorMessage(String email, String username) {
        if (username.equals("")) {
            this.userErrLabel.setText("Username is empty");
        } else {
            this.userErrLabel.setText("");
        }
        if (email.equals("")) {
            this.emailErrLabel.setText("Email is empty");

        } else {
            this.emailErrLabel.setText("");
        }
    }

    public void saveUserInfo(String email, String username) throws IOException {
        resetAllErrMessages();
//        String URI = ""
        HttpPut httpPut = new HttpPut(URI);
    }


    @FXML
    public void groupsClicked(ActionEvent event) throws IOException {
        goToPage(stage, scene, event, "/fxml/main_pages/groups_page.fxml");
    }


    @FXML
    public void changePwdClick() {
        this.stage = controllerUtils.getStage(saveBtn, this.stage);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main_pages/changePwd.fxml"));
        this.controllerUtils.updateStage(this.stage, fxmlLoader);
    }

    // sidebar
    @FXML
    public void accountBtnClick() {

    }

    @FXML
    public void deleteBtnClick() {
        System.out.println("Deleting user");
        String token = TokenStorage.getToken();
        HttpDelete httpDelete = new HttpDelete(URI);
        httpDelete.addHeader("Accept", "application/json");
        httpDelete.addHeader("Content-Type", "application/json");
        httpDelete.addHeader("Authorization", "Bearer " + token);
        new Thread(() -> {
            try (CloseableHttpResponse response = httpClient.execute(httpDelete)) {
                HttpEntity responseEntity = response.getEntity();
                String data = EntityUtils.toString(responseEntity);
                System.out.println("data " + data);
                JSONObject jsonResponse = new JSONObject(data);
                EntityUtils.consume(responseEntity);
                // Do more processing here...
                StatusLine statusLine = response.getStatusLine();
//                System.out.println("json " + jsonResponse);

                System.out.println("response " + responseEntity);
                System.out.println("status code " + statusLine);
                Platform.runLater(() -> {
                    // the callback response from controller using this method, the callback will extract the response and update the GUI of the controller
                    handleDeleteResponse(jsonResponse);
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

    private void handleDeleteResponse(JSONObject jsonResponse) {
        String message = (String) jsonResponse.get("message");
        try {
            System.out.println("message: " + message);
            String helloPage = "/fxml/hello_view.fxml";
            TokenStorage.clearToken();
            controllerUtils.gotoPage(stage, deleteBtn, helloPage);
        } catch (JSONException e) {
//            String errMessage = (String) jsonResponse.get("message");
            displayGeneralErrMessages(message);
        }
    }

}
