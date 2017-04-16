package com.itsoha.mobilesafe.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.itsoha.mobilesafe.Engine.ProcessProvider;

/**
 * 接收窗体小部件，清理进程的广播
 * Created by Administrator on 2017/4/15.
 */

public class KillProcessReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ProcessProvider.killAllProcess(context);
    }
}
