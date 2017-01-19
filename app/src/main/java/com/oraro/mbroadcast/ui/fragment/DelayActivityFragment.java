package com.oraro.mbroadcast.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.listener.DelayDialogListener;
import com.oraro.mbroadcast.logicService.DataService;
import com.oraro.mbroadcast.model.FlightInfoTemp;
import com.oraro.mbroadcast.model.PlayEntry;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.ui.adapter.DelayAdapter;
import com.oraro.mbroadcast.utils.LogUtils;
import com.oraro.mbroadcast.vo.PlayVO;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2016/11/23 0023.
 *
 * @author
 */
public class DelayActivityFragment extends Fragment {
    public  static final  String TAG_DELAY="DelayActivityFragment";

    private static final int MSG_LOAD_FINISH = 1;
    private static final int MSG_UPDATE_START = 2;
    private static final int MSG_UPDATE_FINISH = 3;

    private Button bt_yanwu;
    private TextView header_left_btn;
    private CheckBox checkBox1;
    private Button quanxuan;
    private ListView listView;
    private DBManager dbManager;
    private DelayAdapter delayAdapter;
    private DelayAdapter.ViewHolder holder;
    private WheelView hourWheelView, minuteWheelView;
    private ArrayWheelAdapter mArrayWheelAdapter;
    private DelayFragment delayFragment;
    private View mLoadingView;

    private boolean mIsLoading = false;
    private boolean mIsUpdating = false;

    private List<PlayVO> mPlayvoData = new ArrayList<>();

    private int hours;
    private int minutes;
    //private List<PlayEntry> mplayVO = new ArrayList<>();

    //将查找出来的播报信息按航班号分类记录
    private HashMap<String,List<PlayEntry>> mOriginalPlayEntryMap = new HashMap<>();
    //将选中的播报信息按航班号分类记录
    private HashMap<String,List<PlayEntry>> mSelectedPlayEntryMap = new HashMap<>();

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_LOAD_FINISH:
                    mIsLoading = false;
                    if (mPlayvoData.size()>0){
                        delayAdapter.setData(mPlayvoData);
                    }else {
                        Toast.makeText(getActivity(),"没有当前航班信息,无法延误",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MSG_UPDATE_START:
                    mLoadingView.setVisibility(View.VISIBLE);
                    break;
                case MSG_UPDATE_FINISH:
                    mIsUpdating = false;
                    mLoadingView.setVisibility(View.GONE);
                    EventBus.getDefault().post(new SimpleEvent(Constants.BACK_MAIN_CLOSE));
                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //LogUtils.e("zxl","onCreate--->"+mIsLoading + "--->" +mPlayvoData.size() +"--->"+ mOriginalPlayEntryMap.size() + "--->" + mSelectedPlayEntryMap.size());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        LogUtils.e("xxx","onCreateView");
        View view = inflater.inflate(R.layout.yanwu, null);

        initView(view);

        return view;
    }

    private void initView(View view) {
        mLoadingView = view.findViewById(R.id.ll_loading);
        bt_yanwu = (Button)view.findViewById(R.id.bt_yanwu);
        header_left_btn = (TextView) view.findViewById(R.id.header_left_btn);
        header_left_btn.setVisibility(View.VISIBLE);
        checkBox1 = (CheckBox) view.findViewById(R.id.checkbox1);
        checkBox1.setChecked(true);
        quanxuan = (Button) view.findViewById(R.id.quanxuan);
        listView = (ListView)view.findViewById(R.id.listview1);
        delayAdapter = new DelayAdapter(getActivity());
        listView.setAdapter(delayAdapter);
        getAllData(0);
        dbManager = DBManager.getInstance(getActivity());
        initWheel2(view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlayVO pd = mPlayvoData.get(position);
                String flightNumber = pd.getEntity().getFlightInfoTemp().getFlightNumber();
                if(!pd.isChecked()){
                    if(null == mSelectedPlayEntryMap.get(flightNumber)){
                        List<PlayEntry> list = new ArrayList<PlayEntry>();
                        mSelectedPlayEntryMap.put(flightNumber,list);
                    }
                    mSelectedPlayEntryMap.get(flightNumber).add(pd.getEntity());
                }else{
                    mSelectedPlayEntryMap.get(flightNumber).remove(pd.getEntity());
                    if(mSelectedPlayEntryMap.get(flightNumber).size() <= 0){
                        mSelectedPlayEntryMap.remove(flightNumber);
                    }
                }
                mPlayvoData.get(position).setChecked(!pd.isChecked());
                delayAdapter.notifyDataSetChanged();
            }
        });

