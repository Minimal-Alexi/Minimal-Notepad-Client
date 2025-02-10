package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import model.Note;
import model.TokenStorage;

import java.io.IOException;
import java.util.Objects;

public class MainPageController {
    @FXML
    private TableView<Note> table;
    @FXML
    private TableColumn<Note, SVGPath> icon;
    @FXML
    private TableColumn<Note, String> name;
    @FXML
    private TableColumn<Note, String> group;
    @FXML
    private TableColumn<Note, String> owner;
    @FXML
    private TableColumn<Note, String> category;
    @FXML
    private TableColumn<Note, String> createTime;
    @FXML
    private Label nameLabel;

    SVGPath svgPath = new SVGPath();



    private final ObservableList<Note> notes = FXCollections.observableArrayList(
            new Note("Illustration packs", "Product needs", "Kurnia Majid", "Hobby", "Apr 10, 2022"),
            new Note("Illustration packs", "Product needs", "Kurnia Majid", "Hobby", "Apr 10, 2022")

    );

    public void initialize() {
//        TokenStorage.getIntance();
        table.setItems(notes);
        name.setCellValueFactory(new PropertyValueFactory<Note, String>("name"));
        group.setCellValueFactory(new PropertyValueFactory<Note, String>("group"));
        owner.setCellValueFactory(new PropertyValueFactory<Note, String>("owner"));
        category.setCellValueFactory(new PropertyValueFactory<Note, String>("category"));
        createTime.setCellValueFactory(new PropertyValueFactory<Note, String>("createTime"));
        updateNameWhenLogIn();
    }

    private void updateNameWhenLogIn() {
        try {
            String username = TokenStorage.getUser();
            String token = TokenStorage.getToken();
            nameLabel.setText("Welcome " + username);
            // if username exist , get the notes list

            // get note list

            System.out.println("welcome username " + username + ", token: "+token);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Go to another page
     */
    private Stage stage;
    private Scene scene;
    private Parent root;
    public void newNoteClicked(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/main_pages/edit_note_page.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void groupsClicked(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/main_pages/groups_page.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
