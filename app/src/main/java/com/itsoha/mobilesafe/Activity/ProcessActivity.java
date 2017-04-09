package com.itsoha.mobilesafe.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itsoha.mobilesafe.Bean.ProcessInfo;
import com.itsoha.mobilesafe.Engine.ProcessProvider;
import com.itsoha.mobilesafe.R;
import com.itsoha.mobilesafe.Utils.ConstanVlauel;
import com.itsoha.mobilesafe.Utils.SpUtils;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.VISIBLE;

/**
 * 进程管理界面
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
            mAdapter = new ProcessAdapter();
            lv_process.setAdapter(mAdapter);
            tv_process_des.setText("用户应用(" + mUserList.size() + ")");
            super.handleMessage(msg);
        }
    };
    private TextView tv_process_des;
    private ProcessInfo mProcessInfo;
    private ProcessAdapter mAdapter;
    private long mProcessCount;
    private long mAvailSpace;
    private String memory;

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


        //监听ListView的滚动状态，对常驻悬浮窗进行更新
        tv_process_des = (TextView) findViewById(R.id.tv_process_des);
        lv_process.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mSystemList != null && mUserList != null) {
                    if (firstVisibleItem < mUserList.size() + 1) {
                        tv_process_des.setText("用户进程(" + mUserList.size() + ")");
                    } else {
                        tv_process_des.setText("系统进程(" + mSystemList.size() + ")");
                    }
                }

            }
        });

        //监听每个条目的点击事件
        lv_process.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取每个条目对应的CheckBox
                CheckBox cb_process = (CheckBox) view.findViewById(R.id.cb_process);
                if (position == 0 || position == mUserList.size() + 1) {
                    return;
                } else {
                    if (position < mUserList.size() + 1) {
                        mProcessInfo = mUserList.get(position - 1);
                    } else {
                        mProcessInfo = mSystemList.get(position - mUserList.size() - 2);
                    }
                    if (mProcessInfo != null) {
                        if (!mProcessInfo.getPackageName().equals(getPackageName())) {
                            Log.i(TAG, "onItemClick: 执行这里的内容");
                            //选中的不等于当前的应用程序
                            mProcessInfo.isCheck = !mProcessInfo.isCheck;

                            cb_process.setChecked(mProcessInfo.isCheck);
                        }
                    }
                }

            }
        });
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
                    }
                }

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
            if (position == 0 || position == mUserList.size() + 1) {
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
            //判断是否开启了需要隐藏系统进程
            if (SpUtils.getBoolean(getApplicationContext(), ConstanVlauel.SHOW_SYSTEM, false)) {
                return mUserList.size() + 1;
            } else {
                return mSystemList.size() + mUserList.size() + 2;
            }
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
            int viewType = getItemViewType(position);
            ViewHolderTitle viewHolderTitle = null;
            if (viewType == 0) {
                //条目标题
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.appmanage_title_item, null);
                    viewHolderTitle = new ViewHolderTitle(convertView);
                    convertView.setTag(viewHolderTitle);
                } else {
                    viewHolderTitle = (ViewHolderTitle) convertView.getTag();
                }
                if (position == 0) {
                    viewHolderTitle.tv_app_title.setText("用户进程（" + mUserList.size() + "）");
                } else {
                    viewHolderTitle.tv_app_title.setText("系统进程（" + mSystemList.size() + "）");
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
                if (info != null) {
                    viewHolder.iv_processs_icon.setBackgroundDrawable(info.getIcon());
                    viewHolder.tv_process_name.setText(info.getNames());
                    if (getItem(position).isSystem) {
                        viewHolder.tv_process_path.setText("系统进程");
                    } else {
                        viewHolder.tv_process_path.setText("用户进程");
                    }
                    viewHolder.cb_process.setChecked(info.isCheck);
                }

                tv_process_count.setText("线程总数:" + (mSystemList.size()+mUserList.size()));
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
        mProcessCount = ProcessProvider.getProcessCount(this);
        tv_process_count.setText("线程总数:" + mProcessCount);
        //显示线程占用的空间
        mAvailSpace = ProcessProvider.getAvailSpace(this);
        String Avail = Formatter.formatFileSize(this, mAvailSpace);
        //线程的所有内存大小
        memory = Formatter.formatFileSize(this, ProcessProvider.getMemory());

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
                //全选
                selectAll();
                break;
            case R.id.bt_process_Inverse:
                //对条目的状态进行反选
                selectReverse();
                break;
            case R.id.bt_process_clear:
                //清除进程
                clearAll();
                break;
            case R.id.bt_process_setting:
                Intent intent = new Intent(this, ProcessSettingActivity.class);
                startActivityForResult(intent, 0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 清除进程
     */
    private void clearAll() {
        //将要杀死的进程条目保存起来，再进行删除
        List<ProcessInfo> killList = new ArrayList<>();
        for (ProcessInfo info : mUserList
                ) {
            //是当前应用就跳过
            if (info.getPackageName().equals(getPackageName())) {
                continue;
            }
            if (info.isCheck) {
                killList.add(info);
            }
        }
        for (ProcessInfo info : mSystemList
                ) {
            //是当前应用就跳过
            if (info.getPackageName().equals(getPackageName())) {
                continue;
            }
            if (info.isCheck) {

                killList.add(info);
            }
        }
        //删除被选中的条目还有清除进程
        int memoryAll = 0;
        for (ProcessInfo info : killList
                ) {
            if (mUserList.contains(info)) {
                mUserList.remove(info);
            }
            if (mSystemList.contains(info)) {
                mSystemList.remove(info);
            }

            //清除系统的进程
            ProcessProvider.killProcess(this, info);
            //记录释放空间的总大小
            memoryAll += info.memSize;
        }

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        //对页面其他Ui的更新
        mProcessCount -= killList.size();
        //更新可用空间
        mAvailSpace += memoryAll;
        tv_process_count.setText("进程总数：" + mProcessCount);
        tv_process_memory.setText("剩余/总共:" + Formatter.formatFileSize(this, mAvailSpace) + "/"
                + memory);

        //提示用户
        Toast.makeText(this, "清除了" + killList.size() + "个进程，释放了" + Formatter.formatFileSize(this, memoryAll) +
                "空间", Toast.LENGTH_SHORT).show();
    }

    /**
     * 按钮的反选状态
     */
    private void selectReverse() {
        Log.i(TAG, "selectReverse: 反选");
        for (ProcessInfo info : mUserList
                ) {
            if (info.getPackageName().equals(getPackageName())) {
                continue;
            }
            info.isCheck = !info.isCheck;
        }

        for (ProcessInfo info : mSystemList
                ) {
            if (info.getPackageName().equals(getPackageName())) {
                continue;
            }
            info.isCheck = !info.isCheck;
        }

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 点击全部选中按钮，对除本应用外全部选中
     */
    private void selectAll() {
        Log.i(TAG, "selectAll: 全选");
        for (ProcessInfo info : mUserList
                ) {
            if (info.getPackageName().equals(getPackageName())) {
                continue;
            }
            info.isCheck = true;
        }

        for (ProcessInfo info : mSystemList
                ) {
            if (info.getPackageName().equals(getPackageName())) {
                continue;
            }
            info.isCheck = true;
        }

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
