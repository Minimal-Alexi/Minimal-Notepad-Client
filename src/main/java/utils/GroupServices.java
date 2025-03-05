package utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
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
     * Fetch groups from API
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
                    JSONObject jsonGroup = (JSONObject) groupObject;
                    JSONObject owner = jsonGroup.getJSONObject("owner");

                    GroupOwner groupOwner = new GroupOwner(owner.getInt("id"), owner.getString("username"));
                    int id = jsonGroup.getInt("id");
                    String name = jsonGroup.getString("name");
                    String description = jsonGroup.getString("description");
                    int numberOfMembers = jsonGroup.getInt("numberOfMembers");

                    Group newGroup = new Group(id, name, description, groupOwner, numberOfMembers);
                    System.out.println(newGroup);
                    groups.add(newGroup);
                }
            } else {
                System.out.println("Failed to fetch groups: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Error fetching groups: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /*
     * Update Groups UI
     */
    public void updateGroupsUI(List<Group> groups,
                               TableView<Group> groupsTable,
                               TableColumn<Group, Integer> idCol,
                               TableColumn<Group, String> groupNameCol,
                               TableColumn<Group, String> ownerCol,
                               TableColumn<Group, Integer> numOfMembersCol,
                               TableColumn<Group, Void> actionCol,
                               String actionType) {

        idCol.setCellValueFactory(new PropertyValueFactory<>("Id"));
        groupNameCol.setCellValueFactory(new PropertyValueFactory<>("Name"));
        ownerCol.setCellValueFactory(new PropertyValueFactory<>("GroupOwnerName"));
        numOfMembersCol.setCellValueFactory(new PropertyValueFactory<>("NumberOfMembers"));

        ObservableList<Group> observableList = FXCollections.observableArrayList(groups);
        groupsTable.setItems(observableList);

        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button actionButton = new Button(actionType);

            {
                actionButton.setOnAction(event -> {
                    Group g = getTableView().getItems().get(getIndex());

                    if ("Edit".equals(actionType)) {
                        SelectedGroup selectedGroup = SelectedGroup.getInstance();
                        selectedGroup.setId(g.getId());

                        System.out.println("Editing group: " + g.getId() + " " + g.getName());

                        String pageLink = "/fxml/main_pages/groups/group_info_edit_group.fxml";
                        ControllerUtils c = new ControllerUtils();
                        Stage stage = (Stage) actionButton.getScene().getWindow();
                        c.goPage(stage, actionButton, pageLink);
                    } else if ("Delete".equals(actionType)) {
                        System.out.println("Deleting group: " + g.getId());
                        // Implement delete logic here
                    } else if ("Join".equals(actionType)) {
                        System.out.println("Joining group: " + g.getId());
                        // Implement join logic here
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actionButton);
            }
        });
    }

    /*
     * Find a group by ID
     */
    public static Group findGroupById(String url, Integer id, String token) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());

            if (response.statusCode() == 200) {
                JSONObject result = new JSONObject(response.body());

                int groupId = result.getInt("id");
                String name = result.getString("name");
                String description = result.getString("description");
                int numberOfMembers = result.getInt("numberOfMembers");

                JSONObject ownerJson = result.getJSONObject("owner");
                GroupOwner groupOwner = new GroupOwner(ownerJson.getInt("id"), ownerJson.getString("username"));

                return new Group(groupId, name, description, groupOwner, numberOfMembers);
            } else {
                System.out.println("Failed to find group: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Error finding group: " + e.getMessage());
        }
        return null;
    }
}
