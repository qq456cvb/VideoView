package com.qq456cvb.videoview.Tools;

import android.util.Log;

import com.loopj.android.http.TextHttpResponseHandler;
import com.qq456cvb.videoview.Utils.UserClient;

import org.apache.http.Header;

import java.util.ArrayList;

/**
 * Created by qq456cvb on 9/26/15.
 */
public class VideoLoader {
    private ArrayList<String> videos = new ArrayList<>();
    private OnLoadedListener listener;

    public interface OnLoadedListener {
        void onLoaded(ArrayList<String> arrayList);
    }

    public VideoLoader(OnLoadedListener listener) {
        this.listener = listener;
    }

    public void getVideos() {
        this.videos.clear();
        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, String response) {
                String sub = response.substring(response.indexOf("viewplay") + 10);
                sub = sub.substring(response.indexOf("viewplay") + 10);
                parseHtml(sub);
                listener.onLoaded(videos);
            }

            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Log.d("test", "sssss");
            }
        };
        new Thread() {
            @Override
            public void run() {
                try {
                    UserClient.get("/stpy/videoMainAction!queryVideoMain.action", null, handler);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void parseHtml(String html) {
        if (html.contains("viewplay")) {
            int begin = html.indexOf("viewplay") + 10;
            int end = html.substring(begin).indexOf("');\"") + begin;
            String result = html.substring(begin, end);
            videos.add(result);
            parseHtml(html.substring(end));
        }
    }
}
