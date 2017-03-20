package com.itsoha.mobilesafe.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.itsoha.mobilesafe.R;

/**
 * Created by Administrator on 2017/3/18.
 */

public class AddressService extends Service {

    private static final String TAG = "AddressService";
    private TelephonyManager mTelephony;
    private MyPhoneStateListener mPhoneStateListener;
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private WindowManager mWM;
    private View mToast;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //电话监听
        mTelephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        mPhoneStateListener = new MyPhoneStateListener();
        mTelephony.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        //获取窗体的管理者，把吐司添加到系统的窗体中
        mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        super.onCreate();
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    //电话空闲的状态
                    Log.i(TAG, "onCallStateChanged: 电话挂断了");
                    //从窗口清楚toas
                    if (mWM != null && mToast != null) {

                        mWM.removeView(mToast);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //当电话接通时的状态
                    Log.i(TAG, "onCallStateChanged: 正在通话中");

                    //弹出toast
                    showToast();
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //当铃声响起的时候
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    /**
     * 打印吐司
     */
    private void showToast() {

        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        params.gravity = Gravity.LEFT + Gravity.TOP;

        //把这个吐司添加到布局中去
        mToast = View.inflate(this, R.layout.toast_view, null);
        mWM.addView(mToast, params);

    }

    @Override
    public void onDestroy() {

        if (mTelephony != null && mPhoneStateListener != null) {
            mTelephony.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        super.onDestroy();
    }
}
