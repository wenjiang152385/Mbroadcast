package com.oraro.mbroadcast.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.model.FlightInfoTemp;

import java.util.List;

/**
 * Created by admin on 2016/11/17
 *
 * @author zmy
 */

public class BroadcastFlightInfoAdapter extends BaseAdapter {
    private List<FlightInfoTemp> flightList;
    private ViewHolder holder;
    private Context ctx;
    private boolean isKindlyReminder;
    private int mSelect = 0;

    public BroadcastFlightInfoAdapter(Context ctx, List<FlightInfoTemp> flightList) {
        this.ctx = ctx;
        this.flightList = flightList;
    }

    public void setFlightList(List<FlightInfoTemp> flightList) {
        this.flightList = flightList;
    }

    @Override
    public int getCount() {
        return flightList.size();
    }

    @Override
    public Object getItem(int position) {
        return flightList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = View.inflate(ctx, R.layout.lv_items_flight_info, null);
            holder = new ViewHolder();
            holder.ll = (LinearLayout) convertView.findViewById(R.id.ll_flightifo);
            holder.tvNum = (TextView) convertView.findViewById(R.id.tv_flightinfo_num);
            holder.tvStart = (TextView) convertView.findViewById(R.id.tv_flightinfo_start);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (isKindlyReminder) {// 温馨提示界面
            holder.ll.setBackgroundColor(Color.parseColor("#D8D8D8"));
            holder.tvNum.setTextColor(Color.parseColor("#868686"));
            holder.tvStart.setTextColor(Color.parseColor("#868686"));
        } else {
            if (mSelect == position) {
                holder.ll.setBackgroundColor(Color.parseColor("#f5a623"));
                holder.tvNum.setTextColor(Color.parseColor("#FCFCFC"));
                holder.tvStart.setTextColor(Color.parseColor("#FCFCFC"));
            } else {
                holder.ll.setBackgroundColor(Color.parseColor("#f4f4f4"));
                holder.tvNum.setTextColor(Color.parseColor("#868686"));
                holder.tvStart.setTextColor(Color.parseColor("#868686"));
            }
        }
        // 2016/8/22  根据传进来的航班信息集合（假定为flightInfoPageList）给左下角航班号和起始站赋值。。。。。。
        holder.tvNum.setText(flightList.get(position).getFlightNumber());
        holder.tvStart.setText(flightList.get(position).getDeparture());

        return convertView;
    }

    public void isKindlyReminder(boolean isKindlyReminder) {
        this.isKindlyReminder = isKindlyReminder;
        notifyDataSetChanged();
    }

    public void changeSelected(int position) { //刷新方法

        if (position != mSelect) {
            mSelect = position;
            notifyDataSetChanged();
        }
    }

    private class ViewHolder {
        TextView tvNum, tvStart;
        LinearLayout ll;
    }
}
