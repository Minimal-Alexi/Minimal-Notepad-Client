package controller.auth;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import controller.PageController;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.HttpRequestBuilder;
import model.ObservableResourceFactory;
import model.TokenStorage;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONObject;
import utils.ControllerUtils;
import utils.HttpResponseService;
import utils.HttpResponseServiceImpl;
import java.io.IOException;
import java.util.regex.Pattern;


public class RegisterController extends PageController {

    // === UI Components ===
    @FXML private BorderPane registerPage;
    @FXML private Text errGeneral, errEmail, errUser, errPwd, errConfirmPwd;
    @FXML private Text emailText, usernameText, loginLabel;
    @FXML private Text passwordText, confirmPwdText, welcomeText, registerTo, noteApp, alreadyHaveAccount;
    @FXML private TextField emailInput, userInput, unmaskedPwdInput, unmaskedConfirmPwdInput;
    @FXML private PasswordField pwdInput, confirmPwdInput;
    @FXML private Button registerBtn, backBtn;
    @FXML private StackPane maskedStackPane, unmaskedStackPane, pwdStackPane;
    @FXML private StackPane confirmPwdStackPane, confirmMaskedStackPane, confirmUnmaskedStackPane;
    @FXML private AnchorPane maskedPane, unmaskedPane, maskedConfirmPane, unmaskedConfirmPane;

    // === Helpers ===
    private final ControllerUtils controllerUtil = new ControllerUtils();
    private final HttpResponseService httpResponseService = new HttpResponseServiceImpl();
    private final ObservableResourceFactory RESOURCE_FACTORY = ObservableResourceFactory.getInstance();

    private boolean pwdIsHidden = true;
    private boolean confirmPwdIsHidden = true;
    private Stage stage;

    // === Init ===
    public void initialize() {
        TokenStorage.getIntance();
        RESOURCE_FACTORY.getResourceBundle();
        Platform.runLater(super::updateDisplay);
    }

    @FXML
    private void registerPagePress(KeyEvent ke) {
        if (ke.getCode() == KeyCode.ENTER) registerBtnClick();
    }

    @FXML
    private void registerBtnClick() {
        String email = emailInput.getText();
        String username = userInput.getText();
        String password = getPassword();
        String confirmPwd = getConfirmPassword();

        if (isEmpty(email, username, password, confirmPwd)) {
            showEmptyErrors(email, username, password, confirmPwd);
        } else if (!isValidEmail(email) || !password.equals(confirmPwd)) {
            showFormatErrors(email, password, confirmPwd);
        } else {
            try {
                submitRegistration(username, password, email);
            } catch (Exception e) {
                errGeneral.setText("Error: " + e.getMessage());
            }
        }
    }

    private boolean isEmpty(String... fields) {
        for (String field : fields) if (field.isEmpty()) return true;
        return false;
    }

    private void showEmptyErrors(String email, String username, String pwd, String confirmPwd) {
        errEmail.setText(email.isEmpty() ? "email is empty" : "");
        errUser.setText(username.isEmpty() ? "username is empty" : "");
        errPwd.setText(pwd.isEmpty() ? "password is empty" : "");
        errConfirmPwd.setText(confirmPwd.isEmpty() ? "confirmed password is empty" : "");
    }

    private void showFormatErrors(String email, String pwd, String confirmPwd) {
        resetAllErrors();
        if (!isValidEmail(email)) {
            errGeneral.setText("Input email must have format abc@mail.com");
        } else if (!pwd.equals(confirmPwd)) {
            errGeneral.setText("Passwords do not match");
        }
    }

    private boolean isValidEmail(String email) {
        return Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
                .matcher(email).matches();
    }

    private void resetAllErrors() {
        errEmail.setText(""); errUser.setText("");
        errPwd.setText(""); errConfirmPwd.setText("");
        errGeneral.setText("");
    }

    private void submitRegistration(String username, String password, String email) throws IOException {
        resetAllErrors();
        String URI = "http://localhost:8093/api/users-authentication/register";
        HttpRequestBuilder request = new HttpRequestBuilder("POST", URI);
        request.updateJsonRequest("username", username);
        request.updateJsonRequest("email", email);
        request.updateJsonRequest("password", password);
        request.updateJsonRequest("language", RESOURCE_FACTORY.getResourceBundle().getLocale().getLanguage());
        request.setRequestBody();

        HttpPost httpPost = (HttpPost) request.getHttpRequestBase();
        CloseableHttpClient httpClient = request.getHttpClient();
        httpResponseService.handleReponse(httpPost, httpClient, this::handleRegisterResponse);
    }

    private void handleRegisterResponse(CloseableHttpResponse response, Object jsonResponse) {
        JSONObject object = controllerUtil.toJSonObject(jsonResponse);
        try {
            TokenStorage.saveToken(object.getString("username"), object.getString("token"));
            goToMainPage();
        } catch (Exception e) {
            displayErrGeneral(object.optString("message", "Unexpected error"));
        }
    }

    private void goToMainPage() {
        controllerUtil.updateStage(getStage(), new FXMLLoader(getClass().getResource("/fxml/main_pages/main_page.fxml")));
    }

