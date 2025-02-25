package controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.HttpRequestBuilder;
import model.Note;
import model.selected.SelectedNote;
import model.TokenStorage;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utils.ControllerUtils;
import utils.HttpResponseService;
import utils.HttpResponseServiceImpl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

import static utils.MainPageServices.*;

public class MainPageController {

    @FXML
    private Label localTime;
    @FXML
    private Label nameLabel;
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


    //side bar
    @FXML
    private Button myFileBtn;
    @FXML
    private Button shareNoteBtn;
    @FXML
    private Button favoriteBtn;
    @FXML
    private Button recyleBinBtn;
    @FXML
    private Button groupsBtn;
    @FXML
    private Button settingBtn;
    @FXML
    private Button accountBtn;
    @FXML
    private Button logOutBtn;
    @FXML
    private TextField searchBar;

    private HttpResponseService responseService;
    private ControllerUtils controllerUtils;
    private ObservableList<Note> notes;
    private ArrayList<Note> noteArrayList;


    public void initialize() {
        this.controllerUtils = new ControllerUtils();
        this.responseService = new HttpResponseServiceImpl();

        notes = FXCollections.observableArrayList();
        noteArrayList = findAllMyNotes("http://localhost:8093/api/note/", TokenStorage.getToken());
        if (noteArrayList != null) {
            notes.addAll(noteArrayList);
        } else {
            System.out.println("Connection failed");
        }

        table.setItems(notes);
        title.setCellValueFactory(new PropertyValueFactory<Note, String>("title"));
        group.setCellValueFactory(new PropertyValueFactory<Note, String>("group"));
        owner.setCellValueFactory(new PropertyValueFactory<Note, String>("owner"));
        category.setCellValueFactory(cellData -> {
            HashMap<Integer,String> catMap = cellData.getValue().getCategory();
            String categoriesListString = "";
            if (catMap != null && !catMap.isEmpty()) {
                categoriesListString = catMap.values().stream().collect(Collectors.joining(", "));
            }
            return new ReadOnlyStringWrapper(categoriesListString);
        });
        createTime.setCellValueFactory(new PropertyValueFactory<Note, String>("createdAt"));
        icon.setCellFactory(param -> new TableCell<Note, Void>() {
            private final ImageView imageView = new ImageView(
                    new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/icon/FileText.png")))
            );

            {
                imageView.setFitWidth(20);
                imageView.setFitHeight(20);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(imageView);
                }
            }
        });
        searchBarSetup();
        assert noteArrayList != null;
        updateRecentlyEdited(recentlyEditedHBox, noteArrayList);
        updateLocalTime(localTime);
        updateNameLabel(nameLabel, TokenStorage.getUser());
    }

    /*
    Go to another page
     */
    private Stage stage;
    private Scene scene;
    private Parent root;

    /*
    Click the content in table
    */
    public void tableClicked(MouseEvent event) throws IOException {
        int id = 0;
        if (event.getClickCount() == 1) {
            if (table.getSelectionModel().getSelectedItem() != null) {
                id = table.getSelectionModel().getSelectedItem().getId();
                System.out.println("id: " + id);
                System.out.println(table.getSelectionModel().getSelectedItem());

                SelectedNote selectedNote = SelectedNote.getInstance();
                selectedNote.setId(id);
                goToPage(stage, scene, event, "/fxml/main_pages/edit_note_page.fxml");
            }
        }
    }

    public void newNoteClicked(ActionEvent event) throws IOException {
        goToPage(stage, scene, event, "/fxml/main_pages/create_note_page.fxml");
    }

    public void groupsClicked(ActionEvent event) throws IOException {
        goToPage(stage, scene, event, "/fxml/main_pages/groups_page.fxml");
    }


    @FXML
    public void accountBtnClick() {

//        goToPage(stage, scene, event, "/fxml/main_pages/groups_page.fxml");
//        goToPage(stage, scene, event, "/fxml/main_pages/account_user_info_page.fxml");
        this.stage = controllerUtils.getStage(myFileBtn, this.stage);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main_pages/account_user_info_page.fxml"));
        this.controllerUtils.updateStage(this.stage, fxmlLoader);
    }

    @FXML
    public void logOutBtnClick() {
        this.controllerUtils.goToHelloPage(stage, logOutBtn);
    }

    @FXML
    void mouseEnter(MouseEvent event) {
        this.controllerUtils.setHandCursor(logOutBtn);
    }

    @FXML
    void mouseExit(MouseEvent event) {
        this.controllerUtils.setDefaultCursor(logOutBtn);
    }

    public void searchBarSetup() {
        searchBar.setOnKeyPressed(event -> {
            if (searchBar.isFocused() && event.getCode() == KeyCode.ENTER) {
                String inputText = searchBar.getText();
                HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder("POST","http://localhost:8093/api/note/search",true);
                JSONObject searchRequest= new JSONObject();
                JSONArray noteArray = new JSONArray();
                for(Note note : notes)
                {
                    JSONObject noteJson = new JSONObject();
                    noteJson.put("id", note.getId());
                    noteJson.put("title", note.getTitle());
                    noteArray.put(noteJson);
                }
                searchRequest.put("query", inputText);
                searchRequest.put("notes", noteArray);
                httpRequestBuilder.setJsonRequest(searchRequest);
                HttpRequestBase searchRequestHttp = httpRequestBuilder.getHttpRequest();
                CloseableHttpClient httpClient = httpRequestBuilder.getHttpClient();
                try
                {
                    httpRequestBuilder.setRequestBody();
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                responseService.handleReponse(searchRequestHttp,httpClient,this::handleGetSearchResults);
            }
        });
    }

    private void handleGetSearchResults(CloseableHttpResponse closeableHttpResponse, Object jsonArray) {
        try
        {
            System.out.println(closeableHttpResponse.getStatusLine().getStatusCode());
            System.out.println(jsonArray);
        }catch (JSONException e) {
        //String errMessage = (String) jsonObject.get("message");
    }
    }
}
