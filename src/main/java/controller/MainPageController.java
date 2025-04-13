package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.*;
import model.selected.SelectedNote;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utils.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

import static utils.MainPageServices.*;
import static utils.NoteJson.JsonToNote;
import static utils.NoteJson.NoteToJson;

public class MainPageController extends PageController {

    @FXML
    private Label localTime;
    @FXML
    private Label nameLabel;
    // Note table
    @FXML
    private TableView<Note> table;
    @FXML
    private TableColumn<Note, Void> icon;
    @FXML
    private TableColumn<Note, String> title;
    @FXML
    private TableColumn<Note, String> group;
    @FXML
    private TableColumn<Note, String> owner;
    @FXML
    private TableColumn<Note, String> category;
    @FXML
    private TableColumn<Note, String> createTime;
    // Recently edited
    @FXML
    private HBox recentlyEditedHBox;


    // search bar
    @FXML
    private TextField searchBar;
    @FXML
    private ChoiceBox<String> filterChoice;
    @FXML
    private Button searchReset;

    private boolean isResetting = false;

    //side bar
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

    @FXML
    private Label recentlyEditedLabel;
    @FXML
    private Label yourNotesLabel;
    @FXML
    private Button newNoteBtn;


    private HttpResponseService responseService;
    private ControllerUtils controllerUtils;
    private ObservableList<Note> noteObservableList;
    private ArrayList<Note> noteArrayList;
    private HashMap<Integer, String> categoryList;

    // set language
    private ObservableResourceFactory RESOURCE_FACTORY;

    // STRING KEY
    private final static String ANY_CATEGORY_OPTION = "anyCategoryOption";

    public void initialize() {
        this.controllerUtils = new ControllerUtils();
        this.responseService = new HttpResponseServiceImpl();

        RESOURCE_FACTORY = ObservableResourceFactory.getInstance();

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

        // set sidebar language
        setSidebarLanguages(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn);
        // localize happen
        Platform.runLater(super::updateDisplay);
    }

    /*
    Go to another page
     */
    private Stage stage;
    private Scene scene;

    /*
    Click the content in table
    */
    public void tableClicked(MouseEvent event) throws IOException {
        int id = 0;
        if (event.getClickCount() == 1 && table.getSelectionModel().getSelectedItem() != null) {
            id = table.getSelectionModel().getSelectedItem().getId();
            System.out.println("id: " + id);
            System.out.println(table.getSelectionModel().getSelectedItem());

            SelectedNote selectedNote = SelectedNote.getInstance();
            selectedNote.setId(id);
            goToPage(stage, scene, event, "/fxml/main_pages/edit_note_page.fxml");
        }

    }

    public void newNoteClicked(ActionEvent event) throws IOException {
        goToPage(stage, scene, event, "/fxml/main_pages/create_note_page.fxml");
    }

    // go to myGroupPage
    public void groupsClicked(ActionEvent event) throws IOException {
        String pageLink = "/fxml/main_pages/groups/group_info_create_group.fxml";
        this.controllerUtils.goPage(stage, myGroupsBtn, pageLink);
    }

    // side bar button
    @FXML
    public void myGroupsBtnClick() {
        ControllerUtils_v2.goToMyGroupsPage(stage, myGroupsBtn);
    }

    @FXML
    public void myNotesBtnClick() {
        ControllerUtils_v2.goToMyNotesPage(stage, myNotesBtn);
    }

    @FXML
    public void shareNotesBtnClick() {
        System.out.println("Go to share notes page");
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
    }

