package com.oraro.mbroadcast.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.model.InterCutData;
import com.oraro.mbroadcast.service.IMyAidlInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/26 0026.
 */
public class PromptAdapter extends BaseAdapter {
    private List<InterCutData> mList;
    private Context mContext;
    private Map<Integer, InterCutData> checkMap = new HashMap<>();
    private int mCurrentPosition;
    private EditText mCurrentEditText;
    private boolean mIsAddNew = true;
    private boolean mIsCanEdit = false;
    private int mEditPosition = -1;
    private int touchedPosition = -1;


    public List<InterCutData> getInterCutDataList() {
        return mList;
    }

    public PromptAdapter(Context context, List<InterCutData> list) {
        mList = list;
        mContext = context;
    }

    public boolean isCanAdd() {
        boolean flag = mIsAddNew && mEditPosition == -1;
        return flag;
    }

    public void setEditPosition(int editPosition) {
        mEditPosition = editPosition;
        notifyDataSetChanged();
    }

    public Map<Integer, InterCutData> getChecked() {
        return checkMap;
    }

    @Override
    public int getCount() {

        return null != mList ? mList.size() : -1;
    }

    @Override
    public InterCutData getItem(int position) {
        return null != mList ? mList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (null == viewHolder) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.prompt_item, null, false);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.prompt_text);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.radio_button);
            viewHolder.editText = (EditText) convertView.findViewById(R.id.edit_text);
            viewHolder.imgOk = (ImageButton) convertView.findViewById(R.id.check_ok);
            viewHolder.imgNo = (ImageButton) convertView.findViewById(R.id.check_no);
            final ViewHolder finalViewHolder = viewHolder;
            viewHolder.editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mList.get(position).setText(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            viewHolder.editText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        touchedPosition = position;
                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                    }
                    return false;
                }
            });

            viewHolder.imgOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIsAddNew = true;
                    String text = finalViewHolder.editText.getText().toString();
                    if (!"".equals(text.trim()) && null != text) {
                        mEditPosition = -1;
                        touchedPosition = -1;
                        mList.get(mCurrentPosition).setText(text);
                        mList.get(mCurrentPosition).setTime(text.length() * 300);
                        DBManager.getInstance(mContext).insertOrUpdate(mList.get(mCurrentPosition), DBManager.getInstance(mContext).getInterCutDataDao(DBManager.WRITE_ONLY));
                        refreshAidl();
                        notifyDataSetChanged();

                        InputMethodManager inputMethodManager =
                                (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (null != v.getWindowToken()) {
                            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                    } else {
                        Toast.makeText(mContext, "请输入内容", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            viewHolder.imgNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIsAddNew = true;
                    if (mCurrentPosition <= getCount()) {
                        mEditPosition = -1;
                        touchedPosition = -1;
                        InterCutData interCutData = mList.get(mCurrentPosition);
                        Long id = interCutData.getId();
                        if (null != id && id >= 0) {
                            DBManager.getInstance(mContext).delete(interCutData, DBManager.getInstance(mContext).getInterCutDataDao(DBManager.WRITE_ONLY));
                            refreshAidl();
                        }
                        mList.remove(mCurrentPosition);
                    }
                    notifyDataSetChanged();
                }
            });
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (null != mList) {
            InterCutData interCutData = mList.get(position);
            if (interCutData.getText().equals("") || mEditPosition == position || touchedPosition == position) {
                mIsAddNew = false;
                mCurrentPosition = position;
                viewHolder.textView.setVisibility(View.GONE);
                viewHolder.editText.setText(interCutData.getText().toString());
                viewHolder.imgOk.setVisibility(View.VISIBLE);
                viewHolder.imgNo.setVisibility(View.VISIBLE);
                viewHolder.editText.setVisibility(View.VISIBLE);
                viewHolder.editText.setEnabled(true);
                viewHolder.editText.setFocusable(true);
                viewHolder.editText.setFocusableInTouchMode(true);
                viewHolder.editText.requestFocus();
            } else {
                viewHolder.textView.setText(interCutData.getText());
                viewHolder.textView.setVisibility(View.VISIBLE);
                viewHolder.editText.setVisibility(View.GONE);
                viewHolder.imgOk.setVisibility(View.GONE);
                viewHolder.imgNo.setVisibility(View.GONE);
                viewHolder.editText.clearFocus();
            }


            if (interCutData.getIsPlay()) {
                convertView.setBackgroundColor(Color.parseColor("#e5e5e5"));
                checkMap.put(position, mList.get(position));
            }
            final View finalConvertView = convertView;
            viewHolder.checkBox.setChecked(interCutData.getIsPlay());
            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mList.get(position).setIsPlay(isChecked);
                    DBManager.getInstance(mContext).update(mList.get(position), DBManager.getInstance(mContext).getInterCutDataDao(DBManager.WRITE_ONLY));
                    finalConvertView.setBackgroundColor(true == isChecked ? Color.parseColor("#e5e5e5") : Color.parseColor("#ffffff"));
                    refreshAidl();
                    if (isChecked) {
                        checkMap.put(position, mList.get(position));
                    } else {
                        if (checkMap.containsKey(position)) {
                            checkMap.remove(position);
                        }
                    }
                }
            });

        }
        return convertView;
    }

    class ViewHolder {
        TextView textView;
        CheckBox checkBox;
        EditText editText;
        ImageButton imgOk;
        ImageButton imgNo;
    }

    private void refreshAidl() {
        IMyAidlInterface iMyAidlInterface = MBroadcastApplication.getIMyAidlInterface();
        if (null != iMyAidlInterface) {
            try {
                iMyAidlInterface.needrefresh(true);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.check_ok:
                    mIsAddNew = true;
                    String text = mCurrentEditText.getText().toString();
                    if (!"".equals(text.trim()) && null != text) {
                        mEditPosition = -1;
                        mList.get(mCurrentPosition).setText(text);
                        mList.get(mCurrentPosition).setTime(text.length() * 300);
                        DBManager.getInstance(mContext).insertOrUpdate(mList.get(mCurrentPosition), DBManager.getInstance(mContext).getInterCutDataDao(DBManager.WRITE_ONLY));
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(mContext, "请输入内容", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.check_no:
                    mIsAddNew = true;
                    if (mCurrentPosition <= getCount()) {
                        mEditPosition = -1;
                        InterCutData interCutData = mList.get(mCurrentPosition);
                        Long id = interCutData.getId();
                        if (null != id && id >= 0) {
                            DBManager.getInstance(mContext).delete(interCutData, DBManager.getInstance(mContext).getInterCutDataDao(DBManager.WRITE_ONLY));
                            refreshAidl();
                        }
                        mList.remove(mCurrentPosition);
                    }
                    notifyDataSetChanged();
                    break;
            }
        }
    };

}

