package com.oraro.mbroadcast.logicService;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;
import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.algorithm.PlayAlgorithmByWeight;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.mina.client.MinaStringClientThread;
import com.oraro.mbroadcast.model.PlayEntry;
import com.oraro.mbroadcast.mp.PlayAudio;
import com.oraro.mbroadcast.proxy.TTSProXy;
import com.oraro.mbroadcast.service.OnRefreshUIListener;
import com.oraro.mbroadcast.tts.TTSInterface;
import com.oraro.mbroadcast.utils.SPUtils;
import com.oraro.mbroadcast.vo.PlayAlgorithmVO;
import com.oraro.mbroadcast.vo.PlayVO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wy on 2016/8/24.
 */
public class PlayService {
    /**
     * Debug功能，默认请关闭，否则影响性能
     */
    private final static boolean ISDEBUG = false;
    private static final int FIRST_PLAY_DEVIATION = 30 * 1000;
    private static PlayAlgorithmByWeight playAlgorithm;
    private static PlayService playService;
    private static DataService dataService;
    private static TTSInterface tts;
    private static PlayAudio playAudio;
    private FrushCallBack callBack;
    private PlayAlgorithmVO needPlay;
//    private FrushPlayStatus frushPlayStatus;
    private int returnStep = 0;
    private final int FRUSHTIME = 5000;
    private final int TTSOUTTIMEPLAYING = 12 * 2;//TTS某条播放超过2分钟,认为是网络故障导致的卡顿
    private OnRefreshUIListener onRefreshUIListener;

    private PlayService() {
        playAlgorithm = new PlayAlgorithmByWeight();
        dataService = new DataService();
        tts = TTSProXy.getInstance(MBroadcastApplication.getMyContext(), MBroadcastApplication.getMyPackageName());
        playAudio = PlayAudio.getInstance();
    }

    public static PlayService getInstance() {
        if (playService == null) {
            Log.e("huanghui","playService");
            playService = new PlayService();
        }
        return playService;
    }

    public void delete(long id){
        playAlgorithm.delete(dataService.getPlayEntry(id));
    }

