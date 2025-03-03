package controller.groups;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import model.Note;

public class MyGroupsNotesController {

    @FXML private TableView<Note> table;
    @FXML private TableColumn<Note, Void> icon;
    @FXML private TableColumn<Note, String> title;
    @FXML private TableColumn<Note, String> group;
    @FXML private TableColumn<Note, String> owner;
    @FXML private TableColumn<Note, String> category;
    @FXML private TableColumn<Note, String> createTime;

    private ObservableList<Note> notes = FXCollections.observableArrayList();

    public void initialize() {



    }


    public void tableClicked(MouseEvent mouseEvent) {

    }

    public void groupsBtnClick(MouseEvent mouseEvent) {

    }

    public void accountBtnClick(MouseEvent mouseEvent) {

    }

    public void logOutBtnClick(MouseEvent mouseEvent) {

    }
}
