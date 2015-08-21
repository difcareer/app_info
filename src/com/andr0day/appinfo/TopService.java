package com.andr0day.appinfo;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;
import com.andr0day.appinfo.common.NotificationUtil;

import java.util.List;

/**
 * Created by andr0day on 2015/4/29.
 */
public class TopService extends Service {
    private ComponentName last;
    private volatile boolean keep = true;

    private Handler myHandler = new Handler() {

        public void handleMessage(Message msg) {
            String txt = last.getPackageName() + "\n" + last.getClassName();
            Toast.makeText(TopService.this, txt, Toast.LENGTH_SHORT).show();
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
                        Thread.sleep(100);
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
        NotificationUtil.getInstance(this).sendNotify(this, new Intent(this, AppActivity.class), "Top监控", "TopActivity监控: 已开启");
    }


    void doCheck() {
        try {
            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
            if (runningTaskInfos != null && runningTaskInfos.size() > 0) {
                ActivityManager.RunningTaskInfo taskInfo = runningTaskInfos.get(0);
                ComponentName topActivity = taskInfo.topActivity;
                if (last == null || !last.equals(topActivity)) {
                    last = topActivity;
                    myHandler.sendEmptyMessage(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        Toast.makeText(this, "监控已关闭", Toast.LENGTH_SHORT).show();
        keep = false;
    }

}
