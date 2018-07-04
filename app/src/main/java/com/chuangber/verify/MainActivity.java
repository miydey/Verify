package com.chuangber.verify;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chuangber.verify.adapter.ViewPagerAdapter;
import com.chuangber.verify.application.ConStant;
import com.chuangber.verify.application.FaceApplication;
import com.chuangber.verify.bean.HistoryInfo;
import com.chuangber.verify.bean.HotelInfo;
import com.chuangber.verify.bean.IDCardInfoHd;
import com.chuangber.verify.bean.VerifyRst;
import com.chuangber.verify.broadcast.NetworkReceiver;
import com.chuangber.verify.broadcast.UpdateReceiver;
import com.chuangber.verify.camera.PreviewCamera;
import com.chuangber.verify.card.CarderStatusCallback;
import com.chuangber.verify.card.HsCardReader;
import com.chuangber.verify.db.HisDatabaseHelper;
import com.chuangber.verify.download.DownloadService;
import com.chuangber.verify.iView.IMainView;
import com.chuangber.verify.presenter.impl.MainPresenterImpl;
import com.chuangber.verify.task.CountDownListener;
import com.chuangber.verify.task.MyCountDown;
import com.chuangber.verify.ui.BaseActivity;
import com.chuangber.verify.util.DialogUtil;
import com.chuangber.verify.util.FileUtil;
import com.chuangber.verify.util.ImgUtil;
import com.chuangber.verify.util.SharePreUtil;
import com.chuangber.verify.util.ToastUtil;
import com.chuangber.verify.view.ConfigDialog;
import com.chuangber.verify.view.CountDownTextView;
import com.chuangber.verify.view.DrawFaceRect;
import com.chuangber.verify.view.PasswordDialog;
import com.hdos.idCardUartDevice.JniReturnData;
import com.hdos.idCardUartDevice.publicSecurityIDCardLib;
import com.huashi.otg.sdk.IDCardInfo;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.face.sdk.FaceInfo;
import hdx.HdxUtil;

import static com.chuangber.verify.R.drawable.fail;
import static com.chuangber.verify.R.mipmap.qrcode_cb;
import static com.chuangber.verify.R.mipmap.wechat2;

public class MainActivity extends BaseActivity implements View.OnClickListener,IMainView,CarderStatusCallback {

    private final static String TAG = MainActivity.class.getSimpleName();
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
    public static final int CHECK_SUCCESS = QRCODE_DISMISS + 1 ;
    public static final int CHECK_FAIL = CHECK_SUCCESS +1;
    public static final int SEND_SUCCESS = CHECK_FAIL +1;
    public static final int SEND_FAIL = SEND_SUCCESS +1;
    private static final int SCROLL = SEND_FAIL +1;
    //activity标志
    private static final int INPUT = 1;
    private static final int CHECK = 2; //后由web前端完成，这个界面暂时取消

    //dialog标志
    DialogUtil dialogUtil= DialogUtil.getInstance(this);
    private static final int SHOW_HISTORY_DIALOG = 1;     //历史
    private static final int SHOW_IMAGE_INFO_DIALOG = SHOW_HISTORY_DIALOG +1;//人脸采集后的确认
    private static final int SHOW_ALIPAY_QRCODE = SHOW_IMAGE_INFO_DIALOG +1; //付款二维码
    private static final int SHOW_WAIT_PAY = SHOW_ALIPAY_QRCODE + 1;
    private static final int COMPANY_INFO = SHOW_WAIT_PAY + 1;
    private static final int SHOW_SOCKET_FAIL = 9;
    FaceApplication faceApplication;
    private static final int RATE = 4; //缩放比率
    private long exitTime;
    private final static int WIDTH = ConStant.CAMERA_SET.PREVIEW_WIDTH;
    private final static int HEIGHT = ConStant.CAMERA_SET.PREVIEW_HEIGHT;
    //UI
    ObjectAnimator objectAnimator;
    @Bind(R.id.iv_offline)
    ImageView imageViewOffline;//离线闪烁提示
    @Bind(R.id.image_menu)
    ImageView textViewMenu;
    @Bind(R.id.progress)
    ProgressBar progressBar;
    @Bind(R.id.frame_main)
    FrameLayout frame;
    @Bind(R.id.surface_main)
    PreviewCamera surfaceView;
    private static DrawFaceRect faceRectFirst = null; //两个人脸框
    private static DrawFaceRect faceRectSecond = null;
    @Bind(R.id.iv_Id)
    ImageView imageId;

    private ImageView imageShot;
    @Bind(R.id.iv_result)
    ImageView imageResult;
    @Bind(R.id.image_logo)
    ImageView imageLogo;
    ImageView imageViewTakePicture;
    @Bind(R.id.ll_card_info)
    LinearLayout cardview;
    @Bind(R.id.tv_info)
    TextView textInfo;
    @Bind(R.id.tv_time_out)
    TextView textTimeOut;
    private TextView textNoCard;
    @Bind(R.id.iv_wechat_pro)
    ImageView imageViewWechatPro;
    @Bind(R.id.iv_adv)
    ImageView imageViewAdv;
    //private BackgroundView backgroundView;
    //UI 无证
    private LinearLayout linearLayoutNoCard;
    private TextView textViewNoCard;
    private ImageView imageViewNoCard;
    private ImageView imageViewResultNoCard;
    //设置界面
    PopupWindow popupWindow;
    View popupWindow_view;
    //广告轮播

    ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private int imageIds[];
    private int oldPosition = 0;//记录上一次点的位置
    private int currentItem; //当前页面
    private ArrayList arrayList = new ArrayList<ImageView>();

