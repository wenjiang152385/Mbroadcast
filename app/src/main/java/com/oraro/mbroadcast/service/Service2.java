package com.oraro.mbroadcast.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.oraro.mbroadcast.utils.ServiceUtils;

public class Service2 extends Service {

    private String Service_Name = "com.oraro.mbroadcast.service.Service1";

    private String Process_Name = "com.oraro.mbroadcast.service:service1";

    /**
     * 启动Service1
     */
    private IMyAidlInterface2 service_2 = new IMyAidlInterface2.Stub(){

        @Override
        public void startService() throws RemoteException {
            Intent i = new Intent(Service2.this,Service1.class);
            Service2.this.startService(i);
        }

        @Override
        public void stopService() throws RemoteException {
            Intent i = new Intent(Service2.this,Service1.class);
            Service2.this.stopService(i);
        }
    };

    @Override
    public void onCreate() {
        new Thread(){
            public void run(){
                while (true){
//                    boolean PisRun = ServiceUtils.isProessRunning(Service2.this,Process_Name);
//                    if (PisRun == false){
//                        try {
//                            service_2.startService();
//                        } catch (RemoteException e) {
//                            e.printStackTrace();
//                        }
//                    }
                    boolean SisRun = ServiceUtils.isServiceRunning(Service2.this,Service_Name);
                    if (SisRun == false){
                        try {
                            Log.e("huanghui","SisRun2"+SisRun);
                            service_2.startService();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return (IBinder)service_2;
    }
}
