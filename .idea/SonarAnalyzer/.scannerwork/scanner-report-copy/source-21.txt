package model;

public class GeneralErrorKey {
    private String key;
//    private String errMessage;

    public GeneralErrorKey(String key) {
        this.key = key;
//        this.errMessage = errMessage;
    }
    public String getKey() {
        return key;
    }

//    public String getErrMessage() {
//        return errMessage;
//    }
//    public void setErrMessage(String errMessage) {
//        this.errMessage = errMessage;
//    }
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "GeneralError{" + "key=" + key + '}';
    }
}
