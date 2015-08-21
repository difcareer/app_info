package com.andr0day.common;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;
import com.andr0day.appinfo.common.ClassUtil;
import com.andr0day.xposed.util.ConfigUtil;
import com.andr0day.xposed.XposedMain;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangwanchun
 * @date 2015/8/5 15:12
 * @description
 */
public class XpReflectService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IXpReflect.Stub mBinder = new IXpReflect.Stub() {

        private static final String MONITOR_DIR = "monitor";

        private static final String STACK = "stack";

        private static final String BEFORE = "before";
        private static final String AFTER = "after";

        private static final String BEFORE_TEMP_SET = "before_temp_set";
        private static final String BEFORE_PERM_SET = "before_perm_set";
        private static final String BEFORE_COND_SET = "before_cond_set";
        private static final String AFTER_TEMP_SET = "after_temp_set";
        private static final String AFTER_PERM_SET = "after_perm_set";
        private static final String AFTER_COND_SET = "after_cond_set";

        @Override
        public List<String> getMethods(String className) throws RemoteException {
            Class clazz = ClassUtil.getInstance().findClass(className);
            Method[] methods = clazz.getDeclaredMethods();
            List<String> tmp = new ArrayList<String>();
            for (Method it : methods) {
                tmp.add(it.toString());
            }
            return tmp;
        }

        @Override
        public void monitorStack(String methodId) throws RemoteException {
            ConfigUtil.writeStr(XposedMain.pkgName, "", MONITOR_DIR + "/" + STACK);
            toast("stack done\n" + methodId);
        }

        @Override
        public void monitorBefore(String methodId) throws RemoteException {
            ConfigUtil.writeStr(XposedMain.pkgName, "", MONITOR_DIR + "/" + BEFORE);
            toast("monitor before done\n" + methodId);
        }

        @Override
        public void monitorAfter(String methodId) throws RemoteException {
            ConfigUtil.writeStr(XposedMain.pkgName, "", MONITOR_DIR + "/" + AFTER);
            toast("monitor after done\n" + methodId);
        }

        @Override
        public void set(String methodId, String b_a, String type, String json) throws RemoteException {
            if ("b".equals(b_a)) {
                if (type.equals("temp")) {
                    ConfigUtil.writeStr(XposedMain.pkgName, json, MONITOR_DIR + "/" + BEFORE_TEMP_SET);
                    toast("before temp set done\n" + methodId);
                } else if (type.equals("perm")) {
                    ConfigUtil.writeStr(XposedMain.pkgName, json, MONITOR_DIR + "/" + BEFORE_PERM_SET);
                    toast("before perm set done\n" + methodId);
                } else if (type.equals("cond")) {
                    ConfigUtil.writeStr(XposedMain.pkgName, json, MONITOR_DIR + "/" + BEFORE_COND_SET);
                    toast("before cond set done\n" + methodId);
                }
            } else {
                if (type.equals("temp")) {
                    ConfigUtil.writeStr(XposedMain.pkgName, json, MONITOR_DIR + "/" + AFTER_TEMP_SET);
                    toast("after temp set done\n" + methodId);
                } else if (type.equals("perm")) {
                    ConfigUtil.writeStr(XposedMain.pkgName, json, MONITOR_DIR + "/" + AFTER_PERM_SET);
                    toast("after perm set done\n" + methodId);
                } else if (type.equals("cond")) {
                    ConfigUtil.writeStr(XposedMain.pkgName, json, MONITOR_DIR + "/" + AFTER_COND_SET);
                    toast("after cond set done\n" + methodId);
                }
            }
        }

        private void toast(String txt) {
            Toast.makeText(XposedMain.applicationContext, txt, Toast.LENGTH_SHORT).show();
        }
    };
}
