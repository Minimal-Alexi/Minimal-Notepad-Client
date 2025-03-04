package controller.groups;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.*;
import model.selected.SelectedGroup;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.*;


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
    //    @FXML
//    private TableColumn<?, ?> actionCol;
//    @FXML
//    private TableColumn<?, ?> editCol;
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
    TableColumn<Group, Group> actionOneCol;
    TableColumn<Group, Group> actionTwoCol;


    //    private static final String URI = "http://localhost:8093/api/groups/my-groups";
    private static final String URI = "http://localhost:8093/api/groups";


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

        nameLabel.setText("Welcome, " + username);
        MainPageServices.updateLocalTime(localTime);


        root.getStylesheets().add(getClass().getResource(CSSSOURCE + "/button.css").toExternalForm());
        root.getStylesheets().add(getClass().getResource(CSSSOURCE + "/text_input.css").toExternalForm());

        // manually add actionBtnCollumn
        // cannot change the btn at this stage because group info has not been fetched from db

        actionOneCol = addGroupColumn("Action One");
        actionTwoCol = addGroupColumn("Action Two");

        // update the action collumn based on the groups info
        updateTableView();

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
        String ALL_GROUP_URI = URI + "/all";
        HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder("GET", ALL_GROUP_URI, true);
        HttpRequestBase request = httpRequestBuilder.getHttpRequest();
        CloseableHttpClient httpClient = httpRequestBuilder.getHttpClient();
        httpResponseService.handleReponse(request, httpClient, this::handleGetAllGroups);
