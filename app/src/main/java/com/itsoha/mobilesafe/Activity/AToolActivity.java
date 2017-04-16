package com.itsoha.mobilesafe.Activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.itsoha.mobilesafe.Engine.SmsBackup;
import com.itsoha.mobilesafe.R;

import java.io.File;


public class AToolActivity extends AppCompatActivity {
    private TextView tv_query_phone_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atool);

        //电话归属地查询方法
        initPhoneAddress();
        //备份短信的方法
        copySms();
        //常有电话号码查询
        initCommonNumber();
        //程序锁
        initLock();
    }

    /**
     * 程序锁
     */
    private void initLock() {
        TextView tv_atool_lock = (TextView) findViewById(R.id.tv_atool_lock);
        tv_atool_lock.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AppLockActivity.class));
            }
        });
    }

    /**
     * 常有电话号码查询
     */
    private void initCommonNumber() {
        TextView tv_atool_commonnumber = (TextView) findViewById(R.id.tv_atool_commonnumber);

        tv_atool_commonnumber.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),CommonNumberActivity.class));
            }
        });
    }

    /**
     * 备份短信
     */
    private void copySms() {
        TextView tv_copy_sms = (TextView) findViewById(R.id.tv_copy_sms);
        tv_copy_sms.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示进度条的对话框加载数据
                showProgressbarDialog();
            }
        });
    }

    /**
     * 显示进度条的对话框加载数据
     */
    private void showProgressbarDialog() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("短信备份");
        //设置进度条的样式
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        //调用的短信备份的方法，因为数据比较多是一个耗时的操作，所以要放大子线程中去完成
        new Thread() {
            @Override
            public void run() {
                //判断外置SD卡是否存在
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    //SD存在,就存入SD卡，此处的系统分隔符是为了适配不同的系统
                    String path = Environment.getExternalStorageDirectory().getPath() + File.separator + "Sms_copy.xml";
                    //调用自定义的方法备份短信，需要增加读权限
                    SmsBackup.backup(getApplicationContext(), path, new SmsBackup.callBack() {
                        @Override
                        public void setMax(int max) {
                            if (max == 0){
                                upToast("您没有任何短信内容");
                                return;
                            }else {
                                dialog.setMax(max);
                                upToast("短信备份完成");
                            }

                        }

                        @Override
                        public void setProgress(int current) {
                            dialog.setProgress(current);
                        }
                    });

                    dialog.dismiss();



                } else {
                    Toast.makeText(AToolActivity.this, "您的SD卡还没有安装，请先安装", Toast.LENGTH_SHORT).show();
                    return;
                }

                super.run();
            }
        }.start();
        //show出来
        dialog.show();
    }

    private void upToast(final String content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AToolActivity.this, content, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initPhoneAddress() {
        tv_query_phone_address = (TextView) findViewById(R.id.tv_query_phone_address);
        tv_query_phone_address.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), QueryAddressActivity.class));
            }
        });
    }
}
