package com.qq456cvb.videoview.Subviews;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.loopj.android.http.TextHttpResponseHandler;
import com.qq456cvb.videoview.Activities.MainActivity;
import com.qq456cvb.videoview.Adapters.Channel.ChannelListAdapter;
import com.qq456cvb.videoview.Adapters.Channel.DishiListAdapter;
import com.qq456cvb.videoview.Application.GlobalApp;
import com.qq456cvb.videoview.R;
import com.qq456cvb.videoview.Tools.ChannelLoader;
import com.qq456cvb.videoview.Tools.DishiLoader;
import com.qq456cvb.videoview.Utils.Channel;
import com.qq456cvb.videoview.Utils.Programme;
import com.qq456cvb.videoview.Utils.UserCity;
import com.qq456cvb.videoview.Utils.UserClient;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by qq456cvb on 8/17/15.
 */
public class RightChannelFragment extends Fragment implements ChannelLoader.OnLoadedListener, DishiLoader.OnLoadedListener {
    public final static int TV = 3;
    public final static int RADIO = 1;
    private static int counter = 0;

    private ArrayList<String> channels = new ArrayList<>();
    private ArrayList<Channel> origins = new ArrayList<>();
    private ArrayList<UserCity> cities = new ArrayList<>();
    private ArrayList<ArrayList<Programme>> restore = new ArrayList<>();
    public ChannelListAdapter channelListAdapter;
    private DishiListAdapter dishiListAdapter;
    private ExpandableListView channelList;
    private ChannelLoader channelLoader = new ChannelLoader(this);
    private DishiLoader dishiLoader = new DishiLoader(this);
    private int groupIndex = 0;
    private View view;
    private int type = 0; // 0 means manually expand, 1 means auto expand
    ProgressDialog pdl;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        view = inflater.inflate(R.layout.right_layout_channel, container, false);
        channelList = (ExpandableListView) view.findViewById(R.id.list);
        channelListAdapter = new ChannelListAdapter(this, origins);
        dishiListAdapter = new DishiListAdapter(this, cities);
        channelList.setAdapter(channelListAdapter);

        pdl = ProgressDialog.show(this.getActivity(), "获取中...", "请等待...", true, false);
        channelLoader.getChannelsBycategory("yangshi", TV);


