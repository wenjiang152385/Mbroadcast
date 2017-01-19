package com.oraro.mbroadcast.ui.fragment;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.iflytek.thirdparty.B;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.listener.ITimeCallback;
import com.oraro.mbroadcast.mp.PlayAudio;
import com.oraro.mbroadcast.ui.widget.TimeText;
import com.oraro.mbroadcast.utils.RecAudioUtils;

import java.io.File;

/**
 * Created by weijiaqi on 2016/8/24 0024.
 */
public class RecordDialogFragment extends BaseDialogFragment {
    private Button mRecordBtn;
    private TextView mTxt_Record;
    private TimeText mTimeText;
    private BtnStatus btnStatus = BtnStatus.record;
    private ImageView mVolume_left;
    private ImageView mVolume_right;


    @Override
    protected View initViews(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_record, null, false);
        mVolume_left = (ImageView) view.findViewById(R.id.volume_left);
        mVolume_right = (ImageView) view.findViewById(R.id.volume_right);
        mTimeText = (TimeText) view.findViewById(R.id.time_text);
        mTxt_Record = (TextView) view.findViewById(R.id.txt_record);
        mRecordBtn = (Button) view.findViewById(R.id.btn_record);
        mTxt_Record.setText(Environment.getExternalStorageDirectory().getAbsolutePath());
        mRecordBtn.setOnClickListener(mOnClickListener);
        view.findViewById(R.id.btn_play).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.btn_delete).setOnClickListener(mOnClickListener);
        mTimeText.regesiterCallback(new ITimeCallback() {
            @Override
            public void timeCallback(String time) {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("tim", time);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }
        });
        return view;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            mTimeText.setText(bundle.getString("tim"));
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        File file = null;

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_record:
                    if (btnStatus == BtnStatus.record) {
                        file = getRecAudioFile();
                        btnStatus = BtnStatus.complete;
                        mRecordBtn.setText("完成");
                        mTimeText.startCountTime();
                        mTimeText.setVisibility(View.VISIBLE);
                        animationLogic(mVolume_left,R.drawable.animlist_left,true);
                        animationLogic(mVolume_right,R.drawable.animlist_right,true);
                    } else if (btnStatus == BtnStatus.complete) {
                        RecAudioUtils.audioStop();
                        btnStatus = BtnStatus.record;
                        mRecordBtn.setText("录音");
                        mTimeText.endCountTime();
                        mTimeText.setText("00:00");
                        mTimeText.setVisibility(View.INVISIBLE);
                        animationLogic(mVolume_left,R.drawable.animlist_left,false);
                        animationLogic(mVolume_right,R.drawable.animlist_right,false);
                    }
                    break;
                case R.id.btn_play:

                    PlayAudio playAudio = PlayAudio.getInstance();
                    playAudio.setPath(file.getPath());
                    playAudio.startAudio();

                    break;
                case R.id.btn_delete:
                    break;
            }
        }
    };

    private void animationLogic(ImageView imageView, int animId, boolean isStart) {
        imageView.setImageResource(animId);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
        if (isStart) {
            imageView.setVisibility(View.VISIBLE);
            animationDrawable.start();
        } else {
            animationDrawable.stop();
            imageView.setVisibility(View.INVISIBLE);
        }
    }

    private File getRecAudioFile() {
        return RecAudioUtils.audioStart("tts");
    }

    private enum BtnStatus {record, complete}

}
