package com.qq456cvb.videoview.Adapters.Channel;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qq456cvb.videoview.Utils.Channel;

import java.util.ArrayList;

/**
 * Created by qq456cvb on 8/27/15.
 */
public class ChannelListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<Channel> channels;
    public ChannelListAdapter(Context context, ArrayList<Channel> channels) {
        this.context = context;
        this.channels = channels;
    }
    //设置组视图的显示文字
    private String[] generalsTypes;
    //子视图显示文字
    private String[][] generals = new String[][] {
            { "夏侯惇", "甄姬", "许褚", "郭嘉", "司马懿", "杨修" },
            { "马超", "张飞", "刘备", "诸葛亮", "黄月英", "赵云" },
            { "吕蒙", "陆逊", "孙权", "周瑜", "孙尚香" }

    };

    //自己定义一个获得文字信息的方法
    TextView getTextView() {
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
        ViewGroup.LayoutParams.FILL_PARENT, 64);
        TextView textView = new TextView(context);
        textView.setLayoutParams(lp);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding(36, 0, 0, 0);
        textView.setTextSize(20);
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
        return channels.get(groupPosition).getMulticastIP();
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return groupPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // TODO Auto-generated method stub
        return 6;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return "1";
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
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);
        TextView textView = getTextView();
        textView.setTextColor(Color.BLACK);
        textView.setText(getGroup(groupPosition).toString());
        ll.addView(textView);

        return ll;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
        boolean isLastChild, View convertView, ViewGroup parent) {

        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);
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
