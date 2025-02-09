package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import utils.ControllerUtils;


public class RegisterController {
    @FXML
    private BorderPane registerPage;

    @FXML
    private Text errGeneral;
    @FXML
    private Text errEmail;
    @FXML
    private Text errUser;
    @FXML
    private Text errPwd;
    @FXML
    private Text errConfirmPwd;
    @FXML
    private Text loginLabel;

    @FXML
    private TextField emailInput;
    @FXML
    private TextField userInput;
    @FXML
    private PasswordField pwdInput;
    @FXML
    private PasswordField confirmPwdInput;

    @FXML
    private Button registerBtn;
    @FXML
    private Button backBtn;


    private Stage stage;
    private ControllerUtils controllerUtil = new ControllerUtils();

    @FXML
    private void registerPagePress(){
    }

    @FXML
    private void registerBtnClick(){

    }

    @FXML
    private void mouseEnter(){

    }

    @FXML
    private void mouseExit(){

    }

    @FXML
    private void loginLabelClick(){

    }

    @FXML
    private void backBtnClick(){

    }


    private Stage getStage() {
        if (this.stage == null) {
            this.stage = (Stage) backBtn.getScene().getWindow();
        }
        return this.stage;
    }

}
