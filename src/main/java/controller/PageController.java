package controller;

public abstract class PageController {

    // this will be call in initialize() method to localize all the page
    public void updateDisplay(){
        updateAllUIComponents();
        bindUIComponents();
    }

    // store the custom localization (eg press save button and display message)
    public void updateAllUIComponents() {};

    // bind the fix UI with the key
    public abstract  void bindUIComponents();


}
