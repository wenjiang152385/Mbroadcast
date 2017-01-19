package com.oraro.mbroadcast.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.model.FlightInfoTemp;

/**
 * Created by Administrator on 2016/12/27 0027.
 */
public class RecycleAdapter extends BaseListAdapter<FlightInfoTemp> {

    public RecycleAdapter(Context context, int mode) {
        super(context, mode);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new RecycleViewHolder(mInflater.inflate(R.layout.lv_items_flight_info, parent, false));
    }


    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder h, int position) {
        RecycleViewHolder holder = (RecycleViewHolder) h;
        FlightInfoTemp item = items.get(position);
        holder.mTv_flightinfo_num.setText(item.getFlightNumber());
        holder.mTv_flightinfo_start.setText(item.getDeparture());
        holder.mTv_flightinfo_num.setTextColor(Color.parseColor("#868686"));
        holder.mTv_flightinfo_start.setTextColor(Color.parseColor("#868686"));
        holder.mLl_flightifo.setBackgroundColor(Color.parseColor("#f4f4f4"));
        if (mSelectPosition == position || item.getId() == mId) {
            holder.mTv_flightinfo_num.setTextColor(Color.parseColor("#FCFCFC"));
            holder.mTv_flightinfo_start.setTextColor(Color.parseColor("#FCFCFC"));
            holder.mLl_flightifo.setBackgroundColor(Color.parseColor("#f5a623"));
        }

    }

    public static final class RecycleViewHolder extends RecyclerView.ViewHolder {

        TextView mTv_flightinfo_num;
        TextView mTv_flightinfo_start;
        LinearLayout mLl_flightifo;

        public RecycleViewHolder(View itemView) {
            super(itemView);
            mTv_flightinfo_num = (TextView) itemView.findViewById(R.id.tv_flightinfo_num);
            mTv_flightinfo_start = (TextView) itemView.findViewById(R.id.tv_flightinfo_start);
            mLl_flightifo = (LinearLayout) itemView.findViewById(R.id.ll_flightifo);
        }
    }

}
