package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import model.Note;
import model.TokenStorage;
import model.selected.SelectedNote;
import utils.NoteServices;

import java.io.IOException;
import java.util.HashMap;

import static utils.MainPageServices.*;
import static utils.NoteServices.*;


public class EditNoteController {

    @FXML private Label localTime;
    @FXML private Label nameLabel;
    @FXML private VBox textVBox;
    @FXML private TextField titleTextArea;
    @FXML private TextArea textArea1;
    @FXML private Button saveNoteBtn;
    @FXML private Button deleteNoteBtn;
    @FXML private HBox categoryHBox;
    @FXML private Label addCategory;
    @FXML private ColorPicker colorPicker;
    @FXML private Rectangle noteBackground;

    SelectedNote selectedNote = SelectedNote.getInstance();
    private HashMap<Integer, String> categoryList = new HashMap<>();

    // Initialize
    public void initialize() {

        System.out.println(selectedNote.getId());

        Note note = findNoteById("http://localhost:8093/api/note/", selectedNote.getId(), TokenStorage.getToken());

        assert note != null;
        textArea1.setText(note.getText());
        titleTextArea.setText(note.getTitle());
        categoryList = note.getCategory();

        colorSetUp(note.getColor());

        // query the categoryList to add categories to the ui
        updateCategory(categoryList, categoryHBox);

        updateLocalTime(localTime);
        updateNameLabel(nameLabel, TokenStorage.getUser());
    }

    public void saveNoteClicked(ActionEvent event) throws IOException {
        //Disable the button
        saveNoteBtn.setDisable(true);
        Note note = new Note(0, titleTextArea.getText(), textArea1.getText(), colorPicker.getValue().toString(), "N/A", "N/A", TokenStorage.getUser(), "N/A", categoryList);
        NoteServices.deleteNoteById("http://localhost:8093/api/note/", selectedNote.getId(), TokenStorage.getToken());
        NoteServices.createNote("http://localhost:8093/api/note/", note, TokenStorage.getToken());
        goToPage(stage, scene, event, "/fxml/main_pages/main_page.fxml");
    }

    public void deleteNoteClicked(ActionEvent event) throws IOException {
        //Disable the button
        deleteNoteBtn.setDisable(true);
        NoteServices.deleteNoteById("http://localhost:8093/api/note/", selectedNote.getId(), TokenStorage.getToken());
        goToPage(stage, scene, event, "/fxml/main_pages/main_page.fxml");
    }

    public void addCategoryClicked(MouseEvent mouseEvent) {
        addCategory.setDisable(true);

        // Create a context menu of categories for the user to choose
        HashMap<Integer, String> categories = getAllCategories("http://localhost:8093/api/categories", TokenStorage.getToken());
        ContextMenu contextMenu = new ContextMenu();

        assert categories != null;
        addCategory(categories, categoryList, categoryHBox, contextMenu);

        if (!contextMenu.isShowing()) {
            contextMenu.show(addCategory, mouseEvent.getScreenX(), mouseEvent.getScreenY());
        } else {
            contextMenu.hide();
        }

        // The adding behavior is over, enable the add button
        addCategory.setDisable(false);

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
    private void colorSetUp(String initialColor){
        noteBackground.setFill(Color.web(initialColor));
        colorPicker.setValue(Color.web(initialColor));
        colorPicker.setOnAction(event -> {
            noteBackground.setFill(colorPicker.getValue());
        });
    }



}
