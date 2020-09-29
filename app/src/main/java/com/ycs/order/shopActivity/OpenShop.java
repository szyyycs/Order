package com.ycs.order.shopActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ycs.order.R;
import com.ycs.order.Util.LoadingUtil;
import com.ycs.order.model.MyUser;
import com.ycs.order.model.Store;

import java.io.File;
import java.io.FileNotFoundException;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class OpenShop extends AppCompatActivity {
    private static final int CROP_PHOTO = 2;
    private static final int REQUEST_CODE_PICK_IMAGE=3;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 6;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 7;
    private  File output;
    private Uri imageUri;
    private Uri uri;
    private ImageView icon;
    private RelativeLayout layouticon;
    private MyUser user;
    private BmobFile file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_shop);
        getSupportActionBar().hide();
        ImageView fanhui=(ImageView)findViewById(R.id.fanhui);
        icon =findViewById(R.id.icon);
        layouticon=findViewById(R.id.layouticon);
        user=BmobUser.getCurrentUser(MyUser.class);
        layouticon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String[] items3 = new String[]{"拍照",  "相册"};//创建item
                        AlertDialog alertDialog3 = new AlertDialog.Builder(OpenShop.this)
                                .setTitle("选择导入图片的方式")
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

        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(OpenShop.this, MainBusinessActivity.class);
                startActivity(intent);
                finish();
            }
        });
        final EditText address=findViewById(R.id.shopAddress);
        final EditText shopname=findViewById(R.id.shopname);
        TextView regishop=findViewById(R.id.registershop);
        regishop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Store s = new Store();
                if (shopname.getText().toString().equals("")) {
                    Toast.makeText(OpenShop.this, "店名为空", Toast.LENGTH_SHORT).show();
                } else if (address.getText().toString().equals("")) {
                    Toast.makeText(OpenShop.this, "地址为空", Toast.LENGTH_SHORT).show();
                } else if (uri == null && imageUri == null) {
                    Toast.makeText(OpenShop.this, "店铺头像为空", Toast.LENGTH_SHORT).show();
                } else {
                    LoadingUtil.Loading_show(OpenShop.this);
                    s.setS_name(shopname.getText().toString());
                    s.setShop_address(address.getText().toString());
                    s.setOwnerId(user.getObjectId());
                    s.save(new SaveListener<String>() {
                        @Override
                        public void done(String id, BmobException e) {
                            if (e == null) {
                                MyUser userr=new MyUser();
                                userr.setBusinessid(id);
                                userr.update(user.getObjectId(), new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            if (uri != null) {
                                                file = new BmobFile(uri2File(uri));
                                                upload(file);


                                            } else if (imageUri != null) {
                                                file = new BmobFile(uri2File(imageUri));
                                                upload(file);


                                            }
                                        } else {
                                            LoadingUtil.Loading_close();
                                            Toast.makeText(OpenShop.this, "创建店铺失败" + e.getMessage(), Toast.LENGTH_SHORT);
                                        }
                                    }
                                });

                            } else {
                                LoadingUtil.Loading_close();
                                Toast.makeText(OpenShop.this, "创建店铺失败" + e.getMessage(), Toast.LENGTH_SHORT);
                            }
                        }
                    });


                }
            }
        });


    }



    public void takePhone(){
        if (ContextCompat.checkSelfPermission(getApplication(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);

        }else {
            takePhoto();
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
        imageUri = Uri.fromFile(output);
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
            case CROP_PHOTO:
                if (res==RESULT_OK) {
                    final BmobFile file = new BmobFile(uri2File(imageUri));

                    Bitmap bit = null;
                    try {
                        bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    icon.setImageBitmap(bit);
                }
                else{
                    Log.i("tag", "失败");
                }

                break;
            /**
             * 从相册中选取图片的请求标志
             */

            case REQUEST_CODE_PICK_IMAGE:
                if (res == RESULT_OK) {
                    uri = data.getData();
                    try {
                        Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        TextView tishi=findViewById(R.id.tishi);
                        tishi.setText("已选择照片");
                        icon.setImageBitmap(bit);

                    } catch (Exception ex) {

                        Log.d("tag", ex.getMessage());
                        Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
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
                // Permission Denied
                Toast.makeText(OpenShop.this, "Permission Denied", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(OpenShop.this, "Permission Denied", Toast.LENGTH_SHORT).show();
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
    public void upload(final BmobFile file){

        file.upload(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    MyUser userr=new MyUser();
                    userr.setIcon(file);
                    userr.update(user.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(OpenShop.this, "创建店铺成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(OpenShop.this, MainBusinessActivity.class)
                                               .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                LoadingUtil.Loading_close();
                            } else {
                                LoadingUtil.Loading_close();
                                Toast.makeText(OpenShop.this, "更新失败1" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("提示", e.getMessage());
                            }
                        }
                    });
                } else {
                    Toast.makeText(OpenShop.this, "更新失败2" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("提示", e.getMessage());
                }
            }
        });
    }

}


