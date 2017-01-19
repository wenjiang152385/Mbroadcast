package com.oraro.mbroadcast.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.listener.IDialogFragment;
import com.oraro.mbroadcast.logicService.GenerateService;
import com.oraro.mbroadcast.model.FlightInfoTemp;
import com.oraro.mbroadcast.model.HistoryFlightTempEdit;
import com.oraro.mbroadcast.model.PlayEntry;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.ui.fragment.SimpleDialogFragment;
import com.oraro.mbroadcast.utils.FieldSelectReadXml;
import com.oraro.mbroadcast.utils.SPUtils;
import com.oraro.mbroadcast.vo.FieldsSelectVO;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by dongyu  on 2016/11/2 0002.
 */

public class SettingsActivity extends BaseActivity {

    private ListView set_day_select_list_view;

    private ListView set_word_select_list_view;

    private Integer[] weeks = new Integer[]{R.string.fragment_monday,
            R.string.fragment_tuesday,
            R.string.fragment_wednesday,
            R.string.fragment_thursday,
            R.string.fragment_friday,
            R.string.fragment_saturday,
            R.string.fragment_sunday};

    private List<FieldsSelectVO> mList;

    private DaySelectAdapter daySelectAdapter;

    private FieldsSelectAdapter fieldsSelectAdapter;

    private Context context;

    private EditText et;

    private TextView tv;

    private int[] days = {0x0001, 0x0010, 0x0100, 0x1000, 0x10000, 0x100000, 0x1000000};

    private int total;

    private FieldSelectReadXml mFieldSelectReadXml;

