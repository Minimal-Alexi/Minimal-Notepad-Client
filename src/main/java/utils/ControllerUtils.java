package utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.TokenStorage;
import org.json.JSONArray;
import org.json.JSONObject;

//import java.awt.*;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ControllerUtils {

    Cursor handCursor = Cursor.HAND;
    Cursor defaultCursor = Cursor.DEFAULT;

    public void updateStage(Stage stage, FXMLLoader fxmlLoader) {
        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/logIn_view.fxml"));
            Parent root = fxmlLoader.load();

//            var stage = getStage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Stage getStage(Button btn, Stage stage) {
        if (stage == null) {
            stage = (Stage) btn.getScene().getWindow();
        }
        return stage;
    }

    public void goPage(Stage stage, Button btn, String fxmlPage) {
        stage = getStage(btn, stage);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPage));
        updateStage(stage, fxmlLoader);
    }


    public void setDefaultCursor(Button btn) {
        btn.setCursor(defaultCursor);
    }

    public void setDefaultCursor(Text txt) {
        txt.setCursor(defaultCursor);
    }

    public void setDefaultCursor(Label label) {
        label.setCursor(defaultCursor);
    }

    public void setDefaultCursor(SVGPath svgPath) {
        svgPath.setCursor(defaultCursor);
    }

    public void setDefaultCursor(Pane pane) {
        pane.setCursor(defaultCursor);
    }


    public void setHandCursor(Button btn) {
        btn.setCursor(handCursor);
    }

    public void setHandCursor(Text txt) {
        txt.setCursor(handCursor);
    }

    public void setHandCursor(Label label) {
        label.setCursor(handCursor);
    }

    public void setHandCursor(SVGPath svgPath) {
        svgPath.setCursor(handCursor);
    }

    public void setHandCursor(Pane pane) {
        pane.setCursor(handCursor);
    }

    public boolean validEmail(String email) {
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void goToHelloPage(Stage stage, Button btn) {
        String helloPage = "/fxml/hello_view.fxml";
        TokenStorage.clearToken();
        goPage(stage, btn, helloPage);
    }

    public boolean isInputEmpty(String input){
        return input.trim().equals("");
    }

    public JSONObject toJSonObject(Object response){
        if (response instanceof JSONObject){
            return (JSONObject) response;
        }
        return null;
    }

    public JSONArray toJSONArray(Object response){
        if(response instanceof JSONArray){
            return (JSONArray) response;
        }
        return null;
    }
}
