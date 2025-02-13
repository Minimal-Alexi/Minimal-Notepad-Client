package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Note;
import model.TokenStorage;
import utils.NoteServices;

import java.io.IOException;

import static utils.MainPageServices.*;


public class CreateNoteController {

    @FXML private Label localTime;
    @FXML private Label nameLabel;
    @FXML private VBox textVBox;
    @FXML private TextField titleTextArea;
    @FXML private TextArea textArea1;
    @FXML private Button saveNoteBtn;

    public void initialize() {
        updateLocalTime(localTime);
        updateNameLabel(nameLabel, TokenStorage.getUser());

        /*
        Add some new event handlers
         */

        textArea1.setPrefRowCount(1);

        TextArea textArea = createTextArea();
        textVBox.getChildren().add(textArea);
    }



    public void saveNoteClicked(ActionEvent event) throws IOException {
        //Disable the button
        saveNoteBtn.setDisable(true);
        Note note = new Note(0, titleTextArea.getText(), textArea1.getText(), "#FFD700", "N/A", "N/A", TokenStorage.getUser(), "N/A", "null");
        NoteServices.createNote("http://localhost:8093/api/note/", note, TokenStorage.getToken());
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

    // ******************************
    // * new codes are below here *
    // ******************************


    /*
    ctrl+v insert picture
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

    /*
    press ENTER to create a new textarea
     */
    private TextArea createTextArea() {
        TextArea textArea = new TextArea();
        textArea.setWrapText(true);

        // press ENTER to create a new textArea
        textArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().toString().equals("ENTER")) {
                event.consume(); // prevent the default enter warping behavior

                TextArea newTextArea = createTextArea();
                VBox parent = (VBox) textArea.getParent();
                parent.getChildren().add(parent.getChildren().indexOf(textArea) + 1, newTextArea);

                newTextArea.requestFocus(); // move the cursor to the next textArea
            }
        });

        // Set the height of the textArea changing with the text lines
        textArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                Text text = (Text) textArea.lookup(".text");
                textArea.setPrefHeight(text.boundsInParentProperty().get().getMaxY() + 20);
            }
        });

        return textArea;
    }
}
