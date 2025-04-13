package model;

public class GeneralErrorKey {
    private String key;

    public GeneralErrorKey(String key) {
        this.key = key;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "GeneralError{" + "key=" + key + '}';
    }
}
