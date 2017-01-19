package com.oraro.mbroadcast;

import android.os.Environment;

import com.oraro.mbroadcast.utils.SPUtils;

import java.io.File;

/**
 * Created by weijiaqi on 2016/8/25 0025.
 */
public class Constants {

    public static final int A_PLAY = 123456;
    public static final int A_PLAYED = 12345;

    public static final String AIR_COMPANY_NAME_DF = "东方航空公司";
    public static final String AIR_COMPANY_NAME_SH = "上海航空公司";
    public static final String SELECTED_EXCEL_FILE_DIRECTORY = File.separator + "mbroadcast_excel";
    public static final String SELECTED_MEDIA_FILE_DIRECTORY = File.separator + "mbroadcast_media";

    public static class FlightInfoConstants {
        public static final String[] PART_ONE = {"日期", "计划起飞", "性质", "目的(3码)"};
        public static final String[] PART_TWO = {"航班号", "进出港", "始发(3码)", "目的站"};
        public static final String[] PART_THREE = {"计划到达", "国际/国内", "始发站",
                "登机口"};

        public static final String[] PART_FOUR = {"经停1(3码)", "经停1起飞时间"};

        public static final String[] PART_FIVE = {"经停站1", "经停1登机口"};

        public static final String[] PART_SIX = {"经停1降落时间"};


        public static final String[] PART_SEVEN = {"经停2", "经停2起飞时间"};

        public static final String[] PART_EIGHT = {"经停站2", "经停2登机口"};

        public static final String[] PART_NINE = {"经停2降落时间"};


        public static final String[] PART_TEN = {"数据源来自(国际三码)"};


    }

    public final static int UPDATE_FLIGTH = 101;
    public final static int MONDAY = 1;
    public final static int TUESDAY = 2;
    public final static int WENDESDAY = 3;
    public final static int THURSDAY = 4;
    public final static int FRIDAY = 5;
    public final static int SATURDAY = 6;
    public final static int SUNDAY = 7;
    public final static int BROADCAST_FRAGMENT = 1;
    public final static int FLIGHT_FRAGMENT = 2;

    public final static int TYPE_INTER_CUT_DATA = 0;//0 代表温馨提示
    public final static int TYPE_URGENT_DATA = 1;//1 代表紧急广播
    public final static int WEBVIEW_NO_EDIT = 1;//1 设置html文件不可编辑
    public final static int WEBVIEW_CAN_EDIT = 2;//2 设置html文件可编辑

    /**
     * 生成播放列表控制参数
     *
     * @author 王子榕
     */
    public interface CreatePlayList {
        //提前90分钟进行值机
        public final static int CHECK_IN_BEFORE_TIME = 90 * 60 * 1000;
        //值机播报次数
        public  int CHECK_IN_TIMES = SPUtils.getBroadcastNumber(MBroadcastApplication.getMyContext());

        //提前60分钟催促值机
        public final static long URGE_CHECK_IN_BEFORE_TIME = 60 * 60 * 1000;
        //催促值机播报次数
        public  int URGE_CHECK_IN_TIMES = SPUtils.getBroadcastNumber(MBroadcastApplication.getMyContext());


        //提前41分钟催促最后值机
        public final static long LAST_URGE_CHECK_IN_BEFORE_TIME = 41 * 60 * 1000;
        //催促最后值机播报次数
        public  int LAST_URGE_CHECK_IN_TIMES = SPUtils.getBroadcastNumber(MBroadcastApplication.getMyContext());


        //提前30分钟催促安检
        public final static int URGE_SECURITY_CHECK_BEFORE_TIME = 30 * 60 * 1000;
        //催促安检播报次数
        public  int URGE_SECURITY_CHECK_TIMES = SPUtils.getBroadcastNumber(MBroadcastApplication.getMyContext());


        //提前20分钟值机延误
        public final static int DELAY_CHECK_IN_BEFORE_TIME = 20 * 60 * 1000;
        //值机延误播报次数
        public  int DELAY_CHECK_IN_BEFORE_TIMES = SPUtils.getBroadcastNumber(MBroadcastApplication.getMyContext());

    }

