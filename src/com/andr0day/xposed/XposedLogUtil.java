package com.andr0day.xposed;

import android.os.AsyncTask;
import android.util.Log;

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

    public static void init() {
        try {
            if (XposedMain.applicationInfo != null && eventWriter == null) {
                eventFile = new File(XposedMain.applicationInfo.dataDir, ConfigUtil.XPOSED_EVENT);
                eventWriter = new PrintWriter(new FileWriter(eventFile, true));
                eventFile.setReadable(true, false);
                eventFile.setWritable(true, false);

                logFile = new File(XposedMain.applicationInfo.dataDir, ConfigUtil.XPOSED_LOG);
                logWriter = new PrintWriter(new FileWriter(logFile, true));
                logFile.setReadable(true, false);
                logFile.setWritable(true, false);
            }
        } catch (Exception e) {
            Log.e("XposedLogUtil", "XposedLogUtil init error", e);
        }

        new AsyncTask<Object, Object, Object>() {

            @Override
            protected Object doInBackground(Object... objects) {
                while (true) {
                    try {
                        if (eventFile == null) {
                            break;
                        }
                        checkFileSize(eventFile);
                        Thread.sleep(30 * 1000);
                    } catch (Exception e) {

                    }
                }
                return null;
            }
        }.execute();

        new AsyncTask<Object, Object, Object>() {

            @Override
            protected Object doInBackground(Object... objects) {
                while (true) {
                    try {
                        if (logFile == null) {
                            break;
                        }
                        checkFileSize(logFile);
                        Thread.sleep(30 * 1000);
                    } catch (Exception e) {

                    }
                }
                return null;
            }
        }.execute();
    }

    public static void log(String msg) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
        String dateStr = simpleDateFormat.format(System.currentTimeMillis());
        logWriter.println(dateStr + " " + msg);
        logWriter.flush();
    }

    public static void event(String msg) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
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


    private static void checkFileSize(File logFile) {
        if (logFile.exists() && logFile.length() > MAX_LOGFILE_SIZE) {
            logFile.renameTo(new File(XposedMain.applicationInfo.dataDir, logFile.getName() + "_" + System.currentTimeMillis()));
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
