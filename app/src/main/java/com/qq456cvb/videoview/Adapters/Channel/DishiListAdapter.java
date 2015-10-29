package com.qq456cvb.videoview.Adapters.Channel;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qq456cvb.videoview.R;
import com.qq456cvb.videoview.Subviews.RightChannelFragment;
import com.qq456cvb.videoview.Utils.Channel;
import com.qq456cvb.videoview.Utils.UserCity;

import java.util.ArrayList;

/**
 * Created by qq456cvb on 9/28/15.
 */
public class DishiListAdapter extends BaseExpandableListAdapter {

    private RightChannelFragment rightChannelFragment;
    private ArrayList<ArrayList<Channel>> channels;
    private ArrayList<UserCity> cities;
    public int currGroup = -1, currChild = -1;

    public DishiListAdapter(RightChannelFragment rightChannelFragment, ArrayList<UserCity>cities) {
        this.rightChannelFragment = rightChannelFragment;
        this.cities = cities;
    }

    public void updateChannels (ArrayList<ArrayList<Channel>> channels) {
        this.channels = channels;
    }

    //自己定义一个获得文字信息的方法
    TextView getTextView(float fontSize) {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        TextView textView = new TextView(rightChannelFragment.getActivity());
        textView.setLayoutParams(lp);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextSize(fontSize);
        textView.setTextColor(Color.BLACK);
        return textView;
    }


    //重写ExpandableListAdapter中的各个方法
    @Override
    public int getGroupCount() {
        // TODO Auto-generated method stub
        return cities.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return cities.get(groupPosition);
    }

//    public Channel getGroupChannel(int groupPosition) {
//        return channels.get(groupPosition);
//    }
    public Channel getChildChannel(int groupPosition, int childPosition) {
        return channels.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return groupPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // TODO Auto-generated method stub
//        if (programmes == null) {
//            return 0;
//        }
//        return programmes.get(groupPosition).size();
        return channels.get(groupPosition).size();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return channels.get(groupPosition).get(childPosition).getName();
//        return programmes.get(groupPosition).get(childPosition).starttime.substring(11,16) + " " + programmes.get(groupPosition).get(childPosition).name;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             final View convertView, ViewGroup parent) {
        View view = (rightChannelFragment.getActivity()).getLayoutInflater().inflate(R.layout.channel_list_item, null);
        if (isExpanded) {
            view.setBackgroundColor(0xFFEE4029);
        } else {
            view.setBackgroundColor(0x00000000);
        }
        final TextView textView = (TextView)view.findViewById(R.id.channel_item);
        textView.setText(((UserCity)getGroup(groupPosition)).city);
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        LinearLayout ll = new LinearLayout(rightChannelFragment.getActivity());
        ll.setOrientation(LinearLayout.HORIZONTAL);
        TextView textView = getTextView(30);
        textView.setText(getChild(groupPosition, childPosition)
                .toString());
        if (currGroup == groupPosition && currChild == childPosition) {
            ll.setBackgroundColor(0xFFEE4029);
        } else {
            ll.setBackgroundColor(0x00000000);
        }
        ll.addView(textView);
        return ll;
    }

    @Override
    public boolean isChildSelectable(int groupPosition,
                                     int childPosition) {
        // TODO Auto-generated method stub
        return true;
    }
};