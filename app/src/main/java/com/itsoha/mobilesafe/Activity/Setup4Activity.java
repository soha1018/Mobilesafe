package com.itsoha.mobilesafe.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.itsoha.mobilesafe.R;
import com.itsoha.mobilesafe.Utils.ConstanVlauel;
import com.itsoha.mobilesafe.Utils.SpUtils;

/**
 * 手机防盗界面4
 * Created by Administrator on 2017/2/20.
 */
public class Setup4Activity extends Activity {

    private CheckBox cb_box_setup4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);


        initUi();


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

    //跳转到下一个界面
    public void nextPage(View view) {
        boolean aBoolean = SpUtils.getBoolean(this, ConstanVlauel.OPEN_SAFETY, false);
        if (aBoolean) {

            Intent intent = new Intent(this, SetupOverActivity.class);
            startActivity(intent);

            //告诉导航界面都设置完成了
            SpUtils.putBoolean(this, ConstanVlauel.SETUO_OVER, true);
            finish();
        } else {
            Toast.makeText(this, "请勾选手机防盗", Toast.LENGTH_SHORT).show();
        }
    }

    //跳转到上一页
    public void prePage(View view) {
        Intent intent = new Intent(this, Setup3Activity.class);
        startActivity(intent);
        finish();
    }
}
