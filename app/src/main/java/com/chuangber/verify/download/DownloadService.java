package com.chuangber.verify.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;
import com.chuangber.verify.MainActivity;
import com.chuangber.verify.R;

import java.io.File;

public class DownloadService extends Service {
    private LocalBroadcastManager localBroadcastManager;
    private DownloadTask downloadTask;
    private String downloadUrl;
    private DownloadBinder mbinder = new DownloadBinder();
    private DownloadListener listener = new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            getNotificationManager().notify(1,getNotification("正在下载...",progress));
        }

        @Override
        public void onSuccess() {
            downloadTask = null;
            stopForeground(true);
            localBroadcastManager = LocalBroadcastManager.getInstance(DownloadService.this);
            Intent intent = new Intent(MainActivity.LOCAL_BROADCAST_DOWN);
            localBroadcastManager.sendBroadcast(intent);
            getNotificationManager().notify(1,getNotification("下载成功",-1));


        }

        @Override
        public void onFailed() {
            downloadTask =null;
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("下载失败",-1));
            Toast.makeText(DownloadService.this,"下载失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused() {
            downloadTask =null;
            Toast.makeText(DownloadService.this,"停止下载", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCanceled() {
            downloadTask = null;
            stopForeground(true);
            Toast.makeText(DownloadService.this,"取消下载", Toast.LENGTH_SHORT).show();
        }
    };



    public DownloadService() {
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mbinder;
    }


   public class DownloadBinder extends Binder {
        public void startDownload(String url){
            if (downloadTask ==null){
                downloadUrl = url;
                downloadTask = new DownloadTask(listener);
                downloadTask.execute(downloadUrl);
                startForeground(1,getNotification("正在下载",0));

            }
        }

        public void pauseDownload(){
            if (downloadTask!= null){
                downloadTask.pauseDownload();
            }
        }

        public void cancelDownload(){
            if (downloadTask!= null){
                downloadTask.cancelDownload();
            }else {
                if (downloadUrl!= null){
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory = Environment.getExternalStoragePublicDirectory
                            (Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(directory+fileName);
                    if (file.exists()){
                        file.delete();
                    }
                    getNotificationManager().cancel(1);
                    stopForeground(true);

                }
            }
        }

    }

    private NotificationManager getNotificationManager(){
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }


    private Notification getNotification(String title, int progress){
        Intent intent = new Intent(this, MainActivity.class);
        //PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.logo);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.logo));
        //builder.setContentIntent(pi);
        builder.setContentTitle(title);
        builder.setAutoCancel(true);
        if (progress>0){
            builder.setContentText(progress+"%");
            builder.setProgress(100,progress,false);
        }
        return builder.build();
    }
}
