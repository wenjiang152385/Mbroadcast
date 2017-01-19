package com.oraro.mbroadcast.ui.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.TextView;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.utils.AppUtils;

/**
 * Created by dongyu on 2016/9/18 0018.
 */
public class VersionActivity extends BaseActivity {
   private TextView version_activity_text,header_right_btn;
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.version_activity;
    }

    @Override
    protected void initView() {
       version_activity_text=(TextView) findViewById(R.id.version_activity_text);
        header_right_btn=(TextView) findViewById(R.id.header_right_btn);
    }

    @Override
    protected void initData(Bundle paramBundle) {
        header_right_btn.setVisibility(View.INVISIBLE);
        setHeader_textTitle(R.string.version_activity_title_name);
        setHeader_left_btn();
        version_activity_text.setText("版本信息 "+AppUtils.getAppVersionName(this, Constants.PACKAGENAME));
    }
}
