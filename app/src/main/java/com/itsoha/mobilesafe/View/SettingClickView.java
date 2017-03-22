package com.itsoha.mobilesafe.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itsoha.mobilesafe.R;
import com.itsoha.mobilesafe.Utils.ConstanVlauel;
import com.itsoha.mobilesafe.Utils.SpUtils;

/**
 * 自定义控件组合，设置界面的控件
 * Created by Administrator on 2017/2/16.
 */

public class SettingClickView extends RelativeLayout {


    private TextView toast_style_title;
    private TextView toast_color;

    public SettingClickView(Context context) {
        this(context, null);
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //最后都肯定会调用第三个构造方法里面，在此加载布局文件
        View.inflate(context, R.layout.setting_click_view,this);

        toast_style_title = (TextView) findViewById(R.id.toast_systle_title);
        toast_color = (TextView) findViewById(R.id.toast_color);


    }

    /**
     * 设置标题的内容
     * @param title
     */
    public void setTitle(String title){
        toast_style_title.setText(title);
    }
    public void setItem(String item){
        toast_color.setText(item);
    }
}
