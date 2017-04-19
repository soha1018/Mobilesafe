package com.itsoha.mobilesafe.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Activity的基类
 * Created by Administrator on 2017/2/26.
 */

public abstract class BaseSetupActivity extends Activity {

    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //手势探测器的侦听器
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getX()-e2.getX()<0){
                    //上一页滑动

                    showPrePage();
                }else if (e1.getX()-e2.getX()>0){
                    //下一页滑动
                    showNextPage();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }

        });
    }

    /**
     * 下一页的滑动
     */
    public abstract void showNextPage();

    /**
     * 上一页的滑动
     */
    public abstract void showPrePage();

    public void nextPage(View view){
        showNextPage();
    }

    public void prePage(View view){
        showPrePage();
    }

    /**
     * 调用一个触摸屏事件并非由任何观点。这是最有用的处理触摸事件发生你的窗外,没有得到它。
     * @param event ：参数event为手机屏幕触摸事件封装类的对象，其中封装了该事件的所有信息，例如触摸的位置、触摸的类型以及触摸的时间等。
     *              该对象会在用户触摸手机屏幕时被创建。
     * @return 该方法的返回值机理与键盘响应事件的相同，同样是当已经完整地处理了该事件且不希望其他回调方法再次处理时返回true，否则返回false。
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
