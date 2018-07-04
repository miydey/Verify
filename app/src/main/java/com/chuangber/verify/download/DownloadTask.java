package com.chuangber.verify.download;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DownloadTask extends AsyncTask<String,Integer,Integer> {
    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSED = 2;
    public static final int TYPE_CANCELED = 3;
    private DownloadListener downloadListener;
    private boolean isCanceled = false;
    private boolean isPaused = false;
    private int lastProgress;
    OkHttpClient client = new OkHttpClient();
    public DownloadTask(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    @Override
    protected Integer doInBackground(String... params) {
        InputStream is = null;
        RandomAccessFile savedFile = null;
        File file = null;

        long downloadedLength = 0;//记录已经下载的文件长度
        String downloadUrl = params[0];
        String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
        String directory = Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS).getPath();
        file = new File(directory+fileName);
        if (file.exists()){
            downloadedLength = file.length();
        }
        try {
            long contentLength = getContentLength(downloadUrl);
            if (contentLength ==0){
                return TYPE_FAILED;
            }else if (contentLength == downloadedLength){
                return TYPE_SUCCESS;
            }
            Request request = new Request.Builder()
                    .addHeader("RANGE","bytes="+downloadedLength+"-")
                    .url(downloadUrl)
                    .build();
            Response response = client.newCall(request).execute();
            if (response != null){
                is = response.body().byteStream();
                savedFile = new RandomAccessFile(file,"rw");
                savedFile.seek(downloadedLength);//跳过已经下载的字节
                byte[]b = new byte[1024];
                int total = 0;
                int len;
                while ((len = is.read(b))!= -1){
                    if (isCanceled){
                        return TYPE_CANCELED;
                    }else if (isPaused){
                        return TYPE_PAUSED;
                    }else {
                        total+=len;
                        savedFile.write(b,0,len);//计算下载的进度
                        int progress = (int) ((total+downloadedLength)*100/contentLength);
                        publishProgress(progress);

                    }
                }
                response.body().close();
                return TYPE_SUCCESS;
            }
        } catch (IOException e) {

            e.printStackTrace();
        } finally {
            try {
            if (is!= null){
                    is.close();
            }
                if (savedFile!=null){
                    savedFile.close();
                }
            if (isCanceled && file!=null){
                file.delete();
            }
            }catch (Exception e){
                e.printStackTrace();
            }
            }
        return TYPE_FAILED;

    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if (progress>lastProgress){
            downloadListener.onProgress(progress);
            lastProgress = progress;
        }
    }

    @Override
    protected void onPostExecute(Integer status) {
        switch (status){
            case TYPE_SUCCESS:
                downloadListener.onSuccess();
                break;
            case TYPE_CANCELED:
                downloadListener.onCanceled();
                break;
            case TYPE_FAILED:
                downloadListener.onFailed();
                break;
            case TYPE_PAUSED:
                downloadListener.onPaused();
                break;
        }
    }


    public void pauseDownload(){
        isPaused = true;
    }

    public void cancelDownload(){
        isCanceled = true;
    }

    private long getContentLength(String downloadUrl) throws IOException {
        Request request = new Request.Builder()
                                .url(downloadUrl)
                                .build();
        Response response = client.newCall(request).execute();
        if (response!= null && response.isSuccessful()){
            long contentLength = response.body().contentLength();
            response.body().close();
            Log.d("fileLength", String.valueOf(contentLength));
            return contentLength;

        }

        return 0;
    }
}
