package model;

public class GroupMember {
    private int id;
    private String username;
    private String email;

    // Updated Constructor
    public GroupMember(String id, String username, String email) {
        this.id = Integer.parseInt(id);
        this.username = username;
        this.email = email;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(String id) {
        this.id = Integer.parseInt(id);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "GroupMember{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
