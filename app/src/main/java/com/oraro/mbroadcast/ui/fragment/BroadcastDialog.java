package com.oraro.mbroadcast.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.model.PlayEntry;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.mp.PlayAudio;
import com.oraro.mbroadcast.proxy.TTSProXy;
import com.oraro.mbroadcast.ui.adapter.WeekBroadcastAdapter;
import com.oraro.mbroadcast.utils.PlayStateUtils;
import com.oraro.mbroadcast.utils.UIUtils;
import com.oraro.mbroadcast.vo.PlayVO;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weijiaqi on 2016/8/24 0024.
 */
public class BroadcastDialog extends DialogFragment {
    private ViewPager mViewPager;
    private TextView mTTsText;
    private TextView mRrdText;
    private ImageView mTTSImg;
    private ImageView mRrdImg;
    private PlayVO playVO;
    private TTSDialogFragment ttsDialogFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = setViewParams(inflater, container, savedInstanceState);
        initViews(view);

        return view;
    }


    private View setViewParams(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Window window = getDialog().getWindow();
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.layout_dialog0, ((ViewGroup) window.findViewById(android.R.id.content)), false);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        UIUtils toolUtils = new UIUtils();
        window.setLayout(toolUtils.getDisplayMetrics(getActivity()).widthPixels / 2,
                toolUtils.getDisplayMetrics(getActivity()).heightPixels / 2);
        return view;
    }

    @Subscribe(sticky = true)
    public void onEvent(SimpleEvent event) {/* Do something */
        if(event.getMsg()==9823442){
//            isPlaying = false;

        }


    }
    private List<Fragment> getFragmentList() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        Bundle bundle = getArguments();
        long id = bundle.getLong("playVOID");
        playVO = bundle.getParcelable("playVO");
        List<Fragment> fragmentList = new ArrayList<>();
        ttsDialogFragment = new TTSDialogFragment();
//        ttsDialogFragment.setPlaying(isPlaying);
        RecordDialogFragment recordDialogFragment = new RecordDialogFragment();
        ttsDialogFragment.setId(id);
        recordDialogFragment.setId(id);
        fragmentList.add(ttsDialogFragment);

        FileSelectFragment fileSelectFragment = new FileSelectFragment();
//        fileSelectFragment.setPlaying(isPlaying);
        Bundle bundleFile = new Bundle();
        bundleFile.putParcelable("playVO", playVO);
        fileSelectFragment.setArguments(bundleFile);
        fragmentList.add(fileSelectFragment);

        return fragmentList;
    }

    private void initViews(View view) {
        mTTsText = (TextView) view.findViewById(R.id.txt_tts);
        mRrdText = (TextView) view.findViewById(R.id.txt_filePath);
        mTTSImg = (ImageView) view.findViewById(R.id.tts_img);
        mRrdImg = (ImageView) view.findViewById(R.id.filePath_img);
        mViewPager = (ViewPager) view.findViewById(R.id.fragment_viewpager);
        mViewPager.setAdapter(new FragAdapter(getChildFragmentManager(), getFragmentList()));
        if (playVO.getEntity().getFileParentPath() != null) {// 有路径
            mViewPager.setCurrentItem(1);
            changeStatus(1);
        }else{
            mViewPager.setCurrentItem(0);
        }
        mTTsText.setOnClickListener(mOnClickListener);
        mRrdText.setOnClickListener(mOnClickListener);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);

    }


    public void changeStatus(int i) {
        mTTsText.setTextColor((Color.parseColor(i == 0 ? "#f5a726" : "#343434")));
        mRrdText.setTextColor(Color.parseColor(i == 0 ? "#343434" : "#f5a726"));
        mTTSImg.setImageResource(i == 0 ? R.mipmap.clicked_dialog : R.mipmap.unclicked_dialog);
        mRrdImg.setImageResource(i == 0 ? R.mipmap.unclicked_dialog : R.mipmap.clicked_dialog);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.txt_tts:
                    changeStatus(0);
                    mViewPager.setCurrentItem(0);
                    break;
                case R.id.txt_filePath:
                    changeStatus(1);
                    mViewPager.setCurrentItem(1);
                    break;
                default:
                    break;
            }
        }
    };

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            changeStatus(position);
            boolean isPlaying = PlayStateUtils.isPlaying(getContext());
            if (playVO.getEntity().getFileParentPath() != null || isPlaying) {// 有路径

                ttsDialogFragment.setTTSDialogFragmentBtn(false);
            } else if (playVO.getEntity().getFileParentPath() == null || !isPlaying){// 没有路径
                ttsDialogFragment.setTTSDialogFragmentBtn(true);
            }

            else if (playVO.getEntity().getFileParentPath() == null && isPlaying) {
                ttsDialogFragment.setTTSDialogFragmentBtn(false);

            }

        }


        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
//    private boolean isPlaying;

//    public void setBtnIsPlay(boolean isPlaying) {
//        this.isPlaying = isPlaying;
//    }

    public void setStatusByPlayStatus(boolean mIsPlaying) {
        if (mIsPlaying) {
            ttsDialogFragment.setTTSDialogFragmentBtn(false);

        } else {
            ttsDialogFragment.setTTSDialogFragmentBtn(true);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().removeStickyEvent(this);
    }
}

class FragAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragmentList;

    public FragAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}

