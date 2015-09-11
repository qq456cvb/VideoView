package com.qq456cvb.videoview.Utils;

import android.app.Activity;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qq456cvb.videoview.Application.GlobalApp;

import org.apache.http.Header;

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
    public static void uploadTxtCommentHelper(final String title,  final String content){
        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, String response) {
                Log.d(TAG, "statusCode:" + statusCode + ", response:" + response);
            }

            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Log.d("test", "sssss"+response);
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
                    GlobalApp.user.post(addReviewUrl,params,handler);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static void getCommentHelper(final CommentPanelRetSwitcher cprs){
        final ArrayList<String> ret=new ArrayList<String>();
        final ArrayList<HashMap<String, String>> list=new ArrayList<HashMap<String, String>>();
        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, String response) {
                Log.d(TAG, "getCommentHelper statusCode:" + statusCode + ", response:" + response);
                String[] items=response.split("\n");
                String line;
                String save="";
                String[] names={"type","title","docname","relfile","date"};
                HashMap<String, String> map=new HashMap<String, String>();
                for(int i=0; i<items.length ;i++){
                    if(items[i].trim().equals("</html>")){
                        break;
                    }
                    if(items[i].contains("class=\"fixtableNopositionTd")){
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
                                Log.d(TAG, "mapsize:"+map.size());
                                list.add(map);
                                Log.d(TAG, "mapsize:" + map.size());
                                map=new HashMap<String, String>();
                                break;
                            }else if(line.equals("</center>")){
                                ret.add(save);
                                map.put(names[ret.size()%5],save);
                                save="";
                                break;
                            } else {
                                save += line;
                            }
                        }
                    }
                }
                Log.d(TAG, "ret size"+ret.size());
                cprs.notifyListChange(list);
            }

            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Log.d("test", "sssss"+response);
            }
        };
        new Thread() {
            @Override
            public void run() {
                try {
                    RequestParams params = new RequestParams();
                    GlobalApp.user.get(getReviewUrl,params,handler);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
