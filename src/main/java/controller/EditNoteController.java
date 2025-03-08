package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import model.Note;
import model.TokenStorage;
import model.selected.SelectedNote;
import utils.ControllerUtils;
import utils.GoogleDriveUploader;
import utils.NoteServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static utils.MainPageServices.*;
import static utils.NoteServices.*;


public class EditNoteController {

    @FXML
    private Label localTime;
    @FXML
    private Label nameLabel;
    @FXML
    private VBox textVBox;
    @FXML
    private TextField titleTextArea;
    @FXML
    private TextArea textArea1;
    @FXML
    private Button saveNoteBtn;
    @FXML
    private Button deleteNoteBtn;
    @FXML
    private HBox categoryHBox;
    @FXML
    private Label addCategory;
    @FXML
    private Button uploadPicBtn;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private Rectangle noteBackground;

    @FXML
    private Button myNotesBtn;
    @FXML
    private Button shareNotesBtn;
    @FXML
    private Button myGroupsBtn;
    @FXML
    private Button allGroupsBtn;
    @FXML
    private Button accountBtn;
    @FXML
    private Button logOutBtn;

    private ControllerUtils controllerUtils;


    SelectedNote selectedNote = SelectedNote.getInstance();
    private HashMap<Integer, String> categoryList = new HashMap<>();
    private ArrayList<String> figureList = new ArrayList<>();

    // Initialize
    public void initialize() {
        this.controllerUtils = new ControllerUtils();


        System.out.println(selectedNote.getId());

        Note note = findNoteById("http://localhost:8093/api/note/", selectedNote.getId(), TokenStorage.getToken());

        assert note != null;
        textArea1.setText(note.getText());
        titleTextArea.setText(note.getTitle());
        categoryList = note.getCategory();
        figureList = note.getFigure();

        colorSetUp(note.getColor());

        // query the categoryList to add categories to the ui
        updateCategory(categoryList, categoryHBox);

        updateLocalTime(localTime);
        updateNameLabel(nameLabel, TokenStorage.getUser());

        // add pictures to the ui
        Platform.runLater(() -> {
            figureList.forEach(figure -> {
                GoogleDriveUploader googleDriveUploader = new GoogleDriveUploader();
                ImageView imageView = new ImageView();
                try {
                    Image image = googleDriveUploader.download(figure);
                    imageView.setImage(image);
                    imageView.setFitHeight(200);
                    imageView.setFitWidth(200);
                    imageView.setPreserveRatio(true);
                    textVBox.getChildren().add(imageView);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }

    public void saveNoteClicked(ActionEvent event) throws IOException {
        //Disable the button
        saveNoteBtn.setDisable(true);
        Note note = new Note(selectedNote.getId(), titleTextArea.getText(), textArea1.getText(), colorPicker.getValue().toString(), "N/A", "N/A", TokenStorage.getUser(), "N/A", categoryList, figureList);
        NoteServices.updateNote("http://localhost:8093/api/note/", selectedNote.getId(), TokenStorage.getToken(), note);
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

    public void uploadPicClicked(MouseEvent mouseEvent) throws IOException {
        uploadPicture(uploadPicBtn, figureList, textVBox);
    }

    /*
    Go to another page
     */
    private Stage stage;
    private Scene scene;
    private Parent root;

//    public void groupsClicked(ActionEvent event) throws IOException {
//        goToPage(stage, scene, event, "/fxml/main_pages/groups_page.fxml");
//    }

    private void colorSetUp(String initialColor) {
        noteBackground.setFill(Color.web(initialColor));
        colorPicker.setValue(Color.web(initialColor));
        colorPicker.setOnAction(event -> {
            noteBackground.setFill(colorPicker.getValue());
        });
    }


    // side bar button

    public void myGroupsBtnClick() {
        this.controllerUtils.goPage(stage, myGroupsBtn, "/fxml/main_pages/groups/my_groups.fxml");
    }

    @FXML
    public void myNotesBtnClick() {
        this.controllerUtils.goPage(stage, myNotesBtn, "/fxml/main_pages/main_page.fxml");
    }

    @FXML
    public void shareNotesBtnClick() {
//        this.controllerUtils.goPage(stage,shareNoteBtn,"");
        System.out.println("Go to share notes page");
    }

    @FXML
    public void allGroupsBtnClick() {
        this.controllerUtils.goPage(stage, allGroupsBtn, "/fxml/main_pages/groups/all_groups.fxml");
    }

    @FXML
    public void accountBtnClick() {
        this.controllerUtils.goPage(stage, accountBtn, "/fxml/main_pages/account_user_info_page.fxml");
    }

    @FXML
    public void logOutBtnClick() {
        this.controllerUtils.logout(stage, logOutBtn);
    }

    @FXML
    void mouseEnter() {
        this.controllerUtils.setHandCursor(myNotesBtn);
        this.controllerUtils.setHandCursor(shareNotesBtn);
        this.controllerUtils.setHandCursor(myGroupsBtn);
        this.controllerUtils.setHandCursor(allGroupsBtn);
        this.controllerUtils.setHandCursor(accountBtn);
        this.controllerUtils.setHandCursor(logOutBtn);
    }

    @FXML
    void mouseExit() {
        this.controllerUtils.setDefaultCursor(myNotesBtn);
        this.controllerUtils.setDefaultCursor(shareNotesBtn);
        this.controllerUtils.setDefaultCursor(myGroupsBtn);
        this.controllerUtils.setDefaultCursor(allGroupsBtn);
        this.controllerUtils.setDefaultCursor(accountBtn);
        this.controllerUtils.setDefaultCursor(logOutBtn);
    }

}
