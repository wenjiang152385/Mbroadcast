package com.oraro.mbroadcast.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.ui.activity.MainActivity;
import com.oraro.mbroadcast.utils.InputUtils;
import com.oraro.mbroadcast.utils.LogUtils;
import com.oraro.mbroadcast.utils.UIUtils;

public class TTSUrgentFragment extends Fragment implements View.OnClickListener {
    private String TAG = "zmy";
    private TextView tv_mould_tts;
    private ListView lv_mould_tts;
    private MyMouldAdapter mouldAdapter;
    private MyPopMouldAdapter popAdapter;
    private int mSelect = 0;
    private PopupWindow popWindow;
    private Context ctx;
    private ListView listView;
    private LinearLayout ll_mould_icon;
    private String selectMouldName;// 所选模板的名字
    private RelativeLayout rl;

    private int mTitleIndex = 1;
    private int mContentIndex = 1;
    private TTSUrgentFragmentCallBack ttsUrgentFragmentCallBack;
    private String[] ttsUrgentMouldArray1;
    private String[] ttsUrgentMouldArray2;
    private String[] ttsUrgentMouldArray3;
    private String[] ttsUrgentMouldArray4;
    private String[] ttsMouldTitleArray;
    private UrgentBroadcastFragment fragment;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ctx = getActivity();
//        if (!(activity instanceof TTSUrgentFragmentCallBack)) {
//            throw new IllegalStateException("TTSFragmentCallBack所在的Activity必须实现TitlesListFragmentCallBack接口");
//        }
        // ttsUrgentFragmentCallBack = (TTSUrgentFragmentCallBack) activity;
    }


    //要显示的view
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_tts, null);

        initView(view);
        initData();
        initListener();
        return view;
    }

    private void initView(View view) {
        rl = (RelativeLayout) view.findViewById(R.id.rl);
        ll_mould_icon = (LinearLayout) view.findViewById(R.id.ll_mould_icon);
        tv_mould_tts = (TextView) view.findViewById(R.id.tv_mould_tts);
        lv_mould_tts = (ListView) view.findViewById(R.id.lv_mould_tts);
        listView = new ListView(ctx);
    }

    private void initData() {
        ttsUrgentMouldArray1 = UIUtils.getStringArray(R.array.mouldArray1);
        ttsUrgentMouldArray2 = UIUtils.getStringArray(R.array.mouldArray2);
        ttsUrgentMouldArray3 = UIUtils.getStringArray(R.array.mouldArray3);
        ttsUrgentMouldArray4 = UIUtils.getStringArray(R.array.mouldArray4);
        ttsMouldTitleArray = UIUtils.getStringArray(R.array.ttsMouldTitleArray);

        popAdapter = new MyPopMouldAdapter();
        mouldAdapter = new MyMouldAdapter(ctx, ttsUrgentMouldArray1);
        tv_mould_tts.setText(ttsMouldTitleArray[0]);
    }

    public void setTTSUrgentFragmentCallBack(TTSUrgentFragmentCallBack callBack) {
        ttsUrgentFragmentCallBack = callBack;
    }

    public interface TTSUrgentFragmentCallBack {
        void onItemSelected(String itemId);
    }

    private void initListener() {
        rl.setClickable(true);
        rl.setOnClickListener(this);
        lv_mould_tts.setAdapter(mouldAdapter);
        lv_mould_tts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mouldAdapter.changeSelected(position);
                mContentIndex = position + 1;
                if (ttsUrgentFragmentCallBack != null)
                    ttsUrgentFragmentCallBack.onItemSelected((String) mouldAdapter.getItem(position));

                InputUtils.hideInput(getActivity());
            }
        });
        ll_mould_icon.setOnClickListener(this);

        listView.setAdapter(popAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                InputUtils.hideInput(getActivity());
                selectMouldName = ttsMouldTitleArray[position];
                tv_mould_tts.setText(selectMouldName);

                // 关闭popWindow
                popWindow.dismiss();

                // TODO: 2016/8/24 根据pop点击的模板名称，切换到对应的listview
                switchMouldAdapter(position);
            }
        });
    }

    public String getSelectedType() {
        return "" + mTitleIndex + "-" + mContentIndex;
    }

    private void switchMouldAdapter(int position) {
        switch (position + 1) {
            case 1:
                mouldAdapter.setDataList(ttsUrgentMouldArray1);
                break;
            case 2:
                mouldAdapter.setDataList(ttsUrgentMouldArray2);
                break;
            case 3:
                mouldAdapter.setDataList(ttsUrgentMouldArray3);
                break;
            case 4:
                mouldAdapter.setDataList(ttsUrgentMouldArray4);
                break;
//            case 4:
//                mouldAdapter.setDataList(ttsUrgentMouldNameList5);
//                break;
//            case 5:
//                mouldAdapter.setDataList(ttsUrgentMouldNameList6);
//                break;
//            case 6:
//                mouldAdapter.setDataList(ttsUrgentMouldNameList7);
//                break;
        }
        mouldAdapter.changeSelected(-1);
        lv_mould_tts.setSelection(0);
        mContentIndex = 1;
        mTitleIndex = position + 1;
        if (ttsUrgentFragmentCallBack != null)
            ttsUrgentFragmentCallBack.onItemSelected((String) mouldAdapter.getItem(0));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_mould_icon:
                if (popWindow == null) {
                    popWindow = new PopupWindow(ctx);
                    popWindow.setWidth(tv_mould_tts.getWidth()); // 与输入框等宽
                    popWindow.setHeight(200); // 高度200
                    popWindow.setContentView(listView);
                    popWindow.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.bg_edit));
                    popWindow.setFocusable(true); // 设置为，可以获得焦点
                }
                if (popWindow.isShowing()) {
                    popWindow.dismiss();
                } else {
                    popWindow.showAsDropDown(tv_mould_tts);
                }
                break;
            case R.id.rl:
                InputUtils.hideInput(getActivity());
                break;
        }
    }

    class MyMouldAdapter extends BaseAdapter {
        //        private List<String> list;
        private String[] array;
        private Context context;

        public MyMouldAdapter(Context context, String[] array) {
            this.array = array;
            this.context = context;
        }

        private void setDataList(String[] array) {
            this.array = array;

        }

        @Override
        public int getCount() {
            return array.length;
        }

        @Override
        public Object getItem(int position) {

            return array[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(getActivity(), R.layout.lv_items_mould_info, null);
                holder.tv = (TextView) convertView.findViewById(R.id.tv_mould_item);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv.setText(array[position]);
            LinearLayout ll = (LinearLayout) convertView.findViewById(R.id.ll_mould_item);

            if (mSelect == position) {
                holder.tv.setTextColor(Color.parseColor("#FCFCFC"));
                ll.setBackgroundColor(Color.parseColor("#f5a623"));
            } else {
                holder.tv.setTextColor(Color.parseColor("#868686"));
                ll.setBackgroundColor(Color.parseColor("#f4f4f4"));

            }
            return convertView;
        }

        public void changeSelected(int positon) { //刷新方法
            if (positon != mSelect) {
                if (positon == -1) {
                    positon = 0;
                }
                mSelect = positon;
                notifyDataSetChanged();
            }
        }
    }

    private class ViewHolder {
        TextView tv;
    }

    class MyPopMouldAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return ttsMouldTitleArray.length;
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
                convertView = View.inflate(getActivity(), R.layout.lv_items_mould_info, null);
                holder.tv = (TextView) convertView.findViewById(R.id.tv_mould_item);

                convertView.setTag(holder);
            } else {
                holder = (PopViewHolder) convertView.getTag();
            }
            holder.tv.setText(ttsMouldTitleArray[position]);
            return convertView;
        }
    }

    private class PopViewHolder {
        TextView tv;
    }
}
