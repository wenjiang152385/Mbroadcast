package com.oraro.mbroadcast.mina.client;

import android.os.Handler;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.model.FlightInfoTemp;
import com.oraro.mbroadcast.model.SocketJson;
import com.oraro.mbroadcast.signature.GetSign;
import com.oraro.mbroadcast.utils.ChineseFormatUtil;
import com.oraro.mbroadcast.utils.LogUtils;
import com.oraro.mbroadcast.vo.PlayVO;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MinaAutoStringClientThread extends Thread {
    private static final String TAG = MinaAutoStringClientThread.class.getSimpleName();
    private IoSession session = null;
    private static HashMap<String,IoConnector> connectorHashMap = new HashMap();
    private String ip;
    private PlayVO playVO = null;
    private static MinaAutoStringClientThread mInstance;
    private static ThreadPoolExecutor threadPool;
    private static   MinaStringClientHandler.MinaConnectSuccess mMinaConnectSuccess;
    private FlightInfoTemp flightInfoTemp;
    private Handler handler;
    private  String md5sum;

    /**
     * 获取单例引用
     *
     * @return
     */
    public static ThreadPoolExecutor getThreadPoolExecutor() {
        if (threadPool == null) {
            synchronized (ThreadPoolExecutor.class) {
                if (threadPool == null) {
                    threadPool = new ThreadPoolExecutor(1, 4, 3, TimeUnit.SECONDS,
                            //缓冲队列为4
                            new ArrayBlockingQueue<Runnable>(4),
                            //抛弃旧的任务
                            new ThreadPoolExecutor.DiscardOldestPolicy());
                }
            }
        }
        return threadPool;
    }

    public String getMd5sum() {
        return md5sum;
    }

    public void setMd5sum(String md5sum) {
        this.md5sum = md5sum;
    }

    public void setCallback(MinaStringClientHandler.MinaConnectSuccess minaConnectSuccess) {
        mMinaConnectSuccess = minaConnectSuccess;
    }

    public FlightInfoTemp getFlightInfoTemp() {
        return flightInfoTemp;
    }

    public void setFlightInfoTemp(FlightInfoTemp flightInfoTemp) {
        this.flightInfoTemp = flightInfoTemp;
    }

    /**
     * 获取单例引用
     *
     * @return
     */
    public static IoConnector getIoConnector(String IP) {
        if (connectorHashMap.get(IP)== null) {
            synchronized (MinaAutoStringClientThread.class) {
                if (connectorHashMap.get(IP) == null) {
                    IoConnector ioConnector = new NioSocketConnector();
                    ioConnector.getFilterChain().addLast("codec",
                            new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"),
                                    LineDelimiter.WINDOWS.getValue(), LineDelimiter.WINDOWS.getValue())));
                    // 设置链接超时时间
                    ioConnector.setConnectTimeoutMillis(10000);

                    LogUtils.e(TAG, "ip = " + IP);
                    // 添加过滤器
                    // connector.getFilterChain().addLast("codec", new
                    // ProtocolCodecFilter(new CharsetCodecFactory()));
//		//设置默认连接远程服务器的IP地址和端口
                    ioConnector.setDefaultRemoteAddress(new InetSocketAddress(IP, Constants.WEB_AUTO_MATCH_PORT));
                    connectorHashMap.put(IP,ioConnector);
                }
            }
        }
        return connectorHashMap.get(IP);
    }
    /**
     * 获取单例引用
     *
     * @return
     */
    public static MinaAutoStringClientThread getInstance() {
        if (mInstance == null) {
            synchronized (MinaAutoStringClientThread.class) {
                if (mInstance == null) {
                    mInstance = new MinaAutoStringClientThread();
                }
            }
        }
        return mInstance;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public IoSession getSession() {
        return session;
    }

    public void setSession(IoSession session) {
        this.session = session;
    }

    public PlayVO getPlayVO() {
        return playVO;
    }

    public void setPlayVO(PlayVO playVO) {
        this.playVO = playVO;
    }

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public MinaAutoStringClientThread() {
    }


    @Override
    public void run() {
        super.run();
        try {
            // TODO Auto-generated method stub]
            LogUtils.e(TAG, "自动播报客户端链接开始...");
            IoConnector ioConnector=  getIoConnector(ip);
            if(ioConnector.isActive()){
                //EventBus.getDefault().postSticky(new SimpleEvent(Constants.File_Trans_Deuplicate));
                return;
            }

            //由于是static，所以每次都要替换为最新的minaStringClientHandler和mMinaConnectSuccess
            MinaStringClientHandler minaStringClientHandler = new MinaStringClientHandler();
            minaStringClientHandler.setmIp(ip);
            ioConnector.setHandler(minaStringClientHandler);
            minaStringClientHandler.setMinaConnectSuccess(mMinaConnectSuccess);

            //开始连接
            ConnectFuture future = ioConnector.connect();

            future.awaitUninterruptibly();// 等待连接创建完成
            session = future.getSession();// 获得session
            //判断是否连接服务器成功
            if (session != null && session.isConnected()) {
                SocketJson socketJson = new SocketJson();
                socketJson.setType(type);
                if (null != playVO) {
                    String s = playVO.getEntity().getTextDesc();
                    if(!TextUtils.isEmpty(playVO.getEntity().getFileParentPath())){
                        //表示是文件传输实体，可能播报文字内容是空
                        if(TextUtils.isEmpty(s)){
                            socketJson.setTypeName("file trans " + System.currentTimeMillis());
                        }
                    }else {
                        socketJson.setTypeName(ChineseFormatUtil.replace(s));
                    }
                    socketJson.setAutograph(GetSign.invoked(MBroadcastApplication.getMyContext(), MBroadcastApplication.getMyPackageName(), socketJson.getTypeName()).toString());
                    socketJson.setPlayEntry(playVO.getEntity());
                    socketJson.setIp(ip);
                    socketJson.setMd5sum(md5sum);
                    socketJson.setFlightInfoTemp(flightInfoTemp);
                }

                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                session.write(gson.toJson(socketJson));
                LogUtils.e(TAG, "" + "客户端链接结束");
            } else {
                LogUtils.e(TAG, "" + "写数据失败");
                if (null != mMinaConnectSuccess) {
                    mMinaConnectSuccess.connectFailCallback(ip);
            }
            }
            LogUtils.e(TAG, "" + 118);

        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, e.toString());
            LogUtils.e(TAG, "" + "客户端链接异常...");
            if (null != mMinaConnectSuccess) {
                mMinaConnectSuccess.connectFailCallback(ip);
            }
        }
        LogUtils.e(TAG, "" + 118);

    }

}