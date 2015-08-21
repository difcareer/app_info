package com.andr0day.appinfo;

import android.app.Activity;
import android.os.Bundle;
import com.andr0day.appinfo.common.ClassUtil;

import java.lang.reflect.Method;

public class XposedMethodActivity extends Activity {
    private String clazzName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clazzName = getIntent().getStringExtra("class");
        Class clazz = ClassUtil.getInstance().findClass(clazzName);
        Method[] methods = clazz.getDeclaredMethods();


    }


    public static void main(String[] args) {
        System.out.println(XposedMethodActivity.class.getDeclaredMethods()[1].toGenericString());
    }
}
