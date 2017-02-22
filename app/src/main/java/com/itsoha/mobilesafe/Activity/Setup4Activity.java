package com.itsoha.mobilesafe.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.itsoha.mobilesafe.R;
import com.itsoha.mobilesafe.Utils.ConstanVlauel;
import com.itsoha.mobilesafe.Utils.SpUtils;

/**
 * Created by Administrator on 2017/2/20.
 */
public class Setup4Activity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
    }

    //跳转到下一个界面
    public void nextPage(View view){
        Intent intent = new Intent(this, SetupOverActivity.class);
        startActivity(intent);

        //告诉导航界面都设置完成了
        SpUtils.putBoolean(this, ConstanVlauel.SETUO_OVER,true);
        finish();
    }
    //跳转到上一页
    public void prePage(View view){
        Intent intent = new Intent(this, Setup3Activity.class);
        startActivity(intent);
        finish();
    }
}
