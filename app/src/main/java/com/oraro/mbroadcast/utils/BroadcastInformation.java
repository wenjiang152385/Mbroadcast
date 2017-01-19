package com.oraro.mbroadcast.utils;

/**
 * 播报内容
 * 方法名称C开头的是获取中文播报内容，E开头的是获取英文播报内容
 * @author 刘彬
 */
public class BroadcastInformation {
    /**
     * 开始值机——中文
     * @param destination 目的地
     * @param airCompanyName 航空公司名称
     * @param flightNumber 航班号
     * @return 值机播报的内容
     */
    public String CCheckIn(String destination,String airCompanyName,String flightNumber){
        String checkInMessage = "前往"+destination+"的旅客请注意：\n\t\t\t您乘坐的"+airCompanyName+
                flightNumber+"次航班现在开始办理值机手续。请您到柜台办理。谢谢！";
        return checkInMessage;
    }

    /**
     * 开始值机——英文
     * @param destination 目的地
     * @param airCompanyName 航空公司名称
     * @param flightNumber 航班号
     * @return 值机播报的内容
     */
    public String ECheckIn(String destination,String airCompanyName,String flightNumber){
        String checkInMessage ="Ladies and Gentlemen,may I have your attention please:flight "+
                airCompanyName+" "+flightNumber+" to "+destination+" at counter.thank you!";
        return checkInMessage;
    }

    /**
     * 催促旅客值机——中文
     * @param destination 目的地
     * @param airCompanyName 航空公司名称
     * @param flightNumber 航班号
     * @return 催促值机播报的内容
     */
    public String CUrgeCheckIn(String destination,String airCompanyName,String flightNumber){
        String urgeCheckInMessage = "前往"+destination+"的旅客请注意：\n\t\t\t您乘坐的"+airCompanyName+
                flightNumber+"次航班将截止办理值机手续。乘坐本次航班没有办理手续的旅客，请马上到柜台办理。谢谢！";
        return urgeCheckInMessage;
    }

    /**
     * 催促旅客值机——英文
     * @param destination 目的地
     * @param airCompanyName 航空公司名称
     * @param flightNumber 航班号
     * @return 催促值机播报的内容
     */
    public String EUrgeCheckIn(String destination,String airCompanyName,String flightNumber){
        String urgeCheckInMessage = "Ladies and Gentlemen,may I have your attention please:flight "+
                airCompanyName+" "+flightNumber+" to "+destination+" will be closed at passengers who have not been " +
                "checked in for this flight,please go to counter.immediately thank you!";
        return urgeCheckInMessage;
    }

    /**
     * 最后值机催促——中文
     * @param destination 目的地
     * @param airCompanyName 航空公司名称
     * @param flightNumber 航班号
     * @return 最后值机催促播报的内容
     */
    public String CLastUrgeCheckIn(String destination,String airCompanyName,String flightNumber){
        String lastMessage = "前往"+destination+"的旅客请注意：\n\t\t\t您乘坐的"+airCompanyName+
                flightNumber+"次航班很快就要截止办理值机手续。乘坐本次航班没有办理手续的旅客，请马上到柜台办理。谢谢！";
        return lastMessage;
    }

    /**
     * 最后值机催促——英文
     * @param destination 目的地
     * @param airCompanyName 航空公司名称
     * @param flightNumber 航班号
     * @return 最后值机催促播报的内容
     */
    public String ELastUrgeCheckIn(String destination,String airCompanyName,String flightNumber){
        String lastMessage = "Ladies and Gentlemen,may I have your attention please:flight "+
                airCompanyName+" "+flightNumber+" to "+destination+" will be closed soon.passengers who have not been" +
                " checked in for this flight,please go to counter.immediately thank you!";
        return lastMessage;
    }

    /**
     * 催促安检——中文
     * @param destination 目的地
     * @param airCompanyName 航空公司名称
     * @param flightNumber 航班号
     * @return 催促安检播报的内容
     */
    public String CUrgeSecurityCheck(String destination,String airCompanyName,String flightNumber){
        String securityCheckMessage = "前往"+destination+"的旅客请注意：\n\t\t\t您乘坐的"+airCompanyName+
                flightNumber+"次航班很快就要起飞了，乘坐本次航班已经办理值机手续，还没有安检的旅客，请您尽快通过人身安全检查，到登机口迅速登机。谢谢！";
        return securityCheckMessage;
    }

