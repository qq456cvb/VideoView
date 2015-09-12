package com.qq456cvb.videoview.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qq456cvb.videoview.Application.GlobalApp;

import org.apache.http.Header;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.sample.CommentPanelRetSwitcher;

/**
 * Created by bean on 10/9/15.
 */
public class CommentHttpHelper {
    private static String TAG="CommentHttpHelper";
    private static String addReviewUrl= "/stpy/reviewAction!addReview.action?hdPageNo=1";
    private static String getReviewUrl="/stpy/reviewMainAction!queryReviewMain.action";
    private static String editReviewUrl="/stpy/ajaxReviewAction!queryReviewById.action";
    private static String updateReviewUrl="/stpy/reviewMainAction!updateReviewInfo.action";
    private static String deleteReviewUrl="/stpy/reviewMainAction!deleteReview.action";
    private static String uploadPicReviewUrl="/stpy/reviewMainAction!doUpload.action";
    private static String uploadWordUrl="/stpy/reviewAction!uploadWord.action?hdnPageNo=1";
    public static int VIDEO=1;
    public static int PICTURE=2;

    public static void uploadTxtCommentHelper(final String title,  final String content, final CommentPanelRetSwitcher cprs){
        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, String response) {
//                Log.d(TAG, "statusCode:" + statusCode + ", response:" + response);
//                SystemClock.sleep(1000);
//                cprs.stopProgressDialog();
                cprs.makeToast("评论上传成功");
            }

            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Log.d("test", "sssss" + response);
                cprs.makeToast("评论上传失败");
            }
        };
        new Thread() {
            @Override
            public void run() {
                try {

                    RequestParams params = new RequestParams();
                    params.put("checkedimgname","review_click.png");
                    params.put("checkedimgindex", 2);
                    params.put("radioReview", 1);
                    params.put("reviewMapping.name",title);
                    params.put("reviewMapping.remark",content);
                    params.put("hdnPageNo", 1);
                    params.put("hdnType", 3);
                    GlobalApp.user.post(addReviewUrl, params, handler);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static void getCommentHelper(final CommentPanelRetSwitcher cprs){
//        final ArrayList<String> ret=new ArrayList<String>();
        final ArrayList<Integer> queryIds=new ArrayList<Integer>();
        int count=0;
        final ArrayList<HashMap<String, String>> list=new ArrayList<HashMap<String, String>>();
        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, String response) {
//                Log.d(TAG, "getCommentHelper statusCode:" + statusCode + ", response:" + response);
                String[] items=response.split("\n");
                String line;
                String save="";
                String[] names={"type","title","docname","relfile","date"};
                HashMap<String, String> map=new HashMap<String, String>();
                int j=0;
                for(int i=0; i<items.length ;i++){
                    if(items[i].trim().equals("</html>")){
                        break;
                    }
                    if(items[i].contains("class=\"fixtableNopositionTd")){

//                        Log.d(TAG, "j is reset");
                        while(true){
                            i++;
                            //Log.d(TAG, "i="+i+"line:"+items[i]);
                            if(i==items.length){
                                break;
                            }
                            line=items[i].trim();
                            if(line.equals("<center>")||line.equals("")){
                                //skip
                            }else if (line.contains("update_channel.png")) {
//                                Log.d(TAG, "mapsize:" + map.size());
                                list.add(map);
//                                Log.d(TAG, "mapsize:" + map.size());
                                map=new HashMap<String, String>();
                                //get query id
                                while(true){
                                    i++;
                                    //Log.d(TAG, "i="+i+"line:"+items[i]);
                                    if(i==items.length){
                                        break;
                                    }
                                    line=items[i].trim();
                                    if(line.contains("onclick=\"reviewOpen")){
                                        String queryidstr=line.substring(line.indexOf("(") + 1, line.indexOf(","));
//                                        Log.d(TAG, "queryidstr:" + queryidstr);
                                        queryIds.add(Integer.parseInt(queryidstr));
                                        break;
                                    }
                                }
                                break;
                            }else if(line.equals("</center>")){
                                //ret.add(save);
//                                map.put(names[ret.size()%5],save);
//                                Log.d(TAG, "j="+j);
                                map.put(names[j++%5],save);
                                save="";
                                break;
                            } else {
                                save += line;
                            }
                        }
                    }
                }
                Log.d(TAG, "new list title"+list.get(0).get("title"));
                cprs.notifyListChange(list, queryIds);
                cprs.makeToast("获取评论成功");
//                cprs.stopProgressDialog();
            }

            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Log.d("test", "sssss"+response);
//                cprs.stopProgressDialog();
                cprs.makeToast("获取评论失败");
            }
        };
        new Thread() {
            @Override
            public void run() {
                try {
                    RequestParams params = new RequestParams();
                    GlobalApp.user.get(getReviewUrl, params, handler);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static void editTxtCommentHelper(final ArrayList<Integer> queryIds, final CommentPanelRetSwitcher cprs){
        Log.d(TAG, "-----editTxtCommentHelper");
        final HashMap<Integer, String> retmap=new HashMap<Integer, String>();
        final int idsize=queryIds.size();
        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, String response) {
//                Log.d(TAG, "statusCode:" + statusCode + ", response:" + response);
                String columns[]=response.split(",");
                String contentraw=columns[5].split(":")[1];
                Integer queryid=Integer.parseInt(columns[1].split(":")[1]);
                String content=contentraw.substring(2, contentraw.length()-2);
                retmap.put(queryid, content);
                if(retmap.size()==idsize){
                    cprs.setEditDialogContent(retmap);
                }
            }

            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Log.d("test", "sssss" + response);
            }
        };
        new Thread() {
            @Override
            public void run() {
                for(int i=0;i<queryIds.size();i++){
                    try {
                        Log.d(TAG, "query"+queryIds.get(i));
                        RequestParams params = new RequestParams();
                        params.put("id",queryIds.get(i));
                        GlobalApp.user.post(editReviewUrl, params, handler);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public static void updateTxtCommentHelper(final int queryId, final String title, final String content,
                                              final CommentPanelRetSwitcher cprs){
        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, String response) {
                Log.d(TAG, "statusCode:" + statusCode + ", response:" + response);
                cprs.makeToast("更新评论成功");
                cprs.getCommentList();
            }

            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Log.d("test", "sssss"+response);
                cprs.makeToast("更新评论失败");
            }
        };
        new Thread() {
            @Override
            public void run() {
                try {
                    RequestParams params = new RequestParams();
                    params.put("checkedimgname","review_click.png");
                    params.put("checkedimgindex", 2);
                    params.put("radioReview", 1);
                    params.put("reviewMapping.name",title);
                    params.put("reviewMapping.remark",content);
                    params.put("hdnid", queryId);
                    params.put("hdnPageNo", 1);
                    params.put("hdnType", 3);
                    String tmpUrl=updateReviewUrl+"?id="+queryId;
                    GlobalApp.user.post(tmpUrl, params, handler);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static void deleteTxtCommentHelper(final int queryId, final CommentPanelRetSwitcher cprs){
        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, String response) {
                Log.d(TAG, "statusCode:" + statusCode + ", response:" + response);
                cprs.makeToast("删除评论成功");
            }

            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Log.d("test", "sssss"+response);
                cprs.makeToast("删除评论失败");
            }
        };
        new Thread() {
            @Override
            public void run() {
                try {
                    RequestParams params = new RequestParams();
//                    params.put("checkedimgname","review_click.png");
//                    params.put("checkedimgindex", 2);
//                    params.put("radioReview", 1);
                    params.put("hdnid", queryId);
//                    params.put("hdnPageNo", 1);
//                    params.put("hdnType", 3);
                    String tmpUrl=deleteReviewUrl+"?id="+queryId;
                    GlobalApp.user.post(tmpUrl, params, handler);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public static void uploadCommentFileHelper(final int doctype, final int queryId, final String filepath, final String fileName,
                                               final CommentPanelRetSwitcher cprs){
        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, String response) {
                Log.d(TAG, "---onsucess");
                Log.d(TAG, "statusCode:" + statusCode + ", response:" + response);
                cprs.makeToast("文件上传成功");
                cprs.getCommentList();
            }

            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Log.d(TAG, "---onfailure");
                Log.d(TAG, "upload"+response);
                cprs.makeToast("文件上传失败");
            }
        };
        new Thread() {
            @Override
            public void run() {
                try {
                    RequestParams params = new RequestParams();
                    params.put("hdnId", queryId);
                    try{
                        File picFile=new File(filepath);
                        params.put("fileUrl",picFile);
                        Log.d(TAG, "create file success");
                    }catch (Exception e){
                        Log.d(TAG, "cannot open file!");
                        //TODO: Toast
                    }
                    params.put("filName", fileName);
                    params.put("radioReview", doctype);
                    String tmpUrl=uploadPicReviewUrl+"?id="+queryId;
                    Log.d(TAG, tmpUrl);
                    GlobalApp.user.post(tmpUrl, params, handler);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
