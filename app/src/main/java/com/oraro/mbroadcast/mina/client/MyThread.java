package com.oraro.mbroadcast.mina.client;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.model.SimpleEvent;
import com.oraro.mbroadcast.utils.LogUtils;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.greenrobot.eventbus.EventBus;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by dongyu on 2016/10/11 0011.
 */
public class MyThread extends Thread  {
    private final String TAG = this.getClass().getSimpleName();
    File msendFile;
    String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


    public File getSendFile() {
        return msendFile;
    }

    public void setSendFile(String path) {
        LogUtils.e("MinaFileClient","setSendFile path = " + path);
        this.msendFile = new File(path);
    }

    @Override
    public void run() {
        super.run();
        NioSocketConnector connector = new NioSocketConnector();
        DefaultIoFilterChainBuilder chain = connector.getFilterChain();
        ObjectSerializationCodecFactory factory = new ObjectSerializationCodecFactory();
        factory.setDecoderMaxObjectSize(Integer.MAX_VALUE);
        factory.setEncoderMaxObjectSize(Integer.MAX_VALUE);
        connector.getSessionConfig().setReceiveBufferSize(10240);
        connector.getSessionConfig().setSendBufferSize(10240);
        connector.setHandler(new MinaFileClient(this));
        ConnectFuture connectFuture = connector.connect(new InetSocketAddress(ip,Constants.WEB_FILE_MATCH_PORT));
        connectFuture.awaitUninterruptibly();//写上这句为了得到下面的session 意思是等待连接创建完成 让创建连接由异步变同步


        //后来表明我开始的想法不行 动态依旧不能做到
//      @SuppressWarnings("unused")
//      IoSession session = connectFuture.getSession();
//      setSession(session);



        //启动socket来直接传输文件
//        new Thread(new Runnable() {
//                @Override
//                public void run() {
//                int length = 0;
//                double sumL = 0 ;
//                byte[] sendBytes = null;
//                Socket socket = null;
//                DataOutputStream dos = null;
//                FileInputStream fis = null;
//                boolean bool = false;
//                try {
//                    long l = msendFile.length();
//
//                    SimpleEvent simpleEvent=new SimpleEvent(Constants.File_Trans_Length);
//                    simpleEvent.setmDataLength(100);
//                    EventBus.getDefault().postSticky(simpleEvent);
//
//                    socket = new Socket();
//                    socket.connect(new InetSocketAddress(ip, Constants.FILE_TRANS_PORT));
//                    dos = new DataOutputStream(socket.getOutputStream());
//                    fis = new FileInputStream(msendFile);
//                    sendBytes = new byte[1024];
//                    while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
//                        sumL += length;
//                        //LogUtils.e("MinaFileClient--->"+msendFile.getName()+"--->"+"已传输："+((sumL/l)*100)+"%");
//                        dos.write(sendBytes, 0, length);
//                        dos.flush();
//
//                        SimpleEvent simpleEvent1=new SimpleEvent(Constants.File_Trans_Loading);
//                        simpleEvent1.setmDataLength((int) ((sumL/l)*100));
//                        EventBus.getDefault().postSticky(simpleEvent1);
//                    }
//                    //虽然数据类型不同，但JAVA会自动转换成相同数据类型后在做比较
//                    if(sumL==l){
//                        bool = true;
//                    }
//                }catch (Exception e) {
//                    LogUtils.e("MinaFileClient--->"+msendFile.getName()+"--->"+"客户端文件传输异常");
//                    bool = false;
//                    e.printStackTrace();
//                } finally{
//                    try {
//                        if (dos != null)
//                            dos.close();
//                        if (fis != null)
//                            fis.close();
//                        if (socket != null)
//                            socket.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                LogUtils.e("MinaFileClient--->"+msendFile.getName()+"--->完成");
//            }
//        }).start();


    }
}
