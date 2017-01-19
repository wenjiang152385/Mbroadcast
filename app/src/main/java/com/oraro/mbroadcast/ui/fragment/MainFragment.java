package com.oraro.mbroadcast.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.listener.IAutoPlayStatusListener;
import com.oraro.mbroadcast.listener.OnProgressBarListener;
import com.oraro.mbroadcast.logicService.DataService;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.service.IMyAidlInterface;
import com.oraro.mbroadcast.ui.widget.CustomFragmentTabHost;
import com.oraro.mbroadcast.ui.widget.NumberProgressBar;
import com.oraro.mbroadcast.utils.GuideUtil;
import com.oraro.mbroadcast.utils.LogUtils;
import com.oraro.mbroadcast.utils.TabEntry;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.POWER_SERVICE;

/**
 * Created by Administrator on 2016/11/24 0024.
 *
 * @author
 */
public class MainFragment extends Fragment implements OnProgressBarListener {
    public static final String TAG_MAIN = "MainFragment";
    private ImageView autoPlay;
    private DataService service = new DataService();
    PowerManager powerManager = null;
    PowerManager.WakeLock wakeLock = null;
    private IMyAidlInterface iMyAidlInterface;
    private CustomFragmentTabHost mTabHost;
    private List<TabEntry> tabEntries = new ArrayList<>();
    private IAutoPlayStatusListener mIAutoPlayStatusListener;
    private Context mActivity;
    private NumberProgressBar bnp;

    @Override
    public void onAttach(Context context) {


        super.onAttach(context);
        mActivity = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        View view = inflater.inflate(R.layout.fragment_main, null);

        tabEntries.clear();
        tabEntries.add(new TabEntry("广播", R.drawable.broadcast_radio0_bg, BroadcastFragment.class));
        tabEntries.add(new TabEntry("航班", R.drawable.broadcast_radio1_bg, FlightFragment.class));
        //tabEntries.add(new TabEntry("温馨提示", R.drawable.broadcast_radio2_bg, PromptFragment.class));
        tabEntries.add(new TabEntry("自定义广播", R.drawable.broadcast_radio2_bg, KindlyReminderFragment.class));

        initView(view);
        return view;

    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        powerManager = (PowerManager) mActivity.getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                | PowerManager.ON_AFTER_RELEASE, TAG_MAIN);
        wakeLock.acquire();
        super.onResume();

    }


    private void initView(View content) {
        final View view = content.findViewById(R.id.main);
        mTabHost = (CustomFragmentTabHost) content.findViewById(R.id.custom_tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);
        bnp = (NumberProgressBar) view.findViewById(R.id.numberbar1);
        bnp.setOnProgressBarListener(new OnProgressBarListener() {
            @Override
            public void onProgressChange(int current, int max) {
                if(current >= max){
                    bnp.setVisibility(View.INVISIBLE);
                }
            }
        });
        bnp.setSuffix("");
        for (int i = 0; i < tabEntries.size(); i++) {
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost
                    .newTabSpec(tabEntries.get(i).getTabText()).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, tabEntries.get(i).getTabClass(), null);
            //设置Tab按钮的背景
//            mTabHost.getTabWidget().getChildAt(i)
//                    .setBackgroundResource(R.drawable.selector_tab_background);
        }
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (null != view.getWindowToken()) {
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        if (tabEntries.size() > 0) {
            mTabHost.setCurrentTab(tabEntries.size() - 1);
        }
        autoPlay = (ImageView) content.findViewById(R.id.autoplay);


        if (service.getAutoPlayStatus()) {
            autoPlay.setImageResource(R.mipmap.zidongbig);
        } else {
            autoPlay.setImageResource(R.mipmap.shoudongbig);
        }
        autoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service.setAutoPlayStatus();
                if (iMyAidlInterface == null) {
                    iMyAidlInterface = MBroadcastApplication.getIMyAidlInterface();
                }
                try {
                    iMyAidlInterface.autioPlay(service.getAutoPlayStatus());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                if (service.getAutoPlayStatus()) {
                    autoPlay.setImageResource(R.mipmap.zidongbig);
                    if (null != mIAutoPlayStatusListener) {
                        mIAutoPlayStatusListener.changeAutoPlayStatus(true);
                    }
                    Toast.makeText(mActivity, "自动播放模式已启用。", Toast.LENGTH_SHORT).show();
                } else {
                    autoPlay.setImageResource(R.mipmap.shoudongbig);
                    if (null != mIAutoPlayStatusListener) {
                        mIAutoPlayStatusListener.changeAutoPlayStatus(false);
                    }
                    Toast.makeText(mActivity, "自动播放模式已关闭。", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setCallBack(IAutoPlayStatusListener iAutoPlayStatusListener) {
        mIAutoPlayStatusListener = iAutoPlayStatusListener;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SimpleEvent event) {
        switch (event.getMsg()) {
            case Constants.EXCEL_Transfer_Fail_int:
                Toast.makeText(MBroadcastApplication.getMyContext(), "当前有文件读写异常！", Toast.LENGTH_LONG).show();
                break;
            case Constants.File_Trans_Length:
                bnp.setVisibility(View.VISIBLE);
                bnp.setMax(event.getmDataLength());
                break;
            case Constants.File_Trans_Loading:
                bnp.setProgress(event.getmDataLength());
                break;
        }
    }

    private View getTabItemView(int index) {
        //给Tab按钮设置图标和文字
        View view = LayoutInflater.from(mActivity).inflate(R.layout.tab_item_view, null);
        //API等级过高。不使用getDrawable(@DrawableRes int id, @Nullable Theme theme);
        Drawable tabDrawable = getResources().getDrawable(tabEntries.get(index).getTabImageId());
        tabDrawable.setBounds(0, 0, 50, 50);
        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setCompoundDrawables(null, tabDrawable, null, null);
        textView.setText(tabEntries.get(index).getTabText());
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> frags = getChildFragmentManager().getFragments();
        if (frags != null) {
            for (Fragment f : frags) {
                if (f != null)
                    handleResult(f, requestCode, resultCode, data);
            }
        }
    }

    /**
     * 递归调用，对所有子Fragement生效
     *
     * @param frag
     * @param requestCode
     * @param resultCode
     * @param data
     */
    private void handleResult(Fragment frag, int requestCode, int resultCode, Intent data) {
        frag.onActivityResult(requestCode & 0xffff, resultCode, data);
        List<Fragment> frags = frag.getChildFragmentManager().getFragments();
        if (frags != null) {
            for (Fragment f : frags) {
                if (f != null)
                    handleResult(f, requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != wakeLock) {
            wakeLock.release();
        }

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onProgressChange(int current, int max) {
        if (current >= max) {
            bnp.setVisibility(View.INVISIBLE);
        }

    }
}