    private int mInt;
    //必选字段的set集合
    private Set<String> set1;
    //可选字段的set集合
    private Set<String> set2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeader_left_btn();
        setHeader_textTitle(R.string.activity_settings_name);
    }

    @Override
    protected void initData(Bundle paramBundle) {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_settings;
    }

    private Subscription mSubscription;
    @Override
    protected void setHeader_right_btnOnClickListener() {
        super.setHeader_right_btnOnClickListener();

        try {
            SimpleDialogFragment simpleDialogFragment = new SimpleDialogFragment();
            simpleDialogFragment.show(getFragmentManager(), "simpleDialogFragment");
            simpleDialogFragment.setTitle("提示");
            simpleDialogFragment.setContent("确定需要提交设置吗？");
            simpleDialogFragment.setSubmit("确定");
            simpleDialogFragment.setOnButtonClickListener(new IDialogFragment() {
                @Override
                public void onDialogFragmentButtonClickListener() {
                    final DBManager dbManager = DBManager.getInstance(context);
                    Observable<List<PlayEntry>> observable = dbManager.queryAll1(dbManager.getPlayEntryDao(DBManager.READ_ONLY));
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
                                public void onNext(List<PlayEntry> list) {
                                    //设置播报次数
                                    String s = new String();
                                    if (TextUtils.isEmpty(et.getText().toString())) {
                                        s = "" + SPUtils.getBroadcastNumber(context);
                                    } else {
                                        s = et.getText().toString();
                                    }
                                    int i = 0;
                                    i = Integer.valueOf(s);
                                    SPUtils.setBroadcastNumber(context, i);
                                    Log.e("tiu", "SettingsActivity = " + SPUtils.getBroadcastNumber(context));
                                    for (int j = 0; j < list.size(); j++) {
                                        list.get(j).setTimes(i);
                                    }
                                    dbManager.rxUpdateList(list, dbManager.getPlayEntryDao(DBManager.READ_ONLY));
                                    //天数设置保存
                                    SPUtils.setPrefInt(context, "daySelect", total);
                                    //必选字段保存
                                    set1.add("0&" + mList.get(0).getKey() + "&" + mList.get(0).getValue());
                                    set1.add("1&" + mList.get(1).getKey() + "&" + mList.get(1).getValue());
                                    set1.add("2&" + mList.get(2).getKey() + "&" + mList.get(2).getValue());
                                    set1.add("3&" + mList.get(3).getKey() + "&" + mList.get(3).getValue());
                                    SPUtils.setPrefStringSet(context, "necessary", set1);
                                    //可选择点保存
                                    set2 = readSP(mList);
                                    if (set2 == null || set2.size() == 0) {
                                        SPUtils.setPrefBoolean(context, "property", true);
                                        SPUtils.setPrefBoolean(context, "delay", true);
                                        set2 = readSP(mList);
                                    } else if (set2.size() == 1) {
                                        SPUtils.setPrefBoolean(context, "delay", true);
                                        set2 = readSP(mList);
                                    }
                                    SPUtils.setPrefStringSet(context, "fieldsSelectSet", set2);
                                    EventBus.getDefault().postSticky(new SimpleEvent(Constants.SETTINGS_WEEK));
                                    finish();
                                }
                            });
                }
            });
        } catch (Exception e) {
            Toast.makeText(SettingsActivity.this, "格式不正确", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 可选字段按顺序放入set中
     *
     * @param mList
     * @return set
     */
    protected Set<String> readSP(List<FieldsSelectVO> mList) {
        Set<String> set = new LinkedHashSet<String>();
        int n = 4;
        boolean b4 = SPUtils.getPrefBoolean(SettingsActivity.this, "property", true);
        if (b4) {
            set.add(n + "&" + mList.get(4).getKey() + "&" + mList.get(4).getValue());
            n++;
        }
        boolean b5 = SPUtils.getPrefBoolean(SettingsActivity.this, "delay", true);
        if (b5) {
            if (n == 4) {
                set.add(n + "&" + mList.get(5).getKey() + "&" + mList.get(5).getValue());
                n++;
            } else if (n == 5) {
                set.add(n + "&" + mList.get(5).getKey() + "&" + mList.get(5).getValue());
            }
        }
        boolean b6 = SPUtils.getPrefBoolean(SettingsActivity.this, "origin", false);
        if (b6) {
            if (n == 4) {
                set.add(n + "&" + mList.get(6).getKey() + "&" + mList.get(6).getValue());
                n++;
            } else if (n == 5) {
                set.add(n + "&" + mList.get(6).getKey() + "&" + mList.get(6).getValue());
            }
        }
        boolean b7 = SPUtils.getPrefBoolean(SettingsActivity.this, "planeNumber", false);
        if (b7) {
            if (n == 4) {
                set.add(n + "&" + mList.get(7).getKey() + "&" + mList.get(7).getValue());
                n++;
            } else if (n == 5) {
                set.add(n + "&" + mList.get(7).getKey() + "&" + mList.get(7).getValue());
            }
        }
        boolean b8 = SPUtils.getPrefBoolean(SettingsActivity.this, "proxy", false);
        if (b8) {
            if (n == 4) {
                set.add(n + "&" + mList.get(8).getKey() + "&" + mList.get(8).getValue());
                n++;
            } else if (n == 5) {
                set.add(n + "&" + mList.get(8).getKey() + "&" + mList.get(8).getValue());
            }
        }
        boolean b9 = SPUtils.getPrefBoolean(SettingsActivity.this, "expectFly", false);
        if (b9) {
            if (n == 4) {
                set.add(n + "&" + mList.get(9).getKey() + "&" + mList.get(9).getValue());
                n++;
            } else if (n == 5) {
                set.add(n + "&" + mList.get(9).getKey() + "&" + mList.get(9).getValue());
            }
        }
        boolean b10 = SPUtils.getPrefBoolean(SettingsActivity.this, "planePlace", false);
        if (b10) {
            if (n == 4) {
                set.add(n + "&" + mList.get(10).getKey() + "&" + mList.get(10).getValue());
                n++;
            } else if (n == 5) {
                set.add(n + "&" + mList.get(10).getKey() + "&" + mList.get(10).getValue());
            }
        }
        boolean b11 = SPUtils.getPrefBoolean(SettingsActivity.this, "floorNumber", false);
        if (b11) {
            if (n == 4) {
                set.add(n + "&" + mList.get(11).getKey() + "&" + mList.get(11).getValue());
                n++;
            } else if (n == 5) {
                set.add(n + "&" + mList.get(11).getKey() + "&" + mList.get(11).getValue());
            }
        }
        boolean b12 = SPUtils.getPrefBoolean(SettingsActivity.this, "doorNumber", false);
        if (b12) {
            if (n == 4) {
                set.add(n + "&" + mList.get(12).getKey() + "&" + mList.get(12).getValue());
                n++;
            } else if (n == 5) {
                set.add(n + "&" + mList.get(12).getKey() + "&" + mList.get(12).getValue());
            }
        }
        return set;
    }

    @Override
    protected void initView() {
        setHeader_right_btn(R.string.activity_settings_ok);
        context = this;
        total = SPUtils.getPrefInt(context, "daySelect", 0x01111111);
        mFieldSelectReadXml = new FieldSelectReadXml(this, "fieldsSelect.xml");
        mList = mFieldSelectReadXml.readXML();
        set1 = new LinkedHashSet<String>();
        set2 = new LinkedHashSet<String>();
        et = (EditText) findViewById(R.id.settings_edtext);
        tv = (TextView) findViewById(R.id.settings_edtext_range);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString())) {
                    s = "" + SPUtils.getBroadcastNumber(context);
                }
                int i = Integer.valueOf(s.toString());
                if (i == 0 || i > 10) {
                    et.setText("");
                    tv.setVisibility(View.VISIBLE);
                } else {
                    tv.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et.setText("" + SPUtils.getBroadcastNumber(context));
        et.setSelection(et.getText().length());
        set_day_select_list_view = (ListView) findViewById(R.id.set_day_select_list_view);
        set_word_select_list_view = (ListView) findViewById(R.id.set_word_select_list_view);
        daySelectAdapter = new DaySelectAdapter();
        fieldsSelectAdapter = new FieldsSelectAdapter();
        set_day_select_list_view.setAdapter(daySelectAdapter);
        set_word_select_list_view.setAdapter(fieldsSelectAdapter);
        set_day_select_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ((total & days[position]) == days[position]) {
                    total = total & (~days[position]);
                } else {
                    total = total | days[position];
                }
                daySelectAdapter.notifyDataSetChanged();
            }
        });
        mInt = 2;
        set_word_select_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 4:
                        if (SPUtils.getPrefBoolean(SettingsActivity.this, "property", true)) {
                            SPUtils.setPrefBoolean(SettingsActivity.this, "property", false);
                            mInt--;
                            //set2.remove("4&"+mList.get(4).getKey()+"&"+mList.get(4).getValue());
                        } else {
                            if (mInt < 2) {
                                SPUtils.setPrefBoolean(SettingsActivity.this, "property", true);
                                mInt++;
                                //set2.add("4&"+mList.get(4).getKey()+"&"+mList.get(4).getValue());
                            }
                        }
                        fieldsSelectAdapter.notifyDataSetChanged();
                        break;
                    case 5:
                        if (SPUtils.getPrefBoolean(SettingsActivity.this, "delay", true)) {
                            SPUtils.setPrefBoolean(SettingsActivity.this, "delay", false);
                            mInt--;
                            //set2.remove(mList.get(5).getKey()+"&"+mList.get(5).getValue());
                        } else {
                            if (mInt < 2) {
                                SPUtils.setPrefBoolean(SettingsActivity.this, "delay", true);
                                mInt++;
                                //set2.add(mList.get(5).getKey()+"&"+mList.get(5).getValue());
                            }
                        }
                        fieldsSelectAdapter.notifyDataSetChanged();
                        break;
                    case 6:
                        if (SPUtils.getPrefBoolean(SettingsActivity.this, "origin", false)) {
                            SPUtils.setPrefBoolean(SettingsActivity.this, "origin", false);
                            mInt--;
                            //set2.remove(mList.get(6).getKey()+"&"+mList.get(6).getValue());
                        } else {
                            if (mInt < 2) {
                                SPUtils.setPrefBoolean(SettingsActivity.this, "origin", true);
                                mInt++;
                                //set2.add(mList.get(6).getKey()+"&"+mList.get(6).getValue());
                            }
                        }
                        fieldsSelectAdapter.notifyDataSetChanged();
                        break;
                    case 7:
                        if (SPUtils.getPrefBoolean(SettingsActivity.this, "planeNumber", false)) {
                            SPUtils.setPrefBoolean(SettingsActivity.this, "planeNumber", false);
                            mInt--;
                            //set2.remove(mList.get(7).getKey()+"&"+mList.get(7).getValue());
                        } else {
                            if (mInt < 2) {
                                SPUtils.setPrefBoolean(SettingsActivity.this, "planeNumber", true);
                                mInt++;
                                //set2.add(mList.get(7).getKey()+"&"+mList.get(7).getValue());
                            }
                        }
                        fieldsSelectAdapter.notifyDataSetChanged();
                        break;
                    case 8:
                        if (SPUtils.getPrefBoolean(SettingsActivity.this, "proxy", false)) {
                            SPUtils.setPrefBoolean(SettingsActivity.this, "proxy", false);
                            mInt--;
                            //set2.remove(mList.get(8).getKey()+"&"+mList.get(8).getValue());
                        } else {
                            if (mInt < 2) {
                                SPUtils.setPrefBoolean(SettingsActivity.this, "proxy", true);
                                mInt++;
                                //set2.add(mList.get(8).getKey()+"&"+mList.get(8).getValue());
                            }
                        }
                        fieldsSelectAdapter.notifyDataSetChanged();
                        break;
                    case 9:
                        if (SPUtils.getPrefBoolean(SettingsActivity.this, "expectFly", false)) {
                            SPUtils.setPrefBoolean(SettingsActivity.this, "expectFly", false);
                            mInt--;
                            //set2.remove(mList.get(9).getKey()+"&"+mList.get(9).getValue());
                        } else {
                            if (mInt < 2) {
                                SPUtils.setPrefBoolean(SettingsActivity.this, "expectFly", true);
                                mInt++;
                                //set2.add(mList.get(9).getKey()+"&"+mList.get(9).getValue());
                            }
                        }
                        fieldsSelectAdapter.notifyDataSetChanged();
                        break;
                    case 10:
                        if (SPUtils.getPrefBoolean(SettingsActivity.this, "planePlace", false)) {
                            SPUtils.setPrefBoolean(SettingsActivity.this, "planePlace", false);
                            mInt--;
                            //set2.remove(mList.get(10).getKey()+"&"+mList.get(10).getValue());
                        } else {
                            if (mInt < 2) {
                                SPUtils.setPrefBoolean(SettingsActivity.this, "planePlace", true);
                                mInt++;
                                //set2.add(mList.get(10).getKey()+"&"+mList.get(10).getValue());
                            }
                        }
                        fieldsSelectAdapter.notifyDataSetChanged();
                        break;
                    case 11:
                        if (SPUtils.getPrefBoolean(SettingsActivity.this, "floorNumber", false)) {
                            SPUtils.setPrefBoolean(SettingsActivity.this, "floorNumber", false);
                            mInt--;
                            //set2.remove(mList.get(11).getKey()+"&"+mList.get(11).getValue());
                        } else {
                            if (mInt < 2) {
                                SPUtils.setPrefBoolean(SettingsActivity.this, "floorNumber", true);
                                mInt++;
                                //set2.add(mList.get(11).getKey()+"&"+mList.get(11).getValue());
                            }
                        }
                        fieldsSelectAdapter.notifyDataSetChanged();
                        break;
                    case 12:
                        if (SPUtils.getPrefBoolean(SettingsActivity.this, "doorNumber", false)) {
                            SPUtils.setPrefBoolean(SettingsActivity.this, "doorNumber", false);
                            mInt--;
                            //set2.remove(mList.get(11).getKey()+"&"+mList.get(11).getValue());
                        } else {
                            if (mInt < 2) {
                                SPUtils.setPrefBoolean(SettingsActivity.this, "doorNumber", true);
                                mInt++;
                                //set2.add(mList.get(11).getKey()+"&"+mList.get(11).getValue());
                            }
                        }
                        fieldsSelectAdapter.notifyDataSetChanged();
                        break;
                }
            }
        });

    }

    /**
     * 天数设置的adapter
     */
    class DaySelectAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return weeks.length;
        }

        @Override
        public Object getItem(int position) {
            return weeks[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.settings_day_select_item_view, null);
            }
            ImageView img = (ImageView) convertView.findViewById(R.id.set_day_select_img);
            ImageView img_line = (ImageView) convertView.findViewById(R.id.set_day_select_img_line);
            TextView tv = (TextView) convertView.findViewById(R.id.set_day_select_tv);
            tv.setText(weeks[position]);
            img_line.setVisibility(View.VISIBLE);
            if ((total & days[position]) == days[position]) {
                img.setImageResource(R.drawable.common_checkbox_checked);
            } else {
                img.setImageResource(R.drawable.common_checkbox_normal);
            }
            if (position == 6) {
                img_line.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }
    }

    /**
     * 字段选择adapter
     */
    class FieldsSelectAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.settings_day_select_item_view, null);
            }
            ImageView img = (ImageView) convertView.findViewById(R.id.set_day_select_img);
            ImageView img_line = (ImageView) convertView.findViewById(R.id.set_day_select_img_line);
            TextView tv = (TextView) convertView.findViewById(R.id.set_day_select_tv);
            tv.setText("" + mList.get(position).getKey());
