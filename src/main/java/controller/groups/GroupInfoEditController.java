package controller.groups;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Group;
import model.GroupOwner;
import model.HttpRequestBuilder;
import model.TokenStorage;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utils.ControllerUtils;
import utils.HttpResponseService;
import utils.HttpResponseServiceImpl;
import utils.MainPageServices;

import java.util.ArrayList;
import java.util.List;

public class GroupInfoEditController {

    @FXML
    private Button accountBtn;

    @FXML
    private Button allGroupsBtn;

    @FXML
    private Button editGroupBtn;

    @FXML
    private TextField groupDescInput;

    @FXML
    private TextField groupNameInput;

    @FXML
    private Button groupsBtn;

    @FXML
    private Label localTime;

    @FXML
    private Button logOutBtn;

    @FXML
    private Button myGroupsBtn;

    @FXML
    private Button myNotesBtn;

    @FXML
    private Button mySharedGroupNotesBtn;

    @FXML
    private Label nameLabel;

    @FXML
    private BorderPane root;

    @FXML
    private Label editedGroupNameid;

    @FXML
    private Label membersInGroup;

    @FXML
    private Label notiLabel1;

    @FXML
    private Label editedGroupNameLabel;
    @FXML
    private Label editedGroupDescLabel;


    private Stage stage;
    private Scene scene;
    private Parent parent;
//    private

    private ControllerUtils controllerUtils;
    private HttpResponseService httpResponseService;
//    private HttpClientSingleton httpInstance;


    private static final String URI = "http://localhost:8093/api/groups/9";


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

        editGroupBtn.getStylesheets().add(getClass().getResource(CSSSOURCE + "/groups.css").toExternalForm());
    //  http://localhost:8093/api/groups/9

        HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder("GET", URI, true);
        HttpRequestBase request = httpRequestBuilder.getHttpRequest();
        CloseableHttpClient httpClient = httpRequestBuilder.getHttpClient();
        httpResponseService.handleReponse(request, httpClient, this::handleGetGroupUserId);

    }

    public void handleGetGroupUserId(CloseableHttpResponse response, Object jsonResponse) {
        JSONObject object = controllerUtils.toJSonObject(jsonResponse);
        try {
            System.out.println(object);
            String email = (String) object.get("email");
            String username = (String) object.get("username");
            emailInput.setText(email);
            usernameInput.setText(username);
        } catch (JSONException e) {
            String errMessage = (String) object.get("message");
        }
    }


    @FXML
    void accountBtnClick() {

    }

    @FXML
    void allGroupsBtnClick() {
        String pageLink = "/fxml/main_pages/groups/group_info_create_group.fxml";
        this.controllerUtils.goPage(stage,editGroupBtn, pageLink);
    }

    @FXML
    void editGroupBtnClick() {
/*
        edit code
*/
        String editedGroupName = groupNameInput.getText();
        String editedDescInput = groupDescInput.getText();

        System.out.println("Edited group name: " + editedGroupName);
        System.out.println("Edited group description name: " + editedDescInput);

        System.out.println("edit button is clicked");

        notiLabel1.setText("You've successfully edit group information.");
        editedGroupNameLabel.setText("Edited group name: " + editedGroupName);
        editedGroupDescLabel.setText("Edited group description: " + editedDescInput);
    }


    @FXML
    void groupsBtnClick() {

    }

    @FXML
    void logOutBtnClick() {

    }

    @FXML
    void mouseEnter() {

    }

    @FXML
    void mouseExit() {

    }

    @FXML
    void myGroupsBtnClick() {

    }

    @FXML
    void myNotesBtnClick() {

    }

    @FXML
    void mySharedGroupNotesBtnClick(

    ) {

    }
}
