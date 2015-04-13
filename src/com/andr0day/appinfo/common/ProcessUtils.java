package com.andr0day.appinfo.common;

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
            Process process = Runtime.getRuntime().exec(cmd);
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
