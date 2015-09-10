package com.qq456cvb.videoview.Utils;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

/**
 * Created by bean on 10/9/15.
 */
public class CommentHttpHelper {
    private String TAG="CommentHttpHelper";
    private static final String BASE_URL = "http://220.250.58.250";
    private static AsyncHttpClient client = new AsyncHttpClient();
    private String addReviewUrl= BASE_URL+"/stpy/reviewAction!addReview.action?hdPageNo=1";
    public void uploadTxtComment(final String title, final String content){
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
                    client.post(addReviewUrl, params, handler);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
