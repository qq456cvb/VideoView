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
import android.widget.Toast;

import com.qq456cvb.videoview.Activities.MainActivity;
import com.qq456cvb.videoview.R;

import java.util.ArrayList;

/**
 * Created by qq456cvb on 8/27/15.
 */
public class MiddleFragment extends Fragment {

    private LayoutInflater layoutInflater;
    private View view;
    private Class fragments[] = {RightChannelFragment.class, RightFragment.class};
    private Button tvButton;
    private Button radioButton;
    private VideoFragment videoFragment = new VideoFragment();
    private int totalCnt;
    private ArrayList<Button> tvs = new ArrayList<>();
    private ArrayList<Button> radios = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        layoutInflater = inflater;
        view = inflater.inflate(R.layout.middle_layout, container, false);
        loadButtons();
        initView();
        bindOnClickListeners();
        return view;
    }

    public void loadButtons() {
        totalCnt = ((ViewGroup)view.findViewById(R.id.tab_container)).getChildCount();
        for (int i = 0; i < totalCnt; ++i) {
            Button button = (Button)(((ViewGroup) view.findViewById(R.id.tab_container)).getChildAt(i));
            if (button.getContentDescription().toString().contains("tv")) {
                tvs.add(button);
            } else if (button.getContentDescription().toString().contains("radio")) {
                radios.add(button);
            }
        }
    }

    public void initView() {
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
            }
        });
        for (int i = 0; i < tvs.size(); ++i) {
            final int inner_i = i;
            tvs.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(view.getContext(), "你点击了" + tvs.get(inner_i).getText(), Toast.LENGTH_SHORT).show();
                    highlightButton(inner_i);
                    Message msg = Message.obtain(MainActivity.handler);
                    msg.what = MainActivity.CHANNEL;
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
    }

    public void changeSrc(String src) {
        videoFragment.changeSrc(src);
    }

    private void highlightButton(int index) {
        for (int i = 0; i < tvs.size(); ++i) {
            tvs.get(i).setTextColor(Color.BLACK);
        }
        tvs.get(index).setTextColor(Color.BLUE);
    }
}