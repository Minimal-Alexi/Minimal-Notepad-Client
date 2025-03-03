package utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Group;
import model.GroupOwner;
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

                    System.out.println("ssssssssssssssssssssssssssssssssssssssssssssss");
                    System.out.println(groupObject);
                    System.out.println(groupOwner);
                    System.out.println(id);
                    System.out.println(name);
                    System.out.println(description);
                    System.out.println(numberOfMembers);
                    System.out.println("ssssssssssssssssssssssssssssssssssssssssssssss");

                    Group newGroup = new Group(id, name, description, groupOwner, numberOfMembers);
                    System.out.println(newGroup);
                    System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                    groups.add(newGroup);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
    update table ui
     */
    public void updateGroupsUI(List<Group> groups,
                               TableView<Group> groupsTable,
                               TableColumn<Group, Integer> idCol,
                               TableColumn<Group, String> groupNameCol,
                               TableColumn<Group, String> ownerCol,
                               TableColumn<Group, Integer> numOfMembersCol) {
        idCol.setCellValueFactory(new PropertyValueFactory<>("Id"));
        groupNameCol.setCellValueFactory(new PropertyValueFactory<>("Name"));
        ownerCol.setCellValueFactory(new PropertyValueFactory<>("GroupOwnerName"));
        numOfMembersCol.setCellValueFactory(new PropertyValueFactory<>("NumberOfMembers"));

        ObservableList<Group> observableList = FXCollections.observableArrayList(groups);
        groupsTable.setItems(observableList);
    }

}
