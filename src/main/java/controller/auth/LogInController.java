package controller.auth;

import controller.PageController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import utils.ControllerUtils;
import utils.HttpResponseService;
import utils.HttpResponseServiceImpl;

import java.io.IOException;

public class LogInController extends PageController {

    // FXML UI Components
    @FXML private Button loginBtn, backBtn;
    @FXML private TextField loginUserInput, loginPassTxtInput;
    @FXML private PasswordField loginPassInput;
    @FXML private Text errGeneral, errUser, errPwd, signInText, registerLabel;
    @FXML private CheckBox rememberBox;
    @FXML private Text welcomeText, noteApp, passwordText, DontHaveAccountText, usernameText;
    @FXML private StackPane maskedStackPane, unmaskedStackPane, pwdStackPane;
    @FXML private AnchorPane maskedPane, unmaskedPane;

    // State
    private boolean pwdIsHidden = true;
    private Stage stage;
    private final ControllerUtils controllerUtil = new ControllerUtils();
    private final HttpResponseService httpResponseService = new HttpResponseServiceImpl();
    private ObservableResourceFactory RESOURCE_FACTORY;

    // Constants
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";
    private static final String REMEMBER_TOKEN = "isRemember";

    @FXML
    public void initialize() {
        TokenStorage.getIntance();
        RESOURCE_FACTORY = ObservableResourceFactory.getInstance();

        prefillCredentialsIfRemembered();
        Platform.runLater(super::updateDisplay);
    }

    private void prefillCredentialsIfRemembered() {
        String savedUsername = TokenStorage.getInfo(USERNAME_KEY);
        if (savedUsername != null) {
            loginUserInput.setText(savedUsername);
            loginPassInput.setText(TokenStorage.getInfo(PASSWORD_KEY));
            rememberBox.setSelected(true);
        }
    }

    @FXML
    private void backBtnClick() {
        switchScene("/fxml/hello_view.fxml");
    }

    @FXML
    private void loginBtnClick() throws IOException {
        String username = loginUserInput.getText();
        String password = getPassword();

        clearErrorMessages();
        if (validateInputs(username, password)) {
            handleRememberBox(username, password);
            attemptLogin(username, password);
        }
    }

    private boolean validateInputs(String username, String password) {
        boolean valid = true;

        if (username.isEmpty()) {
            errUser.setText("Username is empty");
            valid = false;
        }

        if (password.isEmpty()) {
            errPwd.setText("Password is empty");
            valid = false;
        }

        return valid;
    }

    private void attemptLogin(String username, String password) throws IOException {
        clearErrorMessages();

        String URI = "http://localhost:8093/api/users-authentication/login";

        HttpRequestBuilder httpRequest = new HttpRequestBuilder("POST", URI)
                .updateJsonRequest(USERNAME_KEY, username)
                .updateJsonRequest(PASSWORD_KEY, password)
                .addHeader("Accept-Language", RESOURCE_FACTORY.getResourceBundle().getLocale().getLanguage());

        httpRequest.setRequestBody();

        HttpPost httpPost = (HttpPost) httpRequest.getHttpRequestBase();
        CloseableHttpClient httpClient = httpRequest.getHttpClient();

        httpResponseService.handleReponse(httpPost, httpClient, this::handleLoginResponse);
    }

    private void clearErrorMessages() {
        this.errUser.setText("");
        this.errPwd.setText("");
        this.errGeneral.setText("");
    }


    private void handleLoginResponse(CloseableHttpResponse response, Object jsonResponse) {
        JSONObject object = controllerUtil.toJSonObject(jsonResponse);
        try {
            String token = object.getString("token");
            String username = object.getString(USERNAME_KEY);
            String languageCode = object.getString("languageCode");

            TokenStorage.saveToken(username, token);
            RESOURCE_FACTORY.changeLanguage(languageCode);

            goToMainPage();
        } catch (JSONException e) {
            displayErrGeneral(object.optString("message", "Unexpected error"));
        }
    }

