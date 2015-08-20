package com.andr0day.xposed.util;

import android.util.Log;
import com.andr0day.xposed.XposedMain;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

public class XposedLogUtil {

    public static PrintWriter eventWriter;
    public static PrintWriter logWriter;
    public static File eventFile;
    public static File logFile;

    private static final int MAX_LOGFILE_SIZE = 1024 * 1024;

    private static final long CHECK_TIME = 60 * 1000;

    @SuppressWarnings("all")
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");

    @SuppressWarnings("all")
    public static void init(String pkgName) {
        try {
            if (eventWriter == null) {
                eventFile = new File(XposedUtil.XPOSED_FULL_PATH + "/" + pkgName + "/" + XposedUtil.EVENT);
                eventWriter = new PrintWriter(new FileWriter(eventFile, true));
                eventFile.setReadable(true, false);
                eventFile.setWritable(true, false);
                event("log init ...");

                logFile = new File(XposedUtil.XPOSED_FULL_PATH + "/" + pkgName + "/" + XposedUtil.LOG);
                logWriter = new PrintWriter(new FileWriter(logFile, true));
                logFile.setReadable(true, false);
                logFile.setWritable(true, false);
                log("log init ...");
            }
        } catch (Exception e) {
            Log.e(XposedUtil.TAG, "init", e);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (eventFile == null) {
                            break;
                        }
                        checkFileSize(eventFile);
                        Thread.sleep(CHECK_TIME);
                    } catch (Exception e) {

                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (logFile == null) {
                            break;
                        }
                        checkFileSize(logFile);
                        Thread.sleep(CHECK_TIME);
                    } catch (Exception e) {

                    }
                }
            }
        }).start();
    }

    public static void log(String msg) {
        String dateStr = simpleDateFormat.format(System.currentTimeMillis());
        logWriter.println(dateStr + " " + msg);
        logWriter.flush();
    }

    public static void event(String msg) {
        String dateStr = simpleDateFormat.format(System.currentTimeMillis());
        eventWriter.println(dateStr + " " + msg);
        eventWriter.flush();
    }

    /**
     * 打印堆栈
     */
    public static void printStack() {
        new Throwable().printStackTrace(eventWriter);
    }

    @SuppressWarnings("all")
    private static void checkFileSize(File logFile) {
        if (logFile.exists() && logFile.length() > MAX_LOGFILE_SIZE) {
            logFile.renameTo(
                    new File(XposedMain.applicationInfo.dataDir, logFile.getName() + "_" + System.currentTimeMillis()));
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                Log.e(XposedUtil.TAG, "checkFileSize", e);
            }
        }
    }
}
