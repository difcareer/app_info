package com.andr0day.appinfo;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.andr0day.appinfo.common.FileUtils;
import com.andr0day.appinfo.common.RootUtil;

import java.io.File;

/**
 * Created by andr0day on 2015/5/7.
 */
public class ModifyBuildActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modifybuild);

        final EditText keyText = (EditText) findViewById(R.id.build_key);
        final EditText valueText = (EditText) findViewById(R.id.build_value);
        Button btn = (Button) findViewById(R.id.build_commit);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = keyText.getText().toString();
                String value = valueText.getText().toString();
                if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                    FileUtils.copyAssetsToFiles(ModifyBuildActivity.this, "setprop");
                    File file = new File(ModifyBuildActivity.this.getFilesDir(), "setprop");
                    RootUtil.safeExecStr("chmod 777 " + file.getAbsolutePath());
                    RootUtil.safeExecStr(file.getAbsolutePath() + " " + key + " " + value);
                    Toast.makeText(ModifyBuildActivity.this, "执行成功，请到build参数中验证", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
