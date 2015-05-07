package com.andr0day.appinfo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;
import com.andr0day.appinfo.common.WifiUtil;

public class WifiService extends Service {

    private volatile boolean stop = false;
    private static final long SLEEP_TIME = 3000;

    private static final int SHOW_ARP_DANGER = 1;

    private static final int SHOW_ROUTE_DANGER = 2;

    private static final int SHOW_ARP_SAFE = 3;

    private static final int SHOW_ROUTE_SAFE = 4;

    private NotificationManager notificationManager;

    private Notification notification;

    private PendingIntent pendingIntent;

    private int notifyId = 9413;

    private Handler myHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_ARP_DANGER:
                    Toast.makeText(WifiService.this, "当前存在arp攻击", Toast.LENGTH_SHORT).show();
                    notification.setLatestEventInfo(WifiService.this, "WiFi监控开启", "当前存在arp攻击", pendingIntent);
                    notificationManager.notify(notifyId, notification);
                    break;
                case SHOW_ROUTE_DANGER:
                    Toast.makeText(WifiService.this, "当前存在route攻击", Toast.LENGTH_SHORT).show();
                    notification.setLatestEventInfo(WifiService.this, "WiFi监控开启", "当前存在route攻击", pendingIntent);
                    notificationManager.notify(notifyId, notification);
                    break;
                case SHOW_ARP_SAFE:
                    notification.setLatestEventInfo(WifiService.this, "WiFi监控开启", "arp安全", pendingIntent);
                    notificationManager.notify(notifyId, notification);
                    break;

                case SHOW_ROUTE_SAFE:
                    notification.setLatestEventInfo(WifiService.this, "WiFi监控开启", "route安全", pendingIntent);
                    notificationManager.notify(notifyId, notification);
                    break;
            }

        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        stop = false;
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification = new Notification(R.drawable.app_icon, "WiFi监控开启", System.currentTimeMillis());
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, AppActivity.class), 0);
        Toast.makeText(this, "wifi监控已开启", Toast.LENGTH_SHORT).show();
        notification.setLatestEventInfo(this, "WiFi监控开启", "WiFi监控:已开启", pendingIntent);
        startForeground(notifyId, notification);

        new AsyncTask<Object, Object, Object>() {

            @Override
            protected Object doInBackground(Object... objects) {
                while (true) {

                    int res = new WifiUtil.ArpChecker().checkArp(WifiService.this);
                    if (res == WifiUtil.ARP_DANGER) {
                        myHandler.sendEmptyMessage(SHOW_ARP_DANGER);
                        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                        wifi.disconnect();
                    } else if (res == WifiUtil.ARP_SAFE) {
                        myHandler.sendEmptyMessage(SHOW_ARP_SAFE);
                        res = new WifiUtil.RouteChecker().doCheck(WifiService.this);
                        if (res == WifiUtil.ROUTE_DANGER) {
                            myHandler.sendEmptyMessage(SHOW_ROUTE_DANGER);
                            WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                            wifi.disconnect();
                        } else if (res == WifiUtil.ROUTE_SAFE) {
                            myHandler.sendEmptyMessage(SHOW_ROUTE_SAFE);
                        }
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
