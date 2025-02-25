import model.TokenStorage;
import view.MainPageView;

public class MainPage {
    public static void main(String[] args) {
        TokenStorage.getIntance();
        TokenStorage.saveToken("username", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTc0MDQ2NzAyOSwiZXhwIjoxNzQwNTUzNDI5fQ._XUQVLYm0r2T8LpASG2wdeWKnVbFRVoj69wk8zWkdr0");

        MainPageView.launch(MainPageView.class);
    }
}
