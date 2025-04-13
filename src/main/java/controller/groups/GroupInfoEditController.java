package controller.groups;

import controller.PageController;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.*;
import model.ObservableResourceFactory;

import model.selected.SelectedGroup;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utils.*;

import static utils.GroupServices.updateGroup;


import java.util.ArrayList;
import java.util.List;
import utils.ControllerUtils;

import static utils.GroupServices.findGroupById;
import static utils.GroupServices.updateGroup;
import static utils.MainPageServices.setSidebarLanguages;
import static utils.MainPageServices.updateNameLabel;

public class GroupInfoEditController extends PageController {


    // group detail
    @FXML
    private Button editGroupBtn;

    @FXML
    private TextField groupDescInput;

    @FXML
    private TextField groupNameInput;


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
    private Label GroupNameId;

    @FXML
    private Label GroupDescId;

    @FXML
    private Label editmygroupid;

    @FXML
    private Label membersid;

    @FXML
    private BorderPane root;



    @FXML
    private Label notiLabel1;

    @FXML
    private Label editedGroupNameLabel;
    @FXML
    private Label editedGroupDescLabel;

    @FXML
    private TableView<AppUser> table1;

    @FXML
    private TableColumn<AppUser, String> group1;  // Username column
    @FXML
    private TableColumn<AppUser, String> category1;  // Email column

    private ObservableList<AppUser> groupMembers = FXCollections.observableArrayList();

    private Group group;
    private Stage stage;

    private ControllerUtils controllerUtils;
    private ObservableResourceFactory RESOURCE_FACTORY ;

    private List<AppUser> memberList;

    SelectedGroup selectedGroup = SelectedGroup.getInstance();
    private HttpResponseService httpResponseService;

    TableColumn<AppUser, AppUser> actionOneCol;
    String baseURI = "http://localhost:8093/api/groups/";

    private String getGroupUri() {
        int groupId = selectedGroup.getId();
        return baseURI + groupId;
    }

    String URI = getGroupUri();
    private static final String FXML_SOURCE = "/fxml";
    private static final String CSS_SOURCE = "/CSS";

    // common used key, both for object as well as for resource bundle
    private final String usernameKey = "username";
    private final String emailKey = "email";
    private final String idKey = "id";
    private final String editedGroupNameLabelEmptyKey =  "editedGroupNameLabelEmpty";

    // style const
    private final String  RED_TEXT = "-fx-text-fill: red;";
    private final String  BLACK_TEXT = "-fx-text-fill: black;";
    private final String GREEN_TEXT = "-fx-text-fill: green;";


    public JSONArray convertToJSONArray(List<GroupMember> groupMembers) {
        JSONArray jsonArray = new JSONArray();
        for (GroupMember member : groupMembers) {
            JSONObject memberObject = new JSONObject();
            memberObject.put(idKey, member.getId());
            memberObject.put(usernameKey, member.getUsername());
            memberObject.put(emailKey, member.getEmail());
            jsonArray.put(memberObject);
        }
        return jsonArray;
    }

    public void initialize() {
        TokenStorage.getIntance();
        this.memberList = new ArrayList<>();

        this.controllerUtils = new ControllerUtils();
        this.httpResponseService = new HttpResponseServiceImpl();
        RESOURCE_FACTORY = ObservableResourceFactory.getInstance();

        RESOURCE_FACTORY.getResourceBundle();

        Platform.runLater(()-> super.updateDisplay());

        // Fetch the group from the server
        Group group = findGroupById(baseURI, selectedGroup.getId(), TokenStorage.getToken());

        if (group != null) {
            groupNameInput.setText(group.getName());
            groupDescInput.setText(group.getDescription());
        } else {

            System.err.println("Group not found");
        }

        TokenStorage.getIntance();

        root.getStylesheets().add(getClass().getResource(CSS_SOURCE + "/button.css").toExternalForm());
        root.getStylesheets().add(getClass().getResource(CSS_SOURCE + "/text_input.css").toExternalForm());

        editGroupBtn.getStylesheets().add(getClass().getResource(CSS_SOURCE + "/groups.css").toExternalForm());
        ControllerUtils_v2.addStyle(logOutBtn,"/logout-button.css");


        table1.setItems(groupMembers);

        // create action column with default button
        actionOneCol = addAppUserCol("Action");

        // update Table View with updated action button
        // 1. get group based on ID and its member list
        // 2. update user table with the groups's member list
        // 3. update the button in the user table based on the member role
        getGroupUserInfoByGroupId();
        //
        // Optional: You can refresh the table or set listeners here if necessary
        //setUpTableListeners();


        // set sidebar language and localtime and header
        setSidebarLanguages(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn);
        MainPageServices.updateLocalTime(localTime);
        Platform.runLater(()->{

        MainPageServices.updateNameLabel(nameLabel, TokenStorage.getUser());
        });

    }

    public void getGroupUserInfoByGroupId() {
        String ALL_GROUP_URI = URI;
        HttpRequestBuilder httpRequest = new HttpRequestBuilder("GET", ALL_GROUP_URI, true);
        httpResponseService.handleReponse(httpRequest.getHttpRequestBase(), httpRequest.getHttpClient(), this::handleGroupUserInfoByGroupId);
    }

