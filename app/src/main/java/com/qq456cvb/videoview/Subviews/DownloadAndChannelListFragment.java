package com.qq456cvb.videoview.Subviews;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qq456cvb.videoview.Activities.MainActivity;
import com.qq456cvb.videoview.Application.GlobalApp;
import com.qq456cvb.videoview.CustomWidgets.DatePickDialog;
import com.qq456cvb.videoview.CustomWidgets.DateTimePickDialogUtil;
import com.qq456cvb.videoview.R;
import com.qq456cvb.videoview.Utils.Channel;
import com.qq456cvb.videoview.Utils.MyNotification;
import com.qq456cvb.videoview.Utils.UserClient;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by qq456cvb on 9/13/15.
 */
public class DownloadAndChannelListFragment extends Fragment implements DateTimePickDialogUtil.OnDateSelectListener {
    private RightChannelFragment rightChannelFragment  = new RightChannelFragment();

    private LinearLayout linearLayoutMp4;
    private LinearLayout linearLayoutZip;

    private String type = "";
    private TextView startDateTime;
    private TextView endDateTime;
    MyNotification myNotification;
    private String initStartDateTime; // 初始化开始时间
    private String initEndDateTime; // 初始化结束时间

    public String chooseDates;

    public DownloadAndChannelListFragment() {
        // Required empty public constructor
    }

    public RightChannelFragment getRightChannelFragment() {
        return rightChannelFragment;
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

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_download_and_channel_list, container, false);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        initStartDateTime = sdf.format(Calendar.getInstance().getTime());
        initEndDateTime = initStartDateTime;

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
                        DownloadAndChannelListFragment.this.getActivity(), DownloadAndChannelListFragment.this, startDateTime.getText().toString());
                dateTimePickDialog.dateTimePickDialog(startDateTime);
                dateTimePickDialog.tag = 0;
            }
        });

        endDateTime.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                DateTimePickDialogUtil dateTimePickDialog = new DateTimePickDialogUtil(
                        DownloadAndChannelListFragment.this.getActivity(), DownloadAndChannelListFragment.this, endDateTime.getText().toString());
                dateTimePickDialog.dateTimePickDialog(endDateTime);
                dateTimePickDialog.tag = 1;
            }
        });



        final AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            private int times = 0;
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // Do something with the file `response`
                String localPath = "";
                myNotification.changeProgressStatus(100, 0);
                long length = 0;
                File sdDir = null, videoPath = null;
                boolean sdCardExist = Environment.getExternalStorageState()
                        .equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
                if (sdCardExist)
                {
                    sdDir = Environment.getExternalStorageDirectory();//获取跟目录
                    videoPath = new File(sdDir.toString() + "/stpy/video");
                    if (!videoPath.exists()) {
                        videoPath.mkdirs();
                    }
                    if (type.equals("3")) {
                        localPath = videoPath.toString() + "/" +GlobalApp.currentChannel.getName()+"_"+ startDateTime.getText().toString() + "_" + endDateTime.getText().toString() + ".mp4";
                    } else {
                        localPath = videoPath.toString() + "/" +GlobalApp.currentChannel.getName()+"_"+ startDateTime.getText().toString() + "_" + endDateTime.getText().toString() + ".mp3";
                    }
                } else {
                    //TODO
                }
                try {
                    int bytesum = 0;
                    int byteread = 0;
                    ByteArrayInputStream inStream = new ByteArrayInputStream(responseBody); //读入原文件
                    length = inStream.available();
                    FileOutputStream fs = new FileOutputStream(localPath);
                    byte[] buffer = new byte[1024];
                    while ((byteread = inStream.read(buffer)) != -1) {
                        bytesum += byteread; //字节数 文件大小
                        fs.write(buffer, 0, byteread);
//                            Toast.makeText(DownloadAndChannelListFragment.this.getActivity(), "已下载:"+String.valueOf(bytesum/1000)+"/"+String.valueOf(length/1000)+"KB", Toast.LENGTH_SHORT).show();
                    }
                    final String inner_path = localPath;
                    DownloadAndChannelListFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DownloadAndChannelListFragment.this.getActivity(), "已保存至" + inner_path, Toast.LENGTH_LONG).show();
                        }
                    });
                }
                catch (Exception e) {
                    System.out.println("下载出错");
                    e.printStackTrace();

                }
            }

            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable throwable) {
                Toast.makeText(DownloadAndChannelListFragment.this.getActivity(), "未知错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                // TODO Auto-generated method stub
                times++;
                super.onProgress(bytesWritten, totalSize);
                int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);
                if (times > 128) {
                    myNotification.changeProgressStatus(count, 0);
                    times = 0;
                }
                // 上传进度显示
//                progress.setProgress(count);
//                Log.e("上传 Progress>>>>>", bytesWritten + " / " + totalSize);
            }

            @Override
            public void onRetry(int retryNo) {
                // TODO Auto-generated method stub
                super.onRetry(retryNo);
                // 返回重试次数
            }
        };

        linearLayoutMp4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setType ("video/mp4");

                            intent.setData(Uri.parse(""));
                            PendingIntent updatePendingIntent = PendingIntent.getActivity(DownloadAndChannelListFragment.this.getActivity(),0,intent,0);
                            myNotification = new MyNotification(DownloadAndChannelListFragment.this.getActivity(), updatePendingIntent,1);
                            myNotification.showCustomizeNotification(R.drawable.ic_launcher, "视频下载", R.layout.notification);
                            Channel channel = GlobalApp.currentChannel;
                            String hdid = "hdid=" + channel.id;
