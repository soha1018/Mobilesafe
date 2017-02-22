package com.itsoha.mobilesafe.View;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 自定义控件，给Textview的走马灯效果获取焦点
 * Created by Administrator on 2017/2/15.
 */

public class FocusTextView extends TextView {
    public FocusTextView(Context context) {
        super(context);
    }

    public FocusTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
        return true;
    }

}
