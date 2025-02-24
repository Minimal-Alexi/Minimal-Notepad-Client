package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.TokenStorage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import utils.ControllerUtils;

import java.io.IOException;
import java.util.Objects;

public class HelloController {
    private static final Log log = LogFactory.getLog(HelloController.class);
    @FXML
    private Label logIn;

    @FXML
    private Label registerLabel;

    @FXML
    private ImageView imageView;

    @FXML
    private VBox loginVBox;

    private Stage stage;
    private ControllerUtils controllerUtil;


    @FXML
    protected void loginClicked() {
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

    @FXML
    public void mouseEnter() {
        registerLabel.setTextFill(Color.GRAY);
        this.controllerUtil.setHandCursor(registerLabel);
    }

    @FXML
    public void mouseExit() {
        registerLabel.setTextFill(Color.BLACK);
        this.controllerUtil.setDefaultCursor(registerLabel);
    }

    @FXML
    public void registerLabelClick() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/register_view.fxml"));
        this.stage = this.getStage();
        this.controllerUtil.updateStage(stage, fxmlLoader);
    }


    public void initialize() {
        TokenStorage.getIntance();
        this.controllerUtil = new ControllerUtils();

        /*
        Set window size UI adaptation
         */
        Platform.runLater(() -> {
            imageView.getScene().getWindow().widthProperty().addListener((observable, oldValueWidth, newValueWidth) -> {
                imageView.setFitWidth((Double) newValueWidth);

                if ((Double) newValueWidth > 700.0) {
                    AnchorPane.setBottomAnchor(loginVBox, 0.0);
                    AnchorPane.setRightAnchor(loginVBox, 0.0);
                } else {
                    AnchorPane.setBottomAnchor(loginVBox, -80.0);
                    AnchorPane.setLeftAnchor(loginVBox, 0.0);
                }
            });
        });
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