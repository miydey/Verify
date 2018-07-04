package com.chuangber.verify.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chuangber.verify.ui.EntryActivity;
import com.chuangber.verify.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by jinyh on 2017/10/30.
 */

public class NameFragment extends Fragment {

    private EditText editTextName;
    private TextView textViewSubmit;
    private TextView textViewCancel;
    private ImageView imageViewBack;
    EntryActivity activity ;
    EditText editTextID;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        editTextID = (EditText) getActivity().findViewById(R.id.et_id_number);
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromInputMethod(editTextID.getWindowToken(), 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_name,container,false);
        editTextName = (EditText) view.findViewById(R.id.edit_id_name);
        textViewSubmit = (TextView) view.findViewById(R.id.text_frag_submit);
        textViewCancel = (TextView) view.findViewById(R.id.text_frag_cancel);
        activity = (EntryActivity)getActivity();
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String editable = editTextName.getText().toString();
                String str = stringFilter(editable.toString());
                if(!editable.equals(str)){
                    editTextName.setText(str);
                    //设置新的光标所在位置
                    editTextName.setSelection(str.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        editTextName.addTextChangedListener(textWatcher);
        textViewSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextName.getText().toString().length()>1){
                    Intent intent = new Intent();
                    String id = editTextID.getText().toString().trim();
                    if (editTextID.getText().toString().length() == 18){
                        intent.putExtra("ID",id);
                        intent.putExtra("name", editTextName.getText().toString().trim());
                        getActivity().setResult(1,intent);
                        getActivity().finish();
                    }
                }else {
                    Toast.makeText(activity,"请输入正确的姓名",Toast.LENGTH_SHORT).show();
                }

            }
        });

        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.swipeLast();
            }
        });

        editTextName.setFocusable(true);

        return view;
    }

    /**
     * 过滤出汉字
     * @param str
     * @return
     * @throws PatternSyntaxException
     */
    public static String stringFilter(String str)throws PatternSyntaxException {

        //   String   regEx  =  "[^a-zA-Z0-9\u4E00-\u9FA5]";
        String   regEx  =  "[^\u4E00-\u9FA5]";
        Pattern   p   =   Pattern.compile(regEx);
        Matcher m   =   p.matcher(str);
        return   m.replaceAll("").trim();
    }


}
