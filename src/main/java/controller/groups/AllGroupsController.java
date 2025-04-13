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
import org.json.JSONArray;
import utils.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import static utils.MainPageServices.setSidebarLanguages;
import static utils.MainPageServices.updateNameLabel;

public class AllGroupsController extends PageController implements Initializable {

    // Sidebar buttons
    @FXML private Button myNotesBtn;
    @FXML private Button shareNotesBtn;
    @FXML private Button myGroupsBtn;
    @FXML private Button allGroupsBtn;
    @FXML private Button accountBtn;
    @FXML private Button logOutBtn;

    // Table columns
    @FXML private TableView<Group> groupTable;
    @FXML private TableColumn<Group, Integer> idCol;
    @FXML private TableColumn<Group, String> groupNameCol;
    @FXML private TableColumn<Group, Integer> numOfMembersCol;
    @FXML private TableColumn<Group, String> ownerCol;

    // UI elements
    @FXML private Label localTime;
    @FXML private Label nameLabel;
    @FXML private Label allGroupsLabel;
    @FXML private BorderPane root;

    // Internal state
    private Stage stage;
    private Scene scene;
    private Parent parent;

    private List<Group> allgroups;
    private ObservableList<Group> groupsInTable;

    private TableColumn<Group, Group> actionOneCol;
    private TableColumn<Group, Group> actionTwoCol;

    private ControllerUtils controllerUtils;
    private HttpResponseService httpResponseService;
    private ObservableResourceFactory resourceFactory;

    private static final String GROUPS_URI = "http://localhost:8093/api/groups";

    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
        System.out.println("Start All Groups Page");

        this.controllerUtils = new ControllerUtils();
        this.httpResponseService = new HttpResponseServiceImpl();
        this.resourceFactory = ObservableResourceFactory.getInstance();

        initializeUserSession();
        initializeUIStyling();
        initializeTableColumns();
        setSidebarLanguages(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn);

        Platform.runLater(super::updateDisplay);
    }

    private void initializeUserSession() {
        TokenStorage.getIntance();
        updateNameLabel(nameLabel, TokenStorage.getUser());
        MainPageServices.updateLocalTime(localTime);
    }

    private void initializeUIStyling() {
        ControllerUtils_v2.addStyle(root, "/button.css");
        ControllerUtils_v2.addStyle(root, "/text_input.css");
        ControllerUtils_v2.addStyle(logOutBtn, "/logout-button.css");
    }

    private void initializeTableColumns() {
        actionOneCol = GroupControllerUtils.addGroupColumn(groupTable, "");
        actionTwoCol = GroupControllerUtils.addGroupColumn(groupTable, "");
        GroupControllerUtils.updateTableView(
                stage,
                actionOneCol,
                actionTwoCol,
                httpResponseService,
                this::handleGetAllGroups,
                this::handleJoinOrLeaveOrDeleteResponse
        );
    }

    @FXML
    private void myGroupsBtnClick() {
        ControllerUtils_v2.goToMyGroupsPage(stage, myGroupsBtn);
    }

    @FXML
    private void myNotesBtnClick() {
        ControllerUtils_v2.goToMyNotesPage(stage, myNotesBtn);
    }

    @FXML
    private void shareNotesBtnClick() {
        ControllerUtils_v2.goToMyGroupNotesPage(stage, shareNotesBtn);
    }

    @FXML
    private void allGroupsBtnClick() {
        ControllerUtils_v2.goToAllGroupsPage(stage, allGroupsBtn);
    }

    @FXML
    private void accountBtnClick() {
        ControllerUtils_v2.goToAccountPage(stage, accountBtn);
    }

    @FXML
    private void logOutBtnClick() {
        ControllerUtils_v2.logout(stage, logOutBtn);
    }

    @FXML
    private void mouseEnter() {
        setSidebarCursor(true);
    }

    @FXML
    private void mouseExit() {
        setSidebarCursor(false);
    }

    private void setSidebarCursor(boolean hand) {
        Button[] buttons = {myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn};
        for (Button button : buttons) {
            if (hand) ControllerUtils_v2.setHandCursor(button);
            else ControllerUtils_v2.setDefaultCursor(button);
        }
    }

    @FXML
    private void tableClicked() {
        System.out.println("Table clicked");
    }

    public void handleGetAllGroups(CloseableHttpResponse response, Object jsonResponse) {
        JSONArray array = controllerUtils.toJSONArray(jsonResponse);
        if (array != null) {
            allgroups = GroupControllerUtils.getGroupListInfoFromJSONArray(array);
            GroupControllerUtils.setupGroupTable(groupTable, allgroups, idCol, groupNameCol, ownerCol, numOfMembersCol);
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
        allGroupsLabel.textProperty().bind(resourceFactory.getStringBinding("allGroupsLLabel"));
        groupNameCol.textProperty().bind(resourceFactory.getStringBinding("groupNameCol"));
        ownerCol.textProperty().bind(resourceFactory.getStringBinding("ownerCol"));
        numOfMembersCol.textProperty().bind(resourceFactory.getStringBinding("numOfMembersCol"));
    }
}