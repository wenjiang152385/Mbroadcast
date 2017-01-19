package com.oraro.mbroadcast.model;


import android.util.Log;
import android.widget.FrameLayout;

import com.oraro.mbroadcast.ui.widget.LineView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/9/12 0012.
 */
public class AddFliDataControl {

    private static final int TRANSFERONE = 0;
    private static final int TRANSFERTWO = 1;
    private static final int TRANSFERTHREE = 2;
    private static final int TRANSFERFOUR = 3;


    public void getHeadData(FlightInfo flightInfo, List<LineView> headViewList) {
        for (int i = 0; i < headViewList.size(); i++) {
            LineView lineView = headViewList.get(i);
            String tag = (String) lineView.getTag();
            String params = lineView.getEditText().getText() + "";
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            switch (tag) {
                case "originating":
                    flightInfo.setDeparture(params);
                    break;
                case "planToTakeOff":
                    try {
                        Date date=sdf.parse(params);
                        flightInfo.setPlanToTakeOffDate(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case "objective":
//                    flightInfo.setDestinationStation(params);
                    break;
                case "planToArrive":
                    flightInfo.setArrivalStation(params);
                    break;
            }
        }
    }


    public void getBodyData(FlightInfo flightInfo, List<LineView> bodyViewList) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < bodyViewList.size(); i++) {
            LineView lineView = bodyViewList.get(i);
            String tag = (String) lineView.getTag();
            String params = lineView.getEditText().getText() + "";
            switch (tag) {
                case "航班号":
                    flightInfo.setFlightNumber(params);
                    break;
                case "日期":
                    Date date = null;
                    try {
                        date = sdf.parse(params);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    flightInfo.setDate(date);
                    break;
                case "国际/国内":
//                    flightInfo.setInternationalOrDomestic(params);
                    break;
                case "进出港":
//                    flightInfo.setImportAndExport(params);
                    break;
                case "性质":
//                    flightInfo.setProperty(params);
                    break;
                case "始发(3码)":
//                    flightInfo.setOriginating(params);
                    break;
                case "始发登机口":
                    flightInfo.setBoardingGate(params);
                    break;
                case "目的(3码)":
//                    flightInfo.setObjective(params);
                    break;
                case "数据来自(国际3码)":
                    flightInfo.setInternationalThreeYard(params);
                    break;
            }
        }
    }

    public void getFootData(FlightInfo flightInfo, List<LineView> footViewList, List<FrameLayout> transfersList) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        for (int i = 0; i < transfersList.size(); i++) {
            FrameLayout transLayout = transfersList.get(i);
            LineView lineView0 = (LineView) transLayout.findViewWithTag("经停(3码)");
            LineView lineView1 = (LineView) transLayout.findViewWithTag("经停降落时间");
            LineView lineView2 = (LineView) transLayout.findViewWithTag("经停登机口");
            LineView lineView3 = (LineView) transLayout.findViewWithTag("经停站");
            LineView lineView4 = (LineView) transLayout.findViewWithTag("经停起飞时间");
            String text0 = lineView0.getEditText().getText() + "";
            String text1 = lineView1.getEditText().getText() + "";
            String text2 = lineView2.getEditText().getText() + "";
            String text3 = lineView3.getEditText().getText() + "";
            String text4 = lineView4.getEditText().getText() + "";

            if (TRANSFERONE == i) {
                try {
                    flightInfo.setStopOne(text0);
                    flightInfo.setStopOneFalTime(sdf.parse(text1));
                    flightInfo.setStopOneBoardingGate(text2);
                    flightInfo.setStopStationOne(text3);
                    flightInfo.setStopOneDepartureTime(sdf.parse(text4));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (TRANSFERTWO == i) {
                try {
                    flightInfo.setStopTwo(text0);
                    flightInfo.setStopTwoFalTime(sdf.parse(text1));
                    flightInfo.setStopTwoBoardingGate(text2);
                    flightInfo.setStopStationTwo(text3);
                    flightInfo.setStopTwoDepartureTime(sdf.parse(text4));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    public boolean checkText(List<LineView> list) {
        for (int i = 0; i < list.size(); i++) {
            LineView lineView = list.get(i);
            String text = lineView.getEditText().getText().toString();
            if (text.equals(null) || text.equals("")) {
                return false;
            }
        }
        return true;
    }

}


//    LineView lineView = footViewList.get(i);
//    String tag = (String) lineView.getTag();
//    int id = lineView.getId();
//    String params = lineView.getEditText().getText() + "";
//if ("经停(3码)".equals(tag)) {
//        if (TRANSFERONE == i) {
//        flightInfo.setStopOne(params);
//        } else if (TRANSFERTWO == i) {
//        flightInfo.setStopTwo(params);
//        }
//        } else if ("经停降落时间".equals(tag)) {
//        try {
//        if (TRANSFERONE == i) {
//        flightInfo.setStopOneFalTime(sdf.parse(params));
//        } else if (TRANSFERTWO == i) {
//        flightInfo.setStopTwoFalTime(sdf.parse(params));
//        }
//        } catch (ParseException e) {
//        e.printStackTrace();
//        }
//        } else if ("经停登机口".equals(tag)) {
//        if (TRANSFERONE == i) {
//        flightInfo.setStopOneBoardingGate(params);
//        } else if (TRANSFERTWO == i) {
//        flightInfo.setStopTwoBoardingGate(params);
//        }
//        } else if ("经停站".equals(tag)) {
//        if (TRANSFERONE == i) {
//        flightInfo.setStopStationOne(params);
//        } else if (TRANSFERTWO == i) {
//        flightInfo.setStopStationTwo(params);
//        }
//        } else if ("经停起飞时间".equals(tag)) {
//        try {
//        if (TRANSFERONE == i) {
//        flightInfo.setStopOneDepartureTime(sdf.parse(params));
//        } else if (TRANSFERTWO == i) {
//        flightInfo.setStopTwoDepartureTime(sdf.parse(params));
//        }
//        } catch (ParseException e) {
//        e.printStackTrace();
//        }
//        }