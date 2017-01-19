package com.oraro.mbroadcast.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.model.FlightInfoTemp;
import com.oraro.mbroadcast.model.HistoryFlightTempEdit;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/9/23 0023.
 */
public class HistoryFlightAdapter extends BaseAdapter {

    private Context mContext;
    private List<HistoryFlightTempEdit> flights;
    private LayoutInflater mLayoutInflater;

    public HistoryFlightAdapter(Context context, List<HistoryFlightTempEdit> flights) {
        mContext = context;
        this.flights = flights;
    }

    public void updateList(List<HistoryFlightTempEdit> addList) {
        for (int i = 0; i < addList.size(); i++) {
            flights.add(addList.get(0));
        }
    }

    public String formatDate(Date date, int flag) {
        SimpleDateFormat simpleDateFormat = null;
        if (flag == 1) {
            simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
        } else if (flag == 2) {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        }else if(flag==3){
            simpleDateFormat = new SimpleDateFormat("HH:mm");
        }

        String dateStr = simpleDateFormat.format(date);
        return dateStr;
    }


    @Override
    public int getCount() {
        return null != flights ? flights.size() : -1;
    }

    @Override
    public Object getItem(int position) {
        return null != flights ? flights.get(position) : -1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = getLayoutInflater().inflate(R.layout.flight_list_item, null, false);
            viewHolder.numberText = (TextView) convertView.findViewById(R.id.number_text);
            viewHolder.statusText = (TextView) convertView.findViewById(R.id.status_text);
            viewHolder.departText = (TextView) convertView.findViewById(R.id.depart_text);
            viewHolder.arriveText = (TextView) convertView.findViewById(R.id.arrvive_text);
            viewHolder.delayText = (TextView) convertView.findViewById(R.id.delay_text);
            viewHolder.timeText = (TextView) convertView.findViewById(R.id.time_text);
            viewHolder.gateText = (TextView) convertView.findViewById(R.id.gate_text);


            viewHolder.numberText.setWidth(247);
            viewHolder.statusText.setWidth(271);
            viewHolder.departText.setWidth(272);
            viewHolder.arriveText.setWidth(272);
            viewHolder.delayText.setWidth(272);
            viewHolder.timeText.setWidth(272);
            viewHolder.gateText.setWidth(224);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (null != flights) {
            HistoryFlightTempEdit historyFlightTempEdit = ((HistoryFlightTempEdit) getItem(position));
            viewHolder.numberText.setText(historyFlightTempEdit.getFlightInfoTemp().getFlightNumber());
            viewHolder.statusText.setText(formatDate(historyFlightTempEdit.getEditDate(),1));
            viewHolder.departText.setText(historyFlightTempEdit.getFlightInfoTemp().getDeparture());
            viewHolder.arriveText.setText(historyFlightTempEdit.getFlightInfoTemp().getArrivalStation());
            viewHolder.delayText.setText(formatDate(historyFlightTempEdit.getFlightInfoTemp().getDate(),2));
            viewHolder.timeText.setText(formatDate(historyFlightTempEdit.getFlightInfoTemp().getPlanToTakeOffDate(),3));
            viewHolder.gateText.setText(historyFlightTempEdit.getFlightInfoTemp().getPlaneNumber());

        }
        return convertView;
    }

    private LayoutInflater getLayoutInflater() {
        return null == mLayoutInflater ? LayoutInflater.from(mContext) : mLayoutInflater;
    }

    class ViewHolder {
        TextView numberText;
        TextView statusText;
        TextView departText;
        TextView arriveText;
        TextView delayText;
        TextView timeText;
        TextView gateText;
    }
}
