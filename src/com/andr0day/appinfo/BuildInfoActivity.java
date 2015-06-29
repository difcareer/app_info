package com.andr0day.appinfo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.andr0day.appinfo.common.ProcessUtils;

/**
 * Created by andr0day on 2015/3/18.
 */
public class BuildInfoActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.largetext);

        StringBuilder sb = new StringBuilder();
        String txt = ProcessUtils.exec("getprop");
        String[] lines = txt.split("\r?\n");
        for (String it : lines) {
            String[] pt = it.split(":");
            sb.append("> " + pt[0].replace("[", "").replace("]", "") + "\n    " + pt[1].replace("[", "").replace("]", "") + "\n\n");
        }
        TextView textView = (TextView) findViewById(R.id.text);
        textView.setText(sb.toString());
    }
}
