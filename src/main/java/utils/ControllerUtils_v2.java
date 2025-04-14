package utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.TokenStorage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ControllerUtils_v2 {


    private static final String CSS_SOURCE = "/CSS";

    final static Cursor handCursor = Cursor.HAND;
    final static Cursor defaultCursor = Cursor.DEFAULT;

    public static void goPage(Stage stage, Button btn, String fxmlPage) {
        stage = getStage(btn, stage);
        FXMLLoader fxmlLoader = new FXMLLoader(ControllerUtils_v2.class.getResource(fxmlPage));
        updateStage(stage, fxmlLoader);
    }

    public static void goPage(Stage stage, Label label, String fxmlPage) {
        stage = getStage(label, stage);
        FXMLLoader fxmlLoader = new FXMLLoader(ControllerUtils_v2.class.getResource(fxmlPage));
        updateStage(stage, fxmlLoader);
    }


    public static void updateStage(Stage stage, FXMLLoader fxmlLoader) {
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

    public static Stage getStage(Button btn, Stage stage) {
        if (stage == null) {
            stage = (Stage) btn.getScene().getWindow();
        }
        return stage;
    }

    public static Stage getStage(Label label, Stage stage) {
        if (stage == null) {
            stage = (Stage) label.getScene().getWindow();
        }
        return stage;
    }


    public static void setDefaultCursor(Button btn) {
        btn.setCursor(defaultCursor);
    }

    public static void setDefaultCursor(Text txt) {
        txt.setCursor(defaultCursor);
    }

    public static void setDefaultCursor(Label label) {
        label.setCursor(defaultCursor);
    }

    public static void setDefaultCursor(SVGPath svgPath) {
        svgPath.setCursor(defaultCursor);
    }

    public static void setDefaultCursor(Pane pane) {
        pane.setCursor(defaultCursor);
    }


    public static void setHandCursor(Button btn) {
        btn.setCursor(handCursor);
    }

    public static void setHandCursor(Text txt) {
        txt.setCursor(handCursor);
    }

    public static void setHandCursor(Label label) {
        label.setCursor(handCursor);
    }

    public static void setHandCursor(SVGPath svgPath) {
        svgPath.setCursor(handCursor);
    }

    public static void setHandCursor(Pane pane) {
        pane.setCursor(handCursor);
    }

    public boolean validEmail(String email) {
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    public static boolean isInputEmpty(String input) {
        return input.trim().equals("");
    }

    public static JSONObject toJSonObject(Object response) {
        if (response instanceof JSONObject) {
            return (JSONObject) response;
        }
        return null;
    }

    public static JSONArray toJSONArray(Object response) {
        if (response instanceof JSONArray) {
            return (JSONArray) response;
        }
        return null;
    }

    public static void setDefaultAndHandCursorBehaviour(Button button) {
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, (e -> setHandCursor((Button) e.getSource())));
        button.addEventHandler(MouseEvent.MOUSE_EXITED, (e -> setDefaultCursor((Button) e.getSource())));
    }

//    public static void goToMainPage(Stage stage, Button btn) {
//        String mainPageLink = "/fxml/main_pages/main_page.fxml";
//        goPage(stage, btn, mainPageLink);
//    }

    public static void goToMyNotesPage(Stage stage, Button btn){
        String mainPage = "/fxml/main_pages/main_page.fxml";
        goPage(stage,btn,mainPage);

    }

    public static void goToMyGroupNotesPage(Stage stage, Button btn){
        String myGroupsNotesPage = "/fxml/main_pages/groups/my_groups_notes.fxml";
        goPage(stage,btn,myGroupsNotesPage);

    }
    public static void goToMyGroupsPage(Stage stage, Button btn){
        String myGroupPage = "/fxml/main_pages/groups/my_groups.fxml";
        goPage(stage,btn,myGroupPage);
    }

    public static void goToAllGroupsPage(Stage stage, Button btn){
        String allGroupsPage = "/fxml/main_pages/groups/all_groups.fxml";
        goPage(stage, btn,allGroupsPage);
    }

    public static void goToAccountPage(Stage stage, Button btn){
        String accountPage = "/fxml/main_pages/account_user_info_page.fxml";
        goPage(stage, btn, accountPage);
    }

    public static void logout(Stage stage, Button btn) {
        String helloPage = "/fxml/hello_view.fxml";
        TokenStorage.clearToken();
        goPage(stage, btn, helloPage);
    }

    public static void addStyle(Button btn, String styleLink){
        btn.getStylesheets().add(btn.getClass().getResource(CSS_SOURCE + styleLink).toExternalForm());
    }

    public static void addStyle(BorderPane root, String styleLink){
        root.getStylesheets().add(root.getClass().getResource(CSS_SOURCE + styleLink).toExternalForm());
    }

}
