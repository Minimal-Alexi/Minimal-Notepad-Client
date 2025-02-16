import model.TokenStorage;
import view.MainPageView;

public class MainPage {
    public static void main(String[] args) {
        TokenStorage.getIntance();
        TokenStorage.saveToken("username", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTczOTY4NzQ5MSwiZXhwIjoxNzM5NzczODkxfQ._6pPVV-6QJVQYwTnRL3rXdIKIYxgFktWGnQPpliY4P8");

        MainPageView.launch(MainPageView.class);
    }
}
