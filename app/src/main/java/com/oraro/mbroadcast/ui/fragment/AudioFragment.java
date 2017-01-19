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
import android.widget.TextView;

import com.oraro.mbroadcast.R;

import java.util.ArrayList;
import java.util.List;


public class AudioFragment extends Fragment {
    private ListView lv_mould_video;
    private List<String> videoList;
    private MyAdapter adapter;
    private int mSelect = 0;
    private OnEditTextEnable onEditTextEnable;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_audio, null);
        initView(view);

        return view;
    }

    private void initView(View view) {
        lv_mould_video = (ListView) view.findViewById(R.id.lv_mould_video);
        videoList = new ArrayList<>();
        videoList.add(getString(R.string.audio_fragment_self_define));

        adapter = new MyAdapter();
        lv_mould_video.setAdapter(adapter);
        lv_mould_video.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.changeSelected(position);
                // 2016/8/26 把mSelect的值传递给父activity
                onEditTextEnable.onEditTextEnable(position);
            }
        });
    }

    /**
     * 自定义监听
     */
    public interface OnEditTextEnable{
        void onEditTextEnable(int selectVideoItemPosition);
    }
    public void setOnEditTextEnable(OnEditTextEnable onEditTextEnable){
        this.onEditTextEnable = onEditTextEnable;
    }
    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return videoList.size();
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
            holder.tv.setText(videoList.get(position));
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

}
