package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.*;
import model.selected.SelectedNote;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static utils.MainPageServices.*;
import static utils.NoteServices.*;

public class EditNoteController extends PageController {

    @FXML private ChoiceBox<String> groupSharingChoiceBox;
    @FXML private Label localTime;
    @FXML private Label nameLabel;
    @FXML private Label editingNoteLabel;
    @FXML private Label categoriesId;
    @FXML private Label GroupsId;
    @FXML private VBox textVBox;
    @FXML private TextField titleTextArea;
    @FXML private TextArea textArea1;
    @FXML private Button saveNoteBtn;
    @FXML private Button deleteNoteBtn;
    @FXML private Button uploadPicBtn;
    @FXML private HBox categoryHBox;
    @FXML private Label addCategory;
    @FXML private ColorPicker colorPicker;
    @FXML private Rectangle noteBackground;
    @FXML private Text welcomeText;
    @FXML private Button myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn;

    private ControllerUtils controllerUtils;
    private ObservableResourceFactory RESOURCE_FACTORY;
    private HttpResponseService responseService;
    private SelectedNote selectedNote = SelectedNote.getInstance();
    private Note note;
    private HashMap<Integer, String> categoryList = new HashMap<>();
    private HashMap<Integer, String> groupList = new HashMap<>();
    private ArrayList<String> figureList = new ArrayList<>();
    private Stage stage;
    private Scene scene;

    public void initialize() {
        TokenStorage.getIntance();
        responseService = new HttpResponseServiceImpl();
        controllerUtils = new ControllerUtils();

        String noteUrl = "http://localhost:8093/api/note/";
        note = findNoteById(noteUrl, selectedNote.getId(), TokenStorage.getToken());
        RESOURCE_FACTORY = ObservableResourceFactory.getInstance();
        RESOURCE_FACTORY.getResourceBundle();

        Platform.runLater(super::updateDisplay);

        if (note == null) return;

        titleTextArea.setText(note.getTitle());
        textArea1.setText(note.getText());
        categoryList = note.getCategory();
        figureList = note.getFigure();

        colorSetUp();
        groupSharingFetching();
        updateCategory(categoryList, categoryHBox);
        updateLocalTime(localTime);
        updateNameLabel(nameLabel, TokenStorage.getUser());

        Platform.runLater(() -> figureList.forEach(figure -> {
                try {
                    ImageView imageView = new ImageView(new Image(new FileInputStream(figure)));
                    imageView.setFitHeight(200);
                    imageView.setFitWidth(200);
                    imageView.setPreserveRatio(true);
                    textVBox.getChildren().add(imageView);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }));

        setSidebarLanguages(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn);
    }

    public void saveNoteClicked(ActionEvent event) throws IOException {
        saveNoteBtn.setDisable(true);
        Note updatedNote = new Note(
                selectedNote.getId(),
                titleTextArea.getText(),
                textArea1.getText(),
                colorPicker.getValue().toString(),
                note.getCreatedAt(),
                note.getUpdatedAt(),
                TokenStorage.getUser(),
                getGroupId(),
                getGroupName(),
                categoryList,
                figureList
        );

        updateNote("http://localhost:8093/api/note/", selectedNote.getId(), TokenStorage.getToken(), updatedNote);
        goToPage(stage, scene, event, "/fxml/main_pages/main_page.fxml");
    }

    public void deleteNoteClicked(ActionEvent event) throws IOException {
        deleteNoteBtn.setDisable(true);
        deleteNoteById("http://localhost:8093/api/note/", selectedNote.getId(), TokenStorage.getToken());
        goToPage(stage, scene, event, "/fxml/main_pages/main_page.fxml");
    }

    public void addCategoryClicked(MouseEvent mouseEvent) {
        addCategory.setDisable(true);
        HashMap<Integer, String> categories = getAllCategories("http://localhost:8093/api/categories", TokenStorage.getToken());

        ContextMenu contextMenu = new ContextMenu();
        if (categories != null) {
            addCategory(categories, categoryList, categoryHBox, contextMenu);
            if (!contextMenu.isShowing()) {
                contextMenu.show(addCategory, mouseEvent.getScreenX(), mouseEvent.getScreenY());
            } else {
                contextMenu.hide();
            }
        }
        addCategory.setDisable(false);
    }

    public void groupSharingFetching() {
        HttpRequestBuilder builder = new HttpRequestBuilder("GET", "http://localhost:8093/api/groups/my-groups", true);
        HttpRequestBase request = builder.getHttpRequestBase();
        try {
            builder.setRequestBody();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        responseService.handleReponse(request, builder.getHttpClient(), this::handleGetOwnGroups);
    }

    private void handleGetOwnGroups(CloseableHttpResponse response, Object responseObject) {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            JSONArray array = (JSONArray) responseObject;
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                groupList.put(obj.getInt("id"), obj.getString("name"));
            }
            groupList.put(-1, "No Group");
            groupSharingSetUp();
        } else if (statusCode == 404) {
            groupList.put(-1, "No Group");
        }
    }

