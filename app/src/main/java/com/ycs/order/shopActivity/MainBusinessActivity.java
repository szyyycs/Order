package com.ycs.order.shopActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ycs.order.R;
import com.ycs.order.Util.LoadingUtil;
import com.ycs.order.Util.SharedPreferencesUtil;
import com.ycs.order.model.MyUser;
import com.ycs.order.model.Store;
import com.ycs.order.userActivity.loginActivity;

import java.io.File;
import java.io.FileNotFoundException;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.QueryListener;

public class MainBusinessActivity extends AppCompatActivity {
    private RelativeLayout fuction;
    private RelativeLayout openshop;
    private File iconfile;
    private MyUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_business);
        getSupportActionBar().hide();
        Bmob.initialize(MainBusinessActivity.this,"682dba275359d04511948d626bff513f");
        RelativeLayout exit = findViewById(R.id.quitAll);
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED)
//        {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},199);
//
//        }

        TextView exittv=findViewById(R.id.exitall);
        exittv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(MainBusinessActivity.this).create();
                dialog.setTitle("退出");
                dialog.setMessage("确认退出程序？");
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                });
                dialog.show();
            }
        });
        //退出程序
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(MainBusinessActivity.this).create();
                dialog.setTitle("退出");
                dialog.setMessage("确认退出程序？");
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                });
                dialog.show();
            }
        });

        //管理店铺
        final RelativeLayout managershop=findViewById(R.id.manashop);
        managershop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainBusinessActivity.this, ManagerShopActivity.class);
                startActivity(intent);

            }
        });
        TextView textViewtv=findViewById(R.id.exit);
        textViewtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog=new AlertDialog.Builder(MainBusinessActivity.this).create();
                dialog.setTitle("退出");
                dialog.setMessage("确认退出登陆？");
                dialog.setButton(DialogInterface.BUTTON_POSITIVE,"是",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoadingUtil.Loading_show(MainBusinessActivity.this);
                        SharedPreferencesUtil nS;
                        nS=new SharedPreferencesUtil(MainBusinessActivity.this,"user");
                        nS.setValue("account","");
                        nS.setValue("pwd","");
                        nS.setValue("type","");
                        File file=new File(Environment.getExternalStorageDirectory()+"/userImage.jpg");
                        try {
                            if (file.exists()) {
                                file.delete();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        BmobUser.logOut();
                        Intent intent =new Intent(MainBusinessActivity.this, loginActivity.class);
                        startActivity(intent);
                        LoadingUtil.Loading_close();
                        finish();
                    }
                });
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE,"取消",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }
        });
        //退出按钮
        RelativeLayout exitView=findViewById(R.id.quitAccount);
        exitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog=new AlertDialog.Builder(MainBusinessActivity.this).create();
                dialog.setTitle("退出");
                dialog.setMessage("确认退出登陆？");
                dialog.setButton(DialogInterface.BUTTON_POSITIVE,"是",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoadingUtil.Loading_show(MainBusinessActivity.this);
                        SharedPreferencesUtil nS;
                        nS=new SharedPreferencesUtil(MainBusinessActivity.this,"user");
                        nS.setValue("account","");
                        nS.setValue("pwd","");
                        nS.setValue("type","");
                        File file=new File(Environment.getExternalStorageDirectory()+"/userImage.jpg");
                        try {
                            if (file.exists()) {
                                file.delete();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        BmobUser.logOut();
                        Intent intent =new Intent(MainBusinessActivity.this,loginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        LoadingUtil.Loading_close();
                        finish();
                    }
                });
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE,"取消",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }
        });

            user = BmobUser.getCurrentUser(MyUser.class);
            fuction=findViewById(R.id.allsetting);
            openshop=findViewById(R.id.openshop);
            if(user.getBusinessid()==null||user.getBusinessid().equals("")){
                fuction.setVisibility(View.INVISIBLE);
                openshop.setVisibility(View.VISIBLE);
                exittv.setVisibility(View.VISIBLE);
                textViewtv.setVisibility(View.VISIBLE);
            }else{
                find();
                exittv.setVisibility(View.INVISIBLE);
                textViewtv.setVisibility(View.INVISIBLE);
                openshop.setVisibility(View.INVISIBLE);
                final TextView shopname=findViewById(R.id.shop_name);
                BmobQuery<Store> s=new BmobQuery<>();
                s.getObject(user.getBusinessid(), new QueryListener<Store>() {
                    @Override
                    public void done(Store store, BmobException e) {
                        if(e==null){
                            shopname.setText(store.getS_name());
                        }else{
                            shopname.setText("无法查询到店铺名");
                        }
                    }
                });
                shopname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                    }
                });

                final ImageView icon=findViewById(R.id.shop_icon);
                iconfile=new File(Environment.getExternalStorageDirectory()+"/userImage.jpg");
                if(iconfile.exists()) {
                    Uri i = Uri.fromFile(iconfile);
                    try {
                         Bitmap b = BitmapFactory.decodeStream(getContentResolver().openInputStream(i));
                         icon.setImageBitmap(b);
                      } catch (FileNotFoundException ex) {
                     Log.e("获取头像失败", ex.getMessage());
                  }
                }else if (user.getIcon() != null) {
                    Log.e("联网获取图片", "");
                    BmobFile iconn = user.getIcon();
                    iconn.download(new File(Environment.getExternalStorageDirectory() + "/userImage.jpg")
                            , new DownloadFileListener() {
                                @Override
                                public void done(String s, BmobException e) {
                                    if (e == null) {
                                        icon.setImageBitmap(BitmapFactory.decodeFile(s));   //根据地址解码并显示图片
                                    } else {
                                        Log.e("获取头像失败", e.getMessage());
                                    }
                                }

                                @Override
                                public void onProgress(Integer integer, long l) {

                                }
                            });

            }
                icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog d=new AlertDialog.Builder(MainBusinessActivity.this).create();
                        LayoutInflater inflater=LayoutInflater.from(MainBusinessActivity.this);
                        View image=inflater.inflate(R.layout.dialog,null);
                        ImageView img= image.findViewById(R.id.largeimage);
                        d.setView(image);
                        iconfile=new File(Environment.getExternalStorageDirectory()+"/userImage.jpg");
                        Uri i = Uri.fromFile(iconfile);
                        try {
                            Bitmap b = BitmapFactory.decodeStream(getContentResolver().openInputStream(i));
                            img.setImageBitmap(b);
                        } catch (FileNotFoundException ex) {
                            Log.e("获取头像失败", ex.getMessage());
                        }
                        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        d.show();
                    }
                });

            }

            //管理货物
            RelativeLayout magoods=findViewById(R.id.managergoods);
            magoods.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(MainBusinessActivity.this, ManageGoodsActivity.class);
                    startActivity(intent);
                }
            });
            //管理订单
        RelativeLayout maorder=findViewById(R.id.managerorder);
        maorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainBusinessActivity.this, ManagerOrderActivity.class);
                startActivity(intent);
            }
        });
        //查看评论
        RelativeLayout com=findViewById(R.id.comment);
        com.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainBusinessActivity.this, BusinessCommentActivity.class);
                Bundle bundle=new Bundle();
                bundle.putCharSequence("shopid","");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


            TextView btn_open=findViewById(R.id.btn_openshop);
            btn_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(MainBusinessActivity.this, OpenShop.class);
                    startActivity(intent);
                }
            });
            final SwipeRefreshLayout refreshLayout=findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        find();
                    }
                },2000);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    public  void find(){
        BmobQuery<MyUser> uq=new BmobQuery<>();
        BmobQuery<Store> s=new BmobQuery<>();
        final TextView shopname=findViewById(R.id.shop_name);
        s.getObject(user.getBusinessid(), new QueryListener<Store>() {
            @Override
            public void done(Store store, BmobException e) {
                if(e==null){
                    shopname.setText(store.getS_name());
                }else{
                    shopname.setText("无法查询到店铺名");
                }
            }
        });
        uq.getObject(user.getObjectId(), new QueryListener<MyUser>() {
            @Override
            public void done(MyUser myUser, BmobException e) {
                if(e==null){
                    final ImageView shopicon=findViewById(R.id.shop_icon);
                    BmobFile icon=myUser.getIcon();
                    icon.download(new File(Environment.getExternalStorageDirectory()+"/userImage.jpg"),new DownloadFileListener() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e == null){

                                shopicon.setImageBitmap(BitmapFactory.decodeFile(s));   //根据地址解码并显示图片
                            }
                            else{
                                Log.e("获取头像失败",e.getMessage());
                            }
                        }

                        @Override
                        public void onProgress(Integer integer, long l) {

                        }
                    });

                }
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

// 按下键盘上返回按钮
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog dialog = new AlertDialog.Builder(MainBusinessActivity.this).create();
            dialog.setTitle("退出");
            dialog.setMessage("确认退出程序？");
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            });
            dialog.show();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
