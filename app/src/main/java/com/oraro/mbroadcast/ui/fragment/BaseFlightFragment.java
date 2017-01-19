package com.oraro.mbroadcast.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.dao.PlayEntryDao;
import com.oraro.mbroadcast.logicService.DataService;
import com.oraro.mbroadcast.mina.client.MinaStringClientThread;
import com.oraro.mbroadcast.model.FlightInfoTemp;
import com.oraro.mbroadcast.model.PlayEntry;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.ui.activity.FlightInfoActivity;
import com.oraro.mbroadcast.ui.adapter.WeekFlightAdapter;
import com.oraro.mbroadcast.ui.widget.RefreshLayout;
import com.oraro.mbroadcast.utils.CustomFragmentManager;
import com.oraro.mbroadcast.utils.DataUtils;
import com.oraro.mbroadcast.utils.PopupList;
import com.oraro.mbroadcast.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public abstract class BaseFlightFragment extends Fragment implements PopupList.OnPopupListClickListener {
    private final static String TAG = BaseFlightFragment.class.getSimpleName();
    protected View mMainView;
    protected Context mContext;
    private List<String> popupMenuItemList = new ArrayList<>();
    WeekFlightAdapter weekFlightAdapter;
    public Date beginTime, endTime;
    List<FlightInfoTemp> flightInfoTemps = new ArrayList<>();
    public RefreshLayout myRefreshListView;
    public int index;
    public ListView listView;
    protected int mViewPostion;
    protected long flightTempId;

    public BaseFlightFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity.getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.flight_listview, container, false);
        listView = (ListView) mMainView.findViewById(R.id.list);
        myRefreshListView = (RefreshLayout) mMainView.findViewById(R.id.swipe_layout);
        flightInfoTemps = getData(getDayIndex(), 0);
        weekFlightAdapter = new WeekFlightAdapter(mContext, flightInfoTemps);
        listView.setAdapter(weekFlightAdapter);
        popupMenuItemList.add(mContext.getString(R.string.popuplist_edit));
        popupMenuItemList.add(mContext.getString(R.string.popuplist_delete));
        PopupList popupList = new PopupList();
        popupList.init(mContext, listView, popupMenuItemList, this);
        ImageView indicator = new ImageView(mContext);
        indicator.setImageResource(R.mipmap.popuplist_default_arrow);
        popupList.setIndicatorView(indicator);
        popupList.setIndicatorSize(dp2px(16), dp2px(8));
        popupList.setTextSizePixel(30);
        myRefreshListView.setRefreshing(true);
        // 加载监听器
        myRefreshListView.setOnLoadListener(new RefreshLayout.OnLoadListener() {

            @Override
            public void onLoad() {

                myRefreshListView.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        index++;
                        List list = getData(getDayIndex(), index);
                        if (list.isEmpty() || list.size() == 0) {
                            Toast.makeText(mContext, "没有更多数据", Toast.LENGTH_LONG).show();
                            myRefreshListView.setLoading(false);
                            return;
                        }
                        DataUtils.addListData(flightInfoTemps, list);
                        weekFlightAdapter.setFlightInfoTempData(flightInfoTemps);
                        // 加载完后调用该方法
                        myRefreshListView.setLoading(false);
                        weekFlightAdapter.notifyDataSetChanged();
                    }
                }, 1500);

            }
        });
        return mMainView;
    }

    private int dp2px(float value) {
        final float scale = this.getResources().getDisplayMetrics().densityDpi;
        return (int) (value * (scale / 160) + 0.5f);
    }

    @Override
    public void onPopupListClick(View contextView, int contextPosition, int position) {
        if (position == 0) {
//            Intent intent = new Intent(getActivity(), AddAndEditActivity.class);
//            intent.putExtra("info", flightInfoTemps.get(contextPosition).getId());
//            getActivity().startActivity(intent);
            SimpleEvent simpleEvent = new SimpleEvent(Constants.CALL_TO_START);
            simpleEvent.setmMsgId(flightInfoTemps.get(contextPosition).getId());
            EventBus.getDefault().post(simpleEvent);
            mViewPostion = contextPosition;
            flightTempId = flightInfoTemps.get(contextPosition).getId();
            contextView.setBackgroundColor(mContext.getResources().getColor(R.color.common_dark_edit));

        }
        if (position == 1) {
            DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
            PlayEntryDao dao = manager.getPlayEntryDao(DBManager.READ_ONLY);
            List<PlayEntry> list = manager.queryBySQL(dao, " where  PLAY_ENTRY_ID = " + flightInfoTemps.get(contextPosition).getId());
            PlayEntryDao dao2 = manager.getPlayEntryDao(DBManager.WRITE_ONLY);
            manager.deleteList(list, dao2);
            DBManager.getInstance(getActivity()).delete(flightInfoTemps.get(contextPosition), DBManager.getInstance(getActivity()).getFlightInfoTempDao(DBManager.WRITE_ONLY));
            weekFlightAdapter.collapseDeleteView(listView, contextPosition);
            EventBus.getDefault().postSticky(new SimpleEvent(Constants.Analytic_Cmpletion_Notice));
        }
    }

    public List<FlightInfoTemp> getData(int day, int pageNum) {
        Date nowDate = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.add(calendar.DATE, day);//把日期往后增加一天.整数往后推,负数往前移动
        nowDate = calendar.getTime(); //这个时间就是日期往后推一天的结果
        nowDate.setHours(0);
        nowDate.setMinutes(0);
        nowDate.setSeconds(0);
        beginTime = new Date(nowDate.getYear(), nowDate.getMonth(), nowDate.getDate(), 0, 0, 0);
        endTime = new Date(nowDate.getYear(), nowDate.getMonth(), nowDate.getDate(), 23, 59, 59);
        DataService s = new DataService();
        List<FlightInfoTemp> dataList = new ArrayList<>();
        dataList = s.getFlightInfoTemp(beginTime, endTime, pageNum);
        Log.e(TAG, "dataList.size()   " + dataList.size());
        return dataList;

    }

    public List<FlightInfoTemp> getData(int day) {
        Date nowDate = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.add(calendar.DATE, day);//把日期往后增加一天.整数往后推,负数往前移动
        nowDate = calendar.getTime(); //这个时间就是日期往后推一天的结果
        nowDate.setHours(0);
        nowDate.setMinutes(0);
        nowDate.setSeconds(0);
        beginTime = new Date(nowDate.getYear(), nowDate.getMonth(), nowDate.getDate(), 0, 0, 0);
        endTime = new Date(nowDate.getYear(), nowDate.getMonth(), nowDate.getDate(), 23, 59, 59);
        DataService s = new DataService();
        List<FlightInfoTemp> dataList = new ArrayList<>();
        dataList = s.getFlightInfoTemp(beginTime, endTime);
        return dataList;

    }

    public abstract int getDayIndex();

    @Override
    public void onDestroyView() {
        popupMenuItemList.clear();
        super.onDestroyView();
    }
}
