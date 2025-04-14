package utils;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.*;
import model.selected.SelectedGroup;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class GroupControllerUtils {
    private static final String URI = "http://localhost:8093/api/groups";
    private static final ObservableResourceFactory RESOURCE_FACTORY = ObservableResourceFactory.getInstance();


    public static void setupGroupTable(
            TableView groupTable,
            List<Group> groupList,
            TableColumn col1,
            TableColumn col2,
            TableColumn col3,
            TableColumn col4) {
        setCellValue(col1, "Id");
        setCellValue(col2, "Name");
        setCellValue(col3, "GroupOwnerName");
        setCellValue(col4, "NumberOfMembers");
        ObservableList<Group> observableList = FXCollections.observableArrayList(groupList);
        groupTable.setItems(observableList);
    }

    public static void setCellValue(TableColumn<Group, Integer> columnLabel, String groupIntProperty) {
        columnLabel.setCellValueFactory(new PropertyValueFactory<>(groupIntProperty));
    }

    public static TableColumn addGroupColumn(TableView table, String columnName) {
        int TABLE_CELL_WIDTH = 100;
        TableColumn<Group, Group> column = ViewUtils.column(columnName, ReadOnlyObjectWrapper<Group>::new, TABLE_CELL_WIDTH);

        table.getColumns().add(column);
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


    public static void updateColumnOne(Stage stage, TableColumn actionOneCol) {
        String owner = TokenStorage.getUser();
        ResourceBundle rb = RESOURCE_FACTORY.getResourceBundle();
//        String fName = "Jacob";
        actionOneCol.setCellFactory(col -> {
            Button editButton = new Button(rb.getString("editBtnText"));
            Button viewButton = new Button (rb.getString("viewBtnText"));
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
                        } else if (group.isExist(owner) &&  (!group.getGroupOwner().equals(owner))) {
                            System.out.println("Owner: "+owner+ "is in group: "+group.getName());
                            setGraphic(viewButton);
                            ViewUtils.addStyle(viewButton, "/view-button.css");
                        }
                        else {
                            setGraphic(null);
                        }
                    }
                }
            };

            // updatedCell.getItem() == group object
            editButton.setOnAction(e -> {
                Button source = (Button) e.getSource();
                System.out.println("is button " + source);
                edit(stage, updatedCell.getItem(), source);
            });
            viewButton.setOnAction( e-> {
                Button source = (Button) e.getSource();
                System.out.println("is button " + source+", view button click");
                view(stage, updatedCell.getItem(), source)
                ;
            });
            ControllerUtils_v2.setDefaultAndHandCursorBehaviour(editButton);
            ControllerUtils_v2.setDefaultAndHandCursorBehaviour(viewButton);
            return updatedCell;

        });
    }


    public static void updateColumnTwo(
            TableColumn actionTwoCol,
            HttpResponseService httpResponseService,
            HandleResponseCallback handleReponse) {
        String owner = TokenStorage.getUser();
        ResourceBundle rb = RESOURCE_FACTORY.getResourceBundle();
//        String fName = "Jacob";
        actionTwoCol.setCellFactory(col -> {
            Button deleteButton = new Button(rb.getString("deleteBtnText"));
            Button leaveButton = new Button(rb.getString("leaveBtnText"));
            Button joinButton = new Button(rb.getString("joinBtnText"));
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
                delete(group, httpResponseService, handleReponse);
            });
            leaveButton.setOnAction(e -> {
                Group group = updatedCell.getItem();
                System.out.println("leave button click: group " + group);
                leave(group, httpResponseService, handleReponse);
            });

            joinButton.setOnAction(e -> {
                Group group = updatedCell.getItem();
                System.out.println("join button click: group " + group);
                join(group, httpResponseService, handleReponse);

            });

            ControllerUtils_v2.setDefaultAndHandCursorBehaviour(deleteButton);
            ControllerUtils_v2.setDefaultAndHandCursorBehaviour(leaveButton);
            ControllerUtils_v2.setDefaultAndHandCursorBehaviour(joinButton);

            return updatedCell;

        });
    }

    public static void view(Stage stage, Group group, Button button){
        String pageLink = "/fxml/main_pages/groups/readonly_group_page.fxml";
        SelectedGroup selectedGroup = SelectedGroup.getInstance();
        selectedGroup.setId(group.getId());
        ControllerUtils_v2.goPage(stage, button, pageLink);
    }

    public static void edit(Stage stage, Group group, Button button) {
        //set a singleton object to use in edit page
//        String FXMLString = "/fxml/main_pages/groups/group_info_create_group.fxml";
        String pageLink = "/fxml/main_pages/groups/group_info_edit_group.fxml";

        SelectedGroup selectedGroup = SelectedGroup.getInstance();
        selectedGroup.setId(group.getId());
        ControllerUtils_v2.goPage(stage, button, pageLink);
    }

    public static void join(Group group, HttpResponseService httpResponseService, HandleResponseCallback callback) {
        System.out.println("Join group");
        int groupId = group.getId();
        String JOIN_URI = URI + "/" + groupId + "/join";
        System.out.println(JOIN_URI);
        HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder("POST", JOIN_URI, true);
        HttpRequestBase request = httpRequestBuilder.getHttpRequestBase();
        CloseableHttpClient httpClient = httpRequestBuilder.getHttpClient();
        httpResponseService.handleReponse(request, httpClient, callback);
    }

    public static void delete(Group group, HttpResponseService httpResponseService, HandleResponseCallback callback) {
        System.out.println("delete group");
        int groupId = group.getId();
        String DELETE_URI = URI + "/" + groupId;
        System.out.println(DELETE_URI);
        HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder("DELETE", DELETE_URI, true);
        HttpRequestBase request = httpRequestBuilder.getHttpRequestBase();
        CloseableHttpClient httpClient = httpRequestBuilder.getHttpClient();
        httpResponseService.handleReponse(request, httpClient, callback);
    }

    public static void leave(Group group, HttpResponseService httpResponseService, HandleResponseCallback callback) {
        System.out.println("leave group");
        int groupId = group.getId();
        String LEAVE_URI = URI + "/" + groupId + "/leave";
        System.out.println(LEAVE_URI);
        HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder("DELETE", LEAVE_URI, true);
        HttpRequestBase request = httpRequestBuilder.getHttpRequestBase();
        CloseableHttpClient httpClient = httpRequestBuilder.getHttpClient();
        httpResponseService.handleReponse(request, httpClient, callback);
    }


    public static void updateTableView(Stage stage, TableColumn actionOneCol, TableColumn actionTwoCol, HttpResponseService httpResponseService, HandleResponseCallback getAllCallback, HandleResponseCallback joinDeleteRemoveCallBack) {
        getAllgroups(httpResponseService, getAllCallback);
        updateColumnOne(stage, actionOneCol);
//        updateColumnOne();
        updateColumnTwo(actionTwoCol, httpResponseService, joinDeleteRemoveCallBack);
    }

