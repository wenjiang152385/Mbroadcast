package com.oraro.mbroadcast.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.oraro.mbroadcast.R;
import com.wx.wheelview.widget.WheelView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/10/10 0010.
 */
public class TimerPickActivity extends Activity {
    private WheelView hourWheelView, minuteWheelView;
    private TextView header_left_btn;
    private Button bt_yanwu;
    private ArrayList<String> date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timepicker);
        Intent intent=getIntent();
        date = intent.getStringArrayListExtra("date");
        Log.e("ggg","date=="+date);
       // initWheel2();
        bt_yanwu = (Button) findViewById(R.id.bt1);
        header_left_btn = (TextView) findViewById(R.id.header_left_btn);
        //返回
        header_left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //时间确认


    }

}
