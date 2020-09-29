package com.ycs.order.userActivity;

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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ycs.order.R;
import com.ycs.order.Util.LoadingUtil;
import com.ycs.order.model.MyUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InfoActivity extends AppCompatActivity {

    private Uri imageUri;
    private StringBuffer namme;
    private static final int CROP_PHOTO = 2;
    private static final int REQUEST_CODE_PICK_IMAGE=3;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 6;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 7;
    private  File output;
    private File iconfile;
    private ImageView icon;
    private MyUser user;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        getSupportActionBar().hide();
        Bmob.initialize(InfoActivity.this,"682dba275359d04511948d626bff513f");
        user = BmobUser.getCurrentUser(MyUser.class);
        ImageView fanhui=(ImageView)findViewById(R.id.fanhui);
        icon=findViewById(R.id.pic);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog d=new AlertDialog.Builder(InfoActivity.this).create();
                LayoutInflater inflater=LayoutInflater.from(InfoActivity.this);
                View image=inflater.inflate(R.layout.dialog,null);
                ImageView img= image.findViewById(R.id.largeimage);
                d.setView(image);
                iconfile=new File(Environment.getExternalStorageDirectory()+"/userImage.jpg");
                Uri i = Uri.fromFile(iconfile);
            try {
                Bitmap b = BitmapFactory.decodeStream(getContentResolver().openInputStream(i));
                img.setImageBitmap(b);
            } catch (FileNotFoundException ex) {
                Log.e("获取头像失败", ex.getMessage());
            }
                d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                d.show();
            }
        });
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(InfoActivity.this, MainActivity.class);
                intent.putExtra("id", 2);
                startActivity(intent);
                finish();
            }
        });
        TextView numtv=(TextView)findViewById(R.id.num);
        String id=user.getObjectId();
        namme=new StringBuffer(user.getUsername());
        String name=user.getUsername();
        String phone=user.getMobilePhoneNumber();
        if(name.equals(phone)){
            name=phone.substring(0,3)+"****"+phone.substring(7,11);
        }
        if(phone.length()==11){
            phone=phone.substring(0,3)+"****"+phone.substring(7,11);
        }
        numtv.setText(phone);
        TextView nametv=(TextView)findViewById(R.id.infoNickname);
        nametv.setText(name);
