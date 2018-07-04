package com.chuangber.verify.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.chuangber.verify.R;
import com.chuangber.verify.util.KeyboardUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by jinyh on 2017/10/30.
 */

public class IDFragment extends Fragment {
    private static String TAG = IDFragment.class.getSimpleName();
    private EditText editTextId;
    private Context context;
    private Activity activity;
    public KeyboardView keyboardView;
    private ImageView imageViewBack;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_id,container,false);
        editTextId = (EditText) view.findViewById(R.id.et_id_number);
        keyboardView = (KeyboardView) view.findViewById(R.id.keyboardView);
        imageViewBack = (ImageView) view.findViewById(R.id.image_id_back);
        // textViewSubmitID = (TextView) view.findViewById(R.id.text_submit_id);
        activity = getActivity();
        context = getActivity();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            editTextId.setShowSoftInputOnFocus(false);
            activity.getWindow().setSoftInputMode(
                    //设置输入模式，窗体获得焦点，始终隐藏软键盘
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus = null;
            try {
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus",
                        boolean.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            //设置是否可访问，为true，表示禁止访问
                setShowSoftInputOnFocus.setAccessible(true);
            try {
                setShowSoftInputOnFocus.invoke(editTextId, false);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }else {
            editTextId.setInputType(InputType.TYPE_NULL);
        }

        //editTextId.setTextIsSelectable(true);
        //editTextId.setCursorVisible(true);

        if (activity!= null)
        new KeyboardUtil(activity, context, editTextId,keyboardView,false).showNumber();
        SharedPreferences sharedPreferences = activity
               .getSharedPreferences(getResources().getString(R.string.storage_key),MODE_PRIVATE);
        String machineToken = sharedPreferences.getString("machine_token","default");
        String machineId = sharedPreferences.getString("machine_id","default");
        //getPrice(machineId,machineToken);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.back_index)
                        .setMessage(R.string.no_save_info)
                        .setIcon(R.mipmap.message)
                        .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.finish();
                            }
                        }).setNegativeButton(R.string.cancel,null);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
                        alertDialog.show();

            }
        });
        return view;
    }


}
