package com.chuangber.verify.broadcast;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

/**4小时重启一次，避免机器长期工作
 * Created by jinyh on 2017/12/29.
 */

public class RestartAppService extends Service {
    @Nullable
    private static final long RESTART_DELAY = 60 * 1000 * 60 * 4; //
    private MyBinder mBinder;

    public class MyBinder extends Binder {

        /**
         * 获取service实例
         * @return
         */
        public RestartAppService getService() {
            return RestartAppService.this;
        }

        /**
         * 启动app重启任务
         */
        public void startRestartTask(final Context context) {

            TimerTask task = new TimerTask() {

                @Override
                public void run() {
                    // restart
                    Intent intent = getPackageManager().getLaunchIntentForPackage(
                            getApplication().getPackageName());
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    System.exit(0);
                }
            };

            Timer timer = new Timer();
            timer.schedule(task, RESTART_DELAY);
        }
    }


    public IBinder onBind(Intent intent) {
        if (mBinder == null) {
            mBinder = new MyBinder();
        }
        return mBinder;
    }
}
