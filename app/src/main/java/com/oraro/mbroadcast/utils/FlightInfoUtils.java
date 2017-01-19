package com.oraro.mbroadcast.utils;

import android.content.Context;
import android.util.Log;

import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.model.FlightInfoTemp;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by weijiaqi on 2016/8/25 0025.
 */
public class FlightInfoUtils {

    public static FlightInfoUtils flightInfoUtils;
    private FlightInfoTemp flightInfoTemp;

    public FlightInfoUtils getInstance() {
        return null == flightInfoUtils ? new FlightInfoUtils() : flightInfoUtils;
    }

    public void createData(Context context, long id) {
        if (null == flightInfoTemp) {
            flightInfoTemp = (FlightInfoTemp) DBManager.getInstance(context).queryById(id,DBManager.getInstance(context).getFlightInfoTempDao(DBManager.READ_ONLY));
        }
    }

    public FlightInfoTemp getInfo() {
        return flightInfoTemp;
    }

    public FlightInfoTemp getFlightInfoTemp() {

        FlightInfoTemp flightInfoTemp = new FlightInfoTemp();

        //日期
        flightInfoTemp.setDate(new Date());
        //计划起飞
       // flightInfoTemp.setPlanToTakeOff("北京");

        //性质
//        flightInfoTemp.setProperty("W/Z");
        //目的
//        flightInfoTemp.setObjective("HGH");
        //经停1
        flightInfoTemp.setStopOne("WNZ");
        //经停1起飞时间
        flightInfoTemp.setStopOneDepartureTime(new Date());
        //经停2
        flightInfoTemp.setStopTwo("上海");
        //经停2起飞时间
        flightInfoTemp.setStopTwoDepartureTime(new Date());
        //数据来源国际三码
        flightInfoTemp.setInternationalThreeYard("HGH");


        //航班号
        flightInfoTemp.setFlightNumber("MU370");
        //进出港
//        flightInfoTemp.setImportAndExport("D");
        //始发
//        flightInfoTemp.setOriginating("PEK");
        //目的站
        flightInfoTemp.setArrivalStation("杭州");
        //经停站1
        flightInfoTemp.setStopStationOne("温州");
        //经停1登机口
        flightInfoTemp.setStopOneBoardingGate("3");
        //经停站2
        flightInfoTemp.setStopStationTwo("天津");
        //经停2登机口
        flightInfoTemp.setStopTwoBoardingGate("4");


        //计划到达
//        flightInfoTemp.setPlanToArrive("百慕大");
        //国际/国内
//        flightInfoTemp.setInternationalOrDomestic("I");
        //始发站
        flightInfoTemp.setDeparture("北京");
        //登机口
        flightInfoTemp.setBoardingGate("1");
        //经停1降落时间
        flightInfoTemp.setStopOneFalTime(new Date());
        //经停2降落时间
        flightInfoTemp.setStopTwoFalTime(new Date());


        return flightInfoTemp;
    }


//    public static final String[] PART = {"日期", "计划起飞", "性质", "目的(3码)", "经停1(3码)",
//            "经停1起飞时间", "经停2", "经停2起飞时间", "数据源来自(国际三码)", "航班号", "进出港", "始发(3码)", "目的站",
//            "经停站1", "经停1登机口", "经停站2", "经停2登机口", "计划到达", "国际/国内", "始发站",
//            "登机口", "经停1降落时间", "占位", "经停2降落时间"};

    public String getTextByTitle(String title) {
        String str = "";
        switch (title) {
            case "日期":
                str = DataFormat(flightInfoTemp.getDate())+"";
                break;
            case "计划起飞":
              //  str = flightInfoTemp.getPlanToTakeOff();
                break;
            case "性质":
//                str = flightInfoTemp.getProperty();
                break;
            case "目的(3码)":
//                str = flightInfoTemp.getObjective();
                break;
            case "经停1(3码)":
                str = flightInfoTemp.getStopOne();
                break;
            case "经停1起飞时间":
                str = TimeFormat(flightInfoTemp.getStopOneDepartureTime()) + "";
                break;
            case "经停2":
                str = flightInfoTemp.getStopTwo();
                break;
            case "经停2起飞时间":
                str = TimeFormat(flightInfoTemp.getStopTwoDepartureTime()) +"";
                break;
            case "数据源来自(国际三码)":
                str = flightInfoTemp.getInternationalThreeYard();
                break;
            case "航班号":
                str = flightInfoTemp.getFlightNumber();
                break;
            case "进出港":
//                str = flightInfoTemp.getImportAndExport();
                break;
            case "始发(3码)":
//                str = flightInfoTemp.getOriginating();
                break;
            case "目的站":
                str = flightInfoTemp.getArrivalStation();
                break;
            case "经停站1":
                str = flightInfoTemp.getStopStationOne();
                break;
            case "经停1登机口":
                str = flightInfoTemp.getStopOneBoardingGate();
                break;
            case "经停站2":
                str = flightInfoTemp.getStopStationTwo();
                break;
            case "经停2登机口":
                str = flightInfoTemp.getStopTwoBoardingGate();
                break;
            case "计划到达":
//                str = flightInfoTemp.getPlanToArrive();
                break;
            case "国际/国内":
//                str = flightInfoTemp.getInternationalOrDomestic();
                break;
            case "始发站":
                str = flightInfoTemp.getDeparture();
                break;
            case "登机口":
                str = flightInfoTemp.getBoardingGate();
                break;
            case "经停1降落时间":
                str =TimeFormat(flightInfoTemp.getStopOneFalTime())+"";
                break;
            case "占位":
                str = "";
                break;
            case "经停2降落时间":
                str = TimeFormat(flightInfoTemp.getStopTwoFalTime())+"";
                break;
            default:
                break;
        }

        if (str == null || str.equals("")) {
            str = "无";
        }

        return str;

    }

    private String DataFormat(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(null==date){
            return "";
        }
        String str = simpleDateFormat.format(date).toString();
        return str;
    }

    private String TimeFormat(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        if(null==date){
            return "";
        }
        String str = simpleDateFormat.format(date).toString();
        return str;
    }


}
