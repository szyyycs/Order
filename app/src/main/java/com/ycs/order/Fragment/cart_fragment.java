package com.ycs.order.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.ycs.order.userActivity.CommentActivity;
import com.ycs.order.userActivity.OrderInfoActivity;
import com.ycs.order.R;
import com.ycs.order.userActivity.ShopActivity;
import com.ycs.order.Util.LoadingUtil;
import com.ycs.order.model.Goods;
import com.ycs.order.model.MyUser;
import com.ycs.order.model.Order;
import com.ycs.order.model.Store;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class cart_fragment extends Fragment {

    private MyUser user=MyUser.getCurrentUser(MyUser.class);


    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        final View view=inflater.inflate(R.layout.cart_fragment,null);
        final RefreshLayout sr=(RefreshLayout)view.findViewById(R.id.cart_refresh);
        final Context context=getActivity();
        sr.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore(2000/*,false*/);//传入false表示加载失败
                final HashMap<String,Float> shoplist=new HashMap<>();
                final HashMap<String,Integer> shopnum=new HashMap<>();
                BmobQuery<Order> orderlist=new BmobQuery<>();
                orderlist.addWhereEqualTo("status","4");
                orderlist.findObjects(new FindListener<Order>() {
                    @Override
                    public void done(final List<Order> list, BmobException e) {
                        if(e==null){
//                            try {
                                for (int i = 0; i < list.size(); i++) {
                                    if(list.get(i).getStar()!=null){
                                        if (shoplist.containsKey(list.get(i).getShop_id())) {
                                            int num = shopnum.get(list.get(i).getShop_id());
                                            num++;
                                            shopnum.put(list.get(i).getShop_id(), num);
                                            float star = Float.parseFloat(list.get(i).getStar());
                                            star+=shoplist.get(list.get(i).getShop_id());
                                            shoplist.put(list.get(i).getShop_id(), star);

                                        } else {
                                            shoplist.put(list.get(i).getShop_id(), Float.parseFloat(list.get(i).getStar()));
                                            shopnum.put(list.get(i).getShop_id(), 1);
                                        }
                                    }
                                }
                                for(String key: shoplist.keySet()){
                                    float i=shoplist.get(key)/shopnum.get(key);   //平均评价
                                    float d=(float) shopnum.get(key)/1000;
                                    float num= (d+1)*i;   //权重
                                    shoplist.put(key,num);
                                    Log.e("数据",key+"分数"+num);
                                }
                                final List<Map.Entry<String, Float>> alist = new ArrayList<>(shoplist.entrySet()); //转换为list
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                    alist.sort(new Comparator<Map.Entry<String, Float>>() {
//                                        @Override
//                                        public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
//                                            return o2.getValue().compareTo(o1.getValue());
//                                        }
//                                    });
//                                }
                            Collections.sort(alist,new Comparator<Map.Entry<String, Float>>() {
                                @Override
                                public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                                    return o2.getValue().compareTo(o1.getValue());
                                }
                            });

                                Log.e("aaaaaaa",alist.toString());
                                LinearLayout llist=view.findViewById(R.id.list);
                                llist.removeAllViews();
                                for(int i=0;i<3;i++){
                                    if(alist.size()>i){
                                        final View vv=inflater.inflate(R.layout.tuijian,null);
                                        TextView star=vv.findViewById(R.id.star);
                                        star.setText(String.valueOf(alist.get(i).getValue()).substring(0,3));
                                        TextView num=vv.findViewById(R.id.num);
                                        num.setText(String.valueOf(shopnum.get(alist.get(i).getKey())));
                                        final TextView shopname=vv.findViewById(R.id.shopname);
                                        final ImageView shopim=vv.findViewById(R.id.shopicon);

                                        BmobQuery<Store> shop=new BmobQuery<>();
                                        final int fi=i;
                                        shop.getObject(alist.get(i).getKey(), new QueryListener<Store>() {
                                            @Override
                                            public void done(final Store store, BmobException e) {
                                                shopname.setText(store.getS_name());
                                                vv.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent =new Intent(getActivity(), ShopActivity.class);
                                                        Bundle bundle=new Bundle();
                                                        //传递name参数
                                                        bundle.putCharSequence("id",store.getObjectId());
                                                        bundle.putCharSequence("shopname",store.getS_name());
                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                                File iconfile=new File(Environment.getExternalStorageDirectory()+"/"+alist.get(fi).getKey()+".jpg");
                                                if(iconfile.exists()) {
                                                    Uri uri = Uri.fromFile(iconfile);
                                                    try {
                                                        Bitmap b = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
                                                        shopim.setImageBitmap(b);
                                                    } catch (FileNotFoundException ex) {
                                                        Log.e("获取头像失败", ex.getMessage());
                                                    }
                                                }else{
                                                    BmobQuery<MyUser> userBmobQuery=new BmobQuery<>();
                                                    userBmobQuery.getObject(store.getOwnerId(), new QueryListener<MyUser>() {
                                                        @Override
                                                        public void done(MyUser myUser, BmobException e) {
                                                            BmobFile icon=myUser.getIcon();
                                                            icon.download(new File(Environment.getExternalStorageDirectory() + "/" + alist.get(fi).getKey() + ".jpg"), new DownloadFileListener() {
                                                                @Override
                                                                public void done(String s, BmobException e) {
                                                                    shopim.setImageBitmap(BitmapFactory.decodeFile(s));
                                                                }
                                                                @Override
                                                                public void onProgress(Integer integer, long l) {

                                                                }
                                                            });
                                                        }
                                                    });

                                                }
                                            }
                                        });

                                        llist.addView(vv);
                                    }
                                }



//                            }catch (Exception ex){
//                                Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
//                                Log.e("错误",ex.getMessage());
//                            }
                        }else{
                            Log.d("zz",e.getMessage());
                        }
                    }
                });

            }
        });
