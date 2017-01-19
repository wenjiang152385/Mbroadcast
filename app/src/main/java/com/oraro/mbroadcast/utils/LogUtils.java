package com.oraro.mbroadcast.utils;

import android.os.Process;
import android.util.Log;

import com.oraro.mbroadcast.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * 日志相关管理类
 *
 * Created by dongyu  on 2016/10/25 0025.
 */

public class LogUtils {


    public static boolean isDebug = true;// 是否需要打印bug，可以在application的onCreate函数里面初始化
    private static final String TAG = "-------------->"; //默认的Tag
    private static String errorLogDir = Constants.ERRORLOGDIR;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // 下面四个是默认tag的函数
    public static void i(String msg) {
        if (isDebug)
            Log.i(TAG, msg);
    }

    public static void d(String msg) {
        if (isDebug)
            Log.d(TAG, msg);
    }

    public static void e(String msg) {
        if (isDebug)
            Log.e(TAG, msg);
    }

    public static void v(String msg) {
        if (isDebug)
            Log.v(TAG, msg);
    }

    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg) {
        if (isDebug)
            Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (isDebug)
            Log.d(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (isDebug)
            Log.e(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (isDebug)
            Log.v(tag, msg);
    }

    public static void disposeThrowable(String TAG,Throwable ex) {
        StringBuffer sb = new StringBuffer();
        sb.append(ex.toString() + "\n");
        StackTraceElement[] steArray = ex.getStackTrace();
        for (StackTraceElement ste : steArray) {
            sb.append("System.err at " + ste.getClassName() + ".\n" + ste.getMethodName() + "\n(" + ste.getFileName() + ":" + ste.getLineNumber() + ")\n");
        }
        if (errorLogDir != null) {
            try {
                File file = new File(errorLogDir + "error_" +TAG+"_"+ sdf.format(System.currentTimeMillis()) + ".txt");
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(sb.toString().getBytes("utf-8"));
                fos.flush();
                fos.close();
                fos = null;
            } catch (FileNotFoundException e) {

            } catch (IOException e) {

            }
        }
    }
}
