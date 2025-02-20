package controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.HttpClientSingleton;
import model.TokenStorage;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

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
    private ControllerUtils controllerUtil ;
    private TokenStorage storage;
    private HttpResponseService httpResponseService;

//    private TokenStorage tokenStorage;
//    Client client;


    public void initialize() {
//        TokenStorage.getIntance(); // this step is important, to access to the token storage
        controllerUtil = new ControllerUtils();
        httpResponseService = new HttpResponseServiceImpl();

        String username = TokenStorage.getInfo("username");
        if (username != null) {
            String password = TokenStorage.getInfo("password");
            loginUserInput.setText(username);
            loginPassInput.setText(password);
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
        String password = loginPassInput.getText();
        handleInput(username, password);

    }


    @FXML
    private void loginPageBtnPress(KeyEvent ke) {
        if (ke.getCode() == KeyCode.ENTER) {
            String username = loginUserInput.getText();
            String password = loginPassInput.getText();
            handleInput(username, password);
        }
    }



    @FXML
    private void mouseEnter() {
        this.controllerUtil.setHandCursor(this.backBtn);
        this.controllerUtil.setHandCursor(this.loginBtn);
        this.controllerUtil.setHandCursor(this.registerLabel);
    }

    @FXML
    private void mouseExit() {
        this.controllerUtil.setDefaultCursor(this.backBtn);
        this.controllerUtil.setDefaultCursor(this.loginBtn);
        this.controllerUtil.setDefaultCursor(this.registerLabel);
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

    private boolean isRememberBoxChecked() {
        if (this.rememberBox.isSelected()) {
            return true;
        }
        return false;
    }

    // working on it
    private void handleRememberBox(String username, String password) {
        if (isRememberBoxChecked()) {
            TokenStorage.saveInfo(username, username);
            TokenStorage.saveInfo(password, password);
        } else {
            TokenStorage.clearData(username);
            TokenStorage.clearData(password);
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

        HttpClientSingleton instance = HttpClientSingleton.getInstance();
        CloseableHttpClient httpClient = instance.getHttpClient();

        String URI = "http://localhost:8093/api/users-authentication/login";
        HttpPost httpPost = new HttpPost(URI);
        httpPost.addHeader("accept", "application/json");
        httpPost.addHeader("Content-Type", "application/json");

        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("password", password);

        StringEntity entity = new StringEntity(json.toString());
        httpPost.setEntity(entity);

//        HttpResponseServiceImpl httpResponseService  = new HttpResponseServiceImpl();
        httpResponseService.handleReponse(httpPost,httpClient,this::handleLoginReponse);


    }


    private void handleLoginReponse(CloseableHttpResponse response, JSONObject jsonResponse) {
        String statusCode = response.getStatusLine().toString();
        try {
            String token = (String) jsonResponse.get("token");
            String username = (String) jsonResponse.get("username");
            TokenStorage.saveToken(username, token);
            String savedUsername = TokenStorage.getUser();
            String savedToken = TokenStorage.getToken();
            goToMainPage();

        } catch (JSONException e) {
            String messsage = (String) jsonResponse.get("message");
            displayErrGeneral(messsage);
        }

    }

    private void goToMainPage() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main_pages/main_page.fxml"));
        this.stage = this.getStage();
        controllerUtil.updateStage(stage, fxmlLoader);
    }


}
