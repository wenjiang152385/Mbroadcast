package com.oraro.mbroadcast.ui.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.logicService.DataService;
import com.oraro.mbroadcast.ui.activity.DelayActivity;
import com.oraro.mbroadcast.ui.fragment.PromptFragment;
import com.oraro.mbroadcast.ui.activity.UrgentBroadcastActivity;
import com.oraro.mbroadcast.vo.PlayVO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


/**
 *
 */
public class AddBroadcastPopWindow extends PopupWindow {
    private View conentView;
    public static final int PERMISSIONS_REQUEST_CODE = 0;
    public static final int FILE_PICKER_REQUEST_CODE = 1;

    @SuppressLint("InflateParams")
    public AddBroadcastPopWindow(final Activity context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.popupwindow_add1, null);

        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);

        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);


        RelativeLayout   re_addfriends =(RelativeLayout) conentView.findViewById(R.id.re_addfriends);
        RelativeLayout   re_chatroom =(RelativeLayout) conentView.findViewById(R.id.re_chatroom);
        RelativeLayout   re_addPrompt =(RelativeLayout) conentView.findViewById(R.id.re_addPrompt);
        re_addPrompt.setVisibility(View.GONE);
       // RelativeLayout   re_saoyisao =(RelativeLayout) conentView.findViewById(R.id.re_saoyisao);

        re_chatroom.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
//                  List<PlayVO>playVOs= getAllData(0);
//                   if (playVOs!=null&&playVOs.size()>0){
//                       Intent intent =  new Intent(context, DelayActivity.class);
//                       intent.putExtra("flag",0);
//                       context.startActivity(intent);
//                       AddBroadcastPopWindow.this.dismiss();
//                   }else {
//                       Toast.makeText(context,"没有当天航班信息,无法延误",Toast.LENGTH_SHORT).show();
//                   }


            }

        } );
//        re_addfriends.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, UrgentBroadcastActivity.class);
//                context.startActivity(intent);
//                AddBroadcastPopWindow.this.dismiss();
//            }
//        });

        re_addPrompt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, PromptFragment.class);
//                context.startActivity(intent);
                AddBroadcastPopWindow.this.dismiss();
            }
        });
//        re_chatroom.setOnClickListener(new OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//                context.startActivity(new Intent(context,ExcelActivity.class));
//                AddBroadcastPopWindow.this.dismiss();
//            }
//
//        } );
//        re_saoyisao.setOnClickListener(new OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, SearchFlightActivity.class);
//                context.startActivity(intent);
//                AddBroadcastPopWindow.this.dismiss();
//            }
//
//        } );

    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent, 0,10);
        } else {
            this.dismiss();
        }
    }
    public List<PlayVO> getAllData(int day) {
        Date nowDate = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.add(calendar.DATE, day);//把日期往后增加一天.整数往后推,负数往前移动
        nowDate = calendar.getTime(); //这个时间就是日期往后推一天的结果
//        nowDate.setHours(0);
//        nowDate.setMinutes(0);
//        nowDate.setSeconds(0);
        Date beginTime = new Date(nowDate.getYear(), nowDate.getMonth(), nowDate.getDate(), nowDate.getHours(), nowDate.getMinutes(), 0);
        Date endTime = new Date(nowDate.getYear(), nowDate.getMonth(), nowDate.getDate(), 23, 59, 59);
        DataService s = new DataService();
        List<PlayVO> dataList = new ArrayList<PlayVO>();
        dataList = s.getPlayVO(beginTime, endTime);
       // Log.e("BaseBroadcastFragment", " dataList size" + dataList.size());
        return dataList;
    }
}

