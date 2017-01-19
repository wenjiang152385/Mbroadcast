package com.oraro.mbroadcast.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.listener.ISearchBarCallback;
import com.oraro.mbroadcast.mina.client.MinaFileClientThread;
import com.oraro.mbroadcast.mina.client.MinaStringClientThread;
import com.oraro.mbroadcast.model.CompileModels;
import com.oraro.mbroadcast.model.FlightInfoTemp;
import com.oraro.mbroadcast.model.ModelEntity;
import com.oraro.mbroadcast.model.PlayEntry;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.ui.adapter.BaseListAdapter;
import com.oraro.mbroadcast.ui.adapter.BroadcastDatePopWindowAdapter;
import com.oraro.mbroadcast.ui.adapter.BroadcastFlightInfoAdapter;
import com.oraro.mbroadcast.ui.adapter.RecycleAdapter;
import com.oraro.mbroadcast.ui.fragment.AudioFragment;
import com.oraro.mbroadcast.ui.fragment.FileSelectFragment;
import com.oraro.mbroadcast.ui.fragment.TTSFragment;
import com.oraro.mbroadcast.ui.widget.EditRefreshLayout;
import com.oraro.mbroadcast.ui.widget.NewSearchBar;
import com.oraro.mbroadcast.utils.BroadcastInformation;
import com.oraro.mbroadcast.utils.DataUtils;
import com.oraro.mbroadcast.utils.LogUtils;
import com.oraro.mbroadcast.utils.MD5Util;
import com.oraro.mbroadcast.utils.SPUtils;
import com.oraro.mbroadcast.utils.UIUtils;
import com.oraro.mbroadcast.utils.UrgentBroadcastXmlUtils;
import com.oraro.mbroadcast.vo.PlayVO;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EditActivity extends AppCompatActivity implements View.OnClickListener, TTSFragment.TTSFragmentCallBack, BaseListAdapter.OnItemClickListener  {
    public static final int EXTRA_ADD_MODE = 0;
    public static final int EXTRA_EDIT_MODE = 1;

    private static final int RESULT_CODE = 1;
    private static final int FILE_PICKER_REQUEST_CODE = 1;
    private Context ctx;
    private List<Fragment> fragmentList;
    private TextView header_left_btn;
    private TextView header_text;
    private TextView tv_edit_tts;
    private TextView tv_edit_audio;
    private ImageView iv_edit_line;
    private ViewPager vp_edit_mould;
    private TextView tv_edit_mould_title;
    private EditText et_edit_flightNum;
    private EditText et_edit_start;
    private EditText et_edit_stop;
    private Button btn_edit_insert;
    private MyFragmentAdapter fragmentAdapter;
    private BroadcastFlightInfoAdapter lvAdapter;
    private String TAG = "zmy";

    private PlayEntry editPlayEntry;// 传过来的需要编辑的对象
    private List<FlightInfoTemp> flightInfoPageList;

    private FlightInfoTemp doingFlightInfo;//当前选中的航班信息
    private int mSelect = 0;
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
    private BroadcastInformation broadcastInformation;
    private TTSFragment ttsFragment;
    private AudioFragment audioFragment;
    private WebView webView_edit;

    private final int countFlag = 1;
    private final int monthFlag = 2;
    private final int dayFlag = 3;
    private final int hourFlag = 4;
    private final int minFlag = 5;
    private int mFlag = 0;
    private int isVisibleFlag = 0;
    private String selectMonthFlag;
    private String[] monthArray1;
    private String[] monthArray2;
    private String[] monthArray3;
    private EditText et_edit_mould_content;
    private ImageView iv_edit_horn;
    private Bundle bundle;
    private CompileModels compileModels;
    private List<ModelEntity> modelDataList;
    private String selectTTSItemId;
    private String selectMouldItemContent;
    private boolean arrowEnableFlag = false;
    private int flag;// flag为0表示点击新建进来，flag为1表示点击编辑进来
    private DBManager dbManager;
    private String count;
    private TextView tv_edit_flightNum;
    private TextView tv_edit_start;
    private TextView tv_edit_destination;
    private TextView tv_edit_count;
    private String datePattern;
    private AnimationDrawable animationDrawable;
    private TextView header_right_btn;
    private LinearLayout ll_edit_father;
    private PlayEntry increasePlayEntry;
    private LinearLayout ll_file_select;
    private TextView tv_file_select;
    private Button btn_file_select;
    private String titleName;
    private int fragmentPosition;
    private NewSearchBar searchBar;
    private List searchFlightInfoList;
    private LinearLayout ll_edit_flightNum_row;
    private int page = 0;
    private boolean isWebView;
    private UrgentBroadcastXmlUtils ubxu;


    private RecyclerView mRecyclerView;
    private RecycleAdapter mRecycleAdapter;
    private int mCurrentPage = 0;
    //如果为true表示是自定义的录音播报编辑
    private boolean mIsCustomAudioEdit = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                mRecyclerView.smoothScrollToPosition(msg.arg1);
            }
        }
    };
    private String mouldContent;
    private String editedMouldContent;
    private boolean isAudioFragment;
    private boolean isCustomTts;

    @Override
    public void onItemClick(int position, long id, View view) {
        if (position >= 0) {
            setRightInfo(0, position, null);
            mRecycleAdapter.notifyDataSetChanged();
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            webView_edit.loadUrl(mouldContent);
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
            LogUtils.e("zmy", "js返回结果====" + paramFromJS);//处理返回的结果
            editedMouldContent = paramFromJS;
            submit();
        }

        @JavascriptInterface
        public void getJsParams(String flightno) {
        }
    }

