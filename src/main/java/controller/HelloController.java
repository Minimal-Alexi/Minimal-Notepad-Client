package controller;

import controller.PageController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.LanguageLabel;
import model.ObservableResourceFactory;
import model.TokenStorage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import utils.ControllerUtils;
import utils.ControllerUtils_v2;
import utils.Utils;

import java.io.IOException;
import java.util.Objects;

public class HelloController extends PageController {
    private static final Log log = LogFactory.getLog(HelloController.class);
    @FXML
    private Label logIn;

    @FXML
    private Label registerLabel;
    @FXML
    private Label accountExistCheckLabel;

    @FXML
    private ImageView imageView;

    @FXML
    private VBox loginVBox;

    private Stage stage;
    private ControllerUtils controllerUtil;

    @FXML private
    ComboBox<LanguageLabel> languageBox;
    private ObservableResourceFactory RESOURCE_FACTORY ;
    private final LanguageLabel[] supportedLanguages = new LanguageLabel[4];

    @FXML
    protected void loginClicked() {
        System.out.println("login button click");
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
        String registerLink = "/fxml/register_view.fxml";
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/register_view.fxml"));
//        this.stage = this.getStage();
//        this.controllerUtil.updateStage(stage, fxmlLoader);
        ControllerUtils_v2.goPage(stage,registerLabel,registerLink);
    }


    public void initialize() {
        TokenStorage.getIntance();
        this.controllerUtil = new ControllerUtils();

        RESOURCE_FACTORY = ObservableResourceFactory.getInstance();

        RESOURCE_FACTORY.getResources();
//        setupLanguageBox();
        Platform.runLater(()->{
            Utils.setupLanguageBox(
                    languageBox,
                    supportedLanguages,
                    RESOURCE_FACTORY,
                    this
            );
            super.updateDisplay();
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
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void updateAllUIComponents() {

    }
    @Override
    public void bindUIComponents() {
        logIn.textProperty().bind(RESOURCE_FACTORY.getStringBinding("logInNowLabel"));
        registerLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("registerHereLabel"));
        accountExistCheckLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("doyouHaveLabel"));
    }
}