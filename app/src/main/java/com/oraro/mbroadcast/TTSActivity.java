package com.oraro.mbroadcast;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.oraro.mbroadcast.tts.AndroidTTS;

/**
 * Created by dongyu on 2016/8/12 0012.
 */
public class TTSActivity  extends AppCompatActivity implements View.OnClickListener {

    private AndroidTTS as;

    //文字内容
    private EditText text;
    //界面语调
    private EditText pitchedit;
    //界面语速
    private EditText speededit;
    //界面音量
    private EditText volumeedit;

    //发音人
    private String voiceName=null;
    //语调
    private Integer pitch=null;
    //语速
    private Integer speed=null;
    //音量
    private Integer volume=null;

    //是否生成文件按钮
    private Button radioButton1;
    //单选按钮组
    private RadioGroup radioGroup;
    //单选按钮
    private RadioButton vixy,xiaoyu,xiaoxin,nannan;
    //播放按钮
    private Button play;
    //暂停按钮
    private Button pause;
    //继续播放按钮
    private Button continueplay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts);
        text = (EditText) findViewById(R.id.message);
        radioButton1 = (Button) findViewById(R.id.radioButton1);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup1);
        vixy = (RadioButton) findViewById(R.id.vixy);
        xiaoyu = (RadioButton) findViewById(R.id.xiaoyu);
        xiaoxin = (RadioButton) findViewById(R.id.xiaoxin);
        nannan = (RadioButton) findViewById(R.id.nannan);
        pitchedit = (EditText) findViewById(R.id.pitch);
        speededit = (EditText) findViewById(R.id.speed);
        volumeedit = (EditText) findViewById(R.id.volume);
        play = (Button) findViewById(R.id.play);
        pause = (Button) findViewById(R.id.pause);
        continueplay = (Button) findViewById(R.id.continueplay);
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        continueplay.setOnClickListener(this);
        String packageName = "com.oraro.androidtts.model";
        as = AndroidTTS.getInstance(this,packageName);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == vixy.getId()) {
                    voiceName = "vixy";
                }
                if (checkedId == xiaoyu.getId()) {
                    voiceName = "xiaoyu";
                }
                if (checkedId == xiaoxin.getId()) {
                    voiceName = "xiaoxin";
                }
                if (checkedId == nannan.getId()) {
                    voiceName = "nannan";
                }
            }
        });





    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play:
                if (text.getText().toString().equals("")){
                    Toast.makeText(this,"请输入内容！",Toast.LENGTH_SHORT).show();
                }else {
                    if (!pitchedit.getText().toString().equals("")){
                        pitch = Integer.valueOf(pitchedit.getText().toString());
                    }else{
                        pitch = null;
                    }
                    if (!speededit.getText().toString().equals("")){
                        speed = Integer.valueOf(speededit.getText().toString());
                    }else {
                        speed = null;
                    }
                    if (!volumeedit.getText().toString().equals("")){
                        volume = Integer.valueOf(volumeedit.getText().toString());
                    }else {
                        volume = null;
                    }
                    Log.e("pitch",pitch+"/");
                    Log.e("speed", speed + "/");
                    Log.e("volume", volume + "/");
                    as.TTSStartPlay(text.getText().toString(), voiceName, pitch, speed, volume, null, null, null);
                    if (!as.isSpeeking()){
                        Log.e("播报状态", "NO");
                    }else {
                        Log.e("播报状态","YES");
                    }

                }
                break;
            case R.id.pause:
                if (text.getText().toString().equals("")){
                    Toast.makeText(this,"请输入内容！",Toast.LENGTH_SHORT).show();
                }else {
                    as.TTSPausePlay();
                }
                break;
            case R.id.continueplay:
                if (text.getText().toString().equals("")){
                    Toast.makeText(this,"请输入内容！",Toast.LENGTH_SHORT).show();
                }else {
                    as.TTSContinuePlay();
                }
                break;
            default:
                break;
        }
    }
}
