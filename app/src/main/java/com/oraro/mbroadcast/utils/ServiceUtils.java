package com.oraro.mbroadcast.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import java.util.List;

/**
 * @author 刘彬
 */
public class ServiceUtils {
    /**
     * 进程是否运行
     * @param context 上下文
     * @param proessName 进程名
     * @return
     */
    public static boolean isProessRunning(Context context,String proessName){
        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> lists = am.getRunningAppProcesses();
        for (RunningAppProcessInfo info : lists){
            if (info.processName.equals(proessName)){
                isRunning = true;
            }
        }
        return isRunning;
    }
    /**
     * 进程是否运行
     * @param context 上下文
     * @param serviceName 服务名
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceName){
        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> lists = am.getRunningServices(30);
        for (RunningServiceInfo info : lists){
            if (info.service.getClassName().equals(serviceName)){
                isRunning = true;
            }
        }
        return isRunning;
    }
}
