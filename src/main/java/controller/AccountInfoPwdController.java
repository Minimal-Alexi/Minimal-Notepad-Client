package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.HttpClientSingleton;
import model.TokenStorage;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
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

public class AccountInfoPwdController {

    @FXML
    private Button accountBtn;

    @FXML
    private PasswordField curPwdInput;

    @FXML
    private Button deleteBtn;

    @FXML
    private Label errCurPwd;

    @FXML
    private Label errNewPwd;

    @FXML
    private Label errRepeatPwd;

    @FXML
    private Button favoritiesBtn;

    @FXML
    private Label generalErrLabel;

    @FXML
    private Button groupsBtn;

    @FXML
    private Label localTime;

    @FXML
    private Button myFileBtn;

    @FXML
    private PasswordField newPwdInput;

    @FXML
    private Button recycleBinBtn;

    @FXML
    private PasswordField repeatPwdInput;

    @FXML
    private Button saveBtn;

    @FXML
    private Button settingBtn;

    @FXML
    private Button shareNoteBtn;

    // properties
    private Stage stage;
    private Scene scene;
    private Parent parent;

    private String username;
    private String email;
//    private

    private MainPageServices mainPageServices;
    private ControllerUtils controllerUtils;
    private HttpResponseService httpResponseService;
    private HttpClientSingleton httpInstance;
    private CloseableHttpClient httpClient;

    //URI API
    private static final String URI = "http://localhost:8093/api/user/";

    public void initialize() {
        System.out.println("start Account Password Page");
        this.mainPageServices = new MainPageServices();
        this.controllerUtils = new ControllerUtils();
        this.httpResponseService = new HttpResponseServiceImpl();

        TokenStorage.getIntance();//
        System.out.println("User: " + TokenStorage.getUser() + ", token: " + TokenStorage.getToken());

        updateLocalTime(localTime);

        httpInstance = HttpClientSingleton.getInstance();
        httpClient = httpInstance.getHttpClient();
//        getUserInfo();
    }

    @FXML
    void accountBtnClick() {
        String pageLink = "/fxml/main_pages/account_user_info_page.fxml";
        this.controllerUtils.gotoPage(stage, accountBtn, pageLink);
    }

    @FXML
    void deleteBtnClick(MouseEvent event) {
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
        try {
            String message = (String) jsonResponse.get("message");
            System.out.println("message: " + message);
            String helloPage = "/fxml/hello_view.fxml";
            TokenStorage.clearToken();
            controllerUtils.gotoPage(stage, deleteBtn, helloPage);
        } catch (JSONException e) {
//            String errMessage = (String) jsonResponse.get("message");
            displayGeneralErrMessages(e.getMessage());
        }
    }

    @FXML
    public void groupsClicked(ActionEvent event) throws IOException {
        goToPage(stage, scene, event, "/fxml/main_pages/groups_page.fxml");
    }

    @FXML
    void mouseEnter(MouseEvent event) {
        this.controllerUtils.setHandCursor(saveBtn);
        this.controllerUtils.setHandCursor(deleteBtn);

    }

    @FXML
    void mouseExit(MouseEvent event) {
        this.controllerUtils.setDefaultCursor(saveBtn);
        this.controllerUtils.setDefaultCursor(deleteBtn);
    }

    @FXML
    void saveBtnClick(MouseEvent event) {
        String curPwd = curPwdInput.getText();
        String newPwd = newPwdInput.getText();
        String repeatNewPwd = repeatPwdInput.getText();
        System.out.println("cur pwd: " + curPwd);
        System.out.println("new pwd: " + newPwd);
        System.out.println("repeat new pwd: " + repeatNewPwd);
        handleInput(curPwd, newPwd, repeatNewPwd);
    }

