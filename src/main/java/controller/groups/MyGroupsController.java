package controller.groups;

import controller.PageController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Group;
import model.ObservableResourceFactory;
import model.TokenStorage;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.json.JSONArray;
import utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static utils.MainPageServices.setSidebarLanguages;
import static utils.MainPageServices.updateNameLabel;


public class MyGroupsController extends PageController {

    @FXML
    private BorderPane root;
    @FXML
    private Label nameLabel;
    @FXML
    private Label localTime;

    // joined group table
    @FXML
    private TableView<Group> joinedGroupTable;

    @FXML
    private TableColumn<Group, Integer> idCol;



    @FXML
    private TableColumn<Group, String> groupNameCol;
    @FXML
    private TableColumn<Group, String> ownerCol;
    @FXML
    private TableColumn<Group, Integer> numOfMembersCol;

    private TableColumn<Group, Group> joinedTableActionOneCol;
    private TableColumn<Group, Group> joinedTableActionTwoCol;


    // can join group table
    @FXML
    private TableView<Group> canJoinGroupTable;
    @FXML
    private TableColumn<Group, Integer> idCol1;

    @FXML
    private TableColumn<Group, String> groupNameCol1;
    @FXML
    private TableColumn<Group, String> ownerCol1;
    @FXML
    private TableColumn<Group, Integer> numOfMembersCol1;

    private TableColumn<Group, Group> canJoinTableActionOneCol;

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
    private Label seeAllLabel;

    private ControllerUtils controllerUtils;
    private HttpResponseService httpResponseService;

    Stage stage;

    private List<Group> joinedGroups = new ArrayList<>();
    private List<Group> canJoinGroups = new ArrayList<>();

    private static final String CSS_SOURCE = "/CSS";

    private ObservableResourceFactory RESOURCE_FACTORY;

    public void initialize() {

        controllerUtils = new ControllerUtils();
        httpResponseService = new HttpResponseServiceImpl();

        TokenStorage.getIntance();//

        updateNameLabel(nameLabel, TokenStorage.getUser());
        MainPageServices.updateLocalTime(localTime);


        // joined table set up
        joinedTableActionOneCol = GroupControllerUtils.addGroupColumn(joinedGroupTable, "");
        joinedTableActionTwoCol = GroupControllerUtils.addGroupColumn(joinedGroupTable, "");
        GroupControllerUtils.updateJoinedTable(
                stage,
                joinedTableActionOneCol,
                joinedTableActionTwoCol,
                httpResponseService,
                this::handleGetJoinedGroupsResponse,
                this::handleResponseFromBothTable
        );

        // can join table set up
        canJoinTableActionOneCol = GroupControllerUtils.addGroupColumn(canJoinGroupTable, "");
        GroupControllerUtils.updateCanJoinTable(
                stage,
                canJoinTableActionOneCol,
                httpResponseService,
                this::handleGetCanJoinTable,
                this::handleResponseFromBothTable
        );

        root.getStylesheets().add(getClass().getResource(CSS_SOURCE + "/button.css").toExternalForm());
        root.getStylesheets().add(getClass().getResource(CSS_SOURCE + "/text_input.css").toExternalForm());
        root.getStylesheets().add(getClass().getResource(CSS_SOURCE + "/table_view.css").toExternalForm());
        ControllerUtils_v2.addStyle(logOutBtn,"/logout-button.css");

        // set sidebar language
        RESOURCE_FACTORY = ObservableResourceFactory.getInstance();
        setSidebarLanguages(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn);

        Platform.runLater(super::updateDisplay);
    }

    // handler of action buttons in joined table
    public void handleGetJoinedGroupsResponse(CloseableHttpResponse response, Object jsonResponse) {
        JSONArray array = controllerUtils.toJSONArray(jsonResponse);
        if (array != null) {
            this.joinedGroups = GroupControllerUtils.getGroupListInfoFromJSONArray(array);
            GroupControllerUtils.setupGroupTable(
                    joinedGroupTable,
                    this.joinedGroups,
                    idCol,
                    groupNameCol,
                    ownerCol,
                    numOfMembersCol
            );
        }
    }

