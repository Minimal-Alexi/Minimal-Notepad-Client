package model;

public class Note {
    private String title;
    private String text;
    private String colour;
    private String group;
    private int ownerId;
    private String category;
    private String createTime;

    public Note(String title, String text, String color ,String group, int ownerId, String category, String createTime) {
        this.title = title;
        this.text = text;
        this.colour = color;
        this.group = group;
        this.ownerId = ownerId;
        this.category = category;
        this.createTime = createTime;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getColour() {
        return colour;
    }
    public void setColour(String colour) {
        this.colour = colour;
    }
    public String getGroup() {
        return group;
    }
    public void setGroup(String group) {
        this.group = group;
    }
    public int getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(int owner) {
        this.ownerId = owner;
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
