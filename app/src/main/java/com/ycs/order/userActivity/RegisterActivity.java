package com.ycs.order.userActivity;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ycs.order.R;
import com.ycs.order.Util.LoadingUtil;
import com.ycs.order.Util.SharedPreferencesUtil;
import com.ycs.order.Util.Timedown;
import com.ycs.order.model.MyUser;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class RegisterActivity extends AppCompatActivity {
private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        final ImageView tvfanhui =(ImageView) findViewById(R.id.fanhui);
        final TextView tvnotice=(TextView)findViewById(R.id.r_notice);
        tvfanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        final TextView tvGetCode =(TextView) findViewById(R.id.r_getCode);
        final EditText tvphone=(EditText) findViewById(R.id.r_phone);
        final EditText tvcode=(EditText)findViewById(R.id.r_code);
        final EditText tvpwd=(EditText)findViewById(R.id.r_pwd);

        final Timedown timedown =new Timedown(60000,1000,tvGetCode,tvnotice);
        tvGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tvphone.getText().toString().length()!=11){
                    Toast.makeText(RegisterActivity.this,"请输入正确的手机号码",Toast.LENGTH_LONG).show();
                }else{
                    BmobQuery<BmobUser> categoryBmobQuery = new BmobQuery<>();
                    categoryBmobQuery.addWhereEqualTo("mobilePhoneNumber", tvphone.getText().toString());
                    categoryBmobQuery.findObjects(new FindListener<BmobUser>() {
                        @Override
                        public void done(List<BmobUser> object, BmobException e) {
                            if (e == null&&object.size()!=0) {
                                Toast.makeText(RegisterActivity.this, "账户已注册,请登录" , Toast.LENGTH_LONG).show();
                            } else if(e==null&&object.size()==0){

                                BmobSMS.requestSMSCode(tvphone.getText().toString(), "", new QueryListener<Integer>() {
                                    @Override
                                    public void done(Integer smsId, BmobException e) {
                                        if (e == null) {
                                            Toast.makeText(RegisterActivity.this,"发送验证码成功" + "\n",Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(RegisterActivity.this,"抱歉验证码发送失败（" + e.getErrorCode() + "-" + e.getMessage() + "）\n",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                timedown.start();
                            }
                            else{
                                Toast.makeText(RegisterActivity.this, "出错"+e.getMessage() , Toast.LENGTH_LONG).show();
                            }
                        }
                    });



                }
            }
        });
        final TextView tvRegister=(TextView)findViewById(R.id.r_register);
        tvRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                final String phone=tvphone.getText().toString();
                String code=tvcode.getText().toString();
                final String password=tvpwd.getText().toString();
                if(phone.length()!=11||code==""||password.length()<6){
                    Toast.makeText(RegisterActivity.this, "请输入完整验证码与密码", Toast.LENGTH_SHORT).show();
                }else {
                    if (tvRegister.getText().toString() == "使用该账户登陆") {
                        LoadingUtil.Loading_show(RegisterActivity.this);
                        MyUser user = new MyUser();
                        //此处替换为你的用户名
                        user.setUsername(phone);
                        //此处替换为你的密码
                        user.setPassword(password);
                        user.login(new SaveListener<MyUser>() {
                            @Override
                            public void done(MyUser bmobUser, BmobException e) {
                                if (e == null) {
                                    MyUser user = MyUser.getCurrentUser(MyUser.class);
                                    Toast.makeText(RegisterActivity.this, "登录成功", Toast.LENGTH_LONG).show();

                                    SharedPreferencesUtil nS;
                                    nS = new SharedPreferencesUtil(RegisterActivity.this, "user");
                                    nS.setValue("account", user.getUsername());
                                    nS.setValue("pwd", password);
                                    nS.setValue("type", "0");
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    LoadingUtil.Loading_close();

                                } else {
                                    LoadingUtil.Loading_close();
                                    Toast.makeText(RegisterActivity.this, "登录失败（" + e.getMessage() + "）", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }else{
                        LoadingUtil.Loading_show(RegisterActivity.this);


                        MyUser user = new MyUser();
                        //设置手机号码（必填）
                        user.setMobilePhoneNumber(phone);
                        //设置用户名，如果没有传用户名，则默认为手机号码
                        user.setUsername(phone);
                        //设置用户密码
                        user.setPassword(password);
                        //设置额外信息：此处为年龄
                        user.setType("0");
                        user.signOrLogin(code, new SaveListener<BmobUser>() {
                            @Override
                            public void done(final BmobUser user, BmobException e) {
                                if (e == null) {
                                    handler = new Handler(){
                                        @Override
                                        public void handleMessage(Message msg) {
                                            if (msg.what == 111) {
                                                LoadingUtil.Loading_close();
                                                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                                tvRegister.setText("使用该账户登陆");
                                            }else if(msg.what==222){
                                                LoadingUtil.Loading_close();
                                                MyUser myuser=new MyUser();
                                                myuser.setObjectId(user.getObjectId());
                                                myuser.delete(new UpdateListener() {
                                                    @Override
                                                    public void done(BmobException e) {
                                                        if(e==null){
                                                            Toast.makeText(RegisterActivity.this, "注册失败，后端错误", Toast.LENGTH_SHORT).show();
                                                        }else{
                                                            Toast.makeText(RegisterActivity.this, "注册失败，后端错误"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            }else if(msg.what==333){
                                                LoadingUtil.Loading_close();
                                                MyUser myuser=new MyUser();
                                                myuser.setObjectId(user.getObjectId());
                                                myuser.delete(new UpdateListener() {
                                                    @Override
                                                    public void done(BmobException e) {
                                                        if(e==null){
                                                            Toast.makeText(RegisterActivity.this, "注册失败，连接失败", Toast.LENGTH_SHORT).show();
                                                        }else{
                                                            Toast.makeText(RegisterActivity.this, "注册失败，连接失败"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    };
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try{
                                                Looper.prepare();
                                                String url="http://"+getString(R.string.host)+":8080/save";
                                                OkHttpClient okHttpClient = new OkHttpClient();
                                                FormBody formBody = new FormBody.Builder().add("name", phone).add("pwd",password).build();
                                                Request request = new Request.Builder()
                                                        .url(url)
                                                        .post(formBody)
                                                        .build();
                                                Response response = okHttpClient.newCall(request).execute();
                                                String oo=response.body().string();
                                                if(oo.equals("true")){
                                                    Message msg=new Message();
                                                    msg.what=111;//通知UI线程Json解析完成
                                                    handler.sendMessage(msg);
                                                }else {
                                                    Message msg = new Message();
                                                    msg.what = 222;  //通知UI线程Json解析完成
                                                    handler.sendMessage(msg);
                                                }
                                                Looper.loop();
                                            } catch (Exception e) {
                                                Message msg=new Message();
                                                msg.what=333;//通知UI线程Json解析完成
                                                msg.obj=e.getMessage();
                                                handler.sendMessage(msg);
                                                Log.e("错误",e.getMessage());
                                            }
                                        }
                                    }).start();



                                    //startActivity(new Intent(UserSignUpOrLoginSmsActivity.this, UserMainActivity.class));
                                } else {
                                    Toast.makeText(RegisterActivity.this,"注册失败(" + e.getErrorCode() + "-" + e.getMessage() +"）",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }


                }
            }
        });

    }
}
