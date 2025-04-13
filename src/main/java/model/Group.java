package model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private SimpleIntegerProperty id;
    private SimpleStringProperty name;
    private SimpleStringProperty description;
    private SimpleStringProperty groupOwnerName;
    private SimpleIntegerProperty numberOfMembers;
    private GroupOwner groupOwner;
    private List<AppUser> userList;
    private List<GroupMember> members;

    public Group(int id, String name, String description, GroupOwner groupOwner, List<AppUser> userList) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.userList = userList;
        this.groupOwner = groupOwner;
        this.numberOfMembers = new SimpleIntegerProperty(getNumberOfUsers() + 1);
        this.groupOwnerName = new SimpleStringProperty(this.groupOwner.getUsername());
    }


    public Group(int id, String name, String description, GroupOwner groupOwner, int numberOfMembers) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.numberOfMembers = new SimpleIntegerProperty(numberOfMembers);
        this.userList = new ArrayList<>();
        this.groupOwner = groupOwner;
        this.groupOwnerName = new SimpleStringProperty(this.groupOwner.getUsername());
    }


    public int getNumberOfUsers() {
        if (userList == null) {
            return numberOfMembers.get();
        } else {
            return userList.size();
        }
    }

    public List<AppUser> getUserList() {
        return this.userList;
    }

    public void setUserList(List<AppUser> userList) {
        this.userList = userList;
    }

    public List<GroupMember> getMembers() {
        List<GroupMember> members = new ArrayList<>();
        if (userList != null) {
            for (AppUser user : userList) {
                members.add(new GroupMember("5", user.getUsername(), user.getEmail()));
            }
        }
        return members;
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

    private List<GroupMember> userGroupParticipationsList;  // This list will hold participating users

    public List<GroupMember> getUserGroupParticipationsList() {
        return userGroupParticipationsList;
    }

    public void setUserGroupParticipationsList(List<GroupMember> userGroupParticipationsList) {
        this.userGroupParticipationsList = userGroupParticipationsList;
    }

    public List<GroupMember> parseMembers(JSONArray membersArray) {
        List<GroupMember> membersList = new ArrayList<>();
        for (int i = 0; i < membersArray.length(); i++) {
            JSONObject memberObject = membersArray.getJSONObject(i);
            GroupMember member = new GroupMember(
                    String.valueOf(id.get()),
                    memberObject.getString("username"),
                    memberObject.getString("email")
            );
            membersList.add(member);
        }
        return membersList;
    }

    public boolean isExist(String name) {
        for (AppUser user : this.userList) {
            if (user.getUsername().equals(name)) {
                return true;
            }
        }
        return false;
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
