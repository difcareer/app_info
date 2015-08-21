package com.andr0day.xposed.svc;

import android.os.RemoteException;
import com.andr0day.appinfo.common.ClassUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class XpServiceImpl extends IXpService.Stub {

    @Override
    public List<String> getClassLoaders() throws RemoteException {
        Set<ClassLoader> classLoaders = ClassUtil.getInstance().classLoaders;
        List<String> classNames = new ArrayList<String>();
        for (ClassLoader it : classLoaders) {
            classNames.add(it.getClass().getCanonicalName());
        }
        return classNames;
    }

    @Override
    public List<String> getClasses() throws RemoteException {
        Set<Class> classes = ClassUtil.getInstance().classes;
        List<String> classNames = new ArrayList<String>();
        for (Class it : classes) {
            classNames.add(it.getCanonicalName());
        }
        return classNames;
    }

    @Override
    public List<String> getMethods(String className) throws RemoteException {
        Class clazz = ClassUtil.getInstance().findClass(className);
        List<String> tmp = new ArrayList<String>();
        if (clazz != null) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method it : methods) {
                tmp.add(it.toString());
            }
        }
        return tmp;
    }

}
