package com.andr0day.appinfo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.andr0day.appinfo.common.FileUtils;
import com.andr0day.appinfo.common.RootUtil;

import java.io.File;
import java.nio.charset.Charset;

public class WifiPwdActivity extends Activity {
    private static final String FILE_PATH = "/data/misc/wifi/wpa_supplicant.conf";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.largetext);
        RootUtil.execStr("chmod 777 " + FILE_PATH);
        String rawInfo = FileUtils.readFileToString(new File(FILE_PATH), Charset.defaultCharset());
        TextView textView = (TextView) findViewById(R.id.text);
        textView.setText(rawInfo);
    }
}