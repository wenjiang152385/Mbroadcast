package com.oraro.mbroadcast.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.vo.PlayVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/8 0008.
 */
public class DelayAdapter extends BaseAdapter {
    public boolean flage=false;
    private Context mContext=null;
    private List<PlayVO> mPlayvoData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    public DelayAdapter(Context mContext) {
        this.mContext = mContext;
//        init();
    }

    // 初始化 设置所有checkbox都为未选择
//    public void init() {
//        isSelected = new HashMap<Integer, Boolean>();
//        for (int i = 0; i < mPlayvoData.size(); i++) {
//            isSelected.put(i, false);
//        }
//    }

    public void setData(List<PlayVO> list){
        mPlayvoData = list;
        Log.e("ccc", " DelayAdapter dataList size" + mPlayvoData.size());
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int count = 0;
        if (null != mPlayvoData) {
            count = mPlayvoData.size();
        }
        return count;
    }

    @Override
    public PlayVO getItem(int position) {
        PlayVO item=null;
        if (null != mPlayvoData) {
            item = mPlayvoData.get(position);
        }

        return item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if (null==convertView){
            viewHolder =new ViewHolder();
            mLayoutInflater = LayoutInflater.from(mContext);
            convertView= mLayoutInflater.inflate(R.layout.activity_yan_wu, null);
            viewHolder.et_hao = (TextView) convertView.findViewById(R.id.et);
            viewHolder.tv_bofang1 = (TextView) convertView.findViewById(R.id.tv1);
            viewHolder.tv_bofang = (TextView) convertView.findViewById(R.id.tv2);
            viewHolder.tv_qifei1 = (TextView)convertView.findViewById(R.id.tv3);
            viewHolder.tv_qifei = (TextView)convertView.findViewById(R.id.tv4);
            viewHolder.tv_mudi1 = (TextView)convertView.findViewById(R.id.tv5);
            viewHolder.tv_mudi = (TextView)convertView.findViewById(R.id.tv6);
            viewHolder.tv_leixing1 = (TextView)convertView.findViewById(R.id.tv7);
            viewHolder.tv_leixing = (TextView)convertView.findViewById(R.id.tv8);
            viewHolder.img_check = (ImageView) convertView.findViewById(R.id.iv1);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        final PlayVO playVo = getItem(position);
        viewHolder.tv_bofang1.setText("播放时间: ");
        Date date = playVo.getEntity().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        viewHolder.tv_bofang.setText(simpleDateFormat.format(date));
        viewHolder.tv_qifei1.setText("起飞地: ");
        viewHolder.tv_mudi1.setText("目的地: ");
        viewHolder.tv_leixing1.setText("类型: ");
        if(playVo.getEntity().getFlightInfoTemp() != null){
            viewHolder.et_hao.setText(playVo.getEntity().getFlightInfoTemp().getFlightNumber());
            viewHolder.tv_qifei.setText(playVo.getEntity().getFlightInfoTemp().getDeparture());
            viewHolder.tv_mudi.setText(playVo.getEntity().getFlightInfoTemp().getArrivalStation());
            viewHolder.tv_leixing.setText(playVo.getEntity().getFlightInfoTemp().getPlaneType());
        }else{
            //如果是自定义新增录音播报
            viewHolder.et_hao.setText("NA");
            viewHolder.tv_qifei.setText("NA");
            viewHolder.tv_mudi.setText("NA");
            viewHolder.tv_leixing.setText("NA");
        }
        // 根据isSelected来设置checkbox的显示状况
        if (flage) {
            viewHolder.img_check.setVisibility(View.GONE);
        } else {
            viewHolder.img_check.setVisibility(View.VISIBLE);
        }
        if(playVo.isChecked()){
            viewHolder.img_check.setImageResource(R.drawable.common_checkbox_checked);
        }else{
            viewHolder.img_check.setImageResource(R.drawable.common_checkbox_normal);
        }
        return convertView;
    }
    public class ViewHolder {
        TextView et_hao;
        TextView  tv_bofang;
        TextView  tv_bofang1;
        TextView tv_qifei;
        TextView tv_qifei1;
        TextView  tv_mudi;
        TextView  tv_mudi1;
        TextView tv_leixing;
        TextView tv_leixing1;
        public ImageView img_check;
    }
}