    public void handleGroupUserInfoByGroupId(CloseableHttpResponse response, Object jsonResponse) {
        List<Group> updatedAllGroups = new ArrayList<>();
        JSONObject groupObject = controllerUtils.toJSonObject(jsonResponse);

        JSONObject owner = (JSONObject) ((JSONObject) groupObject).get("owner");
        String ownerEmail = owner.getString(emailKey);
        GroupOwner groupOwner = new GroupOwner((int) owner.get(idKey), (String) owner.get(usernameKey), ownerEmail);
        int id = (int) ((JSONObject) groupObject).get(idKey);
        String name = (String) ((JSONObject) groupObject).get("name");
        String description = (String) ((JSONObject) groupObject).get("description");
        JSONArray userListObj = (JSONArray) ((JSONObject) groupObject).get("userGroupParticipationsList");
        AppUser currentOwner = new AppUser((int) owner.get(idKey), (String) owner.get(usernameKey), ownerEmail);
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
            int id = (int) converted.get(idKey);
            String name = (String) converted.get(usernameKey);
            String email = (String) converted.get(emailKey);
            userList.add(new AppUser(id, name, email));
        }
        return userList;
    }

    public void displayUserTable() {

        // group1 í column name, "Name" is the property name of the AppUser
        group1.setCellValueFactory(new PropertyValueFactory<>("Username"));
        category1.setCellValueFactory(new PropertyValueFactory<>("Email"));

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
//            System.out.println(user);
            if (username.equals(logginedUsername)) {
                user.setUsername("owner - " + username);
            }
//            System.out.println(user);
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
                editAccountInfo(source);
            });

            removeButton.setOnAction((e -> {
                Button source = (Button) e.getSource();
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
        String REMOVE_URI = URI + "/remove/" + appUserId;
        HttpRequestBuilder httpRequest = new HttpRequestBuilder("DELETE", REMOVE_URI, true);
        httpResponseService.handleReponse(httpRequest.getHttpRequestBase(), httpRequest.getHttpClient(), this::handleRemoveUser);
    }

    public void handleRemoveUser(CloseableHttpResponse response, Object object) {
        // get a updated group info from database after remove member
        getGroupUserInfoByGroupId();
    }

    @FXML
    void editGroupBtnClick() {
        // Get edited values
        String editedGroupName = groupNameInput.getText();
        String editedDescInput = groupDescInput.getText();
        if (isInputEmpty()){
            displayEmptyErrMessages();
        } else{
            int groupId = selectedGroup.getId();
            // Create JSON request body

            System.out.println("Group Id 8/3 " + groupId);
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put(idKey, groupId);
            jsonRequest.put("name", editedGroupName);
            jsonRequest.put("description", editedDescInput);

            // Send update request
            boolean updateSuccess = updateGroup(baseURI, groupId, jsonRequest.toString(), TokenStorage.getToken());

            if (updateSuccess) {
                notiLabel1.setText(getLocalizedSucessNotiLabelText());
                editedGroupNameLabel.setText(getLocalizedEditedGroupName(editedGroupName));
                editedGroupDescLabel.setText(getLocalizedEditedGroupDesc(editedDescInput));

            } else {
                notiLabel1.setText(getLocalizedFailNotiLabelText());
            }
        }
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

    public boolean isInputEmpty(){
        return this.groupNameInput.getText().isEmpty() || this.groupDescInput.getText().isEmpty();
    }

    public void displayEmptyErrMessages(){
        editedGroupDescLabel.setText("");
        editedGroupNameLabel.setText("");
        notiLabel1.setText("");
        if( groupNameInput.getText().isEmpty()){
            editedGroupNameLabel.setStyle(RED_TEXT);
            editedGroupNameLabel.setText(RESOURCE_FACTORY.getString(editedGroupNameLabelEmptyKey));
        }
        if( groupDescInput.getText().isEmpty()){
            editedGroupDescLabel.setStyle(RED_TEXT);
            editedGroupDescLabel.setText(RESOURCE_FACTORY.getString(editedGroupNameLabelEmptyKey));
        }
    }

    public String getLocalizedEditedGroupName(String editedGroupName) {
        editedGroupNameLabel.setText("");
        editedGroupNameLabel.setStyle(BLACK_TEXT);

        return RESOURCE_FACTORY.getString("editedGroupNameLabel") + " "+ editedGroupName;
    }

    public String getLocalizedEditedGroupDesc(String editedGroupDesc) {
        editedGroupDescLabel.setText("");
        editedGroupDescLabel.setStyle(BLACK_TEXT);
        return RESOURCE_FACTORY.getString("editedGroupDescLabel") + " "+ editedGroupDesc;
    }

    public String getLocalizedFailNotiLabelText(){
        notiLabel1.setStyle(RED_TEXT);
        return RESOURCE_FACTORY.getString("notiLabel1FailText");
    }

    public String getLocalizedSucessNotiLabelText(){
        notiLabel1.setStyle(GREEN_TEXT);
        return RESOURCE_FACTORY.getString("notiLabel1SuccessText");
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