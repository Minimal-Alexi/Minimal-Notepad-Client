package controller.auth;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.HttpRequestBuilder;
import model.TokenStorage;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import utils.ControllerUtils;
import utils.HttpResponseService;
import utils.HttpResponseServiceImpl;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterController {
    @FXML
    private BorderPane registerPage;

    @FXML
    private Text errGeneral;
    @FXML
    private Text errEmail;
    @FXML
    private Text errUser;
    @FXML
    private Text errPwd;
    @FXML
    private Text errConfirmPwd;
    @FXML
    private Text loginLabel;

    @FXML
    private TextField emailInput;
    @FXML
    private TextField userInput;
    @FXML
    private PasswordField pwdInput;
    @FXML
    private TextField unmaskedPwdInput;
    @FXML
    private PasswordField confirmPwdInput;
    @FXML
    private TextField unmaskedConfirmPwdInput;

    @FXML
    private Button registerBtn;
    @FXML
    private Button backBtn;


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

    @FXML
    private StackPane confirmPwdStackPane;
    @FXML
    private StackPane confirmMaskedStackPane;
    @FXML
    private StackPane confirmUnmaskedStackPane;
    @FXML
    private AnchorPane unmaskedConfirmPane;
    @FXML
    private AnchorPane maskedConfirmPane;


    private Stage stage;
    private ControllerUtils controllerUtil;
    private HttpResponseService httpResponseService;

    private boolean pwdIsHidden;
    private boolean confirmPwdIsHidden;

    public void initialize() {
        controllerUtil = new ControllerUtils();
        httpResponseService = new HttpResponseServiceImpl();
        pwdIsHidden = true;
        confirmPwdIsHidden = true;
    }

    @FXML
    private void registerPagePress(KeyEvent ke) {
        if (ke.getCode() == KeyCode.ENTER) {
            registerBtnClick();
        }
    }

    @FXML
    private void registerBtnClick() {
        String email = emailInput.getText();
        String username = userInput.getText();
        String password = getPassword();
        String confirmPwd = getConfirmPassword();

        handleInput(email, username, password, confirmPwd);
    }

    private void handleInput(String email, String username, String password, String confirmPwd) {
        if (isEmptyInput(email) || isEmptyInput(username) || isEmptyInput(password) || isEmptyInput(confirmPwd)) {
            displayErrMessages(email, username, password, confirmPwd);
        } else if ((!validEmail(email)) || (!samePassword(password, confirmPwd))) {
            checkEmailAndPassword(email, password, confirmPwd);
        } else {
            try {
                register(username, password, email);

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
    }

    private void checkEmailAndPassword(String email, String pwd, String confirmPwd) {
        resetAllErrMesg();
        if (!controllerUtil.validEmail(email)) {
            errEmail.setText("");
            errGeneral.setText("Input email must have format abc@mail.com");
        } else if (!samePassword(pwd, confirmPwd)) {
            errGeneral.setText("password field and confirm password field must be the same");
            errPwd.setText("");
            errConfirmPwd.setText("");
        }
    }

    private void displayErrMessages(String email, String username, String password, String confirmPwd) {
        if (email.equals("")) {
            this.errEmail.setText("email is empty");
        } else {
            this.errEmail.setText("");
        }
        if (username.equals("")) {
            this.errUser.setText("username is empty");
        } else {
            this.errUser.setText("");
        }
        if (password.equals("")) {
            this.errPwd.setText("password is empty");
        } else {
            this.errPwd.setText("");
        }
        if (confirmPwd.equals("")) {
            this.errConfirmPwd.setText("confirmed password is empty");
        } else {
            this.errConfirmPwd.setText("");
        }
    }

    public boolean validEmail(String email) {
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean samePassword(String password, String confirmPwd) {
        return password.equals(confirmPwd);
    }

    private boolean isEmptyInput(String input) {
        return input.equals("");
    }

    @FXML
    private void mouseEnter() {
        this.controllerUtil.setHandCursor(this.backBtn);
        this.controllerUtil.setHandCursor(this.registerBtn);
        this.controllerUtil.setHandCursor(this.loginLabel);
        this.controllerUtil.setHandCursor(this.maskedPane);
        this.controllerUtil.setHandCursor(this.unmaskedPane);
        this.controllerUtil.setHandCursor(this.maskedConfirmPane);
        this.controllerUtil.setHandCursor(this.unmaskedConfirmPane);

    }

    @FXML
    private void mouseExit() {
        this.controllerUtil.setDefaultCursor(this.backBtn);
        this.controllerUtil.setDefaultCursor(this.registerBtn);
        this.controllerUtil.setDefaultCursor(this.loginLabel);
        this.controllerUtil.setDefaultCursor(this.maskedPane);
        this.controllerUtil.setDefaultCursor(this.unmaskedPane);
        this.controllerUtil.setDefaultCursor(this.maskedConfirmPane);
        this.controllerUtil.setDefaultCursor(this.unmaskedConfirmPane);
    }

    @FXML
    private void loginLabelClick() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login_view.fxml"));
        this.stage = this.getStage();
        this.controllerUtil.updateStage(stage, fxmlLoader);

    }

    @FXML
    private void backBtnClick() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/hello_view.fxml"));
        this.stage = this.getStage();
        controllerUtil.updateStage(stage, fxmlLoader);
    }

    private void resetAllErrMesg() {
        errConfirmPwd.setText("");
        errPwd.setText("");
        errGeneral.setText("");
        errUser.setText("");
        errEmail.setText("");
    }

    private void register(String username, String password, String email) throws IOException {
        resetAllErrMesg();

//        HttpClientSingleton instance = HttpClientSingleton.getInstance();
//        CloseableHttpClient httpClient = instance.getHttpClient();

        String URI = "http://localhost:8093/api/users-authentication/register";
//        HttpPost httpPost = new HttpPost(URI);
//        httpPost.addHeader("accept", "application/json");
//        httpPost.addHeader("Content-Type", "application/json");
//
//        JSONObject json = new JSONObject();
//        json.put("username", username);
//        json.put("email", email);
//        json.put("password", password);
//
//        StringEntity entity = new StringEntity(json.toString());
//        httpPost.setEntity(entity);

        HttpRequestBuilder httpRequest = new HttpRequestBuilder("POST", URI);
        httpRequest.updateJsonRequest("username", username);
        httpRequest.updateJsonRequest("email", email);
        httpRequest.updateJsonRequest("password", password);
        httpRequest.setRequestBody();
        HttpPost httpPost = (HttpPost) httpRequest.getHttpRequest();
        // implement later, need to work with handleReponse method
//        HttpRequestBase httpPost =  httpRequest.getHttpRequest();
        CloseableHttpClient httpClient = httpRequest.getHttpClient();


        httpResponseService.handleReponse(httpPost, httpClient, this::handleRegisterResponse);
    }


    // each controller must implemnt its own reponse as callback when work with httpResponse method
    private void handleRegisterResponse(CloseableHttpResponse response, Object jsonResponse) {
        JSONObject object = controllerUtil.toJSonObject(jsonResponse);

        String statusCode = response.getStatusLine().toString();
        try {
            String token = (String) object.get("token");
            String username = (String) object.get("username");
            TokenStorage.saveToken(username, token);
            goToMainPage();
        } catch (JSONException e) {
            String errMessage = (String) object.get("message");
            displayErrGeneral(errMessage);
        }
    }

    private void goToMainPage() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main_pages/main_page.fxml"));
        this.stage = this.getStage();
        controllerUtil.updateStage(stage, fxmlLoader);
    }

    private Stage getStage() {
        if (this.stage == null) {
            this.stage = (Stage) backBtn.getScene().getWindow();
        }
        return this.stage;
    }

    private void displayErrGeneral(String errMess) {
        this.errGeneral.setText(errMess);
        this.errUser.setText("");
        this.errPwd.setText("");
    }


    @FXML
    private void maskedIconClick() {
        System.out.println("click masked icon");
        System.out.println(pwdStackPane.getChildren());
        int maskedPaneIndex = pwdStackPane.getChildren().indexOf(maskedStackPane);
        int unmaskedPaneIndex = pwdStackPane.getChildren().indexOf(unmaskedStackPane);
        String maskedPwd = pwdInput.getText();
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
        String unmaskedPwd = unmaskedPwdInput.getText();

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

    @FXML
    private void maskedConfirmIconClick() {
        System.out.println("click masked icon");
        System.out.println(confirmPwdStackPane.getChildren());
        int maskedPaneIndex = confirmPwdStackPane.getChildren().indexOf(confirmMaskedStackPane);
        int unmaskedPaneIndex = confirmPwdStackPane.getChildren().indexOf(confirmUnmaskedStackPane);
        String maskedConfirmPwd = confirmPwdInput.getText();
        System.out.println("masked pwd input: " + maskedConfirmPwd);

        System.out.println("masked Pane Index: " + maskedPaneIndex + ", unmasked Pane index: " + unmaskedPaneIndex);

        if (maskedPaneIndex != -1 && unmaskedPaneIndex != -1) {

            Platform.runLater(() -> {
//
                confirmPwdStackPane.getChildren().remove(confirmMaskedStackPane);
                confirmPwdStackPane.getChildren().add(unmaskedPaneIndex, confirmMaskedStackPane);
                showConfirmPassword(maskedConfirmPwd);

            });

        }
    }

    @FXML
    private void unmaskedConfirmIconClick() {
        System.out.println("click unmasked icon");
        System.out.println(confirmPwdStackPane.getChildren());
        int maskedPaneIndex = confirmPwdStackPane.getChildren().indexOf(confirmMaskedStackPane);
        int unmaskedPaneIndex = confirmPwdStackPane.getChildren().indexOf(confirmUnmaskedStackPane);
        String unmaskedConfirmPwd = unmaskedConfirmPwdInput.getText();

        System.out.println("unmasked pwd input: " + unmaskedConfirmPwd);
        System.out.println("masked Pane Index: " + maskedPaneIndex + ", unmasked Pane index: " + unmaskedPaneIndex);

        if (maskedPaneIndex != -1 && unmaskedPaneIndex != -1) {

            Platform.runLater(() -> {
                confirmPwdStackPane.getChildren().remove(confirmUnmaskedStackPane);
                confirmPwdStackPane.getChildren().add(maskedPaneIndex, confirmUnmaskedStackPane);
                hideConfirmPassword(unmaskedConfirmPwd);
            });
        }
    }

    public void showPassword(String password) {
        StringBuilder unmaskedPwdBuilder = new StringBuilder(password);
        unmaskedPwdInput.setText(unmaskedPwdBuilder.toString());
        pwdIsHidden = false;
    }

    public void hidePassword(String unmaskedPassword) {
        StringBuilder maskedPwdBuilder = new StringBuilder(unmaskedPassword);
        pwdInput.setText(maskedPwdBuilder.toString());
        pwdIsHidden = true;
    }

    public void showConfirmPassword(String password) {
        StringBuilder unmaskedPwdBuilder = new StringBuilder(password);
        unmaskedConfirmPwdInput.setText(unmaskedPwdBuilder.toString());
        confirmPwdIsHidden = false;
    }

    public void hideConfirmPassword(String unmaskedPassword) {
        StringBuilder maskedPwdBuilder = new StringBuilder(unmaskedPassword);
        confirmPwdInput.setText(maskedPwdBuilder.toString());
        confirmPwdIsHidden = true;
    }

    public String getPassword() {
        String password = "";
        if (pwdIsHidden) {
            password = pwdInput.getText();
        } else {
            password = unmaskedPwdInput.getText();
        }
        return password;
    }

    public String getConfirmPassword() {
        String password = "";
        if (confirmPwdIsHidden) {
            password = confirmPwdInput.getText();
        } else {
            password = unmaskedConfirmPwdInput.getText();
        }
        return password;
    }
}
