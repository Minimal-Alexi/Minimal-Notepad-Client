package model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Group {
    private SimpleIntegerProperty id;
    private SimpleIntegerProperty numberOfMembers;
    private SimpleStringProperty name;
    private SimpleStringProperty description;
    private SimpleStringProperty groupOwnerName;
    private GroupOwner groupOwner;

    public Group(int id, String name, String description, GroupOwner groupOwner, int numberOfMembers) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.groupOwner = groupOwner;
        this.numberOfMembers = new SimpleIntegerProperty(numberOfMembers);
        this.groupOwnerName = new SimpleStringProperty(this.groupOwner.getUsername());
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id = new SimpleIntegerProperty(id);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name = new SimpleStringProperty(name);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description = new SimpleStringProperty(description);
    }

    public GroupOwner getGroupOwner() {
        return groupOwner;
    }

    public void setGroupOwner(GroupOwner groupOwner) {
        this.groupOwner = groupOwner;
        this.setGroupOwnerName(groupOwner.getUsername());
    }

    public int getNumberOfMembers() {
        return numberOfMembers.get();
    }

    public String getGroupOwnerName() {
        return groupOwnerName.get();
    }

    public SimpleStringProperty groupOwnerNameProperty() {
        return groupOwnerName;
    }

    public void setGroupOwnerName(String groupOwnerName) {
        this.groupOwnerName = new SimpleStringProperty(groupOwnerName);
    }

    public void setNumberOfMembers(int numberOfMembers) {
        this.numberOfMembers = new SimpleIntegerProperty(numberOfMembers);
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", groupOwner=" + groupOwner +
                ", numberOfMembers=" + numberOfMembers +
                '}';
    }
}
