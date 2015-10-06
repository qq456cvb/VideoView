package com.qq456cvb.videoview.Tools;

import android.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.TextHttpResponseHandler;
import com.qq456cvb.videoview.Utils.UserClient;
import com.qq456cvb.videoview.Utils.UserVideo;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

/**
 * Created by qq456cvb on 9/26/15.
 */
public class VideoLoader {
    private ArrayList<UserVideo> videos = new ArrayList<>();
    private int pages;
    private OnLoadedListener listener;

    public interface OnLoadedListener {
        void onLoaded(ArrayList<UserVideo> arrayList);
    }

    public VideoLoader(OnLoadedListener listener) {
        this.listener = listener;
    }

    public void getVideos() {
        pages = 0;
        this.videos.clear();
        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, String response) {
                pages = Integer.valueOf(response.substring(response.lastIndexOf("hdnPagetotal") + 21, response.lastIndexOf("hdnPagetotal") + 22));
                if (pages != 0) {
                    getVideosByPage(1);
                } else {
                    Toast.makeText(((Fragment) listener).getActivity(), "暂无数据", Toast.LENGTH_SHORT).show();
                    listener.onLoaded(videos);
                }
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

    public void getVideosByPage(final int page) {
        if (page <= pages) {
            final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
                public void onSuccess(int statusCode, Header[] headers, String response) {
                    getVideosBySinglePage(response);
                    getVideosByPage(page+1);
                }

                public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                    Log.d("test", "sssss");
                }
            };
            new Thread() {
                @Override
                public void run() {
                    try {
                        UserClient.post("/stpy/ajaxMyvideoAction!queryPagevideo.action?hdnPageNo=" + String.valueOf(page), null, handler);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else {
            listener.onLoaded(videos);
        }
    }

    public void getVideosBySinglePage(String html) {
        html = html.replace("\\", "");
        html = html.substring(2, html.length() - 2);
        JSONTokener parser = new JSONTokener(html);
        try {
            JSONObject channelObj = (JSONObject)parser.nextValue();
            JSONArray list = channelObj.getJSONArray("list");
            for (int i = 0; i < list.length(); i++) {
                JSONObject item = (JSONObject)list.get(i);
                UserVideo userVideo = new UserVideo();
                userVideo.url = item.getString("playurl");
                userVideo.id = item.getString("id");
                videos.add(userVideo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