    @FXML
    void mouseExit() {
        this.controllerUtils.setDefaultCursor(myNotesBtn);
        this.controllerUtils.setDefaultCursor(shareNotesBtn);
        this.controllerUtils.setDefaultCursor(myGroupsBtn);
        this.controllerUtils.setDefaultCursor(allGroupsBtn);
        this.controllerUtils.setDefaultCursor(accountBtn);
        this.controllerUtils.setDefaultCursor(logOutBtn);
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
            noteObservableList.clear();
            noteObservableList.addAll(noteArrayList);
        });
    }

    private void handleGetSearchResults(CloseableHttpResponse response, Object responseObject) {
        System.out.println(response.getStatusLine().getStatusCode());
        if (response.getStatusLine().getStatusCode() == 200) {
            JSONArray jsonResponse = (JSONArray) responseObject;
            try {
                System.out.println(response.getStatusLine().getStatusCode() + "\n" + jsonResponse);
                noteObservableList.clear();
                for (int i = 0; i < jsonResponse.length(); i++) {
                    JSONObject result = (JSONObject) jsonResponse.get(i);
                    Note note = JsonToNote(result);
                    noteObservableList.add(note);
                }
            } catch (JSONException e) {
                System.out.println(e);
            }
        } else {
            JSONObject jsonResponse = (JSONObject) responseObject;
            if (response.getStatusLine().getStatusCode() == 404) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText(jsonResponse.getString("message"));
                alert.showAndWait();
            }
        }
    }

    private void filterChoiceSetup() {
        categoryList = new HashMap<>();
        for (Note note : noteArrayList) {
            categoryList.putAll(note.getCategory());
        }
        categoryList.put(-1, RESOURCE_FACTORY.getString("noCategoryOption"));
        filterChoice.getItems().addAll(categoryList.values());
        filterChoice.getItems().add(RESOURCE_FACTORY.getString(ANY_CATEGORY_OPTION));
        filterChoice.getSelectionModel().select(RESOURCE_FACTORY.getString(ANY_CATEGORY_OPTION));
        filterChoice.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(oldValue) && !isResetting) {
                performFilter();
            }
        });
    }

    private void performSearch() {
        String inputText = searchBar.getText();
        if (!inputText.isEmpty()) {
            HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder("POST", "http://localhost:8093/api/note/search", true);
            JSONObject searchRequest = new JSONObject();
            JSONArray noteArray = arrayInitializer(noteObservableList);
            searchRequest.put("query", inputText);
            searchRequest.put("notes", noteArray);
            requestBuilder(httpRequestBuilder, searchRequest);
        } else {
            noteObservableList.clear();
            noteObservableList.addAll(noteArrayList);
            performFilter();
        }
    }

    private JSONArray arrayInitializer(List<Note> usedList) {
        JSONArray noteArray = new JSONArray();
        for (Note note : usedList) {
            JSONObject noteJson = NoteToJson(note);
            noteArray.put(noteJson);
        }
        return noteArray;
    }

    private void requestBuilder(HttpRequestBuilder httpRequestBuilder, JSONObject request) {
        httpRequestBuilder.setJsonRequest(request);
        HttpRequestBase filterRequestHttp = httpRequestBuilder.getHttpRequestBase();
        CloseableHttpClient httpClient = httpRequestBuilder.getHttpClient();
        try {
            httpRequestBuilder.setRequestBody();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        responseService.handleReponse(filterRequestHttp, httpClient, this::handleGetSearchResults);
    }

    private void performFilter() {
        HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder("POST", "http://localhost:8093/api/note/filter", true);
        JSONObject filterRequest = new JSONObject();
        JSONArray noteArray = arrayInitializer(noteObservableList);
        JSONObject filterCategory = new JSONObject();

        int category = getCategoryViaFilter();
        if (category != -1) {
            filterCategory.put("id", category);
            filterRequest.put("category", filterCategory);
        } else {
            filterRequest.put("category", JSONObject.NULL);
        }
        filterRequest.put("notes", noteArray);
        requestBuilder(httpRequestBuilder, filterRequest);
    }


    private int getCategoryViaFilter() {
        for (Map.Entry<Integer, String> entry : categoryList.entrySet()) {
            if (entry.getValue().equals(filterChoice.getSelectionModel().getSelectedItem())) {
                return entry.getKey();
            }
        }
        return -1;
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
