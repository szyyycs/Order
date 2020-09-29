package com.ycs.order.userActivity;

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
import android.widget.RatingBar;
import android.widget.TextView;

import com.ycs.order.R;
import com.ycs.order.model.Comment;
import com.ycs.order.model.MyUser;
import com.ycs.order.model.Store;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

public class MyCommentActivity extends AppCompatActivity {
    private MyUser user=MyUser.getCurrentUser(MyUser.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_comment);
        getSupportActionBar().hide();
        ImageView fanhui =findViewById(R.id.fanhui);
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final LinearLayout l=findViewById(R.id.commentlist);
        final LayoutInflater inflater=(LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        BmobQuery<Comment> commentBmobQuery=new BmobQuery<>();
        commentBmobQuery.order("-createdAt");
        commentBmobQuery.addWhereEqualTo("user_id",user.getObjectId());
        commentBmobQuery.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> list, BmobException e) {
                if(e==null){
                    if(list.size()==0){
                        View v=inflater.inflate(R.layout.not_found,null);
                        TextView vv=v.findViewById(R.id.toast);
                        vv.setText("您暂时未发表任何评论");
                        l.addView(v);

                        View vvv=findViewById(R.id.scrollView);
                        vvv.setBackgroundColor(Color.parseColor("#F1F1F1"));
                    }else{
                        for(int i=0;i<list.size();i++){
                            View v=inflater.inflate(R.layout.comment_layout,null);
                            ImageView shopicontv=v.findViewById(R.id.icon);
                            ImageView usericontv=v.findViewById(R.id.usericon);
                            final TextView shopnametv=v.findViewById(R.id.shopname);
                            TextView usernametv=v.findViewById(R.id.username);
                            TextView timetv=v.findViewById(R.id.time);
                            TextView commenttv=v.findViewById(R.id.comment);
                            RatingBar rb=v.findViewById(R.id.star);
                            final float star=Float.parseFloat(list.get(i).getStar());
                            rb.setRating(star);
                            usernametv.setText(user.getUsername());
                            timetv.setText(list.get(i).getUpdatedAt());
                            commenttv.setText(list.get(i).getText());
                            File iconfile=new File(Environment.getExternalStorageDirectory() + "/userImage.jpg");
                            if(iconfile.exists()){
                                Uri uri = Uri.fromFile(iconfile);
                                try {
                                    Bitmap b = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                                    usericontv.setImageBitmap(b);
                                } catch (FileNotFoundException ex) {
                                    Log.e("获取头像失败", ex.getMessage());
                                }
                            }
                            File shop=new File(Environment.getExternalStorageDirectory()+"/"+list.get(i).getShop_id()+".jpg");
                            if(shop.exists()){
                                Uri uri = Uri.fromFile(shop);
                                try {
                                    Bitmap b = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                                   shopicontv.setImageBitmap(b);
                                } catch (FileNotFoundException ex) {
                                    Log.e("获取头像失败", ex.getMessage());
                                }

                            }
                            BmobQuery<Store> s=new BmobQuery<>();
                            s.getObject(list.get(i).getShop_id(), new QueryListener<Store>() {
                                @Override
                                public void done(Store store, BmobException e) {
                                    if(e==null){
                                        shopnametv.setText(store.getS_name());
                                    }
                                }
                            });


                            l.addView(v);

                        }
                    }
                }
            }
        });



    }
}
