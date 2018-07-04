package com.chuangber.verify.view;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.chuangber.verify.MainActivity;

/**
 * Created by jinyh on 2017/11/1.
 */

public class CountDownTextView extends CountDownTimer{

    /**
     * 倒计时控件
     */
    public static final int TIME_COUNT = 180*1000;//倒计时总时间为180S
    private TextView textView;
    private Context context;
    public static String TAG = CountDownTextView.class.getSimpleName();

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public CountDownTextView(long millisInFuture, long countDownInterval, TextView textView,Context context) {
        super(millisInFuture, countDownInterval);
        this.context = context;
        this.textView = textView;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        textView.setEnabled(false);
        textView.setText(String.valueOf(millisUntilFinished/1000));
//        if (millisUntilFinished/1000==10){
//            MainActivity mainActivity = (MainActivity) context;
//            mainActivity.sendQueryAli();
//        }
    }

    @Override
    public void onFinish() {
        textView.setEnabled(true);
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.cancelPay();
        this.cancel();
        //textView.setText("取消");
    }


}
