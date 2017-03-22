package com.itsoha.mobilesafe.Activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.itsoha.mobilesafe.R;
import com.itsoha.mobilesafe.Utils.ConstanVlauel;
import com.itsoha.mobilesafe.Utils.SpUtils;

import static android.R.attr.left;

/**
 * toast的位置的activity
 * Created by Administrator on 2017/3/20.
 */

public class ToastLocationActivity extends Activity {

    private static final String TAG = "ToastLocationActivity";
    private ImageView drag;
    private Button toast_location_top;
    private Button toast_location_bottom;
    private int width;
    private int height;
    private long[] mTimeArray = new long[2];


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast_location);

        //找到我们的控件 并监听触摸事件
        initView();
    }

    /**
     * 初始化Ui
     */
    private void initView() {


        drag = (ImageView) findViewById(R.id.drag);
        toast_location_top = (Button) findViewById(R.id.toast_location_top);
        toast_location_bottom = (Button) findViewById(R.id.toast_location_bottom);
        //获取系统窗口的信息
        WindowManager windowManage = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        Display defaultDisplay = windowManage.getDefaultDisplay();
        width = defaultDisplay.getWidth();
        height = defaultDisplay.getHeight();

        //显示上次坐标的位置
        int Pretop = SpUtils.getInt(getApplicationContext(), ConstanVlauel.TOAST_TOP, 0);
        int Preleft = SpUtils.getInt(getApplicationContext(), ConstanVlauel.TOAST_LEFT, 0);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.leftMargin = Preleft;
        layoutParams.topMargin = Pretop;

        drag.setLayoutParams(layoutParams);

        if (Pretop > height / 2) {
            toast_location_top.setVisibility(View.VISIBLE);
            toast_location_bottom.setVisibility(View.INVISIBLE);
        } else {
            toast_location_bottom.setVisibility(View.VISIBLE);
            toast_location_top.setVisibility(View.INVISIBLE);
        }

        //双击图片就举证显示
        drag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //每点击一次就把第二个数组元素拷贝到第一个，并把当前的时间给了数组最后一个元素
                System.arraycopy(mTimeArray,1,mTimeArray,0,mTimeArray.length - 1);
                mTimeArray[mTimeArray.length - 1] = System.currentTimeMillis();
                if (mTimeArray[mTimeArray.length-1]  - mTimeArray[0] < 500 ){
                    Log.i(TAG, "onClick: 少年你成功了");
                    Log.i(TAG, "onClick: "+"left = "+width / 2+"-"+drag.getWidth() / 2);
                    int left = width / 2 - drag.getWidth() / 2;
                    int top = height / 2 - drag.getHeight() / 2;
                    int right = width / 2 + drag.getWidth() / 2;
                    int bottom = height / 2 + drag.getHeight() / 2;

                    drag.layout(left,top,right,bottom);

                    //存储位置
                    SpUtils.putInt(getApplicationContext(), ConstanVlauel.TOAST_LEFT, drag.getLeft());
                    SpUtils.putInt(getApplicationContext(), ConstanVlauel.TOAST_TOP, drag.getTop());
                }
            }
        });

        //监听ImageView的触摸事件
        drag.setOnTouchListener(new View.OnTouchListener() {
            private int startX;
            private int startY;

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
                        //移动
                        int entX = (int) event.getRawX();
                        int entY = (int) event.getRawY();

                        int desX = entX - startX;
                        int desY = entY - startY;

                        int left = drag.getLeft()+desX;
                        int top = drag.getTop()+desY;
                        int right = drag.getRight()+desX;
                        int bottom = drag.getBottom()+desY;


                        //四边不能超出屏幕的宽度
                        if (left < 0 || top < 0 || right > width || bottom > height - 22) {
                            return true;
                        }

                        //设置Button的显示位置
                        Log.i(TAG, "onTouch: "+top+"-----"+height / 2);
                        if (top > height / 2) {
                            toast_location_top.setVisibility(View.VISIBLE);
                            toast_location_bottom.setVisibility(View.INVISIBLE);
                        } else {
                            toast_location_bottom.setVisibility(View.VISIBLE);
                            toast_location_top.setVisibility(View.INVISIBLE);
                        }

                        //设置图片移动后的坐标
                        drag.layout(left, top, right, bottom);

                        //重置后坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        Log.i(TAG, "onTouch: 重置后的坐标" + startX);

                        break;
                    case MotionEvent.ACTION_UP:
                        //抬起 记录坐标位置
                        SpUtils.putInt(getApplicationContext(), ConstanVlauel.TOAST_LEFT, drag.getLeft());
                        SpUtils.putInt(getApplicationContext(), ConstanVlauel.TOAST_TOP, drag.getTop());

                        break;
                }
                //返回True才会响应触摸事件
                return false;
            }
        });
    }
}
