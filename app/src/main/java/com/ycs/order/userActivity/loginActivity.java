package com.ycs.order.userActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.ycs.order.R;
import com.ycs.order.Util.LoadingUtil;
import com.ycs.order.Util.SharedPreferencesUtil;
import com.ycs.order.model.MyUser;
import com.ycs.order.shopActivity.businessLoginActivity;

import java.io.File;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class loginActivity extends AppCompatActivity {
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this,"682dba275359d04511948d626bff513f");
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        if (ContextCompat.checkSelfPermission(loginActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(loginActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},199);

        }
        TextView tvb=(TextView)findViewById(R.id.business);
        tvb.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(loginActivity.this, businessLoginActivity.class);
                startActivity(intent);

            }
        });
        TextView tvr=(TextView)findViewById(R.id.register);
        tvr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(loginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        TextView tvf=(TextView)findViewById(R.id.forget);
        tvf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(loginActivity.this, FindpActivity.class);
                startActivity(intent);
            }
        });
        final EditText edac=(EditText)findViewById(R.id.account);
        final EditText edpwd=(EditText)findViewById(R.id.pwd);
        final TextView login=(TextView)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(edac.getText().toString().length()<1||edpwd.getText().toString().length()<6){
                    Toast.makeText(loginActivity.this,"请输入完整账户名与密码",Toast.LENGTH_LONG).show();
                }else{
                    LoadingUtil.Loading_show(loginActivity.this);
                    login.setEnabled(false);
                    handler = new Handler(){
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 111) {
                                MyUser user=new MyUser();
                                //此处替换为你的用户名
                                user.setUsername(edac.getText().toString());
                                //此处替换为你的密码
                                user.setPassword(edpwd.getText().toString());
                                user.setType("0");
                                user.login(new SaveListener<BmobUser>() {
                                    @Override
                                    public void done(BmobUser bmobUser, BmobException e) {
                                        if (e == null) {
                                            MyUser user = MyUser.getCurrentUser(MyUser.class);
                                            if(user.getType().equals("1")){
                                                Toast.makeText(loginActivity.this, "登录失败（此账户为商家账户，请到用户界面登陆）", Toast.LENGTH_LONG).show();
                                                LoadingUtil.Loading_close();
                                                login.setEnabled(true);
                                            }else{
                                                File file=new File(Environment.getExternalStorageDirectory()+"/userImage.jpg");
                                                try {
                                                    if (file.exists()) {
                                                        file.delete();
                                                    }
                                                } catch (Exception ex) {
                                                    ex.printStackTrace();
                                                }
                                                String account=edac.getText().toString();
                                                String pwd=edpwd.getText().toString();
                                                Toast.makeText(loginActivity.this, "登录成功" , Toast.LENGTH_LONG).show();
                                                SharedPreferencesUtil nS;
                                                nS=new SharedPreferencesUtil(loginActivity.this,"user");
                                                nS.setValue("account",account);
                                                nS.setValue("pwd",pwd);
                                                nS.setValue("type","0");
                                                Intent intent =new Intent(loginActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                                LoadingUtil.Loading_close();
                                            }

                                        } else {

                                            LoadingUtil.Loading_close();
                                            Toast.makeText(loginActivity.this, "登录失败（" + e.getMessage()+"）", Toast.LENGTH_LONG).show();
                                            login.setEnabled(true);
                                        }
                                    }
                                });
                            }else if(msg.what==222){
                                Toast.makeText(loginActivity.this,"登录失败(账号密码错误)",Toast.LENGTH_SHORT).show();
                                LoadingUtil.Loading_close();
                                login.setEnabled(true);
                            }else if(msg.what==333){
                                LoadingUtil.Loading_close();
                                login.setEnabled(true);
                                Toast.makeText(loginActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    };
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                Looper.prepare();
                                String url="http://"+getString(R.string.host)+":8080/login";
                                OkHttpClient okHttpClient = new OkHttpClient();
                                FormBody formBody = new FormBody.Builder().add("name", edac.getText().toString()).add("pwd",edpwd.getText().toString()).build();
                                Request request = new Request.Builder()
                                        .url(url)
                                        .post(formBody)
                                        .build();
                                Response response = okHttpClient.newCall(request).execute();
                                List<MyUser> users = JSONArray.parseArray(response.body().string(),MyUser.class);

                                if(users.size() == 0){
                                    Message msg=new Message();
                                    msg.what=222;//通知UI线程Json解析完成
                                    handler.sendMessage(msg);
                                }else{
                                    Message msg=new Message();
                                    msg.what=111;//通知UI线程Json解析完成
                                    handler.sendMessage(msg);
                                }
                                Looper.loop();
                            } catch (Exception e) {

                                Message msg=new Message();
                                msg.what=333;//通知UI线程Json解析完成
                                handler.sendMessage(msg);
                                msg.obj=e.getMessage();
                                Log.e("错误",e.getMessage());
                            }
                        }
                    }).start();



                }

            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoadingUtil.Loading_close();
    }
}