    public void handleJoinOrLeaveOrDeleteResponseOfJoinedTable(CloseableHttpResponse response, Object object) {

        GroupControllerUtils.updateJoinedTable(
                stage,
                joinedTableActionOneCol,
                joinedTableActionTwoCol,
                httpResponseService,
                // get joined groups from db
                this::handleGetJoinedGroupsResponse,
                this::handleResponseFromBothTable);
    }


    // handler of action button in canjoin table
    public void handleGetCanJoinTable(CloseableHttpResponse response, Object jsonResponse) {
        JSONArray array = controllerUtils.toJSONArray(jsonResponse);
        if (array != null) {

            this.canJoinGroups = GroupControllerUtils.getGroupListInfoFromJSONArray(array);

        } else {
            // reset canjoingroup when response from api is null
            this.canJoinGroups = new ArrayList<>();
        }
        GroupControllerUtils.setupGroupTable(
                canJoinGroupTable,
                this.canJoinGroups,
                idCol1,
                groupNameCol1,
                ownerCol1,
                numOfMembersCol1);
    }

    public void handleJoinResponseOfCanJoinTable(CloseableHttpResponse response, Object object) {
        GroupControllerUtils.updateCanJoinTable(
                stage,
                canJoinTableActionOneCol,
                httpResponseService,
                // get can join group from db
                this::handleGetCanJoinTable,
                // update view to both table
                this::handleResponseFromBothTable);
    }


    public void handleResponseFromBothTable(CloseableHttpResponse response, Object object) {
        handleJoinOrLeaveOrDeleteResponseOfJoinedTable(response, object);
        handleJoinResponseOfCanJoinTable(response, object);
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
        ControllerUtils_v2.logout(stage, logOutBtn);

    }

    @FXML
    void mouseEnter() {
        this.controllerUtils.setHandCursor(myNotesBtn);
        this.controllerUtils.setHandCursor(shareNotesBtn);
        this.controllerUtils.setHandCursor(myGroupsBtn);
        this.controllerUtils.setHandCursor(allGroupsBtn);
        this.controllerUtils.setHandCursor(accountBtn);
        this.controllerUtils.setHandCursor(logOutBtn);
    }

    @FXML
    void mouseExit() {
        this.controllerUtils.setDefaultCursor(myNotesBtn);
        this.controllerUtils.setDefaultCursor(shareNotesBtn);
        this.controllerUtils.setDefaultCursor(myGroupsBtn);
        this.controllerUtils.setDefaultCursor(allGroupsBtn);
        this.controllerUtils.setDefaultCursor(accountBtn);
        this.controllerUtils.setDefaultCursor(logOutBtn);
    }

    @FXML
    private void createGroupBtnClick() {
        this.controllerUtils.goPage(stage, createGroupBtn, "/fxml/main_pages/groups/group_info_create_group.fxml");
    }

    @FXML
    private void seeAllClick() {
        this.controllerUtils.goPage(stage, seeAllLabel, "/fxml/main_pages/groups/all_groups.fxml");
    }

    @FXML
    private void tableClicked() {
        // this do nothing
    }

    @FXML private Label joinedLabel;
    @FXML private Label suggestedLabel;

    @Override
    public void bindUIComponents() {
        joinedLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("myGroupsLabel"));
        groupNameCol.textProperty().bind(RESOURCE_FACTORY.getStringBinding("myGroupsGroupNameCol"));
        ownerCol.textProperty().bind(RESOURCE_FACTORY.getStringBinding("myGroupsGroupOwnerCol"));
        numOfMembersCol.textProperty().bind(RESOURCE_FACTORY.getStringBinding("myGroupsGroupNumberOfMembersCol"));
        suggestedLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("myGroupsSuggestedLabel"));
        seeAllLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("myGroupsSuggestedSeeAll"));
        groupNameCol1.textProperty().bind(RESOURCE_FACTORY.getStringBinding("myGroupsGroupNameCol"));
        ownerCol1.textProperty().bind(RESOURCE_FACTORY.getStringBinding("myGroupsGroupOwnerCol"));
        numOfMembersCol1.textProperty().bind(RESOURCE_FACTORY.getStringBinding("myGroupsGroupNumberOfMembersCol"));
        createGroupBtn.textProperty().bind(RESOURCE_FACTORY.getStringBinding("myGroupscreateGroupBtn"));
    }
}
