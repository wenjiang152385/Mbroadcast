package com.oraro.mbroadcast.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.model.InterCutData;
import com.oraro.mbroadcast.model.ReminderBroadcastBean;
import com.oraro.mbroadcast.model.UrgentItemBean;
import com.oraro.mbroadcast.service.IMyAidlInterface;
import com.oraro.mbroadcast.ui.activity.CustomBroadcastActivity;
import com.oraro.mbroadcast.ui.fragment.KindlyReminderFragment;
import com.oraro.mbroadcast.utils.LogUtils;
import com.oraro.mbroadcast.utils.PlayStateUtils;
import com.oraro.mbroadcast.utils.UrgentBroadcastXmlUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.oraro.mbroadcast.R.id.reminder_broadcast_ll;


/**
 * Created by admin on 2016/11/22
 *
 * @author zmy
 */

public class ReminderAdapter2 extends BaseAdapter {
    private Context context;
    private List<UrgentItemBean> list;
    private List<Integer> selectList;
    private ViewHolder vh;
    private UrgentBroadcastXmlUtils ubxu;
    private KindlyReminderFragment mKindlyReminderFragment;
    public int clickPosition;
    public List<Long> mLongClickId = new ArrayList<>();
    private DBManager mDBManager;
    private String TAG = "zmy";


    public ReminderAdapter2(Context context, List<UrgentItemBean> list, KindlyReminderFragment kindlyReminderFragment) {
        this.context = context;
        this.list = list;
        mKindlyReminderFragment = kindlyReminderFragment;
        selectList = new ArrayList<>();
        ubxu = new UrgentBroadcastXmlUtils(context);
        mDBManager = DBManager.getInstance(context);
    }

