package com.ycs.order.shopActivity;

import android.Manifest;
import android.content.Context;
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
import android.provider.OpenableColumns;
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
import java.io.FileOutputStream;
import java.io.InputStream;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class AddGoodActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_add_good);
        getSupportActionBar().hide();
        user=MyUser.getCurrentUser(MyUser.class);
        View v=findViewById(R.id.fanhui);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddGoodActivity.this, ManageGoodsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        View pick=findViewById(R.id.pickicon);
        iconimg=findViewById(R.id.icon);
        tishi=findViewById(R.id.tishi);
        final EditText nameed=findViewById(R.id.name);
        final EditText priceed=findViewById(R.id.price);
        final TextView savetv=findViewById(R.id.save);
        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items3 = new String[]{"拍照获取图片",  "相册导入图片"};//创建item
                AlertDialog alertDialog3 = new AlertDialog.Builder(AddGoodActivity.this)
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

        savetv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name=nameed.getText().toString();
                final String price=priceed.getText().toString();

                if(name.equals("")){
                    Toast.makeText(AddGoodActivity.this, "商品名称未填写", Toast.LENGTH_SHORT).show();
                } else if(price.equals("")) {
                    Toast.makeText(AddGoodActivity.this, "价格未填写", Toast.LENGTH_SHORT).show();
                }else if(!tishi.getText().equals("照片已选择")){
                    Toast.makeText(AddGoodActivity.this, "商品图片未选择", Toast.LENGTH_SHORT).show();
                }else{
                    savetv.setClickable(false);
                    LoadingUtil.Loading_show(AddGoodActivity.this);
                    final BmobFile file;
                    if(Build.VERSION.SDK_INT<Build.VERSION_CODES.N){
                        file = new BmobFile(uri2File(imageUri));
                    }else{
                        file = new BmobFile(new File(getFilePathForN(AddGoodActivity.this,imageUri)));
                    }
                    file.upload(new UploadFileListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                Goods good=new Goods();
                                good.setGood_name(name);
                                good.setPprice(Double.parseDouble(price));
                                good.setStoreId(user.getBusinessid());
                                good.setGoods_icon(file);
                                good.setStatus("1");
                                good.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if(e==null){
                                            Toast.makeText(AddGoodActivity.this, "保存成功！", Toast.LENGTH_SHORT).show();
                                            Intent intent=new Intent(AddGoodActivity.this,ManageGoodsActivity.class);
                                            startActivity(intent);
                                            LoadingUtil.Loading_close();
                                            finish();
                                        }else{
                                            savetv.setClickable(true);
                                            LoadingUtil.Loading_close();
                                            Toast.makeText(AddGoodActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else{
                                savetv.setClickable(true);
                                LoadingUtil.Loading_close();
                                Toast.makeText(AddGoodActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
//            imageUri= FileProvider.getUriForFile(this,getPackageName()+".provider",output);
            imageUri=FileProvider.getUriForFile(AddGoodActivity.this, "com.ycs.order.provider", output);
            Log.e("数据",imageUri.toString());

//            imageUri= FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".provider",output);
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
                        Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        iconimg.setImageBitmap(bit);
                        tishi.setText("照片已选择");
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
                        Log.d("ta啊啊",e.getMessage());
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
                Toast.makeText(AddGoodActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AddGoodActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
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

    private static String getFilePathForN(Context context, Uri uri) {
        try {
            Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null);
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            String name = (returnCursor.getString(nameIndex));
            File file = new File(context.getFilesDir(), name);
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            returnCursor.close();
            inputStream.close();
            outputStream.close();
            return file.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {

// 按下键盘上返回按钮
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent =new Intent(AddGoodActivity.this,ManageGoodsActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

}
