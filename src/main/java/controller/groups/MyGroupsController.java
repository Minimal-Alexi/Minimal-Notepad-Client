package controller.groups;

import controller.PageController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Group;
import model.ObservableResourceFactory;
import model.TokenStorage;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.json.JSONArray;
import utils.*;
import java.util.ArrayList;
import java.util.List;

import static utils.MainPageServices.*;

public class MyGroupsController extends PageController {

    @FXML private BorderPane root;
    @FXML private Label nameLabel, localTime, seeAllLabel, joinedLabel, suggestedLabel;
    @FXML private Button myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn, createGroupBtn;

    // Joined groups table
    @FXML private TableView<Group> joinedGroupTable;
    @FXML private TableColumn<Group, Integer> idCol;
    @FXML private TableColumn<Group, String> groupNameCol, ownerCol;
    @FXML private TableColumn<Group, Integer> numOfMembersCol;
    private TableColumn<Group, Group> joinedActionCol1, joinedActionCol2;

    // Suggested groups table
    @FXML private TableView<Group> canJoinGroupTable;
    @FXML private TableColumn<Group, Integer> idCol1;
    @FXML private TableColumn<Group, String> groupNameCol1, ownerCol1;
    @FXML private TableColumn<Group, Integer> numOfMembersCol1;
    private TableColumn<Group, Group> canJoinActionCol;

    private final List<Group> joinedGroups = new ArrayList<>();
    private final List<Group> canJoinGroups = new ArrayList<>();
    private static final String CSS_SOURCE = "/CSS";

    private ControllerUtils controllerUtils;
    private HttpResponseService httpService;
    private ObservableResourceFactory resourceFactory;
    private Stage stage;

    public void initialize() {
        initServices();
        setupUI();
        setupTables();
        applyStyles();
        bindUIComponents();
        Platform.runLater(super::updateDisplay);
    }

    private void initServices() {
        controllerUtils = new ControllerUtils();
        httpService = new HttpResponseServiceImpl();
        TokenStorage.getIntance();
    }

    private void setupUI() {
        updateNameLabel(nameLabel, TokenStorage.getUser());
        updateLocalTime(localTime);
        resourceFactory = ObservableResourceFactory.getInstance();
        setSidebarLanguages(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn);
    }

    private void setupTables() {
        // Joined groups
        joinedActionCol1 = GroupControllerUtils.addGroupColumn(joinedGroupTable, "");
        joinedActionCol2 = GroupControllerUtils.addGroupColumn(joinedGroupTable, "");
        GroupControllerUtils.updateJoinedTable(stage, joinedActionCol1, joinedActionCol2, httpService,
                this::handleGetJoinedGroupsResponse, this::handleResponseFromBothTables);

        // Suggested groups
        canJoinActionCol = GroupControllerUtils.addGroupColumn(canJoinGroupTable, "");
        GroupControllerUtils.updateCanJoinTable(stage, canJoinActionCol, httpService,
                this::handleGetCanJoinGroupsResponse, this::handleResponseFromBothTables);
    }

    private void applyStyles() {
        root.getStylesheets().addAll(
                getClass().getResource(CSS_SOURCE + "/button.css").toExternalForm(),
                getClass().getResource(CSS_SOURCE + "/text_input.css").toExternalForm(),
                getClass().getResource(CSS_SOURCE + "/table_view.css").toExternalForm()
        );
        ControllerUtils_v2.addStyle(logOutBtn, "/logout-button.css");
    }

    // Response Handlers
    public void handleGetJoinedGroupsResponse(CloseableHttpResponse response, Object jsonResponse) {
        JSONArray array = controllerUtils.toJSONArray(jsonResponse);
        List<Group> groups = array != null ? GroupControllerUtils.getGroupListInfoFromJSONArray(array) : new ArrayList<>();
        joinedGroups.clear();
        joinedGroups.addAll(groups);

        GroupControllerUtils.setupGroupTable(joinedGroupTable, joinedGroups, idCol, groupNameCol, ownerCol, numOfMembersCol);
    }

