package com.andr0day.appinfo.jdi;

public class ClassPatternFilter {
    private static String SEPARATOR = ";";
    private static String EXCLUDED_CLASS_PATTERN = "ExcludedClassPattern";
    private static String[] excludes = new String[0];

    public static String[] getExcludes() {
        return excludes;
    }
}
