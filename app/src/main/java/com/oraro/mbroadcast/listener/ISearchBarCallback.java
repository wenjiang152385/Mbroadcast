package com.oraro.mbroadcast.listener;

import com.oraro.mbroadcast.model.FlightInfoTemp;

/**
 * Created by Administrator on 2016/9/7 0007.
 */
public interface ISearchBarCallback {

    void setItemInfo(FlightInfoTemp flightInfoTemp, int position);

    void setChangeEditText(String text);

}
