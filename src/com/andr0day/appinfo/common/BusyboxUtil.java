package com.andr0day.appinfo.common;

import java.io.File;

/**
 * Created by andr0day on 2015/4/24.
 */
public class BusyboxUtil {

    public static String getBusyBox() {
        return "busybox-armv7l";
    }

    public static boolean isInstalled() {
        File file = new File("/system/xbin/whoami");
        return file.exists();
    }
}
