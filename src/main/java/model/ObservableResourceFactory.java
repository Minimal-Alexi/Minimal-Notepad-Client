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
        return resourcesProperty().get();
    }

    // set the resource bundle to the new language
    public final void setResources(ResourceBundle resources) {
        resourcesProperty().set(resources);
    }


    public StringBinding getStringBinding(String key) {
        return new StringBinding() {
            { bind(resourcesProperty()); }
            @Override
            public String computeValue() {
                return getResources().getString(key);
            }
        };
    }

    public void getCurrentResourceBundle(){

    }


    public ResourceBundle findResourcebundle(String string) {
        Locale locale;
        if (string.equals("Chinese")) {
            locale = new Locale("zh", "CN");

        } else if (string.equals("Finnish")) {
            locale = new Locale("fi", "FI");

        }else if (string.equals("Russian")) {
            locale = new Locale("ru", "RU");
        }
        else {
            locale = new Locale("en", "US");
        }
        return ResourceBundle.getBundle("messages", locale);
    }
}
