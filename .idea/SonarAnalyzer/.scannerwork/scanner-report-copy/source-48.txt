package utils;

public class PageService implements IPageService {

    @Override
    public void handlePageAction(HandlePageCallback callback) {
        callback.handlePageAction();
    }
}
