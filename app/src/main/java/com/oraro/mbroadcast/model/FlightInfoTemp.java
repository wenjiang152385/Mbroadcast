package com.oraro.mbroadcast.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.oraro.mbroadcast.dao.DaoSession;
import com.oraro.mbroadcast.dao.FlightInfoTempDao;
import com.oraro.mbroadcast.dao.FlightInfoDao;


/**
 * Created by dongyu on 2016/8/16 0016.
 */

@Entity
public class FlightInfoTemp {
    @Id(autoincrement = true)
    private  Long id;
    private Long flightInfoPid;
    //起飞日期
    private Date date;
    //航班号
    private String flightNumber;
    //飞机号
    private String planeNumber;
    //飞机型号
    private String planeType;
    //到站
    private String arrivalStation;
    //计划起飞时间
    private Date planToTakeOffDate;
    //飞机位置
    private String planePosition;
    //楼号
    private String buildingNumber;
    //门号
    private String doorNumber;
    //起飞地
    private String departure;
    //备注
    private String remarks;
    //代理
    private String proxy;
    //延误信息
    private String delayInfo;
    //经停1
    private String stopOne;
    //经停1降落时间
    private Date stopOneFalTime;
    //经停1起飞时间

    private Date stopOneDepartureTime;
    //经停1登机口
    private String StopOneBoardingGate;
    //经停站1
    private String stopStationOne;
    //经停2
    private String stopTwo;
    //经停2降落时间
    private Date stopTwoFalTime;
    //经停2起飞时间
    private Date stopTwoDepartureTime;
    //经停2登机口
    private String StopTwoBoardingGate;
    //经停站2
    private String stopStationTwo;
    //登机口
    private String boardingGate;
    //国际三码
    private String internationalThreeYard;
    //航班延误
    private Boolean isDelay = false;
    @ToOne(joinProperty = "flightInfoPid")
    private FlightInfo flightInfo;
    //0代表未编辑，1代表编辑过了
    private  int edit;
    //标记编辑的时间
    private  Date editDate;
    //所属航空公司
    private String airCompany;
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1200860586)
    public void setFlightInfo(FlightInfo flightInfo) {
        synchronized (this) {
            this.flightInfo = flightInfo;
            flightInfoPid = flightInfo == null ? null : flightInfo.getId();
            flightInfo__resolvedKey = flightInfoPid;
        }
    }
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1497104221)
    public FlightInfo getFlightInfo() {
        Long __key = this.flightInfoPid;
        if (flightInfo__resolvedKey == null
                || !flightInfo__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            FlightInfoDao targetDao = daoSession.getFlightInfoDao();
            FlightInfo flightInfoNew = targetDao.load(__key);
            synchronized (this) {
                flightInfo = flightInfoNew;
                flightInfo__resolvedKey = __key;
            }
        }
        return flightInfo;
    }
    @Generated(hash = 1390429143)
    private transient Long flightInfo__resolvedKey;
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1436465668)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getFlightInfoTempDao() : null;
    }
    /** Used for active entity operations. */
    @Generated(hash = 1926814241)
    private transient FlightInfoTempDao myDao;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    public String getAirCompany() {
        return this.airCompany;
    }
    public void setAirCompany(String airCompany) {
        this.airCompany = airCompany;
    }
    public Date getEditDate() {
        return this.editDate;
    }
    public void setEditDate(Date editDate) {
        this.editDate = editDate;
    }
    public int getEdit() {
        return this.edit;
    }
    public void setEdit(int edit) {
        this.edit = edit;
    }
    public Boolean getIsDelay() {
        return this.isDelay;
    }
    public void setIsDelay(Boolean isDelay) {
        this.isDelay = isDelay;
    }
    public String getInternationalThreeYard() {
        return this.internationalThreeYard;
    }
    public void setInternationalThreeYard(String internationalThreeYard) {
        this.internationalThreeYard = internationalThreeYard;
    }
    public String getBoardingGate() {
        return this.boardingGate;
    }
    public void setBoardingGate(String boardingGate) {
        this.boardingGate = boardingGate;
    }
    public String getStopStationTwo() {
        return this.stopStationTwo;
    }
    public void setStopStationTwo(String stopStationTwo) {
        this.stopStationTwo = stopStationTwo;
    }
    public String getStopTwoBoardingGate() {
        return this.StopTwoBoardingGate;
    }
    public void setStopTwoBoardingGate(String StopTwoBoardingGate) {
        this.StopTwoBoardingGate = StopTwoBoardingGate;
    }
    public Date getStopTwoDepartureTime() {
        return this.stopTwoDepartureTime;
    }
    public void setStopTwoDepartureTime(Date stopTwoDepartureTime) {
        this.stopTwoDepartureTime = stopTwoDepartureTime;
    }
    public Date getStopTwoFalTime() {
        return this.stopTwoFalTime;
    }
    public void setStopTwoFalTime(Date stopTwoFalTime) {
        this.stopTwoFalTime = stopTwoFalTime;
    }
    public String getStopTwo() {
        return this.stopTwo;
    }
    public void setStopTwo(String stopTwo) {
        this.stopTwo = stopTwo;
    }
    public String getStopStationOne() {
        return this.stopStationOne;
    }
    public void setStopStationOne(String stopStationOne) {
        this.stopStationOne = stopStationOne;
    }
    public String getStopOneBoardingGate() {
        return this.StopOneBoardingGate;
    }
    public void setStopOneBoardingGate(String StopOneBoardingGate) {
        this.StopOneBoardingGate = StopOneBoardingGate;
    }
    public Date getStopOneDepartureTime() {
        return this.stopOneDepartureTime;
    }
    public void setStopOneDepartureTime(Date stopOneDepartureTime) {
        this.stopOneDepartureTime = stopOneDepartureTime;
    }
    public Date getStopOneFalTime() {
        return this.stopOneFalTime;
    }
    public void setStopOneFalTime(Date stopOneFalTime) {
        this.stopOneFalTime = stopOneFalTime;
    }
    public String getStopOne() {
        return this.stopOne;
    }
    public void setStopOne(String stopOne) {
        this.stopOne = stopOne;
    }
    public String getDelayInfo() {
        return this.delayInfo;
    }
    public void setDelayInfo(String delayInfo) {
        this.delayInfo = delayInfo;
    }
    public String getProxy() {
        return this.proxy;
    }
    public void setProxy(String proxy) {
        this.proxy = proxy;
    }
    public String getRemarks() {
        return this.remarks;
    }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    public String getDeparture() {
        return this.departure;
    }
    public void setDeparture(String departure) {
        this.departure = departure;
    }
    public String getDoorNumber() {
        return this.doorNumber;
    }
    public void setDoorNumber(String doorNumber) {
        this.doorNumber = doorNumber;
    }
    public String getBuildingNumber() {
        return this.buildingNumber;
    }
    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }
    public String getPlanePosition() {
        return this.planePosition;
    }
    public void setPlanePosition(String planePosition) {
        this.planePosition = planePosition;
    }
    public Date getPlanToTakeOffDate() {
        return this.planToTakeOffDate;
    }
    public void setPlanToTakeOffDate(Date planToTakeOffDate) {
        this.planToTakeOffDate = planToTakeOffDate;
    }
    public String getArrivalStation() {
        return this.arrivalStation;
    }
    public void setArrivalStation(String arrivalStation) {
        this.arrivalStation = arrivalStation;
    }
    public String getPlaneType() {
        return this.planeType;
    }
    public void setPlaneType(String planeType) {
        this.planeType = planeType;
    }
    public String getPlaneNumber() {
        return this.planeNumber;
    }
    public void setPlaneNumber(String planeNumber) {
        this.planeNumber = planeNumber;
    }
    public String getFlightNumber() {
        return this.flightNumber;
    }
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
    public Date getDate() {
        return this.date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public Long getFlightInfoPid() {
        return this.flightInfoPid;
    }
    public void setFlightInfoPid(Long flightInfoPid) {
        this.flightInfoPid = flightInfoPid;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 360359866)
    public FlightInfoTemp(Long id, Long flightInfoPid, Date date,
            String flightNumber, String planeNumber, String planeType,
            String arrivalStation, Date planToTakeOffDate, String planePosition,
            String buildingNumber, String doorNumber, String departure,
            String remarks, String proxy, String delayInfo, String stopOne,
            Date stopOneFalTime, Date stopOneDepartureTime,
            String StopOneBoardingGate, String stopStationOne, String stopTwo,
            Date stopTwoFalTime, Date stopTwoDepartureTime,
            String StopTwoBoardingGate, String stopStationTwo, String boardingGate,
            String internationalThreeYard, Boolean isDelay, int edit,
            Date editDate, String airCompany) {
        this.id = id;
        this.flightInfoPid = flightInfoPid;
        this.date = date;
        this.flightNumber = flightNumber;
        this.planeNumber = planeNumber;
        this.planeType = planeType;
        this.arrivalStation = arrivalStation;
        this.planToTakeOffDate = planToTakeOffDate;
        this.planePosition = planePosition;
        this.buildingNumber = buildingNumber;
        this.doorNumber = doorNumber;
        this.departure = departure;
        this.remarks = remarks;
        this.proxy = proxy;
        this.delayInfo = delayInfo;
        this.stopOne = stopOne;
        this.stopOneFalTime = stopOneFalTime;
        this.stopOneDepartureTime = stopOneDepartureTime;
        this.StopOneBoardingGate = StopOneBoardingGate;
        this.stopStationOne = stopStationOne;
        this.stopTwo = stopTwo;
        this.stopTwoFalTime = stopTwoFalTime;
        this.stopTwoDepartureTime = stopTwoDepartureTime;
        this.StopTwoBoardingGate = StopTwoBoardingGate;
        this.stopStationTwo = stopStationTwo;
        this.boardingGate = boardingGate;
        this.internationalThreeYard = internationalThreeYard;
        this.isDelay = isDelay;
        this.edit = edit;
        this.editDate = editDate;
        this.airCompany = airCompany;
    }
    @Generated(hash = 878663971)
    public FlightInfoTemp() {
    }


    @Override
    public String toString() {
        return "FlightInfoTemp{" +
                "id=" + id +
                ", flightInfoPid=" + flightInfoPid +
                ", date=" + date +
                ", flightNumber='" + flightNumber + '\'' +
                ", planeNumber='" + planeNumber + '\'' +
                ", planeType='" + planeType + '\'' +
                ", arrivalStation='" + arrivalStation + '\'' +
                ", planToTakeOffDate=" + planToTakeOffDate +
                ", planePosition='" + planePosition + '\'' +
                ", buildingNumber='" + buildingNumber + '\'' +
                ", doorNumber='" + doorNumber + '\'' +
                ", departure='" + departure + '\'' +
                ", remarks='" + remarks + '\'' +
                ", proxy='" + proxy + '\'' +
                ", delayInfo='" + delayInfo + '\'' +
                ", stopOne='" + stopOne + '\'' +
                ", stopOneFalTime=" + stopOneFalTime +
                ", stopOneDepartureTime=" + stopOneDepartureTime +
                ", StopOneBoardingGate='" + StopOneBoardingGate + '\'' +
                ", stopStationOne='" + stopStationOne + '\'' +
                ", stopTwo='" + stopTwo + '\'' +
                ", stopTwoFalTime=" + stopTwoFalTime +
                ", stopTwoDepartureTime=" + stopTwoDepartureTime +
                ", StopTwoBoardingGate='" + StopTwoBoardingGate + '\'' +
                ", stopStationTwo='" + stopStationTwo + '\'' +
                ", boardingGate='" + boardingGate + '\'' +
                ", internationalThreeYard='" + internationalThreeYard + '\'' +
                ", isDelay=" + isDelay +
                ", flightInfo=" + flightInfo +
                ", edit=" + edit +
                ", editDate=" + editDate +
                ", airCompany='" + airCompany + '\'' +
                ", flightInfo__resolvedKey=" + flightInfo__resolvedKey +
                ", myDao=" + myDao +
                ", daoSession=" + daoSession +
                '}';
    }
}
