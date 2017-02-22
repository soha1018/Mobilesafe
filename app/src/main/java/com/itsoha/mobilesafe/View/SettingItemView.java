package com.itsoha.mobilesafe.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itsoha.mobilesafe.R;
import com.itsoha.mobilesafe.Utils.SpUtils;

/**
 * 自定义控件组合，设置界面的控件
 * Created by Administrator on 2017/2/16.
 */

public class SettingItemView extends RelativeLayout {

    public static final String NAMESPACE = "http://schemas.android.com/apk/res-auto";
    private CheckBox cb_box;
    private TextView tv_dex;
    private String mdestitle;
    private String mdesoff;
    private String mdeson;

    public SettingItemView(Context context) {
        this(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //最后都肯定会调用第三个构造方法里面，在此加载布局文件
        View.inflate(context, R.layout.setting_item_view,this);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_dex = (TextView) findViewById(R.id.tv_des);
        cb_box = (CheckBox) findViewById(R.id.cb_box);

        //初始化属性集
        initAttrs(attrs);

        tv_title.setText(mdestitle);

    }


    /**
     * 初始化属性集
     * @param attrs 属性的集合
     */
    private void initAttrs(AttributeSet attrs) {

        //根据命名空间和属性的名称去获取属性的值
        mdestitle = attrs.getAttributeValue(NAMESPACE, "destitle");
        mdesoff = attrs.getAttributeValue(NAMESPACE, "desoff");
        mdeson = attrs.getAttributeValue(NAMESPACE, "deson");
    }

    /**
     * 查看CheckBox是否旋选中
     * @return 返回true代表选中，反之代表为选中
     */
    public boolean isCheck(){
        return cb_box.isChecked();
    }

    /**
     * 设置CheckBox的选中状态，并且改变TextView的数据
     * @param check
     */
    public void setCheck(boolean check){
        cb_box.setChecked(check);
        if (check){
            tv_dex.setText(mdesoff);
        }else {
            tv_dex.setText(mdeson);
        }
    }
}
