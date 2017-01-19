package com.oraro.mbroadcast.mina.server;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.dao.FlightInfoDao;
import com.oraro.mbroadcast.dao.PlayEntryDao;
import com.oraro.mbroadcast.logicService.GenerateService;
import com.oraro.mbroadcast.model.FlightInfo;
import com.oraro.mbroadcast.model.MinaFileParam;
import com.oraro.mbroadcast.model.PlayEntry;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.model.SocketJson;
import com.oraro.mbroadcast.utils.LogUtils;
import com.oraro.mbroadcast.utils.MD5Util;
import com.oraro.mbroadcast.utils.ParseExcelUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于mina 服务器上传下载
 * 流处理线程公共类
 *
 * @author dongyu
 */
public class IoStreamThreadWork extends Thread {
    private final static String TAG = IoStreamThreadWork.class.getSimpleName();
    public static final int BUFFER_SIZE = 1024 * 10;

    private BufferedInputStream bis;
    private BufferedOutputStream bos;

    private MinaFileParam mMinaFileParam;

    //得到数据的大小
    private int dataLength = 0;
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    public void setMinaFileParam(MinaFileParam param){
        LogUtils.e(TAG, "setMinaFileParam::param = " + param);
        mMinaFileParam = param;
    }

    public MinaFileParam getMinaFileParam(){
        return mMinaFileParam;
    }

    public IoStreamThreadWork(InputStream in, OutputStream os) {
        bis = new BufferedInputStream(in);
        bos = new BufferedOutputStream(os);
    }

