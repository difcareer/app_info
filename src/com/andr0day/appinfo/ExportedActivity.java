package com.andr0day.appinfo;

import android.app.Activity;
import android.content.pm.*;
import android.os.Bundle;
import android.widget.TextView;

public class ExportedActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.largetext);

        String pkgName = getIntent().getStringExtra("pkgName");
        StringBuilder sb = new StringBuilder();

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(pkgName, PackageManager.GET_ACTIVITIES
                    | PackageManager.GET_SERVICES | PackageManager.GET_PROVIDERS | PackageManager.GET_RECEIVERS);
            ActivityInfo[] activityInfos = packageInfo.activities;
            if (activityInfos != null) {
                sb.append(">>> activities:\n");
                for (ActivityInfo it : activityInfos) {
                    if (it.exported) {
                        sb.append("  " + it.name + "\n");
                    }


                }
            }
            ServiceInfo[] serviceInfos = packageInfo.services;
            if (serviceInfos != null) {
                sb.append("\n>>> services:\n");
                for (ServiceInfo it : serviceInfos) {
                    if (it.exported) {
                        sb.append("  " + it.name + "\n");
                    }
                }
            }
            ActivityInfo[] receivers = packageInfo.receivers;
            if (receivers != null) {
                sb.append("\n>>> receivers:\n");
                for (ActivityInfo it : receivers) {
                    if (it.exported) {
                        sb.append("  " + it.name + "\n");
                    }
                }
            }
            ProviderInfo[] providerInfos = packageInfo.providers;
            if (providerInfos != null) {
                sb.append("\n>>> providers:\n");
                for (ProviderInfo it : providerInfos) {
                    if (it.exported) {
                        sb.append("  " + it.name + "\n");
                    }
                }
            }
            TextView textView = (TextView) findViewById(R.id.text);
            textView.setText(sb.toString());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}