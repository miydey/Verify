package com.chuangber.verify.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.chuangber.verify.application.ConStant;

/**
 * Created by jinyh on 2017/5/5.
 */

public class BootBroadCastReceiver extends BroadcastReceiver {
    private static final String TAG = BootBroadCastReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent_boot = context.getPackageManager().getLaunchIntentForPackage(ConStant.PACKAGE_NAME);
        context.startActivity(intent_boot);
    }
}
