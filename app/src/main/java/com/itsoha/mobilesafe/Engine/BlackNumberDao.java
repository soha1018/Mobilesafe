package com.itsoha.mobilesafe.Engine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.itsoha.mobilesafe.Bean.BlackNumerBen;
import com.itsoha.mobilesafe.SqliteDb.BlackNumberOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用黑名单的类，使用单列模式
 * Created by Administrator on 2017/3/25.
 */

public class BlackNumberDao {

    private final BlackNumberOpenHelper numberOpenHelper;
    private static BlackNumberDao numberDao;
    private String tableName  = "blacknumber";

    //1.私有化构造方法
    private BlackNumberDao(Context context){
        numberOpenHelper = new BlackNumberOpenHelper(context);
    }
    //2.声明一个当前类的对象
    private static BlackNumberDao blackNumberDao;
    //3.提供获取实例的方法
    public static BlackNumberDao getInstance(Context context){
        if (blackNumberDao == null){
            numberDao = new BlackNumberDao(context);
        }
        return numberDao;
    }

    /**
     * 往数据库存入数据
     * @param phone 手机号码
     * @param mode 模式（1.电话 2.短信 3.电话+短信）
     */
    public void insert(String phone,String mode){
        SQLiteDatabase db = numberOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone",phone);
        values.put("mode",mode);
        db.insert(tableName,null,values);

        db.close();
    }

    /**
     * 从黑名单删除不数据
     * @param phone 要删除的电话号码
     */
    public void delete(String phone){
        SQLiteDatabase db = numberOpenHelper.getWritableDatabase();

        db.delete(tableName,"phone = ?",new String[]{phone});

        db.close();

    }

    /**
     * 获取当前数据库中有多少数据
     * @return
     */
    public long getCount(){
        SQLiteDatabase db = numberOpenHelper.getReadableDatabase();
        int count = 0;
        Cursor cursor = db.rawQuery("select count(*) from blacknumber", null);
        while (cursor.moveToNext()){
            count = cursor.getInt(0);
        }
        db.close();
        return count;
    }
    /**
     * 根据电话号码更新屏蔽模式
     * @param phone
     * @param mode
     */
    public void updata(String phone,String mode){
        SQLiteDatabase db = numberOpenHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("mode",mode);
        db.update(tableName,values,"phone = ? ",new String[]{phone});

        db.close();
    }

    /**
     * 查询数据库中的数据
     * @return
     */
    public List<BlackNumerBen> query(){
        SQLiteDatabase db = numberOpenHelper.getReadableDatabase();

        List<BlackNumerBen> list = new ArrayList<>();
        BlackNumerBen blackNumerBen = null;

        Cursor cursor = db.query(tableName, new String[]{"phone", "mode"}, null, null, null, null, "_id desc");
        while (cursor.moveToNext()){
            blackNumerBen = new BlackNumerBen();
            blackNumerBen.setPhone(cursor.getString(0));
            blackNumerBen.setMode(cursor.getString(1));
            list.add(blackNumerBen);
        }

        cursor.close();

        db.close();
        return list;
    }

    /**
     * 使用分页查询数据库中的数据
     * @return
     */
    public List<BlackNumerBen> findPage(int index){
        SQLiteDatabase db = numberOpenHelper.getReadableDatabase();

        List<BlackNumerBen> list = new ArrayList<>();
        BlackNumerBen blackNumerBen = null;

        Cursor cursor = db.rawQuery("select * from blacknumber order by _id desc limit ?,20",new String[]{String.valueOf(index)});
        while (cursor.moveToNext()){
            blackNumerBen = new BlackNumerBen();
            blackNumerBen.setPhone(cursor.getString(1));
            blackNumerBen.setMode(cursor.getString(0));
            list.add(blackNumerBen);
        }

        cursor.close();

        db.close();
        return list;
    }

    public int findMode(String address){
        SQLiteDatabase db = numberOpenHelper.getReadableDatabase();
        Cursor query = db.query(tableName, new String[]{"mode"}, "phone = ?", new String[]{address}, null, null, null);
        int mode = 0;
        while (query.moveToNext()){
            mode = query.getInt(0);
        }
        query.close();
        db.close();

        return mode;
    }
}
