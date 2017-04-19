package com.itsoha.mobilesafe.Activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itsoha.mobilesafe.Engine.VirusDao;
import com.itsoha.mobilesafe.R;
import com.itsoha.mobilesafe.Utils.Md5Util;

import java.util.ArrayList;
import java.util.List;

/**
 * 杀毒的界面
 * Created by Administrator on 2017/4/17.
 */

public class AntiVirusActivity extends AppCompatActivity {
    private ImageView iv_scanning;
    /**
     * 正在扫描应用
     */
    private TextView tv_name;
    private ProgressBar pb_bar;
    private LinearLayout ll_add_text;
    private int index = 0;
    private final int SCANINFO = 100;
    private final int SCANINFO_FINISH = 200;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SCANINFO:
                    //扫描进行中
                    ScanInfo scanInfo = (ScanInfo) msg.obj;
                    tv_name.setText(scanInfo.names);
                    TextView textView = new TextView(getApplicationContext());
                    if (scanInfo.isVirus){
                        textView.setTextColor(Color.RED);
                        textView.setText("发现病毒："+scanInfo.names);
                    }else {
                        textView.setTextColor(Color.BLACK);
                        textView.setText("扫描安全："+scanInfo.names);
                    }
                    //从顶部插入数据
                    ll_add_text.addView(textView,0);
                    break;
                case SCANINFO_FINISH:
                    tv_name.setText("扫描完成");
                    //停止动画
                    iv_scanning.clearAnimation();

                    //卸载有病毒的应用
                    for (ScanInfo info:mVirusInfoList
                         ) {
                        String packageName = info.packageName;

                        Intent intent = new Intent("android.intent.action.DELETE");
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setData(Uri.parse("package:"+packageName));
                        startActivity(intent);
                    }
                    break;
            }

        }
    };
    private RotateAnimation mRotateAnimation;
    private List<ScanInfo> mVirusInfoList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anit_virus);
        //初始化控件
        initView();
        //初始化旋转的动画
        initAnimation();
        //遍历手机所有应用查看是否有病毒
        checkVirus();
    }

    /**
     * 遍历手机所有应用查看是否有病毒
     */
    private void checkVirus() {
        new Thread(){
            @Override
            public void run() {
                //获取数据库中所有病毒的MD5
                List<String> virusList = VirusDao.getVirusList();
                PackageManager pm = getPackageManager();
                //获取已安装的应用签名和卸载残留应用的签名
                List<PackageInfo> packageList = pm.getInstalledPackages(PackageManager.GET_SIGNATURES + PackageManager.GET_UNINSTALLED_PACKAGES);
                //创建病毒的集合
                mVirusInfoList = new ArrayList<ScanInfo>();
                //记录所有应用的集合
                List<ScanInfo> scanInfoList = new ArrayList<ScanInfo>();
                //获取进度条的最大值
                pb_bar.setMax(packageList.size());
                for (PackageInfo info:packageList
                     ) {
                    ScanInfo scanInfo = new ScanInfo();
                    //返回签名文件的数组
                    Signature[] signatures = info.signatures;
                    //获取签名数组的第一位于指定的MD5进行比较
                    Signature signature = signatures[0];
                    String charsString = signature.toCharsString();
                    String encoder = Md5Util.encoder(charsString);
                    if (virusList.contains(encoder)){
                        //记录病毒
                        scanInfo.isVirus = true;
                        mVirusInfoList.add(scanInfo);
                    }else {
                        scanInfo.isVirus = false;
                    }
                    scanInfo.names = info.applicationInfo.loadLabel(pm).toString();
                    scanInfo.packageName = info.packageName;
                    scanInfoList.add(scanInfo);
                    //每次更新的进度条进度
                    index++;
                    pb_bar.setProgress(index);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //发送消息告知主线程更新Ui
                    Message obtain = Message.obtain();
                    obtain.what = SCANINFO;
                    obtain.obj = scanInfo;
                    mHandler.sendMessage(obtain);
                }
                //扫描完成后告知主线程
                Message message = Message.obtain();
                message.what = SCANINFO_FINISH;
                mHandler.sendMessage(message);
            }
        }.start();
    }
    class ScanInfo{
        public  String names;
        public  String packageName;
        public  boolean isVirus;
    }

    /**
     * 初始化旋转的动画
     */
    private void initAnimation() {
        mRotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setDuration(1000);
        //指定动画无限旋转
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        //指定动画结束后的位置
        mRotateAnimation.setFillAfter(true);

        iv_scanning.startAnimation(mRotateAnimation);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        iv_scanning = (ImageView) findViewById(R.id.iv_scanning);
        tv_name = (TextView) findViewById(R.id.tv_name);
        pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
        ll_add_text = (LinearLayout) findViewById(R.id.ll_add_text);
    }
}
