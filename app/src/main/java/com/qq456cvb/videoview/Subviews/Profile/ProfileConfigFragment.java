package com.qq456cvb.videoview.Subviews.Profile;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qq456cvb.videoview.Application.GlobalApp;
import com.qq456cvb.videoview.R;
import com.qq456cvb.videoview.Utils.UserClient;

import org.apache.http.Header;

/**
 * Created by qq456cvb on 8/22/15.
 */
public class ProfileConfigFragment extends Fragment {
    private TextView username;
    private EditText name;
    private EditText oldPwd;
    private EditText newPwd;
    private EditText newPwdCfm;
    private EditText phone;
    private EditText mail;
    private EditText remark;
    private EditText depart;
    private EditText role;
    private Button confirm;
    private Button cancel;
    private View view;
    private UserClient userClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        view = inflater.inflate(R.layout.profile_config, container, false);
        userClient = GlobalApp.user;
        findViews();
        bindOnClickListeners();


        return view;
    }

    public void onHiddenChanged(boolean hidden) {
        if (!hidden && userClient != null) {
            loadDataFromUser();
        }
    }

    public void findViews() {
        username = (TextView)view.findViewById(R.id.username);
        name = (EditText)view.findViewById(R.id.name);
        oldPwd = (EditText)view.findViewById(R.id.oldPwd);
        newPwd = (EditText)view.findViewById(R.id.newPwd);
        newPwdCfm = (EditText)view.findViewById(R.id.newPwdCfm);
        phone = (EditText)view.findViewById(R.id.phone);
        mail = (EditText)view.findViewById(R.id.mail);
        remark = (EditText)view.findViewById(R.id.remark);
        depart = (EditText)view.findViewById(R.id.depart);
        role = (EditText)view.findViewById(R.id.role);
        confirm = (Button)view.findViewById(R.id.configConfirm);
        cancel = (Button)view.findViewById(R.id.configCancel);
    }

    public void loadDataFromUser() {
        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, String response) {
                parseUser(response);
                username.setText(userClient.getUsername());
                name.setText(userClient.realname);
                oldPwd.setText(userClient.getPassword());
                phone.setText(userClient.telephone);
                mail.setText(userClient.email);
                remark.setText(userClient.remark);
                depart.setText(userClient.department);
                role.setText(userClient.role);
            }

            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Log.d("test", "sssss");
            }
        };
        new Thread() {
            @Override
            public void run() {
                try {
                    UserClient.get("/stpy/personalAction!doupdateUser.action", null, handler);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void bindOnClickListeners() {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!name.getText().toString().isEmpty() && !phone.getText().toString().isEmpty()) {
                    if (!newPwd.getText().toString().equals((newPwdCfm.getText().toString()))) {
                        Toast.makeText(view.getContext(), "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                    } else {
                        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
                            public void onSuccess(int statusCode, Header[] headers, String response) {
                                //TODO
                                Toast.makeText(view.getContext(), "更新成功", Toast.LENGTH_SHORT).show();
                            }

                            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                                Log.d("test", "sssss");
                            }
                        };
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    RequestParams params = new RequestParams();
                                    params.put("userMapping.reallyname", name.getText().toString());
                                    params.put("userMapping.password", newPwdCfm.getText().toString());
                                    params.put("userMapping.telephone", phone.getText().toString());
                                    params.put("userMapping.emai", mail.getText().toString());
                                    params.put("userMapping.remark", remark.getText().toString());
                                    params.put("id", userClient.id);
//                                    UserClient.addHeader("Content-Type", "application/x-www-form-urlencoded");
                                    UserClient.post("/stpy/personalAction!updateUser.action", params, handler);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();

                        loadDataFromUser();
                    }
                } else {
                    Toast.makeText(view.getContext(), "必填项不能为空", Toast.LENGTH_SHORT).show();
//                    loadDataFromUser();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDataFromUser();
            }
        });
    }

    void parseUser(String html) {
        String sub = html.substring(html.indexOf("userMapping.reallyname"));
        userClient.realname = sub.substring(sub.indexOf("value=")+7, sub.indexOf("id=")-2);
        sub = html.substring(html.indexOf("id=\"oldpassword\""));
        userClient.setPassword(sub.substring(sub.indexOf("value=")+7, sub.indexOf("\r\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\tdisabled=")-1));
        sub = html.substring(html.indexOf("userMapping.telephone"));
        userClient.telephone = sub.substring(sub.indexOf("value=")+7, sub.indexOf("\r\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\tmaxlength=")-1);
        sub = html.substring(html.indexOf("userMapping.emai"));
        userClient.email = sub.substring(sub.indexOf("value=")+7, sub.indexOf("/></td>")-2);
        sub = html.substring(html.indexOf("userMapping.remark"));
        userClient.remark = sub.substring(sub.indexOf("(0,300)\">")+9, sub.indexOf("</textarea>"));
        sub = html.substring(html.indexOf("<input type=\"hidden\" name=\"id\" value="));
        userClient.id = sub.substring(sub.indexOf("value=")+7, sub.indexOf("/>")-1);
        userClient.department = "福建省广播电视节目收听收看中心";
        userClient.role = "评议员";
    }
}