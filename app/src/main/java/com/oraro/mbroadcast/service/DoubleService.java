package com.oraro.mbroadcast.service;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.oraro.mbroadcast.Constants;

/**
 * 接口实现类
 *
 * @author 刘彬
 */
public class DoubleService implements DoubleServiceInterface {

    private static DoubleService doubleService;

    private DoubleService() {
    }

    /**
     * 单例模式
     *
     * @return DoubleService实例
     */
    public static DoubleService getInstance() {
        if (doubleService == null) {
            doubleService = new DoubleService();
        }
        return doubleService;
    }

    @Override
    public void StartServices(Context context, int flag) {
        if (flag == 0) {
            //音箱
            Intent i3 = new Intent(context, SerService.class);
            context.startService(i3);
        } else if (flag == 1) {
            //平板
            Intent i2 = new Intent(context, Service2.class);
            context.startService(i2);
        }
    }
}
