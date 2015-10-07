package com.qq456cvb.videoview.Tools;

import android.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qq456cvb.videoview.Utils.Channel;
import com.qq456cvb.videoview.Utils.UserCity;
import com.qq456cvb.videoview.Utils.UserClient;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

/**
 * Created by qq456cvb on 10/7/15.
 */
public class DishiLoader {
    private int pages = 0;
    private String category;
    private ArrayList<UserCity> cities = new ArrayList<>();
    private ArrayList<ArrayList<Channel>> channels = new ArrayList<>();
    private OnLoadedListener listener;

    public interface OnLoadedListener {
        void onDishiLoaded(ArrayList<UserCity> arrayList);
        void onDishiChannelLoaded(ArrayList<ArrayList<Channel>> channels);
    }

    public DishiLoader(OnLoadedListener listener) {
        this.listener = listener;
    }

    public void getChannelsBycategory(final String category, final int type) {
        this.category = category;
        this.pages = 0;
        this.channels.clear();
        this.cities.clear();
        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, String response) {
                pages = Integer.valueOf(response.substring(response.lastIndexOf("hdnPagetotal") + 21, response.lastIndexOf("hdnPagetotal") + 22));
                if (pages != 0) {
                    getCitiesByPage(1, type);
                } else {
                    Toast.makeText(((Fragment) listener).getActivity(), "数据菜集中", Toast.LENGTH_SHORT).show();
                    listener.onDishiLoaded(cities);
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
                    UserClient.get("/stpy/videoDownloadAction!queryChannleInfo.action?hdnType=3&channel=dishi", null, handler);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void getCitiesByPage(final int page, final int type)
    {
        if (page <= pages) {
            final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
                public void onSuccess(int statusCode, Header[] headers, String response) {
                    getCitiesFromSinglePage(response);
                    getCitiesByPage(page + 1, type);
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
            listener.onDishiLoaded(cities);
            if (cities.size() > 0) {
                getChannelByCity(cities.get(0));
            } else {
                listener.onDishiChannelLoaded(new ArrayList<ArrayList<Channel>>());
            }
        }
    }

    private void getCitiesFromSinglePage(String html)
    {
        html = html.replace("\\", "");
        html = html.substring(2, html.length() - 2);
        JSONTokener parser = new JSONTokener(html);
        try {
            JSONObject channelObj = (JSONObject)parser.nextValue();
            JSONArray list = channelObj.getJSONArray("list");
            for (int i = 0; i < list.length(); i++) {
                JSONObject item = (JSONObject)list.get(i);
                UserCity userCity = new UserCity();
                userCity.city = item.getString("city");
                userCity.name = item.getString("name");
                userCity.type = item.getString("type");
                cities.add(userCity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getChannelByCity (final UserCity userCity) {

        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, String response) {
                parseChannels(response);
                if (cities.indexOf(userCity) < cities.size() - 1) {
                    getChannelByCity(cities.get(cities.indexOf(userCity) + 1));
                } else {
                    listener.onDishiChannelLoaded(channels);
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

                    UserClient.get("/stpy/ajaxVlcAction!getChannelInfoByName.action?name="+userCity.name+"&hdnType="+userCity.type, null, handler);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    private void parseChannels(String html) {
        if (!html.contains(":")) {
            ArrayList<Channel> channelArrayList = new ArrayList<>();
            channels.add(channelArrayList);
            return;
        }
        ArrayList<Channel> channelArrayList = new ArrayList<>();
        html = html.replace("\\", "");
        html = html.substring(2, html.length() - 2);
        try {
            JSONArray jsonArray = new JSONArray(html); //数据直接为一个数组形式，所以可以直接 用android提供的框架JSONArray读取JSON数据，转换成Array

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i); //每条记录又由
                // 几个Object对象组成
                Channel channel = new Channel(1, item.getString("channle"), "", item.getString("zubo"));
                channel.id = item.getString("id");
                channel.hdnDyass = item.getString("dypass");
                channel.hdnType = item.getString("type");
                channelArrayList.add(channel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        channels.add(channelArrayList);
    }
}
