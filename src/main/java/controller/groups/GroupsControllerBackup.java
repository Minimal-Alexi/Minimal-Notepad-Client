package controller.groups;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.TokenStorage;

import java.io.IOException;
import java.util.Objects;

import static utils.MainPageServices.updateLocalTime;
import static utils.MainPageServices.updateNameLabel;

public class GroupsControllerBackup {

    @FXML
    private Label localTime;
    @FXML
    private Label nameLabel;

    @FXML
    private Button accountBtn;

    @FXML
    private TableColumn<?, ?> category;

    @FXML
    private TableColumn<?, ?> category1;

    @FXML
    private TableColumn<?, ?> createTime;

    @FXML
    private TableColumn<?, ?> createTime1;

    @FXML
    private Button favoritiesBtn;

    @FXML
    private TableColumn<?, ?> group;

    @FXML
    private TableColumn<?, ?> group1;

    @FXML
    private Button groupsBtn;

    @FXML
    private MenuButton groupMenu;
    @FXML
    private MenuItem btn1;
    @FXML
    private MenuItem btn2;

    @FXML
    private MenuItem btn3;


    @FXML
    private Button myFileBtn;

    @FXML
    private TableColumn<?, ?> name;

    @FXML
    private TableColumn<?, ?> name1;


    @FXML
    private TableColumn<?, ?> owner;

    @FXML
    private TableColumn<?, ?> owner1;

    @FXML
    private Button recycleBinBtn;

    @FXML
    private Button settingBtn;

    @FXML
    private Button shareNoteBtn;

    @FXML
    private TableView<?> table;

    @FXML
    private TableView<?> table1;


    public void initialize() {
        updateLocalTime(localTime);
        updateNameLabel(nameLabel, TokenStorage.getUser());
        groupMenu.setOnHiding(event -> {
            // Your code to handle the event when the mouse moves outside the MenuButton
            System.out.println("Mouse moved outside the MenuButton.");
        });
    }

    public void createGroupClicked(ActionEvent event) {

    }

    /*
    Go to another page
     */
    private Stage stage;
    private Scene scene;
    private Parent root;

    public void myFilesClicked(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/main_pages/main_page.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }



    @FXML
    void groupsClicked(ActionEvent event) {

    }

    @FXML
    void accountBtnClick(MouseEvent event) {

    }

    // CAN ONLY WORK ONMOVE EVENT
    @FXML
    void groupMenuEnter() {
        System.out.println("mouse enter");
        groupMenu.setStyle("-fx-background-color: #A276FF; -fx-text-fill: #FFFFFF;");
        groupMenu.show();
    }


    @FXML
    void groupMenuExit() {
        System.out.println("mouse exit");
        groupMenu.setOnMouseExited(event -> groupMenu.setStyle("")); // Reset style on mouse exit
        groupMenu.hide();
    }




}
