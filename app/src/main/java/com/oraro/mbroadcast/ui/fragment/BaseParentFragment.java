package com.oraro.mbroadcast.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.logicService.DataService;
import com.oraro.mbroadcast.model.TabInfo;
import com.oraro.mbroadcast.service.IMyAidlInterface;
import com.oraro.mbroadcast.service.SerService;
import com.oraro.mbroadcast.ui.activity.ChooseActivity;
import com.oraro.mbroadcast.ui.activity.MainActivity;
import com.oraro.mbroadcast.ui.widget.AddBroadcastPopWindow;
import com.oraro.mbroadcast.ui.widget.NewSearchBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongyu on 2016/8/19 0019.
 */
public abstract class BaseParentFragment extends Fragment implements ViewPager.OnPageChangeListener, View.OnClickListener {


    private static final String TAG = "DxFragmentActivity";
    public static final String EXTRA_TAB = "tab";
    public static final String EXTRA_QUIT = "extra.quit";
    protected int mCurrentTab = 0;
    protected int mLastTab = -1;
    //存放选项卡信息的列表
    protected ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
    //viewpager adapter
    protected TitleIndicatorAdapter myAdapter = null;
    //viewpager
    protected ViewPager mPager;
    //选项卡控件
    protected TitleIndicator mIndicator;
    protected View view;
    protected TextView header_right_btn;


    protected NewSearchBar mNewSearch;

    private MainActivity mActivity;
    protected TextView bt_plan;

    private View rootView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(getMainViewResId(), container, false);
        initViews(rootView);
        mPager.setPageMargin(0);
        //设置viewpager内部页面间距的drawable
        mPager.setPageMarginDrawable(R.color.page_viewer_margin_color);

        return rootView;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    protected void initViews(View parentView) {
        // 这里初始化界面

//        AddBroadcastPopWindow popWindow=new AddBroadcastPopWindow(getActivity());
//        popWindow.showPopupWindow(parentView);
        myAdapter = new TitleIndicatorAdapter(getContext(), getChildFragmentManager(), mTabs);
        TextView header_text = (TextView) parentView.findViewById(R.id.header_text);
        header_right_btn = (TextView) parentView.findViewById(R.id.header_right_btn);
        bt_plan = (TextView) parentView.findViewById(R.id.plan_btn);
        bt_plan.setOnClickListener(this);
        LinearLayout header_left_ll = (LinearLayout) parentView.findViewById(R.id.header_left_ll);
        header_text.setText(setTitle());
        header_left_ll.setOnClickListener(this);
        header_right_btn.setOnClickListener(this);
        parentView.findViewById(R.id.click).setOnClickListener(this);
        parentView.findViewById(R.id.click1).setOnClickListener(this);
        if (broadcastORflight() == Constants.FLIGHT_FRAGMENT) {
//            header_right_btn.setVisibility(View.VISIBLE);
            mNewSearch = (NewSearchBar) parentView.findViewById(R.id.newSearch);
        }
        header_right_btn.setBackgroundResource(R.mipmap.title_bar_right);

        mCurrentTab = supplyTabs(mTabs);
        mPager = (ViewPager) parentView.findViewById(R.id.pager);
        mPager.setAdapter(myAdapter);
        mPager.setOnPageChangeListener(this);
        mPager.setOffscreenPageLimit(mTabs.size());
        //  mPager.setOffscreenPageLimit(2);
        mIndicator = (TitleIndicator) parentView.findViewById(R.id.pagerindicator);
        mIndicator.init(mCurrentTab, mTabs, mPager);
        mPager.setCurrentItem(mCurrentTab);
        mLastTab = mCurrentTab;


    }

    /**
     * 删除一个选项卡
     *
     * @param tab
     */
    public void deldeteTabInfo(TabInfo tab) {
        mTabs.remove(tab);
        myAdapter.notifyDataSetChanged();
    }

    /**
     * 添加一个选项卡
     *
     * @param tab
     */
    public void addTabInfo(TabInfo tab) {
        mTabs.add(tab);
        myAdapter.notifyDataSetChanged();
    }

    /**
     * 从列表添加选项卡
     *
     * @param tabs
     */
    public void addTabInfos(ArrayList<TabInfo> tabs) {
        mTabs.addAll(tabs);
        myAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mIndicator.onScrolled((mPager.getWidth() + mPager.getPageMargin()) * position + positionOffsetPixels);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mTabs.size() > 0 && mTabs.get(0).getId() == BroadcastFragment.FRAGMENT_TEM_PLAY) {
            header_right_btn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (BroadcastFragment.FRAGMENT_TEM_PLAY == mTabs.get(position).getId())
            header_right_btn.setVisibility(View.VISIBLE);
        else
            header_right_btn.setVisibility(View.INVISIBLE);
        mIndicator.onSwitched(position);
        mCurrentTab = position;
        if (broadcastORflight() == Constants.FLIGHT_FRAGMENT) {
            return;
        }


    }

    @Override
    public void onAttach(Activity activity) {
        mActivity = (MainActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            mLastTab = mCurrentTab;
        }
    }

    protected TabInfo getFragmentById(int tabId) {
        if (mTabs == null) return null;
        for (int index = 0, count = mTabs.size(); index < count; index++) {
            TabInfo tab = mTabs.get(index);
            if (tab.getId() == tabId) {
                return tab;
            }
        }
        return null;
    }

    /**
     * 跳转到任意选项卡
     *
     * @param tabId 选项卡下标
     */
    public void navigate(int tabId) {
        for (int index = 0, count = mTabs.size(); index < count; index++) {
            if (mTabs.get(index).getId() == tabId) {
                mPager.setCurrentItem(index);
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    /**
     * 返回layout id
     *
     * @return layout id
     */
    protected abstract int getMainViewResId();
    /**
     * 在这里提供要显示的选项卡数据
     */
    /**
     * 在这里提供要显示的选项卡数据
     */
    protected abstract int supplyTabs(List<TabInfo> tabs);

    protected abstract int setTitle();

    public abstract void startInfoActivity(View v);

    protected abstract int broadcastORflight();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.header_right_btn:
                startInfoActivity(v);
                break;
            case R.id.header_left_ll:
//                Intent intent = new Intent();
//                intent.setClass(getActivity(), ChooseActivity.class);
//                getActivity().startActivity(intent);
                mActivity.showSlidingMenu();
                break;
            case R.id.plan_btn:
                AddBroadcastPopWindow popWindow = new AddBroadcastPopWindow(getActivity());
                popWindow.showPopupWindow(v);
                break;

            case R.id.click:
                DataService service = new DataService();
                service.setAutoPlayStatus();
                IMyAidlInterface iMyAidlInterface = MBroadcastApplication.getIMyAidlInterface();
                try {
                    iMyAidlInterface.autioPlay(service.getAutoPlayStatus());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                Intent intent1 = new Intent(getActivity(), SerService.class);
                intent1.putExtra("status", 0);
                getActivity().startService(intent1);


                break;


            case R.id.click1:
                String ip = null;
                break;

            default:
                break;

        }

    }
}
