package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class LogInView {
    public static Parent loadLogInView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LogInView.class.getResource("/fxml/logIn_view.fxml"));

        return fxmlLoader.load();
    }
}
