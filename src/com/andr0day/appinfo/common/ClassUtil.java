package com.andr0day.appinfo.common;

import android.util.Log;
import com.andr0day.appinfo.AppApplication;
import com.andr0day.xposed.util.XposedUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by andr0day
 * on 2015/7/22.
 */
public class ClassUtil {

    public Set<Class> classes = new HashSet<Class>();

    public Set<ClassLoader> classLoaders = new HashSet<ClassLoader>();

    private static ClassUtil instance;

    public static ClassUtil getInstance() {
        if (instance == null) {
            instance = new ClassUtil();
        }
        return instance;
    }

    private ClassUtil() {
        Log.e("Xposed", "before so load");
        RootUtil.safeExecStr("chmod 777 " + AppApplication.SO_FULL_PATH);
        System.load(AppApplication.SO_FULL_PATH);
//                System.loadLibrary("loaded-class");
        Log.e("Xposed", "after so loaded");
    }

    public native Class getLoadedClass(int index);

    public native int getTableSize();

    public native String stringFromJNI();

    public native int close();

    public void loadClasses() {
        int tableSize = getTableSize();
        Log.e(XposedUtil.LOADED_CLASS, "tableSize:" + tableSize);
        for (int i = 0; i < tableSize; i++) {
            Class clazz = getLoadedClass(i);
            if (clazz != null && clazz.getCanonicalName() != null) {
                Log.e(XposedUtil.LOADED_CLASS, "class:" + clazz.getCanonicalName());
                classes.add(clazz);
                classLoaders.add(clazz.getClassLoader());
//                break;
            }
        }
        for (ClassLoader it : classLoaders) {
            Log.e(XposedUtil.LOADED_CLASS, "classloader:" + it.toString());
        }
        close();
    }

    public Class findClass(String name) {
        for (Class it : classes) {
            if (it.getCanonicalName().equals(name)) {
                return it;
            }
        }
        return null;
    }

}
