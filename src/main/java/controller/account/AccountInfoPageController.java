package controller.account;

import controller.PageController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
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

    // Sidebar buttons
    @FXML private Button myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn;

    // Action buttons
    @FXML private Button saveBtn, changePwdBtn, deleteBtn;

    // Labels
    @FXML private Label localTime, userErrLabel, emailErrLabel, generalErrLabel;
    @FXML private Label editYourAccountLabel, changeLanguageLabel, accountDetailLabel, emailLabel, usernameLabel;

    // Inputs
    @FXML private TextField usernameInput, emailInput;
    @FXML private ComboBox<LanguageLabel> languageBox;

    // Constants
    private static final String URI = "http://localhost:8093/api/user/";
    private final LanguageLabel[] supportedLanguages = new LanguageLabel[4];
    private final String USERNAME_KEY = "username";
    private final String MESSAGE_KEY = "message";
    private final String WRONG_EMAIL_FORMAT_KEY = "wrongEmailFormatText";
    private final String UPDATE_SUCCESS_KEY = "updateSuccessText";
    private final String UPDATE_TO_SAVE_KEY = "unableToUpdateText";
    private final String SERVER_EXCEPTION_ERROR_KEY = "serverExeptionText";
    private final String SERVER_ERROR_KEY = "serverErrorText";

    // Services
    private ObservableResourceFactory RESOURCE_FACTORY;
    private MainPageServices mainPageServices;
    private ControllerUtils controllerUtils;
    private HttpResponseService httpResponseService;
    private CloseableHttpClient httpClient;

    // Properties
    private Stage stage;
    private final GeneralErrorKey generalErrorKey = new GeneralErrorKey("");

    @FXML
    public void initialize() {
        initializeServices();
        updateLocalTime(localTime);

        ControllerUtils_v2.addStyle(logOutBtn, "/logout-button.css");
        setSidebarLanguages(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn);

        Platform.runLater(() -> {
            Utils.setupLanguageBox(languageBox, supportedLanguages, RESOURCE_FACTORY, this, this::saveLanguage);
            super.updateDisplay();
        });

        getUserInfo();
    }

    private void initializeServices() {
        mainPageServices = new MainPageServices();
        controllerUtils = new ControllerUtils();
        httpResponseService = new HttpResponseServiceImpl();
        httpClient = HttpClientSingleton.getInstance().getHttpClient();
        RESOURCE_FACTORY = ObservableResourceFactory.getInstance();
        generalErrLabel.setUserData(generalErrorKey);
    }

    @FXML
    public void mouseEnter() {
        setCursor(true);
    }

    @FXML
    public void mouseExit() {
        setCursor(false);
    }

    private void setCursor(boolean isHand) {
        Button[] buttons = { myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn, saveBtn, changePwdBtn, deleteBtn };
        if (isHand) {
            controllerUtils.setHandCursor(buttons);
        } else {
            controllerUtils.setDefaultCursor(buttons);
        }
    }

    @FXML public void myGroupsBtnClick() { ControllerUtils_v2.goToMyGroupsPage(stage, myGroupsBtn); }
    @FXML public void myNotesBtnClick() { ControllerUtils_v2.goToMyNotesPage(stage, myNotesBtn); }
    @FXML public void shareNotesBtnClick() { ControllerUtils_v2.goToMyGroupNotesPage(stage, shareNotesBtn); }
    @FXML public void allGroupsBtnClick() { ControllerUtils_v2.goToAllGroupsPage(stage, allGroupsBtn); }
    @FXML public void accountBtnClick() { ControllerUtils_v2.goToAccountPage(stage, accountBtn); }
    @FXML public void logOutBtnClick() { controllerUtils.logout(stage, logOutBtn); }

    @FXML
    public void saveBtnClick() {
        handleInput(emailInput.getText(), usernameInput.getText());
    }

    @FXML
    public void changePwdClick() {
        stage = controllerUtils.getStage(saveBtn, stage);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main_pages/account_user_password_page.fxml"));
        controllerUtils.updateStage(stage, fxmlLoader);
    }

    @FXML
    public void deleteBtnClick() {
        ResourceBundle rb = RESOURCE_FACTORY.getResourceBundle();
        String yesTxt = rb.getString("yesText");

        Optional<ButtonType> result = Utils.displayDeleteWarningDialog();
        if (result.isPresent() && result.get().getText().equals(yesTxt)) {
            HttpRequestBuilder httpRequest = new HttpRequestBuilder("DELETE", URI, true);
            httpResponseService.handleReponse(httpRequest.getHttpRequestBase(), httpRequest.getHttpClient(), this::handleDeleteResponse);
        }
    }

    private void getUserInfo() {
        HttpRequestBuilder httpRequest = new HttpRequestBuilder("GET", URI, true);
        httpResponseService.handleReponse(httpRequest.getHttpRequestBase(), httpRequest.getHttpClient(), this::handleGetUserInfoResponse);
    }

    private void handleGetUserInfoResponse(CloseableHttpResponse response, Object jsonResponse) {
        JSONObject object = controllerUtils.toJSonObject(jsonResponse);
        try {
            emailInput.setText(object.getString("email"));
            usernameInput.setText(object.getString(USERNAME_KEY));
        } catch (JSONException e) {
            generalErrorKey.setKey(SERVER_EXCEPTION_ERROR_KEY);
            displayGeneralErrMessages(object.optString(MESSAGE_KEY));
        }
    }

    private void handleInput(String email, String username) {
        ResourceBundle rb = RESOURCE_FACTORY.getResourceBundle();
        generalErrLabel.setText("");

        if (username.isEmpty() || email.isEmpty()) {
            displayEmptyErrorMessage();
        } else if (!controllerUtils.validEmail(email)) {
            generalErrorKey.setKey(WRONG_EMAIL_FORMAT_KEY);
            displayGeneralErrMessages(rb.getString(WRONG_EMAIL_FORMAT_KEY));
        } else {
            try {
                saveUserInfo(email, username);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void saveUserInfo(String email, String username) throws IOException {
        resetAllErrMessages();
        HttpRequestBuilder httpRequest = new HttpRequestBuilder("PUT", URI, true);
        httpRequest.updateJsonRequest(USERNAME_KEY, username);
        httpRequest.updateJsonRequest("email", email);
        httpRequest.setRequestBody();

        httpResponseService.handleReponse(httpRequest.getHttpRequestBase(), httpRequest.getHttpClient(), this::handleSaveUserInfoResponse);
    }

    private void handleSaveUserInfoResponse(CloseableHttpResponse response, Object jsonResponse) {
        JSONObject object = controllerUtils.toJSonObject(jsonResponse);
        ResourceBundle rb = RESOURCE_FACTORY.getResourceBundle();

        try {
            if (response.getStatusLine().toString().contains("200")) {
                String newUsername = object.getString(USERNAME_KEY);
                if (!newUsername.equals(TokenStorage.getUser())) {
                    String token = object.getString("token");
                    TokenStorage.saveToken(newUsername, token);
                    TokenStorage.saveInfo(USERNAME_KEY, newUsername);
                }

                generalErrLabel.setTextFill(Color.GREEN);
                generalErrLabel.setText(rb.getString(UPDATE_SUCCESS_KEY));
                generalErrorKey.setKey(UPDATE_SUCCESS_KEY);
            } else {
                generalErrorKey.setKey(SERVER_ERROR_KEY);
                generalErrLabel.setText(object.getString(MESSAGE_KEY));
            }
        } catch (JSONException e) {
            generalErrorKey.setKey(UPDATE_TO_SAVE_KEY);
            generalErrLabel.setText(rb.getString(UPDATE_TO_SAVE_KEY));
        }
    }

    private void handleDeleteResponse(CloseableHttpResponse response, Object jsonResponse) {
        JSONObject object = controllerUtils.toJSonObject(jsonResponse);
        try {
            object.getString(MESSAGE_KEY);
            controllerUtils.logout(stage, deleteBtn);
        } catch (JSONException e) {
            displayGeneralErrMessages(e.getMessage());
        }
    }

    public void saveLanguage() {
        String langKey = RESOURCE_FACTORY.getSelectedLanguage().getKey();
        String uri = URI + "change-language?lang=" + langKey;

        HttpRequestBuilder request = new HttpRequestBuilder("PUT", uri, true);
        httpResponseService.handleReponse(request.getHttpRequestBase(), request.getHttpClient(), this::handleSaveLanguage);
    }

    public void handleSaveLanguage(CloseableHttpResponse response, Object jsonResponse) {
        TokenStorage.saveInfo("lang", RESOURCE_FACTORY.getSelectedLanguage().getKey());
    }

    private void resetAllErrMessages() {
        generalErrLabel.setTextFill(Color.RED);
        userErrLabel.setText("");
        emailErrLabel.setText("");
        generalErrLabel.setText("");
    }

    private void displayGeneralErrMessages(String msg) {
        resetAllErrMessages();
        generalErrLabel.setText(msg);
    }

    private void displayEmptyErrorMessage() {
        ResourceBundle rb = RESOURCE_FACTORY.getResourceBundle();
        if (usernameInput.getText().isEmpty()) {
            userErrLabel.setText(rb.getString("userErrLabel"));
        }
        if (emailInput.getText().isEmpty()) {
            emailErrLabel.setText(rb.getString("emailErrLabel"));
        }
    }

    private void updateEmptyErrorMessagesWhenLanguageChange() {
        ResourceBundle rb = RESOURCE_FACTORY.getResourceBundle();
        if (!userErrLabel.getText().isEmpty()) userErrLabel.setText(rb.getString("userErrLabel"));
        if (!emailErrLabel.getText().isEmpty()) emailErrLabel.setText(rb.getString("emailErrLabel"));
    }

    private void updateGeneralErrorMessageWhenLanguageChange() {
        // Placeholder for future localization if needed
    }

    @Override
    public void updateAllUIComponents() {
        updateLocalTime(localTime);
        updateEmptyErrorMessagesWhenLanguageChange();
        updateGeneralErrorMessageWhenLanguageChange();
        setSidebarLanguages(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn);
    }

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
