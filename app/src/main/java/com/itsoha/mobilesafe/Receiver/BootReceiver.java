package com.itsoha.mobilesafe.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.itsoha.mobilesafe.Utils.ConstanVlauel;
import com.itsoha.mobilesafe.Utils.SpUtils;

/**
 * 监听手机重启后SIM卡是否发生变化
 * Created by Administrator on 2017/2/26.
 */

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        //获取手机SIM卡
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simSerialNumber = telephonyManager.getSimSerialNumber();

        String sim = SpUtils.getString(context, ConstanVlauel.SIM_NUMBER, "");
        //如果两个值不匹配
        if (!sim.equals(simSerialNumber)) {
            Log.i(TAG, "onReceive: " + "您的手机被偷走了");
            SmsManager smsManager = SmsManager.getDefault();
            String phone = SpUtils.getString(context, ConstanVlauel.CONTACT_PHONE, "");
            if (!TextUtils.isEmpty(phone)) {
                smsManager.sendTextMessage(phone, null, "您的手机被偷走了", null, null);
            }
        }
    }
}
