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
    private ChannelListAdapter channelListAdapter;
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
        channelList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });
        this.cities.clear();
        this.cities.addAll(cities);
        dishiListAdapter.notifyDataSetChanged();

        channelList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != groupIndex && dishiListAdapter.getChildrenCount(groupPosition) > 0) {
                    expandGroup(groupIndex, false);
                }
                groupIndex = groupPosition;
            }
        });
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
                                Message msg = Message.obtain(MainActivity.handler);
                                msg.what = MainActivity.PROGRAMME;
                                Bundle bundle = new Bundle();
                                bundle.putString("type", "play");
                                bundle.putString("value", obj.getString("url"));
                                bundle.putFloat("startTime", startTime / timeLength);
//                        msg.obj = channelListAdapter.getGroupChannel(groupPosition);
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
                                    channelListAdapter.getChildProgramme(groupPosition, childPosition).starttime + "&hdnType="
                                    + GlobalApp.currentChannel.hdnType + "&urlNext=1", null, handler);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                return false;
            }
        });
        channelList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != groupIndex && channelListAdapter.getChildrenCount(groupPosition) > 0) {
                    expandGroup(groupIndex, false);
                }
                if (type == 1) {
                    type = 0;
                    return;
                } else {
                    if (!restore.isEmpty()) {
                        channelListAdapter.getProgrammes().clear();
                        channelListAdapter.getProgrammes().addAll(restore);
                        channelListAdapter.notifyDataSetChanged();
                        restore.clear();
                    }
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
        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, String response) {
                if (response.contains(":")) {
                    if (restore.isEmpty()) {
                        restore = (ArrayList<ArrayList<Programme>>) channelListAdapter.getProgrammes().clone();
                    }
                    parseProgrammes(response);
                    type = 1;
                    expandGroup(groupIndex, true);
                }
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

    public ArrayList<ArrayList<Channel>> getDishiRadio() {
        ArrayList<ArrayList<Channel>> channels = new ArrayList<>();

        ArrayList<Channel> dishi = new ArrayList<>();

        Channel channel = new Channel(4, "省新闻广播（103.6）", "", "http://120.40.101.185:7000/G*5010*/");
        channel.hdnType = "1";
        channel.hdnDyass = "9";
        channel.id = "5907";
        dishi.add(channel);
        channel = new Channel(4, "省音乐广播（91.3）", "", "http://120.40.101.185:7000/G*5003*/");
        channel.hdnType = "1";
        channel.hdnDyass = "2";
        channel.id =    "5908";
        dishi.add(channel);
        channel = new Channel(4, "省都市广播（103.6）", "", "http://120.40.101.185:7000/G*5010*/");
        channel.hdnType = "1";
        channel.hdnDyass = "9";
        channel.id =    "4197";
        dishi.add(channel);
        channel = new Channel(4, "经济之声（720）", "", "http://120.40.101.185:7000/G*5013*/");
        channel.hdnType = "1";
        channel.hdnDyass = "12";
        channel.id =    "4199";
        dishi.add(channel);
        channel = new Channel(4, "省经济广播（96.1）", "", "http://120.40.101.185:7000/G*5007*/");
        channel.hdnType = "1";
        channel.hdnDyass = "6";
        channel.id =    "4200";
        dishi.add(channel);
        channel = new Channel(4, "省交通广播（100.7）", "", "http://120.40.101.185:7000/G*5009*/");
        channel.hdnType = "1";
        channel.hdnDyass = "8";
        channel.id =    "4201";
        dishi.add(channel);
        channel = new Channel(4, "省交通广播（100.7）", "", "http://120.40.101.185:7000/G*5009*/");
        channel.hdnType = "1";
        channel.hdnDyass = "8";
        channel.id =    "4201";
        dishi.add(channel);
        channel = new Channel(4, "福州音乐广播（89.3）", "", "http://120.40.101.185:7000/G*5002*/");
        channel.hdnType = "1";
        channel.hdnDyass = "1";
        channel.id =    "4204";
        dishi.add(channel);
        channel = new Channel(4, "福州交通广播（87.6）", "", "http://120.40.101.185:7000/G*5001*/");
        channel.hdnType = "1";
        channel.hdnDyass = "0";
        channel.id =    "4205";
        dishi.add(channel);
        channel = new Channel(4, "福州左海之声（90.1）", "", "udp://@224.5.5.2:5003/");
        channel.hdnType = "1";
        channel.hdnDyass = "2";
        channel.id =    "4206";
        dishi.add(channel);
        channel = new Channel(4, "福州新闻广播（94.4）", "", "http://120.40.101.185:7000/G*5006*/");
        channel.hdnType = "1";
        channel.hdnDyass = "5";
        channel.id =    "4207";
        dishi.add(channel);
        channel = new Channel(4, "中国之声（93.5）", "", "http://120.40.101.185:7000/G*5005*/");
        channel.hdnType = "1";
        channel.hdnDyass = "4";
        channel.id =    "5905";
        dishi.add(channel);
        channel = new Channel(4, "音乐之声（92.6）", "", "http://120.40.101.185:7000/G*5004*/");
        channel.hdnType = "1";
        channel.hdnDyass = "3";
        channel.id =    "5906";
        dishi.add(channel);
        channels.add(dishi);

        dishi = new ArrayList<>();

        channel = new Channel(4, "厦门音乐(90.9)", "", "http://120.40.101.185:7000/G*5036*/");
        channel.hdnType = "1";
        channel.hdnDyass = "0";
        channel.id =    "5866";
        dishi.add(channel);
        channel = new Channel(4, "厦门经济广播（107.0）", "", "http://120.40.101.185:7000/G*5039*/");
        channel.hdnType = "1";
        channel.hdnDyass = "4";
        channel.id =    "4208";
        dishi.add(channel);
        channel = new Channel(4, "厦门综合(93.6)", "", "http://120.40.101.185:7000/G*5040*/");
        channel.hdnType = "1";
        channel.hdnDyass = "5";
        channel.id =    "5902";
        dishi.add(channel);
        channels.add(dishi);

        dishi = new ArrayList<>();

        channel = new Channel(4, "宁德人民(101.7)", "", "http://120.40.101.185:7000/G*5032*/");
        channel.hdnType = "1";
        channel.hdnDyass = "1";
        channel.id =    "4224";
        dishi.add(channel);
        channels.add(dishi);

        dishi = new ArrayList<>();

        channel = new Channel(4, "莆田综合（93.7）", "", "http://120.40.101.185:7000/G*5034*/");
        channel.hdnType = "1";
        channel.hdnDyass = "2";
        channel.id =    "4225";
        dishi.add(channel);
        channel = new Channel(4, "莆田文艺（103.0）", "", "http://120.40.101.185:7000/G*5033*/");
        channel.hdnType = "1";
        channel.hdnDyass = "1";
        channel.id =    "4226";
        dishi.add(channel);
        channels.add(dishi);

        dishi = new ArrayList<>();

        channel = new Channel(4, "泉州综合（88.9）", "", "http://120.40.101.185:7000/G*5021*/");
        channel.hdnType = "1";
        channel.hdnDyass = "0";
        channel.id =    "4212";
        dishi.add(channel);
        channel = new Channel(4, "泉州交通（90.4）", "", "http://120.40.101.185:7000/G*5022*/");
        channel.hdnType = "1";
        channel.hdnDyass = "1";
        channel.id =    "4213";
        dishi.add(channel);
        channel = new Channel(4, "泉州经济（92.3）", "", "http://120.40.101.185:7000/G*5023*/");
        channel.hdnType = "1";
        channel.hdnDyass = "2";
        channel.id =    "4214";
        dishi.add(channel);
        channels.add(dishi);

        dishi = new ArrayList<>();

        channel = new Channel(4, "漳州综合（96.2）", "", "http://120.40.101.185:7000/G*5031*/");
        channel.hdnType = "1";
        channel.hdnDyass = "1";
        channel.id =    "4216";
        dishi.add(channel);
        channel = new Channel(4, "漳州文艺（92.7）", "", "http://120.40.101.185:7000/G*5030*/");
        channel.hdnType = "1";
        channel.hdnDyass = "0";
        channel.id =    "4217";
        dishi.add(channel);
        channels.add(dishi);

        return channels;
    }

    public ArrayList<ArrayList<Channel>> getDishiTv() {
        ArrayList<ArrayList<Channel>> channels = new ArrayList<>();

        ArrayList<Channel> dishi = new ArrayList<>();

        Channel channel = new Channel(4, "福州一套", "", "http://120.40.101.185:7000/D*5001*/");
        channel.hdnType = "3";
        channel.hdnDyass = "0";
        channel.id = "4170";
        dishi.add(channel);
        channel = new Channel(4, "福州二套", "", "http://120.40.101.185:7000/D*5002*/");
        channel.hdnType = "3";
        channel.hdnDyass = "1";
        channel.id = "4171";
        dishi.add(channel);
        channel = new Channel(4, "福州三套", "", "http://120.40.101.185:7000/D*5003*/");
        channel.hdnType = "3";
        channel.hdnDyass = "2";
        channel.id = "4172";
        dishi.add(channel);
        channel = new Channel(4, "福州四套", "", "http://120.40.101.185:7000/D*5004*/");
        channel.hdnType = "3";
        channel.hdnDyass = "3";
        channel.id = "4173";
        dishi.add(channel);
        channels.add(dishi);

        dishi = new ArrayList<>();

        channel = new Channel(4, "厦门一套", "", "http://120.40.101.185:7000/D*5041*/");
        channel.hdnType = "3";
        channel.hdnDyass = "0";
        channel.id = "4175";
        dishi.add(channel);
        channel = new Channel(4, "厦门二套", "", "http://120.40.101.185:7000/D*5042*/");
        channel.hdnType = "3";
        channel.hdnDyass = "1";
        channel.id = "4176";
        dishi.add(channel);
        channel = new Channel(4, "厦门三套", "", "http://120.40.101.185:7000/D*5043*/");
        channel.hdnType = "3";
        channel.hdnDyass = "2";
        channel.id = "4177";
        dishi.add(channel);
        channel = new Channel(4, "厦门四套", "", "http://120.40.101.185:7000/D*5044*/");
        channel.hdnType = "3";
        channel.hdnDyass = "3";
        channel.id = "4178";
        dishi.add(channel);
        channels.add(dishi);

        dishi = new ArrayList<>();

        channel = new Channel(4, "宁德一套", "", "http://120.40.101.185:7000/D*5028*/");
        channel.hdnType = "3";
        channel.hdnDyass = "0";
        channel.id = "4191";
        dishi.add(channel);
        channel = new Channel(4, "宁德二套", "", "http://120.40.101.185:7000/D*5029*/");
        channel.hdnType = "3";
        channel.hdnDyass = "1";
        channel.id = "4192";
        dishi.add(channel);
        channels.add(dishi);

        dishi = new ArrayList<>();

        channel = new Channel(4, "莆田一套", "", "http://120.40.101.185:7000/D*5024*/");
        channel.hdnType = "3";
        channel.hdnDyass = "0";
        channel.id = "4193";
        dishi.add(channel);
        channel = new Channel(4, "莆田二套", "", "http://120.40.101.185:7000/D*5025*/");
        channel.hdnType = "3";
        channel.hdnDyass = "1";
        channel.id = "4194";
        dishi.add(channel);
        channels.add(dishi);

        dishi = new ArrayList<>();

        channel = new Channel(4, "泉州一套", "", "http://120.40.101.185:7000/D*5030*/");
        channel.hdnType = "3";
        channel.hdnDyass = "0";
        channel.id = "4179";
        dishi.add(channel);
        channel = new Channel(4, "泉州二套", "", "http://120.40.101.185:7000/D*5031*/");
        channel.hdnType = "3";
        channel.hdnDyass = "1";
        channel.id = "4180";
        dishi.add(channel);
        channel = new Channel(4, "泉州三套", "", "http://120.40.101.185:7000/D*5032*/");
        channel.hdnType = "3";
        channel.hdnDyass = "2";
        channel.id = "4181";
        dishi.add(channel);
        channel = new Channel(4, "泉州四套", "", "http://120.40.101.185:7000/D*5033*/");
        channel.hdnType = "3";
        channel.hdnDyass = "3";
        channel.id = "4182";
        dishi.add(channel);
        channels.add(dishi);

        dishi = new ArrayList<>();

        channel = new Channel(4, "漳州一套", "", "http://120.40.101.185:7000/D*5034*/");
        channel.hdnType = "3";
        channel.hdnDyass = "0";
        channel.id = "4183";
        dishi.add(channel);
        channel = new Channel(4, "漳州二套", "", "http://120.40.101.185:7000/D*5035*/");
        channel.hdnType = "3";
        channel.hdnDyass = "1";
        channel.id = "4184";
        dishi.add(channel);
        channels.add(dishi);

        return channels;
    }
}
