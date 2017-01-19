package com.oraro.mbroadcast.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.model.PlayEntry;

/**
 * Created by dongyu on 2016/8/30 0030.
 */
public class PlayVO implements Parcelable {
    private boolean isChecked;
    private PlayEntry entity;
    private boolean isInQueue = false;
    private boolean isPlayNow = false;
    public PlayVO(PlayEntry entity){
        this.entity = entity;
    }

    protected PlayVO(Parcel in) {
        isInQueue = in.readByte() != 0;
        isPlayNow = in.readByte() != 0;
    }

    public static final Creator<PlayVO> CREATOR = new Creator<PlayVO>() {
        @Override
        public PlayVO createFromParcel(Parcel in) {
            return new PlayVO(in);
        }

        @Override
        public PlayVO[] newArray(int size) {
            return new PlayVO[size];
        }
    };

    public boolean isChecked(){
        return  isChecked;
    }

    public void setChecked(boolean check){
        isChecked = check;
    }

    public PlayEntry getEntity() {
        return entity;
    }

    public void setEntity(PlayEntry entity) {
        this.entity = entity;
    }

    public boolean isInQueue() {
        return isInQueue;
    }

    public void setIsInQueue(boolean isInQueue) {
        this.isInQueue = isInQueue;
    }

    public boolean isPlayNow() {
        if(MBroadcastApplication.getPlayID()!=null
                && entity != null
                && MBroadcastApplication.getPlayID().equals(entity.getId())){
            return true;
        }
        return isPlayNow;
    }

    public void setIsPlayNow(boolean isPlayNow) {
        this.isPlayNow = isPlayNow;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isInQueue ? 1 : 0));
        dest.writeByte((byte) (isPlayNow ? 1 : 0));
    }
}
