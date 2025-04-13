package model;

public class LanguageLabel {
    private final String key;
    private String text;

    public LanguageLabel(String key, String text) {
        this.key = key;
        this.text = text;
    }

    public String getKey() {
        return key;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LanguageLabel other) {
            return key.equals(other.key);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
