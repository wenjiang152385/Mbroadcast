package com.oraro.mbroadcast.mina.client;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.broadcasts.OlympicsReceiver;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.model.SocketJson;
import com.oraro.mbroadcast.utils.LogUtils;
import com.oraro.mbroadcast.utils.MD5Util;
import com.oraro.mbroadcast.utils.SPUtils;
import com.oraro.mbroadcast.vo.PlayVO;

import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

public class MinaStringClientHandler extends IoHandlerAdapter {
    private final String TAG = this.getClass().getSimpleName();
    private MinaConnectSuccess minaConnectSuccess;
    private String mIp;

    public String getmIp() {
        return mIp;
    }

    public void setmIp(String mIp) {
        this.mIp = mIp;
    }

    public MinaConnectSuccess getMinaConnectSuccess() {
        return minaConnectSuccess;
    }

    public void setMinaConnectSuccess(MinaConnectSuccess minaConnectSuccess) {
        this.minaConnectSuccess = minaConnectSuccess;
    }


    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        super.exceptionCaught(session, cause);
        LogUtils.e(TAG, "客户端发生异常");
        LogUtils.e(TAG, cause.toString());
        minaConnectSuccess.connectFailCallback(mIp);
        LogUtils.e(TAG, "exceptionCaught");
        LogUtils.disposeThrowable(TAG,cause);
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
        LogUtils.e(TAG, "客户端创建链接");
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        super.messageReceived(session, message);
        LogUtils.e(TAG, "客户端接收到的信息为:" + message.toString());
        LogUtils.e(TAG, "音响要开始播放广播");
        LogUtils.e(TAG, "session " + session.getConfig());
        if (message.toString().equals(Constants.MINA_Connect_Successfully)) {
            minaConnectSuccess.connectSuccessCallback(mIp);
            session.closeNow();
            return;
        }
        if (message.toString().equals("sucess")) {
            LogUtils.e(TAG, "其他正常的消息发送，直接返回");
            session.closeNow();
            return;
        }
        if (message.toString().equals(Constants.MINA_TEST_CONNECT_String)) {
            minaConnectSuccess.connectSuccessCallback(mIp);
            session.closeNow();
            return;
        }
        if (message.toString().equals(Constants.CHECK_SIGN_FAIL)) {
            minaConnectSuccess.connectFailCallback(mIp);
            session.closeNow();
            return;
        }

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        SocketJson socketJson = gson.fromJson(message.toString(), SocketJson.class);
        LogUtils.e(TAG, "socketJson.getType() = " + socketJson.getType());
        switch (socketJson.getType()) {
            case Constants.MINA_START_RECV_EXCEL_FILE:
                //开始发送EXCEL文件
                MyThread mSendExcelFileThread = new MyThread();
                String mExcelFilePath = socketJson.getPlayEntry().getFileParentPath();
                LogUtils.e(TAG, "MINA_START_RECV_EXCEL_FILE messageReceived mExcelFilePath = " + session + "  " + mExcelFilePath);
                mSendExcelFileThread.setSendFile(mExcelFilePath);
                mSendExcelFileThread.setIp(socketJson.getIp());
                MinaStringClientThread.getThreadPoolExecutor().execute(mSendExcelFileThread);
                LogUtils.e(TAG, "send EXCEL file message ip = " + socketJson.getIp());
                LogUtils.e(TAG, "send EXCEL file message id = " + socketJson.getPlayEntry().getId());
                LogUtils.e(TAG, "send EXCEL file message path = " + socketJson.getPlayEntry().getFileParentPath());
                break;
            case Constants.MINA_START_RECV_MEDIA_FILE:
                //开始发送Media文件
                MyThread mSendMediaFileThread = new MyThread();
                String mMediaFilePath = socketJson.getPlayEntry().getFileParentPath();
                mSendMediaFileThread.setIp(socketJson.getIp());
                LogUtils.e(TAG,socketJson.getPlayEntry().toString());
                LogUtils.e(TAG, "MINA_START_RECV_MEDIA_FILE messageReceived mMediaFilePath = " + socketJson.getIp() + mMediaFilePath);
                mSendMediaFileThread.setSendFile(mMediaFilePath);
                MinaStringClientThread.getThreadPoolExecutor().execute(mSendMediaFileThread);

                break;
            case Constants.EXCEL_Transfer_Fail_int:
                Intent intent1 = new Intent(Constants.EXCEL_Transfer_Fail);
                intent1.putExtra("ip", socketJson.getIp());
                intent1.putExtra(OlympicsReceiver.EXTRA_EXCEL_FILE_PATH,socketJson.getPlayEntry().getFileParentPath());
                intent1.putExtra(OlympicsReceiver.EXTRA_EXCEL_FILE_NAME,socketJson.getPlayEntry().getFileName());
                MBroadcastApplication.getMyContext().sendBroadcast(intent1);
                session.closeNow();
                LogUtils.e(TAG, "Excel文件传输失败的通知！");
                break;
            case Constants.EXCEL_Transfer_Finish_int:
                Intent intent2 = new Intent(Constants.EXCEL_Transfer_Finish_String);
                intent2.putExtra("ip", socketJson.getIp());
                MBroadcastApplication.getMyContext().sendBroadcast(intent2);
                session.closeNow();
                LogUtils.e(TAG, "Excel文件传输完成的通知！");
                break;
            case Constants.MD_Transfer_Fail_int:
                Intent intent3 = new Intent(Constants.MD_Transfer_Fail);
                intent3.putExtra("ip", socketJson.getIp());
                MBroadcastApplication.getMyContext().sendBroadcast(intent3);
                session.closeNow();
                LogUtils.e(TAG, "音频文件传输失败的通知！");
                break;
            case Constants.MD_Transfer_Finish_int:
                Intent intent4 = new Intent(Constants.MD_Transfer_Finish_String);
                intent4.putExtra("ip", socketJson.getIp());
                MBroadcastApplication.getMyContext().sendBroadcast(intent4);
                session.closeNow();
                LogUtils.e(TAG, "音频文件传输完成的通知！");
                break;
            case Constants.Audio_NO_DATA:
                Intent intent5 = new Intent(Constants.Audio_NO_DATA_String);
                intent5.putExtra("ip", socketJson.getIp());
                MBroadcastApplication.getMyContext().sendBroadcast(intent5);
                session.closeNow();
                LogUtils.e(TAG, "音响没有数据！");
                break;
            case Constants.File_Trans_Loading:
                SimpleEvent simpleEvent1=new SimpleEvent(Constants.File_Trans_Loading);
                simpleEvent1.setmDataLength(socketJson.getProgess());
                EventBus.getDefault().postSticky(simpleEvent1);
                break;
            case Constants.A_DATA_UPDATE_SUCCESS:
                LogUtils.e(TAG, "数据更新完成！");
                //如果有文件更新
                if(!TextUtils.isEmpty(socketJson.getPlayEntry().getFileParentPath())){
                    MinaFileClientThread minaFileClientThread = new MinaFileClientThread();
                    minaFileClientThread.setType(Constants.MD_FILE_UPDATE);
                    minaFileClientThread.setPlayVO(new PlayVO(socketJson.getPlayEntry()));
                    minaFileClientThread.setIp(socketJson.getIp());
                    String MDmd5sum = MD5Util.getFileMD5String(new File(socketJson.getPlayEntry().getFileParentPath()));
                    minaFileClientThread.setMd5sum(MDmd5sum);
                    MinaStringClientThread.getThreadPoolExecutor().execute(minaFileClientThread);
                }
                session.closeNow();
                break;
            default:
                break;
        }

    }

    public interface MinaConnectSuccess {
        void connectSuccessCallback(String ip);

        void connectFailCallback(String ip);
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        // TODO Auto-generated method stub
        super.messageSent(session, message);
        LogUtils.e(TAG, "messageSent()");
        LogUtils.e(TAG, "message = " + message.toString());

    }


}
