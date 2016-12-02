package com.andr0day.appinfo;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by andr0day on 2015/4/29.
 */
public class TopService extends Service {
    private ComponentName last;
    private volatile boolean keep = true;

    private Handler myHandler = new Handler() {

        public void handleMessage(Message msg) {
            //            String txt = last.getPackageName() + "\n" + last.getClassName();
            //            Toast.makeText(TopService.this, txt, Toast.LENGTH_SHORT).show();

            Toast.makeText(TopService.this, "camera is free:" + (msg.obj), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        new AsyncTask<Object, Object, Object>() {

            @Override
            protected Object doInBackground(Object... objects) {
                while (true) {
                    doCheck();
                    try {
                        Thread.sleep(3000);
                    } catch (Exception e) {

                    }
                    if (!keep) {
                        break;
                    }
                }
                return null;
            }
        }.execute();

        Toast.makeText(this, "监控已开启", Toast.LENGTH_SHORT).show();

        Notification note = new Notification(R.drawable.app_icon,
                "Top监控已开启",
                System.currentTimeMillis());
        Intent i = new Intent(this, AppActivity.class);

        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

        note.setLatestEventInfo(this, "Top监控",
                "TopActivity监控: 已开启",
                pi);
        note.flags |= Notification.FLAG_NO_CLEAR;

        startForeground(1337, note);
    }

    void doCheck() {
        //        try {
        //            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        //            List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
        //            if (runningTaskInfos != null && runningTaskInfos.size() > 0) {
        //                ActivityManager.RunningTaskInfo taskInfo = runningTaskInfos.get(0);
        //                ComponentName topActivity = taskInfo.topActivity;
        //                if (last == null || !last.equals(topActivity)) {
        //                    last = topActivity;
        //                    myHandler.sendEmptyMessage(1);
        //                }
        //            }
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }
        boolean free = false;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            free = true;
        } catch (Exception e) {
            Log.e("TopService", "", e);
        } finally {
            try {
                if (mCamera != null) {
                    mCamera.release();
                }
            } catch (Exception e) {

            }
        }
        Message msg = myHandler.obtainMessage(1);
        msg.obj = free;
        myHandler.sendMessage(msg);
    }

    public void onDestroy() {
        Toast.makeText(this, "监控已关闭", Toast.LENGTH_SHORT).show();
        keep = false;
    }

}
