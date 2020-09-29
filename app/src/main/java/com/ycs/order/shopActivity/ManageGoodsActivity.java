package com.ycs.order.shopActivity;

import android.content.DialogInterface;
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
import com.ycs.order.Util.LoadingUtil;
import com.ycs.order.model.Goods;
import com.ycs.order.model.MyUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class ManageGoodsActivity extends AppCompatActivity {
    private LayoutInflater inflater;
    private LinearLayout GoodsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_goods);
        getSupportActionBar().hide();
        View v=findViewById(R.id.fanhui);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView add=findViewById(R.id.newgoods);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ManageGoodsActivity.this, AddGoodActivity.class);
                startActivity(intent);
                finish();
            }
        });
        final MyUser user=MyUser.getCurrentUser(MyUser.class);
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        GoodsList=findViewById(R.id.goodslist);
        BmobQuery<Goods> goodsBmobQuery=new BmobQuery<>();
        goodsBmobQuery.order("-updatedAt");
        goodsBmobQuery.addWhereEqualTo("storeId",user.getBusinessid());
        goodsBmobQuery.addWhereEqualTo("status","1");
        LoadingUtil.Loading_show(ManageGoodsActivity.this);
        goodsBmobQuery.findObjects(new FindListener<Goods>() {
            @Override
            public void done(final List<Goods> list, BmobException e) {

                if(e==null){
                    if(list.size()==0){
                        View v=inflater.inflate(R.layout.not_found,null);
                        TextView tv=v.findViewById(R.id.toast);
                        tv.setText("您的店铺暂时无任何商品哦，快去添加吧");
                        GoodsList.addView(v);
                        LoadingUtil.Loading_close();
                    }else{
                        TextView numtv=findViewById(R.id.num);
                        numtv.setText("您的店铺共有"+list.size()+"件商品");
                        for(int i=0;i<list.size();i++){
                            final Goods good=list.get(i);
                            View v=inflater.inflate(R.layout.business_goods,null);
                            final ImageView tvicon=v.findViewById(R.id.goods_icon);
                            TextView tvname=v.findViewById(R.id.goods_name);
                            TextView tvprice=v.findViewById(R.id.price);

                            tvname.setText(list.get(i).getGood_name());
                            DecimalFormat df=new DecimalFormat("#.00");
                            Double d=list.get(i).getPprice();
//                            tvprice.setText(list.get(i).getPrice());
                            String pp="";
                            if(d<1){
                                tvprice.setText("0"+df.format(d));
                                pp="0"+df.format(d);
                            }else {
                                tvprice.setText(df.format(d));
                                pp=df.format(d);
                            }

                            File iconfile=new File(Environment.getExternalStorageDirectory()+"/"+list.get(i).getObjectId()+".jpg");
                            if(iconfile.exists()){
                                Uri uri = Uri.fromFile(iconfile);
                                try {
                                    Bitmap b = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                                    tvicon.setImageBitmap(b);
                                } catch (FileNotFoundException ex) {
                                    Log.e("获取头像失败", ex.getMessage());
                                }
                            }else{
                                BmobFile goodsicon=list.get(i).getGoods_icon();

                                goodsicon.download(new File(Environment.getExternalStorageDirectory() + "/" + list.get(i).getObjectId() + ".jpg"),
                                        new DownloadFileListener() {
                                            @Override
                                            public void done(String s, BmobException e) {
                                                if(e==null){

                                                    tvicon.setImageBitmap(BitmapFactory.decodeFile(s));
                                                }else{

                                                    Toast.makeText(ManageGoodsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            @Override
                                            public void onProgress(Integer integer, long l) {

                                            }
                                        });
                            }
                            final String finalPp = pp;
                            v.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Bundle bundle=new Bundle();
                                    Intent intent=new Intent(ManageGoodsActivity.this, EditGoodsActivity.class);
                                    bundle.putCharSequence("id",good.getObjectId());
                                    bundle.putCharSequence("name",good.getGood_name());
                                    bundle.putCharSequence("price", finalPp);
                                    bundle.putCharSequence("shopid",good.getStoreId());
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();

                                }
                            });

                            final int finalI = i;
                            v.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    AlertDialog dialog=new AlertDialog.Builder(ManageGoodsActivity.this).create();
                                    dialog.setTitle("删除");
                                    dialog.setMessage("确认删除该商品？");
                                    dialog.setButton(DialogInterface.BUTTON_POSITIVE,"是",new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Goods g=new Goods();
                                            g.setObjectId(list.get(finalI).getObjectId());
                                            g.setStatus("0");
                                            g.update(new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {
                                                    if(e==null){
                                                        Toast.makeText(ManageGoodsActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                                                        Intent intent=new Intent(ManageGoodsActivity.this,ManageGoodsActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }else{
                                                        Toast.makeText(ManageGoodsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                    dialog.setButton(DialogInterface.BUTTON_NEGATIVE,"取消",new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    dialog.show();

                                    return false;
                                }
                            });
                            GoodsList.addView(v);
                        }
                        LoadingUtil.Loading_close();



                    }
                }
            }
        });
    }
}
