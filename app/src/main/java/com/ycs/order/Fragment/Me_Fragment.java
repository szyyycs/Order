package com.ycs.order.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ycs.order.userActivity.AboutMeActivity;
import com.ycs.order.userActivity.AddressActivity;
import com.ycs.order.userActivity.HistoryActivity;
import com.ycs.order.userActivity.InfoActivity;
import com.ycs.order.userActivity.LikeActivity;
import com.ycs.order.userActivity.MyCommentActivity;
import com.ycs.order.R;
import com.ycs.order.Util.SharedPreferencesUtil;
import com.ycs.order.userActivity.loginActivity;
import com.ycs.order.model.MyUser;

import java.io.File;
import java.io.FileNotFoundException;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.QueryListener;

public class Me_Fragment extends Fragment {
    private  View view;
    private MyUser user;
    private ImageView usericon;
    private File iconfile;
    private TextView nicknameview;
    private TextView numview;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.me_fragment,null);
        final Context context=getActivity();
        Bmob.initialize(getActivity(),"682dba275359d04511948d626bff513f");
        usericon=view.findViewById(R.id.icon);
        final SwipeRefreshLayout refreshLayout=view.findViewById(R.id.aa);
        user = BmobUser.getCurrentUser(MyUser.class);
        nicknameview=view .findViewById(R.id.nickname);
        numview=view.findViewById(R.id.num);
        //刷新界面
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        find();
                    }
                },2000);
            }
        });
        try{
            setNickname();
            iconfile=new File(Environment.getExternalStorageDirectory()+"/userImage.jpg");
            if(user.getIcon()!=null) {
                if (iconfile.exists()) {
                    Uri i = Uri.fromFile(iconfile);
                    try {
                        Bitmap b = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(i));
                        usericon.setImageBitmap(b);
                    } catch (FileNotFoundException ex) {
                        Log.e("获取头像失败", ex.getMessage());
                    }
                } else {
                    BmobFile icon = user.getIcon();

                    icon.download(new File(Environment.getExternalStorageDirectory() + "/userImage.jpg"), new DownloadFileListener() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                usericon.setImageBitmap(BitmapFactory.decodeFile(s));   //根据地址解码并显示图片
                            } else {
                                Log.e("获取头像失败", e.getMessage());
                            }
                        }

                        @Override
                        public void onProgress(Integer integer, long l) {

                        }
                    });

                }
            }
            //信息修改页面
            RelativeLayout info = (RelativeLayout) view.findViewById(R.id.info);
            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(getActivity(), InfoActivity.class);
                    startActivity(intent);
//                    getActivity().startActivityForResult(intent, 111);

                }
            });
            //退出程序
            TextView exit = (TextView) view.findViewById(R.id.exitall);
            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
                    dialog.setTitle("退出");
                    dialog.setMessage("确认退出程序？");

                    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, "是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    });
                    dialog.show();
                }
            });
            //退出登陆
            TextView exitView=(TextView)view.findViewById(R.id.exit);
            exitView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog dialog=new AlertDialog.Builder(getActivity()).create();
                    dialog.setTitle("退出");
                    dialog.setMessage("确认退出登陆？");
                    dialog.setButton(DialogInterface.BUTTON_POSITIVE,"是",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            SharedPreferencesUtil nS;
                            nS=new SharedPreferencesUtil(getActivity(),"user");
                            nS.setValue("account","");
                            nS.setValue("pwd","");
                            nS.setValue("type","");
                            File file=new File(Environment.getExternalStorageDirectory()+"/userImage.jpg");
                            try {
                                if (file.exists()) {
                                    file.delete();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            BmobUser.logOut();
                            Intent intent =new Intent(getActivity(), loginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);;
                            startActivity(intent);
                            getActivity().finish();
                        }
                    });
                    dialog.setButton(DialogInterface.BUTTON_NEGATIVE,"取消",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog.show();
                }
            });
            //关于我们
            View aboutView=(View)view.findViewById(R.id.about_us);
            aboutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(getActivity(), AboutMeActivity.class);
                    startActivity(intent);
//                    LoadingUtil.Loading_show(getActivity());
                }
            });
            //历史
            View history=view.findViewById(R.id.history);
            history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, HistoryActivity.class);
                    startActivity(intent);

                }
            });

            //地址管理界面
            TextView addressview=view.findViewById(R.id.myaddress);
            addressview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(context, AddressActivity.class);
                    startActivity(intent);
                }
            });
            //评论
            View commenttv=view.findViewById(R.id.comment);
            commenttv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, MyCommentActivity.class);
                    startActivity(intent);
                }
            });
            //收藏
            View shoucang=view.findViewById(R.id.shoucang1);
            shoucang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, LikeActivity.class);
                    startActivity(intent);
                }
            });

        }catch (Exception e){
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT);
        }


        return view;
    }



    public void setNickname(){

        String id=user.getObjectId();
        String name=user.getUsername();
        String phone=user.getMobilePhoneNumber();
        if(name.equals(phone)){
            name=phone.substring(0,3)+"****"+phone.substring(7,11);
        }
        if(phone.length()==11){
            phone=phone.substring(0,3)+"****"+phone.substring(7,11);
        }
        TextView tvnickname=(TextView)view.findViewById(R.id.nickname);
        tvnickname.setText(name);
        TextView tvnum=(TextView)view.findViewById(R.id.num);
        tvnum.setText(phone);
        }
     public void find(){
        BmobQuery<MyUser> uq=new BmobQuery<>();
        uq.getObject(user.getObjectId(), new QueryListener<MyUser>() {
            @Override
            public void done(MyUser myUser, BmobException e) {
                if(e==null){
                    String name=myUser.getUsername();
                    String phone=myUser.getMobilePhoneNumber();
                    String id=myUser.getObjectId();
                    if(name.equals(phone)){
                        name=id;
                    }
                    if(phone.length()==11){
                        phone=phone.substring(0,3)+"****"+phone.substring(7,11);
                    }
                    nicknameview.setText(name);
                    numview.setText(phone);
                    BmobFile icon=myUser.getIcon();
                    icon.download(new File(Environment.getExternalStorageDirectory()+"/userImage.jpg"),new DownloadFileListener() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e == null){
                                usericon.setImageBitmap(BitmapFactory.decodeFile(s));   //根据地址解码并显示图片
                            }
                            else{
                                Log.e("获取头像失败",e.getMessage());
                            }
                        }

                        @Override
                        public void onProgress(Integer integer, long l) {

                        }
                    });

                }
            }
        });
     }


}
