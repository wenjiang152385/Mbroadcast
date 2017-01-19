package com.oraro.mbroadcast.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.oraro.mbroadcast.mina.server.MinaStringServerHandler;
import com.oraro.mbroadcast.utils.LogUtils;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Created by dongyu on 2016/10/8 0008.
 */
public class MinaStringServerService extends Service {
    private  final String TAG = MinaStringServerService.class.getSimpleName();
    IoAcceptor acceptor = null;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.e(TAG, "Service onBind--->");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        return Service.START_STICKY;
    }
}
