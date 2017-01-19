package com.oraro.mbroadcast.utils;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.model.DeviceEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/29 0029.
 */
public class DeviceListControl {

//    private static DeviceListControl mDeviceListControl;

//    private List<DeviceEntity> mDeviceList = new ArrayList<>();
//    private String mMsg;


//    public static DeviceListControl newInstance() {
//        return null == mDeviceListControl ? mDeviceListControl = new DeviceListControl() : mDeviceListControl;
//    }

    private Handler mHeartHandler = new Handler();

    public DeviceEntity getDeviceEntity(String msg) {
        Gson gon = new Gson();
        DeviceEntity deviceEntity = gon.fromJson(msg, DeviceEntity.class);
        return deviceEntity;
    }

    public boolean checkIsExit(List<DeviceEntity> list, DeviceEntity recEntity) {
        for (final DeviceEntity deviceEntity : list) {
            if (deviceEntity.getMac().equals(recEntity.getMac())) {
                return false;
            }
        }
        return true;
    }






//    public void getMsg(String msg) {
//        mMsg = msg;
//    }

//    public List<DeviceEntity> getDeviceList() {
//        Gson gon = new Gson();
//        DeviceEntity deviceEntity = gon.fromJson(mMsg, DeviceEntity.class);
//        if (checkEntity(deviceEntity)) {
//            mDeviceList.add(deviceEntity);
//        }
//        return mDeviceList;
//    }

//    private boolean checkEntity(DeviceEntity entity) {
//        for (int i = 0; i < mDeviceList.size(); i++) {
//            DeviceEntity checkEntity = mDeviceList.get(i);
//            if (checkEntity.getMac().equals(entity.getMac())) {
//                return false;
//            }
//        }
//        return true;
//    }

//    public boolean compareList(List<DeviceEntity> list1, List<DeviceEntity> list2) {
//        for (int i = 0; i < list1.size(); i++) {
//            for (int j = 0; j < list2.size(); j++) {
//                if (list1.get(i).equals(list2.get(j))) {
//                    return false;
//                }
//            }
//        }
//        return true;
//    }
}
