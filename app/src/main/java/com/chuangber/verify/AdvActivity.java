package com.chuangber.verify;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chuangber.verify.bean.HistoryInfo;
import com.chuangber.verify.card.CarderStatusCallback;
import com.chuangber.verify.card.HsCardReader;
import com.chuangber.verify.ui.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jinyh on 2018/5/3.
 */

public class AdvActivity extends BaseActivity implements CarderStatusCallback {
    public static final int FIND_CARD = 0;
    public static final int CONNECTED = FIND_CARD + 1;
    public static final int SHOW_CARD = CONNECTED + 1;
    public static final int TIME_OUT = SHOW_CARD + 1;
    public static final int DETECT_FACE = TIME_OUT + 1;
    public static final int NO_FACE = DETECT_FACE + 1;
    public static final int FACE_CAMERA = NO_FACE + 1;
    public static final int CLEAR_NO_FACE = FACE_CAMERA + 1;
    public static final int SWIPE_AGAIN = CLEAR_NO_FACE +1;
    public static final int SHOW_HISTORY = SWIPE_AGAIN +1;
    public static final int NO_CARD_READER = SHOW_HISTORY +1 ;
    public static final int RECONNECT = NO_CARD_READER +1 ;
    public static final int DETACHED = RECONNECT + 1;
    public static final int QRCODE_DISMISS = DETACHED + 1;
    private HsCardReader hsCardReader;

    @Bind(R.id.iv_adv_b)
    ImageView imageViewadv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adv);
        ButterKnife.bind(this);
        hsCardReader = new HsCardReader(this,this);
    }

    @Override
    public void onDeviceExist() {

    }

    @Override
    public void onDeviceDis() {

    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onOpenFail() {

    }

    @Override
    public void onClose() {

    }

    @Override
    public void getPic(byte[] buffer) {

    }

    @Override
    public void onTimeOut() {

    }

    @Override
    public void onShow(HistoryInfo historyInfo) {

    }

    @Override
    public void clear() {

    }

    @Override
    public void onSwipeFail() {

    }

    @Override
    public void onSwiped() {

    }


    @Override
    public void sendMessage(int msg) {

    }

    private class AdvHandler extends Handler {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //clearCardInfo();
            }
        };
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case FIND_CARD:

                    //跳转到Main
                    break;
                case CONNECTED:

                    reConnetCardReader();
                    break;
                case TIME_OUT:
                    //身份证过期
                    showTimeOut();
                    break;
                case SWIPE_AGAIN:
                    swipeAgain();
                    break;
                case NO_CARD_READER:
                    showNoCardReader();
                    break;
                case DETACHED:
                    if (hsCardReader!= null)
                        hsCardReader.closeDevice();
                    showNoCardReader();
                    break;
            }
        }


        private void showNoCardReader(){
            //cardview.setVisibility(View.VISIBLE);
            //textInfo.setText("请插入读卡器");

        }

        private void reConnetCardReader(){
            //cardview.setVisibility(View.INVISIBLE);
            //textInfo.setText("");
        }


        private void swipeAgain() {
            //cardview.setVisibility(View.VISIBLE);
            //textInfo.setVisibility(View.VISIBLE);
            //textInfo.setText(R.string.swipe_card);
            //faceHandler.postDelayed(runnable, 3000);

        }

        private void showTimeOut(){
//            beep(ConStant.BEEP.TIME_OUT);
//            cardview.setVisibility(View.VISIBLE);
//            //textTimeOut.setVisibility(View.VISIBLE);
//            textTimeOut.setText(R.string.time_out);
//            textInfo.setText("");
//            imageResult.setImageDrawable(getResources().getDrawable(fail));
//            faceHandler.postDelayed(runnable, 3000);
        }


    }
}
