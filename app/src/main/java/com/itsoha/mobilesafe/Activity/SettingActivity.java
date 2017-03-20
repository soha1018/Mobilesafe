package com.itsoha.mobilesafe.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.itsoha.mobilesafe.R;
import com.itsoha.mobilesafe.Service.AddressService;
import com.itsoha.mobilesafe.Utils.ConstanVlauel;

import com.itsoha.mobilesafe.Utils.IsServiceRunning;
import com.itsoha.mobilesafe.Utils.SpUtils;
import com.itsoha.mobilesafe.View.SettingItemView;

/**
 * Created by Administrator on 2017/2/16.
 */
public class SettingActivity extends Activity {
    private static final String TAG = "SettingActivity";
    private SettingItemView siv_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //自动更新的条目
        initUpdata();
        //是否显示归属地
        initAddress();

    }

    /**
     * 是否显示归属地
     */
    private void initAddress() {
        siv_address = (SettingItemView) findViewById(R.id.siv_address);

        //获取服务的开启状态
        boolean serviceState = IsServiceRunning.getServiceState(this, "com.itsoha.mobilesafe.Service.AddressService");
        if (serviceState){
            siv_address.setCheck(true);
        }else {
            siv_address.setCheck(false);
        }
        siv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = siv_address.isCheck();
                siv_address.setCheck(!check);

                if (!check){
                    //跳转到服务
                    boolean state = SpUtils.getBoolean(getApplicationContext(), ConstanVlauel.FLOATING_OFF, false);
                    if (state){
                        startService(new Intent(getApplicationContext(), AddressService.class));

                    }else {
                        Toast.makeText(SettingActivity.this, "请开启悬浮窗。", Toast.LENGTH_SHORT).show();
                        setFloating();//开启悬浮窗
                    }

                }else {
                    //停止服务
                    SpUtils.putBoolean(getApplicationContext(),ConstanVlauel.FLOATING_OFF,false);
                    stopService(new Intent(getApplicationContext(), AddressService.class));
                }
            }
        });
    }

    private void setFloating(){
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:"+"com.itsoha.mobilesafe"));
        startActivity(intent);

        SpUtils.putBoolean(this,ConstanVlauel.FLOATING_OFF,true);
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
