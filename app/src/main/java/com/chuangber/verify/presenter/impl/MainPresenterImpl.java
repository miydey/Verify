package com.chuangber.verify.presenter.impl;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.chuangber.verify.application.ConStant;
import com.chuangber.verify.bean.CheckVerRst;
import com.chuangber.verify.bean.HotelInfo;
import com.chuangber.verify.bean.Record1Rst;
import com.chuangber.verify.bean.RecordEntity;
import com.chuangber.verify.bean.ThrRst;
import com.chuangber.verify.bean.VerifyRst;
import com.chuangber.verify.bean.WechatQrRst;
import com.chuangber.verify.camera.FaceInfoCallback;
import com.chuangber.verify.card.HsCardReader;
import com.chuangber.verify.db.HisDatabaseHelper;
import com.chuangber.verify.iView.IMainView;
import com.chuangber.verify.net.ApiBuild;
import com.chuangber.verify.net.ApiService;
import com.chuangber.verify.presenter.IMainPresenter;
import com.chuangber.verify.util.FaceSDK;
import com.chuangber.verify.util.FileUtil;
import com.chuangber.verify.util.HashUtil;
import com.chuangber.verify.util.IDUtil;
import com.chuangber.verify.util.ImgUtil;
import com.chuangber.verify.util.SharePreUtil;
import com.chuangber.verify.util.ToastUtil;
import com.huashi.otg.sdk.IDCardInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

import cn.face.sdk.FaceInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jinyh on 2017/12/18.
 */

public class MainPresenterImpl extends BasePresenterImpl<IMainView> implements IMainPresenter,FaceInfoCallback {
    private static final String TAG = MainPresenterImpl.class.getSimpleName();
    private Context context;
    private final static int WIDTH = 1280;
    private final static int HEIGHT = 720;
    FaceInfo[] faceInfos;
    int faceNum; //抓到的人脸数量
    private Bitmap bitmapShot;
    private Bitmap bmpCamera;
    private Bitmap bmpCut;
    private Bitmap bmpId;
    private FaceSDK faceSDK;
    HsCardReader hsCardReader;
    IDCardInfo idCardInfo;
    //时间
    Calendar calendar;

    HisDatabaseHelper databaseHelper;//数据库
    private int httpTimeOutCount = 0; //http请求次数

    public MainPresenterImpl(Context context,IMainView view){
        this.context =context;
        mView = view;
        attachView(mView);
    }

    @Override
    //获取小程序二维码显示在界面上
    public void getWXQr(String loginToken,final String hotelId) {
        ApiService service = ApiBuild.getService();
        Call <WechatQrRst> call = service.getWechatQr(loginToken,hotelId);
        call.enqueue(new Callback<WechatQrRst>() {
            @Override
            public void onResponse(Call<WechatQrRst> call, Response<WechatQrRst> response) {
                boolean success = response.isSuccessful();
                if (success){
                        WechatQrRst wechatQrRst = response.body();
                        if (wechatQrRst.success){
                            String aheadurl = wechatQrRst.getData().getAheadurl();
                            String machine_id = wechatQrRst.getWxMachine().getMachine_id();
                            String machine_token = wechatQrRst.getWxMachine().getMachine_token();
                            String urlWechat = aheadurl
                                    +"&machine_id="+machine_id
                                    +"&hotel_id="+hotelId
                                    +"&machine_token="+machine_token;
                            SharePreUtil.saveStringData(context,"urlWechat",urlWechat);
                            mView.showWechatQr(urlWechat);
                        }else {
                            mView.showToast("获取二维码失败");

                        }


                }else {
                    mView.showToast("获取二维码失败");
                    Log.e("imp","二维码获取失败");
                }
            }

            @Override
            public void onFailure(Call<WechatQrRst> call, Throwable t) {
                mView.showToast("网络连接失败");
            }
        });

    }

    public void getHotelThr(String machineId,String machineToken){
        ApiService service = ApiBuild.getService();
        Call <ThrRst> call = service.getHotelThr(machineId,machineToken);
        call.enqueue(new Callback<ThrRst>() {
            @Override
            public void onResponse(Call<ThrRst> call, Response<ThrRst> response) {
                boolean success = response.isSuccessful();
                if (success){
                    try {
                        ThrRst thrRst = response.body();
                        String thr = thrRst.getData().getThr();
                        SharePreUtil.saveStringData(context,"threshold",thr);
                        mView.changeThr(thr);
                    }catch (Exception e){
                        mView.showToast("获取阈值失败");
                    }

                }else {
                    mView.showToast("获取阈值失败");
                }

            }

            @Override
            public void onFailure(Call<ThrRst> call, Throwable t) {
                mView.showToast("网络连接失败");
            }
        });
    }

