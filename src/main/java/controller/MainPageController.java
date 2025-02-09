package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import model.Note;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static utils.MainPageServices.findAllMyNotes;
import static utils.MainPageServices.updateTime;

public class MainPageController {

    // Label of time
    @FXML
    private Label localTime;

    // Note table
    @FXML
    private TableView<Note> table;
    @FXML
    private TableColumn<Note, SVGPath> icon;
    @FXML
    private TableColumn<Note, String> title;
    @FXML
    private TableColumn<Note, String> group;
    @FXML
    private TableColumn<Note, String> owner;
    @FXML
    private TableColumn<Note, String> category;
    @FXML
    private TableColumn<Note, String> createTime;

    SVGPath svgPath = new SVGPath();

    /*
    private final ObservableList<Note> notes = FXCollections.observableArrayList(
            new Note("Illustration packs", "Lorem ipsum dorime", "#FFD700", "Product needs", "Kurnia Majid", "Hobby", "Apr 10, 2022"),
            new Note("Illustration packs", "Lorem ipsum dorime", "#FFD700", "Product needs", "Kurnia Majid", "Hobby", "Apr 10, 2022")
    );

     */


    public void initialize() {
        ObservableList<Note> notes = FXCollections.observableArrayList();
        ArrayList<Note> noteArrayList = findAllMyNotes("http://localhost:8093/api/note/", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTczOTExMTcxMiwiZXhwIjoxNzM5MTk4MTEyfQ.uwVoxBRW6KkYE9avu-MZvsBM9JvOVyFhheraCapGIgk");

        assert noteArrayList != null;
        notes.addAll(noteArrayList);

        table.setItems(notes);
        title.setCellValueFactory(new PropertyValueFactory<Note, String>("title"));
        group.setCellValueFactory(new PropertyValueFactory<Note, String>("group"));
        owner.setCellValueFactory(new PropertyValueFactory<Note, String>("owner"));
        category.setCellValueFactory(new PropertyValueFactory<Note, String>("category"));
        createTime.setCellValueFactory(new PropertyValueFactory<Note, String>("createTime"));

        updateTime(localTime);
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
