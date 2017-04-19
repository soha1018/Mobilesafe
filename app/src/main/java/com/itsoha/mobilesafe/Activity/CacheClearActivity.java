package com.itsoha.mobilesafe.Activity;

import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.itsoha.mobilesafe.R;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 缓冲清理的界面
 * Created by Administrator on 2017/4/18.
 */

public class CacheClearActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * 一键清理
     */
    private Button bt_cache_clear;
    /**
     * 开始清理
     */
    private TextView tv_cache;
    private LinearLayout layout_item;
    private PackageManager pm;
    private final int CACHE_UPDATE = 100;
    private Handler mHanderl = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CACHE_UPDATE:
                    //更新信息
                    View view = View.inflate(getApplicationContext(), R.layout.linearlayout_cache_item, null);
                    final CacheInfo info = (CacheInfo) msg.obj;
                    ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                    TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
                    TextView tv_memory_info = (TextView) view.findViewById(R.id.tv_memory_info);
                    ImageView iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
                    iv_icon.setBackgroundDrawable(info.icon);
                    tv_name.setText(info.name);
                    tv_memory_info.setText(Formatter.formatFileSize(getApplicationContext(), info.cacheSize));

                    layout_item.addView(view, 0);
                    iv_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //跳转到应用信息的界面
                            startAppInfo(info.packageName);
                        }
                    });
                    break;
                case SCAN_APP:
                    String name = (String) msg.obj;
                    tv_cache.setText(name);
                    break;
                case CACHE_FINISH:
                    tv_cache.setText("更新完成");
                    break;
                case CACHE_CLEAR:
                    //一键清理完成
                    layout_item.removeAllViews();
                    Toast.makeText(CacheClearActivity.this, "清理完成", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /**
     * 跳转到应用信息的界面
     * @param packageName 包名
     */
    private void startAppInfo(String packageName) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:"+packageName));
        startActivity(intent);
    }

    private ProgressBar pb_cache;
    private int index = 0;
    private final int SCAN_APP = 200;
    private final int CACHE_FINISH = 111;
    private final int CACHE_CLEAR = 123;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_clear);
        //初始化控件
        initView();
        //初始化数据
        initData();

    }

    /**
     * 初始化数据
     */
    private void initData() {
        new Thread() {
            @Override
            public void run() {
                pm = getPackageManager();
                List<PackageInfo> packageInfoList = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
                //设置进度条的最大值
                pb_cache.setMax(packageInfoList.size());
                for (PackageInfo info : packageInfoList
                        ) {
                    String packageName = info.packageName;
                    //获取应用的缓存
                    getCacheSize(packageName);

                    try {
                        Thread.sleep(60);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //每次的进度
                    index++;
                    pb_cache.setProgress(index);

                    //每次都要发送消息告知正在扫描的应用
                    Message message = Message.obtain();
                    message.what = SCAN_APP;
                    try {
                        message.obj = pm.getApplicationInfo(packageName,0).loadLabel(pm).toString();
                        mHanderl.sendMessage(message);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                Message obtain = Message.obtain();
                obtain.what = CACHE_FINISH;
                mHanderl.sendMessage(obtain);
            }
        }.start();
    }

    /**
     * 获取应用的缓存
     *
     * @param packageName 包名
     */
    private void getCacheSize(final String packageName) {
        IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
            @Override
            public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                //获取缓存文件的大小
                long cacheSize = pStats.cacheSize;
                if (cacheSize > 0) {
                    Message message = Message.obtain();
                    message.what = CACHE_UPDATE;
                    CacheInfo cacheInfo = null;
                    try {
                        cacheInfo = new CacheInfo();
                        cacheInfo.cacheSize = cacheSize;
                        cacheInfo.packageName = pStats.packageName;
                        cacheInfo.name = pm.getApplicationInfo(pStats.packageName, 0).loadLabel(pm).toString();
                        cacheInfo.icon = pm.getApplicationInfo(pStats.packageName, 0).loadIcon(pm);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    //把消息的内容告知主线程去更新Ui
                    message.obj = cacheInfo;
                    mHanderl.sendMessage(message);
                }
            }
        };
        try {
            //通过反射获取到类的对象
            Class<?> clazz = Class.forName("android.content.pm.PackageManager");
            Method clazzMethod = clazz.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            //方法的调用者以及调用的参数
            clazzMethod.invoke(pm, packageName, mStatsObserver);
        } catch (Exception e) {

        }
    }

    class CacheInfo {
        public String name;
        public String packageName;
        public Drawable icon;
        public long cacheSize;
    }

    /**
     * 初始化控件
     */
    private void initView() {
        bt_cache_clear = (Button) findViewById(R.id.bt_cache_clear);
        bt_cache_clear.setOnClickListener(this);
        tv_cache = (TextView) findViewById(R.id.tv_cache);
        layout_item = (LinearLayout) findViewById(R.id.layout_item);
        pb_cache = (ProgressBar) findViewById(R.id.pb_cache);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_cache_clear:
                //点击一键清理的按钮
                clearCaches();
                break;
        }
    }

    /**
     * 点击一键清理的按钮
     */
    private void clearCaches() {
        try {
            Class<?> clazz = Class.forName("android.content.pm.PackageManager");
            Method clazzMethod = clazz.getMethod("freeStorageAndNotify", long.class, IPackageDataObserver.class);
            clazzMethod.invoke(pm, Long.MAX_VALUE, new IPackageDataObserver.Stub() {
                @Override
                public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                    //当缓存清理成功后
                    Message message = Message.obtain();
                    message.what = CACHE_CLEAR;
                    mHanderl.sendMessage(message);
                }
            });


        }catch (Exception e){

        }
    }
}
