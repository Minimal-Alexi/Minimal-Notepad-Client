package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.util.Collection;

public class EditNoteController{

    @FXML
    private VBox textVBox;

    public void addTextArea(MouseEvent mouseEvent) {
        textVBox.setSpacing(10);
        TextArea textArea = new TextArea();
        textVBox.getChildren().add(textArea);
    }



    public void saveNoteClicked(ActionEvent event) {

    }
}
