package com.itsoha.mobilesafe.Utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * 获取服务开启状态的工具类
 * Created by Administrator on 2017/3/18.
 */

public class IsServiceRunning {
    /**
     * 获取服务的开启状态
     * @param context 上下文
     * @param serviceName 服务的名称
     * @return 返回true服务开启，反之服务关闭
     */
    public static boolean getServiceState(Context context,String serviceName){
        
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(5);

        for (ActivityManager.RunningServiceInfo info:runningServices
        ){
            if (serviceName.equals(info.service.getClassName())){
                return true;
            }
        }
        return false;
    }
}
