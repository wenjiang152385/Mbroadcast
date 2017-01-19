package com.oraro.mbroadcast.proxy;

import android.content.Context;
import android.util.Log;

import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;
import com.oraro.mbroadcast.tts.AndroidTTS;
import com.oraro.mbroadcast.tts.TTSInterface;
import com.oraro.mbroadcast.utils.TTSControlUtil;

/**
 * 代理TTS，处理航班号正确播报
 *
 * @author 王子榕
 */
public class TTSProXy implements TTSInterface {
    private static AndroidTTS tts;
    private static TTSProXy ttsProXy;

    public static TTSInterface getInstance(Context context, String packageName) {
        if (tts == null) {
            tts = AndroidTTS.getInstance(context, packageName);
        }
        if (ttsProXy == null) {
            ttsProXy = new TTSProXy();
        }

        return ttsProXy;

    }

    @Override
    public boolean isSpeeking() {
        return tts.isSpeeking();
    }

    @Override
    public void TTSStartPlay(String text, String voiceName, Integer pitch, Integer speed, Integer volume, String path, String textName, String suffix) {
        tts.TTSStartPlay(TTSControlUtil.playFormat(text), voiceName, pitch, speed, volume, path, textName, suffix);
    }

    private boolean checkPerission(String methodname, SynthesizerListener synthesizerListener) {
        StackTraceElement stack[] = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stack.length; i++) {
            StackTraceElement ste = stack[i];

//            Log.e("huanghui", "called by ClassName = " + stack[i].getClassName() + ",MethodName = " + stack[i].getMethodName() + ",FileName = " + stack[i].getFileName());
            if (ste.getFileName().equals("TTSProXy.java") && ste.getMethodName().equals(methodname)) {
                String next = stack[i + 1].getFileName();
                if (next.equals("Service1.java") || next.equals("SpeekingService.java") || next.equals("MinaStringServerHandler.java")) {
//                    Log.e("huanghui", "have permission  called by ClassName = " + stack[i + 1].getClassName() + ",MethodName = " + stack[i + 1].getMethodName() + ",FileName = " + stack[i + 1].getFileName());
                    return false;
                } else {
                    Log.e("huanghui", "have no permission  called by ClassName = " + stack[i + 1].getClassName() + ",MethodName = " + stack[i + 1].getMethodName() + ",FileName = " + stack[i + 1].getFileName() );

                    if (synthesizerListener != null) {
                        synthesizerListener.onCompleted(new SpeechError(new Exception("have no permission use this method,please use service1 to call this method")));
                    }
                    return true;
                }
            }
        }
        return true;
    }

    @Override
    public void TTSStartPlay(String text, String voiceName, Integer pitch, Integer speed, Integer volume, String path, String textName, String suffix, SynthesizerListener synthesizerListener) {
        if (checkPerission("TTSStartPlay", synthesizerListener)) {
            return;
        }
        tts.TTSStartPlay(TTSControlUtil.playFormat(text), voiceName, pitch, speed, volume, path, textName, suffix, synthesizerListener);
    }

    @Override
    public void TTSStartPlay(String text, String voiceName, Integer pitch, Integer speed, Integer volume) {
        if (checkPerission("TTSStartPlay", null)) {
            return;
        }
        tts.TTSStartPlay(TTSControlUtil.playFormat(text), voiceName, pitch, speed, volume);
    }

    @Override
    public void TTSPausePlay() {
        if (checkPerission("TTSPausePlay", null)) {
            return;
        }
        tts.TTSPausePlay();
    }

    @Override
    public void TTSContinuePlay() {
        if (checkPerission("TTSContinuePlay", null)) {
            return;
        }
        tts.TTSContinuePlay();
    }

    @Override
    public void setVoiceName(String voiceName) {
        if (checkPerission("setVoiceName", null)) {
            return;
        }
        tts.setVoiceName(voiceName);
    }

    @Override
    public String getVoiceName() {
        return tts.getVoiceName();
    }

    @Override
    public void setPitch(Integer pitch) {
        tts.setPitch(pitch);
    }

    @Override
    public Integer getPitch() {
        return tts.getPitch();
    }

    @Override
    public void setSpeed(Integer speed) {
        tts.setSpeed(speed);
    }

    @Override
    public Integer getSpeed() {
        return tts.getSpeed();
    }

    @Override
    public void setVolume(Integer volume) {
        tts.setVolume(volume);
    }

    @Override
    public Integer getVolume() {
        return tts.getVolume();
    }

    @Override
    public void setPath(String path) {
        tts.setPath(path);
    }

    @Override
    public String getPath() {
        return tts.getPath();
    }

    @Override
    public void setTextName(String textName) {
        tts.setTextName(textName);
    }

    @Override
    public String getTextName() {
        return tts.getTextName();
    }

    @Override
    public void setSuffix(String suffix) {
        tts.setSuffix(suffix);
    }

    @Override
    public String getSuffix() {
        return tts.getSuffix();
    }

    @Override
    public void stopPlaying() {
        if (checkPerission("stopPlaying", null)) {
            return;
        }
        tts.stopPlaying();
    }
}
