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
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static utils.GroupServices.findGroupById;
import static utils.GroupServices.updateGroup;
import static utils.MainPageServices.setSidebarLanguages;
import static utils.MainPageServices.updateNameLabel;

public class ReadOnlyGroupController extends PageController {


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


    // header
    @FXML
    private Label nameLabel;
    @FXML
    private Label localTime;

    @FXML
    private BorderPane root;


    @FXML
    private Label notiLabel1;

    @FXML
    private Label groupDetailLabel;
    @FXML
    private Label groupNameLabel;
    @FXML
    private Label groupDescLabel;
    @FXML
    private Label membersInThisGroupLabel;

    @FXML
    private Label groupNameInfoLabel;
    @FXML
    private Label groupDescInfoLabel;


    @FXML
    private TableView<AppUser> table1;

    @FXML
    private TableColumn<AppUser, String> usernameCol;  // Username column
    @FXML
    private TableColumn<AppUser, String> emailCol;  // Email column

    private ObservableList<AppUser> groupMembers ;



    private Group group;
    private Stage stage;
    private Scene scene;

    private ControllerUtils controllerUtils;
    private List<AppUser> memberList;

    private ObservableResourceFactory RESOURCE_FACTORY;

    SelectedGroup selectedGroup = SelectedGroup.getInstance(); ;
    private HttpResponseService httpResponseService;
//    private HttpClientSingleton httpInstance;

    TableColumn<AppUser, AppUser> actionOneCol;

    String SELECTED_GROUP_URI = GroupControllerUtils.getSelectGroupURI(selectedGroup);

    private static final String CSS_SOURCE = "/CSS";

    // common String key that is common
    private static final String emailKey = "email";
    private static final String usernameKey = "username";


    public void initialize() {
        this.memberList = new ArrayList<>();

//        selectedGroup = SelectedGroup.getInstance();
        groupMembers = FXCollections.observableArrayList();
        RESOURCE_FACTORY = ObservableResourceFactory.getInstance();

        System.out.println("Start Edit Group Page");
        System.out.println("scene " + scene);
        this.controllerUtils = new ControllerUtils();
        this.httpResponseService = new HttpResponseServiceImpl();

        // Fetch the group from the server
        Group group = findGroupById("http://localhost:8093/api/groups/", selectedGroup.getId(), TokenStorage.getToken());

        System.out.println("Group is " + group);
        System.out.println("Fetching group with ID: " + selectedGroup.getId());

        if (group != null) {
//            groupNameInput.setText(group.getName());
//            groupDescInput.setText(group.getDescription());
            groupNameInfoLabel.setText(group.getName());
            groupDescInfoLabel.setText(group.getDescription());
        } else {
            System.out.println("Group not found.");

        }

        TokenStorage.getIntance();
        String username = TokenStorage.getUser();
        String password = TokenStorage.getToken();
        System.out.println("User: " + TokenStorage.getUser() + ", token: " + TokenStorage.getToken());

        updateNameLabel(nameLabel, TokenStorage.getUser());
        MainPageServices.updateLocalTime(localTime);

        root.getStylesheets().add(getClass().getResource(CSS_SOURCE + "/button.css").toExternalForm());
        root.getStylesheets().add(getClass().getResource(CSS_SOURCE + "/text_input.css").toExternalForm());

        ControllerUtils_v2.addStyle(logOutBtn,"/logout-button.css");

        table1.setItems(groupMembers);

        // create action column with default button
        actionOneCol = addAppUserCol(getLocalizedActionColOneName());

        // update Table View with updated action button
        // 1. get group based on ID and its member list
        // 2. update user table with the groups's member list
        // 3. update the button in the user table based on the member role
        getGroupUserInfoByGroupId();
        // Optional: You can refresh the table or set listeners here if necessary
        //setUpTableListeners();

        // set sidebar language
        setSidebarLanguages(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn);
        super.updateDisplay();
    }

    public void getGroupUserInfoByGroupId() {
        String ALL_GROUP_URI = SELECTED_GROUP_URI;
        HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder("GET", ALL_GROUP_URI, true);
        HttpRequestBase request = httpRequestBuilder.getHttpRequestBase();
        CloseableHttpClient httpClient = httpRequestBuilder.getHttpClient();
        httpResponseService.handleReponse(request, httpClient, this::handleGroupUserInfoByGroupId);
    }

    public void handleGroupUserInfoByGroupId(CloseableHttpResponse response, Object jsonResponse) {
        List<Group> updatedAllGroups = new ArrayList<>();
        JSONObject groupObject = controllerUtils.toJSonObject(jsonResponse);
        System.out.println("Group Object " + groupObject);

        JSONObject owner = (JSONObject) ((JSONObject) groupObject).get("owner");
        String ownerEmail = owner.getString(emailKey);
        GroupOwner groupOwner = new GroupOwner((int) owner.get("id"), (String) owner.get(usernameKey), ownerEmail);
        int id = (int) ((JSONObject) groupObject).get("id");
        String name = (String) ((JSONObject) groupObject).get("name");
        String description = (String) ((JSONObject) groupObject).get("description");
        JSONArray userListObj = (JSONArray) ((JSONObject) groupObject).get("userGroupParticipationsList");
        AppUser currentOwner = new AppUser((int) owner.get("id"), (String) owner.get(usernameKey), ownerEmail);
        List<AppUser> userList = createUserList(userListObj);
        userList.add(currentOwner);

        this.group = new Group(id, name, description, groupOwner, userList);



        this.memberList = this.group.getUserList();

        // setup, display data to table with processed data
        displayUserTable();
        updateColumnOne();
    }

