package com.andr0day.xposed;

import android.content.pm.ApplicationInfo;

import java.io.File;
import java.io.IOException;

public class ConfigUtil {

    public static final String XPOSED_DIR = "xposed";

    public static final String XPOSED_CLASSLOADER = "xposed/classloader";

    public static final String XPOSED_CLASS = "xposed/class";

    public static final String XPOSED_LOG = "xposed/log";

    public static final String XPOSED_EVENT = "xposed/event";

    public static void init(ApplicationInfo applicationInfo) {

        if (applicationInfo != null) {
            String dataDir = applicationInfo.dataDir;
            File xposedDir = new File(dataDir, XPOSED_DIR);
            if (!xposedDir.exists()) {
                xposedDir.mkdir();
            }

            createIfNotExist(dataDir, XPOSED_CLASSLOADER);
            createIfNotExist(dataDir, XPOSED_CLASS);
            createIfNotExist(dataDir, XPOSED_LOG);
            createIfNotExist(dataDir, XPOSED_EVENT);
        }
    }

    private static void createIfNotExist(String dataDir, String fileName) {
        File file = new File(dataDir, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
