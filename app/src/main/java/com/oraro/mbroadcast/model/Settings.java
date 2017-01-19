package com.oraro.mbroadcast.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by wy on 16/10/8.
 */
@Entity
public class Settings {
    @Id(autoincrement = true)
    private  Long id;
    private String settingsName;
    private String settingsValue;
    private Boolean settingsStatus;
    @Generated(hash = 761273118)
    public Settings(Long id, String settingsName, String settingsValue,
            Boolean settingsStatus) {
        this.id = id;
        this.settingsName = settingsName;
        this.settingsValue = settingsValue;
        this.settingsStatus = settingsStatus;
    }
    @Generated(hash = 456090543)
    public Settings() {
    }
    public Boolean getSettingsStatus() {
        return this.settingsStatus;
    }
    public void setSettingsStatus(Boolean settingsStatus) {
        this.settingsStatus = settingsStatus;
    }
    public String getSettingsValue() {
        return this.settingsValue;
    }
    public void setSettingsValue(String settingsValue) {
        this.settingsValue = settingsValue;
    }
    public String getSettingsName() {
        return this.settingsName;
    }
    public void setSettingsName(String settingsName) {
        this.settingsName = settingsName;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }

}
