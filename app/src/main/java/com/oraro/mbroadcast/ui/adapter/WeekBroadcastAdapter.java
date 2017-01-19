package com.oraro.mbroadcast.ui.adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.listener.OnImgClickListener;
import com.oraro.mbroadcast.model.PlayEntry;
import com.oraro.mbroadcast.mp.PlayAudio;
import com.oraro.mbroadcast.proxy.TTSProXy;
import com.oraro.mbroadcast.utils.BeanUtil;
import com.oraro.mbroadcast.utils.DebugUtil;
import com.oraro.mbroadcast.utils.LogUtils;
import com.oraro.mbroadcast.utils.PlayStateUtils;
import com.oraro.mbroadcast.vo.PlayVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by dongyu on 2016/8/19 0019.
 */
public class WeekBroadcastAdapter extends BaseAdapter {
    private final String TAG = "WeekBroadcastAdapter";
    private  Set<String> necessary;
    private  Set<String> notnecessary;
    private Context mContext = null;
    private List<PlayVO> mPlayVOData = null;
    private List<ViewHolder> holders = new ArrayList<ViewHolder>();
    private OnImgClickListener mSetOnImageClickListener;
    private int currentDelteOrder = 0;
    private float currentScale = 0;
    private int originHeight;
    private boolean hadGetOriginHeight;
    private boolean isNeedDelete = false;
    private boolean isDeleting = false;
    private Handler scollHandler;
    private long playid;
    private long playafterid;

    public void setOnImgClickListener(OnImgClickListener setOnImageClickListener) {
        this.mSetOnImageClickListener = setOnImageClickListener;
    }

    public WeekBroadcastAdapter(Context context, List<PlayVO> playVOs) {
        mContext = context;
        mPlayVOData = playVOs;
    }

    public WeekBroadcastAdapter(Context context, List<PlayVO> playVOs,Set<String> necessary,Set<String> notnecessary) {
        mContext = context;
        mPlayVOData = playVOs;
        this.necessary = necessary;
        this.notnecessary = notnecessary;
    }

    public void setNecessary(Set<String> necessary,Set<String> notnecessary){
        this.necessary = necessary;
        this.notnecessary = notnecessary;
    }

    public void setPlayVOData(List<PlayVO> playVOs) {
        mPlayVOData = playVOs;
        Log.e("WeekBroadcastAdapter", "mPlayVOData.size  " + mPlayVOData.size());
    }

    public void setHandler(Handler handler) {
        scollHandler = handler;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (null != mPlayVOData) {
            count = mPlayVOData.size();
        }
        return count;
    }

