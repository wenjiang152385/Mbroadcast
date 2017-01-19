package com.oraro.mbroadcast.utils;

import com.oraro.mbroadcast.model.FlightInfoTemp;
import com.oraro.mbroadcast.vo.PlayVO;

import java.util.Date;
import java.util.List;

/**
 * Created by dongyu on 2016/9/7 0007.
 */
public class DataUtils {


    public static List addListData(List list1, List list2) {
        if (list1.isEmpty() || list2.isEmpty()) {
            return null;
        }

        for (int i = 0; i < list2.size(); i++) {
            list1.add(list2.get(i));
        }
        return list1;
    }

    public static int getNowPostion(List<PlayVO> list) {
        Date date = new Date();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getEntity().getTime().getTime() >= date.getTime()) {
                return i;
            }
        }
        return -1;
    }

    public static int getFlightNowPostion(List<FlightInfoTemp> list) {
        Date date = new Date();
        for (int i = 0; i < list.size(); i++) {
            FlightInfoTemp flightInfoTemp = list.get(i);
            if (flightInfoTemp.getPlanToTakeOffDate().getTime() >= date.getTime()) {
                return i;
            }
        }
        return -1;
    }

    public static boolean isHasId(List<PlayVO> list, long playVOId) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getEntity().getId() == playVOId) {
                return true;
            }
        }
        return false;
    }
}
