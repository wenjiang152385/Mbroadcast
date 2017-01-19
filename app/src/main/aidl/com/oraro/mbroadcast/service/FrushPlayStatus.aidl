// FrushPlayStatus.aidl
package com.oraro.mbroadcast.service;

// Declare any non-default types here with import statements
/**
 * AIDL通知界面刷新
 * @author 王子榕
 */
interface FrushPlayStatus {

    /**
     * 通知当前播放的ID,并刷新界面
     */
    void frushPlaying(long id);

     /**
     * 通知当前播放的ID,并刷新界面
     */
    void frushCompleted(long id);
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
            boolean isPlay();
}
