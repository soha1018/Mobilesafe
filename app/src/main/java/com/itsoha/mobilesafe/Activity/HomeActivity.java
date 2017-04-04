package com.itsoha.mobilesafe.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.itsoha.mobilesafe.R;
import com.itsoha.mobilesafe.Utils.ConstanVlauel;
import com.itsoha.mobilesafe.Utils.Md5Util;
import com.itsoha.mobilesafe.Utils.SpUtils;

/**
 * 主界面的Activity
 * Created by Administrator on 2017/2/14.
 */
public class HomeActivity extends Activity {

    private static final String TAG = "HomeActivity";
    private GridView gr_home;
    private String[] mTitleStrs;
    private int[] mDrawableIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initUi();

        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mTitleStrs = new String[]{
                "手机防盗", "通信卫士", "软件管理", "进程管理", "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"
        };

        mDrawableIds = new int[]{
                R.drawable.home_safe, R.drawable.home_callmsgsafe,
                R.drawable.home_apps, R.drawable.home_taskmanager,
                R.drawable.home_netmanager, R.drawable.home_trojan,
                R.drawable.home_sysoptimize, R.drawable.home_tools, R.drawable.home_settings
        };

        //对数据进行适配
        gr_home.setAdapter(new MyAdapter());
        //单击条目跳转到对应的Activity
        gr_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //手机防盗的界面
                        showDialog();
                        break;
                    case 1:
                        //通信卫士的界面
                        startActivity(new Intent(getApplicationContext(),BlackNumberActivity.class));
                        break;
                    case 2:
                        //软件管理的Activity
                        startActivity(new Intent(getApplicationContext(),AppManageActivity.class));
                        break;
                    case 7:
                        //高级工具的界面
                        Intent intent = new Intent(getApplicationContext(), AToolActivity.class);
                        startActivity(intent);
                        break;
                    case 8:
                        //设置的界面
                        Intent intent1 = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(intent1);
                        break;
                }
            }
        });

    }

    /**
     * 弹出密码对话框
     */
    private void showDialog() {

        String pwd = SpUtils.getString(this, ConstanVlauel.MOBILE_SAFE_PWD, "");

        if (TextUtils.isEmpty(pwd)){
            Log.i(TAG, "showDialog: "+pwd);

            //设置密码
            showSetPwd();
        }else {
            //确认密码
            showConfirmPwd();

        }

    }

    /**
     * 确认密码
     */
    private void showConfirmPwd() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //创建一个自定义的对话框
        final AlertDialog alertDialog = builder.create();
        final View view = View.inflate(this,R.layout.dialog_confirm_pwd,null);
        alertDialog.setView(view);
        alertDialog.show();

        //找到需要的控件
        Button bt_commit_pwd = (Button) view.findViewById(R.id.bt_commit_pwd);
        Button bt_undo_pwd = (Button) view.findViewById(R.id.bt_undo_pwd);

        //点击确认验证密码，跳转到手机防盗的界面
        bt_commit_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView et_set_pwd = (TextView) view.findViewById(R.id.et_set_pwd);

                String pwd = et_set_pwd.getText().toString();

                //对输入的密码也进行加密，然后进行比较
                String md5Pwd = Md5Util.encoder(pwd);
                if(!TextUtils.isEmpty(pwd)){
                    //去工具类查数据
                    String confirmPwd = SpUtils.getString(getApplicationContext(), ConstanVlauel.MOBILE_SAFE_PWD, "");
                    if (md5Pwd.equals(confirmPwd)){

                        Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
                        startActivity(intent);

                        alertDialog.dismiss();

                    }else {
                        Toast.makeText(HomeActivity.this, "确认密码错误，请重新输入", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(HomeActivity.this, "密码不能为空，请重新输入", Toast.LENGTH_SHORT).show();
                }

            }
        });
        //点击对话框的取消
        bt_undo_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    /**
     * 设置密码
     */
    private void showSetPwd() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //创建一个自定义的对话框
        final AlertDialog alertDialog = builder.create();
        final View view = View.inflate(this,R.layout.dialog_set_pwd,null);
        alertDialog.setView(view);
        alertDialog.show();

        //找到需要的控件
        Button bt_commit_pwd = (Button) view.findViewById(R.id.bt_commit_pwd);
        Button bt_undo_pwd = (Button) view.findViewById(R.id.bt_undo_pwd);
        //点击确认验证密码，跳转到手机防盗的界面
        bt_commit_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView et_set_pwd = (TextView) view.findViewById(R.id.et_set_pwd);
                TextView et_confirm_pwd = (TextView) view.findViewById(R.id.et_confirm_pwd);

                String pwd = et_set_pwd.getText().toString();
                String confirmPwd = et_confirm_pwd.getText().toString();
                if(!TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(confirmPwd)){
                    if (pwd.equals(confirmPwd)){

                        Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
                        startActivity(intent);
                        alertDialog.dismiss();

                        String md5Pwd = Md5Util.encoder(pwd);
                        SpUtils.putString(getApplicationContext(),ConstanVlauel.MOBILE_SAFE_PWD,md5Pwd);
                    }else {
                        Toast.makeText(HomeActivity.this, "确认密码错误，请重新输入", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(HomeActivity.this, "密码不能为空，请重新输入", Toast.LENGTH_SHORT).show();
                }

            }
        });
        //点击对话框的取消
        bt_undo_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mTitleStrs.length;
        }

        @Override
        public Object getItem(int position) {
            return mTitleStrs[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
            TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
            ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);

            tv_title.setText(mTitleStrs[position]);
            iv_icon.setBackgroundResource(mDrawableIds[position]);

            return view;
        }
    }

    /**
     * 初始化控件
     */
    private void initUi() {
        gr_home = (GridView) findViewById(R.id.gv_home);
    }
}
