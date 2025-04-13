package controller.groups;

import controller.PageController;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.*;
import model.selected.SelectedGroup;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import static utils.GroupServices.findGroupById;
import static utils.MainPageServices.setSidebarLanguages;
import static utils.MainPageServices.updateNameLabel;

public class ReadOnlyGroupController extends PageController {

    // Sidebar
    @FXML private Button myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn;

    // Header
    @FXML private Label nameLabel, localTime;

    // Group Details Display
    @FXML private Label editedGroupNameid, membersInGroup, notiLabel1;
    @FXML private Label groupDetailLabel, groupNameLabel, groupDescLabel, membersInThisGroupLabel;
    @FXML private Label groupNameInfoLabel, groupDescInfoLabel;

    // Table
    @FXML private TableView<AppUser> table1;
    @FXML private TableColumn<AppUser, String> usernameCol, emailCol;
    private TableColumn<AppUser, AppUser> actionOneCol;

    @FXML private BorderPane root;

    private final ObservableList<AppUser> groupMembers = FXCollections.observableArrayList();
    private final SelectedGroup selectedGroup = SelectedGroup.getInstance();
    private final ControllerUtils controllerUtils = new ControllerUtils();
    private final HttpResponseService httpResponseService = new HttpResponseServiceImpl();
    private final ObservableResourceFactory RESOURCE_FACTORY = ObservableResourceFactory.getInstance();

    private List<AppUser> memberList = new ArrayList<>();
    private Group group;
    private Stage stage;
    private Scene scene;
    private Parent parent;

    private static final String FXML_SOURCE = "/fxml";
    private static final String CSS_SOURCE = "/CSS";
    private static final String BASE_GROUP_URI = "http://localhost:8093/api/groups/";

    private final String emailKey = "email";
    private final String usernameKey = "username";

    public void initialize() {
        setupInitialUI();
        fetchAndDisplayGroup();
        setupTable();
        super.updateDisplay();
    }

    private void setupInitialUI() {
        updateNameLabel(nameLabel, TokenStorage.getUser());
        MainPageServices.updateLocalTime(localTime);
        applyStyles();
        table1.setItems(groupMembers);
        actionOneCol = addAppUserCol(getLocalizedActionColOneName());
    }

    private void applyStyles() {
        root.getStylesheets().add(getClass().getResource(CSS_SOURCE + "/button.css").toExternalForm());
        root.getStylesheets().add(getClass().getResource(CSS_SOURCE + "/text_input.css").toExternalForm());
        ControllerUtils_v2.addStyle(logOutBtn, "/logout-button.css");
    }

    private void fetchAndDisplayGroup() {
        group = findGroupById(BASE_GROUP_URI, selectedGroup.getId(), TokenStorage.getToken());
        if (group != null) {
            groupNameInfoLabel.setText(group.getName());
            groupDescInfoLabel.setText(group.getDescription());
            getGroupUserInfoByGroupId();
        }
    }

