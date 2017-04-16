package com.itsoha.mobilesafe.Activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.itsoha.mobilesafe.R;

/**
 * 程序锁的界面
 * Created by Administrator on 2017/4/16.
 */

public class AppLockActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applock);
    }
}
