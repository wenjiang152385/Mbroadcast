package com.oraro.mbroadcast.broadcasts;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.service.SerService;
import com.oraro.mbroadcast.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2016/12/20 0020.
 */
public class NetWorkReceiver extends BroadcastReceiver {

    private ConnectivityManager mConnectivityManager;

    private NetworkInfo netInfo;

    private WifiInfo mWifiInfo;

    //监听网络状态变化的广播接收器
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = mConnectivityManager.getActiveNetworkInfo();

            if (netInfo != null && netInfo.isAvailable()) {
                if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                   if (wifiInfo != mWifiInfo) {
                       SPUtils.setPrefInt(context, "STATUS", 0);
                       EventBus.getDefault().post(new SimpleEvent(Constants.NEWWORK_CHANGE));
                       Intent intent1 = new Intent(context, SerService.class);
                       intent1.putExtra("status",2);
                       context.startService(intent1);
                   }
                    mWifiInfo = wifiInfo;
                }

            } else {
                SPUtils.setPrefInt(context, "STATUS", 0);
            }
        }
    }


    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }
}
