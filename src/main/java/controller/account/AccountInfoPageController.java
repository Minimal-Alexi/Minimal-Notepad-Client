package controller.account;

import controller.PageController;
import javafx.application.Platform;
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
import java.util.ResourceBundle;

import static utils.MainPageServices.setSidebarLanguages;
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

    private ObservableResourceFactory RESOURCE_FACTORY;
    private final LanguageLabel[] supportedLanguages = new LanguageLabel[4];


    // properties
    private Stage stage;
//    private

    private MainPageServices mainPageServices;
    private ControllerUtils controllerUtils;
    private HttpResponseService httpResponseService;
    private HttpClientSingleton httpInstance;
    private CloseableHttpClient httpClient;


    //URI API
    private static final String URI = "http://localhost:8093/api/user/";

    // variable to store key of generalError key and keyValue
    private GeneralErrorKey generalErrorKey;
    private static final String WRONG_EMAIL_FORMAT_KEY = "wrongEmailFormatText";
    private static final String UPDATE_SUCCESS_KEY = "updateSuccessText";
    private static final  String UPDATE_TO_SAVE_KEY = "unableToUpdateText";
    private static final String SERVER_EXCEPTION_ERROR_KEY = "serverExeptionText";
    private static final String SERVER_ERROR_KEY = "serverErrorText";
    private static final String MESSAGE_KEY = "message";

    // key to storage in StorageKey
    private static final String USERNAME_KEY = "username";




    // this method must be public so javafx can use it
    public void initialize() {
        System.out.println("start Account User Page");
        this.mainPageServices = new MainPageServices();
        this.controllerUtils = new ControllerUtils();
        this.httpResponseService = new HttpResponseServiceImpl();
        this.generalErrorKey = new GeneralErrorKey("");
        this.generalErrLabel.setUserData(this.generalErrorKey);

        RESOURCE_FACTORY = ObservableResourceFactory.getInstance();

        TokenStorage.getIntance();//
        System.out.println("User: " + TokenStorage.getUser() + ", token: " + TokenStorage.getToken());

        updateLocalTime(localTime);
        httpInstance = HttpClientSingleton.getInstance();
        httpClient = httpInstance.getHttpClient();
        getUserInfo();
        ControllerUtils_v2.addStyle(logOutBtn,"/logout-button.css");

        RESOURCE_FACTORY.getResourceBundle();

        Platform.runLater(()->{
            Utils.setupLanguageBox(
                    languageBox,
                    supportedLanguages,
                    RESOURCE_FACTORY,
                    this,
                    this::saveLanguage
            );
            super.updateDisplay();
        });


        // set sidebar language
        setSidebarLanguages(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn);

    }

    public void saveLanguage(){
        String languageKey = RESOURCE_FACTORY.getSelectedLanguage().getKey();
        String savingLanguageURI = URI+ "change-language?lang=" + languageKey;

        HttpRequestBuilder httpRequest = new HttpRequestBuilder("PUT", savingLanguageURI, true);

        httpResponseService.handleReponse(
                httpRequest.getHttpRequestBase(),
                httpRequest.getHttpClient(),
                this::handleSaveLanguage
        );

    }

    public void handleSaveLanguage(CloseableHttpResponse response, Object jsonResponse){// save selected language to client
        System.out.println("Update UI after saving language from User Detail Page successful");
        String languageKey = RESOURCE_FACTORY.getSelectedLanguage().getKey();
        TokenStorage.saveInfo("lang", languageKey);
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
    }

    // sidebar
    @FXML
    public void myGroupsBtnClick() {
        ControllerUtils_v2.goToMyGroupsPage(stage, myGroupsBtn);
    }

    @FXML
    public void myNotesBtnClick() {
        ControllerUtils_v2.goToMyNotesPage(stage, myNotesBtn);
    }

    @FXML
    public void shareNotesBtnClick() {
        ControllerUtils_v2.goToMyGroupNotesPage(stage, shareNotesBtn);
    }

    @FXML
    public void allGroupsBtnClick() {
        ControllerUtils_v2.goToAllGroupsPage(stage, allGroupsBtn);
    }

    @FXML
    public void accountBtnClick() {
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
        HttpRequestBuilder httpRequest = new HttpRequestBuilder("GET", URI, true);
        httpResponseService.handleReponse(
                httpRequest.getHttpRequestBase(),
                httpRequest.getHttpClient(),
                this::handleGetUserInfoResponse);
    }

    private void handleGetUserInfoResponse(CloseableHttpResponse response, Object jsonResponse) {

        JSONObject object = controllerUtils.toJSonObject(jsonResponse);
        try {
            String email = (String) object.get("email");
            String username = (String) object.get(USERNAME_KEY);
            emailInput.setText(email);
            usernameInput.setText(username);
        } catch (JSONException e) {
            String errMessage = (String) object.get(MESSAGE_KEY);

            generalErrorKey.setKey(SERVER_EXCEPTION_ERROR_KEY);
            displayGeneralErrMessages(errMessage);
        }
    }

    private void handleInput(String email, String username) {
        ResourceBundle rb = RESOURCE_FACTORY.getResourceBundle();
        generalErrLabel.setText("");
        if (username.equals("") || email.equals("")) {
            displayEmptyErrorMessage();
        } else if (!controllerUtils.validEmail(email)) {
            String wrongEmailFormatMessage = rb.getString(WRONG_EMAIL_FORMAT_KEY);
            generalErrorKey.setKey(WRONG_EMAIL_FORMAT_KEY);
//            displayGeneralErrMessages("Wrong email format. Should follow xyz@mail.com");
            displayGeneralErrMessages(wrongEmailFormatMessage);
//            generalErrLabel.setUserData(generalErrorKey);
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

    private void displayEmptyErrorMessage() {
        ResourceBundle rb = RESOURCE_FACTORY.getResourceBundle();
        String emailInputText = emailInput.getText();
        String usernameInputText = usernameInput.getText();

        if (usernameInputText.equals("")) {
            String userErrMessage = rb.getString("userErrLabel");
            this.userErrLabel.setText(userErrMessage);

        } else {
            this.userErrLabel.setText("");
        }
        if (emailInputText.equals("")) {
            String emailErrMessage = rb.getString("emailErrLabel");
            this.emailErrLabel.setText(emailErrMessage);

        } else {
            this.emailErrLabel.setText("");
        }
    }

    private void updateEmptyErrorMessagesWhenLanguageChange() {
        ResourceBundle rb = RESOURCE_FACTORY.getResourceBundle();

        if (!userErrLabel.getText().isEmpty()) {
            userErrLabel.setText(rb.getString("userErrLabel"));
        }

        if (!emailErrLabel.getText().isEmpty()) {
            emailErrLabel.setText(rb.getString("emailErrLabel"));
        }

        // Add similar check if you want to localize generalErrLabel later
    }

    private void updateGeneralErrorMessageWhenLanguageChange(){
        if ( !generalErrLabel.getText().isEmpty()){
        ResourceBundle rb = RESOURCE_FACTORY.getResourceBundle();
        }
    }

    public void saveUserInfo(String email, String username) throws IOException {
        resetAllErrMessages();


        HttpRequestBuilder httpRequest = new HttpRequestBuilder("PUT", URI, true);

        // set JSON
        httpRequest.updateJsonRequest(USERNAME_KEY, username);
        httpRequest.updateJsonRequest("email", email);

        // call this method only if you have body in your request
        httpRequest.setRequestBody();
        httpResponseService.handleReponse(
                httpRequest.getHttpRequestBase(),
                httpRequest.getHttpClient(),
                this::handleSaveUserInfoResponse);
    }

    public void handleSaveUserInfoResponse(CloseableHttpResponse response, Object jsonResponse) {

        JSONObject object = controllerUtils.toJSonObject(jsonResponse);
        ResourceBundle rb = RESOURCE_FACTORY.getResourceBundle();
        try {

            String statusLine = response.getStatusLine().toString();
            if (statusLine.contains("200")) {
                String newUsername = (String) object.get(USERNAME_KEY);
                String curUsername = TokenStorage.getUser();
                // check if username is the same
                // 1. if the same, email change, no token in the response body
                if (!newUsername.equals(curUsername)) {
                    String newToken = (String) object.get("token");
                    TokenStorage.saveToken(newUsername, newToken);
                    TokenStorage.saveInfo(USERNAME_KEY, newUsername);
                }

                // 2. if username is not the same, token in response body
                generalErrLabel.setTextFill(Color.GREEN);

//                generalErrLabel.setText("User Information updates successfully");
                String updateSuccessMessage = rb.getString(UPDATE_SUCCESS_KEY);
                generalErrLabel.setText(updateSuccessMessage);
                generalErrorKey.setKey(UPDATE_SUCCESS_KEY);
//                generalErrLabel.setUserData(generalErrorKey);
//                generalErrLabel.setTextFill(Color.RED);
            } else {
                // get message from generic response

                String message = (String) object.get(MESSAGE_KEY);
                generalErrorKey.setKey(SERVER_ERROR_KEY);
                generalErrLabel.setText(message);
//                generalErrLabel.setUserData(generalErrorKey);
            }
        } catch (JSONException e) {
//            generalErrLabel.setText("Unable to save information");
            String unabletoSaveMessage = rb.getString("unableToSaveText");
            generalErrorKey.setKey(UPDATE_TO_SAVE_KEY);
            generalErrLabel.setText(rb.getString(unabletoSaveMessage));
//            generalErrLabel.setUserData(generalErrorKey);

        }
//
    }

    @FXML
    public void changePwdClick() {
        this.stage = controllerUtils.getStage(saveBtn, this.stage);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main_pages/account_user_password_page.fxml"));
        this.controllerUtils.updateStage(this.stage, fxmlLoader);
    }



    @FXML
    public void deleteBtnClick() {

        ResourceBundle rb = RESOURCE_FACTORY.getResourceBundle();
        String yesTxt =rb.getString("yesText");

        Optional<ButtonType> result = Utils.displayDeleteWarningDialog();
        System.out.println("result of dialog " + result.get().getText());
        if (result.get().getText().equals(yesTxt)) {
            System.out.println("Deleting user");
            HttpRequestBuilder httpRequest = new HttpRequestBuilder("DELETE", URI, true);

            // call this method only if you have body in your request
            this.httpResponseService.handleReponse(httpRequest.getHttpRequestBase(), httpRequest.getHttpClient(), this::handleDeleteResponse);

        }
    }

    private void handleDeleteResponse(CloseableHttpResponse response, Object jsonResponse) {
        JSONObject object = controllerUtils.toJSonObject(jsonResponse);

        try {
            this.controllerUtils.logout(stage, deleteBtn);
        } catch (JSONException e) {
            displayGeneralErrMessages(e.getMessage());
        }
    }

//
    // include in this method all the update/display methods of this controller
    // eg: display error message, update local time, show/hide buttons
    //
    @Override
    public void updateAllUIComponents() {
        updateLocalTime(localTime);
        updateEmptyErrorMessagesWhenLanguageChange();
        updateGeneralErrorMessageWhenLanguageChange();

        // set sidebar language
        setSidebarLanguages(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn);

    }

    // this method is used to bind the UI components with the resource bundle
    // these UI components will be constant throughout the user session in this page
    // example, sidebar, page title, page labels/ input, save button, etc
    @Override
    public void bindUIComponents() {
        editYourAccountLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("editYourAccountLabel"));
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
