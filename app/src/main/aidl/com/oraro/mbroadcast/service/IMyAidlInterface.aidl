// IMyAidlInterface.aidl
package com.oraro.mbroadcast.service;

// Declare any non-default types here with import statements
import com.oraro.mbroadcast.service.OnRefreshUIListener;

interface IMyAidlInterface {
     void startService();
     void stopService();
     void stopMediaPlay();
     void stopTTSPlay();
     void stopPlay();
     void startTTSPlay(long ttsid ,String textDesc , int count,OnRefreshUIListener onRefreshUIListener);
     void startMediaPlay(long mdid,String path,int count,OnRefreshUIListener onRefreshUIListener);
     void setOnRefreshUIListener(OnRefreshUIListener onRefreshUIListener);
     void unRegisterOnRefreshUIListener();
     void deleteCatch(long id);
     void refresh();
     void needrefresh(boolean tag);
     void needrefreshbyty(boolean tag,int ty);
     void autioPlay(boolean tag);
     void startInterCut(int count,long space);
     void initSpeekingServiceForInterCut();
}
