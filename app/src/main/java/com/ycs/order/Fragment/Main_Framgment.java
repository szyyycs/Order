package com.ycs.order.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ycs.order.R;
import com.ycs.order.model.Order;
import com.ycs.order.userActivity.InfoActivity;
import com.ycs.order.userActivity.ShopActivity;
import com.ycs.order.model.MyUser;
import com.ycs.order.model.Store;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Main_Framgment extends Fragment {
    private int[] imageId=new int[]{
            R.mipmap.p1,
            R.mipmap.p2,
            R.mipmap.p3,
            R.mipmap.p4,

    };
    private List<ImageView> images;
    private ScheduledExecutorService scheduledExecutorService;
    private int currentItem;
    private ViewPager viewPager;
    private ViewPaperAdapter adapter;
    private TextView tvTT;
    private LinearLayout l;
    private View view;
    private String id;
    private String shopname;
    private View r;
    private Context context;
    private MyUser user ;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context =getActivity();
        Bmob.initialize(context,"682dba275359d04511948d626bff513f");
        user=BmobUser.getCurrentUser(MyUser.class);
        view=inflater.inflate(R.layout.main_fragment,null);
        super.onCreate(savedInstanceState);
        viewPager=(ViewPager)view.findViewById(R.id.vp);
        images=new ArrayList<ImageView>();
        for(int i=0;i<imageId.length;i++){
            ImageView imageView=new ImageView(context);
            imageView.setBackgroundResource(imageId[i]);
            images.add(imageView);
        }
        adapter=new ViewPaperAdapter();
        viewPager.setAdapter(adapter);
        try{
            String name=user.getUsername();
            String phone=user.getMobilePhoneNumber();
            if(name.equals(phone)&&phone.length()==11){
                name=phone.substring(0,3)+"****"+phone.substring(7,11);
            }
            tvTT=(TextView)view.findViewById(R.id.tt);
            tvTT.setText("欢迎您，"+name);
        }catch(Exception e){
            Log.d("提示",e.getMessage());
        }
        l=(LinearLayout)view.findViewById(R.id.shoplist);
        init(inflater,0);
//        final BmobQuery<Store> query=new BmobQuery<>();
//        query.order("-createAt");
//        query.findObjects(new FindListener<Store>() {
//            @Override
//            public void done(final List<Store> list, BmobException e) {
//                if(e==null){
//                    int i=0;
//                    for(final Store s:list){
//                        r=inflater.inflate(R.layout.shop_main,null);
//                        final ImageView shop_icon=(ImageView)r.findViewById(R.id.shop_icon);
//
//                        File iconfile=new File(Environment.getExternalStorageDirectory()+"/"+s.getObjectId()+".jpg");
//                        if(iconfile.exists()){
//                             Uri uri = Uri.fromFile(iconfile);
//                             try {
//                                 Bitmap b = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
//                                 shop_icon.setImageBitmap(b);
//                             } catch (FileNotFoundException ex) {
//                                 Log.e("获取头像失败", ex.getMessage());
//                             }
//                        }else{
//                            BmobQuery<MyUser> shop=new BmobQuery<>();
//                            shop.getObject(s.getOwnerId(), new QueryListener<MyUser>() {
//                                @Override
//                                public void done(MyUser myUser, BmobException e) {
//                                    if(e==null){
//                                        BmobFile iicon=myUser.getIcon();
//                                        iicon.download(new File(Environment.getExternalStorageDirectory()+"/"+s.getObjectId()+".jpg"),new DownloadFileListener() {
//                                            @Override
//                                            public void done(String s, BmobException e) {
//                                                if(e==null){
//                                                    shop_icon.setImageBitmap(BitmapFactory.decodeFile(s));
//                                                }else{
//                                                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onProgress(Integer integer, long l) {
//
//                                            }
//                                        });
//                                    }else{
//                                        Log.d("错误",e.getMessage());
//                                    }
//                                }
//                            });
//                        }
//                        final TextView shop_name=(TextView)r.findViewById(R.id.shop_name);
//                        shop_name.setText(s.getS_name());
//                        final int finalI = i;
//                        r.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent intent =new Intent(getActivity(), ShopActivity.class);
//                                Bundle bundle=new Bundle();
//                                //传递name参数
//                                bundle.putCharSequence("id",list.get(finalI).getObjectId());
//                                bundle.putCharSequence("shopname",list.get(finalI).getS_name());
//                                intent.putExtras(bundle);
//                                startActivity(intent);
//                                //Toast.makeText(getActivity(),"点击的店铺是"+ list.get(finalI).getS_name(),Toast.LENGTH_LONG).show();
//
//                            }
//                        });
//                        r.setOnLongClickListener(new View.OnLongClickListener() {
//                            @Override
//                            public boolean onLongClick(View v) {
//                                AlertDialog dialog=new AlertDialog.Builder(context).create();
//                                dialog.setIcon(R.mipmap.star);
//                                dialog.setTitle("添加收藏");
//                                dialog.setButton(DialogInterface.BUTTON_POSITIVE,"是",new DialogInterface.OnClickListener(){
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        final Handler mhandler = new Handler(){
//                                            @Override
//                                            public void handleMessage(Message msg) {
//                                                if (msg.what == 111) {
//                                                    Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show();
//                                                    Log.e("1","sucess");
//                                                }else if(msg.what == 222){
//                                                    Toast.makeText(context, "收藏失败，后端错误", Toast.LENGTH_SHORT).show();
//                                                    Log.e("1","shibai");
//                                                }else if(msg.what == 333){
//                                                    Toast.makeText(context, "收藏失败"+msg.obj.toString(), Toast.LENGTH_SHORT).show();
//                                                    Log.e("1",msg.obj.toString());
//                                                }else if(msg.what==444){
//                                                    Toast.makeText(context, "您已收藏过该店铺", Toast.LENGTH_SHORT).show();
//
//                                                }
//                                            }
//                                        };
//                                        new Thread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                try{
//                                                    Looper.prepare();
//                                                    String url="http://"+getString(R.string.host)+":8080/addlike";
//                                                    OkHttpClient okHttpClient = new OkHttpClient();
//                                                    FormBody formBody = new FormBody.Builder().add("userid",user.getObjectId()).add("shopid",list.get(finalI).getObjectId()).build();
//                                                    Request request = new Request.Builder()
//                                                            .url(url)
//                                                            .post(formBody)
//                                                            .build();
//                                                    Response response = okHttpClient.newCall(request).execute();
//                                                    String oo=response.body().string();
//                                                    if(oo.equals("true")){
//                                                        Message msg=new Message();
//                                                        msg.what=111;//通知UI线程Json解析完成
//                                                        mhandler.sendMessage(msg);
//                                                    }else if(oo.equals("already")){
//                                                        Message msg=new Message();
//                                                        msg.what=444;//通知UI线程Json解析完成
//                                                        mhandler.sendMessage(msg);
//                                                    } else {
//                                                        Message msg = new Message();
//                                                        msg.what = 222;  //通知UI线程Json解析完成
//                                                        mhandler.sendMessage(msg);
//                                                    }
//                                                    Looper.loop();
//                                                }catch (Exception e){
//                                                    Message msg = new Message();
//                                                    msg.what = 333;  //通知UI线程Json解析完成
//                                                    msg.obj=e.getMessage();
//                                                    mhandler.sendMessage(msg);
//                                                }
//                                            }
//                                        }).start();
//                                    }
//                                    });
//                                dialog.setButton(DialogInterface.BUTTON_NEGATIVE,"取消",new DialogInterface.OnClickListener(){
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                    }
//                                });
//                                dialog.show();
//                                return true;
//                            }
//                        });
//
//                        l.addView(r);
//                        i=i+1;
//                    }
//
//                }else{
//                    Toast.makeText(context,e.getMessage()+e.getErrorCode(),Toast.LENGTH_LONG).show();
//
//                    Log.i("错误",e.getMessage()+e.getErrorCode());
//                }
//            }
//        });
        RelativeLayout re=view.findViewById(R.id.p);
        TextView ptv=view.findViewById(R.id.ptv);
        re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = new String[]{"按评分排序",  "按销量排序","按推荐排序","按创建时间排序","按商品数量排序"};
                android.support.v7.app.AlertDialog dialog =new android.support.v7.app.AlertDialog.Builder(context)
                        .setTitle("选择排序方式")
                        .setItems(items, new DialogInterface.OnClickListener() {//添加列表
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                TextView ptv=view.findViewById(R.id.ptv);
                                if(i==0){
                                    paixu(inflater,i);
                                    ptv.setText("按评分排序");
                                }else if(i==1){
                                    paixu(inflater,i);
                                    ptv.setText("按销量排序");
                                }else if(i==2){
                                    paixu(inflater,i);
                                    ptv.setText("按推荐排序");
                                }else if(i==3){
                                    init(inflater,0);
                                    ptv.setText("按创建时间排序");
                                }else if(i==4){
                                    init(inflater,i);
                                    ptv.setText("按商品数量排序");
                                }
                            }
                        }).create();
                dialog.show();


            }
        });


        return view;
    }
    private class ViewPaperAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view,Object o) {
            return view==o;
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(images.get(position));
            return images.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            container.removeView(images.get(position));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        scheduledExecutorService= Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(
                new ViewPageTask(),
                5,
                5,
                TimeUnit.SECONDS);
    }
    private class ViewPageTask implements Runnable{
        @Override
        public void run() {
            currentItem=(currentItem+1)%imageId.length;
            handler.sendEmptyMessage(0);

        }
    }
    private Handler handler=new Handler(){
      public void handleMessage(android.os.Message msg){
          viewPager.setCurrentItem(currentItem);
      }
    };
    private void paixu(final LayoutInflater inflater, final int n){
        final HashMap<String,Float> shoplist=new HashMap<>();
        final HashMap<String,Float> shopnum=new HashMap<>();
        BmobQuery<Order> orderlist=new BmobQuery<>();
        orderlist.addWhereEqualTo("status","4");
        orderlist.findObjects(new FindListener<Order>() {
            @Override
            public void done(List<Order> list, BmobException e) {
                if(e==null){
                    for (int i = 0; i < list.size(); i++) {
                        if(list.get(i).getStar()!=null) {
                            if (shoplist.containsKey(list.get(i).getShop_id())) {
                                float num = shopnum.get(list.get(i).getShop_id());
                                num++;
                                shopnum.put(list.get(i).getShop_id(), num);
                                float star = Float.parseFloat(list.get(i).getStar());
                                star += shoplist.get(list.get(i).getShop_id());
                                shoplist.put(list.get(i).getShop_id(), star);
                            } else {
                                shoplist.put(list.get(i).getShop_id(), Float.parseFloat(list.get(i).getStar()));
                                shopnum.put(list.get(i).getShop_id(), (float)1);
                            }
                        }
                    }
                    final HashMap<String,Float> starlist=new HashMap<>();
                    final List<Map.Entry<String, Float>> alist;
                    if(n==2){//推荐
                        for(String key: shoplist.keySet()){
                            float ii=shoplist.get(key)/shopnum.get(key);   //平均评价
                            float d=(float) shopnum.get(key)/1000;
                            float num= (d+1)*ii;   //权重
                            starlist.put(key,ii);
                            shoplist.put(key,num);
                            //Log.e("数据",key+"分数"+num);
                        }
                        alist = new ArrayList<>(shoplist.entrySet()); //转换为list
                        Collections.sort(alist,new Comparator<Map.Entry<String, Float>>() {
                            @Override
                            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                                return o2.getValue().compareTo(o1.getValue());
                            }
                        });
                    }else if(n==0){//星级
                        for(String key: shoplist.keySet()){
                            float ii=shoplist.get(key)/shopnum.get(key);   //平均评价
                            starlist.put(key,ii);
                            //Log.e("数据",key+"分数"+num);
                        }
                         alist= new ArrayList<>(starlist.entrySet()); //转换为list
                        Collections.sort(alist,new Comparator<Map.Entry<String, Float>>() {
                            @Override
                            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                                return o2.getValue().compareTo(o1.getValue());
                            }
                        });
                    }else{//
                        for(String key: shoplist.keySet()){
                            float ii=shoplist.get(key)/shopnum.get(key);   //平均评价
                            starlist.put(key,ii);
                            //Log.e("数据",key+"分数"+num);
                        }
                        alist= new ArrayList<>(shopnum.entrySet()); //转换为list
                        Collections.sort(alist,new Comparator<Map.Entry<String, Float>>() {
                            @Override
                            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                                return o2.getValue().compareTo(o1.getValue());
                            }
                        });
                    }
                            Log.e("显示",alist.toString());
                            l.removeAllViews();
                            for(int j=0;j<alist.size();j++){
                                final View v=inflater.inflate(R.layout.shop_main,null);
                                final TextView shopname=v.findViewById(R.id.shop_name);
                                final ImageView shopim=v.findViewById(R.id.shop_icon);
                                RelativeLayout tt=v.findViewById(R.id.nn);
                                tt.setVisibility(View.INVISIBLE);
                                TextView star=v.findViewById(R.id.star);
                                String s=String.valueOf(starlist.get(alist.get(j).getKey())).substring(0,3);
                                star.setText(s);
                                TextView num=v.findViewById(R.id.num);
                                //String snum=
                                num.setText(Math.round(shopnum.get(alist.get(j).getKey()))+"");
                                BmobQuery<Store> shop=new BmobQuery<>();
                                final int finalJ = j;
                                shop.getObject(alist.get(j).getKey(), new QueryListener<Store>() {
                                    @Override
                                    public void done(final Store store, BmobException e) {
                                        shopname.setText(store.getS_name());
                                        v.setOnClickListener(new View.OnClickListener() {
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
                                        File iconfile=new File(Environment.getExternalStorageDirectory()+"/"+alist.get(finalJ).getKey()+".jpg");
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
                                                    icon.download(new File(Environment.getExternalStorageDirectory() + "/" + alist.get(finalJ).getKey() + ".jpg"), new DownloadFileListener() {
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
                                l.addView(v);
                            }
                            BmobQuery<Store> shoplist=new BmobQuery<>();
                            shoplist.findObjects(new FindListener<Store>() {
                                @Override
                                public void done(final List<Store> list, BmobException e) {
                                    for(int i=0;i<list.size();i++){
                                        if(!shopnum.containsKey(list.get(i).getObjectId())){
                                            final View v=inflater.inflate(R.layout.shop_main,null);
                                            TextView star=v.findViewById(R.id.star);
                                            star.setText("0");
                                            TextView num=v.findViewById(R.id.num);
                                            num.setText("0");
                                            RelativeLayout tt=v.findViewById(R.id.nn);
                                            tt.setVisibility(View.INVISIBLE);
                                            final TextView shopname=v.findViewById(R.id.shop_name);
                                            final ImageView shopim=v.findViewById(R.id.shop_icon);
                                            shopname.setText(list.get(i).getS_name());
                                            final int finalI = i;
                                            v.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent =new Intent(getActivity(), ShopActivity.class);
                                                    Bundle bundle=new Bundle();
                                                    //传递name参数
                                                    bundle.putCharSequence("id",list.get(finalI).getObjectId());
                                                    bundle.putCharSequence("shopname",list.get(finalI).getS_name());
                                                    intent.putExtras(bundle);
                                                    startActivity(intent);
                                                }
                                            });
                                            File iconfile=new File(Environment.getExternalStorageDirectory()+"/"+list.get(i).getObjectId()+".jpg");
                                            if(iconfile.exists()) {
                                                Uri uri = Uri.fromFile(iconfile);
                                                try {
                                                    Bitmap b = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
                                                    shopim.setImageBitmap(b);
                                                } catch (FileNotFoundException ex) {
                                                    Log.e("获取头像失败", ex.getMessage());
                                                }
                                            }
                                            l.addView(v);
                                        }
                                    }
                                }
                            });
                        }


            }
        });
    }
    private void init(final LayoutInflater inflater,final int n) {
        l.removeAllViews();
        final BmobQuery<Store> query = new BmobQuery<>();
        if(n==4){
            query.order("-goodsnum");
        }else{
            query.order("createdAt");
        }
        query.findObjects(new FindListener<Store>() {
            @Override
            public void done(final List<Store> list, BmobException e) {
                if (e == null) {
                    int i = 0;
                    for (final Store s : list) {
                        r = inflater.inflate(R.layout.shop_main, null);
                        RelativeLayout pingfen=r.findViewById(R.id.pingfen);
                        pingfen.setVisibility(View.INVISIBLE);
                        if(n==4){
                            TextView nnn=r.findViewById(R.id.gnum);
                            nnn.setText(s.getGoodsnum()+"");
                        }else{
                            RelativeLayout tt=r.findViewById(R.id.nn);
                            tt.setVisibility(View.INVISIBLE);

                        }
                        final ImageView shop_icon = (ImageView) r.findViewById(R.id.shop_icon);
                        File iconfile = new File(Environment.getExternalStorageDirectory() + "/" + s.getObjectId() + ".jpg");
                        if (iconfile.exists()) {
                            Uri uri = Uri.fromFile(iconfile);
                            try {
                                Bitmap b = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
                                shop_icon.setImageBitmap(b);
                            } catch (FileNotFoundException ex) {
                                Log.e("获取头像失败", ex.getMessage());
                            }
                        } else {
                            BmobQuery<MyUser> shop = new BmobQuery<>();
                            shop.getObject(s.getOwnerId(), new QueryListener<MyUser>() {
                                @Override
                                public void done(MyUser myUser, BmobException e) {
                                    if (e == null) {
                                        BmobFile iicon = myUser.getIcon();
                                        iicon.download(new File(Environment.getExternalStorageDirectory() + "/" + s.getObjectId() + ".jpg"), new DownloadFileListener() {
                                            @Override
                                            public void done(String s, BmobException e) {
                                                if (e == null) {
                                                    shop_icon.setImageBitmap(BitmapFactory.decodeFile(s));
                                                } else {
                                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            }

                                            @Override
                                            public void onProgress(Integer integer, long l) {

                                            }
                                        });
                                    } else {
                                        Log.d("错误", e.getMessage());
                                    }
                                }
                            });
                        }
                        final TextView shop_name = (TextView) r.findViewById(R.id.shop_name);
                        shop_name.setText(s.getS_name());
                        final int finalI = i;
                        r.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), ShopActivity.class);
                                Bundle bundle = new Bundle();
                                //传递name参数
                                bundle.putCharSequence("id", list.get(finalI).getObjectId());
                                bundle.putCharSequence("shopname", list.get(finalI).getS_name());
                                intent.putExtras(bundle);
                                startActivity(intent);
                                //Toast.makeText(getActivity(),"点击的店铺是"+ list.get(finalI).getS_name(),Toast.LENGTH_LONG).show();

                            }
                        });
                        longclick(list,finalI);
//                        r.setOnLongClickListener(new View.OnLongClickListener() {
//                            @Override
//                            public boolean onLongClick(View v) {
//                                AlertDialog dialog = new AlertDialog.Builder(context).create();
//                                dialog.setIcon(R.mipmap.star);
//                                dialog.setTitle("添加收藏");
//                                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "是", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        final Handler mhandler = new Handler() {
//                                            @Override
//                                            public void handleMessage(Message msg) {
//                                                if (msg.what == 111) {
//                                                    Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show();
//                                                    Log.e("1", "sucess");
//                                                } else if (msg.what == 222) {
//                                                    Toast.makeText(context, "收藏失败，后端错误", Toast.LENGTH_SHORT).show();
//                                                    Log.e("1", "shibai");
//                                                } else if (msg.what == 333) {
//                                                    Toast.makeText(context, "收藏失败" + msg.obj.toString(), Toast.LENGTH_SHORT).show();
//                                                    Log.e("1", msg.obj.toString());
//                                                } else if (msg.what == 444) {
//                                                    Toast.makeText(context, "您已收藏过该店铺", Toast.LENGTH_SHORT).show();
//
//                                                }
//                                            }
//                                        };
//                                        new Thread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                try {
//                                                    Looper.prepare();
//                                                    String url = "http://" + getString(R.string.host) + ":8080/addlike";
//                                                    OkHttpClient okHttpClient = new OkHttpClient();
//                                                    FormBody formBody = new FormBody.Builder().add("userid", user.getObjectId()).add("shopid", list.get(finalI).getObjectId()).build();
//                                                    Request request = new Request.Builder()
//                                                            .url(url)
//                                                            .post(formBody)
//                                                            .build();
//                                                    Response response = okHttpClient.newCall(request).execute();
//                                                    String oo = response.body().string();
//                                                    if (oo.equals("true")) {
//                                                        Message msg = new Message();
//                                                        msg.what = 111;//通知UI线程Json解析完成
//                                                        mhandler.sendMessage(msg);
//                                                    } else if (oo.equals("already")) {
//                                                        Message msg = new Message();
//                                                        msg.what = 444;//通知UI线程Json解析完成
//                                                        mhandler.sendMessage(msg);
//                                                    } else {
//                                                        Message msg = new Message();
//                                                        msg.what = 222;  //通知UI线程Json解析完成
//                                                        mhandler.sendMessage(msg);
//                                                    }
//                                                    Looper.loop();
//                                                } catch (Exception e) {
//                                                    Message msg = new Message();
//                                                    msg.what = 333;  //通知UI线程Json解析完成
//                                                    msg.obj = e.getMessage();
//                                                    mhandler.sendMessage(msg);
//                                                }
//                                            }
//                                        }).start();
//                                    }
//                                });
//                                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                    }
//                                });
//                                dialog.show();
//                                return true;
//                            }
//                        });
                        l.addView(r);
                        i = i + 1;
                    }

                } else {
                    Toast.makeText(context, e.getMessage() + e.getErrorCode(), Toast.LENGTH_LONG).show();

                    Log.i("错误", e.getMessage() + e.getErrorCode());
                }
            }
        });
    }
    private void longclick(final List<Store> list, final int finalI){
        r.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(context).create();
                dialog.setIcon(R.mipmap.star);
                dialog.setTitle("添加收藏");
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Handler mhandler = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.what == 111) {
                                    Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show();
                                    Log.e("1", "sucess");
                                } else if (msg.what == 222) {
                                    Toast.makeText(context, "收藏失败，后端错误", Toast.LENGTH_SHORT).show();
                                    Log.e("1", "shibai");
                                } else if (msg.what == 333) {
                                    Toast.makeText(context, "收藏失败" + msg.obj.toString(), Toast.LENGTH_SHORT).show();
                                    Log.e("1", msg.obj.toString());
                                } else if (msg.what == 444) {
                                    Toast.makeText(context, "您已收藏过该店铺", Toast.LENGTH_SHORT).show();

                                }
                            }
                        };
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Looper.prepare();
                                    String url = "http://" + getString(R.string.host) + ":8080/addlike";
                                    OkHttpClient okHttpClient = new OkHttpClient();
                                    FormBody formBody = new FormBody.Builder().add("userid", user.getObjectId()).add("shopid", list.get(finalI).getObjectId()).build();
                                    Request request = new Request.Builder()
                                            .url(url)
                                            .post(formBody)
                                            .build();
                                    Response response = okHttpClient.newCall(request).execute();
                                    String oo = response.body().string();
                                    if (oo.equals("true")) {
                                        Message msg = new Message();
                                        msg.what = 111;//通知UI线程Json解析完成
                                        mhandler.sendMessage(msg);
                                    } else if (oo.equals("already")) {
                                        Message msg = new Message();
                                        msg.what = 444;//通知UI线程Json解析完成
                                        mhandler.sendMessage(msg);
                                    } else {
                                        Message msg = new Message();
                                        msg.what = 222;  //通知UI线程Json解析完成
                                        mhandler.sendMessage(msg);
                                    }
                                    Looper.loop();
                                } catch (Exception e) {
                                    Message msg = new Message();
                                    msg.what = 333;  //通知UI线程Json解析完成
                                    msg.obj = e.getMessage();
                                    mhandler.sendMessage(msg);
                                }
                            }
                        }).start();
                    }
                });
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
                return true;
            }
        });
    }
}
