package com.andr0day.appinfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.andr0day.appinfo.common.AppUtil;
import com.andr0day.appinfo.common.CertUtils;
import com.andr0day.appinfo.common.Constants;
import com.andr0day.appinfo.common.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class AppActivity extends Activity {

    private static final int MSG_LOAD_START = 1;

    private static final int MSG_LOAD_FINISH = 2;

    private LayoutInflater mLayoutInflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button systemInfoB = (Button) findViewById(R.id.systemInfo);
        Button buildInfoB = (Button) findViewById(R.id.buildInfo);
        Button appInfoB = (Button) findViewById(R.id.appInfo);


        systemInfoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Properties properties = System.getProperties();
                StringBuilder sb = new StringBuilder();
                for (Object key : properties.keySet()) {
                    sb.append("> " + key + ":\n    " + properties.get(key) + "\n\n");
                }
                TextView textview = new TextView(AppActivity.this);
                textview.setMovementMethod(new ScrollingMovementMethod());
                textview.setText(sb.toString());
                AppActivity.this.setContentView(textview);
            }
        });

        buildInfoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sb = new StringBuilder();
                sb.append("> ro.debuggable:\n    " + reflectGetDebug() + "\n");
                sb.append("> ro.build.id:\n    " + Build.ID + "\n");
                sb.append("> ro.build.display.id:\n    " + Build.DISPLAY + "\n");
                sb.append("> ro.product.name:\n    " + Build.PRODUCT + "\n");
                sb.append("> ro.product.device:\n    " + Build.DEVICE + "\n");
                sb.append("> ro.product.board:\n    " + Build.BOARD + "\n");
                sb.append("> ro.product.manufacturer:\n    " + Build.MANUFACTURER + "\n");
                sb.append("> ro.product.brand:\n    " + Build.BRAND + "\n");
                sb.append("> gsm.version.baseband:\n    " + Build.RADIO + "\n");
                sb.append("> ro.hardware:\n    " + Build.HARDWARE + "\n");
                sb.append("> ro.serialno:\n    " + Build.SERIAL + "\n");
                sb.append("> ro.build.version.incremental:\n    " + Build.VERSION.INCREMENTAL + "\n");
                sb.append("> ro.build.version.release:\n    " + Build.VERSION.RELEASE + "\n");
                sb.append("> ro.build.version.sdk:\n    " + Build.VERSION.SDK + "\n");
                sb.append("> ro.build.version.sdk.int:\n    " + Build.VERSION.SDK_INT + "\n");
                sb.append("> ro.build.version.codename:\n    " + Build.VERSION.CODENAME + "\n");
                TextView textview = new TextView(AppActivity.this);
                textview.setMovementMethod(new ScrollingMovementMethod());
                textview.setText(sb.toString());
                AppActivity.this.setContentView(textview);
            }
        });

        appInfoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AppInfoCollector(AppActivity.this).execute();
            }
        });
    }

    private Object reflectGetDebug() {
        Class clazz = Build.class;
        try {
            Field field = clazz.getDeclaredField("IS_DEBUGGABLE");
            field.setAccessible(true);
            return field.get(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class MyAdaptor extends BaseAdapter {

        List<Map<String, Object>> data;

        MyAdaptor(List<Map<String, Object>> data) {
            this.data = data;
            mLayoutInflater = LayoutInflater.from(AppActivity.this);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.appitem, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.appIcon = (ImageView) convertView.findViewById(R.id.appIcon);
                viewHolder.appName = (TextView) convertView.findViewById(R.id.appName);
                viewHolder.pkgName = (TextView) convertView.findViewById(R.id.pkgName);
                viewHolder.apkPath = (TextView) convertView.findViewById(R.id.apkPath);
                viewHolder.dataDir = (TextView) convertView.findViewById(R.id.dataDir);
                viewHolder.sigs = (TextView) convertView.findViewById(R.id.sigs);
                viewHolder.debugable = (ImageView) convertView.findViewById(R.id.debugable);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (position < data.size()) {
                viewHolder.appIcon.setImageDrawable((Drawable) data.get(position).get(Constants.APP_ICON));
                String appName = (String) data.get(position).get(Constants.APP_NAME);
                String pkgName = (String) data.get(position).get(Constants.PKG_NAME);
                if (AppUtil.getAppLauncherIntent(pkgName, AppActivity.this.getPackageManager()) != null) {
                    viewHolder.appName.setText(Html.fromHtml("<u>" + appName + "</u>"));
                } else {
                    viewHolder.appName.setText(appName);
                }
                if (AppUtil.isSystemApp((Integer) data.get(position).get(Constants.APP_FLAGS))) {
                    viewHolder.appName.setTextColor(getResources().getColor(R.color.yellow));
                } else {
                    viewHolder.appName.setTextColor(getResources().getColor(R.color.green));
                }
                if (AppUtil.isDebugable((Integer) data.get(position).get(Constants.APP_FLAGS))) {
                    viewHolder.debugable.setImageDrawable(getResources().getDrawable(R.drawable.debug));
                }

                viewHolder.pkgName.setText(pkgName);
                viewHolder.apkPath.setText((String) data.get(position).get(Constants.APK_PATH));
                viewHolder.dataDir.setText((String) data.get(position).get(Constants.DATA_DIR));
                viewHolder.sigs.setText(StringUtils.join((List) data.get(position).get(Constants.APP_SIGS), ","));
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String pkgName = (String) data.get(position).get(Constants.PKG_NAME);
                        Intent intent = AppUtil.getAppLauncherIntent(pkgName, AppActivity.this.getPackageManager());
                        AppActivity.this.startActivity(intent);
                    } catch (Exception e) {
                        //ignore
                    }
                }
            });

            return convertView;
        }
    }

    public final class ViewHolder {
        public ImageView appIcon;
        public TextView appName;
        public TextView pkgName;
        public TextView apkPath;
        public TextView dataDir;
        public TextView sigs;
        public ImageView debugable;
    }

    class AppInfoCollector extends AsyncTask<Object, Integer, List<Map<String, Object>>> {

        private Context context;

        private PackageManager packageManager;

        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

        public AppInfoCollector(Context context) {
            this.context = context;
            this.packageManager = context.getPackageManager();
        }


        @Override
        protected List<Map<String, Object>> doInBackground(Object[] params) {
            List<PackageInfo> pkgs = packageManager.getInstalledPackages(0);
            data = new ArrayList<Map<String, Object>>();
            for (PackageInfo pkg : pkgs) {
                Map tmp = new HashMap();
                tmp.put(Constants.APP_ICON, AppUtil.getAppIcon(pkg, packageManager));
                tmp.put(Constants.APP_NAME, AppUtil.getAppName(pkg, packageManager));
                tmp.put(Constants.PKG_NAME, AppUtil.getPkgName(pkg));
                tmp.put(Constants.APK_PATH, AppUtil.getApkPath(pkg));
                tmp.put(Constants.DATA_DIR, AppUtil.getDataDir(pkg));
                PackageInfo tmpPkg = null;
                try {
                    tmpPkg = packageManager.getPackageInfo(AppUtil.getPkgName(pkg), PackageManager.GET_SIGNATURES);
//                    Log.e("SIG",AppUtil.getAppName(pkg, packageManager));
//                    CertUtils.parseSignature(tmpPkg.signatures[0].toByteArray());
                    tmp.put(Constants.APP_SIGS, CertUtils.getSigMd5s(tmpPkg));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                tmp.put(Constants.APP_FLAGS, AppUtil.getAppFlags(pkg));
                if (AppUtil.isSystemApp(AppUtil.getAppFlags(pkg))) {
                    data.add(tmp);
                } else {
                    data.add(0, tmp);
                }
            }
            return data;
        }

        protected void onPostExecute(List<Map<String, Object>> result) {
            ListView listView = new ListView(context);
            listView.setAdapter(new MyAdaptor(data));
            AppActivity.this.setContentView(listView);
        }
    }

}
