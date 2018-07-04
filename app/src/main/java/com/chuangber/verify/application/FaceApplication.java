package com.chuangber.verify.application;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.chuangber.verify.MainActivity;
import com.chuangber.verify.R;
import com.chuangber.verify.broadcast.RestartAppService;
import com.chuangber.verify.task.KeepAliveWatchDog;
import com.chuangber.verify.util.SharePreUtil;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static java.lang.System.currentTimeMillis;

/**
 * Created by jinyh on 2017/5/8.
 * 湖南项目
 */

public class FaceApplication extends Application  {
    private static final String TAG = FaceApplication.class.getSimpleName();
    static FaceApplication faceApplication;
    private String licence;
    private String customName = "ysck";
    private String customCode = "ysck20170428";
    String appVersion = "1.3";
    private SharedPreferences sharedPreferences;
    private static final String LOG_DIR = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/verify/log/";
    private static final String LOG_NAME = getCurrentDateString() + ".txt";
    KeepAliveWatchDog watchDog;
    private boolean keepDeviceAlive;
    String fileName;
    Thread threadAlive;
    private ServiceConnection conn = new ServiceConnection() {

        /**
         * Called when a connection to the Service has been established,
         * with the android.os.IBinder of the communication channel to the Service.
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            RestartAppService.MyBinder mBinder = (RestartAppService.MyBinder) service;
            mBinder.startRestartTask(FaceApplication.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {}

    };

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        SophixManager.getInstance().setContext(this)
//                .setAppVersion(appVersion)
//                .setAesKey(null)
//                .setEnableDebug(true)
//                .setSecretMetaData(ConStant.ALI_FIX.ID_SECRET, ConStant.ALI_FIX.APP_SECRET,
//                        ConStant.ALI_FIX.RSA_SECRET)
//                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
//                    @Override
//                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
//                        // 补丁加载回调通知
//                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
//                            // 表明补丁加载成功
//                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
//                            // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
//                            SophixManager.getInstance().killProcessSafely();
//                            // 建议: 用户可以监听进入后台事件, 然后调用killProcessSafely自杀，以此加快应用补丁，详见1.3.2.3
//                        } else {
//                            // 其它错误信息, 查看PatchStatus类说明
//                            Log.e(TAG, "error code"+code );
//                        }
//                    }
//                }).initialize();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ZXingLibrary.initDisplayOpinion(this); // 初始化二维码插件
        faceApplication = this;
        //SophixManager.getInstance().queryAndLoadNewPatch(); //初始化热更新
        Thread.setDefaultUncaughtExceptionHandler(handler);
        Intent intent = new Intent(this, RestartAppService.class); //定时重启
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            writeErrorLog(ex);
            Intent intent = getPackageManager().getLaunchIntentForPackage(
                    FaceApplication.this.getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            System.exit(1);
        }
    };

    private void initDaemon(){
        Intent intent = new Intent(MainActivity.LOCAL_BROADCAST_DAEMON);
        sendBroadcast(intent);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

    }

    public static FaceApplication getFaceApplication(){
        return faceApplication;
    }


    public boolean getKeepAlive(){
        return keepDeviceAlive;
    }

    public void initWatchDog(MainActivity activity){  //向服务端发送心跳
        Log.e(TAG, "initWatchDog: " );
        //为了防止打错字 以后的sharePre直接在application获取
        String machineId = SharePreUtil.getStringData(this,"machine_id","default");
        String machineName = SharePreUtil.getStringData(this,"machine_name","default");
        String hotelName = SharePreUtil.getStringData(this,"hotel_name","default");
        if (watchDog == null && (!machineId.equals("default"))){
            watchDog = new KeepAliveWatchDog(machineId,
                    SharePreUtil.getStringData(this,"machine_token","default"),machineName,hotelName,getLocalVersion());
            watchDog.setRun(true);
            watchDog.setContext(activity);
            threadAlive =  new Thread(watchDog);
            threadAlive.start();
            keepDeviceAlive = true;
        }else {
            keepDeviceAlive = false;
        }

    }
    public SharedPreferences getShare(){
        if (sharedPreferences == null)
            sharedPreferences = getSharedPreferences(getResources().getString(R.string.storage_key),MODE_PRIVATE);
        return sharedPreferences;
    }

    public void shutdownWatchDog(){
        if (watchDog!= null){
            watchDog.setRun(false);
            watchDog.closeWatchDog();
            threadAlive.interrupt();
            try {
                threadAlive.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            watchDog = null;
            keepDeviceAlive = false;
            Log.e(TAG, "shutdownWatchDog: ");
        }

    }
    protected void writeErrorLog(Throwable ex) {
        String info = null;
        ByteArrayOutputStream baos = null;
        PrintStream printStream = null;
        try {
            baos = new ByteArrayOutputStream();
            printStream = new PrintStream(baos);
            ex.printStackTrace(printStream);
            byte[] data = baos.toByteArray();
            info = new String(data);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String time = simpleDateFormat.format(currentTimeMillis());
            info = time +"\n"+ info;
            data = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (printStream != null) {
                    printStream.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File dir = new File(LOG_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, LOG_NAME);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write(info.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * 获取当前日期
     *
     */
    private static String getCurrentDateString() {
        String result = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault());
        Date nowDate = new Date();
        result = sdf.format(nowDate);
        return result;
    }

    //获取当前版本
    public String getLocalVersion() {
        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return  info.versionName;
    }
}
