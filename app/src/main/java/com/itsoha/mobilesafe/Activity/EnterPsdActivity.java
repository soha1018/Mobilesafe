package com.itsoha.mobilesafe.Activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itsoha.mobilesafe.R;

/**
 * 程序锁输入密码的界面
 * Created by Administrator on 2017/4/17.
 */

public class EnterPsdActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * 拦截应用的名称
     */
    private TextView tv_app_name;
    private ImageView iv_app_icon;
    /**
     * 请输入解锁密码
     */
    private EditText et_psd;
    /**
     * 提交
     */
    private Button bt_submit;
    private String pakName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pakName = getIntent().getStringExtra("pakName");
        setContentView(R.layout.activity_enter_psd);
        //初始化控件
        initView();
        //初始化数据
        initData();

    }

    /**
     * 初始化数据
     */
    private void initData() {
        //获取应用程序的信息
        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo info = pm.getApplicationInfo(pakName, 0);
            Drawable drawable = info.loadIcon(pm);
            iv_app_icon.setBackgroundDrawable(drawable);
            tv_app_name.setText(info.loadLabel(pm));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        tv_app_name = (TextView) findViewById(R.id.tv_app_name);
        iv_app_icon = (ImageView) findViewById(R.id.iv_app_icon);
        et_psd = (EditText) findViewById(R.id.et_psd);
        bt_submit = (Button) findViewById(R.id.bt_submit);
        bt_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_submit:
                //点击提交按钮比对密码
                String pwd = et_psd.getText().toString().trim();
                if (!TextUtils.isEmpty(pwd)){
                    if (pwd.equals("123")){
                        //密码输入正确以后发送广播，告诉看门狗不需要再去监听此应用
                        Intent intent = new Intent("android.app.action.SKIP_LOCK");
                        intent.putExtra("pakName",pakName);
                        sendBroadcast(intent);
                        //关闭当前界面
                        finish();
                    }else {
                        Toast.makeText(this, "密码错误请重新输入", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //退出到桌面
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        super.onBackPressed();
    }
}