    public void groupSharingSetUp() {
        groupSharingChoiceBox.getItems().addAll(groupList.values());
        if (note.getGroupId() == -1) {
            groupSharingChoiceBox.getSelectionModel().select("No Group");
        } else {
            groupSharingChoiceBox.getSelectionModel().select(note.getGroup());
        }
    }

    public void uploadPicClicked() {
        uploadPictureLocal(uploadPicBtn, figureList, textVBox);
    }

    private void colorSetUp() {
        noteBackground.setFill(Color.web(note.getColor()));
        colorPicker.setValue(Color.web(note.getColor()));
        colorPicker.setOnAction(e -> noteBackground.setFill(colorPicker.getValue()));
    }

    private int getGroupId() {
        return groupList.entrySet().stream()
                .filter(entry -> entry.getValue().equals(groupSharingChoiceBox.getValue()))
                .map(Map.Entry::getKey)
                .findFirst().orElse(-1);
    }

    private String getGroupName() {
        return groupSharingChoiceBox.getValue();
    }

    public String getLocalizedActionColOneName() {
        return RESOURCE_FACTORY.getResourceBundle().getString("actionColOneName");
    }

    // Sidebar Navigation
    @FXML public void myNotesBtnClick() { ControllerUtils_v2.goToMyNotesPage(stage, myNotesBtn); }
    @FXML public void shareNotesBtnClick() { ControllerUtils_v2.goToMyGroupNotesPage(stage, shareNotesBtn); }
    @FXML public void myGroupsBtnClick() { ControllerUtils_v2.goToMyGroupsPage(stage, myGroupsBtn); }
    @FXML public void allGroupsBtnClick() { ControllerUtils_v2.goToAllGroupsPage(stage, allGroupsBtn); }
    @FXML public void accountBtnClick() { ControllerUtils_v2.goToAccountPage(stage, accountBtn); }
    @FXML public void logOutBtnClick() { controllerUtils.logout(stage, logOutBtn); }

    @FXML void mouseEnter() {
        List<Button> buttons = List.of(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn);
        buttons.forEach(controllerUtils::setHandCursor);
    }

    @FXML void mouseExit() {
        List<Button> buttons = List.of(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn);
        buttons.forEach(controllerUtils::setDefaultCursor);
    }

    @Override
    public void bindUIComponents() {
        editingNoteLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("editingNoteLabel"));
        saveNoteBtn.textProperty().bind(RESOURCE_FACTORY.getStringBinding("saveNoteBtn"));
        deleteNoteBtn.textProperty().bind(RESOURCE_FACTORY.getStringBinding("deleteNoteBtn"));
        titleTextArea.promptTextProperty().bind(RESOURCE_FACTORY.getStringBinding("titleTextArea"));
        uploadPicBtn.textProperty().bind(RESOURCE_FACTORY.getStringBinding("uploadPicBtn"));
        categoriesId.textProperty().bind(RESOURCE_FACTORY.getStringBinding("categoriesId"));
        GroupsId.textProperty().bind(RESOURCE_FACTORY.getStringBinding("GroupsId"));
    }
}
