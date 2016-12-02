package com.andr0day.appinfo;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodInfo;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.andr0day.appinfo.common.AppUtil;
import com.andr0day.appinfo.common.CertUtils;
import com.andr0day.appinfo.common.DbHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by andr0day on 2015/3/18.
 */
public class AppInfoActivity extends Activity {
    private static final String TAG = "AppInfoActivity";

    private LayoutInflater mLayoutInflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("信息采集中，请稍后......");
        setContentView(textView);
        new AppInfoCollector(this).execute();
    }

    public void onResume() {
        super.onResume();
    }

    class AppInfoCollector extends AsyncTask<Object, Integer, List<PackageInfo>> {

        private Context context;

        private PackageManager packageManager;

        List<ResolveInfo> homes;
        List<InputMethodInfo> inputs;

        public AppInfoCollector(Context context) {
            this.context = context;
            this.packageManager = context.getPackageManager();
            homes = AppUtil.getHomes(context);
            inputs = AppUtil.getInputs(context);
        }

        @Override
        protected List<PackageInfo> doInBackground(Object[] params) {
            List<PackageInfo> packageInfos = packageManager.getInstalledPackages(PackageManager.GET_SIGNATURES);
            List<String> deviceAdmins = getDeviceAdmins(context);
            try {
                String pubKey = CertUtils
                        .getPubKey(packageManager.getPackageInfo("android", PackageManager.GET_SIGNATURES));
                int i = 0;
                int j = 0;
                for (PackageInfo it : packageInfos) {
                    if (AppUtil.isSystemApp(it) || AppUtil.isSystemUpdateApp(it)) {
                        j++;
                        if (deviceAdmins.contains(it.packageName)) {
                            i++;
                            Log.e(TAG, "Device: " + AppUtil.getAppName(it, packageManager));
                        } else if (isHome(context, it.packageName)) {
                            i++;
                            Log.e(TAG, "Home: " + AppUtil.getAppName(it, packageManager));
                        }
//                        else if (!isDisabled(it)) {
//                            i++;
//                            Log.e(TAG, "NoDisable: " + AppUtil.getAppName(it, packageManager));
//                        }
                        else if (pubKey.equals(CertUtils.getPubKey(it))) {
                            i++;
                            Log.e(TAG, "Cert: " + AppUtil.getAppName(it, packageManager));
                        }
                    }
                }
                Log.e(TAG, "count: " + i+" "+ j);
                Collections.sort(packageInfos, new Comparator<PackageInfo>() {
                    @Override
                    public int compare(PackageInfo lhs, PackageInfo rhs) {
                        if (!AppUtil.isSystemApp(lhs) && AppUtil.isSystemApp(rhs)) {
                            return -1;
                        } else if (AppUtil.isSystemApp(lhs) && !AppUtil.isSystemApp(rhs)) {
                            return 1;
                        } else if (AppUtil.isSystemUpdateApp(lhs) && !AppUtil.isSystemUpdateApp(rhs)) {
                            return -1;
                        } else if (!AppUtil.isSystemUpdateApp(lhs) && AppUtil.isSystemUpdateApp(rhs)) {
                            return 1;
                        }
                        return 0;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return packageInfos;
        }

        private boolean isHome(Context context, String pkgName) {
            Intent home = new Intent("android.intent.action.MAIN");
            home.addCategory("android.intent.category.HOME");
            home.setPackage(pkgName);
            List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(home, 0);
            if (resolveInfos != null && resolveInfos.size() > 0) {
                return true;
            }
            return false;
        }

        private boolean isDisabled(PackageInfo packageInfo) {
            try {
                Class applicationInfoClazz = ApplicationInfo.class;
                Field enableField = applicationInfoClazz.getField("enabled");
                enableField.setAccessible(true);
                Boolean b = (Boolean) enableField.get(packageInfo.applicationInfo);
                if (!b) {
                    return true;
                }
                Field enabledSettingField = applicationInfoClazz.getField("enabledSetting");
                enabledSettingField.setAccessible(true);
                Integer v = (Integer) enabledSettingField.get(packageInfo.applicationInfo);
                if (v == 3) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        private List<String> getDeviceAdmins(Context context) {
            List<String> pkgs = new ArrayList<String>();
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context
                    .getSystemService(DEVICE_POLICY_SERVICE);
            List<ComponentName> deviceAdmins = devicePolicyManager.getActiveAdmins();
            if (deviceAdmins != null && !deviceAdmins.isEmpty()) {
                for (ComponentName it : deviceAdmins) {
                    pkgs.add(it.getPackageName());
                }
            }
            return pkgs;
        }

        @Override
        protected void onPostExecute(List<PackageInfo> packageInfos) {
            ListView listView = new ListView(context);
            listView.setAdapter(new MyAdaptor(packageInfos, packageManager, homes, inputs));
            AppInfoActivity.this.setContentView(listView);
        }

    }

    private class MyAdaptor extends BaseAdapter {

        List<PackageInfo> packageInfos;
        PackageManager packageManager;
        List<ResolveInfo> homes;
        List<InputMethodInfo> inputs;

        MyAdaptor(List<PackageInfo> packageInfos, PackageManager packageManager, List<ResolveInfo> homes,
                List<InputMethodInfo> inputs) {
            this.packageInfos = packageInfos;
            this.packageManager = packageManager;
            this.homes = homes;
            this.inputs = inputs;
            mLayoutInflater = LayoutInflater.from(AppInfoActivity.this);
        }

        @Override
        public int getCount() {
            return packageInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return packageInfos.get(position);
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
                viewHolder.disabled = (ImageView) convertView.findViewById(R.id.disabled);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (position < packageInfos.size()) {
                PackageInfo packageInfo = packageInfos.get(position);
                viewHolder.appIcon.setImageDrawable(AppUtil.getAppIcon(packageInfo, packageManager));
                String appName = AppUtil.getAppName(packageInfo, packageManager);
                final String pkgName = AppUtil.getPkgName(packageInfo);
                if (AppUtil.getAppLauncherIntent(pkgName, packageManager) != null) {
                    viewHolder.appName.setText(Html.fromHtml("<u>" + appName + "</u>"));
                } else {
                    viewHolder.appName.setText(appName);
                }

                if (AppUtil.isSystemApp(packageInfo)) {
                    if (AppUtil.isSystemUpdateApp(packageInfo)) {
                        viewHolder.appName.setTextColor(getResources().getColor(R.color.lightYellow));
                    } else {
                        viewHolder.appName.setTextColor(getResources().getColor(R.color.yellow));
                    }
                } else {
                    viewHolder.appName.setTextColor(getResources().getColor(R.color.green));
                }

                if (AppUtil.isHome(packageInfo, homes) && !AppUtil.isInput(packageInfo, inputs)) {
                    viewHolder.appName.append(" [桌面]");
                } else if (AppUtil.isInput(packageInfo, inputs) && !AppUtil.isHome(packageInfo, homes)) {
                    viewHolder.appName.append(" [输入法]");
                } else if (AppUtil.isHome(packageInfo, homes) && AppUtil.isInput(packageInfo, inputs)) {
                    viewHolder.appName.append(" [桌面、输入法]");
                }

                if (AppUtil.isDebugable(packageInfo)) {
                    viewHolder.debugable.setImageDrawable(getResources().getDrawable(R.drawable.debug));
                    viewHolder.debugable.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.debugable.setVisibility(View.GONE);
                }

                if (DbHelper.getInstance(AppInfoActivity.this).existIsolate(pkgName)) {
                    viewHolder.disabled.setImageDrawable(getResources().getDrawable(R.drawable.disabled));
                    viewHolder.disabled.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.disabled.setVisibility(View.GONE);
                }

                viewHolder.pkgName.setText(pkgName);
                viewHolder.apkPath.setText(AppUtil.getApkPath(packageInfo));
                viewHolder.dataDir.setText(AppUtil.getDataDir(packageInfo));
                viewHolder.sigs.setText(TextUtils.join(",", CertUtils.getSigMd5s(packageInfo)));
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent();
                            intent.putExtra("pkgName", pkgName);
                            intent.setClass(AppInfoActivity.this, AppDetailActivity.class);
                            startActivity(intent);
                            //
                            //                            Intent intent = AppUtil.getAppLauncherIntent(pkgName, AppInfoActivity.this.getPackageManager());
                            //                            AppInfoActivity.this.startActivity(intent);
                        } catch (Exception e) {
                            //ignore
                        }
                    }
                });
            }
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
        public ImageView disabled;
    }

}
