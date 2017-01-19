package com.oraro.mbroadcast.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.model.PlayEntry;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.model.TabInfo;
import com.oraro.mbroadcast.service.OnRefreshUIListener;
import com.oraro.mbroadcast.utils.DataUtils;
import com.oraro.mbroadcast.utils.DateUtils;
import com.oraro.mbroadcast.utils.DebugUtil;
import com.oraro.mbroadcast.utils.LogUtils;
import com.oraro.mbroadcast.utils.SPUtils;
import com.oraro.mbroadcast.vo.PlayVO;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

public class WeekFragment extends BaseBroadcastFragment {
    private final static String TAG = WeekFragment.class.getSimpleName();
    private int mCatalog;
    private DBManager dbManager = DBManager.getInstance(mContext);

    public WeekFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCatalog = getArguments().getInt(TabInfo.BUNDLE_TYPE, 0);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        weekIndex = mCatalog + 1;
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public boolean isReset() {
        return SPUtils.hasKey(mContext, "isAdd") && SPUtils.getPrefBoolean(mContext, "isAdd", false);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

   @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(SimpleEvent event) {/* Do something */
        String msg = "onEventMainThread收到了消息：" + event.getMsg();
        if (event.getMsg() == (mCatalog + 1)) {
            listView.setVisibility(View.GONE);
            fragment_temporary_rl0.setVisibility(View.GONE);
            myRefreshListView.setVisibility(View.GONE);
            fragment_temporary_rl1.setVisibility(View.VISIBLE);
        }  else if (event.getMsg() == Constants.UPDATE_PLAYVO_ONE) {
            weekBroadcastAdapter.updataView(mViewPostion,listView,mPlayVOId);
        } else if (event.getMsg() == Constants.FLIGHT_EXCEL_ADD) {

            if(getNowdayWeekIndex() == 0){
                if (iMyAidlInterface != null) {
                    try {
                        iMyAidlInterface.refresh();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
            index=-999999999;
            flightInfoTemps = getAllData(getNowdayWeekIndex());
            weekBroadcastAdapter.setPlayVOData(flightInfoTemps);
            weekBroadcastAdapter.notifyDataSetChanged();
        }else if (event.getMsg()==Constants.Analytic_Cmpletion_Delay){
            Handler handler1 = new Handler();
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    flightInfoTemps.clear();
                    flightInfoTemps = getAllData(getNowdayWeekIndex());
                    weekBroadcastAdapter.setPlayVOData(flightInfoTemps);
                    index = -999999999;
                    weekBroadcastAdapter.notifyDataSetChanged();
                    int mindex = DataUtils.getNowPostion(flightInfoTemps);
                    if (mindex == -1) {
                        listView.setSelection(flightInfoTemps.size());
                    } else {
                        listView.setSelection(mindex);
                    }
                }
            }, 0);
        }

        else if (event.getMsg() == Constants.Analytic_Cmpletion_Notice) {
            if(!getUserVisibleHint()){
                return;
            }
            String msg2 = "onEventMainThread2收到了消息：" + event.getMsg();
            Handler handler1 = new Handler();
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    flightInfoTemps.clear();
                    flightInfoTemps = getAllData(getNowdayWeekIndex());
                    weekBroadcastAdapter.setPlayVOData(flightInfoTemps);
                    index = -999999999;
                    weekBroadcastAdapter.notifyDataSetChanged();
                    int mindex = DataUtils.getNowPostion(flightInfoTemps);
                    if (mindex == -1) {
                        listView.setSelection(flightInfoTemps.size());
                    } else {
                        listView.setSelection(mindex);
                    }
                }
            }, 3000);
        }
        else if(event.getMsg() == Constants.A_PLAY){
//            Log.e("WeekFragment ", "onEvent" + event.getMsg()+"    mPlayVOId = "+mPlayVOId);
//            weekBroadcastAdapter.isIconPlaying(mPlayVOId);
            weekBroadcastAdapter.isIconPlaying(MBroadcastApplication.getPlayID());
        } else if(event.getMsg() == Constants.A_PLAYED){
//            weekBroadcastAdapter.setAdapterNotify();
            weekBroadcastAdapter.setAdapterNotify();
        }else if(event.getMsg() == Constants.SETTINGS_WEEK){
            refreshUI();
        }else if (event.getMsg() == Constants.URGENT_BROADCAST_INSTANT) {
            Log.e("WeekFragment",mCatalog+"");
            if(mCatalog == 0){

                Log.e("WeekFragment ", "onEvent" + event.getMsg());
                Log.e("WeekFragment ", "id " + Constants.URGENT_BROADCAST_INSTANT_id);
                PlayEntry playEntry = (PlayEntry) dbManager.queryById(Constants.URGENT_BROADCAST_INSTANT_id, dbManager.getPlayEntryDao(DBManager.READ_ONLY));
                if (null != playEntry) {
                    PlayVO playVO = new PlayVO(playEntry);
                    Long id = playVO.getEntity().getId();
                    Log.e("wk","1111111111111");
                    if (playVO.getEntity().getFileParentPath() != null) {
                        Log.e("wk","222222222222222222222");
    //                    if (tts.isSpeeking()) {
    //                        tts.TTSPausePlay();
    //                    }
    //                    //执行Media逻辑
    //                    playAudio.setPath(playVO.getEntity().getFileParentPath());
    //                    playAudio.startAudio();
                        if (iMyAidlInterface != null) {
                            try {
                                Log.e("WeekFragment ", "isJinJiPlay media " + DebugUtil.isJinJiPlay);
                                iMyAidlInterface.startMediaPlay(playVO.getEntity().getId(), playVO.getEntity().getFileParentPath(), playVO.getEntity().getDoTimes(), new OnRefreshUIListener.Stub() {
                                    @Override
                                    public void completed(long id, String error) throws RemoteException {
                                        Log.e("huanghui", "completed = " + id);
                                        Log.e("WeekFragment", "error==meida = " + error);
                                        MBroadcastApplication.setPlayID((long)-1);
                                        if(DebugUtil.isJinJiPlay){
                                            DebugUtil.isJinJiPlay = false;
                                        }
                                        Log.e("WeekFragment ", "isJinJiPlay media completed " + DebugUtil.isJinJiPlay);
                                    }

                                    @Override
                                    public void frushPlaying(long id) throws RemoteException {
                                        Log.e("huanghui", "frushPlaying = " + id);
                                        MBroadcastApplication.setPlayID(id);
                                        DebugUtil.isJinJiPlay = true;
                                    }
                                });
                            } catch (RemoteException e) {
                                MBroadcastApplication.setPlayID((long)-1);
                                e.printStackTrace();
                            }
                        }

                    } else {
                        Log.e("wk","333333333333333333333");
    //                    if (playAudio.isPlaying()) {
    //                        playAudio.pauseAudio();
    //                    }
    //                    //执行TTS逻辑
    //                    tts.TTSStartPlay(playVO.getEntity().getTextDesc(), null, null, null, null, null, null, null);
                        Log.e("wk", "iMyAidlInterface =  " + iMyAidlInterface);
                        if (iMyAidlInterface != null) {
                            try {
                                Log.e("wk ", "isJinJiPlay tts " + DebugUtil.isJinJiPlay);

                                iMyAidlInterface.startTTSPlay(playVO.getEntity().getId(), playVO.getEntity().getTextDesc(), playVO.getEntity().getTimes(), new OnRefreshUIListener.Stub() {
                                    @Override
                                    public void completed(long id, String error) throws RemoteException {
                                        Log.e("huanghui", "completed = " + id);
                                        Log.e("WeekFragment", "error==tts = " + error);
                                        MBroadcastApplication.setPlayID((long)-1);
                                        if(DebugUtil.isJinJiPlay){
                                            DebugUtil.isJinJiPlay = false;
                                        }
                                        Log.e("WeekFragment ", "isJinJiPlay tts completed " + DebugUtil.isJinJiPlay);
                                    }

                                    @Override
                                    public void frushPlaying(long id) throws RemoteException {
                                        Log.e("huanghui", "frushPlaying = " + id);
                                        MBroadcastApplication.setPlayID(id);
                                        DebugUtil.isJinJiPlay = true;
                                    }
                                });
                            } catch (RemoteException e) {
                                MBroadcastApplication.setPlayID((long)-1);
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

    }

    @Override
    public int getNowdayWeekIndex() {
        Date nowDate = new Date();
        if (DateUtils.getWeek(nowDate) == Constants.MONDAY) {
            return mCatalog;
        } else if (DateUtils.getWeek(nowDate) == Constants.TUESDAY) {
            return mCatalog - 1;
        } else if (DateUtils.getWeek(nowDate) == Constants.WENDESDAY) {
            return mCatalog - 2;
        } else if (DateUtils.getWeek(nowDate) == Constants.THURSDAY) {
            return mCatalog - 3;
        } else if (DateUtils.getWeek(nowDate) == Constants.FRIDAY) {
            return mCatalog - 4;
        } else if (DateUtils.getWeek(nowDate) == Constants.SATURDAY) {
            return mCatalog - 5;
        } else if (DateUtils.getWeek(nowDate) == Constants.SUNDAY) {
            return mCatalog - 6;
        }
        return -100;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().removeStickyEvent(this);
//        try {
//            if (null != iMyAidlInterface)
//                iMyAidlInterface.unRegisterOnRefreshUIListener();
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void play(PlayVO playVO,int postion) {
        super.play(playVO,postion);
        EventBus.getDefault().post(new SimpleEvent(333));
    }
}
