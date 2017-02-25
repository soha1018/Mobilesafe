package com.itsoha.mobilesafe.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itsoha.mobilesafe.R;
import com.itsoha.mobilesafe.Utils.ConstanVlauel;
import com.itsoha.mobilesafe.Utils.SpUtils;

/**
 * 手机防盗界面3
 * Created by Administrator on 2017/2/20.
 */
public class Setup3Activity extends Activity{

    private static final String TAG = "Setup3Activity";
    private EditText et_phone_setup3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);


        initUi();
    }

    /**
     * 初始化Ui
     */
    private void initUi() {
        et_phone_setup3 = (EditText) findViewById(R.id.et_phone_setup3);

        String contact = SpUtils.getString(getApplicationContext(), ConstanVlauel.CONTACT_PHONE, "");
        et_phone_setup3.setText(contact);

        Button btn_select_phone = (Button) findViewById(R.id.btn_select_phone);

        btn_select_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectContactActivity.class);
                startActivityForResult(intent,0);
            }
        });
    }

    /**
     * 接收返回的电话号码
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //返回的数据不为空
        if (data != null){

            String phone = data.getStringExtra("phone");
            Log.i(TAG, "onActivityResult: "+phone);
            //对返回的数据进行处理
            String replace = phone.replace(" ", "");
            replace.trim();
            SpUtils.putString(getApplicationContext(), ConstanVlauel.CONTACT_PHONE,replace);

            et_phone_setup3.setText(replace);
            super.onActivityResult(requestCode, resultCode, data);
        }

    }


    //跳转到下一个界面
    public void nextPage(View view){
        String phone = SpUtils.getString(getApplicationContext(), ConstanVlauel.CONTACT_PHONE, "");
        if (!TextUtils.isEmpty(phone)){
            Intent intent = new Intent(this, Setup4Activity.class);
            startActivity(intent);

            finish();
        }else {
            Toast.makeText(this, "请输入电话号码", Toast.LENGTH_SHORT).show();
        }
        
    }
    //跳转到上一页
    public void prePage(View view){
        Intent intent = new Intent(this, Setup2Activity.class);
        startActivity(intent);
        finish();
    }
}
