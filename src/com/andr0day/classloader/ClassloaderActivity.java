package com.andr0day.classloader;

import android.app.Activity;
import android.os.Bundle;
import com.andr0day.appinfo.jdi.Trace;

public class ClassloaderActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Trace(new String[]{});
    }
}