    private void setupTable() {
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("Username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("Email"));
    }

    public void getGroupUserInfoByGroupId() {
        String groupUri = BASE_GROUP_URI + selectedGroup.getId();
        HttpRequestBuilder builder = new HttpRequestBuilder("GET", groupUri, true);
        httpResponseService.handleReponse(builder.getHttpRequestBase(), builder.getHttpClient(), this::handleGroupInfoResponse);
    }

    private void handleGroupInfoResponse(CloseableHttpResponse response, Object jsonResponse) {
        JSONObject groupObject = controllerUtils.toJSonObject(jsonResponse);
        JSONObject ownerJson = groupObject.getJSONObject("owner");
        GroupOwner groupOwner = new GroupOwner(ownerJson.getInt("id"), ownerJson.getString(usernameKey), ownerJson.getString(emailKey));

        JSONArray userListObj = groupObject.getJSONArray("userGroupParticipationsList");
        AppUser currentOwner = new AppUser(ownerJson.getInt("id"), ownerJson.getString(usernameKey), ownerJson.getString(emailKey));

        List<AppUser> users = createUserList(userListObj);
        users.add(currentOwner);

        group = new Group(groupObject.getInt("id"), groupObject.getString("name"),
                groupObject.getString("description"), groupOwner, users);

        this.memberList = group.getUserList();
        displayUserTable();
        updateActionColumn();
    }

    private List<AppUser> createUserList(JSONArray userArray) {
        List<AppUser> users = new ArrayList<>();
        for (Object obj : userArray) {
            JSONObject userJson = (JSONObject) obj;
            users.add(new AppUser(userJson.getInt("id"), userJson.getString(usernameKey), userJson.getString(emailKey)));
        }
        return users;
    }

    private void displayUserTable() {
        groupMembers.setAll(memberList);
        transformOwnerUsername();
    }

    private void transformOwnerUsername() {
        String loggedUser = TokenStorage.getUser();
        for (AppUser user : table1.getItems()) {
            if (user.getUsername().equals(loggedUser)) {
                user.setUsername("owner - " + user.getUsername());
            }
        }
    }

    private void updateActionColumn() {
        String currentUser = TokenStorage.getUser();
        String groupOwner = group.getGroupOwnerName();

        actionOneCol.setCellFactory(col -> {
            Button editButton = new Button("Edit");
            Button removeButton = new Button("Remove");

            TableCell<AppUser, AppUser> cell = new TableCell<>() {
                @Override
                protected void updateItem(AppUser user, boolean empty) {
                    super.updateItem(user, empty);
                    if (empty) {
                        setGraphic(null);
                        return;
                    }

                    if (!currentUser.equals(groupOwner)) {
                        setGraphic(null);
                    } else if (isOwner(user.getUsername())) {
                        ViewUtils.addStyle(editButton, "/edit-button.css");
                        setGraphic(editButton);
                    } else {
                        ViewUtils.addStyle(removeButton, "/delete-button.css");
                        setGraphic(removeButton);
                    }
                }
            };

            editButton.setOnAction(e -> editAccountInfo(editButton));
            removeButton.setOnAction(e -> removeUser(cell.getItem()));

            controllerUtils.setDefaultAndHandCursorBehaviour(removeButton);
            controllerUtils.setDefaultAndHandCursorBehaviour(editButton);

            return cell;
        });
    }

    public boolean isOwner(String username) {
        return username.equals(TokenStorage.getUser()) || username.equals("owner - " + TokenStorage.getUser());
    }

    public TableColumn<AppUser, AppUser> addAppUserCol(String columnName) {
        TableColumn<AppUser, AppUser> col = ViewUtils.column(columnName, ReadOnlyObjectWrapper::new, 100);
        table1.getColumns().add(col);
        return col;
    }

    public void editAccountInfo(Button btn) {
        selectedGroup.setId(group.getId());
        controllerUtils.goPage(stage, btn, FXML_SOURCE + "/main_pages/account_user_info_page.fxml");
    }

    public void removeUser(AppUser user) {
        String removeUri = BASE_GROUP_URI + selectedGroup.getId() + "/remove/" + user.getId();
        HttpRequestBuilder builder = new HttpRequestBuilder("DELETE", removeUri, true);
        httpResponseService.handleReponse(builder.getHttpRequestBase(), builder.getHttpClient(), this::handleRemoveResponse);
    }

    private void handleRemoveResponse(CloseableHttpResponse response, Object obj) {
        getGroupUserInfoByGroupId(); // Refresh data
    }

    // Sidebar button actions
    public void myGroupsBtnClick() { ControllerUtils_v2.goToMyGroupsPage(stage, myGroupsBtn); }
    @FXML public void myNotesBtnClick() { ControllerUtils_v2.goToMyNotesPage(stage, myNotesBtn); }
    @FXML public void shareNotesBtnClick() { ControllerUtils_v2.goToMyGroupNotesPage(stage, shareNotesBtn); }
    @FXML public void allGroupsBtnClick() { ControllerUtils_v2.goToAllGroupsPage(stage, allGroupsBtn); }
    @FXML public void accountBtnClick() { ControllerUtils_v2.goToAccountPage(stage, accountBtn); }
    @FXML public void logOutBtnClick() { controllerUtils.logout(stage, logOutBtn); }

    // Sidebar hover
    @FXML void mouseEnter() { setCursorStyle(true); }
    @FXML void mouseExit() { setCursorStyle(false); }

    private void setCursorStyle(boolean isHover) {
        Button[] buttons = {myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn};
        for (Button btn : buttons) {
            if (isHover) controllerUtils.setHandCursor(btn);
            else controllerUtils.setDefaultCursor(btn);
        }
    }

    public String getLocalizedActionColOneName() {
        ResourceBundle rb = RESOURCE_FACTORY.getResourceBundle();
        return rb.getString("actionColOneName");
    }

    @Override
    public void updateAllUIComponents() {
        getLocalizedActionColOneName();
    }

    @Override
    public void bindUIComponents() {
        groupDetailLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("groupDetailLabel"));
        groupDescLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("groupDescLabel"));
        groupNameLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("groupNameLabel"));
        membersInThisGroupLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("membersInThisGroupLabel"));
        usernameCol.textProperty().bind(RESOURCE_FACTORY.getStringBinding("usernameCol"));
        emailCol.textProperty().bind(RESOURCE_FACTORY.getStringBinding("emailCol"));
    }
}