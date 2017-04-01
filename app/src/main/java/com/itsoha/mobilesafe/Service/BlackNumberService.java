package com.itsoha.mobilesafe.Service;

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
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.itsoha.mobilesafe.Engine.BlackNumberDao;

import java.lang.reflect.Method;

import static android.content.ContentValues.TAG;

/**
 * 黑名单拦截的服务
 * Created by Administrator on 2017/3/28.
 */

public class BlackNumberService extends Service {

    private BlackNumberDao mNumberDao;
    private MyContentObserver mContentObserver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mNumberDao = BlackNumberDao.getInstance(getApplicationContext());

        //对短信的拦截
        initSmsListener();
        //对电话的拦截
        initPhoneListener();
        super.onCreate();
    }

    /**
     * 对电话的拦截
     */
    private void initPhoneListener() {
        TelephonyManager pManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        pManager.listen(new PhoneState(), PhoneStateListener.LISTEN_CALL_STATE);


    }

    private class PhoneState extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    //响铃的时候对电话进行过滤
//                    return ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
                    int mode = mNumberDao.findMode(incomingNumber);
                    if (mode == 1 || mode == 3){
                        //当电话号码符合拦截的条件的时候
                        endCall(incomingNumber);
                    }
                    break;

            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    /**
     * 拦截电话号码并且在通话记录中删除记录
     * @param incomingNumber
     */
    private void endCall(String incomingNumber) {
        try {
            //通过反射调用安卓的类以及它的方法
            Class<?> clazz = Class.forName("android.os.ServiceManager");
            //获取方法
            Method method = clazz.getMethod("getService", String.class);
            //反射调用此方法
            IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
            //断开电话
            ITelephony.Stub.asInterface(iBinder).endCall();

            //通过内容观察者查看数据是否发生变化，发生改变后再对其中的数据全部删除
            mContentObserver = new MyContentObserver(new Handler(), incomingNumber);
            getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"),true,mContentObserver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 对短信的拦截
     */
    private void initSmsListener() {
        IntentFilter intentFilter = new IntentFilter();
        //短信广播的接收者
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        //设置这个广播接收者的优先级是最高
        intentFilter.setPriority(Integer.MAX_VALUE);
        //广播接收者的类
        SmsBroadcastReceiver smsBroadcastReceiver = new SmsBroadcastReceiver();
        registerReceiver(smsBroadcastReceiver, intentFilter);
    }

    //继承广播接收者的类
    private class SmsBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");

            for (Object object : pdus
                    ) {
                //获取短信*消息*
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) object);
                //获取短信的地址
                String address = smsMessage.getOriginatingAddress();
                Log.i("拦截的短信", "onReceive: 短信的地址：" + address);

                //根据短信的号码去查拦截的模式
                int mode = mNumberDao.findMode(address);
                if (mode == 2 || mode == 3) {
                    Log.i(TAG, "onReceive: 开始终止广播");
                    //终止广播
                    abortBroadcast();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        //注销内容提供者
        if (mContentObserver != null){
            getContentResolver().unregisterContentObserver(mContentObserver);
        }
        super.onDestroy();
    }

    /**
     * 内容观察者的类，用于检测数据库中的内容发生改变所做的操作
     */
    private class MyContentObserver extends ContentObserver{
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         * @param incomingNumber
         */
        private String phone;
        public MyContentObserver(Handler handler, String incomingNumber) {
            super(handler);
            this.phone = incomingNumber;
        }

        @Override
        public void onChange(boolean selfChange) {
            //删除数据需要添加读写权限
            getContentResolver().delete(Uri.parse("content://call_log/calls"),"number = ?",new  String[]{phone});
            super.onChange(selfChange);
        }
    }
}
