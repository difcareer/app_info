package com.andr0day.appinfo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by andr0day on 2015/7/23.
 */
public class FileModifyService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
