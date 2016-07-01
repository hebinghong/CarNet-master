package com.rtech.carnet.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.rtech.carnet.dialog.DialogHelper;
import com.rtech.carnet.MainApplication;
import com.rtech.carnet.R;
import com.rtech.carnet.qrcode_manage.qr_codescan.MipcaActivityCapture;
import com.rtech.carnet.qrcode_manage.qr_codescan.QrCodeActivity;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView pauseButton;
    private MediaPlayer player;
    private String[] musics;
    private int playingMusic = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pauseButton = (ImageView) findViewById(R.id.pause);

        checkUpdate();
        playInit();
        setOnClickListener();
        ((TextView) findViewById(R.id.username)).setText(MainApplication.user.getUsername());
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
                play(playingMusic + 1);
            }
        });
        play(playingMusic);
    }

    private void play(int which) {
        if (musics == null) return;
        playingMusic = which;
        if (playingMusic < 0) playingMusic = 0;
        if (playingMusic >= musics.length) playingMusic = musics.length - 1;
        if (player.isPlaying()) player.reset();
        try {
            player.setDataSource(musics[playingMusic]);
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setOnClickListener() {
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
                play(playingMusic - 1);
            }
        });
        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(playingMusic + 1);
            }
        });
        findViewById(R.id.clwh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CarListActivity.class));
            }
        });
        findViewById(R.id.map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapActivity.class));
            }
        });
        findViewById(R.id.scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MipcaActivityCapture.class));
            }
        });
        findViewById(R.id.user_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UserActivity.class));
            }
        });
        findViewById(R.id.my_yuyue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, OrderActivity.class));
            }
        });
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
                    if (code <= getVersionCode(MainActivity.this)) return;
                    DialogHelper.showCommonDialog(MainActivity.this
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
