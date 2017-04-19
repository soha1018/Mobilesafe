package com.itsoha.mobilesafe.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.itsoha.mobilesafe.Bean.AppInfo;
import com.itsoha.mobilesafe.Engine.AppInfoProvider;
import com.itsoha.mobilesafe.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 手机软件管理的界面，显示手机可用空间
 * Created by Administrator on 2017/4/2.
 */

public class AppManageActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView lv_app;
    private static final String TAG = "AppManageActivity";
    private List<AppInfo> mInfoList;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            infoAdapter = new InfoAdapter();
            lv_app.setAdapter(infoAdapter);

            if (tv_app_des != null && mUserList.size() != 0) {
                tv_app_des.setText("用户应用(" + mUserList.size() + ")");
            }
            super.handleMessage(msg);
        }
    };
    private InfoAdapter infoAdapter;
    private List<AppInfo> mSystemList, mUserList;
    private TextView tv_app_des;
    /**
     * 卸载
     */
    private TextView tv_uninstall;
    /**
     * 启动
     */
    private TextView tv_app_start;
    /**
     * 分享
     */
    private TextView tv_app_share;
    private AppInfo mAppInfo;
    private PopupWindow mPopupWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appmanage);

        //获取手机和内存卡的可用空间
        initMemorySize();
        //初始化ListView条目，显示应用信息
        initList();
    }

    private void initView(View popup_view) {
        tv_uninstall = (TextView) popup_view.findViewById(R.id.tv_uninstall);
        tv_uninstall.setOnClickListener(this);
        tv_app_start = (TextView) popup_view.findViewById(R.id.tv_app_start);
        tv_app_start.setOnClickListener(this);
        tv_app_share = (TextView) popup_view.findViewById(R.id.tv_app_share);
        tv_app_share.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_uninstall:
                //卸载应用，需要先判断是否是系统的应用
                if (mAppInfo.isSystem()) {
                    Toast.makeText(this, "这是系统的应用，您没有权限删除", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent("android.intent.action.DELETE");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse("package:" + mAppInfo.getPackageName()));
                    startActivity(intent);
                }
                break;
            case R.id.tv_app_start:
                Log.i(TAG, "onClick: 这是：" + mAppInfo.isSystem());
                //从桌面开启应用
                if (mAppInfo != null) {
                    PackageManager packageManager = getPackageManager();
                    Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(mAppInfo.getPackageName());
                    if (launchIntentForPackage != null) {
                        startActivity(launchIntentForPackage);
                    } else {
                        Toast.makeText(this, "抱歉，此应用不能被开启。", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.tv_app_share:
                //通过短信分享应用
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, mAppInfo.getName() + "<--这个应用很好用，和我一起来玩吧!");
                intent.setType("text/plain");
                startActivity(intent);
                break;
        }
        //点击内容以后就关闭弹出的窗口
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }

    }

    private class InfoAdapter extends BaseAdapter {

        //获取条目的类型
        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mUserList.size() + 1) {
                return 0;
            } else {
                return 1;
            }
        }

        //获取所有视图类型的总数
        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        @Override
        public int getCount() {
            return mSystemList.size() + mUserList.size() + 2;
        }

        @Override
        public AppInfo getItem(int position) {
            if (position == 0 || position == mUserList.size() + 1) {
                return null;
            } else {
                if (position < mUserList.size() + 1) {
                    return mUserList.get(position - 1);
                } else {
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
                if (convertView == null) {
                    viewHolderTitle = new ViewHolderTitle();
                    convertView = View.inflate(getApplicationContext(), R.layout.appmanage_title_item, null);
                    viewHolderTitle.tv_title = (TextView) convertView.findViewById(R.id.tv_app_title);
                    convertView.setTag(viewHolderTitle);
                } else {
                    viewHolderTitle = (ViewHolderTitle) convertView.getTag();
                }
                if (position == 0) {
                    viewHolderTitle.tv_title.setText("用户应用(" + mUserList.size() + ")");
                } else {
                    viewHolderTitle.tv_title.setText("系统应用(" + mSystemList.size() + ")");
                }
                return convertView;
            } else {
                //显示图片带文本的内容
                ViewHolder viewHolder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.appinfo_item, null);
                    viewHolder = new ViewHolder(convertView);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                viewHolder.iv_app_icon.setBackgroundDrawable(getItem(position).getIcon());
                viewHolder.tv_app_name.setText(getItem(position).getName());
                if (getItem(position).isSystem()) {
                    viewHolder.tv_app_path.setText("手机应用");
                } else {
                    viewHolder.tv_app_path.setText("SD卡应用");
                }
                return convertView;
            }

        }

    }

    static class ViewHolderTitle {
        TextView tv_title;
    }


    static class ViewHolder {
        ImageView iv_app_icon;
        TextView tv_app_name;
        TextView tv_app_path;

        ViewHolder(View view) {
            this.iv_app_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
            this.tv_app_name = (TextView) view.findViewById(R.id.tv_app_name);
            this.tv_app_path = (TextView) view.findViewById(R.id.tv_app_path);
        }
    }


    /**
     * 当Activity重新获取焦点的时候，刷新列表中的数据
     */
    @Override
    protected void onResume() {
        //查询应用的信息是耗时的操作 应该放到子线程中去完成
        new Thread() {
            @Override
            public void run() {
                mInfoList = AppInfoProvider.getInfoList(getApplicationContext());
                mSystemList = new ArrayList<>();
                mUserList = new ArrayList<>();

                for (AppInfo appInfo : mInfoList
                        ) {
                    if (appInfo.isSystem()) {
                        //系统应用
                        mSystemList.add(appInfo);
                    } else {
                        //用户应用
                        mUserList.add(appInfo);
                        Log.i(TAG, "run: " + appInfo.getName());
                    }
                }
                //发送消息更新ListView
                handler.sendEmptyMessage(0);
                super.run();
            }
        }.start();
        super.onResume();
    }

    /**
     * 初始化ListView条目，显示应用信息
     */
    private void initList() {
        lv_app = (ListView) findViewById(R.id.lv_app);
        tv_app_des = (TextView) findViewById(R.id.tv_app_des);

        //监听ListView滚动的监听事件
        lv_app.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mSystemList != null && mUserList != null) {
                    if (firstVisibleItem >= mUserList.size() + 1) {
                        //系统条目
                        tv_app_des.setText("系统应用（" + mSystemList.size() + "）");
                    } else {
                        //用户应用
                        tv_app_des.setText("用户应用（" + mUserList.size() + "）");
                    }
                }
            }
        });

        //监听ListView的点击事件
        lv_app.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //判断是否点击了title的条目 如果是就直接返回
                if (position == 0 && position == mUserList.size() + 1) {
                    return;
                } else {
                    if (position < mUserList.size() + 1) {
                        mAppInfo = mUserList.get(position - 1);
                    } else {
                        mAppInfo = mSystemList.get(position - 2);
                    }
                    //弹出窗口
                    showPopupWindows(view);
                }
            }
        });
    }

    /**
     * 弹出窗口选择操作
     *
     * @param view
     */
    private void showPopupWindows(View view) {
        View popup_view = View.inflate(this, R.layout.app_popup_view, null);
        //初始化点击事件
        initView(popup_view);

        //透明动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setFillAfter(true);//保留动画最初的位置

        //淡入淡出的缩放动画，相对于自己的中心点
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1000);
        scaleAnimation.setFillAfter(true);

        //把两个动画添加到一个动画集合中 ，让他们一起显示
        AnimationSet animationSet = new AnimationSet(true);//同一个插补器的效果
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);

        mPopupWindow = new PopupWindow(popup_view,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true);
        //已经有背景了，所以设置透明的背景
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.showAsDropDown(view, 100, -view.getHeight());

        //把此动画加载到视图中
        popup_view.startAnimation(animationSet);
    }

    /**
     * 获取手机和内存卡的可用空间
     */
    private void initMemorySize() {
        TextView tv_app_memory = (TextView) findViewById(R.id.tv_app_memory);
        TextView tv_app_sd_memory = (TextView) findViewById(R.id.tv_app_sd_memory);


        //首先获取路径
        String path = Environment.getDataDirectory().getPath();
        String sdPath = Environment.getExternalStorageDirectory().getPath();


        //获取以上两个路径下文件夹的可用大小
        //获取手机的可用空间大小
        long availableSpace = getAvailableSpace(path);
        if (availableSpace > 0) {
            String memory = Formatter.formatFileSize(this,availableSpace);
            tv_app_memory.setText(memory);
        }
        //获取SD卡可用空间的大小
        long sdSize = getAvailableSpace(sdPath);
        if (sdSize < 0) {
            tv_app_sd_memory.setText("SD卡还没有装载");
        } else {
            String sdmemory = Formatter.formatFileSize(this, sdSize);
            tv_app_sd_memory.setText(sdmemory);
        }


    }

    /**
     * 获取手机的可用空间大小
     *
     * @param path
     */
    private long getAvailableSpace(String path) {
        StatFs statFs = new StatFs(path);

        //获取可用的块
//        statFs.getAvailableBlocks();
        //获取块的大小
//        statFs.getBlocksize();

        return statFs.getAvailableBlocks() * statFs.getBlockSize();
    }
}