//        return null;
    }

    public void handleGetAllGroups(CloseableHttpResponse response, Object jsonResponse) {
        List<Group> updatedAllGroups = new ArrayList<>();
        JSONArray array = controllerUtils.toJSONArray(jsonResponse);
//        System.out.println(array);
        if (array != null) {
            for (Object groupObject : array) {
//                System.out.println(groupObject);
                JSONObject owner = (JSONObject) ((JSONObject) groupObject).get("owner");
                GroupOwner groupOwner = new GroupOwner((int) owner.get("id"), (String) owner.get("username"));
                int id = (int) ((JSONObject) groupObject).get("id");
                String name = (String) ((JSONObject) groupObject).get("name");
                String description = (String) ((JSONObject) groupObject).get("description");
                JSONArray userListObj = (JSONArray) ((JSONObject) groupObject).get("userGroupParticipationsList");
                List<AppUser> userList = createUserList(userListObj);
                Group newGroup = new Group(id, name, description, groupOwner, userList);
                updatedAllGroups.add(newGroup);
            }
            this.allgroups = updatedAllGroups;
            System.out.println(this.allgroups);
            setupTable();
        }
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

    public TableColumn<Group, Group> addGroupColumn(String columnName) {
        int TABLE_CELL_WIDTH = 100;
        TableColumn<Group, Group> column = ViewUtils.column(columnName, ReadOnlyObjectWrapper<Group>::new, TABLE_CELL_WIDTH);

        groupTable.getColumns().add(column);
        column.setCellFactory(col -> {
            Button editButton = new Button(columnName);
            TableCell<Group, Group> cell = new TableCell<Group, Group>() {
                @Override
                public void updateItem(Group person, boolean empty) {
                    super.updateItem(person, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(editButton);
                    }
                }
            };

            editButton.setOnAction(e -> System.out.println("click edit button for: " + cell.getItem().getGroupOwner()));
            return cell;
        });
        return column;
    }

    private void updateColumnOne() {
        String owner = TokenStorage.getUser();
//        String fName = "Jacob";
        actionOneCol.setCellFactory(col -> {
            Button editButton = new Button("Edit");
//            Button joinButton = new Button("Join");
            TableCell<Group, Group> updatedCell = new TableCell<Group, Group>() {
                @Override
                // display button
                public void updateItem(Group group, boolean empty) {
                    super.updateItem(group, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        if (owner.equals(group.getGroupOwner().getUsername())) {
                            setGraphic(editButton);
                            ViewUtils.addStyle(editButton, "/edit-button.css");
                        } else {
                            setGraphic(null);
                        }
                    }
                }
            };

            // updatedCell.getItem() == group object
            editButton.setOnAction(e -> {
                Button source = (Button) e.getSource();
                System.out.println("is button " + source);
                edit(updatedCell.getItem(), (source));
            });
            this.controllerUtils.setDefaultAndHandCursorBehaviour(editButton);
            return updatedCell;

        });
    }


    private void updateColumnTwo() {
        String owner = TokenStorage.getUser();
//        String fName = "Jacob";
        actionTwoCol.setCellFactory(col -> {
            Button deleteButton = new Button("Delete");
            Button leaveButton = new Button("Leave");
            Button joinButton = new Button("Join");
//            Button joinButton = new Button("Join");
            TableCell<Group, Group> updatedCell = new TableCell<Group, Group>() {
                @Override
                public void updateItem(Group group, boolean empty) {
                    super.updateItem(group, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        if (owner.equals(group.getGroupOwner().getUsername())) {
                            setGraphic(deleteButton);
                            ViewUtils.addStyle(deleteButton, "/delete-button.css");
//                            deleteButton.setOnAction(e -> System.out.println("delete button click id: "+updatedCell.getId()));
                        } else if (group.isExist(owner)) {
                            setGraphic(leaveButton);
                            ViewUtils.addStyle(leaveButton, "/leave-button.css");
                        } else {
                            setGraphic(joinButton);
                            ViewUtils.addStyle(joinButton, "/join-button.css");
                        }
                    }
                }

            };


            deleteButton.setOnAction(e -> {
                Group group = updatedCell.getItem();
                System.out.println("delete button click group: " + group);
                delete(group);
            });
            leaveButton.setOnAction(e -> {
                Group group = updatedCell.getItem();
                System.out.println("leave button click: group " + group);
                leave(group);
            });

            joinButton.setOnAction(e -> {
                Group group = updatedCell.getItem();
                System.out.println("join button click: group " + group);
                join(group);

            });

            this.controllerUtils.setDefaultAndHandCursorBehaviour(deleteButton);
            this.controllerUtils.setDefaultAndHandCursorBehaviour(leaveButton);
            this.controllerUtils.setDefaultAndHandCursorBehaviour(joinButton);

            return updatedCell;

        });
    }

    private void updateTableView() {
        getAllgroups();
        updateColumnOne();
        updateColumnTwo();
    }


    public void join(Group group) {
        System.out.println("Join group");
        int groupId = group.getId();
        String JOIN_URI = URI + "/" + groupId + "/join";
        System.out.println(JOIN_URI);
        HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder("POST", JOIN_URI, true);
        HttpRequestBase request = httpRequestBuilder.getHttpRequest();
        CloseableHttpClient httpClient = httpRequestBuilder.getHttpClient();
        httpResponseService.handleReponse(request, httpClient, this::handleJoinOrLeaveOrDeleteResponse);
    }

    public void leave(Group group) {
        System.out.println("leave group");
        int groupId = group.getId();
        String LEAVE_URI = URI + "/" + groupId + "/leave";
        System.out.println(LEAVE_URI);
        HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder("DELETE", LEAVE_URI, true);
        HttpRequestBase request = httpRequestBuilder.getHttpRequest();
        CloseableHttpClient httpClient = httpRequestBuilder.getHttpClient();
        httpResponseService.handleReponse(request, httpClient, this::handleJoinOrLeaveOrDeleteResponse);
    }

    public void delete(Group group) {
        System.out.println("delete group");
        int groupId = group.getId();
        String DELETE_URI = URI + "/" + groupId;
        System.out.println(DELETE_URI);
        HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder("DELETE", DELETE_URI, true);
        HttpRequestBase request = httpRequestBuilder.getHttpRequest();
        CloseableHttpClient httpClient = httpRequestBuilder.getHttpClient();
        httpResponseService.handleReponse(request, httpClient, this::handleJoinOrLeaveOrDeleteResponse);
    }

    public void handleJoinOrLeaveOrDeleteResponse(CloseableHttpResponse response, Object object) {
        System.out.println("response " + response);
        updateTableView();
    }

    public void edit(Group group, Button button) {
        //set a singleton object to use in edit page
        String FXMLString = "/fxml/main_pages/groups/group_info_create_group.fxml";
        SelectedGroup selectedGroup = SelectedGroup.getInstance();
        selectedGroup.setId(group.getId());
        controllerUtils.goPage(stage, button, FXMLString);
    }

    public void setDefaultAndHandCursorBehaviour(Button button) {
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, (e -> this.controllerUtils.setHandCursor((Button) e.getSource())));
        button.addEventHandler(MouseEvent.MOUSE_EXITED, (e -> this.controllerUtils.setDefaultCursor((Button) e.getSource())));
    }

}