//        sr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        sr.setRefreshing(false);
//                    }
//                }, 1500);
//
//            }
//        });
        final LinearLayout l=view.findViewById(R.id.order_list);
        BmobQuery<Order> orderlist=new BmobQuery<>();
        orderlist.order("-createdAt");
        orderlist.addWhereEqualTo("buyer",user.getObjectId());
        LoadingUtil.Loading_show(context);
        String[] s = {"4", "5"};
        orderlist.addWhereNotContainedIn("status", Arrays.asList(s));
        orderlist.addWhereEqualTo("buyer",user.getObjectId());
//        orderlist.addWhereNotEqualTo("status","3");
//        orderlist.addWhereNotEqualTo("status","4");
        orderlist.findObjects(new FindListener<Order>() {
            @Override
            public void done(final List<Order> list, BmobException e) {
                if(e==null){

                    if (list.size()==0){
                        View v=inflater.inflate(R.layout.not_found,null);
                        TextView tv=v.findViewById(R.id.toast);
                        tv.setText("您暂无待处理订单，快去店铺逛逛吧！");
                        l.addView(v);
                        LoadingUtil.Loading_close();
                        TextView ll=view.findViewById(R.id.like);
                        ll.setText("猜你喜欢");
                        TextView tishi=view.findViewById(R.id.tishi);
                        tishi.setText("已完成订单在历史订单页查看");
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
                            }else if(list.get(i).getStatus().equals("2")){
                                statustv.setText("订单在配送途中");
                                tvcomment.setVisibility(View.INVISIBLE);
                                tvaccept.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AlertDialog d=new AlertDialog.Builder(getActivity()).create();
                                        d.setTitle("确认");
                                        d.setMessage("确认收货？");
                                        d.setButton(DialogInterface.BUTTON_POSITIVE, "确认", new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Order o=new Order();
                                                o.setStatus("3");
                                                o.update(list.get(fi).getObjectId(),new UpdateListener() {
                                                    @Override
                                                    public void done(BmobException e) {
                                                        if(e==null){
                                                            Toast.makeText(getActivity(), "收货成功", Toast.LENGTH_SHORT).show();
                                                            statustv.setText("订单已完成");
                                                            tvaccept.setVisibility(View.INVISIBLE);
                                                            tvcomment.setVisibility(View.VISIBLE);
                                                        }else{
                                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                                                    Bitmap b = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
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
                                                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onProgress(Integer integer, long l) {

                                                                }
                                                            });
                                                        }else{
                                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }else{
                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                            tvcomment.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //评论页
                                    Intent intent=new Intent(context, CommentActivity.class);
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
                                                    Bitmap b = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
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
                                                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }

                                                            @Override
                                                            public void onProgress(Integer integer, long l) {

                                                            }
                                                        });
                                            }
                                        }else{
                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                goodsl.addView(vv);
                            }
                            v.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Bundle bundle = new Bundle();
                                    Intent intent =new Intent(context, OrderInfoActivity.class);
                                    bundle.putCharSequence("orderid",orderid);
                                    bundle.putCharSequence("shopid",list.get(fi).getShop_id());
                                    intent.putExtras(bundle);
                                    intent.putExtra("map",(Serializable)hashMap);
                                    startActivity(intent);
                                }
                            });
                            l.addView(v);

                        }
                        TextView ll=view.findViewById(R.id.like);
                        ll.setText("猜你喜欢");
                        TextView tishi=view.findViewById(R.id.tishi);
                        tishi.setText("已完成订单在历史订单页查看");
                        LoadingUtil.Loading_close();
                    }
                }else{
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    LoadingUtil.Loading_close();
                }
            }
        });



        return view;
    }


}
