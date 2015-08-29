package com.qq456cvb.videoview.Subviews;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.qq456cvb.videoview.Activities.MainActivity;
import com.qq456cvb.videoview.Adapters.Channel.ChannelListAdapter;
import com.qq456cvb.videoview.R;
import com.qq456cvb.videoview.Tools.ChannelLoader;
import com.qq456cvb.videoview.Utils.Channel;

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
        for (int i = 0; i < 30; ++i) {
            channels.add(String.valueOf(i));
        }
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
                                        int groupPosition, int childPosition, long id) {

                Toast.makeText(RightChannelFragment.this.getActivity(),
                        "你点击了" + channelListAdapter.getChild(groupPosition, childPosition),
                        Toast.LENGTH_SHORT).show();

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
                bundle.putString("value", channelListAdapter.getGroupMulticastIP(groupPosition));
                msg.setData(bundle);
                msg.sendToTarget();
            }
        });
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
