package com.chuangber.verify;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**暂时用不到
 * Created by jinyh on 2017/9/21.
 */

public class InputActivity extends Activity {
    private long exitTime;
    private EditText editTextID;
    private EditText editTextName;
    private TextView textViewSubmit;
    private TextView textViewResult;
    private TextView textViewBack;
    private String IDNumber;
    private String IDName;
    private String IDGender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenOrientation();
        setContentView(R.layout.activity_input);
        textViewSubmit = (TextView) findViewById(R.id.text_submit);
        editTextID = (EditText) findViewById(R.id.edit_id);
        editTextName = (EditText) findViewById(R.id.edit_name);
        textViewResult = (TextView) findViewById(R.id.text_result);
        textViewBack = (TextView) findViewById(R.id.text_back);
        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputActivity.this.setResult(2);
                InputActivity.this.finish();
            }
        });


        textViewSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IDNumber = editTextID.getText().toString().trim();
                IDName = editTextName.getText().toString().trim();
                if (IDNumber.length()== 18 && IDName.length()>1){
                    AlertDialog.Builder builder = new AlertDialog.Builder(InputActivity.this);
                    builder.setTitle("是否提交身份证信息")
                            .setMessage("点击下一步，进行人脸采集，请将脸部正对屏幕中央")
                            .setIcon(R.mipmap.message)
                            .setNegativeButton("取消",null)
                            .setPositiveButton("下一步", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent mIntent = new Intent();
                                    String genderNumber = IDNumber.substring(16,17);
                                    if (Integer.valueOf(genderNumber)%2!= 0){
                                        IDGender = "男";//奇数
                                    }else {
                                        IDGender = "女";
                                    }

                                    mIntent.putExtra("gender",IDGender);
                                    mIntent.putExtra("ID",IDNumber);
                                    mIntent.putExtra("name",IDName);
                                    // 设置结果，并进行传送
                                    InputActivity.this.setResult(1, mIntent);
                                    InputActivity.this.finish();
                                }
                            }).show();
                } else if (IDNumber.length()<1){
                    Toast.makeText(InputActivity.this,"身份证号码不可为空", Toast.LENGTH_SHORT).show();
                }else if (IDName.length()<1){
                    Toast.makeText(InputActivity.this,"姓名不可为空", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(InputActivity.this,"请核对身份证号码是否正确", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (data!= null){
                String result = data.getStringExtra("result");
                if (result!=null){
                    textViewResult.setText(result);
                }

            }else {
               // textViewResult.setText("请重试");
            }

            textViewResult.setTextColor(Color.rgb(122,0,55));
        }

    }

    private void setScreenOrientation(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        if (width < height){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
}


