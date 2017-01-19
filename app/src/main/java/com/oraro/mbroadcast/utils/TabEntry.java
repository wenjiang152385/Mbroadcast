package com.oraro.mbroadcast.utils;

/**
 * Created by Administrator on 2016/11/4 0004.
 */
public class TabEntry {
    String tabText;
    int tabImageId;
    Class tabClass;

    public TabEntry(String text, int id, Class classes) {
        tabText = text;
        tabImageId = id;
        tabClass = classes;

    }

    public String getTabText() {
        return tabText;
    }

    public void setTabText(String tabText) {
        this.tabText = tabText;
    }

    public int getTabImageId() {
        return tabImageId;
    }

    public void setTabImageId(int tabImageId) {
        this.tabImageId = tabImageId;
    }

    public Class getTabClass() {
        return tabClass;
    }

    public void setTabClass(Class tabClass) {
        this.tabClass = tabClass;
    }
}
