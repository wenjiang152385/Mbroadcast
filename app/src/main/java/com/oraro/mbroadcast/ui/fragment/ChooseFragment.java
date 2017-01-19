package com.oraro.mbroadcast.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.ui.activity.MainActivity;
import com.oraro.mbroadcast.utils.CustomFragmentManager;
import com.oraro.mbroadcast.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

/**
 * Created by Administrator on 2016/11/24 0024.
 *
 * @author [佛祖保佑 永无BUG]
 */
public class ChooseFragment extends Fragment {
    public final static String TAG_CHOOSE = "ChooseFragment";
    private MainActivity mainActivity;
    private CustomFragmentManager customFragmentManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_choose, null);
        ((TextView) view.findViewById(R.id.header_text)).setText("设置");
        initView(view);
        return view;

    }

    @Override
    public void onAttach(Activity activity) {
        mainActivity = (MainActivity) activity;
        super.onAttach(activity);
    }

    private View.OnClickListener mOnclicklister = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.choose:
                    DeviceFragment deviceFragment = new DeviceFragment();
                    customFragmentManager.startFragment(deviceFragment);
                    break;
                case R.id.connect:
//                    Intent connectIntent = new Intent(getActivity(),CliActivity.class);
//                    startActivity(connectIntent);
                    CliActivityFragment cliActivityFragment = new CliActivityFragment();
                    customFragmentManager.startFragment(cliActivityFragment);
                    break;
                case R.id.version:
//                    Intent versionIntent = new Intent(getActivity(),VersionActivity.class);
//                    startActivity(versionIntent);
                    VersionFragment versionFragment = new VersionFragment();
                    customFragmentManager.startFragment(versionFragment);
                    break;
                case R.id.settings:
//                    Intent settingsIntent = new Intent(getActivity(),SettingsActivity.class);
//                    startActivity(settingsIntent);
                    SettingFragment settingFragment = new SettingFragment();
                    customFragmentManager.startFragment(settingFragment);
                    break;
                case R.id.header_left_btn:
                    EventBus.getDefault().post(new SimpleEvent(Constants.BACK_MAIN_CLOSE));
            }

        }
    };

    private void initView(View view) {
        int type = SPUtils.getPrefInt(getActivity(), "deviceType", -1);
        if (0 == type) {
            view.findViewById(R.id.connect).setVisibility(View.GONE);
        }
        customFragmentManager = CustomFragmentManager.getInstance(mainActivity);
        view.findViewById(R.id.header_left_img).setVisibility(View.GONE);
        view.findViewById(R.id.click).setVisibility(View.GONE);
        view.findViewById(R.id.click1).setVisibility(View.GONE);
        view.findViewById(R.id.header_left_btn).setOnClickListener(mOnclicklister);
        view.findViewById(R.id.header_left_btn).setVisibility(View.VISIBLE);
        view.findViewById(R.id.connect).setOnClickListener(mOnclicklister);
        view.findViewById(R.id.version).setOnClickListener(mOnclicklister);
        view.findViewById(R.id.settings).setOnClickListener(mOnclicklister);
        view.findViewById(R.id.choose).setOnClickListener(mOnclicklister);
    }
}
