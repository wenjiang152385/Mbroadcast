package com.oraro.mbroadcast.udpthread;

import android.os.Bundle;
import android.util.Log;


import com.oraro.mbroadcast.listener.IReceiveMsg;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * Created by Administrator on 2016/9/29 0029.
 */
public class UDPReceiveThread extends Thread {

    private IReceiveMsg mIReceiveMsg;
    private int mPORT;

    private DatagramSocket mDataSocket;
    private byte[] mReceiveByte;

    private static UDPReceiveThread mUDPReceiveThread;

    private boolean mInterrupted = false;

    public static UDPReceiveThread newInstance() {

        return null == mUDPReceiveThread ? mUDPReceiveThread = new UDPReceiveThread() : mUDPReceiveThread;
    }

    private UDPReceiveThread() {

    }

    public void startUDPReceiveThread(int port, IReceiveMsg iReceiveMsg) {
        mPORT = port;
        mIReceiveMsg = iReceiveMsg;
        start();
    }

    public void setInterrupted(boolean interrupted) {
        mInterrupted = interrupted;
        if (mInterrupted) {
            mUDPReceiveThread = null;
        }
        mIReceiveMsg = null;
    }

    @Override
    public void run() {
        super.run();
        try {

            if (null == mDataSocket) {
                mDataSocket = new DatagramSocket(null);
                mDataSocket.setReuseAddress(true);
                mDataSocket.bind(new InetSocketAddress(mPORT));

            }
            mReceiveByte = new byte[1024];
            DatagramPacket dataPacket = new DatagramPacket(mReceiveByte, mReceiveByte.length);
            String receiveStr;

            while (!mInterrupted) {
                mDataSocket.receive(dataPacket);
//                Log.e("wjq", "dataPacket ?" + (dataPacket == null));


                if (null != dataPacket) {
                    receiveStr = new String(mReceiveByte, 0, dataPacket.getLength());
                    if (null != mIReceiveMsg)
                    mIReceiveMsg.receiveMsg(receiveStr);
//                    Log.e("wjq", "receiveStr = " + receiveStr);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
