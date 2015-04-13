package com.andr0day.appinfo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class AppActivity extends Activity {

    private static final int MSG_LOAD_START = 1;

    private static final int MSG_LOAD_FINISH = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button systemInfoB = (Button) findViewById(R.id.systemInfo);
        Button buildInfoB = (Button) findViewById(R.id.buildInfo);
        Button appInfoB = (Button) findViewById(R.id.appInfo);
        Button mountInfoB = (Button) findViewById(R.id.mountInfo);
        Button netInfoB = (Button) findViewById(R.id.netInfo);


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
    }
}
