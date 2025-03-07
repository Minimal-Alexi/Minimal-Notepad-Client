package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import model.Note;
import model.TokenStorage;
import utils.NoteServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static utils.MainPageServices.*;
import static utils.NoteServices.*;


public class CreateNoteController {

    @FXML
    private Label localTime;
    @FXML
    private Label nameLabel;
    @FXML
    private VBox noteBackground;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private VBox textVBox;
    @FXML
    private TextField titleTextArea;
    @FXML
    private TextArea textArea1;
    @FXML
    private Button saveNoteBtn;
    @FXML
    private HBox categoryHBox;
    @FXML
    private Label addCategory;
    @FXML
    private Button uploadPicBtn;

    private final HashMap<Integer, String> categoryList = new HashMap<>();
    private final ArrayList<String> figureList = new ArrayList<>();


    public void initialize() {
        updateLocalTime(localTime);
        updateNameLabel(nameLabel, TokenStorage.getUser());
        colorSetUp();
    }

    public void saveNoteClicked(ActionEvent event) throws IOException {
        //Disable the button
        saveNoteBtn.setDisable(true);
        Note note = new Note(0, titleTextArea.getText(), textArea1.getText(), colorPicker.getValue().toString(), "N/A", "N/A", TokenStorage.getUser(),-1 ,"N/A", categoryList, figureList);
        NoteServices.createNote("http://localhost:8093/api/note/", note, TokenStorage.getToken());
        goToPage(stage, scene, event, "/fxml/main_pages/main_page.fxml");
    }

    public void uploadPicClicked(MouseEvent mouseEvent) throws IOException {
        uploadPicture(uploadPicBtn, figureList, textVBox);
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
    Click to add a Category
     */
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

     */
    private void colorSetUp() {
        noteBackground.setStyle("-fx-background-color:" + Color.WHITE + ";");

        colorPicker.setOnAction(event -> {
            Color selectedColor = colorPicker.getValue();
            // Transform color into rgba code
            String hexColor = String.format("#%02X%02X%02X",
                    (int) (selectedColor.getRed() * 255),
                    (int) (selectedColor.getGreen() * 255),
                    (int) (selectedColor.getBlue() * 255));

            // Implement new css to make the radius work
            noteBackground.setStyle("-fx-background-color: " + hexColor + ";");
        });
    }
}
