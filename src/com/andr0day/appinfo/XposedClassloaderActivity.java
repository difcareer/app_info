package com.andr0day.appinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.andr0day.xposed.svc.IXpService;
import com.andr0day.xposed.util.XposedUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andr0day
 * on 2015/7/27.
 */
public class XposedClassloaderActivity extends Activity {

    private String pkgName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pkgName = getIntent().getStringExtra(XposedUtil.PKG_NAME);
        IXpService xpService = (IXpService) ServiceManager.getService(XposedUtil.XPOSED_SERVICE_PREFIX + pkgName);
        List<String> classLoaders = new ArrayList<String>();
        try {
            classLoaders = xpService.getClassLoaders();
        } catch (RemoteException e) {
            Log.e(XposedUtil.TAG, "getClassLoaders", e);
        }
        CommonAdaptor<String> classloaderAdaptor = new CommonAdaptor<String>(classLoaders, this, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(XposedClassloaderActivity.this, XposedClassActivity.class);
                intent.putExtra(XposedUtil.CLASS_LOADER, ((TextView) findViewById(view.getId())).getText());
                intent.putExtra(XposedUtil.PKG_NAME, pkgName);
                startActivity(intent);
            }
        });
        ListView listView = new ListView(this);
        listView.setAdapter(classloaderAdaptor);
    }
}
