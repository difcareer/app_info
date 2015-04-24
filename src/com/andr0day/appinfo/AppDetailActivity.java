package com.andr0day.appinfo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.andr0day.appinfo.common.AppUtil;
import com.andr0day.appinfo.common.FileUtils;
import com.andr0day.appinfo.common.ProcessUtils;

import java.io.File;

/**
 * Created by andr0day on 2015/4/9.
 */
public class AppDetailActivity extends Activity {
    private Button openIt;
    private Button disableIt;
    private Button enableIt;
    private String pkgName;
    private PackageManager packageManager;
    private static final String JAR_FILE = "iso.jar";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appdetail);
        pkgName = getIntent().getStringExtra("pkgName");
        packageManager = getPackageManager();
    }

    public void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {
        openIt = (Button) findViewById(R.id.open_it);
        disableIt = (Button) findViewById(R.id.disable_it);
        enableIt = (Button) findViewById(R.id.enable_it);

        final File jarFile = new File(getFilesDir(), JAR_FILE);
        if (!jarFile.exists()) {
            FileUtils.copyAssetsToFiles(this, JAR_FILE);
        }


        final Intent launcherIntent = AppUtil.getAppLauncherIntent(pkgName, packageManager);
        if (launcherIntent == null) {
            openIt.setEnabled(false);
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
                String cmd = "forceStopPackage " + pkgName + " 0";
                JarUtil.execJar(cmd, jarFile.getAbsolutePath());
            }
        });

        enableIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    static class JarUtil {

        private static final String APP_PROCESS = "/system/bin/app_process";
        private static final String MAIN_CLASS = "com.qihoo360.androidtool.ManagerUtil";

        static String execJar(String cmd, String classPath) {
            String[] commands = new String[3];
            commands[0] = "/system/bin/su";
            commands[1] = "-c";
            commands[2] = APP_PROCESS + " /system/bin/ " + MAIN_CLASS + " " + cmd;
            return ProcessUtils.newExec(commands, classPath);
        }


    }
}
