import model.TokenStorage;
import view.MainPageView;

public class MainPage {
    public static void main(String[] args) {
        TokenStorage.getIntance();
        TokenStorage.saveToken("username", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTczOTg3ODg2MiwiZXhwIjoxNzM5OTY1MjYyfQ.jp7Ny3zfF8dOaGpKJJM7J5aoD6CglkLJtuARx2JLk58");

        MainPageView.launch(MainPageView.class);
    }
}
