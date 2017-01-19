package com.oraro.mbroadcast.ui.activity;

/**
 * Created by Administrator on 2016/10/26 0026.
 */
public class City {
    private String alias;
    private String name;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "City{" +
                "alias='" + alias + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
