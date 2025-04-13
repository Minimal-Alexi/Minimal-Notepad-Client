package utils;

import controller.PageController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import model.LanguageLabel;
import model.ObservableResourceFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class Utils {
    public static ObservableResourceFactory RESOURCE_FACTORY = ObservableResourceFactory.getInstance();
    public static PageService pageService = new PageService();

    public  static void gotoPage(String pageName, Button btn){

        FXMLLoader fxmlLoader = new FXMLLoader(Utils.class.getResource(pageName));
        Stage stage = (Stage) btn.getScene().getWindow();
        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/logIn_view.fxml"));
            Parent root = fxmlLoader.load();

//            var stage = getStage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void setupLanguageBox(
            ComboBox<LanguageLabel> languageBox,
            LanguageLabel[] supportedLanguages,
            ObservableResourceFactory RESOURCE_FACTORY,
            PageController pageController,
            HandlePageCallback callback) {
        // get the supported languages from the resource bundle
        getAndSetSupportedLanguages(supportedLanguages, RESOURCE_FACTORY);

        // set the supportedLanguages to the languageBox
        languageBox.getItems().setAll(supportedLanguages);

        // Cell display logic
        languageBox.setCellFactory(new Callback<>() {
            @Override
            public ListCell<LanguageLabel> call(ListView<LanguageLabel> listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(LanguageLabel item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty || item == null ? null : item.getText());
                    }
                };
            }
        });

        // Converter logic
        // this handle display logic in the dropdown
        languageBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(LanguageLabel object) {
                // 🟢 this displays the label in the dropdown
                return object == null ? "" : object.getText();
            }

            @Override
            public LanguageLabel fromString(String string) {
                return null;
            }
        });

        // Set default selection if no preserved selected Language available
        LanguageLabel preservedSelected = RESOURCE_FACTORY.getSelectedLanguage();

        languageBox.setValue(preservedSelected);

        // Language change handler
        languageBox.setOnAction(event -> {
            LanguageLabel selected = languageBox.getValue();
            if (selected != null) {
                ResourceBundle bundle = getResourceBundleFromKey(selected.getKey());
                RESOURCE_FACTORY.setResources(bundle);
                updateLanguageBoxLabels(RESOURCE_FACTORY, languageBox);

                // call the UI update function from the page
                pageController.updateAllUIComponents();

                pageController.bindUIComponents();      // Rebind labels
//                updateNameLabelIfInputExists();
                pageService.handlePageAction(callback);
            }
        });

        // Initial label setup
        updateLanguageBoxLabels(RESOURCE_FACTORY, languageBox);
    }
    public static void setupLanguageBox(
            ComboBox<LanguageLabel> languageBox,
            LanguageLabel[] supportedLanguages,
            ObservableResourceFactory RESOURCE_FACTORY,
            PageController pageController){
        setupLanguageBox(languageBox, supportedLanguages, RESOURCE_FACTORY, pageController, Utils::dummyFunction);
    }

    // a placeholder function when you dont want to add any action from the page where you inject setupLanguageBox
    public static void dummyFunction(){
        System.out.println("dummyFunction");
    }

    //





    //    public LanguageLabel[] getSupportedLanguages() {
    public static void getAndSetSupportedLanguages(LanguageLabel[] supportedLanguages, ObservableResourceFactory RESOURCE_FACTORY) {
        String[] keys = {"en", "fi", "zh", "ru"};
        ResourceBundle rb = RESOURCE_FACTORY.getResourceBundle();
        for (int i = 0; i < keys.length; i++) {
            String key  = keys[i];
            String value = rb.getString(key);
            supportedLanguages[i] = new LanguageLabel(key, value);
        }
    }

    private static ResourceBundle getResourceBundleFromKey(String key) {
        return switch (key) {
            case "en"-> ResourceBundle.getBundle("messages", new Locale("en", "US"));
            case "fi" -> ResourceBundle.getBundle("messages", new Locale("fi", "FI"));
            case "zh" -> ResourceBundle.getBundle("messages", new Locale("zh", "CN"));
            case "ru" -> ResourceBundle.getBundle("messages", new Locale("ru", "RU"));
            default -> ResourceBundle.getBundle("messages", new Locale("en", "US"));
        };
    }


    private static void updateLanguageBoxLabels(ObservableResourceFactory RESOURCE_FACTORY, ComboBox<LanguageLabel> languageBox) {
        // get and set the supported languages from the resource bundle
        ResourceBundle rb = RESOURCE_FACTORY.getResourceBundle();
        languageBox.getItems();

        // preserve old selection key
        LanguageLabel selected = languageBox.getValue();
        RESOURCE_FACTORY.setSelectedLanguage(selected);

        //retrieve the preserved LanguageLabel, so that it will be used in another page
        LanguageLabel preservedSelected = RESOURCE_FACTORY.getSelectedLanguage();

        // preserve old selection key
        String selectedKey = getSelectedLanguageKey(selected, preservedSelected);

        // Rebuild language items with translated labels
        LanguageLabel[] updatedLanguages = {
                new LanguageLabel("en", rb.getString("en")),
                new LanguageLabel("fi", rb.getString("fi")),
                new LanguageLabel("zh", rb.getString("zh")),
                new LanguageLabel("ru", rb.getString("ru"))
        };

        // Temporarily remove event handler to avoid recursion
        // if not removed, when the language is changed, it will trigger the event handler again,
        // Another benefit is when we set the setaction = null and  update the languageBox, the UI will be automatically rerender

        var handler = languageBox.getOnAction();
        languageBox.setOnAction(null);

        languageBox.getItems().setAll(updatedLanguages);

        // Restore selection
        for (LanguageLabel label : updatedLanguages) {
            if (label.getKey().equals(selectedKey)) {
                languageBox.setValue(label);
                break;
            }
        }

        // Restore event handler
        languageBox.setOnAction(handler);
    }

    private static void updateNameLabelIfInputExists(TextField textInput, ObservableResourceFactory RESOURCE_FACTORY, Label label, String key) {
        String input = textInput.getText();
        if (!input.isEmpty()) {
            ResourceBundle rb = RESOURCE_FACTORY.getResourceBundle();
            String result = MessageFormat.format(rb.getString(key), input);
            Platform.runLater(() -> label.setText(result));
        }
    }

    public static String getSelectedLanguageKey(LanguageLabel selected, LanguageLabel preservedLabel) {
        if (selected != null) {
            return selected.getKey();
        } else if (preservedLabel != null) {
            return preservedLabel.getKey();
        } else {
            return "en";
        }
//        return selected != null ? selected.getKey() : preservedLabel != null ? preservedLabel.getKey() : "en";
    }


    public static SimpleDateFormat getTheCurrentLocaleDateTimeFormatString() {
        Locale currentLocale = RESOURCE_FACTORY.getResourceBundle().getLocale();
        return (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, currentLocale);
    }


    public static Optional<ButtonType> displayDeleteWarningDialog() {
        // add alert dialog
        Alert alert = new Alert(Alert.AlertType.WARNING);

        // TODO: replace these yes and no with the localization
        // get the resource bundle from the RESOURCE_FACTORY
        ResourceBundle rb = RESOURCE_FACTORY.getResourceBundle();

        // retrive all the yes, no , title and warning text from resource bundle
        String yesTxt = rb.getString("yesText");
        String noTxt = rb.getString("noText");
        String warningTxt = rb.getString("deleteWarningText");
        String warningTitle = rb.getString("deleteWarningTitle");
        String warningHeader = rb.getString("deleteWarningHeader");

        ButtonType yesBtn = new ButtonType(yesTxt);
        // Button.ButtonData.CANCEL_CLOSE will let closing the button by click the X button
        ButtonType noBtn = new ButtonType(noTxt, ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.setTitle(warningTitle);
        alert.setHeaderText(warningHeader);
        alert.setContentText(warningTxt);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(yesBtn, noBtn);
//        alert.getButtonTypes().add()
        Optional<ButtonType> result = alert.showAndWait();
        return result;
    }
}

