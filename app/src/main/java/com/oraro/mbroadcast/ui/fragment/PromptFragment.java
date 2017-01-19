package com.oraro.mbroadcast.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.listener.IAutoPlayStatusListener;
import com.oraro.mbroadcast.logicService.DataService;
import com.oraro.mbroadcast.model.InterCutData;
import com.oraro.mbroadcast.service.IMyAidlInterface;
import com.oraro.mbroadcast.ui.activity.MainActivity;
import com.oraro.mbroadcast.ui.adapter.PromptAdapter;
import com.oraro.mbroadcast.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;


public class PromptFragment extends Fragment implements View.OnClickListener {
    private ListView mListView;
    private PromptAdapter mPromptAdapter;
    private Button mButton;
    private TextView tv_edit_time;
    private LinearLayout mLinear;
    private TextView tv_edit_hour;
    private ImageView iv_edit_hour;
    private TextView tv_edit_min;
    private ImageView iv_edit_min;
    private final int hourFlag = 4;
    private final int minFlag = 5;
    private final int timFlag = 6;
    private int mFlag = 0;
    private List<String> replaceList;
    private List<String> hourList;
    private List<String> minList;
    private List<String> timeList;
    private Context ctx;
    private ListView listView;
    private PopupWindow popWindow;
    private MyPopMouldAdapter popAdapter;
    private  MainActivity mActivity;
    private LinearLayout header_left_ll;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_prompt, container, false);
        ctx = getActivity();
        initViews(view);
        List<InterCutData> list = DBManager.getInstance(getActivity()).queryAll(DBManager.getInstance(getActivity()).getInterCutDataDao(DBManager.READ_ONLY));
        mPromptAdapter = new PromptAdapter(getActivity(), list);
        mListView.setAdapter(mPromptAdapter);
        initData();
        initList();
        initListener();
        return view;
    }

    private void initListener() {
        ((MainActivity) getActivity()).setCallBack(new IAutoPlayStatusListener() {
            @Override
            public void changeAutoPlayStatus(boolean flag) {
                if (flag) {
                    mButton.setText("插入");
                    mButton.setVisibility(View.INVISIBLE);
                    mLinear.setVisibility(View.INVISIBLE);
                } else {
                    mButton.setText("立即播放");
                    mLinear.setVisibility(View.VISIBLE);
                    mButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    private void initViews(View view) {
        header_left_ll = (LinearLayout) view.findViewById(R.id.header_left_ll);
        header_left_ll.setOnClickListener(this);
        TextView right = (TextView) view.findViewById(R.id.header_right_btn);
        mListView = (ListView) view.findViewById(R.id.list_view);
        TextView left = (TextView) view.findViewById(R.id.header_left_btn);
        left.setVisibility(View.GONE);
        mLinear = (LinearLayout) view.findViewById(R.id.ll_linear);
        ((TextView) view.findViewById(R.id.header_text)).setText("温馨提示");
        view.findViewById(R.id.insert).setOnClickListener(this);
        mButton = (Button) view.findViewById(R.id.insert);
        mButton.setOnClickListener(this);
        tv_edit_time = (TextView) view.findViewById(R.id.tv_edit_times);
        tv_edit_time.setText(""+3);
        view.findViewById(R.id.iv_edit_times).setOnClickListener(this);
        tv_edit_hour = (TextView) view.findViewById(R.id.tv_edit_hour);
        tv_edit_hour.setOnClickListener(this);
        tv_edit_hour.setText("00" + getString(R.string.edit_activity_hour));
        iv_edit_hour = (ImageView) view.findViewById(R.id.iv_edit_hour);
        iv_edit_hour.setOnClickListener(this);
        tv_edit_min = (TextView) view.findViewById(R.id.tv_edit_min);
        tv_edit_min.setOnClickListener(this);
        tv_edit_min.setText("00" + getString(R.string.edit_activity_min));
        iv_edit_min = (ImageView) view.findViewById(R.id.iv_edit_min);
        iv_edit_min.setOnClickListener(this);
        right.setVisibility(View.VISIBLE);
        right.setOnClickListener(this);
        mListView.setOnItemLongClickListener(mOnItemLongClickListener);
        DataService dataService = new DataService();
        if (!dataService.getAutoPlayStatus()) {
            mButton.setText("立即播放");
            mButton.setVisibility(View.VISIBLE);
            mLinear.setVisibility(View.VISIBLE);
        } else {
            mButton.setText("插入");
            mButton.setVisibility(View.INVISIBLE);
            mLinear.setVisibility(View.INVISIBLE);
        }
    }

    private void initData() {

        popAdapter = new MyPopMouldAdapter();

        listView = new ListView(getActivity());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String msg;
                if (mFlag == hourFlag) {
                    msg = hourList.get(position % replaceList.size());
                    tv_edit_hour.setText(msg);
                } else if (mFlag == minFlag) {
                    msg = minList.get(position % replaceList.size());
                    tv_edit_min.setText(msg);
                } else if (mFlag == timFlag) {
                    msg = timeList.get(position % replaceList.size());
                    tv_edit_time.setText(msg);
                }
                // 关闭popWindow
                popWindow.dismiss();
            }
        });
        hourList = new ArrayList<>();
        minList = new ArrayList<>();
        timeList = new ArrayList<>();
        replaceList = new ArrayList<>();
    }

    private void setPopRecircle(int flag, List<String> list, int num) {
        mFlag = flag;
        replaceList = list;
        listView.setAdapter(popAdapter);
        switch (mFlag) {
            case hourFlag:
                listView.setSelection(num + 24 * 100);
                break;
            case minFlag:
                listView.setSelection(num + 60 * 100);
                break;

            case timFlag:
                listView.setSelection(num);
                break;
        }
    }

    private void showPop() {
        int popHeight = 300;
        if (popWindow == null) {
            popWindow = new PopupWindow(ctx);
            popWindow.setWidth(tv_edit_hour.getWidth()); // 与输入框等宽
            popWindow.setHeight(popHeight); // 高度300
            popWindow.setContentView(listView);
            popWindow.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.bg_edit));
            popWindow.setFocusable(true); // 设置为，可以获得焦点
        }
        if (popWindow.isShowing()) {
            popWindow.dismiss();
        } else {
            if (mFlag == hourFlag) {
                popWindow.showAsDropDown(tv_edit_hour);
            } else if (mFlag == minFlag) {
                popWindow.showAsDropDown(tv_edit_min);
            } else if (mFlag == timFlag) {
                popWindow.showAsDropDown(tv_edit_time);
            }
        }
    }

    private void initList() {
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

        if (timeList.isEmpty()) {
            for (int i = 1; i < 61; i++) {
                if (i < 10) {
                    timeList.add("0" + i);
                } else {
                    timeList.add(i+"");
                }
            }
        }
    }


    class MyPopMouldAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PopViewHolder holder;
            if (convertView == null) {
                holder = new PopViewHolder();
                convertView = View.inflate(ctx, R.layout.lv_items_mould_info, null);
                holder.tv = (TextView) convertView.findViewById(R.id.tv_mould_item);
                convertView.setTag(holder);
            } else {
                holder = (PopViewHolder) convertView.getTag();
            }
            holder.tv.setText(replaceList.get(position % replaceList.size()));
            return convertView;
        }
    }

    private class PopViewHolder {
        TextView tv;
    }
    @Override
    public void onAttach(Activity activity) {
        mActivity = (MainActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
            case R.id.header_left_ll:
                mActivity.showSlidingMenu();
                break;

            case R.id.iv_edit_times:
                String timeText = (String) tv_edit_time.getText();
                setPopRecircle(timFlag, timeList, Integer.parseInt(timeText));
                showPop();
                break;

            case R.id.insert:
                String hourString = (String) tv_edit_hour.getText();
                String minString = (String) tv_edit_min.getText();
                String hour = hourString.substring(0, hourString.indexOf(getString(R.string.edit_activity_hour)));
                int hours = Integer.parseInt(hour);
                String min = minString.substring(0, minString.indexOf(getString(R.string.edit_activity_min)));
                int minutes = Integer.parseInt(min);

                long spaceun = hours * 3600000 + minutes * 60000;

                Log.e("huanghui", " hours = " + hours + "   minutes = " + minutes);

                if (mButton.getText().equals("插入") ) {
                    if (mPromptAdapter.isCanAdd() && mPromptAdapter.getChecked().size() > 0) {
                        insertGreenDao(false, spaceun);
                        Toast.makeText(getActivity(), "插入成功", Toast.LENGTH_SHORT).show();
                    } else if (mPromptAdapter.getChecked().size() == 0) {
                        Toast.makeText(getActivity(), "未选择温馨提示,无法插入", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "含有未编辑的提示没有保存", Toast.LENGTH_SHORT).show();
                    }
                } else if (mButton.getText().equals("立即播放")) {
                    IMyAidlInterface iMyAidlInterface = MBroadcastApplication.getIMyAidlInterface();
                    if (null != iMyAidlInterface) {
                        try {
                            int times = -1;
//                            iMyAidlInterface.needrefresh(true);
                            if ("".equals(tv_edit_time.getText().toString().trim())) {
                                times = 0;
                            } else {
                                times = Integer.parseInt(tv_edit_time.getText().toString());
                            }
                            if (times > 0 && mPromptAdapter.getChecked().size() > 0) {
                                insertGreenDao(false, spaceun);
                                iMyAidlInterface.startInterCut(times, spaceun);
                                Toast.makeText(getActivity(), "开始播放", Toast.LENGTH_SHORT).show();
                            } else if (mPromptAdapter.getChecked().size() == 0) {
                                Toast.makeText(getActivity(), "未选择温馨提示,无法播报", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "请输入次数", Toast.LENGTH_SHORT).show();
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;

            case R.id.header_right_btn:
                if (mPromptAdapter.isCanAdd()) {
                    mPromptAdapter.notifyDataSetChanged();
                    insertNewInterCutData();
                } else {
                    Toast.makeText(getActivity(), "含有未编辑的提示没有保存", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private void insertNewInterCutData() {
        InterCutData interCutData = new InterCutData();
        interCutData.setText("");
        interCutData.setIsPlay(true);
        mPromptAdapter.getInterCutDataList().add(interCutData);
        mPromptAdapter.notifyDataSetChanged();
        mListView.setSelection(mPromptAdapter.getCount());
    }

    private void insertGreenDao(boolean auto, long spaceun) {
//        List<InterCutData> list = new ArrayList<>();
//        Map<Integer, InterCutData> map = mPromptAdapter.getChecked();
//        for (Map.Entry<Integer, InterCutData> entry : map.entrySet()) {
//            Log.e("dddd", "key = " + entry.getKey() + "value = " + entry.getValue().toString());
//            if (!entry.getValue().getText().equals("")) {
//                list.add( entry.getValue());
//            }
//        }

        if (auto) {
            DataService dataService = new DataService();
            dataService.setSpaceStatus(spaceun);
        }
        List<InterCutData> list;
        list = mPromptAdapter.getInterCutDataList();
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).getText().equals("")) {
                DBManager.getInstance(getActivity()).insertOrUpdate(list.get(i), DBManager.getInstance(getActivity()).getInterCutDataDao(DBManager.WRITE_ONLY));
            }
        }

        IMyAidlInterface iMyAidlInterface = MBroadcastApplication.getIMyAidlInterface();
        if (null != iMyAidlInterface) {
            try {
                iMyAidlInterface.needrefresh(true);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    private AdapterView.OnItemLongClickListener mOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if (mPromptAdapter.isCanAdd()) {
                mPromptAdapter.setEditPosition(position);
            } else {
                Toast.makeText(getActivity(), "含有未编辑的提示没有保存", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    };

}
