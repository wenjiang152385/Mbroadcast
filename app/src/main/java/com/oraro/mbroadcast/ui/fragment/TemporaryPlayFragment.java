package com.oraro.mbroadcast.ui.fragment;

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
import android.support.annotation.Nullable;
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
import com.oraro.mbroadcast.mina.client.MinaStringClientThread;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.service.IMyAidlInterface;
import com.oraro.mbroadcast.service.OnRefreshUIListener;
import com.oraro.mbroadcast.service.Service1;
import com.oraro.mbroadcast.ui.activity.EditActivity;
import com.oraro.mbroadcast.ui.adapter.WeekBroadcastAdapter;
import com.oraro.mbroadcast.ui.widget.RefreshLayout;
import com.oraro.mbroadcast.utils.DataUtils;
import com.oraro.mbroadcast.utils.DateUtils;
import com.oraro.mbroadcast.utils.PopupList;
import com.oraro.mbroadcast.utils.SPUtils;
import com.oraro.mbroadcast.vo.PlayVO;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by dongyu on 2016/8/23 0023.
 */
public class TemporaryPlayFragment extends BaseBroadcastFragment implements OnImgClickListener {
    private final static String TAG = TemporaryPlayFragment.class.getSimpleName();
    private View mTemporaryPlayView;
    private Button imageView;
    private RelativeLayout fragment_temporary_rl0, fragment_temporary_rl1;
    private ListView listView;
    WeekBroadcastAdapter weekBroadcastAdapter;
    private List<String> popupMenuItemList = new ArrayList<>();
    PopupList popupList = new PopupList();
    private RefreshLayout myRefreshListView;
    protected IMyAidlInterface iMyAidlInterface;
    private TextView text_title_play_time, text_title_flight_number, text_title_take_off,
            text_title_destination, text_title_type, text_title_yanwu;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        try {
            iMyAidlInterface = MBroadcastApplication.getIMyAidlInterface();
            if (null != iMyAidlInterface)
                iMyAidlInterface.setOnRefreshUIListener(onRefreshUIListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (null == mTemporaryPlayView) {
            mTemporaryPlayView = inflater.inflate(R.layout.fragment_temporary_play, container, false);
            imageView = (Button) mTemporaryPlayView.findViewById(R.id.fragment_temporary_play_img);
            fragment_temporary_rl0 = (RelativeLayout) mTemporaryPlayView.findViewById(R.id.fragment_temporary_rl0);
            fragment_temporary_rl1 = (RelativeLayout) mTemporaryPlayView.findViewById(R.id.fragment_temporary_rl1);

            text_title_play_time = (TextView) mTemporaryPlayView.findViewById(R.id.text_title_play_time);
            text_title_flight_number = (TextView) mTemporaryPlayView.findViewById(R.id.text_title_flight_number);
            text_title_take_off = (TextView) mTemporaryPlayView.findViewById(R.id.text_title_take_off);
            text_title_destination = (TextView) mTemporaryPlayView.findViewById(R.id.text_title_destination);
            text_title_type = (TextView) mTemporaryPlayView.findViewById(R.id.text_title_type);
            text_title_yanwu = (TextView) mTemporaryPlayView.findViewById(R.id.text_title_yanwu);

            listView = (ListView) mTemporaryPlayView.findViewById(R.id.list);
            // 获取RefreshLayout实例
            myRefreshListView = (RefreshLayout) mTemporaryPlayView.findViewById(R.id.swipe_layout);
        }
        flightInfoTemps = getData(0, 0);

        LinkedHashSet<String> defult = new LinkedHashSet<String>();
        defult.add("0&" + getString(R.string.listview_text_title_play_time) + "&time");
        defult.add("1&" + getString(R.string.listview_text_title_flight_number) + "&flightNumber");
        defult.add("2&" + getString(R.string.listview_text_title_flight_type) + "&planeType");
        defult.add("3&" + getString(R.string.listview_text_title_destination) + "&arrivalStation");
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
                    text_title_take_off.setText(ss);
                    break;
                case 3:
                    text_title_destination.setText(ss);
                    break;
            }
            i++;
        }

        LinkedHashSet<String> defultn = new LinkedHashSet<String>();
        defultn.add("4&" + getString(R.string.listview_text_title_type) + "&remarks");
        defultn.add("5&" + "延误信息&delayInfo");
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

