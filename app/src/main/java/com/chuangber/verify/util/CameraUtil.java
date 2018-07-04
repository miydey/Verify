package com.chuangber.verify.util;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.chuangber.verify.MainActivity;
import com.chuangber.verify.R;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by jinyh on 2017/10/21.
 *
 * 用于设置相机参数
 */

public class CameraUtil {
    private  int cameraIndex = 0;
    private  Camera camera;
    private Context context;
    private  int WIDTH ;
    private  int HEIGHT;
    private static final int RATE = 4; //缩放比率
    private boolean isPreview = false;
    private static final String TAG = CameraUtil.class.getSimpleName();
    private static CameraUtil cameraUtil = new CameraUtil();

    public static CameraUtil geInstance (){
        return cameraUtil;
    }

    private CameraUtil(){

    }

    public void setSize(int preWidth,int preHeight){
        WIDTH = preWidth;
        HEIGHT = preHeight;
    }

    public Camera getCamera(){
        return camera;
    }

    public void getCamera(MainActivity mainActivity, SurfaceHolder surfaceHolder,byte[] camBuf)  {
        Log.d(TAG, "initCamera: ");
        if (!isPreview){
            try {
                int cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
//                if (cameraCount == 2){
//                    cameraIndex = 1;
//                }
                camera = Camera.open(cameraIndex);

            } catch (RuntimeException e){
                Toast.makeText(mainActivity, R.string.camera_open_fail,Toast.LENGTH_LONG).show();
            }

            if (camera != null){
                setCameraDisplayOrientation(mainActivity,cameraIndex,camera);
                Camera.Parameters parameters = camera.getParameters();
                List<Camera.Size> list = parameters.getSupportedPictureSizes();
                Iterator its = list.iterator();
                Camera.Size size = null;
                while (its.hasNext()){
                    size = (Camera.Size) its.next();
                    Log.i(TAG, "Camera: "+size.width+" X "+size.height );
                }
                parameters.setPreviewSize(WIDTH,HEIGHT);

                if (camBuf == null){
                    camBuf = new byte[WIDTH * HEIGHT *3/2];
                }
                try {
                    camera.setParameters(parameters);
                    camera.setPreviewDisplay(surfaceHolder);
                    camera.addCallbackBuffer(camBuf);
                    //camera.setPreviewCallbackWithBuffer(mainActivity);
                    camera.startPreview();
                    isPreview = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(mainActivity,R.string.camera_open_fail,Toast.LENGTH_LONG);
                    closeCamera();
                }catch (RuntimeException e){
                    Toast.makeText(mainActivity,R.string.camera_open_fail,Toast.LENGTH_LONG);
                    closeCamera();
                }
            }
        }
    }

    public void closeCamera() {
        if (null != camera) {
            camera.setPreviewCallbackWithBuffer(null);
            camera.stopPreview();
            camera.release();
            camera = null;
            isPreview = false;
            Log.d(TAG, "closeCamera: " );
        }
    }

    public static void setCameraDisplayOrientation(
            Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0; break;
            case Surface.ROTATION_90:
                degrees = 90; break;
            case Surface.ROTATION_180:
                degrees = 180; break;
            case Surface.ROTATION_270:
                degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }


}
