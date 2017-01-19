package com.oraro.mbroadcast.service;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;
import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.dao.InterCutDataDao;
import com.oraro.mbroadcast.dao.UrgentItemBeanDao;
import com.oraro.mbroadcast.logicService.DataService;
import com.oraro.mbroadcast.mina.client.MinaAutoStringClientThread;
import com.oraro.mbroadcast.mina.client.MinaStringClientThread;
import com.oraro.mbroadcast.model.InterCutData;
import com.oraro.mbroadcast.model.PlayEntry;
import com.oraro.mbroadcast.model.UrgentItemBean;
import com.oraro.mbroadcast.mp.PlayAudio;
import com.oraro.mbroadcast.proxy.TTSProXy;
import com.oraro.mbroadcast.tts.TTSInterface;
import com.oraro.mbroadcast.utils.LogUtils;
import com.oraro.mbroadcast.utils.SPUtils;
import com.oraro.mbroadcast.vo.PlayVO;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/10/18.
 */
public class SpeekingService {

    private static SpeekingService speekingService;
    private final PlayAudio playAudio;
    private final TTSInterface tts;
    private long space = 0;
    private List<InterCutData> interCutDate;
    private DataService dataService;
    private List<PlayEntry> playList;
    private Timer timer;
    private PlayEntry firstplay;
    private OnRefreshUIListener onRefreshUIListener;
    private int count = 0;
    private Date endtime;
    private Long id;
    private boolean tag;
    private Date playtime;
    private Timer protect;
    private boolean autiotag = true;
    private int times = 0;
    private int interCount = 0;
    private long beginInter = 0;
    private long endtimeInter = 0;
    private PlayEntry playInte;
    private int immCount;
    private boolean interCut = false;
    private Timer interImmed;
    private boolean waitinit = false;
    private long mspace = 0;
    private boolean fist = false;
    private int ty = 0;
    private List<UrgentItemBean> urgentDate;


    public static SpeekingService getInstance() {
        if (speekingService == null) {
            speekingService = new SpeekingService();
        }
        return speekingService;
    }

    private SpeekingService() {
        dataService = new DataService();
        autiotag = dataService.getAutoPlayStatus();
        interCutDate = dataService.getInterCutData();
        space = dataService.getSpaceStatus();
//        playAlgorithm = new PlayAlgorithmByWeight();
        tts = TTSProXy.getInstance(MBroadcastApplication.getMyContext(), MBroadcastApplication.getMyPackageName());
        playAudio = PlayAudio.getInstance();
        timer = new Timer();
    }

    /**
     * 控制开关自动播报
     *
     * @param autiotag
     */
    public void autioPlay(boolean autiotag) {
        Log.e("huanghui", "autioPlay = " + autiotag);
        this.autiotag = autiotag;
        if (autiotag) {
            if (tts.isSpeeking() || playAudio.isPlaying()) {
                waitinit = true;
            } else {
                init(onRefreshUIListener);
            }
        } else {
            if (playList != null && playList.size() > 0) {
                playList.clear();
            }
            endtime = null;
            timer.cancel();
            if (protect != null) {
                protect.cancel();
            }
        }
    }

    public boolean getWaitinit() {
        return waitinit;
    }

    private void startWaitinit() {
        Log.e("huanghui", " startWaitinit  waitinit = " + waitinit);
        if (waitinit) {
            waitinit = false;
            init(onRefreshUIListener);
        }
    }

    /**
     * 刷新数据
     */
    public void refresh() {
        if (tts.isSpeeking() || playAudio.isPlaying()) {
            Log.e("huanghui", "refresh  isPlaying");
            tag = true;
        } else {
            fresh();
        }
    }

    /**
     * 刷新数据
     */
    public void refresh(boolean needrefresh) {
        if (needrefresh) {
            interCutDate = dataService.getInterCutData();
            space = dataService.getSpaceStatus();
        }
        if (tts.isSpeeking() || playAudio.isPlaying()) {
            Log.e("huanghui", "needrefresh  isPlaying");
            tag = true;
        } else {
            fresh();
        }
    }

