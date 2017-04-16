package com.itsoha.mobilesafe.Receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.itsoha.mobilesafe.Service.UpdateWidgetService;

/**
 * 窗口小部件的广播
 * Created by Administrator on 2017/4/15.
 */

public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    /**
     * 创建一个窗体小部件的方法
     * @param context 上下文
     */
    @Override
    public void onEnabled(Context context) {
        context.startService(new Intent(context,UpdateWidgetService.class));
        super.onEnabled(context);
    }

    /**
     * 创建多个小部件调用
     * @param context 上下文
     * @param appWidgetManager
     * @param appWidgetIds
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context,UpdateWidgetService.class));
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    /**
     * 窗体小部件宽高发生改变的时候或者创建的时候调用此方法
     * @param context
     * @param appWidgetManager
     * @param appWidgetId
     * @param newOptions
     */
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        context.startService(new Intent(context,UpdateWidgetService.class));
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    /**
     * 删除一个窗体小部件的时候调用此方法
     * @param context
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    /**
     * 删除最后一个窗体小部件调用的方法
     * @param context
     * @param appWidgetIds
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        context.stopService(new Intent(context,UpdateWidgetService.class));
        super.onDeleted(context, appWidgetIds);
    }
}
