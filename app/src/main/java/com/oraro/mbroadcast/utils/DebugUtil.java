package com.oraro.mbroadcast.utils;

import android.util.Log;

import java.util.Date;

/**
 * Created by wy on 16/10/9.
 */
public class DebugUtil {
    public static boolean isJinJiPlay = false;

    public static void showStartTime(String name){
        Date date = new Date();
        long mMinute =  date.getTime()%1000;

        Log.e("wyDebug",name+" 模块被执行当时间为:  "+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds()+"-"+mMinute);
    }
}
