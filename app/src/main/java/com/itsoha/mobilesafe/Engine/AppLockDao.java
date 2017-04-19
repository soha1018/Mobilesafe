package com.itsoha.mobilesafe.Engine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.itsoha.mobilesafe.SqliteDb.AppLockOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 对程序锁数据库操作的类
 * Created by Administrator on 2017/4/16.
 */

public class AppLockDao {
    private AppLockOpenHelper appLockOpenHelper;
    private String tableName = "applock";
    private Context context;

    private AppLockDao(Context context) {
        this.context = context;
        appLockOpenHelper = new AppLockOpenHelper(context);
    }

    private static AppLockDao appLockDao = null;

    public static AppLockDao getInstance(Context context){
        if (appLockDao == null){
            appLockDao = new AppLockDao(context);
        }
        return appLockDao;
    }

    /**
     * 向数据库插入数据
     * @param pakName 包名
     */
    public void Insert(String pakName){
        SQLiteDatabase db = appLockOpenHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("pakName",pakName);
        db.insert(tableName,null,values);
        db.close();

        //获取内容接收者，发送数据改变的消息
        context.getContentResolver().notifyChange(Uri.parse("content://applock/change"),null);
    }

    /**
     * 根据包名查找数据
     * @param pakName 包名
     * @return
     */
    public List<String> Query(String pakName){
        SQLiteDatabase db = appLockOpenHelper.getReadableDatabase();
        List<String> list = new ArrayList<>();
        Cursor query = db.query(tableName, new String[]{"pakName"}, "pakName = ?", new String[]{pakName}, null, null, null);
        while (query.moveToNext()){
            list.add(query.getString(0));
        }
        query.close();
        db.close();
        return list;
    }

    /**
     * 查询所有应用的包名
     * @return 返回所有应用的集合
     */
    public List<String> findAll(){
        SQLiteDatabase db = appLockOpenHelper.getReadableDatabase();
        List<String> list = new ArrayList<>();
        Cursor query = db.query(tableName, null, null, null, null, null, null);
        while (query.moveToNext()){
            list.add(query.getString(1));
        }
        query.close();
        db.close();
        return list;
    }

    /***
     * 根据包名删除数据
     * @param pakName 包名
     */
    public void delete(String pakName){
        SQLiteDatabase db = appLockOpenHelper.getReadableDatabase();
        db.delete(tableName,"pakName = ?",new String[]{pakName});
        db.close();
        //获取内容接收者，发送数据改变的消息
        context.getContentResolver().notifyChange(Uri.parse("content://applock/change"),null);
    }
}
