package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Note;
import model.User;
import model.selected.SelectedNote;
import utils.NoteServices;

import java.io.IOException;

import static utils.MainPageServices.goToPage;
import static utils.NoteServices.findNoteById;


public class EditNoteController {

    @FXML private VBox textVBox;
    @FXML private TextField titleTextArea;
    @FXML private TextArea textArea1;
    @FXML private Button saveNoteBtn;
    @FXML private Button deleteNoteBtn;

    User user = User.getInstance();
    SelectedNote selectedNote = SelectedNote.getInstance();

    // Initialize
    public void initialize() {

        System.out.println(selectedNote.getId());

        Note note = findNoteById("http://localhost:8093/api/note/", selectedNote.getId(), user.getToken());

        assert note != null;
        textArea1.setText(note.getText());
        titleTextArea.setText(note.getTitle());
    }



    public void saveNoteClicked(ActionEvent event) throws IOException {
        //Disable the button
        saveNoteBtn.setDisable(true);
        Note note = new Note(0, "TITLE", textArea1.getText(), "#FFD700", "N/A", "N/A", user.getUsername(), "N/A", "null");
        NoteServices.deleteNoteById("http://localhost:8093/api/note/", selectedNote.getId(), user.getToken());
        NoteServices.createNote("http://localhost:8093/api/note/", note, user.getToken());
        goToPage(stage, scene, event, "/fxml/main_pages/main_page.fxml");
    }

    public void deleteNoteClicked(ActionEvent event) throws IOException {
        //Disable the button
        deleteNoteBtn.setDisable(true);
        NoteServices.deleteNoteById("http://localhost:8093/api/note/", selectedNote.getId(), user.getToken());
        goToPage(stage, scene, event, "/fxml/main_pages/main_page.fxml");
    }

    /*
    Go to another page
     */
    private Stage stage;
    private Scene scene;
    private Parent root;

    public void groupsClicked(ActionEvent event) throws IOException {
        goToPage(stage, scene, event, "/fxml/main_pages/groups_page.fxml");
    }

    /*
    Unfinished functions
     */
    public void textAreaKeyPressed(KeyEvent keyEvent) {
        /*
            When the user press control + v, the app creates a imageView and insert a picture into it.
            Then create a new text area.
        */
        if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.V) {
            // Get the content of the clipboard
            Clipboard clipboard = Clipboard.getSystemClipboard();

            if (clipboard.hasImage()) {
                // Add an image and imageView to the Vbox
                Image image = clipboard.getImage();
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(500);
                imageView.setPreserveRatio(true);

                textVBox.getChildren().add(imageView);

                // Add a new textArea
                TextArea textArea = new TextArea();
                textArea.addEventHandler(KeyEvent.KEY_PRESSED, this::textAreaKeyPressed);
                textVBox.getChildren().add(textArea);
            }
        }
    }


}
