package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;


public class EditNoteController {

    @FXML
    private VBox textVBox;

    @FXML
    private TextArea textArea1;

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

    }

    /*
    Below is the tool methods:
     */

}
