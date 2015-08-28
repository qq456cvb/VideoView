package com.qq456cvb.videoview.Tools;

import android.util.Log;

import com.loopj.android.http.TextHttpResponseHandler;
import com.qq456cvb.videoview.Utils.Channel;
import com.qq456cvb.videoview.Utils.UserClient;

import org.apache.http.Header;

import java.util.ArrayList;

/**
 * Created by qq456cvb on 8/28/15.
 */
public class ChannelLoader {

    private int pages = 0;
    private String category;
    private ArrayList<Channel> result = new ArrayList<>();
    private OnLoadedListener listener;

    public interface OnLoadedListener {
        void onLoaded(ArrayList<Channel> arrayList);
    }

    public ChannelLoader(OnLoadedListener listener) {
        this.listener = listener;
    }

    public void getChannelsBycategory(final String category) {
        this.category = category;
        this.pages = 0;
        this.result.clear();
        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, String response) {
                pages = Integer.valueOf(response.substring(response.lastIndexOf("hdnPagetotal") + 21, response.lastIndexOf("hdnPagetotal") + 22));
                getChannelsByPage(0);
            }

            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Log.d("test", "sssss");
            }
        };
        new Thread() {
            @Override
            public void run() {
                try {
                    UserClient.get("/stpy/audioVisualAction!queryChannleInfo.action?hdnType=3&channel=" + category, null, handler);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        Log.d("test", "~~~~~~~~~~~~~~~~~~~going on...");
    }

    private void getChannelsByPage(final int page)
    {
        if (page < pages) {
            final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
                public void onSuccess(int statusCode, Header[] headers, String response) {
                    getChannelsFromSinglePage(response);
                    getChannelsByPage(page+1);
                }

                public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                    Log.d("test", "sssss");
                }
            };
            new Thread() {
                @Override
                public void run() {
                    try {
                        UserClient.get("/stpy/audioVisualAction!queryChannleInfo.action?hdnType=3&channel=" + category + "&hdnPageNo=" + page
                                , null, handler);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else {
            listener.onLoaded(result);
        }
    }

    private void getChannelsFromSinglePage(String html)
    {
        if (html.contains("id=\"channelName")) {
            int begin = html.indexOf("id=\"channelName");
            int end = begin + html.substring(html.indexOf("id=\"channelName")).indexOf("/li");
            String block = html.substring(begin, end);
            block = block.substring(block.indexOf("viewplay") + 10);
            String multicastIP = block.substring(0, block.indexOf("'"));
            Channel channel = new Channel(1, "test", "", multicastIP);
            result.add(channel);
            String test = html.substring(end);
            getChannelsFromSinglePage(test);
        }
    }
}
