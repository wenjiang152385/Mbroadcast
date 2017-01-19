//package com.oraro.mbroadcast.logicService;
//
//import android.util.Log;
//
//import com.oraro.mbroadcast.MBroadcastApplication;
//import com.oraro.mbroadcast.dao.DBManager;
//import com.oraro.mbroadcast.dao.FlightInfoDao;
//import com.oraro.mbroadcast.dao.FlightInfoTempDao;
//import com.oraro.mbroadcast.model.FlightInfo;
//import com.oraro.mbroadcast.model.FlightInfoTemp;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by wy on 2016/8/25.
// */
//public class FlightService {
//
//    public void copeFlightToTemp(){
//        DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
//        FlightInfoDao infoDao =  manager.getFlightInfoDao(DBManager.READ_ONLY);
//        List<FlightInfo> list = manager.queryAll(infoDao);
//        if(list == null || list.size() == 0) return;
//        List<FlightInfoTemp> tempList = new ArrayList<FlightInfoTemp>();
//        for(FlightInfo info:list){
//            FlightInfoTemp temp = new FlightInfoTemp();
//            temp.setBoardingGate(info.getBoardingGate());
//            temp.setDate(info.getDate());
//            temp.setDepartureStation(info.getDepartureStation());
//            temp.setDestinationStation(info.getDestinationStation());
//            temp.setFlightInfo(info);
//            temp.setFlightInfoId(info.getId());
//            temp.setImportAndExport(info.getImportAndExport());
//            temp.setInternationalOrDomestic(info.getInternationalOrDomestic());
//            temp.setInternationalThreeYard(info.getInternationalThreeYard());
//            temp.setObjective(info.getObjective());
//            temp.setOriginating(info.getOriginating());
//            temp.setPlanToArrive(info.getPlanToArrive());
//            temp.setPlanToTakeOff(info.getPlanToTakeOff());
//            temp.setProperty(info.getProperty());
//            temp.setStopOne(info.getStopOne());
//            temp.setStopOneBoardingGate(info.getStopOneBoardingGate());
//            temp.setStopOneDepartureTime(info.getStopOneDepartureTime());
//            temp.setStopOneFalTime(info.getStopOneFalTime());
//            temp.setStopStationOne(info.getStopStationOne());
//            temp.setStopTwo(info.getStopTwo());
//            temp.setStopTwoBoardingGate(info.getStopTwoBoardingGate());
//            temp.setStopTwoDepartureTime(info.getStopTwoDepartureTime());
//            temp.setStopTwoFalTime(info.getStopTwoFalTime());
//            temp.setStopStationTwo(info.getStopStationTwo());
//            temp.setFlightInfoPid(info.getId());
//            temp.setFlightNumber(info.getFlightNumber());
//            tempList.add(temp);
//        }
//        FlightInfoTempDao tempDao = manager.getFlightInfoTempDao(DBManager.WRITE_ONLY);
//        manager.insertList(tempList, tempDao);
//        List<FlightInfoTemp> lista = manager.queryAll(tempDao);
//        Log.e("wy","FlightInfoTempList size = "+lista.size());
//    }
//}
