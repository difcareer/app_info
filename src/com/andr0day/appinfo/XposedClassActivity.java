package com.andr0day.appinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.andr0day.xposed.util.ConfigUtil;

import java.util.Arrays;

/**
 * Created by andr0day on 2015/7/27.
 */
public class XposedClassActivity extends Activity {
    private String classloader;
    private String pkgName;

    private CommonAdaptor<String> classAdaptor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pkgName = getIntent().getStringExtra("pkgName");
        classloader = getIntent().getStringExtra("classloader");
        String content = ConfigUtil.readStr(pkgName, "class");
        if (!TextUtils.isEmpty(content)) {
            String[] lines = content.split("\r?\n");
            classAdaptor = new CommonAdaptor<String>(Arrays.asList(lines), this, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(XposedClassActivity.this, XposedMethodActivity.class);
                    intent.putExtra("class", ((TextView) findViewById(view.getId())).getText());
                    startActivity(intent);
                }
            });
        }

    }
}