//    private class OnMyLoadListener implements EditRefreshLayout.OnLoadListener {
//        @Override
//        public void onLoad() {
//            refresh_edit_flight_info.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    handler.sendEmptyMessage(0);
//                    page++;
//                    List loadFlightList = dbManager.queryFlightTempByPagenum(page);
//                    if (loadFlightList.isEmpty() || loadFlightList.size() == 0) {
//                        Toast.makeText(ctx, R.string.edit_no_more_data, Toast.LENGTH_LONG).show();
//                        // TODO: 2016/9/27 更新UI？
//                        handler.sendEmptyMessage(3);
//                        return;
//                    }
//                    DataUtils.addListData(flightInfoPageList, loadFlightList);
//                    handler.sendEmptyMessage(1);
//                }
//            }, 1500);
//        }
//    }

    @Override// TTSFragment里边方法
    public void onItemSelected(String selectItemId) {
        selectTTSItemId = selectItemId;
        // 2016/8/24 在此处理根据点击左上边不同模板的item，右边切换相应模板标题、内容的逻辑。。。。。。。。。。
        if (null != flightInfoPageList && !flightInfoPageList.isEmpty()) {
            setMouldContent();
        }
    }

//    private class OnMyClickListener implements AdapterView.OnItemClickListener {
//
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            lvAdapter.changeSelected(position);
//            setRightInfo(position);
//        }
//    }

    private void setRightInfo(int mode, int position, FlightInfoTemp flightInfoTemp) {
        if (mode == 0) {
            doingFlightInfo = flightInfoPageList.get(position);
        } else if (mode == 1) {
            doingFlightInfo = flightInfoTemp;
        }
        // 根据传进来的航班信息集合（假定为flightInfoPageList）给3个编辑框赋值。。。。。。
        et_edit_flightNum.setText(doingFlightInfo.getFlightNumber());
        et_edit_start.setText(doingFlightInfo.getDeparture());
        et_edit_stop.setText(doingFlightInfo.getArrivalStation());
        //  textView月、日、时、分无需再设置
        // 在此处理根据点击左下边不同的航班信息item，右边切换相应模板标题、内容的逻辑。。。。。。。。。
        if (null != flightInfoPageList && !flightInfoPageList.isEmpty() && fragmentPosition == 0) {
            // 表示只有在TTSFragment时才去切换右边模板内容
            setMouldContent();
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

    private class OnMainPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            float disX = positionOffset * iv_edit_line.getWidth();
            float startX = position * iv_edit_line.getWidth();
            float endX = startX + disX;
            ViewHelper.setTranslationX(iv_edit_line, endX);//直接改变属性(位移坐标)

            Animation scale = AnimationUtils.loadAnimation(ctx, R.anim.anim_scale_in);
            tv_edit_tts.setAnimation(scale);
            tv_edit_audio.setAnimation(scale);
        }

        @Override
        public void onPageSelected(int position) {
            mSelect = position;
            fragmentPosition = position;
            ViewPropertyAnimator.animate(tv_edit_tts).scaleX(position == 0 ? 1.2f : 0.8f).scaleY(position == 0 ? 1.2f : 0.8f);
            ViewPropertyAnimator.animate(tv_edit_audio).scaleX(position == 1 ? 1.2f : 0.8f).scaleY(position == 1 ? 1.2f : 0.8f);

            tv_edit_tts.setTextColor(position == 0 ? Color.parseColor("#f5a623") : Color.parseColor("#808080"));
            tv_edit_audio.setTextColor(position == 1 ? Color.parseColor("#f5a623") : Color.parseColor("#808080"));
            isVisibleFlag = position;
            if (position == 0) {
                if(!canAudioToTtsEdit()){
                    vp_edit_mould.setCurrentItem(1);
                    return;
                }
                isAudioFragment = false;
                setHorn();
                tv_edit_mould_title.setText(titleName);
                if (getString(R.string.edit_custom_tts).equals(titleName)) {
                    ll_edit_flightNum_row.setVisibility(View.GONE);
                    isCustomTts = true;
                } else {
                    ll_edit_flightNum_row.setVisibility(View.VISIBLE);
                    isCustomTts = false;
                }
                if (isWebView) {
                    webView_edit.setVisibility(View.VISIBLE);
                    et_edit_mould_content.setVisibility(View.GONE);
                } else {
                    webView_edit.setVisibility(View.GONE);
                    et_edit_mould_content.setVisibility(View.VISIBLE);
                }
            } else {
                isAudioFragment = true;
                ll_edit_flightNum_row.setVisibility(View.GONE);
                tv_edit_mould_title.setText(R.string.self_definition_audio);

                if (isWebView) {
                    webView_edit.setVisibility(View.GONE);
                }
                setHorn();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_edit);
        ctx = this;

        getConveyInfo();
        getMouldData();
        initView();
        initData();
        initList();
        initListener();
    }

    private void getConveyInfo() {
        //  从启动这个界面的activity中获取需要的信息，包括：航班信息、编辑单条和编辑整个页面的标记。。。。。。。
        int activityFlag = -1;
        Intent intent = getIntent();
        flag = intent.getIntExtra("flag", activityFlag);

        dbManager = DBManager.getInstance(ctx);
        if (flag == EXTRA_EDIT_MODE) {// 表示点击“编辑”进来
            //  得到编辑条目的对象。。。。。。。
            long peid = intent.getLongExtra("PEID", 0);
            editPlayEntry = (PlayEntry) dbManager.queryById(peid, dbManager.getPlayEntryDao(DBManager.READ_ONLY));
        }
        //  得到航班信息集合。。。。。。
        flightInfoPageList = dbManager.queryFlightTempByPagenum(page);
    }

    /**
     * 该方法初始化该编辑页面整体布局右边部分，即给模板信息和模板下边的航班号、次数、时间等相关信息赋值
     */
    private void initEditInfo() {
        // 在此设置刚进来时左边模板的被选中的itemId默认为传过来数据的第一个ID，在此是为了解决进来后只点击左下边部分不点击左上边的部分的问题
        if (flag == EXTRA_ADD_MODE) {// 表示点击“新增”进来
            LogUtils.e("zmy", "表示点击“新增”进来-----------------");
            if (null != flightInfoPageList && !flightInfoPageList.isEmpty()) {
                initNewIncreaseData();
            }
        } else if (flag == EXTRA_EDIT_MODE) {// 表示点击“编辑”进来
            LogUtils.e("zmy", "表示点击“编辑”进来-----------------"+(flightInfoPageList != null ? flightInfoPageList.size() : null));
            if (null != flightInfoPageList && !flightInfoPageList.isEmpty()) {
                initEditorData();
            }
            if (editPlayEntry.getFileParentPath() != null) {
                vp_edit_mould.setCurrentItem(1);
//                tv_file_select.setText(editPlayEntry.getFileParentPath());
//                iv_edit_horn.setVisibility(View.GONE);
//                ll_file_select.setVisibility(View.VISIBLE);
//                setMarquee();
            }
        }
    }

    /**
     * 初始化点击“新增”进来的数据
     */
    private void initNewIncreaseData() {
        doingFlightInfo = flightInfoPageList.get(0);//初始化默认为第一个航班
        // 点击“新增”进来，默认左上边模板标题为“值机模板”，item为第一条“开始值机模板”
        selectTTSItemId = modelDataList.get(0).getItemEntity().get(0).getId();

        // 第1步：设置模板的title
        titleName = modelDataList.get(0).getItemEntity().get(0).getName();
        tv_edit_mould_title.setText(titleName);
        // 第2步：设置模板内容，初始化默认为第一个模板,即开始值机模板。。。。。。。。。。。。。。
        selectMouldItemContent = broadcastInformation.CCheckIn(doingFlightInfo.getArrivalStation(), doingFlightInfo.getFlightInfo().getAirCompany(), doingFlightInfo.getFlightNumber());
        et_edit_mould_content.setText(selectMouldItemContent);
        // 第3步：处理3个编辑框
        et_edit_flightNum.setText(doingFlightInfo.getFlightNumber());
        et_edit_start.setText(doingFlightInfo.getDeparture());
        et_edit_stop.setText(doingFlightInfo.getArrivalStation());
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

    /**
     * 初始化点击“编辑”进来的数据
     */
    private void initEditorData() {
        // 点击“编辑”进来，左上边模板标题为和item的显示根据选中航班信息的ID来确定
        selectTTSItemId = String.valueOf(editPlayEntry.getXmlKey());
        // 第1步：设置模板的title
        for (int i = 0; i < modelDataList.size(); i++) {
            ModelEntity modelEntity = modelDataList.get(i);
            for (int j = 0; j < modelEntity.getItemEntity().size(); j++) {
                String id = modelEntity.getItemEntity().get(j).getId();
                int id1 = Integer.parseInt(id);
                if (id1 == editPlayEntry.getXmlKey()) {
                    titleName = modelEntity.getItemEntity().get(j).getName();
                    tv_edit_mould_title.setText(titleName);
                }
            }
        }
        // 第2步：  设置模板内容。。。。。。。。。。。
        et_edit_mould_content.setText(editPlayEntry.getTextDesc());

        showMediaPath(editPlayEntry.getFileParentPath());

        // 第4步：处理次数
        tv_edit_count.setText(editPlayEntry.getTimes() + getString(R.string.edit_activity_count));
        // 第5步：处理4个日期  根据日期不同格式进行转换，分别取月、日、时、分进行赋值。。。。。。。。。
        Date time = editPlayEntry.getTime();
        datePattern = "yyyy-MM-dd-HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
        String dateStr = sdf.format(time);
        String regularExpression = "-";
        String[] dateArray = dateStr.split(regularExpression);
        String regularExpression1 = ":";
        String[] timeArray = dateArray[dateArray.length - 1].split(regularExpression1);
        tv_edit_month.setText(dateArray[1] + getString(R.string.edit_activity_month));
        tv_edit_day.setText(dateArray[2] + getString(R.string.edit_activity_day));
        tv_edit_hour.setText(timeArray[0] + getString(R.string.edit_activity_hour));
        tv_edit_min.setText(timeArray[1] + getString(R.string.edit_activity_min));

        // 月和日的下拉箭头不可点击
        setArrowEnable(arrowEnableFlag);

        doingFlightInfo = editPlayEntry.getFlightInfoTemp();//初始化
        if (null == doingFlightInfo || -1 == editPlayEntry.getXmlKey()) {
            mIsCustomAudioEdit = true;
            return;
        }

        // 第3步：处理3个编辑框
        et_edit_flightNum.setText(doingFlightInfo.getFlightNumber());
        et_edit_start.setText(doingFlightInfo.getDeparture());
        et_edit_stop.setText(doingFlightInfo.getArrivalStation());

        //  进来后定位到左下边的相应的航班信息item
        setFlightInfoItem(doingFlightInfo);
    }

    /**
     * 通过不同方式进入该界面时需要调用这个方法，传递不同的boolean值来控制月、日是否可编辑
     *
     * @param arrowEnableFlag 布尔类型决定是否让月、日的下拉框可用
     */
    private void setArrowEnable(boolean arrowEnableFlag) {
        if (arrowEnableFlag) {
            iv_edit_month.setClickable(true);
            iv_edit_day.setClickable(true);
        } else {
            iv_edit_month.setBackgroundResource(R.drawable.bg_edit_xiala);
            iv_edit_day.setBackgroundResource(R.drawable.bg_edit_xiala);
            iv_edit_month.setClickable(false);
            iv_edit_day.setClickable(false);
        }
    }

    private void getMouldData() {
        compileModels = new CompileModels().newInstance();
        // 模板总集合
        modelDataList = compileModels.parseXMLByPull(this, R.raw.compile_model);

        ttsFragment = new TTSFragment();
        audioFragment = new AudioFragment();
        bundle = new Bundle();
        // 在此需要将模板集合内容存入bundle，传到TTSFragment中。。。。。。。。。
        bundle.putParcelableArrayList("modelData", (ArrayList<? extends Parcelable>) modelDataList);
        if (flag == EXTRA_EDIT_MODE) {// 表示点击“编辑”进来
            bundle.putInt("xmlKey", editPlayEntry.getXmlKey());
        }
        ttsFragment.setArguments(bundle);
        fragmentList = new ArrayList<>();
        fragmentList.add(ttsFragment);
        fragmentList.add(audioFragment);
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.list_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(setDividerColor())
                .size(setDividerSize())
                .build());

        mRecycleAdapter = new RecycleAdapter(this, BaseListAdapter.NEITHER);
        mRecycleAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mRecycleAdapter);

        onLoadResult();


        ll_edit_flightNum_row = (LinearLayout) findViewById(R.id.ll_edit_flightNum_row);
        ll_file_select = (LinearLayout) findViewById(R.id.ll_file_select);
        tv_file_select = (TextView) findViewById(R.id.tv_file_select);
        btn_file_select = (Button) findViewById(R.id.btn_file_select);
        header_left_btn = (TextView) findViewById(R.id.header_left_btn);
        header_left_btn.setVisibility(View.VISIBLE);
        header_text = (TextView) findViewById(R.id.header_text);
        if (flag == 0) {
            header_text.setText(getString(R.string.edit_activity_increase));
        } else {
            header_text.setText(getString(R.string.edit_activity_edit));
        }
        tv_edit_tts = (TextView) findViewById(R.id.tv_edit_tts);
        webView_edit = (WebView) findViewById(R.id.webView_edit);
        webView_edit.setBackgroundColor(0);
        webView_edit.getSettings().setJavaScriptEnabled(true);
        webView_edit.addJavascriptInterface(new JsToJava(), "stub");
        webView_edit.setWebViewClient(new MyWebViewClient());
        tv_edit_audio = (TextView) findViewById(R.id.tv_edit_audio);
        tv_edit_audio.setVisibility(View.VISIBLE);
        iv_edit_line = (ImageView) findViewById(R.id.iv_edit_line);
        vp_edit_mould = (ViewPager) findViewById(R.id.vp_edit_mould);
        tv_edit_mould_title = (TextView) findViewById(R.id.tv_edit_mould_title);
        et_edit_mould_content = (EditText) findViewById(R.id.et_edit_mould_content);
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
        iv_edit_horn = (ImageView) findViewById(R.id.iv_edit_horn);
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
        ViewPropertyAnimator.animate(tv_edit_tts).scaleX(1.2f).scaleY(1.2f);
        ViewPropertyAnimator.animate(tv_edit_audio).scaleX(0.8f).scaleY(0.8f);
        setMarquee();
        titleName = getResources().getString(R.string.zhiji);
    }

    private void setMarquee() {
        tv_file_select.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tv_file_select.setFocusableInTouchMode(true);
        tv_file_select.setFocusable(true);
        tv_file_select.requestFocusFromTouch();
        tv_file_select.requestFocus();
    }

    private void initData() {
        if (flag == 1) {
            btn_edit_insert.setText(getString(R.string.edit_activity_save));
        }
        broadcastInformation = new BroadcastInformation();
        fragmentAdapter = new MyFragmentAdapter(getSupportFragmentManager());
        lvAdapter = new BroadcastFlightInfoAdapter(ctx, flightInfoPageList);
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
        ubxu = new UrgentBroadcastXmlUtils(ctx);
        searchBar.setSearchBarCallback(new ISearchBarCallback() {

            @Override
            public void setItemInfo(final FlightInfoTemp flightInfoTemp, int position) {
                long chooseId = flightInfoTemp.getId();
                mRecycleAdapter.setChooseId(chooseId);
                mRecycleAdapter.notifyDataSetChanged();
                setRightInfo(1, -1, flightInfoTemp);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        for (int i = 0; i < flightInfoPageList.size(); i++) {
                            if (flightInfoTemp.getId() == flightInfoPageList.get(i).getId()) {
                                Message msg = new Message();
                                msg.what = 0;
                                msg.arg1 = i;
                                mHandler.sendMessage(msg);
                                return;
                            }
                        }
                    }
                }.start();
            }

            // 表示搜索框中每输入一个字符需要执行的动作
            @Override
            public void setChangeEditText(String text) {
                // 2016/9/22 查询方法
                searchFlightInfoList = dbManager.queryFlightTempNumberLike(text);
                searchBar.upDateListView(searchFlightInfoList);
            }
        });
    }

    private void setFlightInfoItem(FlightInfoTemp flightInfoTemp) {
        flightInfoPageList.add(flightInfoPageList.size() - 10, flightInfoTemp);
//        lvAdapter.changeSelected(flightInfoPageList.size() - 11);
//        lv_edit_flight_info.setSelection(flightInfoPageList.size() - 11);
    }

    private void setMouldContent() {
        isWebView = false;
        for (int i = 0; i < modelDataList.size(); i++) {
            ModelEntity modelEntity = modelDataList.get(i);
            for (int j = 0; j < modelEntity.getItemEntity().size(); j++) {
                String itemId = modelEntity.getItemEntity().get(j).getId();
                if (itemId.equals(selectTTSItemId)) {
                    switch (itemId) {
 /*值机模板*/
                        case "910": //开始值机模版
                            selectMouldItemContent = broadcastInformation.CCheckIn(doingFlightInfo.getArrivalStation(), doingFlightInfo.getFlightInfo().getAirCompany(), doingFlightInfo.getFlightNumber());
                            break;
                        case "911"://催促旅客值机模版
                            selectMouldItemContent = broadcastInformation.CUrgeCheckIn(doingFlightInfo.getArrivalStation(), doingFlightInfo.getFlightInfo().getAirCompany(), doingFlightInfo.getFlightNumber());
                            break;
                        case "912":// 最后值机催促模版
                            selectMouldItemContent = broadcastInformation.CLastUrgeCheckIn(doingFlightInfo.getArrivalStation(), doingFlightInfo.getFlightInfo().getAirCompany(), doingFlightInfo.getFlightNumber());
                            break;
                        case "913"://催促安检
                            selectMouldItemContent = broadcastInformation.CUrgeSecurityCheck(doingFlightInfo.getArrivalStation(), doingFlightInfo.getFlightInfo().getAirCompany(), doingFlightInfo.getFlightNumber());
                            break;
                        case "914"://值机延误（有时间、无时间）
                            selectMouldItemContent = broadcastInformation.CDelayCheckIn(doingFlightInfo.getArrivalStation(), doingFlightInfo.getFlightInfo().getAirCompany(), doingFlightInfo.getFlightNumber());
                            break;
                        case "915"://值机延误（有时间、无时间）
                            isWebView = true;
                            et_edit_mould_content.setVisibility(View.GONE);
                            webView_edit.setVisibility(View.VISIBLE);
                            setWebView();
                            break;
/*登机模板*/
//                        case "915"://过站旅客候机
//                            selectMouldItemContent = broadcastInformation.COverStationWait(doingFlightInfo.getDestinationStation());
//                            break;
//                        case "1020"://开始登机模版
////                            selectMouldItemContent = broadcastInformation.CStartBoard(doingFlightInfo.getDestinationStation(), doingFlightInfo.getFlightInfo().getAirlineCompany().getAirlineCompanyName(), doingFlightInfo.getFlightNumber(), );
//                            break;
//                        case "1021"://催促旅客登机模版
////                            selectMouldItemContent = broadcastInformation.CUrgeBoard(doingFlightInfo.getDestinationStation(), doingFlightInfo.getFlightInfo().getAirlineCompany().getAirlineCompanyName(), doingFlightInfo.getFlightNumber(),);
//                            break;
//                        case "1022"://过站旅客登机
////                            selectMouldItemContent = broadcastInformation.COverStationBoard(doingFlightInfo.getDestinationStation(), doingFlightInfo.getFlightInfo().getAirlineCompany().getAirlineCompanyName(), doingFlightInfo.getFlightNumber(),);
//                            break;
//                        case "1023"://催促过站旅客登机
////                            selectMouldItemContent = broadcastInformation.CUrgeOverStationBoard(doingFlightInfo.getDestinationStation(), doingFlightInfo.getFlightInfo().getAirlineCompany().getAirlineCompanyName(), doingFlightInfo.getFlightNumber(), );
//                            break;
///*自定义模板*/
//                        case "1101"://出港延误
//                            selectMouldItemContent = broadcastInformation.CDepartureDelay(doingFlightInfo.getDestinationStation(), doingFlightInfo.getFlightInfo().getAirlineCompany().getAirlineCompanyName(), doingFlightInfo.getFlightNumber());
//
//                            break;
//                        case "1102"://旅客宾馆通知
//                            selectMouldItemContent = broadcastInformation.CHotelNotice(doingFlightInfo.getDestinationStation(), doingFlightInfo.getFlightInfo().getAirlineCompany().getAirlineCompanyName(), doingFlightInfo.getFlightNumber(), doingFlightInfo.getBoardingGate());
//                            break;
//                        case "1103"://呼叫旅客
////                            selectMouldItemContent = broadcastInformation.CCallTraveller()
//                            break;
//                        case "1104"://航班取消
//                            selectMouldItemContent = broadcastInformation.CFlightCancel(doingFlightInfo.getDestinationStation(), doingFlightInfo.getFlightInfo().getAirlineCompany().getAirlineCompanyName(), doingFlightInfo.getFlightNumber());
//                            break;
//                        case "1105"://特殊航班服务
////                            selectMouldItemContent = broadcastInformation.CSpecialService()
//                            break;
//                        case "1106"://登机口变更
////                            selectMouldItemContent = broadcastInformation.CChangeBoardNumber(doingFlightInfo.getDestinationStation(), doingFlightInfo.getFlightInfo().getAirlineCompany().getAirlineCompanyName(), doingFlightInfo.getFlightNumber(), );
//                            break;
//                        case "1107"://全部延误通知
//                            selectMouldItemContent = broadcastInformation.CAllDelay();
//                            break;
//                        case "1108"://失物招领
//                            selectMouldItemContent = broadcastInformation.CLostAndFound();
//                            break;
//                        case "1109"://旅客到达引导
//                            selectMouldItemContent = broadcastInformation.CTravellerGuide(doingFlightInfo.getDestinationStation());
//                            break;
                        case "1110"://自定义TTS
                            selectMouldItemContent = getString(R.string.input_edit_content);
//                            setEditText(2);
                            break;
                    }
                    if (!isWebView) {
                        et_edit_mould_content.setVisibility(View.VISIBLE);
                        webView_edit.setVisibility(View.GONE);
                    }
                    titleName = modelEntity.getItemEntity().get(j).getName();
                    if (getString(R.string.edit_custom_tts).equals(titleName)) {
                        ll_edit_flightNum_row.setVisibility(View.GONE);
                        isCustomTts = true;
                    } else {
                        ll_edit_flightNum_row.setVisibility(View.VISIBLE);
                        isCustomTts = false;
                    }
                    // 设置模板名字
                    tv_edit_mould_title.setText(titleName);
                    // 设置模板内容
                    if (itemId.equals(String.valueOf(1110))) {
                        et_edit_mould_content.setText(null);
                        et_edit_mould_content.setHint(selectMouldItemContent);
                        et_edit_mould_content.setEnabled(true);
                    } else if (!isWebView && !itemId.equals(String.valueOf(1110))) {
                        et_edit_mould_content.setText(selectMouldItemContent);
                        et_edit_mould_content.setEnabled(false);
                    }
                }
            }
        }
    }

    private String selectType = "E-1-6";

    private void setWebView() {
        mouldContent = ubxu.clickFlightInfo(selectType, doingFlightInfo);
        webView_edit.loadUrl(ubxu.hashMap.get(selectType).getFile());
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
        tv_edit_tts.setOnClickListener(this);
        tv_edit_audio.setOnClickListener(this);
        iv_edit_horn.setOnClickListener(this);
        btn_file_select.setOnClickListener(this);

        vp_edit_mould.setAdapter(fragmentAdapter);
        vp_edit_mould.setOnPageChangeListener(new OnMainPageChangeListener());
//        if (null != flightInfoPageList && !flightInfoPageList.isEmpty()) {
//            lv_edit_flight_info.setAdapter(lvAdapter);
//        }
//        lv_edit_flight_info.setOnItemClickListener(new OnMyClickListener());
//
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
                    adjustDay();//当这个月没有29号或者30号或者31号时，调整日期
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
        audioFragment.setOnEditTextEnable(new AudioFragment.OnEditTextEnable() {
            @Override
            public void onEditTextEnable(int selectAudioItemPosition) {
                if (selectAudioItemPosition == 0) {
                    tv_edit_mould_title.setText(R.string.self_definition_audio);
                } /*else {
                    tv_edit_mould_title.setText(R.string.self_definition_audio_mould);
                }*/
            }
        });
//        refresh_edit_flight_info.setRefreshing(true);
//        refresh_edit_flight_info.setOnLoadListener(new OnMyLoadListener());
        iv_edit_horn.setOnClickListener(this);

        //  处理点击喇叭后进行录音的相关逻辑
//        iv_edit_horn.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                audioFile = RecAudioUtils.audioStart(name);
//                iv_edit_horn.setImageResource(R.drawable.anim);
//                animationDrawable = (AnimationDrawable) iv_edit_horn.getDrawable();
//                animationDrawable.start();
//                return true;
//            }
//        });
//        iv_edit_horn.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_UP:
//                        RecAudioUtils.audioStop();
//                        if (animationDrawable != null) {
//
//                            animationDrawable.stop();
//                            iv_edit_horn.setImageResource(R.drawable.iv_edit_horn);
//                        }
//                        break;
//                }
//                return false;
//            }
//        });
//        et_edit_flightNum.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setTypewriting();
//            }
//        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.header_left_btn:
                finish();
                EventBus.getDefault().post(new SimpleEvent(11));
                break;
            case R.id.btn_edit_insert:
                //  执行保存编辑信息的逻辑
                if (isWebView) {
                    getWebViewData();
                } else {
                    submit();
                }
                if (flag == EXTRA_ADD_MODE) {
                    EventBus.getDefault().postSticky(new SimpleEvent(Constants.BROADCAST_ADD));
                } else if (flag == EXTRA_EDIT_MODE) {
                    EventBus.getDefault().postSticky(new SimpleEvent(Constants.Analytic_Cmpletion_Notice));
                }
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
                if(!canAudioToTtsEdit()){
                    return;
                }
                vp_edit_mould.setCurrentItem(0);
                break;
            case R.id.tv_edit_audio:
                vp_edit_mould.setCurrentItem(1);
                break;
            case R.id.iv_edit_horn:
                openSelectFileActivity();
                break;
            case R.id.btn_file_select:
                iv_edit_horn.setVisibility(View.VISIBLE);
                ll_file_select.setVisibility(View.GONE);
                tv_file_select.setText(null);
                break;
        }
    }

    private boolean canAudioToTtsEdit() {
        if(mIsCustomAudioEdit){
            //如果是编辑自定义的录音播报，不能进行TTS编辑
            Toast.makeText(this,"该广播是自定义录音，不能进行TTS编辑！",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                View view = getCurrentFocus();
                hideKeyboard(ev, view, this);//调用方法判断是否需要隐藏键盘
                break;

            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(MotionEvent event, View view, Activity activity) {
        try {
            if (view != null && view instanceof EditText) {
                int[] location = {0, 0};
                view.getLocationInWindow(location);
                int left = location[0], top = location[1], right = left
                        + view.getWidth(), bottom = top + view.getHeight();
                // 判断焦点位置坐标是否在空间内，如果位置在控件外，则隐藏键盘
//                if (event.getRawX() < left || event.getRawX() > right
//                        || event.getY() < top || event.getRawY() > bottom) {
                if (event.getRawX() > left || event.getRawX() < right
                        || event.getY() > top || event.getRawY() < bottom) {
                    // 隐藏键盘
                    IBinder token = view.getWindowToken();
                    InputMethodManager inputMethodManager = (InputMethodManager) activity
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(token,
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getWebViewData() {
        webView_edit.loadUrl(ubxu.hashMap.get(selectType).getGet_data());
    }

    private void openSelectFileActivity() {
        Intent intent = new Intent(this, FlightExcelActivity.class);
        intent.putExtra("type", 2);
        startActivityForResult(intent, FileSelectFragment.FILE_PICKER_REQUEST_CODES);
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("file/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        try {
//            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_PICKER_REQUEST_CODE);
//        } catch (android.content.ActivityNotFoundException ex) {
//            ex.printStackTrace();
//        }
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

    private int audioFlag = -1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FileSelectFragment.FILE_PICKER_REQUEST_CODES && resultCode == RESULT_OK) {
            String path = data.getStringExtra(FlightExcelActivity.REQUEST_FILE_PATH);
//            String[] audioFlagArray = new String[]{"mp3","3gpp", "M4A", "WAV", "AMR", "AWB", "WMA", "OGG", "MID", "XMF", "RTTTL", "SMF", "IMY", "flac", "ape", "aac", "VQF", "eAAC+",};
//            String path1 = path.substring(path.indexOf(".") + 1);
//            for (String audio : audioFlagArray) {
//                boolean isAudioFile = path1.equalsIgnoreCase(audio);
//                if (isAudioFile) {
//                    audioFlag = 1;
//                }
//            }
//            if (audioFlag == 1) {
//                tv_file_select.setText(path);
//                audioFlag = -1;
//            } else {
//                tv_file_select.setText(R.string.FileSelectFragment_path_wrong);
//            }
            showMediaPath(path);

        }
    }

    private void showMediaPath(String path) {
        if(TextUtils.isEmpty(path)){
            return;
        }
        //  给textvie赋值
        tv_file_select.setText(path);
        iv_edit_horn.setVisibility(View.GONE);
        ll_file_select.setVisibility(View.VISIBLE);
        setMarquee();
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

    private void setHorn() {
        String tv_path = (String) tv_file_select.getText();
        boolean isTextViewNull = tv_path.contains(".");
        if (isVisibleFlag == 1) {// 表示滑动到录音的fragment时
            if (isTextViewNull) {// 表示textview有路径值，
                iv_edit_horn.setVisibility(View.GONE);
                ll_file_select.setVisibility(View.VISIBLE);
                setMarquee();
            } else {// 表示textview没有有路径值
                iv_edit_horn.setVisibility(View.VISIBLE);
            }
            et_edit_mould_content.setVisibility(View.GONE);
        } else {// 表示滑动到TTS的fragment时
            et_edit_mould_content.setVisibility(View.VISIBLE);
            iv_edit_horn.setVisibility(View.GONE);
            ll_file_select.setVisibility(View.GONE);
        }
    }

    private void submit() {
        // validate
        if (null == flightInfoPageList || flightInfoPageList.isEmpty()) {
            Toast.makeText(ctx, R.string.errorOperatePrompt, Toast.LENGTH_LONG).show();
            return;
        }
        if (isVisibleFlag == 0) {// 表示滑动到tts的fragment时
            String mouldContent = et_edit_mould_content.getText().toString().trim();
            if (TextUtils.isEmpty(mouldContent)) {
                Toast.makeText(this, "\"" + getString(R.string.edit_mould_content) + "\"" + getString(R.string.edit_mould_isnull), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String flightNum = et_edit_flightNum.getText().toString().trim();
        if (TextUtils.isEmpty(flightNum)) {
            Toast.makeText(this, "\"" + tv_edit_flightNum.getText() + "\"" + getString(R.string.edit_activity_notnull), Toast.LENGTH_SHORT).show();
            return;
        }

        String start = et_edit_start.getText().toString().trim();
        if (TextUtils.isEmpty(start)) {
            Toast.makeText(this, "\"" + tv_edit_start.getText() + "\"" + getString(R.string.edit_activity_notnull), Toast.LENGTH_SHORT).show();
            return;
        }
        String stop = et_edit_stop.getText().toString().trim();
        if (TextUtils.isEmpty(stop)) {
            Toast.makeText(this, "\"" + tv_edit_destination.getText() + "\"" + getString(R.string.edit_activity_notnull), Toast.LENGTH_SHORT).show();
            return;
        }
        if (isWebView && getString(R.string.edit_incomplete_info).equals(editedMouldContent)) {
            Toast.makeText(this, getString(R.string.edit_outside_brackets_info), Toast.LENGTH_LONG).show();
            return;
        }
//        判断是否是录音的Fragment，并且判断选择的音频文件格式正确
        String tv_path = (String) tv_file_select.getText();
        if (TextUtils.isEmpty(tv_path) && isAudioFragment) {
            Toast.makeText(ctx, R.string.FileSelectFragment_path_null, Toast.LENGTH_LONG).show();
            return;
        }
        if (getString(R.string.FileSelectFragment_path_wrong).equals(tv_path) && isAudioFragment) {
            Toast.makeText(ctx, getString(R.string.FileSelectFragment_path_wrong), Toast.LENGTH_LONG).show();
            return;
        }
        saveEditInfoAndExit();
    }

    private void saveEditInfoAndExit() {
        Intent intent = new Intent();
        if (flag == 0) {// 表示点击“新增”进来
            Set set = SPUtils.getPrefStringSet(this, "set", null);
            if (1 == mSelect) {
                if (SPUtils.getPrefInt(this, "STATUS", -1) == 0 ||
                        SPUtils.getPrefInt(this, "STATUS", -1) == -1 ||
                        null == set || set.size() == 0) {
                    Toast.makeText(this, "未连接任何设备无法传输", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            increasePlayEntry = new PlayEntry();
            setPlayEntryValue(increasePlayEntry);
            dbManager.insert(increasePlayEntry, dbManager.getPlayEntryDao(DBManager.WRITE_ONLY));

            Toast.makeText(ctx, getString(R.string.edit_activity_increase_success), Toast.LENGTH_SHORT).show();
            intent.putExtra("playEntryId", increasePlayEntry.getId());
            Constants.TEMP_PLAYEN_ID = increasePlayEntry.getId();

            if (null != set) {
                Iterator iterator = set.iterator();
                while (iterator.hasNext()) {
                    String ip = (String) iterator.next();
                    MinaStringClientThread minaStringClientThread = new MinaStringClientThread();
                    minaStringClientThread.setType(Constants.A_DATA_ADD);
                    minaStringClientThread.setPlayVO(new PlayVO(increasePlayEntry));
                    minaStringClientThread.setIp(ip);
                    MinaStringClientThread.getThreadPoolExecutor().execute(minaStringClientThread);
                }
            }
        } else if (flag == 1) {// 表示点击“编辑”进来
            Log.e("wjq","mSelect = " + mSelect);
            Set set = SPUtils.getPrefStringSet(this, "set", null);
            if (1 == mSelect) {
                if (SPUtils.getPrefInt(this, "STATUS", -1) == 0 ||
                        SPUtils.getPrefInt(this, "STATUS", -1) == -1 ||
                        null == set || set.size() == 0) {
                    Toast.makeText(this, "未连接任何设备无法传输", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            setPlayEntryValue(editPlayEntry);
            editPlayEntry.update();
            Toast.makeText(ctx, getString(R.string.edit_activity_edit_success), Toast.LENGTH_SHORT).show();
            intent.putExtra("playEntryId", editPlayEntry.getId());
            if (null != editPlayEntry.getFileParentPath()) {
                if (null != set) {
                    Iterator iterator = set.iterator();
                    while (iterator.hasNext()) {
                        String ip = (String) iterator.next();
                        Log.e("wjq","ip = " + ip);
                        MinaFileClientThread minaFileClientThread = new MinaFileClientThread();
                        minaFileClientThread.setType(Constants.MD_FILE_UPDATE);
                        minaFileClientThread.setPlayVO(new PlayVO(editPlayEntry));
                        minaFileClientThread.setIp(ip);
                        String MDmd5sum = MD5Util.getFileMD5String(new File(editPlayEntry.getFileParentPath()));
                        minaFileClientThread.setMd5sum(MDmd5sum);
                        MinaStringClientThread.getThreadPoolExecutor().execute(minaFileClientThread);
                        Toast.makeText(this, "开始向ip地址为" + minaFileClientThread.getIp() + "音响发送录音文件", Toast.LENGTH_LONG).show();
                    }
                }
            }else if (null != set) {
                Iterator iterator = set.iterator();
                while (iterator.hasNext()) {
                    String ip = (String) iterator.next();
                    MinaStringClientThread minaStringClientThread = new MinaStringClientThread();
                    minaStringClientThread.setType(Constants.A_DATA_UPDATE);
                    minaStringClientThread.setPlayVO(new PlayVO(editPlayEntry));
                    minaStringClientThread.setIp(ip);
                    MinaStringClientThread.getThreadPoolExecutor().execute(minaStringClientThread);
                }
            }
        }
        try {
            MBroadcastApplication.getIMyAidlInterface().refresh();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
        if (flag == 0) {// 表示点击“新增”进来
            Date now = new Date();
            Date date = new Date(now.getYear(), Integer.parseInt(month) - 1, Integer.parseInt(day), Integer.parseInt(hour), Integer.parseInt(min), 0);
            playEntry.setTime(date);
        } else if (flag == 1) {// 表示点击“编辑”进来
            Date now = playEntry.getTime();
            Date date = new Date(now.getYear(), now.getMonth(), now.getDate(), Integer.parseInt(hour), Integer.parseInt(min), 0);
            playEntry.setTime(date);
        }
        count = tv_edit_count.getText().toString().trim();
        playEntry.setTimes(Integer.parseInt(count.substring(0, count.indexOf(getString(R.string.edit_activity_count)))));
        if (isAudioFragment) {// 表明是录音模块
            String tv_path = (String) tv_file_select.getText();
            if (tv_path.contains(".")) {
                playEntry.setFileParentPath(tv_path);
                playEntry.setFileName(tv_path.substring(tv_path.lastIndexOf(File.separator) + 1));
            }
            playEntry.setFlightInfoTemp(null);
        } else if (isCustomTts) {// 表明是自定义tts模板
            playEntry.setTextDesc(String.valueOf(et_edit_mould_content.getText()));
            playEntry.setFlightInfoTemp(null);
        } else {// 表明是正常模板
            if (isWebView) {
                playEntry.setTextDesc(editedMouldContent);
            } else {
                playEntry.setTextDesc(String.valueOf(et_edit_mould_content.getText()));
            }
            if (selectTTSItemId != null) {
                playEntry.setXmlKey(Integer.parseInt(selectTTSItemId));
            }
            if (doingFlightInfo != null) {
                playEntry.setFlightInfoTemp(doingFlightInfo);
            }
        }


        playEntry.setDoTimes(0);
        playEntry.setIsQueue(0);
        playEntry.setIsTemporary(true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != webView_edit) {
            webView_edit.destroy();
        }
        mCurrentPage = 0;
        mSubscription.unsubscribe();
    }

    private Subscription mSubscription;
    private void onLoadResult() {
        Observable<List<FlightInfoTemp>> observable = DBManager.getInstance(
                this).queryAll1(DBManager.getInstance(this).getFlightInfoTempDao(DBManager.READ_ONLY));
        mSubscription = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<FlightInfoTemp>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(final List<FlightInfoTemp> flightInfoTemps) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                flightInfoPageList = flightInfoTemps;
                                mRecycleAdapter.addItems(flightInfoTemps);
                                initEditInfo();
                            }
                        }, 0);
                    }
                });
    }


    public int setDividerSize() {
        return (int) getResources().getDimension(R.dimen.divider_height);
    }

    public int setDividerColor() {
        return getResources().getColor(R.color.listview_title_text);
    }


}
