package com.oraro.mbroadcast.logicService;

import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.dao.PlayEntryDao;
import com.oraro.mbroadcast.model.PlayEntry;

/**
 * Created by wy on 2016/8/31.
 */
public class DBService {
    private DBManager dbManager =DBManager.getInstance(MBroadcastApplication.getMyContext());
    private  PlayEntryDao playEntryDao  = dbManager.getPlayEntryDao(DBManager.WRITE_ONLY);

    /**
     * 新增播放对象
     * @param pe 播放对象
     */
    public void insertPlayEntry(PlayEntry pe){

        dbManager.insert(pe,playEntryDao);
    }

    /**
     * 更新播放对象
     * @param pe 播放对象
     */
    public void UpdatePlayEntry(PlayEntry pe){
        dbManager.update(pe,playEntryDao);
    }
}
