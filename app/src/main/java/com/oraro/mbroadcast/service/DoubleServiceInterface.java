package com.oraro.mbroadcast.service;

import android.content.Context;

/**
 * Service外部调用接口
 * @author 刘彬
 */
public interface DoubleServiceInterface {
    /**
     * 开启两个Service
     * @param context 上下文
     */
    public abstract void StartServices(Context context, int fag);
}
