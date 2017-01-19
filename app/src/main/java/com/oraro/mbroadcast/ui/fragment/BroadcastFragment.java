package com.oraro.mbroadcast.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.model.TabInfo;
import com.oraro.mbroadcast.ui.activity.EditActivity;
import com.oraro.mbroadcast.utils.DateUtils;
import com.oraro.mbroadcast.utils.LogUtils;
import com.oraro.mbroadcast.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.List;

/**
 * Created by dongyu on 2016/8/19 0019.
 */
public class BroadcastFragment extends BaseParentFragment {

    public static final int FRAGMENT_MONDAY = 0;
    public static final int FRAGMENT_TUESDAY = 1;
    public static final int FRAGMENT_WENDNESDAY = 2;
    public static final int FRAGMENT_THURSDAY = 3;
    public static final int FRAGMENT_FRIDAY = 4;
    public static final int FRAGMENT_SATURDAY = 5;
    public static final int FRAGMENT_SUNDAY = 6;
    public static final int FRAGMENT_TEM_PLAY = 7;
    private TabInfo tabInfoMonday, tabInfoTuesday, tabInfoWendnesday,
            tabInfoThursday, tabInfoFriday, tabInfoSaturdday, tabInfoSunday,
            tabInfoTemp;
    private View broadcastFragmentview;
    int[] days = {0x0001, 0x0010, 0x0100, 0x1000, 0x10000, 0x100000, 0x1000000};
    private Integer[] weeks = new Integer[]{R.string.fragment_monday,
            R.string.fragment_tuesday,
            R.string.fragment_wednesday,
            R.string.fragment_thursday,
            R.string.fragment_friday,
            R.string.fragment_saturday,
            R.string.fragment_sunday};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        broadcastFragmentview = super.onCreateView(inflater, container, savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        return broadcastFragmentview;
    }

    @Subscribe()
    public void onEventMainThread(SimpleEvent event) {
        if (event.getMsg() == Constants.SETTINGS_WEEK) {
            mTabs.clear();
            addTabs(mTabs);
            myAdapter = new TitleIndicatorAdapter(getContext(), getChildFragmentManager(), mTabs);
            mPager = (ViewPager) broadcastFragmentview.findViewById(R.id.pager);
            mIndicator = (TitleIndicator) broadcastFragmentview.findViewById(R.id.pagerindicator);
            mPager.setAdapter(myAdapter);
            mPager.setOnPageChangeListener(this);
            mPager.setOffscreenPageLimit(mTabs.size());
            if (!isExit()) {
                mCurrentTab = 0;
            }
            mIndicator.init(mCurrentTab, mTabs, mPager);
            mPager.setCurrentItem(mCurrentTab);
        }
    }

    private boolean isExit() {
        for (int i = 0; i < mTabs.size(); i++) {
            if (mCurrentTab == mTabs.get(i).getId()) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected int supplyTabs(List<TabInfo> tabs) {
        Date noNDate = new Date();
        addTabs(tabs);

        int weekIndex = DateUtils.getWeek(noNDate) - 1;
        for (int i = 0; i < tabs.size(); i++) {
            if (BroadcastFragment.FRAGMENT_TEM_PLAY != mTabs.get(i).getId())
                header_right_btn.setVisibility(View.INVISIBLE);
            if (tabs.get(i).getId() == weekIndex) {
                return i;
            }
        }
        return 0;
    }


    @Override
    protected int setTitle() {
        return R.string.radiogroup1;
    }

    @Override
    public void startInfoActivity(View v) {
        Intent intent = new Intent(getActivity(), EditActivity.class);
        intent.putExtra("flag", 0);
        getActivity().startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected int getMainViewResId() {
        return R.layout.broadcast_fragment_tab_title;
    }

    @Override
    protected int broadcastORflight() {
        return Constants.BROADCAST_FRAGMENT;
    }

    private void addTabs(List<TabInfo> tabs) {
        int daySelect = SPUtils.getPrefInt(this.getActivity(), "daySelect", 0x01111111);
        tabInfoTemp = new TabInfo(FRAGMENT_TEM_PLAY, getString(R.string.fragment_temp),
                TemporaryPlayFragment.class, this.getActivity());
        for (int i = 0; i < days.length; i++) {
            if ((daySelect & days[i]) == days[i]) {
                tabs.add(new TabInfo(i, getString(weeks[i]),
                        WeekFragment.class, this.getActivity()));
            }
        }
        tabs.add(tabInfoTemp);
    }

}