        weekBroadcastAdapter = new WeekBroadcastAdapter(mContext, flightInfoTemps, necessary, notnecessary);
        listView.setAdapter(weekBroadcastAdapter);
        weekBroadcastAdapter.setOnImgClickListener(this);
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
        //  listView.setSelection(DataUtils.getNowPostion(flightInfoTemps));
        popupMenuItemList.add(mContext.getString(R.string.popuplist_edit));
        popupMenuItemList.add(mContext.getString(R.string.popuplist_delete));
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
                        Log.e("BaseBroadcastFragment", "up  up   up  " + index);
                        List list = getData(getNowdayWeekIndex(), index);
                        if (list.isEmpty() || list.size() == 0) {
                            Toast.makeText(mContext, "没有更多数据", Toast.LENGTH_LONG).show();
                            myRefreshListView.setLoading(false);
                            return;
                        }
                        DataUtils.addListData(flightInfoTemps, list);
                        weekBroadcastAdapter.setPlayVOData(flightInfoTemps);
                        // 加载完后调用该方法
                        myRefreshListView.setLoading(false);
                        weekBroadcastAdapter.notifyDataSetChanged();
                    }
                }, 1500);

            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRefreshListView.setVisibility(View.VISIBLE);
                fragment_temporary_rl0.setVisibility(View.VISIBLE);
                fragment_temporary_rl1.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                flightInfoTemps = getAllData(0);
                weekBroadcastAdapter.setPlayVOData(flightInfoTemps);
                weekBroadcastAdapter.notifyDataSetChanged();
                SPUtils.setPrefBoolean(mContext, "isAdd", true);
                EventBus.getDefault().postSticky(new SimpleEvent(DateUtils.getWeek(new Date())));
                listView.setSelection(DataUtils.getNowPostion(flightInfoTemps));
                weekBroadcastAdapter.addFrushHandle();
                weekBroadcastAdapter.setHandler(handler);
            }
        });
        if (SPUtils.hasKey(mContext, "isAdd") && SPUtils.getPrefBoolean(mContext, "isAdd", true)) {
            listView.setVisibility(View.VISIBLE);
            fragment_temporary_rl0.setVisibility(View.VISIBLE);
            fragment_temporary_rl1.setVisibility(View.GONE);
            myRefreshListView.setVisibility(View.VISIBLE);
            weekBroadcastAdapter.addFrushHandle();
            weekBroadcastAdapter.setHandler(handler);
        } else {
            listView.setVisibility(View.GONE);
            fragment_temporary_rl0.setVisibility(View.GONE);
            fragment_temporary_rl1.setVisibility(View.VISIBLE);
            myRefreshListView.setVisibility(View.GONE);
        }
        return mTemporaryPlayView;
    }

    @Subscribe(sticky = true)
    public void onEvent(SimpleEvent event) {/* Do something */
        if (event.getMsg() == Constants.TemporaryPlayFragment) {
            listView.setVisibility(View.GONE);
            fragment_temporary_rl0.setVisibility(View.GONE);
            fragment_temporary_rl1.setVisibility(View.VISIBLE);
            myRefreshListView.setVisibility(View.GONE);
        } else if (event.getMsg() == Constants.Analytic_Cmpletion_Notice) {
            if (!getUserVisibleHint()) {
                return;
            }
            String msg = "onEventMainThread收到了消息：" + event.getMsg();
            Log.e(TAG, msg + "  mPlayVOId  = " + mPlayVOId);
            if (iMyAidlInterface != null) {
                try {
                    iMyAidlInterface.refresh();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            if (listView.getVisibility() == View.VISIBLE) {
                weekBroadcastAdapter.updataView(flightInfoTemps, mViewPostion, listView, mPlayVOId);
                Handler handler0 = new Handler();
                handler0.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        flightInfoTemps.clear();
                        flightInfoTemps = getAllData(getNowdayWeekIndex());
                        weekBroadcastAdapter.setPlayVOData(flightInfoTemps);
                        index = -999999999;
                        weekBroadcastAdapter.notifyDataSetChanged();
                        int mindex = DataUtils.getNowPostion(flightInfoTemps);
                        if (mindex == -1) {
                            listView.setSelection(flightInfoTemps.size());
                        } else {
                            listView.setSelection(mindex);
                        }
                    }
                }, 3000);
            }
        } else if (event.getMsg() == Constants.SETTINGS_WEEK) {
            refreshUI();
        } else if (event.getMsg() == Constants.BROADCAST_ADD) {
            String msg = "onEventMainThread收到了消息：" + event.getMsg();
            if (iMyAidlInterface != null) {
                try {
                    iMyAidlInterface.refresh();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            if (listView.getVisibility() == View.VISIBLE) {
                Log.e(TAG, msg + "  playEntryId   = " + Constants.BROADCAST_ADD);
                weekBroadcastAdapter.updataView(flightInfoTemps, mViewPostion, listView, mPlayVOId);
                Handler handler0 = new Handler();
                handler0.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        flightInfoTemps.clear();
                        flightInfoTemps = getAllData(getNowdayWeekIndex());
                        weekBroadcastAdapter.setPlayVOData(flightInfoTemps);
                        index = -999999999;
                        weekBroadcastAdapter.notifyDataSetChanged();
                        int mindex = DataUtils.getNowPostion(flightInfoTemps);
                        if (mindex == -1) {
                            listView.setSelection(flightInfoTemps.size());
                        } else {
                            listView.setSelection(mindex);
                        }
                    }
                }, 3000);
            }
        } else if (event.getMsg() == Constants.UPDATE_PLAYVO_ONE) {
            weekBroadcastAdapter.updataView(mViewPostion, listView, mPlayVOId);
        } else if (event.getMsg() == Constants.A_PLAY) {
            weekBroadcastAdapter.isIconPlaying(MBroadcastApplication.getPlayID());
        } else if (event.getMsg() == Constants.A_PLAYED) {
            weekBroadcastAdapter.setAdapterNotify();
        }
    }

    @Override
    public void onPopupListClick(View contextView, int contextPosition, int position) {
        if (position == 0) {
            Intent intent = new Intent(getActivity(), EditActivity.class);
            mPlayVOId = flightInfoTemps.get(contextPosition).getEntity().getId();
            intent.putExtra("PEID", flightInfoTemps.get(contextPosition).getEntity().getId());
            intent.putExtra("flag", 1);
            getActivity().startActivity(intent);
            mViewPostion = contextPosition;
            contextView.setBackgroundColor(mContext.getResources().getColor(R.color.common_dark_edit));
        }
        if (position == 1) {
            Long playid = flightInfoTemps.get(contextPosition).getEntity().getId();
            DBManager.getInstance(getActivity()).delete(flightInfoTemps.get(contextPosition).getEntity(), DBManager.getInstance(getActivity()).getPlayEntryDao(DBManager.WRITE_ONLY));
            weekBroadcastAdapter.collapseDeleteView(listView, contextPosition);
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
        super.play(playVO, postion);
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

                if (playVO.getEntity().getFileParentPath() != null) {
                    if (iMyAidlInterface != null) {
                        try {
                            iMyAidlInterface.startMediaPlay(playVO.getEntity().getId(), playVO.getEntity().getFileParentPath(), 1, new OnRefreshUIListener.Stub() {
                                @Override
                                public void completed(long id, String error) throws RemoteException {
                                    Log.e("huanghui", "completed = " + id);
                                    MBroadcastApplication.setPlayID((long) -1);
                                    weekBroadcastAdapter.setIsClickAdapterPlayBtn(false);
                                    weekBroadcastAdapter.setAdapterNotify();
                                    EventBus.getDefault().post(new SimpleEvent(9823442));//通知dialog,刷新按钮状态

                                }

                                @Override
                                public void frushPlaying(long id) throws RemoteException {
                                    Log.e("huanghui", "frushPlaying = " + id);
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
                    if (iMyAidlInterface != null) {
                        try {
                            iMyAidlInterface.startTTSPlay(playVO.getEntity().getId(), playVO.getEntity().getTextDesc(), 1, new OnRefreshUIListener.Stub() {
                                @Override
                                public void completed(long id, String error) throws RemoteException {
                                    Log.e("huanghui", "completed = " + id);
                                    MBroadcastApplication.setPlayID((long) -1);
                                    weekBroadcastAdapter.setIsClickAdapterPlayBtn(false);
                                    weekBroadcastAdapter.setAdapterNotify();
                                    EventBus.getDefault().post(new SimpleEvent(9823442));//通知dialog,刷新按钮状态
                                }

                                @Override
                                public void frushPlaying(long id) throws RemoteException {
                                    Log.e("huanghui", "frushPlaying = " + id);
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

    @Override
    public int getNowdayWeekIndex() {
        return 0;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        popupMenuItemList.clear();
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().removeAllStickyEvents();
    }

    @Override
    public void onDestroy() {
        super.onDestroyView();
    }

    private Handler handlerUI;
    OnRefreshUIListener onRefreshUIListener = new OnRefreshUIListener.Stub() {
        @Override
        public void completed(long id, String error) throws RemoteException {
            Log.e("huanghui", "completed = " + id);
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
            Log.e("huanghui", "frushPlaying = " + id);
            handlerUI = MBroadcastApplication.getFrushHandler();
            if (handlerUI != null) {
                Message message = Message.obtain();
                message.what = Constants.HandlerConstants.PLAY;
                message.obj = id;
                handlerUI.sendMessage(message);
            }
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int option = msg.arg1;
            listView.setSelection(option);
        }
    };
}
