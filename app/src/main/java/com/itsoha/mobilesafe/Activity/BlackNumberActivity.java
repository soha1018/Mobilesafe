package com.itsoha.mobilesafe.Activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.itsoha.mobilesafe.Bean.BlackNumerBen;
import com.itsoha.mobilesafe.Engine.BlackNumberDao;
import com.itsoha.mobilesafe.R;

import java.util.List;

/**
 * 通信卫士的界面---黑名单
 * Created by Administrator on 2017/3/23.
 */

public class BlackNumberActivity extends AppCompatActivity {

    private BlackNumberDao numberDao;
    private int mode = 1;
    private ListView lv_black_bumber;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (bumberAdapter == null){
                bumberAdapter = new BlackNumberAdapter();
            }else {
                bumberAdapter.notifyDataSetChanged();
            }

            lv_black_bumber.setAdapter(bumberAdapter);
            super.handleMessage(msg);
        }
    };
    private List<BlackNumerBen> queryList;
    private String TAG = "BlackNumberActivity";
    private BlackNumberAdapter bumberAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacknumber);

        //初始化控件
        initView();
        //初始化数据
        initData();
    }

    /**
     * 初始化ListView的数据---查询数据库
     */
    private void initData() {

        new Thread() {
            @Override
            public void run() {
                //初始化数据库
                numberDao = BlackNumberDao.getInstance(getApplicationContext());

                queryList = numberDao.findPage(20);

                handler.sendEmptyMessage(0);

            }
        }.start();
    }

    /**
     * 初始化控件
     */
    private void initView() {


        lv_black_bumber = (ListView) findViewById(R.id.lv_black_bumber);

        Button add_black_phone = (Button) findViewById(R.id.add_black_phone);

        add_black_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出对话框输入内容
                showDialogNumber();
            }
        });
    }

    private void showDialogNumber() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();

        //使用自定义的视图显示Dialog
        View view = View.inflate(this, R.layout.dialog_balcknumber, null);
        dialog.setView(view, 0, 0, 0, 0);

        final EditText et_black_dialog_phone = (EditText) view.findViewById(R.id.et_black_dialog_phone);
        RadioGroup rg_black_dialog = (RadioGroup) view.findViewById(R.id.rg_black_dialog);
        Button bt_black_ok = (Button) view.findViewById(R.id.bt_black_ok);
        Button bt_black_no = (Button) view.findViewById(R.id.bt_black_no);

        //监听RadioGroup的
        rg_black_dialog.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                switch (checkedId) {
                    case R.id.rb_black_phone:
                        mode = 1;
                        break;
                    case R.id.rb_black_sms:
                        mode = 2;
                        break;
                    case R.id.rb_black_all:
                        mode = 3;
                        break;
                }
            }
        });
        //点击确定提交数据以后
        bt_black_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = et_black_dialog_phone.getText().toString().trim();
                if (!TextUtils.isEmpty(phone)) {
                    for (int i = 0; i < 100; i++) {
                        numberDao.insert(phone+i, String.valueOf(mode));

                    }

                    BlackNumerBen numerBen = new BlackNumerBen();
                    numerBen.setPhone(phone);
                    numerBen.setMode(String.valueOf(mode));
                    //通知集合第一个元素添加数据
                    queryList.add(0, numerBen);
                    if (bumberAdapter != null) {
                        bumberAdapter.notifyDataSetChanged();
                    }
                    dialog.dismiss();
                } else {
                    Toast.makeText(BlackNumberActivity.this, "请输入电话号码", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //点击取消隐藏对话框
        bt_black_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 数据适配器
     */
    private class BlackNumberAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return queryList.size();
        }

        @Override
        public Object getItem(int position) {
            return queryList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //使用ViewHolder惊醒优化
            ViewHolder holder = null;
            if (convertView == null) {
                //convertview等于空就新建初始化对象的控件
                holder = new ViewHolder();
                convertView = View.inflate(getApplicationContext(), R.layout.blacknumber_item, null);
                holder.tv_black_item_phone = (TextView) convertView.findViewById(R.id.tv_black_item_phone);
                holder.tv_black_item_mode = (TextView) convertView.findViewById(R.id.tv_black_item_mode);
                holder.iv_black_item_delete = (ImageView) convertView.findViewById(R.id.iv_black_item_delete);

                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tv_black_item_phone.setText(queryList.get(position).getPhone());
            String mode = queryList.get(position).getMode();

            switch (Integer.parseInt(mode)) {
                case 1:
                    holder.tv_black_item_mode.setText("拦截电话");
                    break;
                case 2:
                    holder.tv_black_item_mode.setText("拦截短信");
                    break;
                case 3:
                    holder.tv_black_item_mode.setText("拦截所有");
                    break;
            }

            holder.iv_black_item_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    numberDao.delete(queryList.get(position).getPhone());

                    queryList.remove(position);
                    if (bumberAdapter != null) {
                        bumberAdapter.notifyDataSetChanged();
                    }
                }
            });
            return convertView;
        }
    }

    private class ViewHolder {
        public TextView tv_black_item_phone;
        public TextView tv_black_item_mode;
        public ImageView iv_black_item_delete;
    }
}
