package com.andr0day.appinfo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by andr0day on 2015/3/18.
 */
public class MountInfoActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.largetext);

        StringBuilder sb = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec("mount");
            InputStream is = process.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n\n");
            }

            TextView textView = (TextView) findViewById(R.id.text);
            textView.setText(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
