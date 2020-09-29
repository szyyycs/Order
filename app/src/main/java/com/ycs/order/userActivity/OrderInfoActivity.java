package com.ycs.order.userActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ycs.order.R;
import com.ycs.order.model.Goods;
import com.ycs.order.model.MyUser;
import com.ycs.order.model.Order;
import com.ycs.order.model.Store;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.HashMap;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class OrderInfoActivity extends AppCompatActivity {
    private HashMap<String,Integer> hashMap;
    private LayoutInflater inflater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);
        getSupportActionBar().hide();
        ImageView fanhui =findViewById(R.id.fanhui);
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        inflater=(LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            Bundle bundle = getIntent().getExtras();
            final String orderid=bundle.getString("orderid");
            final String shopid=bundle.getString("shopid");
            hashMap = (HashMap<String, Integer>) getIntent().getSerializableExtra("map");
            final TextView shopnametv=findViewById(R.id.shopname);
            BmobQuery<Store> storeBmobQuery=new BmobQuery<>();
            storeBmobQuery.getObject(shopid, new QueryListener<Store>() {
                @Override
                public void done(Store store, BmobException e) {
                    if(e==null){
                        shopnametv.setText(store.getS_name());
                    }
                }
            });
            BmobQuery<Order> orderBmobQuery=new BmobQuery<>();
            orderBmobQuery.getObject(orderid, new QueryListener<Order>() {
                @Override
                public void done(final Order order, BmobException e) {
                    if(e==null){
                        TextView commit=findViewById(R.id.commit);
                        if(order.getStatus().equals("0")){
                            commit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Order o=new Order();
                                    o.setStatus("5");
                                    o.update(orderid, new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if(e==null){
                                                Toast.makeText(OrderInfoActivity.this, "订单取消成功!", Toast.LENGTH_SHORT).show();
                                                Intent intent =new Intent(OrderInfoActivity.this, MainActivity.class)
                                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.putExtra("id", 1);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                                }
                            });
                        }else{
                            commit.setVisibility(View.INVISIBLE);
                        }

                        TextView sum=findViewById(R.id.sum);
                        sum.setText(order.getSum());
                        TextView address=findViewById(R.id.address);
                        address.setText(order.getReceive_address());
                        TextView name=findViewById(R.id.name);
                        name.setText(order.getReceiver());
                        TextView phone=findViewById(R.id.telephone);
                        phone.setText(order.getReceive_num());
                        TextView time=findViewById(R.id.time);
                        time.setText(order.getCreatedAt());
                    }
                }
            });
            TextView call=findViewById(R.id.call);
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BmobQuery<Store> storeBmobQuery=new BmobQuery<>();
                    storeBmobQuery.getObject(shopid, new QueryListener<Store>() {
                        @Override
                        public void done(Store store, BmobException e) {
                            if(e==null){
                                BmobQuery<MyUser>  myUserBmobQuery=new BmobQuery<>();
                                myUserBmobQuery.getObject(store.getOwnerId(), new QueryListener<MyUser>() {
                                    @Override
                                    public void done(MyUser myUser, BmobException e) {
                                        if(e==null){
                                            AlertDialog dialog=new AlertDialog.Builder(OrderInfoActivity.this).create();

                                            if(myUser.getMobilePhoneNumber().length()>=6){
                                                dialog.setTitle("店家电话号码为");

                                                dialog.setMessage(myUser.getMobilePhoneNumber());
                                                dialog.show();
                                            }else{
                                                dialog.setMessage("该商家暂无电话号码");
                                                dialog.show();
                                            }

                                        }else{
                                            Log.e("222",e.getMessage());
                                        }
                                    }
                                });
                            }else{
                                Log.e("222",e.getMessage());
                            }
                        }
                    });
                }
            });
            LinearLayout l=findViewById(R.id.goodslist);
            for (String key : hashMap.keySet()) {
                final View view=inflater.inflate(R.layout.order_shop,null);
                final ImageView icon=view.findViewById(R.id.goods_icon);
                final TextView name=view.findViewById(R.id.goods_name);
                final TextView goodsprice=view.findViewById(R.id.goods_price);
                final TextView num=view.findViewById(R.id.goods_num);
                num.setText(String.valueOf(hashMap.get(key)));
                BmobQuery<Goods> goodsBmobQuery=new BmobQuery<>();
                goodsBmobQuery.getObject(key, new QueryListener<Goods>() {
                    @Override
                    public void done(Goods goods, BmobException e) {
                        if(e==null){
                            name.setText(goods.getGood_name());
                            DecimalFormat df=new DecimalFormat("#.00");
                            Double d=goods.getPprice();
                            goodsprice.setText(df.format(d));
                            File iconfile=new File(Environment.getExternalStorageDirectory()+"/"+goods.getObjectId()+".jpg");
                            if(iconfile.exists()){
                                Uri uri = Uri.fromFile(iconfile);
                                try {
                                    Bitmap b = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                                    icon.setImageBitmap(b);
                                } catch (FileNotFoundException ex) {
                                    Log.e("获取头像失败", ex.getMessage());
                                }
                            }else {
                                BmobFile goodsiconf = goods.getGoods_icon();
                                goodsiconf.download(new File(Environment.getExternalStorageDirectory() + "/" + goods.getObjectId() + ".jpg"),
                                        new DownloadFileListener() {
                                            @Override
                                            public void done(String s, BmobException e) {
                                                if(e==null){
                                                    icon.setImageBitmap(BitmapFactory.decodeFile(s));
                                                }else{
                                                    Toast.makeText(OrderInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onProgress(Integer integer, long l) {

                                            }
                                        });
                            }

                        }else{
                            Log.e("错误1",e.getMessage());
                        }
                    }
                });

                l.addView(view);
            }




    }
}
