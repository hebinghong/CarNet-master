package com.rtech.carnet.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.rtech.carnet.MainApplication;
import com.rtech.carnet.R;
import com.rtech.carnet.adapter.CarAdapter;
import com.rtech.carnet.dialog.DialogHelper;
import com.rtech.carnet.qrcode_manage.qr_codescan.MipcaActivityCapture;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScrollingActivity extends AppCompatActivity {

    public static ArrayList<AVObject> cars;
    private CarAdapter adapter;

    private ImageView pauseButton;
    private MediaPlayer player;
    private String[] musics;
    private int playingMusic = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrolling_real_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView list = ((RecyclerView) findViewById(R.id.car_list));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(linearLayoutManager);
        list.setHasFixedSize(true);
        cars = new ArrayList<>();
        adapter = new CarAdapter(this, new ArrayList<AVObject>());
        list.setAdapter(adapter);/*

        ((SwipeRefreshLayout) findViewById(R.id.swipe_layout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadCars();
            }
        });*/
        loadCars();

        checkUpdate();
        playInit();
        setOnClickListener();

        findViewById(R.id.add_car).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CarInfoActivity.car = null;
                startActivity(new Intent(ScrollingActivity.this, CarInfoActivity.class));
            }
        });
    }

    private void loadCars() {
        //((SwipeRefreshLayout) findViewById(R.id.swipe_layout)).setRefreshing(true);
        ((TextView) findViewById(R.id.car_message)).setText("");
        AVQuery<AVObject> query = AVQuery.getQuery("car");
        query.whereEqualTo("owner", MainApplication.user);
        int c = cars.size();
        cars.clear();
        if (c != 0) adapter.notifyItemRangeRemoved(0, c);
        ((TextView) findViewById(R.id.car_message)).setText("正在加载");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                //((SwipeRefreshLayout) findViewById(R.id.swipe_layout)).setRefreshing(false);
                if (e == null) {
                    int i = 0;
                    while (i < list.size()) {
                        cars.add(list.get(i));
                        i++;
                    }
                    adapter.cars = cars;
                    adapter.notifyDataSetChanged();
                    if (cars.size() == 0) ((TextView) findViewById(R.id.car_message)).setText("你没有添加任何车辆");
                    else ((TextView) findViewById(R.id.car_message)).setText("");
                }
                else if (adapter.getItemCount() == 0) ((TextView) findViewById(R.id.car_message)).setText("无法连接上网络");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.map:
                startActivity(new Intent(ScrollingActivity.this, MapActivity.class));
                break;
            case R.id.order:
                startActivity(new Intent(ScrollingActivity.this, OrderActivity.class));
                break;
            case R.id.scan:
                startActivity(new Intent(ScrollingActivity.this, MipcaActivityCapture.class));
                break;
            case R.id.query:
                startActivity(new Intent(ScrollingActivity.this, com.rtech.carnet.deahu.activity.MainActivity.class));
                break;
            case R.id.account:
                startActivity(new Intent(ScrollingActivity.this, UserActivity.class));
                break;
            case R.id.logout:
                AVUser.logOut();
                MainApplication.user = null;
                startActivity(new Intent(ScrollingActivity.this, LoginActivity.class));
                finish();
                break;
        }
        return true;
    }

    public void setOnClickListener() {
        pauseButton = (ImageView) findViewById(R.id.pause);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    player.pause();
                    pauseButton.setImageResource(R.drawable.ic_play_circle_outline_white_24dp);
                }
                else {
                    player.start();
                    pauseButton.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
                }
            }
        });
        findViewById(R.id.prev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    play(playingMusic - 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    play(playingMusic + 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void playInit() {
        musics = getVideoPath(this);
        if (musics == null) return;
        player = new MediaPlayer();
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                player.reset();
            }
        });
        try {
            play(playingMusic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void play(int which) throws Exception{
        if (musics == null) return;
        playingMusic = which;
        if (playingMusic < 0) playingMusic = 0;
        if (playingMusic >= musics.length) playingMusic = musics.length - 1;
        ((TextView) findViewById(R.id.music_name)).setText(new File(musics[playingMusic]).getName());
        if (player.isPlaying()) player.reset();
        try {
            player.setDataSource(musics[playingMusic]);
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String[] getVideoPath(Context context) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor == null) return null;
        cursor.moveToFirst();
        int counter = cursor.getCount();
        String[] path = new String[counter];
        int i = 0;
        while (i< counter) {
            path[i] = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            cursor.moveToNext();
            i++;
        }
        cursor.close();
        return path;
    }

    private static int getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();//context为当前Activity上下文
        PackageInfo packageInfo;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void checkUpdate() {
        AVQuery<AVObject> query = AVQuery.getQuery("version");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null && list != null && list.size() > 0) {
                    final AVObject object = list.get(0);
                    int code = object.getInt("version_code");
                    if (code <= getVersionCode(ScrollingActivity.this)) return;
                    DialogHelper.showCommonDialog(ScrollingActivity.this
                            , "发现新版本"
                            , object.getString("updateMsg")
                            , "下载", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(object.getAVFile("apk").getUrl())));
                                }
                            });
                }
            }
        });
    }
}
