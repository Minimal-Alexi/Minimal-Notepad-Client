import model.TokenStorage;
import view.MainPageView;

public class MainPage {
    public static void main(String[] args) {
        TokenStorage.getIntance();
        TokenStorage.saveToken("username", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTc0MDQwNTI0NiwiZXhwIjoxNzQwNDkxNjQ2fQ.zjOHjdkeF6DPCzGZgFEZQOUM9JsqWwluYTYPmN3S7Ao");

        MainPageView.launch(MainPageView.class);
    }
}
