package controller;

import javafx.application.Platform;
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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static utils.MainPageServices.*;
import static utils.NoteServices.findNoteById;

public class ReadOnlyNoteController extends PageController {

    @FXML private Label groupName;
    @FXML private Label localTime;
    @FXML private Label nameLabel;
    @FXML private VBox textVBox;
    @FXML private Label titleTextArea;
    @FXML private TextArea textArea1;
    @FXML private HBox categoryHBox;
    @FXML private Label colorPicker;
    @FXML private Label backToAllNotes;
    @FXML private Rectangle noteBackground;

    @FXML private Button myNotesBtn;
    @FXML private Button shareNotesBtn;
    @FXML private Button myGroupsBtn;
    @FXML private Button allGroupsBtn;
    @FXML private Button accountBtn;
    @FXML private Button logOutBtn;

    private static final String findNoteByIdURL = "http://localhost:8093/api/note/";
    private final SelectedReadOnlyNote selectedReadOnlyNote = SelectedReadOnlyNote.getInstance();

    private final ControllerUtils controllerUtils = new ControllerUtils();
    private final HttpResponseService responseService = new HttpResponseServiceImpl();
    private final ObservableResourceFactory RESOURCE_FACTORY = ObservableResourceFactory.getInstance();

    private Note note;
    private final HashMap<Integer, String> groupList = new HashMap<>();
    private final ArrayList<String> figureList = new ArrayList<>();

    private Stage stage;

    // Initialization logic
    public void initialize() {
        TokenStorage.getIntance();

        RESOURCE_FACTORY.getResourceBundle();
        Platform.runLater(this::updateUI);

        note = findNoteById(findNoteByIdURL, selectedReadOnlyNote.getId(), TokenStorage.getToken());
        assert note != null;

        populateNoteFields();
        groupSharingFetching();

        setSidebarLanguages(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn);
    }

    private void updateUI() {
        super.updateDisplay();
    }

    private void populateNoteFields() {
        textArea1.setText(note.getText());
        titleTextArea.setText(note.getTitle());
        figureList.addAll(note.getFigure());

        updateLocalTime(localTime);
        updateNameLabel(nameLabel, TokenStorage.getUser());
        loadFigures();
        colorSetUp();
    }

    private void loadFigures() {
        Platform.runLater(() -> {
            figureList.forEach(figure -> {
                try {
                    Image image = new Image(new FileInputStream(figure));
                    ImageView imageView = new ImageView(image);
                    imageView.setFitHeight(200);
                    imageView.setFitWidth(200);
                    imageView.setPreserveRatio(true);
                    textVBox.getChildren().add(imageView);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }

    private void colorSetUp() {
        noteBackground.setFill(Color.web(note.getColor()));
        colorPicker.setText("");  // Display-only
    }

    private void groupSharingSetUp() {
        groupName.setText(note.getGroup());
    }

    private void groupSharingFetching() {
        HttpRequestBuilder builder = new HttpRequestBuilder("GET", "http://localhost:8093/api/groups/my-groups", true);
        HttpRequestBase request = builder.getHttpRequestBase();
        CloseableHttpClient client = builder.getHttpClient();

        try {
            builder.setRequestBody();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        responseService.handleReponse(request, client, this::handleGetOwnGroups);
    }

    private void handleGetOwnGroups(CloseableHttpResponse response, Object responseObject) {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            JSONArray jsonArray = (JSONArray) responseObject;
            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject group = jsonArray.getJSONObject(i);
                    groupList.put(group.getInt("id"), group.getString("name"));
                }
                groupList.put(-1, "No Group");
                groupSharingSetUp();
            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }
        } else if (statusCode == 404) {
            groupList.put(-1, "No Group");
        }
    }

    // Navigation logic
    @FXML public void myGroupsBtnClick() { ControllerUtils_v2.goToMyGroupsPage(stage, myGroupsBtn); }
    @FXML public void myNotesBtnClick() { ControllerUtils_v2.goToMyNotesPage(stage, myNotesBtn); }
    @FXML public void shareNotesBtnClick() { ControllerUtils_v2.goToMyGroupNotesPage(stage, shareNotesBtn); }
    @FXML public void allGroupsBtnClick() { ControllerUtils_v2.goToAllGroupsPage(stage, allGroupsBtn); }
    @FXML public void accountBtnClick() { ControllerUtils_v2.goToAccountPage(stage, accountBtn); }
    @FXML public void logOutBtnClick() { controllerUtils.logout(stage, logOutBtn); }

    @FXML public void backToAllNotesClick() {
        controllerUtils.goPage(stage, backToAllNotes, "/fxml/main_pages/groups/my_groups_notes.fxml");
    }

    @FXML void mouseEnter() {
        setCursor(true);
    }

    @FXML void mouseExit() {
        setCursor(false);
    }

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
