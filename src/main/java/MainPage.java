import model.TokenStorage;
import view.MainPageView;

public class MainPage {
    public static void main(String[] args) {
        TokenStorage.getIntance();
        TokenStorage.saveToken("username", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTc0MDgxMzkwMywiZXhwIjoxNzQwOTAwMzAzfQ.1JmdcEjAi0SMZTurd0knRLLqm86XRPtj4OzNLg17Ofc");

        MainPageView.launch(MainPageView.class);
    }
}
