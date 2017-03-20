package com.itsoha.mobilesafe.Activity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.itsoha.mobilesafe.R;
import com.itsoha.mobilesafe.Receiver.DeviceAdminReceiver;
import com.itsoha.mobilesafe.Utils.ConstanVlauel;
import com.itsoha.mobilesafe.Utils.SpUtils;

/**
 * 手机防盗界面4
 * Created by Administrator on 2017/2/20.
 */
public class Setup4Activity extends BaseSetupActivity {

    private CheckBox cb_box_setup4;
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mComponentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);


        initUi();


    }

    @Override
    public void showNextPage() {
        //设备管理员
        mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mComponentName = new ComponentName(this, DeviceAdminReceiver.class);

        boolean aBoolean = SpUtils.getBoolean(this, ConstanVlauel.OPEN_SAFETY, false);
        if (aBoolean) {

            //设备管理器是否开启的判断,没有开启就跳转到设备激活界面，开启后就跳转到设置完成界面
            if (!mDevicePolicyManager.isAdminActive(mComponentName)){
                //告诉系统向设备添加一个新的管理员
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                //管理员组件名称
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
                //额外的说明
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "设备管理器");
                startActivity(intent);


            }else {
                Intent setupOver = new Intent(this, SetupOverActivity.class);
                startActivity(setupOver);

                //告诉导航界面都设置完成了
                SpUtils.putBoolean(this, ConstanVlauel.SETUO_OVER, true);
                finish();

                //平移的效果
                overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
            }

        } else {
            Toast.makeText(this, "请勾选手机防盗", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showPrePage() {
        Intent intent = new Intent(this, Setup3Activity.class);
        startActivity(intent);
        finish();

        //平移的效果
        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
    }

    /**
     * 初始化Ui
     */
    private void initUi() {
        cb_box_setup4 = (CheckBox) findViewById(R.id.cb_box_setup4);

        //先获取它的状态
        boolean check = SpUtils.getBoolean(this, ConstanVlauel.OPEN_SAFETY, false);
        cb_box_setup4.setChecked(check);

        if (check) {
            cb_box_setup4.setText("手机防盗已开启");
        } else {
            cb_box_setup4.setText("手机防盗已关闭");
        }

        cb_box_setup4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_box_setup4.setText("手机防盗已开启");
                } else {
                    cb_box_setup4.setText("手机防盗已关闭");
                }

                SpUtils.putBoolean(getApplicationContext(), ConstanVlauel.OPEN_SAFETY, isChecked);
            }
        });


    }

}
