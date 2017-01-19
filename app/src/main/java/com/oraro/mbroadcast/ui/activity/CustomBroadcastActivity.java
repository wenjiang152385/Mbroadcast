package com.oraro.mbroadcast.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.model.FlightInfoTemp;
import com.oraro.mbroadcast.model.UrgentItemBean;
import com.oraro.mbroadcast.ui.adapter.BaseListAdapter;
import com.oraro.mbroadcast.ui.adapter.BroadcastFlightInfoAdapter;
import com.oraro.mbroadcast.ui.adapter.RecycleAdapter;
import com.oraro.mbroadcast.ui.widget.EditRefreshLayout;
import com.oraro.mbroadcast.utils.DataUtils;
import com.oraro.mbroadcast.utils.LogUtils;
import com.oraro.mbroadcast.utils.UrgentBroadcastXmlUtils;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.oraro.mbroadcast.signature.GetSign.s;

public class CustomBroadcastActivity extends AppCompatActivity implements View.OnClickListener, BaseListAdapter.OnLoadingListener, BaseListAdapter.OnItemClickListener {

    private TextView header_left_btn;
    private ImageView header_left_img;
    private String TAG = "zmy";
    private TextView header_text;
    private TextView tv_edit_mould_title;
    private WebView edit_webView;
    private TextView tv_edit_flightNum;
    private EditText et_edit_flightNum;
    private EditText et_edit_start;
    private EditText et_edit_stop;
    private Button btn_save;
    private LinearLayout activity_custom_broadcast;
    private Context ctx;
    private List<FlightInfoTemp> flightInfoPageList;
    private int page = 0;
    private BroadcastFlightInfoAdapter lvAdapter;
    private FlightInfoTemp doingFlightInfo;//当前选中的航班信息
    private String titleName;
    private UrgentBroadcastXmlUtils ubxu;
    private String mouldContent;
    private String type;
    private List<String> selectedFlightnoList;
    private String jsParams;


