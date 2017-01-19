package com.oraro.mbroadcast.model;

import org.apache.mina.core.session.IoSession;

/**
 * Created by Administrator on 2016/11/28 0028.
 */

public class MinaFileParam {

    public static final int INVALID_MEDIA_ID = -1;

    public MinaFileParam(){

    }

    public MinaFileParam(long mediaId,
                         boolean isToSaveFile,
                         String fileMD5,
                         String toSaveFilePath,
                         String originalFilePath,
                         String originalFileName,
                         IoSession stringSession,
                         IoSession fileSession){
        mMediaId = mediaId;
        mIsToSaveFile = isToSaveFile;
        mFileMD5 = fileMD5;
        mToSaveFilePath = toSaveFilePath;
        mOriginalFilePath = originalFilePath;
        mOriginalFileName = originalFileName;
        mStringSession = stringSession;
        mFileSession = fileSession;
    }

    //在音响端接收媒体文件后，用mMediaId来更新音响端数据库对应的数据
    public long         mMediaId            = INVALID_MEDIA_ID;

    //如果为false表示平板端发送文件，如果为true表示音响端接收文件
    public boolean      mIsToSaveFile       = false;
    //如果为false表示文件传输没有完成，如果为true表示文件传输完成
    public boolean      mIsTransFileFinish  = false;

    //用于音响端保存将要接收的文件的md5值
    public String       mFileMD5            = "";
    //表示音响端将要接收的文件进行保存的路径
    public String       mToSaveFilePath     = "";
    //记录传输的文件对应在平板端的文件路径
    public String       mOriginalFilePath   = "";
    //记录传输的文件对应在平板端的文件名称
    public String       mOriginalFileName   = "";

    //在文件传输完成后，通过该指令session来通知平板端文件传输成功、失败结果
    public IoSession    mStringSession            = null;
    //文件传输通道的session
    public IoSession    mFileSession            = null;

    public IoSession getStringSession(){
        return mStringSession;
    }

    public void setFileSession(IoSession session){
        mFileSession = session;
    }

    public void setTransFileFinish(boolean isFinish){
        mIsTransFileFinish = isFinish;
    }

    public boolean isTransFileFinish(){
        return mIsTransFileFinish;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("mMediaId = " + mMediaId);
        sb.append("\n");
        sb.append("mIsToSaveFile = " + mIsToSaveFile);
        sb.append("\n");
        sb.append("mIsTransFileFinish = " + mIsTransFileFinish);
        sb.append("\n");
        sb.append("mFileMD5 = " + mFileMD5);
        sb.append("\n");
        sb.append("mToSaveFilePath = " + mToSaveFilePath);
        sb.append("\n");
        sb.append("mOriginalFilePath = " + mOriginalFilePath);
        sb.append("\n");
        sb.append("mOriginalFileName = " + mOriginalFileName);
        sb.append("\n");
        sb.append("mStringSession = " + mStringSession);
        sb.append("\n");
        sb.append("mFileSession = " + mFileSession);
        sb.append("\n");
        return sb.toString();
    }
}
