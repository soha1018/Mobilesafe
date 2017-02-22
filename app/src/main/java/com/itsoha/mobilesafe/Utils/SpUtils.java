package com.itsoha.mobilesafe.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 记录CheckBox的状态
 * Created by Administrator on 2017/2/16.
 */

public class SpUtils {

    private static SharedPreferences sp;

    /**
     * 更新设置开关的写入的方法
     * @param con 上下文环境
     * @param key 存储节点名称
     * @param values 存储节点的值
     */
    public static void putBoolean(Context con, String key, boolean values) {
        if (sp == null) {
            sp = con.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key,values).commit();
    }

    /**
     * 更新设置开关的读取的方法
     * @param con 上下文环境
     * @param key 存储节点名称
     * @param defValues 没有此节点默认值
     * @return 返回true代表之前已经选中，反之代表未选中
     */
    public static boolean getBoolean(Context con,String key,boolean defValues){
        if (sp == null) {
            sp = con.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key,defValues);
    }

    /**
     * 设置密码的写入方法
     * @param con 上下文环境
     * @param key 存储节点名称
     * @param values 存储节点的值
     */
    public static void putString(Context con, String key, String values) {
        if (sp == null) {
            sp = con.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putString(key,values).commit();
    }

    /**
     * 更新设置开关的读取的方法
     * @param con 上下文环境
     * @param key 存储节点名称
     * @param defValues 没有此节点默认值
     * @return 返回true代表之前已经选中，反之代表未选中
     */
    public static String getString(Context con,String key,String defValues){
        if (sp == null) {
            sp = con.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getString(key,defValues);
    }

    /**
     * 删除SP的节点
     * @param applicationContext  上下文环境
     * @param simNumber  节点名称
     */
    public static void remove(Context applicationContext, String simNumber) {
        if (sp == null) {
            sp = applicationContext.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().remove(simNumber).commit();

    }
}
