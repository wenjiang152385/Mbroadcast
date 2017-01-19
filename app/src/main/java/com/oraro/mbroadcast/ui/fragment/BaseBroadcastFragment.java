package com.oraro.mbroadcast.ui.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.listener.OnImgClickListener;
import com.oraro.mbroadcast.logicService.DataService;
import com.oraro.mbroadcast.mina.client.MinaStringClientThread;
import com.oraro.mbroadcast.model.PlayEntry;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.service.IMyAidlInterface;
import com.oraro.mbroadcast.service.OnRefreshUIListener;
import com.oraro.mbroadcast.service.Service1;
import com.oraro.mbroadcast.ui.activity.EditActivity;
import com.oraro.mbroadcast.ui.activity.MainActivity;
import com.oraro.mbroadcast.ui.activity.UrgentBroadcastActivity;
import com.oraro.mbroadcast.ui.adapter.WeekBroadcastAdapter;
import com.oraro.mbroadcast.ui.widget.RefreshLayout;
import com.oraro.mbroadcast.utils.DataUtils;
import com.oraro.mbroadcast.utils.DateUtils;
import com.oraro.mbroadcast.utils.LogUtils;
import com.oraro.mbroadcast.utils.PopupList;
import com.oraro.mbroadcast.utils.SPUtils;
import com.oraro.mbroadcast.vo.PlayVO;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class BaseBroadcastFragment extends Fragment implements PopupList.OnPopupListClickListener, OnImgClickListener {
    private final static String TAG = BaseBroadcastFragment.class.getSimpleName();
    protected View mMainView;
    protected Context mContext;
    public List<String> popupMenuItemList = new ArrayList<>();
    List<PlayVO> flightInfoTemps;
    ListView listView;
    RelativeLayout fragment_temporary_rl0, fragment_temporary_rl1;
    public int weekIndex;
    public Button button;
    public WeekBroadcastAdapter weekBroadcastAdapter;
    public Date beginTime, endTime;
    private Calendar calendar;
    public RefreshLayout myRefreshListView;
    protected long mPlayVOId;
    protected int mViewPostion;
    protected boolean flag = true;
    public PopupList popupList = new PopupList();
    protected IMyAidlInterface iMyAidlInterface;
    protected MinaStringClientThread minaStringClientThread;
    protected TextView text_title_play_time, text_title_flight_number, text_title_flight_type,
            text_title_destination, text_title_type, text_title_yanwu;


    public interface BindCallBack {
        void bindSuccess();
    }

    public BaseBroadcastFragment() {
        super();
    }

    public int index;

    public boolean mFlag;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            iMyAidlInterface = MBroadcastApplication.getIMyAidlInterface();
            if (null != iMyAidlInterface)
                iMyAidlInterface.setOnRefreshUIListener(onRefreshUIListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SimpleEvent event) {
        if(event.getMsg()==Constants.SERVICE1_CONNECT_SUCESSFUL){
            try {
                Log.e("dy", "onEventMainThread iMyAidlInterface = " + iMyAidlInterface);
                iMyAidlInterface = MBroadcastApplication.getIMyAidlInterface();
                iMyAidlInterface.setOnRefreshUIListener(onRefreshUIListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            mFlag = true;
            LinkedHashSet<String> defult = new LinkedHashSet<String>();
            initDefaultNecesaryWord(defult);
            LinkedHashSet<String> defultn = new LinkedHashSet<String>();
            initDefaultNotNecesaryWord(defultn);

            int day = getNowdayWeekIndex();
            Set<String> necessary = SPUtils.getPrefStringSet(mContext, "necessary", defult);
            Set<String> notnecessary = SPUtils.getPrefStringSet(mContext, "fieldsSelectSet", defultn);
            setRxGreenDaoData(day, 0, necessary, notnecessary);
        } else {
            mFlag = false;
        }
    }

    /**
     * 初始化播报设置的必须默认字段
     * @param defult
     * @return
     */
    @NonNull
    private void initDefaultNecesaryWord(LinkedHashSet<String> defult) {
        defult.add("0&" + getString(R.string.listview_text_title_play_time) + "&time");
        defult.add("1&" + getString(R.string.listview_text_title_flight_number) + "&flightNumber");
        defult.add("2&" + getString(R.string.listview_text_title_flight_type) + "&planeType");
        defult.add("3&" + getString(R.string.listview_text_title_destination) + "&arrivalStation");

        LinkedHashSet<String> defultn = new LinkedHashSet<String>();
        defultn.add("4&" + getString(R.string.listview_text_title_type) + "&remarks");
        defultn.add("5&" + "延误信息&isDelay");
    }

    /**
     * 初始化播报设置的必须可选默认字段
     * @param defult
     * @return
     */
    @NonNull
    private void initDefaultNotNecesaryWord(LinkedHashSet<String> defult) {
        defult.add("4&" + getString(R.string.listview_text_title_type) + "&remarks");
        defult.add("5&" + "延误信息&delayInfo");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        long start = System.currentTimeMillis();
        calendar = new GregorianCalendar();

        mMainView = inflater.inflate(R.layout.broadcast_listview, container, false);

        text_title_play_time = (TextView) mMainView.findViewById(R.id.text_title_play_time);
        text_title_flight_number = (TextView) mMainView.findViewById(R.id.text_title_flight_number);
        text_title_flight_type = (TextView) mMainView.findViewById(R.id.text_title_flight_type);
        text_title_destination = (TextView) mMainView.findViewById(R.id.text_title_destination);
        LinkedHashSet<String> defult = new LinkedHashSet<String>();
        initDefaultNotNecesaryWord(defult);
        Set<String> necessary = SPUtils.getPrefStringSet(mContext, "necessary", defult);
        int i = 0;
        for (String s : necessary) {
            String[] sss = s.split("&");
            int key = Integer.parseInt(sss[0]);
            String ss = sss[1];
            if (ss == null || ss.equals("")) {
                i++;
                continue;
            }
            switch (key) {
                case 0:
                    text_title_play_time.setText(ss);
                    break;
                case 1:
                    text_title_flight_number.setText(ss);
                    break;
                case 2:
                    text_title_flight_type.setText(ss);
                    break;
                case 3:
                    text_title_destination.setText(ss);
                    break;
            }
            i++;
        }

        text_title_type = (TextView) mMainView.findViewById(R.id.text_title_type);
        text_title_yanwu = (TextView) mMainView.findViewById(R.id.text_title_yanwu);
        LinkedHashSet<String> defultn = new LinkedHashSet<String>();
        initDefaultNotNecesaryWord(defultn);
        Set<String> notnecessary = SPUtils.getPrefStringSet(mContext, "fieldsSelectSet", defultn);

        i = 0;
        LinkedHashSet<String> truenecessary = new LinkedHashSet<String>();
        for (String s : notnecessary) {
            truenecessary.add(s);
            String[] sss = s.split("&");
            int key = Integer.parseInt(sss[0]);
            String ss = sss[1];
            if (ss == null || ss.equals("")) {
                i++;
                continue;
            }
            switch (key) {
                case 4:
                    text_title_type.setText(ss);
                    break;
                case 5:
                    text_title_yanwu.setText(ss);
                    break;
            }
            i++;
        }
        if (i < 2) {
            int j = 0;
            for (String s : defultn) {
                if (j < i) {
                    j++;
                    continue;
                }
                truenecessary.add(s);
                j++;
            }
            notnecessary = truenecessary;
        }

        listView = (ListView) mMainView.findViewById(R.id.list);
        fragment_temporary_rl0 = (RelativeLayout) mMainView.findViewById(R.id.fragment_temporary_rl0);
        fragment_temporary_rl1 = (RelativeLayout) mMainView.findViewById(R.id.fragment_temporary_rl1);
        button = (Button) mMainView.findViewById(R.id.button);
        // 获取RefreshLayout实例
        myRefreshListView = (RefreshLayout) mMainView.findViewById(R.id.swipe_layout);
        flightInfoTemps = new ArrayList<>();
        int day = getNowdayWeekIndex();
        //wjq
//        flightInfoTemps = getData(day, 0);
//        if (mFlag) {
//            Log.e("wjq","1111111111111111111111");
//            setRxGreenDaoData(day, 0, necessary, notnecessary);
//        }
        popupMenuItemList.add(mContext.getString(R.string.popuplist_edit));
        popupMenuItemList.add(mContext.getString(R.string.popuplist_delete));
        popupList.init(mContext, listView, popupMenuItemList, this);
        final ImageView indicator = new ImageView(mContext);
        indicator.setImageResource(R.mipmap.popuplist_default_arrow);
        popupList.setIndicatorView(indicator);
        popupList.setIndicatorSize(dp2px(16), dp2px(8));
        popupList.setTextSizePixel(30);
        if (isReset() && DateUtils.getWeek(new Date()) == weekIndex) {
            setReset();
        }
        myRefreshListView.setRefreshing(true);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setVisibility(View.VISIBLE);
                fragment_temporary_rl0.setVisibility(View.VISIBLE);
                fragment_temporary_rl1.setVisibility(View.GONE);
                myRefreshListView.setVisibility(View.VISIBLE);
                SPUtils.setPrefBoolean(mContext, "isAdd", false);
                EventBus.getDefault().post(new SimpleEvent(Constants.TemporaryPlayFragment));
                DBManager db = DBManager.getInstance(getContext());
                List<PlayEntry> pl = db.queryIsTemporary();
                db.deleteList(pl, db.getPlayEntryDao(DBManager.WRITE_ONLY));
                flightInfoTemps = getAllData(0);
                weekBroadcastAdapter.setPlayVOData(flightInfoTemps);
                listView.setSelection(DataUtils.getNowPostion(flightInfoTemps));
                weekBroadcastAdapter.addFrushHandle();
                weekBroadcastAdapter.notifyDataSetChanged();
            }
        });

        return mMainView;
    }

    protected void refreshUI() {
        LinkedHashSet<String> defult = new LinkedHashSet<String>();
        initDefaultNecesaryWord(defult);
        Set<String> necessary = SPUtils.getPrefStringSet(mContext, "necessary", defult);

        int i = 0;
        for (String s : necessary) {
            String[] sss = s.split("&");
            int key = Integer.parseInt(sss[0]);
            String ss = sss[1];
            if (ss == null || ss.equals("")) {
                i++;
                continue;
            }
            switch (key) {
                case 0:
                    text_title_play_time.setText(ss);
                    break;
                case 1:
                    text_title_flight_number.setText(ss);
                    break;
                case 2:
                    text_title_flight_type.setText(ss);
                    break;
                case 3:
                    text_title_destination.setText(ss);
                    break;
            }
            i++;
        }

        LinkedHashSet<String> defultn = new LinkedHashSet<String>();
        initDefaultNotNecesaryWord(defultn);
        Set<String> notnecessary = SPUtils.getPrefStringSet(mContext, "fieldsSelectSet", defultn);

        i = 0;
        LinkedHashSet<String> truenecessary = new LinkedHashSet<String>();
        for (String s : notnecessary) {
            truenecessary.add(s);
            String[] sss = s.split("&");
            int key = Integer.parseInt(sss[0]);
            String ss = sss[1];
            if (ss == null || ss.equals("")) {
                i++;
                continue;
            }
            switch (key) {
                case 4:
                    text_title_type.setText(ss);
                    break;
                case 5:
                    text_title_yanwu.setText(ss);
                    break;
            }
            i++;
        }
        if (i < 2) {
            int j = 0;
            for (String s : defultn) {
                if (j < i) {
                    j++;
                    continue;
                }
                truenecessary.add(s);
                j++;
            }
            notnecessary = truenecessary;
        }
        weekBroadcastAdapter.setNecessary(necessary, notnecessary);
        weekBroadcastAdapter.notifyDataSetChanged();
    }

    protected int dp2px(float value) {
        final float scale = this.getResources().getDisplayMetrics().densityDpi;
        return (int) (value * (scale / 160) + 0.5f);
    }

    @Override
    public void onPopupListClick(View contextView, int contextPosition, int position) {
        if (position == 0) {
            mPlayVOId = flightInfoTemps.get(contextPosition).getEntity().getId();
            mViewPostion = contextPosition;
            Intent intent = new Intent(getActivity(), EditActivity.class);
            intent.putExtra("PEID", mPlayVOId);
            intent.putExtra("flag", 1);
            Intent intent2 = new Intent(getActivity(), UrgentBroadcastActivity.class);
            intent2.putExtra("PEID", mPlayVOId);
            intent2.putExtra("flag", 1);
            if (2 == flightInfoTemps.get(contextPosition).getEntity().getIsEmeng()) {
                getActivity().startActivity(intent2);
            } else {
                getActivity().startActivity(intent);
            }

            contextView.setBackgroundColor(mContext.getResources().getColor(R.color.common_dark_edit));
        }
        if (position == 1) {
            Long playid = flightInfoTemps.get(contextPosition).getEntity().getId();
            DBManager.getInstance(getActivity()).delete(flightInfoTemps.get(contextPosition).getEntity(), DBManager.getInstance(getActivity()).getPlayEntryDao(DBManager.WRITE_ONLY));
            if (iMyAidlInterface != null) {
                try {
                    if (playid.equals(MBroadcastApplication.getPlayID())) {
                        iMyAidlInterface.stopPlay();
                    }
                    iMyAidlInterface.deleteCatch(flightInfoTemps.get(contextPosition).getEntity().getId());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            weekBroadcastAdapter.collapseDeleteView(listView, contextPosition);
            Set set = SPUtils.getPrefStringSet(getContext(), "set", null);
            if (null != set) {
                Iterator iterator = set.iterator();
                while (iterator.hasNext()) {
                    String ip = (String) iterator.next();
                    MinaStringClientThread minaStringClientThread = new MinaStringClientThread();
                    minaStringClientThread.setType(Constants.A_DATA_DELETE);
                    minaStringClientThread.setPlayVO(flightInfoTemps.get(contextPosition));
                    minaStringClientThread.setIp(ip);
                    MinaStringClientThread.getThreadPoolExecutor().execute(minaStringClientThread);
                }
            }
        }
    }

    @Override
    public void play(PlayVO playVO, int postion) {
        BroadcastDialog broadcastDialog = new BroadcastDialog();
        Bundle bundle = new Bundle();
        bundle.putLong("playVOID", playVO.getEntity().getId());
        bundle.putParcelable("playVO", playVO);
        broadcastDialog.setArguments(bundle);
        broadcastDialog.show(getActivity().getSupportFragmentManager(), "broadcastDialog");
        mPlayVOId = playVO.getEntity().getId();
        mViewPostion = postion;

    }

    @Override
    public void playAagain(final PlayVO playVO) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogCustom);
        builder.setTitle("快捷播放");
        builder.setMessage("确定是立即播放？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("BaseBroadcastFragment", "点击了再次播放。。。。。 ");
//                TTSInterface tts = TTSProXy.getInstance(MBroadcastApplication.getMyContext(), MBroadcastApplication.getMyPackageName());
//                PlayAudio playAudio = PlayAudio.getInstance();


//                if (iMyAidlInterface != null) {
//                    try {
//                        iMyAidlInterface.stopMediaPlay();
//                        iMyAidlInterface.stopTTSPlay();
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
//                }

                if (playVO.getEntity().getFileParentPath() != null) {
//                    if (tts.isSpeeking()) {
//                        tts.TTSPausePlay();
//                    }
//                    //执行Media逻辑
//                    playAudio.setPath(playVO.getEntity().getFileParentPath());
//                    playAudio.startAudio();

                    if (iMyAidlInterface != null) {
                        try {
                            iMyAidlInterface.startMediaPlay(playVO.getEntity().getId(), playVO.getEntity().getFileParentPath(), 1, new OnRefreshUIListener.Stub() {
                                @Override
                                public void completed(long id, String error) throws RemoteException {
                                    MBroadcastApplication.setPlayID((long) -1);
                                    weekBroadcastAdapter.setIsClickAdapterPlayBtn(false);
                                    weekBroadcastAdapter.setAdapterNotify();
                                    EventBus.getDefault().post(new SimpleEvent(9823442));//通知dialog,刷新按钮状态
                                }

                                @Override
                                public void frushPlaying(long id) throws RemoteException {
                                    MBroadcastApplication.setPlayID(playVO.getEntity().getId());
                                    weekBroadcastAdapter.setIsClickAdapterPlayBtn(true);
                                    weekBroadcastAdapter.isIconPlaying(playVO.getEntity().getId());
                                }
                            });
                        } catch (RemoteException e) {
                            MBroadcastApplication.setPlayID((long) -1);
                            weekBroadcastAdapter.setIsClickAdapterPlayBtn(false);
                            weekBroadcastAdapter.setAdapterNotify();
                            EventBus.getDefault().post(new SimpleEvent(9823442));//通知dialog,刷新按钮状态
                            e.printStackTrace();
                        }
                    }

                } else {
//                    if (playAudio.isPlaying()) {
//                        playAudio.pauseAudio();
//                    }
//                    //执行TTS逻辑
//                    tts.TTSStartPlay(playVO.getEntity().getTextDesc(), null, null, null, null, null, null, null);

                    Log.e("dy", "click iMyAidlInterface = " + iMyAidlInterface);
                    if (iMyAidlInterface != null) {
                        try {

                            iMyAidlInterface.startTTSPlay(playVO.getEntity().getId(), playVO.getEntity().getTextDesc(), 1, new OnRefreshUIListener.Stub() {
                                @Override
                                public void completed(long id, String error) throws RemoteException {
                                    MBroadcastApplication.setPlayID((long) -1);
                                    weekBroadcastAdapter.setIsClickAdapterPlayBtn(false);
                                    weekBroadcastAdapter.setAdapterNotify();
//                                    ttsDialogFragment.setBtn();
                                    EventBus.getDefault().post(new SimpleEvent(9823442));
                                }

                                @Override
                                public void frushPlaying(long id) throws RemoteException {
                                    MBroadcastApplication.setPlayID(playVO.getEntity().getId());
                                    weekBroadcastAdapter.setIsClickAdapterPlayBtn(true);
                                    weekBroadcastAdapter.isIconPlaying(playVO.getEntity().getId());
                                }
                            });
                        } catch (RemoteException e) {
                            MBroadcastApplication.setPlayID((long) -1);
                            weekBroadcastAdapter.setIsClickAdapterPlayBtn(false);
                            weekBroadcastAdapter.setAdapterNotify();
                            EventBus.getDefault().post(new SimpleEvent(9823442));//通知dialog,刷新按钮状态
                            e.printStackTrace();
                        }
                    }
                }
                Toast.makeText(MBroadcastApplication.getMyContext(), "即将停止当前播报,立即开始插播。", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    public boolean isReset() {
        return false;
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    public void setReset() {
        listView.setVisibility(View.GONE);
        fragment_temporary_rl0.setVisibility(View.GONE);
        myRefreshListView.setVisibility(View.GONE);
        fragment_temporary_rl1.setVisibility(View.VISIBLE);
    }

    public abstract int getNowdayWeekIndex();

    public List<PlayVO> getData(int day, int pageNum) {
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
        List<PlayVO> dataList = new ArrayList<PlayVO>();
        dataList = s.getPlayVO(beginTime, endTime, pageNum);
        return dataList;
    }

    private Subscription mSubscription;
    public void setRxGreenDaoData(final int day, int pageNum, final Set<String> necessary, final Set<String> notnecessary) {
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
        Observable<List<PlayEntry>> observable = s.getPlayEntry1(beginTime, endTime);
        mSubscription = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<PlayEntry>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<PlayEntry> playEntries) {
                        List<PlayVO> returnList = new LinkedList<>();
                        if (playEntries != null && playEntries.size() != 0) {
                            for (PlayEntry pe : playEntries) {
                                returnList.add(new PlayVO(pe));
                            }
                        }
                        flightInfoTemps = returnList;
                        weekBroadcastAdapter = new WeekBroadcastAdapter(mContext, flightInfoTemps, necessary, notnecessary);
                        weekBroadcastAdapter.setOnImgClickListener(BaseBroadcastFragment.this);
                        weekBroadcastAdapter.setHandler(handler);
                        listView.setAdapter(weekBroadcastAdapter);
                        if (day == 0) {
                            weekBroadcastAdapter.addFrushHandle();
                        }
                        if (flightInfoTemps.size() > 0) {
                            if (weekIndex == new Date().getDay()) {
                                index = -999999999;
                                flightInfoTemps = getAllData(getNowdayWeekIndex());
                                weekBroadcastAdapter.setPlayVOData(flightInfoTemps);
                                weekBroadcastAdapter.notifyDataSetChanged();
                                int nowTimePostion = DataUtils.getNowPostion(flightInfoTemps);
                                if (flightInfoTemps.size() > 1) {
                                    if (nowTimePostion == -1) {
                                        listView.setSelection(flightInfoTemps.size() - 1);
                                    } else {
                                        listView.setSelection(nowTimePostion);
                                    }
                                }
                            }
                        }

                    }
                });
    }

    public List<PlayVO> getAllData(int day) {
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
        List<PlayVO> dataList = new ArrayList<PlayVO>();
        dataList = s.getPlayVO(beginTime, endTime);
        return dataList;
    }

    @Override
    public void onDestroyView() {
        popupMenuItemList.clear();
        super.onDestroyView();
        try {
            if (null != iMyAidlInterface)
            iMyAidlInterface.unRegisterOnRefreshUIListener();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().unregister(this);
        if(mSubscription != null){
            mSubscription.unsubscribe();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int option = msg.arg1;
            if (option > flightInfoTemps.size()) {
                getAllData(getNowdayWeekIndex());
                listView.setSelection(option);
            } else {
                listView.setSelection(option);
            }


        }
    };

    private Handler handlerUI;

    OnRefreshUIListener onRefreshUIListener = new OnRefreshUIListener.Stub() {
        @Override
        public void completed(long id, String error) throws RemoteException {
            handlerUI = MBroadcastApplication.getFrushHandler();
            if (handlerUI != null) {
                Message message = Message.obtain();
                message.what = Constants.HandlerConstants.COMPLETED;
                message.obj = id;
                handlerUI.sendMessage(message);
            }
        }

        @Override
        public void frushPlaying(long id) throws RemoteException {
            handlerUI = MBroadcastApplication.getFrushHandler();
            if (handlerUI != null) {
                Message message = Message.obtain();
                message.what = Constants.HandlerConstants.PLAY;
                message.obj = id;
                handlerUI.sendMessage(message);
            }
        }
    };
}
