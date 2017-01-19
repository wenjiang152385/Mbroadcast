package com.oraro.mbroadcast.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.model.FlightInfoTemp;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.utils.DateUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;

/**
 * Created by dongyu on 2016/8/23 0023.
 */
public class TodayFragment extends BaseFlightFragment {
    private final static String TAG=TodayFragment.class.getSimpleName();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Subscribe()
    public void onEventMainThread(SimpleEvent event) {
        if (event.getMsg() == Constants.UPDATE_FLIGTH) {
            String msg = "onEventMainThread收到了消息：" + event.getMsg();
            Log.e(TAG, msg);
            Log.e(TAG, msg + "  Flighttempid   = " + Constants.FLIGHTTEMP_ID);
            FlightInfoTemp flightInfoTemp = (FlightInfoTemp) DBManager.getInstance(mContext).queryById(Constants.FLIGHTTEMP_ID, DBManager.getInstance(mContext).getFlightInfoTempDao(DBManager.READ_ONLY));
            Date nowDate=new Date();
            if(DateUtils.daysBetween(nowDate,flightInfoTemp.getDate())==0){
                flightInfoTemps.add(flightInfoTemp);
                weekFlightAdapter.setFlightInfoTempData(flightInfoTemps);
                listView.setSelection(flightInfoTemps.size());
                Constants.FLIGHTTEMP_ID=0;
            }
        }
//        else if (event.getMsg() == Constants.Analytic_Cmpletion_Notice){
//            String msg = "onEventMainThread收到了消息：" + event.getMsg();
//            Log.e(TAG, msg);
//            weekFlightAdapter.updataView(mViewPostion,listView,flightTempId);
//        }
        else if (event.getMsg() == Constants.FLIGHT_EXCEL_ADD){
            String msg = "onEventMainThread收到了消息：" + event.getMsg();
            Log.e(TAG, msg);
            index=0;
            flightInfoTemps = getData(0, 0);
            weekFlightAdapter.setFlightInfoTempData(flightInfoTemps);
            weekFlightAdapter.notifyDataSetChanged();
        }

    }


    @Override
    public int getDayIndex() {
        return 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
