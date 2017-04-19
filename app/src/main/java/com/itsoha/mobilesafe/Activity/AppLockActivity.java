package com.itsoha.mobilesafe.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.itsoha.mobilesafe.Bean.AppInfo;
import com.itsoha.mobilesafe.Engine.AppInfoProvider;
import com.itsoha.mobilesafe.Engine.AppLockDao;
import com.itsoha.mobilesafe.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 程序锁的界面
 * Created by Administrator on 2017/4/16.
 */

public class AppLockActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AppLockActivity";
    /**
     * 为加锁
     */
    private Button mBtUnlock;
    /**
     * 已加锁
     */
    private Button mBtLock;
    /**
     * 未加锁的应用
     */
    private TextView mTvAppUnlock;
    private ListView mLvUnlock;
    private LinearLayout mLlUnclock;
    /**
     * 已加锁的应用
     */
    private TextView mTvApplock;
    private ListView mLvLock;
    private LinearLayout mLlClock;
    private List<AppInfo> mList;
    private List<AppInfo> mLockList;
    private List<AppInfo> mUnlockList;
    private AppLockDao mDao;
    private Myadapter mUnLockAdapter, mLockAdapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mLockAdapter = new Myadapter(true);
            mLvLock.setAdapter(mLockAdapter);

            mUnLockAdapter = new Myadapter(false);
            mLvUnlock.setAdapter(mUnLockAdapter);
        }
    };
    private TranslateAnimation mTranslateAnimation;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applock);
        //初始化控件
        initView();
        //初始化数据
        initData();
        //初始化动画
        initAnimation();
    }

    /**
     * 初始化动画
     */
    private void initAnimation() {
        mTranslateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_PARENT, 0);
        mTranslateAnimation.setDuration(500);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        new Thread() {
            @Override
            public void run() {
                //获取所有应用的总数
                mList = AppInfoProvider.getInfoList(getApplicationContext());
                //区分已加锁和未加锁的应用
                mLockList = new ArrayList<AppInfo>();
                mUnlockList = new ArrayList<AppInfo>();

                //获取数据库中已加锁的应用
                mDao = AppLockDao.getInstance(getApplicationContext());
                List<String> lockPackage = mDao.findAll();
                for (AppInfo appInfo : mList
                        ) {
                    if (lockPackage.contains(appInfo.getPackageName())) {
                        mLockList.add(appInfo);
                    } else {
                        mUnlockList.add(appInfo);
                    }
                }
                Log.i(TAG, "run: 未加锁应用总数" + mUnlockList.size());

                //通知应用程序更新ListView
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    /**
     * 数据适配器
     */
    private class Myadapter extends BaseAdapter {

        private boolean isLock;

        public Myadapter(boolean isLock) {
            this.isLock = isLock;
        }

        @Override
        public int getCount() {
            if (isLock) {
                mTvApplock.setText("已加锁应用总数：" + mLockList.size());
                return mLockList.size();
            } else {
                mTvAppUnlock.setText("未加锁应用总数：" + mUnlockList.size());
                return mUnlockList.size();
            }
        }

        @Override
        public AppInfo getItem(int position) {
            if (isLock) {
                return mLockList.get(position);
            } else {
                return mUnlockList.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.listview_islock_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final AppInfo info = getItem(position);
            viewHolder.mIvIcon.setBackgroundDrawable(info.getIcon());
            viewHolder.mTvName.setText(info.getName());
            if (isLock) {
                viewHolder.mIvLock.setImageResource(R.mipmap.lock);
            } else {
                viewHolder.mIvLock.setImageResource(R.mipmap.unlock);
            }
            final View translateView = convertView;
            //点击小锁的图标
            viewHolder.mIvLock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //为条目添加移除的动画
                    translateView.startAnimation(mTranslateAnimation);
                    mTranslateAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            //动画结束后移除
                            if (isLock){
                                //点击已加锁的，删除条目并且添加到未加锁条目（从数据库中删除）
                                mLockList.remove(info);
                                mUnlockList.add(info);

                                mDao.delete(info.getPackageName());

                                mLockAdapter.notifyDataSetChanged();
                            }else {
                                //点击未加锁的，删除条目并且添加到已加锁条目（从数据库中增加）
                                mUnlockList.remove(info);
                                mLockList.add(info);

                                mDao.Insert(info.getPackageName());

                                mUnLockAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                }
            });
            return convertView;
        }


    }

    static class ViewHolder {
        View view;
        ImageView mIvIcon;
        TextView mTvName;
        ImageView mIvLock;

        ViewHolder(View view) {
            this.view = view;
            this.mIvIcon = (ImageView) view.findViewById(R.id.iv_icon);
            this.mTvName = (TextView) view.findViewById(R.id.tv_name);
            this.mIvLock = (ImageView) view.findViewById(R.id.iv_lock);
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mBtUnlock = (Button) findViewById(R.id.bt_unlock);
        mBtUnlock.setOnClickListener(this);
        mBtLock = (Button) findViewById(R.id.bt_lock);
        mBtLock.setOnClickListener(this);
        mTvAppUnlock = (TextView) findViewById(R.id.tv_app_unlock);
        mLvUnlock = (ListView) findViewById(R.id.lv_unlock);
        mLlUnclock = (LinearLayout) findViewById(R.id.ll_unclock);
        mTvApplock = (TextView) findViewById(R.id.tv_applock);
        mLvLock = (ListView) findViewById(R.id.lv_lock);
        mLlClock = (LinearLayout) findViewById(R.id.ll_clock);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_unlock:
                //点击未加锁按钮
                mBtUnlock.setBackgroundResource(R.mipmap.tab_right_pressed);
                mBtLock.setBackgroundResource(R.mipmap.tab_left_default);

                mLlClock.setVisibility(View.GONE);
                mLlUnclock.setVisibility(View.VISIBLE);
                break;
            case R.id.bt_lock:
                //点击加锁按钮
                mBtUnlock.setBackgroundResource(R.mipmap.tab_left_default);
                mBtLock.setBackgroundResource(R.mipmap.tab_right_pressed);

                mLlClock.setVisibility(View.VISIBLE);
                mLlUnclock.setVisibility(View.GONE);
                break;
        }
    }
}
