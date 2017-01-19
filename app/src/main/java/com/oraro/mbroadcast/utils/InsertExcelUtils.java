package com.oraro.mbroadcast.utils;

import android.content.Context;
import android.util.Log;

import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.model.FlightInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/22 0022.
 */
public class InsertExcelUtils {
    static SimpleDateFormat dateFormat = null;
    static SimpleDateFormat timeFormat = null;

    static {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    //用于Excel解析赋值属性的方法，不能删除
    private String mDateStr = "";
    private static Map<String, String> mCityMap;

    public static void init() {
        Context context = MBroadcastApplication.getMyContext();
        MAPXmlPullParser cityXmlPullParser = new MAPXmlPullParser(context, "city.xml");
        mCityMap = cityXmlPullParser.parseByPull();

    }

    public static void setData(String key, Date value, FlightInfo info) {
        switch (key) {
            case "JingTing1JiangLuoShiJian":
                info.setStopOneFalTime(value);
                break;
            case "JingTing1QiFeiShiJian":
                info.setStopOneDepartureTime(value);
                break;
            case "JingTing2JiangLuoShiJian":
                info.setStopTwoFalTime(value);
                break;
            case "JingTing2QiFeiShiJian":
                info.setStopTwoDepartureTime(value);
                break;
        }
    }

    private static Object parseData(String key, String value) {
        Object obj = "";
        switch (key) {
            case "flightNumber":
                obj = checkKey(value);
                break;

            case "delayInfo":
                if (value == null || value == "") {
                    obj = "未延误";
                } else {
                    obj = value;
                }
                break;

            case "arrive":
                obj = value;
                for (Map.Entry<String, String> entry : mCityMap.entrySet()) {
                    String entryKey = entry.getKey();
                    if (entryKey.contains(value)) {
                        obj = entry.getValue();
                    }
                }
                break;
            default:
                break;
        }
        return obj;
    }

    private static String checkKey(String value) {
        String newValue = "";
        int firstIndex = -1;
        boolean isDigit = false;//定义一个boolean值，用来表示是否包含数字
        boolean isLetter = false;//定义一个boolean值，用来表示是否包含字母
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i)) && !(Character.isLetter(value.charAt(i)))) {
                firstIndex = i;
                break;
            }
        }

        if (value.length() >= firstIndex && firstIndex != -1) {
            newValue = value.substring(0, firstIndex);
        } else if (firstIndex == -1) {
            newValue = value;
        }

        return newValue;

    }

    public static void setData(String key, String value, FlightInfo info) {
        switch (key) {
            case "flightNumber":
                info.setFlightNumber(String.valueOf(parseData(key, value)));
                break;

            case "planeType":
                info.setPlaneType(value);
                break;

            case "arrivalStation":
                info.setArrivalStation(String.valueOf(parseData("arrive", value)));
                break;

            case "delayInfo":
                info.setDelayInfo(String.valueOf(parseData("delayInfo", value)));
                break;

            case "remarks":
                info.setRemarks(value);
                break;


            case "departure":
                info.setDeparture(value);
                break;


            case "planeNumber":
                info.setPlaneNumber(value);
                break;


            case "proxy":
                info.setProxy(value);
                break;

            case "planToTakeOffDate":
                //如果起飞时间是从数值类型转换的，则其值是带有小数点的，需要去除小数点以及后面的值
                if(value.contains(".")){
                    value = value.substring(0,value.indexOf("."));
                }
                String launch0 = value.trim().substring(0, 2);
                String launch1 = value.trim().substring(2, value.length());
//                value = launch0 + ":" + launch1;
                Date date = info.getDate();
                try{
                    int launch01=Integer.valueOf(launch0);
                    int launch02=Integer.valueOf(launch1);
                    Date takeOffDate = new Date(date.getYear(), date.getMonth(), date.getDate(),launch01, launch02, 0);
                    info.setPlanToTakeOffDate(takeOffDate);
                }
                catch (Exception e){
                    LogUtils.e("dy",e.toString() );
                }

                break;

            case "planePosition":
                info.setPlanePosition(value);
                break;

            case "buildingNumber":
                info.setBuildingNumber(value);
                break;

            case "area":
//                info.setInternationalOrDomestic(value);
                break;
            case "property":
//                info.setProperty(value);
                break;

            case "start3":
//                info.setOriginating(value);
                break;

            case "aim3":
//                info.setObjective(value);
                break;


            case "DengJiKou":
                info.setBoardingGate(value);
                break;
            case "JingTing1":
                info.setStopOne(value);
                break;
            case "JingTingZhan1":
                info.setStopStationOne(value);
                break;
            case "JingTing1DengJiKou":
                info.setStopOneBoardingGate(value);
                break;
            case "JingTing2":
                info.setStopTwo(value);
                break;
            case "JingTingZhan2":
                info.setStopStationTwo(value);
                break;
            case "JingTing2DengJiKou":
                info.setStopTwoBoardingGate(value);
                break;
            case "ShuJuYuanMa":
                info.setInternationalThreeYard(value);
                break;
            default:
                break;
        }
    }

    public String getMDateStr() {
        return this.mDateStr;
    }

    public void setMDateStr(String mDateStr) {
        this.mDateStr = mDateStr;
    }

