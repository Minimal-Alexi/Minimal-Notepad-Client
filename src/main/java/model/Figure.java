package model;

public class Figure {

    private int id;
    private String path;

    public Figure(int id, String path) {
        this.id = id;
        this.path = path;
    }

    public Figure(String path) {
        this(0, path);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        path = path;
    }

    @Override
    public String toString() {
        return "Figure{" +
                "id=" + id +
                ", path='" + path + '\'' +
                '}';
    }
}
