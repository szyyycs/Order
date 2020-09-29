package com.ycs.order.Util;

import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

public class Timedown extends CountDownTimer {
    private TextView bt;
    private TextView tv;
    public Timedown(long millisInFuture, long countDownInterval, TextView mButton,TextView tev ) {
        super(millisInFuture, countDownInterval);
        this.bt = mButton;
        this.tv = tev;
    }

    @Override
    public void onTick(long l) {
        bt.setClickable(false);
        bt.setText("已发送"+l / 1000 + "s");//60s显示内容
        //bt.setBackgroundResource(R.drawable.btn_countdown);//点击后按钮的颜色
    }

    @Override
    public void onFinish() {
        bt.setText("重新获取验证码");//60s后显示的文字
        //设置可点击
        bt.setClickable(true);
       // bt.setBackgroundResource(R.drawable.btn_shape);//60s过后按钮恢复的颜色
    }
}
