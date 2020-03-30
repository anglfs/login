package com.example.xinglanqianbao.tool;

import android.os.CountDownTimer;
import android.widget.TextView;
/**
 * Created by 李福森 2020/03/23
 */
public class MyCountDownTimer extends CountDownTimer {
    public  TextView a;
    public TextView b;
    public MyCountDownTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    public void init(TextView a,TextView b){
         a =this.a;
         b =this.b;
    }
    //计时过程
    @Override
    public void onTick(long l) {
        //防止计时过程中重复点击
        a.setClickable(false);
        b.setText(l/1000+"秒");
    }

    //计时完毕的方法
    @Override
    public void onFinish() {
        //重新给Button设置文字
        b.setText("重新获取");
        //设置可点击
        a.setClickable(true);
    }
}