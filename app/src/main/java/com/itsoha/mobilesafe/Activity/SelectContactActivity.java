package com.itsoha.mobilesafe.Activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.itsoha.mobilesafe.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/2/24.
 */

public class SelectContactActivity extends Activity {
    private static final String TAG = "SelectContactActivity";
    private List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //填充数据适配器
            mAdapter = new MyAdapter();
            lv_select_contact.setAdapter(mAdapter);
        }
    };
    private ListView lv_select_contact;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        initUi();

        initData();

    }

    /**
     * 初始化数据
     */
    private void initData() {
        //查询数据可能是一个费时的操作，放到子线程中完成
        new Thread() {
            @Override
            public void run() {
                //先把之前的数据清除
                list.clear();
                ContentResolver resolver = getContentResolver();
                Cursor query = resolver.query(Uri.parse("content://com.android.contacts/raw_contacts"), new String[]{"contact_id"}, null, null, null);

                while (query.moveToNext()) {
                    HashMap<String, String> hash = new HashMap<String, String>();
                    String id = query.getString(0);
                    Cursor cursor = resolver.query(Uri.parse("content://com.android.contacts/data"), new String[]{"data1", "mimetype"}, "raw_contact_id = ?", new String[]{id}, null);
                    while (cursor.moveToNext()) {
                        String data1 = cursor.getString(cursor.getColumnIndex("data1"));
                        String mimeType = cursor.getString(cursor.getColumnIndex("mimetype"));
                        if ("vnd.android.cursor.item/name".equals(mimeType)) { //是姓名
                            Log.i("name", data1);
                            hash.put("name", data1);
                        } else if ("vnd.android.cursor.item/phone_v2".equals(mimeType)) { //手机
                            hash.put("phone", data1);
                            Log.i("phone", data1);
                        }
                    }

                    list.add(hash);
                    Log.i(TAG, "onCreate: " + list.toString());
                    cursor.close();
                }
                query.close();
                mHandler.sendEmptyMessage(0);
            }
        }.start();

    }

    /**
     * 初始化Ui
     */
    private void initUi() {


        //初始化ListView
        lv_select_contact = (ListView) findViewById(R.id.lv_select_contact);

        lv_select_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAdapter != null){
                    HashMap<String, String> hash = mAdapter.getItem(position);
                    //获取当前条目指向集合的电话号码
                    String phone = hash.get("phone");
                    //在结束此界面回到导航界面，需要把数据返回去
                    Intent intent = new Intent();
                    intent.putExtra("phone",phone);

                    Log.i(TAG, "onItemClick: "+phone);
                    setResult(0,intent);
                    finish();
                }
            }
        });

    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            if (convertView == null) {
                view = View.inflate(getApplicationContext(), R.layout.contact_item, null);
            } else {
                view = convertView;
            }
            TextView tv_contact_name = (TextView) view.findViewById(R.id.tv_contact_name);
            TextView tv_contact_phone = (TextView) view.findViewById(R.id.tv_contact_phone);

            tv_contact_name.setText(getItem(position).get("name"));
            tv_contact_phone.setText(getItem(position).get("phone"));

            return view;
        }
    }

}
