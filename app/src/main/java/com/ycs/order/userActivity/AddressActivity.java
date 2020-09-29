package com.ycs.order.userActivity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.ycs.order.model.Address;
import com.ycs.order.model.MyUser;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class AddressActivity extends AppCompatActivity {
    private LayoutInflater inflater;
    private LinearLayout adresslist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        getSupportActionBar().hide();
        final MyUser user=MyUser.getCurrentUser(MyUser.class);
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        adresslist=findViewById(R.id.adresslist);
        ImageView fanhui =findViewById(R.id.fanhui);
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final TextView newaddress=findViewById(R.id.newaddress);
        newaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddressActivity.this, AddAddressActivity.class);
                startActivity(intent);
                finish();
            }
        });
        BmobQuery<Address> ad=new BmobQuery<>();
        String userid=user.getObjectId();

        ad.order("-updatedAt");
        ad.order("-shoucang");
        ad.addWhereEqualTo("userid",userid);
        ad.findObjects(new FindListener<Address>() {
            @Override
            public void done(final List<Address> list, BmobException e) {
                if(e==null){
                    if(list.size()==0){
                        View view=inflater.inflate(R.layout.not_found,null);
                        TextView v=view.findViewById(R.id.toast);
                        v.setText("您的地址为空，现在去创建一个吧！");
                        Log.e("提示","地址为空");
                        adresslist.setBackgroundColor(Color.parseColor("#EEEEEE"));
                        adresslist.addView(view);
                    }else{
                        for(int i=0;i<list.size();i++){
                            final Address a=list.get(i);
                            View view=inflater.inflate(R.layout.address,null);
                            TextView tvname=view.findViewById(R.id.name1);
                            TextView tvad=view.findViewById(R.id.address1);
                            TextView tvph=view.findViewById(R.id.telephone1);
                            TextView shoucangim=view.findViewById(R.id.shoucang);
                            if(a.getShoucang()!=null){
                                if(a.getShoucang().equals("0")){
                                    shoucangim.setVisibility(View.INVISIBLE);
                                }
                            }
                            tvname.setText(a.getName());
                            tvad.setText(a.getUserAddress());
                            tvph.setText(a.getUserPhone());
                            ImageView edit=view.findViewById(R.id.editaddress);
                            edit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent =new Intent(AddressActivity.this, EditAddressActivity.class);
                                    Bundle bundle=new Bundle();
                                    //传递name参数
                                    bundle.putCharSequence("adressid",a.getObjectId());
                                    bundle.putCharSequence("adress",a.getUserAddress());
                                    bundle.putCharSequence("phone",a.getUserPhone());
                                    bundle.putCharSequence("name",a.getName());
                                    bundle.putCharSequence("shoucang",a.getShoucang());
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            adresslist.addView(view);
                        }
                    }

                }else{
                    Toast.makeText(AddressActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
        final SwipeRefreshLayout refreshLayout =findViewById(R.id.refresh);
//        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        refreshLayout.setRefreshing(false);
//                        adresslist.removeAllViews();
//                        BmobQuery<Address> ad=new BmobQuery<>();
//                        String userid=user.getObjectId();
//                        ad.order("-updatedAt");
//                        ad.order("-shoucang");
//                        ad.addWhereEqualTo("userid",userid);
//                        ad.findObjects(new FindListener<Address>() {
//                            @Override
//                            public void done(final List<Address> list, BmobException e) {
//                                if(e==null){
//                                    if(list.size()==0){
//                                        View view=inflater.inflate(R.layout.not_found,null);
//                                        TextView v=view.findViewById(R.id.toast);
//                                        v.setText("您的地址为空，现在去创建一个吧！");
//                                        Log.e("提示","地址为空");
//                                        adresslist.addView(view);
//                                    }else{
//                                        for(int i=0;i<list.size();i++){
//                                            final Address a=list.get(i);
//                                            View view=inflater.inflate(R.layout.address,null);
//                                            TextView tvname=view.findViewById(R.id.name1);
//                                            TextView tvad=view.findViewById(R.id.address1);
//                                            TextView tvph=view.findViewById(R.id.telephone1);
//                                            TextView shoucangim=view.findViewById(R.id.shoucang);
//                                            if(a.getShoucang()!=null){
//                                                if(a.getShoucang().equals("0")){
//                                                    shoucangim.setVisibility(View.INVISIBLE);
//                                                }
//                                            }
//                                            tvname.setText(a.getName());
//                                            tvad.setText(a.getUserAddress());
//                                            tvph.setText(a.getUserPhone());
//                                            ImageView edit=view.findViewById(R.id.editaddress);
//                                            adresslist.addView(view);
//                                        }
//                                    }
//
//                                }else{
//                                    Toast.makeText(AddressActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//
//                    }
//                },2000);
//            }
//        });



    }
}
