package com.andr0day.xposed;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.text.TextUtils;
import android.util.Log;
import com.andr0day.appinfo.common.ClassUtil;
import com.andr0day.xposed.hooker.ContextHooker;
import com.andr0day.xposed.hooker.Hookers;
import com.andr0day.xposed.svc.XpServiceImpl;
import com.andr0day.xposed.util.ConfigUtil;
import com.andr0day.xposed.util.HookUtil;
import com.andr0day.xposed.util.XposedLogUtil;
import com.andr0day.xposed.util.XposedUtil;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import org.w3c.dom.Text;

import java.lang.reflect.Method;
import java.util.Arrays;

public class XposedMain implements IXposedHookLoadPackage {

    public static ApplicationInfo applicationInfo;
    public static String pkgName;
    public static String applicationName;
    private static long lastLoadTime = System.currentTimeMillis();

    public static Context applicationContext;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam == null) {
            return;
        }
        applicationInfo = lpparam.appInfo;
        if (applicationInfo == null) {
            return;
        }
        pkgName = applicationInfo.packageName;
        applicationName = applicationInfo.className;
        if (TextUtils.isEmpty(pkgName)) {
            return;
        }
        new Thread(new Runnable() {
            @Override public void run() {
                boolean xposedEnabled = "true".equals(ConfigUtil.getConfigNormal(pkgName, "enable", "false"));
                if (!xposedEnabled) {
                    return;
                }
                Log.e(XposedUtil.TAG, "active pkgName:" + pkgName);
                Log.e(XposedUtil.TAG, "active applicationName:" + applicationName);
                if (lpparam.isFirstApplication) {
                    initEnv();
                    registerService(pkgName);
                    startLoopLoad();
                    Hookers.instance.startHook();
                }
            }
        }).start();
    }

    private void initEnv() {
        ConfigUtil.init(pkgName);
        XposedLogUtil.init(pkgName);
    }

    private void startLoopLoad() {
        long current = System.currentTimeMillis();
        while (true) {
            if (current - lastLoadTime >= 10 * 1000) {
                lastLoadTime = current;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ClassUtil.getInstance().loadClasses();
//                        if(ContextHooker.instance.needHook()){
//                            ContextHooker.instance.startHook();
//                        }
                        StringBuilder sb = new StringBuilder();
                        for (Class it : ClassUtil.getInstance().classes) {
                            sb.append(it.getCanonicalName()).append("\n");
                        }
                        ConfigUtil.writeStr(pkgName, sb.toString(), XposedUtil.CLASS);
                        sb = new StringBuilder();
                        for (ClassLoader cl : ClassUtil.getInstance().classLoaders) {
                            sb.append(cl.getClass().getCanonicalName()).append("\n");
                        }
                        ConfigUtil.writeStr(pkgName, sb.toString(), XposedUtil.CLASS_LOADER);
                    }
                }).start();
            }
        }
    }

    private void registerBroadCast(String pkgName) {
        applicationContext.registerReceiver(new XpBroadcastReceiver(),
                new IntentFilter(XposedUtil.XPOSED_BROADCAST_PREFIX + pkgName));
        Log.e(XposedUtil.TAG, "broadcast registered.");
    }

    private void registerService(String pkgName) {
        //        ServiceManager.addService(XposedUtil.XPOSED_SERVICE_PREFIX + pkgName, new XpServiceImpl());
        //        Log.e(XposedUtil.TAG, "service registered.");
        try {
            Log.e(XposedUtil.TAG, TextUtils.join("\n", ServiceManager.listServices()));
        } catch (RemoteException e) {
            Log.e(XposedUtil.TAG, "listServices", e);
        }
    }
}
