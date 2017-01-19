package com.oraro.mbroadcast.mina.server;

import android.widget.Toast;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.model.MinaFileParam;
import com.oraro.mbroadcast.utils.LogUtils;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.handler.stream.StreamIoHandler;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author dongyu
 *         文件传输服务器
 */
public class MinaFileServerHandler extends StreamIoHandler {
    public static final int PORT = 8888;
    public static final String TAG = MinaFileServerHandler.class.getSimpleName();

    private static MinaFileServerHandler mMinaFileServerHandler = null;

    private Map<String,MinaFileParam> mMinaFileParams = new HashMap<>();

    public static MinaFileServerHandler getInstance() {
        if (null == mMinaFileServerHandler) {
            synchronized (MinaFileServerHandler.class) {
                if (null == mMinaFileServerHandler) {
                    mMinaFileServerHandler = new MinaFileServerHandler();
                }
            }
        }
        return mMinaFileServerHandler;
    }

    public void setMinaFileParam(String ip,MinaFileParam param){
        LogUtils.e(TAG, "setMinaFileParam::ip = " + ip);
        LogUtils.e(TAG, "setMinaFileParam::param = " + param);
        mMinaFileParams.put(ip,param);
    }

    public MinaFileParam getMinaFileParam(String ip){
        return mMinaFileParams.get(ip);
    }

    @Override
    public void sessionOpened(IoSession session) {
        LogUtils.e("MinaFileServerHandler", "客户端连接了:" + session.getRemoteAddress());
        super.sessionOpened(session);
    }

    protected void processStreamIo(IoSession session, InputStream in, OutputStream out) {
        //设定一个线程池
        //参数说明：最少数量3，最大数量6 空闲时间 3秒
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 6, 3, TimeUnit.SECONDS,
                //缓冲队列为3
                new ArrayBlockingQueue<Runnable>(3),
                //抛弃旧的任务
                new ThreadPoolExecutor.DiscardOldestPolicy());

        String mRemoteIP = getIpFromSession(session);
        LogUtils.e(TAG, "processStreamIo::mRemoteIP = " + mRemoteIP);

        MinaFileParam mMinaFileParam = getMinaFileParam(mRemoteIP);
        LogUtils.e(TAG, "processStreamIo::mMinaFileParam = " + mMinaFileParam);
        if(null == mMinaFileParam){
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return;
        }
        //将线程放入线程池 当连接很多时候可以通过线程池处理
        LogUtils.e(TAG, "processStreamIo::mMediaId = " + mMinaFileParam.mMediaId);
        LogUtils.e(TAG, "processStreamIo::mIsToSaveFile = " + mMinaFileParam.mIsToSaveFile);
        LogUtils.e(TAG, "processStreamIo::mToSaveFilePath = " + mMinaFileParam.mToSaveFilePath);
        //以当前时间作为要保存的文件名
        mMinaFileParam.setFileSession(session);
        IoStreamThreadWork mIoStreamThreadWork = new IoStreamThreadWork(in,mMinaFileParam);
        threadPool.execute(mIoStreamThreadWork);
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

    @Override
    public void messageReceived(IoSession session, Object buf) {
        super.messageReceived(session, buf);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
        LogUtils.e("MinaFileServerHandler", "文件服务器关闭！");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {
        super.sessionIdle(session, status);
        LogUtils.e("MinaFileServerHandler", "文件服务器进入空闲状态！");
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        super.exceptionCaught(session, cause);
        LogUtils.disposeThrowable(TAG,cause);
        session.closeNow();
    }

    public void createServerStream() {
        //建立一个无阻塞服务端socket 用nio
        NioSocketAcceptor acceptor = new NioSocketAcceptor();
        //创建接收过滤器 也就是你要传送对象的类型
        DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
        //===========过滤器创建好了就开始设定============
        //设定 对象传输工厂
        ObjectSerializationCodecFactory factory = new ObjectSerializationCodecFactory();
        //设定传输最大值
        factory.setDecoderMaxObjectSize(Integer.MAX_VALUE);// 设定后服务器可以接收大数据
        factory.setEncoderMaxObjectSize(Integer.MAX_VALUE);
        acceptor.getSessionConfig().setReceiveBufferSize(10240);
        acceptor.getSessionConfig().setSendBufferSize(10240);
        chain.addLast("logging", new LoggingFilter());//这个用于打印日志 可以不写
        //设定服务端消息处理器
        acceptor.setHandler(this);
        InetSocketAddress inetSocketAddress = null;
        try {
            inetSocketAddress = new InetSocketAddress(Constants.WEB_FILE_MATCH_PORT);
            acceptor.bind(inetSocketAddress);
            LogUtils.e("文件服务器已经开启：", " " + Constants.WEB_FILE_MATCH_PORT);
            Toast.makeText(MBroadcastApplication.getMyContext(), "音响接收文件服务启动成功", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            LogUtils.e("文件服务器已经开启：", " " + "服务器启动异常...");
            LogUtils.e("文件服务器已经开启：", " " + e);
            Toast.makeText(MBroadcastApplication.getMyContext(), "音响接收文件服务启动失败" + e, Toast.LENGTH_LONG).show();
            acceptor.dispose(false);
            e.printStackTrace();
        }
    }

}  