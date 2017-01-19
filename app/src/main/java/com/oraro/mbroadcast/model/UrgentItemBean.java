package com.oraro.mbroadcast.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;


/**
 * Created by admin on 2016/11/25
 *
 * @author zmy
 */
@Entity
public class UrgentItemBean {
    @Id(autoincrement = true)
    private Long id;
    private Long interCutDataId;
    private Boolean isSelected = false;
    private String type = "";
    private String params = "";
    private String content;

    @Generated(hash = 1733207495)
    public UrgentItemBean(Long id, Long interCutDataId, Boolean isSelected,
            String type, String params, String content) {
        this.id = id;
        this.interCutDataId = interCutDataId;
        this.isSelected = isSelected;
        this.type = type;
        this.params = params;
        this.content = content;
    }

    @Generated(hash = 1326792083)
    public UrgentItemBean() {
    }

    @Override
    public String toString() {
        return "UrgentItemBean{" +
                "id=" + id +
                ", interCutDataId=" + interCutDataId +
                ", isSelected=" + isSelected +
                ", type='" + type + '\'' +
                ", params='" + params + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParams() {
        return this.params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsSelected() {
        return this.isSelected;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    public Long getInterCutDataId() {
        return this.interCutDataId;
    }

    public void setInterCutDataId(Long interCutDataId) {
        this.interCutDataId = interCutDataId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
