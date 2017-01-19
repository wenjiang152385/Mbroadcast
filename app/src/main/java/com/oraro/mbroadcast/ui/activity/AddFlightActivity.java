package com.oraro.mbroadcast.ui.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.logicService.GenerateService;
import com.oraro.mbroadcast.model.FlightInfo;
import com.oraro.mbroadcast.model.FlightInfoTemp;
import com.oraro.mbroadcast.model.OrderView;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.ui.widget.LineEditText;
import com.oraro.mbroadcast.ui.widget.LineView;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class AddFlightActivity extends Activity implements View.OnClickListener {
    private LineEditText mDepartText;
    private LineEditText mArriveText;
    private LineView mDepartTime;
    private LineView mArriveTime;
    private LinearLayout mScrollView;
    private FrameLayout mTransferLayout;
    private FrameLayout mFlightInfo;
    private ImageView imageInfo;

    private boolean flag = false;
    private static int mIndex = 0;

    private enum animStatus {IN, OUT}

    private animStatus mCurrentStatus = animStatus.IN;
    private List<OrderView> mTransferList = new ArrayList<>();
    private List<OrderView> mLineViewList = new ArrayList<>();

    private static final int TRANSFER = 1;
    private static final int INFO = 2;
    private static final int titleSize = 64;
    private static final int timeSize = 22;
    private static final String[] titles1 = {"航班号", "日期", "国际/国内"};
    private static final String[] titles2 = {"进出港", "性质", "始发(3码)"};
    private static final String[] titles3 = {"始发登机口", "数据来自(国际3码)"};
    private static String[] edits1 = {"经停(3码)", "经停降落时间", "经停登机口"};
    private static String[] edits2 = {"经停站", "经停起飞时间"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews(savedInstanceState);
        initFlightView();
        setData();
        setQuality();
        setNormalEdit(true);
    }

    private void setData() {
        mDepartText.setHint("LAX");
        mArriveText.setHint("JFK");
        mDepartTime.setTwoTexts("出发", null);
        mDepartTime.getEditText().setHint("00:00");
        mArriveTime.setTwoTexts("到达", null);
        mArriveTime.getEditText().setHint("00:00");
        mDepartTime.getEditText().setFocusableInTouchMode(false);
        mArriveTime.getEditText().setFocusableInTouchMode(false);
        mDepartTime.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              setTime( mDepartTime.getEditText());
            }
        });
        mArriveTime.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime( mArriveTime.getEditText());
            }
        });
    }

