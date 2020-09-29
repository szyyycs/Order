package com.ycs.order.userActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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
import com.ycs.order.Util.LoadingUtil;
import com.ycs.order.model.Address;
import com.ycs.order.model.MyUser;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class EditAddressActivity extends AppCompatActivity {
    private String id;
    private String phone;
    private String name;
    private String address;
    private String moren;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);
        getSupportActionBar().hide();
        final MyUser user=MyUser.getCurrentUser(MyUser.class);
        Bundle bundle = EditAddressActivity.this.getIntent().getExtras();
        ImageView fanhui =findViewById(R.id.fanhui);
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EditAddressActivity.this, AddressActivity.class);
                //.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }
        });
        id=bundle.getString("adressid");
        phone=bundle.getString("phone");
        address=bundle.getString("adress");
        name=bundle.getString("name");
        moren=bundle.getString("shoucang");
        final EditText addressed=findViewById(R.id.address);
        final EditText nameed=findViewById(R.id.name);
        final EditText phoneed=findViewById(R.id.phone);
        final CheckBox shoucang=findViewById(R.id.moren);
        final TextView saveaddress=findViewById(R.id.saveaddress);
        final TextView delete=findViewById(R.id.deleteaddress);

        addressed.setText(address);
        nameed.setText(name);
        phoneed.setText(phone);
        if(moren.equals("1")){
            shoucang.setChecked(true);
        }
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(EditAddressActivity.this)
                    .setMessage("确定删除该地址吗？")
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    LoadingUtil.Loading_show(EditAddressActivity.this);
                                    delete.setClickable(false);
                                    final Address address1=new Address();
                                    address1.setObjectId(id);
                                    address1.delete(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if(e==null){
                                                Toast.makeText(EditAddressActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                                                Intent intent=new Intent(EditAddressActivity.this,AddressActivity.class);
                                                //.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                LoadingUtil.Loading_close();
                                                finish();
                                            }else{
                                                LoadingUtil.Loading_close();
                                                delete.setClickable(true);
                                                Toast.makeText(EditAddressActivity.this,"出错"+e.getMessage(),Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });

                                }
                            }).show();


            }
        });
        saveaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address=addressed.getText().toString();
                String phone=phoneed.getText().toString();
                String name=nameed.getText().toString();
                String userid=user.getObjectId();
                if(shoucang.isChecked()){
                    moren="1";
                }else{
                    moren="0";
                }
                Log.e("id",id);
                if(address.equals("")){
                    Toast.makeText(EditAddressActivity.this,"地址为空",Toast.LENGTH_SHORT).show();
                }else if(name.equals("")){
                    Toast.makeText(EditAddressActivity.this,"收货人名字为空",Toast.LENGTH_SHORT).show();
                }else if(phone.equals("")||phone.length()!=11){
                    Toast.makeText(EditAddressActivity.this,"请输入正确的手机号",Toast.LENGTH_SHORT).show();
                }else {
                    saveaddress.setClickable(false);
                    LoadingUtil.Loading_show(EditAddressActivity.this);
                    final Address ad=new Address();
                    ad.setUserAddress(address);
                    ad.setUserid(userid);
                    ad.setUserPhone(phone);
                    ad.setName(name);
                    ad.setShoucang(moren);
                    ad.update(id, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                if(moren.equals("1")){
                                    BmobQuery<Address> ad=new BmobQuery<>();
                                    String userid=user.getObjectId();
                                    ad.addWhereEqualTo("userid",userid);
                                    ad.addWhereNotEqualTo("objectId",id);
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
                                                                    Toast.makeText(EditAddressActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
                                                                    Intent intent=new Intent(EditAddressActivity.this,AddressActivity.class);
                                                                            //.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                    startActivity(intent);
                                                                    LoadingUtil.Loading_close();
                                                                    finish();
                                                                }else{

                                                                }
                                                            }
                                                        });
                                                    }
                                                }else{
                                                    Toast.makeText(EditAddressActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
                                                    Intent intent=new Intent(EditAddressActivity.this,AddressActivity.class);
                                                    //.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    LoadingUtil.Loading_close();
                                                    finish();
                                                }
                                            }else{
                                                saveaddress.setClickable(true);
                                                LoadingUtil.Loading_close();
                                                Toast.makeText(EditAddressActivity.this,e.getMessage(),Toast.LENGTH_SHORT);
                                            }
                                        }
                                    });
                                }else{
                                    LoadingUtil.Loading_close();
                                    Toast.makeText(EditAddressActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(EditAddressActivity.this,AddressActivity.class);
                                            //.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }


                            }else{
                                saveaddress.setClickable(true);
                                LoadingUtil.Loading_close();
                                Toast.makeText(EditAddressActivity.this,"更新失败"+e.getMessage(),Toast.LENGTH_SHORT).show();
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
            Intent intent=new Intent(EditAddressActivity.this,AddressActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


}
