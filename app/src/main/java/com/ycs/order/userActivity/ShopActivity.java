package com.ycs.order.userActivity;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ycs.order.R;
import com.ycs.order.Util.LoadingUtil;
import com.ycs.order.model.Goods;
import com.ycs.order.model.MyUser;
import com.ycs.order.model.Store;
import com.ycs.order.shopActivity.BusinessCommentActivity;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

public class ShopActivity extends AppCompatActivity {
    private String id;
    private LinearLayout l;
    private String shopName;
    private LayoutInflater inflater;
    private RelativeLayout re;
    private double sum;
    private int num[];
    private double prices[];
    private HashMap<String,Integer> hashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        getSupportActionBar().hide();
        final ImageView fanhui=(ImageView)findViewById(R.id.fanhui);
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView commentv=findViewById(R.id.comment);

        Bundle bundle = ShopActivity.this.getIntent().getExtras();
        shopName = bundle.getString("shopname");
        id=bundle.getString("id");
        commentv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(ShopActivity.this, BusinessCommentActivity.class);
                Bundle bundle=new Bundle();
                bundle.putCharSequence("shopid",id);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

//        Toast.makeText(ShopActivity.this,"点击的是"+id+shopName,Toast.LENGTH_LONG).show();
        TextView shopn=(TextView)findViewById(R.id.main_shop_name);
        final ImageView shopicon=(ImageView)findViewById(R.id.shopicon);
        shopn.setText(shopName);
        File iconfile=new File(Environment.getExternalStorageDirectory()+"/"+id+".jpg");
        if(iconfile.exists()){
            Uri uri = Uri.fromFile(iconfile);
            try {
                Bitmap b = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                shopicon.setImageBitmap(b);
            } catch (FileNotFoundException ex) {
                Log.e("获取头像失败", ex.getMessage());
            }
        }else{
            BmobQuery<Store> storeBmobQuery=new BmobQuery<>();
            storeBmobQuery.getObject(id, new QueryListener<Store>() {
                @Override
                public void done(Store store, BmobException e) {
                    if (e==null){
                        BmobQuery<MyUser> shop=new BmobQuery<>();
                        shop.getObject(store.getOwnerId(), new QueryListener<MyUser>() {
                            @Override
                            public void done(MyUser myUser, BmobException e) {
                                if(e==null){
                                    BmobFile shopiconf=myUser.getIcon();
                                    shopiconf.download(new File(Environment.getExternalStorageDirectory()+"/"+id+".jpg"),new DownloadFileListener() {
                                        @Override
                                        public void done(String s, BmobException e) {
                                            if(e==null){
                                                shopicon.setImageBitmap(BitmapFactory.decodeFile(s));
                                            }else{
                                                Toast.makeText(ShopActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onProgress(Integer integer, long l) {

                                        }
                                    });
                                }else{
                                    Toast.makeText(ShopActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        Toast.makeText(ShopActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
        init("按上架时间排序");
        RelativeLayout pa=findViewById(R.id.paixu);
        final TextView p=findViewById(R.id.p);
        pa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(p.getText().equals("按上架时间排序")){
                    init("按价格排序");
                    p.setText("按价格排序");
                }else if(p.getText().equals("按价格排序")){
                    init("按上架时间排序");
                    p.setText("按上架时间排序");
                }
            }
        });
        TextView jiesuan=findViewById(R.id.jiesuan);
        jiesuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hashMap.size()!=0){
                    Log.e("数据",hashMap.toString());
                    Intent intent =new Intent(ShopActivity.this, CountActivity.class);
                    //传递name参数
                    Bundle bundle=new Bundle();
                    //传递name参数
                    bundle.putCharSequence("shopname",shopName);
                    bundle.putCharSequence("sum",sum+"");
                    bundle.putCharSequence("shopid",id+"");
                    intent.putExtras(bundle);
                    intent.putExtra("map",(Serializable)hashMap);
                    startActivity(intent);
                }else{
                    Toast.makeText(ShopActivity.this,"购物车为空",Toast.LENGTH_SHORT).show();
                }


            }
        });

    }
    private void init(String s){
        l=(LinearLayout)findViewById(R.id.goods_list);
        l.removeAllViews();
        final TextView summ=(TextView)findViewById(R.id.sum);
        hashMap=new HashMap<>();
        re=(RelativeLayout)findViewById(R.id.empty) ;
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        BmobQuery<Goods> query=new BmobQuery<Goods>();
        query.addWhereEqualTo("storeId",id);
        if(s.equals("按上架时间排序")){
            query.order("-createdAt");
        }else{
            query.order("pprice");
        }
        query.addWhereEqualTo("status","1");
        query.findObjects(new FindListener<Goods>() {
            @Override
            public void done(final List<Goods> list, BmobException e) {
                if(e==null&&list.size()!=0){
                    LoadingUtil.Loading_show(ShopActivity.this);
                    prices=new double[list.size()];
                    num=new int[list.size()];
                    sum=0;
                    int i=0;
                    for(Goods g:list){
                        View r=inflater.inflate(R.layout.shop_goods,null);
                        final ImageView goodiconim=r.findViewById(R.id.goods_icon);
                        File iconfile=new File(Environment.getExternalStorageDirectory()+"/"+list.get(i).getObjectId()+".jpg");
                        if(iconfile.exists()){
                            Uri uri = Uri.fromFile(iconfile);
                            try {
                                Bitmap b = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                                goodiconim.setImageBitmap(b);
                            } catch (FileNotFoundException ex) {
                                Log.e("获取头像失败", ex.getMessage());
                            }
                        }else{
                            BmobFile goodsicon=list.get(i).getGoods_icon();
                            goodsicon.download(new File(Environment.getExternalStorageDirectory() + "/" + list.get(i).getObjectId() + ".jpg")
                                    , new DownloadFileListener() {
                                        @Override
                                        public void done(String s, BmobException e) {
                                            if(e==null){
                                                goodiconim.setImageBitmap(BitmapFactory.decodeFile(s));
                                            }else{
                                                Toast.makeText(ShopActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onProgress(Integer integer, long l) {

                                        }
                                    });
                        }


                        TextView goods_name=(TextView)r.findViewById(R.id.goods_name);
                        goods_name.setText(g.getGood_name());
                        final TextView add=(TextView)r.findViewById(R.id.add);
                        final TextView sub=(TextView)r.findViewById(R.id.sub);
                        final TextView edsum=(TextView) r.findViewById(R.id.editsum);
                        final TextView price=(TextView)r.findViewById(R.id.price);

                        DecimalFormat df=new DecimalFormat("#.00");
                        Double d=g.getPprice();
                        price.setText(df.format(d));

                        prices[i]=Double.parseDouble(price.getText().toString());
                        num[i]= Integer.parseInt(edsum.getText().toString());
                        if(num[i]==0){
                            sub.setVisibility(View.INVISIBLE);
                            edsum.setVisibility(View.INVISIBLE);
                        }
                        final int finali=i;
                        add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sub.setVisibility(View.VISIBLE);
                                edsum.setVisibility(View.VISIBLE);
                                if(Integer.parseInt(edsum.getText().toString())<9999) {
                                    num[finali] = Integer.parseInt(edsum.getText().toString()) + 1;
                                    sum+=prices[finali];
                                    //sum=(double)Math.round(sum*100)/100;
                                    DecimalFormat df=new DecimalFormat("#.00");
                                    summ.setText(df.format(sum));
                                    edsum.setText("" + num[finali]);
                                    hashMap.put(list.get(finali).getObjectId(),num[finali]);

                                }else{

                                }
                            }
                        });
                        sub.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(Integer.parseInt(edsum.getText().toString())>0){
                                    num[finali]=Integer.parseInt(edsum.getText().toString())-1;
                                    edsum.setText(""+num[finali]);
                                    sum-=prices[finali];
                                    sum=(double)Math.round(sum*100)/100;
//                                    String strsum=String.format("%.2f");
                                    DecimalFormat df=new DecimalFormat("#.00");
                                    if(sum==0){
                                        summ.setText("0.00");
                                    }else{
                                        summ.setText(df.format(sum));
                                    }
                                    hashMap.put(list.get(finali).getObjectId(),num[finali]);

                                }else{

                                }
                                if(Integer.parseInt(edsum.getText().toString())==0){
                                    sub.setVisibility(View.INVISIBLE);
                                    edsum.setVisibility(View.INVISIBLE);
                                    hashMap.remove(list.get(finali).getObjectId());
                                }

                            }
                        });
                        i++;
                        l.addView(r);

                    }
                    LoadingUtil.Loading_close();
                    View a=inflater.inflate(R.layout.blank,null);
                    re.addView(a);
                }else if(e==null&&list.size()==0){
                    RelativeLayout empty= (RelativeLayout) inflater.inflate(R.layout.not_found,null);
                    re.addView(empty);

                }else{
                    Toast.makeText(ShopActivity.this,e.getMessage()+e.getErrorCode(),Toast.LENGTH_LONG).show();
                    Log.i("错误",e.getMessage()+e.getErrorCode());
                }
            }
        });
}

}
