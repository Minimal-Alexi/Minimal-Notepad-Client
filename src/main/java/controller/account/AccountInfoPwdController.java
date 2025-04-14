package controller.account;

import controller.PageController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
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

import static utils.MainPageServices.*;

public class AccountInfoPwdController extends PageController {

    @FXML private PasswordField curPwdInput, newPwdInput, repeatPwdInput;
    @FXML private Label errCurPwd, errNewPwd, errRepeatPwd, generalErrLabel, localTime;
    @FXML private Button myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, deleteBtn, saveBtn, logOutBtn;
    @FXML private Label editAccountLabel, changePasswordLabel, curPasswordLabel, newPwdLabel, repeatPwdLabel;

    private static final String URI = "http://localhost:8093/api/user/";

    private Stage stage;
    private String newPassword;
    private ObservableResourceFactory resourceFactory;

    private final ControllerUtils controllerUtils = new ControllerUtils();
    private final MainPageServices mainPageServices = new MainPageServices();
    private final HttpClientSingleton httpInstance = HttpClientSingleton.getInstance();
    private final HttpResponseService httpResponseService = new HttpResponseServiceImpl();

    public void initialize() {
        System.out.println("Initializing AccountInfoPwdController");

        resourceFactory = ObservableResourceFactory.getInstance();
        ControllerUtils_v2.addStyle(logOutBtn, "/logout-button.css");
        updateLocalTime(localTime);
        TokenStorage.getIntance();

        setSidebarLanguages(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn);
        Platform.runLater(super::updateDisplay);
    }

    @FXML
    public void deleteBtnClick() throws IOException {
        Optional<ButtonType> result = Utils.displayDeleteWarningDialog();
        if (result.isPresent() && result.get().getText().equals("Yes")) {
            HttpRequestBuilder requestBuilder = new HttpRequestBuilder("DELETE", URI, true);
            httpResponseService.handleReponse(requestBuilder.getHttpRequestBase(), requestBuilder.getHttpClient(), this::handleDeleteResponse);
        }
    }

    private void handleDeleteResponse(CloseableHttpResponse response, Object jsonResponse) {
        JSONObject json = controllerUtils.toJSonObject(jsonResponse);
        try {
            String message = json.getString("message");
            System.out.println("User deleted: " + message);
            TokenStorage.clearToken();
            controllerUtils.goPage(stage, deleteBtn, "/fxml/hello_view.fxml");
        } catch (JSONException e) {
            displayGeneralErrMessages(e.getMessage());
        }
    }

    @FXML
    void mouseEnter(MouseEvent event) {
        controllerUtils.setHandCursor(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn, deleteBtn, saveBtn);
    }

    @FXML
    void mouseExit(MouseEvent event) {
        controllerUtils.setDefaultCursor(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn, deleteBtn, saveBtn);
    }

    // Sidebar navigation
    @FXML public void myGroupsBtnClick() { ControllerUtils_v2.goToMyGroupsPage(stage, myGroupsBtn); }
    @FXML public void myNotesBtnClick() { ControllerUtils_v2.goToMyNotesPage(stage, myNotesBtn); }
    @FXML public void shareNotesBtnClick() { ControllerUtils_v2.goToMyGroupNotesPage(stage, shareNotesBtn); }
    @FXML public void allGroupsBtnClick() { ControllerUtils_v2.goToAllGroupsPage(stage, allGroupsBtn); }
    @FXML public void accountBtnClick() { ControllerUtils_v2.goToAccountPage(stage, accountBtn); }
    @FXML public void logOutBtnClick() { controllerUtils.logout(stage, logOutBtn); }

    @FXML
    void saveBtnClick(MouseEvent event) {
        handleInput(curPwdInput.getText(), newPwdInput.getText(), repeatPwdInput.getText());
    }

