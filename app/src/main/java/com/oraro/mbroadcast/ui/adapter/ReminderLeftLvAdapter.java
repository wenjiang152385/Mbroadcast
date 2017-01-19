package com.oraro.mbroadcast.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oraro.mbroadcast.R;

import static com.oraro.mbroadcast.R.id.imageView;
import static com.oraro.mbroadcast.R.id.iv;

/**
 * Created by admin on 2016/11/22
 *
 * @author zmy
 */

public class ReminderLeftLvAdapter extends BaseAdapter {
    private Context context;
    private String[] array;
    private int mSelect = 0;

    public ReminderLeftLvAdapter(Context context, String[] array) {
        this.context = context;
        this.array = array;
    }

    @Override
    public int getCount() {
        return array.length;
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
        convertView = View.inflate(context, R.layout.reminder_left_lv, null);
        RelativeLayout rl = (RelativeLayout) convertView.findViewById(R.id.rl_lv);
        TextView tv = (TextView) convertView.findViewById(R.id.tv);
        ImageView iv = (ImageView) convertView.findViewById(R.id.iv);
        tv.setText(array[position]);
        if (mSelect == position) {
            rl.setBackgroundColor(Color.parseColor("#f5a623"));
            tv.setTextColor(Color.parseColor("#FCFCFC"));
            iv.setBackgroundResource(R.mipmap.reminder_arrow2);
        } else {
            rl.setBackgroundColor(Color.parseColor("#f4f4f4"));
            tv.setTextColor(Color.parseColor("#868686"));
            iv.setBackgroundResource(R.mipmap.reminder_arrow);
        }
        return convertView;
    }

    public void changeSelected(int position) { //刷新方法
        if (position != mSelect) {
            mSelect = position;
            notifyDataSetChanged();
        }
    }

}
