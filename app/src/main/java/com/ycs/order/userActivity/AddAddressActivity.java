package com.ycs.order.userActivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ycs.order.R;
import com.ycs.order.model.Address;
import com.ycs.order.model.MyUser;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class AddAddressActivity extends AppCompatActivity {
    private String shoucang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        getSupportActionBar().hide();
        final MyUser user=MyUser.getCurrentUser(MyUser.class);
        ImageView fanhui =findViewById(R.id.fanhui);
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddAddressActivity.this, AddressActivity.class);
                //.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }
        });
        final EditText addressed=findViewById(R.id.address);
        final EditText nameed=findViewById(R.id.name);
        final EditText phoneed=findViewById(R.id.phone);
        final CheckBox moren=findViewById(R.id.moren);
        final TextView saveaddress=findViewById(R.id.saveaddress);
        saveaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String address=addressed.getText().toString();
                String phone=phoneed.getText().toString();
                String name=nameed.getText().toString();
                String userid=user.getObjectId();
                if(moren.isChecked()){
                    shoucang="1";
                }else{
                    shoucang="0";
                }
                if(address.equals("")){
                    Toast.makeText(AddAddressActivity.this,"地址为空",Toast.LENGTH_SHORT).show();
                }else if(name.equals("")){
                    Toast.makeText(AddAddressActivity.this,"收货人名字为空",Toast.LENGTH_SHORT).show();
                }else if(phone.equals("")||phone.length()!=11){
                    Toast.makeText(AddAddressActivity.this,"请输入正确的手机号",Toast.LENGTH_SHORT).show();
                }else{
                    saveaddress.setClickable(false);

                    final Address ad=new Address();
                    ad.setUserAddress(address);
                    ad.setUserid(userid);
                    ad.setUserPhone(phone);
                    ad.setName(name);
                    ad.setShoucang(shoucang);
                    ad.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e==null){
                                Toast.makeText(AddAddressActivity.this,"保存地址成功！",Toast.LENGTH_SHORT).show();
                                if(shoucang.equals("1")){
                                    BmobQuery<Address> ad=new BmobQuery<>();
                                    String userid=user.getObjectId();
                                    ad.addWhereEqualTo("userid",userid);
                                    ad.addWhereNotEqualTo("objectId",s);
                                    ad.addWhereEqualTo("shoucang","1");
                                    ad.findObjects(new FindListener<Address>() {
                                        @Override
                                        public void done(List<Address> list, BmobException e) {
                                            if(e==null){
                                                if(list.size()!=0){
                                                    for(int i=0;i<list.size();i++){
                                                        Address a=list.get(i);
                                                        a.setShoucang("0");
                                                        a.update(a.getObjectId(),new UpdateListener() {
                                                            @Override
                                                            public void done(BmobException e) {
                                                                if(e==null){
                                                                    Intent intent=new Intent(AddAddressActivity.this,AddressActivity.class);
                                                                            //.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            }
                                                        });
                                                    }
                                                }else{
                                                    Intent intent=new Intent(AddAddressActivity.this,AddressActivity.class);
                                                    //.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }else{
                                                Toast.makeText(AddAddressActivity.this,e.getMessage(),Toast.LENGTH_SHORT);
                                            }
                                        }
                                    });
                                } else{
                                    Log.e("AAAAAAAAAAAAAAA","查询错误");
                                    Intent intent=new Intent(AddAddressActivity.this,AddressActivity.class);
                                            //.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }

                            }else{
                                Toast.makeText(AddAddressActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {

// 按下键盘上返回按钮
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent=new Intent(AddAddressActivity.this,AddressActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

}
