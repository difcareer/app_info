package com.andr0day.xposed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.text.TextUtils;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.andr0day.appinfo.common.ClassUtil;
import com.andr0day.appinfo.common.StringUtils;
import com.andr0day.xposed.util.ConfigUtil;
import com.andr0day.xposed.util.XposedUtil;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class XpBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String json = intent.getStringExtra(XposedUtil.JSON);
        if (TextUtils.isEmpty(json)) {
            return;
        }
////        JSON.p
//        if (XposedUtil.BROADCAST_REFLECT.equals(action)) {
//            if (XposedUtil.CMD_GET_METHODS.equals(cmd)) {
//
//            }
//
//        }
    }

    private List<String> getMethods(String className) throws RemoteException {
        Class clazz = ClassUtil.getInstance().findClass(className);
        Method[] methods = clazz.getDeclaredMethods();
        List<String> tmp = new ArrayList<String>();
        for (Method it : methods) {
            tmp.add(it.toString());
        }
        return tmp;
    }

//    private void monitorStack(String methodId) throws RemoteException {
//        ConfigUtil.writeStr(XposedMain.pkgName, "", MONITOR_DIR + "/" + STACK);
//        toast("stack done\n" + methodId);
//    }
//
//    private void monitorBefore(String methodId) throws RemoteException {
//        ConfigUtil.writeStr(XposedMain.pkgName, "", MONITOR_DIR + "/" + BEFORE);
//        toast("monitor before done\n" + methodId);
//    }
//
//    private void monitorAfter(String methodId) throws RemoteException {
//        ConfigUtil.writeStr(XposedMain.pkgName, "", MONITOR_DIR + "/" + AFTER);
//        toast("monitor after done\n" + methodId);
//    }
//
//    private void set(String methodId, String b_a, String type, String json) throws RemoteException {
//        if ("b".equals(b_a)) {
//            if (type.equals("temp")) {
//                ConfigUtil.writeStr(XposedMain.pkgName, json, MONITOR_DIR + "/" + BEFORE_TEMP_SET);
//                toast("before temp set done\n" + methodId);
//            } else if (type.equals("perm")) {
//                ConfigUtil.writeStr(XposedMain.pkgName, json, MONITOR_DIR + "/" + BEFORE_PERM_SET);
//                toast("before perm set done\n" + methodId);
//            } else if (type.equals("cond")) {
//                ConfigUtil.writeStr(XposedMain.pkgName, json, MONITOR_DIR + "/" + BEFORE_COND_SET);
//                toast("before cond set done\n" + methodId);
//            }
//        } else {
//            if (type.equals("temp")) {
//                ConfigUtil.writeStr(XposedMain.pkgName, json, MONITOR_DIR + "/" + AFTER_TEMP_SET);
//                toast("after temp set done\n" + methodId);
//            } else if (type.equals("perm")) {
//                ConfigUtil.writeStr(XposedMain.pkgName, json, MONITOR_DIR + "/" + AFTER_PERM_SET);
//                toast("after perm set done\n" + methodId);
//            } else if (type.equals("cond")) {
//                ConfigUtil.writeStr(XposedMain.pkgName, json, MONITOR_DIR + "/" + AFTER_COND_SET);
//                toast("after cond set done\n" + methodId);
//            }
//        }
//    }
//
//    private void toast(String txt) {
//        Toast.makeText(XposedMain.application, txt, Toast.LENGTH_SHORT).show();
//    }
}
