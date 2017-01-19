package com.oraro.mbroadcast.tts;

import com.iflytek.cloud.SynthesizerListener;

/**
 * TTS模块，外部调用接口,一共18个方法
 * @author 刘彬
 */
public interface TTSInterface {

    /**
     * 获得播放状态
     * @return true或者false
     */
    public abstract boolean isSpeeking();

    /**
     * 语音合成，云端播放，生成语音文件
     * @param text 文本内容
     * @param voiceName 发音人
     * @param pitch 语调
     * @param speed 语速
     * @param volume 音量
     * @param path 目录，最后需要加上"/"
     * @param textName 文件名
     * @param suffix 后缀名
     */
    public abstract void TTSStartPlay(String text, String voiceName, Integer pitch, Integer speed, Integer volume, String path, String textName, String suffix);

    /**
     * 语音合成，云端播放，生成语音文件
     * @param text 文本内容
     * @param voiceName 发音人
     * @param pitch 语调
     * @param speed 语速
     * @param volume 音量
     * @param path 目录，最后需要加上"/"
     * @param textName 文件名
     * @param suffix 后缀名
     * @param synthesizerListener 播放状态监听
     */
    public abstract void TTSStartPlay(String text, String voiceName, Integer pitch, Integer speed, Integer volume, String path, String textName, String suffix, SynthesizerListener synthesizerListener);

    /**
     * 语音合成，云端播放，不生成语音文件
     * @param text 文本内容
     * @param voiceName 发音人
     * @param pitch 语调
     * @param speed 语速
     * @param volume 音量
     */
    public abstract void TTSStartPlay(String text, String voiceName, Integer pitch, Integer speed, Integer volume);

    /**
     * 暂停播放,只能把播放暂停，不能不会暂停音频的获取过程，且只在合成播放模式下有效
     */
    public abstract void TTSPausePlay();

    /**
     * 继续播放，在暂停之后，在当前暂定位置开始播放合成的音频
     */
    public abstract void TTSContinuePlay();

    /**
     * 修改默认发音人
     * @param voiceName 默认值xiaoyan，不是必须设置,不要传null
     */
    public abstract void setVoiceName(String voiceName);

    /**
     * 获取默认的发音人
     */
    public abstract String getVoiceName();

    /**
     * 修改默认语调
     * @param pitch 范围0~100,0最小值，100最大值，默认值50，不是必须设置,不要传null
     */
    public abstract void setPitch(Integer pitch);

    /**
     * 获取默认的语调
     */
    public abstract Integer getPitch();

    /**
     * 修改默认语速
     * @param speed 范围0~100，0最小值，100最大值，默认值50，不是必须设置,不要传null
     */
    public abstract void setSpeed(Integer speed);

    /**
     * 获取默认的语速
     */
    public abstract Integer getSpeed();

    /**
     * 修改默认音量
     * @param volume 范围0~100，0最小值，100最大值,不要传null
     */
    public abstract void setVolume(Integer volume);

    /**
     * 获取默认的音量
     */
    public abstract Integer getVolume();

    /**
     * 修改默认语音文件保存路径,不要传null
     * @param path 目录，最后需要加上"/"
     */
    public abstract void setPath(String path);

    /**
     * 获得默认的语音文件保存路径
     * @return 目录
     */
    public abstract String getPath();

    /**
     * 修改默认保存文件名，若不填写保存的文件名，则会使用默认的文件名oraro，同名会覆盖
     * @param textName 文件名
     */
    public abstract void setTextName(String textName);

    /**
     * 获得默认的保存文件名
     * @return 文件名
     */
    public abstract String getTextName();

    /**
     * 修改默认的音频文件保存格式
     * @param suffix 后缀名，即保存格式,如 pcm（前面不要加“.”）
     */
    public abstract void setSuffix(String suffix);

    /**
     * 获得默认的音频文件保存格式
     * @return 后缀名，即保存格式，即保存格式,如 pcm（前面不要加“.”）
     */
    public abstract String getSuffix();

    public abstract void stopPlaying();
}
