package controller.account;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.HttpClientSingleton;
import model.HttpRequestBuilder;
import model.TokenStorage;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import utils.ControllerUtils;
import utils.HttpResponseService;
import utils.HttpResponseServiceImpl;
import utils.MainPageServices;

import java.io.IOException;
import java.util.Optional;

import static utils.MainPageServices.goToPage;
import static utils.MainPageServices.updateLocalTime;

public class AccountInfoPwdController {

    @FXML
    private PasswordField curPwdInput;

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
    private Label localTime;

    @FXML
    private PasswordField newPwdInput;

    @FXML
    private PasswordField repeatPwdInput;

    @FXML
    private Button myFileBtn;

    @FXML
    private Button groupsBtn;

    @FXML
    private Button deleteBtn;

    @FXML
    private Button accountBtn;

    @FXML
    private Button recycleBinBtn;

    @FXML
    private Button saveBtn;

    @FXML
    private Button settingBtn;

    @FXML
    private Button shareNoteBtn;

    @FXML
    private Button logOutBtn;


    // properties
    private Stage stage;
    private Scene scene;
    private Parent parent;

    private String username;
    private String email;
    private String newPassword;
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
        this.controllerUtils.goPage(stage, accountBtn, pageLink);
    }

    @FXML
    public void deleteBtnClick() throws IOException {

        String yesTxt = "Yes";

        Optional<ButtonType> result = displayDeleteWarningDialog();
        System.out.println("result of dialog " + result.get().getText());
        if (result.get().getText().equals(yesTxt)) {
            System.out.println("Deleting user");
//            String token = TokenStorage.getToken();
//            HttpDelete httpDelete = new HttpDelete(URI);
//            httpDelete.addHeader("Accept", "application/json");
//            httpDelete.addHeader("Content-Type", "application/json");
//            httpDelete.addHeader("Authorization", "Bearer " + token);

            HttpRequestBuilder httpRequest = new HttpRequestBuilder("DELETE", URI, true);

            // call this method only if you have body in your request
//            httpRequest.setRequestBody();
            HttpRequestBase httpDelete = httpRequest.getHttpRequest();
            CloseableHttpClient httpClient = httpRequest.getHttpClient();

//            new Thread(() -> {
//                try (CloseableHttpResponse response = httpClient.execute(httpDelete)) {
//                    HttpEntity responseEntity = response.getEntity();
//                    String data = EntityUtils.toString(responseEntity);
//                    System.out.println("data " + data);
//                    JSONObject jsonResponse = new JSONObject(data);
//                    EntityUtils.consume(responseEntity);
//                    // Do more processing here...
//                    StatusLine statusLine = response.getStatusLine();
////                System.out.println("json " + jsonResponse);
//
//                    System.out.println("response " + responseEntity);
//                    System.out.println("status code " + statusLine);
//                    Platform.runLater(() -> {
//                        // the callback response from controller using this method, the callback will extract the response and update the GUI of the controller
//                        handleDeleteResponse(response, jsonResponse);
//                    });
//                } catch (IOException e) {
//                    Alert a = new Alert(Alert.AlertType.ERROR);
//                    a.setContentText("Unable to connect to server. Check your connection or try at a later time. To report this error please contact admin.");
//                    a.show();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    System.out.println(e.getMessage());
//                }
//            }).start();
            httpResponseService.handleReponse(httpDelete, httpClient, this::handleDeleteResponse);
        }
    }

    private void handleDeleteResponse(CloseableHttpResponse response, Object jsonResponse) {
        JSONObject object = controllerUtils.toJSonObject(response);

        try {
            String message = (String) object.get("message");
            System.out.println("message: " + message);
            String helloPage = "/fxml/hello_view.fxml";
            TokenStorage.clearToken();
            controllerUtils.goPage(stage, deleteBtn, helloPage);
        } catch (JSONException e) {
//            String errMessage = (String) jsonResponse.get("message");
            displayGeneralErrMessages(e.getMessage());
        }
    }

