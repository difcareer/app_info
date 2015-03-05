package com.andr0day.appinfo.common;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

/**
 * Created by andr0day on 2015/3/4.
 */
public class AppUtil {


    public static Drawable getAppIcon(PackageInfo packageInfo, PackageManager pm) {
        try {
            return packageInfo.applicationInfo.loadIcon(pm);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getAppName(PackageInfo packageInfo, PackageManager pm) {
        try {
            String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
            if (TextUtils.isEmpty(appName)) {
                return getDefault();
            }
            return appName;
        } catch (Exception e) {
            return getDefault();
        }
    }

    public static String getApkPath(PackageInfo packageInfo) {
        try {
            return packageInfo.applicationInfo.sourceDir;
        } catch (Exception e) {
            return getDefault();
        }
    }

    public static String getDataDir(PackageInfo packageInfo) {
        try {
            return packageInfo.applicationInfo.dataDir;
        } catch (Exception e) {
            return getDefault();
        }
    }

    public static String getPkgName(PackageInfo packageInfo) {
        try {
            String pkgName = packageInfo.packageName;
            if (TextUtils.isEmpty(pkgName)) {
                return getDefault();
            }
            return pkgName;
        } catch (Exception e) {
            return getDefault();
        }
    }

    public static int getAppFlags(PackageInfo packageInfo) {
        try {
            return packageInfo.applicationInfo.flags;
        } catch (Exception e) {
            return -1;
        }
    }

    public static boolean isSystemApp(int flags) {
        return (flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM;
    }


    private static String getDefault() {
        return "default";
    }
}
