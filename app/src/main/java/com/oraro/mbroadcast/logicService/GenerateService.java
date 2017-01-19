package com.oraro.mbroadcast.logicService;

import android.util.Log;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.dao.FlightInfoDao;
import com.oraro.mbroadcast.dao.FlightInfoTempDao;
import com.oraro.mbroadcast.dao.PlayEntryDao;
import com.oraro.mbroadcast.model.AirlineCompany;
import com.oraro.mbroadcast.model.FlightInfo;
import com.oraro.mbroadcast.model.FlightInfoTemp;
import com.oraro.mbroadcast.model.PlayEntry;
import com.oraro.mbroadcast.utils.BroadcastInformation;
import com.oraro.mbroadcast.utils.LogUtils;
import com.oraro.mbroadcast.utils.SPUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wy on 2016/8/25.
 */
public class GenerateService {
    private static final String TAG = GenerateService.class.getSimpleName();
    private List<PlayEntry> playList = new ArrayList<PlayEntry>();
    private BroadcastInformation broadcastInformation = new BroadcastInformation();
    private List<FlightInfoTemp> errorlist = new ArrayList<FlightInfoTemp>();
    private List<FlightInfoTemp> tempList = new ArrayList<>();
    private DBManager dbManager = DBManager.getInstance(MBroadcastApplication.getMyContext());

    /**
     * 拷贝航班到临时航班
     */
    public void copeFlightToTemp(List<FlightInfo> list) {
        LogUtils.e(TAG, "GenerateService --> copeFlightToTemp()");
        DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
        LogUtils.e(TAG, "GenerateService --> " + list.size());
        if (list == null || list.size() == 0) return;
        tempList.clear();
        for (FlightInfo info : list) {

            /**
             * 只处理起飞的航班，在机场降落的航班不会被加入航班临时表
             */
            Date time = info.getPlanToTakeOffDate();
            if (time == null) {
                continue;
            }
            FlightInfoTemp temp = new FlightInfoTemp();
            temp.setAirCompany(info.getAirCompany());
            temp.setBoardingGate(info.getBoardingGate());
            temp.setDate(info.getDate());
            temp.setDeparture(info.getDeparture());
            temp.setArrivalStation(info.getArrivalStation());
            temp.setPlaneType(info.getPlaneType());
            temp.setPlaneNumber(info.getPlaneNumber());
            temp.setFlightInfo(info);
            temp.setDelayInfo(info.getDelayInfo());
            temp.setPlanToTakeOffDate(info.getPlanToTakeOffDate());
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
            temp.setFlightInfoPid(info.getId());
            temp.setFlightNumber(info.getFlightNumber());
            tempList.add(temp);
        }
        FlightInfoTempDao tempDao = manager.getFlightInfoTempDao(DBManager.WRITE_ONLY);
        manager.insertList(tempList, tempDao);
    }

    /**
     * 生成临时航班对象（仅对单条操作）
     */
    public Long copeFlightToTemp(FlightInfo info) {
        /**
         * 只处理起飞的航班，在机场降落的航班不会被加入航班临时表
         */
        Date time = info.getPlanToTakeOffDate();
        if (time == null) {
            return Long.valueOf(-1);
        }
        LogUtils.e(TAG, info.getPlanToTakeOffDate() + " ");
        LogUtils.e(TAG, "GenerateService --> copeFlightToTemp()");
        DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
        FlightInfoTemp temp = manager.queryByFlightInfoPid(info.getId());
        if (null == temp) {
            temp = new FlightInfoTemp();
        }
        temp.setAirCompany(info.getAirCompany());
        temp.setBoardingGate(info.getBoardingGate());
        temp.setDate(info.getDate());
        temp.setDeparture(info.getDeparture());
        temp.setPlaneType(info.getPlaneType());
        temp.setPlaneNumber(info.getPlaneNumber());
        temp.setArrivalStation(info.getArrivalStation());
        temp.setFlightInfoPid(info.getId());
        temp.setDelayInfo(info.getDelayInfo());
        temp.setInternationalThreeYard(info.getInternationalThreeYard());
        temp.setStopOne(info.getStopOne());
        temp.setStopOneBoardingGate(info.getStopOneBoardingGate());
        temp.setStopOneDepartureTime(info.getStopOneDepartureTime());
        temp.setStopOneFalTime(info.getStopOneFalTime());
        temp.setStopStationOne(info.getStopStationOne());
        temp.setStopTwo(info.getStopTwo());
        temp.setStopTwoBoardingGate(info.getStopTwoBoardingGate());
        temp.setStopTwoDepartureTime(info.getStopTwoDepartureTime());
        temp.setStopTwoFalTime(info.getStopTwoFalTime());
        temp.setStopStationTwo(info.getStopStationTwo());
        temp.setFlightNumber(info.getFlightNumber());
        temp.setPlanToTakeOffDate(info.getPlanToTakeOffDate());

        temp.setRemarks(info.getRemarks());
        temp.setProxy(info.getProxy());
        temp.setPlanePosition(info.getPlanePosition());
        temp.setBuildingNumber(info.getBuildingNumber());
        temp.setDoorNumber(info.getDoorNumber());

        FlightInfoTempDao tempDao = manager.getFlightInfoTempDao(DBManager.WRITE_ONLY);
        long tempId = manager.insertOrUpdate(temp, tempDao);
        return tempId;
    }

