package org.metropolia.minimalnotepadclient;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class HelloController {
    @FXML
    private Label logIn;

    @FXML
    protected void loginClicked() {
        System.out.println("Login clicked");
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