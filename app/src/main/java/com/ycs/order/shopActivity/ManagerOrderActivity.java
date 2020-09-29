package com.ycs.order.shopActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class ManagerOrderActivity extends AppCompatActivity {
        private LayoutInflater inflater;
        private double s;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_order);
        getSupportActionBar().hide();
        final MyUser user=MyUser.getCurrentUser(MyUser.class);
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View v=findViewById(R.id.fanhui);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final LinearLayout l=findViewById(R.id.orderlist);
        BmobQuery<Order> orderlist=new BmobQuery<>();
        orderlist.order("status,-createdAt");

        orderlist.addWhereEqualTo("shop_id",user.getBusinessid());
        LoadingUtil.Loading_show(ManagerOrderActivity.this);
        orderlist.findObjects(new FindListener<Order>() {
            @Override
            public void done(final List<Order> list, BmobException e) {
                if(e==null){
                    if (list.size()==0){
                        View v=inflater.inflate(R.layout.not_found,null);
                        TextView tv=v.findViewById(R.id.toast);
                        tv.setText("您的店铺暂无历史订单");
                        l.addView(v);
                        l.setBackgroundColor(Color.parseColor("#eeeeee"));
                        LoadingUtil.Loading_close();
                    }else {
                       // String d=list.get(0).getCreatedAt();
                        for (int i = 0; i < list.size(); i++) {
                            View v = inflater.inflate(R.layout.shop_order, null);
                            String address = list.get(i).getReceive_address();
                            String num = list.get(i).getReceive_num();
                            String name = list.get(i).getReceiver();
                            ArrayList<String> goods=list.get(i).getGoods_list();
                            TextView nametv = v.findViewById(R.id.username);
                            TextView numtv = v.findViewById(R.id.userphone);
                            TextView addresstv = v.findViewById(R.id.useraddress);
                            TextView timetv=v.findViewById(R.id.time);
                            final TextView song=v.findViewById(R.id.song);
                            final TextView jiedan=v.findViewById(R.id.jiedan);
                            final TextView statustv=v.findViewById(R.id.status);
                            final int fi=i;
                            if(list.get(i).getStatus().equals("0")){
                                song.setVisibility(View.INVISIBLE);
                                jiedan.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                                Order o=new Order();
                                                o.setStatus("1");
                                                o.update(list.get(fi).getObjectId(),new UpdateListener() {
                                                    @Override
                                                    public void done(BmobException e) {
                                                        if(e==null){
                                                            Toast.makeText(ManagerOrderActivity.this, "接单成功", Toast.LENGTH_SHORT).show();
                                                            statustv.setText("等待配送");
                                                            jiedan.setVisibility(View.INVISIBLE);
                                                            song.setVisibility(View.VISIBLE);
                                                        }else{
                                                            Toast.makeText(ManagerOrderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });


                                    }
                                });
                            }else if(list.get(i).getStatus().equals("1")){
                                jiedan.setVisibility(View.INVISIBLE);

                            }else if(list.get(i).getStatus().equals("2")){
                                jiedan.setVisibility(View.INVISIBLE);
                                song.setVisibility(View.INVISIBLE);
                                statustv.setText("等待用户确认收货");
                            }else if(list.get(i).getStatus().equals("3")){
                                jiedan.setVisibility(View.INVISIBLE);
                                song.setVisibility(View.INVISIBLE);
                                statustv.setText("订单已完成，等待用户评论");
                            }else if(list.get(i).getStatus().equals("5")){
                                jiedan.setVisibility(View.INVISIBLE);
                                song.setVisibility(View.INVISIBLE);
                                statustv.setText("订单已被用户取消");
                            }else if(list.get(i).getStatus().equals("4")){
                                jiedan.setVisibility(View.INVISIBLE);
                                song.setVisibility(View.INVISIBLE);
                                statustv.setText("用户已评论，请在评价页查看");
                            }

                            song.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Order o=new Order();
                                    o.setStatus("2");
                                    o.update(list.get(fi).getObjectId(),new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if(e==null){
                                                Toast.makeText(ManagerOrderActivity.this, "提交配送", Toast.LENGTH_SHORT).show();
                                                statustv.setText("等待用户确认收货");
                                                jiedan.setVisibility(View.INVISIBLE);
                                                song.setVisibility(View.INVISIBLE);
                                            }else{
                                                Toast.makeText(ManagerOrderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                            timetv.setText(list.get(i).getCreatedAt());
                            final ImageView iconimg = v.findViewById(R.id.user_icon);
                            LinearLayout rl = v.findViewById(R.id.goodslist);
                            TextView sumtv = v.findViewById(R.id.sum);
                            TextView goodsnumtv = v.findViewById(R.id.goodsnum);
                            BmobQuery<MyUser> uu=new BmobQuery<>();
                            uu.getObject(list.get(i).getBuyer(), new QueryListener<MyUser>() {
                                @Override
                                public void done(MyUser myUser, BmobException e) {
                                    if(e==null){
                                        BmobFile buyericon=myUser.getIcon();
                                        buyericon.download(new DownloadFileListener() {
                                            @Override
                                            public void done(String s, BmobException e) {
                                                if(e==null){
                                                    iconimg.setImageBitmap(BitmapFactory.decodeFile(s));
                                                }else{
                                                    Toast.makeText(ManagerOrderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onProgress(Integer integer, long l) {

                                            }
                                        });
                                    }else{
                                        Toast.makeText(ManagerOrderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                            nametv.setText(name);
                            numtv.setText(num);
                            addresstv.setText(address);
                            goodsnumtv.setText(goods.size()+"");

                            sumtv.setText(list.get(i).getSum());
                            HashMap<String,Integer> hashMap=new HashMap<>();
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
                                View vv=inflater.inflate(R.layout.goodslist,null);
                                final TextView goodsname=vv.findViewById(R.id.goodsname);
                                final ImageView goodsicon=vv.findViewById(R.id.goodsicon);

                                BmobQuery<Goods> goodsBmobQuery=new BmobQuery<>();
                                goodsBmobQuery.getObject(key, new QueryListener<Goods>() {
                                    @Override
                                    public void done(Goods goods, BmobException e) {
                                        if (e==null){
                                            goodsname.setText(goods.getGood_name());
                                            File iconfile=new File(Environment.getExternalStorageDirectory()+"/"+goods.getObjectId()+".jpg");
                                            if(iconfile.exists()){
                                                Uri uri = Uri.fromFile(iconfile);
                                                try {
                                                    Bitmap b = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                                                    goodsicon.setImageBitmap(b);
                                                } catch (FileNotFoundException ex) {
                                                    Log.e("获取头像失败", ex.getMessage());
                                                }
                                            }else {
                                                BmobFile icon = goods.getGoods_icon();
                                                icon.download(new File(Environment.getExternalStorageDirectory() + "/" + goods.getObjectId()+ ".jpg"),
                                                        new DownloadFileListener() {
                                                            @Override
                                                            public void done(String s, BmobException e) {
                                                                if(e==null){
                                                                    goodsicon.setImageBitmap(BitmapFactory.decodeFile(s));
                                                                }else {
                                                                    Toast.makeText(ManagerOrderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }

                                                            @Override
                                                            public void onProgress(Integer integer, long l) {

                                                            }
                                                        });
                                            }
                                        }else{
                                            Toast.makeText(ManagerOrderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                rl.addView(vv);
                            }
//
                            l.addView(v);

                        }
                        LoadingUtil.Loading_close();
                    }
                }else{
                    LoadingUtil.Loading_close();
                    Toast.makeText(ManagerOrderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView monthtv=findViewById(R.id.month);
        final TextView numtv=findViewById(R.id.ordernum);
        final TextView sumtv=findViewById(R.id.input);
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH)+1;
        monthtv.setText(month+"");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createdAtStart = c.get(Calendar.YEAR)+"-"+month+"-"+"01"+" 00:00:00";
        Log.e("时间",createdAtStart);
        Date createdAtDateStart = null;
        try {
            createdAtDateStart = sdf.parse(createdAtStart);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        BmobDate bmobCreatedAtDateStart = new BmobDate(createdAtDateStart);

        String createdAtEnd = c.get(Calendar.YEAR)+"-"+(month+1)+"-"+"01"+" 00:00:00";
        Log.e("时间",createdAtEnd);
        Date createdAtDateEnd = null;
        try {
            createdAtDateEnd = sdf.parse(createdAtEnd);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        BmobDate bmobCreatedAtDateEnd = new BmobDate(createdAtDateEnd);

        BmobQuery<Order> categoryBmobQueryStart = new BmobQuery<>();
        categoryBmobQueryStart.addWhereGreaterThanOrEqualTo("createdAt", bmobCreatedAtDateStart);
        BmobQuery<Order> categoryBmobQueryEnd = new BmobQuery<>();
        categoryBmobQueryEnd.addWhereLessThanOrEqualTo("createdAt", bmobCreatedAtDateEnd);
        List<BmobQuery<Order>> queries = new ArrayList<>();
        queries.add(categoryBmobQueryStart);
        queries.add(categoryBmobQueryEnd);

        BmobQuery<Order> orderlistt=new BmobQuery<>();
        orderlistt.and(queries);
        orderlistt.addWhereEqualTo("shop_id",user.getBusinessid());
        String[] names = {"3", "4"};
        orderlistt.addWhereContainedIn("status", Arrays.asList(names));
//        orderlistt.addWhereEqualTo("status","2");
        orderlistt.findObjects(new FindListener<Order>() {
            @Override
            public void done(List<Order> list, BmobException e) {
                if(e==null){
                    numtv.setText(list.size()+"");
                    for(int i=0;i<list.size();i++){
                        s+=Double.parseDouble(list.get(i).getSum());
                    }
                    if(s==0){
                        sumtv.setText("0.00");
                        return;
                    }
                    DecimalFormat df=new DecimalFormat("#.00");
                    String strsum=df.format(s);
                    sumtv.setText(strsum);
                }else{
                    Toast.makeText(ManagerOrderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
