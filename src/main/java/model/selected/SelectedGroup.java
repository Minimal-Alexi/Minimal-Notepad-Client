package model.selected;

import model.Group;

public class SelectedGroup {
    private static SelectedGroup selectedGroup;
//    private Group group;
    private Integer id;

    private SelectedGroup() {
        this.id = null;
    }

    public static SelectedGroup getInstance() {
        if (selectedGroup == null) {
            selectedGroup = new SelectedGroup();
        }
        return selectedGroup;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
