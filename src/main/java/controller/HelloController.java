package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import view.LogInView;

import java.io.IOException;

public class HelloController {
    @FXML
    private Label logIn;

    @FXML
    protected void loginClicked() throws IOException {
        Stage stage = (Stage) logIn.getScene().getWindow();
        Parent root = LogInView.loadLogInView();

        stage.setScene(root.getScene());

    }

    @FXML
    public void loginMouseEnter() {
        logIn.setTextFill(Color.GRAY);
    }

    @FXML
    public void loginMouseExit() {
        logIn.setTextFill(Color.BLACK);
    }
}