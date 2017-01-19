package com.oraro.mbroadcast.model;

/**
 * Created by dongyu on 2016/9/2 0002.
 */
public class SimpleEvent {
    private int mMsg;
    private int mDataLength;
    private long mMsgId;
    public SimpleEvent(int mMsg) {
        this.mMsg = mMsg;
    }

    public int getMsg() {
        return mMsg;
    }

    public int getmDataLength() {
        return mDataLength;
    }

    public void setmDataLength(int mDataLength) {
        this.mDataLength = mDataLength;
    }

    public void setMsg(int mMsg) {
        this.mMsg = mMsg;
    }

    public long getmMsgId() {
        return mMsgId;
    }

    public void setmMsgId(long mMsgId) {
        this.mMsgId = mMsgId;
    }
}
