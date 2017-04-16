package com.itsoha.mobilesafe.SqliteDb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 程序锁的数据库
 * Created by Administrator on 2017/4/16.
 */

public class AppLockOpenHelper extends SQLiteOpenHelper {
    public AppLockOpenHelper(Context context) {
        super(context, "AppLock.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table applock(_id Integer primary key autoincrement,pakName vachar(40))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
