package com.itsoha.mobilesafe.Service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.itsoha.mobilesafe.Engine.ProcessProvider;
import com.itsoha.mobilesafe.R;
import com.itsoha.mobilesafe.Receiver.WidgetProvider;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 创建以及对窗体小部件更新的服务
 * Created by Administrator on 2017/4/15.
 */

public class UpdateWidgetService extends Service {

    private Timer timer;
    private InnerReceiver mInnerReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //定时器每秒钟去更新的数据
        startTimer();

        //接收锁屏和解锁的广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        mInnerReceiver = new InnerReceiver();
        registerReceiver(mInnerReceiver, intentFilter);
        super.onCreate();
    }

    /**
     * 定时器每秒钟去更新的数据
     */
    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //更新窗体的内容
                updateAppwidget();
            }
        }, 0, 5000);
    }

    /**
     * 更新窗体的内容
     */
    private void updateAppwidget() {
        //先获取窗体小部件的管理者
        AppWidgetManager aWm = AppWidgetManager.getInstance(this);
        //更新窗体小部件
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
        views.setTextViewText(R.id.process_count, "线程总数:" + ProcessProvider.getProcessCount(this));
        String strAvail = Formatter.formatFileSize(this, ProcessProvider.getAvailSpace(this));
        views.setTextViewText(R.id.process_memory, "可用内存:" + strAvail);

        //点击窗口跳转到应用程序
        Intent intent = new Intent("android.intent.action.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0, new Intent[]{intent},
                PendingIntent.FLAG_CANCEL_CURRENT);
        views.setOnClickPendingIntent(R.id.process_linear, pendingIntent);

        //点击清理按钮
        Intent killIntent = new Intent("android.appwidget.action.KILLPROCESSRECEIVER");
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, killIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        views.setOnClickPendingIntent(R.id.btn_clear, broadcast);
        //窗体小部件的字节码文件
        ComponentName provider = new ComponentName(this, WidgetProvider.class);
        aWm.updateAppWidget(provider, views);
    }

    @Override
    public void onDestroy() {
        if (mInnerReceiver!=null){
            unregisterReceiver(mInnerReceiver);
        }
        cancelTimer();
        super.onDestroy();
    }

    /**
     * 接收锁屏和解锁的广播
     */
    private class InnerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                //开启更新
                startTimer();
            } else {
                //关闭更新
                cancelTimer();
            }
        }
    }

    /**
     * 关闭更新
     */
    private void cancelTimer() {
        if (timer!=null){
            timer.cancel();
            timer =null;
        }
    }
}
