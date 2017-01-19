package com.oraro.mbroadcast.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.oraro.mbroadcast.model.PlayEntry;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.ui.activity.MainActivity;
import com.oraro.mbroadcast.utils.CustomFragmentManager;
import com.oraro.mbroadcast.utils.FieldSelectReadXml;
import com.oraro.mbroadcast.utils.LogUtils;
import com.oraro.mbroadcast.utils.SPUtils;
import com.oraro.mbroadcast.vo.FieldsSelectVO;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2016/11/28 0028.
 *
 * @author jiang wen[佛祖保佑 永无BUG]
 */
public class SettingFragment extends Fragment {
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
    private List<FieldsSelectVO> mNoRequiredDefaultList;

    private DaySelectAdapter daySelectAdapter;

    private FieldsSelectAdapter fieldsSelectAdapter;

    private EditText et;

    private TextView tv;

    private int[] days = {0x0001,0x0010,0x0100,0x1000,0x10000,0x100000,0x1000000};

    private int total;

    private FieldSelectReadXml mFieldSelectReadXml;

    private int mInt;
    //必选字段的set集合
    private Set<String> set1;
    //可选字段的set集合
    private Set<String> set2;
    private TextView header_left_btn;
    private MainActivity mainActivity;
    private CustomFragmentManager customFragmentManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_settings,null);
        initView(view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        mainActivity = (MainActivity) activity;
        super.onAttach(activity);
    }

    private void initView(View view) {
        header_left_btn = (TextView)view.findViewById(R.id.header_left_btn);
        header_left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customFragmentManager.finishFragment();
            }
        });
        TextView header_text= (TextView)view.findViewById(R.id.header_text);
        header_text.setText("播报设置");
        customFragmentManager = CustomFragmentManager.getInstance(mainActivity);
        total = SPUtils.getPrefInt(mainActivity,"daySelect",0x01111111);
        mFieldSelectReadXml = new FieldSelectReadXml(mainActivity,"fieldsSelect.xml");
        mList = mFieldSelectReadXml.readXML();
        mNoRequiredDefaultList = new ArrayList<>();

        //根据SharePreference中保存的字段勾选设置，更新fieldsSelect.xml解析的数据集合
        for (FieldsSelectVO fieldsSelectVO : mList){
            if(getFieldsSetFromPreference(fieldsSelectVO.getValue(),fieldsSelectVO.isNoRequiredSet())){
                fieldsSelectVO.setNoRequiredSet(true);
                mInt++;
            }else{
                fieldsSelectVO.setNoRequiredSet(false);
            }
            if(fieldsSelectVO.isNoRequiredDefault()){
                mNoRequiredDefaultList.add(fieldsSelectVO);
            }
        }

        set1 = new LinkedHashSet<String>();
        set2 = new LinkedHashSet<String>();
        et = (EditText)view.findViewById(R.id.settings_edtext);
        tv = (TextView)view.findViewById(R.id.settings_edtext_range);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString())){
                    s = ""+ SPUtils.getBroadcastNumber(mainActivity);
                }
                int i = Integer.valueOf(s.toString());
                if (i==0||i>10){
                    et.setText("");
                    tv.setVisibility(View.VISIBLE);
                }else{
                    tv.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et.setText("" + SPUtils.getBroadcastNumber(mainActivity));
        et.setSelection(et.getText().length());
        set_day_select_list_view = (ListView)view.findViewById(R.id.set_day_select_list_view);
        set_word_select_list_view = (ListView)view.findViewById(R.id.set_word_select_list_view);
        daySelectAdapter = new DaySelectAdapter();
        fieldsSelectAdapter = new FieldsSelectAdapter();
        set_day_select_list_view.setAdapter(daySelectAdapter);
        set_word_select_list_view.setAdapter(fieldsSelectAdapter);
        set_day_select_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ( (total & days[position])== days[position]){
                    total = total & ( ~days[position]);
                }else{
                    total = total | days[position];
                }
                daySelectAdapter.notifyDataSetChanged();
            }
        });
        set_word_select_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mList.get(position).isRequired()){
                    return;
                }else{
                    if(mList.get(position).isNoRequiredSet()){
                        mList.get(position).setNoRequiredSet(false);
                        mInt--;
                    }else{
                        if (mInt < 2){
                            mList.get(position).setNoRequiredSet(true);
                            mInt++;
                        }else{
                            Toast.makeText(mainActivity, "最多勾选两个可选字段!", Toast.LENGTH_LONG).show();
                        }
                    }
                    fieldsSelectAdapter.notifyDataSetChanged();
                }
            }
        });
        TextView header_right_btn= (TextView)view.findViewById(R.id.header_right_btn);
        header_right_btn.setVisibility(View.VISIBLE);
        header_right_btn.setBackground(null);
        header_right_btn.setText("完成");
        header_right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
            final SimpleDialogFragment simpleDialogFragment = new SimpleDialogFragment();
            simpleDialogFragment.show(mainActivity.getFragmentManager(), "simpleDialogFragment");
            simpleDialogFragment.setTitle("提示");
            simpleDialogFragment.setContent("确定需要提交设置吗？");
            simpleDialogFragment.setSubmit("确定");
            simpleDialogFragment.setOnButtonClickListener(new IDialogFragment() {
                @Override
                public void onDialogFragmentButtonClickListener() {
                    //设置播报次数
                    String s = new String();
                    if (TextUtils.isEmpty(et.getText().toString())){
                        s = ""+ SPUtils.getBroadcastNumber(mainActivity);
                    }else{
                        s = et.getText().toString();
                    }
                    int count = 0;
                    try {
                        count = Integer.valueOf(s);
                    } catch (Exception e) {
                        Toast.makeText(mainActivity, "格式不正确", Toast.LENGTH_LONG).show();
                        return;
                    }
                    SPUtils.setBroadcastNumber(mainActivity, count);
                    Log.e("tiu","SettingsActivity = " + SPUtils.getBroadcastNumber(mainActivity));
                    DBManager dbManager = DBManager.getInstance(mainActivity);
                    List<PlayEntry> list = dbManager.queryAll(dbManager.getPlayEntryDao(DBManager.READ_ONLY));
                    for ( int j = 0;j<list.size();j++){
                        list.get(j).setTimes(count);
                    }
                    dbManager.updateList(list,dbManager.getPlayEntryDao(DBManager.READ_ONLY));

                    //天数设置保存
                    SPUtils.setPrefInt(mainActivity,"daySelect",total);

                    //必选字段保存
                    int index = 0;
                    for(int i = 0; i < mList.size(); i++){
                        FieldsSelectVO fieldsSelectVO = mList.get(i);
                        if(fieldsSelectVO.isRequired()){
                            set1.add((index++) + "&" + fieldsSelectVO.getKey() + "&" + fieldsSelectVO.getValue());
                        }else {
                            boolean isToSave = false;
                            if(0 == mInt){//一个可选字段都没有勾选
                                if(fieldsSelectVO.isNoRequiredDefault()){
                                    isToSave = true;
                                }
                            }else if(1 == mInt){//只勾选了一个可选字段
                                if(fieldsSelectVO.isNoRequiredSet()){
                                    if(fieldsSelectVO.isNoRequiredDefault()){
                                        for(FieldsSelectVO fv : mNoRequiredDefaultList){
                                            set2.add((index++) + "&" + fv.getKey() + "&" + fv.getValue());
                                            setFieldsSetFromPreference(fv.getValue(),true);
                                        }
                                    }else{
                                        set2.add((index++) + "&" + mNoRequiredDefaultList.get(0).getKey() + "&" + mNoRequiredDefaultList.get(0).getValue());
                                        setFieldsSetFromPreference(mNoRequiredDefaultList.get(0).getValue(),true);
                                        setFieldsSetFromPreference(mNoRequiredDefaultList.get(1).getValue(),false);

                                        set2.add((index++) + "&" + fieldsSelectVO.getKey() + "&" + fieldsSelectVO.getValue());
                                        setFieldsSetFromPreference(fieldsSelectVO.getValue(),true);
                                    }
                                    continue;
                                }
                            }else if(2 == mInt){//勾选了两个字段
                                if(fieldsSelectVO.isNoRequiredSet()){
                                    isToSave = true;
                                }
                            }else{
                                Toast.makeText(mainActivity, "最多勾选两个可选字段!", Toast.LENGTH_LONG).show();
                                return;
                            }
                            if(isToSave){
                                set2.add((index++) + "&" + fieldsSelectVO.getKey() + "&" + fieldsSelectVO.getValue());
                            }
                            if(mInt != 1){
                                setFieldsSetFromPreference(fieldsSelectVO.getValue(),isToSave);
                            }else{
                                if(!fieldsSelectVO.isNoRequiredDefault()){
                                    setFieldsSetFromPreference(fieldsSelectVO.getValue(),fieldsSelectVO.isNoRequiredSet());
                                }
                            }
                        }
                    }

                    SPUtils.setPrefStringSet(mainActivity,"necessary",set1);
                    SPUtils.setPrefStringSet(mainActivity,"fieldsSelectSet",set2);

                    simpleDialogFragment.dismiss();

                    EventBus.getDefault().postSticky(new SimpleEvent(Constants.SETTINGS_WEEK));
                    customFragmentManager.finishFragment();
                }
            });
            }
        });
    }

    private boolean getFieldsSetFromPreference(String name,boolean defaultValue){
        return SPUtils.getPrefBoolean(mainActivity, name, defaultValue);
    }

    private void setFieldsSetFromPreference(String name,boolean value){
        SPUtils.setPrefBoolean(mainActivity, name, value);
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
                convertView = LayoutInflater.from(mainActivity).inflate(R.layout.settings_day_select_item_view, null);
            }
            ImageView img = (ImageView) convertView.findViewById(R.id.set_day_select_img);
            ImageView img_line = (ImageView) convertView.findViewById(R.id.set_day_select_img_line);
            TextView tv = (TextView) convertView.findViewById(R.id.set_day_select_tv);
            tv.setText(weeks[position]);
            img_line.setVisibility(View.VISIBLE);
            if ( (total & days[position]) == days[position]){
                img.setImageResource(R.drawable.common_checkbox_checked);
            } else {
                img.setImageResource(R.drawable.common_checkbox_normal);
            }
            if (position == 6){
                img_line.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }
    }

    /**
     * 字段选择adapter
     */
    class FieldsSelectAdapter extends BaseAdapter{

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
                convertView = LayoutInflater.from(mainActivity).inflate(R.layout.settings_day_select_item_view, null);
            }
            ImageView img = (ImageView) convertView.findViewById(R.id.set_day_select_img);
            ImageView img_line = (ImageView) convertView.findViewById(R.id.set_day_select_img_line);
            TextView tv = (TextView) convertView.findViewById(R.id.set_day_select_tv);
            tv.setText( ""+mList.get(position).getKey());
