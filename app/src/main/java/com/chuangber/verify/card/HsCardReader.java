package com.chuangber.verify.card;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.chuangber.verify.MainActivity;
import com.chuangber.verify.bean.HistoryInfo;
import com.chuangber.verify.util.ToastUtil;
import com.huashi.otg.sdk.HSInterface;
import com.huashi.otg.sdk.HsOtgService;
import com.huashi.otg.sdk.IDCardInfo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jinyh on 2017/4/8.
 * 华视读卡器
 */

public class HsCardReader implements FaceExistCallback {
    private static final String TAG = HsCardReader.class.getSimpleName();
    private CardConnection conn;
    private HSInterface hSinterface;
    private IDCardInfo IDInfoHs; //华视的信息
    private Intent service;
    private String filepath = "";
    CarderStatusCallback carderStatusCallback;
    Context context;
    private int haveID ;  //读卡器读到照片
    private int waitTime; //等待的秒数
    private long firstScan;
    private boolean connect;
    Thread findDeviceThread;
    FindCardReaderRun findCardReaderRun;
    Thread findCardThread;
    boolean isReading;
    boolean faceExist;
    public HsCardReader(CarderStatusCallback carderStatusCallback, Context context){
        this.carderStatusCallback = carderStatusCallback;
        this.context = context;
        findDevice();//开始寻找设备
    }

    @Override
    public void getFaceNumber(boolean faceExist) {
        this.faceExist = faceExist;
    }

    /**
     * 读卡器服务连接
     */
    private class CardConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            hSinterface = (HSInterface) service;
            if (hSinterface == null){
                carderStatusCallback.onOpenFail();
                Log.e(TAG, "onServiceConnected: bind fail " );
            }
            int i = 5;
            while (i > 0) {
                i--;
                int ret = hSinterface.init();
                Log.e(TAG, "HSinterface init: "+ret );
                if (ret == 1) {
                    i = 0;
                   // String SAM_ID =  hSinterface.GetSAM();
                    connect = true;
                    isReading = true;
                    findCard();
                    carderStatusCallback.onConnected();
                    return;
                } else {
                    try {
                        Thread.sleep(400);
                        i++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            conn = null;
            hSinterface = null;
        }

    }

    /**
     * 开启读卡器
     */
    public void openDevice(){
        filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wltlib";// 读卡器授权目录
        service = new Intent(context, HsOtgService.class);
        conn = new CardConnection();
        context.bindService(service, conn, Service.BIND_AUTO_CREATE);
    }


    /**
     * 关闭读卡器
     */
    public void closeDevice(){
        if (connect){
            if (hSinterface!= null){
                isReading = false;
                hSinterface.unInit();
                context.stopService(service);
                context.unbindService(conn);
                conn = null;
                findCardReaderRun.setFinding(isReading);
                findCardThread.interrupt();
                findDeviceThread.interrupt();
                try {
                    findCardThread.join();
                    hSinterface = null;
                    findDeviceThread.join();
                } catch (InterruptedException e) {
                    Log.e(TAG, e.toString() );
                }

            }

        }
    }


    /**
     * 读卡
     */
    private void readCard(){
        if (hSinterface == null)
            return;
        long time = System.currentTimeMillis();
        int ret = hSinterface.ReadCard();
        if (ret == 1){
            IDInfoHs = HsOtgService.ic;
            SimpleDateFormat sdf = new SimpleDateFormat( "yyyy.MM.dd" );

            String endDate = IDInfoHs.getEndDate();
            try {
                Date date_end = sdf.parse(endDate);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.YEAR,5);
                if (date_end.getTime() - calendar.getTimeInMillis() < 0 &&(!endDate.substring(0,1).equals("长"))){
                    carderStatusCallback.onTimeOut();
                    haveID = 0;
                    return;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                ret = hSinterface.Unpack();// 照片解码
                if (ret != 0) {// 读卡失败
                    Log.e(TAG, "readCard: unpack fail" );
                    return;
                }
                FileInputStream fis = new FileInputStream(filepath + "/zp.bmp");
                Bitmap bmpId = BitmapFactory.decodeStream(fis);
                fis.close();
                haveID = 1;
                while (haveID == 1){
                    if (faceExist){
                        HistoryInfo historyInfo = new HistoryInfo();
                        historyInfo.setBitmap(bmpId);
                        historyInfo.setIdCardInfo(IDInfoHs);
                        carderStatusCallback.onShow(historyInfo);
                        waitTime = 0;
                        haveID = 0;
                    }else {
                        carderStatusCallback.sendMessage(MainActivity.FACE_CAMERA);
                        while (!faceExist){  //无人脸的时候等待
                            waitTime++;
                            Thread.sleep(600);
                            if (waitTime >= 10){
                                haveID = 0;
                                carderStatusCallback.clear();
                                waitTime = 0;
                                break;
                            }
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                Toast.makeText(context, "头像不存在！", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(context, "头像读取错误", Toast.LENGTH_SHORT).show();
            }catch (Exception e)
            {
                Toast.makeText(context, "头像解码失败", Toast.LENGTH_SHORT).show();
            }finally {
                HsOtgService.ic = null;
            }
        }else {
            Log.e("readCard fail", String.valueOf(System.currentTimeMillis()-time) );
            carderStatusCallback.onSwipeFail();
        }
    }


    /**
     *寻找设备
     */
    private void findDevice(){
        findCardReaderRun = new FindCardReaderRun(context,carderStatusCallback);
        findDeviceThread = new Thread(findCardReaderRun);
        findDeviceThread.start();
    }

    /**
     * 寻卡
     */
    private void findCard(){

        //开启读卡线程，轮巡扫卡
        findCardThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isReading){
                    if (hSinterface == null)
                        return;
                        int ret =0;
                        try {
                           ret = hSinterface.Authenticate();
                        }catch (Exception e){

                            ToastUtil.showToast(context,"读卡器连接失败");
                        }


                    if (ret == 1){
                        if (System.currentTimeMillis() - firstScan > 1500){ //防抖动
                            carderStatusCallback.onSwiped();

                            readCard();
                            firstScan = System.currentTimeMillis();
                        } else{
                            firstScan = System.currentTimeMillis();
                        }

                    }else if (ret == 2){
                        Log.d(TAG, " no IDCard" );
                    }else if (ret == 0){
                        Log.d(TAG, "no reader connect" );
                    }
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        findCardThread.start();

    }
}
