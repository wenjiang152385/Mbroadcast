package com.oraro.mbroadcast.ui.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.listener.IDialogFragment;
import com.oraro.mbroadcast.logicService.GenerateService;
import com.oraro.mbroadcast.model.AddFliDataControl;
import com.oraro.mbroadcast.model.FlightInfo;
import com.oraro.mbroadcast.model.FlightInfoTemp;
import com.oraro.mbroadcast.model.HistoryFlightTempEdit;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.ui.activity.MainActivity;
import com.oraro.mbroadcast.ui.widget.LineView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/11/28 0028.
 *
 * @author jiang wen[佛祖保佑 永无BUG]
 */
public class AddFlightFragment extends Fragment {
    private LineView mDepartText;
    private LineView mArriveText;
    private LineView mDepartTime;
    private LineView mArriveTime;
    private LinearLayout mScrollView;
    private FrameLayout mTransferLayout;
    private FrameLayout mFlightInfo;
    private ImageView imageInfo;

    private boolean flag = false;
    private static int mIndex = 0;
    private TextView right_btn;
    private MainActivity mActivity;

    private enum animStatus {IN, OUT}

    private AddFlightFragment. animStatus mCurrentStatus = animStatus.IN;
    private List<LineView> mFootViewList = new ArrayList<>();
    private List<LineView> mHeadViewList = new ArrayList<>();
    private List<LineView> mBodyViewList = new ArrayList<>();
    private List<FrameLayout> mTransfersList = new ArrayList<>();
    private static final int TRANSFER = 1;
    private static final int INFO = 2;
    private static final int titleSize = 64;
    private static final int timeSize = 22;
    private static final String[] title = {"originating", "planToTakeOff", "objective", "planToArrive"};
    private static final String[] titles1 = {"航班号", "日期", "国际/国内"};
    private static final String[] titles2 = {"进出港", "性质", "始发(3码)"};
    private static final String[] titles3 = {"始发登机口", "目的(3码)"};
    private static final String[] titles4 = {"数据来自(国际3码)"};
    private static String[] edits1 = {"经停(3码)", "经停降落时间", "经停登机口"};
    private static String[] edits2 = {"经停站", "经停起飞时间"};


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_add,null);
        initView(view);
        initBodyView(view);
        return  view;
    }

    private void initBodyView(View view) {
        LinearLayout line1 = (LinearLayout) mFlightInfo.findViewById(R.id.line1);
        LinearLayout line2 = (LinearLayout) mFlightInfo.findViewById(R.id.line2);
        LinearLayout line3 = (LinearLayout) mFlightInfo.findViewById(R.id.line3);
        LinearLayout line4 = (LinearLayout) mFlightInfo.findViewById(R.id.line4);
        initBodyParts(line1, titles1, Color.WHITE, -1);
        initBodyParts(line2, titles2, Color.WHITE, -1);
        initBodyParts(line3, titles3, Color.WHITE, -1);
        initBodyParts(line4, titles4, Color.WHITE, -1);
        setHeadViewData();
        setHeadViewQuality();
    }

    private void initView(View view) {
        View subLayout1 = view.findViewById(R.id.item_depart);
        View subLayout2 = view.findViewById(R.id.item_arrive);
        mDepartText = (LineView) subLayout1.findViewById(R.id.location);
        mArriveText = (LineView) subLayout2.findViewById(R.id.location);
        mDepartTime = (LineView) subLayout1.findViewById(R.id.time);
        mArriveTime = (LineView) subLayout2.findViewById(R.id.time);
        mScrollView = (LinearLayout)view.findViewById(R.id.transfers);
        mFlightInfo = (FrameLayout)view.findViewById(R.id.flight_info);
        setViewMargin(mFlightInfo, 20, 0, 20, 0);
        imageInfo = (ImageView)view.findViewById(R.id.airplane);

        imageInfo.setOnClickListener(mOnClickListener);
        view.findViewById(R.id.add_transfer).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.header_left_btn).setOnClickListener(mOnClickListener);
        right_btn = (TextView)view.findViewById(R.id.header_right_btn);
        right_btn.setVisibility(View.VISIBLE);
        right_btn.setOnClickListener(mOnClickListener);

    }
    private void setHeadViewData() {
        mDepartText.getEditText().setHint("LAX");
        mArriveText.getEditText().setHint("JFK");
        mDepartTime.setTwoTexts("出发", null);
        mDepartTime.getEditText().setHint("00:00");
        mArriveTime.setTwoTexts("到达", null);
        mArriveTime.getEditText().setHint("00:00");
        mDepartTime.getEditText().setFocusableInTouchMode(false);
        mArriveTime.getEditText().setFocusableInTouchMode(false);

        mDepartText.setTag(title[0]);
        mDepartTime.setTag(title[1]);
        mArriveText.setTag(title[2]);
        mArriveTime.setTag(title[3]);

        mHeadViewList.add(mDepartText);
        mHeadViewList.add(mArriveText);
        mHeadViewList.add(mDepartTime);
        mHeadViewList.add(mArriveTime);

        mDepartTime.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime(mDepartTime);
            }
        });
        mArriveTime.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime(mArriveTime);
            }
        });
    }

    private void setTime(final LineView lineView) {
        Calendar c = Calendar.getInstance();
        Dialog dialog = new TimePickerDialog(
                mActivity,
                new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String min = minute + "";
                        if (minute < 10) {
                            min = "0" + min;
                        }
                        lineView.getEditText().setText(hourOfDay + ":" + min);
                    }
                },
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                false
        );
        dialog.show();

    }

    private void setHeadViewQuality() {
        mDepartText.getEditText().setTextSize(titleSize);
        mArriveText.getEditText().setTextSize(titleSize);
        mArriveText.getEditText().setLineColor(Color.BLACK);
        mDepartText.getEditText().setLineColor(Color.BLACK);

        mDepartTime.getTextView().setTextSize(timeSize);
        mDepartTime.getEditText().setTextSize(timeSize);
        mDepartTime.getEditText().setLineColor(Color.BLACK);
        mArriveTime.getTextView().setTextSize(timeSize);
        mArriveTime.getEditText().setTextSize(timeSize);
        mArriveTime.getEditText().setLineColor(Color.BLACK);

        mDepartText.getEditText().showLine(true);
        mArriveText.getEditText().showLine(true);
        mDepartTime.getEditText().showLine(true);
        mArriveTime.getEditText().showLine(true);
    }

    @Override
    public void onAttach(Activity activity) {
        mActivity = (MainActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIndex = 0;
//        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
    private View.OnClickListener mOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.add_transfer:
                    if (mTransfersList.size() <= 1) {
                        addTransfers();
                        if (flag) {
                            closeAnim();
                        }
                    } else {
                        Toast.makeText(mActivity, "暂时支持两个经停站", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case R.id.airplane:
                    changeStatus(mCurrentStatus);
                    break;

                case R.id.header_left_btn:
                    EventBus.getDefault().post(new SimpleEvent(Constants.BACK_MAIN_CLOSE));
                    break;

                case R.id.header_right_btn:
                    FlightInfo flightInfo = new FlightInfo();
                    new AddFliDataControl().getHeadData(flightInfo, mHeadViewList);
                    new AddFliDataControl().getBodyData(flightInfo, mBodyViewList);
                    new AddFliDataControl().getFootData(flightInfo, mFootViewList, mTransfersList);
                    if (new AddFliDataControl().checkText(mHeadViewList) && new AddFliDataControl().checkText(mBodyViewList)) {
                        Date date = flightInfo.getDate();
                        Date date2 = flightInfo.getPlanToTakeOffDate();
                        flightInfo.setPlanToTakeOffDate(new Date(date.getYear(), date.getMonth(), date.getDate(), date2.getHours(), date2.getMinutes(), 0));
                        showDialogFragment(flightInfo);
                    } else {
                        Toast.makeText(mActivity, "航班信息不可为空", Toast.LENGTH_LONG).show();
                    }
                    break;

                default:
                    break;
            }
        }
    };
    private void showDialogFragment(final FlightInfo flightInfo) {
        final SimpleDialogFragment simpleDialogFragment = new SimpleDialogFragment();
        simpleDialogFragment.show(mActivity.getFragmentManager(), "simpleDialogFragment");
        simpleDialogFragment.setTitle("提示");
        simpleDialogFragment.setContent("确定需要提交吗？");
        simpleDialogFragment.setSubmit("确定");
        simpleDialogFragment.setOnButtonClickListener(new IDialogFragment() {
            @Override
            public void onDialogFragmentButtonClickListener() {
                GenerateService generateService = new GenerateService();
                generateService.insertAndGeneratePlay(flightInfo);
                FlightInfoTemp flightInfoTemp = DBManager.getInstance(mActivity).queryByFlightInfoPid(flightInfo.getId());
                HistoryFlightTempEdit historyFlightTempEdit = new HistoryFlightTempEdit();
                historyFlightTempEdit.setFlightInfoTempPid(flightInfoTemp.getId());
                historyFlightTempEdit.setEditDate(new Date());
                DBManager.getInstance(mActivity).insert(historyFlightTempEdit, DBManager.getInstance(mActivity).getHistoryFlightTempEditDao(DBManager.WRITE_ONLY));
                EventBus.getDefault().post(new SimpleEvent(Constants.UPDATE_FLIGTH));
                EventBus.getDefault().post(new SimpleEvent(Constants.Analytic_Cmpletion_Notice));
                simpleDialogFragment.dismiss();
                EventBus.getDefault().post(new SimpleEvent(Constants.BACK_MAIN_CLOSE));

            }
        });
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
    private void addTransfers() {
        final FrameLayout transferLayout = (FrameLayout) getInflater().inflate(R.layout.add_transfer, null, false);
        ImageView delete = (ImageView) transferLayout.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mTransfersList.size(); i++) {
                    FrameLayout deleteView = mTransfersList.get(i);
                    if (transferLayout == deleteView) {
                        startAnim(deleteView, R.anim.anim_scale_out, false, TRANSFER);
                        mTransfersList.remove(deleteView);
                    }

                    int deleteId = (int) deleteView.getTag();
                    Iterator<LineView> it = mFootViewList.iterator();
                    while (it.hasNext()) {
                        LineView lineView = it.next();
                        if (deleteId == lineView.getId()) {
                            it.remove();
                        }
                    }
                }
                updateFootLayout();

            }
        });
        transferLayout.setVisibility(View.INVISIBLE);
        transferLayout.setTag(mIndex);
        mTransferLayout = transferLayout;
        mScrollView.addView(transferLayout);
        setViewMargin(transferLayout, 20, 20, 20, 20);
        startAnim(transferLayout, R.anim.anim_scale_in, true, TRANSFER);
        mTransfersList.add(transferLayout);
        initTransfers(transferLayout);
        updateFootLayout();
        mIndex++;
    }
    private void initTransfers(FrameLayout transferLayout) {
        LinearLayout part1 = (LinearLayout) transferLayout.findViewById(R.id.part1);
        LinearLayout part2 = (LinearLayout) transferLayout.findViewById(R.id.part2);
        int index = (int) transferLayout.getTag();
        initParts(part1, edits1, Color.WHITE, index);
        initParts(part2, edits2, Color.WHITE, index);

    }

    private void initParts(LinearLayout linearLayout, String[] titles, int color, int index) {
        linearLayout.removeAllViews();
        LinearLayout.LayoutParams lp;
        lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        for (int i = 0; i < titles.length; i++) {
            String title = titles[i];
            final LineView lineView = new LineView(getActivity());
            lineView.setLineEdtEdited(true);
            lineView.setId(mIndex);
            lineView.setTag(title);
            lineView.getEditText().setLineColor(color);
            lineView.getTextView().setTextColor(color);
            lineView.getEditText().setTextColor(color);
            lineView.getEditText().setHint("请输入" + title);
            lineView.setTwoTexts(title, null);
            linearLayout.addView(lineView, lp);
            if (title.contains("经停起飞时间") || title.contains("经停降落时间")) {
                lineView.getEditText().setFocusableInTouchMode(false);
                lineView.getEditText().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setTime(lineView);
                    }
                });
            }
            setViewMargin(lineView, 60, 0, 0, 20);
            mFootViewList.add(lineView);
        }
    }
    private LayoutInflater getInflater() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        return inflater;
    }


    private void updateFootLayout() {
        for (int i = 0; i < mTransfersList.size(); i++) {
            FrameLayout transferLayout = mTransfersList.get(i);
            TextView title = (TextView) transferLayout.findViewById(R.id.title);
            title.setText("经停" + (i + 1));
        }
    }

    private void startAnim(final View view, int animId, final boolean isShow, final int type) {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), animId);
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

    private void initBodyParts(LinearLayout linearLayout, String[] titles, int color, int id) {
        linearLayout.removeAllViews();
        LinearLayout.LayoutParams lp;
        if (titles.length > 2) {
            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        } else {
            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        for (int i = 0; i < titles.length; i++) {
            String title = titles[i];
            final LineView lineView = new LineView(getActivity());
            lineView.setTag(title);
            lineView.setLineEdtEdited(true);
            lineView.getEditText().setLineColor(color);
            lineView.getTextView().setTextColor(color);
            lineView.getEditText().setTextColor(color);
            lineView.getEditText().setHint("请输入" + title);
            lineView.setTwoTexts(title, null);
            linearLayout.addView(lineView, lp);

            if (title.equals(titles1[1])) {
                lineView.getEditText().setFocusableInTouchMode(false);
                lineView.getEditText().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar c = Calendar.getInstance();
                        Dialog dialog = new DatePickerDialog(getActivity(),
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

            if ("目的(3码)".equals(title)) {
                setViewMargin(lineView, 60, 0, 160, 20);
            } else {
                setViewMargin(lineView, 60, 0, 0, 20);
            }

            mBodyViewList.add(lineView);
        }
    }

}