    private void goToMainPage() {
        switchScene("/fxml/main_pages/main_page.fxml");
    }

    private void switchScene(String fxmlPath) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
        controllerUtil.updateStage(getStage(), fxmlLoader);
    }

    private Stage getStage() {
        if (stage == null) {
            stage = (Stage) backBtn.getScene().getWindow();
        }
        return stage;
    }

    private void handleRememberBox(String username, String password) {
        if (rememberBox.isSelected()) {
            TokenStorage.saveInfo(USERNAME_KEY, username);
            TokenStorage.saveInfo(PASSWORD_KEY, password);
            TokenStorage.saveToken(REMEMBER_TOKEN, "true");
        } else {
            TokenStorage.clearData(USERNAME_KEY);
            TokenStorage.clearData(PASSWORD_KEY);
            TokenStorage.clearData(REMEMBER_TOKEN);
        }
    }

    private void displayErrGeneral(String errMessage) {
        errGeneral.setText(errMessage);
    }

    // Password visibility toggle
    @FXML
    private void maskedIconClick() {
        togglePasswordVisibility(false, loginPassInput.getText());
    }

    @FXML
    private void unmaskedIconClick() {
        togglePasswordVisibility(true, loginPassTxtInput.getText());
    }

    private void togglePasswordVisibility(boolean hide, String password) {
        Platform.runLater(() -> {
            if (hide) {
                maskedStackPane.setVisible(false);
                unmaskedStackPane.setVisible(true);
                loginPassInput.setText(password);
                loginPassTxtInput.setText(password);
            } else {
                maskedStackPane.setVisible(true);
                unmaskedStackPane.setVisible(false);
                loginPassInput.setText(password);
                loginPassTxtInput.setText(password);
            }
            pwdIsHidden = hide;
        });
    }

    private String getPassword() {
        return pwdIsHidden ? loginPassInput.getText() : loginPassTxtInput.getText();
    }

    // UI Handlers
    @FXML
    private void mouseEnter(MouseEvent event) {
        controllerUtil.setHandCursor(loginBtn, backBtn, registerLabel, maskedPane, unmaskedPane);
    }

    @FXML
    private void mouseExit(MouseEvent event) {
        controllerUtil.setDefaultCursor(loginBtn, backBtn, registerLabel, maskedPane, unmaskedPane);
    }

    @FXML
    private void registerClick() {
        switchScene("/fxml/register_view.fxml");
    }

    @Override
    public void bindUIComponents() {
        welcomeText.textProperty().bind(RESOURCE_FACTORY.getStringBinding("welcomeText"));
        signInText.textProperty().bind(RESOURCE_FACTORY.getStringBinding("signInText"));
        noteApp.textProperty().bind(RESOURCE_FACTORY.getStringBinding("noteApp"));
        usernameText.textProperty().bind(RESOURCE_FACTORY.getStringBinding("usernameText"));
        passwordText.textProperty().bind(RESOURCE_FACTORY.getStringBinding("passwordText"));
        loginUserInput.promptTextProperty().bind(RESOURCE_FACTORY.getStringBinding("userInputPrompt"));
        loginPassInput.promptTextProperty().bind(RESOURCE_FACTORY.getStringBinding("loginPassInputPrompt"));
        loginPassTxtInput.promptTextProperty().bind(RESOURCE_FACTORY.getStringBinding("loginPassTxtInputPrompt"));
        rememberBox.textProperty().bind(RESOURCE_FACTORY.getStringBinding("rememberBox"));
        loginBtn.textProperty().bind(RESOURCE_FACTORY.getStringBinding("loginBtn"));
        DontHaveAccountText.textProperty().bind(RESOURCE_FACTORY.getStringBinding("DontHaveAccountText"));
        registerLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("registerLabel"));
        backBtn.textProperty().bind(RESOURCE_FACTORY.getStringBinding("backBtn"));
    }
}
