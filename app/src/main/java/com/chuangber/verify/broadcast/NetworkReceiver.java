package com.chuangber.verify.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.chuangber.verify.MainActivity;

/**
 * Created by jinyh on 2017/10/24.
 */

public class NetworkReceiver extends BroadcastReceiver {

    private static String TAG = NetworkReceiver.class.getSimpleName();
    int offLine;
    @Override
    public void onReceive(Context context, Intent intent) {
        MainActivity mainAct = (MainActivity) context;
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
      if (networkInfo!= null){
          NetworkInfo mNetworkInfo = connectivityManager
                  .getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);

          if (networkInfo!= null && networkInfo.isAvailable()){
              mainAct.writeLog("---network status is "+networkInfo.getDetailedState()+"---");
              // connectivityManager.reportBadNetwork(connectivityManager.getActiveNetwork());
              mainAct.setOfflineAnimator(false);
              mainAct.reConnectWebSocket();
          }else {
              mainAct.shutdownWeb();
              mainAct.setOfflineAnimator(true);
                  mainAct.writeLog("---network status is "+networkInfo.getDetailedState()+"---");

          }
      } else {
          mainAct.shutdownWeb();
          mainAct.setOfflineAnimator(true);
          mainAct.writeLog("---network is not available---");
      }

    }
}
