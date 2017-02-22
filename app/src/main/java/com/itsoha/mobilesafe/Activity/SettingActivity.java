package com.itsoha.mobilesafe.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.itsoha.mobilesafe.R;
import com.itsoha.mobilesafe.Utils.ConstanVlauel;

import com.itsoha.mobilesafe.Utils.SpUtils;
import com.itsoha.mobilesafe.View.SettingItemView;

/**
 * Created by Administrator on 2017/2/16.
 */
public class SettingActivity extends Activity {
    private static final String TAG = "SettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //自动更新的条目
        initUpdata();
    }

    /**
     * 自动更新条目的方法
     */
    private void initUpdata() {

        final SettingItemView siv_updata = (SettingItemView) findViewById(R.id.siv_updata);

        boolean checkBoolean = SpUtils.getBoolean(this, ConstanVlauel.IS_CHECK, false);
        Log.i(TAG, "initUpdata: "+checkBoolean);
        siv_updata.setCheck(checkBoolean);
        siv_updata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = siv_updata.isCheck();
                Log.i(TAG, "onClick: "+!check);
                siv_updata.setCheck(!check);
                SpUtils.putBoolean(getApplication(),ConstanVlauel.IS_CHECK,!check);
            }
        });
    }
}
