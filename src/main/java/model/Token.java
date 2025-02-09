package model;

public class Token {
    private static Token instance;
    private String token;
    private Token() {
        // Private constructor to prevent external instantiation
        this.token = "";
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public static Token getInstance() {
        if (instance == null) {
            instance = new Token();
        }
        return instance;
    }
}