package com.oraro.mbroadcast.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.dao.FlightInfoDao;
import com.oraro.mbroadcast.dao.PlayEntryDao;
import com.oraro.mbroadcast.listener.IReceiveMsg;
import com.oraro.mbroadcast.logicService.DataService;
import com.oraro.mbroadcast.logicService.GenerateService;
import com.oraro.mbroadcast.mina.server.MinaFileServerHandler;
import com.oraro.mbroadcast.mina.server.MinaStringServerHandler;
import com.oraro.mbroadcast.model.DeviceEntity;
import com.oraro.mbroadcast.model.FlightInfo;
import com.oraro.mbroadcast.model.MinaFileParam;
import com.oraro.mbroadcast.model.PlayEntry;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.model.SocketJson;
import com.oraro.mbroadcast.udpthread.UDPSendThread;
import com.oraro.mbroadcast.utils.IPControl;
import com.oraro.mbroadcast.utils.LogUtils;
import com.oraro.mbroadcast.utils.MD5Util;
import com.oraro.mbroadcast.utils.ParseExcelUtils;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.greenrobot.eventbus.EventBus;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by Administrator on 2016/10/8 0008.
 */
public class SerService extends Service {
    private final String TAG = SerService.class.getSimpleName();
    private final static int CSPORT = 4436;
    private UDPSendThread mUDPSendThread;
    IoAcceptor acceptor = null;
    //接收自动播报服务
    IoAcceptor mAutoAcceptor = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e(TAG, "Service onCreate--->");
        LogUtils.e(TAG, "服务器启动成功... 端口号未：" + Constants.WEB_MATCH_PORT);
        if (null == mUDPSendThread) {
            mUDPSendThread = UDPSendThread.newInstance();
            IPControl ipControl = new IPControl();
            DeviceEntity deviceEntity = new DeviceEntity();
            deviceEntity.setIp(ipControl.getIpv4Address());
            deviceEntity.setMac(ipControl.getLocalMac(MBroadcastApplication.getMyContext()));
            deviceEntity.setNickname("speaker");
            deviceEntity.setIsRoot(false);
            mUDPSendThread.setInterrupted(false);
            mUDPSendThread.startUDPSendThread(CSPORT, deviceEntity.jsonToString(), ipControl.getUDPAddress(ipControl.getIpv4Address()), "server");
        }
        Toast.makeText(MBroadcastApplication.getMyContext(),"服务启动成功",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(MBroadcastApplication.getMyContext(), Service1.class);
        MBroadcastApplication.getMyContext().bindService(intent, conn, Context.BIND_AUTO_CREATE);
        try {
            // 创建一个增删改查接收服务
            if (null == acceptor) {
                acceptor = new NioSocketAcceptor();
                TextLineCodecFactory factory = new TextLineCodecFactory(Charset.forName("UTF-8"),
                        LineDelimiter.WINDOWS.getValue(), LineDelimiter.WINDOWS.getValue());
                factory.setDecoderMaxLineLength(Integer.MAX_VALUE);// 设定后服务器可以接收大数据
                factory.setEncoderMaxLineLength(Integer.MAX_VALUE);
                // 设置过滤器（使用mina提供的文本换行符编解码器）
                acceptor.getFilterChain().addLast("codec",
                        new ProtocolCodecFilter(factory));
                // 自定义的编解码器
                // acceptor.getFilterChain().addLast("codec", new
                // ProtocolCodecFilter(new CharsetCodecFactory()));
                // 设置读取数据的换从区大小
                acceptor.getSessionConfig().setReadBufferSize(10240);
                // 读写通道10秒内无操作进入空闲状态
                acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
                // 为接收器设置管理服务
                acceptor.setHandler(new MinaStringServerHandler());
                // 绑定端口
                acceptor.bind(new InetSocketAddress(Constants.WEB_MATCH_PORT));
                LogUtils.e(TAG, "服务器启动成功... 端口号未：" + Constants.WEB_MATCH_PORT);
                Toast.makeText(this, "音响接收服务启动成功", Toast.LENGTH_LONG).show();
            }

            // 创建一个接收自动播报服务
            if (null == mAutoAcceptor) {
                mAutoAcceptor = new NioSocketAcceptor();
                TextLineCodecFactory factory = new TextLineCodecFactory(Charset.forName("UTF-8"),
                        LineDelimiter.WINDOWS.getValue(), LineDelimiter.WINDOWS.getValue());
                factory.setDecoderMaxLineLength(Integer.MAX_VALUE);// 设定后服务器可以接收大数据
                factory.setEncoderMaxLineLength(Integer.MAX_VALUE);
                // 设置过滤器（使用mina提供的文本换行符编解码器）
                mAutoAcceptor.getFilterChain().addLast("codec",
                        new ProtocolCodecFilter(factory));
                // 自定义的编解码器
                // acceptor.getFilterChain().addLast("codec", new
                // ProtocolCodecFilter(new CharsetCodecFactory()));
                // 设置读取数据的换从区大小
                mAutoAcceptor.getSessionConfig().setReadBufferSize(10240);
                // 读写通道10秒内无操作进入空闲状态
                mAutoAcceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
                // 为接收器设置管理服务
                mAutoAcceptor.setHandler(new MinaStringServerHandler());
                // 绑定端口
                mAutoAcceptor.bind(new InetSocketAddress(Constants.WEB_AUTO_MATCH_PORT));
                LogUtils.e(TAG, "自动接收播报服务器启动成功... 端口号未：" + Constants.WEB_AUTO_MATCH_PORT);
            }

            MinaFileServerHandler.getInstance().createServerStream();

            //启动socket服务来等待接收需要传输的文件
            //startSocketRecvFileService();

        } catch (Exception e) {
            LogUtils.e(TAG, "服务器启动异常...");
            Toast.makeText(this, "音响接收服务启动失败" + e, Toast.LENGTH_LONG).show();
            acceptor.unbind();
            acceptor.dispose();

            Log.e(TAG, e + " ");
        }

    }

//    private void startSocketRecvFileService() throws IOException {
//        final ServerSocket server = new ServerSocket(Constants.FILE_TRANS_PORT);
//        Thread th = new Thread(new Runnable() {
//            public void run() {
//                while (true) {
//                    try {
//                        /*
//                         * 如果没有访问它会自动等待
//                         */
//                        Socket socket = server.accept();
//                        receiveFileTest(socket);
//                    } catch (Exception e) {
//                        LogUtils.e(TAG, "startSocketRecvFileService--->服务器异常...--->"+e);
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//        th.start(); //启动线程运行
//    }
//
//    //启动socket服务来等待接收需要传输的文件
//    public void receiveFileTest(Socket socket) {
//        Gson gson = new Gson();
//        MinaFileParam param = MBroadcastApplication.mMinaFileParams.get(socket.getInetAddress().getHostAddress());
//        FileOutputStream fos = null;
//        DataInputStream dis = null;
//        try {
//            byte[] inputByte = new byte[1024];
//            int length = 0;
//
//            if(null == param){
//                if(dis != null){
//                    try {
//                        dis.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                return;
//            }
//
//            File toSaveFile = new File(param.mToSaveFilePath);
//            if(toSaveFile.exists()){
//                toSaveFile.delete();
//            }
//            toSaveFile.createNewFile();
//
//            fos = new FileOutputStream(toSaveFile);
//            dis = new DataInputStream(socket.getInputStream());
//
//            LogUtils.e(TAG, "startSocketRecvFileService--->receiveFileTest::fos = " + fos);
//
//            if (null == fos) {
//                if(dis != null){
//                    try {
//                        dis.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                return;
//            }
//
//            LogUtils.e(TAG, "startSocketRecvFileService--->receiveFileTest 开始接收数据");
//            while ((length = dis.read(inputByte, 0, inputByte.length)) > 0) {
//                fos.write(inputByte, 0, length);
//                fos.flush();
//            }
//            LogUtils.e(TAG, "startSocketRecvFileService--->receiveFileTest 接收数据完成");
//        } catch (Exception e) {
//            LogUtils.disposeThrowable(TAG,e);
//            LogUtils.e(TAG, "startSocketRecvFileService--->receiveFileTest 服务器异常--->" + e);
//            e.printStackTrace();
//        } finally {
//            try {
//                if(fos != null){
//                    fos.close();
//                }
//                if(dis != null){
//                    dis.close();
//                }
//                if (null == param ||
//                        !param.mIsToSaveFile ||
//                        null == param.mStringSession ||
//                        null == param.mFileSession ||
//                        TextUtils.isEmpty(param.mToSaveFilePath)) {
//                    return;
//                }
//                String suffix = param.mToSaveFilePath.substring(param.mToSaveFilePath.lastIndexOf(".") + 1);
//                LogUtils.e(TAG, "startSocketRecvFileService--->to save excel file suffix = " + suffix + "this = " + this);
//                DBManager mDBManager = DBManager.getInstance(MBroadcastApplication.getMyContext());
//                if (suffix.equals("xls") || suffix.equals("XLS")) {
//                    //EXCEL文件解析
//                    File receiveFile = new File(param.mToSaveFilePath);
//                    String HWmd5sum = param.mFileMD5;
//                    String sound = MD5Util.getFileMD5String(receiveFile);
//                    if (!sound.equals(HWmd5sum)) {
//                        SocketJson socketJson = new SocketJson();
//                        socketJson.setIp(socket.getInetAddress().getHostAddress());
//                        socketJson.setType(Constants.EXCEL_Transfer_Fail_int);
//                        socketJson.setTypeName(Constants.EXCEL_Transfer_Fail);
//                        PlayEntry playEntry = new PlayEntry();
//                        playEntry.setFileParentPath(param.mOriginalFilePath);
//                        playEntry.setFileName(param.mOriginalFileName);
//                        playEntry.setTextDesc("Import_Excel_File");
//                        socketJson.setPlayEntry(playEntry);
//                        param.mStringSession.write(gson.toJson(socketJson));
//                        Constants.isFlieC = false;
//                        param.mStringSession.close(true);
//                        LogUtils.e(TAG, "startSocketRecvFileService--->md5 failed session =  " + param.mStringSession);
//                        return;
//                    }
//
//                    List<FlightInfo> list = new ArrayList<FlightInfo>();
//                    ParseExcelUtils.parse(param.mToSaveFilePath,list);
//                    FlightInfoDao dao = mDBManager.getFlightInfoDao(DBManager.WRITE_ONLY);
//                    mDBManager.insertList(list, dao);
//                    FlightInfoDao dao2 = mDBManager.getFlightInfoDao(DBManager.READ_ONLY);
////                    List<FlightInfo> list2 = mDBManager.queryAll(dao2);
//                    GenerateService s = new GenerateService();
//                    s.copeFlightToTemp(list);
//                    s.generatePlay();
//                    PlayEntryDao playEntryDao = mDBManager.getPlayEntryDao(DBManager.READ_ONLY);
//                    int i = mDBManager.queryAll(playEntryDao).size();
//                    LogUtils.e(TAG, "startSocketRecvFileService--->excel finsh = " + list.size());
//                    try {
//                        if (param.mStringSession == null) {
//                            LogUtils.e(TAG, "startSocketRecvFileService--->excel session1 =  " + param.mStringSession);
//                        } else {
//                            LogUtils.e(TAG, "startSocketRecvFileService--->to save excel session2 =  " + param.mStringSession);
//                            SocketJson socketJson = new SocketJson();
//                            socketJson.setIp(socket.getInetAddress().getHostAddress());
//                            socketJson.setTypeName(Constants.EXCEL_Transfer_Finish_String);
//                            socketJson.setType(Constants.EXCEL_Transfer_Finish_int);
//                            param.mStringSession.write(gson.toJson(socketJson));
//                            Constants.isFlieC = false;
//                            param.mStringSession.close(true);
//                        }
//
//                    } catch (Exception e) {
//                        LogUtils.e(TAG, "startSocketRecvFileService--->e =  " + e);
//                    }
//
//                    LogUtils.e(TAG, "startSocketRecvFileService--->excel finsh2 = " + list.size());
//                } else {
//                    PlayEntry mPlayEntry = (PlayEntry) mDBManager.queryById(param.mMediaId, mDBManager.getPlayEntryDao(DBManager.READ_ONLY));
//                    if (mPlayEntry != null) {
//                        mPlayEntry.setFileParentPath(param.mToSaveFilePath);
//                        File receiveFile = new File(param.mToSaveFilePath);
//                        String HWmd5sum = param.mFileMD5;
//                        String sound = MD5Util.getFileMD5String(receiveFile);
//                        if (!sound.equals(HWmd5sum)) {
//                            LogUtils.e(TAG, "startSocketRecvFileService--->mPlayEntry session0 =  " + param.mStringSession.getCurrentWriteRequest());
//                            SocketJson socketJson = new SocketJson();
//                            socketJson.setIp(socket.getInetAddress().getHostAddress());
//                            socketJson.setTypeName(Constants.MD_Transfer_Fail);
//                            socketJson.setType(Constants.MD_Transfer_Fail_int);
//                            socketJson.setPlayEntry(mPlayEntry);
//                            param.mStringSession.write(gson.toJson(socketJson));
//                            Constants.isFlieC = false;
//                            param.mStringSession.close(true);
//                            return;
//                        }
//                        mDBManager.update(mPlayEntry, mDBManager.getPlayEntryDao(DBManager.WRITE_ONLY));
//                        try {
//                            if (param.mStringSession == null) {
//                                LogUtils.e(TAG, "startSocketRecvFileService--->mPlayEntry session 1=  " + param.mStringSession);
//                            } else {
//                                LogUtils.e(TAG, "startSocketRecvFileService--->mPlayEntry session2 =  " + param.mStringSession);
//                                SocketJson socketJson = new SocketJson();
//                                socketJson.setIp(socket.getInetAddress().getHostAddress());
//                                socketJson.setTypeName(Constants.MD_Transfer_Finish_String);
//                                socketJson.setType(Constants.MD_Transfer_Finish_int);
//                                param.mStringSession.write(gson.toJson(socketJson));
//                                Constants.isFlieC = false;
//                                param.mStringSession.close(true);
//                            }
//
//                        } catch (Exception e) {
//                            LogUtils.e(TAG, "startSocketRecvFileService--->e =  " + e);
//                        }
//
//                    }
//
//                    LogUtils.e(TAG, "startSocketRecvFileService--->media file save finish mMediaId = " + param.mMediaId);
//                    LogUtils.e(TAG, "startSocketRecvFileService--->media file save finish mPlayEntry = " + mPlayEntry);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.e(TAG, "onStartCommand ");
        int tag = -1;
        if (null != intent) {
            tag = intent.getIntExtra("status", -1);
        }

