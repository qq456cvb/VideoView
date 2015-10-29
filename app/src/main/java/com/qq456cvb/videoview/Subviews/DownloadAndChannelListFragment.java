package com.qq456cvb.videoview.Subviews;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.DialogInterface;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by qq456cvb on 9/13/15.
 */
public class DownloadAndChannelListFragment extends Fragment implements DateTimePickDialogUtil.OnDateSelectListener {
    private RightChannelFragment rightChannelFragment  = new RightChannelFragment();
    private static int notificationID = 1;
    static final int BUFFER_SIZE = 8192;

    private LinearLayout linearLayoutMp4;
    private LinearLayout linearLayoutZip;

    private String type = "";
    private TextView startDateTime;
    private TextView endDateTime;
    private HashMap<Integer,MyNotification> myNotifications = new HashMap<>();
    private HashMap<Integer,Info> infos = new HashMap<>();
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(DownloadAndChannelListFragment.this.getActivity());
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

        linearLayoutMp4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
                    private int times = 0;
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        // Do something with the file `response`
                        String localPath = "";
                        myNotifications.get((int)this.getTag()).changeProgressStatus(100, 0);
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
                            localPath = localPath.replaceAll(":", "-");
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
                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri uri = Uri.fromFile(new File(localPath));
                            intent.setData(uri);
                            DownloadAndChannelListFragment.this.getActivity().sendBroadcast(intent);
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
                            Integer tag = (Integer)this.getTag();
                            myNotifications.get((int)this.getTag()).changeProgressStatus(count, 0);
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
                            intent.setType ("video/mp4");

                            intent.setData(Uri.parse(""));
                            PendingIntent updatePendingIntent = PendingIntent.getActivity(DownloadAndChannelListFragment.this.getActivity(),0,intent,0);
                            MyNotification myNotification = new MyNotification(DownloadAndChannelListFragment.this.getActivity(), updatePendingIntent, notificationID++);
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
                            myNotifications.put(notificationID, myNotification);
                            handler.setTag(notificationID);
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
                        DownloadAndChannelListFragment.this.getActivity(), str);
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

    public class Info {
        public Integer tag;
        public String suffix;
        public Info() {
            tag = null;
            suffix = null;
        }
    }

    public void manyDownload() {
        final AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            private int times = 0;
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    // Do something with the file `response`
                    myNotifications.get(((Info)this.getTag()).tag).changeProgressStatus(100, 0);
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
                        String filename = headers[1].getValue();
                        filename = filename.substring(filename.indexOf("filename=") + 9);
                        localPath = videoPath.toString() + "/"+filename;
                    } else {
                        videoPath = new File("/stpy/video");
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
//                        DownloadAndChannelListFragment.this.getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(DownloadAndChannelListFragment.this.getActivity(), "已保存至" + inner_path, Toast.LENGTH_LONG).show();
//                            }
//                        });
                        inStream.close();
                        if (localPath.substring(localPath.lastIndexOf(".")).contains("zip")) {
                            UnZipFolder(localPath, videoPath.getAbsolutePath());
                        }
                        DownloadAndChannelListFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DownloadAndChannelListFragment.this.getActivity(), "已保存至" + inner_path, Toast.LENGTH_LONG).show();
                            }
                        });
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
                int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);
                if (times > 128) {
                    Info i = (Info)this.getTag();
                    myNotifications.get(((Info)this.getTag()).tag).changeProgressStatus(count, 0);
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
                    MyNotification myNotification = new MyNotification(DownloadAndChannelListFragment.this.getActivity(), updatePendingIntent, notificationID++);
                    myNotification.showCustomizeNotification(R.drawable.ic_launcher, "视频下载", R.layout.notification);
                    Channel channel = GlobalApp.currentChannel;
                    String hdid = "hdid=" + channel.id;
                    String dateBegin = "dateBegin=" + chooseDates.replaceAll(";", "%3B");
                    String sstartTime = "sstartTime=" + (startDateTime.getText().toString() + ":00").substring(11).replaceAll(":", "%3A");
                    String sendTime = "sendTime=" + (endDateTime.getText().toString() + ":00").substring(11).replaceAll(":", "%3A");
                    String hdnType = "hdnType=" + channel.hdnType;
                    Info info = new Info();
                    if (chooseDates.isEmpty()) {
                        return;
                    }
                    if (!chooseDates.contains(";")) {
                        if (channel.hdnType.equals("1")) {
                            info.suffix = "mp3";
                        } else {
                            info.suffix = "mp4";
                        }
                    } else {
                        info.suffix = "zip";
                    }
                    String hdnDypass = "hdnDypass=" + channel.hdnDyass;
                    String url = "/stpy/videoDownloadAction!manydownload.action?" + hdid + "&" + dateBegin + "&" + sstartTime
                            + "&" + sendTime + "&" + hdnType + "&" + hdnDypass;
                    myNotifications.put(notificationID, myNotification);
                    info.tag = notificationID;
                    infos.put(notificationID, info);
                    handler.setTag(info);
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

    public void UnZipFolder(String zipFileString, String outPathString) throws Exception {
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
        ZipEntry zipEntry;
        String szName = "";
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                // get the folder name of the widget
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPathString + File.separator + szName);
                folder.mkdirs();
            } else {
                File file = new File(outPathString + File.separator + szName);
                file.createNewFile();
                // get the output stream of the file
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // read (len) bytes into buffer
                while ((len = inZip.read(buffer)) != -1) {
                    // write (len) byte from buffer at the position 0
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(new File(outPathString + File.separator + szName));
                intent.setData(uri);
                DownloadAndChannelListFragment.this.getActivity().sendBroadcast(intent);
            }
        }
        inZip.close();
    }
}
