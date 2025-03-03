package controller.groups;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import model.Group;
import model.TokenStorage;
import utils.GroupServices;

import java.util.ArrayList;
import java.util.List;


public class MyGroupsController {

    @FXML
    private TableView<Group> joinedGroupTable;

    @FXML
    private TableColumn<Group, Integer> idCol;
    @FXML
    private TableColumn<?, ?> actionCol;
    @FXML
    private TableColumn<?, ?> editCol;
    @FXML
    private TableColumn<Group, String> groupNameCol;
    @FXML
    private TableColumn<Group, Integer> numOfMembersCol;
    @FXML
    private TableColumn<Group, String> ownerCol;

    @FXML
    private TableView<Group> canJoinGroupTable;
    @FXML
    private TableColumn<Group, Integer> idCol1;
    @FXML
    private TableColumn<?, ?> actionCol1;
    @FXML
    private TableColumn<?, ?> editCol1;
    @FXML
    private TableColumn<Group, String> groupNameCol1;
    @FXML
    private TableColumn<Group, Integer> numOfMembersCol1;
    @FXML
    private TableColumn<Group, String> ownerCol1;

    private final List<Group> joinedGroups = new ArrayList<>();
    private final List<Group> canJoinGroups = new ArrayList<>();


    public void initialize() {

        /*
        update the joined group table
         */
        GroupServices gs = new GroupServices();

        new Thread(() -> {
            gs.fetchGroups("http://localhost:8093/api/groups/my-groups", TokenStorage.getToken(), joinedGroups);

            Platform.runLater(() -> {
                gs.updateGroupsUI(joinedGroups, joinedGroupTable, idCol, groupNameCol, ownerCol, numOfMembersCol);
            });
        }).start();

        new Thread(() -> {
            gs.fetchGroups("http://localhost:8093/api/groups/available", TokenStorage.getToken(), canJoinGroups);

            Platform.runLater(() -> {
               gs.updateGroupsUI(canJoinGroups, canJoinGroupTable, idCol1, groupNameCol1, ownerCol1, numOfMembersCol1);
            });

        }).start();


    }


    public void groupsBtnClick(MouseEvent mouseEvent) {

    }

    public void allGroupsBtnClick(MouseEvent mouseEvent) {

    }

    public void accountBtnClick(MouseEvent mouseEvent) {

    }

    public void logOutBtnClick(MouseEvent mouseEvent) {

    }

    public void tableClicked(MouseEvent mouseEvent) {

    }
}
