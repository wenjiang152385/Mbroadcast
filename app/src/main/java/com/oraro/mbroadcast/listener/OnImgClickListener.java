package com.oraro.mbroadcast.listener;

import com.oraro.mbroadcast.vo.PlayVO;

/**
 * Created by dongyu on 2016/8/24 0024.
 */
public interface OnImgClickListener {
    void play(PlayVO playVO,int postion);
    void playAagain(PlayVO playVO);
}
