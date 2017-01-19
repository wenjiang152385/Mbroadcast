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

import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.ui.activity.AddFliActivity;
import com.oraro.mbroadcast.ui.activity.AddFlightActivity;
import com.oraro.mbroadcast.ui.activity.ExcelActivity;
import com.oraro.mbroadcast.ui.activity.SearchFlightActivity;


/**
 * Created by dongyu on 2016/8/31 0031.
 */
public class AddPopWindow extends PopupWindow {
    private View conentView;
    public static final int PERMISSIONS_REQUEST_CODE = 0;
    public static final int FILE_PICKER_REQUEST_CODE = 1;

    @SuppressLint("InflateParams")
    public AddPopWindow(final Activity context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.popupwindow_add, null);

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
        RelativeLayout   re_saoyisao =(RelativeLayout) conentView.findViewById(R.id.re_saoyisao);

        re_addfriends.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {

                Intent intent =  new Intent(context, AddFliActivity.class);
                intent.putExtra("flag",0);
                context.startActivity(intent);
                AddPopWindow.this.dismiss();

            }

        } );
        re_chatroom.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context,ExcelActivity.class));
                AddPopWindow.this.dismiss();
            }

        } );
//        re_saoyisao.setOnClickListener(new OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, SearchFlightActivity.class);
//                context.startActivity(intent);
//                AddPopWindow.this.dismiss();
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
}

