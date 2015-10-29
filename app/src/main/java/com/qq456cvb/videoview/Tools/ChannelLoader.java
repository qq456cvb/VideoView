package com.qq456cvb.videoview.Tools;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.util.Log;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qq456cvb.videoview.Utils.Channel;
import com.qq456cvb.videoview.Utils.Programme;
import com.qq456cvb.videoview.Utils.UserClient;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

/**
 * Created by qq456cvb on 8/28/15.
 */
public class ChannelLoader {

    private int pages = 0;
    private String category;
    private ArrayList<Channel> result = new ArrayList<>();
    private ArrayList<ArrayList<Programme>> programmes = new ArrayList<>();
    private OnLoadedListener listener;

    public interface OnLoadedListener {
        void onLoaded(ArrayList<Channel> arrayList);
        void onProgrammeLoaded(ArrayList<ArrayList<Programme>> programmes);
    }

    public ChannelLoader(OnLoadedListener listener) {
        this.listener = listener;
    }

    public void getChannelsBycategory(final String category, final int type) {
        this.category = category;
        this.pages = 0;
        this.result.clear();
        this.programmes.clear();
        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, String response) {
                pages = Integer.valueOf(response.substring(response.lastIndexOf("hdnPagetotal") + 21, response.lastIndexOf("hdnPagetotal") + 22));
                if (pages != 0) {
                    getChannelsByPage(1, type);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(((Fragment) listener).getActivity());
                    builder.setMessage("数据采集中");
                    builder.setTitle("提示");
                    builder.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
                    listener.onLoaded(result);
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
                    UserClient.get("/stpy/audioVisualAction!queryChannleInfo.action?hdnType=" + type + "&channel=" + category, null, handler);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void getChannelsByPage(final int page, final int type)
    {
        if (page <= pages) {
            final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
                public void onSuccess(int statusCode, Header[] headers, String response) {
                    getChannelsFromSinglePage(response);
                    getChannelsByPage(page+1, type);
                }

                public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                    Log.d("test", "sssss");
                }
            };
            new Thread() {
                @Override
                public void run() {
                    try {
                        RequestParams params = new RequestParams();
                        params.put("channel", category);
                        params.put("hdnPageNo", page);
                        params.put("hdnType", type);
                        UserClient.post("/stpy/ajaxVlcAction!channlePage.action", params, handler);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else {
            listener.onLoaded(result);
            if (result.size() > 0) {
                getProgrammesByChannel(result.get(0));
            } else {
                listener.onProgrammeLoaded(new ArrayList<ArrayList<Programme>>());
            }
        }
    }

    private void getChannelsFromSinglePage(String html)
    {
        html = html.replace("\\", "");
        html = html.substring(2, html.length() - 2);
        JSONTokener parser = new JSONTokener(html);
        try {
            JSONObject channelObj = (JSONObject)parser.nextValue();
            JSONArray list = channelObj.getJSONArray("list");
            for (int i = 0; i < list.length(); i++) {
                JSONObject item = (JSONObject)list.get(i);
                String name = item.getString("channle");
                String multicastIP = item.getString("zubo");
                Channel channel = new Channel(1, name, "", multicastIP);
                channel.id = item.getString("id");
                channel.hdnDyass = item.getString("dypass");
                channel.hdnType = item.getString("type");
                result.add(channel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getProgrammesByChannel (final Channel channel) {

        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, String response) {
                parseProgrammes(response);
                if (result.indexOf(channel) < result.size() - 1) {
                    getProgrammesByChannel(result.get(result.indexOf(channel) + 1));
                } else {
                    listener.onProgrammeLoaded(programmes);
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
                    UserClient.get("/stpy/ajaxVlcAction!queryEpgInfo.action?channel=" + channel.getName() + "&dateTime=", null, handler);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    private void parseProgrammes(String html) {
        if (!html.contains(":")) {
            ArrayList<Programme> programmeArrayList = new ArrayList<>();
            programmes.add(programmeArrayList);
            return;
        }
        ArrayList<Programme> programmeArrayList = new ArrayList<>();
        html = html.replace("\\", "");
        html = html.substring(2, html.length() - 2);
        try {
            JSONArray jsonArray = new JSONArray(html); //数据直接为一个数组形式，所以可以直接 用android提供的框架JSONArray读取JSON数据，转换成Array

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i); //每条记录又由
                // 几个Object对象组成
                String channel = item.getString("channle");
                String name = item.getString("program");
                String starttime = item.getString("starttime");
                Programme programme = new Programme();
                programme.name = name;
                programme.channel = getChannelByName(channel);
                programme.starttime = starttime;
                programmeArrayList.add(programme);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        programmes.add(programmeArrayList);
    }

    private Channel getChannelByName(String name) {
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).getName().equals(name)) {
                return result.get(i);
            }
        }
        return null;
    }
}
