package com.oraro.mbroadcast.service;

import com.oraro.mbroadcast.logicService.PlayService;

import java.util.Date;

/**
 * Created by admin on 2016/8/10.
 */
public class MyRunnableImpl implements Runnable {
    private OnRefreshUIListener onRefreshUIListener;
    private PlayService playService;

    //private int step=0;
    @Override
    public void run() {
        /*
        具体业务逻辑代码
         */
     //   doFreshAndPlay();

    }

    public void run(OnRefreshUIListener onRefreshUIListener,PlayService playService){

        if(this.onRefreshUIListener == null){
            this.onRefreshUIListener = onRefreshUIListener;
        }
        if(this.playService == null){
            this.playService = playService;
        }
        doFreshAndPlay();


//        if(frushPlayStatus !=null){
//            Log.e("wyaidl","MyRunnable thread = "+ Process.myPid());
////            step = 1;
//            try {
//                frushPlayStatus.frushPlaying(100);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
    }

    /**
     * 刷新播放队列并播放
     */
    private void doFreshAndPlay(){
     //   Log.e("wy","MyRunnableImpl.run()");
        Date date = new Date();
        Date beginTime = new Date(1,1,1,0,0,0);
     //   Date beginTime = new Date(time.getTime()-1000);
        Date endTime = new Date(date.getYear(),date.getMonth(),date.getDate(),23,59,59);
        playService.fresh(beginTime,endTime,onRefreshUIListener);
    }





}
