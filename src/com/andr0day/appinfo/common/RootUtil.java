package com.andr0day.appinfo.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

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

    public static String safeExecStr(String cmd) {
        execStr2("setenforce 0");
        String res = execStr2(cmd);
        execStr2("setenforce 1");
        return res;
    }

    public static String execStr2(String cmd) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            Process process = Runtime.getRuntime().exec("su");
            bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            bw.write(cmd);
            bw.flush();
            bw.close();
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            process.waitFor();
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
