package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.HttpClientSingleton;

public class HelloView extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/hello_view.fxml"));
        Parent root = fxmlLoader.load();

        stage.setScene(new Scene(root));
        stage.setTitle("Notebook");
        stage.show();
    }


    @Override
    public void stop() {
        HttpClientSingleton.getInstance().closeHttpClient();
    }
//    public static void main(String[] args) {
//        launch(args);
//    }}
}
