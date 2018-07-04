package com.chuangber.verify.card;


import com.chuangber.verify.bean.HistoryInfo;

/**
 * Created by jinyh on 2017/4/16.
 */

public interface CarderStatusCallback {
    void onDeviceExist();//存在设备
    void onDeviceDis();//设备断开
    void onConnected();//设备连接成功
    void onOpenFail();//设备存在，但连接设备失败
    void onClose();
    void getPic(byte[] buffer);
    void onTimeOut();
    void onShow(HistoryInfo historyInfo);
    void clear();
    void onSwipeFail();//刷卡或者解码失败、
    void onSwiped();
    void sendMessage(int msg);
}
