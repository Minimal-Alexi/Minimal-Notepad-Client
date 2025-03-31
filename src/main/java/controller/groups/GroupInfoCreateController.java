package controller.groups;


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
import model.TokenStorage;
import model.selected.SelectedGroup;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import utils.*;

import java.io.IOException;

import static utils.MainPageServices.setSidebarLanguages;
import static utils.MainPageServices.updateNameLabel;


public class GroupInfoCreateController {

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


    // properties
    private Stage stage;
    private Scene scene;
    private Parent parent;
//    private

    private ControllerUtils controllerUtils;
    private HttpResponseService httpResponseService;
//    private HttpClientSingleton httpInstance;


    private static final String URI = "http://localhost:8093/api/groups";


    //URI API

    private static final String FXMLSource = "/fxml";
    private static final String CSSSOURCE = "/CSS";

    public void initialize() {
        System.out.println("start Create Group Page");

        // try to get id from selected group
        try {
            SelectedGroup selectedGroup = SelectedGroup.getInstance();

            System.out.println("selected group id: " + selectedGroup.getId());

        } catch (NullPointerException e) {
            System.out.println("there is no selected group id");
        } finally {

            System.out.println("scene " + scene);
            this.controllerUtils = new ControllerUtils();
            this.httpResponseService = new HttpResponseServiceImpl();

            TokenStorage.getIntance();//
            String username = TokenStorage.getUser();
            String password = TokenStorage.getToken();
            System.out.println("User: " + TokenStorage.getUser() + ", token: " + TokenStorage.getToken());

            updateNameLabel(nameLabel, TokenStorage.getUser());
            MainPageServices.updateLocalTime(localTime);
            root.getStylesheets().add(getClass().getResource(CSSSOURCE + "/button.css").toExternalForm());
            root.getStylesheets().add(getClass().getResource(CSSSOURCE + "/text_input.css").toExternalForm());
            createGroupBtn.getStylesheets().add(getClass().getResource(CSSSOURCE + "/groups.css").toExternalForm());
            ControllerUtils_v2.addStyle(logOutBtn,"/logout-button.css");

        }


        // set sidebar language
        setSidebarLanguages(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn);

    }



    //sidebar
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean validInputs(String groupName, String groupDesc) {
        return (!controllerUtils.isInputEmpty(groupName)) && (!controllerUtils.isInputEmpty(groupDesc));
    }

    private void createGroup(String groupName, String groupDesc) throws IOException {

        HttpRequestBuilder httpRequest = new HttpRequestBuilder("POST", URI, true);
        httpRequest.updateJsonRequest("name", groupName);
        httpRequest.updateJsonRequest("description", groupDesc);
        httpRequest.setRequestBody();
        HttpRequestBase request = httpRequest.getHttpRequest();
        CloseableHttpClient httpClient = httpRequest.getHttpClient();
        httpResponseService.handleReponse(request, httpClient, this::handleCreateGroup);
    }

    //    private void handleCreateGroup(CloseableHttpResponse response, JSONObject jsonResponse) {
    private void handleCreateGroup(CloseableHttpResponse response, Object jsonResponse) {

        JSONObject object = controllerUtils.toJSonObject(jsonResponse);
        String statusCode = response.getStatusLine().toString();
        try {
            System.out.println("response " + response);
            this.controllerUtils.goPage(stage,createGroupBtn,FXMLSource+"/main_pages/groups/my_groups.fxml");
        } catch (JSONException e) {
            String message = (String) object.get("message");
        }
    }

    public void mySharedGroupNotesBtnClick(MouseEvent mouseEvent) {

    }

    public void groupsBtnClick(MouseEvent mouseEvent) {

    }
}