    public IoStreamThreadWork(InputStream in, MinaFileParam param) {
        if(null == param){
            return;
        }
        bis = new BufferedInputStream(in);
        setMinaFileParam(param);
        try {
            bos = new BufferedOutputStream(new FileOutputStream(new File(mMinaFileParam.mToSaveFilePath)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String getMyIp() {
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) MBroadcastApplication.getMyContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);
        return ip;
    }

    private String intToIp(int i) {

        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }

    public synchronized void run() {
        LogUtils.e(TAG, "IoStreamThreadWork to save excel file run bos = " + bos);
        if (null == bos) {
            if(bis != null){
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return;
        }

        byte[] bufferByte = new byte[BUFFER_SIZE];
        int tempData = 0;
        int temppross = 0;
        try {
            //如果是客户端
            if(!mMinaFileParam.mIsToSaveFile){
                dataLength=bis.available();
                SimpleEvent simpleEvent=new SimpleEvent(Constants.File_Trans_Length);
                simpleEvent.setmDataLength(dataLength);
                EventBus.getDefault().postSticky(simpleEvent);
            }

            int count = 0;
            while ((tempData = bis.read(bufferByte)) != -1) {
                count++;
                temppross += tempData;

                bos.write(bufferByte, 0, tempData);
                //如果是服务端,每10次更新一下进度条
                if(mMinaFileParam.mIsToSaveFile && count >= 10){
                    SocketJson socketJson = new SocketJson();
                    socketJson.setType(Constants.File_Trans_Loading);
                    socketJson.setProgess(temppross);
                    //LogUtils.e(TAG, "IoStreamThreadWork to save excel file run while--->" + mMinaFileParam.mStringSession+"--->"+mMinaFileParam.mStringSession.isConnected()+"--->"+mMinaFileParam.mStringSession.isActive());
                    mMinaFileParam.mStringSession.write(gson.toJson(socketJson));
                    count = 0;
                }
            }
            //如果是服务端
            if(mMinaFileParam.mIsToSaveFile){
                if(count > 0){
                    SocketJson socketJson = new SocketJson();
                    socketJson.setType(Constants.File_Trans_Loading);
                    socketJson.setProgess(temppross);
                    mMinaFileParam.mStringSession.write(gson.toJson(socketJson));
                }
                bos.flush();
            }
            LogUtils.e(TAG, "IoStreamThreadWork to save excel file run finish" + this);
        } catch (Exception e) {
            LogUtils.disposeThrowable(TAG,e);
            e.printStackTrace();
            EventBus.getDefault().postSticky(new SimpleEvent(Constants.EXCEL_Transfer_Fail_int));
        } finally {
            try {
                bos.close();
                bis.close();
                //如果是客户端
                if(!mMinaFileParam.mIsToSaveFile){
                    mMinaFileParam.mFileSession.close(true) ;
                }
                if (null == mMinaFileParam ||
                        !mMinaFileParam.mIsToSaveFile ||
                        null == mMinaFileParam.mStringSession ||
                        TextUtils.isEmpty(mMinaFileParam.mToSaveFilePath)) {
                    return;
                }
                String suffix = mMinaFileParam.mToSaveFilePath.substring(mMinaFileParam.mToSaveFilePath.lastIndexOf(".") + 1);
                LogUtils.e(TAG, "IoStreamThreadWork to save excel file suffix = " + suffix + "this = " + this);
                DBManager mDBManager = DBManager.getInstance(MBroadcastApplication.getMyContext());
                if (suffix.equals("xls") || suffix.equals("XLS")) {
                    //EXCEL文件解析
                    File receiveFile = new File(mMinaFileParam.mToSaveFilePath);
                    String HWmd5sum = mMinaFileParam.mFileMD5;
                    String sound = MD5Util.getFileMD5String(receiveFile);
                    if (!sound.equals(HWmd5sum)) {
                        SocketJson socketJson = new SocketJson();
                        socketJson.setIp(getMyIp());
                        socketJson.setType(Constants.EXCEL_Transfer_Fail_int);
                        socketJson.setTypeName(Constants.EXCEL_Transfer_Fail);
                        PlayEntry playEntry = new PlayEntry();
                        playEntry.setFileParentPath(mMinaFileParam.mOriginalFilePath);
                        playEntry.setFileName(mMinaFileParam.mOriginalFileName);
                        playEntry.setTextDesc("Import_Excel_File");
                        socketJson.setPlayEntry(playEntry);
                        mMinaFileParam.mStringSession.write(gson.toJson(socketJson));
                        mMinaFileParam.setTransFileFinish(true);
                        LogUtils.e(TAG, "md5 failed session =  " + mMinaFileParam.mStringSession);
                        return;
                    }

                    List<FlightInfo> list = new ArrayList<FlightInfo>();
                    ParseExcelUtils.parse(mMinaFileParam.mToSaveFilePath,list);
                    FlightInfoDao dao = mDBManager.getFlightInfoDao(DBManager.WRITE_ONLY);
                    mDBManager.insertList(list, dao);
                    FlightInfoDao dao2 = mDBManager.getFlightInfoDao(DBManager.READ_ONLY);
//                    List<FlightInfo> list2 = mDBManager.queryAll(dao2);
                    GenerateService s = new GenerateService();
                    s.copeFlightToTemp(list);
                    s.generatePlay();
                    PlayEntryDao playEntryDao = mDBManager.getPlayEntryDao(DBManager.READ_ONLY);
                    int i = mDBManager.queryAll(playEntryDao).size();
                    LogUtils.e(TAG, "IoStreamThreadWork excel finsh = " + list.size());
                    try {
                        if (mMinaFileParam.mStringSession == null) {
                            LogUtils.e(TAG, "IoStreamThreadWork excel session1 =  " + mMinaFileParam.mStringSession);
                        } else {
                            LogUtils.e(TAG, "IoStreamThreadWork to save excel session2 =  " + mMinaFileParam.mStringSession);
                            SocketJson socketJson = new SocketJson();
                            socketJson.setIp(getMyIp());
                            socketJson.setTypeName(Constants.EXCEL_Transfer_Finish_String);
                            socketJson.setType(Constants.EXCEL_Transfer_Finish_int);
                            mMinaFileParam.mStringSession.write(gson.toJson(socketJson));
                            mMinaFileParam.setTransFileFinish(true);
                        }

                    } catch (Exception e) {
                        LogUtils.e(TAG, "e =  " + e);
                    }

                    LogUtils.e(TAG, "IoStreamThreadWork excel finsh2 = " + list.size());
                } else {
                    PlayEntry mPlayEntry = (PlayEntry) mDBManager.queryById(mMinaFileParam.mMediaId, mDBManager.getPlayEntryDao(DBManager.READ_ONLY));
                    if (mPlayEntry != null) {
                        mPlayEntry.setFileParentPath(mMinaFileParam.mToSaveFilePath);
                        File receiveFile = new File(mMinaFileParam.mToSaveFilePath);
                        String HWmd5sum = mMinaFileParam.mFileMD5;
                        String sound = MD5Util.getFileMD5String(receiveFile);
                        if (!sound.equals(HWmd5sum)) {
                            LogUtils.e(TAG, "mPlayEntry session0 =  " + mMinaFileParam.mStringSession.getCurrentWriteRequest());
                            SocketJson socketJson = new SocketJson();
                            socketJson.setIp(getMyIp());
                            socketJson.setTypeName(Constants.MD_Transfer_Fail);
                            socketJson.setType(Constants.MD_Transfer_Fail_int);
                            socketJson.setPlayEntry(mPlayEntry);
                            mMinaFileParam.mStringSession.write(gson.toJson(socketJson));
                            mMinaFileParam.setTransFileFinish(true);
                            return;
                        }
                        mDBManager.update(mPlayEntry, mDBManager.getPlayEntryDao(DBManager.WRITE_ONLY));
                        try {
                            if (mMinaFileParam.mStringSession == null) {
                                LogUtils.e(TAG, "mPlayEntry session 1=  " + mMinaFileParam.mStringSession);
                            } else {
                                LogUtils.e(TAG, " mPlayEntry session2 =  " + mMinaFileParam.mStringSession);
                                SocketJson socketJson = new SocketJson();
                                socketJson.setIp(getMyIp());
                                socketJson.setTypeName(Constants.MD_Transfer_Finish_String);
                                socketJson.setType(Constants.MD_Transfer_Finish_int);
                                mMinaFileParam.mStringSession.write(gson.toJson(socketJson));
                                mMinaFileParam.setTransFileFinish(true);
                           }

                        } catch (Exception e) {
                            LogUtils.e(TAG, "e =  " + e);
                        }

                    }

                    LogUtils.e(TAG, "IoStreamThreadWork media file save finish mMediaId = " + mMinaFileParam.mStringSession);
                    LogUtils.e(TAG, "IoStreamThreadWork media file save finish mPlayEntry = " + mPlayEntry);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