    public final static int TemporaryPlayFragment = 0;
    public final static String PACKAGENAME = "com.oraro.mbroadcast";
    public final static String ERRORLOGDIR = Environment.getExternalStorageDirectory().toString() + "/";
    public final static int Analytic_Cmpletion_Notice = 10002;
    public final static int Analytic_Cmpletion_Delay = 10010;
    public final static int BROADCAST_ADD = 10003;
    public final static int URGENT_BROADCAST_INSTANT = 12;
    public static long URGENT_BROADCAST_INSTANT_id = -1;
    public final static int FLIGHT_EXCEL_ADD = 10004;
    public static long TEMP_PLAYEN_ID = 0;
    public static long FLIGHTTEMP_ID = 0;

    public interface HandlerConstants {
        public final static int PLAY = 1;
        public final static int QUEUE = 2;
        public final static int COMPLETED = 3;
    }

    public static interface SettingsConstants {
        public static String AUTO_PLAY = "autoPlay";
        public static String SPACE = "space";
    }


    /**
     * 本地局域网IP地址
     **/
    public static String WEB_MATCH_PATH = "-1";
    public static String WEB_MATCH_PATH1 = "-1";
    public static String WEB_MATCH_PATH2 = "-1";
    public static String WEB_MATCH_PATH3 = "-1";
    /**
     * 本地局域网的端口号
     **/
    public final static int WEB_MATCH_PORT = 4440;
    //接收自动播报服务端口
    public final static int WEB_AUTO_MATCH_PORT = 4441;
    public final static int WEB_FILE_MATCH_PORT = 4442;
//    public static boolean isFlieC = false;
    /**
     *EventBus指令
     **/
    public final static int TTS_PLAY = 0;
    public final static int MD_PLAY = 1;
    public final static int MD_FILE_UPDATE = 2;
    public final static int A_DATA_UPDATE = 3;
    public final static int A_DATA_ADD = 4;
    public final static int A_DATA_DELETE = 5;
    public final static int Import_Excel_File = 6;
    public final static int MINA_START_RECV_EXCEL_FILE = 7;
    public final static int MINA_START_RECV_MEDIA_FILE = 8;
    public final static int A_FLIGHT_UPDATE = 9;
    public final static int A_FLIGHT_ADD = 10;
    public final static int A_FLIGHT_DELETE = 11;
    public final static int MINA_TEST = 12;
    public final static int MINA_TEST_CONNECT_int = 13;
    public final static int EXCEL_Transfer_Finish = 14;
    public final static int MD_Transfer_Finish = 15;
    public final static int NO_DATA = 16;
    public final static int NO_MD_NOT_FIND = 17;
    public final static int CHECK_SIGN_FAIL_INT = 18;
    public final static int EXCEL_Transfer_Finish_int = 19;
    public final static int UPDATE_PLAYVO_ONE = 20;
    public final static int EXCEL_Transfer_Fail_int = 21;
    public final static int MD_Transfer_Fail_int = 22;
    public final static int MD_Transfer_Finish_int = 23;
    public final static int Audio_NO_DATA = 24;
    public final static int SETTINGS_WEEK = 25;
    public final static int File_Trans_Deuplicate = 26;
    public final static int File_Trans_Length = 27;
    public final static int File_Trans_Loading = 28;
    public final static int BACK_MAIN_CLOSE         =      29;
    public final static int CALL_TO_START         =      30;
    public final static int SERVICE1_CONNECT_SUCESSFUL         =      31;
    public final static int NEWWORK_CHANGE         =      32;
    public final static int A_DATA_UPDATE_SUCCESS = 33;
    public final static String EXCEL_Transfer_Fail = "EXCEL_Transfer_Fail";
    public final static String MD_Transfer_Fail = "MD_Transfer_Fail";
    public final static String EXCEL_Transfer_Pogress = "EXCEL_Transfer_Pogress";
    public final static String Audio_NO_DATA_String = "Audio_NO_DATA";
    public final static String MD_Transfer_Finish_String = "MD_Transfer_Finish_String";
    public final static String EXCEL_Transfer_Finish_String = "EXCEL_Transfer_Finish_String";
    public final static String MINA_TEST_CONNECT_String = "5496646";
    public final static String MINA_Connect_Successfully = "9999999";
    public final static String CHECK_SIGN_FAIL = "444444444";
    public final static String HUAWEI_DeviceId = "47Q6R16720000828";
}