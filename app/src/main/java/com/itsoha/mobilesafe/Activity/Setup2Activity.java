package com.itsoha.mobilesafe.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.itsoha.mobilesafe.R;
import com.itsoha.mobilesafe.Utils.ConstanVlauel;
import com.itsoha.mobilesafe.Utils.SpUtils;
import com.itsoha.mobilesafe.View.SettingItemView;

/**
 * 手机防盗绑定sim卡的界面
 * Created by Administrator on 2017/2/20.
 */
public class Setup2Activity extends BaseSetupActivity {

    private SettingItemView siv_bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        siv_bound = (SettingItemView) findViewById(R.id.siv_bound);

        //判断sp的节点是否为空
        String sim = SpUtils.getString(this, ConstanVlauel.SIM_NUMBER, "");
        if (TextUtils.isEmpty(sim)) {
            siv_bound.setCheck(false);
        } else {
            siv_bound.setCheck(true);
        }
        //点击事件
        siv_bound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = siv_bound.isCheck();
                if (!check) {

                    //存储sim卡的序列号
                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String simSerialNumber = telephonyManager.getSimSerialNumber();
                    SpUtils.putString(getApplicationContext(), ConstanVlauel.SIM_NUMBER, simSerialNumber);

                    siv_bound.setCheck(!check);
                } else {
                    //删除节点
                    siv_bound.setCheck(!check);
                    SpUtils.remove(getApplicationContext(), ConstanVlauel.SIM_NUMBER);
                }
            }
        });
    }

    @Override
    public void showNextPage() {
        String simNumber = SpUtils.getString(this, ConstanVlauel.SIM_NUMBER, "");
        if (!TextUtils.isEmpty(simNumber)) {

            Intent intent = new Intent(this, Setup3Activity.class);
            startActivity(intent);

            finish();

            //平移的效果
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        } else {
            Toast.makeText(this, "请绑定sim卡", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showPrePage() {
        Intent intent = new Intent(this, Setup1Activity.class);
        startActivity(intent);
        finish();

        //平移的效果
        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);

    }


}
