package com.chuangber.verify.view;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chuangber.verify.MainActivity;
import com.chuangber.verify.R;

/**
 * Created by jinyh on 2017/12/6.
 * 确认采集的信息或者重拍照片
 */

public class InfoDialog extends AlertDialog {

   private ImageView photo ;
   private TextView back ;
   private TextView submit;
   private TextView textViewName ;
   private TextView textViewID ;
   private TextView reTake ;
   private String name;
   private String idNumber;
   private Bitmap bitmap;
    private LayoutInflater inflater;
    private Context context;
    MainActivity mainActivity;

    public InfoDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        inflater = LayoutInflater.from(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_check_info);
        getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        setCancelable(false);
        //WindowManager m =context.getWindowManager();
        //Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        // WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
        // p.height = (int) (d.getHeight() * 0.9); // 高度设置为屏幕的0.8
        //lp.height = 700;
        // lp.width = (int) (d.getWidth() * 0.7); // 宽度设置为屏幕的0.7
        //dialogWindow.setAttributes(lp);
         photo = (ImageView) findViewById(R.id.image_dialog);
         back = (TextView) findViewById(R.id.tv_photo_back);
         submit = (TextView)findViewById(R.id.tv_photo_submit);
         textViewName = (TextView) findViewById(R.id.dialog_info_name);
         textViewID = (TextView) findViewById(R.id.dialog_info_id);
         reTake = (TextView) findViewById(R.id.tv_image_re);
         textViewName.setText(name);
         textViewID.setText(idNumber);
         photo.setImageBitmap(bitmap);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bitmap = null;
            }
        });

         reTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               InfoDialog.this.dismiss();

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InfoDialog.this.dismiss();
                            mainActivity.cancelCheck();
                        }
                    });
        submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InfoDialog.this.dismiss();
                            mainActivity.showQrcode(true);

                        }
                    });
    }

    public void setData(String name, String idNumber , Bitmap photo){
        this.name = name;
        this.idNumber = idNumber;
        this.bitmap = photo;
    }
}