    /**
     * 生成临时航班对象（仅对单条操作）
     */
    public FlightInfoTemp copeExcelFlightToTemp(FlightInfo info) {
        /**
         * 只处理起飞的航班，在机场降落的航班不会被加入航班临时表
         */
        Date time = info.getPlanToTakeOffDate();
        if (time == null) {
            return null;
        }
        LogUtils.e(TAG, "GenerateService --> copeFlightToTemp()");
        DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
        FlightInfoTemp temp = new FlightInfoTemp();
        temp.setAirCompany(info.getAirCompany());
        temp.setBoardingGate(info.getBoardingGate());
        temp.setDate(info.getDate());
        temp.setDeparture(info.getDeparture());
        temp.setPlaneType(info.getPlaneType());
        temp.setPlaneNumber(info.getPlaneNumber());
        temp.setArrivalStation(info.getArrivalStation());
        temp.setFlightInfoPid(info.getId());
        temp.setDelayInfo(info.getDelayInfo());
        temp.setRemarks(info.getRemarks());
        temp.setInternationalThreeYard(info.getInternationalThreeYard());
        temp.setStopOne(info.getStopOne());
        temp.setStopOneBoardingGate(info.getStopOneBoardingGate());
        temp.setStopOneDepartureTime(info.getStopOneDepartureTime());
        temp.setStopOneFalTime(info.getStopOneFalTime());
        temp.setStopStationOne(info.getStopStationOne());
        temp.setStopTwo(info.getStopTwo());
        temp.setStopTwoBoardingGate(info.getStopTwoBoardingGate());
        temp.setStopTwoDepartureTime(info.getStopTwoDepartureTime());
        temp.setStopTwoFalTime(info.getStopTwoFalTime());
        temp.setStopStationTwo(info.getStopStationTwo());
        temp.setFlightNumber(info.getFlightNumber());
        FlightInfoTempDao tempDao = manager.getFlightInfoTempDao(DBManager.WRITE_ONLY);
        long tempId = manager.insert(temp, tempDao);
        PlayEntry playEntry = new PlayEntry();
        playEntry.setIsQueue(0);
        Long playEntryTime = temp.getPlanToTakeOffDate().getTime() - Constants.CreatePlayList.URGE_CHECK_IN_BEFORE_TIME;
        Date date = new Date(playEntryTime);
        playEntry.setTime(date);
        playEntry.setTimes(Constants.CreatePlayList.URGE_CHECK_IN_TIMES);
        playEntry.setPlayEntryId(tempId);
        playEntry.setXmlKey(911);
        String msg = broadcastInformation.CUrgeCheckIn(temp.getArrivalStation(), info.getAirCompany(), temp.getFlightNumber());
        playEntry.setTextDesc(msg);
        PlayEntry playEntry2 = new PlayEntry();
        playEntry.setIsQueue(0);
        Long playEntryTime2 = temp.getPlanToTakeOffDate().getTime() - Constants.CreatePlayList.LAST_URGE_CHECK_IN_BEFORE_TIME;
        Date date2 = new Date(playEntryTime2);
        playEntry2.setTime(date2);
        playEntry2.setTimes(Constants.CreatePlayList.LAST_URGE_CHECK_IN_TIMES);
        playEntry2.setPlayEntryId(tempId);
        //保证最后一条催促值机广播时，不被打断。
        playEntry2.setIsEmeng(1);
        playEntry2.setXmlKey(912);
        String msg2 = broadcastInformation.CLastUrgeCheckIn(temp.getArrivalStation(), info.getAirCompany(), temp.getFlightNumber());
        playEntry2.setTextDesc(msg2);
        dbManager.insert(playEntry, dbManager.getPlayEntryDao(DBManager.WRITE_ONLY));
        dbManager.insert(playEntry2, dbManager.getPlayEntryDao(DBManager.WRITE_ONLY));
        return temp;
    }

