package com.qq456cvb.videoview.CustomWidgets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.qq456cvb.videoview.Activities.MainActivity;
import com.qq456cvb.videoview.Adapters.Profile.ProfileGalleryAdapter;
import com.qq456cvb.videoview.R;
import com.qq456cvb.videoview.Subviews.Profile.ProfileImageFragment;

import java.util.ArrayList;

/**
 * Created by qq456cvb on 9/26/15.
 */
public class BigImageDialog extends DialogFragment{

    private View view;
    private ViewPager viewPager;
    private ArrayList<View> views;
    private ArrayList<String> urls;

    private int count;
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        urls = getArguments().getStringArrayList("urls");
        int idx = getArguments().getInt("idx");
        FragmentManager fm = getActivity().getFragmentManager();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if(view!=null){
            //!fix the bug: reload a dialog will cause a crash
            ViewGroup parent=(ViewGroup)view.getParent();
            if(parent!=null){
                parent.removeView(view);
            }
        }
        try{
            view=inflater.inflate(R.layout.large_image,null);
        }catch(InflateException e){
//            Log.d(TAG, "in fact exception is here, but don't worry, just ignore it");
        }





        viewPager=(ViewPager) view.findViewById(R.id.viewPager);
        views=new ArrayList<View>();
        ImageLoader imLoader = new ImageLoader(((MainActivity)getActivity()).mProfileFragment.profileImageFragment.mQueue, ProfileImageFragment.bitmapLruCache);
        for (int i = 0; i < urls.size(); i++) {
            View imageView = (View)inflater.inflate(R.layout.big_image_view, null);
            NetworkImageView networkImageView = (NetworkImageView)imageView.findViewById(R.id.big_image);
            networkImageView.setDefaultImageResId(R.drawable.my_loading);
            networkImageView.setErrorImageResId(R.drawable.error);
            networkImageView.setImageUrl(urls.get(i), imLoader);
            views.add(imageView);
        }
//        views.add(imageView);
        viewPager.setAdapter(new ProfileGalleryAdapter(views));
        viewPager.setCurrentItem(idx);
        viewPager.addOnPageChangeListener(pageChangeListener);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
        Dialog dialog=builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        public void onPageSelected(int arg0) {// 页面选择响应函数
            // 如果需要实现页面滑动时动态添加 请在此判断arg0的值
            // 当然此方式在必须在初始化ViewPager的时候给的页数必须>2
            // 因为给1页的话 ViewPager是响应不了此函数的
            // 例：
            System.out.println("================"+arg0);
//            if (arg0 >= urls.size()-1) {
//                return;
//            }
//            if (arg0 == viewPager.getAdapter().getCount() - 1) {// 滑动到最后一页
//                initListViews(arg0);// listViews添加数据
//                ((ProfileGalleryAdapter)viewPager.getAdapter()).setListViews(views);// 重构adapter对象  这是一个很重要
//                viewPager.getAdapter().notifyDataSetChanged();// 刷新
//            }

//            Toast.makeText(BigImageDialog.this.getActivity(), "翻到了第" + (arg0 + 1)
//                        + "页", Toast.LENGTH_SHORT).show();
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {// 滑动中。。。

        }

        public void onPageScrollStateChanged(int arg0) {// 滑动状态改变

        }
    };

    private void initListViews(int count) {
        View imageView = (View)this.getActivity().getLayoutInflater().inflate(R.layout.big_image_view, null);
        NetworkImageView networkImageView = (NetworkImageView)imageView.findViewById(R.id.big_image);
        ImageLoader imLoader = new ImageLoader(((MainActivity)getActivity()).mProfileFragment.profileImageFragment.mQueue, ProfileImageFragment.bitmapLruCache);
        networkImageView.setDefaultImageResId(R.drawable.my_loading);
        networkImageView.setErrorImageResId(R.drawable.error);
        networkImageView.setImageUrl(urls.get(count), imLoader);
        views.add(imageView);// 添加view
    }
}
