package com.oraro.mbroadcast.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.listener.IOnTouchListener;
import com.oraro.mbroadcast.listener.ISearchBarCallback;
import com.oraro.mbroadcast.model.FlightInfoTemp;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.model.TabInfo;
import com.oraro.mbroadcast.ui.activity.FlightInfoActivity;
import com.oraro.mbroadcast.ui.widget.AddPopWindow;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by dongyu on 2016/8/23 0023.
 */
public class FlightFragment extends BaseParentFragment {

    public static final int FRAGMENT_MONDAY = 0;
    public static final int FRAGMENT_TUESDAY = 1;
    public static final int FRAGMENT_WENDNESDAY = 2;
    public static final int FRAGMENT_THURSDAY = 3;
    public static final int FRAGMENT_FRIDAY = 4;
    public static final int FRAGMENT_SATURDAY = 5;
    public static final int FRAGMENT_SUNDAY = 6;


    private List<FlightInfoTemp> infoTempsList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mNewSearch.setSearchBarCallback(new ISearchBarCallback() {
            @Override
            public void setItemInfo(FlightInfoTemp flightInfoTemp, int position) {
                if (null != flightInfoTemp) {
                    SimpleEvent simpleEvent = new SimpleEvent(Constants.CALL_TO_START);
                    simpleEvent.setmMsgId(flightInfoTemp.getId());
                    EventBus.getDefault().post(simpleEvent);
                }
            }

            @Override
            public void setChangeEditText(String text) {
                mNewSearch.upDateListView(getFlightInfoByNumber(text));
            }

        });
        return view;
    }

    private IOnTouchListener onTouchListener = new IOnTouchListener() {
        @Override
        public boolean onTouch(MotionEvent event) {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    Log.e("wjq","ACTION_DOWN");
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    Log.e("wjq","ACTION_MOVE");
//                    break;
//                case MotionEvent.ACTION_UP:
//                    Log.e("wjq","ACTION_UP");
//                    break;
//            }
            return true;
        }
    };

    private List<FlightInfoTemp> getFlightInfoByNumber(String number) {
        infoTempsList = DBManager.getInstance(getActivity()).queryFlightNumberLike(number);

        return infoTempsList;
    }

    @Override
    protected int supplyTabs(List<TabInfo> tabs) {
        Date date = new Date();//取时间
        Calendar calendar = new GregorianCalendar();
        tabs.add(new TabInfo(FRAGMENT_MONDAY, ((date.getYear() + 1900) + "/" + (date.getMonth() + 1) + "/" + date.getDate()),
                DayFragment.class,getActivity()));
        calendar.setTime(date);
        calendar.add(calendar.DATE, 1);//把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); //这个时间就是日期往后推一天的结果
        tabs.add(new TabInfo(FRAGMENT_TUESDAY, ((date.getYear() + 1900) + "/" + (date.getMonth() + 1) + "/" + date.getDate()),
                DayFragment.class,getActivity()));
        calendar.add(calendar.DATE, 1);//把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); //这个时间就是日期往后推一天的结果
        tabs.add(new TabInfo(FRAGMENT_WENDNESDAY, ((date.getYear() + 1900) + "/" + (date.getMonth() + 1) + "/" + date.getDate()),
                DayFragment.class,getActivity()));
        calendar.add(calendar.DATE, 1);//把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); //这个时间就是日期往后推一天的结果
        tabs.add(new TabInfo(FRAGMENT_THURSDAY, ((date.getYear() + 1900) + "/" + (date.getMonth() + 1) + "/" + date.getDate()),
                DayFragment.class,getActivity()));
        calendar.add(calendar.DATE, 1);//把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); //这个时间就是日期往后推一天的结果
        tabs.add(new TabInfo(FRAGMENT_FRIDAY, ((date.getYear() + 1900) + "/" + (date.getMonth() + 1) + "/" + date.getDate()),
                DayFragment.class,getActivity()));
        calendar.add(calendar.DATE, 1);//把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); //这个时间就是日期往后推一天的结果
        tabs.add(new TabInfo(FRAGMENT_SATURDAY, ((date.getYear() + 1900) + "/" + (date.getMonth() + 1) + "/" + date.getDate()),
                DayFragment.class,getActivity()));
        calendar.add(calendar.DATE, 1);//把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); //这个时间就是日期往后推一天的结果
        tabs.add(new TabInfo(FRAGMENT_SUNDAY, ((date.getYear() + 1900) + "/" + (date.getMonth() + 1) + "/" + date.getDate()),
                DayFragment.class,getActivity()));
        return FRAGMENT_MONDAY;
    }


    @Override
    protected int setTitle() {
        return R.string.radiogroup2;
    }

    @Override
    public void startInfoActivity(View v) {
        AddPopWindow addPopWindow = new AddPopWindow(getActivity());
        addPopWindow.showPopupWindow(v);
    }

    @Override
    protected int getMainViewResId() {
        return R.layout.fligth_fragment_tab_title;
    }

    @Override
    protected int broadcastORflight() {
        return Constants.FLIGHT_FRAGMENT;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
