package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Note;

public class MainPageController {
    @FXML
    private TableView<Note> table;
    @FXML
    private TableColumn<Note, String> name;
    @FXML
    private TableColumn<Note, String> group;
    @FXML
    private TableColumn<Note, String> owner;
    @FXML
    private TableColumn<Note, String> category;
    @FXML
    private TableColumn<Note, String> createTime;

    private final ObservableList<Note> notes = FXCollections.observableArrayList(
            new Note("Illustration packs", "Product needs", "Kurnia Majid", "Hobby", "Apr 10, 2022"),
            new Note("Illustration packs", "Product needs", "Kurnia Majid", "Hobby", "Apr 10, 2022")

    );


    public void initialize() {
        table.setItems(notes);
        name.setCellValueFactory(new PropertyValueFactory<Note, String>("name"));
        group.setCellValueFactory(new PropertyValueFactory<Note, String>("group"));
        owner.setCellValueFactory(new PropertyValueFactory<Note, String>("owner"));
        category.setCellValueFactory(new PropertyValueFactory<Note, String>("category"));
        createTime.setCellValueFactory(new PropertyValueFactory<Note, String>("createTime"));
    }

}
