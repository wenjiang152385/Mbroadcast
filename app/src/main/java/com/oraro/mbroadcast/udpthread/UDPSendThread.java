package com.oraro.mbroadcast.udpthread;

import android.util.Log;

import com.oraro.mbroadcast.utils.LogUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by Administrator on 2016/9/29 0029.
 */
public class UDPSendThread extends Thread {

    private DatagramSocket dataSocket;
    private DatagramPacket dataPacket;
    private byte sendDataByte[];

    private int mPORT;
    private String mInfoMsg;
    private String mSendIp;
    private String mStr;
    private boolean mInterrupted = false;

    private static UDPSendThread mUDPSendThread;

    public static UDPSendThread newInstance() {
        return null == mUDPSendThread ? mUDPSendThread = new UDPSendThread() : mUDPSendThread;
    }

    public void startUDPSendThread(int port, String msg, String ip, String str) {
        mInfoMsg = msg;
        mSendIp = ip;
        mPORT = port;
        mStr = str;
        this.start();
    }


    public void setSendIp(String ip,String msg) {
        mSendIp = ip;
        mInfoMsg = msg;
    }

    public void setInterrupted(boolean interrupted) {
        mInterrupted = interrupted;
    }


    @Override
    public void run() {
        super.run();
        while (!mInterrupted) {
            try {
                sleep(1000 * 2);
                if (null == dataSocket) {
                    dataSocket = new DatagramSocket(null);
                    dataSocket.setReuseAddress(true);
                    dataSocket.bind(new InetSocketAddress(mPORT));
                }

                sendDataByte = new byte[1024];
                sendDataByte = mInfoMsg.getBytes();
                dataPacket = new DatagramPacket(sendDataByte, sendDataByte.length,
                        InetAddress.getByName(mSendIp), mPORT);
                dataSocket.send(dataPacket);
//                Log.e("wjq", "infoMsg from " + mStr + " = " + mInfoMsg);
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
