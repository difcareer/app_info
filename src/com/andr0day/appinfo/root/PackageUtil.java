package com.andr0day.appinfo.root;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.os.Build;
import android.os.IBinder;

import java.lang.reflect.Method;

/**
 * Created by andr0day on 2015/4/10.
 */
public class PackageUtil {

    public void disableApp(String pkgName) {

    }

    private void forceStop(String pkgName) {
        try {
            Class iPackageMangerClass = Class.forName("android.content.pm.IPackageManager");
            Object iPackageManager = getIPackageManager();
            if (iPackageManager != null) {
                Method setComponentEnabledSettingMethod = iPackageMangerClass.getMethod("setComponentEnabledSetting", ComponentName.class, int.class, int.class);
                setComponentEnabledSettingMethod.invoke(iPackageManager, pkgName, )
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * sdk_int < 16 userid is not needed
     */
    public void setComponentEnabledSetting(String pkgName, int newState, int flags, int userId) {
        try {
            Class iPackageMangerClass = Class.forName("android.content.pm.IPackageManager");
            Object iPackageManager = getIPackageManager();
            if (iPackageManager != null) {
                if (Build.VERSION.SDK_INT < 16) {
                    Method setComponentEnabledSettingMethod = iPackageMangerClass.getMethod("setComponentEnabledSetting", ComponentName.class, int.class, int.class);
                    setComponentEnabledSettingMethod.invoke(iPackageManager, pkgName, newState, flags);
                } else {
                    Method setComponentEnabledSettingMethod = iPackageMangerClass.getMethod("setComponentEnabledSetting", ComponentName.class, int.class, int.class, int.class);
                    setComponentEnabledSettingMethod.invoke(iPackageManager, pkgName, newState, flags, userId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * sdk_int < 16 userid is not needed
     */
    public void setApplicationEnabledSetting(String pkgName, int newState, int flags, int userId) {
        try {
            Class iPackageMangerClass = Class.forName("android.content.pm.IPackageManager");
            Object iPackageManager = getIPackageManager();
            if (iPackageManager != null) {
                if (Build.VERSION.SDK_INT < 16) {
                    Method setComponentEnabledSettingMethod = iPackageMangerClass.getMethod("setApplicationEnabledSetting", ComponentName.class, int.class, int.class);
                    setComponentEnabledSettingMethod.invoke(iPackageManager, pkgName, newState, flags);
                } else {
                    Method setComponentEnabledSettingMethod = iPackageMangerClass.getMethod("setApplicationEnabledSetting", ComponentName.class, int.class, int.class, int.class);
                    setComponentEnabledSettingMethod.invoke(iPackageManager, pkgName, newState, flags, userId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getApplicationEnabledSetting(String pkgName) {
        try {
            Class iPackageMangerClass = Class.forName("android.content.pm.IPackageManager");
            Object iPackageManager = getIPackageManager();
            if (iPackageManager != null) {
                if (Build.VERSION.SDK_INT < 16) {
                    Method setComponentEnabledSettingMethod = iPackageMangerClass.getMethod("getApplicationEnabledSetting", String.class);
                    setComponentEnabledSettingMethod.invoke(iPackageManager, pkgName, newState, flags);
                } else {
                    Method setComponentEnabledSettingMethod = iPackageMangerClass.getMethod("getApplicationEnabledSetting", ComponentName.class, int.class, int.class, int.class);
                    setComponentEnabledSettingMethod.invoke(iPackageManager, pkgName, newState, flags, userId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return IPackageManager
     */
    private Object getIPackageManager() {
        try {
            Class PackageManagerStubClass = Class.forName("android.content.pm.IPackageManager$Stub");
            Method asInterfaceMethod = PackageManagerStubClass.getMethod("asInterface", IBinder.class);
            Class serviceManagerClass = Class.forName("android.os.ServiceManager");
            Method getServiceMethod = serviceManagerClass.getMethod("getService", String.class);
            Object packageService = getServiceMethod.invoke(null, "package");
            return asInterfaceMethod.invoke(null, packageService);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object getActivityManager() {
        try {
            Class clazz = Class.forName("android.app.ActivityManagerNative");
            Method method = clazz.getMethod("getDefault");
            return (ActivityManager) method.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
