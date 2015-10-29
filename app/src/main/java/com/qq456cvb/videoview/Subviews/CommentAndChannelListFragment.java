package com.qq456cvb.videoview.Subviews;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.TextHttpResponseHandler;
import com.qq456cvb.videoview.Activities.MainActivity;
import com.qq456cvb.videoview.Application.GlobalApp;
import com.qq456cvb.videoview.CustomWidgets.DateTimePickDialogUtil;
import com.qq456cvb.videoview.R;
import com.qq456cvb.videoview.Utils.UserClient;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class CommentAndChannelListFragment extends Fragment implements DateTimePickDialogUtil.OnDateSelectListener {
    private LinearLayout ll_comchanMycomment;
    private LinearLayout ll_comchanEditcomment;
    private RightChannelFragment rightChannelFragment  = new RightChannelFragment();
    private TextView txtHistory;

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

        txtHistory = (TextView) view.findViewById(R.id.txt_com_history);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String initStartDateTime = sdf.format(Calendar.getInstance().getTime());
        txtHistory.setText(initStartDateTime);

        txtHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickDialogUtil dateTimePickDialog = new DateTimePickDialogUtil(
                        CommentAndChannelListFragment.this.getActivity(), CommentAndChannelListFragment.this, txtHistory.getText().toString());
                dateTimePickDialog.dateTimePickDialog(txtHistory);
            }
        });

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

    public void OnDateComplete(final String dateTime) {
        final String convertDateTime = (dateTime+":00").replace(" ", "%20");
        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, String response) {
                if (response.contains(":")) {
                    response = response.replace("\\", "");
                    response = response.substring(1, response.length() - 1);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        float startTime = jsonObject.getInt("startTime");
                        float timeLength = jsonObject.getInt("timeLength");
                        GlobalApp.endTime = jsonObject.getString("endTime");
                        Message msg = Message.obtain(MainActivity.handler);
                        msg.what = MainActivity.CHANNEL;
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "play");
                        bundle.putString("value", jsonObject.getString("url"));
                        bundle.putFloat("startTime", startTime / timeLength);
                        msg.obj = GlobalApp.currentChannel;
                        msg.setData(bundle);
                        msg.sendToTarget();
                        rightChannelFragment.loadHistoryEDG(GlobalApp.currentChannel, convertDateTime);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CommentAndChannelListFragment.this.getActivity());
                    builder.setMessage("暂无历史记录信息");
                    builder.setTitle("提示");
                    builder.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
                }
            }

            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Log.d("test", "sssss");
            }
        };
        new Thread() {
            @Override
            public void run() {
                try {
                    UserClient.get("/stpy/ajaxVlcAction!getUrl.action?id=" + GlobalApp.currentChannel.id + "" +
                            "&dateTime=" + convertDateTime + "&hdnType=" + GlobalApp.currentChannel.hdnType + "&urlNext=1", null, handler);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
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
