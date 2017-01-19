package com.oraro.mbroadcast.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.model.FlightInfoTemp;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.model.TabInfo;
import com.oraro.mbroadcast.utils.DataUtils;
import com.oraro.mbroadcast.utils.DateUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.List;

public class DayFragment extends BaseFlightFragment {
    private final static String TAG=DayFragment.class.getSimpleName();
    private int mCatalog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCatalog = getArguments().getInt(TabInfo.BUNDLE_TYPE,0);
    }

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
            //FlightInfoTemp flightInfoTemp = (FlightInfoTemp) DBManager.getInstance(mContext).queryById(Constants.FLIGHTTEMP_ID, DBManager.getInstance(mContext).getFlightInfoTempDao(DBManager.READ_ONLY));
            List<FlightInfoTemp> flightInfoTemp = getData(getDayIndex());
            Date nowDate=new Date();
            if(flightInfoTemp != null && flightInfoTemp.size()> 0){
                if(DateUtils.daysBetween(nowDate,flightInfoTemp.get(0).getDate())==mCatalog){
                    flightInfoTemps = flightInfoTemp;
                    weekFlightAdapter.setFlightInfoTempData(flightInfoTemps);
                    weekFlightAdapter.notifyDataSetChanged();
                    int selection = DataUtils.getFlightNowPostion(flightInfoTemps);
                    Log.e("huanghi","selection = "+selection);
                    if(selection == -1){
                        listView.setSelection(flightInfoTemp.size());
                    }else {
                        listView.setSelection(selection);
                    }
                }
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
            flightInfoTemps = getData(mCatalog, 0);
            weekFlightAdapter.setFlightInfoTempData(flightInfoTemps);
            weekFlightAdapter.notifyDataSetChanged();
        }

    }


    @Override
    public int getDayIndex() {
        return mCatalog;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
