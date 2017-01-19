package com.oraro.mbroadcast.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.oraro.mbroadcast.listener.IAutoPlayStatusListener;
import com.oraro.mbroadcast.listener.OnImageClickListener;
import com.oraro.mbroadcast.logicService.DataService;
import com.oraro.mbroadcast.model.InterCutData;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.model.TabInfo;
import com.oraro.mbroadcast.model.UrgentItemBean;
import com.oraro.mbroadcast.service.IMyAidlInterface;
import com.oraro.mbroadcast.ui.activity.AddUrgentBroadcastActivity;
import com.oraro.mbroadcast.ui.activity.MainActivity;
import com.oraro.mbroadcast.ui.adapter.BroadcastDatePopWindowAdapter;
import com.oraro.mbroadcast.ui.adapter.ReminderAdapter1;
import com.oraro.mbroadcast.ui.adapter.ReminderAdapter2;
import com.oraro.mbroadcast.ui.adapter.ReminderLeftLvAdapter;
import com.oraro.mbroadcast.utils.GuideUtil;
import com.oraro.mbroadcast.utils.LogUtils;
import com.oraro.mbroadcast.utils.SPUtils;
import com.oraro.mbroadcast.utils.UIUtils;
import com.oraro.mbroadcast.utils.UrgentBroadcastXmlUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.oraro.mbroadcast.R.string.edit_activity_count;

public class KindlyReminderFragment extends BaseParentFragment implements View.OnClickListener {

