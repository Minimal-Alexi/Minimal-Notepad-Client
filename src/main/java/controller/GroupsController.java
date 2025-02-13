package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.TokenStorage;

import java.io.IOException;
import java.util.Objects;

import static utils.MainPageServices.updateLocalTime;
import static utils.MainPageServices.updateNameLabel;

public class GroupsController {

    @FXML
    private Label localTime;
    @FXML private Label nameLabel;

    public void initialize() {
        updateLocalTime(localTime);
        updateNameLabel(nameLabel, TokenStorage.getUser());
    }

    public void createGroupClicked(ActionEvent event) {

    }

    /*
    Go to another page
     */
    private Stage stage;
    private Scene scene;
    private Parent root;
    public void myFilesClicked(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/main_pages/main_page.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
