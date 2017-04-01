package com.itsoha.mobilesafe.Receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.itsoha.mobilesafe.R;
import com.itsoha.mobilesafe.Service.LocationService;
import com.itsoha.mobilesafe.Utils.ConstanVlauel;
import com.itsoha.mobilesafe.Utils.SpUtils;

/**
 * Created by Administrator on 2017/2/27.
 */

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";
    private DevicePolicyManager mDeviceManager;


    @Override
    public void onReceive(Context context, Intent intent) {

        boolean open_safety = SpUtils.getBoolean(context, ConstanVlauel.OPEN_SAFETY, false);

        if (open_safety){
            //获取设备管理
            mDeviceManager = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            //获取短信的内容
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            for (Object object: pdus
                 ) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[])object);

                //短信的地址
                String address = sms.getOriginatingAddress();
                Log.i(TAG, "onReceive: 电话号码  "+address);
                //短信的内容
                String body = sms.getMessageBody();
                //获取安全号码
                String phone = SpUtils.getString(context, ConstanVlauel.CONTACT_PHONE, "");

                //判断短信的内容是否是防盗的报警音乐
                if (address.equals(phone) && body.contains("#*alarm*#")){

                    //准备播放音乐
                    MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
                    //播放器循环
                    player.setLooping(true);
                    //开始播放音乐
                    player.start();
                }
                //判断短信的内容是否是防盗的发送位置
                if (address.equals(phone) && body.contains("#*location*#")){
                    //开启位置的服务
                    Intent intent1 = new Intent(context, LocationService.class);
                    context.startService(intent1);
                }

                //判断短信的内容是否需要一键锁屏
                if (address.equals(phone) && body.contains("#*lockscreen*#")){
                    mDeviceManager.lockNow();
                }

                //判断短信内容是否需要清除数据
                if (address.equals(phone) && body.contains("#*wipedata*#")){
                    //清除手机的数据
                    mDeviceManager.wipeData(0);
                    //清除SD卡的数据
                    mDeviceManager.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                }

            }
        }
    }
}