    private void handleInput(String curPwd, String newPwd, String repeatNewPwd) {
        generalErrLabel.setText("");
        if (curPwd.equals("") || newPwd.equals("") || repeatNewPwd.equals("")) {
            displayEmptyErrorMessage(curPwd, newPwd, repeatNewPwd);
        } else if (!samePwdAndRepeatPwd(newPwd, repeatNewPwd)) {
            displayGeneralErrMessages("New password and Repeat New Password Must Match");
        } else {
            try {
                saveUserPwd(curPwd, newPwd, repeatNewPwd);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void saveUserPwd(String curPwd, String newPwd, String repeatNewPwd) throws IOException {
        resetAllErrMessages();
//        String URI = ""

        String token = TokenStorage.getToken();
        HttpPut httpPut = new HttpPut(URI + "change-password");
        httpPut.addHeader("Accept", "application/json");
        httpPut.addHeader("Content-Type", "application/json");
        httpPut.addHeader("Authorization", "Bearer " + token);

        JSONObject json = new JSONObject();
//        {
//            "oldPassword":"123",
//                "newPassword":"333",
//                "confirmPassword":"333"
//        }
        json.put("oldPassword", curPwd);
        json.put("newPassword", newPwd);
        json.put("confirmPassword", repeatNewPwd);

        StringEntity entity = new StringEntity(json.toString());
        httpPut.setEntity(entity);
        new Thread(() -> {
            try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
                HttpEntity responseEntity = response.getEntity();
                String data = EntityUtils.toString(responseEntity);
                JSONObject jsonResponse = new JSONObject(data);
                EntityUtils.consume(responseEntity);
                // Do more processing here...
                String statusLine = response.getStatusLine().toString();
                System.out.println("json " + jsonResponse);
                System.out.println("response " + responseEntity);
                System.out.println("status code " + statusLine);
                Platform.runLater(() -> {
                    // the callback response from controller using this method, the callback will extract the response and update the GUI of the controller
                    handleSaveUserinfoResponse(jsonResponse, statusLine);
                });
            } catch (IOException e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Unable to connect to server. Check your connection or try at a later time. To report this error please contact admin.");
                a.show();
            } catch (JSONException e) {
//                e.setStackTrace();
                generalErrLabel.setText(e.getMessage());
            }
        }).start();
    }

    private void handleSaveUserinfoResponse(JSONObject jsonResponse, String statusLine) {
        try {
            if (statusLine.contains("200")) {
                generalErrLabel.setTextFill(Color.GREEN);
                generalErrLabel.setText("User Password changes successfully");
//            String pageLink = "/fxml/main_pages/account_user_info_page.fxml";
//            this.controllerUtils.gotoPage(stage, accountBtn, pageLink);
            } else {
                String message = (String) jsonResponse.get("message");
                generalErrLabel.setText(message);
            }
        } catch (JSONException e) {
            generalErrLabel.setText("Cannot change password. Please contact admin");
        }
    }

    private boolean samePwdAndRepeatPwd(String newPwd, String repeatNewPwd) {
        return newPwd.equals(repeatNewPwd);
    }

    private void displayEmptyErrorMessage(String curPwd, String newPwd, String repeatNewPwd) {
        if (curPwd.equals("")) {
            this.errCurPwd.setText("This field cannot be empty");
        } else {
            this.errCurPwd.setText("");
        }
        if (newPwd.equals("")) {
            this.errNewPwd.setText("This field cannot be empty");

        } else {
            this.errNewPwd.setText("");
        }
        if (repeatNewPwd.equals("")) {
            this.errRepeatPwd.setText("This field cannot be empty");

        } else {
            this.errRepeatPwd.setText("");
        }

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
//            String email = (String) jsonResponse.get("email");
//            String username = (String) jsonResponse.get("username");
            String password = (String) jsonResponse.get("password");
            System.out.println("Current password: " + password);
            curPwdInput.setText(password);
//            usernameInput.setText(username);
        } catch (JSONException e) {
            String errMessage = (String) jsonResponse.get("message");
            displayGeneralErrMessages(errMessage);
        }
    }

    private void resetAllErrMessages() {
        generalErrLabel.setTextFill(Color.RED);
        errCurPwd.setText("");
        errNewPwd.setText("");
        errRepeatPwd.setText("");
    }

    private void displayGeneralErrMessages(String errMessage) {
        resetAllErrMessages();
        this.generalErrLabel.setText(errMessage);
    }
}


