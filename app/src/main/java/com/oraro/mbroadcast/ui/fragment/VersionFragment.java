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
import com.oraro.mbroadcast.ui.activity.MainActivity;
import com.oraro.mbroadcast.utils.AppUtils;
import com.oraro.mbroadcast.utils.CustomFragmentManager;

/**
 * Created by Administrator on 2016/11/24 0024.
 *
 * @author [佛祖保佑 永无BUG]
 */
public class VersionFragment extends Fragment {
    public static final String TAG_VERSION="VersionFragment";
    private TextView   version_activity_text;
    private TextView header_right_btn;
    private MainActivity mainActivity;
    private CustomFragmentManager customFragmentManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.version_activity,null);
        initView(view);
        return view;

    }

    @Override
    public void onAttach(Activity activity) {
        mainActivity = (MainActivity) activity;
        super.onAttach(activity);
    }

    private void initView(View view) {
        ((TextView)view.findViewById(R.id.header_text)).setText("版本信息");
        customFragmentManager = CustomFragmentManager.getInstance(mainActivity);
        version_activity_text=(TextView) view.findViewById(R.id.version_activity_text);
        TextView header_left_btn= (TextView) view.findViewById(R.id.header_left_btn);
        header_right_btn=(TextView) view.findViewById(R.id.header_right_btn);
        header_right_btn.setVisibility(View.INVISIBLE);
        header_left_btn.setVisibility(View.VISIBLE);
        header_left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 customFragmentManager.finishFragment();
            }
        });
        version_activity_text.setText("版本信息 "+ AppUtils.getAppVersionName(getActivity(), Constants.PACKAGENAME));
    }
}
