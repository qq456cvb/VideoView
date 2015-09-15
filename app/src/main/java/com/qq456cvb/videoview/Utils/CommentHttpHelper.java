package com.qq456cvb.videoview.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qq456cvb.videoview.Application.GlobalApp;

import org.apache.http.Header;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
    private static String downloadWordUrl="/stpy/reviewMainAction!queryWrod.action";
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
                boolean flag=false;
                int j=0;
                int i=0;
                while(i<items.length){
                    line=items[i].trim();
                    if(line.equals("<center>")){
                        flag=true;
                    }else if(line.equals("</center>")){
                        map.put(names[j%5],save);
                        save="";
                        if(j%5==4){
                            list.add(map);
                            Log.d(TAG, "map size:"+map.size());
                            map=new HashMap<String, String>();
                        }
                        j++;
                        flag=false;
                    }else if(flag){
                        save+=line;
                    }else if(line.contains("onclick=\"reviewOpen")){
                                        String queryidstr=line.substring(line.indexOf("(") + 1, line.indexOf(","));
//                                        Log.d(TAG, "queryidstr:" + queryidstr);
                                        queryIds.add(Integer.parseInt(queryidstr));
                    }else if(line.contains("onclick=\"location='reviewMainAction!queryWrod.action?id=")){
                        String queryidstr=line.substring(line.indexOf("id=") + 3, line.indexOf("'\""));
                                        Log.d(TAG, "queryidstr:" + queryidstr);
                        queryIds.add(Integer.parseInt(queryidstr));
                    }
                    i++;
                }
//                for(int i=0; i<items.length ;i++){
//                    if(items[i].trim().equals("</html>")){
//                        break;
//                    }
//                    if(items[i].contains("class=\"fixtableNopositionTd")){
//
////                        Log.d(TAG, "j is reset");
//                        while(true){
//                            i++;
//                            //Log.d(TAG, "i="+i+"line:"+items[i]);
//                            if(i==items.length){
//                                break;
//                            }
//                            line=items[i].trim();
//                            if(line.equals("<center>")||line.equals("")){
//                                //skip
//                                flag=true;
//                            }else if (line.contains("update_channel.png")) {
////                                Log.d(TAG, "mapsize:" + map.size());
//                                list.add(map);
////                                Log.d(TAG, "mapsize:" + map.size());
//                                map=new HashMap<String, String>();
//                                //get query id
//                                while(true){
//                                    i++;
//                                    //Log.d(TAG, "i="+i+"line:"+items[i]);
//                                    if(i==items.length){
//                                        break;
//                                    }
//                                    line=items[i].trim();
//                                    if(line.contains("onclick=\"reviewOpen")){
//                                        String queryidstr=line.substring(line.indexOf("(") + 1, line.indexOf(","));
////                                        Log.d(TAG, "queryidstr:" + queryidstr);
//                                        queryIds.add(Integer.parseInt(queryidstr));
//                                        break;
//                                    }
//                                }
//                                break;
//                            }else if(line.equals("</center>")){
//                                flag=false;
//                                //ret.add(save);
////                                map.put(names[ret.size()%5],save);
////                                Log.d(TAG, "j="+j);
//                                map.put(names[j++%5],save);
//                                save="";
//                                break;
//                            } else {
//                                if(flag){
//                                    save += line;
//                                }else{
//                                    Log.d(TAG, "not contain:"+line);
//                                }
//                            }
//                        }
//                    }
//                }
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

    public static void uploadWordCommentHelper(final String filepath,  final String fileName, final CommentPanelRetSwitcher cprs){
        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, String response) {
                cprs.makeToast("word上传成功");
            }

            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Log.d("test", "sssss" + response);
                cprs.makeToast("word上传失败");
            }
        };
        new Thread() {
            @Override
            public void run() {
                try {

                    RequestParams params = new RequestParams();
                    params.put("checkedimgname","review_click.png");
                    params.put("checkedimgindex", 2);
                    params.put("radioReview", 2);
                    params.put("hdnPageNo", 1);
                    params.put("hdnType", 3);
                    try{
                        File picFile=new File(filepath);
                        params.put("fileUrlWrod",picFile);
                        Log.d(TAG, "create file success");
                    }catch (Exception e){
                        Log.d(TAG, "cannot open file!");
                        //TODO: Toast
                    }
                    params.put("filName", fileName);
                    GlobalApp.user.post(uploadWordUrl, params, handler);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public static void downloadWordHelper(final int queryId, final String fileName, final CommentPanelRetSwitcher cprs, Activity act){
        final FileAsyncHttpResponseHandler handler = new FileAsyncHttpResponseHandler(act) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                // Do something with the file `response`
                File sdDir = null;
                long length = 0;
                String localPath = "", remotePath = response.getPath();
                boolean sdCardExist = Environment.getExternalStorageState()
                        .equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
                if (sdCardExist)
                {
                    sdDir = Environment.getExternalStorageDirectory();//获取跟目录
                    localPath = sdDir.toString() + "/"+fileName;
                } else {
                    //TODO
                    cprs.makeToast("SD卡不存在");
                }
                try {
                    int bytesum = 0;
                    int byteread = 0;
                    File file = new File(remotePath);
                    if (file.exists()) { //文件存在时
                        FileInputStream inStream = new FileInputStream(remotePath); //读入原文件
                        length = inStream.available();
                        Log.d(TAG,"localPath:"+localPath);
                        FileOutputStream fs = new FileOutputStream(localPath);
                        byte[] buffer = new byte[4096];
                        while ( (byteread = inStream.read(buffer)) != -1) {
                            bytesum += byteread; //字节数 文件大小
                            System.out.println(bytesum);
                            fs.write(buffer, 0, byteread);
                        }
                        inStream.close();
                    }
                    cprs.makeToast("下载word成功，路径："+localPath);
                }
                catch (Exception e) {
                    cprs.makeToast("下载word文档出错");
                    e.printStackTrace();
                }
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File response) {
                cprs.makeToast("下载word文档失败");
            }
        };
        new Thread() {
            @Override
            public void run() {
                try {
                    String tmpUrl=updateReviewUrl+"?id="+queryId;
                    RequestParams params = new RequestParams();
                    params.put("id", queryId);
                    GlobalApp.user.get(tmpUrl, params, handler);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
}