    //发送比对记录
    public void sendRecord2(final Map<String ,Object> map,final IDCardInfo idCardInfo){
        ApiService service = ApiBuild.getService();
        Call<Record1Rst> call = service.sendRecord1(map);

        call.enqueue(new Callback<Record1Rst>() {
            @Override
            public void onResponse(Call<Record1Rst> call, Response<Record1Rst> response) {
                Record1Rst record1Rst = response.body();
                if (response.isSuccessful()){
                    if (record1Rst.success){
                        //插入数据成功
                        databaseHelper = HisDatabaseHelper.getInstance(context);
                        databaseHelper.insertHsData(true,idCardInfo,Float.valueOf(map.get("percentage").toString())
                                ,ImgUtil.bitmapToByte(bmpCut,Bitmap.CompressFormat.JPEG,100),
                                ImgUtil.bitmapToByte(bmpId,Bitmap.CompressFormat.JPEG,100));
                        if (httpTimeOutCount > 0)
                            httpTimeOutCount = 0;
                    }
                }
            }

            @Override
            public void onFailure(Call<Record1Rst> call, Throwable t) {

                if (httpTimeOutCount < ConStant.HTTP_FAIL_MAX_COUNT){
                    httpTimeOutCount++;
                    //重发一次
                            sendRecord2(map,idCardInfo);
                } else {
                    httpTimeOutCount = 0;
                    databaseHelper = HisDatabaseHelper.getInstance(context);
                    databaseHelper.insertHsData(false,idCardInfo,Float.valueOf(map.get("percentage").toString())
                            ,ImgUtil.bitmapToByte(bmpCut,Bitmap.CompressFormat.JPEG,100),
                            ImgUtil.bitmapToByte(bmpId,Bitmap.CompressFormat.JPEG,100));
                }
            }
        });
    }

    public void initUpdate(){
        ApiService service = ApiBuild.getService();
        Call<CheckVerRst> call = service.checkVersion();
        call.enqueue(new Callback<CheckVerRst>() {
            @Override
            public void onResponse(Call<CheckVerRst> call, Response<CheckVerRst> response) {
                if (response.isSuccessful()){
                    CheckVerRst checkVerRst = response.body();
                    String download_url = checkVerRst.getVersion().getDownload_url();
                    String version_l = checkVerRst.getVersion().getL_version();
                    String version_local = getLocalVersion();
                    Log.e(TAG, version_local );
                    if (Float.valueOf(version_local) < Float.valueOf(version_l) ){
                        //开始弹出更新
                        mView.showVersion(version_l,download_url);
                    }else {
                        mView.showToast("当前版本已为最新版本");
                    }
                }else {
                    mView.showToast("检查更新失败");
                }

            }

            @Override
            public void onFailure(Call<CheckVerRst> call, Throwable t) {
                mView.showToast("网络连接失败");
            }
        });
    }


    /**
     * 为亿恩提供需要的信息
     * @param success
     * @param idCardInfo
     */
    private void saveGuestInfo(boolean success, IDCardInfo idCardInfo) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        String guestPath = path+"/guestInfo/";
        FileUtil.mkDir(guestPath);
        String fileName = guestPath+"guest.xml";
        if (success){
            FileUtil.initHsXml(idCardInfo,ConStant.SUCCESSFUL,fileName,SharePreUtil.getStringData(context,"xml_path","c:\\guest"));
        }else {
            FileUtil.initHsXml(idCardInfo,ConStant.FAILURE,fileName,SharePreUtil.getStringData(context,"xml_path","c:\\guest"));
        }