    /**
     * 生成播放列表
     */
    public void generatePlay(FlightInfoTemp temp) {
        playList.clear();
        DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
        errorlist.clear();
        notifyFly(temp);
        PlayEntryDao playEntryDao = manager.getPlayEntryDao(DBManager.WRITE_ONLY);
        manager.insertOrUpdateList(playList, playEntryDao);

    }

    /**
     * 生成播放列表
     */
    public void generatePlay() {
        DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
        errorlist.clear();
        for (FlightInfoTemp fip : tempList) {
            notifyFly(fip);
        }

        PlayEntryDao playEntryDao = manager.getPlayEntryDao(DBManager.WRITE_ONLY);
        manager.insertList(playList, playEntryDao);

    }

    /**
     * 生成播放列表
     */
    public void generatePlayUpdate(FlightInfoTemp flightInfoTemp) {
        notifyFlyUpdate(flightInfoTemp);
    }

    /**
     * 生成播放列表
     */
    public List<FlightInfoTemp> generatePlayRerror() {
        DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
        errorlist.clear();
        playList.clear();
        for (FlightInfoTemp fip : tempList) {
            notifyFly(fip);
        }

        PlayEntryDao playEntryDao = manager.getPlayEntryDao(DBManager.WRITE_ONLY);
        manager.insertList(playList, playEntryDao);
        return errorlist;
    }


    /**
     * 插入单个航班，生成对应播放列表
     *
     * @param info
     */
    public void insertAndGeneratePlay(FlightInfo info) {

        List<AirlineCompany> list = DBManager.getInstance(MBroadcastApplication.getMyContext()).queryAll(DBManager.getInstance(MBroadcastApplication.getMyContext()).getAirlineCompanyDao(DBManager.READ_ONLY));
        AirlineCompany airlineCompany;
        if (list.isEmpty()) {
            airlineCompany = new AirlineCompany();
            airlineCompany.setAirlineCompanyName("东方航空公司");
            DBManager.getInstance(MBroadcastApplication.getMyContext()).insert(airlineCompany, DBManager.getInstance(MBroadcastApplication.getMyContext()).getAirlineCompanyDao(DBManager.WRITE_ONLY));
        } else {
            airlineCompany = list.get(0);
        }
        info.setAirlineCompany(airlineCompany);


        /**
         * 将航班插入数据库，并获得插入后的对象
         */
        DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
        FlightInfoDao infoDao = manager.getFlightInfoDao(DBManager.WRITE_ONLY);
        Long infoId = manager.insert(info, infoDao);
        FlightInfo insertInfo = (FlightInfo) manager.queryById(infoId, infoDao);
        //生成临时航班模板
        Long tempId = copeFlightToTemp(insertInfo);
        Constants.FLIGHTTEMP_ID = tempId;
        if (tempId == -1) return;
        FlightInfoTempDao tempDao = manager.getFlightInfoTempDao(DBManager.READ_ONLY);
        FlightInfoTemp temp = (FlightInfoTemp) manager.queryById(tempId, tempDao);
        //生成播放列表
        generatePlay(temp);
    }

