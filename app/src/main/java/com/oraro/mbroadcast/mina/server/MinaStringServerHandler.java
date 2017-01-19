package com.oraro.mbroadcast.mina.server;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.RemoteException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.dao.PlayEntryDao;
import com.oraro.mbroadcast.logicService.GenerateService;
import com.oraro.mbroadcast.model.MinaFileParam;
import com.oraro.mbroadcast.model.PlayEntry;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.model.SocketJson;
import com.oraro.mbroadcast.mp.PlayAudio;
import com.oraro.mbroadcast.proxy.TTSProXy;
import com.oraro.mbroadcast.service.OnRefreshUIListener;
import com.oraro.mbroadcast.signature.GetSign;
import com.oraro.mbroadcast.tts.TTSInterface;
import com.oraro.mbroadcast.utils.LogUtils;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

public class MinaStringServerHandler extends IoHandlerAdapter {
    private final String TAG = MinaStringServerHandler.class.getSimpleName();

    private MinaFileParam mCurrentMinaFileParam = null;

    // 从端口接受消息，会响应此方法来对消息进行处理
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        super.messageReceived(session, message);
        LogUtils.e(TAG, message.toString());
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        DBManager dbManager = DBManager.getInstance(MBroadcastApplication.getMyContext());
        TTSInterface tts = TTSProXy.getInstance(MBroadcastApplication.getMyContext(), MBroadcastApplication.getMyPackageName());
        final SocketJson socketJson = gson.fromJson(message.toString(), SocketJson.class);
        String autoGraph = GetSign.invoked(MBroadcastApplication.getMyContext(), MBroadcastApplication.getMyPackageName(), socketJson.getTypeName()).toString();
        PlayAudio playAudio = PlayAudio.getInstance();
        if (autoGraph.equals(socketJson.getAutograph())) {
            LogUtils.e(TAG, "验证通过");
            switch (socketJson.getType()) {
                case Constants.TTS_PLAY:
                    LogUtils.e(TAG, "TTS_PLAY::socketJson.getPlayEntry() = " + socketJson.getPlayEntry());
                    if (null == socketJson.getPlayEntry()) {
                        SocketJson mediaSocketJson = new SocketJson();
                        mediaSocketJson.setType(Constants.Audio_NO_DATA);
                        mediaSocketJson.setTypeName(Constants.Audio_NO_DATA_String);
                        mediaSocketJson.setIp(getMyIp());
                        session.write(gson.toJson(socketJson));
                        return;
                    }else if(null == socketJson.getPlayEntry().getId()){
                        if (playAudio.isPlaying()) {
                            playAudio.releaseAudio();
                        }
                        //执行温馨提示播报逻辑
                        tts.TTSStartPlay(socketJson.getTypeName(), null, null, null, null, null, null, null);
                    }else{
                        MBroadcastApplication.setPlayID(socketJson.getPlayEntry().getId());
                        if (MBroadcastApplication.getIMyAidlInterface() != null) {
                            MBroadcastApplication.getIMyAidlInterface().startTTSPlay(
                                    socketJson.getPlayEntry().getId(),
                                    socketJson.getPlayEntry().getTextDesc(),
                                    1,
                                    new OnRefreshUIListener.Stub() {
                                        @Override
                                        public void completed(long id, String error) throws RemoteException {
                                            MBroadcastApplication.setPlayID((long) -1);
                                        }

                                        @Override
                                        public void frushPlaying(long id) throws RemoteException {
                                            MBroadcastApplication.setPlayID(socketJson.getPlayEntry().getId());
                                        }
                                    });
                        }
                    }

                    session.write("sucess");
                    break;
                case Constants.MD_PLAY:
                    //执行Media逻辑
                    LogUtils.e(TAG, "MD_PLAY::socketJson.getPlayEntry() = " + socketJson.getPlayEntry());
                    final PlayEntry playEntry = (PlayEntry) dbManager.queryById(socketJson.getPlayEntry().getId(), dbManager.getPlayEntryDao(DBManager.READ_ONLY));
                    if (null == playEntry) {
                        SocketJson mediaSocketJson = new SocketJson();
                        mediaSocketJson.setType(Constants.Audio_NO_DATA);
                        mediaSocketJson.setTypeName(Constants.Audio_NO_DATA_String);
                        mediaSocketJson.setIp(getMyIp());
                        session.write(gson.toJson(socketJson));
                        return;
                    }
                    if (MBroadcastApplication.getIMyAidlInterface() != null) {
                        MBroadcastApplication.getIMyAidlInterface().startMediaPlay(
                                playEntry.getId(),
                                playEntry.getFileParentPath(),
                                1,
                                new OnRefreshUIListener.Stub() {
                                    @Override
                                    public void completed(long id, String error) throws RemoteException {
                                        MBroadcastApplication.setPlayID((long) -1);
                                    }

                                    @Override
                                    public void frushPlaying(long id) throws RemoteException {
                                        MBroadcastApplication.setPlayID(playEntry.getId());
                                    }
                                });
                    }
                    session.write("sucess");
                    break;
                case Constants.MD_FILE_UPDATE:
                    LogUtils.e(TAG, "导入Media文件的操作！");
                    LogUtils.e(TAG, message.toString());
                    LogUtils.e(TAG, "导入Media文件的操作！ session =  " + session);
                    //记录平板端需要传输的Excel文件路径、文件名称,在Excel传输失败重新传输时要用到
                    MinaFileParam mMinaMediaFileParam = new MinaFileParam(
                            socketJson.getPlayEntry().getId(),
                            true,
                            socketJson.getMd5sum(),
                            Environment.getExternalStorageDirectory().toString() + File.separator + Constants.SELECTED_MEDIA_FILE_DIRECTORY + File.separator + System.currentTimeMillis() + "_" + socketJson.getPlayEntry().getFileName(),
                            socketJson.getPlayEntry().getFileParentPath(),
                            socketJson.getPlayEntry().getFileName(),
                            session,
                            null);
                    mCurrentMinaFileParam = mMinaMediaFileParam;

                    MinaFileServerHandler.getInstance().setMinaFileParam(getIpFromSession(session),mMinaMediaFileParam);
                    //记录文件传输时需要的参数
                    MBroadcastApplication.mMinaFileParams.put(getIpFromSession(session),mMinaMediaFileParam);
                    //创建准备接收Media文件的协议结构
                    Gson mStartRecvMediaFileGson =  new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

                    PlayEntry mStartRecvMediaFileEntry = new PlayEntry();
                    mStartRecvMediaFileEntry.setFileParentPath(socketJson.getPlayEntry().getFileParentPath());
                    mStartRecvMediaFileEntry.setTextDesc("MINA_START_RECV_MEDIA_FILE");

                    SocketJson mStartRecvMediaFileJson = new SocketJson();
                    mStartRecvMediaFileJson.setType(Constants.MINA_START_RECV_MEDIA_FILE);
                    mStartRecvMediaFileJson.setPlayEntry(mStartRecvMediaFileEntry);
                    mStartRecvMediaFileJson.setIp(socketJson.getIp());

                    //接收Media文件的服务已启动,通知平板发送getPlayEntry().getFilePath()对应路径的文件，等待发送Media文件
                    session.write(mStartRecvMediaFileGson.toJson(mStartRecvMediaFileJson));
                    break;
                case Constants.A_DATA_UPDATE:
                    LogUtils.e(TAG, message.toString());
                    LogUtils.e(TAG, "更新播放实体的操作！");
                    if (null == socketJson.getPlayEntry()) {
                        SocketJson mediaSocketJson = new SocketJson();
                        mediaSocketJson.setType(Constants.Audio_NO_DATA);
                        mediaSocketJson.setTypeName(Constants.Audio_NO_DATA_String);
                        mediaSocketJson.setIp(getMyIp());
                        session.write(gson.toJson(socketJson));
                        return;
                    }
                    dbManager.update(socketJson.getPlayEntry(), dbManager.getPlayEntryDao(DBManager.WRITE_ONLY));
                    session.write("sucess");
                    break;
                case Constants.A_DATA_ADD:
                    LogUtils.e(TAG, message.toString());
                    LogUtils.e(TAG, "新增播放实体的操作！");
                    if (null == socketJson.getPlayEntry()) {
                        SocketJson mediaSocketJson = new SocketJson();
                        mediaSocketJson.setType(Constants.Audio_NO_DATA);
                        mediaSocketJson.setTypeName(Constants.Audio_NO_DATA_String);
                        mediaSocketJson.setIp(getMyIp());
                        session.write(gson.toJson(socketJson));
                        return;
                    }
                    dbManager.insert(socketJson.getPlayEntry(), dbManager.getPlayEntryDao(DBManager.WRITE_ONLY));

                    Gson mDataUpdateSuccessGson =  new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();


                    PlayEntry mDataUpdateSuccessEntry = new PlayEntry();
                    mDataUpdateSuccessEntry.setId(socketJson.getPlayEntry().getId());
                    mDataUpdateSuccessEntry.setFileName(socketJson.getPlayEntry().getFileName());
                    mDataUpdateSuccessEntry.setFileParentPath(socketJson.getPlayEntry().getFileParentPath());
                    mDataUpdateSuccessEntry.setTextDesc("A_DATA_UPDATE_SUCCESS");

                    SocketJson mDataUpdateSuccessJson = new SocketJson();
                    mDataUpdateSuccessJson.setType(Constants.A_DATA_UPDATE_SUCCESS);
                    mDataUpdateSuccessJson.setIp(socketJson.getIp());
                    mDataUpdateSuccessJson.setPlayEntry(mDataUpdateSuccessEntry);

                    session.write(mDataUpdateSuccessGson.toJson(mDataUpdateSuccessJson));
                    break;
                case Constants.A_DATA_DELETE:
                    LogUtils.e(TAG, message.toString());
                    LogUtils.e(TAG, "A_DATA_DELETE::socketJson.getPlayEntry() = " + socketJson.getPlayEntry());
                    LogUtils.e(TAG, "删除播放播放实体的操作！");
                    if (null == socketJson.getPlayEntry()) {
                        SocketJson mediaSocketJson = new SocketJson();
                        mediaSocketJson.setType(Constants.Audio_NO_DATA);
                        mediaSocketJson.setTypeName(Constants.Audio_NO_DATA_String);
                        mediaSocketJson.setIp(getMyIp());
                        session.write(gson.toJson(socketJson));
                        return;
                    }

                    Long playid = socketJson.getPlayEntry().getId();
                    DBManager.getInstance(MBroadcastApplication.getMyContext()).delete(socketJson.getPlayEntry(), DBManager.getInstance(MBroadcastApplication.getMyContext()).getPlayEntryDao(DBManager.WRITE_ONLY));
                    LogUtils.e(TAG, "A_DATA_DELETE::MBroadcastApplication.getIMyAidlInterface = " + MBroadcastApplication.getIMyAidlInterface());
                    if (MBroadcastApplication.getIMyAidlInterface() != null) {
                        try {
                            LogUtils.e(TAG, "A_DATA_DELETE::playid = " + playid);
                            LogUtils.e(TAG, "A_DATA_DELETE::MBroadcastApplication.getPlayID() = " + MBroadcastApplication.getPlayID());
                            if (playid.equals(MBroadcastApplication.getPlayID())) {
                                MBroadcastApplication.getIMyAidlInterface().stopPlay();
                            }
                            MBroadcastApplication.getIMyAidlInterface().deleteCatch(socketJson.getPlayEntry().getId());
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                    session.write("sucess");
                    break;
                case Constants.Import_Excel_File:
                    LogUtils.e(TAG, "导入excel文件的操作！");
                    LogUtils.e(TAG, message.toString());
                    LogUtils.e(TAG, "启动导入文件的service");

                    //由于excel文件名格式要求:日期_始发站_名称,接收文件重命名时需要保留该名称
                    String mTempFileName = socketJson.getPlayEntry().getFileName();
                    mTempFileName = mTempFileName.substring(0, mTempFileName.lastIndexOf("."));

                    //记录平板端需要传输的Excel文件路径、文件名称,在Excel传输失败重新传输时要用到
                    MinaFileParam mMinaExcelFileParam = new MinaFileParam(
                            MinaFileParam.INVALID_MEDIA_ID,
                            true,
                            socketJson.getMd5sum(),
                            Environment.getExternalStorageDirectory().toString() + File.separator + Constants.SELECTED_EXCEL_FILE_DIRECTORY + File.separator + mTempFileName + "_" + System.currentTimeMillis() + ".xls",
                            socketJson.getPlayEntry().getFileParentPath(),
                            socketJson.getPlayEntry().getFileName(),
                            session,
                            null);
                    mCurrentMinaFileParam = mMinaExcelFileParam;

                    MinaFileServerHandler.getInstance().setMinaFileParam(getIpFromSession(session),mMinaExcelFileParam);
                    //记录文件传输时需要的参数
                    MBroadcastApplication.mMinaFileParams.put(getIpFromSession(session),mMinaExcelFileParam);
                    //创建准备接收EXCEL文件的协议结构
                    Gson mStartRecvExcelFileGson =  new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

                    PlayEntry mStartRecvExcelFileEntry = new PlayEntry();
                    mStartRecvExcelFileEntry.setFileParentPath(socketJson.getPlayEntry().getFileParentPath());
                    mStartRecvExcelFileEntry.setTextDesc("MINA_START_RECV_EXCEL_FILE");

                    SocketJson mStartRecvExcelFileJson = new SocketJson();
                    mStartRecvExcelFileJson.setType(Constants.MINA_START_RECV_EXCEL_FILE);
                    mStartRecvExcelFileJson.setIp(socketJson.getIp());
                    mStartRecvExcelFileJson.setPlayEntry(mStartRecvExcelFileEntry);

                    //接收EXCEL文件的服务已启动,通知平板发送getPlayEntry().getFilePath()对应路径的文件，等待发送EXCEL文件
                    session.write(mStartRecvExcelFileGson.toJson(mStartRecvExcelFileJson));
                    break;
                case Constants.MINA_TEST:
                    LogUtils.e(TAG, message.toString());
                    LogUtils.e(TAG, "建立连接的测试的消息！");
                    LogUtils.e(TAG, "准备关闭UDP广播！");
//                    UDPSendThread.newInstance().setInterrupted(true);
                    //执行TTS逻辑
                    tts.TTSStartPlay(socketJson.getTypeName(), null, null, null, null, null, null, null);
                    //  String autoGraph = GetSign.invoked(MBroadcastApplication.getMyContext(), MBroadcastApplication.getMyPackageName(), socketJson.getTypeName()).toString();
                    session.write(Constants.MINA_Connect_Successfully);
                    break;
                case Constants.MINA_TEST_CONNECT_int:
                     LogUtils.e(TAG, message.toString());
                    LogUtils.e(TAG, "测试连接是否还在类型！");
                    session.write(Constants.MINA_TEST_CONNECT_String);
                    break;
                case Constants.A_FLIGHT_DELETE:
                    LogUtils.e(TAG, message.toString());
                    LogUtils.e(TAG, "删除航班的操作");
                    PlayEntryDao dao = dbManager.getPlayEntryDao(DBManager.READ_ONLY);
                    List<PlayEntry> list = dbManager.queryBySQL(dao, " where  PLAY_ENTRY_ID = " + socketJson.getFlightInfoTemp().getId());
                    if (list.size() < 1) {
                        SocketJson mediaSocketJson = new SocketJson();
                        mediaSocketJson.setType(Constants.Audio_NO_DATA);
                        mediaSocketJson.setTypeName(Constants.Audio_NO_DATA_String);
                        mediaSocketJson.setIp(getMyIp());
                        session.write(gson.toJson(socketJson));
                        return;
                    }
                    PlayEntryDao dao2 = dbManager.getPlayEntryDao(DBManager.WRITE_ONLY);
                    dbManager.deleteList(list, dao2);
                    session.write("sucess");
                    break;
                case Constants.A_FLIGHT_UPDATE:
                    LogUtils.e(TAG, "更新航班的操作");
                    PlayEntryDao playEntryDao = dbManager.getPlayEntryDao(DBManager.READ_ONLY);
                    QueryBuilder qb = playEntryDao.queryBuilder();
                    List<PlayEntry> playEntries = qb.where(PlayEntryDao.Properties.PlayEntryId.eq(socketJson.getFlightInfoTemp()), PlayEntryDao.Properties.PlayEntryId.eq(socketJson.getFlightInfoTemp().getId())).list();
                    LogUtils.e(TAG, "list  size = " + playEntries.size());
                    if (playEntries.size() < 1) {
                        SocketJson mediaSocketJson = new SocketJson();
                        mediaSocketJson.setType(Constants.Audio_NO_DATA);
                        mediaSocketJson.setTypeName(Constants.Audio_NO_DATA_String);
                        mediaSocketJson.setIp(getMyIp());
                        session.write(gson.toJson(socketJson));
                        return;
                    }
                    PlayEntryDao playEntryDao1 = dbManager.getPlayEntryDao(DBManager.WRITE_ONLY);
                    dbManager.deleteList(playEntries, playEntryDao1);
                    GenerateService service = new GenerateService();
                    service.generatePlay(socketJson.getFlightInfoTemp());
                    session.write("sucess");
                    break;
            }
        } else {
            EventBus.getDefault().postSticky(new SimpleEvent(Constants.CHECK_SIGN_FAIL_INT));
            LogUtils.e(TAG, "签名验证失败");
            session.write(Constants.CHECK_SIGN_FAIL);
        }
        LogUtils.e(TAG, autoGraph);
        LogUtils.e(TAG, socketJson.getAutograph());
        LogUtils.e(TAG, socketJson.getPlayEntry().toString());

    }

    private String getIpFromSession(IoSession session) {
        SocketAddress sd = session.getRemoteAddress();
        InetSocketAddress isa = null;
        if(sd instanceof InetSocketAddress){
            isa = (InetSocketAddress) sd;
        }
        String ip = "";
        if(null == isa){
            ip = session.getRemoteAddress().toString();
            if(ip.contains(":")){
                ip = ip.substring(0,ip.lastIndexOf(":"));
            }
        }else{
            ip = isa.getAddress().getHostAddress();
        }
        return ip;
    }

    // 向客服端发送消息后会调用此方法
    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
        LogUtils.e(TAG, "服务器发送消息成功...");
//        LogUtils.e(TAG, "IoStreamThreadWork messageSent ---> " + (mCurrentMinaFileParam != null ? mCurrentMinaFileParam.getStringSession()+"--->"+mCurrentMinaFileParam.isTransFileFinish() : null) +"--->"+session);
//        if (mCurrentMinaFileParam != null && mCurrentMinaFileParam.getStringSession() == session) {
//            if(mCurrentMinaFileParam.isTransFileFinish()){
//                mCurrentMinaFileParam.mStringSession.close(true);
//            }
//        }else{
//            session.close(true);//加上这句话实现短连接的效果，向客户端成功发送数据后断开连接
//        }

    }

    // 关闭与客户端的连接时会调用此方法
    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
        LogUtils.e(TAG, "服务器与客户端断开连接...");
    }

    // 服务器与客户端创建连接
    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
        LogUtils.e(TAG, "服务器与客户端创建连接...");
    }

    // 服务器与客户端连接打开
    @Override
    public void sessionOpened(IoSession session) throws Exception {
        LogUtils.e(TAG, "服务器与客户端连接打开..." + session);
        super.sessionOpened(session);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        super.sessionIdle(session, status);
        LogUtils.e(TAG, "服务器进入空闲状态..." + session);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        super.exceptionCaught(session, cause);
        LogUtils.e(TAG, "服务器发送异常...");
        LogUtils.e(TAG, cause.toString());
        LogUtils.disposeThrowable(TAG, cause);
        session.close(true);
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
}
