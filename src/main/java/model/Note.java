package model;

public class Note {
    private String name;
    private String group;
    private String owner;
    private String category;
    private String createTime;

    public Note(String name, String group, String owner, String category, String createTime) {
        this.name = name;
        this.group = group;
        this.owner = owner;
        this.category = category;
        this.createTime = createTime;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getGroup() {
        return group;
    }
    public void setGroup(String group) {
        this.group = group;
    }
    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getCreateTime() {
        return createTime;
    }
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