    public void handleGetCanJoinGroupsResponse(CloseableHttpResponse response, Object jsonResponse) {
        JSONArray array = controllerUtils.toJSONArray(jsonResponse);
        List<Group> groups = array != null ? GroupControllerUtils.getGroupListInfoFromJSONArray(array) : new ArrayList<>();
        canJoinGroups.clear();
        canJoinGroups.addAll(groups);

        GroupControllerUtils.setupGroupTable(canJoinGroupTable, canJoinGroups, idCol1, groupNameCol1, ownerCol1, numOfMembersCol1);
    }

    public void handleJoinOrLeaveResponse(CloseableHttpResponse response, Object object) {
        GroupControllerUtils.updateJoinedTable(stage, joinedActionCol1, joinedActionCol2, httpService,
                this::handleGetJoinedGroupsResponse, this::handleResponseFromBothTables);
    }

    public void handleJoinResponse(CloseableHttpResponse response, Object object) {
        GroupControllerUtils.updateCanJoinTable(stage, canJoinActionCol, httpService,
                this::handleGetCanJoinGroupsResponse, this::handleResponseFromBothTables);
    }

    public void handleResponseFromBothTables(CloseableHttpResponse response, Object object) {
        handleJoinOrLeaveResponse(response, object);
        handleJoinResponse(response, object);
    }

    // Navigation Methods
    @FXML public void myGroupsBtnClick() { ControllerUtils_v2.goToMyGroupsPage(stage, myGroupsBtn); }
    @FXML public void myNotesBtnClick() { ControllerUtils_v2.goToMyNotesPage(stage, myNotesBtn); }
    @FXML public void shareNotesBtnClick() { ControllerUtils_v2.goToMyGroupNotesPage(stage, shareNotesBtn); }
    @FXML public void allGroupsBtnClick() { ControllerUtils_v2.goToAllGroupsPage(stage, allGroupsBtn); }
    @FXML public void accountBtnClick() { ControllerUtils_v2.goToAccountPage(stage, accountBtn); }
    @FXML public void logOutBtnClick() { ControllerUtils_v2.logout(stage, logOutBtn); }

    @FXML public void createGroupBtnClick() {
        controllerUtils.goPage(stage, createGroupBtn, "/fxml/main_pages/groups/group_info_create_group.fxml");
    }

    @FXML public void seeAllClick() {
        controllerUtils.goPage(stage, seeAllLabel, "/fxml/main_pages/groups/all_groups.fxml");
    }

    @FXML private void tableClicked() {
        // Placeholder for table click — no action defined
    }

    @FXML void mouseEnter() {
        setCursorOnButtons(true);
    }

    @FXML void mouseExit() {
        setCursorOnButtons(false);
    }

    private void setCursorOnButtons(boolean hand) {
        Button[] buttons = {myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn};
        for (Button btn : buttons) {
            if (hand) controllerUtils.setHandCursor(btn);
            else controllerUtils.setDefaultCursor(btn);
        }
    }

    @Override
    public void bindUIComponents() {
        joinedLabel.textProperty().bind(resourceFactory.getStringBinding("myGroupsLabel"));
        groupNameCol.textProperty().bind(resourceFactory.getStringBinding("myGroupsGroupNameCol"));
        ownerCol.textProperty().bind(resourceFactory.getStringBinding("myGroupsGroupOwnerCol"));
        numOfMembersCol.textProperty().bind(resourceFactory.getStringBinding("myGroupsGroupNumberOfMembersCol"));

        suggestedLabel.textProperty().bind(resourceFactory.getStringBinding("myGroupsSuggestedLabel"));
        seeAllLabel.textProperty().bind(resourceFactory.getStringBinding("myGroupsSuggestedSeeAll"));
        groupNameCol1.textProperty().bind(resourceFactory.getStringBinding("myGroupsGroupNameCol"));
        ownerCol1.textProperty().bind(resourceFactory.getStringBinding("myGroupsGroupOwnerCol"));
        numOfMembersCol1.textProperty().bind(resourceFactory.getStringBinding("myGroupsGroupNumberOfMembersCol"));

        createGroupBtn.textProperty().bind(resourceFactory.getStringBinding("myGroupscreateGroupBtn"));
    }
}