//                            String dateBegin = "dateBegin=" + chooseDates.replaceAll(";", "%3B");
                            String sstartTime = "startTime=" + (startDateTime.getText().toString() + ":00").replaceAll(" ","+").replaceAll(":", "%3A");
                            String sendTime = "endTime=" + (endDateTime.getText().toString() + ":00").replaceAll(" ", "+").replaceAll(":", "%3A");
                            String hdnType = "hdnType=" + channel.hdnType;
                            String hdnDypass = "hdnDypass=" + channel.hdnDyass;
                            type = channel.hdnType;
                            String url = "/stpy/videoDownloadAction!doDown.action?" + hdid + "&" + sstartTime
                                    + "&" + sendTime + "&" + hdnType + "&" + hdnDypass;
                            UserClient.get(url, null, handler);
                            DownloadAndChannelListFragment.this.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(DownloadAndChannelListFragment.this.getActivity(), "请稍后", Toast.LENGTH_SHORT).show();
                                }
                            });
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

        ft.replace(R.id.downloadChannelFragment,rightChannelFragment);
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
        final AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            private int times = 0;
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    // Do something with the file `response`
                    String localPath = "";
                    long length = 0;
                    File sdDir = null, videoPath = null;
                    boolean sdCardExist = Environment.getExternalStorageState()
                            .equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
                    if (sdCardExist)
                    {
                        sdDir = Environment.getExternalStorageDirectory();//获取跟目录
                        videoPath = new File(sdDir.toString() + "/stpy/video");
                        if (!videoPath.exists()) {
                            videoPath.mkdirs();
                        }
                        localPath = videoPath.toString() + "/"+GlobalApp.currentChannel.getName()+"_"+chooseDates+" "+startDateTime.getText().toString().substring(11)+"_"+endDateTime.getText().toString().substring(11)+".zip";
                    } else {
                        //TODO
                    }
                    try {
                        int bytesum = 0;
                        int byteread = 0;
                        ByteArrayInputStream inStream = new ByteArrayInputStream(responseBody); //读入原文件
                        length = inStream.available();
                        FileOutputStream fs = new FileOutputStream(localPath);
                        byte[] buffer = new byte[1024];
                        while ((byteread = inStream.read(buffer)) != -1) {
                            bytesum += byteread; //字节数 文件大小
                            fs.write(buffer, 0, byteread);
//                            Toast.makeText(DownloadAndChannelListFragment.this.getActivity(), "已下载:"+String.valueOf(bytesum/1000)+"/"+String.valueOf(length/1000)+"KB", Toast.LENGTH_SHORT).show();
                        }
                        final String inner_path = localPath;
                        DownloadAndChannelListFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DownloadAndChannelListFragment.this.getActivity(), "已保存至" + inner_path, Toast.LENGTH_LONG).show();
                            }
                        });
                        inStream.close();
//                    Intent intent = new Intent(DownloadAndChannelListFragment.this.getActivity(), DownloadService.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("url", remotePath);
//                    bundle.putString("local", localPath);
//                    intent.putExtras(bundle);
//                    DownloadAndChannelListFragment.this.getActivity().startService(intent);
//                }
                    }
                    catch (Exception e) {
                        System.out.println("下载出错");
                        e.printStackTrace();

                    }
                }

            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable throwable) {
                Toast.makeText(DownloadAndChannelListFragment.this.getActivity(), "未知错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                // TODO Auto-generated method stub
                times++;
                super.onProgress(bytesWritten, totalSize);
                int count = (int) ((bytesWritten * 1.0 / totalSize) / 1000);
                if (times > 128) {
                    myNotification.changeProgressStatus(count, 1);
                    times = 0;
                }
                // 上传进度显示
//                progress.setProgress(count);
//                Log.e("上传 Progress>>>>>", bytesWritten + " / " + totalSize);
            }

            @Override
            public void onRetry(int retryNo) {
                // TODO Auto-generated method stub
                super.onRetry(retryNo);
                // 返回重试次数
            }
        };
        new Thread() {
            @Override
            public void run() {
                try {

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setType("video/mp4");
                    PendingIntent updatePendingIntent = PendingIntent.getActivity(DownloadAndChannelListFragment.this.getActivity(),0,intent,0);
                    myNotification = new MyNotification(DownloadAndChannelListFragment.this.getActivity(), updatePendingIntent,1);
                    myNotification.showCustomizeNotification(R.drawable.ic_launcher, "视频下载", R.layout.notification);
                    Channel channel = GlobalApp.currentChannel;
                    String hdid = "hdid=" + channel.id;
                    String dateBegin = "dateBegin=" + chooseDates.replaceAll(";", "%3B");
                    String sstartTime = "sstartTime=" + (startDateTime.getText().toString() + ":00").substring(11).replaceAll(":", "%3A");
                    String sendTime = "sendTime=" + (endDateTime.getText().toString() + ":00").substring(11).replaceAll(":", "%3A");
                    String hdnType = "hdnType=" + channel.hdnType;
                    String hdnDypass = "hdnDypass=" + channel.hdnDyass;
                    String url = "/stpy/videoDownloadAction!manydownload.action?" + hdid + "&" + dateBegin + "&" + sstartTime
                            + "&" + sendTime + "&" + hdnType + "&" + hdnDypass;
                    UserClient.get(url, null, handler);
                    DownloadAndChannelListFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DownloadAndChannelListFragment.this.getActivity(), "请稍后", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }}.start();
    }
}