private  void setTime(final  EditText editText){
    Calendar   c=Calendar.getInstance();
    Dialog dialog=new TimePickerDialog(
            AddFlightActivity.this,
            new TimePickerDialog.OnTimeSetListener(){
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    editText.setText(hourOfDay+":"+minute);
                }
            },
            c.get(Calendar.HOUR_OF_DAY),
            c.get(Calendar.MINUTE),
            false
    );
    dialog.show();

}
    private void setQuality() {
        mDepartText.setTextSize(titleSize);
        mArriveText.setTextSize(titleSize);
        mArriveText.setLineColor(Color.BLACK);
        mDepartText.setLineColor(Color.BLACK);

        mDepartTime.getTextView().setTextSize(timeSize);
        mDepartTime.getEditText().setTextSize(timeSize);
        mDepartTime.getEditText().setLineColor(Color.BLACK);
        mArriveTime.getTextView().setTextSize(timeSize);
        mArriveTime.getEditText().setTextSize(timeSize);
        mArriveTime.getEditText().setLineColor(Color.BLACK);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIndex = 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_transfer:
                if (mTransferList.size() > 1) {
                    Toast.makeText(AddFlightActivity.this, "暂时支持2条数据", Toast.LENGTH_SHORT).show();
                } else {
                    addTransfer();
                    initTransfer();
                }
                if (flag) {
                    closeAnim();
                }
                break;

            case R.id.airplane:
                changeStatus(mCurrentStatus);
                break;

            case R.id.header_left_btn:
                finish();
                break;

            case R.id.header_right_btn:
                getData();
                break;

            default:
                break;
        }
    }

    private void changeStatus(animStatus status) {
        switch (status) {
            case IN:
                startAnim(imageInfo, R.anim.anim_rotate_in, true, INFO);
                mCurrentStatus = animStatus.OUT;
                mFlightInfo.setVisibility(View.VISIBLE);
                startAnim(mFlightInfo, R.anim.anim_translate_in, true, INFO);
                flag = true;
                break;
            case OUT:
                closeAnim();
                break;
        }
    }

    private void closeAnim() {
        startAnim(imageInfo, R.anim.anim_rotate_out, true, INFO);
        mCurrentStatus = animStatus.IN;
        startAnim(mFlightInfo, R.anim.anim_translate_out, false, INFO);
        flag = false;
    }

    private void initFlightView() {
        LinearLayout line1 = (LinearLayout) mFlightInfo.findViewById(R.id.line1);
        LinearLayout line2 = (LinearLayout) mFlightInfo.findViewById(R.id.line2);
        LinearLayout line3 = (LinearLayout) mFlightInfo.findViewById(R.id.line3);
        initParts(line1, titles1, Color.WHITE, -1);
        initParts(line2, titles2, Color.WHITE, -1);
        initParts(line3, titles3, Color.WHITE, -1);
    }

    private void initParts(LinearLayout linearLayout, String[] titles, int color, int id) {
        linearLayout.removeAllViews();
        LinearLayout.LayoutParams lp;
        if (titles.length != 2) {
            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        } else {
            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        for (int i = 0; i < titles.length; i++) {
            String title = titles[i];
            final LineView lineView = new LineView(this);
            lineView.getEditText().setLineColor(color);
            lineView.getTextView().setTextColor(color);
            lineView.getEditText().setTextColor(color);
            lineView.getEditText().setHint("请输入" + title);
            lineView.setTwoTexts(title, null);
            linearLayout.addView(lineView, lp);
            if (title.contains("日期") || title.contains("经停起飞时间") || title.contains("经停降落时间")) {
                lineView.getEditText().setFocusableInTouchMode(false);
                lineView.getEditText().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar c = Calendar.getInstance();
                        Dialog dialog = new DatePickerDialog(
                                AddFlightActivity.this,
                                new DatePickerDialog.OnDateSetListener() {
                                    public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {
                                        Log.e("dy", "您选择了：" + year + "年" + (month + 1) + "月" + dayOfMonth + "日");
                                        Log.e("dy", " " + dp.toString());
                                        lineView.getEditText().setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                                    }
                                },
                                c.get(Calendar.YEAR), // 传入年份
                                c.get(Calendar.MONTH), // 传入月份
                                c.get(Calendar.DAY_OF_MONTH) // 传入天数
                        );
                        dialog.show();
                    }
                });
            }
            if ("数据来自(国际3码)".equals(title)) {
                setViewMargin(lineView, 60, 0, 110, 20);
            } else {
                setViewMargin(lineView, 60, 0, 0, 20);
            }
            OrderView orderView = new OrderView();
            orderView.setId(id);
            orderView.setViewGroup(lineView);
            mLineViewList.add(orderView);
        }
        setEdit(true);

    }


    private void initTransfer() {
        LinearLayout part1 = (LinearLayout) mTransferLayout.findViewById(R.id.part1);
        LinearLayout part2 = (LinearLayout) mTransferLayout.findViewById(R.id.part2);
        initParts(part1, edits1, Color.WHITE, mIndex);
        initParts(part2, edits2, Color.WHITE, mIndex);
        mIndex++;
        TextView text_title = (TextView) mTransferLayout.findViewById(R.id.title);
        text_title.setText("TRANSPORTATION");
        final ImageView img_delete = (ImageView) mTransferLayout.findViewById(R.id.delete);
        img_delete.setTag(mTransferLayout.getTag());
        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(AddFlightActivity.this, "正在开发中", Toast.LENGTH_LONG).show();

                int imgId = (int) img_delete.getTag();
                Iterator<OrderView> it = mLineViewList.iterator();
                while (it.hasNext()) {
                    OrderView orderView = it.next();
                    if (imgId == orderView.getId()) {
                        it.remove();
                    }
                }

                OrderView deleteView = null;
                for (int i = 0; i < mTransferList.size(); i++) {
                    deleteView = mTransferList.get(i);
                    if (deleteView.getId() == imgId) {
                        FrameLayout deleteTransfer = (FrameLayout) deleteView.getViewGroup();
                        startAnim(deleteTransfer, R.anim.anim_scale_out, false, TRANSFER);
                        mTransferList.remove(deleteView);
                    }
                }


            }
        });
    }


    private LayoutInflater getInflater() {
        LayoutInflater inflater = LayoutInflater.from(this);
        return inflater;
    }

    private void addTransfer() {
        OrderView orderView = new OrderView();
        FrameLayout transferLayout = (FrameLayout) getInflater().inflate(R.layout.add_transfer, null, false);
        transferLayout.setVisibility(View.INVISIBLE);
        transferLayout.setTag(mIndex);
        orderView.setId(mIndex);
        orderView.setViewGroup(transferLayout);
        mTransferLayout = transferLayout;
        mScrollView.addView(transferLayout);
        setViewMargin(transferLayout, 20, 20, 20, 20);
        startAnim(transferLayout, R.anim.anim_scale_in, true, TRANSFER);
        mTransferList.add(orderView);
    }


    private void startAnim(final View view, int animId, final boolean isShow, final int type) {
        Animation animation = AnimationUtils.loadAnimation(this, animId);
        view.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(true == isShow ? View.VISIBLE : View.INVISIBLE);
                if (!isShow && TRANSFER == type) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            mScrollView.removeView(view);
                        }
                    });
                } else if (!isShow && INFO == type) {
                    view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void setViewMargin(View view, int left, int top, int right, int bottom) {
        ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).setMargins(left, top, right, bottom);
    }


    private void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add);
        View subLayout1 = findViewById(R.id.item_depart);
        View subLayout2 = findViewById(R.id.item_arrive);
        mDepartText = (LineEditText) subLayout1.findViewById(R.id.location);
        mArriveText = (LineEditText) subLayout2.findViewById(R.id.location);
        mDepartTime = (LineView) subLayout1.findViewById(R.id.time);
        mArriveTime = (LineView) subLayout2.findViewById(R.id.time);
        mScrollView = (LinearLayout) findViewById(R.id.transfers);
        mFlightInfo = (FrameLayout) findViewById(R.id.flight_info);
        setViewMargin(mFlightInfo, 20, 0, 20, 0);
        imageInfo = (ImageView) findViewById(R.id.airplane);

        imageInfo.setOnClickListener(this);
        findViewById(R.id.add_transfer).setOnClickListener(this);
        findViewById(R.id.header_left_btn).setOnClickListener(this);
        findViewById(R.id.header_right_btn).setOnClickListener(this);
    }

    private void setNormalEdit(boolean flag) {
        mDepartText.showLine(flag);
        mArriveText.showLine(flag);
        mDepartTime.getEditText().showLine(flag);
        mArriveTime.getEditText().showLine(flag);

    }


    private void setEdit(boolean flag) {
        for (int i = 0; i < mLineViewList.size(); i++) {
            ((LineView) mLineViewList.get(i).getViewGroup()).setLineEdtEdited(flag);
        }
    }


    private void getData() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        FlightInfoTemp flightInfoTemp = new FlightInfoTemp();
        FlightInfo flightInfo = new FlightInfo();
        flightInfo.setDeparture(mDepartText.getText().toString());
        flightInfo.setArrivalStation(mArriveText.getText().toString());
        try {
            flightInfo.setPlanToTakeOffDate(sdf.parse(mDepartTime.getEditText().getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        flightInfo.setArrivalStation(mArriveTime.getEditText().getText().toString());
        flightInfoTemp.setDeparture(mDepartText.getText().toString());
        flightInfoTemp.setArrivalStation(mArriveText.getText().toString());
        try {
            flightInfoTemp.setPlanToTakeOffDate(sdf.parse(mDepartTime.getEditText().getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        flightInfoTemp.setPlanToArrive(mArriveTime.getEditText().getText().toString());
        for (int i = 0; i < mLineViewList.size(); i++) {
            OrderView edt = mLineViewList.get(i);
            try {
                checkData(flightInfo, flightInfoTemp, edt);
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("dy",e.toString());

            }
        }
        flightInfoTemp.setFlightInfo(flightInfo);
        GenerateService generateService=new GenerateService();
        generateService.insertAndGeneratePlay(flightInfo);
        EventBus.getDefault().post(new SimpleEvent(Constants.UPDATE_FLIGTH));
        finish();
    }


    private void checkData(FlightInfo flightInfo, FlightInfoTemp flightInfoTemp, OrderView orderView) throws ParseException {
        LineView edt = (LineView) orderView.getViewGroup();
        int id = orderView.getId();
        String title = edt.getTextView().getText().toString();
        String edtText = edt.getEditText().getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (title.contains("航班号")) {
            flightInfo.setFlightNumber(edtText);
            flightInfoTemp.setFlightNumber(edtText);
        } else if (title.contains("日期")) {
            Date date = sdf.parse(edtText);
            flightInfo.setDate(date);
            flightInfoTemp.setDate(date);
        } else if (title.contains("国际/国内")) {
//            flightInfo.setInternationalOrDomestic(edtText);
//            flightInfoTemp.setInternationalOrDomestic(edtText);
        } else if (title.contains("进出港")) {
//            flightInfo.setImportAndExport(edtText);
//            flightInfoTemp.setImportAndExport(edtText);
        } else if (title.contains("性质")) {
//            flightInfo.setProperty(edtText);
//            flightInfoTemp.setProperty(edtText);
        } else if (title.contains("始发(3码)")) {
//            flightInfo.setDepartureStation(edtText);
//            flightInfoTemp.setDepartureStation(edtText);
        } else if (title.contains("始发登机口")) {
            flightInfo.setBoardingGate(edtText);
            flightInfoTemp.setBoardingGate(edtText);
        } else if (title.contains("数据来自(国际3码)")) {
            flightInfo.setInternationalThreeYard(edtText);
            flightInfoTemp.setInternationalThreeYard(edtText);
        } else if (title.contains("经停(3码)")) {
            if (id % 2 != 0) {
                flightInfo.setStopOne(edtText);
                flightInfoTemp.setStopOne(edtText);
            } else {
                flightInfo.setStopTwo(edtText);
                flightInfoTemp.setStopTwo(edtText);
            }
        } else if (title.contains("经停降落时间")) {
            if (id % 2 != 0) {
                Date date = sdf.parse(edtText);
                flightInfo.setStopOneFalTime(date);
                flightInfoTemp.setStopOneFalTime(date);
            } else {
                Date date = sdf.parse(edtText);
                flightInfo.setStopTwoFalTime(date);
                flightInfoTemp.setStopTwoFalTime(date);
            }
        } else if (title.contains("经停登机口")) {
            if (id % 2 != 0) {
                flightInfo.setStopOneBoardingGate(edtText);
                flightInfoTemp.setStopOneBoardingGate(edtText);
            } else {
                flightInfo.setStopTwoBoardingGate(edtText);
                flightInfoTemp.setStopTwoBoardingGate(edtText);
            }
        } else if (title.contains("经停站")) {
            if (id % 2 != 0) {
                flightInfo.setStopStationOne(edtText);
                flightInfoTemp.setStopStationOne(edtText);
            } else {
                flightInfo.setStopStationTwo(edtText);
                flightInfoTemp.setStopStationTwo(edtText);
            }
        } else if (title.contains("经停起飞时间")) {
            if (id % 2 != 0) {
                Date date = sdf.parse(edtText);
                flightInfo.setStopOneDepartureTime(date);
                flightInfoTemp.setStopOneDepartureTime(date);
            } else {
                Date date = sdf.parse(edtText);
                flightInfo.setStopTwoDepartureTime(date);
                flightInfoTemp.setStopTwoDepartureTime(date);
            }
        }
    }
}

