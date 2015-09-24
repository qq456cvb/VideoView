package com.qq456cvb.videoview.Tools;

import android.util.Log;

import com.loopj.android.http.TextHttpResponseHandler;
import com.qq456cvb.videoview.Utils.UserClient;
import com.qq456cvb.videoview.Utils.UserImage;

import org.apache.http.Header;

import java.util.ArrayList;

/**
 * Created by qq456cvb on 9/14/15.
 */
public class ProfileImageLoader {
    private ArrayList<UserImage> result = new ArrayList<>();
    private OnLoadedListener listener;

    public interface OnLoadedListener {
        void onLoaded(ArrayList<UserImage> arrayList, int type);
    }

    public ProfileImageLoader(OnLoadedListener listener) {
        this.listener = listener;
    }

    public void getImages() {
        result.clear();
        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, String response) {
                parseHtml(response);
                listener.onLoaded(result, 0);
            }

            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Log.d("test", "sssss");
            }
        };
        new Thread() {
            @Override
            public void run() {
                try {
                    UserClient.get("/stpy/imageMainAction!queryImageMain.action", null, handler);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void getImagesByReviewId(final String id) {
        result.clear();
        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, String response) {
                parseHtmlByReviewId(response, id);
                listener.onLoaded(result, 1);
            }

            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Log.d("test", "sssss");
            }
        };
        new Thread() {
            @Override
            public void run() {
                try {
                    UserClient.get("/stpy/showImageAction!showimage.action?reviewid="+id, null, handler);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void parseHtml(String html) {
        if (html.contains("onclick=\"showimage\"")) {
            UserImage img = new UserImage();
            int begin = html.indexOf("onclick=\"showimage");
            int end = begin + html.substring(html.indexOf("onclick=\"showimage")).indexOf("/>");
            String block = html.substring(begin, end);
            String reviewId = block.substring(block.indexOf("(")+1, block.indexOf(");"));
            String url = block.substring(block.indexOf("src=\"")+5, block.lastIndexOf("\""));
            img.setReviewId(reviewId);
            img.setUrl(url);
            result.add(img);
            parseHtml(html.substring(end));
        }
    }

    public void parseHtmlByReviewId(String html, String id) {
        if (html.contains("class=\"image-zoom\"")) {
            UserImage img = new UserImage();
            int begin = html.indexOf("class=\"image-zoom\"");
            int end = begin + html.substring(html.indexOf("class=\"image-zoom\"")).indexOf("provincialBox");
            String block = html.substring(begin, end);
            String url = block.substring(block.indexOf("href=\"")+6, block.substring(0, block.indexOf("rel=\"")).lastIndexOf("\""));
            int valueStart = block.indexOf("value=")+7;
            String provincial = block.substring(valueStart,
                    valueStart+block.substring(valueStart, valueStart+10).indexOf("\""));
            img.setReviewId(id);
            img.setUrl(url);
            img.setProvincial(provincial);
            result.add(img);
            parseHtmlByReviewId(html.substring(end), id);
        }
    }
}
