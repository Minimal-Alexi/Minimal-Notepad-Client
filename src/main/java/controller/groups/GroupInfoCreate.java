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
import model.HttpClientSingleton;
import model.HttpRequestBuilder;
import model.TokenStorage;
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


public class GroupInfoCreate {

    @FXML
    private BorderPane root;

    @FXML
    private AnchorPane searchPane;
    @FXML
    private TextField searchTextField;

    @FXML
    private Button myNotesBtn;

    @FXML
    private Button shareNoteBtn;

    @FXML
    private Button favoritiesBtn;

    @FXML
    private Button recycleBinBtn;
    @FXML
    private Button groupsBtn;
    @FXML
    private Button groupInfoBtn;
    @FXML
    private Button createGroupBtn;
    @FXML
    private Button editGroupBtn;

    @FXML
    private Button myGroupsBtn;

    @FXML
    private Button allGroupsBtn;

    @FXML
    private Button accountBtn;
    @FXML
    private Button settingBtn;

    @FXML
    private Button createBtn;


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
        System.out.println("start Create Group  Page");

        System.out.println("scene " + scene);
        this.controllerUtils = new ControllerUtils();
        this.httpResponseService = new HttpResponseServiceImpl();

        TokenStorage.getIntance();//
        String username = TokenStorage.getUser();
        String password = TokenStorage.getToken();
        System.out.println("User: " + TokenStorage.getUser() + ", token: " + TokenStorage.getToken());

        nameLabel.setText("Wellcome " + username);
        MainPageServices.updateLocalTime(localTime);
//        httpInstance = HttpClientSingleton.getInstance();


//        myNotesBtn.getStylesheets().add(getClass().getResource(CSSSOURCE +"/button.css").toExternalForm());
        root.getStylesheets().add(getClass().getResource(CSSSOURCE + "/button.css").toExternalForm());
//        root.getStylesheets().add(getClass().getResource(CSSSOURCE +"/search_bar.css").toExternalForm());
//        root.getStylesheets().add(getClass().getResource(CSSSOURCE +"/table_view.css").toExternalForm());
        root.getStylesheets().add(getClass().getResource(CSSSOURCE + "/text_input.css").toExternalForm());
//        root.getStylesheets().add(getClass().getResource(CSSSOURCE +"/groups.css").toExternalForm());
//        root.getStylesheets().add(getClass().getResource(CSSSOURCE +"/button.css").toExternalForm());
        searchPane.getStylesheets().add(getClass().getResource(CSSSOURCE + "/search_bar.css").toExternalForm());
        searchTextField.getStylesheets().add(getClass().getResource(CSSSOURCE + "/text_input.css").toExternalForm());
        createBtn.getStylesheets().add(getClass().getResource(CSSSOURCE + "/groups.css").toExternalForm());


    }

    @FXML
    public void accountBtnClick() {
        controllerUtils.goPage(stage, accountBtn, FXMLSource + "/main_pages/account_user_info_page.fxml");
    }

    @FXML
    public void allGroupsBtnClick() {
        // go to allgroup page
    }

    @FXML
    public void createGroupBtnClick() {
        // go to create group page
        controllerUtils.goPage(stage, createGroupBtn, FXMLSource + "/main_pages/groups/group_info_create_group.fxml");
    }

    @FXML
    public void editGroupBtnClick() {
        // go to edit group page
    }


    @FXML
    public void mouseEnter() {
        controllerUtils.setHandCursor(myNotesBtn);
        controllerUtils.setHandCursor(shareNoteBtn);
        controllerUtils.setHandCursor(favoritiesBtn);
        controllerUtils.setHandCursor(recycleBinBtn);
        controllerUtils.setHandCursor(groupsBtn);
        controllerUtils.setHandCursor(groupInfoBtn);
        controllerUtils.setHandCursor(createGroupBtn);
        controllerUtils.setHandCursor(editGroupBtn);
        controllerUtils.setHandCursor(myGroupsBtn);
        controllerUtils.setHandCursor(allGroupsBtn);
        controllerUtils.setHandCursor(accountBtn);
        controllerUtils.setHandCursor(settingBtn);
        controllerUtils.setHandCursor(createBtn);


    }

    @FXML
    public void mouseExit() {
        controllerUtils.setDefaultCursor(myNotesBtn);
        controllerUtils.setDefaultCursor(shareNoteBtn);
        controllerUtils.setDefaultCursor(favoritiesBtn);
        controllerUtils.setDefaultCursor(recycleBinBtn);
        controllerUtils.setDefaultCursor(groupsBtn);
        controllerUtils.setDefaultCursor(groupInfoBtn);
        controllerUtils.setDefaultCursor(createGroupBtn);
        controllerUtils.setDefaultCursor(editGroupBtn);
        controllerUtils.setDefaultCursor(myGroupsBtn);
        controllerUtils.setDefaultCursor(allGroupsBtn);
        controllerUtils.setDefaultCursor(accountBtn);
        controllerUtils.setDefaultCursor(settingBtn);
        controllerUtils.setDefaultCursor(createBtn);
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
    public void groupsClick() {

    }

    @FXML
    public void groupInfoBtnClick() {

    }

    @FXML
    public void createBtnClick() {
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
        httpResponseService.handleReponse(request,httpClient,this::handleCreateGroup);
    }

    private void handleCreateGroup(CloseableHttpResponse response, JSONObject jsonResponse) {
        String statusCode = response.getStatusLine().toString();
        try {
            System.out.println("response " + response);
        } catch (JSONException e) {
            String message = (String) jsonResponse.get("message");
        }

    }


}
