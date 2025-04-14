package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.HttpRequestBuilder;
import model.Note;
import model.TokenStorage;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utils.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static utils.MainPageServices.*;
import static utils.NoteServices.*;


public class CreateNoteController {

    @FXML
    private Label localTime;
    @FXML
    private Label nameLabel;
    @FXML
    private VBox noteBackground;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private VBox textVBox;
    @FXML
    private TextField titleTextArea;
    @FXML
    private TextArea textArea1;
    @FXML
    private Button saveNoteBtn;
    @FXML
    private HBox categoryHBox;
    @FXML
    private Label addCategory;
    @FXML
    private Button uploadPicBtn;
    @FXML private ChoiceBox<String> groupSharingChoiceBox;

    private HttpResponseService responseService;

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

    private final HashMap<Integer, String> categoryList = new HashMap<>();
    private final ArrayList<String> figureList = new ArrayList<>();
    private final HashMap<Integer, String> groupList = new HashMap<>();

    private ControllerUtils controllerUtils;



    public void initialize() {
        responseService = new HttpResponseServiceImpl();
        this.controllerUtils = new ControllerUtils();

        updateLocalTime(localTime);
        updateNameLabel(nameLabel, TokenStorage.getUser());
        colorSetUp();
        groupSharingFetching();

        // set sidebar language
        setSidebarLanguages(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn);
    }

    public void saveNoteClicked(ActionEvent event) throws IOException {
        //Disable the button
        saveNoteBtn.setDisable(true);
        Note note = new Note(0, titleTextArea.getText(), textArea1.getText(), colorPicker.getValue().toString(), "1970-01-01", "1970-01-01", TokenStorage.getUser(),getGroupId() ,getGroupName(), categoryList, figureList);
        NoteServices.createNote("http://localhost:8093/api/note/", note, TokenStorage.getToken());
        goToPage(event, "/fxml/main_pages/main_page.fxml");
    }

    public void uploadPicClicked() {
        uploadPictureLocal(uploadPicBtn, figureList, textVBox);
    }

    /*
    Go to another page
     */
    private Stage stage;
    private Scene scene;

    // sidebar
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


    // ******************************
    // * new codes are below here *
    // ******************************

    /*
    Click to add a Category
     */
    public void addCategoryClicked(MouseEvent mouseEvent) {
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
    }

    /*

     */
    private void colorSetUp() {
        noteBackground.setStyle("-fx-background-color:" + Color.WHITE + ";");

        colorPicker.setOnAction(event -> {
            Color selectedColor = colorPicker.getValue();
            // Transform color into rgba code
            String hexColor = String.format("#%02X%02X%02X",
                    (int) (selectedColor.getRed() * 255),
                    (int) (selectedColor.getGreen() * 255),
                    (int) (selectedColor.getBlue() * 255));

            // Implement new css to make the radius work
            noteBackground.setStyle("-fx-background-color: " + hexColor + ";");
        });
    }
    private int getGroupId(){
        for(Map.Entry<Integer,String> entry:groupList.entrySet())
        {
            if(entry.getValue().equals(groupSharingChoiceBox.getValue()))
            {
                return entry.getKey();
            }
        }
        return -1;
    }
    private String getGroupName(){
        return groupSharingChoiceBox.getValue();
    }
    public void groupSharingSetUp(){
        groupSharingChoiceBox.getItems().addAll(groupList.values());
        groupSharingChoiceBox.getSelectionModel().select("No Group");
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
            if (response.getStatusLine().getStatusCode() == 404) {
                groupList.put(-1,"No Group");
            }
        }
    }
}