//        iconfile=new File(Environment.getExternalStorageDirectory()+"/userImage.jpg");
//        if(iconfile.exists()) {
//            Uri i = Uri.fromFile(iconfile);
//            try {
//                Bitmap b = BitmapFactory.decodeStream(getContentResolver().openInputStream(i));
//                icon.setImageBitmap(b);
//            } catch (FileNotFoundException ex) {
//                Log.e("获取头像失败", ex.getMessage());
//            }
//        }else {

            if (user.getIcon() != null) {
                iconfile=new File(Environment.getExternalStorageDirectory()+"/userImage.jpg");
                if(iconfile.exists()) {
                    iconfile.delete();
                    Log.e("联网获取图片", "vvvvvvvvvvv");
                }
                BmobFile iconn = user.getIcon();
                iconn.download(new File(Environment.getExternalStorageDirectory() + "/userImage.jpg")
                        , new DownloadFileListener() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    icon.setImageBitmap(BitmapFactory.decodeFile(s));   //根据地址解码并显示图片
                                } else {
                                    Log.e("获取头像失败", e.getMessage());
                                }
                            }
                            @Override
                            public void onProgress(Integer integer, long l) {

                            }
                        });
            }


        numtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InfoActivity.this,"账号无法进行修改",Toast.LENGTH_LONG).show();
            }
        });
        RelativeLayout layout=(RelativeLayout)findViewById(R.id.modifyNickname);
        TextView infonickname=(TextView)findViewById(R.id.infoNickname);
        final String nickname=infonickname.getText().toString();
        final EditText et=new EditText(InfoActivity.this);

        final AlertDialog dialog =new AlertDialog.Builder(InfoActivity.this).create();
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setView(et);
                dialog.setTitle("修改昵称");
                dialog.setMessage("请输入新的昵称\n(您需使用该名称登陆，请谨慎修改)");
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String text = et.getText().toString();
                        if (!("".equals(text))) {
                            LoadingUtil.Loading_show(InfoActivity.this);
                            handler = new Handler(){
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 111) {
                                        BmobUser bmobUser=new BmobUser();
                                        bmobUser.setUsername(text);
                                        MyUser user = BmobUser.getCurrentUser(MyUser.class);
                                        bmobUser.update(user.getObjectId(), new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if(e==null){
                                                    LoadingUtil.Loading_close();
                                                    Toast.makeText(InfoActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                                                    TextView infotv=findViewById(R.id.infoNickname);
                                                    infotv.setText(text);
                                                    namme=new StringBuffer(text);
                                                }else{
                                                    LoadingUtil.Loading_close();
                                                    Toast.makeText(InfoActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                                }
                                            }


                                        });
                                    }else if(msg.what==222){
                                        LoadingUtil.Loading_close();
                                        Toast.makeText(InfoActivity.this, "修改失败，用户名已存在", Toast.LENGTH_SHORT).show();
                                    }else if(msg.what==333){
                                        LoadingUtil.Loading_close();
                                        Toast.makeText(InfoActivity.this, "修改失败，连接失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            };

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        Looper.prepare();
                                        String url="http://192.168.1.2:8080/savename";
                                        OkHttpClient okHttpClient = new OkHttpClient();
                                        FormBody formBody = new FormBody.Builder().add("name", namme.toString()).add("newname",text).build();
                                        Request request = new Request.Builder()
                                                .url(url)
                                                .post(formBody)
                                                .build();
                                        Response response = okHttpClient.newCall(request).execute();
                                        String oo=response.body().string();
                                        if(oo.equals("true")){
                                            Message msg=new Message();
                                            msg.what=111;//通知UI线程Json解析完成
                                            handler.sendMessage(msg);
                                        }else {
                                            Message msg = new Message();
                                            msg.what = 222;  //通知UI线程Json解析完成
                                            handler.sendMessage(msg);
                                        }
                                        Looper.loop();
                                    } catch (Exception e) {

                                        Message msg=new Message();
                                        msg.what=333;//通知UI线程Json解析完成
                                        handler.sendMessage(msg);
                                        msg.obj=e.getMessage();
                                        Log.e("错误",e.getMessage());
                                    }
                                }
                            }).start();

                        }else{
                            Toast.makeText(InfoActivity.this,"昵称名为空，修改昵称未成功",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE,"取消",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }
        });
        RelativeLayout iconlayout=(RelativeLayout)findViewById(R.id.icon);
        iconlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        final String[] items3 = new String[]{"拍照获取图片",  "相册导入图片"};//创建item
                        AlertDialog alertDialog3 = new AlertDialog.Builder(InfoActivity.this)
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
            imageUri=FileProvider.getUriForFile(InfoActivity.this, "com.ycs.order.provider", output);
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
//                    try {
//                        Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
//                        icon.setImageBitmap(bit);
                    final BmobFile file;
                    if(Build.VERSION.SDK_INT<Build.VERSION_CODES.N){
                        file = new BmobFile(uri2File(imageUri));
                    }else{
                        file = new BmobFile(new File(getFilePathForN(InfoActivity.this,imageUri)));
                    }
                    file.upload(new UploadFileListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    MyUser myuser=new MyUser();
                                    myuser.setIcon(file);
                                    myuser.update(user.getObjectId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                BmobFile iconn = user.getIcon();
                                                iconn.download(new File(Environment.getExternalStorageDirectory() + "/userImage.jpg"), new DownloadFileListener() {
                                                    @Override
                                                    public void done(String s, BmobException e) {
                                                        if (e == null) {
                                                            icon.setImageBitmap(BitmapFactory.decodeFile(s));
                                                        } else {
                                                            Log.e("获取头像失败", e.getMessage());
                                                        }
                                                    }

                                                    @Override
                                                    public void onProgress(Integer integer, long l) {

                                                    }
                                                });
                                                Toast.makeText(InfoActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(InfoActivity.this, "更新失败1" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                Log.e("提示", e.getMessage());
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(InfoActivity.this, "更新失败2" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e("提示", e.getMessage());
                                }
                            }
                        });

//                    } catch (Exception ex) {
//                        Log.d("tag", ex.getMessage());
//                        Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
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
                        Uri uri = data.getData();
//                        Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));

                        final BmobFile file=new BmobFile(uri2File(uri));
                        file.upload(new UploadFileListener(){
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    user.setIcon(file);
                                    user.update(user.getObjectId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if(e==null){
                                                BmobFile iconn=user.getIcon();
                                                iconn.download(new File(Environment.getExternalStorageDirectory()+"/userImage.jpg"),new DownloadFileListener() {
                                                    @Override
                                                    public void done(String s, BmobException e) {
                                                        if(e == null){
                                                            icon.setImageBitmap(BitmapFactory.decodeFile(s));
                                                        }
                                                        else{
                                                            Log.e("获取头像失败",e.getMessage());
                                                        }
                                                    }

                                                    @Override
                                                    public void onProgress(Integer integer, long l) {

                                                    }
                                                });
                                                Toast.makeText(InfoActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(InfoActivity.this,"更新失败1"+e.getMessage(),Toast.LENGTH_SHORT).show();
                                                Log.e("提示",e.getMessage());
                                            }
                                        }
                                    });
                                }else {
                                    Toast.makeText(InfoActivity.this, "更新失败2"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e("提示",e.getMessage());
                                }
                            }
                        });

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
                Toast.makeText(InfoActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(InfoActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
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
            Intent intent =new Intent(InfoActivity.this,MainActivity.class);
            intent.putExtra("id", 2);
            startActivity(intent);
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

}


