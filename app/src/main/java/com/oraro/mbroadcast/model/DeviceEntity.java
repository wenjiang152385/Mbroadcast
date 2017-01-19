package com.oraro.mbroadcast.model;

import com.google.gson.JsonObject;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Administrator on 2016/9/29 0029.
 */
@Entity
public class DeviceEntity {
    @Id(autoincrement = true)
    private Long id;
    private String ip;
    private String mac;
    private String nickname;
    private boolean isRoot;
    private long values;
    private boolean isblack;
    private int status;


    @Generated(hash = 540593481)
    public DeviceEntity(Long id, String ip, String mac, String nickname,
            boolean isRoot, long values, boolean isblack, int status) {
        this.id = id;
        this.ip = ip;
        this.mac = mac;
        this.nickname = nickname;
        this.isRoot = isRoot;
        this.values = values;
        this.isblack = isblack;
        this.status = status;
    }

    @Generated(hash = 1449836520)
    public DeviceEntity() {
    }


    public boolean isblack() {
        return isblack;
    }

    public void setIsblack(boolean isblack) {
        this.isblack = isblack;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public long getValues() {
        return values;
    }

    public void setValues(long values) {
        this.values = values;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String jsonToString() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ip", ip);
        jsonObject.addProperty("mac", mac);
        jsonObject.addProperty("nickname", nickname);
        jsonObject.addProperty("isRoot", true);
        return jsonObject.toString();
    }

    @Override
    public String toString() {
        return "DeviceEntity{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                ", mac='" + mac + '\'' +
                ", nickname='" + nickname + '\'' +
                ", isRoot=" + isRoot +
                ", values=" + values +
                ", isblack=" + isblack +
                ", status=" + status +
                '}';
    }

    public boolean getIsblack() {
        return this.isblack;
    }

    public boolean getIsRoot() {
        return this.isRoot;
    }

    public void setIsRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }
}
