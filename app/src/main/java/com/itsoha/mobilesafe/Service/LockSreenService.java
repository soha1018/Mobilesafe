package com.itsoha.mobilesafe.Service;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.itsoha.mobilesafe.Engine.ProcessProvider;

/**
 * Created by Administrator on 2017/4/8.
 */

public class LockSreenService extends Service{

    private IntentFilter intentFilter;
    private InnerReceiver innerReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //监听锁屏的广播
        intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);

        innerReceiver = new InnerReceiver();
        registerReceiver(innerReceiver,intentFilter);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (innerReceiver != null){
            unregisterReceiver(innerReceiver);
        }
        super.onDestroy();
    }

    private class InnerReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //清理手机正在运行的进程
            ProcessProvider.killAllProcess(context);
        }
    }
}
