package com.oraro.mbroadcast.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.oraro.mbroadcast.R;

/**
 * Created by dongyu on 2016/8/18 0018.
 */
public abstract class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initView();
        initData(savedInstanceState);

    }
    protected abstract int getLayoutId();
    protected abstract void initData(Bundle paramBundle);
    protected abstract void initView();
    protected  void setHeader_textTitle(int resid){
        TextView header_text= (TextView) findViewById(R.id.header_text);
        header_text.setText(resid);
    }
    protected  void setHeader_right_btn(int resid){
        TextView header_right_btn= (TextView) findViewById(R.id.header_right_btn);
        header_right_btn.setVisibility(View.VISIBLE);
        header_right_btn.setBackground(null);
        header_right_btn.setText(resid);
        header_right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHeader_right_btnOnClickListener();
            }
        });
    }

    protected  void setHeader_right_btnOnClickListener(){


    };

    protected  void setHeader_left_btn(){
        TextView header_left_btn= (TextView) findViewById(R.id.header_left_btn);
        header_left_btn.setVisibility(View.VISIBLE);
        header_left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHeader_left_btnOnClickListener();
            }
        });
    }
    protected  void  setHeader_left_btnOnClickListener(){
        finish();
    }
}



