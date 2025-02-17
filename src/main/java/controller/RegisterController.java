package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
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
import org.apache.http.util.EntityUtils;
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
    private PasswordField confirmPwdInput;

    @FXML
    private Button registerBtn;
    @FXML
    private Button backBtn;


    private Stage stage;
    private ControllerUtils controllerUtil;
    private HttpResponseService httpResponseService;

    public void initialize() {
        controllerUtil = new ControllerUtils();
        httpResponseService = new HttpResponseServiceImpl();

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
        String password = pwdInput.getText();
        String confirmPwd = confirmPwdInput.getText();

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
            this.errConfirmPwd.setText("password is empty");
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

    }

    @FXML
    private void mouseExit() {
        this.controllerUtil.setDefaultCursor(this.backBtn);
        this.controllerUtil.setDefaultCursor(this.registerBtn);
        this.controllerUtil.setDefaultCursor(this.loginLabel);
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

        HttpClientSingleton instance = HttpClientSingleton.getInstance();
        CloseableHttpClient httpClient = instance.getHttpClient();

        String URI = "http://localhost:8093/api/users-authentication/register";
        HttpPost httpPost = new HttpPost(URI);
        httpPost.addHeader("accept", "application/json");
        httpPost.addHeader("Content-Type", "application/json");

        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("email", email);
        json.put("password", password);

        StringEntity entity = new StringEntity(json.toString());
        httpPost.setEntity(entity);

        httpResponseService.handleReponse(httpPost,httpClient,this::handleRegisterResponse);
}



    // each controller must implemnt its own reponse as callback when work with httpResponse method
    private void handleRegisterResponse(CloseableHttpResponse response, JSONObject jsonResponse) {
        String statusCode = response.getStatusLine().toString();
        try {
            String token = (String) jsonResponse.get("token");
            String username = (String) jsonResponse.get("username");
            TokenStorage.saveToken(username, token);
            goToMainPage();
        } catch (JSONException e) {
            String errMessage = (String) jsonResponse.get("message");
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
}
