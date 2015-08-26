package com.qq456cvb.videoview.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qq456cvb.videoview.R;
import com.qq456cvb.videoview.Utils.UserClient;

import org.apache.http.Header;

/**
 * Created by qq456cvb on 8/26/15.
 */
public class LoginActivity extends Activity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        findViews();
        bindOnClickListeners();
    }

    public void findViews() {
        usernameEditText = (EditText) findViewById(R.id.login_username);
        passwordEditText = (EditText) findViewById(R.id.login_password);
        loginButton = (Button) findViewById(R.id.login_cfm);
    }

    public void bindOnClickListeners() {
        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, String response) {
                // If the response is JSONObject instead of expected JSONArray
                System.out.println(response);
            }

            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {

            }
        };
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            RequestParams params = new RequestParams();
                            params.put("userMapping.userName", "ceshi");
                            params.put("userMapping.password", "1234");
                            UserClient.post("/stpy/logonAction!doLogin.action", params, handler);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }
}
