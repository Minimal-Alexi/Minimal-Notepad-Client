package utils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Note;
import model.ObservableResourceFactory;
import model.TokenStorage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class MainPageServices {

    /*
    Update the note table
     */
    public static void updateNoteTable(ObservableList<Note> notes, TableView<Note> table, TableColumn<Note, String> title, TableColumn<Note, String> group, TableColumn<Note, String> owner, TableColumn<Note, String> category, TableColumn<Note, String> createTime, TableColumn<Note, Void> icon) {
        table.setItems(notes);
        title.setCellValueFactory(new PropertyValueFactory<Note, String>("title"));
        group.setCellValueFactory(new PropertyValueFactory<Note, String>("group"));
        owner.setCellValueFactory(new PropertyValueFactory<Note, String>("owner"));
        category.setCellValueFactory(cellData -> {
            HashMap<Integer, String> catMap = cellData.getValue().getCategory();
            String categoriesListString = "";
            if (catMap != null && !catMap.isEmpty()) {
                categoriesListString = String.join(", ", catMap.values());
            }
            return new ReadOnlyStringWrapper(categoriesListString);
        });
        createTime.setCellValueFactory(new PropertyValueFactory<Note, String>("createdAt"));
        icon.setCellFactory(param -> new TableCell<Note, Void>() {
            private final ImageView imageView = new ImageView(
                    new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/icon/FileText.png")))
            );

            {
                imageView.setFitWidth(20);
                imageView.setFitHeight(20);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(imageView);
                }
            }
        });
    }

    /*
    Find the notes
     */
    public static ArrayList<Note> findAllMyNotes(String url, String token) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ArrayList<Note> notes = new ArrayList<>();
                JSONArray result = new JSONArray(response.body());
                for (int i = 0; i < result.length(); i++) {
                    JSONObject noteJson = result.getJSONObject(i);
                    Note note = new Note(noteJson.getInt("id"),
                            noteJson.getString("title"),
                            noteJson.getString("text"),
                            noteJson.getString("colour"),
                            timestampToString(noteJson.getString("createdAt")),
                            timestampToString(noteJson.getString("updatedAt")),
                            noteJson.getJSONObject("user").getString("username"),
                            -1,
                            noteJson.isNull("group")? "N/A" : noteJson.getJSONObject("group").getString("name"),
                            jsonArrayToHashMap(noteJson.getJSONArray("categoriesList")),
                            jsonArrayToFigureList(noteJson.getJSONArray("figures"))
                    );
                    notes.add(note);
                }
                return notes;
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /*
    Update the time Label
     */
    public static void updateLocalTime(Label timeLabel) {

        DateFormat currentTime = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");

        // the method from Utils will get the date time format from current locale
        // however, there is bug with the current implementation, the date time format is not displayed correctly
        // it shuffle between the old format and new format

//        DateFormat currentTime = Utils.getTheCurrentLocaleDateTimeFormatString();
        System.out.println(timeLabel.getUserData());
        if (timeLabel.getUserData() instanceof Timeline) {
//            ((Timeline) timeLabel.getUserData()).stop();
            System.out.println("time label is instance of timeline");
        }

        EventHandler<ActionEvent> eventHandler = e -> {
            // get current time
//            Utils.
            timeLabel.setText(currentTime.format(new Date()));
//            Utils.displayTime(timeLabel);
        };

        Timeline animation = new Timeline(new KeyFrame(Duration.millis(1000), eventHandler));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }

    public static void updateLocalTime(Label timeLabel, ObservableResourceFactory RESOURCE_FACTORY) {
        DateFormat currentTime = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");

        EventHandler<ActionEvent> eventHandler = e -> {
            // get current time
//            Utils.
//            timeLabel.setText(currentTime.format(new Date()));
//            Utils.displayTime(timeLabel, RESOURCE_FACTORY);
        };

        Timeline animation = new Timeline(new KeyFrame(Duration.millis(1000), eventHandler));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }

    /*
    Change the timestamp into read-friendly form
     */
    public static String timestampToString(String timestamp) {
        OffsetDateTime odt = OffsetDateTime.parse(timestamp);
        return odt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /*
    Update the stage
     */
    public static <T extends Event> void goToPage(Stage stage, Scene scene, T event, String url) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(MainPageServices.class.getResource(url)));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /*
    Update the name label after log in
     */
    public static void updateNameLabel(Label nameLabel, String username) {
        nameLabel.setText("Welcome " + username);
    }

    /*
    Update the recently edited part of the main page
     */
    public static void updateRecentlyEdited(HBox REHBox, ArrayList<Note> noteArrayList) {
        // Get the first 4 notes
        noteArrayList.sort(new Comparator<Note>() {

            @Override
            public int compare(Note o1, Note o2) {
                return o1.getUpdatedAt().compareTo(o2.getUpdatedAt());
            }
        });

        int i = Math.min(noteArrayList.size(), 4);

        for (int j = 0; j < i; j++) {
            AnchorPane pane = getAnchorPane(noteArrayList.get(j).getColor());
            Label title = new Label();
            title.setText(noteArrayList.get(j).getTitle());
            title.getStyleClass().add("title");
            pane.getChildren().add(title);
            Label editAt = new Label();
            editAt.setText("Edited at " + noteArrayList.get(j).getUpdatedAt());
            editAt.getStyleClass().add("edit-at");
            pane.getChildren().add(editAt);

            REHBox.getChildren().add(pane);
        }
    }

    private static AnchorPane getAnchorPane(String circleColor) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getStyleClass().add("anchor-pane");

        Rectangle rec1 = new Rectangle(200, 146);
        rec1.getStyleClass().add("rectangle1");

        SVGPath svg = new SVGPath();
        svg.setContent("M0 8C0 3.58172 3.58172 0 8 0H166C170.418 0 174 3.58172 174 8V131H0V8Z");
        svg.getStyleClass().add("svg");


        Circle circle = new Circle(14.0);
        circle.getStyleClass().add("circle");
        circle.setFill(Color.web(circleColor));

        Rectangle rec2 = new Rectangle(148, 15);
        rec2.getStyleClass().add("rectangle2");
        Rectangle rec3 = new Rectangle(148, 15);
        rec3.getStyleClass().add("rectangle3");
        Rectangle rec4 = new Rectangle(148, 15);
        rec4.getStyleClass().add("rectangle4");

        anchorPane.getChildren().add(rec1);
        anchorPane.getChildren().add(svg);
        anchorPane.getChildren().add(circle);
        anchorPane.getChildren().add(rec2);
        anchorPane.getChildren().add(rec3);
        anchorPane.getChildren().add(rec4);
        return anchorPane;
    }


    /*
    JsonArray and hashMap mutual transformation
     */
    public static HashMap<Integer, String> jsonArrayToHashMap(JSONArray jsonArray) {
        HashMap<Integer, String> hashMap = new HashMap<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            int id = jsonObject.getInt("id");
            String name = jsonObject.getString("name");
            hashMap.put(id, name);
        }
        return hashMap;
    }

    public static JSONArray hashMapToJSONArray(HashMap<Integer, String> hashMap) {
        JSONArray jsonArray = new JSONArray();
        for (Map.Entry<Integer, String> entry : hashMap.entrySet()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", entry.getKey());
            jsonObject.put("name", entry.getValue());
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    public static ArrayList<String> jsonArrayToFigureList(JSONArray jsonArray) {
        ArrayList<String> figureList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String link = jsonObject.getString("link");
            figureList.add(link);
        }
        return figureList;
    }

    public static JSONArray figureListToJSONArray(ArrayList<String> figureList) {
        JSONArray jsonArray = new JSONArray();
        for (String s : figureList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("link", s);
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }
}
