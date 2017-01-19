package com.oraro.mbroadcast.algorithm;

import android.util.Log;

import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.model.PlayEntry;
import com.oraro.mbroadcast.utils.ListContainsUtil;
import com.oraro.mbroadcast.vo.PlayAlgorithmVO;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 播放核心算法(基于权值)
 *
 * @author 王子榕
 */
public class PlayAlgorithmByWeight {


    /**
     * 队列时间范围（秒）
     */
    private final int CHACHE_TIME = 2 * 60;
    /**
     * 队列超时时间范围（秒），超时且已经不是第一次播放的会被移除
     */
    private final int OUT_TIME = 5 * 60;
    /**
     * 自动刷新时间（秒），每隔多久刷新一次队列
     */
    public static final int REFRESH_TIME = 1;

    private List<PlayAlgorithmVO> playCacheList = new LinkedList<PlayAlgorithmVO>();

    public PlayAlgorithmVO fresh(List<PlayEntry> list) {
        List<PlayEntry> canIntoList = checkCanIntoCache(list);//选出可进入播放池的对象集合
        intoCache(canIntoList);//进入播放池
        removeOutTime();//排查播放池超时的对象，并移除超时对象
        PlayAlgorithmVO play = getNeedPlayVO();
        debugPlayCacheList();
        addWeight();
        Log.e("wyplaycache", "playVO ==" + play);
        return play;

    }

    public void delete(PlayEntry playEntry) {
        if (playCacheList != null && !playCacheList.isEmpty() && playEntry != null) {
            Log.e("huanghui","delete  playCacheList = " +playCacheList.size());
            for (int i = 0; i < playCacheList.size(); i++) {
                PlayAlgorithmVO vo = playCacheList.get(i);
                if (vo.getPe().getId() == playEntry.getId()) {
                    playCacheList.remove(i);
                }
            }
        }
    }

    private void debugPlayCacheList() {
        if (playCacheList != null && !playCacheList.isEmpty()) {
            for (PlayAlgorithmVO vo : playCacheList) {
                Log.e("wyplaycache", "playCacheList -->" + vo);
            }
        } else {
            Log.e("wyplaycache", "playCacheList == null");
        }
    }


    private void addWeight() {
        if (playCacheList != null && !playCacheList.isEmpty()) {
            for (PlayAlgorithmVO vo : playCacheList
                    ) {
                vo.setWeightValue(vo.getWeightValue() + 1000);
            }
        }

    }


    /**
     * 判别是否符合进栈条件
     *
     * @param list 待判别的数据集合
     * @return 符合条件的对象集合
     */
    private List<PlayEntry> checkCanIntoCache(List<PlayEntry> list) {
        /**
         * 临时存储队列（符合插入条件）
         */
        List<PlayEntry> temporaryList = new LinkedList<PlayEntry>();
        Date nowDate = new Date();
        /**
         * 判断传入数据的合理性
         */
        if (list == null) return temporaryList;
        if (list.size() == 0) return temporaryList;

        /**
         * 遍历得到符合条件的对象
         */
        for (PlayEntry pe : list) {
            if (checkPlayTime(pe)) {
                continue;
            }

            Date date = pe.getTime();
            long betweenSecond = (date.getTime() - nowDate.getTime()) / 1000;
            /**
             * 分IF-ELSE是因为如果被压入堆栈，数据库就会被标志为1，那么这时断电重启，要保证在当前时间之后的数据仍然可播放
             */
            if (betweenSecond <= CHACHE_TIME && betweenSecond >= 0) {
                temporaryList.add(pe);//没过时的，直接被压入
            } else if (betweenSecond >= -CHACHE_TIME && betweenSecond < 0) {
                if (pe.getIsQueue() == 0)
                    temporaryList.add(pe);//过时的，只有没有被压入过队列，才会被再次压入
            }
        }
        return temporaryList;
    }

    /**
     * 插入播放缓存堆栈
     *
     * @param list 满足插入条件的播放对象集合
     */
    private void intoCache(List<PlayEntry> list) {
        /**
         * 判断传入数据的合理性
         */
        if (list == null) return;
        if (list.size() == 0) return;
        int step = list.size() + 1;//微偏差,让数据库排在前面的,获得优先播放的机会
        Date nowDate = new Date();
        for (PlayEntry pe : list) {

            if (!ListContainsUtil.containsForAlg(playCacheList, pe)) {//如果是新的，就加入队列
                PlayAlgorithmVO vo = new PlayAlgorithmVO();
                vo.setPe(pe);
                vo.setWeightValue(step--);
                playCacheList.add(vo);
                modifyEntityIntoCache(pe);
            } else {//如果已经存在，则更替对象
                ListContainsUtil.replaceForAlg(playCacheList, pe);
            }
        }

    }


    /**
     * 标注实体已进入播放队列
     *
     * @param pe
     */
    private void modifyEntityIntoCache(PlayEntry pe) {
        pe.setIsQueue(1);
        DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
        manager.update(pe, manager.getPlayEntryDao(DBManager.WRITE_ONLY));
    }

    /**
     * 移除超时的对象
     */
    private void removeOutTime() {

        if (playCacheList == null) return;
        if (playCacheList.size() == 0) return;
        Date nowDate = new Date();
        List<PlayAlgorithmVO> needRemoveList = new ArrayList<PlayAlgorithmVO>();
        /**
         * 遍历得到符合条件的对象
         */
        for (PlayAlgorithmVO pa : playCacheList) {
            PlayEntry pe = pa.getPe();
            if (checkPlayTime(pe)) {
                needRemoveList.add(pa);
                //playCacheList.remove(pa);
                continue;
            }
            Date date = pe.getTime();
            long betweenSecond = (date.getTime() - nowDate.getTime()) / 1000;
            if (betweenSecond < -OUT_TIME && pe.getDoTimes() > 1) {
                needRemoveList.add(pa);
                //playCacheList.remove(pa);
            }

        }

        for (PlayAlgorithmVO vo : needRemoveList) {
            playCacheList.remove(vo);
        }
        needRemoveList.clear();
    }

    private PlayAlgorithmVO getNeedPlayVO() {
        if (playCacheList == null) return null;
        if (playCacheList.size() == 0) return null;


        PlayAlgorithmVO needPlayVo = null;
        Date nowDate = new Date();
        for (PlayAlgorithmVO pa : playCacheList) {
            if (pa.getPe().getTime().getTime() > nowDate.getTime()) continue;
            if (needPlayVo == null && pa.getPe().getTime().getTime() < nowDate.getTime()) {
                needPlayVo = pa;
            } else if (needPlayVo != null && pa.getWeightValue() > needPlayVo.getWeightValue()) {
                needPlayVo = pa;
            }
        }

        if (needPlayVo != null && needPlayVo.getPe().getTime().getTime() < nowDate.getTime())
            return needPlayVo;
        else
            return null;
    }

    private boolean checkPlayTime(PlayEntry pe) {
        if (pe.getTimes() <= pe.getDoTimes()) return true;
        else return false;
    }


}
