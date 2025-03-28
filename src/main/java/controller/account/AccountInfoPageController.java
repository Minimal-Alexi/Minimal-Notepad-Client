package controller.account;

import controller.PageController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import utils.*;

import java.io.IOException;
import java.util.Optional;

import static utils.MainPageServices.goToPage;
import static utils.MainPageServices.updateLocalTime;


public class AccountInfoPageController extends PageController {

    //FXML element
    @FXML
    private Button myNotesBtn;
    @FXML
    private Button shareNotesBtn;
    @FXML
    private Button myGroupsBtn;
    @FXML
    private Button allGroupsBtn;
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
    private Label editYourAccountLabel;
    @FXML
    private Label changeLanguageLabel;
    @FXML
    private Label accountDetailLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label usernameLabel;




    @FXML
    private TextField usernameInput;
    @FXML
    private TextField emailInput;

    @FXML private
    ComboBox<LanguageLabel> languageBox;

    private ObservableResourceFactory RESOURCE_FACTORY ;
    private final LanguageLabel[] supportedLanguages = new LanguageLabel[4];


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

        RESOURCE_FACTORY = ObservableResourceFactory.getInstance();

        TokenStorage.getIntance();//
        System.out.println("User: " + TokenStorage.getUser() + ", token: " + TokenStorage.getToken());

//        updateLocalTime(localTime,RESOURCE_FACTORY);
        updateLocalTime(localTime);
        httpInstance = HttpClientSingleton.getInstance();
        httpClient = httpInstance.getHttpClient();
        getUserInfo();
        ControllerUtils_v2.addStyle(logOutBtn,"/logout-button.css");

