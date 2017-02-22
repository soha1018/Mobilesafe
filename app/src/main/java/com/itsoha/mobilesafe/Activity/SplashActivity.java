package com.itsoha.mobilesafe.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.itsoha.mobilesafe.R;
import com.itsoha.mobilesafe.Utils.ConstanVlauel;
import com.itsoha.mobilesafe.Utils.SpUtils;
import com.itsoha.mobilesafe.Utils.StreamUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 启动界面
 */
public class SplashActivity extends AppCompatActivity {
    private static final int ENTER_HOME = 104;
    public static final int JSON_ERROR = 103;
    public static final int IO_ERROR = 102 ;
    public static final int URL_ERROR = 101;
    public static final int UPDATA_VERSION = 100;

    private static final String TAG = "SplashActivity";
    private TextView tv_version_name;
    private int mLocalVersionCode;
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //程序需要更新
                case UPDATA_VERSION:
                    showUpdataDialog();
                    break;
                //进入主界面
                case ENTER_HOME:
                    enterHome();
                    break;
                default:
                    enterHome();
                    Toast.makeText(SplashActivity.this, "请检查您的网路连接", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private String mVersionDes;
    private String mDownloadUrl;
    private RelativeLayout rl_root;

    /**
     * 弹出对话框来提示用户更新应用
     */
    private void showUpdataDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.alert_dialog);
        builder.setTitle("版本更新");
        builder.setMessage(mVersionDes);
        builder.setPositiveButton("立即下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //去官网下载
                downloadApk();
            }
        });
        builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //进入主程序
                enterHome();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                //进入主界面
                enterHome();
                //关闭对话框
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * 下载应用Apk的方法
     */
    private void downloadApk() {
        //判断外部存储卡是否挂载
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            //保存文件的路劲
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "mobilesafe.apk";
            HttpUtils httpUtils = new HttpUtils();
            httpUtils.download(mDownloadUrl, path, new RequestCallBack<File>() {
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    //下载成功
                    File file = responseInfo.result;
                    Log.i(TAG, "onSuccess: "+"下载成功");
                    //提示用户安装
                    installApk(file);
                }

                @Override
                public void onFailure(HttpException e, String s) {

                }
            });
        }
    }

    /**
     * 提示用户安装
     * @param file 安装文件
     */
    private void installApk(File file) {
        //接收一个Action
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        startActivityForResult(intent,0);
    }

    //开启一个Activity返回结果调用的方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 跳转到主界面
     */
    private void enterHome() {
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //初始化Ui
        initUi();
        //初始化数据
        initData();
        //初始化动画
        initAnimation();
    }

    /**
     * 初始化动画
     */
    private void initAnimation() {
        //透明动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
        //设置时间
        alphaAnimation.setDuration(3000);
        rl_root.setAnimation(alphaAnimation);

    }

    /**
     * 初始化数据
     */
    private void initData() {
        //给TextView设置数据
        tv_version_name.setText("版本名称:" + getVersionName());
        //获取服务器的版本进行比对
        //1.先获取本地的版本代码
        mLocalVersionCode = getVersionCode();

        //2.判断是否开启自动更新的开关
        if(SpUtils.getBoolean(this, ConstanVlauel.IS_CHECK,false)){

            //开启就获取服务器的代码
            checkVersion();
        }else {
            //关闭就发送消息，延迟处理
            mHandler.sendEmptyMessageDelayed(ENTER_HOME,3500);
        }
    }

    /**
     * 检查版本号
     */
    private void checkVersion() {
        new Thread(){


            @Override
            public void run() {
                //设置开始的时间
                long startTime = System.currentTimeMillis();
                Message message = Message.obtain();
                try {
                    URL url = new URL("http://192.168.1.102:8080/updata74.json");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    //设置超时时间
                    connection.setConnectTimeout(3000);
                    //设置读取超时时间
                    connection.setReadTimeout(3000);
                    int code = connection.getResponseCode();
                    //请求成功
                    if (code == 200){
                        InputStream inputStream = connection.getInputStream();
                        String json = StreamUtils.StreamToString(inputStream);

                        //通过Json解析数据
                        JSONObject jsonObject = new JSONObject(json);

                        String versionName = jsonObject.getString("versionName");
                        mVersionDes = jsonObject.getString("versionDes");
                        String versionCode = jsonObject.getString("versionCode");
                        mDownloadUrl = jsonObject.getString("downloadUrl");

                        if (mLocalVersionCode<Integer.parseInt(versionCode)){
                            //提示用户更新版本
                            message.what = UPDATA_VERSION;
                        }else {
                            message.what = ENTER_HOME;
                            //进主程序
                        }
                    }
                } catch (MalformedURLException e) {
                    message.what = URL_ERROR;
                    e.printStackTrace();
                } catch (IOException e) {
                    message.what = IO_ERROR;
                    e.printStackTrace();
                } catch (JSONException e) {
                    message.what = JSON_ERROR;
                    e.printStackTrace();
                }finally {
                    //设置结束的时间
                    long endTime = System.currentTimeMillis();
                    if ((endTime - startTime)<4000){
                        SystemClock.sleep(4000-(endTime - startTime));
                    }
                    //发送消息
                    mHandler.sendMessage(message);
                }
            }
        }.start();
    }

    /**
     * 初始化Ui
     */
    private void initUi() {
        tv_version_name = (TextView) findViewById(R.id.tv_version_name);
        rl_root = (RelativeLayout) findViewById(R.id.rl_root);
    }

    /**
     * 获取应用的版本：再清单文件在
     * @return 返回应用的版本 返回null代表异常
     */
    public String getVersionName() {
        //包的管理者
        PackageManager packageManager = getPackageManager();
        try {
            //返回整体包的信息
            PackageInfo info = packageManager.getPackageInfo(getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取本地版本的代码
     * @return 如果异常就返回零
     */
    public int getVersionCode() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo info = packageManager.getPackageInfo(getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
