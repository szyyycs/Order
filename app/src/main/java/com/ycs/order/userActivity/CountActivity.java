package com.ycs.order.userActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ycs.order.R;
import com.ycs.order.Util.LoadingUtil;
import com.ycs.order.model.Address;
import com.ycs.order.model.Goods;
import com.ycs.order.model.MyUser;
import com.ycs.order.model.Order;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

public class CountActivity extends AppCompatActivity {
        private HashMap<String, Integer> hashMap;
        private MyUser user;
        private LayoutInflater inflater;
        private ArrayList<String> list=new ArrayList<>();
        private String receiveAddress;
    private String receiver;
    private String receiveNum;
    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count);
        getSupportActionBar().hide();
        user=MyUser.getCurrentUser(MyUser.class);
        ImageView fanhui =findViewById(R.id.fanhui);
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final TextView shouhuoaddress=findViewById(R.id.address);
        final TextView shouhuoname=findViewById(R.id.name);
        final TextView shouhuophone=findViewById(R.id.telephone);
        final LinearLayout goodslist=findViewById(R.id.goodslist);
        TextView sum=findViewById(R.id.sum);
        final TextView commit=findViewById(R.id.commit);
        TextView shopname=findViewById(R.id.shopname);
        Bundle bundle = CountActivity.this.getIntent().getExtras();
        String summ=bundle.getString("sum");
        String shop_name=bundle.getString("shopname");
        final String shop_id=bundle.getString("shopid");
        Double ssum=Double.parseDouble(summ);
        ssum=ssum+2;
        DecimalFormat df=new DecimalFormat("#.00");
        final String finalsum=df.format(ssum);
        sum.setText(finalsum);
        shopname.setText(shop_name);
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        hashMap = (HashMap<String, Integer>) getIntent().getSerializableExtra("map");

        for (String key : hashMap.keySet()) {
           View view=inflater.inflate(R.layout.order_shop,null);
           final TextView goodname=view.findViewById(R.id.goods_name);
           final ImageView icon=view.findViewById(R.id.goods_icon);
           TextView num=view.findViewById(R.id.goods_num);
           final TextView price=view.findViewById(R.id.goods_price);
           for(int i=0;i<hashMap.get(key);i++){
               list.add(key);
           }
           String id=key;
           num.setText(hashMap.get(key).toString());
           BmobQuery<Goods> goodsBmobQuery = new BmobQuery<Goods>();
           goodsBmobQuery.getObject(id, new QueryListener<Goods>() {
               @Override
               public void done(Goods goods, BmobException e) {
                   if(e==null){
                       goodname.setText(goods.getGood_name());
                       DecimalFormat df=new DecimalFormat("#.00");
                       Double d=goods.getPprice();
                       price.setText(df.format(d));

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
                           final BmobFile goodsiconf = goods.getGoods_icon();
                           goodsiconf.download(new File(Environment.getExternalStorageDirectory() + "/" + goods.getObjectId() + ".jpg"),
                                   new DownloadFileListener() {
                                       @Override
                                       public void done(String s, BmobException e) {
                                           if (e == null) {
                                               icon.setImageBitmap(BitmapFactory.decodeFile(s));
                                           } else {
                                               Toast.makeText(CountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                           }
                                       }

                                       @Override
                                       public void onProgress(Integer integer, long l) {

                                       }
                                   });
                       }
                   }else{
                       Toast.makeText(CountActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                   }
               }
           });
           goodslist.addView(view);
        }
        BmobQuery<Address> address=new BmobQuery<>();
        final String userid=user.getObjectId();
        address.order("-updatedAt");
        address.order("-shoucang");
        address.addWhereEqualTo("userid",userid);
        address.findObjects(new FindListener<Address>() {
            @Override
            public void done(List<Address> list, BmobException e) {
                if(e==null){
                    if(list.size()==0){
                        flag=0;
//                        shouhuoaddress.setText("暂无收货地址,先到地址页创建一个吧！");
//
//                        shouhuoaddress.setGravity(Gravity.CENTER);
//                        shouhuoaddress.setTextColor(Color.parseColor("#333333"));
//                        shouhuophone.setText("");
//                        shouhuoname.setText("");
                        View v=inflater.inflate(R.layout.blank_address,null);

//                        TextView tt=new TextView(CountActivity.this);
//                        tt.setText("暂无收货地址,先到地址页创建一个吧！");
                        RelativeLayout ad=findViewById(R.id.aadd);
                        ad.addView(v);
                        ad.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog dialog = new AlertDialog.Builder(CountActivity.this).create();
                                dialog.setTitle("转到地址页");
                                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent=new Intent(CountActivity.this, AddAddressActivity.class);
//                                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();

                                    }
                                });
                                dialog.show();
                            }
                        });
                    }else{
                        flag=1;
                        receiveAddress=list.get(0).getUserAddress();
                        receiveNum=list.get(0).getUserPhone();
                        receiver=list.get(0).getName();
                        shouhuoaddress.setText(list.get(0).getUserAddress());
                        shouhuoname.setText(list.get(0).getName());
                        shouhuophone.setText(list.get(0).getUserPhone());
                    }

                }else{
                    Toast.makeText(CountActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(flag==0){
                    Toast.makeText(CountActivity.this, "暂无收货地址,先到地址页创建一个吧！", Toast.LENGTH_SHORT).show();
                }else if(flag==1){
                    AlertDialog dialog = new AlertDialog.Builder(CountActivity.this).create();
                    dialog.setTitle("请输入登陆密码");
                    final EditText ed=new EditText(CountActivity.this);
                    ed.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                ed.setHint("输入您的登陆密码以提交订单");
                    dialog.setView(ed);
                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, "提交订单", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LoadingUtil.Loading_show(CountActivity.this);
                            commit.setClickable(false);
                            final String text = ed.getText().toString();
                            if (!("".equals(text))) {
                                MyUser uu=new MyUser();
                                uu.setUsername(user.getUsername());
                                uu.setPassword(text);
                                uu.login(new SaveListener<MyUser>() {
                                    @Override
                                    public void done(MyUser myUser, BmobException e) {
                                        if(e==null){
                                            Order oo=new Order();
                                            oo.setBuyer(user.getObjectId());
                                            oo.setShop_id(shop_id);
                                            oo.setGoods_list(list);
                                            oo.setSum(finalsum);
                                            oo.setReceive_address(receiveAddress);
                                            oo.setReceive_num(receiveNum);
                                            oo.setReceiver(receiver);
                                            oo.setStatus("0");
                                            oo.save(new SaveListener<String>() {
                                                @Override
                                                public void done(String s, BmobException e) {
                                                    if (e==null){
                                                        Toast.makeText(CountActivity.this, "提交成功！", Toast.LENGTH_SHORT).show();
                                                        Intent intent =new Intent(CountActivity.this, MainActivity.class)
                                                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        intent.putExtra("id", 1);
                                                        startActivity(intent);
                                                        finish();
                                                        LoadingUtil.Loading_close();
                                                    }else{
                                                        commit.setClickable(true);
                                                        Toast.makeText(CountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        Log.e("错误",e.getMessage());
                                                    }
                                                }
                                            });
                                        }else{
                                            commit.setClickable(true);
                                            LoadingUtil.Loading_close();
                                            Toast.makeText(CountActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }else{
                                commit.setClickable(true);
                                LoadingUtil.Loading_close();
                                Toast.makeText(CountActivity.this,"输入为空，请重新输入",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                    dialog.setButton(DialogInterface.BUTTON_NEGATIVE,"取消",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog.show();
                }



//

            }
        });


    }
}
