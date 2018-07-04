package com.chuangber.verify.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.chuangber.verify.InputActivity;
import com.chuangber.verify.MainActivity;
import com.chuangber.verify.R;
import com.chuangber.verify.application.FaceApplication;
import com.chuangber.verify.view.CountDownTextView;
import com.uuzuche.lib_zxing.activity.CodeUtils;

/**
 * Created by jinyh on 2017/10/27.
 * 管理一些逻辑简单的dialog
 */

public class DialogUtil {
   static DialogUtil dialogUtil;

    private Context context;
    private DialogUtil(Context context){
        this.context = context;
    }
    private AlertDialog alipay;
    public static DialogUtil getInstance(Context context){
        if (dialogUtil == null)
        dialogUtil =  new DialogUtil(context);
        return dialogUtil;
    }


    public void dismissAlipay(){
        alipay.dismiss();
    }

    public AlertDialog createQrCodeDialog(String qrCode){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = LayoutInflater.from(context).inflate(R.layout.dialog_qrcode,null);
        final AlertDialog alertDialog = builder.create();
        Window dialogWindow = alertDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        MainActivity mainActivity = (MainActivity) context;
        WindowManager m = mainActivity.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = mainActivity.getWindow().getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.9);
        p.width = (int) (d.getWidth() * 0.65);
        dialogWindow.setAttributes(p);
        alertDialog.setCancelable(true);
        alertDialog.setView(layout);
        alertDialog.setTitle("请扫描下方二维码");
        ImageView imageView = (ImageView) layout.findViewById(R.id.image_qrcode);
        TextView textCount = (TextView) layout.findViewById(R.id.tv_count_down);
        textCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 alertDialog.dismiss();
                MainActivity activity = (MainActivity) context;
                 activity.cancelPay();
            }
        });
        CountDownTextView myCountDown = new CountDownTextView(5*1000,1000,textCount,mainActivity);
        myCountDown.start();

        Bitmap mBitmap = CodeUtils.createImage(qrCode, 400, 400,
                BitmapFactory.decodeResource(context.getResources(), R.mipmap.qrcode_cb));
//                            Bitmap mBitmap = CodeUtils.createImage("http://192.168.0.234/hhh.html", 400, 400,
//                                   null);
        imageView.setImageBitmap(mBitmap);
        alipay = alertDialog;
        return alertDialog;
    }

    public AlertDialog createWaitDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("等待支付中")
                .setIcon(R.mipmap.message)
                .setNegativeButton(R.string.submit,null);
        final AlertDialog alertDialog = builder.create();
        Window dialogWindow = alertDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL );
        alertDialog.setCancelable(true);
        return alertDialog;
    }

    public AlertDialog createInfoDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = LayoutInflater.from(context).inflate(R.layout.dialog_company_info,null);
        TextView textView = (TextView) layout.findViewById(R.id.tv_version_name);
        FaceApplication faceApplication = FaceApplication.getFaceApplication();
        String verionName = faceApplication.getLocalVersion();
        textView.setText("当前版本号："+verionName);
        builder.setView(layout)
                .setNegativeButton(R.string.submit,null);
        final AlertDialog alertDialog = builder.create();
        Window dialogWindow = alertDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        alertDialog.setCancelable(true);
        return alertDialog;
    }
    public AlertDialog createSocketFailDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("网络请求错误")
                .setCancelable(false)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        return builder.create();
    }

    public AlertDialog createMessageDialog(final String IDNumber, final String IDName){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            final InputActivity inputActivity = (InputActivity) context;
            builder.setTitle("是否提交身份证信息")
                    .setMessage("点击下一步，进行人脸采集，请将脸部正对屏幕中央")
                    .setIcon(R.mipmap.message)
                    .setNegativeButton("取消",null)
                    .setPositiveButton("下一步", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent mIntent = new Intent();
                            mIntent.putExtra("ID",IDNumber);
                            mIntent.putExtra("name",IDName);
                            // 设置结果，并进行传送
                            inputActivity.setResult(1, mIntent);
                            inputActivity.finish();
                        }
                    });
        return builder.create();
    }


}
