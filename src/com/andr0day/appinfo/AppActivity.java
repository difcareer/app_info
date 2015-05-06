package com.andr0day.appinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.andr0day.appinfo.common.BusyboxUtil;
import com.andr0day.appinfo.common.FileUtils;
import com.andr0day.appinfo.common.RootUtil;

import java.io.File;

public class AppActivity extends Activity {

    private static final String TAG = "AppActivity";

    private static final int MSG_LOAD_START = 1;

    private static final int MSG_LOAD_FINISH = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button appInfoB = (Button) findViewById(R.id.appInfo);
        Button systemInfoB = (Button) findViewById(R.id.systemInfo);
        Button buildInfoB = (Button) findViewById(R.id.buildInfo);

        Button mountInfoB = (Button) findViewById(R.id.mountInfo);
        Button netInfoB = (Button) findViewById(R.id.netInfo);
        Button installBusyBox = (Button) findViewById(R.id.installBusybox);

        Button search777 = (Button) findViewById(R.id.seach777);
        Button openTop = (Button) findViewById(R.id.openTop);
        Button stopTop = (Button) findViewById(R.id.stopTop);

        Button startWifi = (Button) findViewById(R.id.startWifi);
        Button stopWifi = (Button) findViewById(R.id.stopWifi);

        systemInfoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(AppActivity.this, SystemInfoActivity.class);
                startActivity(intent);
            }
        });

        buildInfoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(AppActivity.this, BuildInfoActivity.class);
                startActivity(intent);
            }
        });

        appInfoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(AppActivity.this, AppInfoActivity.class);
                startActivity(intent);
            }
        });

        mountInfoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(AppActivity.this, MountInfoActivity.class);
                startActivity(intent);
            }
        });

        netInfoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(AppActivity.this, NetInfoActivity.class);
                startActivity(intent);
            }
        });

        installBusyBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BusyboxUtil.isInstalled()) {
                    Toast.makeText(AppActivity.this, "已经安装", Toast.LENGTH_SHORT).show();
                    return;
                }
                FileUtils.copyAssetsToFiles(AppActivity.this, BusyboxUtil.getBusyBox());
                File file = new File(AppActivity.this.getFilesDir(), BusyboxUtil.getBusyBox());
                String path = file.getAbsolutePath();
                String content = RootUtil.execStr("mount -o rw,remount /system; cp -r /system/xbin /system/xbin_bak; cp "
                        + path + " /system/xbin; chmod 755 /system/xbin/" + BusyboxUtil.getBusyBox() + "; /system/xbin/" + BusyboxUtil.getBusyBox()
                        + " --install /system/xbin/; mount -o ro,remount /system");
                if (BusyboxUtil.isInstalled()) {
                    Toast.makeText(AppActivity.this, "安装成功,安装路径：/system/xbin/", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        search777.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(AppActivity.this, Search777Activity.class);
                startActivity(intent);
            }
        });

        openTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(AppActivity.this, TopService.class);
                startService(intent);
            }
        });

        stopTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(AppActivity.this, TopService.class);
                stopService(intent);
            }
        });

        startWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(AppActivity.this, WifiService.class);
                startService(intent);
            }
        });

        stopWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(AppActivity.this, WifiService.class);
                stopService(intent);
            }
        });
    }
}