    @Override
    public PlayVO getItem(int position) {
        PlayVO item = null;

        if (null != mPlayVOData) {
            item = mPlayVOData.get(position);
        }

        return item;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            convertView = mInflater.inflate(R.layout.broadcast_listview_item, null);
            viewHolder.list_item_text_play_time = (TextView) convertView.findViewById(R.id.list_item_text_play_time);
            viewHolder.list_item_text_flight_number = (TextView) convertView.findViewById(R.id.list_item_text_flight_number);
            viewHolder.list_item_text_plane_type = (TextView) convertView.findViewById(R.id.list_item_text_plane_type);
            viewHolder.list_item_text_destination = (TextView) convertView.findViewById(R.id.list_item_text_destination);
            viewHolder.list_item_text_type = (TextView) convertView.findViewById(R.id.list_item_text_type);
            viewHolder.list_item_img = (ImageView) convertView.findViewById(R.id.list_item_img);
            viewHolder.isPlay = false;
            viewHolder.text_title_yanwu = (TextView) convertView.findViewById(R.id.text_title_yanwu);
            viewHolder.list_item_view1 = convertView.findViewById(R.id.list_item_view1);
            viewHolder.list_item_view2 = convertView.findViewById(R.id.list_item_view2);
            viewHolder.list_item_line = (LinearLayout) convertView.findViewById(R.id.list_item_line);
            viewHolder.backgroudRelative = (RelativeLayout) convertView.findViewById(R.id.list_item_RelativeLayout);
            viewHolder.list_title_img_play_again = (ImageView) convertView.findViewById(R.id.list_title_img_play_again);
            convertView.setTag(viewHolder);
            holders.add(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // set item values to the viewHolder:
        final PlayVO playVO = getItem(position);
        if (null != playVO) {
//            if (playVO.isPlayNow()) {
//                viewHolder.isPlay = true;
//            } else {
//                viewHolder.isPlay = false;
//            }
            viewHolder.isPlayAfter = false;
            viewHolder.isPlay = false;
            if (playVO.getEntity() != null) {
                //    Log.e("wyDebug","now is play :"+MBroadcastApplication.getPlayID() + ",playVO.getEntity().getId() :"+playVO.getEntity().getId());
                if (playVO.getEntity().getId().equals(MBroadcastApplication.getPlayID())) {
                    viewHolder.isPlay = true;
                } else {
                    viewHolder.isPlay = false;
                }
                if (playVO.getEntity().getDoTimes() != 0 || playVO.getEntity().getId() == playafterid) {
                    viewHolder.isPlayAfter = true;
                    playafterid = -1;
                } else {
                    viewHolder.isPlayAfter = false;
                }
                //  Log.e("wyDebug","now is play :"+MBroadcastApplication.getPlayID());

                Date date = playVO.getEntity().getTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                viewHolder.list_item_text_play_time.setText(simpleDateFormat.format(date));
                if(null!=playVO.getEntity().getFlightInfoTemp()){
                    int i = 0;
                    for(String s : necessary){
                        String[] sss = s.split("&");
                        int key = Integer.parseInt(sss[0]);
                        String ss = sss[2];
                        switch (key){
                            case 0:
                                break;
                            case 1:
                                viewHolder.list_item_text_flight_number.setText("" + BeanUtil.invokeGet(playVO.getEntity().getFlightInfoTemp(),ss));
                                break;
                            case 2:
                                viewHolder.list_item_text_plane_type.setText(""+BeanUtil.invokeGet(playVO.getEntity().getFlightInfoTemp(),ss));
                                break;
                            case 3:
                                viewHolder.list_item_text_destination.setText(""+BeanUtil.invokeGet(playVO.getEntity().getFlightInfoTemp(),ss));
                                break;
                        }
                        i++;
                    }

                    i = 0;
                    for(String s : notnecessary){
                        String[] sss = s.split("&");
                        int key = Integer.parseInt(sss[0]);
                        String ss = sss[2];
                        switch (key){
                            case 4:
                                if(s.split("&")[1].equals("延误信息")){
                                    if (playVO.getEntity().getFlightInfoTemp().getIsDelay()) {
                                        viewHolder.list_item_text_type.setText("延误");
                                    } else {
                                        viewHolder.list_item_text_type.setText("未延误");
                                    }
                                }else if(s.split("&")[1].equals("预计起飞")){
                                    Date planToTakeOffDate = (Date)BeanUtil.invokeGet(playVO.getEntity().getFlightInfoTemp(),ss);
                                    SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
                                    viewHolder.list_item_text_type.setText(sf.format(planToTakeOffDate));
                                }else{
                                    viewHolder.list_item_text_type.setText("" + BeanUtil.invokeGet(playVO.getEntity().getFlightInfoTemp(),ss));
                                }
                                break;
                            case 5:
                                if(s.split("&")[1].equals("延误信息")){
                                    if (playVO.getEntity().getFlightInfoTemp().getIsDelay()) {
                                        viewHolder.text_title_yanwu.setText("延误");
                                    } else {
                                        viewHolder.text_title_yanwu.setText("未延误");
                                    }
                                }else if(s.split("&")[1].equals("预计起飞")){
                                    Date planToTakeOffDate = (Date)BeanUtil.invokeGet(playVO.getEntity().getFlightInfoTemp(),ss);
                                    SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
                                    viewHolder.text_title_yanwu.setText(sf.format(planToTakeOffDate));
                                }else{
                                    viewHolder.text_title_yanwu.setText("" + BeanUtil.invokeGet(playVO.getEntity().getFlightInfoTemp(),ss));
                                }
                                break;
                        }
                        i++;
                    }
                }else {
                    viewHolder.list_item_text_flight_number.setText("NA");
                    viewHolder.list_item_text_plane_type.setText(""+"NA");
                    viewHolder.list_item_text_destination.setText(""+"NA");
                    viewHolder.list_item_text_type.setText(""+"NA");
                    viewHolder.text_title_yanwu.setText(""+"NA");
                    viewHolder.text_title_yanwu.setText("NA");
                }


                viewHolder.id = playVO.getEntity().getId();
                if (playVO.getEntity().getFileParentPath() != null) {
                    viewHolder.list_item_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.listview_item_md_img_bg));
                } else {
                    viewHolder.list_item_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.listview_item_tts_md_img_bg));
                }
            }
        }

        //Log.e("wydebug","isplay ="+viewHolder.isPlay+",isplayafter = "+viewHolder.isPlayAfter);


        if (viewHolder.isPlay) {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.week_list_item_bg));
        } else {
            if (viewHolder.isPlayAfter) {
                convertView.setBackgroundColor(mContext.getResources().getColor(R.color.play_after));
            } else {
                convertView.setBackground(mContext.getResources().getDrawable(R.drawable.list_item_bg));
            }

        }
        viewHolder.list_item_view2.setVisibility(View.GONE);
        viewHolder.list_item_view1.setVisibility(View.GONE);

        viewHolder.list_item_img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSetOnImageClickListener.play(playVO, position);
            }
        });
        viewHolder.list_title_img_play_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPlaying = PlayStateUtils.isPlaying(mContext);
                Log.e("WeekFragment ", "list isJinJiPlay " + DebugUtil.isJinJiPlay);
                if (DebugUtil.isJinJiPlay) {
                    return;
                }
                if (isClickAdapterPlayBtn) {
                    return;
                }
                mSetOnImageClickListener.playAagain(playVO);
                if (mCurrentIconPosition >= 0) {
                    mLastIconPosition = mCurrentIconPosition;
                }
                mCurrentIconPosition = playVO.getEntity().getId();
            }
        });

        if (!isClickAdapterPlayBtn) {
            viewHolder.list_title_img_play_again.setImageResource(R.drawable.listview_item_play_again_img_bg);

        } else {
            if (playVO.getEntity().getId() == mLastIconPosition) {
                viewHolder.list_title_img_play_again.setImageResource(R.drawable.listview_item_play_again_img_bg);

            }
            if (playVO.getEntity().getId() == mCurrentIconPosition) {
                viewHolder.list_title_img_play_again.setImageResource(R.mipmap.listview_item_playing);

//                viewHolder.backgroudRelative.setBackgroundColor(mContext.getResources().getColor(R.color.week_list_item_bg));
            } else {
                viewHolder.list_title_img_play_again.setImageResource(R.drawable.listview_item_play_again_img_up);


            }

//            if (isCancel) {
//                viewHolder.list_title_img_play_again.setBackgroundResource(R.drawable.listview_item_play_again_img_up);
//            }
        }
        return convertView;
    }


    boolean isClickAdapterPlayBtn = false;
    boolean isCancel = false;
    private long mLastIconPosition = -1;
    private long mCurrentIconPosition = -1;
