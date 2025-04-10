package model;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Locale;
import java.util.ResourceBundle;

public class ObservableResourceFactory {
    private static ObservableResourceFactory instance;

    private ObjectProperty<ResourceBundle> resources ;

    private LanguageLabel selectedLanguage;

    private ObservableResourceFactory() {
        resources = new SimpleObjectProperty<>();
    }

    public static ObservableResourceFactory getInstance() {
        if (instance == null){
            instance = new ObservableResourceFactory();
        }
        return instance;
    }

    public ObjectProperty<ResourceBundle> resourcesProperty() {
        return resources ;
    }

    public void setSelectedLanguage(LanguageLabel selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
    }

    public LanguageLabel getSelectedLanguage() {
        // in case this is a total new app, new user, get default selected language
        if (selectedLanguage == null){
            ResourceBundle rb = getResources();
            return new LanguageLabel("en",rb.getString("en"));
        }
        return selectedLanguage;
    }

    // get the resource bundle
    public final ResourceBundle getResources() {
        ResourceBundle rs = resourcesProperty().get();
        if (rs == null){
            ResourceBundle defaultRs = ResourceBundle.getBundle("messages", new Locale("en", "US"));
            setResources(defaultRs);
            return defaultRs;
        }
        return resources.get(); // Use resources.get() directly
    }

    // set the resource bundle to the new language
    public final void setResources(ResourceBundle newResources) {
        resources.set(newResources); // Correct way to update JavaFX property
    }


    public StringBinding getStringBinding(String key) {
        return new StringBinding() {
            { bind(resourcesProperty()); } // Bind the property so it updates dynamically
            @Override
            public String computeValue() {
                ResourceBundle rb = getResources();
                return rb.containsKey(key) ? rb.getString(key) : "!!" + key + "!!"; // Handle missing keys
            }
        };
    }

    public String getString(String key) {
        ResourceBundle rb = getResources();
        return rb.getString(key);
    }

    public void getCurrentResourceBundle(){

    }


    public ResourceBundle findResourcebundle(String key) {
        Locale locale = switch (key.toLowerCase()) { // Use lowercase for consistency
            case "chinese" -> new Locale("zh", "CN");
            case "finnish" -> new Locale("fi", "FI");
            case "russian" -> new Locale("ru", "RU");
            default -> new Locale("en", "US"); // Default to English
        };
        return ResourceBundle.getBundle("messages", locale);
    }

}
