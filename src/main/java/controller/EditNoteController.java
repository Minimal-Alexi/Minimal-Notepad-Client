package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import model.HttpRequestBuilder;
import model.Note;
import model.TokenStorage;
import model.selected.SelectedNote;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utils.GoogleDriveUploader;
import utils.HttpResponseService;
import utils.HttpResponseServiceImpl;
import utils.NoteServices;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import static utils.MainPageServices.*;
import static utils.NoteServices.*;


public class EditNoteController {

    @FXML private Label localTime;
    @FXML private Label nameLabel;
    @FXML private VBox textVBox;
    @FXML private TextField titleTextArea;
    @FXML private TextArea textArea1;
    @FXML private Button saveNoteBtn;
    @FXML private Button deleteNoteBtn;
    @FXML private HBox categoryHBox;
    @FXML private Label addCategory;
    @FXML private Button uploadPicBtn;
    @FXML private ColorPicker colorPicker;
    @FXML private Rectangle noteBackground;
    @FXML private ChoiceBox<String> groupSharingChoiceBox;

    private HttpResponseService responseService;
    SelectedNote selectedNote = SelectedNote.getInstance();
    private HashMap<Integer, String> categoryList = new HashMap<>();
    private ArrayList<String> figureList = new ArrayList<>();

    // Initialize
    public void initialize() {
        responseService = new HttpResponseServiceImpl();

        System.out.println(selectedNote.getId());

        Note note = findNoteById("http://localhost:8093/api/note/", selectedNote.getId(), TokenStorage.getToken());

        assert note != null;
        textArea1.setText(note.getText());
        titleTextArea.setText(note.getTitle());
        categoryList = note.getCategory();
        figureList = note.getFigure();

        colorSetUp(note.getColor());

        // query the categoryList to add categories to the ui
        updateCategory(categoryList, categoryHBox);

        updateLocalTime(localTime);
        updateNameLabel(nameLabel, TokenStorage.getUser());

        // add pictures to the ui
        Platform.runLater(() -> {
            figureList.forEach(figure -> {
                GoogleDriveUploader googleDriveUploader = new GoogleDriveUploader();
                ImageView imageView = new ImageView();
                try {
                    Image image = googleDriveUploader.download(figure);
                    imageView.setImage(image);
                    imageView.setFitHeight(200);
                    imageView.setFitWidth(200);
                    imageView.setPreserveRatio(true);
                    textVBox.getChildren().add(imageView);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }

    public void saveNoteClicked(ActionEvent event) throws IOException {
        //Disable the button
        saveNoteBtn.setDisable(true);
        Note note = new Note(selectedNote.getId(), titleTextArea.getText(), textArea1.getText(), colorPicker.getValue().toString(), "N/A", "N/A", TokenStorage.getUser(), "N/A", categoryList, figureList);
        NoteServices.updateNote("http://localhost:8093/api/note/", selectedNote.getId(), TokenStorage.getToken(), note);
        goToPage(stage, scene, event, "/fxml/main_pages/main_page.fxml");
    }

    public void deleteNoteClicked(ActionEvent event) throws IOException {
        //Disable the button
        deleteNoteBtn.setDisable(true);
        NoteServices.deleteNoteById("http://localhost:8093/api/note/", selectedNote.getId(), TokenStorage.getToken());
        goToPage(stage, scene, event, "/fxml/main_pages/main_page.fxml");
    }

    public void addCategoryClicked(MouseEvent mouseEvent) {
        addCategory.setDisable(true);

        // Create a context menu of categories for the user to choose
        HashMap<Integer, String> categories = getAllCategories("http://localhost:8093/api/categories", TokenStorage.getToken());
        ContextMenu contextMenu = new ContextMenu();

        assert categories != null;
        addCategory(categories, categoryList, categoryHBox, contextMenu);

        if (!contextMenu.isShowing()) {
            contextMenu.show(addCategory, mouseEvent.getScreenX(), mouseEvent.getScreenY());
        } else {
            contextMenu.hide();
        }

        // The adding behavior is over, enable the add button
        addCategory.setDisable(false);

    }
    public void groupSharingSetup(){
        HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder("GET","http://localhost:8093/api/groups/my-groups",true);
        HttpRequestBase filterRequestHttp = httpRequestBuilder.getHttpRequest();
        CloseableHttpClient httpClient = httpRequestBuilder.getHttpClient();
        try
        {
            httpRequestBuilder.setRequestBody();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        responseService.handleReponse(filterRequestHttp,httpClient,this::handleGetOwnGroups);
    }

    public void uploadPicClicked(MouseEvent mouseEvent) throws IOException {
        uploadPicture(uploadPicBtn, figureList, textVBox);
    }

    /*
    Go to another page
     */
    private Stage stage;
    private Scene scene;
    private Parent root;

    public void groupsClicked(ActionEvent event) throws IOException {
        goToPage(stage, scene, event, "/fxml/main_pages/groups_page.fxml");
    }

    private void colorSetUp(String initialColor) {
        noteBackground.setFill(Color.web(initialColor));
        colorPicker.setValue(Color.web(initialColor));
        colorPicker.setOnAction(event -> {
            noteBackground.setFill(colorPicker.getValue());
        });
    }
    private void handleGetOwnGroups(CloseableHttpResponse response, Object responseObject){
        System.out.println(response.getStatusLine().getStatusCode());
        if (response.getStatusLine().getStatusCode() == 200) {
            JSONArray jsonResponse = (JSONArray) responseObject;
            try {
                for(int i = 0; i < jsonResponse.length(); i++){
                    JSONObject jsonObject = jsonResponse.getJSONObject(i);
                }
            } catch (JSONException e) {
                System.out.println(e);
            }
        } else {
            JSONObject jsonResponse = (JSONObject) responseObject;
            if (response.getStatusLine().getStatusCode() == 404) {
//                Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                alert.setTitle("Error");
//                alert.setHeaderText(null);
//                alert.setContentText(jsonResponse.getString("message"));
//                alert.showAndWait();
            }
        }
    }


}
