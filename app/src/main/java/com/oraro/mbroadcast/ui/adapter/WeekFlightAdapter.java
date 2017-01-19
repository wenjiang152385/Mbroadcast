package com.oraro.mbroadcast.ui.adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.listener.OnImgClickListener;
import com.oraro.mbroadcast.model.FlightInfoTemp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by dongyu on 2016/8/23 0023.
 */
public class WeekFlightAdapter extends BaseAdapter {
    private final String TAG = "WeekFlightAdapter";
    private Context mContext = null;
    private List<FlightInfoTemp> mFlightInfoTempData = null;
    private OnImgClickListener mSetOnImageClickListener;
    private int currentDelteOrder = 0;
    private float currentScale = 0;
    private int originHeight;
    private boolean hadGetOriginHeight;
    private boolean isNeedDelete = false;
    private boolean isDeleting = false;

    public WeekFlightAdapter(Context context, List<FlightInfoTemp> flightInfoTemps) {
        mContext = context;
        mFlightInfoTempData = flightInfoTemps;
    }

    public void setFlightInfoTempData(List<FlightInfoTemp> flightInfoTemps) {
        mFlightInfoTempData = flightInfoTemps;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (null != mFlightInfoTempData) {
            count = mFlightInfoTempData.size();
        }
        return count;
    }

    @Override
    public FlightInfoTemp getItem(int position) {
        FlightInfoTemp item = null;

        if (null != mFlightInfoTempData) {
            item = mFlightInfoTempData.get(position);
        }

        return item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            convertView = mInflater.inflate(R.layout.flight_listview_item, null);
            viewHolder.list_item_text_title__flight_number = (TextView) convertView.findViewById(R.id.list_item_text_title__flight_number);
            viewHolder.list_item_text_title_flightno = (TextView) convertView.findViewById(R.id.list_item_text_title_flightno);
            viewHolder.list_item_text_title__flight_type = (TextView) convertView.findViewById(R.id.list_item_text_title__flight_type);
            viewHolder.list_item_text_title_destination = (TextView) convertView.findViewById(R.id.list_item_text_title_destination);
            viewHolder.list_item_text_title_arrival_information = (TextView) convertView.findViewById(R.id.list_item_text_title_arrival_information);
            viewHolder.list_item_text_title_delay_information = (TextView) convertView.findViewById(R.id.list_item_text_title_delay_information);
            viewHolder.list_item_text_title_time = (TextView) convertView.findViewById(R.id.list_item_text_title_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        convertView.setBackground(mContext.getResources().getDrawable(R.drawable.list_item_bg));
        // set item values to the viewHolder:

        FlightInfoTemp flightInfoTemp = getItem(position);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

        if (null != flightInfoTemp) {
            viewHolder.list_item_text_title__flight_number.setText("            " + flightInfoTemp.getFlightNumber());
            viewHolder.list_item_text_title_flightno.setText(flightInfoTemp.getPlaneNumber());
            viewHolder.list_item_text_title__flight_type.setText(flightInfoTemp.getPlaneType());
            viewHolder.list_item_text_title_destination.setText(flightInfoTemp.getDeparture());
            viewHolder.list_item_text_title_arrival_information.setText(flightInfoTemp.getArrivalStation());
            viewHolder.list_item_text_title_delay_information.setText(flightInfoTemp.getDelayInfo());
            viewHolder.list_item_text_title_time.setText(simpleDateFormat.format(flightInfoTemp.getPlanToTakeOffDate()));
            if (flightInfoTemp.getIsDelay()) {
                viewHolder.list_item_text_title_delay_information.setText("延误");
            } else {
                viewHolder.list_item_text_title_delay_information.setText("未延误");
            }
        }

        return convertView;
    }

    public void collapseDeleteView(ListView listView, int position) {
        final View view;
        if (listView == null) {
            throw new RuntimeException("listView can not be null");
        }
        if (isDeleting)
            return;
        int first = listView.getFirstVisiblePosition();
        view = listView.getChildAt(position - first);
        isDeleting = true;
        isNeedDelete = true;
        hadGetOriginHeight = false;
        currentDelteOrder = position;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentScale = animation.getAnimatedFraction();
                if (isNeedDelete) {
                    doCollapse(view);
                }
            }
        });
        valueAnimator.setDuration(1000);
        valueAnimator.start();
    }

    public void updataView(int position, ListView listView, long flightTempId) {
        int visibleFirstPosi = listView.getFirstVisiblePosition();
        int visibleLastPosi = listView.getLastVisiblePosition();

        if (position >= visibleFirstPosi && position <= visibleLastPosi) {
            final View view;
            if (listView == null) {
                throw new RuntimeException("listView can not be null");
            }
            int first = listView.getFirstVisiblePosition();
            view = listView.getChildAt(position - first);
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            Log.e(TAG, "flightTempId = " + flightTempId);
            FlightInfoTemp flightInfoTemp = (FlightInfoTemp) DBManager.getInstance(MBroadcastApplication.getMyContext()).queryById(flightTempId, DBManager.getInstance(MBroadcastApplication.getMyContext()).getFlightInfoTempDao(DBManager.READ_ONLY));
            if (null == flightInfoTemp) {
                return;
            }
            Log.e(TAG, "flightTempId = " + flightInfoTemp.toString());
            for (int i = 0; i < mFlightInfoTempData.size(); i++) {
                if (mFlightInfoTempData.get(i).getId() == flightTempId) {
                    mFlightInfoTempData.remove(i);
                    mFlightInfoTempData.add(i, flightInfoTemp);
                }
            }
            SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm");
            viewHolder.list_item_text_title__flight_number.setText("            " + flightInfoTemp.getFlightNumber());
            viewHolder.list_item_text_title_flightno.setText(flightInfoTemp.getPlaneNumber());
            viewHolder.list_item_text_title__flight_type.setText(flightInfoTemp.getPlaneType());
            viewHolder.list_item_text_title_destination.setText(flightInfoTemp.getDeparture());
            viewHolder.list_item_text_title_arrival_information.setText(flightInfoTemp.getArrivalStation());
            viewHolder.list_item_text_title_delay_information.setText(flightInfoTemp.getDelayInfo());
            viewHolder.list_item_text_title_time.setText(sdfDate.format(flightInfoTemp.getPlanToTakeOffDate()));
//            if (flightInfoTemp.getIsDelay()) {
//                viewHolder.list_item_text_title_delay_information.setText("延误");
//            } else {
//                viewHolder.list_item_text_title_delay_information.setText("未延误");
//
//            }
        }
    }

    private void doCollapse(View view) {
        if (!hadGetOriginHeight) {
            originHeight = view.getHeight();
            hadGetOriginHeight = true;
        }
        view.getLayoutParams().height = (int) (originHeight - originHeight * currentScale);
        view.requestLayout();
        if (view.getLayoutParams().height == 0) {
            isNeedDelete = false;
            mFlightInfoTempData.remove(currentDelteOrder);
            notifyDataSetChanged();
            isDeleting = false;
        }
    }

    private static class ViewHolder {
        TextView list_item_text_title__flight_number;
        TextView list_item_text_title_flightno;
        TextView list_item_text_title__flight_type;
        TextView list_item_text_title_destination;
        TextView list_item_text_title_arrival_information;
        TextView list_item_text_title_delay_information;
        TextView list_item_text_title_time;
    }


}
