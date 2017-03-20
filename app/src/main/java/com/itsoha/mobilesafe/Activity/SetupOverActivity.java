package com.itsoha.mobilesafe.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.itsoha.mobilesafe.R;
import com.itsoha.mobilesafe.Utils.ConstanVlauel;
import com.itsoha.mobilesafe.Utils.SpUtils;

/**
 * 手机防盗的界面
 * Created by Administrator on 2017/2/17.
 */
public class SetupOverActivity extends Activity{

    private TextView tv_phone_over;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean setuo_over = SpUtils.getBoolean(getApplicationContext(), ConstanVlauel.SETUO_OVER, false);
        if (setuo_over){
            //密码输入成功，并且四个界面设置完成，停留再设置完成的界面
            setContentView(R.layout.activity_setup_over);
            //初始化数据
            initUi();
        }else {
            //密码输入成功，四个导航界面没有设置完成-----停留在第一个界面
            Intent intent = new Intent(this, Setup1Activity.class);
            startActivity(intent);

            finish();
        }



    }

    /**
     * 更新ui
     */
    private void initUi() {
        tv_phone_over = (TextView) findViewById(R.id.tv_phone_over);

        //设置已存入的安全号码
        String phone = SpUtils.getString(this, ConstanVlauel.CONTACT_PHONE, "");
        if (!TextUtils.isEmpty(phone)){
            tv_phone_over.setText(phone);
        }


        //点击重新进入防盗向导
        TextView tv_reset_setup = (TextView) findViewById(R.id.tv_reset_setup);

        tv_reset_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
                startActivity(intent);

                finish();
            }
        });
    }


}