    /**
     * 催促安检——英文
     * @param destination 目的地
     * @param airCompanyName 航空公司名称
     * @param flightNumber 航班号
     * @param boardNumber  登机口号
     * @return 催促安检播报的内容
     */
    public String EUrgeSecurityCheck(String destination,String airCompanyName,String flightNumber,int boardNumber){
        String securityCheckMessage = "Ladies and Gentlemen,may I have your attention please:flight "+
                airCompanyName+" "+flightNumber+" to "+destination+" is now boarding.Would you please have your belongings" +
                " and boarding passes ready and board the aircraft through gate "+boardNumber+".thank you!";
        return securityCheckMessage;
    }

    /**
     * 值机延误——中文
     * @param destination 目的地
     * @param airCompanyName 航空公司名称
     * @param flightNumber 航班号
     * @return 值机延误播报的内容
     */
    public String CDelayCheckIn(String destination,String airCompanyName,String flightNumber){
        String delayCheckIn = "前往"+destination+"的旅客请注意：\n\t\t\t您乘坐的"+airCompanyName+flightNumber+
                "次航班起飞时间待定。请您在出发厅休息，等候通知。谢谢！";
        return delayCheckIn;
    }

    /**
     * 值机延误——英文
     * @param destination 目的地
     * @param airCompanyName 航空公司名称
     * @param flightNumber 航班号
     * @return 值机延误播报的内容
     */
    public String EDelayCheckIn(String destination,String airCompanyName,String flightNumber){
        String delayCheckIn =  "Ladies and Gentlemen,may I have your attention please:flight "+
                airCompanyName+" "+flightNumber+" to "+destination+" will be further delayed." +
                "please wait in the departure hall for further information.thank you!";
        return delayCheckIn;
    }

    /**
     * 过站旅客候机——中文
     * @param destination 目的地
     * @return 过站旅客候机播报的内容
     */
    public String COverStationWait(String destination){
        String overStationWait ="前往"+destination+"的过站旅客请注意：\n\t\t\t请您在候机厅休息，等候通知。谢谢！";
        return overStationWait;
    }

    /**
     * 过站旅客候机——英文
     * @param destination 目的地
     * @param airCompanyName 航空公司名称
     * @param flightNumber 航班号
     * @return 过站旅客候机播报的内容
     */
    public String EOverStationWait(String destination,String airCompanyName,String flightNumber){
        String overStationWait ="Ladies and Gentlemen,may I have your attention please:flight "+
                airCompanyName+" "+flightNumber+" to "+destination+" Attention please:please wait in " +
                "the departure hall for further information.thank you!";
        return overStationWait;
    }

    /**
     * 开始登机——中文
     * @param destination 目的地
     * @param airCompanyName 航空公司名称
     * @param flightNumber 航班号
     * @param someWhere  地点
     * @return 开始登机播报的内容
     */
    public String CStartBoard(String destination,String airCompanyName,String flightNumber,String someWhere){
        String startBoard ="前往"+destination+"的旅客请注意：\n\t\t\t您乘坐的"+airCompanyName+flightNumber+
                "次航班现在开始登机。请带好您的随身物品，出示登机牌，由"+someWhere+"上飞机。谢谢！";
        return startBoard;
    }

    /**
     * 开始登机——英文
     * @param destination 目的地
     * @param airCompanyName 航空公司名称
     * @param flightNumber 航班号
     * @return 开始登机播报的内容
     */
    public String EStartBoard(String destination,String airCompanyName,String flightNumber){
        String startBoard = "Ladies and Gentlemen,may I have your attention please:flight "+
                airCompanyName+" "+flightNumber+" to "+destination+" is now boarding.Would you please " +
                "have your belongings and boarding passes ready and board the aircraft through gat." +
                "thank you!";
        return startBoard;
    }

    /**
     * 催促登机——中文
     * @param destination 目的地
     * @param airCompanyName 航空公司名称
     * @param flightNumber 航班号
     * @param someWhere 地点
     * @return 催促登机播报的内容
     */
    public String CUrgeBoard(String destination,String airCompanyName,String flightNumber,String someWhere){
        String urgeBoard ="请前往"+destination+"的旅客请注意：\n\t\t\t您乘坐的"+airCompanyName+flightNumber+
                "次航班很快就要起飞了，还没有登机的旅客，请由"+someWhere+"迅速登机。谢谢！";
        return urgeBoard;
    }

    /**
     * 催促登机——英文
     * @param destination 目的地
     * @param airCompanyName 航空公司名称
     * @param flightNumber 航班号
     * @param boardNumber  登机口号
     * @return 催促登机播报的内容
     */
    public String EUrgeBoard(String destination,String airCompanyName,String flightNumber,int boardNumber){
        String urgeBoard ="Ladies and Gentlemen,may I have your attention please:flight "+
                airCompanyName+" "+flightNumber+" to "+destination+" who have not boarding," +
                "please be quick to board through gate "+boardNumber+".thank you!";
        return urgeBoard;
    }

