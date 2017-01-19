package com.oraro.mbroadcast.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.listener.ISearchBarCallback;
import com.oraro.mbroadcast.model.FlightInfoTemp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weijiaqi on 2016/9/7 0007.
 */
public class SearchBar extends LinearLayout {
    private Context mContext;
    private EditText mEditText;
    private ListView mListView;
    private WindowManager windowManager;
    private WindowManager.LayoutParams wmParams;

    private TextAdapter adapter;
    private ISearchBarCallback callback;

    public SearchBar(Context context) {
        super(context);
        mContext = context;
    }


    public SearchBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        windowManager = (WindowManager) mContext.getSystemService(mContext.WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams();
        this.setOrientation(LinearLayout.VERTICAL);
        addEditText();
        setEditTextStyle();
        setListViewStyle();
        addListView();
        setListView();
    }

    public SearchBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public EditText getEditText() {
        return mEditText;
    }


    public ListView getListView() {
        return mListView;
    }

    public void removeWindowView() {
        windowManager.removeView(mListView);
    }

    public void hideListView() {
        mListView.setVisibility(View.GONE);
    }

    public void setSearchBarCallback(ISearchBarCallback callback) {
        this.callback = callback;
    }

    public void upDateListView(List<FlightInfoTemp> list) {
//        List<String> data = new ArrayList<>();
//        data.add("11111111111111");
//        data.add("22222222222222");
//        data.add("33333333333333");
//        data.add("44444444444444");
//        data.add("55555555555555");
//        data.add("66666666666666");
//        data.add("77777777777777");
//        data.add("88888888888888");
        adapter.setData(list);
        adapter.notifyDataSetChanged();
    }


    private void addEditText() {
        mEditText = new EditText(mContext);
        mEditText.addTextChangedListener(mTextWatcher);
        addView(mEditText);
        observerView(mEditText);
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (s.length() > 0) {
                mListView.setVisibility(View.VISIBLE);
                updateListView(-1,400, -1, -1);
                callback.setChangeEditText(s.toString());
            } else {
                if (null != mListView) {
                    mListView.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void addListView() {
        mListView = new ListView(mContext);
        mListView.setBackgroundColor(Color.LTGRAY);
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.START | Gravity.TOP;
        wmParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wmParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        windowManager.addView(mListView, wmParams);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
    }

    private void updateListView(int width,int height, int x, int y) {
        if (-1 != width) {
            wmParams.width = width;
        }

        if (-1 != height) {
            wmParams.height = height;
        }
        if (-1 != x) {
            wmParams.x = x;
        }
        if (-1 != y) {
            wmParams.y = y;
        }
        windowManager.updateViewLayout(mListView, wmParams);
    }


    private void setEditTextStyle() {
        mEditText.setBackgroundResource(R.drawable.bg_search_bar);
        mEditText.setPadding(5, 5, 5, 5);
        mEditText.setHintTextColor(Color.parseColor("#ffffff"));
        mEditText.setHint("请输入航班号");
        mEditText.setTextSize(16);
        mEditText.setSingleLine(true);
        mEditText.setWidth(45);

        Drawable drawableLeft = getResources().getDrawable(R.mipmap.icon_search);
        drawableLeft.setBounds(0, 0, drawableLeft.getMinimumWidth(), drawableLeft.getMinimumHeight());
        mEditText.setCompoundDrawables(drawableLeft, null, null, null);

    }


    private void setListViewStyle() {
        if (null != mListView) {
            setViewLayoutParams(mListView, ViewGroup.LayoutParams.MATCH_PARENT, 400);

        }

    }


    private void observerView(final View view) {
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];
                int width = view.getWidth();
                int height = view.getHeight();
                updateListView(width,-1, x, y);
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
    }


    private void setListView() {
        adapter = new TextAdapter(mContext);
        adapter.notifyDataSetChanged();
        mListView.setAdapter(adapter);
    }


    private void setViewLayoutParams(View view, int width, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
    }

    class TextAdapter extends BaseAdapter {
        private Context mContext;
        private List<FlightInfoTemp> mList;

        private TextAdapter(Context context) {
            mContext = context;
        }


        private void setData(List<FlightInfoTemp> list) {
            mList = list;
        }


        @Override
        public int getCount() {
            int count = 0;
            if (null != mList) {
                count = mList.size();
            }
            return count;
        }

        @Override
        public Object getItem(int position) {
            String itemText = "";
            if (null != mList) {
                itemText = mList.get(position).getFlightNumber();
            }
            return itemText;
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
            if (null != mList) {
                viewHolder.textView.setText(text);
            }

            return convertView;
        }

        class ViewHolder {
            TextView textView;
        }
    }

}

