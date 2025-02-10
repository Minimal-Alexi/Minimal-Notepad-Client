package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HelloController {
    @FXML
    private Label logIn;

    private Stage stage;

    @FXML
    protected void loginClicked(){
        System.out.println("addButtonClicked() called");
        updateStage();
    }

    @FXML
    public void loginMouseEnter() {
        logIn.setTextFill(Color.GRAY);
    }

    @FXML
    public void loginMouseExit() {
        logIn.setTextFill(Color.BLACK);
    }


    private Stage getStage() {
        if (stage == null) {
            stage = (Stage) logIn.getScene().getWindow();
        }
        return stage;
    }

    private void updateStage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/logIn_view.fxml"));
            Parent root = fxmlLoader.load();

            var stage = getStage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}