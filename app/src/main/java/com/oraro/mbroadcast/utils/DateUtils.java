package com.oraro.mbroadcast.utils;

import android.util.Log;

import com.oraro.mbroadcast.model.FlightInfo;
import com.oraro.mbroadcast.model.FlightInfoTemp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dongyu on 2016/8/26 0026.
 */
public class DateUtils {
    public static int getWeek(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if(week_index == 0 ) week_index = 7;
        return week_index;
    }
    /**
     * 计算两个日期之间相差的天数
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(Date smdate,Date bdate)
    {long between_days=0;
        try {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            smdate=sdf.parse(sdf.format(smdate));
            bdate=sdf.parse(sdf.format(bdate));
            Calendar cal = Calendar.getInstance();
            cal.setTime(smdate);
            long time1 = cal.getTimeInMillis();
            cal.setTime(bdate);
            long time2 = cal.getTimeInMillis();
            between_days=(time2-time1)/(1000*3600*24);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return Integer.parseInt(String.valueOf(between_days));

    }

    public static String formatSHowDate(String str) {
        Date date = new Date(str);
        String hours = date.getHours()+"";
        String minutes = date.getMinutes()+"";
//        if (Integer.parseInt(hours) < 10) hours = "0"+hours;
        if (Integer.parseInt(minutes) < 10) minutes = "0"+minutes;
        return hours + ":" + minutes;
    }

    public static Date formatInsertDate(FlightInfo flightInfo, String str) {
        Date date = flightInfo.getPlanToTakeOffDate();
        if (null == date) {
            date = new Date(System.currentTimeMillis());
            Date date1 = new Date(date.getYear(),date.getMonth(),date.getDate(),0,0,0);
            flightInfo.setDate(date1);
        }
        String[] times = str.split(":");
        date.setHours(Integer.parseInt(times[0]));
        date.setMinutes(Integer.parseInt(times[1]));
        return date;
    }
}
