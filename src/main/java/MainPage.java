import model.TokenStorage;
import view.MainPageView;

public class MainPage {
    public static void main(String[] args) {
        TokenStorage.getIntance();
        TokenStorage.saveToken("username", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTc0MTA5MTYxNSwiZXhwIjoxNzQxMTc4MDE1fQ.RSU0dBdoJIYPEDMGYwUYXibDDLvRUKxa193QHJoPYck");

        MainPageView.launch(MainPageView.class);
    }
}
