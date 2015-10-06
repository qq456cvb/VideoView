package com.qq456cvb.videoview.Subviews;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.qq456cvb.videoview.Activities.MainActivity;
import com.qq456cvb.videoview.R;

import java.util.ArrayList;

/**
 * Created by qq456cvb on 8/27/15.
 */
public class MiddleFragment extends Fragment {

    private int totalCnt;
    private ArrayList<Button> tvs = new ArrayList<>();
    private ArrayList<Button> radios = new ArrayList<>();

    private LayoutInflater layoutInflater;
    private View view;
    private Class fragments[] = {RightChannelFragment.class, RightFragment.class};
    private Button tvButton;
    private Button radioButton;
    private LinearLayout tabMenu;
    private LinearLayout tabContainer;
    public VideoFragment videoFragment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        layoutInflater = inflater;
        view = inflater.inflate(R.layout.middle_layout, container, false);
        videoFragment = new VideoFragment();
        loadButtons();
        initView();
        bindOnClickListeners();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        videoFragment.onDestroy();

//
    }

    public void togglePlay(boolean play) {
        videoFragment.togglePlay(play);
    }

    public boolean isPlaying() {
        return videoFragment.isPlaying();
    }

    public void loadButtons() {
        totalCnt = ((ViewGroup)view.findViewById(R.id.tab_container)).getChildCount();
        for (int i = 0; i < totalCnt; ++i) {
            Button button = (Button)(((ViewGroup)view.findViewById(R.id.tab_container)).getChildAt(i));
            if (button.getContentDescription().toString().contains("tv")) {
                tvs.add(button);
            } else if (button.getContentDescription().toString().contains("radio")) {
                radios.add(button);
            }
        }
    }

    public void initView() {
        tabMenu = (LinearLayout) view.findViewById(R.id.tab_menu);
        tabContainer = (LinearLayout) view.findViewById(R.id.tab_container);
        tvButton = (Button) view.findViewById(R.id.menu_tv);
        radioButton = (Button) view.findViewById(R.id.menu_radio);
        tvButton.setTextColor(Color.BLUE);
        for (int i = 0; i < radios.size(); ++i) {
            radios.get(i).setVisibility(View.GONE);
        }
        tvs.get(0).setTextColor(Color.BLUE);

        //fragment
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.video_fragment, videoFragment);
        transaction.commit();
    }
    @Override
    public void onPause() {
        videoFragment.pause();
        super.onPause();

    }

    @Override
    public void onResume() {
//        if (isAdded()) {
//            videoFragment.resume();
//        }
        super.onResume();
//
    }

    public void bindOnClickListeners() {
        tvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton.setTextColor(Color.BLACK);
                tvButton.setTextColor(Color.BLUE);
                for (int i = 0; i < tvs.size(); ++i) {
                    tvs.get(i).setVisibility(View.VISIBLE);
                }
                for (int i = 0; i < radios.size(); ++i) {
                    radios.get(i).setVisibility(View.GONE);
                }
                tvs.get(0).callOnClick();
            }
        });
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton.setTextColor(Color.BLUE);
                tvButton.setTextColor(Color.BLACK);
                for (int i = 0; i < tvs.size(); ++i) {
                    tvs.get(i).setVisibility(View.GONE);
                }
                for (int i = 0; i < radios.size(); ++i) {
                    radios.get(i).setVisibility(View.VISIBLE);
                }
                radios.get(0).callOnClick();
            }
        });
        for (int i = 0; i < radios.size(); ++i) {
            final int inner_i = i;
            if (!radios.get(i).getText().toString().equals("地市")) {
                radios.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(view.getContext(), "你点击了" + radios.get(inner_i).getText(), Toast.LENGTH_SHORT).show();
                        highlightButton(inner_i, false);
                        Message msg = Message.obtain(MainActivity.handler);
                        msg.what = MainActivity.CHANNEL;
                        msg.arg1 = RightChannelFragment.RADIO;
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "list");
                        if (radios.get(inner_i).getText().equals("中央")) {
                            bundle.putString("value", "yangshi");
                        } else if (radios.get(inner_i).getText().equals("省级")) {
                            bundle.putString("value", "shengji");
                        } else if (radios.get(inner_i).getText().equals("地市")) {
                            bundle.putString("value", "dishi");
                        } else if (radios.get(inner_i).getText().equals("县级")) {
                            bundle.putString("value", "xianji");
                        }
                        msg.setData(bundle);
                        msg.sendToTarget();
                    }
                });
            }
            if (radios.get(i).getText().toString().equals("地市")) {
                radios.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(view.getContext(), "你点击了" + radios.get(inner_i).getText(), Toast.LENGTH_SHORT).show();
                        highlightButton(inner_i, false);
                        Message msg = Message.obtain(MainActivity.handler);
                        msg.what = MainActivity.DISHI;
                        msg.arg1 = RightChannelFragment.RADIO;
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "list");
                        bundle.putString("value", "dishi");
                        msg.setData(bundle);
                        msg.sendToTarget();
                    }
                });
            }
        }
        for (int i = 0; i < tvs.size(); ++i) {
            final int inner_i = i;
            if (!tvs.get(i).getText().toString().equals("地市")) {
                tvs.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(view.getContext(), "你点击了" + tvs.get(inner_i).getText(), Toast.LENGTH_SHORT).show();
                        highlightButton(inner_i, true);
                        Message msg = Message.obtain(MainActivity.handler);
                        msg.what = MainActivity.CHANNEL;
                        msg.arg1 = RightChannelFragment.TV;
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "list");
                        if (tvs.get(inner_i).getText().equals("央视")) {
                            bundle.putString("value", "yangshi");
                        } else if (tvs.get(inner_i).getText().equals("卫视")) {
                            bundle.putString("value", "weishi");
                        } else if (tvs.get(inner_i).getText().equals("省级")) {
                            bundle.putString("value", "shengji");
                        } else if (tvs.get(inner_i).getText().equals("地市")) {
                            bundle.putString("value", "dishi");
                        } else if (tvs.get(inner_i).getText().equals("县级")) {
                            bundle.putString("value", "xianji");
                        } else if (tvs.get(inner_i).getText().equals("境外")) {
                            bundle.putString("value", "jingwai");
                        }
                        msg.setData(bundle);
                        msg.sendToTarget();
                    }
                });

            }
            if (tvs.get(i).getText().toString().equals("地市")) {
                tvs.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(view.getContext(), "你点击了" + tvs.get(inner_i).getText(), Toast.LENGTH_SHORT).show();
                        highlightButton(inner_i, true);
                        Message msg = Message.obtain(MainActivity.handler);
                        msg.what = MainActivity.DISHI;
                        msg.arg1 = RightChannelFragment.TV;
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "list");
                        bundle.putString("value", "dishi");
                        msg.setData(bundle);
                        msg.sendToTarget();
                    }
                });
            }
        }
    }

    public void changeSrc(String src, float startTime) {
        videoFragment.changeSrc(src, startTime);
    }

    public void toggleFullscreen(boolean fullscreen) {
        if (fullscreen) {

            LinearLayout vlcContainer = (LinearLayout)view.findViewById(R.id.vlc_container);
            LinearLayout.LayoutParams p = (LinearLayout.LayoutParams)vlcContainer.getLayoutParams();
            vlcContainer.setTag(p);
            p.setMargins(0, 0, 0, 0);
            vlcContainer.setLayoutParams(p);
            vlcContainer.requestLayout();
            tabMenu.setVisibility(View.GONE);
            tabContainer.setVisibility(View.GONE);
            Message msg = Message.obtain(videoFragment.mHandler, VideoFragment.VideoSizeChanged, videoFragment.getView().getWidth(), videoFragment.getView().getHeight());
            msg.sendToTarget();
        } else {
            LinearLayout vlcContainer = (LinearLayout)view.findViewById(R.id.vlc_container);
            LinearLayout.LayoutParams p = (LinearLayout.LayoutParams)vlcContainer.getLayoutParams();
            vlcContainer.setTag(p);
            p.setMargins(50, 20, 0, 20);
            vlcContainer.setLayoutParams(p);
            vlcContainer.requestLayout();
            tabMenu.setVisibility(View.VISIBLE);
            tabContainer.setVisibility(View.VISIBLE);
            Message msg = Message.obtain(videoFragment.mHandler, VideoFragment.VideoSizeChanged, videoFragment.getView().getWidth(), videoFragment.getView().getHeight());
            msg.sendToTarget();
        }
    }
    private void highlightButton(int index, boolean isTv) {
        if (isTv) {
            for (int i = 0; i < tvs.size(); ++i) {
                tvs.get(i).setTextColor(Color.BLACK);
            }
            tvs.get(index).setTextColor(Color.BLUE);
        } else {
            for (int i = 0; i < radios.size(); ++i) {
                radios.get(i).setTextColor(Color.BLACK);
            }
            radios.get(index).setTextColor(Color.BLUE);
        }
    }


}