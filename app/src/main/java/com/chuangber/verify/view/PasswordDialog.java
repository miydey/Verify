package com.chuangber.verify.view;

import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chuangber.verify.HistoryActivity;
import com.chuangber.verify.MainActivity;
import com.chuangber.verify.R;
import com.chuangber.verify.util.KeyboardUtil;

/**
 * Created by jinyh on 2017/12/6.
 */

public class PasswordDialog extends AlertDialog {
    private Context context;
    private EditText editTextHisPassword;
    private TextView textViewSub;
    private TextView textViewCancel;
    private TextView textViewTitle;
    private boolean firstOpen;
    private String password;
    private MainActivity mainActivity;
    public KeyboardView keyboardView;
    public PasswordDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        mainActivity = (MainActivity) context;
    }

    public void setFirstOpen(boolean firstOpen){
        this.firstOpen = firstOpen;
    }

    public void setPassword(String password){
        this.password = password;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setView(mainActivity.getLayoutInflater().inflate(R.layout.dialog_password, null));
        setContentView(R.layout.dialog_password);
        setCancelable(true);
//        setOnShowListener(new OnShowListener() {
//            @Override
//            public void onShow(DialogInterface dialog) {
//                InputMethodManager inputMethodManager = (InputMethodManager) mainActivity
//                        .getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//            }
//        });

        getWindow().setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP);
        editTextHisPassword = (EditText) findViewById(R.id.et_password);
        textViewTitle = (TextView) findViewById(R.id.tv_password_title);
        textViewCancel = (TextView) findViewById(R.id.tv_login_back);
        textViewSub = (TextView) findViewById(R.id.tv_login_submit);
        keyboardView = (KeyboardView) findViewById(R.id.keyboard_password);
        new KeyboardUtil(mainActivity, context, editTextHisPassword,keyboardView,true).showNumber();
        if (firstOpen){
            textViewTitle.setText(R.string.set_password);
        }else
            textViewTitle.setText(R.string.input_password);
        textViewSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passwordGet = editTextHisPassword.getText().toString().trim();
                if (firstOpen){
                    if (!passwordGet.equals("")){
                        mainActivity.savePassword(passwordGet);
                        Toast.makeText(mainActivity,R.string.set_password_success,Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(mainActivity,HistoryActivity.class);
                        PasswordDialog.this.dismiss();
                        context.startActivity(intent);

                    } else{
                        Toast.makeText(mainActivity,R.string.password_not_empty,Toast.LENGTH_SHORT).show();
                    }
                }else {
                    if (passwordGet.equals(password)) {
                        Intent intent = new Intent(mainActivity, HistoryActivity.class);
                        PasswordDialog.this.dismiss();
                        mainActivity.startActivity(intent);

                    } else {
                        Toast.makeText(mainActivity, R.string.password_wrong, Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PasswordDialog.this.dismiss();
            }
        });

    }
}