    /**
     * 刷新数据
     */
    public void refresh(boolean needrefresh,int ty) {
        this.ty = ty;
        if (needrefresh) {
            interCutDate = dataService.getInterCutData(ty);
            urgentDate = dataService.getUrgentData();
            for(UrgentItemBean u : urgentDate){
                InterCutData interCut = new InterCutData();
                interCut.setText(u.getContent());
                interCut.setTime(u.getContent().length() * 300);
                interCut.setIsPlay(u.getIsSelected());
                interCut.setTy(1);
                interCutDate.add(interCut);
            }
            Log.e("huanghui", "SpeekingService refresh::interCutDate = " + interCutDate);
            space = dataService.getSpaceStatus();
        }else{
            interCutDate = dataService.getInterCutData(ty);
            space = dataService.getSpaceStatus();
        }
        if (tts.isSpeeking() || playAudio.isPlaying()) {
            Log.e("huanghui", "needrefresh  isPlaying");
            tag = true;
        } else {
            fresh();
        }
    }

    /**
     * 删除数据后，刷新数据
     *
     * @param id
     */
    public void delete(long id) {
        if (tts.isSpeeking() || playAudio.isPlaying()) {
            Log.e("huanghui", "refresh  isPlaying");
            tag = true;
        } else {
            fresh();
        }
    }

    private void fresh() {
        Date nowTime = new Date();
        if (tag && playtime != null) {
            nowTime = playtime;
        }
        Log.e("huanghui", "nowTime  " + nowTime);
        Date beginTime = new Date(nowTime.getYear(), nowTime.getMonth(), nowTime.getDate(), nowTime.getHours(), nowTime.getMinutes(), 0);
        playList = dataService.getPlayEntry(beginTime);
        if (null != playList && !playList.isEmpty()) {
            Log.e("huanghui", "fresh  " + playList.get(0).getTime() + "     " + beginTime);
            init(onRefreshUIListener);
        }
        tag = false;
    }


    /**
     * 初始化数据
     *
     * @param onRefreshUIListener
     */
    public void init(OnRefreshUIListener onRefreshUIListener) {
        if (onRefreshUIListener != null) {
            this.onRefreshUIListener = onRefreshUIListener;
        }
        if (!autiotag) {
            Log.e("huanghui", " autiotag");
            if (playList != null && playList.size() > 0) {
                playList.clear();
            }
            endtime = null;
            timer.cancel();
            if (protect != null) {
                protect.cancel();
            }
            return;
        }
        if (protect != null) {
            protect.cancel();
        }

        if (playList == null || playList.size() <= 0) {
            Date nowTime = new Date();
            Date beginTime = new Date(nowTime.getYear(), nowTime.getMonth(), nowTime.getDate(), nowTime.getHours(), nowTime.getMinutes(), 0);
            if (endtime != null) {
                beginTime = endtime;
            }
            playList = dataService.getPlayEntry(beginTime);
            if (playList != null && playList.size() > 0) {
                Log.e("huanghui", "refresh  " + playList.get(0).getTime() + "     " + beginTime);
            }
        }

        if (playList != null && playList.size() > 0) {
            timer.cancel();
            endtime = playList.get(playList.size() - 1).getTime();
            firstplay = playList.get(0);
            Log.e("huanghui", "needplay = " + firstplay);
            play(firstplay.getTime());
//            needPlay = playAlgorithm.fresh(playList);
//            if (needPlay == null) {
//                play(firstplay.getTime());
//                Log.e("huanghui","firstplay "+ firstplay.getTime().toString());
//            } else {
//                play(needPlay.getPe().getTime());
//                Log.e("huanghui","needPlay "+ needPlay.getPe().getTime().toString());
//            }
        } else {
            Log.e("huanghui", "have null data");
//            TimerTask timerTask = new TimerTask() {
//                @Override
//                public void run() {
//                    init(null);
//                }
//            };
//            timer = new Timer();
//            timer.schedule(timerTask, 0, 5000);
        }
    }


