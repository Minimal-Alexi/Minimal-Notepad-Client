package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import model.*;
import model.selected.SelectedReadOnlyNote;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utils.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import static utils.MainPageServices.*;
import static utils.NoteServices.findNoteById;

public class ReadOnlyNoteController extends PageController {

    // UI elements
    @FXML private Label groupName, localTime, nameLabel, titleTextArea, colorPicker, backToAllNotes;
    @FXML private TextArea textArea1;
    @FXML private VBox textVBox;
    @FXML private HBox categoryHBox;
    @FXML private Rectangle noteBackground;
    @FXML private Button myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn;

    // Services and helpers
    private final ControllerUtils controllerUtils = new ControllerUtils();
    private final HttpResponseService responseService = new HttpResponseServiceImpl();
    private final ObservableResourceFactory RESOURCE_FACTORY = ObservableResourceFactory.getInstance();

    // Note and related data
    private final SelectedReadOnlyNote selectedNote = SelectedReadOnlyNote.getInstance();
    private final HashMap<Integer, String> groupList = new HashMap<>();
    private final ArrayList<String> figureList = new ArrayList<>();
    private Note note;
    private Stage stage;

    private static final String FIND_NOTE_URL = "http://localhost:8093/api/note/";

    // Init method
    public void initialize() {
        TokenStorage.getIntance();
        RESOURCE_FACTORY.getResourceBundle();
        Platform.runLater(this::updateUI);

        note = findNoteById(FIND_NOTE_URL, selectedNote.getId(), TokenStorage.getToken());
        if (note == null) throw new RuntimeException("Note not found");

        populateNoteFields();
        fetchGroupSharingData();

        setSidebarLanguages(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn);
    }

    private void updateUI() {
        super.updateDisplay();
    }

    private void populateNoteFields() {
        titleTextArea.setText(note.getTitle());
        textArea1.setText(note.getText());
        figureList.addAll(note.getFigure());

        updateLocalTime(localTime);
        updateNameLabel(nameLabel, TokenStorage.getUser());
        loadFigures();
        setupColor();
    }

    private void loadFigures() {
        Platform.runLater(() -> figureList.forEach(figure -> {
            try {
                Image image = new Image(new FileInputStream(figure));
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(200);
                imageView.setFitWidth(200);
                imageView.setPreserveRatio(true);
                textVBox.getChildren().add(imageView);
            } catch (FileNotFoundException e) {
                System.err.println("Image file not found: " + figure);
            }
        }));
    }

    private void setupColor() {
        noteBackground.setFill(Color.web(note.getColor()));
        colorPicker.setText(""); // Read-only
    }

    private void fetchGroupSharingData() {
        HttpRequestBuilder builder = new HttpRequestBuilder("GET", "http://localhost:8093/api/groups/my-groups", true);
        HttpRequestBase request = builder.getHttpRequestBase();
        CloseableHttpClient client = builder.getHttpClient();

        try {
            builder.setRequestBody();
            responseService.handleReponse(request, client, this::handleGroupResponse);
        } catch (UnsupportedEncodingException e) {
            System.err.println("Failed to set request body: " + e.getMessage());
        }
    }

    private void handleGroupResponse(CloseableHttpResponse response, Object responseObject) {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 200 && responseObject instanceof JSONArray jsonArray) {
            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject group = jsonArray.getJSONObject(i);
                    groupList.put(group.getInt("id"), group.getString("name"));
                }
            } catch (JSONException e) {
                System.err.println("Failed to parse group list: " + e.getMessage());
            }
        }
        groupList.put(-1, "No Group");
        groupName.setText(note.getGroup());
    }

    // Sidebar navigation
    @FXML public void myNotesBtnClick() { ControllerUtils_v2.goToMyNotesPage(stage, myNotesBtn); }
    @FXML public void shareNotesBtnClick() { ControllerUtils_v2.goToMyGroupNotesPage(stage, shareNotesBtn); }
    @FXML public void myGroupsBtnClick() { ControllerUtils_v2.goToMyGroupsPage(stage, myGroupsBtn); }
    @FXML public void allGroupsBtnClick() { ControllerUtils_v2.goToAllGroupsPage(stage, allGroupsBtn); }
    @FXML public void accountBtnClick() { ControllerUtils_v2.goToAccountPage(stage, accountBtn); }
    @FXML public void logOutBtnClick() { controllerUtils.logout(stage, logOutBtn); }

    // Navigation
    @FXML public void backToAllNotesClick() {
        controllerUtils.goPage(stage, backToAllNotes, "/fxml/main_pages/groups/my_groups_notes.fxml");
    }

    // Cursor UI feedback
    @FXML void mouseEnter() { setCursor(true); }
    @FXML void mouseExit() { setCursor(false); }

    private void setCursor(boolean hand) {
        if (hand) {
            controllerUtils.setHandCursor(
                    myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn,
                    accountBtn, logOutBtn, backToAllNotes
            );
        } else {
            controllerUtils.setDefaultCursor(
                    myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn,
                    accountBtn, logOutBtn, backToAllNotes
            );
        }
    }

    @Override
    public void bindUIComponents() {
        titleTextArea.textProperty().bind(RESOURCE_FACTORY.getStringBinding("titleTextArea"));
        nameLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("welcomeText"));
        backToAllNotes.textProperty().bind(RESOURCE_FACTORY.getStringBinding("backToAllNotes"));
    }
}
