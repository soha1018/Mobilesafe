package com.itsoha.mobilesafe.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.itsoha.mobilesafe.Bean.ProcessInfo;
import com.itsoha.mobilesafe.Engine.ProcessProvider;
import com.itsoha.mobilesafe.R;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.VISIBLE;

/**
 * Created by Administrator on 2017/4/6.
 */

public class ProcessActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * 进程总数
     */
    private TextView tv_process_count;
    /**
     * 剩余空间/总大小
     */
    private TextView tv_process_memory;
    private ListView lv_process;
    /**
     * 全选
     */
    private Button bt_process_all;
    /**
     * 反选
     */
    private Button bt_process_Inverse;
    private static final String TAG = "ProcessActivity";
    /**
     * 一键清理
     */
    private Button bt_process_clear;
    /**
     * 设置
     */
    private Button bt_process_setting;
    private List<ProcessInfo> mList;
    private List<ProcessInfo> mSystemList, mUserList;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ProcessAdapter adapter = new ProcessAdapter();
            lv_process.setAdapter(adapter);
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        //初始化控件
        initView();
        //初始化内存显示
        initMemory();
        //初始化ListView的数据
        initListData();
    }

    /**
     * 初始化ListView的数据
     */
    private void initListData() {

    }


    /**
     * 在Activity获取焦点时候初始化ListView中的数据
     */
    @Override
    protected void onResume() {
        new Thread() {
            @Override
            public void run() {
                mList = ProcessProvider.getProcessInfo(getApplicationContext());
                Log.i(TAG, "run: " + mList.size());
                mSystemList = new ArrayList<ProcessInfo>();
                mUserList = new ArrayList<ProcessInfo>();

                for (ProcessInfo info : mList
                        ) {
                    if (info.isSystem) {
                        mSystemList.add(info);
                    } else {
                        mUserList.add(info);
                        Log.i(TAG, "run: " + mUserList.size());
                    }
                }
                Log.i(TAG, "run: " + mSystemList.size());

                //发送消息更新Listview
                handler.sendEmptyMessage(0);

                super.run();
            }
        }.start();
        super.onResume();
    }

    /**
     * 进程信息适配器
     */
    private class ProcessAdapter extends BaseAdapter {

        /**
         * 获取条目的类型
         *
         * @param position 条目的位置
         * @return
         */
        @Override
        public int getItemViewType(int position) {
            if (position == 0 && position == mUserList.size() + 1) {
                //文本条目
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        @Override
        public int getCount() {
            return mSystemList.size() + mUserList.size() + 2;
        }

        @Override
        public ProcessInfo getItem(int position) {
            if (position == 0 || position == mUserList.size() + 1) {
                //条目标题不做处理
                return null;
            } else {
                if (position < mUserList.size() + 1) {
                    return mUserList.get(position - 1);
                } else {
                    //返回系统的条目，所有的用户进程减去标题
                    return mSystemList.get(position - mUserList.size() - 2);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolderTitle viewHolderTitle = null;
            if (getItemViewType(position) == 0) {
                //条目标题
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.process_item, null);
                    viewHolderTitle = new ViewHolderTitle(convertView);
                    convertView.setTag(viewHolderTitle);
                } else {
                    viewHolderTitle = (ViewHolderTitle) convertView.getTag();
                }
                if (position == 0) {
                    viewHolderTitle.tv_app_title.setText("用户进程（" + mUserList.size() + ")");
                } else {
                    viewHolderTitle.tv_app_title.setText("系统进程（" + mSystemList.size() + ")");
                }
                return convertView;
            } else {
                //进程信息
                ViewHolder viewHolder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.process_item, null);
                    viewHolder = new ViewHolder(convertView);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                //条目信息的设置
                ProcessInfo info = getItem(position);
                if (info != null){
                    viewHolder.iv_processs_icon.setBackgroundDrawable(info.getIcon());
                    viewHolder.tv_process_name.setText(info.getNames());
                    if (getItem(position).isSystem) {
                        viewHolder.tv_process_path.setText("系统进程");
                        viewHolder.cb_process.setVisibility(VISIBLE);
                    } else {
                        viewHolder.tv_process_path.setText("用户进程");
                        viewHolder.cb_process.setVisibility(View.INVISIBLE);
                    }
                }

                return convertView;
            }
        }


    }

    /**
     * 显示进程信息的条目
     */
    static class ViewHolder {
        ImageView iv_processs_icon;
        TextView tv_process_name;
        TextView tv_process_path;
        CheckBox cb_process;

        ViewHolder(View view) {
            this.iv_processs_icon = (ImageView) view.findViewById(R.id.iv_process_icon);
            this.tv_process_name = (TextView) view.findViewById(R.id.tv_process_name);
            this.tv_process_path = (TextView) view.findViewById(R.id.tv_process_path);
            this.cb_process = (CheckBox) view.findViewById(R.id.cb_process);
        }
    }

    /**
     * 条目的标题
     */
    static class ViewHolderTitle {
        TextView tv_app_title;

        ViewHolderTitle(View view) {
            this.tv_app_title = (TextView) view.findViewById(R.id.tv_app_title);
        }
    }

    /**
     * 初始化内存显示
     */
    private void initMemory() {
        //显示正在运行线程的数量
        tv_process_count.setText("线程总数:" + ProcessProvider.getProcessCount(this));
        //显示线程占用的空间
        String Avail = Formatter.formatFileSize(this, ProcessProvider.getAvailSpace(this));
        //线程的所有内存大小
        String memory = Formatter.formatFileSize(this, ProcessProvider.getMemory());

        tv_process_memory.setText("剩余/总共:" + Avail + "/" + memory);

    }

    /**
     * 初始化控件
     */
    private void initView() {
        tv_process_count = (TextView) findViewById(R.id.tv_process_count);
        tv_process_memory = (TextView) findViewById(R.id.tv_process_memory);
        lv_process = (ListView) findViewById(R.id.lv_process);
        bt_process_all = (Button) findViewById(R.id.bt_process_all);
        bt_process_all.setOnClickListener(this);
        bt_process_Inverse = (Button) findViewById(R.id.bt_process_Inverse);
        bt_process_Inverse.setOnClickListener(this);
        bt_process_clear = (Button) findViewById(R.id.bt_process_clear);
        bt_process_clear.setOnClickListener(this);
        bt_process_setting = (Button) findViewById(R.id.bt_process_setting);
        bt_process_setting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_process_all:
                break;
            case R.id.bt_process_Inverse:
                break;
            case R.id.bt_process_clear:
                break;
            case R.id.bt_process_setting:
                break;
        }
    }
}
