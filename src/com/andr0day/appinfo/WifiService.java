package com.andr0day.appinfo;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.Toast;
import com.andr0day.appinfo.common.WifiUtil;

public class WifiService extends Service {

    private volatile boolean stop = false;
    private static final long SLEEP_TIME = 3000;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        stop = false;
        Toast.makeText(this, "wifi监控已开启", Toast.LENGTH_SHORT).show();
        final Notification notification = new Notification(R.drawable.app_icon,
                "WiFi监控开启",
                System.currentTimeMillis());
        final PendingIntent p_intent = PendingIntent.getActivity(this, 0,
                new Intent(this, AppActivity.class), 0);
        notification.setLatestEventInfo(this, "WiFi监控开启", "WiFi监控:已开启", p_intent);
        startForeground(0x1982, notification);

        new AsyncTask<Object, Object, Object>() {

            @Override
            protected Object doInBackground(Object... objects) {
                while (true) {
                    int res = new WifiUtil.ArpChecker().checkArp(WifiService.this);
                    if (res == WifiUtil.ARP_DANGER) {
                        Toast.makeText(WifiService.this, "有arp攻击，网络已断开", Toast.LENGTH_SHORT).show();
                        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                        wifi.disconnect();
                    } else if (res == WifiUtil.ARP_SAFE) {
                        notification.setLatestEventInfo(WifiService.this, "WiFi监控开启", "arp安全", p_intent);
                    }
                    try {
                        Thread.sleep(SLEEP_TIME);
                    } catch (Exception e) {

                    }
                    if (stop) {
                        break;
                    }
                }
                return null;
            }
        }.execute();

        new AsyncTask<Object, Object, Object>() {

            @Override
            protected Object doInBackground(Object... objects) {
                while (true) {
                    int res = new WifiUtil.RouteChecker().doCheck(WifiService.this);
                    if (res == WifiUtil.ROUTE_DANGER) {
                        Toast.makeText(WifiService.this, "有route攻击，网络已断开", Toast.LENGTH_SHORT).show();
                        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                        wifi.disconnect();
                    } else if (res == WifiUtil.ROUTE_SAFE) {
                        notification.setLatestEventInfo(WifiService.this, "WiFi监控开启", "route安全", p_intent);
                    }
                    try {
                        Thread.sleep(SLEEP_TIME);
                    } catch (Exception e) {

                    }
                    if (stop) {
                        break;
                    }
                }
                return null;
            }
        }.execute();
    }

    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "wifi监控已关闭", Toast.LENGTH_SHORT).show();
        stop = true;
    }

}
