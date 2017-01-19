package com.oraro.mbroadcast.algorithm;

import android.database.DatabaseErrorHandler;
import android.util.Log;

import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.model.PlayEntry;
import com.oraro.mbroadcast.utils.ListContainsUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 播放核心算法
 * @author 王子榕
 */
public class PlayAlgorithm {

    /**
     * Debug功能，默认请关闭，否则影响性能
     */
    private final static boolean ISDEBUG = false;
    private final static boolean IS_SHOW_IN_OUT_DEBUG = true;
    /**
     * 队列时间范围（秒）
     */
    private final int CHACHE_TIME = 2*60;
    /**
     * 队列超时时间范围（秒），超时且已经不是第一次播放的会被移除
     */
    private final int OUT_TIME = 5*60;
    /**
     * 自动刷新时间（秒），每隔多久刷新一次队列
     */
    public static final int REFRESH_TIME = 1;

    private List<PlayEntry> playCacheList = new LinkedList<PlayEntry>();
    private List<PlayEntry> sortList = new LinkedList<PlayEntry>();
    private List<PlayEntry> shouldSaveList = new LinkedList<PlayEntry>();
    public List<PlayEntry> fresh(List<PlayEntry> list){
        Log.e("wy", "fresh  ---begin");

        sortListByTime(list);//排序传入的List
        List<PlayEntry> canIntoList =  checkCanIntoCache(sortList);//选出可进入播放池的对象集合
        Log.e("wy", "fresh  +" + canIntoList.size());
        intoCache(canIntoList);//进入播放池
        removeOutTime();//排查播放池超时的对象，并移除超时对象
        Log.e("wy", "fresh  ---end");
        return playCacheList;

    }
    public void debug(){
        if(ISDEBUG){
            Log.e("wy", "isdebug + "+playCacheList.size());
            for(PlayEntry pe:playCacheList){
                Log.e("wy","PlayAlgorithm---debug---pe:"+pe.getId());
            }
        }
    }

    /**
     * 将传入的集合按时间排序放置到sortList中
     * @param list 待排序的集合
     */
    private void sortListByTime(List<PlayEntry> list){
        sortList.clear();
        for(PlayEntry pe:list){


            if(!ListContainsUtil.contains(sortList,pe)){
                intoCacheByTime(pe,sortList);
            }
        }
    }


    /**
     * 将传入的集合按时间排序放置到sortList中
     * @param list 待排序的集合
     * @return  排序后的队列
     */
    public static List<PlayEntry> sortListByTimeForInterface(List<PlayEntry> list){
        List<PlayEntry> playEntryList = new LinkedList<>();

        for(PlayEntry pe:list){
            if(!ListContainsUtil.contains(playEntryList,pe)){
                intoCacheByTime(pe,playEntryList);
            }
        }
        return playEntryList;
    }

    /**
     * 判别是否符合进栈条件
     * @param list 待判别的数据集合
     * @return 符合条件的对象集合
     */
    private List<PlayEntry> checkCanIntoCache(List<PlayEntry> list){
        /**
         * 临时存储队列（符合插入条件）
         */
         List<PlayEntry> temporaryList = new LinkedList<PlayEntry>();
         Date nowDate = new Date();
        /**
         * 判断传入数据的合理性
         */
        if(list == null) return temporaryList;
        if(list.size() == 0 ) return temporaryList;

        /**
         * 遍历得到符合条件的对象
         */
        for(PlayEntry pe:list){
            if(checkPlayTime(pe)){
                continue;
            }

            Date date = pe.getTime();
            long betweenSecond = (date.getTime() - nowDate.getTime()) / 1000;
//            Log.e("wy","pe = "+pe.getId()+"--->"+date);
//            Log.e("wy","pe = "+pe.getId()+"--->"+nowDate);
//            Log.e("wy","pe = "+pe.getId()+"--->"+betweenSecond);
            /**
             * 分IF-ELSE是因为如果被压入堆栈，数据库就会被标志为1，那么这时断电重启，要保证在当前时间之后的数据仍然可播放
             */
            if( betweenSecond<=CHACHE_TIME && betweenSecond>=0){
                    temporaryList.add(pe);//没过时的，直接被压入
            }
            else if(betweenSecond>=-CHACHE_TIME && betweenSecond<0){
                if(pe.getIsQueue() == 0)
                    temporaryList.add(pe);//过时的，只有没有被压入过队列，才会被再次压入
            }
        }
        return temporaryList;
    }

    /**
     * 插入播放缓存堆栈
     * @param list 满足插入条件的播放对象集合
     */
    private void intoCache(List<PlayEntry> list){
        /**
         * 判断传入数据的合理性
         */
        if(list == null) return;
        if(list.size() == 0 ) return;

//        shouldSaveList.clear();
//        /**
//         * 保留务必存在的
//         */
//        for(PlayEntry pe:playCacheList){
//            if(pe.){
//                shouldSaveList.add(pe);
//            }
//        }

        for(PlayEntry pe:list){

            if(!ListContainsUtil.contains(playCacheList,pe)){//如果是新的，就加入队列

                intoCacheByTime(pe,playCacheList);
                modifyEntityIntoCache(pe);
            }else{//如果已经存在，则更替对象
                ListContainsUtil.replace(playCacheList,pe);
            }
        }

    }

    /**
     * 时间排序插入算法(播放顺序错了，就是由此处引起)
     */
    private static void intoCacheByTime(PlayEntry pe,List<PlayEntry> list){
        if(list.size() == 0){
            list.add(pe);
        }else{
            for(int i= 0; i < list.size();i++){
                if((list.get(i).getTime().getTime()>pe.getTime().getTime())|| (i == (list.size()-1) )){
                    list.add(i, pe);
                    break;
                }
            }
        }

        /**
         * Debug功能
         */
        if(ISDEBUG){
            if(!ListContainsUtil.contains(list,pe)) Log.e("wy","核心算法--时间排序算法--出现严重异常：数据丢失！被丢失的播放对象的id为："+pe.getId());
        }
    }

    /**
     * 标注实体已进入播放队列
     * @param pe
     */
    private void modifyEntityIntoCache(PlayEntry pe){
        pe.setIsQueue(1);
        DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
        manager.update(pe, manager.getPlayEntryDao(DBManager.WRITE_ONLY));
        //manager.closeDbConnections();
    }

    /**
     * 移除超时的对象
     */
    private void removeOutTime(){

        if(playCacheList == null) return;
        if(playCacheList.size() == 0 ) return;
        Date nowDate = new Date();
        /**
         * 遍历得到符合条件的对象
         */
        for(PlayEntry pe:playCacheList){
            if(checkPlayTime(pe)){
                playCacheList.remove(pe);
                continue;
            }
            Date date = pe.getTime();
            long betweenSecond = (date.getTime() - nowDate.getTime()) / 1000;
            if( betweenSecond < -OUT_TIME && pe.getIsQueue()>1){
                playCacheList.remove(pe);
            }

        }
    }

    private boolean checkPlayTime(PlayEntry pe){
        if(pe.getTimes() <= pe.getDoTimes()) return true ;
        else return  false;
    }


}
