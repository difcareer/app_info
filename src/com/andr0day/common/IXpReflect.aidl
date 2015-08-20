package com.andr0day.common;

/**
 * Created by andr0day on 2015/7/27.
 */
interface XpReflect {

    List<String> getMethods(String className);

    void monitorStack(String methodId);

    void monitorBefore(String methodId);

    void monitorAfter(String methodId);

    void set(String methodId, String b_a, String type,String json);

}
