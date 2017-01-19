package com.oraro.mbroadcast.ui.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.oraro.mbroadcast.model.TabInfo;
import com.oraro.mbroadcast.utils.LogUtils;
import com.oraro.mbroadcast.utils.SPUtils;

import java.util.ArrayList;

/**
 * Created by dongyu on 2016/8/12 0012.
 */
public class TitleIndicatorAdapter extends FragmentPagerAdapter {
    ArrayList<TabInfo> tabs = null;
    Context context = null;
    FragmentManager fragmentManager = null;

    public TitleIndicatorAdapter(Context context, FragmentManager fm, ArrayList<TabInfo> tabs) {
        super(fm);
        this.fragmentManager = fm;
        this.tabs = tabs;
        this.context = context;
    }


    @Override
    public Fragment getItem(int pos) {
        Fragment fragment = null;
        if (tabs != null && pos < tabs.size()) {
            TabInfo tab = tabs.get(pos);
            if (tab == null)
                return null;
            fragment = tab.createFragment();
        }
        return fragment;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public long getItemId(int position) {
        return tabs.get(position).getId();
    }

    @Override
    public int getCount() {
        if (tabs != null && tabs.size() > 0)
            return tabs.size();
        return 0;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        TabInfo tab = tabs.get(position);
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        tab.fragment = fragment;
        return tab.fragment;
    }
}