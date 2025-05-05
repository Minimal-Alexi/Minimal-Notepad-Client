package controller.groups;

import controller.PageController;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Note;
import model.ObservableResourceFactory;
import model.TokenStorage;
import model.selected.SelectedNote;
import model.selected.SelectedReadOnlyNote;
import utils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static utils.MainPageServices.*;

public class MyGroupsNotesController extends PageController {

    @FXML private Label localTime, nameLabel, myGroupNotesLabel;
    @FXML private TableView<Note> table;
    @FXML private TableColumn<Note, Void> icon, actionCol;
    @FXML private TableColumn<Note, String> title, group, owner, category, createTime;
    @FXML private Button myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn;

    private final ObservableList<Note> notes = FXCollections.observableArrayList();
    private ObservableResourceFactory resourceFactory;
    private ControllerUtils controllerUtils;
    private HttpResponseService responseService;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void initialize() {
        controllerUtils = new ControllerUtils();
        responseService = new HttpResponseServiceImpl();
        resourceFactory = ObservableResourceFactory.getInstance();

        initializeNoteData();
        initializeTable();
        bindUIComponents();

        updateLocalTime(localTime);
        updateNameLabel(nameLabel, TokenStorage.getUser());

        setSidebarLanguages(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn);

        Platform.runLater(super::updateDisplay);
    }

    private void initializeNoteData() {
        ArrayList<Note> fetchedNotes = findAllMyNotes("http://localhost:8093/api/note/my-groups", TokenStorage.getToken());
        if (fetchedNotes != null) {
            notes.setAll(fetchedNotes);
        } else {
            System.err.println("Failed to fetch notes.");
        }
    }

    private void initializeTable() {
        table.setItems(notes);
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        group.setCellValueFactory(new PropertyValueFactory<>("group"));
        owner.setCellValueFactory(new PropertyValueFactory<>("owner"));
        category.setCellValueFactory(cell -> {
            HashMap<Integer, String> catMap = cell.getValue().getCategory();
            return new ReadOnlyStringWrapper(catMap != null ? String.join(", ", catMap.values()) : "");
        });
        createTime.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        setupIconColumn();
        setupActionColumn("Read");
    }

    private void setupIconColumn() {
        icon.setCellFactory(param -> new TableCell<>() {
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
                setGraphic(empty ? null : imageView);
            }
        });
    }

    private void setupActionColumn(String buttonLabel) {
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button actionButton = new Button(buttonLabel);

            {
                actionButton.setOnAction(event -> {
                    Note selectedNote = getTableView().getItems().get(getIndex());
                    handleAction(selectedNote, buttonLabel);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actionButton);
                ViewUtils.addStyle(actionButton, "/button.css");
            }
        });
    }

    private void handleAction(Note note, String action) {
        switch (action) {
            case "Read" -> {
                SelectedReadOnlyNote selectedReadOnlyNote = SelectedReadOnlyNote.getInstance();
                selectedReadOnlyNote.setId(note.getId());

                ControllerUtils c = new ControllerUtils();
                c.goPage((Stage) table.getScene().getWindow(), table, "/fxml/main_pages/readonly_note_page.fxml");
            }
            case "Delete" -> System.out.println("Delete note: " + note.getId());
            case "Join" -> System.out.println("Join note: " + note.getId());
            default -> throw new UnsupportedOperationException("Action not implemented: " + action);
        }
    }

    @FXML
    private void tableClicked(MouseEvent event) throws IOException {
        if (event.getClickCount() == 1 && table.getSelectionModel().getSelectedItem() != null) {
            int noteId = table.getSelectionModel().getSelectedItem().getId();

            SelectedNote.getInstance().setId(noteId);
            goToPage(event, "/fxml/main_pages/edit_note_page.fxml");
        }
    }

    // Sidebar Navigation
    @FXML private void myGroupsBtnClick() {
        ControllerUtils_v2.goToMyGroupsPage(stage, myGroupsBtn);
    }

    @FXML private void myNotesBtnClick() {
        ControllerUtils_v2.goToMyNotesPage(stage, myNotesBtn);
    }

    @FXML private void shareNotesBtnClick() {
        ControllerUtils_v2.goToMyGroupNotesPage(stage, shareNotesBtn);
    }

    @FXML private void allGroupsBtnClick() {
        ControllerUtils_v2.goToAllGroupsPage(stage, allGroupsBtn);
    }

    @FXML private void accountBtnClick() {
        ControllerUtils_v2.goToAccountPage(stage, accountBtn);
    }

    @FXML private void logOutBtnClick() {
        controllerUtils.logout(stage, logOutBtn);
    }

    // Cursor style handlers
    @FXML private void mouseEnter() {
        setCursorStyle(true);
    }

    @FXML private void mouseExit() {
        setCursorStyle(false);
    }

    private void setCursorStyle(boolean isHand) {
        Button[] buttons = { myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn };
        for (Button btn : buttons) {
            if (isHand) {
                controllerUtils.setHandCursor(btn);
            } else {
                controllerUtils.setDefaultCursor(btn);
            }
        }
    }

    @Override
    public void bindUIComponents() {
        myGroupNotesLabel.textProperty().bind(resourceFactory.getStringBinding("myGroupNotesLabel"));
        title.textProperty().bind(resourceFactory.getStringBinding("myGroupNotesTitleCol"));
        group.textProperty().bind(resourceFactory.getStringBinding("myGroupNotesGroupCol"));
        owner.textProperty().bind(resourceFactory.getStringBinding("myGroupNotesOwnerCol"));
        category.textProperty().bind(resourceFactory.getStringBinding("myGroupNotesCategoryCol"));
        createTime.textProperty().bind(resourceFactory.getStringBinding("myGroupNotesCreateTimeCol"));
    }
}
