package com.qq456cvb.videoview.Adapters.Profile;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by qq456cvb on 10/31/15.
 */
public class ProfileGalleryAdapter extends PagerAdapter {

    ArrayList<View> viewLists;
    private int size;

    public ProfileGalleryAdapter(ArrayList<View> views) {
        viewLists = views;
        size = viewLists == null ? 0 : viewLists.size();
    }

    public void setListViews(ArrayList<View> listViews) {// 自己写的一个方法用来添加数据  这个可是重点啊
        this.viewLists = listViews;
        size = listViews == null ? 0 : listViews.size();
    }

    //获得size
    @Override
    public int getCount() {
        return size;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    //销毁Item
    @Override
    public void destroyItem(View view, int position, Object object) {
        ((ViewPager) view).removeView(viewLists.get(position));
    }

    //实例化Item
    @Override
    public Object instantiateItem(View view, int position) {
        ((ViewPager) view).addView(viewLists.get(position), 0);
        return viewLists.get(position);
    }
}
