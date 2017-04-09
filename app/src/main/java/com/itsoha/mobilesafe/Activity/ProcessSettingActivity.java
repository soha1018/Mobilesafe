package com.itsoha.mobilesafe.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.itsoha.mobilesafe.R;
import com.itsoha.mobilesafe.Service.LockSreenService;
import com.itsoha.mobilesafe.Utils.ConstanVlauel;
import com.itsoha.mobilesafe.Utils.IsServiceRunning;
import com.itsoha.mobilesafe.Utils.SpUtils;

/**
 * 进程管理的设置界面
 * Created by Administrator on 2017/4/8.
 */

public class ProcessSettingActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_setting);

        //初始化控件
        initView();
        //初始化锁屏清理的服务
        initLockScreen();
    }

    /**
     * 初始化锁屏清理的服务
     */
    private void initLockScreen() {
        final CheckBox cb_lock_clear = (CheckBox) findViewById(R.id.cb_lock_clear);

        boolean state = IsServiceRunning.getServiceState(this, "com.itsoha.mobilesafe.Service.LockSreenService");


        if (state){
            cb_lock_clear.setText("锁屏清理已开启");
        }else {
            cb_lock_clear.setText("锁屏清理已关启");
        }
        cb_lock_clear.setChecked(state);

        cb_lock_clear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cb_lock_clear.setText("锁屏清理已开启");
                    startService(new Intent(getApplicationContext(),LockSreenService.class));
                }else {
                    cb_lock_clear.setText("锁屏清理已关启");
                    //停止服务
                    stopService(new Intent(getApplicationContext(),LockSreenService.class));
                }
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initView() {
        final CheckBox cb_process_system = (CheckBox) findViewById(R.id.cb_process_system);

        boolean isCheck = SpUtils.getBoolean(this, ConstanVlauel.SHOW_SYSTEM, false);
        if (isCheck){
            cb_process_system.setText("隐藏系统进程");
        }else {
            cb_process_system.setText("显示系统进程");
        }
        cb_process_system.setChecked(isCheck);

        cb_process_system.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cb_process_system.setText("隐藏系统进程");
                }else {
                    cb_process_system.setText("显示系统进程");
                }
                SpUtils.putBoolean(getApplicationContext(), ConstanVlauel.SHOW_SYSTEM,isChecked);

            }
        });
    }
}
