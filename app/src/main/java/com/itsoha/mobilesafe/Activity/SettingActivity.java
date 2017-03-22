package com.itsoha.mobilesafe.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.itsoha.mobilesafe.View.SettingClickView;
import com.itsoha.mobilesafe.View.SettingItemView;

/**
 * Created by Administrator on 2017/2/16.
 */
public class SettingActivity extends Activity {
    private static final String TAG = "SettingActivity";
    private SettingItemView siv_address;
    private SettingClickView scv_toast_style;
    private String[] mToastStyle;
    private int mStyle;
    private SettingClickView scv_toast_location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //自动更新的条目
        initUpdata();
        //是否显示归属地
        initAddress();
        //显示Toast的风格样式
        initToastStyle();
        //设置Toast的位置
        initLocation();

    }

    /**
     * 设置Toast的位置
     */
    private void initLocation() {
        scv_toast_location = (SettingClickView) findViewById(R.id.scv_toast_location);
        scv_toast_location.setTitle("归属地提示框的位置");
        scv_toast_location.setItem("设置归属地提示框的位置");
        scv_toast_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ToastLocationActivity.class));
            }
        });
    }

    /**
     * 显示Toast的风格样式
     */
    private void initToastStyle() {

        //初始化控件
        scv_toast_style = (SettingClickView) findViewById(R.id.scv_toast_style);

        //给控件赋值
        mToastStyle = new String[]{"透明", "橙色", "蓝色", "灰色", "绿色"};
        mStyle = SpUtils.getInt(getApplicationContext(), ConstanVlauel.TOAST_STYLE, 0);
        scv_toast_style.setTitle("设置归属地显示风格");
        scv_toast_style.setItem(mToastStyle[mStyle]);

        //监听点击事件，弹出对话框
        scv_toast_style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出对话框选择样式
                showToastStyleDialog();
            }
        });

    }

    /**
     * 弹出对话框选择
     */
    private void showToastStyleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.launch);
        builder.setTitle("请选择归属地样式");
        builder.setSingleChoiceItems(mToastStyle, mStyle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //把数据存储
                SpUtils.putInt(getApplicationContext(), ConstanVlauel.TOAST_STYLE, which);
                //显示到页面
                scv_toast_style.setItem(mToastStyle[which]);
                //关闭
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    /**
     * 是否显示归属地
     */
    private void initAddress() {
        siv_address = (SettingItemView) findViewById(R.id.siv_address);

        //获取服务的开启状态
        boolean serviceState = IsServiceRunning.getServiceState(this, "com.itsoha.mobilesafe.Service.AddressService");
        siv_address.setCheck(serviceState);

        siv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = siv_address.isCheck();
                siv_address.setCheck(!check);

                if (!check) {
                    boolean off = SpUtils.getBoolean(getApplicationContext(), ConstanVlauel.FLOATING_OFF, false);
                    Log.i(TAG, "initAddress: 服务的状态:--" + off);

                    if (off){
                        startService(new Intent(getApplicationContext(), AddressService.class));
                    }else {
                        Toast.makeText(SettingActivity.this, "请开启悬浮窗。", Toast.LENGTH_SHORT).show();
                        SpUtils.putBoolean(getApplicationContext(),ConstanVlauel.FLOATING_OFF,true);
                        setFloating();//开启悬浮窗权限
                    }

                } else {
                    //停止服务
                    stopService(new Intent(getApplicationContext(), AddressService.class));
                }
            }
        });
    }

    private void setFloating() {
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:" + "com.itsoha.mobilesafe"));
        startActivity(intent);

    }

    /**
     * 自动更新条目的方法
     */
    private void initUpdata() {

        final SettingItemView siv_updata = (SettingItemView) findViewById(R.id.siv_updata);

        boolean checkBoolean = SpUtils.getBoolean(this, ConstanVlauel.IS_CHECK, false);
        Log.i(TAG, "initUpdata: " + checkBoolean);
        siv_updata.setCheck(checkBoolean);
        siv_updata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = siv_updata.isCheck();
                Log.i(TAG, "onClick: " + !check);
                siv_updata.setCheck(!check);
                SpUtils.putBoolean(getApplication(), ConstanVlauel.IS_CHECK, !check);
            }
        });
    }
}
