import model.TokenStorage;
import view.MainPageView;

public class MainPage {
    public static void main(String[] args) {
        TokenStorage.getIntance();
        TokenStorage.saveToken("username", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTc0MDk5NTU3NCwiZXhwIjoxNzQxMDgxOTc0fQ.USI6mKfKKtvSsXyOD73L7JA-KMsZvwLAAGNOEVG04pA");

        MainPageView.launch(MainPageView.class);
    }
}