        ImgUtil.saveImage(context,bmpId,guestPath,"PHOTO.jpg");
        ImgUtil.saveImage(context,bmpCut,guestPath,"ZPHOTO.jpg");
        //强制刷新
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(fileName);
        intent.setData(Uri.fromFile(file));    // 需要更新的文件路径
        context.sendBroadcast(intent);
    }

    public Bitmap  takePicture(byte[] data) {
        YuvImage img = new YuvImage(data, ImageFormat.NV21
                , ConStant.CAMERA_SET.PREVIEW_WIDTH, ConStant.CAMERA_SET.PREVIEW_HEIGHT, null);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        //Matrix matrix = new Matrix();
        //matrix.postScale(-1,1);
        img.compressToJpeg(new Rect(0, 0, ConStant.CAMERA_SET.PREVIEW_WIDTH, ConStant.CAMERA_SET.PREVIEW_HEIGHT), 100, output);
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;

        Bitmap bmp = BitmapFactory.decodeByteArray(output.toByteArray(), 0, output.size(), opt);
        //Bitmap  bmpCamera = bmp.copy(Bitmap.Config.RGB_565,true);
        return bmp;
    }

    public void compareAndSave(byte[] dataFrame, Bitmap bmpId,
                               float threshold, IDCardInfo idCardInfo,
                               HotelInfo hotelInfo){
        int cutX = 0;
        int cutY = 0;
        int cutHeight = 0;
        int cutWidth = 0;
        bmpCamera = takePicture(dataFrame);
        try {
            FaceInfo face =   faceSDK.getFeatureFromSurface(dataFrame,WIDTH,HEIGHT);
            if (face==null){
                mView.getFeatureFail();
                return;
            }else {
                cutX = face.x;
                cutY = face.y;
                cutHeight = face.height;
                cutWidth = face.width;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        //开始比对
        try {
            if (faceSDK.getFeatureFromCard(bmpId)==0)
            {
                this.bmpId = bmpId;
                VerifyRst rst =  faceSDK.compare(threshold);
                if (bmpCamera != null){
                    String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wltlib";// 读卡器授权目录
                    try {
                        if (cutWidth > 0 && cutX > 0){
                            if (cutY + cutHeight < HEIGHT ){
                                if (cutY - cutHeight*2/5 > 0 && (cutY+ cutHeight*6/5 < HEIGHT ) && (cutWidth*6/5 < WIDTH ) && (cutX - cutWidth/5 > 0))
                                {
                                    bmpCut = Bitmap.createBitmap(bmpCamera,cutX-cutWidth/5,cutY-cutHeight/5,cutWidth*6/5,cutWidth*7/5);
//
                                    //ImgUtil.saveJPGE_After(bmpCut,filepath,"live.bmp",100);
                                }
                                else if(cutX > 20 && cutY > 30){
                                    bmpCut = Bitmap.createBitmap(bmpCamera,cutX-20,cutY-25,cutWidth+40,cutHeight+40);
                                    //ImgUtil.saveJPGE_After(bmpCut,filepath,"live.bmp",100);
                                    //bmpCut = ImgUtil.getSmallBitmap(filepath+"/live.bmp");
                                    //ImgUtil.saveJPGE_After(bmpCut,filepath,"scale.bmp",100);

                                } else  {
                                    bmpCut = Bitmap.createBitmap(bmpCamera,cutX,cutY,cutWidth,cutHeight);
                                    //ImgUtil.saveJPGE_After(bmpCut,filepath,"live.bmp",100);
                                    //bmpCut = ImgUtil.getSmallBitmap(filepath+"/live.bmp");
                                }
                            }
                        } else {
                            bmpCut = bmpCamera.copy(Bitmap.Config.RGB_565,true);
                        }
                        mView.showResult(rst,bmpId,bmpCut);
                        saveRecord(rst,idCardInfo,hotelInfo);
                    }catch (Exception e){
                        ToastUtil.showToast(context,"请正视屏幕");
                    }


                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //保存并发送
    private void saveRecord(VerifyRst rst,IDCardInfo idCardInfo,HotelInfo hotelInfo) {
        String gender = IDUtil.getGenderByIdCard(idCardInfo.getIDCard());
        String genderToInt;
        if (gender.equals("女")){
            genderToInt = "0";
        }else{
            genderToInt = "1";
        }
        RecordEntity recordEntity = new RecordEntity();
        Map<String, Object> params = null ;
        recordEntity.setCard_addr(idCardInfo.getAddr());
        recordEntity.setCard_number(idCardInfo.getIDCard());
        recordEntity.setHotel_id(Integer.parseInt(hotelInfo.getHotelID()));
        recordEntity.setMachine_id(hotelInfo.getMachineID());
        recordEntity.setMachine_token(hotelInfo.getMachineToken());
        recordEntity.setPercentage((int) (rst.score*100));
        recordEntity.setName(idCardInfo.getPeopleName());
        recordEntity.setSex(genderToInt);
        recordEntity.setPic_camera(ImgUtil.bitmapToBase64(bmpCut,true));
        recordEntity.setPic_id(ImgUtil.bitmapToBase64(bmpId,true));
        if (rst.success){
                saveGuestInfo(true,idCardInfo); //保存xml信息给旅业系统
                recordEntity.setCheck_success(1);
                try {
                    params = HashUtil.convertToMap(recordEntity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sendRecord2(params,idCardInfo);

        }else {
            recordEntity.setCheck_success(0);
            try {
                params = HashUtil.convertToMap(recordEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            sendRecord2(params,idCardInfo);
        }
    }


    @Override
    public void initHandle() {
        faceSDK = FaceSDK.getInstance();
        faceSDK.createHandles();
        calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR,5);
    }

    @Override
    public void initHotelData() {

    }

    @Override
    public void detachView() {
        super.detachView();
        faceSDK.destroyHandles();
    }

    @Override
    public void detectFaceInfo(FaceInfo[] faceInfos, int faceNum) {
        if (faceNum > 0) {
            this.faceInfos = faceInfos;
            this.faceNum = faceNum;
            mView.showRect(faceInfos,faceNum);
        }else {
            mView.clearRect();
        }
    }

    //获取当前版本
    public String getLocalVersion() {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return  info.versionName;
    }

}
