package com.oraro.mbroadcast.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by dongyu on 2016/9/5 0005.
 */
public class BackService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        MyRunnableImpl runnable =  new MyRunnableImpl();
        new Thread(){
            public void run(){
                MyRunnableImpl runnable =  new MyRunnableImpl();
                while (true){
                    //运行业务逻辑类
                    if(runnable != null)
                        runnable.run();
                    else
                        runnable = new MyRunnableImpl();

                }
            }
        }.start();
        return Service.START_STICKY;
    }
}
