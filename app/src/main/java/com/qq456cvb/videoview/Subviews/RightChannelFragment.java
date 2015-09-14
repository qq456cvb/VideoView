package com.qq456cvb.videoview.Subviews;

import android.app.Fragment;
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
import com.qq456cvb.videoview.R;
import com.qq456cvb.videoview.Tools.ChannelLoader;
import com.qq456cvb.videoview.Utils.Channel;
import com.qq456cvb.videoview.Utils.Programme;
import com.qq456cvb.videoview.Utils.UserClient;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by qq456cvb on 8/17/15.
 */
public class RightChannelFragment extends Fragment implements ChannelLoader.OnLoadedListener {
    private ArrayList<String> channels = new ArrayList<>();
    private ChannelListAdapter channelListAdapter;
    private ExpandableListView channelList;
    private ChannelLoader channelLoader = new ChannelLoader(this);
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        view = inflater.inflate(R.layout.right_layout_channel, container, false);
        channelList = (ExpandableListView) view.findViewById(R.id.list);
        channelLoader.getChannelsBycategory("yangshi");

        return view;
    }

    public void onLoaded(ArrayList<Channel> channels) {
        channelListAdapter = new ChannelListAdapter(this, channels);
        channelList.setAdapter(channelListAdapter);

        channelList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        final int groupPosition, final int childPosition, long id) {

                Toast.makeText(RightChannelFragment.this.getActivity(),
                        "你点击了" + channelListAdapter.getChild(groupPosition, childPosition),
                        Toast.LENGTH_SHORT).show();
                final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
                    public void onSuccess(int statusCode, Header[] headers, String response) {
                        response = response.replace("\\", "");
                        response = response.substring(1, response.length() - 1);
                        try {
                            JSONObject obj = new JSONObject(response);
                            Message msg = Message.obtain(MainActivity.handler);
                            msg.what = MainActivity.PROGRAMME;
                            Bundle bundle = new Bundle();
                            bundle.putString("type", "play");
                            bundle.putString("value", obj.getString("url"));
//                        msg.obj = channelListAdapter.getGroupChannel(groupPosition);
                            msg.setData(bundle);
                            msg.sendToTarget();
                        }catch (JSONException e) {
                            e.printStackTrace();
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
                            int id = Integer.valueOf(programme.channel.substring(5));
                            if (id > 4) {
                                id = id - 1;
                            } else {
                                if (id == 4) {
                                    id = 7;
                                }
                            }
                            UserClient.get("/stpy/ajaxVlcAction!getUrl.action?id=" + String.valueOf(id) + "&dateTime="+
                                    channelListAdapter.getChildProgramme(groupPosition, childPosition).starttime+"&hdnType=3&urlNext=1", null, handler);
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
                Toast.makeText(RightChannelFragment.this.getActivity(),
                        "你点击了" + channelListAdapter.getGroup(groupPosition),
                        Toast.LENGTH_SHORT).show();
                Message msg = Message.obtain(MainActivity.handler);
                msg.what = MainActivity.CHANNEL;
                Bundle bundle = new Bundle();
                bundle.putString("type", "play");
                bundle.putString("value", channelListAdapter.getGroupChannel(groupPosition).getMulticastIP());
                msg.obj = channelListAdapter.getGroupChannel(groupPosition);
                msg.setData(bundle);
                msg.sendToTarget();
            }
        });
    }

    public void onProgrammeLoaded(ArrayList<ArrayList<Programme>> programmes) {
        channelListAdapter.updateProgramme(programmes);
        channelListAdapter.notifyDataSetChanged();
    }

    public void changeCategory(String category) {
        channelLoader.getChannelsBycategory(category);
    }

    public void expandGroup(int groupPosition)
    {
        if(channelList.isGroupExpanded(groupPosition))
            channelList.collapseGroup(groupPosition);
        else
            channelList.expandGroup(groupPosition);
    }
}