//    @FXML
//    public void groupsClicked(ActionEvent event) throws IOException {
////        goToPage(stage, scene, event, "/fxml/main_pages/groups_page.fxml");
//        goToPage(stage, scene, event, "/fxml/main_pages/groups/group_info.fxml");
//
//    }

    @FXML
    void mouseEnter(MouseEvent event) {
        this.controllerUtils.setHandCursor(saveBtn);
        this.controllerUtils.setHandCursor(deleteBtn);
        this.controllerUtils.setHandCursor(groupsBtn);

    }

    @FXML
    void mouseExit(MouseEvent event) {
        this.controllerUtils.setDefaultCursor(saveBtn);
        this.controllerUtils.setDefaultCursor(deleteBtn);
        this.controllerUtils.setDefaultCursor(groupsBtn);
    }

    @FXML
    void logOutBtnClick() {
        this.controllerUtils.goToHelloPage(stage, logOutBtn);
    }

    @FXML
    void groupsBtnClick(){
        controllerUtils.goPage(stage,groupsBtn,"/fxml/main_pages/groups/group_info_create_group.fxml/");

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


    // must add IOException
    private void saveUserPwd(String curPwd, String newPwd, String repeatNewPwd) throws IOException {
        resetAllErrMessages();
//        String URI = ""

//        String token = TokenStorage.getToken();
//        HttpPut httpPut = new HttpPut(URI + "change-password");
//        httpPut.addHeader("Accept", "application/json");
//        httpPut.addHeader("Content-Type", "application/json");
//        httpPut.addHeader("Authorization", "Bearer " + token);
//
//        JSONObject json = new JSONObject();
////        {
////            "oldPassword":"123",
////                "newPassword":"333",
////                "confirmPassword":"333"
////        }
//        json.put("oldPassword", curPwd);
//        json.put("newPassword", newPwd);
//        json.put("confirmPassword", repeatNewPwd);
//
//        StringEntity entity = new StringEntity(json.toString());
//        httpPut.setEntity(entity);

        String changwPwdURI = URI + "change-password";
        HttpRequestBuilder httpRequest = new HttpRequestBuilder("PUT", changwPwdURI, true);

        // set JSON
        httpRequest.updateJsonRequest("oldPassword", curPwd);
        httpRequest.updateJsonRequest("newPassword", newPwd);
        httpRequest.updateJsonRequest("confirmPassword", repeatNewPwd);

        //save new password to update to the user info in the token storage
        this.newPassword = newPwd;

        // call this method only if you have body in your request
        httpRequest.setRequestBody();
//            HttpDelete httpDelete = (HttpDelete) httpRequest.getHttpRequest();
        HttpRequestBase httpPut = httpRequest.getHttpRequest();
        CloseableHttpClient httpClient = httpRequest.getHttpClient();

//        new Thread(() -> {
//            try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
//                HttpEntity responseEntity = response.getEntity();
//                String data = EntityUtils.toString(responseEntity);
//                JSONObject jsonResponse = new JSONObject(data);
//                EntityUtils.consume(responseEntity);
//                // Do more processing here...
//                String statusLine = response.getStatusLine().toString();
//                System.out.println("json " + jsonResponse);
//                System.out.println("response " + responseEntity);
//                System.out.println("status code " + statusLine);
//                Platform.runLater(() -> {
//                    // the callback response from controller using this method, the callback will extract the response and update the GUI of the controller
//                    TokenStorage.saveInfo("password",newPwd);
//                    handleSaveUserinfoResponse(response,jsonResponse);
//                });
//            } catch (IOException e) {
//                Alert a = new Alert(Alert.AlertType.ERROR);
//                a.setContentText("Unable to connect to server. Check your connection or try at a later time. To report this error please contact admin.");
//                a.show();
//            } catch (JSONException e) {
////                e.setStackTrace();
//                generalErrLabel.setText(e.getMessage());
//            }
//        }).start();
        this.httpResponseService.handleReponse(httpPut, httpClient, this::handleSaveUserinfoResponse);
        // after the update info successfully, do does below
//        TokenStorage.saveInfo("password", newPwd);
    }

    private void handleSaveUserinfoResponse(CloseableHttpResponse response, Object jsonResponse) {
        JSONObject object = controllerUtils.toJSonObject(jsonResponse);

        try {
            String statusLine = response.getStatusLine().toString();

            if (statusLine.contains("200")) {
                generalErrLabel.setTextFill(Color.GREEN);
                generalErrLabel.setText("User Password changes successfully");
                TokenStorage.saveInfo("password",this.newPassword);


            } else {
                String message = (String) object.get("message");
                generalErrLabel.setText(message);
            }
        } catch (JSONException e) {
            generalErrLabel.setText("Cannot change password. Please contact admin");
        }
        // only with 200, return true, the rest return fa

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
//
//    private void getUserInfo() {
//        String username = TokenStorage.getUser();
//        String token = TokenStorage.getToken();
////        String URI = "http://localhost:8093/api/user/";
//        HttpGet httpGet = new HttpGet(URI);
//        httpGet.addHeader("Accept", "application/json");
//        httpGet.addHeader("Content-Type", "application/json");
//        httpGet.addHeader("Authorization", "Bearer " + token);
//
////        JSONObject json = new JSONObject();
////        json.put("username",)
////        httpResponseService.handleReponse(httpGet);
//        new Thread(() -> {
//            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
//                HttpEntity responseEntity = response.getEntity();
//                String data = EntityUtils.toString(responseEntity);
//                JSONObject jsonResponse = new JSONObject(data);
//                EntityUtils.consume(responseEntity);
//                // Do more processing here...
//                StatusLine statusLine = response.getStatusLine();
//                System.out.println("json " + jsonResponse);
//                System.out.println("response " + responseEntity);
//                System.out.println("status code " + statusLine);
//                Platform.runLater(() -> {
//                    // the callback response from controller using this method, the callback will extract the response and update the GUI of the controller
////                    callback.handleResponse(response, jsonResponse);
//                    handleGetUserInfoResponse(jsonResponse);
//                });
//            } catch (IOException e) {
//                Alert a = new Alert(Alert.AlertType.ERROR);
//                a.setContentText("Unable to connect to server. Check your connection or try at a later time. To report this error please contact admin.");
//                a.show();
//            }
//        }).start();
//    }
//
//    private void handleGetUserInfoResponse(JSONObject jsonResponse) {
////        JSONObject jsonObject = new JSONObject(response);
//        try {
////            String email = (String) jsonResponse.get("email");
////            String username = (String) jsonResponse.get("username");
//            String password = (String) jsonResponse.get("password");
//            System.out.println("Current password: " + password);
//            curPwdInput.setText(password);
////            usernameInput.setText(username);
//        } catch (JSONException e) {
//            String errMessage = (String) jsonResponse.get("message");
//            displayGeneralErrMessages(errMessage);
//        }
//    }

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

    private Optional<ButtonType> displayDeleteWarningDialog() {
        // add alert dialog
        Alert alert = new Alert(Alert.AlertType.WARNING);
        String yesTxt = "Yes";
        String noTxt = "No";
        ButtonType yesBtn = new ButtonType(yesTxt);
        ButtonType noBtn = new ButtonType(noTxt);
        alert.setTitle("Warning");
        alert.setContentText("Are you sure you want to delete your account?");
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(yesBtn, noBtn);
//        alert.getButtonTypes().add()
        Optional<ButtonType> result = alert.showAndWait();
        return result;
    }
}


