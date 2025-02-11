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
import javafx.scene.control.Label;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

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
    public static void updateTime(Label timeLabel) {
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

     */
    public static <T extends Event> void goToPage(Stage stage, Scene scene, T event, String url) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(MainPageServices.class.getResource(url)));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