//            Log.e("tiu",position+":"+mList.get(position).getKey());
            img_line.setVisibility(View.VISIBLE);
            if (mList.get(position).isRequired()) {
                img.setImageResource(R.drawable.red_q);
            }
            switch (position) {
                case 4:
                    if (SPUtils.getPrefBoolean(SettingsActivity.this, "property", true)) {
                        img.setImageResource(R.drawable.common_checkbox_checked);
                    } else {
                        img.setImageResource(R.drawable.common_checkbox_normal);
                    }
                    break;
                case 5:
                    if (SPUtils.getPrefBoolean(SettingsActivity.this, "delay", true)) {
                        img.setImageResource(R.drawable.common_checkbox_checked);
                    } else {
                        img.setImageResource(R.drawable.common_checkbox_normal);
                    }
                    break;
                case 6:
                    if (SPUtils.getPrefBoolean(SettingsActivity.this, "origin", false)) {
                        img.setImageResource(R.drawable.common_checkbox_checked);
                    } else {
                        img.setImageResource(R.drawable.common_checkbox_normal);
                    }
                    break;
                case 7:
                    if (SPUtils.getPrefBoolean(SettingsActivity.this, "planeNumber", false)) {
                        img.setImageResource(R.drawable.common_checkbox_checked);
                    } else {
                        img.setImageResource(R.drawable.common_checkbox_normal);
                    }
                    break;
                case 8:
                    if (SPUtils.getPrefBoolean(SettingsActivity.this, "proxy", false)) {
                        img.setImageResource(R.drawable.common_checkbox_checked);
                    } else {
                        img.setImageResource(R.drawable.common_checkbox_normal);
                    }
                    break;
                case 9:
                    if (SPUtils.getPrefBoolean(SettingsActivity.this, "expectFly", false)) {
                        img.setImageResource(R.drawable.common_checkbox_checked);
                    } else {
                        img.setImageResource(R.drawable.common_checkbox_normal);
                    }
                    break;
                case 10:
                    if (SPUtils.getPrefBoolean(SettingsActivity.this, "planePlace", false)) {
                        img.setImageResource(R.drawable.common_checkbox_checked);
                    } else {
                        img.setImageResource(R.drawable.common_checkbox_normal);
                    }
                    break;
                case 11:
                    if (SPUtils.getPrefBoolean(SettingsActivity.this, "floorNumber", false)) {
                        img.setImageResource(R.drawable.common_checkbox_checked);
                    } else {
                        img.setImageResource(R.drawable.common_checkbox_normal);
                    }
                    break;
                case 12:
                    if (SPUtils.getPrefBoolean(SettingsActivity.this, "doorNumber", false)) {
                        img.setImageResource(R.drawable.common_checkbox_checked);
                    } else {
                        img.setImageResource(R.drawable.common_checkbox_normal);
                    }
                    img_line.setVisibility(View.INVISIBLE);
                    break;
            }
            return convertView;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mInt = 2;
        if(mSubscription != null){
            mSubscription.unsubscribe();
        }
    }
}
