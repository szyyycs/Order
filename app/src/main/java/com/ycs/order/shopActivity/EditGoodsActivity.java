package com.ycs.order.shopActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ycs.order.R;
import com.ycs.order.Util.LoadingUtil;
import com.ycs.order.model.Goods;
import com.ycs.order.model.MyUser;

import java.io.File;
import java.io.FileNotFoundException;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class EditGoodsActivity extends AppCompatActivity {
    private String id;
    private String price;
    private String name;
    private String shopid;
    private Uri imageUri;
    private static final int CROP_PHOTO = 2;
    private static final int REQUEST_CODE_PICK_IMAGE=3;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 6;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 7;
    private File output;
    private File iconfile;
    private ImageView iconimg;

    private MyUser user;
    private TextView tishi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_goods);
        getSupportActionBar().hide();
        ImageView fanhui =findViewById(R.id.fanhui);
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EditGoodsActivity.this, ManageGoodsActivity.class);
                //.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        Bundle bundle = EditGoodsActivity.this.getIntent().getExtras();
        id=bundle.getString("id");
        price=bundle.getString("price");
        name=bundle.getString("name");
        shopid=bundle.getString("shopid");

        final EditText nametv=findViewById(R.id.name);
        final EditText pricetv=findViewById(R.id.price);
        final TextView  savetv=findViewById(R.id.save);
        TextView deletv=findViewById(R.id.deleteaddress);
        deletv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(EditGoodsActivity.this)
                        .setMessage("确定删除该商品吗？")
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                LoadingUtil.Loading_show(EditGoodsActivity.this);
                                Goods g=new Goods();
                                g.setObjectId(id);
                                g.setStatus("0");
                                g.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if(e==null){
                                            Toast.makeText(EditGoodsActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(EditGoodsActivity.this, ManageGoodsActivity.class);
                                            startActivity(intent);
                                            LoadingUtil.Loading_close();
                                            finish();
                                        }else{
                                            Toast.makeText(EditGoodsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }).show();
            }
        });

        iconimg=findViewById(R.id.icon);
        tishi=findViewById(R.id.tishi);
        iconimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items3 = new String[]{"拍照获取图片",  "相册导入图片"};//创建item
                AlertDialog alertDialog3 = new AlertDialog.Builder(EditGoodsActivity.this)
                        .setTitle("更改头像")
                        .setItems(items3, new DialogInterface.OnClickListener() {//添加列表
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i==0){
                                    takePhone();
                                }else{
                                    choosePhone();
                                }

                            }
                        })
                        .create();
                alertDialog3.show();
            }
        });

        File iconfile=new File(Environment.getExternalStorageDirectory()+"/"+id+".jpg");
        if(iconfile.exists()) {
            Uri uri = Uri.fromFile(iconfile);
            try {
                Bitmap b = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                iconimg.setImageBitmap(b);
            } catch (FileNotFoundException ex) {
                Log.e("获取头像失败", ex.getMessage());
            }
        }
        nametv.setText(name);
        pricetv.setText(price);
        savetv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = nametv.getText().toString();
                final String price = pricetv.getText().toString();

                    savetv.setClickable(false);
                    LoadingUtil.Loading_show(EditGoodsActivity.this);
                    if(imageUri!=null){
                        final BmobFile file = new BmobFile(uri2File(imageUri));
                        file.upload(new UploadFileListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Goods good = new Goods();
                                    good.setGood_name(name);

                                    good.setPprice(Double.parseDouble(price));
                                    good.setGoods_icon(file);
                                    good.update( id,new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                Toast.makeText(EditGoodsActivity.this, "保存成功！", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(EditGoodsActivity.this, ManageGoodsActivity.class);
                                                startActivity(intent);
                                                LoadingUtil.Loading_close();
                                                finish();
                                            } else {
                                                savetv.setClickable(true);
                                                LoadingUtil.Loading_close();
                                                Toast.makeText(EditGoodsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                } else {
                                    savetv.setClickable(true);
                                    LoadingUtil.Loading_close();
                                    Toast.makeText(EditGoodsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{

                        Goods good = new Goods();
                        good.setGood_name(name);
                        good.setPprice(Double.parseDouble(price));
                        good.update( id,new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Toast.makeText(EditGoodsActivity.this, "保存成功！", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(EditGoodsActivity.this, ManageGoodsActivity.class);
                                    startActivity(intent);
                                    LoadingUtil.Loading_close();
                                    finish();
                                } else {
                                    savetv.setClickable(true);
                                    LoadingUtil.Loading_close();
                                    Toast.makeText(EditGoodsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }


                }

        });


    }



    public void takePhone(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);

        }else {
            try{
                takePhoto();
            }catch (Exception e){
                Log.e("错误提示",e.getMessage());
            }

        }

    }
    public void choosePhone(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE2);

        }else {
            choosePhoto();
        }
    }
    void takePhoto(){
        /**
         * 最后一个参数是文件夹的名称，可以随便起
         */
        File file=new File(Environment.getExternalStorageDirectory(),"拍照");
        if(!file.exists()){
            file.mkdir();
        }
        output=new File(file,System.currentTimeMillis()+".jpg");
        try {
            if (output.exists()) {
                output.delete();
            }
            output.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * 隐式打开拍照的Activity，并且传入CROP_PHOTO常量作为拍照结束后回调的标志
         */
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.N){
            imageUri = Uri.fromFile(output);
        }else{
            imageUri= FileProvider.getUriForFile(this,getPackageName()+".provider",output);
        }

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CROP_PHOTO);

    }
    void choosePhoto(){
        /**
         * 打开选择图片的界面
         */
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);

    }
    public void onActivityResult(int req, int res, Intent data) {
        switch (req) {
            /**
             * 拍照的请求标志
             */
            case CROP_PHOTO:
                if (res==RESULT_OK) {
                    try {
                        /**
                         * 该uri就是照片文件夹对应的uri
                         */
                        Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        iconimg.setImageBitmap(bit);
                        tishi.setText("照片已选择");
//

                    } catch (Exception ex) {

                        Log.d("tag", ex.getMessage());
                        Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }


                break;
            /**
             * 从相册中选取图片的请求标志
             */

            case REQUEST_CODE_PICK_IMAGE:
                if (res == RESULT_OK) {
                    try {
                        /**
                         * 该uri是上一个Activity返回的
                         */
                        imageUri = data.getData();
                        Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        iconimg.setImageBitmap(bit);
                        tishi.setText("照片已选择");

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("tag",e.getMessage());
                        Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Log.i("提示", "失败");
                }

                break;

            default:
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                takePhoto();
            } else
            {
                Toast.makeText(EditGoodsActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }


        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE2)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                choosePhoto();
            } else
            {
                // Permission Denied
                Toast.makeText(EditGoodsActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    public File uri2File(Uri uri) {
        String img_path;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = managedQuery(uri, proj, null,
                null, null);
        if (actualimagecursor == null) {
            img_path = uri.getPath();
        } else {
            int actual_image_column_index = actualimagecursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            img_path = actualimagecursor
                    .getString(actual_image_column_index);
        }
        File file = new File(img_path);
        return file;
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {

// 按下键盘上返回按钮
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent=new Intent(EditGoodsActivity.this,ManageGoodsActivity.class);
            //.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
