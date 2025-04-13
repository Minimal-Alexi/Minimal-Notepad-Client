package controller.groups;


import controller.PageController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.HttpRequestBuilder;
import model.ObservableResourceFactory;
import model.TokenStorage;
import model.selected.SelectedGroup;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import utils.*;

import java.io.IOException;
import java.util.ResourceBundle;

import static utils.MainPageServices.setSidebarLanguages;
import static utils.MainPageServices.updateNameLabel;


public class GroupInfoCreateController extends PageController {

    @FXML
    private BorderPane root;

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
    private Button logOutBtn;

    @FXML
    private Button createGroupBtn;


    @FXML
    private Label localTime;

    @FXML
    private Label nameLabel;

    @FXML
    private TextField groupNameInput;

    @FXML
    private TextField groupDescInput;

    //Error Message
    @FXML
    private Label groupNameErrLabel;
    @FXML
    private Label groupDescErrLabel;

    //UI components Label
    @FXML
    private Label createNewGroupLabel;
    @FXML
    private Label createGroupNameLabel;
    @FXML
    private Label createGroupDescLabel;

    // properties
    private Stage stage;

//    private

    private ControllerUtils controllerUtils;
    private HttpResponseService httpResponseService;
    private ObservableResourceFactory RESOURCE_FACTORY;



    private static final String URI = "http://localhost:8093/api/groups";


    //URI API

    private static final String FXML_SOURCE = "/fxml";
    private static final String CSS_SOURCE = "/CSS";

    public void initialize() {
        RESOURCE_FACTORY = ObservableResourceFactory.getInstance();

        this.controllerUtils = new ControllerUtils();
        this.httpResponseService = new HttpResponseServiceImpl();

        TokenStorage.getIntance();

        updateNameLabel(nameLabel, TokenStorage.getUser());
        MainPageServices.updateLocalTime(localTime);
        root.getStylesheets().add(getClass().getResource(CSS_SOURCE + "/button.css").toExternalForm());
        root.getStylesheets().add(getClass().getResource(CSS_SOURCE + "/text_input.css").toExternalForm());
        createGroupBtn.getStylesheets().add(getClass().getResource(CSS_SOURCE + "/groups.css").toExternalForm());
        ControllerUtils_v2.addStyle(logOutBtn,"/logout-button.css");


        // set sidebar language
        setSidebarLanguages(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn);

        // set up localization
        super.updateDisplay();
    }



    //sidebar
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
    void mouseEnter() {
        this.controllerUtils.setHandCursor(myNotesBtn);
        this.controllerUtils.setHandCursor(shareNotesBtn);
        this.controllerUtils.setHandCursor(myGroupsBtn);
        this.controllerUtils.setHandCursor(allGroupsBtn);
        this.controllerUtils.setHandCursor(accountBtn);
        this.controllerUtils.setHandCursor(logOutBtn);
        this.controllerUtils.setHandCursor(createGroupBtn);
    }

    @FXML
    void mouseExit() {
        this.controllerUtils.setDefaultCursor(myNotesBtn);
        this.controllerUtils.setDefaultCursor(shareNotesBtn);
        this.controllerUtils.setDefaultCursor(myGroupsBtn);
        this.controllerUtils.setDefaultCursor(allGroupsBtn);
        this.controllerUtils.setDefaultCursor(accountBtn);
        this.controllerUtils.setDefaultCursor(logOutBtn);
        this.controllerUtils.setDefaultCursor(createGroupBtn);
    }


    @FXML
    public void createGroupBtnClick() {
        try {
            String groupName = groupNameInput.getText();
            String groupDesc = groupDescInput.getText();
            if (validInputs(groupName, groupDesc)) {
                createGroup(groupName, groupDesc);
            } else if(controllerUtils.isInputEmpty(groupName) || controllerUtils.isInputEmpty(groupDesc)) {
                displayEmptyErrorMessages(groupName, groupDesc);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }

    public void displayEmptyErrorMessages(String groupName, String groupDesc) {
        this.groupNameErrLabel.setText("");
        this.groupDescErrLabel.setText("");

        if (controllerUtils.isInputEmpty(groupName)) {
            displayErrorEmptyGroupName();
        }
        if (controllerUtils.isInputEmpty(groupDesc)) {
            displayErrorEmptyGroupDesc();
        }
    }

    public void updateLocalizedEmptyErrorMessages() {
        if (!groupNameErrLabel.getText().isEmpty()) {
            displayErrorEmptyGroupName();
        }
        if (!groupDescErrLabel.getText().isEmpty()) {
            displayErrorEmptyGroupDesc();
        }
    }

    public void displayErrorEmptyGroupName(){
        ResourceBundle rb = RESOURCE_FACTORY.getResourceBundle();
        this.groupNameErrLabel.setText(rb.getString("groupNameErrLabel"));
    }

    public void displayErrorEmptyGroupDesc(){
        ResourceBundle rb = RESOURCE_FACTORY.getResourceBundle();
        this.groupDescErrLabel.setText(rb.getString("groupDescErrLabel"));
    }

    public boolean validInputs(String groupName, String groupDesc) {
        return (!controllerUtils.isInputEmpty(groupName)) && (!controllerUtils.isInputEmpty(groupDesc));
    }

    private void createGroup(String groupName, String groupDesc) throws IOException {

        HttpRequestBuilder httpRequest = new HttpRequestBuilder("POST", URI, true);
        httpRequest.updateJsonRequest("name", groupName);
        httpRequest.updateJsonRequest("description", groupDesc);
        httpRequest.setRequestBody();

        httpResponseService.handleReponse(
                httpRequest.getHttpRequestBase(),
                httpRequest.getHttpClient(),
                this::handleCreateGroup);
    }

    //    private void handleCreateGroup(CloseableHttpResponse response, JSONObject jsonResponse) {
    private void handleCreateGroup(CloseableHttpResponse response, Object jsonResponse) {

        JSONObject object = controllerUtils.toJSonObject(jsonResponse);
        try {
            System.out.println("response " + response);
            this.controllerUtils.goPage(stage,createGroupBtn,FXML_SOURCE+"/main_pages/groups/my_groups.fxml");
        } catch (JSONException e) {
            String message = (String) object.get("message");
            System.err.println(message);
        }
    }

    @Override
    public void updateAllUIComponents() {
        updateLocalizedEmptyErrorMessages();
    }

    @Override
    public void bindUIComponents() {
        createNewGroupLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("createNewGroupLabel"));
        createGroupNameLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("createGroupNameLabel"));
        groupNameInput.promptTextProperty().bind(RESOURCE_FACTORY.getStringBinding("groupNameInput"));
        createGroupDescLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("createGroupDescLabel"));
        groupDescInput.promptTextProperty().bind(RESOURCE_FACTORY.getStringBinding("groupDescInput"));
        createGroupBtn.textProperty().bind(RESOURCE_FACTORY.getStringBinding("createGroupBtnTxt"));

    }
}
