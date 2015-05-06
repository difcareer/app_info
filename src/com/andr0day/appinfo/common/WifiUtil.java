package com.andr0day.appinfo.common;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.text.format.Formatter;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WifiUtil {

    public static final String CMD_ROUTE = "/proc/net/route";

    public static final String CMD_ARP = "/proc/net/arp";

    public static final String CAP_NAME = "cap";

    public static final int ARP_SAFE = 1;

    public static final int ARP_DANGER = 2;

    public static final int ARP_UNKNOWN = 3;

    public static final int ROUTE_SAFE = 4;

    public static final int ROUTE_DANGER = 5;

    public static final int ROUTE_UNKNOWN = 6;


    public static class ArpChecker {

        private static final String DEFAULT_IFACE = "wlan0";


        public int checkArp(Context context) {
            String uniq = UUID.randomUUID().toString();
            invokeCheck(context, "arm", uniq);
            return parseRes(context, uniq);
        }

        private void invokeCheck(Context context, String arch, String uniq) {
            String srcFile = WifiUtil.CAP_NAME;
            if (Build.VERSION.SDK_INT > 20) {
                srcFile = srcFile + "_5";
            }
            FileUtils.copyAssetsToFilesWithPerm(context, srcFile, CAP_NAME, arch);
            File cap = new File(context.getFilesDir(), CAP_NAME);

            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            String selfIp = WifiUtil.getSelfIp(wifiManager);
            String selfMac = WifiUtil.getSelfMac(wifiManager);
            String gatewayIp = WifiUtil.getGatewayIp(wifiManager);

            String cmd = cap.getAbsolutePath() + " " + cap.getParent() + "/" + uniq
                    + " " + getGatewayIFace(gatewayIp) + " " + selfIp
                    + " " + selfMac + " " + gatewayIp + " 5 5";
            RootUtil.execStr(cmd);
        }

        /**
         * 获取网关对应的网卡
         */
        private String getGatewayIFace(String gatewayIp) {
            String routeInfo = FileUtils.readFileToString(new File(CMD_ROUTE), Charset.defaultCharset());
            if (TextUtils.isEmpty(routeInfo)) {
                return DEFAULT_IFACE;
            }

            String[] lines = routeInfo.split("\r*\n");
            Set<String> ifaces = new HashSet<String>();
            for (String l : lines) {
                if (TextUtils.isEmpty(l) || l.toLowerCase().contains("gateway")) {
                    continue;
                }
                String[] parts = l.split("( +|\\t+)");
                String iface = parts[0];
                String gateway = parts[2];
                String reverseHexGatewayIp = WifiUtil.convertToReverseHex(gatewayIp);
                if (reverseHexGatewayIp.equalsIgnoreCase(gateway)) {
                    return iface;
                }
                ifaces.add(iface);
            }
            if (ifaces.size() == 1) {
                return (String) ifaces.toArray()[0];
            }
            return DEFAULT_IFACE;
        }

        private int parseRes(Context context, String uniq) {
            File file = new File(context.getFilesDir(), uniq + "");
            RootUtil.execStr("chmod 666 " + file.getAbsolutePath());
            String res = FileUtils.readFileToString(file, Charset.defaultCharset());
            RootUtil.execStr("rm -f " + file.getAbsolutePath());
            if (TextUtils.isEmpty(res)) {
                return ARP_UNKNOWN;
            }

            String[] lines = res.split("\r*\n");
            String tmp = "";
            for (int i = 0; i < lines.length; i++) {
                if (TextUtils.isEmpty(tmp) && !TextUtils.isEmpty(lines[i])) {
                    tmp = lines[i];
                }
                if (!TextUtils.isEmpty(tmp) && !TextUtils.isEmpty(lines[i]) && !tmp.equals(lines[i])) {
                    return ARP_DANGER;
                }
            }
            return ARP_SAFE;
        }


    }


    public static class RouteChecker {


        private String gatewayIp;
        private String netmask;


        public int doCheck(Context context) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            gatewayIp = WifiUtil.getGatewayIp(wifiManager);
            netmask = WifiUtil.getNetMask(wifiManager);

            String routeInfo = FileUtils.readFileToString(new File(WifiUtil.CMD_ROUTE), Charset.defaultCharset());
            if (TextUtils.isEmpty(routeInfo)) {
                return ROUTE_UNKNOWN;
            }

            String[] lines = routeInfo.split("\r*\n");
            for (String l : lines) {
                if (TextUtils.isEmpty(l) || l.toLowerCase().contains("gateway")) {
                    continue;
                }
                String[] parts = l.split("( +|\\t+)");
                String dst = parts[1];
                String gateway = parts[2];
                if (isAllZero(gateway)) {
                    continue;
                }

                String reverseHexGatewayIp = WifiUtil.convertToReverseHex(gatewayIp);

                if (reverseHexGatewayIp.equalsIgnoreCase(gateway)) {
                    continue;
                }

                //默认网关被修改
                if (isAllZero(dst)) {
                    return ROUTE_DANGER;
                }

                String reverseHexMask = WifiUtil.convertToReverseHex(netmask);
                long dstL = Long.parseLong(dst, 16);
                long gatewayL = Long.parseLong(gateway, 16);
                long maskL = Long.parseLong(reverseHexMask, 16);

                //判断是否在同一个子网中
                if ((dstL & maskL) == (gatewayL & maskL)) {
                    return ROUTE_DANGER;
                }
            }
            return ROUTE_SAFE;
        }

        private boolean isAllZero(String str) {
            if (!TextUtils.isEmpty(str)) {
                for (int i = 0; i < str.length(); i++) {
                    if (str.charAt(i) != '0') {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }

    }


    public static String getSelfIp(WifiManager wifiManager) {
        if (wifiManager == null) {
            return "";
        }
        String selfIp = "";
        try {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            selfIp = Formatter.formatIpAddress(wifiInfo.getIpAddress());
        } catch (Exception e) {

        }
        return selfIp;
    }

    public static String getSelfMac(WifiManager wifiManager) {
        if (wifiManager == null) {
            return "";
        }
        String selfMac = "";
        try {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            selfMac = wifiInfo.getMacAddress();
        } catch (Exception e) {

        }
        return selfMac;
    }

    public static String getGatewayIp(WifiManager wifiManager) {
        if (wifiManager == null) {
            return "";
        }
        String gatewayIp = "";
        try {
            DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
            gatewayIp = Formatter.formatIpAddress(dhcpInfo.gateway);
        } catch (Exception e) {

        }
        return gatewayIp;
    }

    public static String getNetMask(WifiManager wifiManager) {
        if (wifiManager == null) {
            return "";
        }
        String netMask = "";
        try {
            DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
            netMask = Formatter.formatIpAddress(dhcpInfo.netmask);
        } catch (Exception e) {

        }
        return netMask;
    }

    //将xxx.xxx.xxx.xxx转换为逆序16进制表示法
    public static String convertToReverseHex(String gatewayIp) {
        String[] pt = gatewayIp.split("\\.");
        String tmp = "";
        for (int i = pt.length - 1; i >= 0; i--) {
            String hex = Integer.toHexString(Integer.parseInt(pt[i]));
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            tmp = tmp + hex;
        }
        return tmp;
    }

}
