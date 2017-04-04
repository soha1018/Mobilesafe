package com.itsoha.mobilesafe.Bean;

import android.graphics.drawable.Drawable;

/**
 * 应用信息的Bean类
 * Created by Administrator on 2017/4/2.
 */

public class AppInfo {
    //应用名称
    private String name;
    //包名
    private String packageName;
    //图标
    private Drawable icon;
    //内存应用还是SD卡
    private boolean isSDCard;
    //检测是系统应用还是SD卡
    private boolean isSystem;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isSDCard() {
        return isSDCard;
    }

    public void setSDCard(boolean SDCard) {
        isSDCard = SDCard;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }
}
