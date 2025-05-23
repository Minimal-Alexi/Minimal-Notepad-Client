package model;

import java.util.ArrayList;
import java.util.HashMap;

public class Note {
    private int id;
    private String title;
    private String text;
    private String color;
    private String createdAt;
    private String updatedAt;
    private String owner;
    private int groupId;
    private String group;
    private HashMap<Integer, String> category;
//    private ArrayList<String> figure;
    private ArrayList<Figure> figure;


    public Note(int id, String title, String text, String color, String createdAt, String updatedAt, String owner, int groupId, String group, HashMap<Integer, String> category, ArrayList<Figure> figure) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.color = color;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.owner = owner;
        this.groupId = groupId;
        this.group = group;
        this.category = category;
        this.figure = figure;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
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
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public String getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }
    public int getGroupId() {
        return groupId;
    }
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
    public String getGroup() {
        return group;
    }
    public void setGroup(String group) {
        this.group = group;
    }
    public HashMap<Integer, String> getCategory() {
        return category;
    }
    public void setCategory(HashMap<Integer, String> category) {
        this.category = category;
    }
    public void setFigure(ArrayList<Figure> figure) {
        this.figure = figure;
    }
    public ArrayList<Figure> getFigure() {
        return figure;
    }
}
