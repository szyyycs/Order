package com.ycs.order.userActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.ycs.order.R;
import com.ycs.order.model.Love;
import com.ycs.order.model.MyUser;
import com.ycs.order.model.Store;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LikeActivity extends AppCompatActivity {
    private Handler handler;
    private LayoutInflater inflater;
    private MyUser user=MyUser.getCurrentUser(MyUser.class);
    private List<Love> loves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);
        getSupportActionBar().hide();
        inflater=(LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final ImageView fanhui=(ImageView)findViewById(R.id.fanhui);
        final LinearLayout l=findViewById(R.id.shoplist);
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 111) {
                    for(int i=0;i<loves.size();i++){
                        final View v=inflater.inflate(R.layout.like_shop,null);
                        BmobQuery<Store> storeBmobQuery=new BmobQuery<>();
                        final int finalI = i;
                        storeBmobQuery.getObject(loves.get(i).getShopid(), new QueryListener<Store>() {
                            @Override
                            public void done(final Store store, BmobException e) {
                                if(e==null){
                                    TextView shopname=v.findViewById(R.id.shop_name);
                                    shopname.setText(store.getS_name());
                                    ImageView shop_icon=v.findViewById(R.id.shop_icon);
                                    File iconfile=new File(Environment.getExternalStorageDirectory()+"/"+loves.get(finalI).getShopid()+".jpg");
                                    if(iconfile.exists()){
                                        Uri uri = Uri.fromFile(iconfile);
                                        try {
                                            Bitmap b = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                                            shop_icon.setImageBitmap(b);
                                        } catch (FileNotFoundException ex) {
                                            Log.e("获取头像失败", ex.getMessage());
                                        }
                                    }
                                    final ImageView star=v.findViewById(R.id.star);
                                    star.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            shoucang(loves.get(finalI).getShopid(),star);

                                        }
                                    });
                                    v.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent =new Intent(LikeActivity.this, ShopActivity.class);
                                            Bundle bundle=new Bundle();
                                            //传递name参数
                                            bundle.putCharSequence("id",loves.get(finalI).getShopid());
                                            bundle.putCharSequence("shopname",store.getS_name());
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }
                        });
                        l.addView(v);
                    }
                    Log.e("1","sucess");

                }else if(msg.what == 222){
                    View vv=inflater.inflate(R.layout.not_found,null);
                    TextView toast=vv.findViewById(R.id.toast);
                    toast.setText("您暂未收藏任何店铺");
                    l.addView(vv);
                    Log.e("1","失败"+msg.obj.toString());

                }else if(msg.what == 333){
//                    Toast.makeText(LikeActivity.this, "失败"+msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    Log.e("1",msg.obj.toString());
                    View vv=inflater.inflate(R.layout.not_found,null);
                    TextView toast=vv.findViewById(R.id.toast);
                    toast.setText("加载失败");
                    l.addView(vv);
                    Log.e("1","失败"+msg.obj.toString());

                }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{

                    Looper.prepare();
                    String url="http://"+getString(R.string.host)+":8080/findlike";
                    Log.e("提示",url);
                    OkHttpClient okHttpClient = new OkHttpClient();
                    FormBody formBody = new FormBody.Builder().add("userid", user.getObjectId()).build();
                    Request request = new Request.Builder()
                            .url(url)
                            .post(formBody)
                            .build();
                    Response response = okHttpClient.newCall(request).execute();
                    loves = JSONArray.parseArray(response.body().string(),Love.class);
                    if(loves.size() != 0){
                        Message msg=new Message();
                        msg.what=111;//通知UI线程Json解析完成
                        handler.sendMessage(msg);
                    }else{
                        Message msg=new Message();
                        msg.what=222;//通知UI线程Json解析完成
                        msg.obj=loves.toString();
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

    void shoucang(final String shopid, final ImageView im){
        final Handler mhandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 11) {
                    Toast.makeText(LikeActivity.this, "取消收藏成功", Toast.LENGTH_SHORT).show();
                    Log.e("22","sucess");
                    im.setImageResource(R.mipmap.shoucang);
                }else if(msg.what == 22){
                    Toast.makeText(LikeActivity.this, "取消收藏失败，后端错误", Toast.LENGTH_SHORT).show();
                    Log.e("22","shibai");
                }else if(msg.what == 33){
                    Toast.makeText(LikeActivity.this, "取消收藏失败"+msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    Log.e("22",msg.obj.toString());
                }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Looper.prepare();
                    String url="http://"+getString(R.string.host)+":8080/deletelike";
                    OkHttpClient okHttpClient = new OkHttpClient();
                    FormBody formBody = new FormBody.Builder().add("userid",user.getObjectId()).add("shopid",shopid).build();
                    Request request = new Request.Builder()
                            .url(url)
                            .post(formBody)
                            .build();
                    Response response = okHttpClient.newCall(request).execute();
                    String oo=response.body().string();
                    if(oo.equals("true")){
                        Message msg=new Message();
                        msg.what=11;//通知UI线程Json解析完成
                        mhandler.sendMessage(msg);
                    }else {
                        Message msg = new Message();
                        msg.what = 22;  //通知UI线程Json解析完成
                        mhandler.sendMessage(msg);
                    }
                    Looper.loop();
                }catch (Exception e){
                    Message msg = new Message();
                    msg.what = 33;  //通知UI线程Json解析完成
                    msg.obj=e.getMessage();
                    mhandler.sendMessage(msg);
                }
            }
        }).start();
    }
}