    private void handleInput(String curPwd, String newPwd, String repeatNewPwd) {
        generalErrLabel.setText("");

        if (curPwd.isEmpty() || newPwd.isEmpty() || repeatNewPwd.isEmpty()) {
            displayEmptyErrorMessages(curPwd, newPwd, repeatNewPwd);
            return;
        }

        if (!newPwd.equals(repeatNewPwd)) {
            displayGeneralErrMessages("New password and Repeat New Password must match");
            return;
        }

        try {
            saveUserPwd(curPwd, newPwd, repeatNewPwd);
        } catch (IOException e) {
            System.out.println("Error saving user password: " + e.getMessage());
        }
    }

    private void saveUserPwd(String curPwd, String newPwd, String repeatNewPwd) throws IOException {
        resetAllErrMessages();
        this.newPassword = newPwd;

        String changePwdURI = URI + "change-password";
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder("PUT", changePwdURI, true);
        requestBuilder.updateJsonRequest("oldPassword", curPwd);
        requestBuilder.updateJsonRequest("newPassword", newPwd);
        requestBuilder.updateJsonRequest("confirmPassword", repeatNewPwd);
        requestBuilder.setRequestBody();

        httpResponseService.handleReponse(requestBuilder.getHttpRequestBase(), requestBuilder.getHttpClient(), this::handleSaveUserinfoResponse);
    }

    private void handleSaveUserinfoResponse(CloseableHttpResponse response, Object jsonResponse) {
        JSONObject json = controllerUtils.toJSonObject(jsonResponse);

        try {
            if (response.getStatusLine().toString().contains("200")) {
                generalErrLabel.setTextFill(Color.GREEN);
                generalErrLabel.setText("Password changed successfully");
                TokenStorage.saveInfo("password", this.newPassword);
            } else {
                generalErrLabel.setText(json.getString("message"));
            }
        } catch (JSONException e) {
            displayGeneralErrMessages("Cannot change password. Please contact admin.");
        }
    }

    private void displayEmptyErrorMessages(String curPwd, String newPwd, String repeatNewPwd) {
        errCurPwd.setText(curPwd.isEmpty() ? getResource("errCurPwdLabel") : "");
        errNewPwd.setText(newPwd.isEmpty() ? getResource("errNewPwdLabel") : "");
        errRepeatPwd.setText(repeatNewPwd.isEmpty() ? getResource("errRepeatPwd") : "");
    }

    private void displayGeneralErrMessages(String errMessage) {
        resetAllErrMessages();
        generalErrLabel.setText(errMessage);
    }

    private void resetAllErrMessages() {
        generalErrLabel.setTextFill(Color.RED);
        errCurPwd.setText("");
        errNewPwd.setText("");
        errRepeatPwd.setText("");
    }

    private String getResource(String key) {
        return resourceFactory.getResourceBundle().getString(key);
    }

    private void updateEmptyErrorMessagesWhenLanguageChange() {
        if (!errCurPwd.getText().isEmpty()) errCurPwd.setText(getResource("errCurPwdLabel"));
        if (!errNewPwd.getText().isEmpty()) errNewPwd.setText(getResource("errNewPwdLabel"));
        if (!errRepeatPwd.getText().isEmpty()) errRepeatPwd.setText(getResource("errRepeatPwd"));
    }

    @Override
    public void updateAllUIComponents() {
        updateEmptyErrorMessagesWhenLanguageChange();
    }

    @Override
    public void bindUIComponents() {
        editAccountLabel.textProperty().bind(resourceFactory.getStringBinding("editAccountLabel"));
        deleteBtn.textProperty().bind(resourceFactory.getStringBinding("deleteBtn"));
        changePasswordLabel.textProperty().bind(resourceFactory.getStringBinding("changePasswordLabel"));
        curPasswordLabel.textProperty().bind(resourceFactory.getStringBinding("curPasswordLabel"));
        curPwdInput.promptTextProperty().bind(resourceFactory.getStringBinding("curPwdInput"));
        newPwdLabel.textProperty().bind(resourceFactory.getStringBinding("newPwdLabel"));
        newPwdInput.promptTextProperty().bind(resourceFactory.getStringBinding("newPwdInput"));
        repeatPwdLabel.textProperty().bind(resourceFactory.getStringBinding("repeatPwdLabel"));
        repeatPwdInput.promptTextProperty().bind(resourceFactory.getStringBinding("repeatPwdInput"));
    }
}
