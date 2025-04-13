package controller.groups;

import controller.PageController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONArray;
import utils.*;


import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static utils.MainPageServices.setSidebarLanguages;
import static utils.MainPageServices.updateNameLabel;

public class AllGroupsController extends PageController implements Initializable {


    //sidebar
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

    //table

    @FXML
    private TableColumn<Group, Integer> idCol;
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

    @FXML
    private Label allGroupsLabel;



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

    private ObservableResourceFactory RESOURCE_FACTORY;

    //    private HttpClientSingleton httpInstance;
    TableColumn<Group, Group> actionOneCol;
    TableColumn<Group, Group> actionTwoCol;


    //    private static final String URI = "http://localhost:8093/api/groups/my-groups";
    private static final String URI = "http://localhost:8093/api/groups";


    //URI API

    private static final String FXML_SOURCE = "/fxml";
    private static final String CSS_SOURCE = "/CSS";


//myTableView.setPlaceholder(new Label("My table is empty message"));


    public void initialize(URL location, ResourceBundle resourceBundle) {
        System.out.println("start Create Group  Page");

        System.out.println("scene " + scene);
        this.controllerUtils = new ControllerUtils();
        this.httpResponseService = new HttpResponseServiceImpl();
        RESOURCE_FACTORY = ObservableResourceFactory.getInstance();

        TokenStorage.getIntance();//
        String username = TokenStorage.getUser();
        String password = TokenStorage.getToken();
        System.out.println("User: " + TokenStorage.getUser() + ", token: " + TokenStorage.getToken());

        updateNameLabel(nameLabel, TokenStorage.getUser());
        MainPageServices.updateLocalTime(localTime);


//        root.getStylesheets().add(getClass().getResource(CSS_SOURCE + "/button.css").toExternalForm());
//        root.getStylesheets().add(getClass().getResource(CSS_SOURCE + "/text_input.css").toExternalForm());
        ControllerUtils_v2.addStyle(root,"/button.css");
        ControllerUtils_v2.addStyle(root,"/text_input.css");
        ControllerUtils_v2.addStyle(logOutBtn,"/logout-button.css");

        actionOneCol=GroupControllerUtils.addGroupColumn(groupTable,"");
        actionTwoCol=GroupControllerUtils.addGroupColumn(groupTable,"");
        GroupControllerUtils.updateTableView(stage,actionOneCol,actionTwoCol,httpResponseService,this::handleGetAllGroups, this::handleJoinOrLeaveOrDeleteResponse);

        // set sidebar language
        setSidebarLanguages(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn);
        Platform.runLater(super::updateDisplay);
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
        ControllerUtils_v2.logout(stage,logOutBtn);
    }

    @FXML
    void mouseEnter() {
        ControllerUtils_v2.setHandCursor(myNotesBtn);
        ControllerUtils_v2.setHandCursor(shareNotesBtn);
        ControllerUtils_v2.setHandCursor(myGroupsBtn);
        ControllerUtils_v2.setHandCursor(allGroupsBtn);
        ControllerUtils_v2.setHandCursor(accountBtn);
        ControllerUtils_v2.setHandCursor(logOutBtn);
    }

    @FXML
    void mouseExit() {
        ControllerUtils_v2.setDefaultCursor(myNotesBtn);
        ControllerUtils_v2.setDefaultCursor(shareNotesBtn);
        ControllerUtils_v2.setDefaultCursor(myGroupsBtn);
        ControllerUtils_v2.setDefaultCursor(allGroupsBtn);
        ControllerUtils_v2.setDefaultCursor(accountBtn);
        ControllerUtils_v2.setDefaultCursor(logOutBtn);
    }

    @FXML
    void tableClicked() {
        System.out.println("table click");
    }

    public void handleGetAllGroups(CloseableHttpResponse response, Object jsonResponse) {

        JSONArray array = controllerUtils.toJSONArray(jsonResponse);

        if (array != null) {

            this.allgroups = GroupControllerUtils.getGroupListInfoFromJSONArray(array);

            GroupControllerUtils.setupGroupTable(groupTable,
                    this.allgroups,
                    idCol,
                    groupNameCol,
                    ownerCol,
                    numOfMembersCol
            );
        }
    }

    public void handleJoinOrLeaveOrDeleteResponse(CloseableHttpResponse response, Object object) {
        GroupControllerUtils.updateTableView(
                stage,
                actionOneCol,
                actionTwoCol,
                httpResponseService,
                this::handleGetAllGroups,
                this::handleJoinOrLeaveOrDeleteResponse
        );
    }




    @Override
    public void bindUIComponents() {
        allGroupsLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("allGroupsLLabel"));
        groupNameCol.textProperty().bind(RESOURCE_FACTORY.getStringBinding("groupNameCol"));
        ownerCol.textProperty().bind(RESOURCE_FACTORY.getStringBinding("ownerCol"));
        numOfMembersCol.textProperty().bind(RESOURCE_FACTORY.getStringBinding("numOfMembersCol"));
    }
}
