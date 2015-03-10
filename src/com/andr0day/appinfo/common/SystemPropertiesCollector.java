package com.andr0day.appinfo.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by andr0day on 2015/3/10.
 */
public class SystemPropertiesCollector {

    public static final String SYSTEM_SECURE = "ro.secure";

    public static final String IS_DEBUGGABLE = "ro.debuggable";

    private static Method reflectGet() {
        try {
            Class clazz = Class.forName("android.os.SystemProperties");
            return clazz.getDeclaredMethod("get", String.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Method reflectGetDefault() {
        try {
            Class clazz = Class.forName("android.os.SystemProperties");
            Class[] types = new Class[2];
            types[0] = String.class;
            types[1] = String.class;
            return clazz.getDeclaredMethod("get", types);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Method reflectGetBooleanDefault() {
        try {
            Class clazz = Class.forName("android.os.SystemProperties");
            Class[] types = new Class[2];
            types[0] = String.class;
            types[1] = boolean.class;
            return clazz.getDeclaredMethod("getBoolean", types);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Method reflectGetIntDefault() {
        try {
            Class clazz = Class.forName("android.os.SystemProperties");
            Class[] types = new Class[2];
            types[0] = String.class;
            types[1] = int.class;
            return clazz.getDeclaredMethod("getInt", types);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Method reflectGetLongDefault() {
        try {
            Class clazz = Class.forName("android.os.SystemProperties");
            Class[] types = new Class[2];
            types[0] = String.class;
            types[1] = long.class;
            return clazz.getDeclaredMethod("getLong", types);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String get(String key) {
        Method method = reflectGet();
        if (method != null) {
            try {
                return (String) method.invoke(null, key);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String get(String key, String def) {
        Method method = reflectGetDefault();
        if (method != null) {
            try {
                return (String) method.invoke(null, key, def);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Boolean getBoolean(String key, boolean def) {
        Method method = reflectGetBooleanDefault();
        if (method != null) {
            try {
                return (Boolean) method.invoke(null, key, def);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Integer getInt(String key, int def) {
        Method method = reflectGetIntDefault();
        if (method != null) {
            try {
                return (Integer) method.invoke(null, key, def);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Long getLong(String key, long def) {
        Method method = reflectGetLongDefault();
        if (method != null) {
            try {
                return (Long) method.invoke(null, key, def);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
