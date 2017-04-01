package com.itsoha.mobilesafe.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import com.itsoha.mobilesafe.R;


/**
 * 开启小火箭的服务
 * Created by Administrator on 2017/3/23.
 */

public class RocketService extends Service {
    private static final String TAG = "RocketService";
    private WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private View viewRocket;
    private ImageView iv_rocket;
    private WindowManager mWM;
    private int mWidth;
    private int mHeight;
    private ImageView smoke_top;
    private ImageView smoke_bottom;
    private WindowManager.LayoutParams params;
    private View inflate;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    //更新小火箭的Y轴
                    params.y = (int) msg.obj;
                    mWM.updateViewLayout(viewRocket, params);

                    if (params.y == 0){
                        mWM.removeView(viewRocket);
                        Message message = new Message();
                        message.what = 5;
                        handler.sendMessageDelayed(message,1500);
                    }
                    break;
                case 3:
                    Log.i(TAG, "handleMessage: 移除火箭尾气");
                    mWM.removeView(inflate);
                    break;
                case 5:
                    //升空完成以后再把视图添加回来
                    params.x = 0;
                    params.y = 0;
                    mWM.addView(viewRocket,params);
                    break;
            }
        }
    };



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        //获取系统服务的窗口
        mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = mWM.getDefaultDisplay();
        mHeight = display.getHeight();
        mWidth = display.getWidth();
        //小火箭
        showRocket();

        super.onCreate();

    }

    /**
     * 初始化小火箭
     */
    private void showRocket() {

        //来一个小火箭
        params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //显示的位置
        params.gravity = Gravity.LEFT + Gravity.TOP;

        viewRocket = View.inflate(this, R.layout.rocket_view, null);

        //初始化小火箭的动画
        iv_rocket = (ImageView) viewRocket.findViewById(R.id.iv_rocket);
        AnimationDrawable rocket = (AnimationDrawable) iv_rocket.getBackground();
        rocket.start();

        //设置小火箭的退拽效果，对触摸事件进行监听
        iv_rocket.setOnTouchListener(new View.OnTouchListener() {

            private int startX;
            private int startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //记录开始按下的位置
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();

                        int disX = moveX - startX;
                        int disY = moveY - startY;

                        params.x = params.x + disX;
                        params.y = params.y + disY;

                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.x > mWidth - viewRocket.getWidth()) {
                            params.x = mWidth - viewRocket.getWidth();
                        }
                        if (params.y > mHeight - viewRocket.getHeight() - 10) {
                            params.y = mHeight - viewRocket.getHeight() - 10;
                        }

                        //刷新窗口
                        mWM.updateViewLayout(viewRocket, params);

                        //在第一次移动完成后,将最终坐标作为第二次移动的起始坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:

                        //在指定的区域松开手,小火箭升天
                        if (params.x > 100 && params.x < 350 && params.y > (mWidth + iv_rocket.getHeight()/2)) {

                            //发射小火箭
                            sendRocket();

                            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                            layoutParams.format = PixelFormat.TRANSLUCENT;
                            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                            layoutParams.setTitle("Toast");
                            layoutParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                            //显示的位置
                            layoutParams.gravity = Gravity.BOTTOM;

                            //对尾气的窗口进行初始化
                            inflate = View.inflate(getApplicationContext(), R.layout.rocket_background_view, null);
                            smoke_top = (ImageView) inflate.findViewById(R.id.smoke_top);
                            smoke_bottom = (ImageView) inflate.findViewById(R.id.smoke_bottom);
                            AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
                            alphaAnimation.setDuration(500);
                            smoke_top.startAnimation(alphaAnimation);
                            smoke_bottom.startAnimation(alphaAnimation);
                            //松开小火箭的时候出现尾气
                            mWM.addView(inflate,layoutParams);

                            //添加之后，只有在其不等于空的时候延迟发送消息，清空尾气视图
                            if (mWM != null && inflate != null) {
                                handler.sendEmptyMessageDelayed(3,1500);
                            }
                        }

                        break;
                }
                return true;
            }
        });

        Log.i(TAG, "showRocket: 添加小火箭视图");
        mWM.addView(viewRocket, params);

    }

    /**
     * 发送小火箭升空
     */
    private void sendRocket() {
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 11; i++) {
                    int y = 550 - i * 55;
                    SystemClock.sleep(40);

                    //发送消息
                    Message message = new Message();
                    message.what = 1;
                    message.obj = y;
                    handler.sendMessage(message);
                }
                super.run();
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        if (mWM != null && viewRocket != null ) {
            mWM.removeView(viewRocket);
        }
        super.onDestroy();
    }
}
