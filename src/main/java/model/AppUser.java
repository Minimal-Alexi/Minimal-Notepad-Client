package model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class AppUser {
    private SimpleIntegerProperty id;
    private SimpleStringProperty username;
    private SimpleStringProperty email;

    public AppUser(int id, String username, String email) {
        this.id = new SimpleIntegerProperty(id);
        this.username = new SimpleStringProperty(username);
        this.email = new SimpleStringProperty(email) ;
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id =new SimpleIntegerProperty(id) ;
    }

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username = new SimpleStringProperty(username) ;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email = new SimpleStringProperty(email);
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "id=" + id +
                ", username=" + username +
                ", email=" + email +
                '}';
    }
}
