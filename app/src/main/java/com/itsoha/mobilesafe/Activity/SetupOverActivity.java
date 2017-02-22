package com.itsoha.mobilesafe.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.itsoha.mobilesafe.R;
import com.itsoha.mobilesafe.Utils.ConstanVlauel;
import com.itsoha.mobilesafe.Utils.SpUtils;

/**
 * 手机防盗的界面
 * Created by Administrator on 2017/2/17.
 */
public class SetupOverActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean setuo_over = SpUtils.getBoolean(getApplicationContext(), ConstanVlauel.SETUO_OVER, false);
        if (setuo_over){
            //密码输入成功，并且四个界面设置完成，停留再设置完成的界面
            setContentView(R.layout.activity_setup_over);
        }else {
            //密码输入成功，四个导航界面没有设置完成-----停留在第一个界面
            Intent intent = new Intent(this, Setup1Activity.class);
            startActivity(intent);
        }
    }
}
