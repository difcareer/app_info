package com.andr0day.common;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import com.andr0day.appinfo.common.ClassUtil;

import java.util.ArrayList;
import java.util.List;

public class XpClassService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IXpClass.Stub mBinder = new IXpClass.Stub() {

        @Override
        public List<String> getClasses() throws RemoteException {
            List<String> tmp = new ArrayList<String>();
            for (Class it : ClassUtil.getInstance().classes) {
                tmp.add(it.getCanonicalName());
            }
            return tmp;
        }
    };

}
