package model.selected;

public class SelectedNote {
    private static SelectedNote selectedNote;
    private Integer id;
    private SelectedNote() {
        this.id = null;
    }
    public static SelectedNote getInstance() {
        if (selectedNote == null) {
            selectedNote = new SelectedNote();
        }
        return selectedNote;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
}