    private void play(Date beginTime) {

        long NowTime = new Date().getTime();
        long BeginTime = beginTime.getTime();
        long betweenTime = BeginTime - NowTime;
        if (betweenTime > 0) {
            Log.e("huanghui", "state play ");
            interCut(betweenTime);//插播温馨提示
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    startPlay();
                }
            };
            timer = new Timer();
            timer.schedule(timerTask, beginTime);
        } else {
            Log.e("huanghui", "play");
            startPlay();
        }

    }

    /**
     * 设置播放次数加一
     *
     * @param vo
     */
    private void setPlay(PlayEntry vo) {
        if (vo == null) return;
        PlayEntry pe = vo;
        pe.setDoTimes(pe.getDoTimes() + 1);
        DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
        manager.update(pe, manager.getPlayEntryDao(DBManager.WRITE_ONLY));
//        int value = vo.getWeightValue();
//        if (value > 1000) {
//            int valueForDefault = value % 1000;
//            vo.setWeightValue(valueForDefault);
//        }

    }

    private PlayEntry startPlay() {
        Log.e("huanghui", "startPlay--->"+tts.isSpeeking()+"--->"+playAudio.isPlaying()+"--->"+MBroadcastApplication.getPlayID());
        if(interImmed != null){
            interImmed.cancel();
        }

        MBroadcastApplication.isincout = false;
        MBroadcastApplication.isUrgent = false;
        if ((tts.isSpeeking() || playAudio.isPlaying())) {
            return null;
        }
        if (firstplay.getDoTimes() < firstplay.getTimes()) {
            count = firstplay.getDoTimes();
        } else {
            return null;
        }

        /**
         * 守护定时器，十分钟没有结束，就重新初始化，放置意外错误
         */
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                init(onRefreshUIListener);
            }
        };
        protect = new Timer();
        protect.schedule(timerTask, 600000);

//        if(needPlay == null){
//            needPlay = playAlgorithm.fresh(playList);
//
//        }
//        if(needPlay == null){
//            return null;
//        }

        playList.remove(0);
