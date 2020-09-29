package com.ycs.order.shopActivity;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ycs.order.R;
import com.ycs.order.Util.LoadingUtil;
import com.ycs.order.model.Comment;
import com.ycs.order.model.MyUser;
import com.ycs.order.model.Zan;


import org.w3c.dom.Text;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class BusinessCommentActivity extends AppCompatActivity {
    private MyUser user=MyUser.getCurrentUser(MyUser.class);
    private String shopid;
    private  int zanflag[]=new int[20];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_comment);
        getSupportActionBar().hide();
        ImageView fanhui =findViewById(R.id.fanhui);
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle bundle = BusinessCommentActivity.this.getIntent().getExtras();
        final int flag;
        final String id=bundle.getString("shopid");
        if(id.equals("")){
            flag=0;//店家进入
            shopid=user.getBusinessid();
        }else{
            flag=1;//用户查看
            shopid=id;
        }
        String ss="按时间排序";
        init(flag,ss);
        RelativeLayout paixu=findViewById(R.id.paixu);
        paixu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv=findViewById(R.id.t);
                if(tv.getText().equals("按时间排序")){
                    String s="按点赞数排序";
                    tv.setText("按点赞数排序");
                    init(flag,s);
                }else{
                    String s="按时间排序";
                    tv.setText("按时间排序");
                    init(flag,s);
                }

            }
        });

    }
    private void init(final int flag,String s){
        final LinearLayout l=findViewById(R.id.commentlist);
        l.removeAllViews();
        final LayoutInflater inflater=(LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final BmobQuery<Comment> commentBmobQuery=new BmobQuery<>();
        if(s.equals("按时间排序")){
            commentBmobQuery.order("-createdAt");
        }else{
            commentBmobQuery.order("-znum");
        }
        commentBmobQuery.addWhereEqualTo("shop_id",shopid);
        commentBmobQuery.findObjects(new FindListener<Comment>() {
            @Override
            public void done(final List<Comment> list, BmobException e) {
                if(e==null){
                    if(list.size()==0){
                        View v=inflater.inflate(R.layout.not_found,null);
                        TextView vv=v.findViewById(R.id.toast);
                        vv.setText("您暂时未发表任何评论");
                        l.addView(v);
                        View vvv=findViewById(R.id.scrollView);
                        vvv.setBackgroundColor(Color.parseColor("#F1F1F1"));
                    }else{
                        LoadingUtil.Loading_show(BusinessCommentActivity.this);
                        for(int i=0;i<list.size();i++){
//                       final int zanflag;
                            final int finalI=i;
                            View v=inflater.inflate(R.layout.shop_comment,null);
                            final ImageView usericontv=v.findViewById(R.id.usericon);
                            final TextView usernametv=v.findViewById(R.id.username);
                            final ImageView zan=v.findViewById(R.id.zan);
                            final TextView zannum=v.findViewById(R.id.zannum);
                            TextView timetv=v.findViewById(R.id.time);
                            timetv.setText(list.get(i).getCreatedAt());
                            TextView commenttv=v.findViewById(R.id.comment);
                            if(list.get(i).getZnum()==0){

                            }else{
                                zannum.setText(list.get(i).getZnum()+"");
                            }

                            commenttv.setText(list.get(i).getText());
                            RatingBar rb=v.findViewById(R.id.star);
                            final float star=Float.parseFloat(list.get(i).getStar());
                            rb.setRating(star);
                            BmobQuery<MyUser> userBmobQuery=new BmobQuery<>();
                            userBmobQuery.getObject(list.get(i).getUser_id(), new QueryListener<MyUser>() {
                                @Override
                                public void done(MyUser myUser, BmobException e) {
                                    if(e==null){
                                        int len=myUser.getUsername().length();
                                        String name;
                                        if(len<4){
                                            name=myUser.getUsername().substring(0,1)+"***"+myUser.getUsername().substring(len-1,len);
                                        }else{
                                            name =myUser.getUsername().substring(0,1)+"***"+myUser.getUsername().substring(4,len);
                                        }
                                        usernametv.setText(name);
                                        BmobFile usericon=myUser.getIcon();
                                        usericon.download(new DownloadFileListener() {
                                            @Override
                                            public void done(String s, BmobException e) {
                                                if(e==null){
                                                    usericontv.setImageBitmap(BitmapFactory.decodeFile(s));
                                                    LoadingUtil.Loading_close();
                                                }
                                            }

                                            @Override
                                            public void onProgress(Integer integer, long l) {

                                            }
                                        });
                                    }
                                }
                            });

                            if (flag == 1) {

                                BmobQuery<Zan> zanBmobQuery=new BmobQuery<>();
                                zanBmobQuery.addWhereEqualTo("commentid",list.get(i).getObjectId());
                                zanBmobQuery.addWhereEqualTo("uid",user.getObjectId());
                                zanBmobQuery.findObjects(new FindListener<Zan>() {
                                    @Override
                                    public void done(List<Zan> alist, BmobException e) {

                                        if(alist.size()!=0){
                                            zan.setImageResource(R.mipmap.zan1);
                                            zannum.setTextColor(getResources().getColor(R.color.red));
                                            zanflag[finalI]=1;//已经赞过，取消点赞
                                        }else{
                                            zanflag[finalI]=0;//未赞过
                                        }

                                    }
                                });
                                zan.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
//                                           Log.e("flag",zanflag+"");
                                        if (zanflag[finalI] == 0) {
                                            final int num = list.get(finalI).getZnum() + 1;

                                            Comment c = new Comment();
                                            c.setZnum(num);
                                            c.setObjectId(list.get(finalI).getObjectId());
                                            c.update(new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {

                                                    Zan z = new Zan();
                                                    z.setCommentid(list.get(finalI).getObjectId());
                                                    z.setUid(user.getObjectId());
                                                    z.save(new SaveListener<String>() {
                                                        @Override
                                                        public void done(String s, BmobException e) {
                                                            if (e == null) {
                                                                zannum.setText(num + "");
                                                                zan.setImageResource(R.mipmap.zan1);
                                                                zannum.setTextColor(getResources().getColor(R.color.red));
                                                                zanflag[finalI]= 1;
                                                                list.get(finalI).setZnum(num);
                                                            } else {
                                                                Toast.makeText(BusinessCommentActivity.this, "点赞失败", Toast.LENGTH_SHORT).show();
                                                                Log.e("错误", "失败");
                                                            }

                                                        }

                                                    });

                                                }

                                            });
                                        }else if(zanflag[finalI]==1){
                                            Log.e("赞过","赞过");
                                            Comment c=new Comment();
                                            final int num=list.get(finalI).getZnum()-1;

                                            c.setZnum(num);
                                            c.setObjectId(list.get(finalI).getObjectId());
                                            c.update(new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {
                                                    if(e==null){
                                                        BmobQuery<Zan> zanQuery=new BmobQuery<>();
                                                        zanQuery.addWhereEqualTo("commentid",list.get(finalI).getObjectId());
                                                        zanQuery.addWhereEqualTo("uid",user.getObjectId());
                                                        zanQuery.findObjects(new FindListener<Zan>() {
                                                            @Override
                                                            public void done(List<Zan> zanlist, BmobException e) {
                                                                if(e==null){
                                                                    for(int i=0;i<zanlist.size();i++){
                                                                        Zan z=new Zan();
                                                                        z.setObjectId(zanlist.get(i).getObjectId());
                                                                        z.delete(new UpdateListener() {
                                                                            @Override
                                                                            public void done(BmobException e) {
                                                                                if(e==null){


                                                                                }else{
                                                                                    Toast.makeText(BusinessCommentActivity.this, "取消点赞失败", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                    zannum.setText(num + "");
                                                                    zan.setImageResource(R.mipmap.zan);
                                                                    zannum.setTextColor(getResources().getColor(R.color.black));
                                                                    zanflag[finalI]=0;
                                                                    list.get(finalI).setZnum(num);

                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });


                                        }
                                    }
                                });

                            }
                            l.addView(v);

                        }
                        LoadingUtil.Loading_close();
                    }
                }
            }
        });
    }
}
