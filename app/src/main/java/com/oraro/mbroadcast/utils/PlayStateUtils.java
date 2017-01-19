package com.oraro.mbroadcast.utils;

import android.content.Context;

import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.mp.PlayAudio;
import com.oraro.mbroadcast.proxy.TTSProXy;

/**
 * Created by admin on 2016/11/15
 *
 * @author zmy
 */

public class PlayStateUtils {
    /**
     *
     * @param context
     * @return
     */
    public static boolean isPlaying(Context context) {
        return (PlayAudio.getInstance().isPlaying() || TTSProXy.getInstance(context, context.getPackageName()).isSpeeking()) && !MBroadcastApplication.isincout;

    }
}
