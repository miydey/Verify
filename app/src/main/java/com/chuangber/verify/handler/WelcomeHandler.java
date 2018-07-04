package com.chuangber.verify.handler;

import android.os.Handler;
import android.os.Message;

import com.chuangber.verify.WelcomeActivity;
import com.chuangber.verify.util.ToastUtil;

import java.lang.ref.WeakReference;

/**
 * Created by jinyh on 2018/2/10.
 */

public class WelcomeHandler extends Handler {

    WeakReference<WelcomeActivity> welcomeActivityWeakReference = null;

    public WelcomeHandler(WelcomeActivity welcomeActivity){
        this.welcomeActivityWeakReference = new WeakReference<WelcomeActivity>(welcomeActivity);
    }


    @Override
    public void handleMessage(Message msg) {
        WelcomeActivity welcomeActivity = this.welcomeActivityWeakReference.get();
      switch (msg.what){
          case WelcomeActivity.FIRST_LOGIN:
              welcomeActivity.firstLogin();
              break;
          case WelcomeActivity.QUERY_MACHINE:
             // welcomeActivity.selectMachine();
              break;
          case WelcomeActivity.SET_MACHINE:

              break;
          case WelcomeActivity.HTTP_FAIL:
              ToastUtil.showToast(welcomeActivity,"请检查网络和输入的账号");
      }

    }
}
