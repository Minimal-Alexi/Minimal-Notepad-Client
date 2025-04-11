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
import model.HttpClientSingleton;
import model.HttpRequestBuilder;
import model.ObservableResourceFactory;
import model.TokenStorage;
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

    @FXML
    private PasswordField curPwdInput;

    @FXML
    private Label errCurPwd;

    @FXML
    private Label errNewPwd;

    @FXML
    private Label errRepeatPwd;

    @FXML
    private Label generalErrLabel;

    @FXML
    private Label localTime;

    @FXML
    private PasswordField newPwdInput;

    @FXML
    private PasswordField repeatPwdInput;

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
    private Button deleteBtn;


    @FXML
    private Button saveBtn;

    @FXML
    private Button logOutBtn;

    @FXML
    private Label editAccountLabel;
    @FXML
    private Label changePasswordLabel;
    @FXML
    private Label curPasswordLabel;
    @FXML
    private Label newPwdLabel;
    @FXML
    private Label repeatPwdLabel;




    // properties
    private Stage stage;
    private Scene scene;
    private Parent parent;

    private String username;
    private String email;
    private String newPassword;
//    private

    private MainPageServices mainPageServices;
    private ControllerUtils controllerUtils;
    private HttpResponseService httpResponseService;
    private HttpClientSingleton httpInstance;
    private CloseableHttpClient httpClient;

    private ObservableResourceFactory RESOURCE_FACTORY;

    //URI API
    private static final String URI = "http://localhost:8093/api/user/";

    public void initialize() {
        System.out.println("start Account Password Page");
        this.mainPageServices = new MainPageServices();
        this.controllerUtils = new ControllerUtils();
        this.httpResponseService = new HttpResponseServiceImpl();
        RESOURCE_FACTORY = ObservableResourceFactory.getInstance();

        TokenStorage.getIntance();//
        System.out.println("User: " + TokenStorage.getUser() + ", token: " + TokenStorage.getToken());

        ControllerUtils_v2.addStyle(logOutBtn,"/logout-button.css");
        updateLocalTime(localTime);

        httpInstance = HttpClientSingleton.getInstance();
        httpClient = httpInstance.getHttpClient();
//        getUserInfo();

        // set sidebar language
        setSidebarLanguages(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn);

        Platform.runLater(super::updateDisplay);
    }

    @FXML
    public void deleteBtnClick() throws IOException {
        String yesTxt = "Yes";
//        Optional<ButtonType> result = displayDeleteWarningDialog();
        Optional<ButtonType> result = Utils.displayDeleteWarningDialog();
        System.out.println("result of dialog " + result.get().getText());
        if (result.get().getText().equals(yesTxt)) {
            System.out.println("Deleting user");


            HttpRequestBuilder httpRequest = new HttpRequestBuilder("DELETE", URI, true);

            // call this method only if you have body in your request

            HttpRequestBase httpDelete = httpRequest.getHttpRequest();
            CloseableHttpClient httpClient = httpRequest.getHttpClient();

            httpResponseService.handleReponse(httpDelete, httpClient, this::handleDeleteResponse);
        }
    }

    private void handleDeleteResponse(CloseableHttpResponse response, Object jsonResponse) {

        JSONObject object = controllerUtils.toJSonObject(jsonResponse);

        try {
            String message = (String) object.get("message");
            System.out.println("message: " + message);
            String helloPage = "/fxml/hello_view.fxml";
            TokenStorage.clearToken();
            controllerUtils.goPage(stage, deleteBtn, helloPage);
        } catch (JSONException e) {
//            String errMessage = (String) jsonResponse.get("message");
            displayGeneralErrMessages(e.getMessage());
        }
    }

