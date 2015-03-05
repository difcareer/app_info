package com.andr0day.appinfo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.andr0day.appinfo.common.AppUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppActivity extends Activity {

    private static final String APP_ICON = "appIcon";
    private static final String APP_NAME = "appName";
    private static final String PKG_NAME = "pkgName";
    private static final String APK_PATH = "apkPath";
    private static final String DATA_DIR = "dataDir";
    private static final String APP_FLAGS = "appFlags";


    private static final int MSG_LOAD_START = 1;
    private static final int MSG_LOAD_FINISH = 2;

    private LayoutInflater mLayoutInflater;


    private Handler mHander = new Handler() {

        public void handleMessage(Message msg) {
            List<Map<String, Object>> data;
            switch (msg.what) {
                case MSG_LOAD_START:
                    List<PackageInfo> pkgs = getPackageManager().getInstalledPackages(0);
                    data = new ArrayList<Map<String, Object>>();
                    for (PackageInfo pkg : pkgs) {
                        Map tmp = new HashMap();
                        //no permission
//                        tmp.put(APP_ICON, null);
                        tmp.put(APP_ICON, AppUtil.getAppIcon(pkg, getPackageManager()));
                        tmp.put(APP_NAME, AppUtil.getAppName(pkg, getPackageManager()));
                        tmp.put(PKG_NAME, AppUtil.getPkgName(pkg));
                        tmp.put(APK_PATH, AppUtil.getApkPath(pkg));
                        tmp.put(DATA_DIR, AppUtil.getDataDir(pkg));
                        tmp.put(APP_FLAGS, AppUtil.getAppFlags(pkg));
                        if (AppUtil.isSystemApp(AppUtil.getAppFlags(pkg))) {
                            data.add(tmp);
                        } else {
                            data.add(0, tmp);
                        }
                    }
                    Message message = obtainMessage();
                    message.what = MSG_LOAD_FINISH;
                    message.obj = data;
                    sendMessage(message);
                    break;
                case MSG_LOAD_FINISH:
                    data = (List<Map<String, Object>>) msg.obj;
                    ListView listView = new ListView(AppActivity.this);
                    listView.setAdapter(new MyAdaptor(data));
                    AppActivity.this.setContentView(listView);
                    break;
            }

        }

    };

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
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (position < data.size()) {
//                no permission
                viewHolder.appIcon.setImageDrawable((Drawable) data.get(position).get(APP_ICON));
                String appName = (String) data.get(position).get(APP_NAME);
                String pkgName = (String) data.get(position).get(PKG_NAME);
                if (AppUtil.getAppLauncherIntent(pkgName, AppActivity.this.getPackageManager()) != null) {
                    viewHolder.appName.setText(Html.fromHtml("<u>" + appName + "</u>"));
                } else {
                    viewHolder.appName.setText(appName);
                }
                if (AppUtil.isSystemApp((Integer) data.get(position).get(APP_FLAGS))) {
                    viewHolder.appName.setTextColor(getResources().getColor(R.color.yellow));
                } else {
                    viewHolder.appName.setTextColor(getResources().getColor(R.color.green));
                }
                viewHolder.pkgName.setText(pkgName);
                viewHolder.apkPath.setText((String) data.get(position).get(APK_PATH));
                viewHolder.dataDir.setText((String) data.get(position).get(DATA_DIR));
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String pkgName = (String) data.get(position).get(PKG_NAME);
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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Message message = mHander.obtainMessage();
        message.what = MSG_LOAD_START;
        mHander.sendMessage(message);
    }
}