    /**
     * 过站旅客登机——中文
     * @param destination 目的地
     * @param airCompanyName 航空公司名称
     * @param flightNumber 航班号
     * @param someWhere  地点
     * @return 过站旅客登机播报的内容
     */
    public String COverStationBoard(String destination,String airCompanyName,String flightNumber,String someWhere){
        String overStationBoard="请前往"+destination+"的过站旅客请注意：\n\t\t\t您乘坐的"+airCompanyName+flightNumber+
                "次航班现在开始登机，请过站旅客出示过站登机牌，由"+someWhere+"先上飞机。谢谢！";
        return overStationBoard;
    }

    /**
     * 过站旅客登机——英文
     * @param destination 目的地
     * @param airCompanyName 航空公司名称
     * @param flightNumber 航班号
     * @param boardNumber   登机口号
     * @return 过站旅客登机播报的内容
     */
    public String EOverStationBoard(String destination,String airCompanyName,String flightNumber,int boardNumber){
        String overStationBoard="Ladies and Gentlemen,may I have your attention please:flight "+
                airCompanyName+" "+flightNumber+" to "+destination+" is now ready for boarding.Transit " +
                "passengers please show your passes and board first through gate "+boardNumber+". thank you!";
        return overStationBoard;
    }

    /**
     * 催促过站旅客登机——中文
     * @param destination 目的地
     * @param airCompanyName 航空公司名称
     * @param flightNumber 航班号
     * @param someWhere  地点
     * @return 催促过站旅客登机播报的内容
     */
    public String CUrgeOverStationBoard(String destination,String airCompanyName,String flightNumber,String someWhere){
        String urgeOverStationBoard="请前往"+destination+"的过站旅客请注意：\n\t\t\t您乘坐的"+airCompanyName+flightNumber+
                "次航班现在开始登机，请过站旅客出示过站登机牌，由"+someWhere+"先上飞机。谢谢！";
        return urgeOverStationBoard;
    }

    /**
     * 催促过站旅客登机——英文
     * @param destination 目的地
     * @param airCompanyName 航空公司名称
     * @param flightNumber 航班号
     * @param boardNumber  登机口号
     * @return 催促过站旅客登机播报的内容
     */
    public String EUrgeOverStationBoard(String destination,String airCompanyName,String flightNumber,int boardNumber){
        String urgeOverStationBoard="Ladies and Gentlemen,may I have your attention please:flight "+
                airCompanyName+" "+flightNumber+" to "+destination+" is now ready for boarding.Transit " +
                "passengers please show your passes and board first through gate "+boardNumber+". thank you!";
        return urgeOverStationBoard;
    }

    /**
     * 出港延误——中文
     * @param destination 目的地
     * @param airCompanyName 航空公司名称
     * @param flightNumber 航班号
     * @return 出港延误播报的内容
     */
    public String CDepartureDelay(String destination,String airCompanyName,String flightNumber){
        String departureDelay="前往"+destination+"的旅客请注意：\n\t\t\t我们抱歉的通知您乘坐的"+airCompanyName+
                flightNumber+"次航班不能按时起飞，起飞时间待定。在此我们深表歉意。请您在候机厅休息，等待通知。谢谢！";
        return departureDelay;
    }

    /**
     * 出港延误——英文
     * @param destination 目的地
     * @param airCompanyName 航空公司名称
     * @param flightNumber 航班号
     * @return 出港延误播报的内容
     */
    public String EDepartureDelay(String destination,String airCompanyName,String flightNumber){
        String departureDelay="Ladies and Gentlemen,may I have your attention please:flight "+
                airCompanyName+" "+flightNumber+" to "+destination+" will be further delayed.thank you!";
        return departureDelay;
    }

    /**
     * 旅客宾馆通知——中文
     * @param destination 目的地
     * @param airCompanyName 航空公司名称
     * @param flightNumber 航班号
     * @param boardNumber  登机口号
     * @return 旅客宾馆通知播报的内容
     */
    public String CHotelNotice(String destination,String airCompanyName,String flightNumber,String boardNumber){
        String hotelNotice="乘坐"+airCompanyName+flightNumber+"前往"+destination+"的旅客请注意：\n\t\t\t请您到"+boardNumber+
                "号登机口上车，我们将安排您到宾馆休息，请您在工作人员的带领下前往宾馆。多谢您的合作！";
        return hotelNotice;
    }

    /**
     * 呼叫旅客——中文
     * @param travellerName 旅客姓名
     * @param destination 目的地
     * @param airCompanyName 航空公司名称
     * @param flightNumber 航班号
     * @param someWhere 地点
     * @return 呼叫旅客播报的内容
     */
    public String CCallTraveller(String travellerName,String destination,String airCompanyName,String flightNumber,String someWhere){
        String callTraveller="乘坐"+airCompanyName+flightNumber+"前往"+destination+"的旅客"+travellerName+
                "，请您马上到"+someWhere+"。谢谢！";
        return callTraveller;
    }

