package com.qq456cvb.videoview.Subviews.Profile;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qq456cvb.videoview.R;
import com.qq456cvb.videoview.Utils.DebugUser;

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
    private Button confirm;
    private Button cancel;
    private View view;
    private DebugUser debugUser = new DebugUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        debugUser.setUsername("username");
        debugUser.setName("test");
        debugUser.setPhone("123456");

        view = inflater.inflate(R.layout.profile_config, container, false);
        findViews();
        bindOnClickListeners();

        loadDataFromUser();
        return view;
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
        confirm = (Button)view.findViewById(R.id.configConfirm);
        cancel = (Button)view.findViewById(R.id.configCancel);
    }

    public void loadDataFromUser() {
        username.setText(debugUser.getUsername());
        name.setText(debugUser.getName());
        phone.setText(debugUser.getPhone());
    }

    public void bindOnClickListeners() {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!name.getText().toString().isEmpty() && !phone.getText().toString().isEmpty()) {
                    debugUser.setName(name.getText().toString());
                    debugUser.setPhone(phone.getText().toString());
                    loadDataFromUser();
                } else {
                    Toast.makeText(view.getContext(), "必填项不能为空", Toast.LENGTH_SHORT).show();
                    loadDataFromUser();
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
}