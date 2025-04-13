package model;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Locale;
import java.util.ResourceBundle;

public class ObservableResourceFactory {
    private static ObservableResourceFactory instance;

    // get the current resource bundle, the reason using Object Property is in order to use the bind method,
    // and turning the resource bundle into a Observable object
    // by binding other object to this object, other objects become observers
    private ObjectProperty<ResourceBundle> resourceBundle ;

    private LanguageLabel selectedLanguage;

    private ObservableResourceFactory() {
        resourceBundle = new SimpleObjectProperty<>();
    }

    public static ObservableResourceFactory getInstance() {
        if (instance == null){
            instance = new ObservableResourceFactory();
        }
        return instance;
    }

    public ObjectProperty<ResourceBundle> resourcesProperty() {
        return resourceBundle ;
    }

    public void setSelectedLanguage(LanguageLabel selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
    }

    public LanguageLabel getSelectedLanguage() {
        // in case this is a total new app, new user, get default selected language
        if (selectedLanguage == null){
            ResourceBundle rb = getResourceBundle();
            return new LanguageLabel("en",rb.getString("en"));
        }
        return selectedLanguage;
    }

    // get the resource bundle
    public final ResourceBundle getResourceBundle() {
        ResourceBundle rs = resourcesProperty().get();
        if (rs == null){
            ResourceBundle defaultRs = ResourceBundle.getBundle("messages", new Locale("en", "US"));
            setResources(defaultRs);
            return defaultRs;
        }
        return resourceBundle.get(); // Use resourceBundle.get() directly
    }

    // set the resource bundle to the new language
    public final void setResources(ResourceBundle newResources) {
        resourceBundle.set(newResources); // Correct way to update JavaFX property
    }


    public StringBinding getStringBinding(String key) {
        return new StringBinding() {
            { bind(resourcesProperty()); } // Bind the property so it updates dynamically
            @Override
            public String computeValue() {
                ResourceBundle rb = getResourceBundle();
                return rb.containsKey(key) ? rb.getString(key) : "!!" + key + "!!"; // Handle missing keys
            }
        };
    }

    public String getString(String key) {
        ResourceBundle rb = getResourceBundle();
        return rb.getString(key);
    }


    public ResourceBundle findResourcebundle(String key) {
        Locale locale = switch (key.toLowerCase()) { // Use lowercase for consistency
            case "zh" -> new Locale("zh", "CN");
            case "fi" -> new Locale("fi", "FI");
            case "ru" -> new Locale("ru", "RU");
            default -> new Locale("en", "US"); // Default to English
        };
        return ResourceBundle.getBundle("messages", locale);
    }

    public void changeLanguage(String key) {
        ResourceBundle newBundle = findResourcebundle(key);
        LanguageLabel languageLabel = getLanguageLabel(key);
        setSelectedLanguage(languageLabel);
        setResources(newBundle);
    }

    public LanguageLabel getLanguageLabel(String languageKey){
        String languageValueFromResourceBundle = getResourceBundle().getString(languageKey);
        return  new LanguageLabel(languageKey, languageValueFromResourceBundle);
    }

}
