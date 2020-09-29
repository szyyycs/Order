package com.ycs.order.userActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.ycs.order.Fragment.Main_Framgment;
import com.ycs.order.Fragment.Me_Fragment;
import com.ycs.order.Fragment.cart_fragment;
import com.ycs.order.shopActivity.MainBusinessActivity;
import com.ycs.order.R;
import com.ycs.order.Util.SharedPreferencesUtil;

public class MainActivity extends AppCompatActivity {

    private TextView tvm;
    private TextView tv1;
    private TextView tvc;
    private TextView tv2;
    private TextView tvme;
    private TextView tv3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        SharedPreferencesUtil mS;
        mS=new SharedPreferencesUtil(this,"user");
        String account=mS.getValue("account","");
        String pwd=mS.getValue("pwd","");
        String type=mS.getValue("type","");
        if(account.equals("")||pwd.equals("")){
            Intent intent =new Intent(MainActivity.this, loginActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else if(type.equals("1")){
            Intent intent =new Intent(MainActivity.this, MainBusinessActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }


        View maintv=findViewById(R.id.main1);
        maintv.setOnClickListener(l);
        View carttv=findViewById(R.id.cart1);
        carttv.setOnClickListener(l);
        View metv=findViewById(R.id.me1);
        metv.setOnClickListener(l);
        int id = getIntent().getIntExtra("id", 0);
        if(id==1){
            FragmentManager fm=getSupportFragmentManager();
            FragmentTransaction ft=fm.beginTransaction();
            Fragment f=null;
            tvm=(TextView)findViewById(R.id.main);
            tv1=(TextView)findViewById(R.id.text1);
            tvc=(TextView)findViewById(R.id.cart);
            tv2=(TextView)findViewById(R.id.text2);
            tvme=(TextView)findViewById(R.id.me);
            tv3=(TextView)findViewById(R.id.text3);
            tvc.setBackgroundResource(R.mipmap.orderr1);
            tv1.setTextColor(getResources().getColor(R.color.select));
            tvm.setBackgroundResource(R.mipmap.main);
            tv2.setTextColor(getResources().getColor(R.color.selected));
            tvme.setBackgroundResource(R.mipmap.me);
            tv3.setTextColor(getResources().getColor(R.color.select));
            f=new cart_fragment();
            ft.replace(R.id.fragment, f);
//            ft.commit();
            ft.commitAllowingStateLoss();
        }
        if(id==2){
            FragmentManager fm=getSupportFragmentManager();
            FragmentTransaction ft=fm.beginTransaction();
            Fragment f=null;
            tvm=(TextView)findViewById(R.id.main);
            tv1=(TextView)findViewById(R.id.text1);
            tvc=(TextView)findViewById(R.id.cart);
            tv2=(TextView)findViewById(R.id.text2);
            tvme=(TextView)findViewById(R.id.me);
            tv3=(TextView)findViewById(R.id.text3);
            tvc.setBackgroundResource(R.mipmap.orderr);
            tv1.setTextColor(getResources().getColor(R.color.select));
            tvm.setBackgroundResource(R.mipmap.main);
            tv2.setTextColor(getResources().getColor(R.color.select));
            tvme.setBackgroundResource(R.mipmap.me2);
            tv3.setTextColor(getResources().getColor(R.color.selected));
            f=new Me_Fragment();
            ft.replace(R.id.fragment, f);
//            ft.commit();
            ft.commitAllowingStateLoss();
        }

    }

    View.OnClickListener l=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            FragmentManager fm=getSupportFragmentManager();
            FragmentTransaction ft=fm.beginTransaction();
            Fragment f=null;
            tvm=(TextView)findViewById(R.id.main);
            tv1=(TextView)findViewById(R.id.text1);
            tvc=(TextView)findViewById(R.id.cart);
            tv2=(TextView)findViewById(R.id.text2);
            tvme=(TextView)findViewById(R.id.me);
            tv3=(TextView)findViewById(R.id.text3);
            switch (v.getId()){
                case R.id.main1:
                    tvc.setBackgroundResource(R.mipmap.orderr);
                    tv1.setTextColor(getResources().getColor(R.color.selected));
                    tvm.setBackgroundResource(R.mipmap.main2);
                    tv2.setTextColor(getResources().getColor(R.color.select));
                    tvme.setBackgroundResource(R.mipmap.me);
                    tv3.setTextColor(getResources().getColor(R.color.select));
                    f=new Main_Framgment();
                    break;
                case R.id.cart1:
                    tvc.setBackgroundResource(R.mipmap.orderr1);
                    tv1.setTextColor(getResources().getColor(R.color.select));
                    tvm.setBackgroundResource(R.mipmap.main);
                    tv2.setTextColor(getResources().getColor(R.color.selected));
                    tvme.setBackgroundResource(R.mipmap.me);
                    tv3.setTextColor(getResources().getColor(R.color.select));
                    f=new cart_fragment();
                    break;
                case R.id.me1:
                    tvc.setBackgroundResource(R.mipmap.orderr);
                    tv1.setTextColor(getResources().getColor(R.color.select));
                    tvm.setBackgroundResource(R.mipmap.main);
                    tv2.setTextColor(getResources().getColor(R.color.select));
                    tvme.setBackgroundResource(R.mipmap.me2);
                    tv3.setTextColor(getResources().getColor(R.color.selected));
                    f=new Me_Fragment();
                    break;
                default:
                    break;
            }
//            Fragment bf = (Fragment) fm.findFragmentById(R.id.fragment);
//            if(bf==null||bf!=f){
//                ft.add(R.id.fragment,f);
//            }
//            List<Fragment> ls = fm.getFragments();
//            if(ls!=null){
//                for(Fragment ff:ls){
//                    ft.hide(f);
//                }
//            }
//            ft.show(f);
            if(!f.isAdded()){
                ft.replace(R.id.fragment, f);
//            ft.commit();
                ft.commitAllowingStateLoss();
            }


        }
    };

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

// 按下键盘上返回按钮
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
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
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
