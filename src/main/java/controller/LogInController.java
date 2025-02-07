package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.ControllerUtils;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Response;
import java.net.URI;
//import java.awt.*;

public class LogInController {

    @FXML
    public Button registerBtn;
    @FXML
    private Button backBtn;
    @FXML
    private TextField regUserInput;
    @FXML
    private PasswordField regPassInput;

    private Stage stage;
    private ControllerUtils controllerUtil = new ControllerUtils();
    Client client;

    Cursor handCursor = Cursor.HAND;
    Cursor defaultCursor = Cursor.DEFAULT;

    @FXML
    private void backBtnClick() {
        System.out.println("back btn is called " + this.backBtn);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/hello_view.fxml"));
        this.stage = this.getStage();
        controllerUtil.updateStage(stage, fxmlLoader);
//        System.out.println(backbtn);

    }

    @FXML
    private void mouseEnter() {
        System.out.println("Mouse enter");
//        System.out.println(this.stage.getClass());
//        Button btn = new Button()
//        switch ()
//        this.backBtn.setCursor(handCursor);

//        this.backBtn.setStyle("-fx-background-color: ");
        this.controllerUtil.setHandcursor(this.backBtn);
        this.controllerUtil.setHandcursor(this.registerBtn);
    }

    @FXML
    private void mouseExit() {
        System.out.println("Mouse exit");
//        this.backBtn.setCursor(defaultCursor);
        this.controllerUtil.setDefaultCursor(this.backBtn);
        this.controllerUtil.setDefaultCursor(this.registerBtn);
    }

    @FXML
    private void registerBtnClick() {
        System.out.println("register button click");
        String userName = regUserInput.getText();
        String password = regPassInput.getText();
        System.out.println("Name: " + userName + " - password: " + password);

    }

    private Stage getStage() {
        if (this.stage == null) {
            this.stage = (Stage) backBtn.getScene().getWindow();
        }
        return this.stage;
    }

    private void register(String username, String password) {


    }

}
