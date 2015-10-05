package com.qq456cvb.videoview.Utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.widget.RemoteViews;

import com.qq456cvb.videoview.R;

/**
 * Notification类，既可用系统默认的通知布局，也可以用自定义的布局
 *
 * @author lz
 *
 */
public class MyNotification {
    public final static int DOWNLOAD_COMPLETE = -2;
    public final static int DOWNLOAD_FAIL = -1;
    Context mContext;   //Activity或Service上下文
    public Notification notification;  //notification
    NotificationManager nm;
    String titleStr;   //通知标题
    String contentStr; //通知内容
    PendingIntent contentIntent; //点击通知后的动作
    int notificationID;   //通知的唯一标示ID
    int iconID;         //通知栏图标
    long when = System.currentTimeMillis();
    public String path;
    RemoteViews remoteView=null;  //自定义的通知栏视图
    /**
     *
     * @param context Activity或Service上下文
     * @param contentIntent  点击通知后的动作
     * @param id    通知的唯一标示ID
     */
    public MyNotification(Context context,PendingIntent contentIntent,int id) {
        // TODO Auto-generated constructor stub
        mContext=context;
        notificationID=id;
        this.contentIntent=contentIntent;
        this.nm=(NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * 显示自定义通知
     * @param icoId 自定义视图中的图片ID
     * @param titleStr 通知栏标题
     * @param layoutId 自定义布局文件ID
     */
    public void showCustomizeNotification(int icoId,String titleStr,int layoutId) {
        this.titleStr=titleStr;
        notification=new Notification(R.drawable.ic_launcher, titleStr, when);
        notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.contentIntent=this.contentIntent;

        // 1、创建一个自定义的消息布局 view.xml
        // 2、在程序代码中使用RemoteViews的方法来定义image和text。然后把RemoteViews对象传到contentView字段
        if(remoteView==null)
        {
            remoteView = new RemoteViews(mContext.getPackageName(),layoutId);
            remoteView.setImageViewResource(R.id.ivNotification, icoId);
            remoteView.setTextViewText(R.id.tvTitle, titleStr);
            remoteView.setTextViewText(R.id.tvTip, "开始下载");
            remoteView.setProgressBar(R.id.pbNotification, 100, 0, false);
            notification.contentView = remoteView;
        }
        nm.notify(notificationID, notification);
    }
    /**
     * 更改自定义布局文件中的进度条的值
     * @param p 进度值(0~100)
     */
    public void changeProgressStatus(int p, int type)
    {
        if (type == 0) {
            if (notification.contentView != null) {
                if (p == DOWNLOAD_FAIL)
                    notification.contentView.setTextViewText(R.id.tvTip, "下载失败！ ");
                else if (p == 100)
                    notification.contentView.setTextViewText(R.id.tvTip, "下载完成");
                else
                    notification.contentView.setTextViewText(R.id.tvTip, "进度(" + p + "%) : ");
                notification.contentView.setProgressBar(R.id.pbNotification, 100, p, false);
            }
        } else {
            if (notification.contentView != null) {
                if (p == DOWNLOAD_FAIL)
                    notification.contentView.setTextViewText(R.id.tvTip, "下载失败！ ");
                else
                    notification.contentView.setTextViewText(R.id.tvTip, "进度(" + p + "KB/大小未知) : ");
                notification.contentView.setProgressBar(R.id.pbNotification, 100, p, false);
            }
        }
        nm.notify(notificationID, notification);
    }
    public void changeContentIntent(PendingIntent intent)
    {
        this.contentIntent=intent;
        notification.contentIntent=intent;
    }
    /**
     * 显示系统默认格式通知
     * @param iconId 通知栏图标ID
     * @param titleText 通知栏标题
     * @param contentStr 通知栏内容
     */
    public void showDefaultNotification(int iconId,String titleText,String contentStr) {
        this.titleStr=titleText;
        this.contentStr=contentStr;
        this.iconID=iconId;

        notification=new Notification();
        notification.tickerText=titleStr;
        notification.icon=iconID;
        notification.flags = Notification.FLAG_INSISTENT;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.contentIntent=this.contentIntent;

        // 添加声音效果
        // notification.defaults |= Notification.DEFAULT_SOUND;

        // 添加震动,后来得知需要添加震动权限 : Virbate Permission
        // mNotification.defaults |= Notification.DEFAULT_VIBRATE ;

        //添加状态标志
        //FLAG_AUTO_CANCEL        该通知能被状态栏的清除按钮给清除掉
        //FLAG_NO_CLEAR           该通知能被状态栏的清除按钮给清除掉
        //FLAG_ONGOING_EVENT      通知放置在正在运行
        //FLAG_INSISTENT          通知的音乐效果一直播放
        notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        changeNotificationText(contentStr);
    }
    /**
     * 改变默认通知栏的通知内容
     * @param content
     */
    public void changeNotificationText(String content)
    {
        notification.setLatestEventInfo(mContext, titleStr, content,contentIntent);

        // 设置setLatestEventInfo方法,如果不设置会App报错异常
        //  NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //注册此通知
        // 如果该NOTIFICATION_ID的通知已存在，会显示最新通知的相关信息 ，比如tickerText 等
        nm.notify(notificationID, notification);
    }

    /**
     * 移除通知
     */
    public void removeNotification()
    {
        // 取消的只是当前Context的Notification
        nm.cancel(notificationID);
    }

}