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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import model.Note;
import model.User;
import model.selected.SelectedNote;
import model.TokenStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static utils.MainPageServices.findAllMyNotes;
import static utils.MainPageServices.updateTime;
import static utils.NoteServices.findNoteById;

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
    @FXML
    private Label nameLabel;

    SVGPath svgPath = new SVGPath();
    User user = User.getInstance();

    public void initialize() {
//        TokenStorage.getIntance();
        ObservableList<Note> notes = FXCollections.observableArrayList();

        /*
        The user info is hardcoded for now
         */
        user.setToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTczOTEyODMwOCwiZXhwIjoxNzM5MjE0NzA4fQ.KzLRbNe1r8EkouVhMMawgEVhxZdPnHibM6X4Zrb6miw");
        user.setId(5);

        ArrayList<Note> noteArrayList = findAllMyNotes("http://localhost:8093/api/note/", user.getToken());

        assert noteArrayList != null;
        notes.addAll(noteArrayList);

        table.setItems(notes);
        title.setCellValueFactory(new PropertyValueFactory<Note, String>("title"));
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

        updateTime(localTime);
    }




    /*
    Go to another page
     */
    private Stage stage;
    private Scene scene;
    private Parent root;

    /*
    Click the content in table
    */
    public void tableClicked(MouseEvent event) throws IOException {
        int id = 0;
        if (event.getClickCount() == 1) {
            id = table.getSelectionModel().getSelectedItem().getId();
            System.out.println("id: " + id);
            System.out.println(table.getSelectionModel().getSelectedItem());
        }

        SelectedNote selectedNote = SelectedNote.getInstance();
        selectedNote.setId(id);

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/main_pages/edit_note_page.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void newNoteClicked(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/main_pages/create_note_page.fxml")));
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
