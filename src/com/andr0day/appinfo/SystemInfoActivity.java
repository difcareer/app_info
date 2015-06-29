package com.andr0day.appinfo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Properties;

/**
 * Created by andr0day on 2015/3/18.
 */
public class SystemInfoActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.largetext);

        Properties properties = System.getProperties();
        StringBuilder sb = new StringBuilder();
        for (Object key : properties.keySet()) {
            sb.append("> " + key + ":\n    " + properties.get(key) + "\n\n");
        }

        TextView textView = (TextView) findViewById(R.id.text);
        textView.setText(sb.toString());
    }
}
