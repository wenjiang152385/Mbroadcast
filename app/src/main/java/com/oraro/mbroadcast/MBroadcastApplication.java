package com.oraro.mbroadcast;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.oraro.mbroadcast.broadcasts.NetWorkReceiver;
import com.oraro.mbroadcast.exception.CrashHandler;
import com.oraro.mbroadcast.model.MinaFileParam;
import com.oraro.mbroadcast.service.IMyAidlInterface;
import com.squareup.leakcanary.LeakCanary;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dongyu on 2016/8/9 0009.
 */
public class MBroadcastApplication extends MultiDexApplication {


    private static Context mContext;
    private static String  packageName;
    private static Handler frushHandler;//刷新界面的handler
    private static Long playID =(long)-1;//当前播放的对象
    private static IMyAidlInterface iMyAidlInterface;
    public static boolean isincout = false;// true表示正在播放温馨提示内容
    public static boolean isUrgent = false;// true表示正在播放紧急播放内容

    public  static  IMyAidlInterface getIMyAidlInterface(){
        return iMyAidlInterface;
    }
    public  static  void setIMyAidlInterface(IMyAidlInterface iMyAidlInterface){
        MBroadcastApplication.iMyAidlInterface = iMyAidlInterface;
    }
    public  static  Context getMyContext(){
        return mContext;
    }
    public static String getMyPackageName(){
        return packageName;
    }

    //用来记录文件传输时需要的参数
    public static Map<String,MinaFileParam> mMinaFileParams = new HashMap<>();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
        mContext=getApplicationContext();
        packageName = getPackageName();
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(this,Constants.ERRORLOGDIR);
        //第一次进入应用，创建EXCEL、MEDIA的文件目录
        //判断sd卡是否存在
        try {
            boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
            if (sdCardExist) {
                File sdDir = Environment.getExternalStorageDirectory();//获取根目录
                //创建文件夹
                String path1=sdDir.getPath()+ Constants.SELECTED_EXCEL_FILE_DIRECTORY;
                String path2=sdDir.getPath()+Constants.SELECTED_MEDIA_FILE_DIRECTORY ;
                File file1=new File(path1);
                File file2=new File(path2);
                if(!file1.exists()){
                    file1.mkdir();
                }
                if(!file2.exists()){
                    file2.mkdir();
                }
            }
        } catch (Exception e) {

        }
    }

    public static Handler getFrushHandler() {
        return frushHandler;
    }

    public static void setFrushHandler(Handler frushHandler) {
        MBroadcastApplication.frushHandler = frushHandler;
    }


    public static Long getPlayID() {
        return playID;
    }

    public static void setPlayID(Long playID) {
        MBroadcastApplication.playID = playID;
    }
}
