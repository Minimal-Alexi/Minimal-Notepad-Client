package controller.groups;

import controller.PageController;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.*;
import model.ObservableResourceFactory;
import model.selected.SelectedGroup;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.*;
import static utils.GroupServices.updateGroup;
import java.util.ArrayList;
import java.util.List;
import utils.ControllerUtils;
import static utils.GroupServices.findGroupById;
import static utils.MainPageServices.setSidebarLanguages;
import static utils.MainPageServices.updateNameLabel;

public class GroupInfoEditController extends PageController {

    // Constants
    private static final String BASE_URI = "http://localhost:8093/api/groups/";
    private static final String FXML_ACCOUNT_INFO = "/fxml/main_pages/account_user_info_page.fxml";
    private static final String CSS_BUTTON = "/CSS/button.css";
    private static final String CSS_TEXT_INPUT = "/CSS/text_input.css";
    private static final String CSS_GROUPS = "/CSS/groups.css";

    private static final String USERNAME_KEY = "username";
    private static final String EMAIL_KEY = "email";
    private static final String ID_KEY = "id";

    private static final String  RED_TEXT = "-fx-text-fill: red;";
    private static final String  BLACK_TEXT = "-fx-text-fill: black;";
    private static final String GREEN_TEXT = "-fx-text-fill: green;";

    // Sidebar
    @FXML private Button myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn;

    // Header
    @FXML private Label nameLabel, localTime;

    // Group Edit Fields
    @FXML private TextField groupDescInput, groupNameInput;
    @FXML private Button editGroupBtn;
    @FXML private Label GroupNameId, GroupDescId, editmygroupid, membersid;
    @FXML private Label notiLabel1, editedGroupNameLabel, editedGroupDescLabel;

    // Table
    @FXML private TableView<AppUser> table1;
    @FXML private TableColumn<AppUser, String> group1, category1;

    @FXML private BorderPane root;

    private ObservableList<AppUser> groupMembers = FXCollections.observableArrayList();
    private List<AppUser> memberList = new ArrayList<>();
    private Group group;
    private Stage stage;
    private TableColumn<AppUser, AppUser> actionOneCol;

    private final SelectedGroup selectedGroup = SelectedGroup.getInstance();
    private final ControllerUtils controllerUtils = new ControllerUtils();
    private final HttpResponseService httpResponseService = new HttpResponseServiceImpl();
    private final ObservableResourceFactory RESOURCE_FACTORY = ObservableResourceFactory.getInstance();

    private String getGroupUri() {
        return BASE_URI + selectedGroup.getId();
    }

    public void initialize() {
        RESOURCE_FACTORY.getResourceBundle();
        Platform.runLater(super::updateDisplay);

        loadGroupDetails();
        styleUIComponents();
        setupSidebar();
        setupTable();

        MainPageServices.updateLocalTime(localTime);
        Platform.runLater(() -> updateNameLabel(nameLabel, TokenStorage.getUser()));
    }

    private void loadGroupDetails() {
        group = findGroupById(BASE_URI, selectedGroup.getId(), TokenStorage.getToken());
        if (group != null) {
            groupNameInput.setText(group.getName());
            groupDescInput.setText(group.getDescription());
        } else {
            System.err.println("Group not found");
        }
    }

    private void styleUIComponents() {
        root.getStylesheets().add(getClass().getResource(CSS_BUTTON).toExternalForm());
        root.getStylesheets().add(getClass().getResource(CSS_TEXT_INPUT).toExternalForm());
        editGroupBtn.getStylesheets().add(getClass().getResource(CSS_GROUPS).toExternalForm());
        ControllerUtils_v2.addStyle(logOutBtn, "/logout-button.css");
    }

    private void setupSidebar() {
        setSidebarLanguages(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn);
    }

    private void setupTable() {
        table1.setItems(groupMembers);
        actionOneCol = addAppUserCol("Action");
        getGroupUserInfoByGroupId();
    }

    public void getGroupUserInfoByGroupId() {
        HttpRequestBuilder request = new HttpRequestBuilder("GET", getGroupUri(), true);
        httpResponseService.handleReponse(request.getHttpRequestBase(), request.getHttpClient(), this::handleGroupUserInfoByGroupId);
    }

