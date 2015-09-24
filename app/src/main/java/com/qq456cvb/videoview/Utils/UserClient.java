package com.qq456cvb.videoview.Utils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by qq456cvb on 8/26/15.
 */
public class UserClient {
    private static final String BASE_URL = "http://120.40.101.185";
    private static AsyncHttpClient client = new AsyncHttpClient();
    private String username;
    private String password;

    public String id;
    public String realname;
    public String telephone;
    public String email;
    public String remark;
    public String department;
    public String role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static void addHeader(String header, String value) {
        client.addHeader(header, value);
    }
    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
