package com.andr0day.xposed.util;

import android.util.Log;
import com.andr0day.appinfo.common.RootUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigUtil {

    public static void init(String pkgName) {
        String pkgDir = createSelfDir(pkgName);
        String logPath = pkgDir + "/" + XposedUtil.LOG;
        String eventPath = pkgDir + "/" + XposedUtil.EVENT;
        createIfNotExist(logPath);
        RootUtil.safeExecStr("chmod 777 " + logPath);
        createIfNotExist(eventPath);
        RootUtil.safeExecStr("chmod 777 " + eventPath);
    }

    @SuppressWarnings("all")
    private static void createIfNotExist(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.e(XposedUtil.TAG, "createIfNotExist", e);
            }
        }
    }

    @SuppressWarnings("all")
    public static void setConfig(String pkgName, String k, String v) {
        String pkgDir = createSelfDir(pkgName);
        File injectConfig = new File(pkgDir, XposedUtil.INJECT_PKG_CONFIG);
        if (!injectConfig.exists()) {
            try {
                injectConfig.createNewFile();
            } catch (Exception e) {
                Log.e(XposedUtil.TAG, "createNewFile", e);
            }
        }
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(injectConfig));
            properties.put(k, v);
            properties.store(new FileOutputStream(injectConfig), "");
            RootUtil.safeExecStr("chmod 777 " + injectConfig.getAbsolutePath());
        } catch (IOException e) {
            Log.e(XposedUtil.TAG, "setConfig", e);
        }
    }

    public static String getConfigNormal(String pkgName, String k, String def) {
        File pkgDir = new File(XposedUtil.XPOSED_FULL_PATH + "/" + pkgName);
        if (pkgDir.exists()) {
            File injectConfig = new File(pkgDir, XposedUtil.INJECT_PKG_CONFIG);
            if (injectConfig.exists()) {
                Properties properties = new Properties();
                try {
                    properties.load(new FileInputStream(injectConfig));
                    return (String) properties.get(k);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return def;
    }

    @SuppressWarnings("all")
    public static String getConfig(String pkgName, String k, String def) {
        String pkgDir = createSelfDir(pkgName);
        File injectConfig = new File(pkgDir, XposedUtil.INJECT_PKG_CONFIG);
        if (!injectConfig.exists()) {
            try {
                injectConfig.createNewFile();
            } catch (Exception e) {
                //ignore
            }
        }
        RootUtil.safeExecStr("chmod 777 " + injectConfig.getAbsolutePath());
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(injectConfig));
            return (String) properties.get(k);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return def;
    }

    @SuppressWarnings("all")
    private static String createSelfDir(String pkgName) {
        File xposedDir = new File(XposedUtil.XPOSED_FULL_PATH);
        if (!xposedDir.exists()) {
            xposedDir.mkdir();
        }
        RootUtil.safeExecStr("chmod 777 " + XposedUtil.XPOSED_FULL_PATH);
        File pkgDir = new File(xposedDir, pkgName);
        if (!pkgDir.exists()) {
            pkgDir.mkdir();
        }
        RootUtil.safeExecStr("chmod 777 " + pkgDir.getAbsolutePath());
        return pkgDir.getAbsolutePath();
    }

    public static void writeStr(String pkgName, String txt, String fileName) {
        String pkgDir = createSelfDir(pkgName);
        File file = new File(pkgDir, fileName);
        try {
            org.apache.commons.io.FileUtils.writeStringToFile(file, txt);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readStr(String pkgName, String fileName) {
        String pkgDir = createSelfDir(pkgName);
        File file = new File(pkgDir, fileName);
        try {
            return FileUtils.readFileToString(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
