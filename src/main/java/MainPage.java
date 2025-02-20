import model.TokenStorage;
import view.MainPageView;

public class MainPage {
    public static void main(String[] args) {
        TokenStorage.getIntance();
        TokenStorage.saveToken("username", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTc0MDA1MjM0NCwiZXhwIjoxNzQwMTM4NzQ0fQ.rHlFaJ18kWmnumYDG9fkGNqVb1DnZDLvVRAnWEWDk9A");

        MainPageView.launch(MainPageView.class);
    }
}
