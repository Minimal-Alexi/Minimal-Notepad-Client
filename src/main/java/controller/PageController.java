package controller;

public abstract class PageController {

    public void updateDisplay(){
        updateAllUIComponents();
        bindUIComponents();
    }

    public abstract void updateAllUIComponents();

    public abstract  void bindUIComponents();


}
