package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.*;
import model.selected.SelectedNote;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import static utils.MainPageServices.*;
import static utils.NoteJson.JsonToNote;
import static utils.NoteJson.NoteToJson;

public class MainPageController extends PageController {

    @FXML private Label localTime;
    @FXML private Label nameLabel;

    // Note table
    @FXML private TableView<Note> table;
    @FXML private TableColumn<Note, Void> icon;
    @FXML private TableColumn<Note, String> title;
    @FXML private TableColumn<Note, String> group;
    @FXML private TableColumn<Note, String> owner;
    @FXML private TableColumn<Note, String> category;
    @FXML private TableColumn<Note, String> createTime;

    // Recently edited
    @FXML private HBox recentlyEditedHBox;

    // Search bar
    @FXML private TextField searchBar;
    @FXML private ChoiceBox<String> filterChoice;
    @FXML private Button searchReset;

    // Side bar
    @FXML private Button myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn;

    @FXML private Label recentlyEditedLabel, yourNotesLabel;
    @FXML private Button newNoteBtn;

    private boolean isResetting = false;
    private HttpResponseService responseService;
    private ControllerUtils controllerUtils;
    private ObservableList<Note> noteObservableList;
    private ArrayList<Note> noteArrayList;
    private HashMap<Integer, String> categoryList;

    private ObservableResourceFactory RESOURCE_FACTORY;

    // STRING KEY
    private final static String ANY_CATEGORY_OPTION = "anyCategoryOption";
    private final static String NOTES_KEY = "notes";
    private final static String CATEGORY_KEY = "category";


    public void initialize() {
        this.controllerUtils = new ControllerUtils();
        this.responseService = new HttpResponseServiceImpl();
        this.RESOURCE_FACTORY = ObservableResourceFactory.getInstance();

        noteObservableList = FXCollections.observableArrayList();
        noteArrayList = findAllMyNotes("http://localhost:8093/api/note/", TokenStorage.getToken());
        if (noteArrayList != null) {
            noteObservableList.addAll(noteArrayList);
        } else {
            System.out.println("Connection failed");
        }

        updateNoteTable(noteObservableList, table, title, group, owner, category, createTime, icon);
        if (noteArrayList != null) {
            updateRecentlyEdited(recentlyEditedHBox, noteArrayList, RESOURCE_FACTORY);
        }

        filterChoiceSetup();
        searchBarSetup();
        updateLocalTime(localTime);
        updateNameLabel(nameLabel, TokenStorage.getUser());

        setSidebarLanguages(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn);
        Platform.runLater(super::updateDisplay);
    }

    @FXML
    public void tableClicked(MouseEvent event) throws IOException {
        if (event.getClickCount() == 1 && table.getSelectionModel().getSelectedItem() != null) {
            int id = table.getSelectionModel().getSelectedItem().getId();
            SelectedNote.getInstance().setId(id);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            goToPage(stage, "/fxml/main_pages/edit_note_page.fxml");
        }
    }

    @FXML
    public void newNoteClicked(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        goToPage(stage, "/fxml/main_pages/create_note_page.fxml");
    }

    @FXML
    public void groupsClicked(ActionEvent event) throws IOException {
        controllerUtils.goPage((Stage) ((Node) event.getSource()).getScene().getWindow(), myGroupsBtn,
                "/fxml/main_pages/groups/group_info_create_group.fxml");
    }

    // Sidebar navigation
    @FXML public void myGroupsBtnClick() { ControllerUtils_v2.goToMyGroupsPage(getStage(myGroupsBtn), myGroupsBtn); }
    @FXML public void myNotesBtnClick() { ControllerUtils_v2.goToMyNotesPage(getStage(myNotesBtn), myNotesBtn); }
    @FXML public void shareNotesBtnClick() { ControllerUtils_v2.goToMyGroupNotesPage(getStage(shareNotesBtn), shareNotesBtn); }
    @FXML public void allGroupsBtnClick() { ControllerUtils_v2.goToAllGroupsPage(getStage(allGroupsBtn), allGroupsBtn); }
    @FXML public void accountBtnClick() { ControllerUtils_v2.goToAccountPage(getStage(accountBtn), accountBtn); }
    @FXML public void logOutBtnClick() { controllerUtils.logout(getStage(logOutBtn), logOutBtn); }

    // Cursor styling
    @FXML void mouseEnter() { setCursorStyle(true); }
    @FXML void mouseExit() { setCursorStyle(false); }

    private void setCursorStyle(boolean hand) {
        Button[] buttons = { myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn };
        for (Button btn : buttons) {
            if (hand) controllerUtils.setHandCursor(btn);
            else controllerUtils.setDefaultCursor(btn);
        }
    }

