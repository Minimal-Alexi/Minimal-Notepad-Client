import model.TokenStorage;
import view.MainPageView;

public class MainPage {
    public static void main(String[] args) {
        TokenStorage.getIntance();
        TokenStorage.saveToken("username", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTc0MDExOTcxMiwiZXhwIjoxNzQwMjA2MTEyfQ.ipr3xcTBEIEQzc95zGj8pjjHifDEfEfkeHomu-8AWcQ");

        MainPageView.launch(MainPageView.class);
    }
}
