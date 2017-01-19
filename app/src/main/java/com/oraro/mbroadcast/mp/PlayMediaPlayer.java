package com.oraro.mbroadcast.mp;

import android.media.MediaPlayer;

/**
 * Created by admin on 2016/8/9
 *
 * @author zmy
 */
public interface PlayMediaPlayer {
    /**
     * 开始播放或恢复已经暂停音频的播放
     */
    void startAudio();

    /**
     * 开始播放或恢复已经暂停音频的播放
     */
    void startAudio(MediaPlayer.OnCompletionListener onCompletionListener );

    /**
     * 暂停正在播放的音频
     */
    void pauseAudio();

    /**
     * 停止正在播放的音频
     */
    void stopAudio();


    /**
     * 循环播放
     */
    void loopAudio();

    /**
     * 判断是否正在播放
     * @return
     */
    boolean isPlaying();

    /**
     * 釋放資源
     */
    void releaseAudio();
}
