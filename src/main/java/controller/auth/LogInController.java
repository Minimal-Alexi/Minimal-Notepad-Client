package controller.auth;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.TokenStorage;

import model.HttpRequestBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utils.ControllerUtils;
import utils.HttpResponseService;
import utils.HttpResponseServiceImpl;


import java.io.IOException;


public class LogInController {

    @FXML
    private Button loginBtn;
    @FXML
    private Button backBtn;

    @FXML
    private TextField loginUserInput;
    @FXML
    private PasswordField loginPassInput;
    @FXML
    private TextField loginPassTxtInput;

    @FXML
    private Text errGeneral;
    @FXML
    private Text errUser;
    @FXML
    private Text errPwd;

    @FXML
    Text registerLabel;

    @FXML
    private CheckBox rememberBox;


    private Stage stage;
    private ControllerUtils controllerUtil;
    private TokenStorage storage;
    private HttpResponseService httpResponseService;

    @FXML
    private StackPane maskedStackPane;
    @FXML
    private StackPane unmaskedStackPane;
    @FXML
    private StackPane pwdStackPane;
    @FXML
    private AnchorPane maskedPane;
    @FXML
    private AnchorPane unmaskedPane;

    private boolean pwdIsHidden;


//    private TokenStorage tokenStorage;
//    Client client;


    public void initialize() {
//        TokenStorage.getIntance(); // this step is important, to access to the token storage
        controllerUtil = new ControllerUtils();
        httpResponseService = new HttpResponseServiceImpl();
        pwdIsHidden = true;

        System.out.println(TokenStorage.getIntance());
        String username = TokenStorage.getInfo("username");
        if (username != null) {
            String password = TokenStorage.getInfo("password");
            loginUserInput.setText(username);
            loginPassInput.setText(password);
            this.rememberBox.setSelected(true);
        }
    }

    @FXML
    private void backBtnClick() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/hello_view.fxml"));
        this.stage = this.getStage();
        controllerUtil.updateStage(stage, fxmlLoader);
    }

    @FXML
    private void loginBtnClick() {
        String username = loginUserInput.getText();
        String password = getPassword();
        handleInput(username, password);
        handleRememberBox(username, password);
        System.out.println("username: " + TokenStorage.getInfo("username") + ", password: " + TokenStorage.getInfo("password"));

    }


    @FXML
    private void loginPageBtnPress(KeyEvent ke) {
        if (ke.getCode() == KeyCode.ENTER) {
            String username = loginUserInput.getText();
            String password = getPassword();
            handleInput(username, password);
        }
    }


    @FXML
    private void mouseEnter() {
        this.controllerUtil.setHandCursor(this.backBtn);
        this.controllerUtil.setHandCursor(this.loginBtn);
        this.controllerUtil.setHandCursor(this.registerLabel);
        this.controllerUtil.setHandCursor(this.maskedPane);
        this.controllerUtil.setHandCursor(this.unmaskedPane);
    }

    @FXML
    private void mouseExit() {
        this.controllerUtil.setDefaultCursor(this.backBtn);
        this.controllerUtil.setDefaultCursor(this.loginBtn);
        this.controllerUtil.setDefaultCursor(this.registerLabel);
        this.controllerUtil.setDefaultCursor(this.maskedPane);
        this.controllerUtil.setDefaultCursor(this.unmaskedPane);
    }


    @FXML
    private void registerClick() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/register_view.fxml"));
        this.stage = this.getStage();
        this.controllerUtil.updateStage(stage, fxmlLoader);
    }

    private Stage getStage() {
        if (this.stage == null) {
            this.stage = (Stage) backBtn.getScene().getWindow();
        }
        return this.stage;
    }

