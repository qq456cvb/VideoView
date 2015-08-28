package com.qq456cvb.videoview.Subviews;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.qq456cvb.videoview.Activities.MainActivity;
import com.qq456cvb.videoview.R;

/**
 * Created by qq456cvb on 8/27/15.
 */
public class MiddleFragment extends Fragment {

    private LayoutInflater layoutInflater;
    private View view;
    private Class fragments[] = {RightChannelFragment.class, RightFragment.class};
    private Button buttons[];
    private Button tvButton;
    private Button radioButton;
    private VideoFragment videoFragment = new VideoFragment();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        layoutInflater = inflater;
        view = inflater.inflate(R.layout.middle_layout, container, false);
        initView();
        bindOnClickListeners();
        return view;
    }

    public void initView() {
        // buttons
        int cnt = ((ViewGroup)view.findViewById(R.id.tab_container)).getChildCount();
        buttons = new Button[cnt];
        for (int i = 0; i < cnt; ++i) {
            buttons[i] = (Button)((ViewGroup)view.findViewById(R.id.tab_container)).getChildAt(i);
//            buttons[i].setText("测试" + String.valueOf(i));
            buttons[i].setGravity(1);
            final int inner_i = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(view.getContext(), "你点击了" + buttons[inner_i].getText(), Toast.LENGTH_SHORT).show();
                    if (buttons[inner_i].getText().equals("央视")) {
                        Message msg = Message.obtain(MainActivity.handler);
                        msg.what = MainActivity.CHANNEL;
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "list");
                        bundle.putString("value", "yangshi");
                        msg.setData(bundle);
                        msg.sendToTarget();
                    } else if (buttons[inner_i].getText().equals("省级")) {
                        Message msg = Message.obtain(MainActivity.handler);
                        msg.what = MainActivity.CHANNEL;
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "list");
                        bundle.putString("value", "shengji");
                        msg.setData(bundle);
                        msg.sendToTarget();
                    }
                }
            });
        }
        tvButton = (Button) view.findViewById(R.id.menu_tv);
        radioButton = (Button) view.findViewById(R.id.menu_radio);

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
                for (int i = 0; i < 6; ++i) {
                    buttons[i].setVisibility(View.VISIBLE);
                }
                for (int i = 0; i < 4; ++i) {
                    buttons[i].setVisibility(View.GONE);
                }
            }
        });
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 6; ++i) {
                    buttons[i].setVisibility(View.VISIBLE);
                }
                for (int i = 4; i < 6; ++i) {
                    buttons[i].setVisibility(View.GONE);
                }
            }
        });
    }

    public void changeSrc(String src) {
        videoFragment.changeSrc(src);
    }
}