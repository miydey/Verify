package com.chuangber.verify.task;

/**
 * Created by jinyh on 2018/3/5.
 */

public interface CountDownListener {
    void onFinish(boolean finish);
   void restartCount();
}
