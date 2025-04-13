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
import controller.PageController;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import model.HttpRequestBuilder;
import model.Note;
import model.ObservableResourceFactory;
import model.TokenStorage;
import model.selected.SelectedReadOnlyNote;
import org.apache.http.client.methods.CloseableHttpResponse;
import model.ObservableResourceFactory;

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
import static utils.NoteServices.*;

public class ReadOnlyNoteController extends PageController {


    @FXML
    private Label groupName;
    @FXML
    private Label localTime;
    @FXML
    private Label nameLabel;
    @FXML
    private VBox textVBox;
    @FXML
    private Label titleTextArea;
    @FXML
    private TextArea textArea1;

    @FXML
    private HBox categoryHBox;
    @FXML
    private Label colorPicker;
    @FXML
    private Label backToAllNotes;

    @FXML
    private Rectangle noteBackground;

    @FXML
    private Button myNotesBtn;
    @FXML
    private Button shareNotesBtn;
    @FXML
    private Button myGroupsBtn;
    @FXML
    private Button allGroupsBtn;
    @FXML
    private Button accountBtn;
    @FXML
    private Button logOutBtn;

    private ControllerUtils controllerUtils;


    private HttpResponseService responseService;
    private SelectedReadOnlyNote selectedReadOnlyNote = SelectedReadOnlyNote.getInstance();
    private Note note;
    private HashMap<Integer, String> groupList = new HashMap<>();
    private ArrayList<String> figureList = new ArrayList<>();

    private ObservableResourceFactory RESOURCE_FACTORY ;

    final String  findNoteByIdURL = "http://localhost:8093/api/note/";

    // Initialize
    public void initialize() {
        TokenStorage.getIntance();
        responseService = new HttpResponseServiceImpl();
        this.controllerUtils = new ControllerUtils();

        RESOURCE_FACTORY = ObservableResourceFactory.getInstance();

        RESOURCE_FACTORY.getResourceBundle();
        Platform.runLater(()-> super.updateDisplay());
        note = findNoteById(findNoteByIdURL, selectedReadOnlyNote.getId(), TokenStorage.getToken());

        assert note != null;
        textArea1.setText(note.getText());
        titleTextArea.setText(note.getTitle());
        figureList = note.getFigure();

        colorSetUp();
        groupSharingFetching();

        updateLocalTime(localTime);
        updateNameLabel(nameLabel, TokenStorage.getUser());

        Platform.runLater(() -> {
            figureList.forEach(figure -> {
                ImageView imageView = new ImageView();
                try {
                    Image image = new Image(new FileInputStream(figure));
                    imageView.setImage(image);
                    imageView.setFitHeight(200);
                    imageView.setFitWidth(200);
                    imageView.setPreserveRatio(true);
                    textVBox.getChildren().add(imageView);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
        });

        // Set sidebar language
        setSidebarLanguages(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn);
    }

    public void groupSharingSetUp(){
        groupName.setText(note.getGroup());

    }
    public void groupSharingFetching(){
        HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder("GET","http://localhost:8093/api/groups/my-groups",true);
        HttpRequestBase filterRequestHttp = httpRequestBuilder.getHttpRequestBase();
        CloseableHttpClient httpClient = httpRequestBuilder.getHttpClient();
        try
        {
            httpRequestBuilder.setRequestBody();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        responseService.handleReponse(filterRequestHttp,httpClient,this::handleGetOwnGroups);
    }

    private Stage stage;

    private void colorSetUp() {
        noteBackground.setFill(Color.web(note.getColor()));
        colorPicker.setText("");
    }
    private void handleGetOwnGroups(CloseableHttpResponse response, Object responseObject){
        System.out.println(response.getStatusLine().getStatusCode());
        if (response.getStatusLine().getStatusCode() == 200) {
            JSONArray jsonResponse = (JSONArray) responseObject;
            try {
                for(int i = 0; i < jsonResponse.length(); i++){
                    JSONObject jsonObject = jsonResponse.getJSONObject(i);
                    groupList.put(jsonObject.getInt("id"), jsonObject.getString("name"));
                }
                groupList.put(-1,"No Group");
                groupSharingSetUp();
            } catch (JSONException e) {
                System.out.println(e);
            }
        } else {
            JSONObject jsonResponse = (JSONObject) responseObject;
            if (response.getStatusLine().getStatusCode() == 404) {
                groupList.put(-1,"No Group");

            }
        }
    }
    // side bar button
    @FXML
    public void myGroupsBtnClick() {
        ControllerUtils_v2.goToMyGroupsPage(stage, myGroupsBtn);
    }

    // sidebar
    @FXML
    public void myNotesBtnClick() {
        ControllerUtils_v2.goToMyNotesPage(stage, myNotesBtn);
    }

    @FXML
    public void shareNotesBtnClick() {
        ControllerUtils_v2.goToMyGroupNotesPage(stage, shareNotesBtn);
    }

    @FXML
    public void allGroupsBtnClick() {
        ControllerUtils_v2.goToAllGroupsPage(stage, allGroupsBtn);
    }

    @FXML
    public void accountBtnClick() {
        ControllerUtils_v2.goToAccountPage(stage, accountBtn);
    }

    @FXML
    public void logOutBtnClick() {
        this.controllerUtils.logout(stage, logOutBtn);
    }

    @FXML
    void mouseEnter() {
        this.controllerUtils.setHandCursor(myNotesBtn);
        this.controllerUtils.setHandCursor(shareNotesBtn);
        this.controllerUtils.setHandCursor(myGroupsBtn);
        this.controllerUtils.setHandCursor(allGroupsBtn);
        this.controllerUtils.setHandCursor(accountBtn);
        this.controllerUtils.setHandCursor(logOutBtn);
        this.controllerUtils.setHandCursor(backToAllNotes);
    }

    @FXML
    void mouseExit() {
        this.controllerUtils.setDefaultCursor(myNotesBtn);
        this.controllerUtils.setDefaultCursor(shareNotesBtn);
        this.controllerUtils.setDefaultCursor(myGroupsBtn);
        this.controllerUtils.setDefaultCursor(allGroupsBtn);
        this.controllerUtils.setDefaultCursor(accountBtn);
        this.controllerUtils.setDefaultCursor(logOutBtn);
        this.controllerUtils.setDefaultCursor(backToAllNotes);
    }

    @FXML
    void backToAllNotesClick() {
        this.controllerUtils.goPage(stage, backToAllNotes, "/fxml/main_pages/groups/my_groups_notes.fxml");
    }



    @Override
    public void bindUIComponents() {
        titleTextArea.textProperty().bind(RESOURCE_FACTORY.getStringBinding("titleTextArea"));
        nameLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("welcomeText"));
        backToAllNotes.textProperty().bind(RESOURCE_FACTORY.getStringBinding("backToAllNotes"));
    }
}

