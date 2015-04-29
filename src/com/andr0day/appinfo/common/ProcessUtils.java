package com.andr0day.appinfo.common;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by andr0day on 2015/3/23.
 */
public class ProcessUtils {
    private static final String TAG = "ProcessUtils";

    public static String exec(String cmd) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            Process process = Runtime.getRuntime().exec(new String[] { "sh", "-c", cmd });
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (Exception e) {
            Log.e(TAG, "error", e);
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                //ignore
            }
        }
        return sb.toString();
    }

    public static String exec(String[] cmds, String[] envs) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            Process process = Runtime.getRuntime().exec(cmds, envs);
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (Exception e) {
            Log.e(TAG, "error", e);
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                //ignore
            }
        }
        return sb.toString();
    }

    public static String newExec(String[] cmds, String classPath) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(cmds);
            processBuilder.environment().put("CLASSPATH", classPath);
            if (processBuilder.environment().containsKey("BOOTCLASSPATH")) {
                String old = processBuilder.environment().get("BOOTCLASSPATH");
                if (!TextUtils.isEmpty(old)) {
                    processBuilder.environment().put("BOOTCLASSPATH", old + ":" + classPath);
                }
            }
            Process process = processBuilder.start();
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (Exception e) {
            Log.e(TAG, "error", e);
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                //ignore
            }
        }
        return sb.toString();

    }
}
