package com.oraro.mbroadcast.utils;

/**
 * Created by dongyu on 2016/10/8 0008.
 */
public class ChineseFormatUtil {
    public static String replace(String s){
       String s1 = s.replace("，",",");
        String s2 = s1.replace("。",".");
        String s3 = s2.replace("：",":");
        String s4 = s3.replace(" "," ");
        String s5 = s4.replace("！","!");
        return s5;
    }
}