//        id = needPlay.getPe().getId();
        id = firstplay.getId();

        times = firstplay.getTimes() - 1;
        if (times < 0) {
            init(onRefreshUIListener);
            return null;
        }
        playtime = firstplay.getTime();
        if (onRefreshUIListener != null) {
            try {
                onRefreshUIListener.frushPlaying(id);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        if (firstplay.getFileParentPath() != null) {
            //执行Media逻辑
            playAudio.setPath(firstplay.getFileParentPath());
            playAudio.startAudio(onCompletionListener);

            minaStringAudio();

        } else {
            //执行TTS逻辑
            tts.TTSStartPlay(firstplay.getTextDesc(), null, null, null, null, null, null, null, synthesizerListener);

            minaStringTTS();

            return firstplay;
        }
        return null;
    }

    /**
     * mediaPlayer播报回调
     */
    MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            setPlay(firstplay);
            regainWait();
            /**
             * 关闭自动播放
             */
            if (!autiotag) {
                timer.cancel();
                protect.cancel();
                if (onRefreshUIListener != null) {
                    try {
                        onRefreshUIListener.completed(id, null);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    onRefreshUIListener = null;
                }
                return;
            }
            Log.e("huanghui", "MediaPlayer onCompleted" + count + "  times = " + times);
            if (count >= times) {
                timer.cancel();
                protect.cancel();
                if (tag) {
                    fresh();
                } else {
                    init(onRefreshUIListener);
                }
                count = 0;
                if (onRefreshUIListener != null) {
                    try {
                        onRefreshUIListener.completed(id, null);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    onRefreshUIListener = null;
                }
            } else {


                /**
                 * 播放次数小于需要播放次数继续播放
                 */
                playAudio.startAudio(onCompletionListener);
                minaStringAudio();
                count++;
            }

        }
    };

    /**
     * TTS播报回调
     */
    SynthesizerListener synthesizerListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {

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
            setPlay(firstplay);
            regainWait();
            /**
             * 关闭自动播放
             */
            if (!autiotag) {
                timer.cancel();
                protect.cancel();
                if (onRefreshUIListener != null) {
                    try {
                        if (speechError == null) {
                            onRefreshUIListener.completed(id, null);
                        } else {
                            onRefreshUIListener.completed(id, speechError.toString());

                        }
                        onRefreshUIListener = null;
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                return;
            }
            Log.e("huanghui", "TTS onCompleted" + count + "  times = " + times);
            if (count >= times) {
                timer.cancel();
                protect.cancel();
                if (tag) {
                    fresh();
                } else {
                    init(onRefreshUIListener);
                }
                count = 0;
                if (onRefreshUIListener != null) {
                    try {
                        if (speechError == null) {
                            onRefreshUIListener.completed(id, null);
                        } else {
                            onRefreshUIListener.completed(id, speechError.toString());
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            } else {

                /**
                 * 播放次数小于需要播放次数继续播放
                 */
                tts.TTSStartPlay(firstplay.getTextDesc(), null, null, null, null, null, null, null, synthesizerListener);
                minaStringTTS();
                count++;
            }
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };






    /*               手动播放温馨提示逻辑 start                                                  */

    public boolean getInterCut() {
        return interCut;
    }

    private void startPalyImmed() {
        Log.e("huanghui", " startPalyImmed  interCut = " + interCut);
        if (interCut) {
            interCut = false;
            interCount = 0;
            palyImmed();
        }
    }


    public void startInterCut(int immCount,long mspace) {
        Log.e("huanghui", "startInterCut mspace = "+mspace);
        fist = true;
        if(mspace >= 0){
            this.mspace = mspace;
        }else{
            this.mspace = space;
        }

        if (immCount > 0) {
            this.immCount = immCount;
            if (tts.isSpeeking() || playAudio.isPlaying()) {
                Log.e("huanghui", "isSpeeking");
                interCut = true;
            } else {
                interCount = 0;
                if (null != interImmed) {
                    interImmed.cancel();
                }
                palyImmed();
            }

        } else {
            this.immCount = 0;
            return;
        }
    }

    private void palyImmed() {
        if (autiotag) {
            return;
        }
        Log.e("huanghui", " playImmediately  interCount = " + interCount + "   interCutDate.size() = " + interCutDate.size() + "   immCount = " + immCount);
        boolean endcirculation = true;
        if (interCutDate == null || interCutDate.size() <= 0) {
            interCount = 0;
            immCount = 0;
            return;
        } else if (interCutDate.size() <= interCount) {
            immCount--;
            interCount = 0;//计数出错归零/播完归零
            endcirculation = false;
        }
        if (immCount <= 0) {
            return;
        }
        final InterCutData needPlayInterCut = interCutDate.get(interCount);

        Log.e("huanghui", "needPlayInterCut = " + needPlayInterCut +"  endcirculation = "+endcirculation);
        if(fist){
            playImmediately(needPlayInterCut);
            Log.e("huanghui", "fist palyImmed" );
        }else if(endcirculation){
            //一轮温馨提示没有结束
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    playImmediately(needPlayInterCut);
                }
            };
            if(interImmed != null){
                interImmed.cancel();
                interImmed = null;
            }
            interImmed = new Timer();
            interImmed.schedule(timerTask, space);
            Log.e("huanghui", "space palyImmed = "+space );
        }else{
            //一轮温馨提示结束
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    playImmediately(needPlayInterCut);
                }
            };
            if(interImmed != null){
                interImmed.cancel();
                interImmed = null;
            }
            interImmed = new Timer();
            interImmed.schedule(timerTask, mspace);
            Log.e("huanghui", "mspace palyImmed = "+mspace );
        }

    }

    private void playImmediately(InterCutData needPlayInterCut) {
        Log.e("huanghui", "start playImmediately ");
        fist = false;
        if (tts.isSpeeking() || playAudio.isPlaying()) {
            palyImmed();
            return;
        }
        if(ty == 0){
            MBroadcastApplication.isincout = true;
            MBroadcastApplication.isUrgent = false;
        }else{
            MBroadcastApplication.isincout = false;
            MBroadcastApplication.isUrgent = true;
        }

        tts.TTSStartPlay(needPlayInterCut.getText(), null, null, null, null, null, null, null, new SynthesizerListener() {
            @Override
            public void onSpeakBegin() {

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
                MBroadcastApplication.isincout = false;
                MBroadcastApplication.isUrgent = false;
                LogUtils.e("huanghui","onCompleted ===  interCut  1======="+interCut);
                if (interCut) {
                    interCount = 0;
                    interCut = false;
                    LogUtils.e("huanghui","onCompleted ===  interCut======="+interCut);
                } else {
                    interCount++;
                }
                regainWait();
                palyImmed();
            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        });
        if (playInte == null) {
            playInte = new PlayEntry();
        }
        playInte.setTextDesc(needPlayInterCut.getText());
        minaStringTTS(new PlayVO(playInte));
    }
    /*                           手动播放温馨提示逻辑 end                                                     */


    /*                         自动播放温馨提示逻辑   start                                 */

    /**
     * 自动播放温馨提示
     *
     * @param betweenTime
     */
    private void interCut(final long betweenTime) {
        if (!autiotag) {
            return;
        }
        Log.e("huanghui", " interCut  interCount = " + interCount + "   interCutDate.size() = " + interCutDate.size());
        if (interCutDate == null || interCutDate.size() <= 0) {
            interCount = 0;
            return;
        } else if (interCutDate.size() <= interCount) {
            interCount = 0;//计数出错归零/播完归零
        }
        final InterCutData needPlayInterCut = interCutDate.get(interCount);
        Log.e("huanghui", " interCut  betweenTime = " + betweenTime);
        Log.e("huanghui", " interCut  needPlayInterCut.getTime() = " + needPlayInterCut.getTime());
        if (betweenTime < (needPlayInterCut.getTime() + space * 2) || betweenTime < 0) {
            interCount = 0;//计数出错归零
            return;
        }
        beginInter = new Date().getTime();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                playInterCut(betweenTime, needPlayInterCut);
            }
        };
        if(interImmed != null){
            interImmed.cancel();
            interImmed = null;
        }
        interImmed = new Timer();
        interImmed.schedule(timerTask, space);
    }

    /**
     * 开始插播
     *
     * @param betweenTime
     * @param needPlayInterCut
     */
    private void playInterCut(final long betweenTime, InterCutData needPlayInterCut) {
        if (tts.isSpeeking() || playAudio.isPlaying()) {
            interCount = 0;//计数出错归零
            return;
        }
        if(ty == 0){
            MBroadcastApplication.isincout = true;
            MBroadcastApplication.isUrgent = false;
        }else{
            MBroadcastApplication.isincout = false;
            MBroadcastApplication.isUrgent = true;
        }
        tts.TTSStartPlay(needPlayInterCut.getText(), null, null, null, null, null, null, null, new SynthesizerListener() {
            @Override
            public void onSpeakBegin() {

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
                MBroadcastApplication.isincout = false;
                MBroadcastApplication.isUrgent = false;
                Log.e("huanghui", " playInterCut  onCompleted tag = " + tag);
                if (tag) {
                    fresh();
                }else{
                    endtimeInter = new Date().getTime();
                    long actualtime = endtimeInter - beginInter;
                    Log.e("huanghui", " actualtime = " + actualtime);
                    interCount++;
                    interCut(betweenTime - actualtime);
                }
                regainWait();
            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        });
        if (playInte == null) {
            playInte = new PlayEntry();
        }
        playInte.setTextDesc(needPlayInterCut.getText());
        minaStringTTS(new PlayVO(playInte));
    }

    /*                      自动播放温馨提示逻辑   end                                   */

    /*             恢复等待队列                         */
    public void regainWait() {
        startWaitinit();
        startPalyImmed();
    }
    /*             恢复等待队列                             */

    /**
     * 开启mina播报
     */
    private void minaStringTTS() {
        Set set = SPUtils.getPrefStringSet(MBroadcastApplication.getMyContext(), "set", null);
        if (null != set) {
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                String ip = (String) iterator.next();
                MinaAutoStringClientThread minaStringClientThread = new MinaAutoStringClientThread();
                minaStringClientThread.setType(Constants.TTS_PLAY);
                minaStringClientThread.setPlayVO(new PlayVO(firstplay));
                minaStringClientThread.setIp(ip);
                MinaStringClientThread.getThreadPoolExecutor().execute(minaStringClientThread);
            }
        }
    }

    /**
     * 开启mina播报
     */
    private void minaStringTTS(PlayVO play) {
        Set set = SPUtils.getPrefStringSet(MBroadcastApplication.getMyContext(), "set", null);
        if (null != set) {
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                String ip = (String) iterator.next();
                MinaAutoStringClientThread minaStringClientThread = new MinaAutoStringClientThread();
                minaStringClientThread.setType(Constants.TTS_PLAY);
                minaStringClientThread.setPlayVO(play);
                minaStringClientThread.setIp(ip);
                MinaStringClientThread.getThreadPoolExecutor().execute(minaStringClientThread);
            }
        }
    }

    /**
     * 开启mina播报
     */
    private void minaStringAudio() {
        Set set = SPUtils.getPrefStringSet(MBroadcastApplication.getMyContext(), "set", null);
        if (null != set) {
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                String ip = (String) iterator.next();
                MinaAutoStringClientThread minaStringClientThread = new MinaAutoStringClientThread();
                minaStringClientThread.setType(Constants.MD_PLAY);
                minaStringClientThread.setPlayVO(new PlayVO(firstplay));
                minaStringClientThread.setIp(ip);
                MinaStringClientThread.getThreadPoolExecutor().execute(minaStringClientThread);
            }
        }

    }

    public void setOnRefreshUIListener(OnRefreshUIListener l) {
        onRefreshUIListener = l;
    }

    public void unRegisterOnRefreshUIListener() {
        onRefreshUIListener = null;
    }

}
