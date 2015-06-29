package com.andr0day.appinfo.jdi;

import java.util.Hashtable;


public class ExceptionFilter {
    private static Hashtable<String, String> allowedHashTable = new Hashtable<String, String>();
    private static String SEPARATOR = ";";
    private static String EXCEPTION_LIST_NAME_KEY = "ExceptionName";

    public static boolean isAllowedException(String exception) {
        if (allowedHashTable.containsKey(exception)) {
            return true;
        }
        return false;
    }
}