    public void setData(List<UrgentItemBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            vh = new ViewHolder();
            convertView = View.inflate(context, R.layout.reminder_broadcast_lv_item, null);
            vh.reminder_broadcast_ll = (LinearLayout) convertView.findViewById(reminder_broadcast_ll);
            vh.ll = (LinearLayout) convertView.findViewById(R.id.ll);
            vh.reminder_broadcast_rl = (RelativeLayout) convertView.findViewById(R.id.reminder_broadcast_rl);
            vh.tv = (TextView) convertView.findViewById(R.id.tv);
            vh.fl_divide = (FrameLayout) convertView.findViewById(R.id.fl_divide);
            vh.check_ok = (ImageView) convertView.findViewById(R.id.check_ok);
            vh.check_delete = (ImageView) convertView.findViewById(R.id.check_delete);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        //
        vh.check_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLongClickId.contains(Long.valueOf(list.get(position).getId()))) {// 正在编辑 取消删除按钮
                    mLongClickId.remove(Long.valueOf(list.get(position).getId()));
                } else {
                    if (TextUtils.isEmpty(list.get(position).getContent())) {
                        Toast.makeText(context, "请先点击该条目进行编辑，然后再执行选中操作", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        if (selectList.contains(new Integer(position))) {
                            selectList.remove(new Integer(position));
                            list.get(position).setIsSelected(false);

                        } else {
                            selectList.add(new Integer(position));
                            list.get(position).setIsSelected(true);

//                            LogUtils.e("zmy", "selectList.size==11=====" + selectList.size());
                        }
                    }
//                    LogUtils.e("zmy", "list.get(position).isselect=====" + list.get(position));
                    mDBManager.update(list.get(position), mDBManager.getUrgentItemBeanDao(DBManager.WRITE_ONLY));

                }
                notifyDataSetChanged();
                selectPlay();
            }
        });
        // 删除
        vh.check_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                LogUtils.e("zmy", "MBroadcastApplication.isUrgent=======" + MBroadcastApplication.isUrgent);
                if (!MBroadcastApplication.isUrgent) {// 非正在播放状态执行下边删除逻辑
                    mLongClickId.remove(Long.valueOf(list.get(position).getId()));
                    if (null != list.get(position).getInterCutDataId()) {

                        mDBManager.deleteById(list.get(position).getInterCutDataId(), mDBManager.getInterCutDataDao(DBManager.WRITE_ONLY));
                    }
                    mDBManager.delete(list.get(position), mDBManager.getUrgentItemBeanDao(DBManager.WRITE_ONLY));
                    list.remove(position);
                    notifyDataSetChanged();
                }
            }
        });
        // item竖线右边布局
        vh.reminder_broadcast_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(list.get(position).getContent())) {
                    Toast.makeText(context, "请先点击该条目进行编辑，然后再执行选中操作", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    if (selectList.contains(new Integer(position))) {
                        selectList.remove(new Integer(position));
                        list.get(position).setIsSelected(false);
//                        LogUtils.e("zmy","list.get(position).list.get(position).getIsSelected()====if======"+list.get(position).getIsSelected());
                    } else {
                        selectList.add(new Integer(position));
                        list.get(position).setIsSelected(true);
//                        dbManager.insert(list.get(position), dbManager.getUrgentItemBeanDao(DBManager.WRITE_ONLY));
                    }
                }
                mDBManager.update(list.get(position), mDBManager.getUrgentItemBeanDao(DBManager.WRITE_ONLY));
                notifyDataSetChanged();
                selectPlay();
            }
        });
        // item竖线左边布局
        vh.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CustomBroadcastActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", list.get(position).getType());
                if (null != list.get(position).getContent()) {
                    bundle.putString("content", list.get(position).getContent());
                }
                if (null != list.get(position).getParams()) {
                    bundle.putString("params", list.get(position).getParams());
                }
                intent.putExtra("bundle", bundle);
                clickPosition = position;
                mKindlyReminderFragment.customStartActivityForResult(intent, 12);
            }
        });
        // 长按
        vh.ll.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!mLongClickId.contains(Long.valueOf(list.get(position).getId()))) {
                    mLongClickId.add(list.get(position).getId());
                }
                notifyDataSetChanged();
                return true;
            }
        });
        if (list.get(position).getIsSelected()) {
            vh.reminder_broadcast_ll.setBackgroundColor(Color.parseColor("#e5e5e5"));
            vh.check_ok.setBackgroundResource(R.mipmap.btn_ok);
            vh.fl_divide.setBackgroundColor(Color.parseColor("#ffffff"));
        } else {
            if (!TextUtils.isEmpty(list.get(position).getParams())) {
                vh.reminder_broadcast_ll.setBackgroundColor(Color.parseColor("#f8f4e5"));
            } else {
                vh.reminder_broadcast_ll.setBackgroundColor(Color.parseColor("#ffffff"));
            }
            vh.check_ok.setBackgroundResource(R.mipmap.reminder_unselect);
            vh.fl_divide.setBackgroundColor(Color.parseColor("#e5e5e5"));
        }

        if (mLongClickId.contains(Long.valueOf(list.get(position).getId()))) {
            vh.check_delete.setVisibility(View.VISIBLE);
            vh.check_ok.setBackgroundResource(R.mipmap.btn_ok);
            vh.reminder_broadcast_rl.setClickable(false);
        } else {
            vh.check_delete.setVisibility(View.INVISIBLE);
            vh.reminder_broadcast_rl.setClickable(true);
        }
        vh.tv.setText(ubxu.hashMap.get((list.get(position)).getType()).getTitle());
        return convertView;
    }

    private void selectPlay() {
        // 快速紧急广播的播放逻辑
        for (int j = 0; j < list.size(); j++) {
            if (list.get(j).getIsSelected() && !TextUtils.isEmpty(list.get(j).getContent())) {
                InterCutData interCutData = (InterCutData) mDBManager.queryById(list.get(j).getInterCutDataId(), mDBManager.getInterCutDataDao(DBManager.READ_ONLY));
//                LogUtils.e(TAG, "interCutData=0000000000=======" + interCutData);

                if (null == interCutData) {
                    interCutData = new InterCutData();
                    interCutData.setTy(Constants.TYPE_URGENT_DATA);
                    interCutData.setIsPlay(true);
                    interCutData.setText(list.get(j).getContent());
                    interCutData.setTime(list.get(j).getContent().length() * 300);
                    long interCutDataId = mDBManager.insert(interCutData, mDBManager.getInterCutDataDao(DBManager.WRITE_ONLY));
                    list.get(j).setInterCutDataId(interCutDataId);
                    mDBManager.update(list.get(j), mDBManager.getUrgentItemBeanDao(DBManager.WRITE_ONLY));
                } else {
                    interCutData.setIsPlay(true);
                    interCutData.setText(list.get(j).getContent());
                    interCutData.setTime(list.get(j).getContent().length() * 300);
                    mDBManager.deleteById(interCutData.getId(), mDBManager.getInterCutDataDao(DBManager.WRITE_ONLY));

                    interCutData = new InterCutData();
                    interCutData.setTy(Constants.TYPE_URGENT_DATA);
                    interCutData.setIsPlay(true);
                    interCutData.setText(list.get(j).getContent());
                    interCutData.setTime(list.get(j).getContent().length() * 300);
                    long interCutDataId = mDBManager.insert(interCutData, mDBManager.getInterCutDataDao(DBManager.WRITE_ONLY));
                    list.get(j).setInterCutDataId(interCutDataId);
                    mDBManager.update(list.get(j), mDBManager.getUrgentItemBeanDao(DBManager.WRITE_ONLY));
                }
//                LogUtils.e(TAG, "interCutData=111111111111111=======" + interCutData);
            } else if (!list.get(j).getIsSelected()) {// 没有被选中，则在温馨提示的播放逻辑中删除
                InterCutData interCutData = (InterCutData) mDBManager.queryById(list.get(j).getInterCutDataId(), mDBManager.getInterCutDataDao(DBManager.READ_ONLY));
                if (interCutData != null) {
//                    LogUtils.e(TAG, "未被选中，需要被删除的interCutData========" + interCutData);
                    mDBManager.delete(interCutData, mDBManager.getInterCutDataDao(DBManager.WRITE_ONLY));
                }
//                mDBManager.update(adapter2BeanList.get(j), mDBManager.getUrgentItemBeanDao(DBManager.WRITE_ONLY));
            }
        }
        IMyAidlInterface iMyAidlInterface = MBroadcastApplication.getIMyAidlInterface();
        if (null != iMyAidlInterface) {
            try {
                iMyAidlInterface.needrefresh(true);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public class ViewHolder {
        LinearLayout reminder_broadcast_ll;
        LinearLayout ll;
        FrameLayout fl_divide;
        TextView tv;
        ImageView check_ok;
        ImageView check_delete;
        RelativeLayout reminder_broadcast_rl;
    }


}
