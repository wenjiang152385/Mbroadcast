package com.oraro.mbroadcast.ui.fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.R;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.mp.PlayAudio;
import com.oraro.mbroadcast.proxy.TTSProXy;
import com.oraro.mbroadcast.service.IMyAidlInterface;
import com.oraro.mbroadcast.service.OnRefreshUIListener;
import com.oraro.mbroadcast.utils.PlayStateUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by weijiaqi on 2016/8/24 0024.
 */
public class TTSDialogFragment extends BaseDialogFragment {
    private TextView mTxtDesc;
    private Button btn_play;
    private boolean isPlaying;


    @Override
    protected View initViews(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_tts, null, false);
        mTxtDesc = (TextView) view.findViewById(R.id.txt_desc);
        btn_play = (Button) view.findViewById(R.id.btn_play);
        isPlaying = (PlayStateUtils.isPlaying(getContext())) && !MBroadcastApplication.isincout;
        Log.e("huanghui","isincout = " + MBroadcastApplication.isincout);

        setTTSDialogFragmentBtn(!isPlaying);
        btn_play.setOnClickListener(mOnClickListener);
        mTxtDesc.setText(getTextDesc());
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        return view;
    }

    @Subscribe(sticky = true)
    public void onEvent(SimpleEvent event) {/* Do something */
        if (event.getMsg() == 9823442) {
            setBtn();

        }


    }

    @Override
    protected void setId(long id) {
        super.setId(id);
    }

    public void setTTSDialogFragmentBtn(boolean isTTsBtnEnable) {
        if (isTTsBtnEnable) {
            btn_play.setBackgroundResource(R.mipmap.clickedplay_dialog);
            btn_play.setEnabled(true);
        } else {
            btn_play.setBackgroundResource(R.mipmap.ttsdialog_btn);
            btn_play.setEnabled(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_play:
                    IMyAidlInterface iMyAidlInterface = MBroadcastApplication.getIMyAidlInterface();
                    try {
                        if (iMyAidlInterface != null) {

                            iMyAidlInterface.startTTSPlay(getTTSId(), mTxtDesc.getText().toString(), 1, new OnRefreshUIListener.Stub() {
                                @Override
                                public void completed(long id, String error) throws RemoteException {
                                    Log.e("huanghui", "completed = " + id);
                                    MBroadcastApplication.setPlayID((long) -1);
                                    btn_play.setBackgroundResource(R.mipmap.clickedplay_dialog);
                                    btn_play.setEnabled(true);
                                    EventBus.getDefault().post(new SimpleEvent(9823442));
                                    EventBus.getDefault().post(new SimpleEvent(Constants.A_PLAYED));
                                }

                                @Override
                                public void frushPlaying(long id) throws RemoteException {
                                    Log.e("huanghui", "frushPlaying = " + id+"  ttsid = "+getTTSId());
                                    MBroadcastApplication.setPlayID(getTTSId());
                                    btn_play.setBackgroundResource(R.mipmap.ttsdialog_btn);
                                    btn_play.setEnabled(false);
                                    isPlaying = true;
                                    EventBus.getDefault().post(new SimpleEvent(Constants.A_PLAY));
                                }
                            });
                        }
                    } catch (RemoteException e) {
                        MBroadcastApplication.setPlayID((long) -1);
                        btn_play.setBackgroundResource(R.mipmap.clickedplay_dialog);
                        btn_play.setEnabled(true);
                        EventBus.getDefault().post(new SimpleEvent(Constants.A_PLAYED));
                        e.printStackTrace();
                    }
//                    TTSInterface tts = TTSProXy.getInstance(MBroadcastApplication.getMyContext(), MBroadcastApplication.getMyPackageName());
//                    PlayAudio playAudio = PlayAudio.getInstance();
//                    if(playAudio.isPlaying()){
//                        playAudio.stopAudio();
//                    }
//                    if(tts.isSpeeking()){
//                        tts.stopPlaying();
//                    }
//                    //执行TTS逻辑
//                    tts.TTSStartPlay(mTxtDesc.getText().toString(), null, null, null, null, null, null, null, new SynthesizerListener() {
//                        @Override
//                        public void onSpeakBegin() {
//
//                        }
//
//                        @Override
//                        public void onBufferProgress(int i, int i1, int i2, String s) {
//
//                        }
//
//                        @Override
//                        public void onSpeakPaused() {
//
//                        }
//
//                        @Override
//                        public void onSpeakResumed() {
//
//                        }
//
//                        @Override
//                        public void onSpeakProgress(int i, int i1, int i2) {
//
//                        }
//
//                        @Override
//                        public void onCompleted(SpeechError speechError) {
//                            btn_play.setBackgroundResource(R.mipmap.clickedplay_dialog);
//                            btn_play.setEnabled(true);
//                        }
//
//                        @Override
//                        public void onEvent(int i, int i1, int i2, Bundle bundle) {
//
//                        }
//                    });
//                    MinaStringClientThread minaStringClientThread = new MinaStringClientThread();
//                    MinaStringClientThread minaStringClientThread1 = new MinaStringClientThread();
//                    MinaStringClientThread minaStringClientThread2 = new MinaStringClientThread();
//                    MinaStringClientThread minaStringClientThread3 = new MinaStringClientThread();
//                    minaStringClientThread.setType(Constants.TTS_PLAY);
//                    minaStringClientThread1.setType(Constants.TTS_PLAY);
//                    minaStringClientThread2.setType(Constants.TTS_PLAY);
//                    minaStringClientThread3.setType(Constants.TTS_PLAY);
//                    PlayEntry playEntry = new PlayEntry();
//                    playEntry.setTextDesc(mTxtDesc.getText().toString());
//                    PlayVO playVO = new PlayVO(playEntry);
//                    minaStringClientThread.setPlayVO(playVO);
//                    minaStringClientThread1.setPlayVO(playVO);
//                    minaStringClientThread2.setPlayVO(playVO);
//                    minaStringClientThread3.setPlayVO(playVO);
//                    if(SPUtils.hasKey(getContext(),"ip1")){
//                        Log.e("BaseBroadcastFragment", " ip1 = "+SPUtils.getPrefString(getContext(),"ip1","-1"));
//                        if (!SPUtils.getPrefString(getContext(),"ip1","-1").equals("-1")){
//                            minaStringClientThread.setIp(SPUtils.getPrefString(getContext(),"ip1","-1"));
//                            MinaStringClientThread.getThreadPoolExecutor().execute(minaStringClientThread);
//                        }
//                    }
//                    if(SPUtils.hasKey(getContext(),"ip2")){
//                        Log.e("BaseBroadcastFragment", " ip2 = "+SPUtils.getPrefString(getContext(),"ip2","-1"));
//                        if (!SPUtils.getPrefString(getContext(),"ip2","-1").equals("-1")){
//                            minaStringClientThread.setIp(SPUtils.getPrefString(getContext(),"ip2","-1"));
//                            MinaStringClientThread.getThreadPoolExecutor().execute(minaStringClientThread1);
//                        }
//                    }
//                    if(SPUtils.hasKey(getContext(),"ip3")){
//                        Log.e("BaseBroadcastFragment", " ip3 = "+SPUtils.getPrefString(getContext(),"ip3","-1"));
//                        if (!SPUtils.getPrefString(getContext(),"ip3","-1").equals("-1")){
//                            minaStringClientThread.setIp(SPUtils.getPrefString(getContext(),"ip3","-1"));
//                            MinaStringClientThread.getThreadPoolExecutor().execute(minaStringClientThread2);
//                        }
//                    }
//                    if(SPUtils.hasKey(getContext(),"ip4")){
//                        Log.e("BaseBroadcastFragment", " ip4 = "+SPUtils.getPrefString(getContext(),"ip4","-1"));
//                        if (!SPUtils.getPrefString(getContext(),"ip4","-1").equals("-1")){
//                            minaStringClientThread.setIp(SPUtils.getPrefString(getContext(),"ip4","-1"));
//                            MinaStringClientThread.getThreadPoolExecutor().execute(minaStringClientThread3);
//                        }
//                    }
                    break;
            }
        }
    };

    public void setBtn() {
        if (null != mPlayEntry.getFileParentPath()) {
            return;
        }
        btn_play.setBackgroundResource(R.mipmap.clickedplay_dialog);
        btn_play.setEnabled(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().removeStickyEvent(this);
    }

}

