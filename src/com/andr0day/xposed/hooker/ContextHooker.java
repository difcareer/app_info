package com.andr0day.xposed.hooker;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.andr0day.appinfo.common.ClassUtil;
import com.andr0day.xposed.util.HookUtil;
import com.andr0day.xposed.util.XposedUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

import java.lang.reflect.Method;
import java.util.logging.Level;

/**
 * @author wangwanchun
 * @date 2015/8/11 18:45
 * @description
 */
public class ContextHooker extends HookerAdaptor {
    private static final String ContextImpl_CLASS_NAME = "android.app.ContextImpl";

    public static ContextHooker instance = new ContextHooker();

    private ContextHooker() {

    }

    public volatile Context applicationContext;

    public volatile boolean hooked = false;

    @Override
    public void startHook() {
        Class contextImplClass;
        try {
            contextImplClass = Class.forName(ContextImpl_CLASS_NAME);
            Log.e(XposedUtil.TAG,"class.forName");
        } catch (ClassNotFoundException e) {
            contextImplClass = ClassUtil.getInstance().findClass(ContextImpl_CLASS_NAME);
            Log.e(XposedUtil.TAG,"ClassUtil.findClass");
        }
        if (contextImplClass != null) {
            Log.e(XposedUtil.TAG,"got ContextImpl");
            Method[] methods = contextImplClass.getDeclaredMethods();
            for (final Method it : methods) {
                Class<?>[] paramTypes = it.getParameterTypes();
                Object[] paramsAndHook = new Object[paramTypes.length + 1];
                System.arraycopy(paramTypes, 0, paramsAndHook, 0, paramTypes.length);
                paramsAndHook[paramsAndHook.length - 1] = new XC_MethodHook() {
                    protected void afterHookedMethod(MethodHookParam param) {
                        if (applicationContext == null) {
                            Context context = (Context) param.thisObject;
                            applicationContext = context.getApplicationContext();
                            Toast.makeText(applicationContext, "application got", Toast.LENGTH_SHORT).show();
                        } else {
                            XposedBridge.unhookMethod(it, this);
                        }
                    }
                };
                HookUtil.findAndHookMethod(contextImplClass, it.getName(), paramsAndHook);
            }
            hooked = true;
        }
    }

    @Override
    public boolean needHook() {
        return !hooked;
    }
}
