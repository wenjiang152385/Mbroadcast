package com.oraro.mbroadcast.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.listener.DelayDialogListener;
import com.oraro.mbroadcast.logicService.DataService;
import com.oraro.mbroadcast.model.FlightInfoTemp;
import com.oraro.mbroadcast.model.PlayEntry;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.ui.adapter.DelayAdapter;
import com.oraro.mbroadcast.ui.fragment.DelayFragment;
import com.oraro.mbroadcast.vo.PlayVO;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/8 0008.
 */
public class DelayActivity extends Activity implements View.OnClickListener {
    private ListView listView;
    private Date beginTime;
    private Date endTime;
    private TextView header_left_btn;
    private Button quanxuan;
    private DelayAdapter delayAdapter;
    private Button bt_yanwu;
    private DelayAdapter.ViewHolder holder;
    private WheelView hourWheelView, minuteWheelView;
    private CheckBox checkBox1;
    private Context context;
    private DBManager dbManager;
    private List<PlayVO> flightInfoTemps;
    //private ArrayList<PlayVO> mSelectedPlayVoDatas = new ArrayList<PlayVO>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yanwu);
        context = this;
        bt_yanwu = (Button) findViewById(R.id.bt_yanwu);
        header_left_btn = (TextView) findViewById(R.id.header_left_btn);
        header_left_btn.setVisibility(View.VISIBLE);
        checkBox1 = (CheckBox) findViewById(R.id.checkbox1);
        checkBox1.setChecked(true);
        quanxuan = (Button) findViewById(R.id.quanxuan);
        listView = (ListView) findViewById(R.id.listview1);
        flightInfoTemps = getAllData(0);
       // delayAdapter = new DelayAdapter(this, flightInfoTemps);
        listView.setAdapter(delayAdapter);
        bt_yanwu.setOnClickListener(this);
        quanxuan.setOnClickListener(this);
        header_left_btn.setOnClickListener(this);
        dbManager = DBManager.getInstance(context);
        initWheel2();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                holder = (DelayAdapter.ViewHolder) view.getTag();
                //每次点击item都对checkbox的状态进行改变

            }
        });
    }

    private void cancelAll() {
        for (int i = 0; i < flightInfoTemps.size(); i++) {
            //DelayAdapter.isSelected.put(i, false);
            delayAdapter.notifyDataSetChanged();
        }
    }

    private void selectAll() {
        for (int i = 0; i < flightInfoTemps.size(); i++) {
            //DelayAdapter.isSelected.put(i, true);
            delayAdapter.notifyDataSetChanged();
        }
    }

    public List<PlayVO> getAllData(int day) {
        Date nowDate = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.add(calendar.DATE, day);//把日期往后增加一天.整数往后推,负数往前移动
        nowDate = calendar.getTime(); //这个时间就是日期往后推一天的结果
//        nowDate.setHours(0);
//        nowDate.setMinutes(0);
//        nowDate.setSeconds(0);
        beginTime = new Date(nowDate.getYear(), nowDate.getMonth(), nowDate.getDate(), nowDate.getHours(), nowDate.getMinutes(), 0);
        endTime = new Date(nowDate.getYear(), nowDate.getMonth(), nowDate.getDate(), 23, 59, 59);
        DataService s = new DataService();
        List<PlayVO> dataList = new ArrayList<PlayVO>();
        dataList = s.getPlayVO(beginTime, endTime);
      //  Log.e("BaseBroadcastFragment", " dataList size" + dataList.size());
        return dataList;
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

    private void initWheel2() {
        //时
        hourWheelView = (WheelView) findViewById(R.id.hour_wheelview);
        hourWheelView.setWheelAdapter(new ArrayWheelAdapter(this));
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
        minuteWheelView = (WheelView) findViewById(R.id.minute_wheelview);
        minuteWheelView.setWheelAdapter(new ArrayWheelAdapter(this));
        minuteWheelView.setSkin(WheelView.Skin.Holo);
        minuteWheelView.setLoop(true);
        minuteWheelView.setWheelData(createMinutes());
        minuteWheelView.setStyle(style);
        minuteWheelView.setExtraText("分", Color.parseColor("#0288ce"), 40, 70);
    }

    int hours;
    int minutes;
    private List<PlayEntry> mplayVO;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.header_left_btn:
                finish();
                break;
            //全选
            case R.id.quanxuan:
                if ("全选".equals(quanxuan.getText())) {
                    selectAll();
                    quanxuan.setText("全不选");
                } else {
                    cancelAll();
                    quanxuan.setText("全选");
                }
                break;
            //延误确定
            case R.id.bt_yanwu:
                boolean tag = true;
                hours = hourWheelView.getCurrentPosition();
                minutes = minuteWheelView.getCurrentPosition();
                mplayVO = new ArrayList<PlayEntry>();
//               for (Map.Entry<Integer, Boolean> entry : DelayAdapter.isSelected.entrySet()) {
//                    if (entry.getValue()) {
//                        PlayEntry entity = flightInfoTemps.get(entry.getKey()).getEntity();
//                        if (mplayVO != null) {
//                            mplayVO.add(entity);
//                        }
//                        tag = false;
//                    }
//
//
//                }
                if (tag) {
                    Toast.makeText(DelayActivity.this, "请选择延误航班", Toast.LENGTH_SHORT).show();
                    return;
                }
                //showDialog();
                if ("全选".equals(quanxuan.getText())) {
                    showDialog();
                } else {
                    showDialog1();

                }
                break;
            default:
                break;
        }

    }

    private void showDialog1() {
        DelayFragment delayFragment=new DelayFragment();
        delayFragment.setTitle("提示");
        delayFragment.setSubmit("延误");
        delayFragment.setmCancle("不延误");
        delayFragment.show(getFragmentManager(), "delayFragment1");
        delayFragment.setContent(SpannableStringBuilder.valueOf("您所选中的航班，后续未播报部分已经批量延误" + hours + "小时" + minutes + "分钟，是否确认?"));
         delayFragment.setOnButtonClickListener(new DelayDialogListener() {
             boolean isIsfrist = false;
             @Override
             public void onDelaySumbitListener() {
                 if (!isIsfrist) {
                     new Thread(new Runnable() {
                         @Override
                         public void run() {
                             quanxuan(mplayVO);
                         }
                     }).start();
                     EventBus.getDefault().postSticky(new SimpleEvent(Constants.Analytic_Cmpletion_Notice));
                     finish();
                    isIsfrist=!isIsfrist;
                 }

             }

             @Override
             public void onDelayCancleListener() {
                 if (!isIsfrist) {
                     finish();
                     isIsfrist=!isIsfrist;
                 }
             }
         });


    }

    private void quanxuan(List<PlayEntry> m) {
        for (int j = 0; j < m.size(); j++) {
            PlayEntry mplayE = m.get(j);
            String number = mplayE.getFlightInfoTemp().getFlightNumber();
            FlightInfoTemp temp = mplayE.getFlightInfoTemp();
            Date date = mplayE.getTime();
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            temp.setIsDelay(true);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, hours);
            calendar.add(Calendar.MINUTE, minutes);
            mplayE.setTime(calendar.getTime());
           // Log.e("jw", "onDelaySumbitListener change time = " + sf.format(mplayE.getTime()));
            dbManager.update(mplayE, dbManager.getPlayEntryDao(DBManager.WRITE_ONLY));
            dbManager.update(temp, dbManager.getFlightInfoTempDao(DBManager.WRITE_ONLY));
        }


    }

    private List<PlayEntry> playEntrys = new ArrayList<PlayEntry>();

    private void checklist() {
        for (PlayEntry mplayVOs : mplayVO) {
            if (playEntrys.size() > 0) {
                for (int i = 0; i <playEntrys.size() ; i++) {
                      String mEntry= playEntrys.get(i).getFlightInfoTemp().getFlightNumber();
                    if (!mEntry.equals(mplayVOs.getFlightInfoTemp().getFlightNumber())) {
                        playEntrys.add(mplayVOs);
                    }
                }
            } else {
                playEntrys.add(mplayVOs);
            }
        }
    }

    private void shunyan() {
        checklist();
        DataService ds = new DataService();
        for (PlayEntry pes : playEntrys) {
            List entryFlightNumber = ds.getPlayEntryFlightNumber(pes);
            quanxuan(entryFlightNumber);
        }
    }

    private void showDialog() {
        final DelayFragment delayFragment = new DelayFragment();
        delayFragment.setTitle("提示");
        delayFragment.setContent(SpannableStringBuilder.valueOf("您所选中的航班，后续未播报部分已经批量延误" + hours + "小时" + minutes + "分钟，是否确认?"));
        delayFragment.setSubmit("顺延");
        delayFragment.setmCancle("不顺延");
        delayFragment.show(getFragmentManager(), "delayFragment");
        delayFragment.setOnButtonClickListener(new DelayDialogListener() {
            boolean isIsfrist = false;

            //顺延 未播报延迟
            @Override
            public void onDelaySumbitListener() {
                if (!isIsfrist) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            shunyan();
                        }
                    }).start();

                    EventBus.getDefault().postSticky(new SimpleEvent(Constants.Analytic_Cmpletion_Notice));
                    finish();
                    isIsfrist = !isIsfrist;
                }

            }

            //不顺延 未播报延迟
            @Override
            public void onDelayCancleListener() {
                if (!isIsfrist) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            quanxuan(mplayVO);
                        }
                    }).start();
                    EventBus.getDefault().postSticky(new SimpleEvent(Constants.Analytic_Cmpletion_Notice));
                    finish();
                    isIsfrist = !isIsfrist;
                }
            }

        });
    }

}


