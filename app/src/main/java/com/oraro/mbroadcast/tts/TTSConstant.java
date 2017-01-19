package com.oraro.mbroadcast.tts;

/**
 * TTS常量类
 * Created by admin on 2016/8/4.
 * @author 刘彬
 */
public class TTSConstant {
    /**
     * 发音人常量
     * 1、语言为中英文的发音人可以支持中英文的混合朗读
     * 2、英文发音人只能朗读英文，中文无法朗读
     * 3、汉语发音人只能朗读中文，遇到英文会以单个字母的方式进行朗读
     */
    public interface VoiceName{
        /**
         * 发音人：小燕，青年女声，中英文（普通话）,默认
         */
        String XIAOYAN ="xiaoyan";
        /**
         * 发音人：小宇，青年男声，中英文（普通话）
         */
        String XIAOYU = "xiaoyu";
        /**
         * 发音人：凯瑟琳，青年女声，英文
         */
        String CATHERINE = "catherine";
        /**
         * 发音人：亨利，青年男声，英文
         */
        String HENRY = "henry";
        /**
         * 发音人：小研，青年女声，中英文（普通话）
         */
        String VIXY = "vixy";
        /**
         * 发音人：小琪，青年女声，中英文（普通话）
         */
        String XIAOQI ="xiaoqi";
        /**
         * 发音人：小峰，青年男声，中英文（普通话）
         */
        String XIAOFENG = "vixf";
        /**
         * 发音人：小梅，青年女声，中英文（粤语）
         */
        String XIAOMEI = "xiaomei";
        /**
         * 发音人：小莉，青年女声，中英文（台湾普通话）
         */
        String XIAOLI = "xiaolin";
        /**
         * 发音人：小蓉，青年女声，汉语（四川话）
         */
        String XIAORONG = "xiaorong";
        /**
         * 发音人：小芸，青年女声，汉语（东北话）
         */
        String XIAOQIAN = "xiaoqian";
        /**
         * 发音人：小坤，青年男声，汉语（河南话）
         */
        String XIAOKUN = "xiaokun";
        /**
         * 发音人：小强，青年男声，汉语（湖南话）
         */
        String XIAOQIANG = "xiaoqiang";
        /**
         * 发音人，小莹，青年女声，汉语（陕西话）
         */
        String XIAOYING = "vixying";
        /**
         * 发音人，小新，童年男声，汉语（普通话）
         */
        String XIAOXIN = "xiaoxin";
        /**
         * 发音人：楠楠，童年女声，汉语（普通话）
         */
        String NANNAN = "nannan";
        /**
         * 发音人：老孙，老年男声，汉语（普通话）
         */
        String LAOSUN = "vils";
        /**
         * 发音人：Mariane，法语
         */
        String MARIANE = "Mariane";
        /**
         * 发音人：Allabent，俄语
         */
        String ALLABENT = "Allabent";
        /**
         * 发音人：Gabriela，西班牙语
         */
        String GABRIELA = "Gabriela";
        /**
         * 发音人，Abha，印地语
         */
        String ABHA = "Abha";
        /**
         * 发音人，XiaoYun，越南语
         */
        String XIAOYUN = "XiaoYun";
    }

    /**
     * 保存格式常量
     */
    public interface SaveFormat{
        /**
         * wav格式
         */
        String WAV = "wav";
        /**
         * pcm格式
         */
        String PCM = "pcm";
    }
}
