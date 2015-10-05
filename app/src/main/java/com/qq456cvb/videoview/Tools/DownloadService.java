package com.qq456cvb.videoview.Tools;

/**
 * Created by qq456cvb on 9/26/15.
 */

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.qq456cvb.videoview.Activities.MainActivity;
import com.qq456cvb.videoview.R;
import com.qq456cvb.videoview.Utils.MyNotification;

public class DownloadService extends Service {
    private final static int DOWNLOAD_COMPLETE = -2;
    private final static int DOWNLOAD_FAIL = -1;

    //自定义通知栏类
    MyNotification myNotification;

    String filePathString; //下载文件绝对路径(包括文件名)

    //通知栏跳转Intent
    private Intent updateIntent = null;
    private PendingIntent updatePendingIntent = null;

    DownloadThread downFileThread;  //自定义文件下载线程

    private Handler updateHandler = new  Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case DOWNLOAD_COMPLETE:
                    //点击安装PendingIntent
                    Uri uri = Uri.fromFile(downFileThread.getApkFile());
//                    Intent installIntent = new Intent(Intent.ACTION_VIEW);
//                    installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setDataAndType (uri, "video/*");
                    updatePendingIntent = PendingIntent.getActivity(DownloadService.this, 0, intent, 0);
                    myNotification.changeContentIntent(updatePendingIntent);
                    myNotification.notification.defaults=Notification.DEFAULT_SOUND;//铃声提醒
                    myNotification.changeNotificationText(downFileThread.getApkFile().getAbsolutePath());

                    //停止服务
                    //  myNotification.removeNotification();
                    stopSelf();
                    break;
                case DOWNLOAD_FAIL:
                    //下载失败
                    //                  myNotification.changeProgressStatus(DOWNLOAD_FAIL);
                    myNotification.changeNotificationText("文件下载失败！");
                    stopSelf();
                    break;
                default:  //下载中
                    Log.i("service", "default"+msg.what);
                    //          myNotification.changeNotificationText(msg.what+"%");
                    myNotification.changeProgressStatus(msg.what, 0);
            }
        }
    };

    public DownloadService() {
        // TODO Auto-generated constructor stub
        //  mcontext=context;
        Log.i("service","DownloadServices1");

    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        Log.i("service","onCreate");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        Log.i("service","onDestroy");
        if(downFileThread!=null)
            downFileThread.interuptThread();
        stopSelf();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Log.i("service","onStartCommand");

        String url = intent.getExtras().getString("url");
        String local = intent.getExtras().getString("local");
        updateIntent = new Intent(this, MainActivity.class);
        PendingIntent   updatePendingIntent = PendingIntent.getActivity(this,0,updateIntent,0);
        myNotification=new MyNotification(this, updatePendingIntent, 1);
        myNotification.path = local;
        //  myNotification.showDefaultNotification(R.drawable.ic_launcher, "测试", "开始下载");
        myNotification.showCustomizeNotification(R.drawable.ic_launcher, "视频下载", R.layout.notification);

        filePathString=local;

        //开启一个新的线程下载，如果使用Service同步下载，会导致ANR问题，Service本身也会阻塞
        downFileThread=new DownloadThread(updateHandler, url,filePathString);
        new Thread(downFileThread).start();

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    @Deprecated
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        Log.i("service","onStart");
        super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        Log.i("service","onBind");
        return null;
    }

}