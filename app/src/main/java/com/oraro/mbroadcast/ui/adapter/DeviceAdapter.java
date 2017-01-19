package com.oraro.mbroadcast.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.model.DeviceEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/29 0029.
 */
public class DeviceAdapter extends BaseAdapter {
    private Context mContext;
    private List<DeviceEntity> mList;
    private int mFlag;

    public DeviceAdapter(Context context, List<DeviceEntity> list,int flag) {
        mContext = context;
        mList = list;
        mFlag = flag;
    }

    public List<DeviceEntity> getList() {
        if (null == mList) {
            mList = new ArrayList<>();
        }
        return mList;
    }

    public void updataView(int position, ListView listView, boolean flag) {
        int visibleFirstPosi = listView.getFirstVisiblePosition();
        int visibleLastPosi = listView.getLastVisiblePosition();
        if (position >= visibleFirstPosi && position <= visibleLastPosi) {
            View view = listView.getChildAt(position - visibleFirstPosi);
            ViewHolder holder = (ViewHolder) view.getTag();

            if (flag) {
                holder.statusImg.setImageResource(R.mipmap.status_on);
            }else {
                holder.statusImg.setImageResource(R.mipmap.status_off);
            }
        }else {
            //TODO: 2016/10/9 0009
            /**
             *  更新数据库
             */
//                String txt = mPatterns[position];
//                txt = txt + text;
//                mPatterns[position] = txt;
        }
    }

    @Override
    public int getCount() {
        return null != mList ? mList.size() : -1;
    }

    @Override
    public Object getItem(int position) {
        return null != mList ? mList.get(position) : -1;
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
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.udp_list_item,null,false);
            viewHolder.connectedImg = (ImageView) convertView.findViewById(R.id.img_connected);
            viewHolder.ipText = (TextView) convertView.findViewById(R.id.text_ip);
            viewHolder.statusImg = (ImageView) convertView.findViewById(R.id.img_status);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (null != mList) {
            DeviceEntity deviceEntity = (DeviceEntity) getItem(position);
            viewHolder.ipText.setText(deviceEntity.getIp());
            viewHolder.connectedImg.setImageResource(R.mipmap.choose);
            viewHolder.statusImg.setImageResource(R.mipmap.status_off);
        }
        if (0 == mFlag) {
            viewHolder.connectedImg.setVisibility(View.VISIBLE);
            viewHolder.statusImg.setVisibility(View.INVISIBLE);
        }else if (1 == mFlag) {
            viewHolder.connectedImg.setVisibility(View.INVISIBLE);
            viewHolder.statusImg.setVisibility(View.INVISIBLE);
        }else if (2 == mFlag) {
            viewHolder.connectedImg.setVisibility(View.INVISIBLE);
            viewHolder.statusImg.setVisibility(View.VISIBLE);
        }
        return convertView;
    }



    class ViewHolder {
        ImageView connectedImg;
        TextView ipText;
        ImageView statusImg;
    }
}
