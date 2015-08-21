package com.andr0day.xposed.util;

public class XposedUtil {

    public final static String TAG = "Xposed";

    public final static String LOADED_CLASS = "loadedClass";

    public final static String CLASS = "class";

    public final static String CLASS_LOADER = "classLoader";

    public final static String LOG = "log";

    public final static String EVENT = "event";

    public static final String HOST_FULL_PATH = "/data/data/com.andr0day.appinfo";

    public static final String XPOSED = "xposed";

    public static final String XPOSED_FULL_PATH = XposedUtil.HOST_FULL_PATH + "/" + XposedUtil.XPOSED;

    public static final String INJECT_PKG_CONFIG = "cfg.properties";

    public final static String XPOSED_BROADCAST_PREFIX = "xposed_broadcast_prefix_";

    public final static String XPOSED_SERVICE_PREFIX = "xposed_service_prefix_";

    public final static String BROADCAST_REFLECT = "broadcast_reflect";

    public static final String CMD_MONITOR_DIR = "monitor";

    public static final String CMD_STACK = "stack";

    public static final String CMD_BEFORE = "before";

    public static final String CMD_AFTER = "after";

    public static final String CMD_BEFORE_TEMP_SET = "before_temp_set";

    public static final String CMD_BEFORE_PERM_SET = "before_perm_set";

    public static final String CMD_BEFORE_COND_SET = "before_cond_set";

    public static final String CMD_AFTER_TEMP_SET = "after_temp_set";

    public static final String CMD_AFTER_PERM_SET = "after_perm_set";

    public static final String CMD_AFTER_COND_SET = "after_cond_set";

    public static final String CMD_GET_METHODS = "get_methods";

    public static final String CMD = "cmd";

    public static final String JSON = "json";

    public static final String PKG_NAME = "pkgName";

}
