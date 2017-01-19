package com.oraro.mbroadcast.vo;

import java.io.Serializable;

/**
 * Created by admin on 2016/11/9.
 * 播报设置的字段对象
 * @author liubin
 */
public class FieldsSelectVO implements Serializable{
    //fieldSelect.xml中required标签，值为1表示必选，0表示不必选
    public static final int REQUIRED_VALUE = 1;
    //fieldSelect.xml中no_required_default标签，值为1表示不必选中的默认值，0表示不是不必选中的默认值
    public static final int NO_REQUIRED_DEFAULT_VALUE = 1;

    public static final String TAG_MAPS                = "maps";
    public static final String TAG_MAP                 = "map";
    public static final String TAG_KEY                 = "key";
    public static final String TAG_VALUE               = "value";
    public static final String TAG_REQUIRED            = "required";
    public static final String TAG_NO_REQUIRED_DEFAULT = "no_required_default";

    //key
    private String mKey;
    //value
    private String mValue;
    //true:必选    false:可选
    private boolean mIsRequired;
    //true:不必选中的默认值 false:不是不必选中的默认值
    private boolean mIsNoRequiredDefault;
    //true:不必选中的一个字段被勾选 false:不必选中的一个字段没有被勾选
    private boolean mIsNoRequiredSet;

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }

    public boolean isRequired() {
        return mIsRequired;
    }

    public void setRequired(boolean aBoolean) {
        mIsRequired = aBoolean;
    }

    public boolean isNoRequiredDefault() {
        return mIsNoRequiredDefault;
    }

    public void setNoRequiredDefault(boolean aBoolean) {
        mIsNoRequiredDefault = aBoolean;
    }

    public boolean isNoRequiredSet() {
        return mIsNoRequiredSet;
    }

    public void setNoRequiredSet(boolean aBoolean) {
        mIsNoRequiredSet = aBoolean;
    }

}
