package com.andr0day.appinfo;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import com.andr0day.appinfo.common.SystemPropertiesCollector;

/**
 * Created by andr0day on 2015/3/18.
 */
public class BuildInfoActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.largetext);

        StringBuilder sb = new StringBuilder();
        sb.append("> " + SystemPropertiesCollector.IS_DEBUGGABLE + "\n    " + SystemPropertiesCollector.getInt(SystemPropertiesCollector.IS_DEBUGGABLE, 0) + "\n\n");
        sb.append("> " + SystemPropertiesCollector.SYSTEM_SECURE + "\n    " + SystemPropertiesCollector.get(SystemPropertiesCollector.SYSTEM_SECURE, "0") + "\n\n");
        sb.append("> ro.build.id:\n    " + Build.ID + "\n\n");
        sb.append("> ro.build.display.id:\n    " + Build.DISPLAY + "\n\n");
        sb.append("> ro.product.name:\n    " + Build.PRODUCT + "\n\n");
        sb.append("> ro.product.device:\n    " + Build.DEVICE + "\n\n");
        sb.append("> ro.product.board:\n    " + Build.BOARD + "\n\n");
        sb.append("> ro.product.manufacturer:\n    " + Build.MANUFACTURER + "\n\n");
        sb.append("> ro.product.brand:\n    " + Build.BRAND + "\n\n");
        sb.append("> gsm.version.baseband:\n    " + Build.RADIO + "\n\n");
        sb.append("> ro.hardware:\n    " + Build.HARDWARE + "\n\n");
        sb.append("> ro.serialno:\n    " + Build.SERIAL + "\n\n");
        sb.append("> ro.build.version.incremental:\n    " + Build.VERSION.INCREMENTAL + "\n\n");
        sb.append("> ro.build.version.release:\n    " + Build.VERSION.RELEASE + "\n\n");
        sb.append("> ro.build.version.sdk:\n    " + Build.VERSION.SDK + "\n\n");
        sb.append("> ro.build.version.sdk.int:\n    " + Build.VERSION.SDK_INT + "\n\n");
        sb.append("> ro.build.version.codename:\n    " + Build.VERSION.CODENAME + "\n\n");

        TextView textView = (TextView) findViewById(R.id.text);
        textView.setText(sb.toString());
    }
}