//    @FXML
//    public void groupsClicked(ActionEvent event) throws IOException {
////        goToPage(stage, scene, event, "/fxml/main_pages/groups_page.fxml");
//        goToPage(stage, scene, event, "/fxml/main_pages/groups/group_info.fxml");
//
//    }

    @FXML
    public void groupsClicked() {

    }

    @FXML
    void mouseEnter(MouseEvent event) {
        this.controllerUtils.setHandCursor(myNotesBtn);
        this.controllerUtils.setHandCursor(shareNotesBtn);
        this.controllerUtils.setHandCursor(myGroupsBtn);
        this.controllerUtils.setHandCursor(allGroupsBtn);
        this.controllerUtils.setHandCursor(accountBtn);
        this.controllerUtils.setHandCursor(logOutBtn);

        this.controllerUtils.setHandCursor(deleteBtn);
//        this.controllerUtils.setHandCursor(groupsBtn);
        this.controllerUtils.setHandCursor(saveBtn);



    }

    @FXML
    void mouseExit(MouseEvent event) {
        this.controllerUtils.setDefaultCursor(myNotesBtn);
        this.controllerUtils.setDefaultCursor(shareNotesBtn);
        this.controllerUtils.setDefaultCursor(myGroupsBtn);
        this.controllerUtils.setDefaultCursor(allGroupsBtn);
        this.controllerUtils.setDefaultCursor(accountBtn);
        this.controllerUtils.setDefaultCursor(logOutBtn);

        this.controllerUtils.setDefaultCursor(deleteBtn);
        this.controllerUtils.setDefaultCursor(saveBtn);
//        this.controllerUtils.setDefaultCursor(groupsBtn);
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
    void saveBtnClick(MouseEvent event) {
        String curPwd = curPwdInput.getText();
        String newPwd = newPwdInput.getText();
        String repeatNewPwd = repeatPwdInput.getText();
        System.out.println("cur pwd: " + curPwd);
        System.out.println("new pwd: " + newPwd);
        System.out.println("repeat new pwd: " + repeatNewPwd);
        handleInput(curPwd, newPwd, repeatNewPwd);
    }

    private void handleInput(String curPwd, String newPwd, String repeatNewPwd) {
        generalErrLabel.setText("");
        if (curPwd.equals("") || newPwd.equals("") || repeatNewPwd.equals("")) {
            displayEmptyErrorMessage(curPwd, newPwd, repeatNewPwd);
        } else if (!samePwdAndRepeatPwd(newPwd, repeatNewPwd)) {
            displayGeneralErrMessages("New password and Repeat New Password Must Match");
        } else {
            try {
                saveUserPwd(curPwd, newPwd, repeatNewPwd);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }


    // must add IOException
    private void saveUserPwd(String curPwd, String newPwd, String repeatNewPwd) throws IOException {
        resetAllErrMessages();


        String changwPwdURI = URI + "change-password";
        HttpRequestBuilder httpRequest = new HttpRequestBuilder("PUT", changwPwdURI, true);

        // set JSON
        httpRequest.updateJsonRequest("oldPassword", curPwd);
        httpRequest.updateJsonRequest("newPassword", newPwd);
        httpRequest.updateJsonRequest("confirmPassword", repeatNewPwd);

        //save new password to update to the user info in the token storage
        this.newPassword = newPwd;

        // call this method only if you have body in your request
        httpRequest.setRequestBody();
//            HttpDelete httpDelete = (HttpDelete) httpRequest.getHttpRequest();
        HttpRequestBase httpPut = httpRequest.getHttpRequest();
        CloseableHttpClient httpClient = httpRequest.getHttpClient();

        this.httpResponseService.handleReponse(httpPut, httpClient, this::handleSaveUserinfoResponse);
        // after the update info successfully, do does below
//        TokenStorage.saveInfo("password", newPwd);
    }

    private void handleSaveUserinfoResponse(CloseableHttpResponse response, Object jsonResponse) {
        JSONObject object = controllerUtils.toJSonObject(jsonResponse);

        try {
            String statusLine = response.getStatusLine().toString();

            if (statusLine.contains("200")) {
                generalErrLabel.setTextFill(Color.GREEN);
                generalErrLabel.setText("User Password changes successfully");
                TokenStorage.saveInfo("password", this.newPassword);


            } else {
                String message = (String) object.get("message");
                generalErrLabel.setText(message);
            }
        } catch (JSONException e) {
            generalErrLabel.setText("Cannot change password. Please contact admin");
        }
        // only with 200, return true, the rest return fa

    }

    private boolean samePwdAndRepeatPwd(String newPwd, String repeatNewPwd) {
        return newPwd.equals(repeatNewPwd);
    }


    private String getEmptyCurPwdErrorMessage() {
        return RESOURCE_FACTORY.getResourceBundle().getString("errCurPwdLabel");
    }

    private String getEmptyNewPwdErrorMessage(){
        return RESOURCE_FACTORY.getResourceBundle().getString("errNewPwdLabel");
    }

    private String getRepeatNewPwdErrorMessage(){
        return RESOURCE_FACTORY.getResourceBundle().getString("errRepeatPwd");
    }

    private void displayEmptyErrorMessage(String curPwd, String newPwd, String repeatNewPwd) {
        if (curPwd.equals("")) {
            this.errCurPwd.setText(getEmptyCurPwdErrorMessage());
        } else {
            this.errCurPwd.setText("");
        }
        if (newPwd.equals("")) {
            this.errNewPwd.setText(getEmptyNewPwdErrorMessage());

        } else {
            this.errNewPwd.setText("");
        }
        if (repeatNewPwd.equals("")) {
            this.errRepeatPwd.setText(getRepeatNewPwdErrorMessage());

        } else {
            this.errRepeatPwd.setText("");
        }

    }
    private void updateEmptyErrorMessagesWhenLanguageChange() {
        ResourceBundle rb = RESOURCE_FACTORY.getResourceBundle();

        if (!errCurPwd.getText().isEmpty()) {
            errCurPwd.setText(getEmptyCurPwdErrorMessage());
        }

        if (!errNewPwd.getText().isEmpty()) {
            errNewPwd.setText(getEmptyNewPwdErrorMessage());
        }

        if (!errRepeatPwd.getText().isEmpty()) {
            errRepeatPwd.setText(getRepeatNewPwdErrorMessage());
        }

        // Add similar check if you want to localize generalErrLabel later
    }

    private void resetAllErrMessages() {
        generalErrLabel.setTextFill(Color.RED);
        errCurPwd.setText("");
        errNewPwd.setText("");
        errRepeatPwd.setText("");
    }

    private void displayGeneralErrMessages(String errMessage) {
        resetAllErrMessages();
        this.generalErrLabel.setText(errMessage);
    }

    @Override
    public void updateAllUIComponents() {
        updateEmptyErrorMessagesWhenLanguageChange();
    }

    @Override
    public void bindUIComponents() {
        editAccountLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("editAccountLabel"));
        deleteBtn.textProperty().bind(RESOURCE_FACTORY.getStringBinding("deleteBtn"));
        changePasswordLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("changePasswordLabel"));
        curPasswordLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("curPasswordLabel"));
        curPwdInput.promptTextProperty().bind(RESOURCE_FACTORY.getStringBinding("curPwdInput"));
        newPwdLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("newPwdLabel"));
        newPwdInput.promptTextProperty().bind(RESOURCE_FACTORY.getStringBinding("newPwdInput"));
        repeatPwdLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("repeatPwdLabel"));
        repeatPwdInput.promptTextProperty().bind(RESOURCE_FACTORY.getStringBinding("repeatPwdInput"));
    }
}


