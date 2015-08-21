package com.andr0day.appinfo.common;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import com.andr0day.appinfo.R;

/**
 * Created by andr0day on 2015/7/23.
 * 统一管理notify
 */
public class NotificationUtil {

    private NotificationManager notificationManager;

    private Notification notification;

    private int notifyId = 9413;

    private static NotificationUtil instance;

    public static NotificationUtil getInstance(Service service) {
        if (instance == null) {
            instance = new NotificationUtil(service);
        }
        return instance;
    }

    private NotificationUtil(Service service) {
        notificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
        notification = new Notification(R.drawable.app_icon, "后台运行中", System.currentTimeMillis());
    }

    public void sendNotify(Service service, Intent intent, String title, String content) {
        PendingIntent pendingIntent = PendingIntent.getActivity(service, 0, intent, 0);
        notification.setLatestEventInfo(service, title, content, pendingIntent);
        service.startForeground(notifyId, notification);
        notificationManager.notify(notifyId, notification);
    }

}
