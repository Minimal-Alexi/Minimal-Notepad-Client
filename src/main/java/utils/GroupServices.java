package utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Group;
import model.GroupOwner;
import model.selected.SelectedGroup;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class GroupServices {
    /*
    fetch groups api
     */
    public void fetchGroups(String url, String token, List<Group> groups) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JSONArray result = new JSONArray(response.body());
                for (Object groupObject : result) {
                    JSONObject owner = (JSONObject) ((JSONObject) groupObject).get("owner");
                    GroupOwner groupOwner = new GroupOwner((int) owner.get("id"), (String) owner.get("username"));
                    int id = (int) ((JSONObject) groupObject).get("id");
                    String name = (String) ((JSONObject) groupObject).get("name");
                    String description = (String) ((JSONObject) groupObject).get("description");
                    int numberOfMembers = (int) ((JSONObject) groupObject).get("numberOfMembers");
                    Group newGroup = new Group(id, name, description, groupOwner, numberOfMembers);
                    System.out.println(newGroup);
                    groups.add(newGroup);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
    fetch note from certain groups
     */


    /*
    update table ui
     */
    public void updateGroupsUI(List<Group> groups,
                               TableView<Group> groupsTable,
                               TableColumn<Group, Integer> idCol,
                               TableColumn<Group, String> groupNameCol,
                               TableColumn<Group, String> ownerCol,
                               TableColumn<Group, Integer> numOfMembersCol,
                               TableColumn<Group, Void> editCol,
                               TableColumn<Group, Void> deleteCol) {
        idCol.setCellValueFactory(new PropertyValueFactory<>("Id"));
        groupNameCol.setCellValueFactory(new PropertyValueFactory<>("Name"));
        ownerCol.setCellValueFactory(new PropertyValueFactory<>("GroupOwnerName"));
        numOfMembersCol.setCellValueFactory(new PropertyValueFactory<>("NumberOfMembers"));

        ObservableList<Group> observableList = FXCollections.observableArrayList(groups);
        groupsTable.setItems(observableList);

        editCol.setCellFactory(param -> new TableCell<Group, Void>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setOnAction(event -> {
                    // Go to edit groups page
                    Group g = getTableView().getItems().get(getIndex());
                    SelectedGroup selectedGroup = SelectedGroup.getInstance();
                    selectedGroup.setId(g.getId());

                    System.out.println(g.getId() + " " + g.getName());

                    String pageLink = "/fxml/main_pages/main_page.fxml";
                    ControllerUtils c = new ControllerUtils();
                    Stage stage = (Stage) editButton.getScene().getWindow();
                    c.goPage(stage ,editButton, pageLink);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });

        deleteCol.setCellFactory(param -> new TableCell<Group, Void>() {
            private final Button editButton = new Button("Delete");

            {
                editButton.setOnAction(event -> {
                    System.out.println("Delete ddddddddddddddd");
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });
    }

    public void updateGroupsUI(List<Group> groups,
                               TableView<Group> groupsTable,
                               TableColumn<Group, Integer> idCol,
                               TableColumn<Group, String> groupNameCol,
                               TableColumn<Group, String> ownerCol,
                               TableColumn<Group, Integer> numOfMembersCol,
                               TableColumn<Group, Void> joinCol) {
        idCol.setCellValueFactory(new PropertyValueFactory<>("Id"));
        groupNameCol.setCellValueFactory(new PropertyValueFactory<>("Name"));
        ownerCol.setCellValueFactory(new PropertyValueFactory<>("GroupOwnerName"));
        numOfMembersCol.setCellValueFactory(new PropertyValueFactory<>("NumberOfMembers"));

        ObservableList<Group> observableList = FXCollections.observableArrayList(groups);
        groupsTable.setItems(observableList);

        joinCol.setCellFactory(param -> new TableCell<Group, Void>() {
            private final Button editButton = new Button("Join");

            {
                editButton.setOnAction(event -> {
                    System.out.println("Join ddddddddddddddd");
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });
    }

    /*

     */

}
