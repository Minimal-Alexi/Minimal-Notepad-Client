package controller.groups;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
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
import utils.ControllerUtils;
import utils.HttpResponseService;
import utils.HttpResponseServiceImpl;
import utils.MainPageServices;
import static utils.GroupServices.updateGroup;


import java.util.ArrayList;
import java.util.List;

import static utils.GroupServices.findGroupById;
import static utils.GroupServices.updateGroup;

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

    @FXML
    private TableView<GroupMember> table1;

    @FXML
    private TableColumn<GroupMember, String> group1;  // Username column
    @FXML
    private TableColumn<GroupMember, String> category1;  // Email column

    private ObservableList<GroupMember> groupMembers = FXCollections.observableArrayList();

    private Group group;
    private Stage stage;
    private Scene scene;
    private Parent parent;
    private ControllerUtils controllerUtils;
    SelectedGroup selectedGroup = SelectedGroup.getInstance();
    private HttpResponseService httpResponseService;
//    private HttpClientSingleton httpInstance;


    private String getGroupUri() {
        int groupId = selectedGroup.getId();
        return "http://localhost:8093/api/groups/" + groupId;
    }
    String URI = getGroupUri();
    private static final String FXMLSource = "/fxml";
    private static final String CSSSOURCE = "/CSS";

    public JSONArray convertToJSONArray(List<GroupMember> groupMembers) {
        JSONArray jsonArray = new JSONArray();
        for (GroupMember member : groupMembers) {
            JSONObject memberObject = new JSONObject();
            memberObject.put("id", member.getId());
            memberObject.put("username", member.getUsername());
            memberObject.put("email", member.getEmail());
            jsonArray.put(memberObject);
        }
        return jsonArray;
    }


    public void initialize() {
        System.out.println("Start Edit Group Page");
        System.out.println("scene " + scene);
        this.controllerUtils = new ControllerUtils();
        this.httpResponseService = new HttpResponseServiceImpl();

        // Fetch the group from the server
        Group group = findGroupById("http://localhost:8093/api/groups/", selectedGroup.getId(), TokenStorage.getToken());

        System.out.println("Group is " + group);
        System.out.println("Fetching group with ID: " + selectedGroup.getId());

        getGroupUserInfoByGroupId();
        /*String groupID = String.valueOf(selectedGroup.getId());
        if (group != null) {
            groupNameInput.setText(group.getName());
            groupDescInput.setText(group.getDescription());

            // Assuming you get the group members as a List<GroupMember>
            List<GroupMember> groupMembersList = group.getMembers(); // Assuming `getMembers()` returns a List

            System.out.println("List of group members: " + groupMembersList);


            // If the group members are not null, add them to the ObservableList
            if (groupMembersList != null) {
                groupMembers.addAll(groupMembersList);
            }

            System.out.println("Group members are " + groupMembers);
        } else {
            System.out.println("Group not found.");
        }*/

        TokenStorage.getIntance();
        String username = TokenStorage.getUser();
        String password = TokenStorage.getToken();
        System.out.println("User: " + TokenStorage.getUser() + ", token: " + TokenStorage.getToken());

        nameLabel.setText("Welcome " + username);
        MainPageServices.updateLocalTime(localTime);

        root.getStylesheets().add(getClass().getResource(CSSSOURCE + "/button.css").toExternalForm());
        root.getStylesheets().add(getClass().getResource(CSSSOURCE + "/text_input.css").toExternalForm());

        editGroupBtn.getStylesheets().add(getClass().getResource(CSSSOURCE + "/groups.css").toExternalForm());

        // Set up the table with members
        group1.setCellValueFactory(new PropertyValueFactory<>("username"));
        category1.setCellValueFactory(new PropertyValueFactory<>("email"));

        table1.setItems(groupMembers);

        // Optional: You can refresh the table or set listeners here if necessary
        //setUpTableListeners();
    }

    public void getGroupUserInfoByGroupId() {
        String ALL_GROUP_URI = URI;
        HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder("GET", ALL_GROUP_URI, true);
        HttpRequestBase request = httpRequestBuilder.getHttpRequest();
        CloseableHttpClient httpClient = httpRequestBuilder.getHttpClient();
        httpResponseService.handleReponse(request, httpClient, this::handleGroupUserInfoByGroupId);
//        return null;
    }

    public void handleGroupUserInfoByGroupId(CloseableHttpResponse response, Object jsonResponse) {
        List<Group> updatedAllGroups = new ArrayList<>();
        JSONObject groupObject = controllerUtils.toJSonObject(jsonResponse);
        System.out.println("Group Object " + groupObject );
//        System.out.println(array);
//               System.out.println(groupObject);
                JSONObject owner = (JSONObject) ((JSONObject) groupObject).get("owner");
                String ownerEmail = owner.getString("email");
                GroupOwner groupOwner = new GroupOwner((int) owner.get("id"), (String) owner.get("username"), ownerEmail);
                int id = (int) ((JSONObject) groupObject).get("id");
                String name = (String) ((JSONObject) groupObject).get("name");
                String description = (String) ((JSONObject) groupObject).get("description");
                JSONArray userListObj = (JSONArray) ((JSONObject) groupObject).get("userGroupParticipationsList");
                AppUser currentOwner = new AppUser((int) owner.get("id"),(String) owner.get("username"), ownerEmail);
                List<AppUser> userList = createUserList(userListObj);
                userList.add(currentOwner);

                System.out.println("Current Owner " + currentOwner);

                System.out.println("User list " + userList);
                Group newGroup = new Group(id, name, description, groupOwner, userList);
                //updatedAllGroups.add(newGroup);

                System.out.println("New Group User List " + newGroup.getMembers());

/*          this.allgroups = updatedAllGroups;
            System.out.println(this.allgroups);*/
            //setupTable();
        //}
    }

    private List<AppUser> createUserList(JSONArray userObjectArray) {
        List<AppUser> userList = new ArrayList<>();
        for (Object userObject : userObjectArray) {
            JSONObject converted = (JSONObject) userObject;
            int id = (int) converted.get("id");
            String name = (String) converted.get("username");
            String email = (String) converted.get("email");
            userList.add(new AppUser(id, name, email));
        }
        return userList;
    }


    private void setUpTableListeners() {
        // Listener for the "Username" column
        group1.setCellFactory(column -> {
            return new TextFieldTableCell<>(); // Create an editable cell for the column
        });

        group1.setOnEditCommit(event -> {
            GroupMember editedMember = event.getRowValue();
            String newUsername = event.getNewValue();
            editedMember.setUsername(newUsername); // Update the username
            // Optionally, send update request to server here if needed
        });

        // Listener for the "Email" column
        category1.setCellFactory(column -> {
            return new TextFieldTableCell<>(); // Create an editable cell for the column
        });

        category1.setOnEditCommit(event -> {
            GroupMember editedMember = event.getRowValue();
            String newEmail = event.getNewValue();
            editedMember.setEmail(newEmail); // Update the email
            // Optionally, send update request to server here if needed
        });
    }

    public void handleGetGroupUserId(CloseableHttpResponse response, Object jsonResponse) {
        JSONObject object = controllerUtils.toJSonObject(jsonResponse);
        try {
            System.out.println(object);
            String email = (String) object.get("email");
            String username = (String) object.get("username");
            group1.setText(email);
            category1.setText(username);
        } catch (JSONException e) {
            System.out.println("Error parsing JSON: " + e.getMessage());
        }
    }


