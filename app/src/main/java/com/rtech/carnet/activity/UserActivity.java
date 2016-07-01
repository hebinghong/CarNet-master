package com.rtech.carnet.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.rtech.carnet.MainApplication;
import com.rtech.carnet.R;
import com.rtech.carnet.dialog.EditDialog;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        ((TextView) findViewById(R.id.username)).setText(MainApplication.user.getUsername());
        ((TextView) findViewById(R.id.real_name)).setText("真实姓名：" + MainApplication.user.getString("real_name"));
        findViewById(R.id.real_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EditDialog(UserActivity.this, "修改姓名", false, new EditDialog.OnEditListener() {
                    @Override
                    public void done(String string) {
                        ((TextView) findViewById(R.id.real_name)).setText("真实姓名：" + string);
                        MainApplication.user.put("real_name", string);
                        MainApplication.user.saveInBackground();
                    }
                });
            }
        });
        ((TextView) findViewById(R.id.phone)).setText("联系电话：" + MainApplication.user.getString("phone"));
        findViewById(R.id.phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EditDialog(UserActivity.this, "修改电话", true, new EditDialog.OnEditListener() {
                    @Override
                    public void done(String string) {
                        ((TextView) findViewById(R.id.phone)).setText("联系电话：" + string);
                        MainApplication.user.put("phone", string);
                        MainApplication.user.saveInBackground();
                    }
                });
            }
        });
        ((TextView) findViewById(R.id.email)).setText("电子邮件：" + MainApplication.user.getEmail());
    }
}
