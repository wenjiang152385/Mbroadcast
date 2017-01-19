package com.oraro.mbroadcast.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oraro.mbroadcast.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/11/22
 *
 * @author zmy
 */

public class ReminderExpandableAdapter extends BaseExpandableListAdapter {
    private TextView tv;
    private LinearLayout ll_expand_subcategory;
    private int epdItemPosition;
    private String[] category = new String[]{"温馨提示", "紧急广播"};

    private Context ctx;
    private List<String> firstSubCategory = new ArrayList<>();
    private List<String> secondSubCategory = new ArrayList<>();

    public ReminderExpandableAdapter(Context ctx, List<String> firstSubCategory, List<String> secondSubCategory) {
        this.ctx = ctx;
        this.firstSubCategory = firstSubCategory;
        this.secondSubCategory = secondSubCategory;
    }

    public void setSecondSubCategory(List<String> secondSubCategory) {
        this.secondSubCategory = secondSubCategory;
        notifyDataSetChanged();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (groupPosition == 0) {
            return firstSubCategory.size();
        } else {
            return secondSubCategory.size();
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (groupPosition == 0) {
            return firstSubCategory.get(childPosition);
        } else {
            return secondSubCategory.get(childPosition);
        }
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(ctx).inflate(R.layout.kindly_reminder_fragment_expandable_subcategory, null);
        ll_expand_subcategory = (LinearLayout) convertView.findViewById(R.id.ll_expand_subcategory);
        TextView textView = (TextView) convertView.findViewById(R.id.tv);
        if (groupPosition == 0) {
            textView.setText(firstSubCategory.get(childPosition));
        } else {
            textView.setText(secondSubCategory.get(childPosition));
        }
        if (epdItemPosition == childPosition) {
            ll_expand_subcategory.setBackgroundColor(Color.parseColor("#f5a623"));
            textView.setTextColor(Color.parseColor("#FCFCFC"));
        } else {
            ll_expand_subcategory.setBackgroundColor(Color.parseColor("#f4f4f4"));
            textView.setTextColor(Color.parseColor("#868686"));

        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public int getGroupCount() {
        return category.length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return category[groupPosition];
    }


    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }


    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ExpandableViewHolder vh;
        if (null == convertView) {
            convertView = LayoutInflater.from(ctx).inflate(R.layout.kindly_reminder_fragment_expandable_category, null);
            vh = new ExpandableViewHolder();
            vh.iv = (ImageView) convertView.findViewById(R.id.expand_iv);
            vh.tv = (TextView) convertView.findViewById(R.id.expand_tv);
            convertView.setTag(vh);
        } else {
            vh = (ExpandableViewHolder) convertView.getTag();
        }
        vh.tv.setText(category[groupPosition]);

        if (isExpanded) {
            vh.iv.setImageResource(R.drawable.arrow1);
        } else {
            vh.iv.setImageResource(R.drawable.arrow);
        }
        return convertView;
    }

    public void changeSelected(int position) {
        if (position != epdItemPosition) {
            epdItemPosition = position;
            notifyDataSetChanged();
        }
    }

    private class ExpandableViewHolder {
        TextView tv;
        ImageView iv;
    }

}



