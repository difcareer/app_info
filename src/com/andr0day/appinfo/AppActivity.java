package com.andr0day.appinfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.andr0day.appinfo.common.AppUtil;
import com.andr0day.appinfo.common.CertUtils;
import com.andr0day.appinfo.common.Constants;
import com.andr0day.appinfo.common.StringUtils;
import com.andr0day.appinfo.common.SystemPropertiesCollector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
    }
}
