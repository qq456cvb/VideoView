package com.qq456cvb.videoview.Adapters.Channel;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qq456cvb.videoview.R;
import com.qq456cvb.videoview.Subviews.RightChannelFragment;
import com.qq456cvb.videoview.Utils.Channel;
import com.qq456cvb.videoview.Utils.Programme;

import java.util.ArrayList;

/**
 * Created by qq456cvb on 8/27/15.
 */
public class ChannelListAdapter extends BaseExpandableListAdapter {

    private RightChannelFragment rightChannelFragment;
    private ArrayList<Channel> channels;
    private ArrayList<ArrayList<Programme>> programmes;
    public ChannelListAdapter(RightChannelFragment rightChannelFragment, ArrayList<Channel> channels) {
        this.rightChannelFragment = rightChannelFragment;
        this.channels = channels;
    }

    public void updateProgramme (ArrayList<ArrayList<Programme>> programmes) {
        this.programmes = programmes;
    }

    //自己定义一个获得文字信息的方法
    TextView getTextView() {
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, 150);
        TextView textView = new TextView(rightChannelFragment.getActivity());
        textView.setLayoutParams(lp);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextSize(40);
        textView.setTextColor(Color.BLACK);
        return textView;
    }


    //重写ExpandableListAdapter中的各个方法
    @Override
    public int getGroupCount() {
        // TODO Auto-generated method stub
        return channels.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return channels.get(groupPosition).getName();
    }

    public Channel getGroupChannel(int groupPosition) {
        return channels.get(groupPosition);
    }
    public Programme getChildProgramme(int groupPosition, int childPosition) {
        return programmes.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return groupPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // TODO Auto-generated method stub
        if (programmes == null) {
            return 0;
        }
        return programmes.get(groupPosition).size();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return programmes.get(groupPosition).get(childPosition).starttime.substring(11,16) + " " + programmes.get(groupPosition).get(childPosition).name;
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
        final TextView textView = (TextView)view.findViewById(R.id.channel_item);
        textView.setText(getGroup(groupPosition).toString());
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                rightChannelFragment.expandGroup(groupPosition);
//            }
//        });
//        view.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return false;
//            }
//        });
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
        boolean isLastChild, View convertView, ViewGroup parent) {

        LinearLayout ll = new LinearLayout(rightChannelFragment.getActivity());
        ll.setOrientation(LinearLayout.HORIZONTAL);
        TextView textView = getTextView();
        textView.setText(getChild(groupPosition, childPosition)
        .toString());
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
