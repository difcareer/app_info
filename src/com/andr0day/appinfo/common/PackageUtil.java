package com.andr0day.appinfo.common;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import org.w3c.dom.Text;

/**
 * Created by andr0day on 2015/4/10.
 */
public class PackageUtil {

    public static Boolean isAppRunning(PackageInfo packageInfo) {
        int uid = packageInfo.applicationInfo.uid;
        String psInfo = ProcessUtils.exec("ps|grep " + uid);
        if (TextUtils.isEmpty(psInfo)) {
            return null;
        }
        String[] lines = psInfo.split("\r*\n");
        for (String l : lines) {
            if (l.contains("ps|grep")) {
                continue;
            }
            String[] parts = l.split("( +|\\t+)");
            if (parts.length > 2) {
                if (parts[1].equals(uid + "")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Boolean isAppDisabled(PackageInfo packageInfo, PackageManager packageManager) {
//        packageManager.getComponentEnabledSetting()
        return null;
    }


}