//    public boolean getAudioIsPlaying() {
//        return mIsPlaying;
//    }
//    public void isDialogCancel() {
//        isCancel = true;
//        notifyDataSetChanged();
//    }

    public void setIsClickAdapterPlayBtn(boolean isPlay) {
        isClickAdapterPlayBtn = isPlay;

    }

    public void isIconPlaying(long playid) {
        this.playid = playid;
        notifyDataSetChanged();
    }

    public void setAdapterNotify() {
        Log.e("haunghui", "123");
        notifyDataSetChanged();
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
//        mIsPlaying = false;
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


    private void doCollapse(View view) {
        if (!hadGetOriginHeight) {
            originHeight = view.getHeight();
            hadGetOriginHeight = true;
        }
        view.getLayoutParams().height = (int) (originHeight - originHeight * currentScale);
        view.requestLayout();
        if (view.getLayoutParams().height == 0) {
            isNeedDelete = false;
            mPlayVOData.remove(currentDelteOrder);
            notifyDataSetChanged();
            isDeleting = false;
        }
    }

    public void updataView(int position, ListView listView, long playVOId) {
        if (null == listView) {
            throw new RuntimeException("listView can not be null");
        }
        int visibleFirstPosi = listView.getFirstVisiblePosition();
        int visibleLastPosi = listView.getLastVisiblePosition();
        if (position >= visibleFirstPosi && position <= visibleLastPosi) {
            final View view;
            int first = listView.getFirstVisiblePosition();
            view = listView.getChildAt(position - first);
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            Log.e(TAG, "playVOId = " + playVOId);
            PlayEntry playEntry = (PlayEntry) DBManager.getInstance(MBroadcastApplication.getMyContext()).queryById(playVOId, DBManager.getInstance(MBroadcastApplication.getMyContext()).getPlayEntryDao(DBManager.READ_ONLY));
            if (null == playEntry) {
                return;
            }
            Log.e(TAG, "playVOId = " + playEntry.toString());
            for (int i = 0; i < mPlayVOData.size(); i++) {
                if (mPlayVOData.get(i).getEntity().getId() == playVOId) {
                    mPlayVOData.get(i).setEntity(playEntry);
                }
            }
            Date date = playEntry.getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            viewHolder.list_item_text_play_time.setText(simpleDateFormat.format(date));

            if(null!=playEntry.getFlightInfoTemp()){
                int i = 0;
                for(String s : necessary){
                    String[] sss = s.split("&");
                    int key = Integer.parseInt(sss[0]);
                    String ss = sss[2];
                    switch (key){
                        case 0:
                            break;
                        case 1:
                            viewHolder.list_item_text_flight_number.setText(""+BeanUtil.invokeGet(playEntry.getFlightInfoTemp(),ss));
                            break;
                        case 2:
                            viewHolder.list_item_text_plane_type.setText(""+BeanUtil.invokeGet(playEntry.getFlightInfoTemp(),ss));
                            break;
                        case 3:
                            viewHolder.list_item_text_destination.setText(""+BeanUtil.invokeGet(playEntry.getFlightInfoTemp(),ss));
                            break;
                    }
                    i++;
                }

                i = 0;
                for(String s : notnecessary){
                    String[] sss = s.split("&");
                    int key = Integer.parseInt(sss[0]);
                    String ss = sss[2];
                    switch (key){
                        case 4:
                            if(s.split("&")[1].equals("延误信息")){
                                if (playEntry.getFlightInfoTemp().getIsDelay()) {
                                    viewHolder.list_item_text_type.setText("延误");
                                } else {
                                    viewHolder.list_item_text_type.setText("未延误");
                                }
                            }else if(s.split("&")[1].equals("预计起飞")){
                                Date planToTakeOffDate = (Date)BeanUtil.invokeGet(playEntry.getFlightInfoTemp(),ss);
                                SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
                                viewHolder.list_item_text_type.setText(sf.format(planToTakeOffDate));
                            }else{
                                viewHolder.list_item_text_type.setText("" + BeanUtil.invokeGet(playEntry.getFlightInfoTemp(),ss));
                            }
                            break;
                        case 5:
                            if(s.split("&")[1].equals("延误信息")){
                                if (playEntry.getFlightInfoTemp().getIsDelay()) {
                                    viewHolder.text_title_yanwu.setText("延误");
                                } else {
                                    viewHolder.text_title_yanwu.setText("未延误");
                                }
                            }else if(s.split("&")[1].equals("预计起飞")){
                                Date planToTakeOffDate = (Date)BeanUtil.invokeGet(playEntry.getFlightInfoTemp(),ss);
                                SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
                                viewHolder.text_title_yanwu.setText(sf.format(planToTakeOffDate));
                            }else{
                                viewHolder.text_title_yanwu.setText("" + BeanUtil.invokeGet(playEntry.getFlightInfoTemp(),ss));
                            }
                            break;
                    }
                    i++;
                }
            }else {
                viewHolder.list_item_text_flight_number.setText("NA");
                viewHolder.list_item_text_plane_type.setText(""+"NA");
                viewHolder.list_item_text_destination.setText(""+"NA");
                viewHolder.list_item_text_type.setText(""+"NA");
                viewHolder.text_title_yanwu.setText(""+"NA");
                viewHolder.text_title_yanwu.setText("NA");
            }


//            if(null!=playEntry.getFlightInfoTemp()){
//                viewHolder.list_item_text_flight_number.setText("            " + playEntry.getFlightInfoTemp().getFlightNumber());
//                viewHolder.list_item_text_take_off.setText(playEntry.getFlightInfoTemp().getDepartureStation());
//                viewHolder.list_item_text_destination.setText(playEntry.getFlightInfoTemp().getDestinationStation());
//                viewHolder.list_item_text_type.setText(playEntry.getFlightInfoTemp().getProperty());
//                viewHolder.text_title_yanwu.setText(playEntry.getFlightInfoTemp().getImportAndExport());
//                if (playEntry.getFlightInfoTemp().getIsDelay()) {
//                    viewHolder.text_title_yanwu.setText("延误");
//                } else {
//                    viewHolder.text_title_yanwu.setText("未延误");
//                }
//            }else {
//                viewHolder.list_item_text_flight_number.setText("            ");
//                viewHolder.list_item_text_take_off.setText("");
//                viewHolder.list_item_text_destination.setText("");
//                viewHolder.list_item_text_type.setText("");
//                viewHolder.text_title_yanwu.setText("");
//                viewHolder.text_title_yanwu.setText("延误");
//            }
            notifyDataSetChanged();
        }
    }

    public void updataView(List<PlayVO> playEntries, int position, ListView listView, long playVOId) {
        if (listView == null) {
            throw new RuntimeException("listView can not be null");
        }
        int visibleFirstPosi = listView.getFirstVisiblePosition();
        int visibleLastPosi = listView.getLastVisiblePosition();
        Log.e(TAG, "visibleFirstPosi = " + visibleFirstPosi);
        Log.e(TAG, "visibleLastPosi = " + visibleLastPosi);
        Log.e(TAG, "position = " + position);
        if (position >= visibleFirstPosi && position <= visibleLastPosi) {
            final View view;
            int first = listView.getFirstVisiblePosition();
            view = listView.getChildAt(position - first);
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            Log.e(TAG, "playVOId = " + playVOId);
            PlayEntry playEntry = (PlayEntry) DBManager.getInstance(MBroadcastApplication.getMyContext()).queryById(playVOId, DBManager.getInstance(MBroadcastApplication.getMyContext()).getPlayEntryDao(DBManager.READ_ONLY));
            if (null == playEntry) {
                return;
            }
            Log.e(TAG, "playVOId = " + playEntry.toString());
            for (int i = 0; i < playEntries.size(); i++) {
                if (playEntries.get(i).getEntity().getId() == playVOId) {
                    playEntries.get(i).setEntity(playEntry);
                }
            }
            Date date = playEntry.getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            viewHolder.list_item_text_play_time.setText(simpleDateFormat.format(date));
            if(null!=playEntry.getFlightInfoTemp()){
                viewHolder.list_item_text_flight_number.setText("            " + playEntry.getFlightInfoTemp().getFlightNumber());
                viewHolder.list_item_text_plane_type.setText(playEntry.getFlightInfoTemp().getDeparture());
                viewHolder.list_item_text_destination.setText(playEntry.getFlightInfoTemp().getArrivalStation());
                if (null == playEntry.getFlightInfoTemp().getRemarks()) {
                    viewHolder.list_item_text_type.setText("");
                }else {
                    viewHolder.list_item_text_type.setText(playEntry.getFlightInfoTemp().getRemarks());
                }
                viewHolder.text_title_yanwu.setText(playEntry.getFlightInfoTemp().getDelayInfo());
                if (playEntry.getFlightInfoTemp().getIsDelay()) {
                    viewHolder.text_title_yanwu.setText("延误");
                } else {
                    viewHolder.text_title_yanwu.setText("未延误");
                }
            }else {
                viewHolder.list_item_text_flight_number.setText("NA");
                viewHolder.list_item_text_plane_type.setText(""+"NA");
                viewHolder.list_item_text_destination.setText(""+"NA");
                viewHolder.list_item_text_type.setText(""+"NA");
                viewHolder.text_title_yanwu.setText(""+"NA");
                viewHolder.text_title_yanwu.setText("NA");
            }
            notifyDataSetChanged();
        }
    }

    private static class ViewHolder {
        TextView list_item_text_play_time;
        TextView list_item_text_flight_number;
        TextView list_item_text_plane_type;
        TextView list_item_text_destination;
        TextView list_item_text_type;
        ImageView list_item_img;
        TextView text_title_yanwu;
        Boolean isPlay;
        Long id;
        LinearLayout list_item_line;
        View list_item_view1;
        View list_item_view2;
        RelativeLayout backgroudRelative;
        ImageView list_title_img_play_again;
        Boolean isPlayAfter;
    }

    public void addFrushHandle() {
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Constants.HandlerConstants.PLAY:
                        Log.e("wyhandler", "msg->obj = " + (long) msg.obj);
                        ViewHolder playView = null;
                        MBroadcastApplication.setPlayID((long) msg.obj);
                        if (holders != null && holders.size() != 0) {
                            for (ViewHolder holder : holders) {
                                if (holder.id != null) {
                                    if (holder.id.equals((long) msg.obj)) {
                                        holder.list_item_view2.setVisibility(View.GONE);
                                        holder.list_item_view1.setVisibility(View.GONE);
                                        holder.backgroudRelative.setBackgroundColor(mContext.getResources().getColor(R.color.week_list_item_bg));
                                        // break;
                                    } else {
                                        holder.list_item_view2.setVisibility(View.GONE);
                                        holder.list_item_view1.setVisibility(View.GONE);
                                        holder.backgroudRelative.setBackground(mContext.getResources().getDrawable(R.drawable.list_item_bg));
                                    }
                                }
                            }
                        }
                        Message message = new Message();

                        for (int i = 0; i < mPlayVOData.size(); i++) {
                            if (mPlayVOData.get(i).getEntity().getId() == (long) msg.obj) {
                                message.arg1 = i;
                                mPlayVOData.get(i).getEntity().setDoTimes(1);//wy fixed Bug for played but not show played Detail when not update data from sql
                                break;
                            } else {
                                message.arg1 = -1;
                            }
                        }
                        scollHandler.sendMessage(message);
                        break;
                    case Constants.HandlerConstants.QUEUE:
                        break;
                    case Constants.HandlerConstants.COMPLETED:
                        Log.e("huanghui", "msg->obj = " + (long) msg.obj);
                        MBroadcastApplication.setPlayID((long) -1);
                        playafterid = (long) msg.obj;
//                        Message message1 = new Message();

                        for (int i = 0; i < mPlayVOData.size(); i++) {
                            if (mPlayVOData.get(i).getEntity().getId() == (long) msg.obj) {
//                                message1.arg1 = i;
                                mPlayVOData.get(i).getEntity().setDoTimes(1);//wy fixed Bug for played but not show played Detail when not update data from sql
                                break;
                            } else {
//                                message1.arg1 = -1;
                            }
                        }
                        notifyDataSetChanged();
//                        scollHandler.sendMessage(message1);
                        break;
                    default:
                }
            }
        };
        MBroadcastApplication.setFrushHandler(handler);
    }
}
