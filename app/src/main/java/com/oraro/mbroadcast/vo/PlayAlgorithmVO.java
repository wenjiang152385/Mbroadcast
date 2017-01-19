package com.oraro.mbroadcast.vo;

import com.oraro.mbroadcast.model.PlayEntry;

/**
 * Created by wy on 16/9/22.
 */
public class PlayAlgorithmVO {
    private int weightValue = 0;
    private PlayEntry pe;

    public int getWeightValue() {
        return weightValue;
    }

    public void setWeightValue(int weightValue) {
        this.weightValue = weightValue;
    }

    public PlayEntry getPe() {
        return pe;
    }

    public void setPe(PlayEntry pe) {
        this.pe = pe;
    }

    @Override
    public String toString() {
        return "航班号:"
                +getPe().getFlightInfoTemp().getFlightNumber()
                +",权值:"+getWeightValue()
                +",已播放:"+getPe().getDoTimes()+"/"+getPe().getTimes();
    }
}