/*
    public void setGroup(Group group) {
        this.group = group;
        updateUI();
    }

    private void updateUI() {
        if (group != null) {
            nameLabel.setText(group.getName());  // Ensure UI is updated
        } else {
            System.out.println("Group is null!");
        }
    }*/


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
        // Get edited values
        /*String editedGroupName = groupNameInput.getText();
        String editedDescInput = groupDescInput.getText();
        int groupId = selectedGroup.getId();

        System.out.println("Edited group name: " + editedGroupName);
        System.out.println("Edited group description: " + editedDescInput);
        System.out.println("Edit button clicked!");

        // Create JSON request body

        System.out.println("Group Id 8/3 " + groupId);
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("id", groupId);
        jsonRequest.put("name", editedGroupName);
        jsonRequest.put("description", editedDescInput);

        // Retrieve updated members from TableView
        JSONArray membersArray = new JSONArray();
        for (GroupMember member : groupMembers) {
            JSONObject memberObject = new JSONObject();
            memberObject.put("id", member.getId());
            memberObject.put("username", member.getUsername());
            memberObject.put("email", member.getEmail());
            membersArray.put(memberObject);
        }

        System.out.println("Whether username and email are printed out or not " + membersArray);

        jsonRequest.put("members", membersArray);  // Add members to request

        // Send update request
        boolean updateSuccess = updateGroup("http://localhost:8093/api/groups/", groupId, jsonRequest.toString(), TokenStorage.getToken());

        System.out.println("Group ID at update success for edit group " + groupId);

        if (updateSuccess) {
            notiLabel1.setText("You've successfully edited group information.");
            editedGroupNameLabel.setText("Edited group name: " + editedGroupName);
            editedGroupDescLabel.setText("Edited group description: " + editedDescInput);

            // Refresh the group list
            //refreshGroupList();
            //controllerUtils.goPage(stage, button, FXMLString);
        } else {
            notiLabel1.setText("Failed to edit group. Please try again.");
        }*/
    }

/*    private void refreshGroupList() {
        // Fetch updated group from the server
        Group updatedGroup = findGroupById("http://localhost:8093/api/groups/", selectedGroup.getId(), TokenStorage.getToken());

        if (updatedGroup != null) {
            // Update UI fields
            groupNameInput.setText(updatedGroup.getName());
            groupDescInput.setText(updatedGroup.getDescription());

            // Update the TableView with the latest members
            groupMembers.clear();
            groupMembers.addAll(updatedGroup.getMembers()); // Ensure Group class has `getMembers()`

            table1.refresh();  // Refresh TableView UI
        } else {
            System.out.println("Failed to refresh group details.");
        }
    }*/


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