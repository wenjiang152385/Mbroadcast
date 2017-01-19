package com.oraro.mbroadcast.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Administrator on 2016/9/27 0027.
 */
public class IPControl {
    public String getIpv4Address() {
        String ipv4address = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (inet.hasMoreElements()) {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress()
                            && ip instanceof Inet4Address) {
                        return ipv4address = ip.getHostAddress();
                    }
                }

            }
        } catch (SocketException e) {
//            Log.e("wjq", "获取本地ip地址失败");
            e.printStackTrace();
        }
        return ipv4address;
    }

    // 得到本机Mac地址
    public String getLocalMac(Context context) {
        String mac = "";
        // 获取wifi管理器
        WifiManager wifiMng = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfor = wifiMng.getConnectionInfo();
        mac = wifiInfor.getMacAddress();
        return mac;
    }

    public String getUDPAddress(String ip) {
        String udpAddress = "";
        String strs = "";
        if (null != ip && ip.length() != 0) {
            strs = ip.substring(0, ip.lastIndexOf("."));
        }
        udpAddress = strs + ".255";
        return udpAddress;
    }

}
