package com.oraro.mbroadcast.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.listener.OnImageClickListener;


/**
 * Created by Administrator on 2017/1/3 0003.
 */
public class GuideUtil {
    private Context context;
    private LinearLayout mLinearLayout;
    private WindowManager windowManager;
    private static GuideUtil instance = null;

    private GuideUtil() {
    }

    /**
     * 采用单例的设计模式，同时用了同步锁
     **/
    public static GuideUtil getInstance() {
        synchronized (GuideUtil.class) {
            if (null == instance) {
                instance = new GuideUtil();
            }
        }
        return instance;
    }

    public static void unRegisterInstance() {
        instance = null;
    }


    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:

                    break;
            }
        }

        ;
    };


    public void initGuide(final Activity context, int layoutResourceId, final OnImageClickListener onClickListener) {
        this.context = context;
        windowManager = context.getWindowManager();
        mLinearLayout = new LinearLayout(context);
        mLinearLayout.setBackgroundColor(Color.parseColor("#60000000"));
        mLinearLayout.setGravity(LinearLayout.VERTICAL);
        mLinearLayout.setLayoutParams(new WindowManager.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT));
        // 设置LayoutParams参数
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        // 设置显示的类型，TYPE_PHONE指的是来电话的时候会被覆盖，其他时候会在最前端，显示位置在stateBar下面，其他更多的值请查阅文档
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        // 设置显示格式
        params.format = PixelFormat.RGBA_8888;
        // 设置对齐方式
        params.gravity = Gravity.LEFT | Gravity.TOP;

        UIUtils uiUtils = new UIUtils();
        // 设置宽高
        params.width = uiUtils.getDisplayMetrics(context).widthPixels;

        params.height = uiUtils.getDisplayMetrics(context).heightPixels;

        // 添加到当前的窗口上
        windowManager.addView(mLinearLayout, params);

        LayoutInflater layoutInflater = LayoutInflater.from(mLinearLayout.getContext());

        layoutInflater.inflate(layoutResourceId, mLinearLayout);

        ImageView imageView = (ImageView) mLinearLayout.findViewById(R.id.guide_know);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onClickListener)
                    onClickListener.callback();
                windowManager.removeView(mLinearLayout);
            }
        });
    }
}
