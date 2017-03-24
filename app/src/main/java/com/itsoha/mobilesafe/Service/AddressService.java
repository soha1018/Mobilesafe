package com.itsoha.mobilesafe.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.itsoha.mobilesafe.Engine.AddressDao;
import com.itsoha.mobilesafe.R;
import com.itsoha.mobilesafe.Utils.ConstanVlauel;
import com.itsoha.mobilesafe.Utils.SpUtils;

/**
 * 归属地查询的Service类
 * Created by Administrator on 2017/3/18.
 */

public class AddressService extends Service {

    private static final String TAG = "AddressService";
    private TelephonyManager mTelephony;
    private MyPhoneStateListener mPhoneStateListener;
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private WindowManager mWM;
    private View mToast;
    private String mAddress;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            tv_toast.setText(mAddress);
            Log.i(TAG, "handleMessage: 地址："+mAddress);
        }
    };
    private TextView tv_toast;
    private int[] mDrawableIds;
    private int width;
    private int height;
    private InnerOutgoingCall innerOutgoingCall;

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
        Display defaultDisplay = mWM.getDefaultDisplay();
        width = defaultDisplay.getWidth();
        height = defaultDisplay.getHeight();

        //过滤打电话广播的条件
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);

        //广播接收者的类
        innerOutgoingCall = new InnerOutgoingCall();
        //注册广播接收者
        registerReceiver(innerOutgoingCall,intentFilter);

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

                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //当铃声响起的时候
                    Log.i(TAG, "onCallStateChanged: 正在通话中");
                    //弹出toast
                    showToast(incomingNumber);
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    /**
     * 打印吐司
     *
     * @param incomingNumber
     */
    private void showToast(String incomingNumber) {

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
        tv_toast = (TextView) mToast.findViewById(R.id.tv_toast);

        //获取在设置中定义的值
        params.x = SpUtils.getInt(getApplicationContext(), ConstanVlauel.TOAST_TOP, 0);
        params.y = SpUtils.getInt(getApplicationContext(), ConstanVlauel.TOAST_LEFT, 0);

        //在打电话中实现toast的触摸事件
        mToast.setOnTouchListener(new View.OnTouchListener() {
            public int startY;
            public int startX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //按下
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        Log.i(TAG, "onTouch: 按下的X：" + startX);

                        break;
                    case MotionEvent.ACTION_MOVE:
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();

                        int disX = moveX-startX;
                        int disY = moveY-startY;

                        params.x = params.x+disX;
                        params.y = params.y+disY;

                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.x > width - mToast.getWidth()) {
                            params.x = width - mToast.getWidth();
                        }
                        if (params.y > height - mToast.getHeight() - 22){
                            params.y = height - mToast.getHeight() - 22;
                        }

                        mWM.updateViewLayout(mToast,params);

                        //重置后坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        Log.i(TAG, "onTouch: 重置后的坐标" + startX);

                        break;
                    case MotionEvent.ACTION_UP:
                        //抬起 记录坐标位置
                        SpUtils.putInt(getApplicationContext(), ConstanVlauel.TOAST_LEFT, params.x);
                        SpUtils.putInt(getApplicationContext(), ConstanVlauel.TOAST_TOP, params.y);

                        break;
                }
                //返回True才会响应触摸事件
                return false;
            }
        });
        //从sp中获取色值文字的索引,匹配图片,用作展示
        mDrawableIds = new int[]{
                R.drawable.call_locate_white,
                R.drawable.call_locate_orange,
                R.drawable.call_locate_blue,
                R.drawable.call_locate_gray,
                R.drawable.call_locate_green};

        //拿到图片的索引
        int anInt = SpUtils.getInt(this, ConstanVlauel.TOAST_STYLE, 0);

        tv_toast.setBackgroundResource(mDrawableIds[anInt]);
        mWM.addView(mToast, params);

        //查询电话号码
        queryPhone(incomingNumber);

    }

    /**
     * 查询电话号码
     *
     * @param incomingNumber
     */
    private void queryPhone(final String incomingNumber) {
        new Thread() {
            @Override
            public void run() {
                Log.i(TAG, "run: 电话号码："+incomingNumber);
                mAddress = AddressDao.getAddress(incomingNumber);

                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    @Override
    public void onDestroy() {

        if (mTelephony != null && mPhoneStateListener != null) {
            mTelephony.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        if (innerOutgoingCall != null){
            unregisterReceiver(innerOutgoingCall);
        }
        Log.i(TAG, "onDestroy: 归属地服务销毁了");
        super.onDestroy();
    }

    /**
     * 拨电话的广播接收者
     */
    private class InnerOutgoingCall extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取拨打电话的号码数据
            String phone = getResultData();
            //弹出Toast去查询电话
            showToast(phone);
        }
    }
}
