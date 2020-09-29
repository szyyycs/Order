package com.ycs.order.userActivity;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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


public class FindpActivity extends AppCompatActivity {
private Handler handler;
private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findp);
        getSupportActionBar().hide();
        ImageView tv =(ImageView) findViewById(R.id.fanhui);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final TextView tvnotice=(TextView)findViewById(R.id.f_notice);
        final TextView tvGetCode =(TextView) findViewById(R.id.f_getCode);
        final TextView tvphone=(TextView)findViewById(R.id.f_phone);
        final TextView tvcode=(TextView)findViewById(R.id.f_code);
        final TextView tvpwd=(TextView)findViewById(R.id.f_pwd);
        final Timedown timedown =new Timedown(60000,1000,tvGetCode,tvnotice);
        tvGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tvphone.getText().toString().length()!=11){
                    Toast.makeText(FindpActivity.this,"请输入正确的手机号码",Toast.LENGTH_LONG).show();
                }else{

                    BmobQuery<MyUser> categoryBmobQuery = new BmobQuery<>();
                    categoryBmobQuery.addWhereEqualTo("mobilePhoneNumber", tvphone.getText().toString());
                    categoryBmobQuery.findObjects(new FindListener<MyUser>() {
                        @Override
                        public void done(List<MyUser> object, BmobException e) {
                            if (e == null&&object.size()==0) {
                                Toast.makeText(FindpActivity.this, "该账户未注册" , Toast.LENGTH_LONG).show();
                            } else if(e==null&&object.size()!=0) {
                                if(object.get(0).getType().equals("1")){
                                    Toast.makeText(FindpActivity.this, "该账户为商家账户，请在商家页面修改密码" , Toast.LENGTH_LONG).show();
                                }else{
                                    id=object.get(0).getObjectId();
                                    timedown.start();
                                    String phone=tvphone.getText().toString();
                                    BmobSMS.requestSMSCode(phone,"", new QueryListener<Integer>() {
                                        @Override
                                        public void done(Integer smsId, BmobException e) {
                                            if (e == null) {
                                                Toast.makeText(FindpActivity.this,"发送验证码成功" ,Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(FindpActivity.this,"发送验证码失败（" + e.getErrorCode() + "-" + e.getMessage() + "）\n",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }


                            }

                        }

                            });



                }
            }
        });
        final TextView reset =(TextView)findViewById(R.id.f_reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newPassword=tvpwd.getText().toString();
                String code =tvcode.getText().toString();
                final String phone=tvphone.getText().toString();
                if(phone.length()!=11||code==""||newPassword.length()<6){
                    Toast.makeText(FindpActivity.this, "请输入完整验证码与密码", Toast.LENGTH_SHORT).show();
                }else {
                    if (reset.getText().toString() == "使用该账户登陆") {
                        LoadingUtil.Loading_show(FindpActivity.this);
                        MyUser user = new MyUser();
                        //此处替换为你的用户名
                        user.setUsername(phone);
                        //此处替换为你的密码
                        user.setPassword(newPassword);
                        user.login(new SaveListener<MyUser>() {
                            @Override
                            public void done(MyUser bmobUser, BmobException e) {
                                if (e == null) {
                                    MyUser user = MyUser.getCurrentUser(MyUser.class);
                                    Toast.makeText(FindpActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                                    SharedPreferencesUtil nS;
                                    nS=new SharedPreferencesUtil(FindpActivity.this,"user");
                                    nS.setValue("account",user.getUsername());
                                    nS.setValue("pwd",newPassword);
                                    nS.setValue("type","0");
                                    LoadingUtil.Loading_close();
                                    Intent intent =new Intent(FindpActivity.this, MainActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);

                                } else {
                                    LoadingUtil.Loading_close();
                                    Toast.makeText(FindpActivity.this, "登录失败（" + e.getMessage()+"）", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        BmobUser.resetPasswordBySMSCode(code, newPassword, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    LoadingUtil.Loading_show(FindpActivity.this);
                                    handler = new Handler(){
                                        @Override
                                        public void handleMessage(Message msg) {
                                            if (msg.what == 111) {
                                                LoadingUtil.Loading_close();
                                                Toast.makeText(FindpActivity.this, "重置成功！", Toast.LENGTH_SHORT).show();
                                                reset.setText("使用该账户登陆");
                                            }else if(msg.what==222){
                                                LoadingUtil.Loading_close();
                                                Toast.makeText(FindpActivity.this, "更改失败，后端错误", Toast.LENGTH_SHORT).show();
                                            }else if(msg.what==333){
                                                LoadingUtil.Loading_close();
                                                Toast.makeText(FindpActivity.this, "更改失败，连接失败", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    };
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try{
                                                Looper.prepare();
                                                String url="http://"+getString(R.string.host)+":8080/savepwd";
                                                OkHttpClient okHttpClient = new OkHttpClient();
                                                FormBody formBody = new FormBody.Builder().add("name", phone).add("pwd",newPassword).build();
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

                                } else {
                                    LoadingUtil.Loading_close();
                                    Toast.makeText(FindpActivity.this, "验证码错误（" + e.getErrorCode() + "-" + e.getMessage()+"）", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }

                }
            }
        });

    }
}
