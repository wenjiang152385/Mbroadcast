package com.oraro.mbroadcast.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2016/10/26.
 */
@Entity
public class InterCutData {
    @Id(autoincrement = true)
    private Long id;
    //插播内容
    private String text;
    //播放时长
    private long time;
    //播放间隔
    private long space;
    //是否播放
    private boolean isPlay;
    //1 代表紧急广播 0 代表温馨提示
    private int ty;

    @Generated(hash = 877199565)
    public InterCutData(Long id, String text, long time, long space,
            boolean isPlay, int ty) {
        this.id = id;
        this.text = text;
        this.time = time;
        this.space = space;
        this.isPlay = isPlay;
        this.ty = ty;
    }

    @Generated(hash = 766862162)
    public InterCutData() {
    }

    @Override
    public String toString() {
        return "InterCutData{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", time=" + time +
                ", space=" + space +
                ", isPlay=" + isPlay +
                ", ty=" + ty +
                '}';
    }

    public int getTy() {
        return this.ty;
    }

    public void setTy(int ty) {
        this.ty = ty;
    }

    public boolean getIsPlay() {
        return this.isPlay;
    }

    public void setIsPlay(boolean isPlay) {
        this.isPlay = isPlay;
    }

    public long getSpace() {
        return this.space;
    }

    public void setSpace(long space) {
        this.space = space;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
