package com.oraro.mbroadcast.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.oraro.mbroadcast.R;

/**
 * Created by admin on 2016/11/23
 *
 * @author zmy
 */

public class UrgentPopAdapter extends BaseAdapter {
    private String[] ttsMouldTitleArray;
    private Context context;

    public UrgentPopAdapter( Context context,String[] ttsMouldTitleArray) {
        this.ttsMouldTitleArray = ttsMouldTitleArray;
        this.context = context;
    }

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
            convertView = View.inflate(context, R.layout.lv_items_mould_info, null);
            holder.tv = (TextView) convertView.findViewById(R.id.tv_mould_item);

            convertView.setTag(holder);
        } else {
            holder = (PopViewHolder) convertView.getTag();
        }
        holder.tv.setText(ttsMouldTitleArray[position]);
        return convertView;
    }

    class PopViewHolder {
        TextView tv;
    }
}
