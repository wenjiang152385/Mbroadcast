package com.oraro.mbroadcast.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.oraro.mbroadcast.ui.activity.EditActivity;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.model.ModelEntity;
import com.oraro.mbroadcast.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class TTSFragment extends Fragment implements View.OnClickListener {
    private String TAG = "zmy";
    private TextView tv_mould_tts;
    private ListView lv_mould_tts;
    private MyMouldAdapter mouldAdapter;
    private MyPopMouldAdapter popAdapter;
    private int mSelect = 0;
    private PopupWindow popWindow;
    private EditActivity ctx;
    private ListView listView;
    private LinearLayout ll_mould_icon;

    private List<String> ttsMouldTitleList;
    private List<String> ttsMouldNameIdList;
    private String selectMouldName;// 所选模板的名字

    private String ttsSelectMouldTitleId;
    private ArrayList<Parcelable> modelDataList;

    private TTSFragmentCallBack ttsFragmentCallBack;
    private String selectMouldItemId;
    private List<ModelEntity.ItemEntity> selectItemEntityList;
    private String selectTitle;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ctx = ((EditActivity) activity);
        if (!(activity instanceof TTSFragmentCallBack)) {
            throw new IllegalStateException("TTSFragmentCallBack所在的Activity必须实现TitlesListFragmentCallBack接口");
        }
        ttsFragmentCallBack = (TTSFragmentCallBack) activity;

    }


    private void getDataFromActivity() {
        Bundle bundle = getArguments();

        // 2016/8/23 从budle中取出数据
        modelDataList = bundle.getParcelableArrayList("modelData");
        int xmlKey = bundle.getInt("xmlKey");
        ttsMouldTitleList = new ArrayList<>();
        ttsMouldNameIdList = new ArrayList<>();

        for (int i = 0; i < modelDataList.size(); i++) {
            ModelEntity modelEntity = (ModelEntity) modelDataList.get(i);
            String modelId = modelEntity.getModelId();
            String title = modelEntity.getTitle();
            ttsMouldTitleList.add(title);
            ttsMouldNameIdList.add(modelId);
        }

        if (xmlKey == 0) {// 表示点击“新增”进来的数据
            mouldAdapter = new MyMouldAdapter(ctx, ((ModelEntity) modelDataList.get(0)).getItemEntity());
            // TODO: 2016/9/18 有待后期注意的问题
            ttsSelectMouldTitleId = ttsMouldNameIdList.get(0);
            tv_mould_tts.setText(ttsMouldTitleList.get(0));

        } else {// 表示点击“编辑”进来的数据
            // TODO: 2016/10/14 在此先把下拉箭头隐藏掉，后期当点击编辑进来TTS模板的一级目录多的时候再让下拉箭头显示
            ll_mould_icon.setVisibility(View.GONE);
            // 2016/9/6 在此要处理首次进来时定位问题
            // 第1步 得到模板的item的集合
            for (int i = 0; i < modelDataList.size(); i++) {
                ModelEntity modelEntity = (ModelEntity) modelDataList.get(i);
                List<ModelEntity.ItemEntity> itemEntityList = modelEntity.getItemEntity();
                for (int j = 0; j < itemEntityList.size(); j++) {
                    String id = itemEntityList.get(j).getId();
                    int id1 = Integer.parseInt(id);
                    if (xmlKey == id1) {
                        selectItemEntityList = modelEntity.getItemEntity();
                        selectTitle = modelEntity.getTitle();
                        ttsSelectMouldTitleId = modelEntity.getModelId();
                    }
                }
            }
            // 第2步 得到进来时选中的item的位置，即给mselect赋值
            for (int k = 0; k < selectItemEntityList.size(); k++) {
                String id = selectItemEntityList.get(k).getId();
                int id1 = Integer.parseInt(id);
                if (xmlKey == id1) {
                    mSelect = selectItemEntityList.indexOf(selectItemEntityList.get(k));
                }
            }
            // 第3步 创建模板item的adapter
            mouldAdapter = new MyMouldAdapter(ctx,selectItemEntityList);
            // 第4步 定位模板popwindow的显示
            tv_mould_tts.setText(selectTitle);
        }


    }

    //要显示的view
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_tts, null);

        initView(view);
        initListener();
        return view;
    }

    private void initView(View view) {
        tv_mould_tts = (TextView) view.findViewById(R.id.tv_mould_tts);
        ll_mould_icon = (LinearLayout) view.findViewById(R.id.ll_mould_icon);
        lv_mould_tts = (ListView) view.findViewById(R.id.lv_mould_tts);
        listView = new ListView(ctx);

        popAdapter = new MyPopMouldAdapter();
        getDataFromActivity();
    }

    public interface TTSFragmentCallBack {
        void onItemSelected(String itemId);
    }

    private void initListener() {
        lv_mould_tts.setAdapter(mouldAdapter);
        lv_mould_tts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mouldAdapter.changeSelected(position);
                // 2016/8/23 在此把所选中的item的ID传给父activity
                for (int i = 0; i < modelDataList.size(); i++) {
                    if (ttsSelectMouldTitleId.equals(((ModelEntity) modelDataList.get(i)).getModelId())) {
                        selectMouldItemId = ((ModelEntity) modelDataList.get(i)).getItemEntity().get(position).getId();
                    }
                }
                ttsFragmentCallBack.onItemSelected(selectMouldItemId);
            }
        });
        ll_mould_icon.setOnClickListener(this);

        listView.setAdapter(popAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                selectMouldName = ttsMouldTitleList.get(position);
                tv_mould_tts.setText(selectMouldName);

                // 关闭popWindow
                popWindow.dismiss();

                // 2016/8/24 根据pop点击的模板名称，切换到对应的listview
                switchMouldAdapter(position);
            }
        });
    }

    private void switchMouldAdapter(int position) {

        mSelect = 0;

        ttsSelectMouldTitleId = ttsMouldNameIdList.get(position);
        for (int i = 0; i < modelDataList.size(); i++) {
            if (ttsSelectMouldTitleId.equals(((ModelEntity) modelDataList.get(i)).getModelId())) {
                mouldAdapter = new MyMouldAdapter(ctx, ((ModelEntity) modelDataList.get(i)).getItemEntity());
                lv_mould_tts.setAdapter(mouldAdapter);

                String selectMouldItemId1 = ((ModelEntity) modelDataList.get(i)).getItemEntity().get(mSelect).getId();
                ttsFragmentCallBack.onItemSelected(selectMouldItemId1);

                mouldAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_mould_icon:
                if (popWindow == null) {
                    popWindow = new PopupWindow(ctx);
                    popWindow.setWidth(tv_mould_tts.getWidth()); // 与输入框等宽
                    popWindow.setHeight(130); // 高度200
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
        }
    }

    class MyMouldAdapter extends BaseAdapter {
        private List<ModelEntity.ItemEntity> list;
        private Context context;

        public MyMouldAdapter(Context context, List<ModelEntity.ItemEntity> list) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
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
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(getActivity(), R.layout.lv_items_mould_info, null);
                holder.tv = (TextView) convertView.findViewById(R.id.tv_mould_item);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv.setText(list.get(position).getName());
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
            return ttsMouldTitleList.size();
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
            holder.tv.setText(ttsMouldTitleList.get(position));
            return convertView;
        }
    }

    private class PopViewHolder {
        TextView tv;
    }
}
