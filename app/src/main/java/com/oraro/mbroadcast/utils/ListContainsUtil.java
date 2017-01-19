package com.oraro.mbroadcast.utils;

import com.oraro.mbroadcast.model.PlayEntry;
import com.oraro.mbroadcast.vo.PlayAlgorithmVO;

import java.util.List;

/**
 * Created by wy on 2016/8/30.
 */
public class ListContainsUtil {
    /**
     * 判断PlayEntry是否存在于队列
     * @param list 队列
     * @param playEntry 对象
     * @return
     */
    public static boolean contains(List<PlayEntry> list,PlayEntry playEntry){
        boolean flag = false;
        for(PlayEntry pe:list){
            if(pe.getId().equals(playEntry.getId())){
                flag = true;
            }
        }

        return flag;
    }

    /**
     * 更新资源
     * @param list
     * @param playEntry
     * @return
     */
    public static boolean replace(List<PlayEntry> list,PlayEntry playEntry){
        boolean flag = false;
        PlayEntry entry = null;
        for(PlayEntry pe:list){
            if(pe.getId().equals(playEntry.getId())){
                flag = true;
                entry = pe;
                break;
            }
        }
        if(entry != null){
            entry.setTextDesc(playEntry.getTextDesc());
            entry.setFlightInfoTemp(playEntry.getFlightInfoTemp());
            entry.setPlayEntryId(playEntry.getPlayEntryId());
            entry.setTimes(playEntry.getTimes());
            entry.setTime(playEntry.getTime());
            entry.setDoTimes(playEntry.getDoTimes());
            entry.setFileName(playEntry.getFileName());
            entry.setFileParentPath(playEntry.getFileParentPath());
            entry.setFileSuffix(playEntry.getFileSuffix());
            entry.setIsQueue(playEntry.getIsQueue());
            entry.setXmlKey(playEntry.getXmlKey());
        }

        return flag;
    }

    /**
     * 判断PlayEntry是否存在于队列
     * @param list 队列
     * @param playEntry 对象
     * @return
     */
    public static boolean containsForAlg(List<PlayAlgorithmVO> list, PlayEntry playEntry){
        boolean flag = false;
        for(PlayAlgorithmVO pa:list){
            PlayEntry pe = pa.getPe();
            if(pe.getId().equals(playEntry.getId())){
                flag = true;
            }
        }

        return flag;
    }

    /**
     * 更新资源
     * @param list
     * @param playEntry
     * @return
     */
        public static boolean replaceForAlg(List<PlayAlgorithmVO> list,PlayEntry playEntry){
        boolean flag = false;
//        PlayEntry playEntry = playVO.getPe();
        PlayEntry entry = null;
        for(PlayAlgorithmVO pa:list){
            PlayEntry pe = pa.getPe();
            if(pe.getId().equals(playEntry.getId())){
                flag = true;
                entry = pe;
                break;
            }
        }
        if(entry != null){
            entry.setTextDesc(playEntry.getTextDesc());
            entry.setFlightInfoTemp(playEntry.getFlightInfoTemp());
            entry.setPlayEntryId(playEntry.getPlayEntryId());
            entry.setTimes(playEntry.getTimes());
            entry.setTime(playEntry.getTime());
            entry.setDoTimes(playEntry.getDoTimes());
            entry.setFileName(playEntry.getFileName());
            entry.setFileParentPath(playEntry.getFileParentPath());
            entry.setFileSuffix(playEntry.getFileSuffix());
            entry.setIsQueue(playEntry.getIsQueue());
            entry.setXmlKey(playEntry.getXmlKey());
        }

        return flag;
    }
}
