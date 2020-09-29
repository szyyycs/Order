package com.ycs.order.userActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ycs.order.R;
import com.ycs.order.Util.LoadingUtil;
import com.ycs.order.model.Comment;
import com.ycs.order.model.MyUser;
import com.ycs.order.model.Order;
import com.ycs.order.model.Store;

import java.io.File;
import java.io.FileNotFoundException;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class CommentActivity extends AppCompatActivity {
    private MyUser user=MyUser.getCurrentUser(MyUser.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        getSupportActionBar().hide();
        ImageView fanhui =findViewById(R.id.fanhui);
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ImageView iconimg=findViewById(R.id.icon);
        final TextView shoptv=findViewById(R.id.shopname);
        final EditText commented=findViewById(R.id.comment);
        final RatingBar ratingBar=findViewById(R.id.rb);
        Bundle bundle = getIntent().getExtras();
        final String shop_id=bundle.getString("shopid");
        final BmobQuery<Store> s=new BmobQuery<>();
        s.getObject(shop_id, new QueryListener<Store>() {
            @Override
            public void done(Store store, BmobException e) {
                if(e==null){
                    shoptv.setText(store.getS_name());
                }
            }
        });


        final String orderid=bundle.getString("orderid");
        File iconfile=new File(Environment.getExternalStorageDirectory()+"/"+shop_id+".jpg");
        if(iconfile.exists()){
            Uri uri = Uri.fromFile(iconfile);
            try {
                Bitmap b = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                iconimg.setImageBitmap(b);
            } catch (FileNotFoundException ex) {
                Log.e("获取头像失败", ex.getMessage());
            }
        }
        final TextView savetv=findViewById(R.id.save);
        savetv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savetv.setClickable(false);
                String comstr= commented.getText().toString();
                if(comstr.equals("")){
                    comstr="这位顾客什么都没有评论呢";
                }
                LoadingUtil.Loading_show(CommentActivity.this);

                float rating=ratingBar.getRating();
                final String star=rating+"";
                Comment comment=new Comment();
                comment.setOrder_id(orderid);
                comment.setShop_id(shop_id);
                comment.setStar(star);
                comment.setText(comstr);
                comment.setZnum(0);
                comment.setUser_id(user.getObjectId());
                comment.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e==null){
                            Order o=new Order();
                            o.setStatus("4");
                            o.setStar(star);
                            o.setObjectId(orderid);
                            o.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        Toast.makeText(CommentActivity.this, "提交成功！", Toast.LENGTH_SHORT).show();
                                        LoadingUtil.Loading_close();
                                        Intent intent =new Intent(CommentActivity.this, MainActivity.class)
                                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("id", 1);
                                        startActivity(intent);
                                        finish();
                                    }else{
                                        savetv.setClickable(true);
                                        Toast.makeText(CommentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        LoadingUtil.Loading_close();
                                    }
                                }
                            });


                        }else{
                            savetv.setClickable(true);
                            Toast.makeText(CommentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            LoadingUtil.Loading_close();
                        }
                    }
                });



            }
        });



    }

}