    /**
     * 插入单个航班，生成对应播放列表
     *
     * @param info
     */
    public Long insertOrUpdateGeneratePlay(FlightInfo info) {

        List<AirlineCompany> list = DBManager.getInstance(MBroadcastApplication.getMyContext()).queryAll(DBManager.getInstance(MBroadcastApplication.getMyContext()).getAirlineCompanyDao(DBManager.READ_ONLY));
        AirlineCompany airlineCompany;
        if (list.isEmpty()) {
            airlineCompany = new AirlineCompany();
            airlineCompany.setAirlineCompanyName("东方航空公司");
            DBManager.getInstance(MBroadcastApplication.getMyContext()).insert(airlineCompany, DBManager.getInstance(MBroadcastApplication.getMyContext()).getAirlineCompanyDao(DBManager.WRITE_ONLY));
        } else {
            airlineCompany = list.get(0);
        }
        info.setAirlineCompany(airlineCompany);
        /**
         * 将航班插入数据库，并获得插入后的对象
         */
        DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
        FlightInfoDao infoDao = manager.getFlightInfoDao(DBManager.WRITE_ONLY);
        Long infoId = manager.insertOrUpdate(info, infoDao);
        //生成临时航班模板
        Long tempId = copeFlightToTemp(info);
        Constants.FLIGHTTEMP_ID = tempId;
        if (tempId == -1)
            return (long) -1;
        FlightInfoTempDao tempDao = manager.getFlightInfoTempDao(DBManager.READ_ONLY);
        FlightInfoTemp temp = (FlightInfoTemp) manager.queryById(tempId, tempDao);
        List playEntrys = manager.queryPlayEntryByFlightInfoTempId(tempId);
        manager.deleteList(playEntrys, manager.getPlayEntryDao(DBManager.WRITE_ONLY));
        //生成播放列表
        urgeCheckInertOrUpdateByFlightInfoTemp(temp);
        lastUrgeCheckInertOrUpdateByFlightInfoTemp(temp);
        return tempId;
    }