    //历史查询
    Calendar calendar;
    private boolean firstOpen = true;
    private int clickTime = 0;// 忘记密码
    private long firstClick;
    EditText editTextHisPassword;
    //识别
    int faceNum; //抓到的人脸数量
    private boolean noCard;
    Bitmap bitmapShot;
    private MainPresenterImpl iMainPresenter;
    private Bitmap bmpId;
    private Bitmap bmpCut;
    private int haveID ;//读卡器读到照片
    private int waitTime;//等待的秒数

    //读卡
    public  int TYPE_READER = ConStant.CARD_READER.TYPE_HS;//默认华视
    String fileName;
    // 华视
    private HsCardReader hsCardReader;
    private FaceHandler faceHandler;

    IDCardInfo idCardInfo; //华视的信息
    String filepath = "";
    // 华大，现在基本用不到
    String comPort ="/dev/ttyS1"; /*串口端口  tq210对应 /dev/s3c2410_serial0、/dev/s3c2410_serial1 /ttyMT2
														加利利串口0对应/dev/ttyS3*/
    //原先是ttyS1
    JniReturnData returnData;
    private publicSecurityIDCardLib iDCardDevice;
    private IDCardInfoHd idCardInfoHd;
    int noFaceCount = 0;

    //其他参数
    HisDatabaseHelper databaseHelper;
    SoundPool soundPool;
    float threshold = 0.62f;
    private NetworkReceiver networkReceiver;
    TimeReceiver timeReceiver;
    //调用寻程无证接口
    String verifyID;
    String verifyName;
    SQLiteDatabase hisData;
    private static final String COMPARE_KEY = "2d6e5b5c449d06afe1d2d0f7587aa043";
    private static final String URL_COMPARE = "http://v.apistore.cn/api/c132";
    //本机旅店信息
    private HotelInfo hotelInfo;
    public  AlertDialog aliPayAlertDialog;//二维码窗口
    ImageView imageViewQrCode;
    TextView textQuery; //主动查询按钮

    CountDownTextView myCountDown;
     MediaPlayer mMediaPlayer;
    //自动更新
    private DownloadService.DownloadBinder downloadBinder;

    private LocalBroadcastManager localBroadcastManager;
    private UpdateReceiver  updateReceiver;
    public static final String LOCAL_BROADCAST_DOWN = "com.chuangba.verify.service.LOCAL_UPDATE";
    public static final String LOCAL_BROADCAST_DAEMON = "com.chuangba.verify.service.LOCAL_DAEMON";


