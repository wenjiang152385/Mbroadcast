package com.oraro.mbroadcast.tts;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;
import com.oraro.mbroadcast.exception.TTSException;

/**
 * 接口实现类
 * Created by admin on 2016/8/3.
 * @author 刘彬
 */
public class AndroidTTS implements TTSInterface {

    private static AndroidTTS adtts;
    private SpeechSynthesizer mTts;
    private Context mContext;
    private SharedPreferences preferences;
    private PackageManager pm;
    //以下为类的配置属性
    /**
     * 发音人
     */
    private String voiceName;
    /**
     * 语调
     */
    private Integer pitch;
    /**
     * 语速
     */
    private Integer speed;
    /**
     * 音量
     */
    private Integer volume;
    /**
     * 路径
     */
    private String path;
    /**
     * 文件保存名称
     */
    private String textName;
    /**
     * 文件保存格式
     */
    private String suffix;
    /**
     * 私有无参数构造，外部无法new出对象，必须要用下面的getInstance方法
     */
    private AndroidTTS(){}

    /**
     * 单例模式
     * @param context 传Activity.this
     * @return AndroidTTS实例
     */
    public static AndroidTTS getInstance(Context context,String packageName){
        if (adtts == null){
            adtts = new AndroidTTS();
            adtts.mContext = context;
            try {
                adtts.checkPermission(adtts.mContext,packageName);
            } catch (TTSException e) {
                e.getMessage();
                e.printStackTrace();
            }
            //TTS语音初始化
            StringBuffer param = new StringBuffer();
            param.append("appid=58787d40");
            param.append(",");
            param.append(SpeechConstant.ENGINE_MODE+"="+SpeechConstant.MODE_MSC);
//            param.append(",");
//            param.append(SpeechConstant.FORCE_LOGIN+"=true");
            SpeechUtility.createUtility(adtts.mContext,param.toString());
            //初始化单例对象mTts
            adtts.mTts = SpeechSynthesizer.createSynthesizer(adtts.mContext,adtts.myInitListener);
            //sp文件对象
            adtts.preferences = adtts.mContext.getSharedPreferences("data", adtts.mContext.MODE_PRIVATE);
            //初始化的时候更新类配置属性值
            adtts.updateSetting();
        }
        return adtts;
    }

    /**
     * 更新类配置属性
     */
    private void updateSetting(){
        adtts.voiceName = adtts.getVoiceName();
        adtts.pitch = adtts.getPitch();
        adtts.speed = adtts.getSpeed();
        adtts.volume = adtts.getVolume();
        adtts.path = adtts.getPath();
        adtts.textName = adtts.getTextName();
        adtts.suffix = adtts.getSuffix();
    }