    public void handleGroupUserInfoByGroupId(CloseableHttpResponse response, Object jsonResponse) {
        JSONObject groupObject = controllerUtils.toJSonObject(jsonResponse);
        JSONObject owner = groupObject.getJSONObject("owner");

        GroupOwner groupOwner = new GroupOwner(owner.getInt(ID_KEY), owner.getString(USERNAME_KEY), owner.getString(EMAIL_KEY));
        AppUser currentOwner = new AppUser(owner.getInt(ID_KEY), owner.getString(USERNAME_KEY), owner.getString(EMAIL_KEY));

        JSONArray membersArray = groupObject.getJSONArray("userGroupParticipationsList");
        List<AppUser> users = createUserList(membersArray);
        users.add(currentOwner);

        group = new Group(groupObject.getInt(ID_KEY), groupObject.getString("name"), groupObject.getString("description"), groupOwner, users);
        memberList = group.getUserList();

        displayUserTable();
        updateColumnOne();
    }

    private List<AppUser> createUserList(JSONArray array) {
        List<AppUser> users = new ArrayList<>();
        for (Object obj : array) {
            JSONObject json = (JSONObject) obj;
            users.add(new AppUser(json.getInt(ID_KEY), json.getString(USERNAME_KEY), json.getString(EMAIL_KEY)));
        }
        return users;
    }

    public void displayUserTable() {
        group1.setCellValueFactory(new PropertyValueFactory<>("Username"));
        category1.setCellValueFactory(new PropertyValueFactory<>("Email"));

        groupMembers.setAll(memberList);
        transformUsername();
    }

    private void transformUsername() {
        String loggedInUsername = TokenStorage.getUser();
        table1.getItems().forEach(user -> {
            if (user.getUsername().equals(loggedInUsername)) {
                user.setUsername("owner - " + loggedInUsername);
            }
        });
    }

    public TableColumn<AppUser, AppUser> addAppUserCol(String columnName) {
        TableColumn<AppUser, AppUser> column = ViewUtils.column(columnName, ReadOnlyObjectWrapper<AppUser>::new, 100);
        table1.getColumns().add(column);

        column.setCellFactory(col -> {
            Button button = new Button(columnName);
            TableCell<AppUser, AppUser> cell = new TableCell<>() {
                @Override
                protected void updateItem(AppUser item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : button);
                }
            };
            button.setOnAction(e -> System.out.println("Edit: " + cell.getItem()));
            return cell;
        });