    private RecyclerView mListView;
    private RecycleAdapter mRecycleAdapter;
    private int mCurrentPage = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        }
    };
    private DBManager dbManager;
    private String editedMouldContent;
    private boolean isPiLiang;
    private UrgentItemBean bean;
    private String[] paramAra;
    private String lastParams;
    private LinearLayout linearLayout;


    private class JsToJava {
        @JavascriptInterface
        public void jsMethod(String paramFromJS) {
//            LogUtils.e("zmy", "js返回结果=============" + paramFromJS);//处理返回的结果
            editedMouldContent = paramFromJS;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    submit();
                }
            });
        }

        @JavascriptInterface
        public void getJsParams(String params) {
//            LogUtils.e("zmy", "js返回参数=============" + params);//处理返回的结果
            jsParams = params;
        }

        @JavascriptInterface
        public void removeFlightnoId(String flightno) {
//            LogUtils.e("zmy", "removeFlightnoId=============" + flightno);//处理返回的结果

            selectedFlightnoList.remove(flightno);
        }
    }


    private class OnMyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // 初始化时设置模板内容
            if (mouldContent != null) {
                edit_webView.loadUrl(mouldContent);
            }
            edit_webView.setVisibility(View.VISIBLE);
            if (lastParams.equals("无参数")) {
                return;
            }
            if (lastParams != null) {
                edit_webView.loadUrl(ubxu.getLastJsParams(type, lastParams));
            }
            view.loadUrl("javascript:changeStyle('" + Constants.WEBVIEW_CAN_EDIT + "')");

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    private void setRightInfo(int position) {
        doingFlightInfo = flightInfoPageList.get(position);
        // 根据传进来的航班信息集合（假定为flightInfoPageList）给3个编辑框赋值。。。。。。
        et_edit_flightNum.setText(doingFlightInfo.getFlightNumber());
        et_edit_start.setText(doingFlightInfo.getDeparture());
        et_edit_stop.setText(doingFlightInfo.getArrivalStation());
        //  textView月、日、时、分无需再设置
        // 在此处理根据点击左边不同的航班信息item，右边切换相应模板内容的逻辑。。。。。。。。。
        if (null != flightInfoPageList && !flightInfoPageList.isEmpty()) {
            String mSetFlightno = doingFlightInfo.getFlightNumber();

            if (isPiLiang && selectedFlightnoList.contains(mSetFlightno)) {// 如果是批量延误模板
                Toast.makeText(ctx, R.string.urgent_broadcast_flightNum_repeat, Toast.LENGTH_SHORT).show();
                return;
            }
            mouldContent = ubxu.clickFlightInfo(type, doingFlightInfo);
            edit_webView.loadUrl(mouldContent);
            selectedFlightnoList.add(mSetFlightno);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_broadcast);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ctx = this;
        initView();
        initData();
        initListener();

        onLoadResult(0);
    }

    private void initView() {
        mListView = (RecyclerView) findViewById(R.id.list_view);
        mListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mListView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(setDividerColor())
                .size(setDividerSize())
                .build());
        mRecycleAdapter = new RecycleAdapter(this, BaseListAdapter.ONLY_FOOTER);
        mRecycleAdapter.setOnLoadingListener(this);
        mRecycleAdapter.setOnItemClickListener(this);
        mListView.setAdapter(mRecycleAdapter);

        header_left_img = (ImageView) findViewById(R.id.header_left_img);
        header_left_img.setVisibility(View.GONE);
        header_left_btn = (TextView) findViewById(R.id.header_left_btn);
        header_left_btn.setVisibility(View.VISIBLE);
        header_text = (TextView) findViewById(R.id.header_text);
        header_text.setText("自定义广播");
        tv_edit_mould_title = (TextView) findViewById(R.id.tv_edit_mould_title);
        linearLayout = (LinearLayout) findViewById(R.id.linear_content);
        edit_webView = new WebView(this);
        edit_webView.setVisibility(View.GONE);
        edit_webView.setBackgroundColor(0);
        edit_webView.getSettings().setJavaScriptEnabled(true);
        edit_webView.getSettings().setBuiltInZoomControls(false);
        edit_webView.getSettings().setSupportZoom(false);
        edit_webView.getSettings().setDisplayZoomControls(false);
        edit_webView.addJavascriptInterface(new JsToJava(), "stub");
        edit_webView.setWebViewClient(new OnMyWebViewClient());
        linearLayout.addView(edit_webView);
        tv_edit_flightNum = (TextView) findViewById(R.id.tv_edit_flightNum);
        et_edit_flightNum = (EditText) findViewById(R.id.et_edit_flightNum);
        et_edit_start = (EditText) findViewById(R.id.et_edit_start);
        et_edit_stop = (EditText) findViewById(R.id.et_edit_stop);
        btn_save = (Button) findViewById(R.id.btn_save);
        activity_custom_broadcast = (LinearLayout) findViewById(R.id.activity_custom_broadcast);

        et_edit_flightNum.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        header_left_btn.setOnClickListener(this);
    }

    private void initData() {
        Bundle bundle = getIntent().getBundleExtra("bundle");
        type = bundle.getString("type");
        lastParams = bundle.getString("params");
        selectedFlightnoList = new ArrayList<>();

        if ("1-4".equals(type)) {// 批量模板
            isPiLiang = true;
//
        }
        ubxu = new UrgentBroadcastXmlUtils(ctx);
        editedMouldContent = getString(R.string.edit_incomplete_info);
        dbManager = DBManager.getInstance(ctx);
        flightInfoPageList = dbManager.queryFlightTempByPagenum(page);
        if (flightInfoPageList != null && !flightInfoPageList.isEmpty()) {
            lvAdapter = new BroadcastFlightInfoAdapter(ctx, flightInfoPageList);
        }
    }

    private void initListener() {
        if (null != flightInfoPageList && !flightInfoPageList.isEmpty()) {
        }
    }

    private void initRightData() {
        if (flightInfoPageList != null && !flightInfoPageList.isEmpty()) {
            doingFlightInfo = flightInfoPageList.get(0);//初始化默认为第一个航班
        }
        // 第1步：设置模板的title
        tv_edit_mould_title.setText(ubxu.hashMap.get(type).getTitle());

        // 第2步：设置模板内容，初始化默认为第一个模板。。。。。。。。。。。。。。
        edit_webView.loadUrl(ubxu.hashMap.get(type).getFile());

        if (flightInfoPageList != null && !flightInfoPageList.isEmpty()) {
            mouldContent = ubxu.clickFlightInfo(type, doingFlightInfo);
            // 第3步：处理3个编辑框
            et_edit_flightNum.setText(doingFlightInfo.getFlightNumber());
            et_edit_start.setText(doingFlightInfo.getDeparture());
            et_edit_stop.setText(doingFlightInfo.getArrivalStation());
            if (isPiLiang) {
                lastParams = lastParams.replace("  " + doingFlightInfo.getFlightNumber(), "");
                String[] split = lastParams.split("  ");
                for (int i = 1; i < split.length; i++) {
                    selectedFlightnoList.add(split[i]);
                }
                selectedFlightnoList.add(doingFlightInfo.getFlightNumber());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_edit_flightNum:

                break;
            case R.id.btn_save:
                edit_webView.loadUrl(ubxu.hashMap.get(type).getGet_data());

                break;
            case R.id.header_left_btn:
                finish();
                break;
        }
    }

    private void submit() {
        /**
         * 由于在重新进入该页面时调用getLastJsParams设置上次的参数，但是会将jsParams中的\当作转义处理
         * 所以需要修改为，如果jsParams中有一个\即"\\",需要用\\\\即"\\\\\\\\"
         * 这样保存在数据库中一个"\"对应"\\\\",再将"\\\\"通过replaceFirst方法后变成了"\\"
         * 最后传给js后，将"\\"转义处理为真正的\
         */
        if(!TextUtils.isEmpty(jsParams)){
            jsParams = jsParams.replace("\\","\\\\\\\\");
        }

        if (null == flightInfoPageList || flightInfoPageList.isEmpty()) {
            Toast.makeText(this, R.string.errorOperatePrompt, Toast.LENGTH_LONG).show();
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
        // TODO validate success, do something
        saveData();

    }

    private void saveData() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        bundle.putBoolean("isPlay", false);
        bundle.putString("content", editedMouldContent);
        bundle.putString("params", jsParams);
        LogUtils.e(TAG, "jsParams==bundle===========" + jsParams);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (edit_webView != null) {
            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
            // destory()
            ViewParent parent = edit_webView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(edit_webView);
            }

            edit_webView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            edit_webView.getSettings().setJavaScriptEnabled(false);
            edit_webView.clearHistory();
            edit_webView.clearView();
            edit_webView.removeAllViews();

            try {
                edit_webView.destroy();
            } catch (Throwable ex) {

            }
        }
        if (null != mSubscription) {
            mSubscription.unsubscribe();
        }

    }


    public int setDividerSize() {
        return (int) getResources().getDimension(R.dimen.divider_height);
    }

    public int setDividerColor() {
        return getResources().getColor(R.color.listview_title_text);
    }


    @Override
    public void onLoading() {
        mCurrentPage++;
        mRecycleAdapter.setState(BaseListAdapter.STATE_LOADING);
        onLoadResult(mCurrentPage);
    }


    @Override
    public void onItemClick(int position, long id, View view) {
        if (flightInfoPageList.size() <= position || position < 0) {
            return;
        }
        mRecycleAdapter.notifyDataSetChanged();
        setRightInfo(position);
    }


    private Subscription mSubscription;
    private void onLoadResult(int page) {
        Observable<List<FlightInfoTemp>> observable = DBManager.getInstance(this).queryFlightTempPagenumByRx(page);
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
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (flightInfoTemps.size() > 0) {
                                    mRecycleAdapter.setState(BaseListAdapter.STATE_LOAD_MORE);
                                } else {
                                    mRecycleAdapter.setState(BaseListAdapter.STATE_NO_MORE);
                                }
                                mRecycleAdapter.addItems(flightInfoTemps);
                                flightInfoPageList.addAll(flightInfoTemps);
                                if(0 == mCurrentPage){
                                    initRightData();
                                }
                            }
                        },0 == mCurrentPage ? 0 : 1000 * 1);
                    }
                });
    }
}
