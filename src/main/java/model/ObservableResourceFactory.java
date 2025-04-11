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

    public void changeLanguage(String key) {
        Locale locale;

        switch (key.toLowerCase()) {
            case "chinese":
            case "zh":
                locale = new Locale("zh", "CN");
                break;
            case "finnish":
            case "fi":
                locale = new Locale("fi", "FI");
                break;
            case "russian":
            case "ru":
                locale = new Locale("ru", "RU");
                break;
            default:
                locale = new Locale("en", "US");
                break;
        }

        ResourceBundle newBundle = ResourceBundle.getBundle("messages", locale);
        LanguageLabel languageLabel = getLanguageLabel(key);
        setSelectedLanguage(languageLabel);
        setResources(newBundle);
    }

    public LanguageLabel getLanguageLabel(String languageKey){
        String languageKeyFromResourceBundle = mapToLanguageKeyInResourceBundle(languageKey);
        String languageValueFromResourceBundle = getResources().getString(languageKeyFromResourceBundle);
        return  new LanguageLabel(languageKeyFromResourceBundle, languageValueFromResourceBundle);
//        switch (languageKey){
//            case "en":
////                Strin
//                return new LanguageLabel("en","en");
//            case "ru":
//                return new LanguageLabel("ru","ru");
//            case "zh":
//                return new LanguageLabel("zh","zh");
//            case "fi":
//                return new LanguageLabel("fi","fi");
//                default:
//                    return new LanguageLabel("en","en");
//        }

    }

    public String mapToLanguageKeyInResourceBundle(String key){
        switch (key.toLowerCase()){
            case "en":
                return "english";
            case "ru":
                return "russian";
            case "zh":
                return "chinese";
            case "fi":
                return "finnish";
            default:
                return "english";
        }
    }
}
