package com.oraro.mbroadcast.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.vo.PlayVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongyu on 2016/9/18 0018.
 */
public class ExcelAdapter extends BaseAdapter {
    private  final String TAG=getClass().getSimpleName();
    private Context mContext = null;
    private List<String> mStringList=new ArrayList<>();


    public ExcelAdapter(Context context, List<String> stringList)
    {
        mContext = context;
        mStringList = stringList;
    }

    public void setTextString( List<String> stringList)
    {
        mStringList = stringList;
    }
    @Override
    public Object getItem(int position) {
        String item = null;

        if (null != mStringList)
        {
            item = mStringList.get(position);
        }

        return item;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (null != mStringList)
        {
            count = mStringList.size();
        }
        return count;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if (null == convertView)
        {
            viewHolder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            convertView = mInflater.inflate(R.layout.activity_excel_listview_item, null);
            viewHolder. textView= (TextView) convertView.findViewById(R.id.textView);
            viewHolder. textView2= (TextView) convertView.findViewById(R.id.textView2);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
     String filePath=  (String) getItem(position);
        viewHolder.textView.setText("文件名");
        viewHolder.textView2.setText(filePath);


        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    private static class ViewHolder
    {
        TextView textView;
        TextView textView2;

    }
}
