package com.itsoha.mobilesafe.Engine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

    private AppLockDao(Context context) {
        appLockOpenHelper = new AppLockOpenHelper(context);
    }

    private static AppLockDao appLockDao = null;

    private static AppLockDao getInstance(Context context){
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
    }

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
}
