package com.ycs.order.Util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;
import com.ycs.order.R;

public class LoadingUtil {
    private static Dialog dialog;
    public static void Loading_show(Context context) {
            if(dialog==null||!dialog.isShowing()){
                AVLoadingIndicatorView view = new AVLoadingIndicatorView(context);
                view.setIndicator("PacmanIndicator");
                view.setIndicatorColor(Color.parseColor("#94d09f"));
                LinearLayout ll = new LinearLayout(context);

                ll.setOrientation(LinearLayout.VERTICAL);
                ll.setBackgroundResource(R.drawable.ed_shape);
                ll.setGravity(Gravity.CENTER);
                ll.addView(view,new LinearLayout.LayoutParams(200,200));
                TextView tv = new TextView(context);
                tv.setTextColor(Color.parseColor("#444444"));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
                tv.setText("加载中,请稍后...");
                ll.addView(tv,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                dialog = new Dialog(context);
                Window window = dialog.getWindow();
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.alpha = 0.7f;
                window.setAttributes(lp);
//                dialog.setCancelable(false);
                dialog.setContentView(ll,new LinearLayout.LayoutParams(400,400));// 设置布局
                dialog.show();
            }


    }

    public static void Loading_close() {
        if(dialog!=null){dialog.cancel();}
    }
}

