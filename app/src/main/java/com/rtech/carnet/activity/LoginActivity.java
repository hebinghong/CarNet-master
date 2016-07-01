package com.rtech.carnet.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SignUpCallback;
import com.rtech.carnet.MainApplication;
import com.rtech.carnet.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        if (AVUser.getCurrentUser() != null) {
            ((TextView) findViewById(R.id.register_email)).setText("");
            ((TextView) findViewById(R.id.register_username)).setText("");
            ((TextView) findViewById(R.id.register_password)).setText("");
            ((TextView) findViewById(R.id.password_confirm)).setText("");
            findViewById(R.id.login_fragment).setVisibility(View.VISIBLE);
            findViewById(R.id.register_fragment).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.login_username)).setText(AVUser.getCurrentUser().getUsername());
        }
        else {
            ((TextView) findViewById(R.id.login_username)).setText("");
            ((TextView) findViewById(R.id.login_password)).setText("");
            findViewById(R.id.login_fragment).setVisibility(View.GONE);
            findViewById(R.id.register_fragment).setVisibility(View.VISIBLE);
        }

        setOnClickListener();

    }

    private void setOnClickListener() {

        findViewById(R.id.goto_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) findViewById(R.id.register_email)).setText("");
                ((TextView) findViewById(R.id.register_username)).setText("");
                ((TextView) findViewById(R.id.register_password)).setText("");
                ((TextView) findViewById(R.id.password_confirm)).setText("");
                findViewById(R.id.login_fragment).setVisibility(View.VISIBLE);
                findViewById(R.id.register_fragment).setVisibility(View.GONE);
            }
        });

        findViewById(R.id.goto_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) findViewById(R.id.login_username)).setText("");
                ((TextView) findViewById(R.id.login_password)).setText("");
                findViewById(R.id.login_fragment).setVisibility(View.GONE);
                findViewById(R.id.register_fragment).setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void login() {
        TextView username = (TextView) findViewById(R.id.login_username);
        TextView password = (TextView) findViewById(R.id.login_password);
        if (username.getText().toString().trim().equals("")) {
            username.setFocusable(true);
            username.setError("用户名不能为空");
            return;
        }
        if (password.getText().toString().trim().equals("")) {
            password.setFocusable(true);
            password.setError("密码不能为空");
            return;
        }
        AVUser.logInInBackground(username.getText().toString().trim(), password.getText().toString().trim(), new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                if (e == null) {
                    MainApplication.user = avUser;
                    Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, ScrollingActivity.class));
                    finish();
                }
                else {
                    if (e.getCode() == 210 || e.getCode() == 211) Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(LoginActivity.this, "无法连接网络", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void register() {

        final TextView email = (TextView) findViewById(R.id.register_email);
        final TextView username = (TextView) findViewById(R.id.register_username);
        TextView password = (TextView) findViewById(R.id.register_password);
        TextView confirm = (TextView) findViewById(R.id.password_confirm);
        if (email.getText().toString().trim().equals("")) {
            email.setFocusable(true);
            email.setError("注册邮箱不能为空");
            return;
        }

        if (username.getText().toString().trim().equals("")) {
            username.setFocusable(true);
            username.setError("用户名不能为空");
            return;
        }

        if (password.getText().toString().trim().equals("")) {
            password.setFocusable(true);
            password.setError("密码不能为空");
            return;
        }

        if (confirm.getText().toString().trim().equals("")) {
            confirm.setFocusable(true);
            confirm.setError("确认密码不能为空");
            return;
        }

        if (!confirm.getText().toString().trim().equals(password.getText().toString())) {
            confirm.setFocusable(true);
            confirm.setText("");
            confirm.setError("确认密码不匹配");
            return;
        }

        final AVUser user = new AVUser();
        user.setEmail(email.getText().toString().trim());
        user.setUsername(username.getText().toString().trim());
        user.setPassword(password.getText().toString().trim());
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    findViewById(R.id.login_fragment).setVisibility(View.VISIBLE);
                    findViewById(R.id.register_fragment).setVisibility(View.GONE);
                    ((TextView) findViewById(R.id.login_username)).setText(user.getUsername());

                }
                else {
                    switch (e.getCode()) {
                        case 202:
                            username.setFocusable(true);
                            username.setError("用户名已存在");
                            break;
                        case 203:
                            email.setFocusable(true);
                            email.setError("邮箱已存在");
                            break;
                        default:
                            Toast.makeText(LoginActivity.this, "无法连接网络", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
