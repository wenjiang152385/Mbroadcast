package com.oraro.mbroadcast.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.oraro.mbroadcast.mina.server.MinaFileServerHandler;

/**
 * Created by dongyu on 2016/9/5 0005.
 */
public class MinaFileServerService extends Service {
    private final String TAG = MinaFileServerService.class.getSimpleName();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "MinaFileServerService onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return Service.START_STICKY;
    }
}
