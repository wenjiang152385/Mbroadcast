package com.oraro.mbroadcast.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.oraro.mbroadcast.R;

import java.util.List;

/**
 * Created by admin on 2016/11/17
 *
 * @author zmy
 */

public class BroadcastDatePopWindowAdapter extends BaseAdapter {
    private Context ctx;
    private List<String> replaceList;
    public BroadcastDatePopWindowAdapter(Context ctx, List<String> replaceList) {
        this.ctx = ctx;
        this.replaceList = replaceList;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
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
            convertView = View.inflate(ctx, R.layout.lv_items_mould_info, null);
            holder.tv = (TextView) convertView.findViewById(R.id.tv_mould_item);
            convertView.setTag(holder);
        } else {
            holder = (PopViewHolder) convertView.getTag();
        }
        holder.tv.setText(replaceList.get(position % replaceList.size()));
        return convertView;
    }

    private class PopViewHolder {
        TextView tv;
    }
}


