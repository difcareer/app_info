package com.andr0day.xposed;

import android.content.pm.ApplicationInfo;
import android.util.Log;
import com.andr0day.appinfo.common.AppUtil;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedMain implements IXposedHookLoadPackage {

    public static ApplicationInfo applicationInfo;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam == null) {
            return;
        }
        applicationInfo = lpparam.appInfo;
        if (applicationInfo == null) {
            return;
        }
        if (AppUtil.isSystemApp(applicationInfo)) {
            return;
        }
        if (AppUtil.isSystemUpdateApp(applicationInfo)) {
            return;
        }

        if (!applicationInfo.packageName.equals("com.andr0day.appinfo")) {
            return;
        }

        if (lpparam.isFirstApplication) {
            ConfigUtil.init(applicationInfo);
            XposedLogUtil.init();

            ClassloaderHooker.startHook(lpparam.classLoader);

        }

    }
}
