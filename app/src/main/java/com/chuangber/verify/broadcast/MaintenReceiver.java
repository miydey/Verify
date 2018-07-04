package com.chuangber.verify.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.chuangber.verify.R;

/**
 * Created by jinyh on 2017/12/21.
 * 后端维护的时候，前端向用户发出提示，没用上
 */

public class MaintenReceiver extends BroadcastReceiver {
    Context context;


    public MaintenReceiver(Context context){
        this.context = context;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = LayoutInflater.from(context).inflate(R.layout.dialog_maintenance,null);
        builder.setView(layout);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        dialog.show();
    }
}