    private boolean checkErrorList(FlightInfoTemp fip) {
        if (fip == null) {
            return false;
        }
        for (int i = 0; i < errorlist.size(); i++) {
            FlightInfoTemp error = errorlist.get(i);
            if (error.getFlightNumber().equals(fip.getFlightNumber()) && error.getPlanToTakeOffDate().equals(fip.getPlanToTakeOffDate())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 通知广播
     *
     * @param fip 航班对象
     */
    private void notifyFly(FlightInfoTemp fip) {
        try {
//            flightCheckIn(fip);
            urgeCheckIn(fip);
            lastUrgeCheckIn(fip);
//            urgeSecurityCheck(fip);
        } catch (Exception e) {
            LogUtils.e("dy", e + " ");
            e.printStackTrace();
            if (errorlist.size() == 0) {
                errorlist.add(fip);
            } else {
                if (checkErrorList(fip)) {
                    errorlist.add(fip);
                }
            }
            DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
            FlightInfoDao infoDao = manager.getFlightInfoDao(DBManager.WRITE_ONLY);
            manager.deleteById(fip.getFlightInfoPid(), infoDao);

            FlightInfoTempDao tempDao = manager.getFlightInfoTempDao(DBManager.WRITE_ONLY);
            tempDao.delete(fip);

        }

        // delayCheckIn(fip);//航班延误，默认不生成
    }

    /**
     * 通知广播
     *
     * @param fip 航班对象
     */
    private void notifyFlyUpdate(FlightInfoTemp fip) {
        try {
            urgeCheckUpdate(fip);
            lastUrgeCheckUpdate(fip);
        } catch (Exception e) {
            if (errorlist.size() == 0) {
                errorlist.add(fip);
            } else {
                if (checkErrorList(fip)) {
                    errorlist.add(fip);
                }
            }
            DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
            FlightInfoDao infoDao = manager.getFlightInfoDao(DBManager.WRITE_ONLY);
            manager.deleteById(fip.getFlightInfoPid(), infoDao);

            FlightInfoTempDao tempDao = manager.getFlightInfoTempDao(DBManager.WRITE_ONLY);
            tempDao.delete(fip);

        }
    }

    /**
     * 开始值机
     *
     * @param fip
     */
    private void flightCheckIn(FlightInfoTemp fip) {
        PlayEntry playEntry = new PlayEntry();
        playEntry.setIsQueue(0);
        Date time = fip.getPlanToTakeOffDate();
        if (time != null) {
            playEntry.setTime(time);
            playEntry.setTimes(Constants.CreatePlayList.CHECK_IN_TIMES);
            playEntry.setPlayEntryId(fip.getId());
            playEntry.setFlightInfoTemp(fip);
            playEntry.setXmlKey(910);
            String msg = broadcastInformation.CCheckIn(fip.getArrivalStation(), fip.getFlightInfo().getAirCompany(), fip.getFlightNumber());
            playEntry.setTextDesc(msg);
            playList.add(playEntry);
        }


    }

    /**
     * 催促值机
     *
     * @param fip
     */
    private void urgeCheckIn1(FlightInfoTemp fip) {
        PlayEntry playEntry = new PlayEntry();
        playEntry.setIsQueue(0);
        Long time = fip.getPlanToTakeOffDate().getTime() - Constants.CreatePlayList.URGE_CHECK_IN_BEFORE_TIME;
        Date date = new Date(time);
        playEntry.setTime(date);
        playEntry.setTimes(SPUtils.getBroadcastNumber(MBroadcastApplication.getMyContext()));
        playEntry.setPlayEntryId(fip.getId());
        playEntry.setXmlKey(911);
        String msg = broadcastInformation.CUrgeCheckIn(fip.getArrivalStation(), fip.getFlightInfo().getAirCompany(), fip.getFlightNumber());
        playEntry.setTextDesc(msg);
        dbManager.insert(playEntry, dbManager.getPlayEntryDao(DBManager.WRITE_ONLY));
    }

    /**
     * 催促值机
     *
     * @param fip
     */
    private void urgeCheckIn(FlightInfoTemp fip) {
        PlayEntry playEntry = new PlayEntry();
        playEntry.setIsQueue(0);
        Long time = fip.getPlanToTakeOffDate().getTime() - Constants.CreatePlayList.URGE_CHECK_IN_BEFORE_TIME;
        Date date = new Date(time);
        playEntry.setTime(date);
        playEntry.setTimes(SPUtils.getBroadcastNumber(MBroadcastApplication.getMyContext()));
        playEntry.setPlayEntryId(fip.getId());
        playEntry.setXmlKey(911);
        String msg = broadcastInformation.CUrgeCheckIn(fip.getArrivalStation(), fip.getFlightInfo().getAirCompany(), fip.getFlightNumber());
        playEntry.setTextDesc(msg);
        playList.add(playEntry);
    }

    /**
     * 催促值机
     *
     * @param fip
     */
    private void urgeCheckUpdate(FlightInfoTemp fip) {
        PlayEntry playEntry = new PlayEntry();
        playEntry.setPlayEntryId(fip.getId());
        playEntry.setIsQueue(0);
        Long time = fip.getPlanToTakeOffDate().getTime() - Constants.CreatePlayList.URGE_CHECK_IN_BEFORE_TIME;
        Date date = new Date(time);
        playEntry.setTime(date);
        playEntry.setTimes(Constants.CreatePlayList.URGE_CHECK_IN_TIMES);
        playEntry.setXmlKey(911);
        String msg = broadcastInformation.CUrgeCheckIn(fip.getArrivalStation(), fip.getFlightInfo().getAirCompany(), fip.getFlightNumber());
        playEntry.setTextDesc(msg);
        dbManager.insert(playEntry, dbManager.getPlayEntryDao(DBManager.WRITE_ONLY));
    }

    /**
     * 最后催促值机
     *
     * @param fip
     */
    private void lastUrgeCheckUpdate(FlightInfoTemp fip) {
        PlayEntry playEntry = new PlayEntry();
        playEntry.setPlayEntryId(fip.getId());
        playEntry.setIsQueue(0);
        Long time = fip.getPlanToTakeOffDate().getTime() - Constants.CreatePlayList.LAST_URGE_CHECK_IN_BEFORE_TIME;
        Date date = new Date(time);
        playEntry.setTime(date);
        playEntry.setTimes(Constants.CreatePlayList.LAST_URGE_CHECK_IN_TIMES);
        //保证最后一条催促值机广播时，不被打断。
        playEntry.setIsEmeng(1);
        playEntry.setXmlKey(912);
        String msg = broadcastInformation.CLastUrgeCheckIn(fip.getArrivalStation(), fip.getFlightInfo().getAirCompany(), fip.getFlightNumber());
        playEntry.setTextDesc(msg);
        dbManager.insert(playEntry, dbManager.getPlayEntryDao(DBManager.WRITE_ONLY));
    }

    /**
     * 最后催促值机
     *
     * @param fip
     */
    private void lastUrgeCheckIn(FlightInfoTemp fip) {
        PlayEntry playEntry = new PlayEntry();
        playEntry.setIsQueue(0);
        Long time = fip.getPlanToTakeOffDate().getTime() - Constants.CreatePlayList.LAST_URGE_CHECK_IN_BEFORE_TIME;
        Date date = new Date(time);
        playEntry.setTime(date);
        playEntry.setTimes(SPUtils.getBroadcastNumber(MBroadcastApplication.getMyContext()));
        playEntry.setPlayEntryId(fip.getId());
        //保证最后一条催促值机广播时，不被打断。
        playEntry.setIsEmeng(1);
        playEntry.setXmlKey(912);
        String msg = broadcastInformation.CLastUrgeCheckIn(fip.getArrivalStation(), fip.getFlightInfo().getAirCompany(), fip.getFlightNumber());
        playEntry.setTextDesc(msg);
        playList.add(playEntry);
    }


    /**
     * 最后催促值机新增航班或者编辑航班
     *
     * @param fip
     */
    private void urgeCheckInertOrUpdateByFlightInfoTemp(FlightInfoTemp fip) {
        PlayEntry playEntry = new PlayEntry();
        playEntry.setIsQueue(0);
        Long time = fip.getPlanToTakeOffDate().getTime() - Constants.CreatePlayList.URGE_CHECK_IN_BEFORE_TIME;
        Date date = new Date(time);
        playEntry.setTime(date);
        playEntry.setTimes(SPUtils.getBroadcastNumber(MBroadcastApplication.getMyContext()));
        playEntry.setPlayEntryId(fip.getId());
        playEntry.setXmlKey(911);
        String msg = broadcastInformation.CUrgeCheckIn(fip.getArrivalStation(), fip.getFlightInfo().getAirCompany(), fip.getFlightNumber());
        playEntry.setTextDesc(msg);
        playEntry.setFlightInfoTemp(fip);
        dbManager.insertOrUpdate(playEntry, dbManager.getPlayEntryDao(DBManager.WRITE_ONLY));

    }

    /**
     * 最后催促值机新增航班或者编辑航班
     *
     * @param fip
     */
    private void lastUrgeCheckInertOrUpdateByFlightInfoTemp(FlightInfoTemp fip) {
        PlayEntry playEntry = dbManager.queryPlayEntryByFlightInfoTempId2(fip.getId());
        if (null == playEntry) {
            playEntry = new PlayEntry();
        }
        playEntry.setIsQueue(0);
        LogUtils.e(TAG, fip.getPlanToTakeOffDate() + " ");
        Long time = fip.getPlanToTakeOffDate().getTime() - Constants.CreatePlayList.LAST_URGE_CHECK_IN_BEFORE_TIME;
        Date date = new Date(time);
        playEntry.setTime(date);
        playEntry.setTimes(SPUtils.getBroadcastNumber(MBroadcastApplication.getMyContext()));
        playEntry.setPlayEntryId(fip.getId());
        //保证最后一条催促值机广播时，不被打断。
        playEntry.setIsEmeng(1);
        playEntry.setXmlKey(912);
        String msg = broadcastInformation.CLastUrgeCheckIn(fip.getArrivalStation(), fip.getFlightInfo().getAirCompany(), fip.getFlightNumber());
        playEntry.setTextDesc(msg);
        dbManager.insertOrUpdate(playEntry, dbManager.getPlayEntryDao(DBManager.WRITE_ONLY));
    }


    /**
     * 催促安检
     *
     * @param fip
     */
    private void urgeSecurityCheck(FlightInfoTemp fip) {
        PlayEntry playEntry = new PlayEntry();
        playEntry.setIsQueue(0);
        Date time = fip.getPlanToTakeOffDate();
        if (time != null) {
            playEntry.setTime(time);
            playEntry.setTimes(Constants.CreatePlayList.URGE_SECURITY_CHECK_TIMES);
            playEntry.setPlayEntryId(fip.getId());
            playEntry.setFlightInfoTemp(fip);
            playEntry.setXmlKey(913);
            String msg = broadcastInformation.CUrgeSecurityCheck(fip.getArrivalStation(), fip.getFlightInfo().getAirCompany(), fip.getFlightNumber());

            playEntry.setTextDesc(msg);
            playList.add(playEntry);
        }
    }


    /**
     * 值机延误
     *
     * @param fip
     */
    private void delayCheckIn(FlightInfoTemp fip) {
        PlayEntry playEntry = new PlayEntry();
        playEntry.setIsQueue(0);
        Date time = fip.getPlanToTakeOffDate();
        if (time != null) {
            playEntry.setTime(time);
            playEntry.setTimes(Constants.CreatePlayList.DELAY_CHECK_IN_BEFORE_TIMES);
            playEntry.setPlayEntryId(fip.getId());
            playEntry.setFlightInfoTemp(fip);
            playEntry.setXmlKey(914);
            String msg = broadcastInformation.CDelayCheckIn(fip.getArrivalStation(), fip.getFlightInfo().getAirCompany(), fip.getFlightNumber());

            playEntry.setTextDesc(msg);
            playList.add(playEntry);
        }
    }

}
