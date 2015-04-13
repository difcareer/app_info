package com.andr0day.appinfo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.andr0day.appinfo.common.AppUtil;

/**
 * Created by andr0day on 2015/4/9.
 */
public class AppDetailActivity extends Activity {
    private Button openIt;
    private Button disableIt;
    private Button enableIt;
    private String pkgName;
    private PackageManager packageManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appdetail);
        pkgName = getIntent().getStringExtra("pkgName");
        packageManager = getPackageManager();
    }

    public void onResume() {
        initView();
    }

    private void initView() {
        openIt = (Button) findViewById(R.id.open_it);
        disableIt = (Button) findViewById(R.id.disable_it);
        enableIt = (Button) findViewById(R.id.open_it);

        final Intent launcherIntent = AppUtil.getAppLauncherIntent(pkgName, packageManager);
        if (launcherIntent == null) {
            enableIt.setEnabled(false);
        } else {
            openIt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(launcherIntent);
                }
            });
        }

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        disableIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        enableIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
