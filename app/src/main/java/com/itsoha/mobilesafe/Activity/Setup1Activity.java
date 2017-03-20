package com.itsoha.mobilesafe.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import com.itsoha.mobilesafe.R;
import com.itsoha.mobilesafe.Receiver.BootReciver;
import com.itsoha.mobilesafe.Receiver.SmsReceiver;

/**
 * Created by Administrator on 2017/2/18.
 */
public class Setup1Activity extends BaseSetupActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);

    }



    @Override
    public void showNextPage() {
        Intent intent = new Intent(this, Setup2Activity.class);
        startActivity(intent);

        //平移的效果
        overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
        finish();
    }

    @Override
    public void showPrePage() {

    }


}