//            Log.e("tiu",position+":"+mList.get(position).getKey());
            img_line.setVisibility(View.VISIBLE);
            if (mList.get(position).isRequired()){
                img.setImageResource(R.drawable.red_q);
            }else{
                if(mList.get(position).isNoRequiredSet()){
                    img.setImageResource(R.drawable.common_checkbox_checked);
                }else {
                    img.setImageResource(R.drawable.common_checkbox_normal);
                }
            }

            if(position == getCount() - 1){
                img_line.setVisibility(View.INVISIBLE);
            }else{
                img_line.setVisibility(View.VISIBLE);
            }

            return convertView;
        }
    }

    /**
     * 可选字段按顺序放入set中
     * @param mList
     * @return set
     */
    protected Set<String> readSP(List<FieldsSelectVO> mList){
        Set<String> set = new LinkedHashSet<String>();
        int n =4;
        boolean b4 = SPUtils.getPrefBoolean(mainActivity,"property" , true);
        if (b4){
            set.add(n+"&"+mList.get(4).getKey()+"&"+mList.get(4).getValue());
            n++;
        }
        boolean b5 = SPUtils.getPrefBoolean(mainActivity, "delay", true);
        if (b5){
            if (n==4){
                set.add(n+"&"+mList.get(5).getKey()+"&"+mList.get(5).getValue());
                n++;
            }else if (n==5){
                set.add(n+"&"+mList.get(5).getKey()+"&"+mList.get(5).getValue());
            }
        }
        boolean b6 = SPUtils.getPrefBoolean(mainActivity, "origin", false);
        if (b6){
            if (n==4){
                set.add(n+"&"+mList.get(6).getKey()+"&"+mList.get(6).getValue());
                n++;
            }else if (n==5){
                set.add(n+"&"+mList.get(6).getKey()+"&"+mList.get(6).getValue());
            }
        }
        boolean b7 = SPUtils.getPrefBoolean(mainActivity, "planeNumber", false);
        if (b7){
            if (n==4){
                set.add(n+"&"+mList.get(7).getKey()+"&"+mList.get(7).getValue());
                n++;
            }else if (n==5){
                set.add(n+"&"+mList.get(7).getKey()+"&"+mList.get(7).getValue());
            }
        }
        boolean b8 = SPUtils.getPrefBoolean(mainActivity, "proxy", false);
        if (b8){
            if (n==4){
                set.add(n+"&"+mList.get(8).getKey()+"&"+mList.get(8).getValue());
                n++;
            }else if (n==5){
                set.add(n+"&"+mList.get(8).getKey()+"&"+mList.get(8).getValue());
            }
        }
        boolean b9 = SPUtils.getPrefBoolean(mainActivity, "expectFly", false);
        if (b9){
            if (n==4){
                set.add(n+"&"+mList.get(9).getKey()+"&"+mList.get(9).getValue());
                n++;
            }else if (n==5){
                set.add(n+"&"+mList.get(9).getKey()+"&"+mList.get(9).getValue());
            }
        }
        boolean b10 = SPUtils.getPrefBoolean(mainActivity, "planePlace", false);
        if (b10){
            if (n==4){
                set.add(n+"&"+mList.get(10).getKey()+"&"+mList.get(10).getValue());
                n++;
            }else if (n==5){
                set.add(n+"&"+mList.get(10).getKey()+"&"+mList.get(10).getValue());
            }
        }
        boolean b11 = SPUtils.getPrefBoolean(mainActivity, "floorNumber", false);
        if (b11){
            if (n==4){
                set.add(n+"&"+mList.get(11).getKey()+"&"+mList.get(11).getValue());
                n++;
            }else if (n==5){
                set.add(n+"&"+mList.get(11).getKey()+"&"+mList.get(11).getValue());
            }
        }
        boolean b12 = SPUtils.getPrefBoolean(mainActivity,"doorNumber" , false);
        if (b12){
            if (n==4){
                set.add(n+"&"+mList.get(12).getKey()+"&"+mList.get(12).getValue());
                n++;
            }else if (n==5){
                set.add(n+"&"+mList.get(12).getKey()+"&"+mList.get(12).getValue());
            }
        }
        return set;
    }
}
