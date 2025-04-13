package utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
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

public class ControllerUtils {

    private static final Cursor HAND_CURSOR = Cursor.HAND;
    private static final Cursor DEFAULT_CURSOR = Cursor.DEFAULT;

    /**
     * Load an FXML and set it to a stage.
     */
    public void updateStage(Stage stage, FXMLLoader fxmlLoader) {
        try {
            Parent root = fxmlLoader.load();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML: " + fxmlLoader.getLocation(), e);
        }
    }

    /**
     * Generic method to get the stage from any Node.
     */
    public Stage getStage(Node node, Stage stage) {
        return (stage == null && node != null) ? (Stage) node.getScene().getWindow() : stage;
    }

    /**
     * Navigation helpers.
     */
    public void goPage(Stage stage, Node node, String fxmlPath) {
        stage = getStage(node, stage);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        updateStage(stage, loader);
    }

    /**
     * Sets default cursor on any node.
     */
    public void setDefaultCursor(Node node) {
        if (node != null) node.setCursor(DEFAULT_CURSOR);
    }

    /**
     * Sets hand cursor on any node.
     */
    public void setHandCursor(Node node) {
        if (node != null) node.setCursor(HAND_CURSOR);
    }

    public void setHandCursor(Node... nodes) {
        for (Node node : nodes) {
            setHandCursor(node);
        }
    }

    public void setDefaultCursor(Node... nodes) {
        for (Node node : nodes) {
            setDefaultCursor(node);
        }
    }


    /**
     * Adds default/hand cursor behavior on hover for any Button.
     */
    public void setDefaultAndHandCursorBehaviour(Button button) {
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> setHandCursor(button));
        button.addEventHandler(MouseEvent.MOUSE_EXITED, e -> setDefaultCursor(button));
    }

    /**
     * Validates email with regex.
     */
    public boolean validEmail(String email) {
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Checks for empty input.
     */
    public boolean isInputEmpty(String input) {
        return input == null || input.trim().isEmpty();
    }

    /**
     * Converts Object to JSONObject if possible.
     */
    public JSONObject toJSonObject(Object response) {
        return response instanceof JSONObject ? (JSONObject) response : null;
    }

    /**
     * Converts Object to JSONArray if possible.
     */
    public JSONArray toJSONArray(Object response) {
        return response instanceof JSONArray ? (JSONArray) response : null;
    }

    /**
     * Logout and go to login screen.
     */
    public void logout(Stage stage, Button triggerBtn) {
        TokenStorage.clearToken();
        goPage(stage, triggerBtn, "/fxml/hello_view.fxml");
    }
}
