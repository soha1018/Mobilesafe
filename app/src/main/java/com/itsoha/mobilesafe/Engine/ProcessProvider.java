package com.itsoha.mobilesafe.Engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Debug;
import android.util.Log;

import com.itsoha.mobilesafe.Activity.ProcessActivity;
import com.itsoha.mobilesafe.Bean.ProcessInfo;
import com.itsoha.mobilesafe.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 进程管理的一些方法
 * Created by Administrator on 2017/4/6.
 */

public class ProcessProvider {
    /**
     * 获取正在运行进程的总数
     *
     * @param context
     */
    public static long getProcessCount(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        return runningAppProcesses.size();
    }

    /**
     * 获取可用的磁盘空间
     */
    public static long getAvailSpace(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(info);
        return info.availMem;
    }

    /**
     * 读取所有内存空间大小
     *
     * @return
     */
    public static long getMemory() {
        FileReader reader = null;
        BufferedReader bufferedReader = null;

        try {
            reader = new FileReader("proc/meminfo");
            bufferedReader = new BufferedReader(reader);
            String readLine = bufferedReader.readLine();
            //读取第一行的内容转换成字符数组，依次比较他们的数据
            char[] chars = readLine.toCharArray();
            StringBuffer buffer = new StringBuffer();
            for (char c : chars
                    ) {
                if (c >= '0' && c <= '9') {
                    buffer.append(c);
                }
            }
            return Long.parseLong(buffer.toString()) * 1024;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null && bufferedReader != null) {
                try {
                    reader.close();
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    /**
     * 获取正在运行进程的所有信息
     * @param context 下午文
     * @return 返回集合中包含的进程信息
     */
    public static List<ProcessInfo> getProcessInfo(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        List<ProcessInfo> list = new ArrayList<>();

        List<ActivityManager.RunningAppProcessInfo> infoList = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : infoList) {
            ProcessInfo Info = new ProcessInfo();
            //获取应用程序的包名
            Info.packageName = processInfo.processName;
            //根据进程的PID获取进程的信息
            Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{processInfo.pid});
            //对PID数组中的数据进行抽取
            Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
            //获取已使用的大小
            Info.memSize = memoryInfo.getTotalPrivateDirty() * 1024;

            try {
                //获取进程的名称，图标等一系列信息
                ApplicationInfo applicationInfo = pm.getApplicationInfo(processInfo.processName, 0);
                Info.names = applicationInfo.loadLabel(pm).toString();
                Info.icon = applicationInfo.loadIcon(pm);
                //判断应用程序是否是系统的进程
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM){
                    Info.isSystem = true;
                }else {
                    Info.isSystem = false;
                }
            } catch (PackageManager.NameNotFoundException e) {
                 //如遇到异常情况，获取不到包名等
                Info.names = processInfo.processName;
                Info.icon = context.getResources().getDrawable(R.mipmap.ic_launcher);
                Info.isSystem = true;


            }
            list.add(Info);
        }
        return list;
    }


    /**
     * 清除进程
     * @param ctx 上下文
     * @param info
     */
    public static void killProcess(Context ctx,ProcessInfo info) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(info.getPackageName());
    }


    /**
     * 锁屏清理手机的进程
     * @param context 上下文
     */
    public static void killAllProcess(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo info:runningAppProcesses
             ) {
            if (info.processName.equals(context.getPackageName())){
                continue;
            }
            am.killBackgroundProcesses(info.processName);
        }

    }
}