    private Context context;
    private String TAG = "zmy";
    private ImageView header_left_img;
    private LinearLayout header_left_ll;
    private TextView header_text;
    private ListView lv_reminder_left;
    private ImageView iv_reminder_add;
    private ListView lv_reminder_right;
    private TextView tv_edit_count;
    private TextView tv_edit_count2;
    private ImageView iv_edit_count;
    private ImageView iv_edit_count2;
    private TextView tv_edit_hour;
    private TextView tv_edit_hour2;
    private ImageView iv_edit_hour;
    private ImageView iv_edit_hour2;
    private TextView tv_edit_min;
    private TextView tv_edit_min2;
    private ImageView iv_edit_min;
    private ImageView iv_edit_min2;
    private TextView tv_edit_second;
    private TextView tv_edit_second2;
    private ImageView iv_edit_second;
    private ImageView iv_edit_second2;
    private Button btn_reminder_instant_broadcast;
    private String[] catalogArray;
    private ReminderLeftLvAdapter reminderLeftLvAdapter;
    private List<InterCutData> list;
    private ReminderAdapter1 reminderAdapter1;
    private ReminderAdapter2 reminderAdapter2;
    private List<String> countList1;
    private List<String> hourList1;
    private List<String> minList1;
    private List<String> secondList1;
    private List<String> countList2;
    private List<String> hourList2;
    private List<String> minList2;
    private List<String> secondList2;
    private List<String> replaceList;
    private ListView listView;
    private final int countFlag = 1;
    private final int hourFlag = 2;
    private final int minFlag = 3;
    private final int secondFlag = 4;
    private int mFlag = 0;
    private BroadcastDatePopWindowAdapter popAdapter;
    private PopupWindow popWindow;
    private List<String> selectTypeList;
    private boolean isReminder;
    private List<UrgentItemBean> selectedUrgentList;
    private LinearLayout ll_count_row;
    private LinearLayout ll_count_row2;
    private LinearLayout ll_time_row;
    private LinearLayout ll_time_row2;
    private UrgentBroadcastXmlUtils ubxu;
    private List<UrgentItemBean> adapter2BeanList;
    private List<String> lastSelectTypeList;
    private DBManager mDBManager;
    private boolean isAuto;
    private DataService dataService;
    private IMyAidlInterface iMyAidlInterface;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        context = getActivity();
        mDBManager = DBManager.getInstance(getActivity());
//        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (SPUtils.getPrefInt(getActivity(), "isGuided1", 0) == 0) {
            GuideUtil.getInstance().initGuide(getActivity(), R.layout.layout_guide1, new OnImageClickListener() {
                @Override
                public void callback() {
                    SPUtils.setPrefInt(getActivity(), "isGuided1", 1);
                }
            });
        }
        View view = inflater.inflate(R.layout.fragment_kindly_reminder, container, false);
        initView(view);
        initData();
        initList();
        initListener();

        iMyAidlInterface = MBroadcastApplication.getIMyAidlInterface();
        if(iMyAidlInterface != null){
            try {
                iMyAidlInterface.initSpeekingServiceForInterCut();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SimpleEvent event) {
        if(event.getMsg()==Constants.SERVICE1_CONNECT_SUCESSFUL){
            try {
                iMyAidlInterface = MBroadcastApplication.getIMyAidlInterface();
                iMyAidlInterface.initSpeekingServiceForInterCut();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected int getMainViewResId() {
//        return R.layout.fragment_kindly_reminder;
        return 0;
    }

    @Override
    protected int supplyTabs(List<TabInfo> tabs) {
        return 0;
    }

    @Override
    protected int setTitle() {
        return 0;
    }

    @Override

    public void startInfoActivity(View v) {

    }

    @Override
    protected int broadcastORflight() {
        return 0;
    }

    private void initView(View view) {
        header_left_img = (ImageView) view.findViewById(R.id.header_left_img);
        header_left_ll = (LinearLayout) view.findViewById(R.id.header_left_ll);
        header_text = (TextView) view.findViewById(R.id.header_text);
        header_text.setText(R.string.custom_broadcast);
        ll_count_row = (LinearLayout) view.findViewById(R.id.ll_count_row);
        ll_count_row2 = (LinearLayout) view.findViewById(R.id.ll_count_row2);
        ll_time_row = (LinearLayout) view.findViewById(R.id.ll_time_row);
        ll_time_row2 = (LinearLayout) view.findViewById(R.id.ll_time_row2);
        lv_reminder_left = (ListView) view.findViewById(R.id.lv_reminder_left);
        iv_reminder_add = (ImageView) view.findViewById(R.id.iv_reminder_add);
        lv_reminder_right = (ListView) view.findViewById(R.id.lv_reminder_right);
        tv_edit_count = (TextView) view.findViewById(R.id.tv_edit_count);
        tv_edit_count2 = (TextView) view.findViewById(R.id.tv_edit_count2);
        iv_edit_count = (ImageView) view.findViewById(R.id.iv_edit_count);
        iv_edit_count2 = (ImageView) view.findViewById(R.id.iv_edit_count2);
        tv_edit_hour = (TextView) view.findViewById(R.id.tv_edit_hour);
        tv_edit_hour2 = (TextView) view.findViewById(R.id.tv_edit_hour2);
        iv_edit_hour = (ImageView) view.findViewById(R.id.iv_edit_hour);
        iv_edit_hour2 = (ImageView) view.findViewById(R.id.iv_edit_hour2);
        tv_edit_min = (TextView) view.findViewById(R.id.tv_edit_min);
        tv_edit_min2 = (TextView) view.findViewById(R.id.tv_edit_min2);
        iv_edit_min = (ImageView) view.findViewById(R.id.iv_edit_min);
        iv_edit_min2 = (ImageView) view.findViewById(R.id.iv_edit_min2);
        tv_edit_second = (TextView) view.findViewById(R.id.tv_edit_second);
        tv_edit_second2 = (TextView) view.findViewById(R.id.tv_edit_second2);
        iv_edit_second = (ImageView) view.findViewById(R.id.iv_edit_second);
        iv_edit_second2 = (ImageView) view.findViewById(R.id.iv_edit_second2);
        btn_reminder_instant_broadcast = (Button) view.findViewById(R.id.btn_reminder_instant_broadcast);
        listView = new ListView(context);
        dataService = new DataService();
        if (!dataService.getAutoPlayStatus()) {
//            mButton.setText("立即播放");
            isShowReminderRow(true);
            btn_reminder_instant_broadcast.setVisibility(View.VISIBLE);
        } else {
//            mButton.setText("插入");
            isShowReminderRow(false);
            btn_reminder_instant_broadcast.setVisibility(View.GONE);
        }
        iv_reminder_add.setOnClickListener(this);
        iv_edit_count.setOnClickListener(this);
        iv_edit_hour.setOnClickListener(this);
        iv_edit_min.setOnClickListener(this);
        iv_edit_second.setOnClickListener(this);
        iv_edit_count2.setOnClickListener(this);
        iv_edit_hour2.setOnClickListener(this);
        iv_edit_min2.setOnClickListener(this);
        iv_edit_second2.setOnClickListener(this);
        header_left_ll.setOnClickListener(this);
        btn_reminder_instant_broadcast.setOnClickListener(this);

        isShowUrgentRow(false);
    }

    private void initData() {
        catalogArray = UIUtils.getStringArray(R.array.typeTitle);
        isReminder = true;
        adapter2BeanList = new ArrayList<>();
        lastSelectTypeList = new ArrayList<>();
        countList1 = new ArrayList<>();
        hourList1 = new ArrayList<>();
        minList1 = new ArrayList<>();
        secondList1 = new ArrayList<>();
        countList2 = new ArrayList<>();
        hourList2 = new ArrayList<>();
        minList2 = new ArrayList<>();
        secondList2 = new ArrayList<>();
        replaceList = new ArrayList<>();
        selectedUrgentList = new ArrayList<>();
        ubxu = new UrgentBroadcastXmlUtils(context);
        list = DBManager.getInstance(getActivity()).queryInterCutDataByTY(0);

        //从数据库查询紧急播报数据到UrgentItemBean
        adapter2BeanList = mDBManager.queryAll(mDBManager.getUrgentItemBeanDao(DBManager.READ_ONLY));
        if (adapter2BeanList.isEmpty()) {
            UrgentItemBean bean1 = new UrgentItemBean();
            UrgentItemBean bean2 = new UrgentItemBean();
            bean1.setType("1-3");
            bean2.setType("1-4");
            mDBManager.insert(bean1, mDBManager.getUrgentItemBeanDao(DBManager.WRITE_ONLY));
            mDBManager.insert(bean2, mDBManager.getUrgentItemBeanDao(DBManager.WRITE_ONLY));
            adapter2BeanList.add(bean1);
            adapter2BeanList.add(bean2);
        }
        for (UrgentItemBean bean : adapter2BeanList) {
            lastSelectTypeList.add(bean.getType());
        }

        reminderLeftLvAdapter = new ReminderLeftLvAdapter(context, catalogArray);
        lv_reminder_left.setAdapter(reminderLeftLvAdapter);
        // “温馨提示” 对应右边的Adapter
        reminderAdapter1 = new ReminderAdapter1(context, list);
        // “紧急广播” 对应右边的Adapter

        reminderAdapter2 = new ReminderAdapter2(context, adapter2BeanList, this);
        lv_reminder_right.setAdapter(reminderAdapter1);

    }

    private void initListener() {
        lv_reminder_right.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isReminder) {
                    ReminderAdapter1.ViewHolder holder = (ReminderAdapter1.ViewHolder) view.getTag();
                    holder.checkBox.toggle();
                }
            }
        });
        lv_reminder_right.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (isReminder) {
                    if (reminderAdapter1.isCanAdd()) {
                        reminderAdapter1.setEditPosition(position);
                    } else {
                        Toast.makeText(getActivity(), "含有未编辑的提示没有保存", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });
        lv_reminder_left.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                reminderLeftLvAdapter.changeSelected(position);
                if (position == 0) {
                    isReminder = true;
                    lv_reminder_right.setAdapter(reminderAdapter1);
                    if (isAuto || dataService.getAutoPlayStatus()) {
                        return;
                    }
                    isShowReminderRow(true);
                    isShowUrgentRow(false);
                } else {
                    if (SPUtils.getPrefInt(getActivity(), "isGuided2", 0) == 0) {
                        GuideUtil.getInstance().initGuide(getActivity(), R.layout.layout_guide2, new OnImageClickListener() {
                            @Override
                            public void callback() {
                                SPUtils.setPrefInt(getActivity(), "isGuided2", 1);
                            }
                        });
                    }
                    isReminder = false;
                    lv_reminder_right.setAdapter(reminderAdapter2);
                    if (isAuto || dataService.getAutoPlayStatus()) {
                        return;
                    }
                    isShowReminderRow(false);
                    isShowUrgentRow(true);
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String msg;
                if (mFlag == countFlag) {
                    if (isReminder) {
                        msg = countList1.get(position % replaceList.size());
                        tv_edit_count.setText(msg);
                    } else {
                        msg = countList2.get(position % replaceList.size());
                        tv_edit_count2.setText(msg);
                    }
                } else if (mFlag == hourFlag) {
                    if (isReminder) {
                        msg = hourList1.get(position % replaceList.size());
                        tv_edit_hour.setText(msg);
                    } else {
                        msg = hourList2.get(position % replaceList.size());
                        tv_edit_hour2.setText(msg);
                    }
                } else if (mFlag == minFlag) {
                    if (isReminder) {
                        msg = minList1.get(position % replaceList.size());
                        tv_edit_min.setText(msg);
                    } else {
                        msg = minList2.get(position % replaceList.size());
                        tv_edit_min2.setText(msg);
                    }
                } else if (mFlag == secondFlag) {
                    if (isReminder) {
                        msg = secondList1.get(position % replaceList.size());
                        tv_edit_second.setText(msg);
                    } else {
                        msg = secondList2.get(position % replaceList.size());
                        tv_edit_second2.setText(msg);
                    }
                }
                // 关闭popWindow
                popWindow.dismiss();
            }
        });
        ((MainActivity) getActivity()).setCallBack(new IAutoPlayStatusListener() {
            @Override
            public void changeAutoPlayStatus(boolean flag) {
                isAuto = flag;
                isShowBtn(flag);
            }
        });
    }

    private void isShowBtn(boolean flag) {
        if (flag) {
            // TODO: 2016/12/13
            isShowReminderRow(false);
            isShowUrgentRow(false);
            btn_reminder_instant_broadcast.setVisibility(View.GONE);
        } else {
            if (isReminder) {
                isShowReminderRow(true);
                isShowUrgentRow(false);
            } else {
                isShowReminderRow(false);
                isShowUrgentRow(true);
            }
            btn_reminder_instant_broadcast.setVisibility(View.VISIBLE);
        }
    }

    private void isShowReminderRow(boolean isShow) {
        if (isShow) {
            ll_count_row.setVisibility(View.VISIBLE);
            ll_time_row.setVisibility(View.VISIBLE);
        } else {
            ll_count_row.setVisibility(View.GONE);
            ll_time_row.setVisibility(View.GONE);
        }
    }

    private void isShowUrgentRow(boolean isShow) {
        if (isShow) {
            ll_count_row2.setVisibility(View.VISIBLE);
            ll_time_row2.setVisibility(View.VISIBLE);
        } else {
            ll_count_row2.setVisibility(View.GONE);
            ll_time_row2.setVisibility(View.GONE);
        }
    }

    private void initList() {
        if (countList1.isEmpty()) {
            for (int i = 1; i <= 10; i++) {
                countList1.add(i + getString(edit_activity_count));
            }
        }
        if (hourList1.isEmpty()) {
            for (int i = 0; i < 24; i++) {
                if (i < 10) {
                    hourList1.add("0" + i + getString(R.string.edit_activity_hour));
                } else {
                    hourList1.add(i + getString(R.string.edit_activity_hour));
                }
            }
        }
        if (minList1.isEmpty()) {
            for (int i = 0; i < 60; i++) {
                if (i < 10) {
                    minList1.add("0" + i + getString(R.string.edit_activity_min));
                } else {
                    minList1.add(i + getString(R.string.edit_activity_min));
                }
            }
        }
        if (secondList1.isEmpty()) {
            for (int i = 0; i < 60; i++) {
                if (i < 10) {
                    secondList1.add("0" + i + getString(R.string.edit_activity_second));
                } else {
                    secondList1.add(i + getString(R.string.edit_activity_second));
                }
            }
        }

        if (countList2.isEmpty()) {
            for (int i = 1; i <= 10; i++) {
                countList2.add(i + getString(edit_activity_count));
            }
        }
        if (hourList2.isEmpty()) {
            for (int i = 0; i < 24; i++) {
                if (i < 10) {
                    hourList2.add("0" + i + getString(R.string.edit_activity_hour));
                } else {
                    hourList2.add(i + getString(R.string.edit_activity_hour));
                }
            }
        }
        if (minList2.isEmpty()) {
            for (int i = 0; i < 60; i++) {
                if (i < 10) {
                    minList2.add("0" + i + getString(R.string.edit_activity_min));
                } else {
                    minList2.add(i + getString(R.string.edit_activity_min));
                }
            }
        }
        if (secondList2.isEmpty()) {
            for (int i = 0; i < 60; i++) {
                if (i < 10) {
                    secondList2.add("0" + i + getString(R.string.edit_activity_second));
                } else {
                    secondList2.add(i + getString(R.string.edit_activity_second));
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
//            case R.id.header_left_ll:
//                ((MainActivity)getActivity()).showSlidingMenu();
//                break;
            case R.id.iv_reminder_add:
                if (isReminder) {
                    if (reminderAdapter1.isCanAdd()) {
                        reminderAdapter1.notifyDataSetChanged();
                        insertNewInterCutData();
                    } else {
                        Toast.makeText(getActivity(), "含有未编辑的提示没有保存", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Intent intent = new Intent(context, AddUrgentBroadcastActivity.class);
                    customStartActivityForResult(intent, 11);
                }
                break;
            // 温馨提示时显示的button
            case R.id.iv_edit_count:
                String countText = (String) tv_edit_count.getText();
                countText = countText.substring(0, countText.length() - 1);
                setPopRecircle(countFlag, countList1, Integer.parseInt(countText));
                showPop();
                break;
            case R.id.iv_edit_hour:
                String hourText = (String) tv_edit_hour.getText();
                hourText = hourText.substring(0, hourText.length() - 1);
                setPopRecircle(hourFlag, hourList1, Integer.parseInt(hourText));
                showPop();
                break;
            case R.id.iv_edit_min:
                String minText = (String) tv_edit_min.getText();
                minText = minText.substring(0, minText.length() - 1);
                setPopRecircle(minFlag, minList1, Integer.parseInt(minText));
                showPop();
                break;
            case R.id.iv_edit_second:
                String secondText = (String) tv_edit_second.getText();
                secondText = secondText.substring(0, secondText.length() - 1);
                setPopRecircle(secondFlag, secondList1, Integer.parseInt(secondText));
                showPop();
                break;

            // 快速紧急广播时显示的button
            case R.id.iv_edit_count2:
                String countText2 = (String) tv_edit_count2.getText();
                countText2 = countText2.substring(0, countText2.length() - 1);
                setPopRecircle2(countFlag, countList2, Integer.parseInt(countText2));
                showPop2();
                break;
            case R.id.iv_edit_hour2:
                String hourText2 = (String) tv_edit_hour2.getText();
                hourText2 = hourText2.substring(0, hourText2.length() - 1);
                setPopRecircle2(hourFlag, hourList1, Integer.parseInt(hourText2));
                showPop2();
                break;
            case R.id.iv_edit_min2:
                String minText2 = (String) tv_edit_min2.getText();
                minText2 = minText2.substring(0, minText2.length() - 1);
                setPopRecircle2(minFlag, minList2, Integer.parseInt(minText2));
                showPop2();
                break;
            case R.id.iv_edit_second2:
                String secondText2 = (String) tv_edit_second2.getText();
                secondText2 = secondText2.substring(0, secondText2.length() - 1);
                setPopRecircle2(secondFlag, secondList1, Integer.parseInt(secondText2));
                showPop2();
                break;
            case R.id.btn_reminder_instant_broadcast:
                // TODO: 2016/12/13
                adapter2BeanList = mDBManager.queryAll(mDBManager.getUrgentItemBeanDao(DBManager.READ_ONLY));
                reminderAdapter2.setData(adapter2BeanList);
                int j = 0;
                for (int i = 0; i < adapter2BeanList.size(); i++) {
                    if (adapter2BeanList.get(i).getIsSelected()) {
                        j++;
                    }
                }
                long interval = getFinalInterval(j);
                int times = getFinalTimes(j);

                IMyAidlInterface iMyAidlInterface = MBroadcastApplication.getIMyAidlInterface();
                if (null != iMyAidlInterface) {
                    try {
                        insertGreenDao();
//                        LogUtils.e(TAG,"times========"+times+"   interval=========="+interval);
                        iMyAidlInterface.startInterCut(times, interval);
                        if (reminderAdapter1.getChecked().size() == 0 && j == 0) {
                            Toast.makeText(getActivity(), R.string.please_select_item, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), R.string.start_play, Toast.LENGTH_SHORT).show();
                        }

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    /**
     * 获取最终需要播放的次数
     *
     * @param j
     * @return
     */
    private int getFinalTimes(int j) {
        String time = tv_edit_count.getText().toString();
        String time2 = tv_edit_count2.getText().toString();
        int times = Integer.parseInt(time.substring(0, time.indexOf(getString(edit_activity_count))));
        int times2 = Integer.parseInt(time2.substring(0, time2.indexOf(getString(edit_activity_count))));
        if (reminderAdapter1.getChecked().size() != 0 && j != 0) {// 温馨提示和快速紧急广播都选择了播放的item
            times = times > times2 ? times : times2;
        } else if (reminderAdapter1.getChecked().size() == 0 && j != 0) {// 温馨提示没有选择需要播放的item，快速紧急广播选择了播放的item
            times = times2;
        } /*else if (reminderAdapter1.getChecked().size() != 0 && j == 0){// 温馨提示选择了需要播放的item，快速紧急广播没有选择播放的item

        } */
        return times;
    }

    /**
     * 获取最终需要播放的间隔时间
     *
     * @param j
     * @return
     */
    private long getFinalInterval(int j) {
        long interval = getSpaceun();
        long interval2 = getSpaceun2();
        // TODO: 2016/12/13
        if (reminderAdapter1.getChecked().size() != 0 && j != 0) {// 温馨提示和快速紧急广播都选择了播放的item
            interval = interval < interval2 ? interval : interval2;
        } else if (reminderAdapter1.getChecked().size() == 0 && j != 0) {// 温馨提示没有选择需要播放的item，快速紧急广播选择了播放的item
            interval = interval2;
        } /*else if (reminderAdapter1.getChecked().size() != 0 && j == 0){// 温馨提示选择了需要播放的item，快速紧急广播没有选择播放的item

        } */
        return interval;
    }


    private void insertGreenDao() {
        // 温馨提示的播放逻辑
        List<InterCutData> list = reminderAdapter1.getInterCutDataList();
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).getText().equals("")) {
                mDBManager.insertOrUpdate(list.get(i), mDBManager.getInterCutDataDao(DBManager.WRITE_ONLY));
            }
        }

        // 快速紧急广播的播放逻辑
        for (int j = 0; j < adapter2BeanList.size(); j++) {
            if (adapter2BeanList.get(j).getIsSelected() && !TextUtils.isEmpty(adapter2BeanList.get(j).getContent())) {
                //只有选中的紧急播报才会在温馨提示播报中被选择，所以需要设置interCutData.setIsPlay(true)

                InterCutData interCutData = (InterCutData) mDBManager.queryById(adapter2BeanList.get(j).getInterCutDataId(), mDBManager.getInterCutDataDao(DBManager.READ_ONLY));
//                LogUtils.e(TAG, "interCutData========" + interCutData);

                if (null == interCutData) {
                    interCutData = new InterCutData();
                    interCutData.setTy(Constants.TYPE_URGENT_DATA);
                    interCutData.setIsPlay(true);
                    interCutData.setText(adapter2BeanList.get(j).getContent());
                    interCutData.setTime(adapter2BeanList.get(j).getContent().length() * 300);
                    long interCutDataId = mDBManager.insert(interCutData, mDBManager.getInterCutDataDao(DBManager.WRITE_ONLY));
                    adapter2BeanList.get(j).setInterCutDataId(interCutDataId);
                    mDBManager.update(adapter2BeanList.get(j), mDBManager.getUrgentItemBeanDao(DBManager.WRITE_ONLY));
                } else {
                    interCutData.setIsPlay(true);
                    interCutData.setText(adapter2BeanList.get(j).getContent());
                    interCutData.setTime(adapter2BeanList.get(j).getContent().length() * 300);
                    //删除原有的温馨提示表中的数据，再按照当前的选中顺序插入，使紧急播报能够按照顺序播放
                    mDBManager.deleteById(interCutData.getId(), mDBManager.getInterCutDataDao(DBManager.WRITE_ONLY));

                    interCutData = new InterCutData();
                    interCutData.setTy(Constants.TYPE_URGENT_DATA);
                    interCutData.setIsPlay(true);
                    interCutData.setText(adapter2BeanList.get(j).getContent());
                    interCutData.setTime(adapter2BeanList.get(j).getContent().length() * 300);
                    long interCutDataId = mDBManager.insert(interCutData, mDBManager.getInterCutDataDao(DBManager.WRITE_ONLY));
                    adapter2BeanList.get(j).setInterCutDataId(interCutDataId);
                    mDBManager.update(adapter2BeanList.get(j), mDBManager.getUrgentItemBeanDao(DBManager.WRITE_ONLY));
                }
//                LogUtils.e(TAG, "interCutData2========" + interCutData);
            } else if (!adapter2BeanList.get(j).getIsSelected()) {// 没有被选中，则在温馨提示的播放逻辑中删除
                InterCutData interCutData = (InterCutData) mDBManager.queryById(adapter2BeanList.get(j).getInterCutDataId(), mDBManager.getInterCutDataDao(DBManager.READ_ONLY));
                if (interCutData != null) {
//                    LogUtils.e(TAG, "未被选中，需要被删除的interCutData========" + interCutData);
                    mDBManager.delete(interCutData, mDBManager.getInterCutDataDao(DBManager.WRITE_ONLY));
                }
            }
        }
//        DataService dataService = new DataService();
//        List<InterCutData> interCutDate = dataService.getInterCutData(Constants.TYPE_URGENT_DATA);
//        Log.e(TAG, "KindlyReminderFragment refresh::interCutDate = " + interCutDate);
        IMyAidlInterface iMyAidlInterface = MBroadcastApplication.getIMyAidlInterface();
        if (null != iMyAidlInterface) {
            try {
                iMyAidlInterface.needrefresh(true);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获得温馨提示状态下的间隔时间
     *
     * @return
     */
    private long getSpaceun() {
        String hourString = (String) tv_edit_hour.getText();
        String minString = (String) tv_edit_min.getText();
        String secondString = (String) tv_edit_second.getText();
        String hour = hourString.substring(0, hourString.indexOf(getString(R.string.edit_activity_hour)));
        int hours = Integer.parseInt(hour);
        String min = minString.substring(0, minString.indexOf(getString(R.string.edit_activity_min)));
        int minutes = Integer.parseInt(min);
        String second = secondString.substring(0, secondString.indexOf(getString(R.string.edit_activity_second)));
        int seconds = Integer.parseInt(second);
        return (long) (hours * 3600000 + minutes * 60000 + seconds * 1000);
    }

    /**
     * 获得快速紧急播放状态下的间隔时间
     *
     * @return
     */
    private long getSpaceun2() {
        String hourString = (String) tv_edit_hour2.getText();
        String minString = (String) tv_edit_min2.getText();
        String secondString = (String) tv_edit_second2.getText();
        String hour = hourString.substring(0, hourString.indexOf(getString(R.string.edit_activity_hour)));
        int hours = Integer.parseInt(hour);
        String min = minString.substring(0, minString.indexOf(getString(R.string.edit_activity_min)));
        int minutes = Integer.parseInt(min);
        String second = secondString.substring(0, secondString.indexOf(getString(R.string.edit_activity_second)));
        int seconds = Integer.parseInt(second);
        return (long) (hours * 3600000 + minutes * 60000 + seconds * 1000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 获取开启AddUrgentBroadcastActivity返回的数据
         */
        if (requestCode == 11 && resultCode == getActivity().RESULT_OK) {
            selectTypeList = data.getStringArrayListExtra("types");
            adapter2BeanList = mDBManager.queryAll(mDBManager.getUrgentItemBeanDao(DBManager.READ_ONLY));
            lastSelectTypeList.clear();
            for (UrgentItemBean bean : adapter2BeanList) {
                lastSelectTypeList.add(bean.getType());
            }
            if (null != lastSelectTypeList && !lastSelectTypeList.isEmpty()) {
                for (int i = 0; i < lastSelectTypeList.size(); i++) {
                    if (selectTypeList.contains(lastSelectTypeList.get(i))) {
                        selectTypeList.remove(lastSelectTypeList.get(i));
                    }
                }
            }

            for (int i = 0; i < selectTypeList.size(); i++) {
                UrgentItemBean bean = new UrgentItemBean();
                bean.setType(selectTypeList.get(i));
                mDBManager.insert(bean, mDBManager.getUrgentItemBeanDao(DBManager.WRITE_ONLY));
//                Log.e(TAG, "KindlyReminderFragment onActivityResult::requestCode 11 bean = " + bean.getId());
                adapter2BeanList.add(bean);
                lastSelectTypeList.add(selectTypeList.get(i));

            }
            reminderAdapter2.setData(adapter2BeanList);

        }
        /**
         * 获取开启CustomBroadcastActivity返回的数据
         */
        if (requestCode == 12 && resultCode == getActivity().RESULT_OK) {
            // TODO: 2016/11/25
            String content = data.getStringExtra("content");
            String param = data.getStringExtra("params");
            adapter2BeanList.get(reminderAdapter2.clickPosition).setContent(content);
            adapter2BeanList.get(reminderAdapter2.clickPosition).setParams(param);
            mDBManager.update(adapter2BeanList.get(reminderAdapter2.clickPosition), mDBManager.getUrgentItemBeanDao(DBManager.WRITE_ONLY));
            reminderAdapter2.setData(adapter2BeanList);
//            Log.e(TAG, "KindlyReminderFragment onActivityResult::requestCode 12 content = " + adapter2BeanList.get(reminderAdapter2.clickPosition).getContent());

        }
    }

    private void showPop() {
        int popHeight = 300;
        if (popWindow == null) {
            popWindow = new PopupWindow(context);
            popWindow.setWidth(tv_edit_count.getWidth()); // 与输入框等宽
            popWindow.setHeight(popHeight); // 高度300
            popWindow.setContentView(listView);
            popWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_edit));
            popWindow.setFocusable(true); // 设置为，可以获得焦点
        }
        if (popWindow.isShowing()) {
            popWindow.dismiss();
        } else {
            if (mFlag == countFlag) {
                popWindow.showAsDropDown(tv_edit_count);
            } else if (mFlag == hourFlag) {
                popWindow.showAsDropDown(tv_edit_hour);
            } else if (mFlag == minFlag) {
                popWindow.showAsDropDown(tv_edit_min);
            } else if (mFlag == secondFlag) {
                popWindow.showAsDropDown(tv_edit_second);
            }
        }
    }

    private void showPop2() {
        int popHeight = 300;
        if (popWindow == null) {
            popWindow = new PopupWindow(context);
            popWindow.setWidth(tv_edit_count2.getWidth()); // 与输入框等宽
            popWindow.setHeight(popHeight); // 高度300
            popWindow.setContentView(listView);
            popWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_edit));
            popWindow.setFocusable(true); // 设置为，可以获得焦点
        }
        if (popWindow.isShowing()) {
            popWindow.dismiss();
        } else {
            if (mFlag == countFlag) {
                popWindow.showAsDropDown(tv_edit_count2);
            } else if (mFlag == hourFlag) {
                popWindow.showAsDropDown(tv_edit_hour2);
            } else if (mFlag == minFlag) {
                popWindow.showAsDropDown(tv_edit_min2);
            } else if (mFlag == secondFlag) {
                popWindow.showAsDropDown(tv_edit_second2);
            }
        }
    }

    private void setPopRecircle(int flag, List<String> list, int num) {
        mFlag = flag;
        replaceList = list;
        popAdapter = new BroadcastDatePopWindowAdapter(context, replaceList);
        listView.setAdapter(popAdapter);
        switch (mFlag) {
            case countFlag:
                listView.setSelection(num + 9 + 10 * 100);
                break;
            case hourFlag:
                listView.setSelection(num + 24 * 100);
                break;
            case minFlag:
                listView.setSelection(num + 60 * 100);
                break;
            case secondFlag:
                listView.setSelection(num + 60 * 100);
                break;
        }
    }

    private void setPopRecircle2(int flag, List<String> list, int num) {
        mFlag = flag;
        replaceList = list;
        popAdapter = new BroadcastDatePopWindowAdapter(context, replaceList);
        listView.setAdapter(popAdapter);
        switch (mFlag) {
            case countFlag:
                listView.setSelection(num + 9 + 10 * 100);
                break;
            case hourFlag:
                listView.setSelection(num + 24 * 100);
                break;
            case minFlag:
                listView.setSelection(num + 60 * 100);
                break;
            case secondFlag:
                listView.setSelection(num + 60 * 100);
                break;
        }
    }

    private void insertNewInterCutData() {
        InterCutData interCutData = new InterCutData();
        interCutData.setText("");
        interCutData.setTy(0);
        interCutData.setIsPlay(true);
        reminderAdapter1.getInterCutDataList().add(interCutData);
        reminderAdapter1.notifyDataSetChanged();
        lv_reminder_right.setSelection(reminderAdapter1.getCount());
    }

    public void customStartActivityForResult(Intent intent, int requestCode) {
        Fragment temp = this;
        while (temp != null && temp.getParentFragment() != null) {
            temp = temp.getParentFragment();
        }
        temp.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().removeAllStickyEvents();
    }
}





