package com.oraro.mbroadcast.model;

import java.util.Date;
import java.util.List;

/**
 * Created by admin on 2016/8/8
 *
 * @author zmy
 */
public class JsonBean {
    /**
     * 日期 : 2003-12-01 00:00:00
     * 航班号 : MU5140
     * 计划到达 : 1899-12-31 12:05:00
     * 计划起飞 : 无
     * 进出港 : A
     * 国际/国内 : D
     * 性质 : W/Z
     * 始发(3码) : PEK（北京）
     * 始发站 : 无
     * 目的(3码) : HGH（杭州）
     * 目的站 : 无
     * 经停1(3码） : 无
     * 经停站1 : 无
     * 经停2（3码） : 无
     * 经停站2 : 无
     */
    public List<Bean> sheet;

    public List<Bean> getmFltSchedule1() {
        return sheet;
    }

    public void setmFltSchedule1(List<Bean> mFltSchedule1) {
        this.sheet = mFltSchedule1;
    }

    public class Bean {

        public String date;
        public String flightNumber;
        public String arrive;
        public Date launch;
        public String inOut;
        public String area;
        public String property;
        public String start3;
        public String startStation;
        public String aim3;
        public String aimStation;

        public String DengJiKou;
        public String JingTing1;
        public String JingTingZhan1;
        public String JingTing1JiangLuoShiJian;

        public String JingTing1QiFeiShiJian;
        public String JingTing1DengJiKou;
        public String JingTing2;
        public String JingTingZhan2;
        public String JingTing2JiangLuoShiJian;
        public String JingTing2QiFeiShiJian;
        public String JingTing2DengJiKou;
        public String ShuJuYuanMa;

        public String getStartStation() {
            return startStation;
        }

        public void setStartStation(String startStation) {
            this.startStation = startStation;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getFlightNumber() {
            return flightNumber;
        }

        public void setFlightNumber(String flightNumber) {
            this.flightNumber = flightNumber;
        }

        public String getArrive() {
            return arrive;
        }

        public void setArrive(String arrive) {
            this.arrive = arrive;
        }

        public Date getLaunch() {
            return launch;
        }

        public void setLaunch(Date launch) {
            this.launch = launch;
        }

        public String getInOut() {
            return inOut;
        }

        public void setInOut(String inOut) {
            this.inOut = inOut;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public String getStart3() {
            return start3;
        }

        public void setStart3(String start3) {
            this.start3 = start3;
        }

        public String getAim3() {
            return aim3;
        }

        public void setAim3(String aim3) {
            this.aim3 = aim3;
        }

        public String getAimStation() {
            return aimStation;
        }

        public void setAimStation(String aimStation) {
            this.aimStation = aimStation;
        }

        public String getDengJiKou() {
            return DengJiKou;
        }

        public void setDengJiKou(String dengJiKou) {
            DengJiKou = dengJiKou;
        }

        public String getJingTing1() {
            return JingTing1;
        }

        public void setJingTing1(String jingTing1) {
            JingTing1 = jingTing1;
        }

        public String getJingTingZhan1() {
            return JingTingZhan1;
        }

        public void setJingTingZhan1(String jingTingZhan1) {
            JingTingZhan1 = jingTingZhan1;
        }

        public String getJingTing1JiangLuoShiJian() {
            return JingTing1JiangLuoShiJian;
        }

        public void setJingTing1JiangLuoShiJian(String jingTing1JiangLuoShiJian) {
            JingTing1JiangLuoShiJian = jingTing1JiangLuoShiJian;
        }

        public String getJingTing1QiFeiShiJian() {
            return JingTing1QiFeiShiJian;
        }

        public void setJingTing1QiFeiShiJian(String jingTing1QiFeiShiJian) {
            JingTing1QiFeiShiJian = jingTing1QiFeiShiJian;
        }

        public String getJingTing1DengJiKou() {
            return JingTing1DengJiKou;
        }

        public void setJingTing1DengJiKou(String jingTing1DengJiKou) {
            JingTing1DengJiKou = jingTing1DengJiKou;
        }

        public String getJingTing2() {
            return JingTing2;
        }

        public void setJingTing2(String jingTing2) {
            JingTing2 = jingTing2;
        }

        public String getJingTingZhan2() {
            return JingTingZhan2;
        }

        public void setJingTingZhan2(String jingTingZhan2) {
            JingTingZhan2 = jingTingZhan2;
        }

        public String getJingTing2JiangLuoShiJian() {
            return JingTing2JiangLuoShiJian;
        }

        public void setJingTing2JiangLuoShiJian(String jingTing2JiangLuoShiJian) {
            JingTing2JiangLuoShiJian = jingTing2JiangLuoShiJian;
        }

        public String getJingTing2QiFeiShiJian() {
            return JingTing2QiFeiShiJian;
        }

        public void setJingTing2QiFeiShiJian(String jingTing2QiFeiShiJian) {
            JingTing2QiFeiShiJian = jingTing2QiFeiShiJian;
        }

        public String getJingTing2DengJiKou() {
            return JingTing2DengJiKou;
        }

        public void setJingTing2DengJiKou(String jingTing2DengJiKou) {
            JingTing2DengJiKou = jingTing2DengJiKou;
        }

        public String getShuJuYuanMa() {
            return ShuJuYuanMa;
        }

        public void setShuJuYuanMa(String shuJuYuanMa) {
            ShuJuYuanMa = shuJuYuanMa;
        }

        @Override
        public String toString() {
            return "Bean{" +
                    "date='" + date + '\'' +
                    ", flightNumber='" + flightNumber + '\'' +
                    ", arrive='" + arrive + '\'' +
                    ", launch='" + launch + '\'' +
                    ", inOut='" + inOut + '\'' +
                    ", area='" + area + '\'' +
                    ", property='" + property + '\'' +
                    ", start3='" + start3 + '\'' +
                    ", startStation='" + startStation + '\'' +
                    ", aim3='" + aim3 + '\'' +
                    ", aimStation='" + aimStation + '\'' +
                    ", DengJiKou='" + DengJiKou + '\'' +
                    ", JingTing1='" + JingTing1 + '\'' +
                    ", JingTingZhan1='" + JingTingZhan1 + '\'' +
                    ", JingTing1JiangLuoShiJian='" + JingTing1JiangLuoShiJian + '\'' +
                    ", JingTing1QiFeiShiJian='" + JingTing1QiFeiShiJian + '\'' +
                    ", JingTing1DengJiKou='" + JingTing1DengJiKou + '\'' +
                    ", JingTing2='" + JingTing2 + '\'' +
                    ", JingTingZhan2='" + JingTingZhan2 + '\'' +
                    ", JingTing2JiangLuoShiJian='" + JingTing2JiangLuoShiJian + '\'' +
                    ", JingTing2QiFeiShiJian='" + JingTing2QiFeiShiJian + '\'' +
                    ", JingTing2DengJiKou='" + JingTing2DengJiKou + '\'' +
                    ", ShuJuYuanMa='" + ShuJuYuanMa + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "JsonBean{" +
                "mFltSchedule1=" + sheet +
                '}';
    }
}



