package com.itsoha.mobilesafe.Service;


import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ServiceCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;

import com.itsoha.mobilesafe.Utils.ConstanVlauel;
import com.itsoha.mobilesafe.Utils.SpUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/2/28.
 */

public class LocationService extends Service {
    private static final String TAG = "LocationService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //一个类表示应用程序提供商标准选择一个位置。
        Criteria criteria = new Criteria();
        //提示应用者是否产生货币成本
        criteria.setCostAllowed(true);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //选择最优的网络地址
        String bestProvider = locationManager.getBestProvider(criteria, true);

        Log.i(TAG, "onCreate: 服务开启成功");


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "onCreate: 权限获取失败");
        } else {
            Log.i(TAG, "onCreate: 权限获取成功");
            MyLocationListener myLocationListener = new MyLocationListener();
            locationManager.requestLocationUpdates(bestProvider, 100, 2, myLocationListener);
        }

    }


    class MyLocationListener implements LocationListener {

        //当位置发生改变
        @Override
        public void onLocationChanged(Location location) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            Log.i(TAG, "onLocationChanged: " + "经度：" + longitude + "，纬度：" + latitude);

            //给用户发送消息
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(SpUtils.getString(getApplicationContext(), ConstanVlauel.CONTACT_PHONE, ""),
                    null, "经度：" + longitude + "，纬度：" + latitude, null, null);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
