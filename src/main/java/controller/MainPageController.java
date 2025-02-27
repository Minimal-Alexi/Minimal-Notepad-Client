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
import java.util.*;
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


    // search bar
    @FXML
    private TextField searchBar;
    @FXML
    private ChoiceBox<String> filterChoice;

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

    private HttpResponseService responseService;
    private ControllerUtils controllerUtils;
    private ObservableList<Note> notes;
    private ArrayList<Note> noteArrayList;
    private HashMap<Integer,String> categoryList;


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
        filterChoiceSetup();
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

    private void searchBarSetup() {
        searchBar.setOnKeyPressed(event -> {
            if (searchBar.isFocused() && event.getCode() == KeyCode.ENTER) {
                performSearchOrFilter();
            }
        });
    }

    private void handleGetSearchResults(CloseableHttpResponse closeableHttpResponse, Object responseObject) {
        System.out.println(closeableHttpResponse.getStatusLine().getStatusCode());
        if(closeableHttpResponse.getStatusLine().getStatusCode() == 200) {
            JSONArray jsonResponse = (JSONArray) responseObject;
            try
            {
                System.out.println(closeableHttpResponse.getStatusLine().getStatusCode() +"\n" + jsonResponse);
                notes.clear();
                for(int i=0; i<jsonResponse.length(); i++)
                {
                    JSONObject result = (JSONObject) jsonResponse.get(i);
                    Note note = new Note(result.getInt("id"),
                            result.getString("title") ,
                            result.getString("text"),
                            result.getString("colour"),
                            timestampToString(result.getString("createdAt")),
                            timestampToString(result.getString("updatedAt")),
                            result.getJSONObject("user").getString("username"),
                            " ",
                            jsonArrayToHashMap(result.getJSONArray("categoriesList")));
                    notes.add(note);
                }
                // System.out.println(notes);
            }catch (JSONException e) {
                System.out.println(e);
            }
        }
        else
        {
            JSONObject jsonResponse = (JSONObject) responseObject;
            if(closeableHttpResponse.getStatusLine().getStatusCode() == 404)
            {
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
        for (Note note : noteArrayList)
        {
            categoryList.putAll(note.getCategory());
        }
        categoryList.put(-1,"No Category");
        filterChoice.getItems().addAll(categoryList.values());
        filterChoice.getItems().add("Any");
        filterChoice.getSelectionModel().select("Any");
        filterChoice.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null && !newValue.equals(oldValue))
            {
                performSearchOrFilter();
            }
        });
    }
    private void performSearchOrFilter(){
        String inputText = searchBar.getText();
        if(!inputText.isEmpty())
        {
            HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder("POST","http://localhost:8093/api/note/search",true);
            JSONObject searchRequest= new JSONObject();
            JSONArray noteArray = arrayInitializer(noteArrayList);
            searchRequest.put("query", inputText);
            searchRequest.put("notes", noteArray);
            requestBuilder(httpRequestBuilder, searchRequest);
        }
        else {
            notes.clear();
            notes.addAll(noteArrayList);
        }
        if(!filterChoice.getSelectionModel().getSelectedItem().equals("Any"))
        {
            HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder("POST","http://localhost:8093/api/note/filter",true);
            JSONObject filterRequest = new JSONObject();
            JSONArray noteArray = arrayInitializer(notes);
            JSONObject filterCategory = new JSONObject();
            int category = getCategoryViaFilter();
            if(category != -1)
            {
                filterCategory.put("id",getCategoryViaFilter());
                filterRequest.put("category",filterCategory);
            }
            else
            {
                filterRequest.put("category", JSONObject.NULL);
            }
            filterRequest.put("notes", noteArray);
            requestBuilder(httpRequestBuilder, filterRequest);
        }
    }
    private JSONArray arrayInitializer(List<Note> usedList) {
        JSONArray noteArray = new JSONArray();
        for(Note note : usedList)
        {
            JSONObject noteJson = new JSONObject();
            noteJson.put("id", note.getId());
            noteJson.put("title", note.getTitle());
            noteJson.put("text",note.getText());
            noteJson.put("colour",note.getColor());
            JSONObject userObject = new JSONObject();
            userObject.put("username",note.getOwner());
            noteJson.put("user", userObject);
            noteJson.put("createdAt", note.getCreatedAt());
            noteJson.put("updatedAt", note.getUpdatedAt());
            noteJson.put("categoriesList", hashMapToJSONArray(note.getCategory()));

            noteArray.put(noteJson);
        }
        return noteArray;
    }

    private void requestBuilder(HttpRequestBuilder httpRequestBuilder, JSONObject request) {
        httpRequestBuilder.setJsonRequest(request);
        HttpRequestBase filterRequestHttp = httpRequestBuilder.getHttpRequest();
        CloseableHttpClient httpClient = httpRequestBuilder.getHttpClient();
        try
        {
            httpRequestBuilder.setRequestBody();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        responseService.handleReponse(filterRequestHttp,httpClient,this::handleGetSearchResults);
    }

    private int getCategoryViaFilter(){
        for(Map.Entry<Integer,String> entry : categoryList.entrySet())
        {
            if(entry.getValue().equals(filterChoice.getSelectionModel().getSelectedItem()))
            {
                return entry.getKey();
            }
        }
        return -1;
    }
}
