package com.qq456cvb.videoview.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qq456cvb.videoview.Application.GlobalApp;
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
    private UserClient user;
    private SharedPreferences sharedPreferences;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        findViews();

        sharedPreferences = getSharedPreferences("cookie", Activity.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String password = sharedPreferences.getString("password", "");
        if (!username.isEmpty() && !password.isEmpty()) {
            usernameEditText.setText(username);
            passwordEditText.setText(password);
        }
        user = GlobalApp.user;

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
                if (response.contains("登录失败")) {
                    Toast.makeText(LoginActivity.this, "登录失败，请检查用户名和密码", Toast.LENGTH_SHORT).show();
                } else if (response.contains("频道信息")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", usernameEditText.getText().toString());
                    editor.putString("password", passwordEditText.getText().toString());
                    editor.commit();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Toast.makeText(LoginActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
            }
        };
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setUsername(usernameEditText.getText().toString());
                user.setPassword(passwordEditText.getText().toString());
                if (usernameEditText.getText().length() == 0 || passwordEditText.getText().length() == 0) {
                    Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            RequestParams params = new RequestParams();
                            params.put("userMapping.userName", usernameEditText.getText());
                            params.put("userMapping.password", passwordEditText.getText());
                            user.post("/stpy/logonAction!doLogin.action", params, handler);
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
