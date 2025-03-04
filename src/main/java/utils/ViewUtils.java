package utils;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.BorderPane;

import java.util.function.Function;

public class ViewUtils {

    private static final String CSSSOURCE = "/CSS";


    public static  <S, T> TableColumn<S, T> column(String title, Function<S, ObservableValue<T>> property, double width) {
        TableColumn<S, T> col = new TableColumn<>(title);
        col.setCellValueFactory(cellData -> property.apply(cellData.getValue()));
        col.setPrefWidth(width);
        return col;
    }

    public static void addStyle(BorderPane root, String styleLink){
        root.getStylesheets().add(ViewUtils.class.getResource(CSSSOURCE + styleLink).toExternalForm());
    }

    public static void addStyle(Button button, String styleLink){
        button.getStylesheets().add(ViewUtils.class.getResource(CSSSOURCE + styleLink).toExternalForm());
    }
}