       header_left_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               EventBus.getDefault().post(new SimpleEvent(Constants.BACK_MAIN_CLOSE));
           }
       });
       quanxuan.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(checkUpdateState(false)){
                   return;
               }
               if ("全选".equals(quanxuan.getText())) {
                   selectAll();
                   quanxuan.setText("全不选");
               } else {
                   cancelAll();
                   quanxuan.setText("全选");
               }
           }
       });
        bt_yanwu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hours = hourWheelView.getCurrentPosition();
                minutes = minuteWheelView.getCurrentPosition();

                if (mSelectedPlayEntryMap.size() <= 0) {
                    Toast.makeText(getActivity(), "请选择延误航班", Toast.LENGTH_SHORT).show();
                    return;
                }
                //showDialog();
                if ("全选".equals(quanxuan.getText())) {
                    showDialog();
                } else {
                    showDialog1();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayvoData.clear();
        mOriginalPlayEntryMap.clear();
        mSelectedPlayEntryMap.clear();
    }

    private void updateData(boolean isShunYan){
        Set<String> mSets = mSelectedPlayEntryMap.keySet();
        for(String key : mSets){
            if(isShunYan){
                List<PlayEntry> list = mOriginalPlayEntryMap.get(key);
                updateDataToDB(list);
            }else{
                List<PlayEntry> list = mSelectedPlayEntryMap.get(key);
                //同一个航班号的播报信息都被选择了，则当作顺延处理
                if(list.size() > 1){
                    updateDataToDB(list);
                }else{
                    PlayEntry playEntry = list.get(0);
                    PlayEntry mLastPlayEntry = null;
                    List<PlayEntry> mOriginalPlayEntrylist = mOriginalPlayEntryMap.get(key);
                    //查找该航班号对应的最后一条播报信息
                    for(PlayEntry pe : mOriginalPlayEntrylist){
                        if(null == mLastPlayEntry){
                            mLastPlayEntry = pe;
                        }else{
                            if(pe.getTime().getTime() > mLastPlayEntry.getTime().getTime()){
                                mLastPlayEntry = pe;
                            }
                        }
                    }
                    if(playEntry == mLastPlayEntry){
                        updateDataToDB(list);
                    }else{
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(playEntry.getTime());
                        calendar.add(Calendar.HOUR_OF_DAY, hours);
                        calendar.add(Calendar.MINUTE, minutes);
                        //如果播报时间超过该航班号对应的最后一条播报信息,则顺延处理
                        if(calendar.getTime().getTime() >= mLastPlayEntry.getTime().getTime()){
                            updateDataToDB(mOriginalPlayEntrylist);
                        }else{
                            updateDataToDB(list);
                        }
                    }
                }
            }
        }

        try {
            MBroadcastApplication.getIMyAidlInterface().refresh();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().postSticky(new SimpleEvent(Constants.Analytic_Cmpletion_Notice));
    }

    private void updateDataToDB(List<PlayEntry> list) {
        for(PlayEntry pe : list){
            String number = pe.getFlightInfoTemp().getFlightNumber();
            FlightInfoTemp temp = pe.getFlightInfoTemp();
            Date date = pe.getTime();
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            temp.setIsDelay(true);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, hours);
            calendar.add(Calendar.MINUTE, minutes);
            pe.setTime(calendar.getTime());
            dbManager.update(pe, dbManager.getPlayEntryDao(DBManager.WRITE_ONLY));
            dbManager.update(temp, dbManager.getFlightInfoTempDao(DBManager.WRITE_ONLY));
        }
    }


    public void getAllData(final int day) {
        if(mIsLoading){
            return;
        }
        mIsLoading = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Date nowDate = new Date();
                Calendar calendar = new GregorianCalendar();
                calendar.add(calendar.DATE, day);//把日期往后增加一天.整数往后推,负数往前移动
                nowDate = calendar.getTime(); //这个时间就是日期往后推一天的结果
                Date beginTime = new Date(nowDate.getYear(), nowDate.getMonth(), nowDate.getDate(), nowDate.getHours(), nowDate.getMinutes(), 0);
                Date endTime = new Date(nowDate.getYear(), nowDate.getMonth(), nowDate.getDate(), 23, 59, 59);
                DataService s = new DataService();

                mPlayvoData.clear();
                mPlayvoData = s.getPlayVO(beginTime, endTime);

                mOriginalPlayEntryMap.clear();
                mSelectedPlayEntryMap.clear();

                Iterator<PlayVO> playVOIterator = mPlayvoData.iterator();
                while(playVOIterator.hasNext()){
                    PlayVO playVO = playVOIterator.next();
                    if (null == playVO.getEntity().getFlightInfoTemp()) {
                        //如果是自定义新增录音播报
                        playVOIterator.remove();
                        continue;
                    }
                    String flightNumber = playVO.getEntity().getFlightInfoTemp().getFlightNumber();
                    if(null == mOriginalPlayEntryMap.get(flightNumber)){
                        List<PlayEntry> list = new ArrayList<PlayEntry>();
                        mOriginalPlayEntryMap.put(flightNumber,list);
                    }
                    mOriginalPlayEntryMap.get(flightNumber).add(playVO.getEntity());
                }

                LogUtils.e("xxx ", " dataList size" + mPlayvoData.size());
                Message ms = Message.obtain();
                ms.what = MSG_LOAD_FINISH;
                mHandler.sendMessage(ms);

            }
        }).start();
    }
    private void initWheel2(View view) {
        //时
        hourWheelView = (WheelView) view.findViewById(R.id.hour_wheelview);
        hourWheelView.setWheelAdapter(new ArrayWheelAdapter(getActivity()));
        hourWheelView.setSkin(WheelView.Skin.Holo);
        hourWheelView.setLoop(true);
        hourWheelView.setWheelData(createHours());
        WheelView.WheelViewStyle style = new WheelView.WheelViewStyle();
        style.selectedTextColor = Color.parseColor("#0288ce");
        style.textColor = Color.GRAY;
        style.selectedTextSize = 20;
        hourWheelView.setStyle(style);
        hourWheelView.setExtraText("时", Color.parseColor("#0288ce"), 40, 70);
        //分
        minuteWheelView = (WheelView) view.findViewById(R.id.minute_wheelview);
        minuteWheelView.setWheelAdapter(new ArrayWheelAdapter(getActivity()));
        minuteWheelView.setSkin(WheelView.Skin.Holo);
        minuteWheelView.setLoop(true);
        minuteWheelView.setWheelData(createMinutes());
        minuteWheelView.setStyle(style);
        minuteWheelView.setExtraText("分", Color.parseColor("#0288ce"), 40, 70);
    }
    private ArrayList<String> createHours() {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i <24; i++) {
            if (i < 10) {
                list.add("0" + i);
            } else {
                list.add("" + i);
            }
        }
        return list;
    }

    private ArrayList<String> createMinutes() {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < 60; i++) {
            if (i < 10) {
                list.add("0" + i);
            } else {
                list.add("" + i);
            }
        }
        return list;
    }
    private void cancelAll() {
        for (int i = 0; i < mPlayvoData.size(); i++) {
            mPlayvoData.get(i).setChecked(false);
            String flightNumber = mPlayvoData.get(i).getEntity().getFlightInfoTemp().getFlightNumber();
            mSelectedPlayEntryMap.remove(flightNumber);
        }
        delayAdapter.notifyDataSetChanged();
    }

    private void selectAll() {
        for (int i = 0; i < mPlayvoData.size(); i++) {
            mPlayvoData.get(i).setChecked(true);
            String flightNumber = mPlayvoData.get(i).getEntity().getFlightInfoTemp().getFlightNumber();
            if(null == mSelectedPlayEntryMap.get(flightNumber)){
                List<PlayEntry> list = new ArrayList<PlayEntry>();
                mSelectedPlayEntryMap.put(flightNumber,list);
            }
            mSelectedPlayEntryMap.get(flightNumber).add(mPlayvoData.get(i).getEntity());
        }
        delayAdapter.notifyDataSetChanged();
    }

    /**
     * 全选情况下顺延，不顺延
     */
    private void showDialog1() {
        delayFragment = new DelayFragment();
        delayFragment.setTitle("提示");
        delayFragment.setSubmit("顺延");
        delayFragment.setmCancle("不顺延");
        delayFragment.show(getActivity().getFragmentManager(), "delayFragment1");
        delayFragment.setContent(SpannableStringBuilder.valueOf("您所选中的航班，后续未播报部分已经批量延误" + hours + "小时" + minutes + "分钟，是否确认?"));
        delayFragment.setOnButtonClickListener(new DelayDialogListener() {
            @Override
            public void onDelaySumbitListener() {
                delayFragment.dismiss();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //检查当前是否正则更新延误信息
                        if(checkUpdateState(true)){
                            return;
                        }
                        mHandler.sendEmptyMessage(MSG_UPDATE_START);
                        updateData(true);
                        mHandler.sendEmptyMessage(MSG_UPDATE_FINISH);
                    }
                }).start();
            }

            @Override
            public void onDelayCancleListener() {
                delayFragment.dismiss();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //检查当前是否正则更新延误信息
                        if(checkUpdateState(true)){
                            return;
                        }
                        mHandler.sendEmptyMessage(MSG_UPDATE_START);
                        updateData(false);
                        mHandler.sendEmptyMessage(MSG_UPDATE_FINISH);
                    }
                }).start();
            }
        });
    }

    /**
     * @param isToUpdateData 是否是开始更新数据
     * 如果true表示需要更新状态，如果false表示返回当前状态
     * @return
     */
    public boolean checkUpdateState(boolean isToUpdateData){
        if(mIsUpdating){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.updating_delay_info),Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }
        if(isToUpdateData){
            mIsUpdating = true;
        }
        return false;
    }

    private void showDialog() {
        final DelayFragment delayFragment = new DelayFragment();
        delayFragment.setTitle("提示");
        delayFragment.setContent(SpannableStringBuilder.valueOf("您所选中的航班，后续未播报部分已经批量延误" + hours + "小时" + minutes + "分钟，是否确认?"));
        delayFragment.setSubmit("顺延");
        delayFragment.setmCancle("不顺延");
        delayFragment.show(getActivity().getFragmentManager(), "delayFragment");
        delayFragment.setOnButtonClickListener(new DelayDialogListener() {
            //顺延 未播报延迟
            @Override
            public void onDelaySumbitListener() {
                delayFragment.dismiss();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //检查当前是否正则更新延误信息
                        if(checkUpdateState(true)){
                            return;
                        }
                        mHandler.sendEmptyMessage(MSG_UPDATE_START);
                        updateData(true);
                        mHandler.sendEmptyMessage(MSG_UPDATE_FINISH);
                    }
                }).start();

            }

            //不顺延 未播报延迟
            @Override
            public void onDelayCancleListener() {
                delayFragment.dismiss();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //检查当前是否正则更新延误信息
                        if(checkUpdateState(true)){
                            return;
                        }
                        mHandler.sendEmptyMessage(MSG_UPDATE_START);
                        updateData(false);
                        mHandler.sendEmptyMessage(MSG_UPDATE_FINISH);
                    }
                }).start();
            }
        });
    }


}
