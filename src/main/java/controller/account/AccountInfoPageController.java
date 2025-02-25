package controller.account;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    private Button logOutBtn;

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
        this.controllerUtils.setHandCursor(logOutBtn);
        this.controllerUtils.setHandCursor(groupsBtn);
    }

    @FXML
    public void mouseExit() {
        this.controllerUtils.setDefaultCursor(saveBtn);
        this.controllerUtils.setDefaultCursor(changePwdBtn);
        this.controllerUtils.setDefaultCursor(deleteBtn);
        this.controllerUtils.setDefaultCursor(logOutBtn);
        this.controllerUtils.setDefaultCursor(groupsBtn);
    }

    @FXML
    void groupsBtnClick() {
        controllerUtils.goPage(stage, groupsBtn, "/fxml/main_pages/groups/group_info_create_group.fxml/");

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
//        HttpGet httpGet = new HttpGet(URI);
//        httpGet.addHeader("Accept", "application/json");
//        httpGet.addHeader("Content-Type", "application/json");
//        httpGet.addHeader("Authorization", "Bearer " + token);

        HttpRequestBuilder httpRequest = new HttpRequestBuilder("GET", URI, true);

        // call this method only if you have body in your request
//            httpRequest.setRequestBody();
//        HttpGet httpGet = (HttpGet) httpRequest.getHttpRequest();
        HttpRequestBase httpGet = httpRequest.getHttpRequest();
        CloseableHttpClient httpClient = httpRequest.getHttpClient();

//        JSONObject json = new JSONObject();
//        json.put("username",)
//        httpResponseService.handleReponse(httpGet);
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
//                    handleGetUserInfoResponse(response, jsonResponse);
//                });
//            } catch (IOException e) {
//                Alert a = new Alert(Alert.AlertType.ERROR);
//                a.setContentText("Unable to connect to server. Check your connection or try at a later time. To report this error please contact admin.");
//                a.show();
//            }
//        }).start();
        httpResponseService.handleReponse(httpGet, httpClient, this::handleGetUserInfoResponse);
    }

    private void handleGetUserInfoResponse(CloseableHttpResponse response, JSONObject jsonResponse) {
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
        generalErrLabel.setTextFill(Color.RED);
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

//        String token = TokenStorage.getToken();
//        HttpPut httpPut = new HttpPut(URI);
//        httpPut.addHeader("Accept", "application/json");
//        httpPut.addHeader("Content-Type", "application/json");
//        httpPut.addHeader("Authorization", "Bearer " + token);
//
//        JSONObject json = new JSONObject();
//        json.put("username", username);
//        json.put("email", email);
//
//        StringEntity entity = new StringEntity(json.toString());
//        httpPut.setEntity(entity);

        HttpRequestBuilder httpRequest = new HttpRequestBuilder("PUT", URI, true);

        // set JSON
        httpRequest.updateJsonRequest("username", username);
        httpRequest.updateJsonRequest("email", email);

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
//                    handleSaveUserInfoResponse(response,jsonResponse);
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
        httpResponseService.handleReponse(httpPut, httpClient, this::handleSaveUserInfoResponse);
    }

    public void handleSaveUserInfoResponse(CloseableHttpResponse response, JSONObject jsonResponse) {
        try {
//            StatusLine statusCode = jsonResponse.getStatusLine();
//            String message = (String) jsonResponse.get("message");
            String statusLine = response.getStatusLine().toString();
            if (statusLine.contains("200")) {
                String newUsername = (String) jsonResponse.get("username");
                String curUsername = TokenStorage.getUser();
                // check if username is the same
                // 1. if the same, email change, no token in the response body
                if (!newUsername.equals(curUsername)) {
                    String newToken = (String) jsonResponse.get("token");
                    System.out.println("New: username: " + newUsername + ", token: " + newToken);
                    TokenStorage.saveToken(newUsername, newToken);
                    TokenStorage.saveInfo("username", newUsername);
                }

                // 2. if username is not the same, token in response body
                generalErrLabel.setTextFill(Color.GREEN);
                generalErrLabel.setText("User Information updates successfully");
//                generalErrLabel.setTextFill(Color.RED);
            } else {
                // get message from generic response
                String message = (String) jsonResponse.get("message");
                generalErrLabel.setText(message);
            }
        } catch (JSONException e) {
            generalErrLabel.setText("Unable to save information");
        }
//        String
    }


//    @FXML
//    public void groupsClicked(ActionEvent event) throws IOException {
//        goToPage(stage, scene, event, "/fxml/main_pages/groups/group_info.fxml");
//    }


    @FXML
    public void changePwdClick() {
        this.stage = controllerUtils.getStage(saveBtn, this.stage);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main_pages/account_user_password_page.fxml"));
        this.controllerUtils.updateStage(this.stage, fxmlLoader);
    }

    // sidebar
    @FXML
    public void accountBtnClick() {
        String pageLink = "/fxml/main_pages/account_user_info_page.fxml";
        this.controllerUtils.goPage(stage, accountBtn, pageLink);
    }

    @FXML
    public void deleteBtnClick() {

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
//            HttpDelete httpDelete = (HttpDelete) httpRequest.getHttpRequest();
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
            this.httpResponseService.handleReponse(httpDelete, httpClient, this::handleDeleteResponse);

        }
    }


    @FXML
    public void logOutBtnClick() {
        this.controllerUtils.goToHelloPage(stage, logOutBtn);
    }

    private void handleDeleteResponse(CloseableHttpResponse response, JSONObject jsonResponse) {
        try {
            String message = (String) jsonResponse.get("message");
            System.out.println("message: " + message);
//            String helloPage = "/fxml/hello_view.fxml";
//            TokenStorage.clearToken();
//            controllerUtils.goPage(stage, deleteBtn, helloPage);
            this.controllerUtils.goToHelloPage(stage, deleteBtn);
        } catch (JSONException e) {

            displayGeneralErrMessages(e.getMessage());
        }
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
