package com.oraro.mbroadcast.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.listener.ISearchBarCallback;
import com.oraro.mbroadcast.model.FlightInfoTemp;
import com.oraro.mbroadcast.model.PlayEntry;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.service.IMyAidlInterface;
import com.oraro.mbroadcast.ui.adapter.BroadcastDatePopWindowAdapter;
import com.oraro.mbroadcast.ui.adapter.BroadcastFlightInfoAdapter;
import com.oraro.mbroadcast.ui.fragment.TTSUrgentFragment;
import com.oraro.mbroadcast.ui.widget.EditRefreshLayout;
import com.oraro.mbroadcast.ui.widget.NewSearchBar;
import com.oraro.mbroadcast.utils.DataUtils;
import com.oraro.mbroadcast.utils.LogUtils;
import com.oraro.mbroadcast.utils.UIUtils;
import com.oraro.mbroadcast.utils.UrgentBroadcastXmlUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UrgentBroadcastActivity extends AppCompatActivity implements View.OnClickListener, TTSUrgentFragment.TTSUrgentFragmentCallBack {
    private static final int RESULT_CODE = 1;
    private Context ctx;
    private List<Fragment> fragmentList;
    private TextView header_left_btn;
    private TextView header_text;
    private TextView tv_edit_tts;
    private ViewPager vp_edit_mould;
    private TextView tv_edit_mould_title;
    private EditText et_edit_flightNum;
    private EditText et_edit_start;
    private EditText et_edit_stop;
    private Button btn_edit_insert;
    private MyFragmentAdapter fragmentAdapter;
    private BroadcastFlightInfoAdapter lvAdapter;

    private List<FlightInfoTemp> flightInfoPageList;
    private FlightInfoTemp doingFlightInfo;//当前选中的航班信息
    private ImageView iv_edit_count;
    private TextView tv_edit_month;
    private ImageView iv_edit_month;
    private TextView tv_edit_day;
    private ImageView iv_edit_day;
    private TextView tv_edit_hour;
    private ImageView iv_edit_hour;
    private TextView tv_edit_min;
    private ImageView iv_edit_min;
    private ListView listView;
    private PopupWindow popWindow;
    private BroadcastDatePopWindowAdapter popAdapter;
    private List<String> countList;
    private List<String> monthList;
    private List<String> dayList;
    private List<String> hourList;
    private List<String> minList;
    private List<String> replaceList;
    private TTSUrgentFragment ttsUrgentFragment;
    private String TAG = "zmy";
    private LinearLayout ll_edit_flightNum_row;

    private final int countFlag = 1;
    private final int monthFlag = 2;
    private final int dayFlag = 3;
    private final int hourFlag = 4;
    private final int minFlag = 5;
    private int mFlag = 0;
    private String selectMonthFlag;
    private String[] monthArray1;
    private String[] monthArray2;
    private String[] monthArray3;
    private String selectTTSItemName;
    private DBManager dbManager;
    private String count;
    private TextView tv_edit_flightNum;
    private TextView tv_edit_start;
    private TextView tv_edit_destination;
    private TextView tv_edit_count;
    private TextView header_right_btn;
    private LinearLayout ll_edit_father;
    private PlayEntry urgentPlayEntry;
    private String titleName;
    private NewSearchBar searchBar;
    private List searchFlightInfoList;
    private EditRefreshLayout refresh_edit_flight_info;
    private int page = 0;
    private WebView webView;
    private Button btn_instant_broadcast;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    refresh_edit_flight_info.setLoading(true);
                    break;
                case 1:
                    refresh_edit_flight_info.setLoading(false);
                    lvAdapter.setFlightList(flightInfoPageList);
                    lvAdapter.notifyDataSetChanged();

                    break;
                case 3:
                    refresh_edit_flight_info.setLoading(false);
                    break;
            }
        }
    };
    private String mouldContent;
    private String editedMouldContent;
    private UrgentBroadcastXmlUtils ubxu;
    private boolean isInstantBroadcast = false;
    private List<String> mHistoryFlightnoList = new ArrayList<>();
    private List<String> mSelectedFlightnoList = new ArrayList<>();
    private Handler mHandler = new Handler();
    private boolean isPiLiang = false;

    private class OnMyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // 设置模板内容
            if (mouldContent != null) {
//                LogUtils.e(TAG, "mouldContent=======onPageFinished===" + mouldContent);
                webView.loadUrl(mouldContent);
            }
            view.loadUrl("javascript:changeStyle('" + Constants.WEBVIEW_CAN_EDIT + "')");

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    private class JsToJava {
        @JavascriptInterface
        public void jsMethod(String paramFromJS) {
            LogUtils.e("zmy", "js返回结果=============" + paramFromJS);//处理返回的结果
            editedMouldContent = paramFromJS;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    submit();
                }
            });
        }

        @JavascriptInterface
        public void removeFlightnoId(String flightno) {
            mSelectedFlightnoList.remove(flightno);
        }

        @JavascriptInterface
        public void getJsParams(String flightno) {
        }
    }

    private class OnMyISearchBarCallback implements ISearchBarCallback {

        // 点击的那条item航班信息
        @Override
        public void setItemInfo(FlightInfoTemp flightInfoTemp, int position) {
            setFlightInfoItem(flightInfoTemp);
            setRightInfo(flightInfoPageList.size() - 11);
        }

        // 表示搜索框中每输入一个字符需要执行的动作
        @Override
        public void setChangeEditText(String text) {
            // TODO: 2016/9/22 查询方法
            searchFlightInfoList = dbManager.queryFlightTempNumberLike(text);
            searchBar.upDateListView(searchFlightInfoList);
        }
    }

    private class OnMyLoadListener implements EditRefreshLayout.OnLoadListener {
        @Override
        public void onLoad() {
            refresh_edit_flight_info.postDelayed(new Runnable() {
                @Override
                public void run() {

                    handler.sendEmptyMessage(0);
                    page++;
                    List loadFlightList = dbManager.queryFlightTempByPagenum(page);
                    if (loadFlightList.isEmpty() || loadFlightList.size() == 0) {
                        Toast.makeText(ctx, R.string.edit_no_more_data, Toast.LENGTH_LONG).show();
                        // TODO: 2016/9/27 更新UI？
                        handler.sendEmptyMessage(3);
                        return;
                    }
                    DataUtils.addListData(flightInfoPageList, loadFlightList);
                    handler.sendEmptyMessage(1);
                }
            }, 1500);
        }
    }

    @Override// TTSUrgentFragment里边方法
    public void onItemSelected(String selectItemName) {
        selectTTSItemName = selectItemName;
        // 在此处理根据点击左上边不同模板的item，右边切换相应模板标题、内容的逻辑。。。。。。。。。。
        String selectedType = ttsUrgentFragment.getSelectedType();
        if ("1-4".equals(selectedType)) {
            isPiLiang = true;
            ll_edit_flightNum_row.setVisibility(View.INVISIBLE);

            mHistoryFlightnoList.clear();
        } else {
            ll_edit_flightNum_row.setVisibility(View.VISIBLE);
            isPiLiang = false;
        }

        setMouldContent();

    }

    private class OnMyClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            lvAdapter.changeSelected(position);
            setRightInfo(position);
        }
    }

    private void setRightInfo(int position) {
        // 设置模板名字
        tv_edit_mould_title.setText(selectTTSItemName);
        if (position >= flightInfoPageList.size()) {
            return;
        }
        doingFlightInfo = flightInfoPageList.get(position);
        // 根据传进来的航班信息集合（假定为flightInfoPageList）给3个编辑框赋值。。。。。。
        et_edit_flightNum.setText(doingFlightInfo.getFlightNumber());
        et_edit_start.setText(doingFlightInfo.getDeparture());
        et_edit_stop.setText(doingFlightInfo.getArrivalStation());
        //  textView月、日、时、分无需再设置
        // 在此处理根据点击左下边不同的航班信息item，右边切换相应模板标题、内容的逻辑。。。。。。。。。
        if (null != flightInfoPageList && !flightInfoPageList.isEmpty()) {
            String mSetFlightno = doingFlightInfo.getFlightNumber();
            String selectedType = ttsUrgentFragment.getSelectedType();

            if (isPiLiang && mSelectedFlightnoList.contains(mSetFlightno)) {// 如果是批量延误模板
                Toast.makeText(ctx, R.string.urgent_broadcast_flightNum_repeat, Toast.LENGTH_SHORT).show();
                return;
            }
            mouldContent = ubxu.clickFlightInfo(selectedType, doingFlightInfo);
            webView.loadUrl(mouldContent);
//            LogUtils.e(TAG,"mouldContent=========..............======="+mouldContent);
            mSelectedFlightnoList.add(mSetFlightno);
        }
    }

    private class MyFragmentAdapter extends FragmentPagerAdapter {

        public MyFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urgent_broadcast);
        ctx = this;

        initView();
        initData();
        initList();
        initListener();

        initRightData();

    }

    private void initRightData() {
        if (flightInfoPageList != null && !flightInfoPageList.isEmpty()) {
            doingFlightInfo = flightInfoPageList.get(0);//初始化默认为第一个航班
        }
        // 第1步：设置模板的title
        titleName = getString(R.string.urgent_broadcast_lookFor_unboarding_passenger);
        tv_edit_mould_title.setText(titleName);

        // 第2步：设置模板内容，初始化默认为第一个模板。。。。。。。。。。。。。。
        String defType = ttsUrgentFragment.getSelectedType();
        webView.loadUrl(ubxu.hashMap.get(defType).getFile());
        if (flightInfoPageList != null && !flightInfoPageList.isEmpty()) {
            mouldContent = ubxu.clickFlightInfo(defType, doingFlightInfo);
            // 第3步：处理3个编辑框
            et_edit_flightNum.setText(doingFlightInfo.getFlightNumber());
            et_edit_start.setText(doingFlightInfo.getDeparture());
            et_edit_stop.setText(doingFlightInfo.getArrivalStation());
        }
        // 第4步：处理次数
        tv_edit_count.setText(countList.get(0));
        // 第5步：处理4个日期
        // 2016/8/22  设置月、日、时、分的textView为当前时刻。。。。。。。。。
        Calendar now = Calendar.getInstance();
        tv_edit_month.setText((now.get(Calendar.MONTH) + 1) + getString(R.string.edit_activity_month));
        tv_edit_day.setText(now.get(Calendar.DAY_OF_MONTH) + getString(R.string.edit_activity_day));
        tv_edit_hour.setText(now.get(Calendar.HOUR_OF_DAY) + getString(R.string.edit_activity_hour));
        int min = now.get(Calendar.MINUTE);
        if (min < 10) {
            tv_edit_min.setText("0" + min + getString(R.string.edit_activity_min));
        } else {
            tv_edit_min.setText(min + getString(R.string.edit_activity_min));
        }

    }

    private void initView() {
        ubxu = new UrgentBroadcastXmlUtils(ctx);
        ll_edit_flightNum_row = (LinearLayout) findViewById(R.id.ll_edit_flightNum_row);
        webView = (WebView) findViewById(R.id.edit_webView);
        webView.setBackgroundColor(0);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JsToJava(), "stub");
        webView.setWebViewClient(new OnMyWebViewClient());

        header_left_btn = (TextView) findViewById(R.id.header_left_btn);
        header_left_btn.setVisibility(View.VISIBLE);
        header_text = (TextView) findViewById(R.id.header_text);
        header_text.setText(R.string.urgent_broadcast_activity_title);
        tv_edit_tts = (TextView) findViewById(R.id.tv_edit_tts);
        vp_edit_mould = (ViewPager) findViewById(R.id.vp_edit_mould);
        tv_edit_mould_title = (TextView) findViewById(R.id.tv_edit_mould_title);
        et_edit_flightNum = (EditText) findViewById(R.id.et_edit_flightNum);
        et_edit_flightNum.setOnClickListener(this);
        et_edit_start = (EditText) findViewById(R.id.et_edit_start);
        et_edit_stop = (EditText) findViewById(R.id.et_edit_stop);
        btn_edit_insert = (Button) findViewById(R.id.btn_edit_insert);
        iv_edit_count = (ImageView) findViewById(R.id.iv_edit_count);
        iv_edit_count.setOnClickListener(this);
        tv_edit_month = (TextView) findViewById(R.id.tv_edit_month);
        iv_edit_month = (ImageView) findViewById(R.id.iv_edit_month);
        iv_edit_month.setOnClickListener(this);
        tv_edit_day = (TextView) findViewById(R.id.tv_edit_day);
        tv_edit_day.setOnClickListener(this);
        iv_edit_day = (ImageView) findViewById(R.id.iv_edit_day);
        iv_edit_day.setOnClickListener(this);
        tv_edit_hour = (TextView) findViewById(R.id.tv_edit_hour);
        tv_edit_hour.setOnClickListener(this);
        iv_edit_hour = (ImageView) findViewById(R.id.iv_edit_hour);
        iv_edit_hour.setOnClickListener(this);
        tv_edit_min = (TextView) findViewById(R.id.tv_edit_min);
        tv_edit_min.setOnClickListener(this);
        iv_edit_min = (ImageView) findViewById(R.id.iv_edit_min);
        iv_edit_min.setOnClickListener(this);
        tv_edit_flightNum = (TextView) findViewById(R.id.tv_edit_flightNum);
        tv_edit_flightNum.setOnClickListener(this);
        tv_edit_start = (TextView) findViewById(R.id.tv_edit_start);
        tv_edit_start.setOnClickListener(this);
        tv_edit_destination = (TextView) findViewById(R.id.tv_edit_destination);
        tv_edit_destination.setOnClickListener(this);
        tv_edit_count = (TextView) findViewById(R.id.tv_edit_count);
        tv_edit_count.setOnClickListener(this);
        header_right_btn = (TextView) findViewById(R.id.header_right_btn);
        header_right_btn.setOnClickListener(this);
        ll_edit_father = (LinearLayout) findViewById(R.id.ll_edit_father);
        ll_edit_father.setOnClickListener(this);
        searchBar = (NewSearchBar) findViewById(R.id.search);
        btn_instant_broadcast = (Button) findViewById(R.id.btn_instant_broadcast);
        titleName = getResources().getString(R.string.zhiji);
    }

    private void initData() {
        editedMouldContent = getString(R.string.edit_incomplete_info);
        selectTTSItemName = getString(R.string.list1_LookingForBoardingPassengers);

        dbManager = DBManager.getInstance(ctx);
        flightInfoPageList = dbManager.queryFlightTempByPagenum(page);

        ttsUrgentFragment = new TTSUrgentFragment();
        ttsUrgentFragment.setTTSUrgentFragmentCallBack(this);
        fragmentList = new ArrayList<>();
        fragmentList.add(ttsUrgentFragment);

        fragmentAdapter = new MyFragmentAdapter(getSupportFragmentManager());
        if (flightInfoPageList != null && !flightInfoPageList.isEmpty()) {
            lvAdapter = new BroadcastFlightInfoAdapter(ctx, flightInfoPageList);
        }
        listView = new ListView(ctx);
        countList = new ArrayList<>();
        monthList = new ArrayList<>();
        dayList = new ArrayList<>();
        hourList = new ArrayList<>();
        minList = new ArrayList<>();
        replaceList = new ArrayList<>();
        monthArray1 = UIUtils.getStringArray(R.array.monthArray1);
        monthArray2 = UIUtils.getStringArray(R.array.monthArray2);
        monthArray3 = UIUtils.getStringArray(R.array.monthArray3);

        searchBar.setSearchBarCallback(new OnMyISearchBarCallback());
    }

    private void setFlightInfoItem(FlightInfoTemp flightInfoTemp) {
        flightInfoPageList.add(flightInfoPageList.size() - 10, flightInfoTemp);
//        lvAdapter.changeSelected(flightInfoPageList.size() - 11);
//        lv_edit_flight_info.setSelection(flightInfoPageList.size() - 11);
    }


    public void setMouldContent() {
        // 设置模板名字
        tv_edit_mould_title.setText(selectTTSItemName);
        String selectedType = ttsUrgentFragment.getSelectedType();
        if (flightInfoPageList != null && !flightInfoPageList.isEmpty()) {

            mouldContent = ubxu.clickFlightInfo(selectedType, doingFlightInfo);
            if (isPiLiang) {
                mSelectedFlightnoList.clear();
                mSelectedFlightnoList.add(doingFlightInfo.getFlightNumber());
            }
        }
        webView.loadUrl(ubxu.hashMap.get(selectedType).getFile());
    }

    private void initList() {
        if (countList.isEmpty()) {
            for (int i = 1; i <= 10; i++) {
                countList.add(i + getString(R.string.edit_activity_count));
            }
        }
        if (monthList.isEmpty()) {
            for (int i = 1; i <= 12; i++) {
                monthList.add(i + getString(R.string.edit_activity_month));
            }
        }
        if (dayList.isEmpty()) {
            for (int i = 1; i <= 31; i++) {
                dayList.add(i + getString(R.string.edit_activity_day));
            }
        }
        if (hourList.isEmpty()) {
            for (int i = 0; i < 24; i++) {
                if (i < 10) {
                    hourList.add("0" + i + getString(R.string.edit_activity_hour));
                } else {
                    hourList.add(i + getString(R.string.edit_activity_hour));
                }
            }
        }
        if (minList.isEmpty()) {
            for (int i = 0; i < 60; i++) {
                if (i < 10) {
                    minList.add("0" + i + getString(R.string.edit_activity_min));
                } else {
                    minList.add(i + getString(R.string.edit_activity_min));
                }
            }
        }
    }

    private void initListener() {
        header_left_btn.setOnClickListener(this);
        btn_edit_insert.setOnClickListener(this);
        btn_instant_broadcast.setOnClickListener(this);
        tv_edit_tts.setOnClickListener(this);
        vp_edit_mould.setAdapter(fragmentAdapter);
        if (null != flightInfoPageList && !flightInfoPageList.isEmpty()) {
//            lv_edit_flight_info.setAdapter(lvAdapter);
        }

//        lv_edit_flight_info.setOnItemClickListener(new OnMyClickListener());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String msg;
                if (mFlag == countFlag) {
                    msg = countList.get(position % replaceList.size());
                    tv_edit_count.setText(msg);
                } else if (mFlag == monthFlag) {
                    msg = monthList.get(position % replaceList.size());
                    tv_edit_month.setText(msg);
                    adjustDay();
                } else if (mFlag == dayFlag) {
                    msg = dayList.get(position % replaceList.size());
                    tv_edit_day.setText(msg);
                } else if (mFlag == hourFlag) {
                    msg = hourList.get(position % replaceList.size());
                    tv_edit_hour.setText(msg);
                } else if (mFlag == minFlag) {
                    msg = minList.get(position % replaceList.size());
                    tv_edit_min.setText(msg);
                }
                // 关闭popWindow
                popWindow.dismiss();
            }
        });
        refresh_edit_flight_info.setRefreshing(true);
        refresh_edit_flight_info.setOnLoadListener(new OnMyLoadListener());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.header_left_btn:
                finish();
                break;
            case R.id.btn_edit_insert:
                //  执行保存编辑信息的逻辑
                getWebViewData();
                EventBus.getDefault().postSticky(new SimpleEvent(Constants.Analytic_Cmpletion_Notice));
                break;
            case R.id.btn_instant_broadcast:
                isInstantBroadcast = true;
                getWebViewData();
                break;
            case R.id.iv_edit_count:
                String countText = (String) tv_edit_count.getText();
                countText = countText.substring(0, countText.length() - 1);
                setPopRecircle(countFlag, countList, Integer.parseInt(countText));
                showPop();
                break;
            case R.id.iv_edit_month:// 点击后需要获取选中了哪个月
                String monthText = (String) tv_edit_month.getText();
                monthText = monthText.substring(0, monthText.length() - 1);
                setPopRecircle(monthFlag, monthList, Integer.parseInt(monthText));
                showPop();
                break;
            case R.id.iv_edit_day:// 根据月判断显示日
                String dayText = (String) tv_edit_day.getText();
                dayText = dayText.substring(0, dayText.length() - 1);
                setPopRecircle(dayFlag, dayList, Integer.parseInt(dayText));
                showPop();
                break;
            case R.id.iv_edit_hour:
                String hourText = (String) tv_edit_hour.getText();
                hourText = hourText.substring(0, hourText.length() - 1);
                setPopRecircle(hourFlag, hourList, Integer.parseInt(hourText));
                showPop();
                break;
            case R.id.iv_edit_min:
                String minText = (String) tv_edit_min.getText();
                minText = minText.substring(0, minText.length() - 1);
                setPopRecircle(minFlag, minList, Integer.parseInt(minText));
                showPop();
                break;
            case R.id.tv_edit_tts:
                vp_edit_mould.setCurrentItem(0);
                break;
        }
    }

    private void getWebViewData() {
        LogUtils.e(TAG, "getWebViewData===========");
        String selectedType = ttsUrgentFragment.getSelectedType();
        webView.loadUrl(ubxu.hashMap.get(selectedType).getGet_data());

    }

    private void setPopRecircle(int flag, List<String> list, int num) {
        mFlag = flag;
        replaceList = list;
        popAdapter = new BroadcastDatePopWindowAdapter(ctx, replaceList);
        listView.setAdapter(popAdapter);
        switch (mFlag) {
            case countFlag:
                listView.setSelection(num + 9 + 10 * 100);
                break;
            case monthFlag:
                listView.setSelection(num + 11 + 12 * 100);
                break;
            case dayFlag:
                setDayList();
                popAdapter = new BroadcastDatePopWindowAdapter(ctx, replaceList);
                listView.setAdapter(popAdapter);
                setDaySelection(num);
                break;
            case hourFlag:
                listView.setSelection(num + 24 * 100);
                break;
            case minFlag:
                listView.setSelection(num + 60 * 100);
                break;
        }
    }

    private void setDaySelection(int num) {
        String selectMonth = (String) tv_edit_month.getText();
        //  根据集合数据中年、月的值来显示日的集合，在此需要处理闰年、平年问题
        if (Arrays.asList(monthArray1).contains(selectMonth)) {// 31天
            listView.setSelection(num + 30 + 31 * 100);
        } else if (getString(R.string.month_2).equals(selectMonth)) {// 30天包含2月的28或者29天
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            if (year % 4 == 0 && year % 100 != 0) {// 29天
                listView.setSelection(num + 28 + 29 * 100);
            } else if (year % 400 == 0) {// 29天
                listView.setSelection(num + 28 + 29 * 100);
            } else {// 28天
                listView.setSelection(num + 27 + 28 * 100);
            }
        } else if (Arrays.asList(monthArray3).contains(selectMonth)) {// 30天
            listView.setSelection(num + 29 + 30 * 100);
        }
    }

    private void setDayList() {
        selectMonthFlag = (String) tv_edit_month.getText();
        //  根据集合数据中年、月的值来显示日的集合，在此需要处理闰年、平年问题
        if (Arrays.asList(monthArray1).contains(selectMonthFlag)) {// 31天
            replaceList = dayList;
        } else if (Arrays.asList(monthArray2).contains(selectMonthFlag)) {// 30天包含2月的28或者29天
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            if (getString(R.string.month_2).equals(selectMonthFlag)) {
                if (year % 4 == 0 && year % 100 != 0) {
                    replaceList = dayList.subList(0, dayList.size() - 2);// 29天
                } else if (year % 400 == 0) {
                    replaceList = dayList.subList(0, dayList.size() - 2);// 29天
                } else {
                    replaceList = dayList.subList(0, dayList.size() - 3);// 28天
                }
            } else {// 30天
                replaceList = dayList.subList(0, dayList.size() - 1);
            }
        }
    }

    private void adjustDay() {
        String selectMonth = (String) tv_edit_month.getText();
        String selectDayFlag = (String) tv_edit_day.getText();
        selectDayFlag = selectDayFlag.substring(0, selectDayFlag.indexOf(getString(R.string.edit_activity_day)));
        int selectDay = Integer.parseInt(selectDayFlag);
        //  根据集合数据中年、月的值来显示日的集合，在此需要处理闰年、平年问题
        if (Arrays.asList(monthArray1).contains(selectMonth)) {// 31天

        } else if (Arrays.asList(monthArray2).contains(selectMonth)) {// 30天包含2月的28或者29天
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            if (getString(R.string.month_2).equals(selectMonth)) {
                if (year % 4 == 0 && year % 100 != 0 && selectDay > 29) {
                    tv_edit_day.setText("29" + getString(R.string.edit_activity_day));// 29天
                } else if (year % 400 == 0 && selectDay > 29) {
                    tv_edit_day.setText("29" + getString(R.string.edit_activity_day));// 29天
                } else if (selectDay > 28) {
                    tv_edit_day.setText("28" + getString(R.string.edit_activity_day));// 28天
                }
            } else if (selectDay > 30) {// 30天
                tv_edit_day.setText("30" + getString(R.string.edit_activity_day));
            }
        }
    }

    private void showPop() {
        int popHeight = 300;
        if (popWindow == null) {
            popWindow = new PopupWindow(ctx);
            popWindow.setWidth(tv_edit_count.getWidth()); // 与输入框等宽
            popWindow.setHeight(popHeight); // 高度300
            popWindow.setContentView(listView);
            popWindow.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.bg_edit));

            popWindow.setFocusable(true); // 设置为，可以获得焦点
        }
        if (popWindow.isShowing()) {
            popWindow.dismiss();
        } else {
            if (mFlag == countFlag) {
                popWindow.showAsDropDown(tv_edit_count);
            } else if (mFlag == monthFlag) {
                popWindow.showAsDropDown(tv_edit_month);
            } else if (mFlag == dayFlag) {
                popWindow.showAsDropDown(tv_edit_day);
            } else if (mFlag == hourFlag) {
                popWindow.showAsDropDown(tv_edit_hour);
            } else if (mFlag == minFlag) {
                popWindow.showAsDropDown(tv_edit_min);
            }
        }
    }

    private void submit() {
        // validate
        LogUtils.e(TAG, "submit==========");
        if (null == flightInfoPageList || flightInfoPageList.isEmpty()) {
            Toast.makeText(ctx, R.string.errorOperatePrompt, Toast.LENGTH_LONG).show();
            return;
        }
        if (getString(R.string.edit_incomplete_info).equals(editedMouldContent)) {
            Toast.makeText(this, getString(R.string.edit_outside_brackets_info), Toast.LENGTH_LONG).show();
            return;
        }
        if (getString(R.string.urgent_broadcast_flightNum_cannot_null).equals(editedMouldContent)) {
            Toast.makeText(this, R.string.urgent_broadcast_select_delay_flightNum, Toast.LENGTH_LONG).show();
            return;
        }
        saveEditInfoAndExit();
    }

    private void saveEditInfoAndExit() {
        Intent intent = new Intent();
        urgentPlayEntry = new PlayEntry();
        setPlayEntryValue(urgentPlayEntry);
        dbManager.insert(urgentPlayEntry, dbManager.getPlayEntryDao(DBManager.WRITE_ONLY));
        if (!isInstantBroadcast) {
            IMyAidlInterface iMyAidlInterface = MBroadcastApplication.getIMyAidlInterface();
            if (iMyAidlInterface != null) {
                try {
                    iMyAidlInterface.refresh();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            Toast.makeText(ctx, R.string.urgent_broadcast_insert_success, Toast.LENGTH_SHORT).show();
        } else {
            Constants.URGENT_BROADCAST_INSTANT_id = urgentPlayEntry.getId();
            EventBus.getDefault().postSticky(new SimpleEvent(Constants.URGENT_BROADCAST_INSTANT));
        }
        intent.putExtra("playEntryId", urgentPlayEntry.getId());
        Constants.TEMP_PLAYEN_ID = urgentPlayEntry.getId();
        setResult(RESULT_CODE, intent);
        finish();
    }

    /**
     * 给playEntry属性赋值
     */
    private void setPlayEntryValue(PlayEntry playEntry) {
        String monthText = (String) tv_edit_month.getText();
        String dayText = (String) tv_edit_day.getText();
        String hourText = (String) tv_edit_hour.getText();
        String minText = (String) tv_edit_min.getText();
        String month = monthText.substring(0, monthText.indexOf(getString(R.string.edit_activity_month)));
        String day = dayText.substring(0, dayText.indexOf(getString(R.string.edit_activity_day)));
        String hour = hourText.substring(0, hourText.indexOf(getString(R.string.edit_activity_hour)));
        String min = minText.substring(0, minText.indexOf(getString(R.string.edit_activity_min)));
        Date now = new Date();
        Date date = new Date(now.getYear(), Integer.parseInt(month) - 1, Integer.parseInt(day), Integer.parseInt(hour), Integer.parseInt(min), 0);
        if (isInstantBroadcast) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            try {
                Date date1 = sdf.parse("5000-12-31 00:00");
                playEntry.setTime(date1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            playEntry.setTime(date);
        }
        count = tv_edit_count.getText().toString().trim();

        playEntry.setTimes(Integer.parseInt(count.substring(0, count.indexOf(getString(R.string.edit_activity_count)))));
        // TODO: 2016/10/8 给播放实体播放内容赋值
        playEntry.setTextDesc(editedMouldContent);

        playEntry.setIsEmeng(2);//设置优先级，值越到，优先级越高
        if (!isPiLiang) {
            if (doingFlightInfo != null) {
                playEntry.setFlightInfoTemp(doingFlightInfo);
            }
        }
        playEntry.setDoTimes(0);
        playEntry.setIsQueue(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}


