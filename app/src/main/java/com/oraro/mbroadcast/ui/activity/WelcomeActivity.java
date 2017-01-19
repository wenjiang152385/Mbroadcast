package com.oraro.mbroadcast.ui.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.broadcasts.NetWorkReceiver;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.listener.IDialogFragment;
import com.oraro.mbroadcast.logicService.DataService;
import com.oraro.mbroadcast.model.InterCutData;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.service.DoubleService;
import com.oraro.mbroadcast.ui.fragment.SimpleDialogFragment;
import com.oraro.mbroadcast.utils.PromptXmlPullParser;
import com.oraro.mbroadcast.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {
    private ImageView mIv_radio;
    private ImageView mIv_pad;
    private ImageView mWelcome;
    private Button mEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mEnter = (Button) findViewById(R.id.enter);
        mIv_radio = (ImageView) findViewById(R.id.iv_radio);
        mIv_pad = (ImageView) findViewById(R.id.iv_pad);
        mWelcome = (ImageView) findViewById(R.id.welcome);
        mIv_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIv_radio.setImageResource(R.mipmap.radio_pressed);
                mIv_pad.setImageResource(R.mipmap.pad_unpressed);
                SPUtils.setPrefInt(WelcomeActivity.this, "deviceType", 0);
            }
        });

        mIv_pad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIv_radio.setImageResource(R.mipmap.radio_unpressed);
                mIv_pad.setImageResource(R.mipmap.pad_pressed);
                SPUtils.setPrefInt(WelcomeActivity.this, "deviceType", 1);
            }
        });

        mEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SPUtils.getPrefInt(WelcomeActivity.this, "deviceType", -1) == -1) {
                    showDialog("提示", "未选择任何设备，默认设备是音箱", "确认", -1);
                } else if (SPUtils.getPrefInt(WelcomeActivity.this, "deviceType", -1) == 1) {
                    showDialog("提示", "确认当前设备为平板？", "确认", 1);
                } else if (SPUtils.getPrefInt(WelcomeActivity.this, "deviceType", -1) == 0) {
                    showDialog("提示", "确认当前设备为音箱？", "确认", 0);
                }
            }
        });
        if (SPUtils.getPrefBoolean(this, "isFirstLogin", true)) {
            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mEnter.setVisibility(View.VISIBLE);
                    mIv_radio.setVisibility(View.VISIBLE);
                    mIv_pad.setVisibility(View.VISIBLE);
                    mWelcome.setVisibility(View.GONE);
                }
            }, 1000 * 2);
        } else {
            int type = SPUtils.getPrefInt(WelcomeActivity.this, "deviceType", -1);
            DoubleService.getInstance().StartServices(MBroadcastApplication.getMyContext(), type);
            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startMainActivity();
                }
            }, 1000 * 2);
        }
    }

    private void initDao() {
        if (SPUtils.getPrefBoolean(this, "isFirstLogin", true)) {
            DataService dataService = new DataService();
            dataService.setSpaceStatus(1000 * 2);
            List<InterCutData> interCutDataList = new ArrayList<>();
            PromptXmlPullParser xmlPullParser = new PromptXmlPullParser(this, "prompt.xml", "东方航空公司");
            List<String> list = xmlPullParser.parseByPull();
            for (int i = 0; i < list.size(); i++) {
                String text = list.get(i);
                InterCutData interCutData = new InterCutData();
                interCutData.setText(text);
                interCutData.setTime(text.length() * 300);
                interCutData.setTy(0);
                interCutData.setIsPlay(true);
                interCutDataList.add(interCutData);
            }
            DBManager.getInstance(this).insertList(interCutDataList, DBManager.getInstance(this).getInterCutDataDao(DBManager.WRITE_ONLY));
        }
    }


    private void showDialog(String title, String content, String submit, final int flag) {
        final SimpleDialogFragment simpleDialogFragment = new SimpleDialogFragment();
        simpleDialogFragment.show(getFragmentManager(), "simpleDialogFragment");
        simpleDialogFragment.setTitle(title);
        simpleDialogFragment.setContent(content);
        simpleDialogFragment.setSubmit(submit);

        simpleDialogFragment.setDismissCallback(new SimpleDialogFragment.DismissCallback() {
            @Override
            public void dismissCallback() {
                mIv_pad.setFocusable(true);
                mIv_radio.setFocusable(true);
                mIv_pad.setEnabled(true);
                mIv_radio.setEnabled(true);
            }
        });

        simpleDialogFragment.setOnButtonClickListener(new IDialogFragment() {
            @Override
            public void onDialogFragmentButtonClickListener() {
                if (-1 == flag) {
                    SPUtils.setPrefInt(WelcomeActivity.this, "deviceType", 0);
                    DoubleService.getInstance().StartServices(MBroadcastApplication.getMyContext(), 0);
                } else {
                    SPUtils.setPrefInt(WelcomeActivity.this, "deviceType", flag);
                    DoubleService.getInstance().StartServices(MBroadcastApplication.getMyContext(), flag);
                }
                simpleDialogFragment.dismiss();
                startMainActivity();
                WelcomeActivity.this.finish();
            }
        });
    }


    private void startMainActivity() {
        initDao();
        SPUtils.setPrefBoolean(this, "isFirstLogin", false);
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        WelcomeActivity.this.finish();
    }

}