//    //用于Excel解析赋值属性的方法，不能删除
//    public String beanToString(){
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("date",date);
//            jsonObject.put("flightNumber",flightNumber);
//            jsonObject.put("arrive",planToArrive);
//            jsonObject.put("launch",planToTakeOff);
//            jsonObject.put("inOut",importAndExport);
//            jsonObject.put("area",internationalOrDomestic);
//            jsonObject.put("property",property);
//            jsonObject.put("start3",start3);
//            jsonObject.put("startStation",startStation);
//            jsonObject.put("aim3",aim3);
//            jsonObject.put("aimStation",aimStation);
//            jsonObject.put("DengJiKou",DengJiKou);
//            jsonObject.put("JingTing1",JingTing1);
//            jsonObject.put("JingTingZhan1",JingTingZhan1);
//            jsonObject.put("JingTing1JiangLuoShiJian",JingTing1JiangLuoShiJian);
//            jsonObject.put("JingTing1QiFeiShiJian",JingTing1QiFeiShiJian);
//            jsonObject.put("JingTing1DengJiKou",JingTing1DengJiKou);
//            jsonObject.put("JingTing2",JingTing2);
//            jsonObject.put("JingTingZhan2",JingTingZhan2);
//            jsonObject.put("JingTing2JiangLuoShiJian",JingTing2JiangLuoShiJian);
//            jsonObject.put("JingTing2QiFeiShiJian",JingTing2QiFeiShiJian);
//            jsonObject.put("JingTing2DengJiKou",JingTing2DengJiKou);
//            jsonObject.put("ShuJuYuanMa",ShuJuYuanMa);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return jsonObject.toString();
//    }
//
//    //用于Excel解析赋值属性的方法，不能删除
//    public void stringToBean(String json){
//        try {
//            JSONObject jsonObject = new JSONObject(json);
//            date = jsonObject.getString("date");
//            flightNumber = jsonObject.getString("flightNumber");
//            arrive = jsonObject.getString("arrive");
//            launch = jsonObject.getString("launch");
//            inOut = jsonObject.getString("inOut");
//            area = jsonObject.getString("area");
//            property = jsonObject.getString("property");
//            start3 = jsonObject.getString("start3");
//            startStation = jsonObject.getString("startStation");
//            aim3 = jsonObject.getString("aim3");
//            aimStation = jsonObject.getString("aimStation");
//            DengJiKou = jsonObject.getString("DengJiKou");
//            JingTing1 = jsonObject.getString("JingTing1");
//            JingTingZhan1 = jsonObject.getString("JingTingZhan1");
//            JingTing1JiangLuoShiJian = jsonObject.getString("JingTing1JiangLuoShiJian");
//            JingTing1QiFeiShiJian = jsonObject.getString("JingTing1QiFeiShiJian");
//            JingTing1DengJiKou = jsonObject.getString("JingTing1DengJiKou");
//            JingTing2 = jsonObject.getString("JingTing2");
//            JingTingZhan2 = jsonObject.getString("JingTingZhan2");
//            JingTing2JiangLuoShiJian = jsonObject.getString("JingTing2JiangLuoShiJian");
//            JingTing2QiFeiShiJian = jsonObject.getString("JingTing2QiFeiShiJian");
//            JingTing2DengJiKou = jsonObject.getString("JingTing2DengJiKou");
//            ShuJuYuanMa = jsonObject.getString("ShuJuYuanMa");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
}
