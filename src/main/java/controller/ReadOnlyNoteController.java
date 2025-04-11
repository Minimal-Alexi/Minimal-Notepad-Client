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


    @FXML private ChoiceBox<String> groupName1;
    @FXML
    private Label groupName;
    @FXML
    private Label localTime;
    @FXML
    private Label nameLabel;
    @FXML
    private VBox textVBox;
    @FXML
    private TextField titleTextArea1;
    @FXML
    private Label titleTextArea;
    @FXML
    private TextArea textArea1;

    @FXML
    private Button saveNoteBtn;
    @FXML
    private Button deleteNoteBtn;
    @FXML
    private HBox categoryHBox;
    @FXML
    private Label addCategory;
    @FXML
    private Button uploadPicBtn;
    @FXML
    private Label colorPicker;
    @FXML
    private ColorPicker colorPicker1;
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
    //private HashMap<Integer, String> categoryList = new HashMap<>();
    private HashMap<Integer, String> groupList = new HashMap<>();
    private ArrayList<String> figureList = new ArrayList<>();

    private ObservableResourceFactory RESOURCE_FACTORY ;

    // Initialize
    public void initialize() {
        TokenStorage.getIntance();
        responseService = new HttpResponseServiceImpl();
        this.controllerUtils = new ControllerUtils();

        RESOURCE_FACTORY = ObservableResourceFactory.getInstance();

        RESOURCE_FACTORY.getResourceBundle();
        Platform.runLater(()-> super.updateDisplay());

        System.out.println(selectedReadOnlyNote.getId());

        note = findNoteById("http://localhost:8093/api/note/", selectedReadOnlyNote.getId(), TokenStorage.getToken());

        assert note != null;
        textArea1.setText(note.getText());
        titleTextArea.setText(note.getTitle());
        //categoryList = note.getCategory();
        figureList = note.getFigure();

        colorSetUp();
        groupSharingFetching();

/*        // query the categoryList to add categories to the ui
        updateCategory(categoryList, categoryHBox);*/

        updateLocalTime(localTime);
        updateNameLabel(nameLabel, TokenStorage.getUser());


        // add pictures to the ui
        /*
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

         */
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

/*    public void saveNoteClicked(ActionEvent event) throws IOException {
        //Disable the button
        saveNoteBtn.setDisable(true);
        Note updatedNote= new Note(selectedReadOnlyNote.getId(), titleTextArea.getText(), textArea1.getText(), colorPicker.getValue().toString(), note.getCreatedAt(), note.getUpdatedAt(), TokenStorage.getUser(), getGroupId(), getGroupName(), categoryList, figureList);
        NoteServices.updateNote("http://localhost:8093/api/note/", selectedReadOnlyNote.getId(), TokenStorage.getToken(), updatedNote);
        goToPage(stage, scene, event, "/fxml/main_pages/main_page.fxml");
    }

    public void deleteNoteClicked(ActionEvent event) throws IOException {
        //Disable the button
        deleteNoteBtn.setDisable(true);
        NoteServices.deleteNoteById("http://localhost:8093/api/note/", selectedReadOnlyNote.getId(), TokenStorage.getToken());
        goToPage(stage, scene, event, "/fxml/main_pages/main_page.fxml");
    }*/

    /*public void addCategoryClicked(MouseEvent mouseEvent) {
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

    }*/
    public void groupSharingSetUp(){
        groupName.setText(note.getGroup());
        /*groupName.getItems().addAll(groupList.values());
        if(note.getGroupId() == -1)
        {
            groupName.getSelectionModel().select("No Group");
        }
        else
        {
            groupName.getSelectionModel().select(note.getGroup());
        }*/
    }
    public void groupSharingFetching(){
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
/*
    public void uploadPicClicked(MouseEvent mouseEvent) throws IOException {
        uploadPictureLocal(uploadPicBtn, figureList, textVBox);
    }*/

    /*
    Go to another page
     */
    private Stage stage;
    private Scene scene;
    private Parent root;

//    public void groupsClicked(ActionEvent event) throws IOException {
//        goToPage(stage, scene, event, "/fxml/main_pages/groups_page.fxml");
//    }

    private void colorSetUp() {
        noteBackground.setFill(Color.web(note.getColor()));
        String colorCode = Color.web(note.getColor()).toString();
        colorPicker.setText("");
        //colorPicker.setStyle("-fx-text-fill: #" + "0xffccccff" + ";");
/*        colorPicker.setValue(Color.web(note.getColor()));
        colorPicker.setOnAction(event -> {
            noteBackground.setFill(colorPicker.getValue());
        });*/
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
//                Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                alert.setTitle("Error");
//                alert.setHeaderText(null);
//                alert.setContentText(jsonResponse.getString("message"));
//                alert.showAndWait();
            }
        }
    }
/*    private int getGroupId(){
        for(Map.Entry<Integer,String> entry:groupList.entrySet())
        {
            if(entry.getValue().equals(groupName.getValue()))
            {
                return entry.getKey();
            }
        }
        return -1;
    }*/
//    private String getGroupName(){
//        return groupName.getValue();
//    }

    // side bar button
    @FXML
    public void myGroupsBtnClick() {
//        this.controllerUtils.goPage(stage, myGroupsBtn, "/fxml/main_pages/groups/my_groups.fxml");
        ControllerUtils_v2.goToMyGroupsPage(stage, myGroupsBtn);
    }

    // sidebar

    @FXML
    public void myNotesBtnClick() {

//        this.controllerUtils.goPage(stage, myNotesBtn, "/fxml/main_pages/main_page.fxml");
        ControllerUtils_v2.goToMyNotesPage(stage, myNotesBtn);
    }

    @FXML
    public void shareNotesBtnClick() {
//        this.controllerUtils.goPage(stage,shareNoteBtn,"");
        System.out.println("Go to share notes page");
//        this.controllerUtils.goPage(stage, allGroupsBtn, "/fxml/main_pages/groups/my_groups_notes.fxml");
        ControllerUtils_v2.goToMyGroupNotesPage(stage, shareNotesBtn);
    }

    @FXML
    public void allGroupsBtnClick() {
//        this.controllerUtils.goPage(stage, allGroupsBtn, "/fxml/main_pages/groups/all_groups.fxml");
        ControllerUtils_v2.goToAllGroupsPage(stage, allGroupsBtn);
    }

    @FXML
    public void accountBtnClick() {
//        this.controllerUtils.goPage(stage, accountBtn, "/fxml/main_pages/account_user_info_page.fxml");
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
    public void updateAllUIComponents() {
    }

    @Override
    public void bindUIComponents() {
        titleTextArea.textProperty().bind(RESOURCE_FACTORY.getStringBinding("titleTextArea"));
        nameLabel.textProperty().bind(RESOURCE_FACTORY.getStringBinding("welcomeText"));
        backToAllNotes.textProperty().bind(RESOURCE_FACTORY.getStringBinding("backToAllNotes"));


    }
}

