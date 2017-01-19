package com.oraro.mbroadcast.model;

/**
 * Created by dongyu on 2016/10/8 0008.
 */
public class SocketJson {
    /**
     * 用来表示类型
     */
    private int type;
    /**
     * 指令的名字
     */

    private String typeName;
    /**
     * 签名后的String字符串
     */
    private String ip;
    private String autograph;

    private PlayEntry playEntry;

    private FlightInfoTemp flightInfoTemp;

    private  String md5sum;
    private  int progess;
    public PlayEntry getPlayEntry() {
        return playEntry;
    }

    public void setPlayEntry(PlayEntry playEntry) {
        this.playEntry = playEntry;
    }

    public String getAutograph() {
        return autograph;
    }

    public void setAutograph(String autograph) {
        this.autograph = autograph;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public FlightInfoTemp getFlightInfoTemp() {
        return flightInfoTemp;
    }

    public void setFlightInfoTemp(FlightInfoTemp flightInfoTemp) {
        this.flightInfoTemp = flightInfoTemp;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMd5sum() {
        return md5sum;
    }

    public void setMd5sum(String md5sum) {
        this.md5sum = md5sum;
    }

    public int getProgess() {
        return progess;
    }

    public void setProgess(int progess) {
        this.progess = progess;
    }
}
