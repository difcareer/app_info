package com.andr0day.appinfo;

import android.app.Application;
import android.content.Intent;
import com.andr0day.appinfo.common.ClassUtil;
import com.andr0day.appinfo.common.FileUtils;
import com.andr0day.appinfo.common.RootUtil;

import java.io.File;

/**
 * Created by andr0day
 * on 2015/7/22.
 */
public class AppApplication extends Application {

    private static final String SO = "libloaded-class.so";

    public static final String SO_FULL_PATH = "/data/data/com.andr0day.appinfo/files/libloaded-class.so";

    public void onCreate() {
        super.onCreate();
        FileUtils.copyAssetsToFiles(this, SO);
        RootUtil.safeExecStr("chmod 777 " + SO_FULL_PATH);
        startService(new Intent(this, FileModifyService.class));
//        ClassUtil.getInstance().loadClasses();
    }
}
