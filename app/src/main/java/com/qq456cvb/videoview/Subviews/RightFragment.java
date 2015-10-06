package com.qq456cvb.videoview.Subviews;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by qq456cvb on 8/27/15.
 */
public class RightFragment extends Fragment implements DateTimePickDialogUtil.OnDateSelectListener {


    private RightChannelFragment rightChannelFragment = new RightChannelFragment();
    private TextView txtHistory;
    private View view;

    public RightChannelFragment getRightChannelFragment() {
        return rightChannelFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.right_layout, container, false);
        txtHistory = (TextView) view.findViewById(R.id.txt_history);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String initStartDateTime = sdf.format(Calendar.getInstance().getTime());
        txtHistory.setText(initStartDateTime);

        txtHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickDialogUtil dateTimePickDialog = new DateTimePickDialogUtil(
                        RightFragment.this.getActivity(), RightFragment.this, txtHistory.getText().toString());
                dateTimePickDialog.dateTimePickDialog(txtHistory);
            }
        });
        try {
            setDefaultFragment();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    public void OnDateComplete(final String dateTime) {
        final String convertDateTime = (dateTime+":00").replace(" ", "%20");
        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, String response) {
                response = response.replace("\\", "");
                response = response.substring(1, response.length() - 1);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    float startTime = jsonObject.getInt("startTime");
                    float timeLength = jsonObject.getInt("timeLength");
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

    public void setDefaultFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.right_content, rightChannelFragment);
        transaction.commit();
    }

    public void changeCategory(String category, int type) {
        rightChannelFragment.changeCategory(category, type);
    }
}
