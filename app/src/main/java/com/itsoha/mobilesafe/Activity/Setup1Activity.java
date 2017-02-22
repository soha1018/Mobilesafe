package com.itsoha.mobilesafe.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.itsoha.mobilesafe.R;

/**
 * Created by Administrator on 2017/2/18.
 */
public class Setup1Activity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }

    //跳转到下一个界面
    public void nextPage(View view){
        Intent intent = new Intent(this, Setup2Activity.class);
        startActivity(intent);

        finish();
    }
}
