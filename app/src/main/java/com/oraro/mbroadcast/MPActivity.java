package com.oraro.mbroadcast;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.oraro.mbroadcast.mp.PlayAudio;

/**
 * Created by dongyu on 2016/8/12 0012.
 */
public class MPActivity extends AppCompatActivity implements View.OnClickListener  {

    private PlayAudio playAudio;
    private String path1 = "/storage/emulated/0/陈瑞 - 白狐.mp3";
    private String path2 = "/storage/emulated/0/missyou.mp3";
    private Button btn_play;
    private Button btn_next;
    private Button btn_pause;
    private Button btn_stop;
    private Button btn_loop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp);
        playAudio = PlayAudio.getInstance();
        playAudio.setPath(path1);
        initView();

    }

    private void initView() {
        btn_play = (Button) findViewById(R.id.btn_start);
        btn_play.setOnClickListener(this);
        btn_pause = (Button) findViewById(R.id.btn_pause);
        btn_pause.setOnClickListener(this);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(this);
        btn_loop = (Button) findViewById(R.id.btn_loop);
        btn_loop.setOnClickListener(this);
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                playAudio.startAudio();
                break;
            case R.id.btn_pause:
                playAudio.pauseAudio();
                break;
            case R.id.btn_stop:
                playAudio.stopAudio();
                break;
            case R.id.btn_loop:
                playAudio.loopAudio();
                break;
            case R.id.btn_next:
                playAudio.setPath(path2);
                playAudio.startAudio();
                break;
        }
    }
}
