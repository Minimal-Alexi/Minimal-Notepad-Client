package utils;

import model.Note;
import org.json.JSONObject;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static utils.MainPageServices.timestampToString;


public class NoteServices {

    public static void createNote(String url, Note note, String token) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("colour", note.getColor());
        jsonBody.put("text", note.getText());
        jsonBody.put("title", note.getTitle());

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Note findNoteById(String url, Integer id, String token) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());

            if (response.statusCode() == 200) {
                JSONObject result = new JSONObject(response.body());

                return new Note(result.getInt("id"),
                        result.getString("title") ,
                        result.getString("text"),
                        result.getString("colour"),
                        timestampToString(result.getString("createdAt")),
                        timestampToString(result.getString("updatedAt")),
                        result.getJSONObject("user").getString("username"),
                        " ",
                        "null");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static void deleteNoteById(String url, Integer id, String token) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response Status Code: " + response.statusCode());

            response.statusCode();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
