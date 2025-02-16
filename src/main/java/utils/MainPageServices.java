package utils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Note;
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

public class MainPageServices {

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
                            noteJson.getString("title") ,
                            noteJson.getString("text"),
                            noteJson.getString("colour"),
                            timestampToString(noteJson.getString("createdAt")),
                            timestampToString(noteJson.getString("updatedAt")),
                            noteJson.getJSONObject("user").getString("username"),
                            " ",
                            "null");
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

        EventHandler<ActionEvent> eventHandler = e -> {
            timeLabel.setText(currentTime.format(new Date()));
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

        int i = 0;
        if (noteArrayList.size() <= 4) {
            i = noteArrayList.size();
        } else {
            i = 4;
        }

        for (int j = 0; j < i; j++) {
            AnchorPane pane = getAnchorPane();
            Label title = new Label();
            title.setText(noteArrayList.get(i-1).getTitle());
            title.getStyleClass().add("title");
            pane.getChildren().add(title);
            Label editAt = new Label();
            editAt.setText("Edited at " + noteArrayList.get(i-1).getUpdatedAt());
            editAt.getStyleClass().add("edit-at");
            pane.getChildren().add(editAt);

            REHBox.getChildren().add(pane);
        }
    }

    private static AnchorPane getAnchorPane() {
        AnchorPane anchorPane = new AnchorPane();

        Rectangle rec1 = new Rectangle(200, 146);
        rec1.getStyleClass().add("rectangle1");

        SVGPath svg = new SVGPath();
        svg.setContent("M0 8C0 3.58172 3.58172 0 8 0H166C170.418 0 174 3.58172 174 8V131H0V8Z");
        svg.getStyleClass().add("svg");


        Circle circle = new Circle(14.0);
        circle.getStyleClass().add("circle");

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
}
