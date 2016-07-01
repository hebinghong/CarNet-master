package com.rtech.carnet.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.SaveCallback;
import com.cheshouye.api.client.WeizhangClient;
import com.cheshouye.api.client.json.CarInfo;
import com.cheshouye.api.client.json.WeizhangResponseJson;
import com.rtech.carnet.MainApplication;
import com.rtech.carnet.R;
import com.rtech.carnet.deahu.activity.*;
import com.rtech.carnet.dialog.DialogHelper;
import com.rtech.carnet.dialog.EditDialog;
import com.rtech.carnet.qrcode_manage.qr_codescan.MipcaActivityCapture;

public class CarInfoActivity extends AppCompatActivity {

    private static class MyHandler extends Handler {

        private CarInfoActivity activity;

        public MyHandler(CarInfoActivity activity) {
            this.activity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            WeizhangResponseJson info = (WeizhangResponseJson) msg.obj;
            DialogHelper.showCommonDialog(activity, "违章记录", "共违章" + info.getCount() + "次, 计"
                    + info.getTotal_score() + "分, 罚款 " + info.getTotal_money()
                    + "元", "确定", null);
        }
    }

    public static AVObject car;
    public static int position;
    private AVObject newCar = new AVObject("car");
    TextView cph, cjh, qcpp, bz, xh, fdjh, csjb, lcs, qyl;
    CheckBox cd, bsqxn, fdjxn;
    private MyHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_info);
        handler = new MyHandler(this);
        if (car == null) {
            ((TextView) findViewById(R.id.car_message)).setText("新建车辆：点击相应信息进行编辑");
            findViewById(R.id.add_car).setVisibility(View.VISIBLE);
        }
        else {
            ((TextView) findViewById(R.id.car_message)).setText("点击相应信息进行编辑");
            findViewById(R.id.add_car).setVisibility(View.GONE);
        }

        cph = ((TextView) findViewById(R.id.cph));
        cph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EditDialog(CarInfoActivity.this, "车牌号", false, new EditDialog.OnEditListener() {
                    @Override
                    public void done(String string) {
                        if (car == null) {
                            cph.setText("车牌号：" + string);
                            if (!string.trim().equals("")) newCar.put("cph", string);
                        }
                        else {
                            car.put("cph", string);
                            car.saveInBackground(createSaveCallBack(cph, "车牌号：", string));
                        }
                    }
                });
            }
        });

        cjh = ((TextView) findViewById(R.id.cjh));
        cjh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EditDialog(CarInfoActivity.this, "车架号", false, new EditDialog.OnEditListener() {
                    @Override
                    public void done(String string) {
                        if (car == null) {
                            cjh.setText("车架号：" + string);
                            if (!string.trim().equals("")) newCar.put("cjh", string);
                        }
                        else {
                            car.put("cjh", string);
                            car.saveInBackground(createSaveCallBack(cjh, "车牌号：", string));
                        }
                    }
                });
            }
        });

        qcpp = ((TextView) findViewById(R.id.qcpp));
        qcpp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EditDialog(CarInfoActivity.this, "汽车品牌", false, new EditDialog.OnEditListener() {
                    @Override
                    public void done(String string) {
                        if (car == null) {
                            qcpp.setText("汽车品牌：" + string);
                            if (!string.trim().equals("")) newCar.put("qcpp", string);
                        }
                        else {
                            car.put("qcpp", string);
                            car.saveInBackground(createSaveCallBack(qcpp, "汽车品牌：", string));
                        }
                    }
                });
            }
        });
        bz = ((TextView) findViewById(R.id.bz));
        bz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EditDialog(CarInfoActivity.this, "标志", false, new EditDialog.OnEditListener() {
                    @Override
                    public void done(String string) {
                        if (car == null) {
                            bz.setText("标志：" + string);
                            if (!string.trim().equals("")) newCar.put("bz", string);
                        }
                        else {
                            car.put("bz", string);
                            car.saveInBackground(createSaveCallBack(bz, "标志：", string));
                        }
                    }
                });
            }
        });
        xh = ((TextView) findViewById(R.id.xh));
        xh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EditDialog(CarInfoActivity.this, "型号", false, new EditDialog.OnEditListener() {
                    @Override
                    public void done(String string) {
                        if (car == null) {
                            xh.setText("型号：" + string);
                            if (!string.trim().equals("")) newCar.put("xh", string);
                        }
                        else {
                            car.put("xh", string);
                            car.saveInBackground(createSaveCallBack(xh, "型号：", string));
                        }
                    }
                });
            }
        });
        fdjh = ((TextView) findViewById(R.id.fdjh));
        fdjh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EditDialog(CarInfoActivity.this, "发动机号", true, new EditDialog.OnEditListener() {
                    @Override
                    public void done(String string) {
                        if (car == null) {
                            fdjh.setText("发动机号：" + string);
                            if (!string.trim().equals("")) newCar.put("fdjh", string);
                        }
                        else {
                            car.put("fdjh", string);
                            car.saveInBackground(createSaveCallBack(fdjh, "发动机号：", string));
                        }
                    }
                });
            }
        });
        csjb = ((TextView) findViewById(R.id.csjb));
        csjb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EditDialog(CarInfoActivity.this, "车身级别", false, new EditDialog.OnEditListener() {
                    @Override
                    public void done(String string) {
                        if (car == null) {
                            csjb.setText("车身级别：" + string);
                            if (!string.trim().equals("")) newCar.put("csjb", string);
                        }
                        else {
                            car.put("csjb", string);
                            car.saveInBackground(createSaveCallBack(csjb, "车身级别：", string));
                        }
                    }
                });
            }
        });
        lcs = ((TextView) findViewById(R.id.lcs));
        lcs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EditDialog(CarInfoActivity.this, "里程数(km)", true, new EditDialog.OnEditListener() {
                    @Override
                    public void done(String string) {
                        try {
                            int i = Integer.parseInt(string);
                            if (i < 0) {
                                Toast.makeText(CarInfoActivity.this, "里程数不能为负数", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        catch (Exception e) {
                            Toast.makeText(CarInfoActivity.this, "只能填写数字", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (car == null) {
                            lcs.setText("里程数(km)：" + string);
                            if (!string.trim().equals("")) newCar.put("lcs", Integer.parseInt(string));
                        }
                        else {
                            car.put("lcs", Integer.parseInt(string));
                            car.saveInBackground(createSaveCallBack(lcs, "里程数(km)：", string));
                        }
                    }
                });
            }
        });
        qyl = ((TextView) findViewById(R.id.qyl));
        qyl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EditDialog(CarInfoActivity.this, "汽油量(%)", true, new EditDialog.OnEditListener() {
                    @Override
                    public void done(String string) {
                        try {
                            int i = Integer.parseInt(string);
                            if (i < 0 || i > 100) {
                                Toast.makeText(CarInfoActivity.this, "汽油箱百分比只能是0到100之间", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        catch (Exception e) {
                            Toast.makeText(CarInfoActivity.this, "只能填写数字", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (car == null) {
                            qyl.setText("汽油量(%)：" + string);
                            if (!string.trim().equals("")) newCar.put("qyl", Integer.parseInt(string));
                        }
                        else {
                            car.put("qyl", Integer.parseInt(string));
                            car.saveInBackground(createSaveCallBack(qyl, "汽油量(%)：", string));
                        }
                    }
                });
            }
        });
        fdjxn = (CheckBox) findViewById(R.id.fdjxn);
        fdjxn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (car == null) newCar.put("fdjxn", fdjxn.isChecked());
                else {
                    car.put("fdjxn", fdjxn.isChecked());
                    car.saveInBackground();
                }
            }
        });
        bsqxn = (CheckBox) findViewById(R.id.bsqxn);
        bsqxn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (car == null) newCar.put("bsqxn", bsqxn.isChecked());
                else {
                    car.put("bsqxn", bsqxn.isChecked());
                    car.saveInBackground();
                }
            }
        });
        cd = (CheckBox) findViewById(R.id.cd);
        cd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (car == null) newCar.put("cd", cd.isChecked());
                else {
                    car.put("cd", cd.isChecked());
                    car.saveInBackground();
                }
            }
        });
        findViewById(R.id.add_car).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newCar.get("cph") == null || newCar.getString("cph").trim().equals("")) {
                    Toast.makeText(CarInfoActivity.this, "你至少要填写车牌号信息", Toast.LENGTH_SHORT).show();
                }
                newCar.put("owner", MainApplication.user);
                newCar.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            Toast.makeText(CarInfoActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
                            ScrollingActivity.cars.add(newCar);
                            finish();
                        }
                        else Toast.makeText(CarInfoActivity.this, "无法连接上网络", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        if (car == null) return;
        cph.setText("车牌号：" + car.getString("cph"));
        cjh.setText("车架号：" + car.getString("cjh"));
        qcpp.setText("汽车品牌：" + car.getString("qcpp"));
        bz.setText("标志：" + car.getString("bz"));
        xh.setText("型号：" + car.getString("xh"));
        fdjh.setText("发动机号：" + car.getString("fdjh"));
        csjb.setText("车身级别：" + car.getString("csjb"));
        lcs.setText("里程数(km)：" + car.getInt("lcs"));
        qyl.setText("汽油量(%)：" + car.getInt("qyl") + "%");
        fdjxn.setChecked(car.getBoolean("fdjxn"));
        bsqxn.setChecked(car.getBoolean("bsqxn"));
        cd.setChecked(car.getBoolean("cd"));
    }

    private SaveCallback createSaveCallBack(final TextView view, final String title, final String string) {
        return new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) view.setText(title + string);
                else Toast.makeText(CarInfoActivity.this, "无法连接上网络", Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (car == null) return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_car, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.remove:
                car.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            Toast.makeText(CarInfoActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            if (ScrollingActivity.cars.size() > position) ScrollingActivity.cars.remove(position);
                            finish();
                        }
                        else{
                            Toast.makeText(CarInfoActivity.this, "无法连接网络", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }
        return true;
    }
}
