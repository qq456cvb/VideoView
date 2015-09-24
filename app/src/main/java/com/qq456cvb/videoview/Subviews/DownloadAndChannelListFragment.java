package com.qq456cvb.videoview.Subviews;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qq456cvb.videoview.Activities.MainActivity;
import com.qq456cvb.videoview.Application.GlobalApp;
import com.qq456cvb.videoview.CustomWidgets.DatePickDialog;
import com.qq456cvb.videoview.CustomWidgets.DateTimePickDialogUtil;
import com.qq456cvb.videoview.R;
import com.qq456cvb.videoview.Utils.Channel;
import com.qq456cvb.videoview.Utils.UserClient;

import org.apache.http.Header;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by qq456cvb on 9/13/15.
 */
public class DownloadAndChannelListFragment extends Fragment {
    private LinearLayout linearLayoutMp4;
    private LinearLayout linearLayoutZip;

    private TextView startDateTime;
    private TextView endDateTime;

    private String initStartDateTime = "2015-09-13 14:44"; // 初始化开始时间
    private String initEndDateTime = "2015-09-13 14:46"; // 初始化结束时间

    public String chooseDates;

    public DownloadAndChannelListFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_download_and_channel_list, container, false);
        linearLayoutMp4= (LinearLayout) view.findViewById(R.id.ll_mp4download);
        linearLayoutZip= (LinearLayout) view.findViewById(R.id.ll_zipdownload);


        startDateTime = (TextView) view.findViewById(R.id.starttime);
        endDateTime = (TextView) view.findViewById(R.id.endtime);

        startDateTime.setText(initStartDateTime);
        endDateTime.setText(initEndDateTime);
        startDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickDialogUtil dateTimePickDialog = new DateTimePickDialogUtil(
                        DownloadAndChannelListFragment.this.getActivity(), startDateTime.getText().toString());
                dateTimePickDialog.dateTimePickDialog(startDateTime);
            }
        });

        endDateTime.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                DateTimePickDialogUtil dateTimePickDialog = new DateTimePickDialogUtil(
                        DownloadAndChannelListFragment.this.getActivity(), endDateTime.getText().toString());
                dateTimePickDialog.dateTimePickDialog(endDateTime);
            }
        });


        final FileAsyncHttpResponseHandler handler = new FileAsyncHttpResponseHandler(DownloadAndChannelListFragment.this.getActivity()) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                // Do something with the file `response`
                File sdDir = null;
                long length = 0;
                String localPath = "", remotePath = response.getPath();
                boolean sdCardExist = Environment.getExternalStorageState()
                        .equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
                if (sdCardExist)
                {
                    sdDir = Environment.getExternalStorageDirectory();//获取跟目录
                    localPath = sdDir.toString() + "/"+startDateTime.getText().toString()+"_"+endDateTime.getText().toString()+".mp4";
                } else {
                    //TODO
                }
                try {
                    int bytesum = 0;
                    int byteread = 0;
                    File file = new File(remotePath);
                    if (file.exists()) { //文件存在时
                        FileInputStream inStream = new FileInputStream(remotePath); //读入原文件
                        length = inStream.available();
                        FileOutputStream fs = new FileOutputStream(localPath);
                        byte[] buffer = new byte[1024];
                        while ( (byteread = inStream.read(buffer)) != -1) {
                            bytesum += byteread; //字节数 文件大小
                            System.out.println(bytesum);
                            fs.write(buffer, 0, byteread);
//                            Toast.makeText(DownloadAndChannelListFragment.this.getActivity(), "已下载:"+String.valueOf(bytesum/1000)+"/"+String.valueOf(length/1000)+"KB", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(DownloadAndChannelListFragment.this.getActivity(), "已保存至"+localPath, Toast.LENGTH_SHORT).show();
                        inStream.close();
                    }
                }
                catch (Exception e) {
                    System.out.println("复制单个文件操作出错");
                    e.printStackTrace();

                }
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File response) {
                Toast.makeText(DownloadAndChannelListFragment.this.getActivity(), "未知错误", Toast.LENGTH_SHORT).show();
            }
        };

        linearLayoutMp4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Channel channel = GlobalApp.currentChannel;
                            String id = channel.id;
                            RequestParams params = new RequestParams();
                            params.put("startTime", startDateTime.getText().toString() + ":00");
                            params.put("endTime", endDateTime.getText().toString() + ":00");
                            params.put("hdnType", "3");
                            params.put("hdnDypass", channel.hdnDyass);
                            UserClient.post("/stpy/videoDownloadAction!doDown.action?hdid=" + id, params, handler);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }}.start();
            }
        });
        linearLayoutZip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                DatePickDialog dateTimePickDialog = new DatePickDialog(
                        DownloadAndChannelListFragment.this.getActivity(),str);
                dateTimePickDialog.dateTimePickDialog(DownloadAndChannelListFragment.this);
            }
        });
        FragmentManager fm=getActivity().getFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        //TODO:change to the current fragment
        RightChannelFragment rcf=new RightChannelFragment();
        ft.replace(R.id.downloadChannelFragment,rcf);
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

    public void manyDownload() {
        final FileAsyncHttpResponseHandler handler = new FileAsyncHttpResponseHandler(DownloadAndChannelListFragment.this.getActivity()) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                // Do something with the file `response`
                File sdDir = null;
                long length = 0;
                String localPath = "", remotePath = response.getPath();
                boolean sdCardExist = Environment.getExternalStorageState()
                        .equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
                if (sdCardExist)
                {
                    sdDir = Environment.getExternalStorageDirectory();//获取跟目录
                    localPath = sdDir.toString() + "/"+chooseDates+".mp4";
                } else {
                    //TODO
                }
                try {
                    int bytesum = 0;
                    int byteread = 0;
                    File file = new File(remotePath);
                    if (file.exists()) { //文件存在时
                        FileInputStream inStream = new FileInputStream(remotePath); //读入原文件
                        length = inStream.available();
                        FileOutputStream fs = new FileOutputStream(localPath);
                        byte[] buffer = new byte[1024];
                        while ( (byteread = inStream.read(buffer)) != -1) {
                            bytesum += byteread; //字节数 文件大小
                            System.out.println(bytesum);
                            fs.write(buffer, 0, byteread);
//                            Toast.makeText(DownloadAndChannelListFragment.this.getActivity(), "已下载:"+String.valueOf(bytesum/1000)+"/"+String.valueOf(length/1000)+"KB", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(DownloadAndChannelListFragment.this.getActivity(), "已保存至"+localPath, Toast.LENGTH_SHORT).show();
                        inStream.close();
                    }
                }
                catch (Exception e) {
                    System.out.println("复制单个文件操作出错");
                    e.printStackTrace();

                }
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File response) {
                Toast.makeText(DownloadAndChannelListFragment.this.getActivity(), "未知错误", Toast.LENGTH_SHORT).show();
            }
        };
        new Thread() {
            @Override
            public void run() {
                try {
                    Channel channel = GlobalApp.currentChannel;
                    String id = channel.id;
                    RequestParams params = new RequestParams();
                    params.put("sstartTime", startDateTime.getText().toString() + ":00");
                    params.put("sendTime", endDateTime.getText().toString() + ":00");
                    params.put("dateBegin", chooseDates);
                    params.put("hdnType", "3");
                    params.put("calendarYear", "2015");
                    params.put("calendarMonth", "8");
                    params.put("hdnDypass", channel.hdnDyass);
                    UserClient.post("/stpy/videoDownloadAction!manydownload.action?hdid=" + id, params, handler);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }}.start();
    }
}