        return view;
    }

    public void onDishiLoaded(final ArrayList<UserCity> cities) {
        channelList.setAdapter(dishiListAdapter);
        expandGroup(groupIndex, false);
        if (cities.isEmpty()) {
            this.cities.clear();
            this.cities.addAll(cities);
            dishiListAdapter.notifyDataSetChanged();
//            pdl.dismiss();
            return;
        }
        channelList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition,
                                        long id) {
                if (!parent.isGroupExpanded(groupPosition)) {
                    expandGroup(groupPosition, true);
                    if (groupPosition != groupIndex) {
                        expandGroup(groupIndex, false);
                    }
                    groupIndex = groupPosition;
                } else {
                    expandGroup(groupPosition, false);
                }
                return true;
            }
        });
        this.cities.clear();
        this.cities.addAll(cities);
        dishiListAdapter.notifyDataSetChanged();

        channelList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        final int groupPosition, final int childPosition, long id) {
                dishiListAdapter.currGroup = groupPosition;
                dishiListAdapter.currChild = childPosition;
                dishiListAdapter.notifyDataSetChanged();
                Toast.makeText(RightChannelFragment.this.getActivity(),
                        "你点击了" + dishiListAdapter.getChildChannel(groupPosition, childPosition).getName(),
                        Toast.LENGTH_SHORT).show();
                Message msg = Message.obtain(MainActivity.handler);
                msg.what = MainActivity.CHANNEL;
                Bundle bundle = new Bundle();
                bundle.putString("type", "play");
                bundle.putString("value", dishiListAdapter.getChildChannel(groupPosition, childPosition).getMulticastIP());
                bundle.putFloat("startTime", 0);
                msg.obj = dishiListAdapter.getChildChannel(groupPosition, childPosition);
                msg.setData(bundle);
                msg.sendToTarget();

                return false;
            }
        });
    }

    public void onDishiChannelLoaded(final ArrayList<ArrayList<Channel>> channels) {
        if (channels.isEmpty()) {
            dishiListAdapter.updateChannels(channels);
            dishiListAdapter.notifyDataSetChanged();
            pdl.dismiss();
            return;
        }
        synchronized (this) {
            Message msg = Message.obtain(MainActivity.handler);
            msg.what = MainActivity.CHANNEL;
            Bundle bundle = new Bundle();
            bundle.putString("type", "play");
            bundle.putString("value", channels.get(0).get(0).getMulticastIP());
            bundle.putFloat("startTime", 0);
            msg.obj = channels.get(0).get(0);
            msg.setData(bundle);
            msg.sendToTarget();
        }
        dishiListAdapter.updateChannels(channels);
        dishiListAdapter.notifyDataSetChanged();
        pdl.dismiss();
    }

    public void onLoaded(final ArrayList<Channel> channels) {
//        channelListAdapter = new ChannelListAdapter(this, channels);
        channelList.setAdapter(channelListAdapter);
        expandGroup(groupIndex, false);
        if (channels.isEmpty()) {
            origins.clear();
            origins.addAll(channels);
            channelListAdapter.notifyDataSetChanged();
            pdl.dismiss();
            return;
        }
        origins.clear();
        origins.addAll(channels);
        channelListAdapter.notifyDataSetChanged();

        synchronized (this) {
            if (counter == 0 || counter > 2) {
                Message msg = Message.obtain(MainActivity.handler);
                msg.what = MainActivity.CHANNEL;
                Bundle bundle = new Bundle();
                bundle.putString("type", "play");
                bundle.putString("value", channels.get(0).getMulticastIP());
                bundle.putFloat("startTime", 0);
                msg.obj = channels.get(0);
                msg.setData(bundle);
                msg.sendToTarget();
            }
        }
        counter++;

        channelList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        final int groupPosition, final int childPosition, long id) {
                channelListAdapter.currGroup = groupPosition;
                channelListAdapter.currChild = childPosition;
                channelListAdapter.notifyDataSetChanged();
                Toast.makeText(RightChannelFragment.this.getActivity(),
                        "你点击了" + channelListAdapter.getChild(groupPosition, childPosition),
                        Toast.LENGTH_SHORT).show();
                final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
                    public void onSuccess(int statusCode, Header[] headers, String response) {
                        if (response.contains("\\")) {
                            response = response.replace("\\", "");
                            response = response.substring(1, response.length() - 1);
                            try {
                                JSONObject obj = new JSONObject(response);
                                float startTime = obj.getInt("startTime");
                                float timeLength = obj.getInt("timeLength");
                                GlobalApp.endTime = obj.getString("endTime");
                                Message msg = Message.obtain(MainActivity.handler);
                                msg.what = MainActivity.PROGRAMME;
                                Bundle bundle = new Bundle();
                                bundle.putString("type", "play");
                                bundle.putString("value", obj.getString("url"));
                                bundle.putFloat("startTime", startTime / timeLength);
                                msg.obj = channelListAdapter.getChildProgramme(groupPosition, childPosition);
                                msg.setData(bundle);
                                msg.sendToTarget();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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
                            Programme programme = channelListAdapter.getChildProgramme(groupPosition, childPosition);
                            String id = programme.channel.id;
                            UserClient.get("/stpy/ajaxVlcAction!getUrl.action?id=" + id + "&dateTime=" +
                                    programme.starttime + "&hdnType="
                                    + GlobalApp.currentChannel.hdnType + "&urlNext=1", null, handler);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                return false;
            }
        });
        channelList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition,
                                     long id) {
                if (!parent.isGroupExpanded(groupPosition)) {
                    expandGroup(groupPosition, true);
                    channelListAdapter.currGroup = groupPosition;
                    channelListAdapter.currChild = -1;
                    channelListAdapter.notifyDataSetChanged();
                    if (groupPosition != groupIndex) {
                        expandGroup(groupIndex, false);
                    }
                    if (!restore.isEmpty()) {
                        channelListAdapter.getProgrammes().clear();
                        channelListAdapter.getProgrammes().addAll(restore);
                        channelListAdapter.notifyDataSetChanged();
                        restore.clear();
                    }
                    Toast.makeText(RightChannelFragment.this.getActivity(),
                            "你点击了" + channelListAdapter.getGroup(groupPosition),
                            Toast.LENGTH_SHORT).show();
                    groupIndex = groupPosition;
                    Message msg = Message.obtain(MainActivity.handler);
                    msg.what = MainActivity.CHANNEL;
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "play");
                    bundle.putString("value", channelListAdapter.getGroupChannel(groupPosition).getMulticastIP());
                    bundle.putFloat("startTime", 0);
                    msg.obj = channelListAdapter.getGroupChannel(groupPosition);
                    msg.setData(bundle);
                    msg.sendToTarget();
                } else {
                    expandGroup(groupPosition, false);
                }
                return true;
            }
        });
    }

    public void onProgrammeLoaded(ArrayList<ArrayList<Programme>> programmes) {
        channelListAdapter.updateProgramme(programmes);
        channelListAdapter.notifyDataSetChanged();
        pdl.dismiss();
    }

    public void changeCategory(String category, int type) {
        pdl = ProgressDialog.show(this.getActivity(), "获取中...", "请等待...", true, false);
        expandGroup(groupIndex, false);
        restore.clear();
        if (category.equals("dishi")) {
            dishiLoader.getChannelsBycategory(category, type);
        } else {
            channelLoader.getChannelsBycategory(category, type);
        }
    }

    private void parseProgrammes(String html) {
        if (!html.contains(":")) {
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
                String name = item.getString("program");
                String starttime = item.getString("starttime");
                Programme programme = new Programme();
                programme.name = name;
                programme.starttime = starttime;
                programme.channel = GlobalApp.currentChannel;
                programmeArrayList.add(programme);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        channelListAdapter.getProgrammes().set(groupIndex, programmeArrayList);
        channelListAdapter.notifyDataSetChanged();
    }

    public void loadHistoryEDG(final Channel channel, final String time) {
        pdl = ProgressDialog.show(this.getActivity(), "获取中...", "请等待...", true, false);
        expandGroup(groupIndex, false);
        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, String response) {
                if (response.contains(":")) {
                    if (restore.isEmpty()) {
                        restore = (ArrayList<ArrayList<Programme>>) channelListAdapter.getProgrammes().clone();
                    }
                    parseProgrammes(response);
                    type = 1;
                }
                expandGroup(groupIndex, true);
                pdl.dismiss();
            }

            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Log.d("test", "sssss");
            }
        };
        new Thread() {
            @Override
            public void run() {
                try {
                    UserClient.get("/stpy/ajaxVlcAction!queryEpgInfo.action?channel="+channel.getName()+"&dateTime="+time, null, handler);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

//    public void changeDishi(int type) {
//        ArrayList<ArrayList<Channel>> channels;
//        if (type == 3) {
//            channels = getDishiTv();
//        } else {
//            channels = getDishiRadio();
//        }
//
//
//        dishiListAdapter = new DishiListAdapter(this, channels);
//        channelList.setAdapter(dishiListAdapter);
//
//        Message msg = Message.obtain(MainActivity.handler);
//        msg.what = MainActivity.CHANNEL;
//        Bundle bundle = new Bundle();
//        bundle.putString("type", "play");
//        bundle.putString("value", channels.get(0).get(0).getMulticastIP());
//        bundle.putFloat("startTime", 0);
//        msg.obj = channels.get(0).get(0);
//        msg.setData(bundle);
//        msg.sendToTarget();
//        channelList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//            @Override
//            public void onGroupExpand(int groupPosition) {
//
//            }
//        });
//
//        channelList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v,
//                                        final int groupPosition, final int childPosition, long id) {
//
//                Toast.makeText(RightChannelFragment.this.getActivity(),
//                        "你点击了" + dishiListAdapter.getChildChannel(groupPosition, childPosition).getName(),
//                        Toast.LENGTH_SHORT).show();
//                Message msg = Message.obtain(MainActivity.handler);
//                msg.what = MainActivity.DISHI;
//                Bundle bundle = new Bundle();
//                bundle.putString("type", "play");
//                bundle.putString("value", dishiListAdapter.getChildChannel(groupPosition, childPosition).getMulticastIP());
//                bundle.putFloat("startTime", 0);
//                msg.obj = dishiListAdapter.getChildChannel(groupPosition, childPosition);
//                msg.setData(bundle);
//                msg.sendToTarget();
//
//                return false;
//            }
//        });
//    }

    public void expandGroup(int groupPosition, boolean expand)
    {
        if(!expand)
            channelList.collapseGroup(groupPosition);
        else
            channelList.expandGroup(groupPosition);
    }
}
