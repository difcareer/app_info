package com.andr0day.appinfo.common;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by andr0day on 2015/4/24.
 */
public class RootUtil {

    public static String execStr(String cmd) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            Process process = Runtime.getRuntime().exec(new String[] { "su", "-c", cmd });
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (Exception e) {
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                //ignore
            }
        }
        return sb.toString();
    }

    public static void execVoid(String cmd) {
        try {
            Runtime.getRuntime().exec(new String[] { "su", "-c", cmd });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
