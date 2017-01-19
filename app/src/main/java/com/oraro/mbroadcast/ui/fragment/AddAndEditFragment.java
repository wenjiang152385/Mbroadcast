package com.oraro.mbroadcast.ui.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.listener.IDialogFragment;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.presenter.AddAndEditPresenter;
import com.oraro.mbroadcast.ui.widget.LineEditText;
import com.oraro.mbroadcast.ui.widget.LineView;
import com.oraro.mbroadcast.utils.MAPXmlPullParser;
import com.oraro.mbroadcast.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusFragment;

/**
 * Created by Administrator on 2016/12/6 0006.
 */
@RequiresPresenter(AddAndEditPresenter.class)
public class AddAndEditFragment extends NucleusFragment<AddAndEditPresenter> {

    public Map<String, String> mFieldMap;
    private UIUtils mUIUtils;
    public LinearLayout main;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_add_and_edit, null, false);
        mUIUtils = new UIUtils();
        MAPXmlPullParser fieldXmlPullParser = new MAPXmlPullParser(MBroadcastApplication.getMyContext(), "field.xml");
        mFieldMap = fieldXmlPullParser.parseByPull();
        initView(rootView);
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        main = null;
        if (null != mFieldMap) {
            mFieldMap.clear();
        }
        mFieldMap =null;
        mUIUtils = null;
        if (null != getPresenter()) {
            getPresenter().dropView();
            getPresenter().destroy();
        }
    }

    private void initView(View view) {
        main = (LinearLayout) view.findViewById(R.id.main);
        view.findViewById(R.id.header_left_img).setVisibility(View.GONE);
        TextView textBack = (TextView) view.findViewById(R.id.header_left_btn);
        TextView header_text = (TextView) view.findViewById(R.id.header_text);
        Bundle bundle =getArguments();
        long id = -1;
        if (null != bundle) {
            id = bundle.getLong("info", -1);
        }
        if (id == -1) {
            header_text.setText("新增");
        }else {
            header_text.setText("编辑");
        }
        textBack.setVisibility(View.VISIBLE);
        textBack.setOnClickListener(mOnClickListener);
        view.findViewById(R.id.save).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.delete).setOnClickListener(mOnClickListener);
        initPart("#d8d8d8");
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.header_left_btn:
                    EventBus.getDefault().post(new SimpleEvent(Constants.BACK_MAIN_CLOSE));
                    break;
                case R.id.save:
                    if (getPresenter().check()) {
                        Toast.makeText(getActivity(), getPresenter().getToastMsg(), Toast.LENGTH_SHORT).show();
                    } else {
                        showDialog("提示", "确定需要保存？", "确认保存", true);
                    }
                    break;
                case R.id.delete:
                    showDialog("提示", "确定需要放弃么？", "确认放弃", false);
                    break;
                default:
                    break;
            }
        }
    };


    private void initPart(String colors) {
        DisplayMetrics displayMetrics = mUIUtils.getDisplayMetrics(getActivity());
        main.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(displayMetrics.widthPixels / 3,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int i = 0;
        LinearLayout linear = null;
        for (Map.Entry<String, String> entry : mFieldMap.entrySet()) {
            if (i % 3 == 0) {
                linear = new LinearLayout(getActivity());
                main.addView(linear);
                mUIUtils.setViewParams(linear, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            String title = entry.getKey();
            final LineView lineView = new LineView(getActivity());
            lineView.getEditText().showLine(true);
            lineView.setTag(entry.getValue());
            lineView.getEditText().setTextSize(28);
            lineView.getTextView().setTextSize(28);
            lineView.setBackgroundColor(Color.parseColor(colors));
            lineView.getEditText().setLineColor(Color.BLACK);
            lineView.setTwoTexts(title, "");
            if ("planToTakeOffDate".equals(entry.getValue())) {
                lineView.getEditText().setFocusableInTouchMode(false);
                showTimeDialog(lineView.getEditText());
            }
            linear.addView(lineView, lp);
            //必须要控件创建完毕才能设置margin。因为控件在layout都还没确定位置，怎么能设置margin呢？会出现空指针。
            mUIUtils.setViewMargin(lineView, 5, 5, 0, 0);
            i++;
        }


    }


    private void showDialog(String title, String content, String submit, final boolean flag) {
        final SimpleDialogFragment simpleDialogFragment = new SimpleDialogFragment();
        simpleDialogFragment.show(getActivity().getFragmentManager(), "simpleDialogFragment");
        simpleDialogFragment.setTitle(title);
        simpleDialogFragment.setContent(content);
        simpleDialogFragment.setSubmit(submit);
        simpleDialogFragment.setOnButtonClickListener(new IDialogFragment() {
            @Override
            public void onDialogFragmentButtonClickListener() {
                if (flag) {
                    switch (getPresenter().save()){
                        case AddAndEditPresenter.RESULT_SAVE_SUCCESS:
                            break;
                        case AddAndEditPresenter.RESULT_SAVE_TAKE_OFF_DATE_ERROR:
                            Toast.makeText(getActivity(), "计飞时间格式错误!", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }
                } else {
                    EventBus.getDefault().post(new SimpleEvent(Constants.BACK_MAIN_CLOSE));
                }
                simpleDialogFragment.dismiss();
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
                        getActivity(),
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
                dialog.show();
            }
        });
    }

}
