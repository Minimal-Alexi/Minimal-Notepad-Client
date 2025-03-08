package controller.groups;


import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
import utils.ControllerUtils;
import utils.HttpResponseService;
import utils.HttpResponseServiceImpl;
import utils.MainPageServices;

import java.io.IOException;


public class GroupInfoCreateController {

    @FXML
    private BorderPane root;

    @FXML
    private Button myNotesBtn;
    @FXML
    private Button mySharedGroupNotesBtn;
    @FXML
    private Button groupsBtn;
    @FXML
    private Button myGroupsBtn;
    @FXML
    private Button allGroupsBtn;
    @FXML
    private Button accountBtn;
    @FXML
    private Button createGroupBtn;
    @FXML
    private Button logOutBtn;


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

            nameLabel.setText("Welcome " + username);
            MainPageServices.updateLocalTime(localTime);
//        httpInstance = HttpClientSingleton.getInstance();


//        myNotesBtn.getStylesheets().add(getClass().getResource(CSSSOURCE +"/button.css").toExternalForm());
            root.getStylesheets().add(getClass().getResource(CSSSOURCE + "/button.css").toExternalForm());
//        root.getStylesheets().add(getClass().getResource(CSSSOURCE +"/search_bar.css").toExternalForm());
//        root.getStylesheets().add(getClass().getResource(CSSSOURCE +"/table_view.css").toExternalForm());
            root.getStylesheets().add(getClass().getResource(CSSSOURCE + "/text_input.css").toExternalForm());
//        root.getStylesheets().add(getClass().getResource(CSSSOURCE +"/groups.css").toExternalForm());
//        root.getStylesheets().add(getClass().getResource(CSSSOURCE +"/button.css").toExternalForm());

            createGroupBtn.getStylesheets().add(getClass().getResource(CSSSOURCE + "/groups.css").toExternalForm());
        }


    }

    @FXML
    public void accountBtnClick() {
        controllerUtils.goPage(stage, accountBtn, FXMLSource + "/main_pages/account_user_info_page.fxml");
    }

    @FXML
    public void allGroupsBtnClick() {
        // go to allgroup page
        controllerUtils.goPage(stage, allGroupsBtn, FXMLSource + "/main_pages/groups/all_groups.fxml");
    }

    @FXML
    public void groupsBtnClick() {
        // go to create group page
        controllerUtils.goPage(stage, createGroupBtn, FXMLSource + "/main_pages/groups/group_info_create_group.fxml");
    }

    @FXML
    public void mouseEnter() {
        controllerUtils.setHandCursor(myNotesBtn);
        controllerUtils.setHandCursor(mySharedGroupNotesBtn);
        controllerUtils.setHandCursor(groupsBtn);
        controllerUtils.setHandCursor(myGroupsBtn);
        controllerUtils.setHandCursor(allGroupsBtn);
        controllerUtils.setHandCursor(accountBtn);
        controllerUtils.setHandCursor(logOutBtn);

        controllerUtils.setHandCursor(createGroupBtn);
    }

    @FXML
    public void mouseExit() {
        controllerUtils.setDefaultCursor(myNotesBtn);
        controllerUtils.setDefaultCursor(mySharedGroupNotesBtn);
        controllerUtils.setDefaultCursor(groupsBtn);
        controllerUtils.setDefaultCursor(myGroupsBtn);
        controllerUtils.setDefaultCursor(allGroupsBtn);
        controllerUtils.setDefaultCursor(accountBtn);
        controllerUtils.setDefaultCursor(logOutBtn);
        controllerUtils.setDefaultCursor(createGroupBtn);
    }


    @FXML
    public void myGroupsBtnClick() {
        // go to my groups page
    }

    @FXML
    public void myNotesBtnClick() {
        // go to my notes page
    }

    @FXML
    public void shareNoteBtnClick() {
        // go to share note page
    }

    @FXML
    public void mySharedGroupNotesBtnClick() {

    }

    @FXML
    public void logOutBtnClick() {
        {
            this.controllerUtils.goToHelloPage(stage, logOutBtn);
        }
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
        } catch (JSONException e) {
            String message = (String) object.get("message");
        }
    }
}
