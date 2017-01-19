package com.oraro.mbroadcast.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.gesture.GestureUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.listener.OnImageClickListener;
import com.oraro.mbroadcast.ui.adapter.AddUrgentAdapter;
import com.oraro.mbroadcast.ui.adapter.UrgentPopAdapter;
import com.oraro.mbroadcast.utils.GuideUtil;
import com.oraro.mbroadcast.utils.SPUtils;
import com.oraro.mbroadcast.utils.UIUtils;
import com.oraro.mbroadcast.utils.UrgentBroadcastXmlUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AddUrgentBroadcastActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView header_left_btn;
    private String TAG = "zmy";
    private TextView header_text;
    private LinearLayout ll_mould_icon;
    private ImageView iv_add;
    private ListView lv_urgent;
    private List<UrgentBroadcastXmlUtils.UrgentFlightInfo> mouldList;
    private Context context;
    private ListView listView;
    private PopupWindow popWindow;
    private List<Integer> selectList;
    private UrgentBroadcastXmlUtils ubxu;
    private String[] urgentMouldArray1;
    private String[] urgentMouldArray2;
    private String[] urgentMouldArray3;
    private String[] urgentMouldArray4;
    private String[] ugentTitleArray;
    private AddUrgentAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_urgent_broadcast);
        if (SPUtils.getPrefInt(this, "isGuided3", 0) == 0) {
            GuideUtil.getInstance().initGuide(this, R.layout.layout_guide3, new OnImageClickListener() {
                @Override
                public void callback() {
                    SPUtils.setPrefInt(AddUrgentBroadcastActivity.this, "isGuided3", 1);
                }
            });
        }
        context = this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        header_left_btn = (TextView) findViewById(R.id.header_left_btn);
        header_text = (TextView) findViewById(R.id.header_text);
        ll_mould_icon = (LinearLayout) findViewById(R.id.ll_mould_icon);
        iv_add = (ImageView) findViewById(R.id.iv_add);
        lv_urgent = (ListView) findViewById(R.id.lv_urgent);
        listView = new ListView(context);

        header_left_btn.setOnClickListener(this);
        ll_mould_icon.setOnClickListener(this);
        iv_add.setOnClickListener(this);

    }

    private void initData() {
        ubxu = new UrgentBroadcastXmlUtils(context);

        urgentMouldArray1 = UIUtils.getStringArray(R.array.mouldArray1);
        urgentMouldArray2 = UIUtils.getStringArray(R.array.mouldArray2);
        urgentMouldArray3 = UIUtils.getStringArray(R.array.mouldArray3);
        urgentMouldArray4 = UIUtils.getStringArray(R.array.mouldArray4);
        ugentTitleArray = UIUtils.getStringArray(R.array.ttsMouldTitleArray);
        mouldList = new ArrayList<>();
        selectList = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            mouldList.add(ubxu.hashMap.get(1 + "-" + i));
        }
        adapter = new AddUrgentAdapter(context, mouldList);
        lv_urgent.setAdapter(adapter);

        UrgentPopAdapter popAdapter = new UrgentPopAdapter(context, ugentTitleArray);
        listView.setAdapter(popAdapter);
    }

    private void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                header_text.setText(ugentTitleArray[position]);
                String key = "" + (position + 1) + "-";
                mouldList.clear();
                Set<String> sets = ubxu.hashMap.keySet();
                for (String k : sets) {
                    if (k.startsWith(key)) {
                        mouldList.add(ubxu.hashMap.get(k));
                    }
                }
                adapter.setData(mouldList);
                // 关闭popWindow
                popWindow.dismiss();

                // TODO: 2016/8/24 根据pop点击的模板名称，切换到对应的listview
//                switchMouldAdapter(position);

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.header_left_btn:
                finish();
                break;
            case R.id.ll_mould_icon:
                if (popWindow == null) {
                    popWindow = new PopupWindow(context);
                    popWindow.setWidth(header_text.getWidth()); // 与输入框等宽
                    popWindow.setHeight(200); // 高度200
                    popWindow.setContentView(listView);
                    popWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_edit));
                    popWindow.setFocusable(true); // 设置为，可以获得焦点
                }
                popWindow.setWidth(header_text.getWidth()); // 与输入框等宽
                if (popWindow.isShowing()) {
                    popWindow.dismiss();
                } else {
                    popWindow.showAsDropDown(header_text);
                }
                break;
            case R.id.iv_add:
                Intent intent = new Intent();
                intent.putStringArrayListExtra("types", (ArrayList<String>) adapter.getData());
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        GuideUtil.unRegisterInstance();
    }
}
