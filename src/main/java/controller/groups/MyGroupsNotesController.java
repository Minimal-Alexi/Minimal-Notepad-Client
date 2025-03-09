package controller.groups;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Note;
import model.TokenStorage;
import model.selected.SelectedNote;
import utils.ControllerUtils;
import utils.HttpResponseService;
import utils.HttpResponseServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static utils.MainPageServices.*;

public class MyGroupsNotesController {

    @FXML
    private Label localTime;
    @FXML
    private Label nameLabel;
    @FXML private TableView<Note> table;
    @FXML private TableColumn<Note, Void> icon;
    @FXML private TableColumn<Note, String> title;
    @FXML private TableColumn<Note, String> group;
    @FXML private TableColumn<Note, String> owner;
    @FXML private TableColumn<Note, String> category;
    @FXML private TableColumn<Note, String> createTime;

    private HttpResponseService responseService;
    private ControllerUtils controllerUtils;
    private ObservableList<Note> noteObservableList;
    private ArrayList<Note> noteArrayList;
    private HashMap<Integer, String> categoryList;


    private ObservableList<Note> notes = FXCollections.observableArrayList();

    public void initialize() {
        this.controllerUtils = new ControllerUtils();
        this.responseService = new HttpResponseServiceImpl();

        noteObservableList = FXCollections.observableArrayList();
        noteArrayList = findAllMyNotes("http://localhost:8093/api/note/", TokenStorage.getToken());
        if (noteArrayList != null) {
            noteObservableList.addAll(noteArrayList);
        } else {
            System.out.println("Connection failed");
        }

        updateNoteTable(noteObservableList, table, title, group, owner, category, createTime, icon);

        updateLocalTime(localTime);
        updateNameLabel(nameLabel, TokenStorage.getUser());

    }

    /*
Go to another page
 */
    private Stage stage;
    private Scene scene;
    private Parent root;

    public void tableClicked(MouseEvent event) throws IOException {
        int id = 0;
        if (event.getClickCount() == 1) {
            if (table.getSelectionModel().getSelectedItem() != null) {
                id = table.getSelectionModel().getSelectedItem().getId();
                System.out.println("id: " + id);
                System.out.println(table.getSelectionModel().getSelectedItem());

                SelectedNote selectedNote = SelectedNote.getInstance();
                selectedNote.setId(id);
                goToPage(stage, scene, event, "/fxml/main_pages/edit_note_page.fxml");
            }
        }

    }

    public void groupsBtnClick(MouseEvent mouseEvent) {

    }

    public void accountBtnClick(MouseEvent mouseEvent) {

    }

    public void logOutBtnClick(MouseEvent mouseEvent) {

    }
}
