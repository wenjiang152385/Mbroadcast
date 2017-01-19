package com.oraro.mbroadcast.ui.activity;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.dao.PlayEntryDao;
import com.oraro.mbroadcast.listener.IDialogFragment;
import com.oraro.mbroadcast.logicService.GenerateService;
import com.oraro.mbroadcast.mina.client.MinaStringClientThread;
import com.oraro.mbroadcast.model.FlightInfoTemp;
import com.oraro.mbroadcast.model.HistoryFlightTempEdit;
import com.oraro.mbroadcast.model.PlayEntry;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.ui.fragment.SimpleDialogFragment;
import com.oraro.mbroadcast.ui.widget.LineEditText;
import com.oraro.mbroadcast.ui.widget.LineView;
import com.oraro.mbroadcast.utils.FlightInfoUtils;
import com.oraro.mbroadcast.utils.SPUtils;
import com.oraro.mbroadcast.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.query.QueryBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FlightInfoActivity extends FragmentActivity implements View.OnClickListener {

    private RelativeLayout mGroupButton;
    private LinearLayout mMain1PartOne;
    private LinearLayout mMain1PartTwo;
    private LinearLayout mMain1PartThree;

    private LinearLayout mMain2PartOne;
    private LinearLayout mMain2PartTwo;
    private LinearLayout mMain2PartThree;

    private LinearLayout mMain3PartOne;
    private LinearLayout mMain3PartTwo;
    private LinearLayout mMain3PartThree;
    private LinearLayout mMain4;
    private TextView mEditButton;
    private TextView title;

    private FlightInfoUtils flightInfoUtils;
    private List<LineView> mLineViewList = new ArrayList<>();
    private UIUtils mUIUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUIUtils = new UIUtils();
        initViews(savedInstanceState);
        DisplayMetrics displayMetrics = mUIUtils.getDisplayMetrics(this);
        mUIUtils.setViewParams(mMain1PartOne, displayMetrics.widthPixels / 3, displayMetrics.heightPixels / 4);
        mUIUtils.setViewParams(mMain1PartTwo, displayMetrics.widthPixels / 3, displayMetrics.heightPixels / 4);
        mUIUtils.setViewParams(mMain1PartThree, displayMetrics.widthPixels / 3, displayMetrics.heightPixels / 4);

        mUIUtils.setViewParams(mMain2PartOne, displayMetrics.widthPixels / 3, displayMetrics.heightPixels / 8);
        mUIUtils.setViewParams(mMain2PartTwo, displayMetrics.widthPixels / 3, displayMetrics.heightPixels / 8);
        mUIUtils.setViewParams(mMain2PartThree, displayMetrics.widthPixels / 3, displayMetrics.heightPixels / 8);

        mUIUtils.setViewParams(mMain3PartOne, displayMetrics.widthPixels / 3, displayMetrics.heightPixels / 8);
        mUIUtils.setViewParams(mMain3PartTwo, displayMetrics.widthPixels / 3, displayMetrics.heightPixels / 8);
        mUIUtils.setViewParams(mMain3PartThree, displayMetrics.widthPixels / 3, displayMetrics.heightPixels / 8);

        mUIUtils.setViewParams(mMain4, displayMetrics.widthPixels, displayMetrics.heightPixels / 8);

        initData();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_flight_info);
        mEditButton = (TextView) findViewById(R.id.header_right_btn);
        mGroupButton = (RelativeLayout) findViewById(R.id.button_group);
        View main1 = findViewById(R.id.main1);
        mMain1PartOne = (LinearLayout) main1.findViewById(R.id.part1);
        mMain1PartTwo = (LinearLayout) main1.findViewById(R.id.part2);
        mMain1PartThree = (LinearLayout) main1.findViewById(R.id.part3);
        View main2 = findViewById(R.id.main2);
        mMain2PartOne = (LinearLayout) main2.findViewById(R.id.part1);
        mMain2PartTwo = (LinearLayout) main2.findViewById(R.id.part2);
        mMain2PartThree = (LinearLayout) main2.findViewById(R.id.part3);

        View main3 = findViewById(R.id.main3);
        mMain3PartOne = (LinearLayout) main3.findViewById(R.id.part1);
        mMain3PartTwo = (LinearLayout) main3.findViewById(R.id.part2);
        mMain3PartThree = (LinearLayout) main3.findViewById(R.id.part3);
        title = (TextView) findViewById(R.id.header_text);

        mMain4 = (LinearLayout) findViewById(R.id.main4);

        mEditButton.setOnClickListener(this);
        mEditButton.setBackground(null);
        title.setText(R.string.radiogroup2);
        mEditButton.setText(R.string.popuplist_edit);
        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.delete).setOnClickListener(this);
        TextView backBtn = (TextView) findViewById(R.id.header_left_btn);
        backBtn.setOnClickListener(this);
        backBtn.setVisibility(View.VISIBLE);
        startAnim(mGroupButton, R.anim.anim_in, true);
    }

    private void initData() {
        flightInfoUtils = new FlightInfoUtils().getInstance();
        Intent intent = getIntent();
        long id = intent.getLongExtra("info", -1);
        flightInfoUtils.createData(this, id);
        initPart(mMain1PartOne, Constants.FlightInfoConstants.PART_ONE, "#d8d8d8");
        initPart(mMain1PartTwo, Constants.FlightInfoConstants.PART_TWO, "#d8d8d8");
        initPart(mMain1PartThree, Constants.FlightInfoConstants.PART_THREE, "#d8d8d8");

        initPart(mMain2PartOne, Constants.FlightInfoConstants.PART_FOUR, "#e8e8e8");
        initPart(mMain2PartTwo, Constants.FlightInfoConstants.PART_FIVE, "#e8e8e8");
        initPart(mMain2PartThree, Constants.FlightInfoConstants.PART_SIX, "#e8e8e8");

        initPart(mMain3PartOne, Constants.FlightInfoConstants.PART_SEVEN, "#d8d8d8");
        initPart(mMain3PartTwo, Constants.FlightInfoConstants.PART_EIGHT, "#d8d8d8");
        initPart(mMain3PartThree, Constants.FlightInfoConstants.PART_NINE, "#d8d8d8");

        initPart(mMain4, Constants.FlightInfoConstants.PART_TEN, "#e8e8e8");
    }


    private void initPart(LinearLayout linearLayout, String[] titles, String colors) {
        linearLayout.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < titles.length; i++) {
            String title = titles[i];
            final LineView lineView = new LineView(this);
            lineView.getEditText().showLine(true);
            lineView.setTag(titles[i]);
            lineView.getEditText().setTextSize(28);
            lineView.getTextView().setTextSize(28);
            lineView.setBackgroundColor(Color.parseColor(colors));
            lineView.getEditText().setLineColor(Color.BLACK);
            lineView.setTwoTexts(title, flightInfoUtils.getTextByTitle(title));
            if (title.contains("日期")) {
                showDataDialog(lineView.getEditText());
            }
            if (title.contains("计划起飞") || title.contains("计划到达") || title.contains("时间")) {
                lineView.getEditText().setText("00:00");
                showTimeDialog(lineView.getEditText());
            }
            linearLayout.addView(lineView, lp);
            mLineViewList.add(lineView);
            //必须要控件创建完毕才能设置margin。因为控件在layout都还没确定位置，怎么能设置margin呢？会出现空指针。
            mUIUtils.setViewMargin(lineView, 5, 5, 0, 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.header_left_btn:
                hideSoftInput();
                finish();

                break;

            case R.id.header_right_btn:
                startAnim(mGroupButton, R.anim.anim_in, true);
                mEditButton.setClickable(false);
//                finish();
                break;

            case R.id.save:
                prompt();
//                showDialog("提示", "确定需要保存么？", "确认保存", true);
                break;
            case R.id.delete:
                showDialog("提示", "确定需要放弃么？", "确认放弃", false);
                break;
            default:
                break;
        }
    }

    private void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), 0);
        }
    }

    private void prompt() {
        for (int i = 0; i < mLineViewList.size(); i++) {
            final LineView lineView = mLineViewList.get(i);
            String title = lineView.getTextView().getText().toString();
            String edtText = lineView.getEditText().getEditableText().toString();
            if (title.contains("始发站")) {
                if ("".equals(edtText) || "无".equals(edtText)) {
                    Toast.makeText(this, "\"始发站\"不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else if (title.contains("目的站")) {
                if ("".equals(edtText) || "无".equals(edtText)) {
                    Toast.makeText(this, "\"目的站\"不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
            }else if (title.contains("航班号")) {
                if ("".equals(edtText) || "无".equals(edtText)) {
                    Toast.makeText(this, "\"航班号\"不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        showDialog("提示", "确定需要保存么？", "确认保存", true);

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
                    startAnim(mGroupButton, R.anim.anim_out, false);
                    mEditButton.setClickable(true);
                    getFlightInfoTemp();
                    finish();
                } else {
                    startAnim(mGroupButton, R.anim.anim_out, false);
                    mEditButton.setClickable(true);
                    simpleDialogFragment.dismiss();
                    initData();
                    finish();
                }
            }
        });
    }

    private void setEditable(boolean flag) {
        for (int i = 0; i < mLineViewList.size(); i++) {
            LineView lineView = mLineViewList.get(i);
            lineView.getEditText().showLine(flag);
            lineView.getEditText().setFalse(flag);
        }
    }


    private void startAnim(View view, int animId, boolean isShow) {
        Animation animation = AnimationUtils.loadAnimation(this, animId);
        view.startAnimation(animation);
        view.setVisibility(true == isShow ? View.VISIBLE : View.INVISIBLE);
    }


    private void getFlightInfoTemp() {
        FlightInfoTemp flightInfoTemp = flightInfoUtils.getInfo();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        for (int i = 0; i < mLineViewList.size(); i++) {
            final LineView lineView = mLineViewList.get(i);
            String title = lineView.getTextView().getText().toString();
            String edtText = lineView.getEditText().getEditableText().toString();
            if (title.contains("航班号")) {
                flightInfoTemp.setFlightNumber(edtText);
            } else if (title.contains("计划起飞")) {
                try {
                    flightInfoTemp.setPlanToTakeOffDate(sdf.parse(edtText));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else if (title.contains("目的站")) {
                flightInfoTemp.setArrivalStation(edtText);
            }else if (title.contains("计划到达")) {
//                flightInfoTemp.setPlanToArrive(edtText);
            } else if (title.contains("始发站")) {
                flightInfoTemp.setDeparture(edtText);
            } else if (title.contains("目的(3码)")) {
//                flightInfoTemp.setDestinationStation(edtText);
            } else if (title.contains("数据源来自")) {
                flightInfoTemp.setInternationalThreeYard(edtText);
            } else if (title.contains("日期")) {
                try {
                    Date date = sdfDate.parse(edtText);
                    flightInfoTemp.setDate(date);
                } catch (ParseException e) {
                    System.out.println(e.getMessage());
                }

            } else if (title.contains("国际/国内")) {
//                flightInfoTemp.setInternationalOrDomestic(edtText);
            } else if (title.contains("进出港")) {
//                flightInfoTemp.setImportAndExport(edtText);
            } else if (title.contains("性质")) {
//                flightInfoTemp.setProperty(edtText);
            } else if (title.contains("始发(3码)")) {
//                flightInfoTemp.setDepartureStation(edtText);
            } else if (title.contains("登机口")) {
                flightInfoTemp.setBoardingGate(edtText);
            } else if (title.contains("数据来自(国际3码)")) {
                flightInfoTemp.setInternationalThreeYard(edtText);
            } else if (title.contains("经停1(3码)")) {
                flightInfoTemp.setStopOne(edtText);
            } else if (title.contains("经停1降落时间")) {
                try {
                    Date date = sdfTime.parse(edtText);
                    flightInfoTemp.setStopOneFalTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } else if (title.contains("经停1登机口")) {
                flightInfoTemp.setStopOneBoardingGate(edtText);
            } else if (title.contains("经停站1")) {
                flightInfoTemp.setStopStationOne(edtText);
            } else if (title.contains("经停1起飞时间")) {
                try {
                    Date date = sdfTime.parse(edtText);
                    flightInfoTemp.setStopOneDepartureTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (title.contains("经停2(3码)")) {
                flightInfoTemp.setStopTwo(edtText);
            } else if (title.contains("经停2降落时间")) {
                try {
                    Date date = sdfTime.parse(edtText);
                    flightInfoTemp.setStopTwoFalTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (title.contains("经停2登机口")) {
                flightInfoTemp.setStopTwoBoardingGate(edtText);
            } else if (title.contains("经停站2")) {
                flightInfoTemp.setStopStationTwo(edtText);
            } else if (title.contains("经停2起飞时间")) {
                try {
                    Date date = sdfTime.parse(edtText);
                    flightInfoTemp.setStopTwoDepartureTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        flightInfoTemp.update();
        HistoryFlightTempEdit historyFlightTempEdit = new HistoryFlightTempEdit();
        historyFlightTempEdit.setFlightInfoTemp(flightInfoTemp);
        historyFlightTempEdit.setEditDate(new Date());
        DBManager.getInstance(FlightInfoActivity.this).insert(historyFlightTempEdit, DBManager.getInstance(FlightInfoActivity.this).getHistoryFlightTempEditDao(DBManager.WRITE_ONLY));
        EventBus.getDefault().post(new SimpleEvent(Constants.Analytic_Cmpletion_Notice));
        DBManager manager = DBManager.getInstance(this);
        // FlightInfoTempDao tempDao =  DBManager.getInstance(this).getFlightInfoTempDao(DBManager.READ_ONLY);
        // PlayEntryDao playEntryDao = manager.getPlayEntryDao(DBManager.WRITE_ONLY);
        PlayEntryDao dao = manager.getPlayEntryDao(DBManager.READ_ONLY);
        QueryBuilder qb = dao.queryBuilder();
        //  List<PlayEntry> list = manager.queryBySQL(dao, " where  PLAY_ENTRY_ID = " + flightInfoTemp.getId());
        List<PlayEntry> list = qb.where(PlayEntryDao.Properties.PlayEntryId.eq(flightInfoTemp.getId()), PlayEntryDao.Properties.PlayEntryId.eq(flightInfoTemp.getId())).list();
        Log.e("BaseFlightFragment", "list  size = " + list.size());
        PlayEntryDao dao2 = manager.getPlayEntryDao(DBManager.WRITE_ONLY);
        manager.deleteList(list, dao2);
        GenerateService service = new GenerateService();
        service.generatePlay(flightInfoTemp);
        MinaStringClientThread minaStringClientThread = new MinaStringClientThread();
        MinaStringClientThread minaStringClientThread1 = new MinaStringClientThread();
        MinaStringClientThread minaStringClientThread2 = new MinaStringClientThread();
        MinaStringClientThread minaStringClientThread3 = new MinaStringClientThread();
        minaStringClientThread.setType(Constants.A_FLIGHT_UPDATE);
        minaStringClientThread1.setType(Constants.A_FLIGHT_UPDATE);
        minaStringClientThread2.setType(Constants.A_FLIGHT_UPDATE);
        minaStringClientThread3.setType(Constants.A_FLIGHT_UPDATE);
        minaStringClientThread.setFlightInfoTemp(flightInfoTemp);
        minaStringClientThread1.setFlightInfoTemp(flightInfoTemp);
        minaStringClientThread2.setFlightInfoTemp(flightInfoTemp);
        minaStringClientThread3.setFlightInfoTemp(flightInfoTemp);
        if (SPUtils.hasKey(this, "ip1")) {
            if (!SPUtils.getPrefString(this, "ip1", "-1").equals("-1")) {
                minaStringClientThread.setIp(SPUtils.getPrefString(this, "ip1", "-1"));
                MinaStringClientThread.getThreadPoolExecutor().execute(minaStringClientThread);
            }
        }
        if (SPUtils.hasKey(this, "ip2")) {
            if (!SPUtils.getPrefString(this, "ip2", "-1").equals("-1")) {
                minaStringClientThread1.setIp(SPUtils.getPrefString(this, "ip2", "-1"));
                MinaStringClientThread.getThreadPoolExecutor().execute(minaStringClientThread1);
            }
        }
        if (SPUtils.hasKey(this, "ip3")) {
            if (!SPUtils.getPrefString(this, "ip3", "-1").equals("-1")) {
                minaStringClientThread2.setIp(SPUtils.getPrefString(this, "ip3", "-1"));
                MinaStringClientThread.getThreadPoolExecutor().execute(minaStringClientThread2);
            }
        }
        if (SPUtils.hasKey(this, "ip4")) {
            if (!SPUtils.getPrefString(this, "ip4", "-1").equals("-1")) {
                minaStringClientThread3.setIp(SPUtils.getPrefString(this, "ip4", "-1"));
                MinaStringClientThread.getThreadPoolExecutor().execute(minaStringClientThread3);
            }
        }
        EventBus.getDefault().post(new SimpleEvent(Constants.Analytic_Cmpletion_Notice));
        //  DBManager.getInstance(MBroadcastApplication.getMyContext()).delete(flightInfoTemp, DBManager.getInstance(MBroadcastApplication.getMyContext()).getFlightInfoTempDao(DBManager.WRITE_ONLY));
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
                        FlightInfoActivity.this,
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

    private void showDataDialog(final LineEditText editText) {
        editText.setFocusableInTouchMode(false);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date = sdf.parse(editText.getEditableText().toString());
                    c.setTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Dialog dialog = new DatePickerDialog(
                        FlightInfoActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {
                                Log.e("dy", "您选择了：" + year + "年" + (month + 1) + "月" + dayOfMonth + "日");
                                Log.e("dy", " " + dp.toString());
                                editText.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                            }
                        },
                        c.get(Calendar.YEAR), // 传入年份
                        c.get(Calendar.MONTH), // 传入月份
                        c.get(Calendar.DAY_OF_MONTH) // 传入天数
                );
                hideSoftInput();
                dialog.show();
            }
        });
    }

}
