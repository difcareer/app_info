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
        sb.append("> ro.build.type:\n    " + SystemPropertiesCollector.get("ro.build.type") + "\n\n");
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

//        # Define the oom_adj values for the classes of processes that can be
//        # killed by the kernel.  These are used in ActivityManagerService.
//        setprop ro.FOREGROUND_APP_ADJ 0
//        setprop ro.VISIBLE_APP_ADJ 1
//        setprop ro.SECONDARY_SERVER_ADJ 2
//        setprop ro.BACKUP_APP_ADJ 2
//        setprop ro.HOME_APP_ADJ 4
//        setprop ro.HIDDEN_APP_MIN_ADJ 7
//        setprop ro.CONTENT_PROVIDER_ADJ 14
//        setprop ro.EMPTY_APP_ADJ 15
//
//        # Define the memory thresholds at which the above process classes will
//        # be killed.  These numbers are in pages (4k).
//        setprop ro.FOREGROUND_APP_MEM 1536
//        setprop ro.VISIBLE_APP_MEM 2048
//        setprop ro.SECONDARY_SERVER_MEM 4096
//        setprop ro.BACKUP_APP_MEM 4096
//        setprop ro.HOME_APP_MEM 4096
//        setprop ro.HIDDEN_APP_MEM 5120
//        setprop ro.CONTENT_PROVIDER_MEM 5632
//        setprop ro.EMPTY_APP_MEM 6144

        sb.append("> ro.FOREGROUND_APP_ADJ:\n    " + SystemPropertiesCollector.get("ro.FOREGROUND_APP_ADJ") + "\n\n");
        sb.append("> ro.VISIBLE_APP_ADJ:\n    " + SystemPropertiesCollector.get("ro.VISIBLE_APP_ADJ") + "\n\n");
        sb.append("> ro.SECONDARY_SERVER_ADJ:\n    " + SystemPropertiesCollector.get("ro.SECONDARY_SERVER_ADJ") + "\n\n");
        sb.append("> ro.BACKUP_APP_ADJ:\n    " + SystemPropertiesCollector.get("ro.BACKUP_APP_ADJ") + "\n\n");
        sb.append("> ro.HOME_APP_ADJ:\n    " + SystemPropertiesCollector.get("ro.HOME_APP_ADJ") + "\n\n");
        sb.append("> ro.HIDDEN_APP_MIN_ADJ:\n    " + SystemPropertiesCollector.get("ro.HIDDEN_APP_MIN_ADJ") + "\n\n");
        sb.append("> ro.CONTENT_PROVIDER_ADJ:\n    " + SystemPropertiesCollector.get("CONTENT_PROVIDER_ADJ") + "\n\n");
        sb.append("> ro.EMPTY_APP_ADJ:\n    " + SystemPropertiesCollector.get("ro.EMPTY_APP_ADJ") + "\n\n");

        sb.append("> ro.FOREGROUND_APP_MEM:\n    " + SystemPropertiesCollector.get("ro.FOREGROUND_APP_MEM") + "\n\n");
        sb.append("> ro.VISIBLE_APP_MEM:\n    " + SystemPropertiesCollector.get("ro.VISIBLE_APP_MEM") + "\n\n");
        sb.append("> ro.SECONDARY_SERVER_MEM:\n    " + SystemPropertiesCollector.get("ro.SECONDARY_SERVER_MEM") + "\n\n");
        sb.append("> ro.BACKUP_APP_MEM:\n    " + SystemPropertiesCollector.get("ro.BACKUP_APP_MEM") + "\n\n");
        sb.append("> ro.HOME_APP_MEM:\n    " + SystemPropertiesCollector.get("ro.HOME_APP_MEM") + "\n\n");
        sb.append("> ro.HIDDEN_APP_MEM:\n    " + SystemPropertiesCollector.get("ro.HIDDEN_APP_MEM") + "\n\n");
        sb.append("> ro.CONTENT_PROVIDER_MEM:\n    " + SystemPropertiesCollector.get("ro.CONTENT_PROVIDER_MEM") + "\n\n");
        sb.append("> ro.EMPTY_APP_MEM:\n    " + SystemPropertiesCollector.get("ro.EMPTY_APP_MEM") + "\n\n");


        TextView textView = (TextView) findViewById(R.id.text);
        textView.setText(sb.toString());
    }
}
