package com.chuangber.verify.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chuangber.verify.application.ConStant;
import com.chuangber.verify.MainActivity;
import com.chuangber.verify.R;

/**
 * Created by jinyh on 2017/11/6.
 * 配置界面
 */

public class ConfigDialog extends AlertDialog {
    private static String TAG = ConfigDialog.class.getSimpleName();
    private LayoutInflater inflater;
    private Context context;
    MainActivity mainActivity;
    RadioGroup radioGroup ;
    RadioButton radioButtonHs ;
    RadioButton radioButtonHd ;
    RadioButton radioButtonAra;
    TextView textViewSubmit ;
    TextView textViewCancel ;
    EditText editTextXml;
    SeekBar seekBar ;
    TextView textView_threshold ;
    int type;
    float threshold;
    int selected ;
    private String xmlPath;
    private static float MIN_THRESHOLD = 0.6f; //最小阈值
    public ConfigDialog(@NonNull Context context) {
        super(context);
        inflater = LayoutInflater.from(context);
        mainActivity = (MainActivity) context;
    }

    public void setType(int type){
        this.type = type;
        selected = type;
    }

    public void setThreshold(float threshold){
        this.threshold = threshold;
    }

    public void setXmlPath(String xmlPath){
        this.xmlPath = xmlPath;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_reader);
         radioGroup = (RadioGroup) findViewById(R.id.rg_reader);
         radioButtonHs = (RadioButton) findViewById(R.id.rb_hs);
         radioButtonHd = (RadioButton) findViewById(R.id.rb_hd);
         textViewSubmit = (TextView) findViewById(R.id.tv_login_submit);
         textViewCancel = (TextView) findViewById(R.id.tv_login_back);

        seekBar = (SeekBar) findViewById(R.id.seek_the);
        textView_threshold = (TextView) findViewById(R.id.text_threshold);
        seekBar.setProgress((int) ((threshold - MIN_THRESHOLD) * 100));
        textView_threshold.setText(String.valueOf((int)(threshold * 100))+"%" );
        if (type == ConStant.CARD_READER.TYPE_HS){
            radioButtonHs.setChecked(true);
        }else {
            radioButtonHd.setChecked(true);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioButtonId = group.getCheckedRadioButtonId();
                if (radioButtonId == R.id.rb_hs){
                    selected = ConStant.CARD_READER.TYPE_HS;
                }else {
                    selected = ConStant.CARD_READER.TYPE_HD;
                }

            }
        });

        getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        textViewSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.saveThreshold(threshold);
                if (selected == type){
                    ConfigDialog.this.dismiss();
                }else {
                    ConfigDialog.this.dismiss();
                    mainActivity.saveCardReaderType(selected);
                }

            }
        });

        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigDialog.this.dismiss();
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView_threshold.setText((int) (progress + MIN_THRESHOLD * 100)+"%");
                threshold = (progress + MIN_THRESHOLD * 100)/100f;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

}