//    public static void getJoinedAndCreatedGroups(HttpResponseService httpResponseService, HandleResponseCallback callback){
//
//    }

    public static void updateJoinedTable(Stage stage, TableColumn actionOneCol, TableColumn actionTwoCol, HttpResponseService httpResponseService, HandleResponseCallback getJoinedCallback, HandleResponseCallback joinDeleteLeaveCallBack) {
//    public static void updateJoinedTable(Stage stage, TableColumn actionOneCol, TableColumn actionTwoCol, HttpResponseService httpResponseService, HandleResponseCallback getJoinedCallback) {
        getJoinedGroup(httpResponseService, getJoinedCallback);
        updateColumnOne(stage, actionOneCol);
////        updateColumnOne();
        updateColumnTwo(actionTwoCol, httpResponseService, joinDeleteLeaveCallBack);
    }

    public static void getJoinedGroup(HttpResponseService httpResponseService, HandleResponseCallback callback) {
        String JOIN_GROUP_URI = URI + "/my-groups";
        HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder("GET", JOIN_GROUP_URI, true);
        HttpRequestBase request = httpRequestBuilder.getHttpRequestBase();
        CloseableHttpClient httpClient = httpRequestBuilder.getHttpClient();
        httpResponseService.handleReponse(request, httpClient, callback);
//        return null;
    }

    public static void getAllgroups(HttpResponseService httpResponseService, HandleResponseCallback callback) {
        String ALL_GROUP_URI = URI + "/all";
        HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder("GET", ALL_GROUP_URI, true);
        HttpRequestBase request = httpRequestBuilder.getHttpRequestBase();
        CloseableHttpClient httpClient = httpRequestBuilder.getHttpClient();
        httpResponseService.handleReponse(request, httpClient, callback);
//        return null;
    }

    //    public static void updateCanJoinTable(Stage stage, TableColumn actionOneCol, TableColumn actionTwoCol, HttpResponseService httpResponseService, HandleResponseCallback getJoinedCallback, HandleResponseCallback joinDeleteLeaveCallBack) {
    public static void updateCanJoinTable(Stage stage, TableColumn actionOneCol, HttpResponseService httpResponseService, HandleResponseCallback getCanJoinCallBack, HandleResponseCallback getJoinCallBack) {
        getCanJoinGroup(httpResponseService, getCanJoinCallBack);
//        updateColumnOne(stage, actionOneCol);
//////        updateColumnOne();
        updateColumnTwo(actionOneCol, httpResponseService, getJoinCallBack);
    }

    public static void getCanJoinGroup(HttpResponseService httpResponseService, HandleResponseCallback callback) {
        String CAN_JOIN_URI = URI + "/available";
        HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder("GET", CAN_JOIN_URI, true);
        HttpRequestBase request = httpRequestBuilder.getHttpRequestBase();
        CloseableHttpClient httpClient = httpRequestBuilder.getHttpClient();
        httpResponseService.handleReponse(request, httpClient, callback);
//        return null;
    }


    public static List<AppUser> createUserList(JSONArray userObjectArray) {
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

    public static List<Group> getGroupListInfoFromJSONArray(JSONArray array) {
        List<Group> updatedAllGroups = new ArrayList<>();
        for (Object groupObject : array) {
            Group newGroup;
//            JSONArray userListObj = null;
            JSONObject owner = (JSONObject) ((JSONObject) groupObject).get("owner");
            String email = owner.optString("email", "");
            GroupOwner groupOwner = new GroupOwner((int) owner.get("id"), (String) owner.get("username"), email);
            int id = (int) ((JSONObject) groupObject).get("id");
            String name = (String) ((JSONObject) groupObject).get("name");
            String description = (String) ((JSONObject) groupObject).get("description");


           JSONArray userListObj = (JSONArray) ((JSONObject) groupObject).optJSONArray("userGroupParticipationsList",null);

            if (userListObj != null) {

                List<AppUser> userList = createUserList(userListObj);
                newGroup = new Group(id, name, description, groupOwner, userList);
            } else {
                int numberOfUsers = (Integer) ((JSONObject) groupObject).get("numberOfMembers");
                newGroup = new Group(id, name, description, groupOwner, numberOfUsers);

            }
            updatedAllGroups.add(newGroup);
        }
        return updatedAllGroups;

    }

    public static String getSelectGroupURI(SelectedGroup selectedGroup) {
        int groupId = selectedGroup.getId();
        return "http://localhost:8093/api/groups/" + groupId;
    }

}