    /**
     * 航班取消——中文
     * @param destination 目的地
     * @param airCompanyName 航空公司名称
     * @param flightNumber 航班号
     * @return 航班取消播报的内容
     */
    public String CFlightCancel(String destination,String airCompanyName,String flightNumber){
        String flightCancel="前往"+destination+"的旅客请注意：\n\t\t\t我们抱歉的通知您乘坐的"+airCompanyName+flightNumber+
                "次航班决定取消今日飞行，在此我们深表歉意。谢谢！";
        return flightCancel;
    }

    /**
     * 航班取消——英文
     * @param destination 目的地
     * @param airCompanyName 航空公司名称
     * @param flightNumber 航班号
     * @return 航班取消播报的内容
     */
    public String EFlightCancel(String destination,String airCompanyName,String flightNumber){
        String flightCancel="Ladies and Gentlemen, may I have your attention please: flight "+
                airCompanyName+" "+flightNumber+" to "+destination+" has been cancelled.thank you!";
        return flightCancel;
    }

    /**
     * 特殊航班服务——中文
     * @param destination 目的地
     * @param airCompanyName 航空公司名称
     * @param flightNumber 航班号
     * @param someWhere 地点
     * @return 特殊航班服务播报的内容
     */
    public String CSpecialService(String destination,String airCompanyName,String flightNumber,String someWhere){
        String specialService="乘坐"+airCompanyName+flightNumber+"次航班前往"+destination+"的旅客请注意：\n\t\t\t请您到"+someWhere+
                "。多谢您的合作！";
        return specialService;
    }

    /**
     * 特殊航班服务——英文
     * @param destination 目的地
     * @param airCompanyName 航空公司名称
     * @param flightNumber 航班号
     * @param someWhere 地点
     * @return 特殊航班服务播报的内容
     */
    public String ESpecialService(String destination,String airCompanyName,String flightNumber,String someWhere){
        String specialService="Passengers for flight "+airCompanyName+" "+flightNumber+" to "+destination+
                " Attention please:please go to "+someWhere+".thank you!";
        return specialService;
    }

    /**
     * 登机口变更——中文
     * @param destination 目的地
     * @param airCompanyName 航空公司名称
     * @param flightNumber 航班号
     * @param boardNumber 登机口号
     * @return 登机口变更播报的内容
     */
    public String CChangeBoardNumber(String destination,String airCompanyName,String flightNumber,String boardNumber){
        String changeBoardNumber="前往"+destination+"的旅客请注意：\n\t\t\t您乘坐的"+airCompanyName+flightNumber+"次航班的登机口变为"+
                boardNumber+"号。请您到指定的候机厅候机，等候广播通知登机。谢谢！";
        return changeBoardNumber;
    }

    /**
     * 登机口变更——英文
     * @param destination 目的地
     * @param airCompanyName
     * @param flightNumber 航班号
     * @param boardNumber 登机口号
     * @return 登机口变更播报的内容
     */
    public String EChangeBoardNumber(String destination,String airCompanyName,String flightNumber,int boardNumber){
        String changeBoardNumber="Ladies and Gentlemen,may I have your attention please:flight "+
                airCompanyName+" "+flightNumber+" to "+destination+" has been changed to gate "+boardNumber+
                ".thank you!";
        return changeBoardNumber;
    }

    /**
     * 全部延误通知
     * @return 全部延误通知播报的内容
     */
    public String CAllDelay(){
        String allDelay="各位旅客请注意：\n\t\t\t我们抱歉的通知，由于航路天气不够飞行标准，由本站始发的所有航班不能按时起飞，起飞时间待定。在此我们深表歉意。" +
                "请您在候机厅休息，等候通知。";
        return allDelay;
    }

    /**
     * 失物招领
     * @return 失物招领播报的内容
     */
    public String CLostAndFound(){
        String lnf="各位旅客请注意：\n\t\t\t哪位旅客错拿了登机牌，请您交到1号服务台，谢谢！";
        return lnf;
    }

    /**
     * 旅客到达引导
     * @param destination 目的地
     * @return 旅客到达引导播报的内容
     */
    public String CTravellerGuide(String destination){
        String tg="旅客，你们好，\n\t\t\t欢迎您来到"+destination+",您交运的行李，请您到行李领取处领取，民航有客车送您到市区，请在到达厅门口购票上车，希望您在本市过得愉快！";
        return tg;
    }
}
