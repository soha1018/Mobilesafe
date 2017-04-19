package com.itsoha.mobilesafe.Applecation;

import android.app.Application;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * 全局的类捕获异常
 * Created by Administrator on 2017/4/19.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //定义捕获异常日志的文件
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "errorSoha.log";
        final File file = new File(path);
        //对未捕获的异常做处理
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                //如果文件等于空就创建一个新的文件
                if (file.exists()){
                    file.mkdirs();
                }
                try {
                    PrintStream printStream = new PrintStream(file);
                    e.printStackTrace(printStream);
                    printStream.close();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }

                //终止程序
                System.exit(0);
            }
        });
    }
}
