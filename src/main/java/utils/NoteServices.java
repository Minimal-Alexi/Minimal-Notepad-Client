package utils;

import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Figure;
import model.Note;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;

import static utils.NoteJson.*;

public class NoteServices {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String RESPONSE_STATUS_CODE_TEXT = "Response Status Code: ";
    private static final String RESPONSE_BODY_TEXT = "Response Body: ";
    private static final String UPLOAD_PICTURE_TEXT = "Upload picture";

    public static void createNote(String url, Note note, String token) {
        JSONObject jsonBody = NoteToJson(note);

        HttpClient client = HttpClient.newHttpClient();

        System.out.println(jsonBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(AUTHORIZATION, BEARER + token)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(RESPONSE_STATUS_CODE_TEXT + response.statusCode());
            System.out.println(RESPONSE_BODY_TEXT + response.body());
        } catch (IOException e) {
            System.err.println("IO error occurred: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Re-interrupt the thread
            System.err.println("Thread was interrupted: " + e.getMessage());
            throw new RuntimeException(e); // Optionally rethrow as RuntimeException
        }
    }

    public static Note findNoteById(String url, Integer id, String token) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + id))
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(AUTHORIZATION, BEARER + token)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(RESPONSE_STATUS_CODE_TEXT + response.statusCode());
            System.out.println(RESPONSE_BODY_TEXT + response.body());

            if (response.statusCode() == 200) {
                JSONObject result = new JSONObject(response.body());
                System.out.println("result from API: "+result);
                return JsonToNote(result);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e); // Rethrow as RuntimeException
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Re-interrupt the thread
            System.err.println(e.getMessage());
            throw new RuntimeException(e); // Optionally rethrow as RuntimeException
        }
        return null;
    }

    public static void deleteNoteById(String url, Integer id, String token) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + id))
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(AUTHORIZATION, BEARER + token)
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(RESPONSE_STATUS_CODE_TEXT + response.statusCode());

            response.statusCode();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e); // Rethrow as RuntimeException
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Re-interrupt the thread
            System.err.println(e.getMessage());
            throw new RuntimeException(e); // Optionally rethrow as RuntimeException
        }
    }

    public static HashMap<Integer, String> getAllCategories(String url, String token) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(AUTHORIZATION, BEARER + token)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONArray result = new JSONArray(response.body());
                HashMap<Integer, String> categories = new HashMap<>();
                // change the json array to the hashmap
                for (int i = 0; i < result.length(); i++) {
                    categories.put(result.getJSONObject(i).getInt("id"), result.getJSONObject(i).getString("name"));
                }
                return categories;
            }
        } catch (IOException e) {
            throw new RuntimeException(e); // Rethrow as RuntimeException
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Re-interrupt the thread
            throw new RuntimeException(e); // Optionally rethrow as RuntimeException
        }
        return null;
    }

    public static void addCategory(HashMap<Integer, String> categories, HashMap<Integer, String> categoryList, HBox categoryHBox, ContextMenu contextMenu) {
        categories.forEach((k, v) -> {
            MenuItem item = new MenuItem(v);
            item.setOnAction(event -> {
                // add category label
                categoryList.put(k, v);
                System.out.println(k + v);
                System.out.println(categoryList);

                /*
                update the ui
                */
                // remove every element in the category HBox instead of the first one(the Category: label) and the last one(the "+")
                if (categoryHBox.getChildren().size() > 2) {
                    categoryHBox.getChildren().remove(1, categoryHBox.getChildren().size() - 1);
                }

                // query the categoryList to add categories to the ui
                updateCategory(categoryList, categoryHBox);
            });
            contextMenu.getItems().add(item);
        });
    }

    public static void updateCategory(HashMap<Integer, String> categoryList, HBox categoryHBox) {
        categoryList.forEach((key, value) -> {
            Label label = new Label(value);
            label.getStyleClass().add("category");
            categoryHBox.getChildren().add(categoryHBox.getChildren().size() - 1, label);
            // add the " - " to the Label
            Label removeCategory = new Label("x");
            removeCategory.getStyleClass().add("remove-category");
            removeCategory.setOnMouseClicked(event1 -> {
                categoryHBox.getChildren().remove(label);
                categoryList.remove(key);
            });
            label.setGraphic(removeCategory);
        });
    }

//    public static void uploadPicture(Button uploadPicBtn, ArrayList<String> figureList, VBox textVBox) throws IOException {
        public static void uploadPicture(Button uploadPicBtn, ArrayList<Figure> figureList, VBox textVBox) throws IOException {

            uploadPicBtn.setDisable(true);
        uploadPicBtn.setText("Uploading... ");

        Stage stage = new Stage();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(UPLOAD_PICTURE_TEXT);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PICTURES", "*.jpg", "*.png", "*.jpeg"));
        fileChooser.setInitialDirectory(new File("C:"));

        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            String filePath = file.getAbsolutePath();
            GoogleDriveUploader googleDriveUploader = new GoogleDriveUploader();
            String googlePath = googleDriveUploader.upload(filePath);
            Figure newPic = new Figure(googlePath);
            figureList.add(newPic);
            System.out.println(googlePath);

            // Get the picture from Google Drive
            ImageView imageView = new ImageView();
            Image image = googleDriveUploader.download(googlePath);
            imageView.setImage(image);
            imageView.setFitHeight(200);
            imageView.setFitWidth(200);
            imageView.setPreserveRatio(true);
            textVBox.getChildren().add(imageView);

            uploadPicBtn.setDisable(false);
            uploadPicBtn.setText(UPLOAD_PICTURE_TEXT);
        } else {
            System.out.println("no file selected");
            uploadPicBtn.setDisable(false);
        }
    }

//    public static void uploadPictureLocal(Button uploadPicBtn, ArrayList<String> figureList, VBox textVBox) {
    public static void uploadPictureLocal(Button uploadPicBtn, ArrayList<Figure> figureList, VBox textVBox) {
        uploadPicBtn.setDisable(true);
        uploadPicBtn.setText("Uploading... ");

        Stage stage = new Stage();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(UPLOAD_PICTURE_TEXT);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PICTURES", "*.jpg", "*.png", "*.jpeg"));
        fileChooser.setInitialDirectory(new File("C:"));

        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            String filePath = file.getAbsolutePath();
            Figure newPic = new Figure(filePath);
            figureList.add(newPic);
            System.out.println(filePath);

            // Get the picture from local disk
            ImageView imageView = new ImageView();
            Image image = new Image(filePath);
            imageView.setImage(image);
            imageView.setFitHeight(200);
            imageView.setFitWidth(200);
            imageView.setPreserveRatio(true);
            textVBox.getChildren().add(imageView);

            uploadPicBtn.setDisable(false);
            uploadPicBtn.setText(UPLOAD_PICTURE_TEXT);
        } else  {
            System.out.println("no file selected");
            uploadPicBtn.setDisable(false);
        }
    }

    public static void updateNote(String url, int id, String token, Note note){
        JSONObject jsonBody = NoteToJson(note);

        HttpClient client = HttpClient.newHttpClient();

        System.out.println(jsonBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + id))
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(AUTHORIZATION, BEARER + token)
                .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(RESPONSE_STATUS_CODE_TEXT + response.statusCode());
            System.out.println(RESPONSE_BODY_TEXT + response.body());
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e); // Rethrow as RuntimeException
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Re-interrupt the thread
            System.err.println(e.getMessage());
            throw new RuntimeException(e); // Optionally rethrow as RuntimeException
        }
    }
}
