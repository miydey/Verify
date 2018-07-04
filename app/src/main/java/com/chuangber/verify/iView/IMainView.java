package com.chuangber.verify.iView;

import android.graphics.Bitmap;

import com.chuangber.verify.bean.VerifyRst;

import cn.face.sdk.FaceInfo;

/**
 * Created by jinyh on 2018/12/15.
 */

public interface IMainView {
    void showToast(String string);
    void showResult(VerifyRst verifyRst, Bitmap id, Bitmap cut);
    /*
     * 展示人脸框
     * 最多两个人脸
     */
    void showRect(FaceInfo[] faceInfos, int faceNum);

    void clearRect();//清除人脸框
    void getFeatureFail();//特征提取失败
    void showWechatQr(String wechatQrcode);//展示二维码
    void changeThr(String threshold);//修改阈值
    void showVersion(String versionName,String urlDownload);//版本更新展示
    void recordRst(boolean result);//发送有证结果

}
