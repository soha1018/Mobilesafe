package com.itsoha.mobilesafe.SqliteDb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/3/23.
 */

public class BlackNumberOpenHelper extends SQLiteOpenHelper {
    public BlackNumberOpenHelper(Context context) {
        super(context, "blacknumber.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table blacknumber (_id Integer primary key autoincrement,phone vachar(20),mode vachar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
