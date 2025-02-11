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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Note;
import model.User;
import model.selected.SelectedNote;
import model.TokenStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static utils.MainPageServices.*;

public class MainPageController {

    @FXML private Label localTime;
    @FXML private Label nameLabel;
    // Note table
    @FXML private TableView<Note> table;
    @FXML private TableColumn<Note, Void> icon;
    @FXML private TableColumn<Note, String> title;
    @FXML private TableColumn<Note, String> group;
    @FXML private TableColumn<Note, String> owner;
    @FXML private TableColumn<Note, String> category;
    @FXML private TableColumn<Note, String> createTime;
    User user = User.getInstance();


    public void initialize() {
        ObservableList<Note> notes = FXCollections.observableArrayList();

        /*
        The user info is hardcoded for now
         */
        user.setToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTczOTIyNzM1MywiZXhwIjoxNzM5MzEzNzUzfQ.NneKiDnTXTEZIpVV1w740aaPCbPnhPOHkdb4ZuxRGmM");
        user.setId(5);
        updateNameWhenLogIn();

        ArrayList<Note> noteArrayList = findAllMyNotes("http://localhost:8093/api/note/", user.getToken());

        if (noteArrayList != null) {
            notes.addAll(noteArrayList);
        } else {
            System.out.println("Connection failed");
        }

        table.setItems(notes);
        title.setCellValueFactory(new PropertyValueFactory<Note, String>("title"));
        group.setCellValueFactory(new PropertyValueFactory<Note, String>("group"));
        owner.setCellValueFactory(new PropertyValueFactory<Note, String>("owner"));
        category.setCellValueFactory(new PropertyValueFactory<Note, String>("category"));
        createTime.setCellValueFactory(new PropertyValueFactory<Note, String>("createTime"));
        icon.setCellFactory(param -> new TableCell<Note, Void>() {
            private final ImageView imageView = new ImageView(
                    new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/icon/FileText.png")))
            );
            {
                imageView.setFitWidth(20);
                imageView.setFitHeight(20);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(imageView);
                }
            }
        });
        updateTime(localTime);
    }

    private void updateNameWhenLogIn() {
        try {
            String username = user.getUsername();
            String token = user.getToken();
            nameLabel.setText("Welcome " + username);
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

        goToPage(stage, scene, event, "/fxml/main_pages/edit_note_page.fxml");
    }

    public void newNoteClicked(ActionEvent event) throws IOException {
        goToPage(stage, scene, event, "/fxml/main_pages/create_note_page.fxml");
    }

    public void groupsClicked(ActionEvent event) throws IOException {
        goToPage(stage, scene, event, "/fxml/main_pages/groups_page.fxml");
    }


}
