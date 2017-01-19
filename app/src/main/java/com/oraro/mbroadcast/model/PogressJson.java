package com.oraro.mbroadcast.model;

/**
 * Created by dongyu  on 2016/10/22 0022.
 */

public class PogressJson {
    private  String  PogressType;
    private  int currentPogress;
    private  int total;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCurrentPogress() {
        return currentPogress;
    }

    public void setCurrentPogress(int currentPogress) {
        this.currentPogress = currentPogress;
    }

    public String getPogressType() {
        return PogressType;
    }

    public void setPogressType(String pogressType) {
        PogressType = pogressType;
    }
}