        return column;
    }

    private void updateColumnOne() {
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
                    } else if (!currentUser.equals(groupOwner)) {
                        setGraphic(null);
                    } else if (isOwner(user.getUsername())) {
                        setGraphic(editButton);
                        ViewUtils.addStyle(editButton, "/edit-button.css");
                    } else {
                        setGraphic(removeButton);
                        ViewUtils.addStyle(removeButton, "/delete-button.css");
                    }
                }
            };

            editButton.setOnAction(e -> editAccountInfo(editButton));
            removeButton.setOnAction(e -> removeUser(cell.getItem()));

            controllerUtils.setDefaultAndHandCursorBehaviour(editButton);
            controllerUtils.setDefaultAndHandCursorBehaviour(removeButton);

            return cell;
        });
    }

    public boolean isOwner(String username) {
        String current = TokenStorage.getUser();
        return username.equals(current) || username.equals("owner - " + current);
    }

    public void editAccountInfo(Button btn) {
        selectedGroup.setId(group.getId());
        controllerUtils.goPage(stage, btn, FXML_ACCOUNT_INFO);
    }

    public void removeUser(AppUser user) {
        String uri = getGroupUri() + "/remove/" + user.getId();
        HttpRequestBuilder request = new HttpRequestBuilder("DELETE", uri, true);
        httpResponseService.handleReponse(request.getHttpRequestBase(), request.getHttpClient(), this::handleRemoveUser);
    }

    public void handleRemoveUser(CloseableHttpResponse response, Object obj) {
        getGroupUserInfoByGroupId();
    }

    @FXML
    void editGroupBtnClick() {
        if (isInputEmpty()) {
            displayEmptyErrMessages();
            return;
        }

        JSONObject jsonRequest = new JSONObject()
                .put(ID_KEY, selectedGroup.getId())
                .put("name", groupNameInput.getText())
                .put("description", groupDescInput.getText());

        boolean success = updateGroup(BASE_URI, selectedGroup.getId(), jsonRequest.toString(), TokenStorage.getToken());

        if (success) {
            notiLabel1.setText(getLocalizedSucessNotiLabelText());
            editedGroupNameLabel.setText(getLocalizedEditedGroupName(groupNameInput.getText()));
            editedGroupDescLabel.setText(getLocalizedEditedGroupDesc(groupDescInput.getText()));
        } else {
            notiLabel1.setText(getLocalizedFailNotiLabelText());
        }
    }

    public boolean isInputEmpty() {
        return groupNameInput.getText().isEmpty() || groupDescInput.getText().isEmpty();
    }

    public void displayEmptyErrMessages() {
        editedGroupNameLabel.setText("");
        editedGroupDescLabel.setText("");
        notiLabel1.setText("");

        if (groupNameInput.getText().isEmpty()) {
            editedGroupNameLabel.setStyle(RED_TEXT);
            editedGroupNameLabel.setText(RESOURCE_FACTORY.getString("editedGroupNameLabelEmpty"));
        }

        if (groupDescInput.getText().isEmpty()) {
            editedGroupDescLabel.setStyle(RED_TEXT);
            editedGroupDescLabel.setText(RESOURCE_FACTORY.getString("editedGroupDescLabelEmpty"));
        }
    }

    public String getLocalizedEditedGroupName(String name) {
        editedGroupNameLabel.setStyle(BLACK_TEXT);
        return RESOURCE_FACTORY.getString("editedGroupNameLabel") + " " + name;
    }

    public String getLocalizedEditedGroupDesc(String desc) {
        editedGroupDescLabel.setStyle(BLACK_TEXT);
        return RESOURCE_FACTORY.getString("editedGroupDescLabel") + " " + desc;
    }

    public String getLocalizedFailNotiLabelText() {
        notiLabel1.setStyle(RED_TEXT);
        return RESOURCE_FACTORY.getString("notiLabel1FailText");
    }

    public String getLocalizedSucessNotiLabelText() {
        notiLabel1.setStyle(GREEN_TEXT);
        return RESOURCE_FACTORY.getString("notiLabel1SuccessText");
    }

    // Sidebar Navigation
    public void myGroupsBtnClick() { ControllerUtils_v2.goToMyGroupsPage(stage, myGroupsBtn); }
    public void myNotesBtnClick() { ControllerUtils_v2.goToMyNotesPage(stage, myNotesBtn); }
    public void shareNotesBtnClick() { ControllerUtils_v2.goToMyGroupNotesPage(stage, shareNotesBtn); }
    public void allGroupsBtnClick() { ControllerUtils_v2.goToAllGroupsPage(stage, allGroupsBtn); }
    public void accountBtnClick() { ControllerUtils_v2.goToAccountPage(stage, accountBtn); }
    public void logOutBtnClick() { controllerUtils.logout(stage, logOutBtn); }

    // Mouse Events
    public void mouseEnter() {
        setCursorForAll(true);
    }

    public void mouseExit() {
        setCursorForAll(false);
    }

    private void setCursorForAll(boolean hand) {
        Button[] buttons = {myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn};
        for (Button btn : buttons) {
            if (hand) controllerUtils.setHandCursor(btn);
            else controllerUtils.setDefaultCursor(btn);
        }
    }

    @Override
    public void bindUIComponents() {

        GroupNameId.textProperty().bind(RESOURCE_FACTORY.getStringBinding("GroupNameId"));
        GroupDescId.textProperty().bind(RESOURCE_FACTORY.getStringBinding("GroupDescId"));
        editmygroupid.textProperty().bind(RESOURCE_FACTORY.getStringBinding("editmygroupid"));
        allGroupsBtn.textProperty().bind(RESOURCE_FACTORY.getStringBinding("allGroupsBtn"));
        accountBtn.textProperty().bind(RESOURCE_FACTORY.getStringBinding("accountBtn"));
        logOutBtn.textProperty().bind(RESOURCE_FACTORY.getStringBinding("logOutBtn"));
        groupNameInput.promptTextProperty().bind(RESOURCE_FACTORY.getStringBinding("groupNameInput"));
        groupDescInput.promptTextProperty().bind(RESOURCE_FACTORY.getStringBinding("groupDescInput"));
        group1.textProperty().bind(RESOURCE_FACTORY.getStringBinding("group1"));
        category1.textProperty().bind(RESOURCE_FACTORY.getStringBinding("category1"));
        editGroupBtn.textProperty().bind(RESOURCE_FACTORY.getStringBinding("editGroupBtn"));
        membersid.textProperty().bind(RESOURCE_FACTORY.getStringBinding("membersid"));
    }
}