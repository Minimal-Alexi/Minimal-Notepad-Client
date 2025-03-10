package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import utils.ControllerUtils;
import utils.HttpResponseService;
import utils.HttpResponseServiceImpl;
import utils.NoteServices;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static utils.MainPageServices.*;
import static utils.NoteServices.*;

public class ReadNoteController {

    @FXML private ChoiceBox<String> groupSharingChoiceBox;
    @FXML private Label localTime;
    @FXML private Label nameLabel;
    @FXML private VBox textVBox;
    @FXML private TextField titleTextArea;
    @FXML private TextArea textArea1;
    @FXML private Button saveNoteBtn;
    @FXML private Button deleteNoteBtn;
    @FXML private HBox categoryHBox;
    @FXML private Label addCategory;
    @FXML private Button backToNoteList;
    @FXML private Button uploadPicBtn;
    @FXML private ColorPicker colorPicker;
    @FXML private Rectangle noteBackground;
    @FXML private Button myNotesBtn;
    @FXML private Button shareNotesBtn;
    @FXML private Button myGroupsBtn;
    @FXML private Button allGroupsBtn;
    @FXML private Button accountBtn;
    @FXML private Button logOutBtn;

    private ControllerUtils controllerUtils;
    private HttpResponseService responseService;
    private SelectedNote selectedNote = SelectedNote.getInstance();
    private Note note;
    private HashMap<Integer, String> categoryList = new HashMap<>();
    private HashMap<Integer, String> groupList = new HashMap<>();
    private ArrayList<String> figureList = new ArrayList<>();
    private Scene scene;

    // Initialize
    public void initialize() {
        responseService = new HttpResponseServiceImpl();
        this.controllerUtils = new ControllerUtils();

        System.out.println(selectedNote.getId());

        note = findNoteById("http://localhost:8093/api/note/id", selectedNote.getId(), TokenStorage.getToken());

        if (note != null) {
            textArea1.setText(note.getText());
            titleTextArea.setText(note.getTitle());
            categoryList = note.getCategory() != null ? note.getCategory() : new HashMap<>();
            figureList = note.getFigure() != null ? note.getFigure() : new ArrayList<>();

            colorSetUp();
            groupSharingFetching();

            // Update UI elements
            updateCategory(categoryList, categoryHBox);
            updateLocalTime(localTime);
            updateNameLabel(nameLabel, TokenStorage.getUser());
        } else {
            System.err.println("Error: Note not found!");
        }
    }

    public void backToNoteListClicked(ActionEvent event) throws IOException {
        backToNoteList.setDisable(true);

        // Delete the note
        NoteServices.deleteNoteById("http://localhost:8093/api/note/", selectedNote.getId(), TokenStorage.getToken());

        // Get the current stage
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

        // Navigate to my groups notes page
        goToPage(stage, scene, event, "/fxml/main_pages/my_groups_notes.fxml");
    }

    public void groupSharingSetUp() {
        groupSharingChoiceBox.getItems().addAll(groupList.values());
        if (note.getGroupId() == -1) {
            groupSharingChoiceBox.getSelectionModel().select("No Group");
        } else {
            groupSharingChoiceBox.getSelectionModel().select(note.getGroup());
        }
    }

    public void groupSharingFetching() {
        HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder("GET", "http://localhost:8093/api/groups/my-groups", true);
        HttpRequestBase filterRequestHttp = httpRequestBuilder.getHttpRequest();
        CloseableHttpClient httpClient = httpRequestBuilder.getHttpClient();
        try {
            httpRequestBuilder.setRequestBody();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        responseService.handleReponse(filterRequestHttp, httpClient, this::handleGetOwnGroups);
    }

    private void colorSetUp() {
        noteBackground.setFill(Color.web(note.getColor()));
        colorPicker.setValue(Color.web(note.getColor()));
        colorPicker.setOnAction(event -> {
            noteBackground.setFill(colorPicker.getValue());
        });
    }

    private void handleGetOwnGroups(CloseableHttpResponse response, Object responseObject) {
        System.out.println(response.getStatusLine().getStatusCode());
        if (response.getStatusLine().getStatusCode() == 200) {
            JSONArray jsonResponse = (JSONArray) responseObject;
            try {
                for (int i = 0; i < jsonResponse.length(); i++) {
                    JSONObject jsonObject = jsonResponse.getJSONObject(i);
                    groupList.put(jsonObject.getInt("id"), jsonObject.getString("name"));
                }
                groupList.put(-1, "No Group");
                groupSharingSetUp();
            } catch (JSONException e) {
                System.out.println(e);
            }
        } else {
            JSONObject jsonResponse = (JSONObject) responseObject;
            if (response.getStatusLine().getStatusCode() == 404) {
                groupList.put(-1, "No Group");
            }
        }
    }

    private int getGroupId() {
        for (Map.Entry<Integer, String> entry : groupList.entrySet()) {
            if (entry.getValue().equals(groupSharingChoiceBox.getValue())) {
                return entry.getKey();
            }
        }
        return -1;
    }

    private String getGroupName() {
        return groupSharingChoiceBox.getValue();
    }
}
