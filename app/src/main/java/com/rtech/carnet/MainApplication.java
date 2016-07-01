package com.rtech.carnet;

import android.app.Application;
import android.content.Intent;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.cheshouye.api.client.WeizhangIntentService;

/*
 * Created by zhimeng on 2016/4/24.
 */
public class MainApplication extends Application {

    public static AVUser user;

    @Override
    public void onCreate() {
        super.onCreate();
        //如果使用美国节点，请加上这行代码 AVOSCloud.useAVCloudUS();
        AVOSCloud.initialize(this, "HzzGHKFRBeyeCag3OcB0R8Tk-gzGzoHsz", "touu3vSP3Oul2kVRbdGoo3dP");
        Intent weizhangIntent = new Intent(this, WeizhangIntentService.class);
        weizhangIntent.putExtra("appId", 1689);
        weizhangIntent.putExtra("appKey", "b9dd98f7662423dcaf2e40d7fb796153");
        startService(weizhangIntent);
    }
}