    public void fresh(Date beginTime, Date endTime, OnRefreshUIListener onRefreshUIListener) {
        try {
            Thread.sleep(FRUSHTIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!dataService.getAutoPlayStatus()) {
            Log.e("wyAutoPlay", "AutoPlay is closed!Nothings will be auto play.");
            return;//判断是否允许自动播报
        }

        this.onRefreshUIListener = onRefreshUIListener;
        /**
         * 刷新核心播放队列
         */
        List<PlayEntry> dataList = dataService.getPlayEntry(beginTime, endTime);
//        if(ISDEBUG){
//            Log.e("wy","dataList.beginTime = " +beginTime);
//            Log.e("wy","dataList.endTime = " +endTime);
//            Log.e("wy","dataList.size() = " +dataList.size());
//
//        }
        PlayAlgorithmVO vo = playAlgorithm.fresh(dataList);
        needPlay = vo;
        // fix bug  begin by wy , is not playing when after network error do again connected
        if (returnStep > TTSOUTTIMEPLAYING) {
            tts.stopPlaying();
            Log.e("wyerror", "TTS网络故障,被强制重置。");
        }
        /**
         * 如果正在播报，则等待
         */
        if (tts.isSpeeking() || playAudio.isPlaying()) {
            returnStep++;
            return;
        }
        returnStep = 0;

        //fix bug end by wy   , is not playing when after network error do again connected
        // Log.e("wy","playService --》fresh");
        if (tts.isSpeeking() || playAudio.isPlaying()) {
            return;
        }


//        if(ISDEBUG){
//            StringBuffer sb = new StringBuffer();
//            for (PlayEntry pe: playCacheList){
//                sb.append(pe.getTime()+",");
//            }
//            Log.e("wy",""+sb.toString());
//
//        }

        PlayAlgorithmVO playAlgorithmVO = play();
        setPlay(playAlgorithmVO);
//        List<PlayVO> voList = dataService.getPlayVO(beginTime, endTime);
//        List<PlayVO> rspList = getFlag(voList,playCacheList,playEntry);
        needPlay = null;

//        if(callBack != null ){
//        //    callBack.frush(rspList);
//        }else {
//        }

    }

    /**
     * 设置数据刷新监听
     *
     * @param callBack
     */
    public void setFrushCallBack(FrushCallBack callBack) {


        this.callBack = callBack;
    }

    /**
     * 标志在队列的，标志正在播放的
     *
     * @param voList     待标志集合
     * @param entityList 在播放列表的对象
     * @param playEntry  正在播放的对象
     * @return 被标志集合
     */
    private List<PlayVO> getFlag(List<PlayVO> voList, List<PlayEntry> entityList, PlayEntry playEntry) {
        if (voList == null) return new ArrayList<PlayVO>();
        if (entityList == null || entityList.size() == 0) return voList;
        for (PlayVO vo : voList) {
            for (PlayEntry pe : entityList) {
                if (pe.getId().equals(vo.getEntity().getId())) {
                    vo.setIsInQueue(true);
                }
            }
            if (playEntry != null && playEntry.getId().equals(vo.getEntity().getId())) {
                vo.setIsPlayNow(true);
            }

        }
        return voList;
    }

    /**
     * 设置播放次数加一
     *
     * @param vo
     */
    private void setPlay(PlayAlgorithmVO vo) {
        if (vo == null) return;
        PlayEntry pe = vo.getPe();
        pe.setDoTimes(pe.getDoTimes() + 1);
        DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
        manager.update(pe, manager.getPlayEntryDao(DBManager.WRITE_ONLY));
        int value = vo.getWeightValue();
        if (value > 1000) {
            int valueForDefault = value % 1000;
            vo.setWeightValue(valueForDefault);
        }

    }

    private PlayAlgorithmVO play() {
//        Log.e("wyplay","coming");
//        if (tts.isSpeeking() && playAudio.isPlaying()) return null;
//        if (playCacheList == null) return null;
//        if (playCacheList.size() == 0) return null;
//        Log.e("wyplay","cross return");
//        needPlay = null;
//
//        /**
//         * 寻找未播放过得文件
//         */
//
//        boolean roundFlag = false;//判读是否回队首
//        for (int i = 0; i < playCacheList.size(); i++) {
//            PlayEntry pe = playCacheList.get(i);
//            if (roundFlag) {
//                Log.e("wyplay","roundFlag ==true---->1");
//                if (checkCanPlay(pe) == 1) {
//                    Log.e("wyplay","roundFlag ==true ----> checkCanPlay  == 1------->2");
//                    needPlay = pe;
//                    break;
//                }
//            }
//
//            /**
//             * 首先找到第一个从未播放过的，判断如果他可以播放，则播放该对象，如果他不可以播放，重置指针，重新开始轮询
//             */
//            if (pe.getDoTimes() == 0 && !roundFlag) {
//                //needPlay = pe;
//
////                if(frushPlayStatus != null){//通知主界面当然播放对象id
////                    try {
////                        frushPlayStatus.frushPlaying(pe.getId());
////                    } catch (RemoteException e) {
////                        e.printStackTrace();
////                    }
////                }
//
//                if (checkCanPlay(pe) != 0) {
//                    Log.e("wyplay","checkCanPlay(pe) != 0------->3    "+checkCanPlay(pe));
//                    Log.e("wyplay","checkCanPlay(pe) != 0------->3");
//                    roundFlag = true;
//                    i = -1;
//                } else {
//                    Log.e("wyplay","checkCanPlay(pe) == 0-------->4");
//                    needPlay = pe;
//                    break;
//                }
//
//            }
//
//            if (i == playCacheList.size() - 1 && !roundFlag) {//如果轮询到最后一个，还没有找到首次播放的
//                Log.e("wyplay","i == playCacheList.size() - 1 && !roundFlag ---->5");
//                i = -1;
//                roundFlag = true;
//            }
//
//
//        }

        if (needPlay != null) {
            Log.e("wyplay", "needPlay != null");
        } else {
            Log.e("wyplay", "needPlay == null");
        }

        if (needPlay != null && !tts.isSpeeking() && !playAudio.isPlaying()) {
            Log.e("wyplay", "playing");

//            if (frushPlayStatus != null) {//通知主界面当然播放对象id
//                try {
//                    Log.e("wyaidl", "frushPlayStatus != null");
//                    frushPlayStatus.frushPlaying(needPlay.getPe().getId());
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                Log.e("wyaidl", "frushPlayStatus == null");
//            }
            final long id = needPlay.getPe().getId();
            if(onRefreshUIListener != null){
                try {
                    onRefreshUIListener.frushPlaying(id);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            if (needPlay.getPe().getFileParentPath() != null) {
                //执行Media逻辑
                playAudio.setPath(needPlay.getPe().getFileParentPath());
                playAudio.startAudio(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if(onRefreshUIListener != null){
                            try {
                                onRefreshUIListener.completed(id,null);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                MinaStringClientThread minaStringClientThread = new MinaStringClientThread();
                MinaStringClientThread minaStringClientThread1 = new MinaStringClientThread();
                MinaStringClientThread minaStringClientThread2 = new MinaStringClientThread();
                MinaStringClientThread minaStringClientThread3 = new MinaStringClientThread();

                minaStringClientThread.setType(Constants.MD_PLAY);
                minaStringClientThread1.setType(Constants.MD_PLAY);
                minaStringClientThread2.setType(Constants.MD_PLAY);
                minaStringClientThread3.setType(Constants.MD_PLAY);
                minaStringClientThread.setPlayVO(new PlayVO(needPlay.getPe()));
                minaStringClientThread1.setPlayVO(new PlayVO(needPlay.getPe()));
                minaStringClientThread2.setPlayVO(new PlayVO(needPlay.getPe()));
                minaStringClientThread3.setPlayVO(new PlayVO(needPlay.getPe()));
                if(SPUtils.hasKey(MBroadcastApplication.getMyContext(),"ip1")){
                    Log.e("BaseBroadcastFragment", " ip1 = "+SPUtils.getPrefString(MBroadcastApplication.getMyContext(),"ip1","-1"));
                    if (!SPUtils.getPrefString(MBroadcastApplication.getMyContext(),"ip1","-1").equals("-1")){
                        minaStringClientThread.setIp(SPUtils.getPrefString(MBroadcastApplication.getMyContext(),"ip1","-1"));
                        MinaStringClientThread.getThreadPoolExecutor().execute(minaStringClientThread);
                    }
                }
                if(SPUtils.hasKey(MBroadcastApplication.getMyContext(),"ip2")){
                    Log.e("BaseBroadcastFragment", " ip2 = "+SPUtils.getPrefString(MBroadcastApplication.getMyContext(),"ip2","-1"));
                    if (!SPUtils.getPrefString(MBroadcastApplication.getMyContext(),"ip2","-1").equals("-1")){
                        minaStringClientThread.setIp(SPUtils.getPrefString(MBroadcastApplication.getMyContext(),"ip2","-1"));
                        MinaStringClientThread.getThreadPoolExecutor().execute(minaStringClientThread1);
                    }
                }
                if(SPUtils.hasKey(MBroadcastApplication.getMyContext(),"ip3")){
                    Log.e("BaseBroadcastFragment", " ip3 = "+SPUtils.getPrefString(MBroadcastApplication.getMyContext(),"ip3","-1"));
                    if (!SPUtils.getPrefString(MBroadcastApplication.getMyContext(),"ip3","-1").equals("-1")){
                        minaStringClientThread.setIp(SPUtils.getPrefString(MBroadcastApplication.getMyContext(),"ip3","-1"));
                        MinaStringClientThread.getThreadPoolExecutor().execute(minaStringClientThread2);
                    }
                }
                if(SPUtils.hasKey(MBroadcastApplication.getMyContext(),"ip4")){
                    Log.e("BaseBroadcastFragment", " ip4 = "+SPUtils.getPrefString(MBroadcastApplication.getMyContext(),"ip4","-1"));
                    if (!SPUtils.getPrefString(MBroadcastApplication.getMyContext(),"ip4","-1").equals("-1")){
                        minaStringClientThread.setIp(SPUtils.getPrefString(MBroadcastApplication.getMyContext(),"ip4","-1"));
                        MinaStringClientThread.getThreadPoolExecutor().execute(minaStringClientThread3);
                    }
                }
            } else {
                //执行TTS逻辑
                tts.TTSStartPlay(needPlay.getPe().getTextDesc(), null, null, null, null, null, null, null, new SynthesizerListener() {
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
                        if(onRefreshUIListener != null){
                            try {
                                if(speechError == null)
                                    onRefreshUIListener.completed(id,null);
                                else
                                    onRefreshUIListener.completed(id,speechError.toString());
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onEvent(int i, int i1, int i2, Bundle bundle) {

                    }
                });
                MinaStringClientThread minaStringClientThread = new MinaStringClientThread();
                MinaStringClientThread minaStringClientThread1 = new MinaStringClientThread();
                MinaStringClientThread minaStringClientThread2 = new MinaStringClientThread();
                MinaStringClientThread minaStringClientThread3 = new MinaStringClientThread();

                minaStringClientThread.setType(Constants.TTS_PLAY);
                minaStringClientThread1.setType(Constants.TTS_PLAY);
                minaStringClientThread2.setType(Constants.TTS_PLAY);
                minaStringClientThread3.setType(Constants.TTS_PLAY);
                minaStringClientThread.setPlayVO(new PlayVO(needPlay.getPe()));
                minaStringClientThread1.setPlayVO(new PlayVO(needPlay.getPe()));
                minaStringClientThread2.setPlayVO(new PlayVO(needPlay.getPe()));
                minaStringClientThread3.setPlayVO(new PlayVO(needPlay.getPe()));
                if(SPUtils.hasKey(MBroadcastApplication.getMyContext(),"ip1")){
                    Log.e("BaseBroadcastFragment", " ip1 = "+SPUtils.getPrefString(MBroadcastApplication.getMyContext(),"ip1","-1"));
                    if (!SPUtils.getPrefString(MBroadcastApplication.getMyContext(),"ip1","-1").equals("-1")){
                        minaStringClientThread.setIp(SPUtils.getPrefString(MBroadcastApplication.getMyContext(),"ip1","-1"));
                        MinaStringClientThread.getThreadPoolExecutor().execute(minaStringClientThread);
                    }
                }
                if(SPUtils.hasKey(MBroadcastApplication.getMyContext(),"ip2")){
                    Log.e("BaseBroadcastFragment", " ip2 = "+SPUtils.getPrefString(MBroadcastApplication.getMyContext(),"ip2","-1"));
                    if (!SPUtils.getPrefString(MBroadcastApplication.getMyContext(),"ip2","-1").equals("-1")){
                        minaStringClientThread.setIp(SPUtils.getPrefString(MBroadcastApplication.getMyContext(),"ip2","-1"));
                        MinaStringClientThread.getThreadPoolExecutor().execute(minaStringClientThread1);
                    }
                }
                if(SPUtils.hasKey(MBroadcastApplication.getMyContext(),"ip3")){
                    Log.e("BaseBroadcastFragment", " ip3 = "+SPUtils.getPrefString(MBroadcastApplication.getMyContext(),"ip3","-1"));
                    if (!SPUtils.getPrefString(MBroadcastApplication.getMyContext(),"ip3","-1").equals("-1")){
                        minaStringClientThread.setIp(SPUtils.getPrefString(MBroadcastApplication.getMyContext(),"ip3","-1"));
                        MinaStringClientThread.getThreadPoolExecutor().execute(minaStringClientThread2);
                    }
                }
                if(SPUtils.hasKey(MBroadcastApplication.getMyContext(),"ip4")){
                    Log.e("BaseBroadcastFragment", " ip4 = "+SPUtils.getPrefString(MBroadcastApplication.getMyContext(),"ip4","-1"));
                    if (!SPUtils.getPrefString(MBroadcastApplication.getMyContext(),"ip4","-1").equals("-1")){
                        minaStringClientThread.setIp(SPUtils.getPrefString(MBroadcastApplication.getMyContext(),"ip4","-1"));
                        MinaStringClientThread.getThreadPoolExecutor().execute(minaStringClientThread3);
                    }
                }
            }
            return needPlay;
        }
        return null;
    }

    /**
     * 判断是否可以播放
     *
     * @param pe 待判断对象
     * @return -1、传入值为空，即不存在未播放过的文件
     * 0、第一次播放且满足播放条件
     * 1、非第一次播放且满足播放条件
     * 2、不满足播放条件
     */
    private int checkCanPlay(PlayEntry pe) {
        if (pe == null) return -1;
        Long playTime = pe.getTime().getTime();
        Long MaxTime = new Date().getTime() + FIRST_PLAY_DEVIATION;
        if (pe.getDoTimes() == 0 && playTime < MaxTime) return 0;
        else if (playTime < MaxTime) return 1;
        else return 2;
    }

    public interface FrushCallBack {
        void frush(List<PlayVO> rspList);
    }

}