    CountDownListener countDownListener;
    MyCountDown myCountDown2;
    private ServiceConnection updateConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            downloadBinder = (DownloadService.DownloadBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void showToast(String string) {
        ToastUtil.showToast(this,string);
    }


    @Override
    public void showResult(VerifyRst rst, Bitmap id, Bitmap cut)  {
        if (rst.success){
            soundPool.play(1, 1, 1, 0, 0, 1);
            if (TYPE_READER == ConStant.CARD_READER.TYPE_HS){
                faceHandler.showCard(true, idCardInfo,id,cut);
            }else {
                //faceHandler.showCard(true,idCardInfoHd,id,cut);
//
            }
        }else {
            soundPool.play(2, 1, 1, 0, 0, 1);
            if (TYPE_READER == ConStant.CARD_READER.TYPE_HS){
                faceHandler.showCard(false, idCardInfo,id,cut);

            }else {
                //faceHandler.showCard(false,idCardInfoHd,id,cut);
//
            }
        }
    }
    @Override
    public void showRect(FaceInfo[] faceInfos, int faceNum) {
        if (faceNum == 1){
            faceRectFirst.setVisibility(View.VISIBLE);
            faceRectSecond.setVisibility(View.GONE);
            FaceInfo faceInfo = faceInfos[0];

            faceRectFirst.setPosition(faceInfo.x,faceInfo.y,
                    faceInfo.x + faceInfo.width,faceInfo.y+faceInfo.height);
        }
        if (faceNum == 2){
            faceRectFirst.setVisibility(View.VISIBLE);
            FaceInfo faceInfo1 = faceInfos[0];
            faceRectFirst.setPosition(faceInfo1.x,faceInfo1.y,
                    faceInfo1.x + faceInfo1.width,faceInfo1.y+faceInfo1.height);
            faceRectSecond.setVisibility(View.VISIBLE);
            FaceInfo faceInfo = faceInfos[1];
            faceRectSecond.setPosition(faceInfo.x,faceInfo.y,
                    faceInfo.x+faceInfo.width,faceInfo.y + faceInfo.height);
        }
    }

    @Override
    public void clearRect() {
        if (faceRectFirst.getVisibility()== View.VISIBLE) {
            faceRectFirst.setVisibility(View.GONE);
            faceRectSecond.setVisibility(View.GONE);
        }
    }

    @Override
    public void getFeatureFail() {
//提取特征失败
        faceHandler.lightLess();
    }

    @Override
    public void showWechatQr(String wechatQrcode) {
         Bitmap mBitmap = CodeUtils.createImage(wechatQrcode, 400, 400,
                BitmapFactory.decodeResource(getResources(), wechat2));
        imageViewWechatPro.setImageBitmap(mBitmap);
    }

    @Override
    public void changeThr(String threshold) {
        saveThreshold(Float.valueOf(threshold));
    }

    //版本更新界面
    @Override
    public void showVersion(String versionName, final String urlDownload) {
        updateReceiver.setVersion(versionName);
        //faceHandler.sendEmptyMessage(UPDATE_VERSION);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View layout = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_update,null);
        builder.setView(layout);

        final AlertDialog dialog =  builder.create();
        TextView textViewSubmit = (TextView) layout.findViewById(R.id.tv_update_submit);
        TextView textViewCancel = (TextView) layout.findViewById(R.id.tv_update_cancel);
        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        textViewSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (downloadBinder!= null){
                    downloadBinder.startDownload(urlDownload);
                    ToastUtil.showToast(MainActivity.this,"开始下载");
                }else {
                    Log.e(TAG, "onClick: ");
                }

                dialog.dismiss();
            }
        });

        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        dialog.show();
    }

    @Override
    public void recordRst(boolean result) {

    }

    @Override
    public void onDeviceExist() {
        sendMessage(RECONNECT);
    }

    @Override
    public void onDeviceDis() {
        sendMessage(DETACHED);
    }

    @Override
    public void onConnected() {

        this.sendMessage(CONNECTED);

    }

    @Override
    public void onOpenFail() {

    }

    @Override
    public void onClose() {

    }

    @Override
    public void getPic(byte[] buffer) {
        Log.e(TAG, "getPic: " );
        byte[] dataFrame = new byte[buffer.length];
        System.arraycopy(buffer,0,dataFrame,0,buffer.length);
        iMainPresenter.compareAndSave(dataFrame,bmpId,threshold, idCardInfo,hotelInfo);
    }

    @Override
    public void onTimeOut() {

    }

    @Override
    public void onShow(HistoryInfo historyInfo) {
        this.sendMessageWithData(SHOW_CARD,historyInfo);
    }

    @Override
    public void clear() {
        sendMessage(CLEAR_NO_FACE);
    }

    @Override
    public void onSwipeFail() {
        sendMessage(SWIPE_AGAIN);
    }

    @Override
    public void onSwiped() {
        sendMessage(FIND_CARD);

    }

    private  class TimeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
           String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK)){
              Calendar  calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
               if (hour == 23 && minute == 58){
                   //netClient.uploadLog(new File(fileName),hotelInfo.getMachineID(),hotelInfo.getMachineToken());
               }
            }

        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (System.currentTimeMillis() - exitTime > 2000) {
                    Toast.makeText(getApplicationContext(), R.string.click_again, Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    finish();
                    System.exit(0);
                }
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onStop() {
        surfaceView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if (requestCode == 1){
           surfaceView.stopGetFeature(false);
           if (data!= null && resultCode == 1){
               verifyID = data.getStringExtra("ID");
               verifyName = data.getStringExtra("name");
               //backgroundView.setVisibility(View.VISIBLE);
               imageViewTakePicture.setVisibility(View.VISIBLE);
              // setAnimator(verifyName, verifyID);

           }
       }
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog dialog = null;
        switch (id){
            case SHOW_HISTORY_DIALOG:
                //dialog = createHistoryDialog();
                break;
            case SHOW_ALIPAY_QRCODE:
                //支付宝的
                //dialog = createQrCodeDialog(trade);

               // dialog = createQrCodeDialog(weChatQrCode);
                aliPayAlertDialog = dialog;
                break;
            case SHOW_SOCKET_FAIL:
                dialog = dialogUtil.createSocketFailDialog();
                break;
            case SHOW_WAIT_PAY:
                dialog = dialogUtil.createWaitDialog();
                break;
            case COMPANY_INFO:
                dialog = dialogUtil.createInfoDialog();
                break;
        }
        return dialog;

    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id){
            case SHOW_HISTORY_DIALOG:
                editTextHisPassword.setText("");
                break;

            case SHOW_ALIPAY_QRCODE:
                Bitmap mBitmap = CodeUtils.createImage("xxxxxxxxxxxxxxxxxxxxxx", 400, 400,
                        BitmapFactory.decodeResource(getResources(), qrcode_cb));
//                            Bitmap mBitmap = CodeUtils.createImage("http://192.168.0.234/hhh.html", 400, 400,
//                                   null);
                imageViewQrCode.setImageBitmap(mBitmap);
                textQuery.setEnabled(true);
                myCountDown.start();
                break;
        }
        super.onPrepareDialog(id, dialog);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getWindow().setBackgroundDrawable(null);
       // NavigationBarStatusBar(this,true);
        initViews();
        initUpdate();
        faceApplication = FaceApplication.getFaceApplication();
        cardview.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.GONE);
        //backgroundView.setVisibility(View.GONE);

        iMainPresenter = new MainPresenterImpl(this,this);
        faceRectFirst = new DrawFaceRect(this, getResources().getColor(R.color.FaceColor));
        frame.addView(faceRectFirst, new ViewGroup.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        faceRectSecond = new DrawFaceRect(this, getResources().getColor(R.color.FaceColor));
        frame.addView(faceRectSecond, new ViewGroup.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        iMainPresenter.initHandle();
        initData();//初始化语音，时间,数据库

        surfaceView.setKeepScreenOn(true);
        surfaceView.setCardCallback(this);
        surfaceView.setCardType(TYPE_READER);

        initCardReader();
        surfaceView.setFaceInfoCallback(iMainPresenter);
        surfaceView.setFaceExistCallback(hsCardReader);

        FileUtil.writeStringToFile("---MainActivity start---",fileName);
        String loginToken = getIntent().getStringExtra("login_token");

        if (loginToken == null){
            String urlWechat = SharePreUtil.getStringData(this,"urlWechat","default");
            Bitmap mBitmap = CodeUtils.createImage(urlWechat, 400, 400,
                    BitmapFactory.decodeResource(getResources(), wechat2));
            imageViewWechatPro.setImageBitmap(mBitmap);
        }else {
            String hotelId = hotelInfo.getHotelID();
            iMainPresenter.getWXQr(loginToken,hotelId);
        }

        String urlWechat = SharePreUtil.getStringData(this,"urlWechat","default");
        if (urlWechat.equals("default")){
            String hotelId = hotelInfo.getHotelID();
            iMainPresenter.getWXQr(loginToken,hotelId);
        }
        iMainPresenter.getHotelThr(hotelInfo.getMachineID(),hotelInfo.getMachineToken());

        // }

        imageLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickTime ++;
                if (clickTime == 1){
                    firstClick = System.currentTimeMillis();
                }
                if (clickTime == 5){
                    long endClick =System.currentTimeMillis();
                    if (endClick - firstClick < 10000){
                        AlertDialog.Builder forget = new AlertDialog.Builder(MainActivity.this);
                        forget.setTitle("产品信息")
                                .setIcon(R.mipmap.message)
                                .setMessage("您的密码是 ："+SharePreUtil.getStringData(MainActivity.this,"password","111"))
                                .setNegativeButton("确定",null)
                                .setCancelable(false);
                        forget.create().show();
                        clickTime = 0;
                    } else {
                        firstClick = System.currentTimeMillis();
                        clickTime = 0;
                    }
                }

            }
        });

        countDownListener = new CountDownListener() {
            @Override
            public void onFinish(boolean finish) {
                faceHandler.clearCardInfo();
            }

            @Override
            public void restartCount() {
                myCountDown2.cancel();
                myCountDown2 = new MyCountDown(5000,1000,countDownListener);
                myCountDown.start();
            }
        };

        myCountDown2 = new MyCountDown(5000,1000,countDownListener);

        //textNoCard.setOnClickListener(this);
        textViewMenu.setOnClickListener(this);
        imageViewTakePicture.setOnClickListener(this);
    }


    public void setViewSize(int width,int height){
        faceRectFirst.setViewSize(width,height);
        faceRectSecond.setViewSize(width,height);
        //backgroundView.setSize(width,height);
    }


    /**
     * 取消验证
     */
    public void cancelCheck(){
        noCard = false;
        if (imageViewTakePicture.getVisibility() == View.VISIBLE)
        imageViewTakePicture.setVisibility(View.GONE);
        //backgroundView.setVisibility(View.GONE);

    }

    /**
     * 保存密码
     * @param passwordGet
     */
    public void savePassword(String passwordGet){

        SharePreUtil.saveStringData(this,"password",passwordGet);
        SharePreUtil.saveBooleanData(this,"firstOpen",false);
        firstOpen = false;
    }

    /**
     * 加载版本升级信息
     */
    private void initUpdate(){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, DownloadService.class);
        startService(intent);
        bindService(intent,updateConn,BIND_AUTO_CREATE);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter_local = new IntentFilter();
        intentFilter_local.addAction(LOCAL_BROADCAST_DOWN);
        updateReceiver = new UpdateReceiver(this);
        localBroadcastManager.registerReceiver(updateReceiver,intentFilter_local);

    }

    private void initViews() {
        progressBar = (ProgressBar) findViewById(R.id.progress);
        imageViewTakePicture = (ImageView) findViewById(R.id.iv_take_picture);
        //backgroundView = (BackgroundView) findViewById(R.id.backView);
        imageShot = (ImageView) findViewById(R.id.iv_live);
        textNoCard = (TextView) findViewById(R.id.tv_no_card);
        linearLayoutNoCard = (LinearLayout) findViewById(R.id.ll_no_card);
        imageViewNoCard = (ImageView) findViewById(R.id.image_no_card);
        textViewNoCard = (TextView) findViewById(R.id.tv_no_card_info);
        popupWindow_view = getLayoutInflater().inflate(R.layout.menu_main, null,false);
        // 创建PopupWindow实例,设置菜单宽度和高度为包裹其自身内容
        popupWindow = new PopupWindow(popupWindow_view, ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, true);

        //imageViewResultNoCard = (ImageView) findViewById(R.id.image_no_card_result);
       // initAdv();

    }

    public void sendMessage(int message){
        faceHandler.sendEmptyMessage(message);
    }

    public void sendMessageWithData(int message,Object obj){
        Message msg = faceHandler.obtainMessage();
        msg.what = message;
        msg.obj = obj;
        faceHandler.sendMessage(msg);
    }

    /**
     * 加载基本信息
     */
    private void initData() {
        //读卡
        filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wltlib";// 读卡器授权目录

        faceHandler = new FaceHandler();
        //数据库
        databaseHelper = HisDatabaseHelper.getInstance(this);

        firstOpen = SharePreUtil.getBooleanData(this,"firstOpen",true);
        //旅店信息
        hotelInfo = new HotelInfo();
        hotelInfo.setThreshold(threshold);
        hotelInfo.setHotelName(SharePreUtil.getStringData(this,"hotel_name","default"));
        hotelInfo.setMachineName(SharePreUtil.getStringData(this,"machine_name","default"));
        hotelInfo.setHotelID(SharePreUtil.getStringData(this,"hotel_id","default"));
        hotelInfo.setMachineID(SharePreUtil.getStringData(this,"machine_id","default"));
        hotelInfo.setMachineToken(SharePreUtil.getStringData(this,"machine_token","default"));
        //时间
        initSound();
        initTime();
        //读卡
        if (!faceApplication.getKeepAlive()){
            faceApplication.initWatchDog(this);
        }
    }

    /**
     * 加载语音播放
     */
    private void initSound() {

        soundPool = new SoundPool(4, AudioManager.RINGER_MODE_NORMAL, 10);
        soundPool.load(this, R.raw.sound_success, ConStant.BEEP.SUCCESS);
        soundPool.load(this, R.raw.sound_fail, ConStant.BEEP.FAIL);
        soundPool.load(this, R.raw.sound_timeout, ConStant.BEEP.TIME_OUT);
        //soundPool.load(this, R.raw.sound_found2, ConStant.BEEP.BEEP);//请正视摄像头
        soundPool.load(this, R.raw.sound_found, ConStant.BEEP.BEEP);//叮咚
    }

    //播放指定声音
    public void beep(int flag){
        soundPool.play(flag, 1, 1, 0, 0, 1);
    }

    /**
     * 断网动画显示
     *
     * @param offline
     */
    public void setOfflineAnimator(boolean offline){
         objectAnimator = ObjectAnimator.ofFloat(imageViewOffline,"alpha",1f,0f,1f);
        if (offline){
            imageViewOffline.setVisibility(View.VISIBLE);
            objectAnimator.setRepeatCount(Animation.INFINITE);
            objectAnimator.setRepeatMode(ValueAnimator.RESTART);
            objectAnimator.setDuration(2000).start();
        }else {
            objectAnimator.pause();
            imageViewOffline.setVisibility(View.INVISIBLE);
        }
    }

    public void shutdownWeb(){
        faceApplication.shutdownWatchDog();
    }

    public void reConnectWebSocket(){
        if (!faceApplication.getKeepAlive()){
            faceApplication.initWatchDog(this);
        }
    }

    /**
     * 根据读卡器类型，开启读卡线程
     **/
    private void initCardReader() {
        if (TYPE_READER == ConStant.CARD_READER.TYPE_HS){

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            String filepath = path+"/FaceLog/"+sdf.format(new Date())+"/";
            FileUtil.mkDir(filepath);
            fileName = filepath+"log.txt";
            hsCardReader = new HsCardReader(this,this);
            Log.e(TAG, "initCardReader: " );
        } else if (TYPE_READER == ConStant.CARD_READER.TYPE_HD){
            Log.e(TAG, "initData: ---华大读卡---" );
            //加载华大读卡器

        }
    }

    /**
     * 发送UDP，获取订单号，随后生成并展示二维码
     **/

    public void showQrcode( boolean pay) {
        noCard = false;
        if (imageViewTakePicture.getVisibility() == View.VISIBLE)
        imageViewTakePicture.setVisibility(View.GONE);
        if (pay){
            try {
                //sendWeChatTrade(hotelInfo.getMachineID());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        faceRectFirst.setVisibility(View.GONE);
        faceRectSecond.setVisibility(View.GONE);
        //backgroundView.setVisibility(View.INVISIBLE);
    }

    /**
     * 关闭二维码界面，取消支付
     **/
    public void cancelPay(){
        aliPayAlertDialog.dismiss();
        Log.e(TAG, "cancel pay" );
    }
    private void initTime() {
        calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR,5);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkReceiver = new NetworkReceiver();
        registerReceiver(networkReceiver,filter);


        IntentFilter filterTime = new IntentFilter();
        filterTime.addAction(Intent.ACTION_TIME_TICK);
        timeReceiver = new TimeReceiver();
        registerReceiver(timeReceiver, filterTime);
    }

    /**
     * 华大寻卡，因华大的寻卡不影响读卡结果，可不进行调用
     **/
    private void findCardHd(){
        byte[]cmdRequst= new byte[]{(byte) 0xAA,(byte) 0xAA,(byte) 0xAA,(byte) 0x96,0x69,0x00,0x03,0x20,0x01,0x22};
        returnData = iDCardDevice.idSamDataExchange(returnData, comPort,cmdRequst);
        Log.d(TAG, "findCard: "+returnData.result );
        if(returnData.result == 0x0f)
        {
            readCardHd();
        }
        else {
            Log.d(TAG, "find card fail");
        }
    }

    /**
     * 华大读卡， 串口
     **/
    public void readCardHd() {
        int retval;
        String pkName;
        pkName=this.getPackageName();
        pkName = this.getFilesDir().getPath()+pkName+"/lib/libwlt2bmp.so";
        Log.e(TAG, "readCardHd: "+pkName );
        //pkName="/data/data/"+pkName+"/lib/libwlt2bmp.so";
        try {
            byte[] name = new byte[32];
            byte[] sex = new byte[6];
            byte[] birth = new byte[18];
            byte[] nation = new byte[12];
            byte[] address = new byte[72];
            byte[] Department = new byte[32];
            byte[] IDNo = new byte[38];
            byte[] EffectDate = new byte[18];
            byte[] ExpireDate = new byte[18];
            byte[] pErrMsg = new byte[20];
            byte[] BmpFile = new byte[38556];
            retval = iDCardDevice.readBaseMsg(comPort,pkName,BmpFile, name, sex, nation, birth, address, IDNo, Department,
                    EffectDate, ExpireDate,pErrMsg);
            if (retval < 0) {
                Log.d(TAG, "readCardHd: don't exist" );
            } else {
                noCard = false;
                idCardInfoHd = new IDCardInfoHd();
                soundPool.play(4,1,1,0,0,1);
                int []colors = iDCardDevice.convertByteToColor(BmpFile);
                bmpId = Bitmap.createBitmap(colors, 102, 126, Bitmap.Config.ARGB_8888);
                // Bitmap bm1=Bitmap.createScaledBitmap(bm, (int)(102*1),(int)(126*1), false); //这里你可以自定义它的大小
                String peopleName = new String(name, "Unicode");
                String idCardNumber = new String(IDNo, "Unicode");
                String peopleSex = new String(sex,"Unicode");
                String endTime = new String(ExpireDate, "Unicode");
                String people = new String(nation,"Unicode");
                //String endTime = new String("        ");
                String add = new String(address, "Unicode");
                idCardInfoHd.setPeopleName(peopleName.trim());
                idCardInfoHd.setIDCard(idCardNumber.trim());
                idCardInfoHd.setSex(peopleSex.trim());
                idCardInfoHd.setAddr(add.trim());
                idCardInfoHd.setPeople(people.trim());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

                 /*
                 * 为了知道华大长期身份证的格式,实际应用中不需要记录这些信息
                 * 长期身份证是非UTF-8字符
                 */
                FileUtil.writeStringToFile(peopleName,fileName);

                if (endTime.substring(0,1).equals("\u007f")||endTime.substring(0,1).equals("\u001f")){ //如果是长期
                    Log.d(TAG, "card is long time" );
                }else {
                    try{
                        Date endDate = sdf.parse(endTime);
                        if ((endDate.getTime() < calendar.getTimeInMillis())){
                            faceHandler.sendEmptyMessage(TIME_OUT);
                            haveID = 0;
                            return;
                        }
                    }catch (ParseException e){
                        haveID = 1;
                        Log.e(TAG, "maybe long time" );
                    }
                }
                haveID = 1;

                while (haveID == 1){
                    Log.e(TAG, "readCardHd: "+faceNum );
                    if (faceNum > 0){

                        surfaceView.setCardExist(true);
                        //faceHandler.sendEmptyMessage(SHOW_CARD);
                        waitTime = 0;
                        haveID = 0;
                    }else {
                        faceHandler.sendEmptyMessage(FACE_CAMERA);
                        while (faceNum == 0){  //无人脸的时候等待
                            waitTime++;
                            Thread.sleep(500);
                            if (waitTime >= 10){
                                haveID = 0;
                                faceHandler.sendEmptyMessage(CLEAR_NO_FACE);
                                waitTime = 0;
                                break;
                            }
                        }
                    }
                }
                // showString("名字=" + new String(name, "Unicode"));
                // showString("性别=" + new String(sex, "Unicode"));
//                showString("民族=" + new String(nation, "Unicode"));
//                showString("生日=" + new String(birth, "Unicode"));
//                showString("地址=" + new String(address, "Unicode"));
//                showString("身份证号=" + new String(IDNo, "Unicode"));
//                showString("发卡机构=" + new String(Department, "Unicode"));
//                showString("有效日期=" + new String(EffectDate, "Unicode") + "至"+ new String(ExpireDate, "Unicode"));
            }

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: " );

        if (TYPE_READER == ConStant.CARD_READER.TYPE_HD){
            HdxUtil.SetIDCARDPower(0);          //关闭读卡器
            HdxUtil.SetCameraBacklightness(0);  //关闭补光灯
        }else
        hsCardReader.closeDevice();
        databaseHelper.close();
        unbindService(updateConn);
        faceApplication.shutdownWatchDog();
        unregisterReceiverSafe(timeReceiver);
        unregisterReceiverSafe(networkReceiver);
        //scheduledExecutorService.shutdown();
    }


    @Override
    protected void onResume() {
        super.onResume();

        surfaceView.startCamera();
        surfaceView.stopGetFeature(false);
        faceRectFirst.setImageSize(WIDTH/RATE,HEIGHT/RATE);
        faceRectSecond.setImageSize(WIDTH/RATE,HEIGHT/RATE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_no_card:
                //由于设备没有触屏，该无证录入功能取消
//                noCard = true;
//                Intent intent = new Intent(MainActivity.this,EntryActivity.class);
//                startActivityForResult(intent,INPUT);
//                surfaceView.stopGetFeature(true);

                break;
            case R.id.iv_take_picture:
                surfaceView.setCardExist(true);
                break;
            case R.id.image_menu:
                //surfaceView.stopGetFeature(true);
                //设置菜单显示在按钮的下面
                popupWindow.showAsDropDown(textViewMenu,0,0);
                popupWindow_view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction()== MotionEvent.ACTION_UP) {
                            //如果菜单存在并且为显示状态，就关闭菜单并初始化菜单
                            if (popupWindow != null && popupWindow.isShowing()) {
                                popupWindow.dismiss();
                            }
                        }
                        return false;
                        }

                });
                Button buttonSearch = (Button) popupWindow_view.findViewById(R.id.btn_menu_search);
                Button buttonInfo = (Button) popupWindow_view.findViewById(R.id.btn_menu_info);
                Button buttonUpdate = (Button) popupWindow_view.findViewById(R.id.btn_menu_update);
                Button buttonConfig = (Button) popupWindow_view.findViewById(R.id.btn_menu_config);
                Button buttonButt = (Button) popupWindow_view.findViewById(R.id.btn_menu_butt);
                buttonSearch.setOnClickListener(MainActivity.this);
                buttonInfo.setOnClickListener(MainActivity.this);
                //单屏暂时不配置
                buttonConfig.setOnClickListener(MainActivity.this);
                buttonUpdate.setOnClickListener(MainActivity.this);
                buttonButt.setOnClickListener(MainActivity.this);
                break;
            case R.id.btn_menu_search:
                //showDialog(SHOW_HISTORY_DIALOG);
                PasswordDialog passwordDialog = new PasswordDialog(this);
                String pass = SharePreUtil.getStringData(this,"password", ConStant.STORAGE.DEFAULT_VALUE);
                passwordDialog.setPassword(pass);
                passwordDialog.setFirstOpen(firstOpen);
                passwordDialog.show();
                popupWindow.dismiss();
                break;
            case R.id.btn_menu_info:
                showDialog(COMPANY_INFO);
                popupWindow.dismiss();

                break;
            case R.id.btn_menu_config:
                final ConfigDialog configDialog = new ConfigDialog(this);
                configDialog.setType(TYPE_READER);
                configDialog.setThreshold(hotelInfo.getThreshold());
                configDialog.setXmlPath(SharePreUtil.getStringData(this,"xml_path","C:\\guest"));
                configDialog.show();

                popupWindow.dismiss();
                break;
            case R.id.btn_menu_update:
                //Toast.makeText(this,"该版本暂不支持更新",Toast.LENGTH_SHORT).show();
                //break;
                popupWindow.dismiss();
                iMainPresenter.initUpdate();
                break;
            case R.id.btn_menu_butt:
                popupWindow.dismiss();
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View layout = LayoutInflater.from(this).inflate(R.layout.dialog_butt,null);
                final AlertDialog dialog = builder.create();
                dialog.setView(layout);
                dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
                final EditText editTextXml = (EditText) layout.findViewById(R.id.et_xml_path);
                editTextXml.setText(SharePreUtil.getStringData(this,"xml_path","C:\\guest"));
                TextView textViewSub = (TextView) layout.findViewById(R.id.tv_login_submit);
                TextView textViewCal = (TextView) layout.findViewById(R.id.tv_login_back);
                textViewSub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String xmlPath = editTextXml.getText().toString();
                        if (xmlPath!=null)
                            saveXmlPath(xmlPath);
                        dialog.dismiss();
                    }
                });
                textViewCal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
        }
    }


    /**
     * 广告轮播任务类，由于没有联系到广告商，暂时取消了这个功能
     */
    private class ViewPagerTask implements Runnable {

        @Override
        public void run() {
            currentItem = (currentItem +1) % imageIds.length;
            faceHandler.sendEmptyMessage(SCROLL);

        }
    }
    private class FaceHandler extends Handler {


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case FIND_CARD:
                    soundPool.play(4, 1, 1, 0, 0, 1);
                    //imageViewAdv.setVisibility(View.GONE);
//                    mMediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.sound_found2);
//                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                        @Override
//                        public void onPrepared(MediaPlayer mp) {
//                            mMediaPlayer.start();
//                        }
//                    });
//
//                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer mp) {
//                            surfaceView.stopGetFeature(false);
//
//                        }
//                    });

                    break;
                case CONNECTED:

                    reConnetCardReader();
                    break;
                case TIME_OUT:
                    //身份证过期
                    showTimeOut();
                    break;
                case SHOW_CARD:

                    HistoryInfo historyInfo = (HistoryInfo) msg.obj;
                    bmpId = historyInfo.getBitmap();
                    idCardInfo = historyInfo.getIdCardInfo();

                    surfaceView.setCardExist(true);

                    break;
                case DETECT_FACE:

                    break;
                case FACE_CAMERA:
                    showNoFace();//读到卡，还没有人脸的时候
                    break;
                case CLEAR_NO_FACE:
                    clearNoFace();
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
                case RECONNECT :
                    hsCardReader.openDevice();
                    break;
                case 96:
                    try {
                        //sendWeChatTrade(hotelInfo.getMachineID());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 97:
                    showDialog(SHOW_ALIPAY_QRCODE);
                    break;
                case SEND_FAIL:
                    ToastUtil.showToast(MainActivity.this,"请检查设备登录状态");
                    break;

                case SCROLL:
                    viewPager.setCurrentItem(currentItem);

                    break;
            }
        }
        public void lightLess() {
            cardview.setVisibility(View.VISIBLE);
            textInfo.setVisibility(View.VISIBLE);
            imageId.setImageBitmap(null);
            textInfo.setText(R.string.light_less);
            myCountDown2.start();

        }

        private void showNoCardReader(){
            cardview.setVisibility(View.VISIBLE);
            textInfo.setText("请插入读卡器");

        }

        private void reConnetCardReader(){
            cardview.setVisibility(View.INVISIBLE);
            textInfo.setText("");
        }

        private void showNoFace(){
            cardview.setVisibility(View.VISIBLE);
            imageId.setImageBitmap(null);
            imageShot.setVisibility(View.INVISIBLE);
            textInfo.setText("请正视摄像头");
        }

        private void clearNoFace(){
            cardview.setVisibility(View.INVISIBLE);
            imageId.setImageBitmap(null);
            textInfo.setText("");
        }


        private void showCard (boolean checkRst, IDCardInfo idCardInfo, Bitmap bitmapID, Bitmap bitmapCut){
            cardview.setVisibility(View.VISIBLE);
            imageId.setImageBitmap(bitmapID);
            imageShot.setVisibility(View.VISIBLE);
            imageShot.setImageBitmap(bitmapCut);
            String name_after = "姓　名："+idCardInfo.getPeopleName();
            textInfo.setText(name_after);
            textTimeOut.setText("性　别："+idCardInfo.getSex());
            //验证结果
            if (checkRst){
                imageResult.setImageDrawable(getResources().getDrawable(R.drawable.success));
            }else {
                imageResult.setImageDrawable(getResources().getDrawable(R.drawable.fail));
            }

            myCountDown2.start();
        }

     private void showCard(boolean checkRst, IDCardInfoHd idCardInfo, Bitmap bitmapID, Bitmap bitmapCut){
         cardview.setVisibility(View.VISIBLE);
         imageId.setImageBitmap(bitmapID);
         imageShot.setImageBitmap(bitmapCut);
         String name_after = "姓　名："+idCardInfo.getPeopleName();

         textInfo.setText(name_after);
         textTimeOut.setText("性　别："+idCardInfo.getSex());
         //验证结果
         if (checkRst){
             imageResult.setImageDrawable(getResources().getDrawable(R.drawable.success));
         }else {
             imageResult.setImageDrawable(getResources().getDrawable(fail));
         }

         myCountDown2.start();
     }
        private void swipeAgain() {
            cardview.setVisibility(View.VISIBLE);
            textInfo.setVisibility(View.VISIBLE);
            textInfo.setText(R.string.swipe_card);
            myCountDown2.start();

        }

        private void showTimeOut(){
            beep(ConStant.BEEP.TIME_OUT);
            cardview.setVisibility(View.VISIBLE);
            //textTimeOut.setVisibility(View.VISIBLE);
            textTimeOut.setText(R.string.time_out);
            textInfo.setText("");
            imageId.setImageBitmap(null);
            imageResult.setImageDrawable(getResources().getDrawable(fail));
            myCountDown2.start();
        }


    private void clearCardInfo() {
        cardview.setVisibility(View.GONE);
        imageId.setImageBitmap(null);
        imageShot.setImageBitmap(null);
        imageResult.setImageBitmap(null);
        textTimeOut.setText("");
        //surfaceView.stopGetFeature(true);
        //imageViewAdv.setVisibility(View.VISIBLE);
        //faceRectFirst.setVisibility(View.INVISIBLE);
        //faceRectSecond.setVisibility(View.INVISIBLE);
    }
        // Executes UNIX command.
        private String exec(String command) {
            try {
                Process process = Runtime.getRuntime().exec(command);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                int read;
                char[] buffer = new char[4096];
                StringBuffer output = new StringBuffer();
                while ((read = reader.read(buffer)) > 0) {
                    output.append(buffer, 0, read);
                }
                reader.close();
                process.waitFor();
                return output.toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void saveGuestInfoHd(boolean success) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        String guestPath = path+"/guestInfo/";
        FileUtil.mkDir(guestPath);
        String fileName = guestPath+"guest.xml";
        if (success){
            FileUtil.initHdXml(idCardInfoHd,ConStant.SUCCESSFUL,fileName,SharePreUtil.getStringData(this,"xml_path","c:\\guest"));
        }else {
            FileUtil.initHdXml(idCardInfoHd,ConStant.FAILURE,fileName,SharePreUtil.getStringData(this,"xml_path","c:\\guest"));
        }

        ImgUtil.saveImage(MainActivity.this,bmpId,guestPath,"PHOTO.jpg");
        ImgUtil.saveImage(MainActivity.this,bmpCut,guestPath,"ZPHOTO.jpg");
        //强制刷新
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(fileName);
        intent.setData(Uri.fromFile(file));    // 需要更新的文件路径
        MainActivity.this.sendBroadcast(intent);
    }


    public void disMiss(){
        faceHandler.sendEmptyMessage(QRCODE_DISMISS);
    }


    private void unregisterReceiverSafe(BroadcastReceiver receiver) {
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            // ignore
            Log.e(TAG, "unregisterReceiverSafe: " );
        }
    }

    public void saveCardReaderType(int selected){

        SharePreUtil.saveIntData(this,"reader_type",selected);

        recreate();
        Toast.makeText(this, R.string.set_success,Toast.LENGTH_SHORT).show();
    }

    public void saveThreshold(float threshold){
        if (this.threshold!= threshold)
        hotelInfo.setThreshold(threshold);

    }

    public void saveXmlPath(String path){


        SharePreUtil.saveStringData(this,"xml_path",path);

    }
    /**
     * 重新加载未上传的记录
     */

    private void reLoad(ArrayList<HistoryInfo> his){
        int count = his.size();
        for (int i = 0; i < count; i++){
            HistoryInfo info = his.get(i);
            try {
                //reSendCard(info);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void writeLog(String log){
        FileUtil.writeStringToFile(log,fileName);
    }

}
