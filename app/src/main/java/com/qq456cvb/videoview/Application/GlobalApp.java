package com.qq456cvb.videoview.Application;

import android.app.Application;

import com.qq456cvb.videoview.Utils.UserClient;

/**
 * Created by qq456cvb on 8/27/15.
 */
public class GlobalApp extends Application {
    public static UserClient user;

    @Override
    public void onCreate() {
        super.onCreate();
        user = new UserClient();
    }
}
