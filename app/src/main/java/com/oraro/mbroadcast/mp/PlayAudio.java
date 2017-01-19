package com.oraro.mbroadcast.mp;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * Created by admin on 2016/8/9
 *
 * @author zmy
 */
public class PlayAudio implements PlayMediaPlayer {
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String fileAbsolutePath;

    private int currentPosition;

    private static PlayAudio instance;

    private PlayAudio() {
    }

    public static synchronized PlayAudio getInstance() {
        if (instance == null) {
            instance = new PlayAudio();
        }
        return instance;
    }

    /**
     * 重新设置播放路径，可以用于切换下一首
     *
     * @param fileAbsolutePath 所要播放文件的绝对路径
     */
    public void setPath(String fileAbsolutePath) {
        this.fileAbsolutePath = fileAbsolutePath;
        currentPosition = 0;
    }

    private boolean checkPerission(String methodname, MediaPlayer.OnCompletionListener onCompletionListener) {
        StackTraceElement stack[] = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stack.length; i++) {
            StackTraceElement ste = stack[i];

//            Log.e("huanghui", "called by ClassName = " + stack[i].getClassName() + ",MethodName = " + stack[i].getMethodName() + ",FileName = " + stack[i].getFileName());
            if (ste.getFileName().equals("PlayAudio.java") && ste.getMethodName().equals(methodname)) {
                String next = stack[i + 1].getFileName();
                if (next.equals("Service1.java") || next.equals("SpeekingService.java") || next.equals("MinaStringServerHandler.java")) {
//                    Log.e("huanghui", "have permission  called by ClassName = " + stack[i + 1].getClassName() + ",MethodName = " + stack[i + 1].getMethodName() + ",FileName = " + stack[i + 1].getFileName());
                    return false;
                } else {
                    Log.e("huanghui", "have no permission  called by ClassName = " + stack[i + 1].getClassName() + ",MethodName = " + stack[i + 1].getMethodName() + ",FileName = " + stack[i + 1].getFileName());

                    if (onCompletionListener != null) {
                        onCompletionListener.onCompletion(null);
//                        onCompletionListener.onCompletion();onCompleted(new SpeechError(new Exception("have no permission use this method,please use service1 to call this method")));
                    }
                    return true;
                }
            }
        }
        return true;
    }

    @Override
    public void startAudio(MediaPlayer.OnCompletionListener onCompletionListener) {
        if (checkPerission("startAudio", onCompletionListener)) {
            return;
        }
        try {
            if (currentPosition == 0) {
                // 1.重置
                mediaPlayer.reset();
                // 2.设置数据源(音频路径）
                mediaPlayer.setDataSource(fileAbsolutePath);
                // 3.异步准备
                mediaPlayer.prepareAsync();
                //4.设置准备完成的监听
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // 5.开始播放
                        mp.start();
                    }
                });
                mediaPlayer.setOnCompletionListener(onCompletionListener);
            } else {
                mediaPlayer.seekTo(currentPosition);
                mediaPlayer.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startAudio() {
        if (checkPerission("startAudio", null)) {
            return;
        }
        try {
            if (currentPosition == 0) {
                // 1.重置
                mediaPlayer.reset();
                // 2.设置数据源(音频路径）
                mediaPlayer.setDataSource(fileAbsolutePath);
                // 3.异步准备
                mediaPlayer.prepareAsync();
                //4.设置准备完成的监听
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // 5.开始播放
                        mp.start();
                    }
                });
            } else {
                mediaPlayer.seekTo(currentPosition);
                mediaPlayer.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 暂停正在播放的音频
     */
    @Override
    public void pauseAudio() {
        if (checkPerission("pauseAudio",null)) {
            return;
        }
        mediaPlayer.pause();
        currentPosition = mediaPlayer.getCurrentPosition();
    }

    /**
     * 停止正在播放的音频
     */
    @Override
    public void stopAudio() {
        if (checkPerission("stopAudio",null)) {
            return;
        }
        mediaPlayer.stop();
        currentPosition = 0;
    }

    /**
     * 循环播放
     */
    @Override
    public void loopAudio() {
        if (checkPerission("loopAudio",null)) {
            return;
        }
        if (mediaPlayer.isLooping()) {
            mediaPlayer.setLooping(false);
        } else {
            mediaPlayer.setLooping(true);
        }
    }

    /**
     * 判断是否正在播放
     *
     * @return
     */
    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    /**
     * 釋放資源
     */
    @Override
    public void releaseAudio() {
        mediaPlayer.release();
        instance = null;
    }
}
