package com.itsoha.mobilesafe.Bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2017/4/6.
 */

public class ProcessInfo {
    public String names;
    public String packageName;
    public Drawable icon;
    //应用已使用的内存数
    public long memSize;
    public boolean isCheck;
    public boolean isSystem;

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
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

    public long getMemSize() {
        return memSize;
    }

    public void setMemSize(long memSize) {
        this.memSize = memSize;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }
}