    /**
     * 审核是否有相应的权限
     * @param context 上下文
     * @param packageName 包名
     * @throws TTSException
     */
    public void checkPermission(Context context,String packageName)throws TTSException {
        pm = context.getPackageManager();
        boolean flage1=(PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.INTERNET",packageName));
        boolean flage2=(PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.ACCESS_NETWORK_STATE",packageName));
        boolean flage3=(PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE",packageName));
        boolean flage4=(PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.READ_EXTERNAL_STORAGE",packageName));
        if (!flage1){
            throw new TTSException("没有连接网络权限！");
        }
        if (!flage2){
            throw new TTSException("没有读取网络信息状态权限！");
        }
        if (!flage3){
            throw new TTSException("没有写的权限！");
        }
        if (!flage4){
            throw new TTSException("没有读的权限！");
        }
    }

    /**
     * 初始化单例对象时，通过回调接口，获取初始化状态
     */
    private InitListener myInitListener = new InitListener() {
        @Override
        public void onInit(int i) {
            Log.e("mySynthesizer:", "单例对象创建成功，InitListener init() code = " + i);
            if(i != ErrorCode.SUCCESS){
                Toast.makeText(adtts.mContext,"初始化失败，错误码："+i,Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * 因为很多方法需要用到这段代码，所以我提取了
     * @param voiceName 发音人
     * @param pitch 语调
     * @param speed 语速
     * @param volume 音量
     */
    public void setSomeParameter(String voiceName, Integer pitch, Integer speed, Integer volume){
        //清空参数
        adtts.mTts.setParameter(SpeechConstant.PARAMS,null);
        //设置发音人资源路径
        String p = getResourcePath(voiceName);
        adtts.mTts.setParameter(ResourceUtil.TTS_RES_PATH,p.toString());
        //设置发音人
        adtts.mTts.setParameter(SpeechConstant.VOICE_NAME, voiceName);
        //设置语调
        adtts.mTts.setParameter(SpeechConstant.PITCH, pitch.toString());
        //设置语速
        adtts.mTts.setParameter(SpeechConstant.SPEED, speed.toString());
        //设置音量
        adtts.mTts.setParameter(SpeechConstant.VOLUME, volume.toString());
        //设置播放器音频流类型
        adtts.mTts.setParameter(SpeechConstant.STREAM_TYPE,"3");
        //设置本端引擎
        adtts.mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
    }

    /**
     * 获取发音人资源路径
     * @return
     */
    private String getResourcePath(String voiceName){
        StringBuffer tempBuffer = new StringBuffer();
        //合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(adtts.mContext, ResourceUtil.RESOURCE_TYPE.assets,"tts/common.jet"));
        tempBuffer.append(";");
        //发音人资源
        //tempBuffer.append(ResourceUtil.generateResourcePath(adtts.mContext, ResourceUtil.RESOURCE_TYPE.assets,"tts/"+voiceName+".jet"));
        tempBuffer.append(ResourceUtil.generateResourcePath(adtts.mContext, ResourceUtil.RESOURCE_TYPE.assets,"tts/"+voiceName+".jet"));
        return tempBuffer.toString();
    }

    @Override
    public void TTSStartPlay(String text,String voiceName, Integer pitch, Integer speed, Integer volume, String path, String textName, String suffix) {
        if ( null == voiceName){
            voiceName = adtts.voiceName;
        }
        if ( null == pitch){
            pitch = adtts.pitch;
        }
        if ( null == speed){
            speed = adtts.speed;
        }
        if ( null == volume){
            volume = adtts.volume;
        }
        if ( null == path){
            path = adtts.path;
        }
        if ( null == textName){
            textName = adtts.textName;
        }
        if ( null == suffix){
            suffix = adtts.suffix;
        }
        //配置参数
        adtts.setSomeParameter(voiceName, pitch, speed, volume);
        //拼接路径
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(path);
        stringBuffer.append(textName);
        stringBuffer.append(".");
        stringBuffer.append(suffix);
        String spath = stringBuffer.toString();
        //设置保存路径
        adtts.mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH,spath);
        //开始合成
        adtts.mTts.startSpeaking(text,null);
    }

    @Override
    public void TTSStartPlay(String text, String voiceName, Integer pitch, Integer speed, Integer volume, String path, String textName, String suffix, SynthesizerListener synthesizerListener) {
        if ( null == voiceName){
            voiceName = adtts.voiceName;
        }
        if ( null == pitch){
            pitch = adtts.pitch;
        }
        if ( null == speed){
            speed = adtts.speed;
        }
        if ( null == volume){
            volume = adtts.volume;
        }
        if ( null == path){
            path = adtts.path;
        }
        if ( null == textName){
            textName = adtts.textName;
        }
        if ( null == suffix){
            suffix = adtts.suffix;
        }
        //配置参数
        adtts.setSomeParameter(voiceName, pitch, speed, volume);
        //拼接路径
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(path);
        stringBuffer.append(textName);
        stringBuffer.append(".");
        stringBuffer.append(suffix);
        String spath = stringBuffer.toString();
        //设置保存路径
        adtts.mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH,spath);
        //开始合成
        adtts.mTts.startSpeaking(text,synthesizerListener);
    }


    @Override
    public void TTSStartPlay(String text, String voiceName, Integer pitch, Integer speed, Integer volume) {
        if ( null == voiceName){
            voiceName = adtts.voiceName;
        }
        if ( null == pitch){
            pitch = adtts.pitch;
        }
        if ( null == speed){
            speed = adtts.speed;
        }
        if ( null == volume){
            volume = adtts.volume;
        }
        //配置参数
        adtts.setSomeParameter(voiceName, pitch, speed, volume);
        //开始合成
        adtts.mTts.startSpeaking(text, null);
    }

    @Override
    public void TTSPausePlay() {
        adtts.mTts.pauseSpeaking();
    }

    @Override
    public void TTSContinuePlay() {
        adtts.mTts.resumeSpeaking();
    }

    @Override
    public void setVoiceName(String voiceName) {
        SharedPreferences.Editor editor = adtts.preferences.edit();
        editor.putString("voiceName", voiceName);
        editor.commit();
        adtts.updateSetting();
    }

    @Override
    public String getVoiceName() {
        String voiceName = adtts.preferences.getString("voiceName", "xiaoyan");
        return voiceName;
    }

    @Override
    public void setPitch(Integer pitch) {
        SharedPreferences.Editor editor = adtts.preferences.edit();
        editor.putInt("pitch",pitch);
        editor.commit();
        adtts.updateSetting();
    }

    @Override
    public Integer getPitch() {
        Integer pitch = adtts.preferences.getInt("pitch", 50);
        return pitch;
    }

    @Override
    public void setSpeed(Integer speed) {
        SharedPreferences.Editor editor = adtts.preferences.edit();
        editor.putInt("speed",speed);
        editor.commit();
        adtts.updateSetting();
    }

    @Override
    public Integer getSpeed() {
        Integer speed = adtts.preferences.getInt("speed", 50);
        return speed;
    }

    @Override
    public void setVolume(Integer volume) {
        SharedPreferences.Editor editor = adtts.preferences.edit();
        editor.putInt("volume", volume);
        editor.commit();
        adtts.updateSetting();
    }

    @Override
    public Integer getVolume() {
        Integer volume = adtts.preferences.getInt("volume", 80);
        return volume;
    }

    @Override
    public void setPath(String path) {
        SharedPreferences.Editor editor = adtts.preferences.edit();
        editor.putString("path",path);
        editor.commit();
        adtts.updateSetting();
    }

    @Override
    public String getPath() {
        String path = adtts.preferences.getString("path", "./sdcard/");
        return path;
    }

    @Override
    public void setTextName(String textName) {
        SharedPreferences.Editor editor = adtts.preferences.edit();
        editor.putString("textName",textName);
        editor.commit();
        adtts.updateSetting();
    }

    @Override
    public String getTextName() {
        String textName = adtts.preferences.getString("textName", "oraro");
        return textName;
    }

    @Override
    public void setSuffix(String suffix) {
        SharedPreferences.Editor editor = adtts.preferences.edit();
        editor.putString("suffix",suffix);
        editor.commit();
        adtts.updateSetting();
    }

    @Override
    public String getSuffix() {
        String suffix = adtts.preferences.getString("suffix","pcm");
        return suffix;
    }

    @Override
    public void stopPlaying() {
        adtts.mTts.stopSpeaking();
    }

    @Override
    public boolean isSpeeking() {
        return adtts.mTts.isSpeaking();
    }


}
