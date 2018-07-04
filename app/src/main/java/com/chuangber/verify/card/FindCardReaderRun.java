package com.chuangber.verify.card;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

/**
 * Created by jinyh on 2017/5/21.
 *
 * 实时检测读卡器插拔情况
 */

public class FindCardReaderRun implements Runnable {

    private String TAG = FindCardReaderRun.class.getSimpleName();
    private CarderStatusCallback carderStatusCallback;
    private boolean connect = false;
    private Context context;
    private boolean isFinding = true;
    public FindCardReaderRun(Context context,CarderStatusCallback carderStatusCallback){
        this.context = context;
        this.carderStatusCallback = carderStatusCallback;

    }

    public void setFinding(boolean status){
        isFinding = status;
    }
    public boolean getReaderStatus(){
        return  connect;
    }

    @Override
    public void run() {
        while (isFinding){
            if (getCardReader()){
                if (!connect)
                {

                    carderStatusCallback.onDeviceExist();
                    connect = true;
                }
            }else{
                if (connect){
                    carderStatusCallback.onDeviceDis();

                    //mainActivity.sendMessage(MainActivity.DETACHED);
                    connect = false;
                } else {
                    carderStatusCallback.onDeviceDis();
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    private boolean getCardReader() {
        UsbManager usbManager = (UsbManager)context.getSystemService(Context.USB_SERVICE);
        for (UsbDevice device : usbManager.getDeviceList().values()) {
            if( device.getVendorId() == 1024 && device.getProductId() == 50010 ) {
                Log.d(TAG, "CardReader exist" );
                return true;
            }
        }
        return false;
    }
}
