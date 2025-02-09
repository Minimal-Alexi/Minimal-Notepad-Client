package model;



import java.util.prefs.Preferences;

// should it be singleton?
public class TokenStorage {
    private static final String STORAGE_KEY = "jwt";
    private static final Preferences storage = Preferences.userNodeForPackage(TokenStorage.class);


    public static void saveToken(String user, String token) {
        String codeStr = user + " " + token;
        storage.put(STORAGE_KEY, codeStr);
    }

    public static String getToken() {
        String value = storage.get(STORAGE_KEY, null);
        if (value == null) {
            return null;
        }
        String user = value.split(" ")[0];
        return user;
    }

    public static String getUser() {
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
        storage.remove(STORAGE_KEY);
    }
}
