package com.itsoha.mobilesafe.Service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.itsoha.mobilesafe.Activity.EnterPsdActivity;
import com.itsoha.mobilesafe.Engine.AppLockDao;

import java.util.List;

/**
 * 程序锁的服务
 * Created by Administrator on 2017/4/17.
 */

public class WatchDogService extends Service {
    private boolean isDog;
    private AppLockDao mDao;
    private List<String> mPakList;
    private String mPakName;
    private InnerBroadcast mInnerBroadcast;
    private IntentFilter intentFilter;
    private MyObserver myObserver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //开启循环查询任务栈顶应用包名是否符合程序锁中的包名
        isDog = true;
        mDao = AppLockDao.getInstance(this);

        //获取数据库内容改变
        myObserver = new MyObserver(new Handler());
        getContentResolver().registerContentObserver(Uri.parse("content://applock/change"),true, myObserver);
        //开启循环查询任务栈顶应用包名是否符合程序锁中的包名
        watch();

        intentFilter = new IntentFilter("android.app.action.SKIP_LOCK");
        mInnerBroadcast = new InnerBroadcast();
        registerReceiver(mInnerBroadcast, intentFilter);
        super.onCreate();
    }

    /**
     * 开启循环查询任务栈顶应用包名是否符合程序锁中的包名
     */
    private void watch() {
        new Thread() {
            @Override
            public void run() {
                //获取数据库中程序锁的包名
                mPakList = mDao.findAll();
                while (isDog) {
                    //获取Activity的管理者
                    ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
                    //获取栈顶应用的包名
                    String packageName = runningTasks.get(0).topActivity.getPackageName();
                    //判断此包名和程序锁包名是否一致
                    if (mPakList.contains(packageName)) {
                        Log.i("Watch", "run: 符合要加锁的应用");
                        //判断此应用是否已经解锁
                        if (mPakName == null || !mPakName.equals(packageName)) {
                            Log.i("sss", "run: 开启界面");
                            Intent intent = new Intent(getApplicationContext(), EnterPsdActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("pakName", packageName);
                            startActivity(intent);
                        }

                    }

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        isDog = false;
        if (mInnerBroadcast!=null){
            unregisterReceiver(mInnerBroadcast);
        }
        if (myObserver!=null){
            getContentResolver().unregisterContentObserver(myObserver);
        }
        super.onDestroy();
    }

    /**
     * 广播接收者
     */
    private class InnerBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mPakName = intent.getStringExtra("pakName");
        }
    }

    /**
     * 内容观察者，内容发生改变的时候
     */
    private class MyObserver extends ContentObserver{
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            new Thread(){
                @Override
                public void run() {
                    mPakList = mDao.findAll();
                }
            }.start();
            super.onChange(selfChange);
        }
    }
}