    private List<AppUser> createUserList(JSONArray userObjectArray) {
        List<AppUser> userList = new ArrayList<>();
        for (Object userObject : userObjectArray) {
            JSONObject converted = (JSONObject) userObject;
            int id = (int) converted.get("id");
            String name = (String) converted.get(usernameKey);
            String email = (String) converted.get(emailKey);
            userList.add(new AppUser(id, name, email));
        }
        return userList;
    }

    public void displayUserTable() {

        // usernameCol í column name, "Name" is the property name of the AppUser
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("Username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("Email"));
        groupMembers = FXCollections.observableArrayList(this.memberList);
        table1.setItems(groupMembers);
        transformUsername();
    }


    public boolean isOwner(String username) {
        String loginnedUsername = TokenStorage.getUser();
        String formattedUsername = "owner - " + loginnedUsername;

        return username.equals(loginnedUsername) || username.equals(formattedUsername);
    }

    public void transformUsername() {
        List<AppUser> curMemberList = table1.getItems();
        String logginedUsername = TokenStorage.getUser();
        for (AppUser user : curMemberList) {
            String username = user.getUsername();
            System.out.println(user);
            if (username.equals(logginedUsername)) {
                user.setUsername("owner - " + username);
            }
        }
    }

    public void handleGetGroupUserId(CloseableHttpResponse response, Object jsonResponse) {
        JSONObject object = controllerUtils.toJSonObject(jsonResponse);
        try {
            System.out.println(object);
            String email = (String) object.get(emailKey);
            String username = (String) object.get(usernameKey);
            usernameCol.setText(email);
            emailCol.setText(username);
        } catch (JSONException e) {
            System.out.println("Error parsing JSON: " + e.getMessage());
        }
    }


    public TableColumn<AppUser, AppUser> addAppUserCol(String columnName) {
        int TABLE_CELL_WIDTH = 100;
        TableColumn<AppUser, AppUser> column = ViewUtils.column(columnName, ReadOnlyObjectWrapper<AppUser>::new, TABLE_CELL_WIDTH);

        table1.getColumns().add(column);
        column.setCellFactory(col -> {
            Button editButton = new Button(columnName);
            TableCell<AppUser, AppUser> cell = new TableCell<AppUser, AppUser>() {
                @Override
                public void updateItem(AppUser person, boolean empty) {
                    super.updateItem(person, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(editButton);
                    }
                }
            };

            editButton.setOnAction(e -> System.out.println("click edit button for: " + cell.getItem()));
            return cell;
        });
        return column;
    }

    private void updateColumnOne() {
        String owner = TokenStorage.getUser();
        String groupOwner = this.group.getGroupOwnerName();



//        String fName = "Jacob";
        actionOneCol.setCellFactory(col -> {
            Button editButton = new Button("Edit");
            Button removeButton = new Button("Remove");
//            Button joinButton = new Button("Join");
            TableCell<AppUser, AppUser> updatedCell = new TableCell<AppUser, AppUser>() {
                @Override
                // display button
                public void updateItem(AppUser appUser, boolean empty) {
                    super.updateItem(appUser, empty);
                    // if data is null, add no button
                    if (empty) {
                        setGraphic(null);
                    } else {
                        // if loggined user != group owner
                        if (!owner.equals(groupOwner)) {
                            setGraphic(null);
                        } else {
                            // if current member is the same as loggined user
                            if (isOwner(appUser.getUsername())) {
                                setGraphic(editButton);
                                ViewUtils.addStyle(editButton, "/edit-button.css");
                                // if current member is not the loggined user
                            } else {
                                setGraphic(removeButton);
                                ViewUtils.addStyle(removeButton, "/delete-button.css");
                            }
                        }
                    }
                }
            };
            //edit my own info
            editButton.setOnAction(e -> {
                Button source = (Button) e.getSource();
                System.out.println("is button " + source);
                editAccountInfo(source);
            });

            removeButton.setOnAction((e -> {
                Button source = (Button) e.getSource();
                System.out.println("is button " + source);
                System.out.println(updatedCell.getItem());
                removeUser(updatedCell.getItem());
            }));


            this.controllerUtils.setDefaultAndHandCursorBehaviour(removeButton);
            this.controllerUtils.setDefaultAndHandCursorBehaviour(editButton);
            return updatedCell;

        });
    }

    public void editAccountInfo(Button btn) {
        String FXMLString = "/fxml/main_pages/account_user_info_page.fxml";

        selectedGroup.setId(group.getId());
        controllerUtils.goPage(stage, btn, FXMLString);
    }

    public void removeUser(AppUser appUser) {
        int appUserId = appUser.getId();
        String REMOVE_URI = SELECTED_GROUP_URI + "/remove/" + appUserId;
        HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder("DELETE", REMOVE_URI, true);
        HttpRequestBase request = httpRequestBuilder.getHttpRequestBase();
        CloseableHttpClient httpClient = httpRequestBuilder.getHttpClient();
        httpResponseService.handleReponse(request, httpClient, this::handleRemoveUser);
    }

    public void handleRemoveUser(CloseableHttpResponse response, Object object) {

        // get a updated group info from database after remove member
        getGroupUserInfoByGroupId();
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
        this.controllerUtils.logout(stage, logOutBtn);
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

    public String getLocalizedActionColOneName(){
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
