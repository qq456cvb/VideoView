package com.qq456cvb.videoview.Subviews;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.qq456cvb.videoview.Activities.MainActivity;
import com.qq456cvb.videoview.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class CommentAndChannelListFragment extends Fragment {
    private LinearLayout ll_comchanMycomment;
    private LinearLayout ll_comchanEditcomment;
    private RightChannelFragment rightChannelFragment = new RightChannelFragment();
    public CommentAndChannelListFragment() {
        // Required empty public constructor
    }

    public RightChannelFragment getRightChannelFragment() {
        return rightChannelFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_comment_and_channel_list, container, false);
        ll_comchanMycomment= (LinearLayout) view.findViewById(R.id.ll_mycomment);
        ll_comchanEditcomment= (LinearLayout) view.findViewById(R.id.ll_editcomment);

        ll_comchanMycomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageToMainActivity(MainActivity.MYCOMMENT,null);
            }
        });
        ll_comchanEditcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageToMainActivity(MainActivity.EDITCOMMENT,null);
            }
        });
        FragmentManager fm=getActivity().getFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();

        ft.replace(R.id.fl_channelfrag,rightChannelFragment);
        ft.commit();
        return view;
    }
    private void sendMessageToMainActivity(int messageType, Bundle bundle){
        Message msg = Message.obtain(MainActivity.handler);
        msg.what = messageType;
        if(bundle!=null){
            msg.setData(bundle);
        }
        msg.sendToTarget();
    }
}
