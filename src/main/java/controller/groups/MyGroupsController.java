package controller.groups;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Group;
import model.TokenStorage;
import utils.ControllerUtils;
import utils.GroupServices;

import java.util.ArrayList;
import java.util.List;


public class MyGroupsController {

    @FXML
    private BorderPane root;

    @FXML
    private TableView<Group> joinedGroupTable;

    @FXML
    private TableColumn<Group, Integer> idCol;
    @FXML
    private TableColumn<Group, Void> editCol;
    @FXML
    private TableColumn<Group, Void> deleteCol;
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
    private TableColumn<Group, Void> joinCol1;
    @FXML
    private TableColumn<Group, String> groupNameCol1;
    @FXML
    private TableColumn<Group, Integer> numOfMembersCol1;
    @FXML
    private TableColumn<Group, String> ownerCol1;

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
    @FXML
    private Button createGroupBtn;

    @FXML
    private Label seeAllLabel;


    private ControllerUtils controllerUtils;


    Stage stage;


    private final List<Group> joinedGroups = new ArrayList<>();
    private final List<Group> canJoinGroups = new ArrayList<>();

    private static final String FXMLSource = "/fxml";
    private static final String CSSSOURCE = "/CSS";

    public void initialize() {

        controllerUtils = new ControllerUtils();

        /*
        update the joined group table
         */
        GroupServices gs = new GroupServices();

        new Thread(() -> {
            gs.fetchGroups("http://localhost:8093/api/groups/my-groups", TokenStorage.getToken(), joinedGroups);

            Platform.runLater(() -> {
                gs.updateGroupsUI(joinedGroups, joinedGroupTable, idCol, groupNameCol, ownerCol, numOfMembersCol, editCol, deleteCol);
            });
        }).start();

        new Thread(() -> {
            gs.fetchGroups("http://localhost:8093/api/groups/available", TokenStorage.getToken(), canJoinGroups);

            Platform.runLater(() -> {
                gs.updateGroupsUI(canJoinGroups, canJoinGroupTable, idCol1, groupNameCol1, ownerCol1, numOfMembersCol1, joinCol1);
            });

        }).start();

        root.getStylesheets().add(getClass().getResource(CSSSOURCE + "/button.css").toExternalForm());
        root.getStylesheets().add(getClass().getResource(CSSSOURCE + "/text_input.css").toExternalForm());
        root.getStylesheets().add(getClass().getResource(CSSSOURCE + "/table_view.css").toExternalForm());

    }

    @FXML
    public void mouseEnter() {
        this.controllerUtils.setHandCursor(myNotesBtn);
        this.controllerUtils.setHandCursor(shareNotesBtn);
        this.controllerUtils.setHandCursor(myGroupsBtn);
        this.controllerUtils.setHandCursor(allGroupsBtn);
        this.controllerUtils.setHandCursor(logOutBtn);
        this.controllerUtils.setHandCursor(createGroupBtn);
        this.controllerUtils.setHandCursor(seeAllLabel);
    }

    @FXML
    public void mouseExit() {
        this.controllerUtils.setDefaultCursor(myNotesBtn);
        this.controllerUtils.setDefaultCursor(shareNotesBtn);
        this.controllerUtils.setDefaultCursor(myGroupsBtn);
        this.controllerUtils.setDefaultCursor(allGroupsBtn);
        this.controllerUtils.setDefaultCursor(logOutBtn);
        this.controllerUtils.setDefaultCursor(createGroupBtn);
        this.controllerUtils.setDefaultCursor(seeAllLabel);

    }

    @FXML
    public void myNotesBtnClick() {
        this.controllerUtils.goPage(stage, myNotesBtn, FXMLSource + "/main_pages/main_page.fxml");
    }

    @FXML
    public void shareNotesBtnClick() {
//        this.controllerUtils.goPage(stage,shareNotesBtn, FXMLSource+"/");
        System.out.println("go to share notes page");
    }

    @FXML
    public void myGroupsBtnClick() {
        this.controllerUtils.goPage(stage, myGroupsBtn, FXMLSource + "/main_pages/groups/my_groups.fxml");
    }

    @FXML
    public void allGroupsBtnClick() {
        this.controllerUtils.goPage(stage, allGroupsBtn, FXMLSource + "/main_pages/groups/all_groups.fxml");
    }

    @FXML
    public void accountBtnClick() {
        this.controllerUtils.goPage(stage, accountBtn, FXMLSource + "/main_pages/account_user_info_page.fxml");
    }

    @FXML
    public void logOutBtnClick() {
        this.controllerUtils.logout(stage, logOutBtn);
    }

    @FXML
    private void createGroupBtnClick() {
        this.controllerUtils.goPage(stage, createGroupBtn, "/fxml/main_pages/groups/group_info_create_group.fxml");
    }

    @FXML
    private void seeAllClick() {
        this.controllerUtils.goPage(stage, seeAllLabel, "/fxml/main_pages/groups/all_groups.fxml");
    }

    @FXML
    private void tableClicked() {

    }

}