    private void displayErrGeneral(String errMess) {
        errGeneral.setText(errMess);
        errUser.setText(""); errPwd.setText("");
    }

    private Stage getStage() {
        if (stage == null) stage = (Stage) backBtn.getScene().getWindow();
        return stage;
    }

    // === UI navigation ===
    @FXML
    private void loginLabelClick() {
        controllerUtil.updateStage(getStage(), new FXMLLoader(getClass().getResource("/fxml/login_view.fxml")));
    }

    @FXML
    private void backBtnClick() {
        controllerUtil.updateStage(getStage(), new FXMLLoader(getClass().getResource("/fxml/hello_view.fxml")));
    }

    @FXML
    private void mouseEnter() {
        controllerUtil.setHandCursor(backBtn, registerBtn, loginLabel, maskedPane, unmaskedPane, maskedConfirmPane, unmaskedConfirmPane);
    }

    @FXML
    private void mouseExit() {
        controllerUtil.setDefaultCursor(backBtn, registerBtn, loginLabel, maskedPane, unmaskedPane, maskedConfirmPane, unmaskedConfirmPane);
    }

    // === Password visibility toggle ===
    @FXML
    private void maskedIconClick() { togglePassword(pwdStackPane, maskedStackPane, unmaskedStackPane, pwdInput.getText(), true); }

    @FXML
    private void unmaskedIconClick() { togglePassword(pwdStackPane, unmaskedStackPane, maskedStackPane, unmaskedPwdInput.getText(), false); }

    @FXML
    private void maskedConfirmIconClick() { togglePassword(confirmPwdStackPane, confirmMaskedStackPane, confirmUnmaskedStackPane, confirmPwdInput.getText(), true); }

    @FXML
    private void unmaskedConfirmIconClick() { togglePassword(confirmPwdStackPane, confirmUnmaskedStackPane, confirmMaskedStackPane, unmaskedConfirmPwdInput.getText(), false); }

    private void togglePassword(StackPane stackPane, StackPane from, StackPane to, String value, boolean hide) {
        Platform.runLater(() -> {
            stackPane.getChildren().remove(from);
            stackPane.getChildren().add(stackPane.getChildren().indexOf(to), from);
            if (hide) {
                toVisibleInput(to).setText(value);
                if (stackPane == pwdStackPane) pwdIsHidden = false; else confirmPwdIsHidden = false;
            } else {
                toMaskedInput(to).setText(value);
                if (stackPane == pwdStackPane) pwdIsHidden = true; else confirmPwdIsHidden = true;
            }
        });
    }

    private TextField toVisibleInput(StackPane pane) {
        return (pane == unmaskedStackPane) ? unmaskedPwdInput : unmaskedConfirmPwdInput;
    }

    private PasswordField toMaskedInput(StackPane pane) {
        return (pane == maskedStackPane) ? pwdInput : confirmPwdInput;
    }

    public String getPassword() {
        return pwdIsHidden ? pwdInput.getText() : unmaskedPwdInput.getText();
    }

    public String getConfirmPassword() {
        return confirmPwdIsHidden ? confirmPwdInput.getText() : unmaskedConfirmPwdInput.getText();
    }

    // === i18n Bindings ===
    @Override
    public void bindUIComponents() {
        welcomeText.textProperty().bind(RESOURCE_FACTORY.getStringBinding("welcomeText"));
        registerTo.textProperty().bind(RESOURCE_FACTORY.getStringBinding("registerTo"));
        noteApp.textProperty().bind(RESOURCE_FACTORY.getStringBinding("noteApp"));
        emailText.textProperty().bind(RESOURCE_FACTORY.getStringBinding("emailText"));
        emailInput.promptTextProperty().bind(RESOURCE_FACTORY.getStringBinding("emailInputPrompt"));
        usernameText.textProperty().bind(RESOURCE_FACTORY.getStringBinding("usernameText"));
        userInput.promptTextProperty().bind(RESOURCE_FACTORY.getStringBinding("userInputPrompt"));
        passwordText.textProperty().bind(RESOURCE_FACTORY.getStringBinding("passwordText"));
        confirmPwdText.textProperty().bind(RESOURCE_FACTORY.getStringBinding("confirmPwdText"));
        pwdInput.promptTextProperty().bind(RESOURCE_FACTORY.getStringBinding("pwdInputPrompt"));
        confirmPwdInput.promptTextProperty().bind(RESOURCE_FACTORY.getStringBinding("confirmPwdInputPrompt"));
        unmaskedPwdInput.promptTextProperty().bind(RESOURCE_FACTORY.getStringBinding("unmaskedPwdInputPrompt"));
        unmaskedConfirmPwdInput.promptTextProperty().bind(RESOURCE_FACTORY.getStringBinding("unmaskedConfirmPwdInputPrompt"));
        alreadyHaveAccount.textProperty().bind(RESOURCE_FACTORY.getStringBinding("alreadyHaveAccount"));
        registerBtn.textProperty().bind(RESOURCE_FACTORY.getStringBinding("registerBtn"));
        backBtn.textProperty().bind(RESOURCE_FACTORY.getStringBinding("backBtn"));
        loginLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("loginLabel"));
    }
}
