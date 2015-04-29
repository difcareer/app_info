package com.andr0day.appinfo;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;
import com.andr0day.appinfo.common.ProcessUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andr0day on 2015/4/28.
 */
public class Search777Activity extends Activity {

    private static final int REFRESH_PAGE = 1;

    private static final int SCAN_FINISH = 2;

    private TextView textView;

    private List<String> searchPaths = new ArrayList<String>();

    private Handler myHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_PAGE:
                    String txt = (String) msg.obj;
                    textView.append(txt + "\n");
                    break;
                case SCAN_FINISH:
                    textView.append("扫描完成");
                    Toast.makeText(Search777Activity.this, "扫描完成", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.largetext);
        textView = (TextView) findViewById(R.id.text);
        searchPaths.add("/");
        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... objects) {
                doSearch();
                return null;
            }
        }.execute();
    }

    void doSearch() {
        if (searchPaths.size() > 0) {
            String path = searchPaths.remove(0);
            boolean first = true;
            String lsInfo = ProcessUtils.exec("ls -al " + path);
            if (!TextUtils.isEmpty(lsInfo)) {
                String[] lines = lsInfo.split("\r*\n");
                for (String line : lines) {
                    if (!TextUtils.isEmpty(line)) {
                        String[] cols = line.split("( +|\\t+)");
                        if (cols.length > 5) {
                            String perm = cols[0];
                            if (perm.length() == 10) {
                                switch (perm.charAt(0)) {
                                    case 'd':
                                        try {
                                            String fullPath = "";
                                            fullPath = path + cols[cols.length - 1] + "/";
                                            if (!fullPath.startsWith("/proc/")
                                                    && !fullPath.startsWith("/emmc@")
                                                    && !fullPath.startsWith("/sys/bus/")
                                                    && !fullPath.startsWith("/dev/block/")
                                                    && !fullPath.startsWith("/sys/block/")
                                                    && !fullPath.startsWith("/sys/class/")
                                                    && !fullPath.startsWith("/sys/kernel/")
                                                    && !fullPath.startsWith("/sys/dev/block/")
                                                    && !fullPath.startsWith("/sys/dev/char/")
                                                    && !fullPath.startsWith("/sys/devices/")) {
                                                searchPaths.add(fullPath);
                                            }
                                        } catch (Exception e) {

                                        }
                                        break;
                                    default:
                                        if ("rwxrwxrwx".equals(perm.substring(1, perm.length()))) {
                                            String txt = "";
                                            if (first) {
                                                txt = "\n>>> " + path + "\n";
                                                first = false;
                                            }
                                            txt = txt + line;
                                            Message msg = myHandler.obtainMessage();
                                            msg.what = REFRESH_PAGE;
                                            msg.obj = txt;
                                            myHandler.sendMessage(msg);
                                        }
                                        break;
                                }
                            }

                        }
                    }

                }
            }
        }

        if (searchPaths.size() > 0) {
            doSearch();
        } else {
            myHandler.sendEmptyMessage(SCAN_FINISH);
        }
    }
}
