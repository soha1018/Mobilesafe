package com.itsoha.mobilesafe.Engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询常用数据库的类
 * Created by Administrator on 2017/4/9.
 */

public class CommonNumDao {
    private String path = "data/data/com.itsoha.mobilesafe/files/commonnum.db";

    /**
     * 查询数据库
     */

    public List<Group> getGroup(){
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        List<Group> list = new ArrayList<>();
        Cursor cursor = db.query("classlist", new String[]{"name", "idx"}, null, null, null, null, null);
        while (cursor.moveToNext()){
            Group group = new Group();
            group.name = cursor.getString(0);
            group.idx = cursor.getString(1);
            group.list = getTable(cursor.getString(1));
            list.add(group);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 查询各个表中的数据
     * @param idx 表名后缀的索引
     */
    private List<Table> getTable(String idx){
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        List<Table> tableList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from table" + idx + ";", null);
        while (cursor.moveToNext()){
            Table table = new Table();
            table._id = cursor.getString(0);
            table.number = cursor.getString(1);
            table.name = cursor.getString(2);
            tableList.add(table);
        }
        cursor.close();
        db.close();

        return tableList;
    }

    public class Table{
        public String _id;
        public String number;
        public String name;
    }

    public class Group{
        public String name;
        public String idx;
        public List<Table> list;
    }
}