        RESOURCE_FACTORY.getResources();
//        setupLanguageBox();
        Utils.setupLanguageBox(
                languageBox,
                supportedLanguages,
                RESOURCE_FACTORY,
                this
        );
        super.updateDisplay();
    }

    @FXML
    public void mouseEnter() {
        this.controllerUtils.setHandCursor(myNotesBtn);
        this.controllerUtils.setHandCursor(shareNotesBtn);
        this.controllerUtils.setHandCursor(myGroupsBtn);
        this.controllerUtils.setHandCursor(allGroupsBtn);
        this.controllerUtils.setHandCursor(accountBtn);
        this.controllerUtils.setHandCursor(logOutBtn);
        this.controllerUtils.setHandCursor(saveBtn);
        this.controllerUtils.setHandCursor(changePwdBtn);
        this.controllerUtils.setHandCursor(deleteBtn);

    }

    @FXML
    public void mouseExit() {
        this.controllerUtils.setDefaultCursor(myNotesBtn);
        this.controllerUtils.setDefaultCursor(shareNotesBtn);
        this.controllerUtils.setDefaultCursor(myGroupsBtn);
        this.controllerUtils.setDefaultCursor(allGroupsBtn);
        this.controllerUtils.setDefaultCursor(accountBtn);
        this.controllerUtils.setDefaultCursor(logOutBtn);
        this.controllerUtils.setDefaultCursor(saveBtn);
        this.controllerUtils.setDefaultCursor(changePwdBtn);
        this.controllerUtils.setDefaultCursor(deleteBtn);
//        this.controllerUtils.setDefaultCursor(logOutBtn);
    }

    // sidebar
    @FXML
    public void myGroupsBtnClick() {
//        this.controllerUtils.goPage(stage, myGroupsBtn, "/fxml/main_pages/groups/my_groups.fxml");
        ControllerUtils_v2.goToMyGroupsPage(stage, myGroupsBtn);
    }

    @FXML
    public void myNotesBtnClick() {

//        this.controllerUtils.goPage(stage, myNotesBtn, "/fxml/main_pages/main_page.fxml");
        ControllerUtils_v2.goToMyNotesPage(stage, myNotesBtn);
    }

    @FXML
    public void shareNotesBtnClick() {
//        this.controllerUtils.goPage(stage,shareNoteBtn,"");
        System.out.println("Go to share notes page");
//        this.controllerUtils.goPage(stage, allGroupsBtn, "/fxml/main_pages/groups/my_groups_notes.fxml");
        ControllerUtils_v2.goToMyGroupNotesPage(stage, shareNotesBtn);
    }

    @FXML
    public void allGroupsBtnClick() {
//        this.controllerUtils.goPage(stage, allGroupsBtn, "/fxml/main_pages/groups/all_groups.fxml");
        ControllerUtils_v2.goToAllGroupsPage(stage, allGroupsBtn);
    }

    @FXML
    public void accountBtnClick() {
//        this.controllerUtils.goPage(stage, accountBtn, "/fxml/main_pages/account_user_info_page.fxml");
        ControllerUtils_v2.goToAccountPage(stage, accountBtn);
    }

    @FXML
    public void logOutBtnClick() {
        this.controllerUtils.logout(stage, logOutBtn);
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


        HttpRequestBuilder httpRequest = new HttpRequestBuilder("GET", URI, true);


        HttpRequestBase httpGet = httpRequest.getHttpRequest();
        CloseableHttpClient httpClient = httpRequest.getHttpClient();

        httpResponseService.handleReponse(httpGet, httpClient, this::handleGetUserInfoResponse);
    }

    //    private void handleGetUserInfoResponse(CloseableHttpResponse response, JSONObject jsonResponse) {
    private void handleGetUserInfoResponse(CloseableHttpResponse response, Object jsonResponse) {
//        JSONObject jsonObject = new JSONObject(response);
//        JSONObject object = null;
//        if (jsonResponse instanceof JSONObject){
//            object = (JSONObject) jsonResponse;
//        }
        JSONObject object = controllerUtils.toJSonObject(jsonResponse);
        try {
            String email = (String) object.get("email");
            String username = (String) object.get("username");
            emailInput.setText(email);
            usernameInput.setText(username);
        } catch (JSONException e) {
            String errMessage = (String) object.get("message");
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


        HttpRequestBuilder httpRequest = new HttpRequestBuilder("PUT", URI, true);

        // set JSON
        httpRequest.updateJsonRequest("username", username);
        httpRequest.updateJsonRequest("email", email);

        // call this method only if you have body in your request
        httpRequest.setRequestBody();
//            HttpDelete httpDelete = (HttpDelete) httpRequest.getHttpRequest();
        HttpRequestBase httpPut = httpRequest.getHttpRequest();
        CloseableHttpClient httpClient = httpRequest.getHttpClient();


        httpResponseService.handleReponse(httpPut, httpClient, this::handleSaveUserInfoResponse);
    }

    public void handleSaveUserInfoResponse(CloseableHttpResponse response, Object jsonResponse) {

        JSONObject object = controllerUtils.toJSonObject(jsonResponse);
        try {

            String statusLine = response.getStatusLine().toString();
            if (statusLine.contains("200")) {
                String newUsername = (String) object.get("username");
                String curUsername = TokenStorage.getUser();
                // check if username is the same
                // 1. if the same, email change, no token in the response body
                if (!newUsername.equals(curUsername)) {
                    String newToken = (String) object.get("token");
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
                String message = (String) object.get("message");
                generalErrLabel.setText(message);
            }
        } catch (JSONException e) {
            generalErrLabel.setText("Unable to save information");
        }
//        String
    }

    @FXML
    public void groupsClicked() {

    }

    @FXML
    public void changePwdClick() {
        this.stage = controllerUtils.getStage(saveBtn, this.stage);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main_pages/account_user_password_page.fxml"));
        this.controllerUtils.updateStage(this.stage, fxmlLoader);
    }



    @FXML
    public void deleteBtnClick() {

        String yesTxt = "Yes";

        Optional<ButtonType> result = displayDeleteWarningDialog();
        System.out.println("result of dialog " + result.get().getText());
        if (result.get().getText().equals(yesTxt)) {
            System.out.println("Deleting user");


            HttpRequestBuilder httpRequest = new HttpRequestBuilder("DELETE", URI, true);

            // call this method only if you have body in your request

            HttpRequestBase httpDelete = httpRequest.getHttpRequest();
            CloseableHttpClient httpClient = httpRequest.getHttpClient();

            this.httpResponseService.handleReponse(httpDelete, httpClient, this::handleDeleteResponse);

        }
    }

    private void handleDeleteResponse(CloseableHttpResponse response, Object jsonResponse) {
        JSONObject object = controllerUtils.toJSonObject(jsonResponse);

        try {
            String message = (String) object.get("message");
            System.out.println("message: " + message);
//            String helloPage = "/fxml/hello_view.fxml";
//            TokenStorage.clearToken();
//            controllerUtils.goPage(stage, deleteBtn, helloPage);
            this.controllerUtils.logout(stage, deleteBtn);
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

    @Override
    public void updateAllUIComponents() {
        updateLocalTime(localTime);

    }

    @Override
    public void bindUIComponents() {
//        textInput.promptTextProperty().bind(RESOURCE_FACTORY.getStringBinding("prompt"));
//        button.textProperty().bind(RESOURCE_FACTORY.getStringBinding("button"));
//        nextBtn.textProperty().bind(RESOURCE_FACTORY.getStringBinding("next"));
        editYourAccountLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("editAccountLabel"));
        deleteBtn.textProperty().bind(RESOURCE_FACTORY.getStringBinding("deleteBtn"));
        changeLanguageLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("changeLanguageLabel"));
        accountDetailLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("accountDetailLabel"));
        emailLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("emailLabel"));
        emailInput.promptTextProperty().bind(RESOURCE_FACTORY.getStringBinding("emailInput"));
        usernameLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("usernameLabel"));
        usernameInput.promptTextProperty().bind(RESOURCE_FACTORY.getStringBinding("usernameInput"));
        saveBtn.textProperty().bind(RESOURCE_FACTORY.getStringBinding("saveBtn"));
        changePwdBtn.textProperty().bind(RESOURCE_FACTORY.getStringBinding("changePwdBtn"));



    }
}