//    @FXML
//    private void rememberBoxClick() {
//        String username = loginUserInput.getText();
//        String password = getPassword();
//        handleRememberBox(username, password);
//    }

    private boolean isRememberBoxChecked() {
        if (this.rememberBox.isSelected()) {
            return true;
        }
        return false;
    }

    // working on it
    private void handleRememberBox(String username, String password) {
        System.out.println("remember box is check: " + isRememberBoxChecked());
        String usernameKey = "username";
        String passwordKey = "password";
        String isRemembered = "isRemember";
        if (isRememberBoxChecked()) {
            TokenStorage.saveInfo(usernameKey, username);
            TokenStorage.saveInfo(passwordKey, password);
            TokenStorage.saveToken(isRemembered, "true");
        } else {
            TokenStorage.clearData(usernameKey);
            TokenStorage.clearData(passwordKey);
            TokenStorage.clearData(isRemembered);
        }
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

//        HttpClientSingleton instance = HttpClientSingleton.getInstance();
//        CloseableHttpClient httpClient = instance.getHttpClient();

        String URI = "http://localhost:8093/api/users-authentication/login";
//        HttpPost httpPost = new HttpPost(URI);
//        httpPost.addHeader("accept", "application/json");
//        httpPost.addHeader("Content-Type", "application/json");
//
//        JSONObject json = new JSONObject();
//        json.put("username", username);
//        json.put("password", password);
//
//        StringEntity entity = new StringEntity(json.toString());
//        httpPost.setEntity(entity);

        HttpRequestBuilder httpRequest = new HttpRequestBuilder("POST", URI);
        httpRequest.updateJsonRequest("username", username);
        httpRequest.updateJsonRequest("password", password);
        httpRequest.setRequestBody();
        HttpPost httpPost = (HttpPost) httpRequest.getHttpRequest();
        CloseableHttpClient httpClient = httpRequest.getHttpClient();

        httpResponseService.handleReponse(httpPost, httpClient, this::handleLoginReponse);


    }

    private void handleLoginReponse(CloseableHttpResponse response, Object jsonResponse) {
        String statusCode = response.getStatusLine().toString();

        JSONObject object = controllerUtil.toJSonObject(jsonResponse);
            try {
                String token = (String) object.get("token");
                String username = (String) object.get("username");
                TokenStorage.saveToken(username, token);
                String savedUsername = TokenStorage.getUser();
                String savedToken = TokenStorage.getToken();
                goToMainPage();

            } catch (JSONException e) {
                String messsage = (String) object.get("message");
                displayErrGeneral(messsage);
            }

    }

    private void goToMainPage() {
        String mainPageLink = "/fxml/main_pages/main_page.fxml";
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main_pages/main_page.fxml"));
//        this.stage = this.getStage();
//        controllerUtil.updateStage(stage, fxmlLoader);
        controllerUtil.goPage(stage,loginBtn,mainPageLink);
    }


    @FXML
    private void maskedIconClick() {
        System.out.println("click masked icon");
        System.out.println(pwdStackPane.getChildren());
        int maskedPaneIndex = pwdStackPane.getChildren().indexOf(maskedStackPane);
        int unmaskedPaneIndex = pwdStackPane.getChildren().indexOf(unmaskedStackPane);
        String maskedPwd = loginPassInput.getText();
        System.out.println("masked pwd input: " + maskedPwd);

        System.out.println("masked Pane Index: " + maskedPaneIndex + ", unmasked Pane index: " + unmaskedPaneIndex);

        if (maskedPaneIndex != -1 && unmaskedPaneIndex != -1) {

            Platform.runLater(() -> {
//
                pwdStackPane.getChildren().remove(maskedStackPane);
                pwdStackPane.getChildren().add(unmaskedPaneIndex, maskedStackPane);
                showPassword(maskedPwd);

            });

        }
    }

    @FXML
    private void unmaskedIconClick() {
        System.out.println("click unmasked icon");
        System.out.println(pwdStackPane.getChildren());
        int maskedPaneIndex = pwdStackPane.getChildren().indexOf(maskedStackPane);
        int unmaskedPaneIndex = pwdStackPane.getChildren().indexOf(unmaskedStackPane);
        String unmaskedPwd = loginPassTxtInput.getText();

        System.out.println("unmasked pwd input: " + unmaskedPwd);
        System.out.println("masked Pane Index: " + maskedPaneIndex + ", unmasked Pane index: " + unmaskedPaneIndex);

        if (maskedPaneIndex != -1 && unmaskedPaneIndex != -1) {

            Platform.runLater(() -> {
                pwdStackPane.getChildren().remove(unmaskedStackPane);
                pwdStackPane.getChildren().add(maskedPaneIndex, unmaskedStackPane);
                hidePassword(unmaskedPwd);
            });
        }
    }

    public void showPassword(String password) {
        StringBuilder unmaskedPwdBuilder = new StringBuilder(password);
        loginPassTxtInput.setText(unmaskedPwdBuilder.toString());
        pwdIsHidden = false;
    }

    public void hidePassword(String unmaskedPassword) {
        StringBuilder maskedPwdBuilder = new StringBuilder(unmaskedPassword);
        loginPassInput.setText(maskedPwdBuilder.toString());
        pwdIsHidden = true;
    }

    public String getPassword() {
        String password = "";
        if (pwdIsHidden) {
            password = loginPassInput.getText();
        } else {
            password = loginPassTxtInput.getText();
        }
        return password;
    }
}
