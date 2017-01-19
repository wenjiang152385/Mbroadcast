package com.oraro.mbroadcast.ui.activity;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.listener.IDialogFragment;
import com.oraro.mbroadcast.model.FlightInfoTemp;
import com.oraro.mbroadcast.ui.fragment.SimpleDialogFragment;
import com.oraro.mbroadcast.ui.widget.LineEditText;
import com.oraro.mbroadcast.ui.widget.LineView;
import com.oraro.mbroadcast.utils.MAPXmlPullParser;
import com.oraro.mbroadcast.utils.UIUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class AddAndEditActivity extends AppCompatActivity implements View.OnClickListener {

    private Map<String, String> mFieldMap;
    private UIUtils mUIUtils;
    private LinearLayout main;
    private FlightInfoTemp mFlightInfoTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUIUtils = new UIUtils();
        Intent intent = getIntent();
        long infoId = intent.getLongExtra("info", -1);
        if (infoId != -1) {
            mFlightInfoTemp = (FlightInfoTemp) DBManager.getInstance(this).queryById(infoId, DBManager.getInstance(this).getFlightInfoTempDao(DBManager.READ_ONLY));
        }
        MAPXmlPullParser fieldXmlPullParser = new MAPXmlPullParser(MBroadcastApplication.getMyContext(), "field.xml");
        mFieldMap = fieldXmlPullParser.parseByPull();
        initView(savedInstanceState);

    }

    private void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_and_edit);
        main = (LinearLayout) findViewById(R.id.main);
        findViewById(R.id.header_left_img).setVisibility(View.GONE);
        TextView textBack = (TextView) findViewById(R.id.header_left_btn);
        textBack.setVisibility(View.VISIBLE);
        textBack.setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.delete).setOnClickListener(this);
        initPart("#d8d8d8");
    }


    private void initPart(String colors) {
        DisplayMetrics displayMetrics = mUIUtils.getDisplayMetrics(this);
        main.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(displayMetrics.widthPixels / 3,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int i = 0;
        LinearLayout linear = null;
        for (Map.Entry<String, String> entry : mFieldMap.entrySet()) {
            if (i % 3 == 0) {
                linear = new LinearLayout(this);
                main.addView(linear);
                mUIUtils.setViewParams(linear, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            String title = entry.getKey();
            final LineView lineView = new LineView(this);
            lineView.getEditText().showLine(true);
            lineView.setTag(entry.getValue());
            lineView.getEditText().setTextSize(28);
            lineView.getTextView().setTextSize(28);
            lineView.setBackgroundColor(Color.parseColor(colors));
            lineView.getEditText().setLineColor(Color.BLACK);
            lineView.setTwoTexts(title, "");
            if ("launch".equals(entry.getValue())) {
                lineView.getEditText().setText("00:00");
                showTimeDialog(lineView.getEditText());
            }
            linear.addView(lineView, lp);
            //必须要控件创建完毕才能设置margin。因为控件在layout都还没确定位置，怎么能设置margin呢？会出现空指针。
            mUIUtils.setViewMargin(lineView, 5, 5, 0, 0);
            i++;
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
//                prompt();
                break;

            case R.id.delete:
                showDialog("提示", "确定需要放弃么？", "确认放弃", false);
                break;

            case R.id.header_left_btn:
                hideSoftInput();
                finish();
                break;
            default:
                break;
        }
    }

    private void showDialog(String title, String content, String submit, final boolean flag) {
        final SimpleDialogFragment simpleDialogFragment = new SimpleDialogFragment();
        simpleDialogFragment.show(getFragmentManager(), "simpleDialogFragment");
        simpleDialogFragment.setTitle(title);
        simpleDialogFragment.setContent(content);
        simpleDialogFragment.setSubmit(submit);
        simpleDialogFragment.setOnButtonClickListener(new IDialogFragment() {
            @Override
            public void onDialogFragmentButtonClickListener() {
                if (flag) {
                    finish();
                } else {
                    simpleDialogFragment.dismiss();
                    finish();
                }
            }
        });
    }

    private void showTimeDialog(final LineEditText editText) {
        editText.setFocusableInTouchMode(false);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                try {
                    Date date = sdf.parse(editText.getEditableText().toString());
                    c.setTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Dialog dialog = new TimePickerDialog(
                        AddAndEditActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String min = minute + "";
                                if (minute < 10) {
                                    min = "0" + minute;
                                }
                                editText.setText(hourOfDay + ":" + min);
                            }
                        },
                        c.get(Calendar.HOUR_OF_DAY),
                        c.get(Calendar.MINUTE),
                        false
                );
                hideSoftInput();
                dialog.show();
            }
        });
    }

    private void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), 0);
        }
    }
}
