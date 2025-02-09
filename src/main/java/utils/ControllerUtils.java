package utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

//import java.awt.*;
import java.io.IOException;

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

    public void setDefaultCursor(Button btn) {
        btn.setCursor(defaultCursor);
    }

    public void setHandcursor(Button btn) {
        btn.setCursor(handCursor);
    }

}
