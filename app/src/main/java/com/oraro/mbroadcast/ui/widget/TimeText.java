package com.oraro.mbroadcast.ui.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

import com.oraro.mbroadcast.listener.ITimeCallback;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/9/6 0006.
 */
public class TimeText extends TextView {
    private ITimeCallback callback;
    private Date startDate;

    public TimeText(Context context) {
        super(context);
    }

    public void regesiterCallback(ITimeCallback callback) {
        this.callback = callback;
    }

    public void startCountTime() {
        startDate = new Date();
        mHander.postDelayed(mRunnable, 1000 * 1);
    }

    public void endCountTime() {
        mHander.removeCallbacks(mRunnable);
    }

    public TimeText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Handler mHander = new Handler();


    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            callback.timeCallback(formatData(getCountDate()));
            mHander.postDelayed(this, 1000 * 1);
        }
    };

    private Date getCountDate() {
        Date date = new Date();
        long countTime = date.getTime() - startDate.getTime();
        Date countDate = new Date(countTime);
        return countDate;
    }

    private String formatData(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        String time = simpleDateFormat.format(date);
        return time;
    }


}
