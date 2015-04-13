package com.andr0day.appinfo;

import android.app.Activity;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.widget.TextView;
import android.widget.Toast;
import com.andr0day.appinfo.common.FileUtils;
import com.andr0day.appinfo.common.ProcessUtils;

import java.io.File;
import java.nio.charset.Charset;

/**
 * Created by andr0day on 2015/3/23.
 */
public class NetInfoActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.largetext);

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();

        if (wifiInfo == null || wifiInfo.getIpAddress() == 0) {
            Toast.makeText(this, "wifi not connect", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("*********************wifi info:\n\n");
        sb.append("> wifi ssid:\n    " + wifiInfo.getSSID() + "\n\n");
        sb.append("> wifi bssid:\n    " + wifiInfo.getBSSID() + "\n\n");
        sb.append("> phone ip:\n    " + Formatter.formatIpAddress(wifiInfo.getIpAddress()) + "\n\n");
        sb.append("> phone mac:\n    " + wifiInfo.getMacAddress() + "\n\n");

        sb.append("*********************gateway info:\n\n");
        sb.append("> gateway:\n    " + Formatter.formatIpAddress(dhcpInfo.gateway) + "\n\n");
        sb.append("> netmask:\n    " + Formatter.formatIpAddress(dhcpInfo.netmask) + "\n\n");
        sb.append("> dns1:\n    " + Formatter.formatIpAddress(dhcpInfo.dns1) + "\n\n");
        sb.append("> dns2:\n    " + Formatter.formatIpAddress(dhcpInfo.dns2) + "\n\n");

        FileUtils.copyAssetsToFiles(this, "ifconfig");

        File ifconfig = new File(this.getFilesDir(), "ifconfig");

        ProcessUtils.exec("chmod 777 " + ifconfig.getPath());

        sb.append("*********************arp table info:\n\n");
        String arpInfo = FileUtils.readFileToString(new File("/proc/net/arp"), Charset.defaultCharset());
        sb.append(arpInfo + "\n\n");


        sb.append("*********************route table info:\n\n");
        String routeInfo = FileUtils.readFileToString(new File("/proc/net/route"), Charset.defaultCharset());
        sb.append(routeInfo + "\n\n");


        sb.append("*********************ifconfig info:\n\n");
        String ifconfigInfo = ProcessUtils.exec("ifconfig");
        if (TextUtils.isEmpty(ifconfigInfo)) {
            ifconfigInfo = ProcessUtils.exec(ifconfig.getPath());
        }
        sb.append(ifconfigInfo + "\n\n");

        TextView textView = (TextView) findViewById(R.id.text);
        textView.setText(sb.toString());


    }
}
