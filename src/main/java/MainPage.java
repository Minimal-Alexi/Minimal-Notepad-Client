import model.TokenStorage;
import view.MainPageView;

public class MainPage {
    public static void main(String[] args) {
        TokenStorage.getIntance();
        TokenStorage.saveToken("username", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTc0MDQxNjI1MSwiZXhwIjoxNzQwNTAyNjUxfQ.SS87KCKD9XNk5GaSHE7DhtQxXFFrGySu2worRlME9Ks");

        MainPageView.launch(MainPageView.class);
    }
}
