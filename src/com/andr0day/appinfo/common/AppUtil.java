package com.andr0day.appinfo.common;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import java.util.List;

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

    public static List<InputMethodInfo> getInputs(Context context) {
        return ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
                .getInputMethodList();
    }

    public static List<ResolveInfo> getHomes(Context context) {
        Intent intent = new Intent("android.intent.action.MAIN", null);
        intent.addCategory("android.intent.category.HOME");
        return context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
    }

    public static boolean isHome(PackageInfo packageInfo, List<ResolveInfo> homes) {
        for (ResolveInfo it : homes) {
            if (packageInfo.packageName.equals(it.activityInfo.packageName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInput(PackageInfo packageInfo, List<InputMethodInfo> inputs) {
        for (InputMethodInfo it : inputs) {
            if (packageInfo.packageName.equals(it.getPackageName())) {
                return true;
            }
        }
        return false;
    }


    public static boolean isSystemApp(PackageInfo packageInfo) {
        return (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

    public static boolean isSystemUpdateApp(PackageInfo packageInfo) {
        return (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
    }

    public static boolean isDebugable(PackageInfo packageInfo) {
        return (packageInfo.applicationInfo.uid != 1000 && (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0 || !"user".equals(Build.TYPE));
    }

    public static Intent getAppLauncherIntent(String pkgName, PackageManager pm) {
        try {
            return pm.getLaunchIntentForPackage(pkgName);
        } catch (Exception e) {
            return null;
        }
    }


    private static String getDefault() {
        return "default";
    }
}
