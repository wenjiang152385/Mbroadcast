package com.oraro.mbroadcast.utils;

import android.media.MediaRecorder;
import android.os.Environment;
import android.widget.ArrayAdapter;

import java.io.File;
import java.io.IOException;

/**
 * Created by dongyu on 2016/8/26 0026.
 */
public class RecAudioUtils {
    private static MediaRecorder mMediaRecorder;// MediaRecorder对象
    private static File mRecAudioFile;        // 录制的音频文件
    private static File mRecAudioPath;        // 录制的音频文件路徑
    private static String strTempFile = "recaudio_";
    public  static File audioStart(String fileName){

        try
        {
            mRecAudioPath = Environment.getExternalStorageDirectory();// 得到SD卡得路径
                    /* ①Initial：实例化MediaRecorder对象 */
            mMediaRecorder = new MediaRecorder();
                    /* ②setAudioSource/setVedioSource*/
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//设置麦克风
                    /* ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default
                     * THREE_GPP(3gp格式，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
                     * */
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default */
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    /* ②设置输出文件的路径 */
            try
            {
                mRecAudioFile = File.createTempFile(fileName, ".amr", mRecAudioPath);

            } catch (Exception e)
            {
                e.printStackTrace();
            }
            mMediaRecorder.setOutputFile(mRecAudioFile.getAbsolutePath());
                    /* ③准备 */
            mMediaRecorder.prepare();
                    /* ④开始 */
            mMediaRecorder.start();
                    /*按钮状态*/
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return mRecAudioFile;
    }

    public static void audioStop(){
        if (mRecAudioFile != null)
        {
                    /* ⑤停止录音 */
            mMediaRecorder.stop();
                    /* ⑥释放MediaRecorder */
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }
    public void getFile(){
        File file = audioStart("ff");
        audioStop();
    }
}
