package com.itsoha.mobilesafe.Engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.itsoha.mobilesafe.Bean.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用信息的查询类
 * Created by Administrator on 2017/4/2.
 */

public class AppInfoProvider {
    /**
     * 返回当前手机所有应用的相关信息
     * @param context 上下文
     */
    public static List<AppInfo> getInfoList(Context context){
        List<AppInfo> appInfoList = new ArrayList<>();
        //获取包的管理者
        PackageManager packageManager = context.getPackageManager();
        //获取应用安装再手机上信息的相关集合
        List<PackageInfo> list = packageManager.getInstalledPackages(0);
        //循环遍历相关应用信息的集合
        for (PackageInfo packageInfo:list
             ) {
            AppInfo appInfo = new AppInfo();
            //获取应用程序的包名
            appInfo.setPackageName(packageInfo.packageName);

            //应用程序的名称
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            String name = applicationInfo.loadLabel(packageManager).toString();
            appInfo.setName(name);
            appInfo.setIcon(applicationInfo.loadIcon(packageManager));
            //判断是否为系统应用
            if ((applicationInfo.flags & applicationInfo.FLAG_SYSTEM)==ApplicationInfo.FLAG_SYSTEM){
                //系统应用
                appInfo.setSystem(true);
            }else {
                //用户应用
                appInfo.setSystem(false);
            }
            //判断是否为SD卡的应用
            if ((applicationInfo.flags & applicationInfo.FLAG_EXTERNAL_STORAGE) == applicationInfo.FLAG_EXTERNAL_STORAGE){
                appInfo.setSDCard(true);
            }else {
                appInfo.setSDCard(false);
            }
            appInfoList.add(appInfo);
        }
        return appInfoList;
    }
}
