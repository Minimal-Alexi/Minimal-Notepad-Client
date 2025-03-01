package controller.groups;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
import org.json.JSONObject;
import utils.ControllerUtils;
import utils.HttpResponseService;
import utils.HttpResponseServiceImpl;
import utils.MainPageServices;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AllGroupsController implements Initializable {


    //sidebar
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
    private Button logOutBtn;

    //table

    @FXML
    private TableColumn<Group, Integer> idCol;
    @FXML
    private TableColumn<?, ?> actionCol;
    @FXML
    private TableColumn<?, ?> editCol;
    @FXML
    private TableColumn<Group, String> groupNameCol;
    @FXML
    private TableColumn<Group, Integer> numOfMembersCol;
    @FXML
    private TableColumn<Group, String> ownerCol;
    @FXML
    private TableView<Group> groupTable;

    @FXML
    private Label localTime;

    @FXML
    private Label nameLabel;

    @FXML
    private BorderPane root;


    // properties
    private Stage stage;
    private Scene scene;
    private Parent parent;
//    private

    // others
    private List<Group> allgroups;
    private ObservableList<Group> groupsInTable;

    private ControllerUtils controllerUtils;
    private HttpResponseService httpResponseService;
//    private HttpClientSingleton httpInstance;


    private static final String URI = "http://localhost:8093/api/groups/my-groups";


    //URI API

    private static final String FXMLSource = "/fxml";
    private static final String CSSSOURCE = "/CSS";

    public void initialize(URL location, ResourceBundle resourceBundle) {
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

        getAllgroups();
//        setupTable();
        // setup table

    }

    public void setupTable() {

        idCol.setCellValueFactory(new PropertyValueFactory<>("Id"));
        groupNameCol.setCellValueFactory(new PropertyValueFactory<>("Name"));
        ownerCol.setCellValueFactory(new PropertyValueFactory<>("GroupOwnerName"));
        numOfMembersCol.setCellValueFactory(new PropertyValueFactory<>("NumberOfMembers"));

        ObservableList<Group> observableList = FXCollections.observableArrayList(this.allgroups);
        groupTable.setItems(observableList);
    }

    @FXML
    void accountBtnClick() {
        controllerUtils.goPage(stage, accountBtn, FXMLSource + "/main_pages/account_user_info_page.fxml");
    }

    @FXML
    void allGroupsBtnClick() {
        controllerUtils.goPage(stage, allGroupsBtn, FXMLSource + "/main_pages/all_groups.fxml");

    }

    @FXML
    void groupsBtnClick() {

    }

    @FXML
    void logOutBtnClick() {
        controllerUtils.goToHelloPage(stage, logOutBtn);
    }


    @FXML
    void mouseEnter() {
        controllerUtils.setHandCursor(myNotesBtn);
        controllerUtils.setHandCursor(mySharedGroupNotesBtn);
        controllerUtils.setHandCursor(groupsBtn);
        controllerUtils.setHandCursor(myGroupsBtn);
        controllerUtils.setHandCursor(allGroupsBtn);
        controllerUtils.setHandCursor(accountBtn);
        controllerUtils.setHandCursor(logOutBtn);


    }

    @FXML
    void mouseExit() {
        controllerUtils.setDefaultCursor(myNotesBtn);
        controllerUtils.setDefaultCursor(mySharedGroupNotesBtn);
        controllerUtils.setDefaultCursor(groupsBtn);
        controllerUtils.setDefaultCursor(myGroupsBtn);
        controllerUtils.setDefaultCursor(allGroupsBtn);
        controllerUtils.setDefaultCursor(accountBtn);
        controllerUtils.setDefaultCursor(logOutBtn);

    }

    @FXML
    void myGroupsBtnClick() {
        controllerUtils.goPage(stage, myGroupsBtn, FXMLSource + "/main_pages/groups/group_info_create_group.fxml");
    }

    @FXML
    void myNotesBtnClick() {
        controllerUtils.goPage(stage, myNotesBtn, FXMLSource + "/main_pages/main_page.fxml");
    }

    @FXML
    void mySharedGroupNotesBtnClick() {

    }


    @FXML
    void tableClicked() {
        System.out.println("table click");
    }

    public void getAllgroups() {
        HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder("GET", URI, true);
        HttpRequestBase request = httpRequestBuilder.getHttpRequest();
        CloseableHttpClient httpClient = httpRequestBuilder.getHttpClient();
        httpResponseService.handleReponse(request, httpClient, this::handleGetAllGroups);
//        return null;
    }

    public void handleGetAllGroups(CloseableHttpResponse response, Object jsonResponse) {
        List<Group> updatedAllGroups = new ArrayList<>();
        JSONArray array = controllerUtils.toJSONArray(jsonResponse);
//        System.out.println(array);
        for (Object groupObject : array) {
            System.out.println(groupObject);
            JSONObject owner = (JSONObject) ((JSONObject) groupObject).get("owner");
            GroupOwner groupOwner = new GroupOwner((int) owner.get("id"), (String) owner.get("username"));
            System.out.println(groupOwner);
            int id = (int) ((JSONObject) groupObject).get("id");
            String name = (String) ((JSONObject) groupObject).get("name");
            String description = (String) ((JSONObject) groupObject).get("description");
            int numberOfMembers = (int) ((JSONObject) groupObject).get("numberOfMembers");
            Group newGroup = new Group(id, name, description, groupOwner, numberOfMembers);
            System.out.println(newGroup);
            updatedAllGroups.add(newGroup);
        }
        this.allgroups = updatedAllGroups;
        System.out.println(this.allgroups);
        setupTable();
    }

//    public void updateAllGroupTable() {
//
//    }

}
