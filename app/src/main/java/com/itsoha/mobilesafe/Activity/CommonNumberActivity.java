package com.itsoha.mobilesafe.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.itsoha.mobilesafe.Engine.CommonNumDao;
import com.itsoha.mobilesafe.R;

import java.util.List;

/**
 * 常用电话号码查询的界面
 * Created by Administrator on 2017/4/9.
 */

public class CommonNumberActivity extends AppCompatActivity {

    private List<CommonNumDao.Group> mgroup;
    private ExpandableListView elv_common;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_number);
        //初始化控件
        initUi();
        //准备数据填充ListView
        initData();
    }

    /**
     * 准备数据填充ListView
     */
    private void initData() {
        CommonNumDao numDao = new CommonNumDao();
        mgroup = numDao.getGroup();

        CommonNumAdapter numAdapter = new CommonNumAdapter();
        elv_common.setAdapter(numAdapter);

        //监听子条目被单击的事件
        elv_common.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //拨打电话号码
                CallNumber(groupPosition, childPosition);
                return false;
            }
        });
    }

    /**
     * 拨打电话号码
     * @param groupPosition 父条目的索引
     * @param childPosition 子条目的索引
     */
    private void CallNumber(int groupPosition, int childPosition) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + mgroup.get(groupPosition).list.get(childPosition).number));
        startActivity(intent);
    }

    /**
     * 初始化控件
     */
    private void initUi() {
        elv_common = (ExpandableListView) findViewById(R.id.elv_common);

    }

    private class CommonNumAdapter extends BaseExpandableListAdapter{

        @Override
        public int getGroupCount() {
            return mgroup.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mgroup.get(groupPosition).list.size();
        }

        @Override
        public CommonNumDao.Group getGroup(int groupPosition) {
            return mgroup.get(groupPosition);
        }

        @Override
        public CommonNumDao.Table getChild(int groupPosition, int childPosition) {
            return mgroup.get(groupPosition).list.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView textView = new TextView(getApplicationContext());
            textView.setText("         "+getGroup(groupPosition).name);
            textView.setTextColor(Color.RED);
            textView.setTextSize(20);
            return textView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.elv_child_item, null);
            TextView tv_child_name = (TextView) view.findViewById(R.id.tv_child_name);
            TextView tv_child_phone = (TextView) view.findViewById(R.id.tv_child_phone);
            tv_child_name.setText(getChild(groupPosition,childPosition).name);
            tv_child_phone.setText(getChild(groupPosition,childPosition).number);
            return view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;//处理子条目
        }
    }
}
