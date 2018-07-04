package com.chuangber.verify.task;

import android.os.CountDownTimer;

/**
 * Created by jinyh on 2018/6/12.
 */

public class MyCountDown extends CountDownTimer {

    boolean isCount;//是否正在计数
    public static final int TIME_COUNT = 5 *1000;//倒计时总时间为180S
    CountDownListener countDownListener;


    public boolean getStatus(){
        return  isCount;
    }
    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public MyCountDown(long millisInFuture, long countDownInterval,CountDownListener countDownListener) {
        super(millisInFuture, countDownInterval);
        this.countDownListener = countDownListener;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        isCount = true;
    }

    @Override
    public void onFinish() {
        //清空数据
        this.cancel();
        countDownListener.onFinish(true);
        isCount = false;
    }
}
