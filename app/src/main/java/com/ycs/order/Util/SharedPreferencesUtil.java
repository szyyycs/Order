package com.ycs.order.Util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil{
    private Context context;
    private SharedPreferences sp=null;
    private SharedPreferences.Editor edit=null;
    public SharedPreferencesUtil(Context context,SharedPreferences sp){
        this.context=context;
        this.sp=sp;
        edit=sp.edit();
    }
    public SharedPreferencesUtil(Context context , String filename) {
        this(context,context.getSharedPreferences(filename,Context.MODE_PRIVATE));
    }
    public void setValue(String key,String value){
        edit.putString(key,value);
        edit.commit();

    }
    public String getValue(String key,String defaltValue){
        return sp.getString(key,defaltValue);
    }
}
