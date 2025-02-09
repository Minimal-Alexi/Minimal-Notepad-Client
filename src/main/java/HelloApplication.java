import javafx.application.Application;
import javafx.stage.Stage;
import model.HttpClientSingleton;
import view.HelloView;

import java.io.IOException;

public class HelloApplication {

    public static void main(String[] args) {
        HelloView.launch(HelloView.class);
    }


}