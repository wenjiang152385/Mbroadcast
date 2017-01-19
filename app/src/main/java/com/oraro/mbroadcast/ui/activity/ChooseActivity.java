package com.oraro.mbroadcast.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.oraro.mbroadcast.R;

public class ChooseActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews(savedInstanceState);
    }

    private void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_choose);
        findViewById(R.id.header_left_img).setVisibility(View.GONE);
        findViewById(R.id.click).setVisibility(View.GONE);
        findViewById(R.id.click1).setVisibility(View.GONE);
        findViewById(R.id.header_left_btn).setOnClickListener(this);
        findViewById(R.id.header_left_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.connect).setOnClickListener(this);
        findViewById(R.id.version).setOnClickListener(this);
        findViewById(R.id.settings).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connect :
                Intent connectIntent = new Intent(ChooseActivity.this,CliActivity.class);
                startActivity(connectIntent);
                break;
            case R.id.version :
                Intent versionIntent = new Intent(ChooseActivity.this,VersionActivity.class);
                startActivity(versionIntent);
                break;
            case R.id.settings :
                Intent settingsIntent = new Intent(ChooseActivity.this,SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.header_left_btn:
                finish();
        }
    }
}
