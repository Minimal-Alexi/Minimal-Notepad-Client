package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Note;
import model.Token;
import model.User;
import utils.NoteServices;

import java.io.IOException;
import java.util.Objects;


public class CreateNoteController {

    @FXML
    private VBox textVBox;

    @FXML
    private TextArea textArea1;

    User user = User.getInstance();

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

    public void saveNoteClicked(ActionEvent event) {
        Note note = new Note("Illustration packs", textArea1.getText(), "#FFD700", "Product needs", user.getId(), "Hobby", "Apr 10, 2022");
        NoteServices.createNote("http://localhost:8093/api/note/", note, user.getToken());
    }


    /*
    Go to another page
     */
    private Stage stage;
    private Scene scene;
    private Parent root;

    public void groupsClicked(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/main_pages/groups_page.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