    private void searchBarSetup() {
        searchBar.setOnKeyPressed(event -> {
            if (searchBar.isFocused() && event.getCode() == KeyCode.ENTER && !isResetting) {
                performSearch();
            }
        });

        searchReset.setOnAction(event -> {
            isResetting = true;
            searchBar.setText("");
            filterChoice.getSelectionModel().select(RESOURCE_FACTORY.getString(ANY_CATEGORY_OPTION));
            isResetting = false;
            noteObservableList.setAll(noteArrayList);
        });
    }

    private void filterChoiceSetup() {
        categoryList = new HashMap<>();
        for (Note note : noteArrayList) categoryList.putAll(note.getCategory());
        categoryList.put(-1, RESOURCE_FACTORY.getString("noCategoryOption"));

        filterChoice.getItems().addAll(categoryList.values());
        filterChoice.getItems().add(RESOURCE_FACTORY.getString(ANY_CATEGORY_OPTION));
        filterChoice.getSelectionModel().select(RESOURCE_FACTORY.getString(ANY_CATEGORY_OPTION));

        filterChoice.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(oldVal) && !isResetting) {
                performFilter();
            }
        });
    }

    private void performSearch() {
        String inputText = searchBar.getText();
        if (!inputText.isEmpty()) {
            JSONObject searchRequest = new JSONObject();
            searchRequest.put("query", inputText);
            searchRequest.put(NOTES_KEY, arrayInitializer(noteObservableList));

            HttpRequestBuilder builder = new HttpRequestBuilder("POST", "http://localhost:8093/api/note/search", true);
            builder.setJsonRequest(searchRequest);
            buildAndSendRequest(builder);
        } else {
            noteObservableList.setAll(noteArrayList);
            performFilter();
        }
    }

    private void performFilter() {
        JSONObject filterRequest = new JSONObject();
        filterRequest.put(NOTES_KEY, arrayInitializer(noteObservableList));

        int category = getCategoryViaFilter();
        if (category != -1) {
            JSONObject catObj = new JSONObject();
            catObj.put("id", category);
            filterRequest.put(CATEGORY_KEY, catObj);
        } else {
            filterRequest.put(CATEGORY_KEY, JSONObject.NULL);
        }

        HttpRequestBuilder builder = new HttpRequestBuilder("POST", "http://localhost:8093/api/note/filter", true);
        builder.setJsonRequest(filterRequest);
        buildAndSendRequest(builder);
    }


    private int getCategoryViaFilter() {
        return categoryList.entrySet().stream()
                .filter(e -> e.getValue().equals(filterChoice.getSelectionModel().getSelectedItem()))
                .map(Map.Entry::getKey)
                .findFirst().orElse(-1);
    }

    private void buildAndSendRequest(HttpRequestBuilder builder) {
        try {
            builder.setRequestBody();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        responseService.handleReponse(
                builder.getHttpRequestBase(),
                builder.getHttpClient(),
                this::handleGetSearchResults
        );
    }

    private void handleGetSearchResults(CloseableHttpResponse response, Object responseObject) {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            JSONArray results = (JSONArray) responseObject;
            noteObservableList.clear();
            for (int i = 0; i < results.length(); i++) {
                noteObservableList.add(JsonToNote(results.getJSONObject(i)));
            }
        } else if (statusCode == 404) {
            JSONObject jsonResponse = (JSONObject) responseObject;
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(jsonResponse.getString("message"));
            alert.showAndWait();
        }
    }

    private Stage getStage(Button button) {
        return (Stage) button.getScene().getWindow();
    }

    @Override
    public void bindUIComponents() {
        recentlyEditedLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("edited"));
        newNoteBtn.textProperty().bind(RESOURCE_FACTORY.getStringBinding("newNote"));
        searchReset.textProperty().bind(RESOURCE_FACTORY.getStringBinding("reset"));
        searchBar.promptTextProperty().bind(RESOURCE_FACTORY.getStringBinding("searchBar"));
        yourNotesLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("yourNotes"));
        title.textProperty().bind(RESOURCE_FACTORY.getStringBinding("titleColName"));
        group.textProperty().bind(RESOURCE_FACTORY.getStringBinding("groupColName"));
        owner.textProperty().bind(RESOURCE_FACTORY.getStringBinding("OwnerColName"));
        category.textProperty().bind(RESOURCE_FACTORY.getStringBinding("CategoryColName"));
        createTime.textProperty().bind(RESOURCE_FACTORY.getStringBinding("CreateTimeColName"));
    }
}