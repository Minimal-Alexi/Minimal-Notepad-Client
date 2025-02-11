package model;


import java.util.prefs.Preferences;

// should it be singleton?
public class TokenStorage {
    private static final String STORAGE_KEY = "jwt";
    private static Preferences storage;


    public static Preferences getIntance() {
        if (storage == null) {
            storage = Preferences.userNodeForPackage(TokenStorage.class);
        }
        return storage;
    }

    /*
     the value will be "username jwtToken"
     later when extract, just call getUser and getToken
     call getUser to check if the logged in user is the same as the user stored in storage
     call getToken to get the token to be insert in the header

     */
    public static void saveToken(String user, String token) {
        String codeStr = user + " " + token;
        storage.put(STORAGE_KEY, codeStr);
    }

    public static String getUser() {
        String value = storage.get(STORAGE_KEY, null);
        if (value == null) {
            return null;
        }
        String user = value.split(" ")[0];
        return user;
    }

    public static String getToken() {
        String value = storage.get(STORAGE_KEY, null);
        if (value == null) {
            return null;
        }
        String token = value.split(" ")[1];
        return token;
    }

    public static void saveInfo(String key, String value) {
        storage.put(key, value);
    }

    public static String getInfo(String key) {
        return storage.get(key, null);
    }


    // refresh token

    // is token valid

    public static void clearToken() {
//        storage.remove(STORAGE_KEY);
        clearData(STORAGE_KEY);
    }

    public static void clearData(String key) {
        storage.remove(key);
    }
}
