package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Note;
import model.selected.SelectedNote;
import model.TokenStorage;
import utils.NoteServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


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
    // Recently edited
    @FXML private HBox recentlyEditedHBox;


    public void initialize() {
        ObservableList<Note> notes = FXCollections.observableArrayList();
        ArrayList<Note> noteArrayList = findAllMyNotes("http://localhost:8093/api/note/", TokenStorage.getToken());
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
        createTime.setCellValueFactory(new PropertyValueFactory<Note, String>("createdAt"));
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

        assert noteArrayList != null;
        updateRecentlyEdited(recentlyEditedHBox, noteArrayList);
        updateLocalTime(localTime);
        updateNameLabel(nameLabel, TokenStorage.getUser());
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

    public void newNoteClicked(ActionEvent event) throws IOException {
        goToPage(stage, scene, event, "/fxml/main_pages/create_note_page.fxml");
    }

    public void groupsClicked(ActionEvent event) throws IOException {
        goToPage(stage, scene, event, "/fxml/main_pages/groups_page.fxml");
    }

    @FXML private ComboBox<String> categoryFilter;

    private void initializeCategoryFilter() {
        Set<String> categories = table.getItems().stream()
                .map(Note::getCategory) // Returns HashMap<Integer, String>
                .filter(Objects::nonNull) // Ensure it's not null
                .flatMap(map -> map.values().stream()) // Extract String values from HashMap
                .filter(category -> !category.isEmpty()) // Remove empty values
                .collect(Collectors.toSet()); // Collect as Set<String>


        ObservableList<String> categoryList = FXCollections.observableArrayList(categories);
        categoryFilter.setItems(categoryList);

        categoryFilter.getItems().add(0, "All");
        categoryFilter.setValue("All");
    }

    private void handleCategoryFilter() {
        String selectedCategory = categoryFilter.getValue();

        if (selectedCategory == null || selectedCategory.equals("All")) {
            // Reset to show all notes
            ArrayList<Note> allNotes = findAllMyNotes("http://localhost:8093/api/note/",
                    TokenStorage.getToken());
            table.setItems(FXCollections.observableArrayList(allNotes));
            return;
        }

        // Get current notes and filter
        List<Note> filteredNotes = NoteServices.filterNotes(
                "http://localhost:8093/api/note/",
                new ArrayList<>(table.getItems()),
                selectedCategory,
                TokenStorage.getToken()
        );

        // Update table with filtered notes
        table.setItems(FXCollections.observableArrayList(filteredNotes));
    }

}
