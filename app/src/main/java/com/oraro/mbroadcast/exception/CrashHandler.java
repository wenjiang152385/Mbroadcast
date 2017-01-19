package com.oraro.mbroadcast.exception;

import android.content.Context;
import android.os.Process;

import com.oraro.mbroadcast.utils.SPUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongyu on 2016/9/12 0012.
 */

public class CrashHandler implements UncaughtExceptionHandler {

    private static CrashHandler instance;
    private Context context;
    private String errorLogDir = null;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final int expirationDate = 7;//设置过期时间(单位：天)

    private CrashHandler() {
    }

    public synchronized static CrashHandler getInstance() {
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    public void init(final Context context, String errorLogDir) {
        this.context = context;
        this.errorLogDir = errorLogDir;
        Thread.setDefaultUncaughtExceptionHandler(this);
        //删除过期文件
        new Thread(new Runnable() {
            @Override
            public void run() {
                deleteExpirationFile();

            }
        }).start();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (ex != null) {
            disposeThrowable(ex);
            ex.printStackTrace();
        }
    }

    private void disposeThrowable(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        sb.append(ex.toString() + "\n");
        StackTraceElement[] steArray = ex.getStackTrace();
        for (StackTraceElement ste : steArray) {
            sb.append("System.err at " + ste.getClassName() + "." + ste.getMethodName() + "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")\n");
        }
        if (errorLogDir != null) {
            try {
                File file = new File(errorLogDir + "error_" + sdf.format(System.currentTimeMillis()) + ".txt");
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
        android.os.Process.killProcess(Process.myPid());
        System.exit(10);
    }

    private void deleteExpirationFile() {
        if (errorLogDir == null) {
            return;
        }
        long expirationTime = expirationDate * 24 * 3600 * 1000;
        long nowTime = System.currentTimeMillis();
        List<File> deleteList = new ArrayList<File>();
        File dir = new File(errorLogDir);
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File f : files) {
                if (f.exists()) {
                    long time = f.lastModified();
                    if (nowTime - time >= expirationTime) {
                        deleteList.add(f);
                    }
                }
            }
            for (File f : deleteList) {
                f.delete();
            }
        }
    }
}
