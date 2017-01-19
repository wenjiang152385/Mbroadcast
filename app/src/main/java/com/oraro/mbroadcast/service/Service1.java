package com.oraro.mbroadcast.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;
import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.broadcasts.OlympicsReceiver;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.mina.client.MinaStringClientThread;
import com.oraro.mbroadcast.model.PlayEntry;
import com.oraro.mbroadcast.mp.PlayAudio;
import com.oraro.mbroadcast.proxy.TTSProXy;
import com.oraro.mbroadcast.tts.TTSInterface;
import com.oraro.mbroadcast.utils.LogUtils;
import com.oraro.mbroadcast.utils.SPUtils;
import com.oraro.mbroadcast.utils.ServiceUtils;
import com.oraro.mbroadcast.vo.PlayVO;

import java.util.Iterator;
import java.util.Set;


public class Service1 extends Service {
    private OlympicsReceiver receiver;
    //    private  String TAG = getClass().getName();
    private String Service_Name = "com.oraro.mbroadcast.service.Service2";
    private String Process_Name = "com.oraro.mbroadcast:service2";
    //    private Thread thread = new Thread(new MyRunnableImpl());
//    private MyRunnableImpl runnable = new MyRunnableImpl();
//    private FrushPlayStatus frushPlayStatus;
    public OnRefreshUIListener onRefreshUIListener;
    //    PlayService playService = PlayService.getInstance();
    SpeekingService speekingService = SpeekingService.getInstance();
    private IMyAidlInterface service_1 = new IMyAidlInterface.Stub() {


        public long mdsid;
        public long ttsid;
        public OnRefreshUIListener onMediaPlayListener;
        public OnRefreshUIListener onTTSPlayListener;
        public String path;
        public int count;
        public String textDesc;
        TTSInterface tts = TTSProXy.getInstance(MBroadcastApplication.getMyContext(), MBroadcastApplication.getMyPackageName());
        PlayAudio playAudio = PlayAudio.getInstance();
        int i = 0;


        @Override
        public void startService() throws RemoteException {
            Intent i = new Intent(Service1.this, Service2.class);
            Service1.this.startService(i);
//            Intent intent = new Intent(MBroadcastApplication.getMyContext(), FrushService.class);
//            MBroadcastApplication.getMyContext().bindService(intent, conn, Context.BIND_AUTO_CREATE);

        }

        @Override
        public void stopService() throws RemoteException {
            Intent i = new Intent(Service1.this, Service2.class);
            Service1.this.stopService(i);
        }

        @Override
        public void stopMediaPlay() throws RemoteException {
            if (onTTSPlayListener != null) {
                try {
                    onTTSPlayListener.completed(ttsid, null);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            playAudio.stopAudio();
        }

        @Override
        public void stopTTSPlay() throws RemoteException {
            if (onTTSPlayListener != null) {
                try {
                    onTTSPlayListener.completed(ttsid, null);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            tts.stopPlaying();
        }

        @Override
        public void startTTSPlay(long ttsid, String textDesc, int count, OnRefreshUIListener onRefreshUIListener) throws RemoteException {
            stopPlay();

            MBroadcastApplication.isincout = false;
            MBroadcastApplication.isUrgent = false;
            Log.e("huanghui", "startTTSPlay");
            i = 0;
            this.textDesc = textDesc;
            this.count = count;
            this.ttsid = ttsid;
            onTTSPlayListener = onRefreshUIListener;
            tts.TTSStartPlay(textDesc, null, null, null, null, null, null, null, synthesizerListener);
            minaStringTTS(ttsid);
        }

        @Override
        public void stopPlay() {
            if (tts.isSpeeking()) {
                if (onTTSPlayListener != null) {
                    try {
                        onTTSPlayListener.completed(ttsid, null);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                tts.stopPlaying();
            }
            if (playAudio.isPlaying()) {
                try {
                    if (onMediaPlayListener != null) {
                        onMediaPlayListener.completed(mdsid, null);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                playAudio.stopAudio();
            }
        }

        @Override
        public void startMediaPlay(long mdsid, String path, int count, OnRefreshUIListener onRefreshUIListener) throws RemoteException {
            stopPlay();
            Log.e("huanghui", "startMediaPlay");
            MBroadcastApplication.isincout = false;
            MBroadcastApplication.isUrgent = false;
            i = 0;
            this.path = path;
            this.count = count;
            this.mdsid = mdsid;
            playAudio.setPath(path);
            onMediaPlayListener = onRefreshUIListener;
            onMediaPlayListener.frushPlaying(mdsid);
            playAudio.startAudio(onCompletionListener);
            minaStringAudio(mdsid);
        }
        @Override
        public void setOnRefreshUIListener(OnRefreshUIListener l) throws RemoteException {
            onRefreshUIListener = l;
            if (null != speekingService) {
                speekingService.setOnRefreshUIListener(onRefreshUIListener);
            }
        }

        @Override
        public void unRegisterOnRefreshUIListener() throws RemoteException {
            onRefreshUIListener = null;
            if (null != speekingService) {
                speekingService.unRegisterOnRefreshUIListener();
            }
        }

        @Override
        public void deleteCatch(long id) throws RemoteException {
            speekingService.delete(id);
//            playService.delete(id);
        }

        @Override
        public void refresh() throws RemoteException {
            speekingService.refresh();
        }

        @Override
        public void needrefresh(boolean tag) throws RemoteException {
            speekingService.refresh(tag);
        }

        @Override
        public void needrefreshbyty(boolean tag, int ty) throws RemoteException {
            speekingService.refresh(tag,ty);
        }

        @Override
        public void autioPlay(boolean tag) throws RemoteException {
            speekingService.autioPlay(tag);
        }

        @Override
        public void startInterCut(int count,long space) throws RemoteException {
            speekingService.startInterCut(count,space);
        }

        @Override
        public void initSpeekingServiceForInterCut() throws RemoteException {
            LogUtils.e("IMyAidlInterface::initSpeekingServiceForInterCut");
            if(mIsInitSpeekingServiceForInterCut){
                return;
            }
            mIsInitSpeekingServiceForInterCut = true;
            speekingService.init(null);
        }

        SynthesizerListener synthesizerListener = new SynthesizerListener() {
            @Override
            public void onSpeakBegin() {
                try {
                    onTTSPlayListener.frushPlaying(ttsid);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onBufferProgress(int i, int i1, int i2, String s) {

            }

            @Override
            public void onSpeakPaused() {

            }

            @Override
            public void onSpeakResumed() {

            }

            @Override
            public void onSpeakProgress(int i, int i1, int i2) {

            }

            @Override
            public void onCompleted(SpeechError speechError) {
                if(speekingService.getInterCut() || speekingService.getWaitinit()){
                    speekingService.regainWait();
                }
                i++;
                if (i < count) {
                    tts.TTSStartPlay(textDesc, null, null, null, null, null, null, null, synthesizerListener);
                    minaStringTTS(ttsid);
                } else {
                    speekingService.refresh();
                    if (onTTSPlayListener != null) {
                        try {
                            if (speechError != null) {
                                onTTSPlayListener.completed(ttsid, speechError.toString());
                            } else {
                                onTTSPlayListener.completed(ttsid, null);
                            }
                            onTTSPlayListener = null;
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        };


        MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                if(speekingService.getInterCut() || speekingService.getWaitinit()){
                    speekingService.regainWait();
                }
                i++;
                if (i < count) {
                    mp.start();
                    minaStringAudio(mdsid);
                } else {
                    mp.stop();
                    speekingService.refresh();
                    try {
                        if (onMediaPlayListener != null) {
                            onMediaPlayListener.completed(mdsid, null);
                        }
                        onMediaPlayListener = null;
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    };


//    private ServiceConnection conn = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//            frushPlayStatus = FrushPlayStatus.Stub.asInterface(iBinder);
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//
//        }
//    };

    int tag = 0;
    private boolean mIsInitSpeekingServiceForInterCut = false;
    private static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    private static final String ACTION1 = Constants.EXCEL_Transfer_Finish_String;
    private static final String ACTION2 = Constants.MD_Transfer_Finish_String;
    private static final String ACTION3 = Constants.EXCEL_Transfer_Fail;
    private static final String ACTION4 = Constants.MD_Transfer_Fail;
    private static final String ACTION5 = Constants.Audio_NO_DATA_String;
    @Override
    public void onCreate() {
//1.创建广播接收者对象
        receiver = new OlympicsReceiver();
        //2.创建intent-filter对象
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION1);
        filter.addAction(ACTION2);
        filter.addAction(ACTION3);
        filter.addAction(ACTION4);
        filter.addAction(ACTION5);
        //3.注册广播接收者
        registerReceiver(receiver, filter);
        new Thread() {
            public void run() {
                while (true) {
                    boolean PisRun = ServiceUtils.isProessRunning(Service1.this, Process_Name);
                    if (PisRun == false) {
                        try {
                            Log.e("huanghui", "PisRun" + PisRun);
                            service_1.startService();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    boolean SisRun = ServiceUtils.isServiceRunning(Service1.this, Service_Name);
                    if (SisRun == false) {
                        try {
                            Log.e("huanghui", "SisRun" + SisRun);
                            service_1.startService();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    sPeekinginit();
                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                    //运行业务逻辑类
//                    if (runnable != null)
//                        runnable.run(onRefreshUIListener,playService);
//                    else
//                        runnable = new MyRunnableImpl();

                }
            }
        }.start();
    }

    private synchronized void sPeekinginit() {
        if (tag == 0 && onRefreshUIListener != null) {
            speekingService.init(onRefreshUIListener);
            Log.e("huanghui", "init");
            tag = 1;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return (IBinder) service_1;
    }

    private void minaStringTTS(Long id) {
        DBManager dbManager = DBManager.getInstance(MBroadcastApplication.getMyContext());
        PlayEntry playEntry = (PlayEntry) dbManager.queryById(id, dbManager.getPlayEntryDao(DBManager.READ_ONLY));
        PlayVO playVO = new PlayVO(playEntry);
        Set set = SPUtils.getPrefStringSet(MBroadcastApplication.getMyContext(), "set", null);
        if (null != set) {
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                String ip = (String) iterator.next();
                MinaStringClientThread minaStringClientThread = new MinaStringClientThread();
                minaStringClientThread.setType(Constants.TTS_PLAY);
                minaStringClientThread.setPlayVO(playVO);
                minaStringClientThread.setIp(ip);
                MinaStringClientThread.getThreadPoolExecutor().execute(minaStringClientThread);
            }
        }
    }

    private void minaStringAudio(Long id) {
        DBManager dbManager = DBManager.getInstance(MBroadcastApplication.getMyContext());
        PlayEntry playEntry = (PlayEntry) dbManager.queryById(id, dbManager.getPlayEntryDao(DBManager.READ_ONLY));
        PlayVO playVO = new PlayVO(playEntry);
        Set set = SPUtils.getPrefStringSet(MBroadcastApplication.getMyContext(), "set", null);
        if (null != set) {
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                String ip = (String) iterator.next();
                MinaStringClientThread minaStringClientThread = new MinaStringClientThread();
                minaStringClientThread.setType(Constants.MD_PLAY);
                minaStringClientThread.setPlayVO(playVO);
                minaStringClientThread.setIp(ip);
                MinaStringClientThread.getThreadPoolExecutor().execute(minaStringClientThread);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
