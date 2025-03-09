package utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Group;
import model.GroupMember;
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

                    String ownerEmail = null;
                    GroupOwner groupOwner = new GroupOwner(owner.getInt("id"), owner.getString("username"), ownerEmail);
                    int id = jsonGroup.getInt("id");
                    String name = jsonGroup.getString("name");
                    String description = jsonGroup.getString("description");
                    int numberOfMembers = jsonGroup.has("numberOfMembers")? jsonGroup.getInt("numberOfMembers") : jsonGroup.getJSONArray("userGroupParticipationsList").length();

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

    public void fetchGroupMembers(String url, String token, int groupId, List<GroupMember> groupMembers) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + groupId + "/members")) // Assuming the API has an endpoint to fetch members for a specific group
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONArray result = new JSONArray(response.body());

                for (Object memberObject : result) {
                    JSONObject jsonMember = (JSONObject) memberObject;
                    int memberId = jsonMember.getInt("id");
                    String username = jsonMember.getString("username");
                    String email = jsonMember.getString("email");
/*
                    GroupMember newMember = new GroupMember(memberId, username, email);
                    System.out.println(newMember);
                    groupMembers.add(newMember);*/
                }
            } else {
                System.out.println("Failed to fetch group members: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Error fetching group members: " + e.getMessage());
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
                String responseBody = response.body();

                int groupId = Integer.parseInt(responseBody.split("\"id\":")[1].split(",")[0]);
                String name = responseBody.split("\"name\":\"")[1].split("\"")[0];
                String description = responseBody.split("\"description\":\"")[1].split("\"")[0];

                int ownerId = Integer.parseInt(responseBody.split("\"owner\":\\{\"id\":")[1].split(",")[0]);
                String ownerUsername = responseBody.split("\"username\":\"")[1].split("\"")[0];
                String ownerEmail = responseBody.split("\"email\":\"")[1].split("\"")[0];

                GroupOwner groupOwner = new GroupOwner(ownerId, ownerUsername, ownerEmail);
                return new Group(groupId, name, description, groupOwner, 0);
            }
        } catch (Exception e) {
            System.err.println("Error finding group: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static boolean updateGroup(String url, int groupId, String requestBody, String token) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + groupId)) // URL with group ID
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody)) // Send updated data
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Update Response Status: " + response.statusCode());
            System.out.println("Update Response Body: " + response.body());

            return response.statusCode() == 200; // Return true if update is successful
        } catch (Exception e) {
            System.err.println("Error updating group: " + e.getMessage());
            return false;
        }
    }
}
