package jp.wasabeef.sample;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bean on 10/9/15.
 */
public interface CommentPanelRetSwitcher {
    public void switchRightPanel();
    public void uploadTxtComment(String title, String content);
    public void getCommentList();
    public void notifyListChange(ArrayList<HashMap<String, String>> newlist);
}
