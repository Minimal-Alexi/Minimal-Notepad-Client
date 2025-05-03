package controller.groups;


import controller.PageController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.HttpRequestBuilder;
import model.ObservableResourceFactory;
import model.TokenStorage;
import model.selected.SelectedGroup;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import utils.*;
import java.io.IOException;
import java.util.ResourceBundle;
import static utils.MainPageServices.setSidebarLanguages;
import static utils.MainPageServices.updateNameLabel;

public class GroupInfoCreateController extends PageController {

    // FXML UI Elements
    @FXML private BorderPane root;
    @FXML private Button myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn;
    @FXML private Button createGroupBtn;
    @FXML private Label localTime, nameLabel;
    @FXML private TextField groupNameInput, groupDescInput;
    @FXML private Label groupNameErrLabel, groupDescErrLabel;
    @FXML private Label createNewGroupLabel, createGroupNameLabel, createGroupDescLabel;

    // Internal Properties
    private Stage stage;
    private Scene scene;
    private Parent parent;
    private ControllerUtils controllerUtils;
    private HttpResponseService httpResponseService;
    private ObservableResourceFactory resourceFactory;

    // Constants
    private static final String URI = "http://localhost:8093/api/groups";
    private static final String CSS_SOURCE = "/CSS";
    private static final String FXML_SOURCE = "/fxml";

    public void initialize() {

        controllerUtils = new ControllerUtils();
        httpResponseService = new HttpResponseServiceImpl();
        resourceFactory = ObservableResourceFactory.getInstance();

        try {
            SelectedGroup selectedGroup = SelectedGroup.getInstance();
        } catch (NullPointerException e) {
            System.err.println("No selected group found.");
        }

        setupUI();
        setSidebarLanguages(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn);
        super.updateDisplay();
    }

    private void setupUI() {
        TokenStorage.getIntance();
        updateNameLabel(nameLabel, TokenStorage.getUser());
        MainPageServices.updateLocalTime(localTime);

        ControllerUtils_v2.addStyle(root,  "/button.css");
        ControllerUtils_v2.addStyle(root,  "/text_input.css");
        ControllerUtils_v2.addStyle(createGroupBtn,  "/groups.css");
        ControllerUtils_v2.addStyle(logOutBtn,  "/logout-button.css");
    }

    // Sidebar Navigation
    public void myGroupsBtnClick() { ControllerUtils_v2.goToMyGroupsPage(stage, myGroupsBtn); }
    public void myNotesBtnClick() { ControllerUtils_v2.goToMyNotesPage(stage, myNotesBtn); }
    public void shareNotesBtnClick() { ControllerUtils_v2.goToMyGroupNotesPage(stage, shareNotesBtn); }
    public void allGroupsBtnClick() { ControllerUtils_v2.goToAllGroupsPage(stage, allGroupsBtn); }
    public void accountBtnClick() { ControllerUtils_v2.goToAccountPage(stage, accountBtn); }
    public void logOutBtnClick() { ControllerUtils_v2.logout(stage, logOutBtn); }

    @FXML
    void mouseEnter() {
        controllerUtils.setHandCursor(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn, createGroupBtn);
    }

    @FXML
    void mouseExit() {
        controllerUtils.setDefaultCursor(myNotesBtn, shareNotesBtn, myGroupsBtn, allGroupsBtn, accountBtn, logOutBtn, createGroupBtn);
    }

    @FXML
    public void createGroupBtnClick() {
        String groupName = groupNameInput.getText();
        String groupDesc = groupDescInput.getText();

        clearErrorLabels();

        if (validInputs(groupName, groupDesc)) {
            try {
                createGroup(groupName, groupDesc);
            } catch (IOException e) {
                System.err.println("Error creating group: " + e.getMessage());
            }
        } else {
            displayEmptyErrorMessages(groupName, groupDesc);
        }
    }

    private void createGroup(String groupName, String groupDesc) throws IOException {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder("POST", URI, true);
        requestBuilder.updateJsonRequest("name", groupName);
        requestBuilder.updateJsonRequest("description", groupDesc);
        requestBuilder.setRequestBody();

        HttpRequestBase request = requestBuilder.getHttpRequestBase();
        CloseableHttpClient client = requestBuilder.getHttpClient();

        httpResponseService.handleReponse(request, client, this::handleCreateGroupResponse);
    }

    private void handleCreateGroupResponse(CloseableHttpResponse response, Object jsonResponse) {
        JSONObject object = controllerUtils.toJSonObject(jsonResponse);
        try {
            controllerUtils.goPage(stage, createGroupBtn, FXML_SOURCE + "/main_pages/groups/my_groups.fxml");
        } catch (JSONException e) {
            System.err.println("Failed to parse response message: " + object.opt("message"));
        }
    }

    // Validation and Error Handling
    private boolean validInputs(String name, String desc) {
        return !controllerUtils.isInputEmpty(name) && !controllerUtils.isInputEmpty(desc);
    }

    private void displayEmptyErrorMessages(String name, String desc) {
        if (controllerUtils.isInputEmpty(name)) {
            displayError(groupNameErrLabel, "groupNameErrLabel");
        }
        if (controllerUtils.isInputEmpty(desc)) {
            displayError(groupDescErrLabel, "groupDescErrLabel");
        }
    }

    private void clearErrorLabels() {
        groupNameErrLabel.setText("");
        groupDescErrLabel.setText("");
    }

    private void displayError(Label label, String resourceKey) {
        ResourceBundle rb = resourceFactory.getResourceBundle();
        label.setText(rb.getString(resourceKey));
    }

    public void updateLocalizedEmptyErrorMessages() {
        if (!groupNameErrLabel.getText().isEmpty()) displayError(groupNameErrLabel, "groupNameErrLabel");
        if (!groupDescErrLabel.getText().isEmpty()) displayError(groupDescErrLabel, "groupDescErrLabel");
    }

    // UI Localization Binding
    @Override
    public void bindUIComponents() {
        createNewGroupLabel.textProperty().bind(resourceFactory.getStringBinding("createNewGroupLabel"));
        createGroupNameLabel.textProperty().bind(resourceFactory.getStringBinding("createGroupNameLabel"));
        groupNameInput.promptTextProperty().bind(resourceFactory.getStringBinding("groupNameInput"));
        createGroupDescLabel.textProperty().bind(resourceFactory.getStringBinding("createGroupDescLabel"));
        groupDescInput.promptTextProperty().bind(resourceFactory.getStringBinding("groupDescInput"));
        createGroupBtn.textProperty().bind(resourceFactory.getStringBinding("createGroupBtnTxt"));
    }

    @Override
    public void updateAllUIComponents() {
        updateLocalizedEmptyErrorMessages();
    }
}