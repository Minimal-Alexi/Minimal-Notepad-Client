import model.TokenStorage;
import view.MainPageView;

public class MainPage {
    public static void main(String[] args) {
        TokenStorage.getIntance();
        TokenStorage.saveToken("username", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTc0MDM5NTU5OCwiZXhwIjoxNzQwNDgxOTk4fQ.k8BOxYvE9g-Gs6YSAL17Suw482ZTy0JG-0V39sbxduc");

        MainPageView.launch(MainPageView.class);
    }
}
