package com.andr0day.appinfo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.andr0day.appinfo.common.AppUtil;
import com.andr0day.appinfo.common.DbHelper;
import com.andr0day.appinfo.common.FileUtils;
import com.andr0day.appinfo.common.RootUtil;

import java.io.File;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * Created by andr0day on 2015/4/9.
 */
public class AppDetailActivity extends Activity {
    private static final String APP_PROCESS = "/system/bin/app_process";
    private static final String MAIN_CLASS = "com.qihoo360.androidtool.ManagerUtil";

    private Button openIt;
    private Button disableIt;
    private Button enableIt;
    private Button exposedComp;
    private Button fullSig;

    private String pkgName;
    private PackageManager packageManager;
    private static final String JAR_FILE = "iso.jar";
    private File jarFile;

    private static final int INIT_VIEW = 1;

    private Handler myHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INIT_VIEW:
                    initView();
                    break;
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appdetail);
        pkgName = getIntent().getStringExtra("pkgName");
        packageManager = getPackageManager();

        jarFile = new File(getFilesDir(), JAR_FILE);
        if (!jarFile.exists()) {
            FileUtils.copyAssetsToFiles(this, JAR_FILE);
        }
        myHandler.sendEmptyMessage(INIT_VIEW);

    }

    public void onResume() {
        super.onResume();

    }

    private void initView() {
        openIt = (Button) findViewById(R.id.open_it);
        disableIt = (Button) findViewById(R.id.disable_it);
        enableIt = (Button) findViewById(R.id.enable_it);
        exposedComp = (Button) findViewById(R.id.exposed_comp);
        fullSig = (Button) findViewById(R.id.full_sig);

        final Intent launcherIntent = AppUtil.getAppLauncherIntent(pkgName, packageManager);
        String launcherCls = "null";
        if (launcherIntent != null) {
            launcherCls = launcherIntent.getComponent().getClassName();
        }
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

//        if (isIsolated(pkgName, launcherCls, true)) {
//            disableIt.setEnabled(false);
//            enableIt.setEnabled(true);
//        } else {
//            disableIt.setEnabled(true);
//            enableIt.setEnabled(false);
//        }

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        disableIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String launcherCls = "null";
                Intent launcherIntent = packageManager.getLaunchIntentForPackage(pkgName);
                if (launcherIntent != null) {
                    launcherCls = launcherIntent.getComponent().getClassName();
                }
                isolate(pkgName, launcherCls, true);
                if (isIsolated(pkgName, launcherCls, true)) {
                    Toast.makeText(AppDetailActivity.this, "禁用成功", Toast.LENGTH_SHORT).show();
                    disableIt.setEnabled(false);
                    enableIt.setEnabled(true);
                    openIt.setEnabled(false);
                    DbHelper.getInstance(AppDetailActivity.this).insertIsolate(pkgName);
                }
            }
        });

        enableIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String launcherCls = "null";
                Intent launcherIntent = packageManager.getLaunchIntentForPackage(pkgName);
                if (launcherIntent != null) {
                    launcherCls = launcherIntent.getComponent().getClassName();
                }
                recovery(pkgName, launcherCls, true);
                if (!isIsolated(pkgName, launcherCls, true)) {
                    Toast.makeText(AppDetailActivity.this, "恢复成功", Toast.LENGTH_SHORT).show();
                    enableIt.setEnabled(false);
                    disableIt.setEnabled(true);
                    if (launcherIntent != null) {
                        openIt.setEnabled(true);
                    }
                    DbHelper.getInstance(AppDetailActivity.this).deleteIsolate(pkgName);
                }

            }
        });

        exposedComp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(AppDetailActivity.this, ExportedActivity.class);
                intent.putExtra("pkgName", pkgName);
                startActivity(intent);
            }
        });

        fullSig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PackageManager pm = getPackageManager();
                    PackageInfo pi = pm.getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);
                    Signature[] signatures = pi.signatures;
                    for (int i = 0; i < signatures.length; i++) {
                        Signature sig = signatures[i];
                        byte[] bt = sig.toByteArray();
                        String str = new String(Base64.encode(bt, Base64.DEFAULT), "iso-8859-1");
                        Log.e("FullSig", pkgName + "\n" + str+"@@@");
                    }
                } catch (Exception e) {

                }
            }
        });

    }

    private boolean isIsolated(String pkgName, String launcherCls, boolean fully) {
        String uniq = UUID.randomUUID().toString();
        String filePath = new File(AppDetailActivity.this.getFilesDir(), uniq).getAbsolutePath();
        RootUtil.safeExecStr("export CLASSPATH=" + jarFile.getAbsolutePath() + "; "
                + APP_PROCESS + " /system/bin/ " + MAIN_CLASS + " isIsolated " + pkgName + " " + launcherCls + " " + fully + " " + filePath);
        int i = 0;
        while (i < 3) {
            if (new File(filePath).exists()) {
                RootUtil.safeExecStr("chmod 777 " + filePath);
                String res = FileUtils.readFileToString(new File(filePath), Charset.defaultCharset());
                RootUtil.safeExecStr("rm -f " + filePath);
                return "1".equals(res);
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {

            }
            i++;
        }
        return false;
    }

    private void isolate(String pkgName, String launcherCls, boolean fully) {
        RootUtil.safeExecStr("export CLASSPATH=" + jarFile.getAbsolutePath() + ";"
                + APP_PROCESS + " /system/bin/ " + MAIN_CLASS + " fastIsolate " + pkgName + " " + launcherCls);
        if (fully) {
            RootUtil.safeExecStr("export CLASSPATH=" + jarFile.getAbsolutePath() + ";"
                    + APP_PROCESS + " /system/bin/ " + MAIN_CLASS + " fullyIsolate " + pkgName);
        }
    }

    private void recovery(String pkgName, String launcherCls, boolean fully) {
        RootUtil.safeExecStr("export CLASSPATH=" + jarFile.getAbsolutePath() + ";"
                + APP_PROCESS + " /system/bin/ " + MAIN_CLASS + " fastRecovery " + pkgName + " " + launcherCls);
        if (fully) {
            RootUtil.safeExecStr("export CLASSPATH=" + jarFile.getAbsolutePath() + ";"
                    + APP_PROCESS + " /system/bin/ " + MAIN_CLASS + " fullyRecovery " + pkgName);
        }
    }
}
