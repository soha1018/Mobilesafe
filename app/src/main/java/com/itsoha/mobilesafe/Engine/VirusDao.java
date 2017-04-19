package com.itsoha.mobilesafe.Engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * 对病毒数据库做查询的操作
 * Created by Administrator on 2017/4/17.
 */
public class VirusDao {
    public static String path = "data/data/com.itsoha.mobilesafe/files/antivirus.db";

    /**
     * 获取数据库中的MD5数据
     * @return 返回携带MD5数据的集合
     */
    public static List<String> getVirusList(){
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        List<String> virusList = new ArrayList<>();
        Cursor cursor = db.query("datable", new String[]{"md5"}, null, null, null, null, null);
        while (cursor.moveToNext()){
            virusList.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return virusList;
    }
}
