package com.qq456cvb.videoview.Tools;

import android.util.Log;

import com.loopj.android.http.TextHttpResponseHandler;
import com.qq456cvb.videoview.Utils.Channel;
import com.qq456cvb.videoview.Utils.UserClient;

import org.apache.http.Header;

import java.util.ArrayList;

/**
 * Created by qq456cvb on 9/13/15.
 */
public class ProgrammeLoader {
    private String programme;
    private ArrayList<Channel> result = new ArrayList<>();
    private OnLoadedListener listener;

    public interface OnLoadedListener {
        void onLoaded(ArrayList<Channel> arrayList);
    }

    public ProgrammeLoader(OnLoadedListener listener) {
        this.listener = listener;
    }

    public void getProgrammeByChannel(final String programme) {
        this.programme = programme;
        this.result.clear();
        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, String response) {
                parseResult(response);
            }

            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Log.d("test", "sssss");
            }
        };
        new Thread() {
            @Override
            public void run() {
                try {
                    UserClient.get("/stpy/ajaxVlcAction!queryEpgInfo.action?channel="+ programme + "&dateTime=", null, handler);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void parseResult(String response) {
//        html = html.replace("\\", "");
//        html = html.substring(2, html.length() - 2);
//        JSONTokener parser = new JSONTokener(html);
//        try {
//            JSONObject channelObj = (JSONObject) parser.nextValue();
//            JSONArray list = channelObj.getJSONArray("list");
//            for (int i = 0; i < list.length(); i++) {
//                JSONObject item = (JSONObject) list.get(i);
//                String name = item.getString("channle");
//                String multicastIP = item.getString("zubo");
//                Channel channel = new Channel(1, name, "", multicastIP);
//                result.add(channel);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }
}
