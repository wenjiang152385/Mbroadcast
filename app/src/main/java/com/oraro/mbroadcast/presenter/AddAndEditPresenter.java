package com.oraro.mbroadcast.presenter;

import android.os.Bundle;
import android.util.Log;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.logicService.GenerateService;
import com.oraro.mbroadcast.model.FlightInfo;
import com.oraro.mbroadcast.model.HistoryFlightTempEdit;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.ui.fragment.AddAndEditFragment;
import com.oraro.mbroadcast.ui.widget.LineView;
import com.oraro.mbroadcast.utils.BeanUtil;
import com.oraro.mbroadcast.utils.DateUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.Map;

import nucleus.presenter.RxPresenter;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/11/28 0028.
 */
public class AddAndEditPresenter extends RxPresenter<AddAndEditFragment> {
    public static final int RESULT_SAVE_SUCCESS = 1;
    public static final int RESULT_SAVE_TAKE_OFF_DATE_ERROR = 2;

    private AddAndEditFragment mAddAndEditFragment;
    private FlightInfo mFlightInfo;
    //必输项限制
    public static String[] str = {"planToTakeOffDate", "flightNumber"};
    public static String[] str1 = {"计飞", "航班号"};

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
    }

    public boolean check() {
        boolean flag = false;
        for (int i = 0; i < str.length; i++) {
            LineView lineView = (LineView) mAddAndEditFragment.main.findViewWithTag(str[i]);
            if ("".equals(lineView.getEditText().getText().toString()) || null == lineView.getEditText().getText()) {
                flag = true;
            }
        }
        return flag;
    }

    public String getToastMsg() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str1.length; i++) {
            if (i == str1.length - 1)
                sb.append(str1[i] + "不能为空");
            else
                sb.append(str1[i] + "且");
        }
        return sb.toString();
    }

    @Override
    protected void onTakeView(AddAndEditFragment addAndEditFragment) {
        mAddAndEditFragment = addAndEditFragment;
        Bundle bundle = addAndEditFragment.getArguments();
        long id = -1;
        if (null != bundle) {
            id = bundle.getLong("info", -1);
        }
//        Log.e("wjq", "id111111 = " + id);
        rxQueryById(addAndEditFragment, id);
        super.onTakeView(addAndEditFragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFlightInfo = null;
        if(mSubscription != null){
            mSubscription.unsubscribe();
        }
    }

    public int save() {
//        Log.e("wjq", "id = " + mFlightInfo.getId());
        for (Map.Entry<String, String> entry : mAddAndEditFragment.mFieldMap.entrySet()) {
            LineView lineView = (LineView) mAddAndEditFragment.main.findViewWithTag(entry.getValue());
            BeanUtil.invokeGet(mFlightInfo, entry.getValue());
            if (entry.getValue().equals("planToTakeOffDate")){
                try {
                    BeanUtil.invokeSet(mFlightInfo, entry.getValue(),
                            DateUtils.formatInsertDate(mFlightInfo, lineView.getEditText().getText().toString()));
                }catch (Exception e){
                    e.printStackTrace();
                    return RESULT_SAVE_TAKE_OFF_DATE_ERROR;
                }
            }else{
                BeanUtil.invokeSet(mFlightInfo, entry.getValue(), lineView.getEditText().getText().toString());
            }
        }
        String flightNumber = mFlightInfo.getFlightNumber();
        if (flightNumber.toUpperCase().startsWith("FM")) {
            mFlightInfo.setAirCompany("上海航空公司");
        } else if (flightNumber.toUpperCase().startsWith("MU")) {
            mFlightInfo.setAirCompany("天合联盟成员东方航空公司");
        } else {
            mFlightInfo.setAirCompany("东方航空公司");
        }
        GenerateService s = new GenerateService();
        Long pid = s.insertOrUpdateGeneratePlay(mFlightInfo);
        HistoryFlightTempEdit historyFlightTempEdit = DBManager.getInstance(mAddAndEditFragment.getActivity()).queryHisFlightTempByflightInfoTempPid(pid);
        if (null == historyFlightTempEdit) {
            historyFlightTempEdit = new HistoryFlightTempEdit();
            historyFlightTempEdit.setFlightInfoTempPid(pid);
            historyFlightTempEdit.setEditDate(new Date());
            DBManager.getInstance(mAddAndEditFragment.getActivity())
                    .insert(historyFlightTempEdit, DBManager.getInstance(mAddAndEditFragment.getActivity())
                            .getHistoryFlightTempEditDao(DBManager.WRITE_ONLY));
        } else {
            historyFlightTempEdit.setEditDate(new Date());
            DBManager.getInstance(mAddAndEditFragment.getActivity())
                    .update(historyFlightTempEdit, DBManager.getInstance(mAddAndEditFragment.getActivity())
                            .getHistoryFlightTempEditDao(DBManager.WRITE_ONLY));
        }

        EventBus.getDefault().post(new SimpleEvent(Constants.BACK_MAIN_CLOSE));
        return RESULT_SAVE_SUCCESS;
    }

    private Subscription mSubscription;
    private void rxQueryById(final AddAndEditFragment addAndEditFragment, long id) {
        Observable<FlightInfo> observable = DBManager.getInstance(addAndEditFragment.getActivity())
                .rxQueryById(id, DBManager.getInstance(addAndEditFragment.getActivity())
                        .getFlightInfoDao(DBManager.READ_ONLY));

        mSubscription = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FlightInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(FlightInfo flightInfo) {
                        if (null != flightInfo) {
                            mFlightInfo = flightInfo;
//                            Log.e("wjq", "mFlightInfoId = " + mFlightInfo.getId());
                            for (Map.Entry<String, String> entry : addAndEditFragment.mFieldMap.entrySet()) {
                                LineView lineView = (LineView) addAndEditFragment.main.findViewWithTag(entry.getValue());
                                if ("planToTakeOffDate".equals(entry.getValue()))
                                    lineView.getEditText().setText(DateUtils.formatSHowDate(BeanUtil.invokeGet(flightInfo, entry.getValue()) + ""));
                                else
                                    lineView.getEditText().setText(BeanUtil.invokeGet(flightInfo, entry.getValue()) + "");

                            }
                        } else {
                            mFlightInfo = new FlightInfo();
                        }
                    }
                });
    }
}
