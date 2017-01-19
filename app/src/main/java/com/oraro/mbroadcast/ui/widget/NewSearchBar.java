package com.oraro.mbroadcast.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.listener.ISearchBarCallback;
import com.oraro.mbroadcast.model.FlightInfo;
import com.oraro.mbroadcast.model.FlightInfoTemp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Administrator on 2016/9/18 0018.
 */
public class NewSearchBar extends AutoCompleteTextView {

    private TextAdapter adapter;
    private ISearchBarCallback callback;
    private Context mContext;

    public NewSearchBar(Context context) {
        super(context);
    }

    public NewSearchBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setStyle();
        adapter = new TextAdapter(context);
        adapter.notifyDataSetChanged();
        this.setAdapter(adapter);
        this.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != callback)
                    callback.setItemInfo(adapter.getItemInfo(position), position);
            }
        });
    }

    public void setSearchBarCallback(ISearchBarCallback callback) {
        this.callback = callback;
    }

    public NewSearchBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void setStyle() {
        this.isPopupShowing();
        this.setThreshold(0);
        this.setBackgroundResource(R.drawable.bg_search_bar);
        this.setPadding(5, 5, 5, 5);
        this.setHintTextColor(Color.parseColor("#ffffff"));
        this.setHint("请输入航班号");
        this.setTextSize(16);
        this.setSingleLine(true);
        this.setWidth(45);
        this.setDropDownHeight(400);
        this.addTextChangedListener(mTextWatcher);
        this.setOnDismissListener(mOnDismissListener);
        Drawable drawableLeft = getResources().getDrawable(R.mipmap.icon_search);
        drawableLeft.setBounds(0, 0, drawableLeft.getMinimumWidth(), drawableLeft.getMinimumHeight());
        this.setCompoundDrawables(drawableLeft, null, null, null);
    }

    public void upDateListView(List<FlightInfoTemp> list) {
//        ArrayList<String> data = new ArrayList<>();
//        for (int i = 0; i < 25; i++) {
//            data.add("11111111111111");
//            data.add("22222222222222");
//            data.add("33333333333333");
//            data.add("44444444444444");
//            data.add("55555555555555");
//            data.add("66666666666666");
//            data.add("77777777777777");
//            data.add("88888888888888");
//        }
        adapter.setData(list);
        adapter.notifyDataSetChanged();
    }

    private OnDismissListener mOnDismissListener = new OnDismissListener() {
        @Override
        public void onDismiss() {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    };

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() > 0) {
                if (null != callback)
                    callback.setChangeEditText(s.toString());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    class TextAdapter extends BaseAdapter implements Filterable {
        private Context mContext;
        private ArrayFilter mFilter;
        private List<FlightInfoTemp> mOriginalValues;//所有的Item
        private List<FlightInfoTemp> mObjects;

        private TextAdapter(Context context) {
            mContext = context;
        }


        private void setData(List<FlightInfoTemp> originalValues) {
            mOriginalValues = originalValues;
        }


        @Override
        public int getCount() {
            int count = 0;
            if (null != mObjects) {
                count = mObjects.size();
            }
            return count;
        }

        @Override
        public Object getItem(int position) {
            String itemText = "";
            if (null != mObjects) {
                itemText = mObjects.get(position).getFlightNumber();
            }
            return itemText;
        }

        public FlightInfoTemp getItemInfo(int position) {
            FlightInfoTemp flightInfoTemp = null;
            if (null != mObjects) {
                flightInfoTemp = mObjects.get(position);
            }
            return flightInfoTemp;
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
                convertView = mInflater.inflate(R.layout.list_item, null);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.text_view);
                viewHolder.textView.setPadding(14, 0, 0, 0);
                viewHolder.textView.setTextSize(23);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String text = (String) getItem(position);
            if (null != mObjects) {
                viewHolder.textView.setText(text);
            }

            return convertView;
        }

        @Override
        public Filter getFilter() {
            if (null == mFilter) {
                mFilter = new ArrayFilter();
            }
            return mFilter;
        }

        class ViewHolder {
            TextView textView;
        }

        private class ArrayFilter extends Filter {

            @Override
            protected FilterResults performFiltering(CharSequence prefix) {
                // TODO Auto-generated method stub
                FilterResults results = new FilterResults();
                results.values = mOriginalValues;
                results.count = mOriginalValues.size();
                return results;

            }

            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                // TODO Auto-generated method stub
                mObjects = (List<FlightInfoTemp>) results.values;
                notifyDataSetChanged();
            }

        }
    }
}