        if (0 == tag) {
            if (null == mUDPSendThread) {
                mUDPSendThread = UDPSendThread.newInstance();
                IPControl ipControl = new IPControl();
                DeviceEntity deviceEntity = new DeviceEntity();
                deviceEntity.setIp(ipControl.getIpv4Address());
                deviceEntity.setMac(ipControl.getLocalMac(MBroadcastApplication.getMyContext()));
                deviceEntity.setNickname("speaker");
                deviceEntity.setIsRoot(false);
                mUDPSendThread.setInterrupted(false);
                mUDPSendThread.startUDPSendThread(CSPORT, deviceEntity.jsonToString(), ipControl.getUDPAddress(ipControl.getIpv4Address()), "server");
            }
        } else if (1 == tag) {
            if (null != mUDPSendThread) {
                mUDPSendThread.setInterrupted(true);
                mUDPSendThread = null;
            }
        } else if (2 == tag) {
            if (null != mUDPSendThread) {
                IPControl ipControl = new IPControl();
                DeviceEntity deviceEntity = new DeviceEntity();
                deviceEntity.setIp(ipControl.getIpv4Address());
                deviceEntity.setMac(ipControl.getLocalMac(MBroadcastApplication.getMyContext()));
                deviceEntity.setNickname("speaker");
                deviceEntity.setIsRoot(false);
              mUDPSendThread.setSendIp(ipControl.getUDPAddress(ipControl.getIpv4Address()), deviceEntity.jsonToString());
            }


        }

        return super.onStartCommand(intent, flags, startId);
    }

    private IMyAidlInterface iMyAidlInterface;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            iMyAidlInterface = IMyAidlInterface.Stub.asInterface(iBinder);
            MBroadcastApplication.setIMyAidlInterface(iMyAidlInterface);
//            DataService service=new DataService();
//            if(service.getAutoPlayStatus()){
//                service.setAutoPlayStatus();
//                try {
//                    iMyAidlInterface.autioPlay(service.getAutoPlayStatus());
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//            }
//            try {
//                iMyAidlInterface.stopMediaPlay();
//                iMyAidlInterface.stopTTSPlay();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }

    };
    @Override
    public void onDestroy() {
        LogUtils.e(TAG, "onDestroy");
        super.onDestroy();
        if(conn != null){
            MBroadcastApplication.getMyContext().unbindService(conn);
        }
    }
}
