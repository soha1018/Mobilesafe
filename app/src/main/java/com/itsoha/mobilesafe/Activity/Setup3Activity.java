package com.itsoha.mobilesafe.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.itsoha.mobilesafe.R;

/**
 * Created by Administrator on 2017/2/20.
 */
public class Setup3Activity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
    }

    //跳转到下一个界面
    public void nextPage(View view){
        Intent intent = new Intent(this, Setup4Activity.class);
        startActivity(intent);

        finish();
    }
    //跳转到上一页
    public void prePage(View view){
        Intent intent = new Intent(this, Setup2Activity.class);
        startActivity(intent);
        finish();
    }
}
