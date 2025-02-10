package utils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
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
import java.util.ArrayList;
import java.util.Date;

public class MainPageServices {

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
            System.out.println("Response Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());

            if (response.statusCode() == 200) {
                ArrayList<Note> notes = new ArrayList<>();
                JSONArray result = new JSONArray(response.body());
                for (int i = 0; i < result.length(); i++) {
                    JSONObject noteJson = result.getJSONObject(i);
                    Note note = new Note(noteJson.getInt("id"),
                            noteJson.getString("text") ,
                            noteJson.getString("title"),
                            noteJson.getString("colour"),
                            ("createdAt"),
                            ("updatedAt"),
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



    // Update the time Label
    public static void updateTime(Label timeLabel) {
        DateFormat currentTime = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");

        EventHandler<ActionEvent> eventHandler = e -> {
            timeLabel.setText(currentTime.format(new Date()));
        };

        Timeline animation = new Timeline(new KeyFrame(Duration.millis(1000), eventHandler));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }
}
