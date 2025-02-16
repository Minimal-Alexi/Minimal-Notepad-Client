import model.TokenStorage;
import view.MainPageView;

public class MainPage {
    public static void main(String[] args) {
        TokenStorage.getIntance();
        TokenStorage.saveToken("username", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTczOTcxMjc3MywiZXhwIjoxNzM5Nzk5MTczfQ.KlTq4ymXIg12qF7J2gg5usqHfG9XEMbrbTQXMilMaN4");

        MainPageView.launch(MainPageView.class);
    }
}
