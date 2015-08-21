package com.andr0day.xposed;

import com.andr0day.xposed.util.HookUtil;
import com.andr0day.xposed.util.XposedLogUtil;
import de.robv.android.xposed.XC_MethodHook;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class ClassloaderHooker {

    public static ConcurrentHashMap classloaderMap = new ConcurrentHashMap();


    public static void startHook(ClassLoader appClassLoader) {
        put(appClassLoader);
        hookLoadClass(appClassLoader);
    }

    public static void hookLoadClass(ClassLoader classLoader) {

        Method[] methods = ClassLoader.class.getDeclaredMethods();
        for (Method it : methods) {

            try {
                Object[] types = it.getParameterTypes();
                Object[] tt = Arrays.copyOf(types, types.length + 1);
                tt[types.length] = new XC_MethodHook() {

                    public void beforeHookedMethod(MethodHookParam param) {
                        XposedLogUtil.log("beforeHookedMethod" + param.method.getName());
                    }

                    public void afterHookedMethod(MethodHookParam param) {

                    }

                };
                HookUtil.findAndHookMethod(ClassLoader.class.getCanonicalName(), classLoader, it.getName(), tt);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void put(ClassLoader classLoader) {
        if (!classloaderMap.containsKey(classLoader)) {
            classloaderMap.put(classLoader, 1);
        }
    }


}
