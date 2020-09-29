package com.ycs.order.userActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
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
import com.ycs.order.Util.LoadingUtil;
import com.ycs.order.model.Goods;
import com.ycs.order.model.MyUser;
import com.ycs.order.model.Order;
import com.ycs.order.model.Store;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class HistoryActivity extends AppCompatActivity {
    private MyUser user=MyUser.getCurrentUser(MyUser.class);
    private LayoutInflater inflater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().hide();
        inflater=(LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        ImageView fanhui =findViewById(R.id.fanhui);
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final LinearLayout l=findViewById(R.id.order_list);
        BmobQuery<Order> orderlist=new BmobQuery<>();
        orderlist.order("-createdAt");
        orderlist.addWhereEqualTo("buyer",user.getObjectId());
        LoadingUtil.Loading_show(this);
        orderlist.findObjects(new FindListener<Order>() {
            @Override
            public void done(final List<Order> list, BmobException e) {
                if(e==null){
                    if (list.size()==0){
                        View v=inflater.inflate(R.layout.not_found,null);
                        TextView tv=v.findViewById(R.id.toast);
                        tv.setText("您暂无历史订单，快去店铺逛逛吧！");
                        l.addView(v);
                    }else{
                        for(int i = 0; i<list.size(); i++){
                            final String orderid=list.get(i).getObjectId();
                            View v=inflater.inflate(R.layout.order_layout,null);
                            final TextView sumtv=v.findViewById(R.id.sum);
                            final TextView numtv=v.findViewById(R.id.goodsnum);
                            final TextView shoptv=v.findViewById(R.id.shopname);
                            final TextView tvcomment =v.findViewById(R.id.comment);
                            final ImageView shopim=v.findViewById(R.id.shop_icon);
                            final TextView tvaccept=v.findViewById(R.id.accept);
                            final int fi=i;
                            final TextView statustv=v.findViewById(R.id.status);
                            if(list.get(i).getStatus().equals("0")){
                                statustv.setText(R.string.接单);
                                tvcomment.setVisibility(View.INVISIBLE);
                                tvaccept.setVisibility(View.INVISIBLE);
                            }else if(list.get(i).getStatus().equals("1")){
                                statustv.setText("商家已接单，等待配送中");
                                tvcomment.setVisibility(View.INVISIBLE);
                                tvaccept.setVisibility(View.INVISIBLE);

                            }else if(list.get(i).getStatus().equals("3")){
                                tvaccept.setVisibility(View.INVISIBLE);
                                statustv.setText("订单已完成");
                            }else if(list.get(i).getStatus().equals("4")){
                                tvaccept.setVisibility(View.INVISIBLE);
                                statustv.setText("订单已完成");
                                tvcomment.setText("已评价");
                                tvcomment.setEnabled(false);
                            }else if(list.get(i).getStatus().equals("5")){
                                tvaccept.setVisibility(View.INVISIBLE);
                                tvcomment.setVisibility(View.INVISIBLE);
                                statustv.setText("订单已取消");
                            }else if(list.get(i).getStatus().equals("2")){
                                statustv.setText(R.string.配送);
                                tvcomment.setVisibility(View.INVISIBLE);
                                tvaccept.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AlertDialog d=new AlertDialog.Builder(HistoryActivity.this).create();
                                        d.setTitle("确认");
                                        d.setMessage("确认收货？");
                                        d.setButton(DialogInterface.BUTTON_POSITIVE, "确认", new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Order o=new Order();
                                                o.setStatus("2");
                                                o.update(list.get(fi).getObjectId(),new UpdateListener() {
                                                    @Override
                                                    public void done(BmobException e) {
                                                        if(e==null){
                                                            Toast.makeText(HistoryActivity.this, "收货成功", Toast.LENGTH_SHORT).show();
                                                            statustv.setText("订单已完成");
                                                            tvaccept.setVisibility(View.INVISIBLE);
                                                            tvcomment.setVisibility(View.VISIBLE);
                                                        }else{
                                                            Toast.makeText(HistoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                        d.setButton(DialogInterface.BUTTON_NEGATIVE,"取消",new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        d.show();
                                    }
                                });
                            }

                            final LinearLayout goodsl=v.findViewById(R.id.goodslist);
                            BmobQuery<Store> storeBmobQuery=new BmobQuery<>();
                            storeBmobQuery.getObject(list.get(i).getShop_id(), new QueryListener<Store>() {
                                @Override
                                public void done(Store store, BmobException e) {
                                    if (e==null){
                                        shoptv.setText(store.getS_name());
                                        String  shopid = store.getOwnerId();
                                        File iconfile=new File(Environment.getExternalStorageDirectory()+"/"+list.get(fi).getShop_id()+".jpg");
                                        if(iconfile.exists()){
                                            Uri uri = Uri.fromFile(iconfile);
                                            try {
                                                Bitmap b = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                                                shopim.setImageBitmap(b);
                                            } catch (FileNotFoundException ex) {
                                                Log.e("获取头像失败", ex.getMessage());
                                            }
                                        }else{
                                            BmobQuery<MyUser> shop=new BmobQuery<>();
                                            shop.getObject(shopid, new QueryListener<MyUser>() {
                                                @Override
                                                public void done(MyUser myUser, BmobException e) {
                                                    if(e==null){
                                                        BmobFile shopicon=myUser.getIcon();
                                                        shopicon.download(new File(Environment.getExternalStorageDirectory()+"/"+list.get(fi).getShop_id()+".jpg"),new DownloadFileListener() {
                                                            @Override
                                                            public void done(String s, BmobException e) {
                                                                if(e==null){
                                                                    shopim.setImageBitmap(BitmapFactory.decodeFile(s));
                                                                }else{
                                                                    Toast.makeText(HistoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }

                                                            @Override
                                                            public void onProgress(Integer integer, long l) {

                                                            }
                                                        });
                                                    }else{
                                                        Toast.makeText(HistoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }else{
                                        Toast.makeText(HistoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                            tvcomment.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //评论页
                                    Intent intent=new Intent(HistoryActivity.this, CommentActivity.class);
                                    Bundle bundle=new Bundle();
                                    bundle.putCharSequence("shopid",list.get(fi).getShop_id());
                                    bundle.putCharSequence("orderid",orderid);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            });

                            ArrayList<String> goods=list.get(i).getGoods_list();
                            numtv.setText(goods.size()+"");
                            sumtv.setText(list.get(i).getSum());
                            final HashMap<String,Integer> hashMap=new HashMap<>();
                            for(int j=0;j<goods.size();j++){
                                if (hashMap.containsKey(goods.get(j))){
                                    hashMap.put(goods.get(j),hashMap.get(goods.get(j))+1);
                                }else{
                                    hashMap.put(goods.get(j),1);
                                }
                            }
                            int flag=0;
                            for (String key : hashMap.keySet()){
                                flag++;
                                if(flag>3){
                                    break;
                                }
                                final String fk=key;
                                final View vv=inflater.inflate(R.layout.goodslist,null);
                                final TextView goodsname=vv.findViewById(R.id.goodsname);
                                final ImageView goodsicon=vv.findViewById(R.id.goodsicon);
                                BmobQuery<Goods> goodsBmobQuery=new BmobQuery<>();
                                goodsBmobQuery.getObject(key, new QueryListener<Goods>() {
                                    @Override
                                    public void done(Goods goods, BmobException e) {
                                        if (e==null){
                                            goodsname.setText(goods.getGood_name());
                                            File iconfile=new File(Environment.getExternalStorageDirectory()+"/"+fk+".jpg");
                                            if(iconfile.exists()){
                                                Uri uri = Uri.fromFile(iconfile);
                                                try {
                                                    Bitmap b = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                                                    goodsicon.setImageBitmap(b);
                                                } catch (FileNotFoundException ex) {
                                                    Log.e("获取头像失败", ex.getMessage());
                                                }
                                            }else {
                                                final BmobFile goodsiconf = goods.getGoods_icon();
                                                goodsiconf.download(new File(Environment.getExternalStorageDirectory() + "/" + fk + ".jpg"),
                                                        new DownloadFileListener() {
                                                            @Override
                                                            public void done(String s, BmobException e) {
                                                                if(e==null){
                                                                    goodsicon.setImageBitmap(BitmapFactory.decodeFile(s));
                                                                }else{
                                                                    Toast.makeText(HistoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }

                                                            @Override
                                                            public void onProgress(Integer integer, long l) {

                                                            }
                                                        });
                                            }
                                        }else{
                                            Toast.makeText(HistoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                goodsl.addView(vv);
                            }
                            v.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Bundle bundle = new Bundle();
                                    Intent intent =new Intent(HistoryActivity.this, OrderInfoActivity.class);
                                    bundle.putCharSequence("orderid",orderid);
                                    bundle.putCharSequence("shopid",list.get(fi).getShop_id());
                                    intent.putExtras(bundle);
                                    intent.putExtra("map",(Serializable)hashMap);
                                    startActivity(intent);
                                }
                            });
                            l.addView(v);

                        }
                        LoadingUtil.Loading_close();
                    }
                }else{
                    Toast.makeText(HistoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
