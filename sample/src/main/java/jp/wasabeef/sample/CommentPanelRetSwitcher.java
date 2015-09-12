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
    public void notifyListChange(ArrayList<HashMap<String, String>> newlist, ArrayList<Integer> queryId);
    public void getContents(ArrayList<Integer> queryId);
    public void setEditDialogContent(HashMap<Integer,String> map);
    public void updateComment(int queryid, String title, String content);
    public void deleteComment(int queryid);
    public void uploadPic(int queryid, String filepath, String fileName);
    public void showProgressDialog(String message);
    public void stopProgressDialog();
    public void makeToast(String msg);
}
