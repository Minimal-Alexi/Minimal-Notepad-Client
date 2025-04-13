package controller;

public abstract class PageController {

    public void updateDisplay(){
        updateAllUIComponents();
        bindUIComponents();
    }

    public void updateAllUIComponents() {};
    public abstract  void bindUIComponents();


}
