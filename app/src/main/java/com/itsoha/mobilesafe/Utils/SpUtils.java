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
    /**
     * 写入boolean变量至sp中
     * @param ctx	上下文环境
     * @param key	存储节点名称
     * @param value	存储节点的值string
     */
    public static void putInt(Context ctx,String key,int value){
        //(存储节点文件名称,读写方式)
        if(sp == null){
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putInt(key, value).commit();
    }
    /**
     * 读取boolean标示从sp中
     * @param ctx	上下文环境
     * @param key	存储节点名称
     * @param defValue	没有此节点默认值
     * @return		默认值或者此节点读取到的结果
     */
    public static int getInt(Context ctx,String key,int defValue){
        //(存储节点文件名称,读写方式)
        if(sp == null){
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getInt(key, defValue);
    }
}
