package com.andr0day.appinfo;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by andr0day on 2015/4/8.
 */
public class TestService extends Service {
    private static final String TAG = "TestService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        new TestTask().execute();
        stopSelf();
        return 0;
    }

    class TestTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(10000);
                Log.e(TAG, "still alive");
            } catch (Exception e) {

            }
            return null;
        }
    }
}
