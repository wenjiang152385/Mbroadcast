package com.oraro.mbroadcast.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.mp.PlayAudio;
import com.oraro.mbroadcast.proxy.TTSProXy;
import com.oraro.mbroadcast.tts.TTSInterface;

/**
 * 界面刷新服务
 * @author 王子榕
 */
public class FrushService extends Service {
    private Handler handler;

    public FrushService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return iBinder;
    }

    public void setHandler(Handler handler){
     this.handler = handler;
    }

    private FrushPlayStatus.Stub iBinder = new FrushPlayStatus.Stub(){

        @Override
        public void frushPlaying(long id) throws RemoteException {
            handler = MBroadcastApplication.getFrushHandler();
            if(handler != null){
                Message message = Message.obtain();
                message.what = Constants.HandlerConstants.PLAY;
                message.obj = id;
                handler.sendMessage(message);
            }


            Log.e("wyaidl","FrushService thread = "+ Process.myPid());
        }

        @Override
        public void frushCompleted(long id) throws RemoteException {
            handler = MBroadcastApplication.getFrushHandler();
            if(handler != null){
                Message message = Message.obtain();
                message.what = Constants.HandlerConstants.COMPLETED;
                message.obj = id;
                handler.sendMessage(message);
            }
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
            handler = MBroadcastApplication.getFrushHandler();
            if(handler != null){
                Message message = Message.obtain();
                message.what = Constants.HandlerConstants.PLAY;
                message.obj = aString;
                handler.sendMessage(message);
            }
        }

        @Override
        public boolean isPlay() throws RemoteException {
            TTSInterface tts = TTSProXy.getInstance(MBroadcastApplication.getMyContext(), MBroadcastApplication.getMyPackageName());
            PlayAudio playAudio = PlayAudio.getInstance();
            if(tts.isSpeeking())
                return true;
            if(playAudio.isPlaying())
                return true;
            return false;
        }
    };
}
