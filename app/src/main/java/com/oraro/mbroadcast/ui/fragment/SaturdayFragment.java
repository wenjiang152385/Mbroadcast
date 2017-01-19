package com.oraro.mbroadcast.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.utils.DataUtils;
import com.oraro.mbroadcast.utils.DateUtils;
import com.oraro.mbroadcast.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;

/**
 * Created by dongyu on 2016/8/19 0019.
 */
public class SaturdayFragment extends BaseBroadcastFragment {
    private final static  String TAG=SaturdayFragment.class.getSimpleName();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        weekIndex=6;
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    @Subscribe(sticky = true)
    public void onEvent(SimpleEvent event) {/* Do something */
        if (event.getMsg() == Constants.SATURDAY) {
            listView.setVisibility(View.GONE);
            fragment_temporary_rl0.setVisibility(View.GONE);
            myRefreshListView.setVisibility(View.GONE);
            fragment_temporary_rl1.setVisibility(View.VISIBLE);
        }else if(event.getMsg()==Constants.Analytic_Cmpletion_Notice){
            String msg = "onEventMainThread收到了消息：" + event.getMsg();
            Log.e(TAG, msg + "  mPlayVOId  = " + mPlayVOId);
            weekBroadcastAdapter.updataView(mViewPostion,listView,mPlayVOId);
            Handler handler6 = new Handler();
            handler6.postDelayed(new Runnable() {
                @Override
                public void run() {
                    flightInfoTemps.clear();
                    flightInfoTemps = getAllData(getNowdayWeekIndex());
                    weekBroadcastAdapter.setPlayVOData(flightInfoTemps);
                    index=-999999999;
                    weekBroadcastAdapter.notifyDataSetChanged();
                    if(DataUtils.getNowPostion(flightInfoTemps)==-1){
                        listView.setSelection(flightInfoTemps.size());
                    }else {
                        listView.setSelection(DataUtils.getNowPostion(flightInfoTemps));
                    }
                }
            }, 3000);
        }
    }
    @Override
    public boolean isReset() {
        return  SPUtils.hasKey(mContext,"isAdd")&&SPUtils.getPrefBoolean(mContext,"isAdd",false);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public int getNowdayWeekIndex() {
        Date nowDate=new Date();
        if (DateUtils.getWeek(nowDate)==Constants.MONDAY){
            return 5;
        }else if(DateUtils.getWeek(nowDate)==Constants.TUESDAY){
            return 4;
        }else if(DateUtils.getWeek(nowDate)==Constants.WENDESDAY){
            return 3;
        }else if(DateUtils.getWeek(nowDate)==Constants.THURSDAY){
            return 2;
        }else if(DateUtils.getWeek(nowDate)==Constants.FRIDAY){
            return 1;
        }else if(DateUtils.getWeek(nowDate)==Constants.SATURDAY){
            return 0;
        }else if(DateUtils.getWeek(nowDate)==Constants.SUNDAY){
            return -1;
        }

        return -100;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().removeStickyEvent(this);
    }
}
