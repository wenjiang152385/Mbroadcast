package com.oraro.mbroadcast.logicService;

import android.util.Log;

import com.oraro.mbroadcast.Constants;
import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.dao.DBManager;
import com.oraro.mbroadcast.dao.FlightInfoTempDao;
import com.oraro.mbroadcast.dao.InterCutDataDao;
import com.oraro.mbroadcast.dao.PlayEntryDao;
import com.oraro.mbroadcast.dao.SettingsDao;
import com.oraro.mbroadcast.dao.UrgentItemBeanDao;
import com.oraro.mbroadcast.model.FlightInfoTemp;
import com.oraro.mbroadcast.model.PlayEntry;
import com.oraro.mbroadcast.model.Settings;
import com.oraro.mbroadcast.vo.PlayVO;

import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.rx.RxDao;
import org.greenrobot.greendao.rx.RxQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import rx.Observable;

/**
 * 数据中心
 * Created by wy on 2016/8/25.
 */
public class DataService {
    private final static String TAG = DataService.class.getSimpleName();

    public List<FlightInfoTemp> getFlightInfoTemp(Date beginTime, Date endTime) {

        DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
        FlightInfoTempDao tempDao = manager.getFlightInfoTempDao(DBManager.READ_ONLY);
        QueryBuilder qb = tempDao.queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        List<FlightInfoTemp> flightInfoTempList = qb.where(FlightInfoTempDao.Properties.Date.ge(beginTime), FlightInfoTempDao.Properties.Date.le(endTime)).orderAsc(FlightInfoTempDao.Properties.PlanToTakeOffDate).list();
        return flightInfoTempList;
    }

    public List<FlightInfoTemp> getFlightInfoTemp(Date beginTime, Date endTime, int pageNum) {
        DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
        FlightInfoTempDao tempDao = manager.getFlightInfoTempDao(DBManager.READ_ONLY);
        QueryBuilder qb = tempDao.queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        List<FlightInfoTemp> flightInfoTempList = qb.where(FlightInfoTempDao.Properties.Date.ge(beginTime), FlightInfoTempDao.Properties.Date.le(endTime)).orderAsc(FlightInfoTempDao.Properties.PlanToTakeOffDate).offset(pageNum * 30).limit(30).list();
        return flightInfoTempList;
    }

    public List getPlayEntry(Date beginTime, Date endTime) {
        DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
        PlayEntryDao dao = manager.getPlayEntryDao(DBManager.READ_ONLY);
        QueryBuilder qb = dao.queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        List list = qb.where(PlayEntryDao.Properties.Time.ge(beginTime), PlayEntryDao.Properties.Time.le(endTime)).orderAsc(PlayEntryDao.Properties.Time).list();
        return list;

    }

    public List getPlayEntry(Date beginTime) {
        DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
        PlayEntryDao dao = manager.getPlayEntryDao(DBManager.READ_ONLY);
        QueryBuilder qb = dao.queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;

        List list = manager.queryBySQL(dao, "WHERE T.\"TIME\">=" + beginTime.getTime() + " AND T.\"DO_TIMES\"< T.'TIMES' ORDER BY T.'TIME' ASC,T.'IS_EMENG' DESC LIMIT 10");

        //List list =qb.where(PlayEntryDao.Properties.Time.ge(beginTime),PlayEntryDao.Properties.DoTimes.lt(PlayEntryDao.Properties.Times)).orderAsc(PlayEntryDao.Properties.Time).orderDesc(PlayEntryDao.Properties.IsEmeng).limit(10).list();
        return list;

    }

    public List getPlayEntryFlightNumber(PlayEntry playEntry) {
        DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
        PlayEntryDao dao = manager.getPlayEntryDao(DBManager.READ_ONLY);
        QueryBuilder qb = dao.queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        List list = qb.where(PlayEntryDao.Properties.Time.ge(playEntry.getTime()), PlayEntryDao.Properties.PlayEntryId.eq(playEntry.getPlayEntryId())).list();
        return list;

    }

    /**
     * 得到当天的该航班号对应的广播数据
     */
    public List getAllTodayPlayEntry(PlayEntry playEntry){
        Date nowDate = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.add(calendar.DATE, 0);
        nowDate = calendar.getTime();
        Date endTime = new Date(nowDate.getYear(), nowDate.getMonth(), nowDate.getDate(), 23, 59, 59);

        DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
        PlayEntryDao dao = manager.getPlayEntryDao(DBManager.READ_ONLY);
        QueryBuilder qb = dao.queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        List list = qb.where(PlayEntryDao.Properties.Time.ge(playEntry.getTime()), PlayEntryDao.Properties.Time.le(endTime),PlayEntryDao.Properties.PlayEntryId.eq(playEntry.getPlayEntryId())).list();
        return list;
    }

    public PlayEntry getPlayEntry(long id) {
        DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
        PlayEntryDao dao = manager.getPlayEntryDao(DBManager.READ_ONLY);
        PlayEntry playEntry = (PlayEntry) manager.queryById(id, dao);
        return playEntry;

    }

    public List getPlayVO(Date beginTime, Date endTime) {
        List<PlayEntry> sortList = getPlayEntry(beginTime, endTime);
        List<PlayVO> returnList = new LinkedList<>();
        if (sortList != null && sortList.size() != 0)
            for (PlayEntry pe : sortList) {
                returnList.add(new PlayVO(pe));
            }
        return returnList;
    }

    public List getPlayVO(Date beginTime, Date endTime, int pageNum) {
        List<PlayEntry> sortList = getPlayEntry(beginTime, endTime, pageNum);
        List<PlayVO> returnList = new LinkedList<>();
        if (sortList != null && sortList.size() != 0) {
            for (PlayEntry pe : sortList) {
                returnList.add(new PlayVO(pe));
            }
        }
        return returnList;
    }

    public List getPlayEntry(Date beginTime, Date endTime, int pageNum) {
        DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
        PlayEntryDao dao = manager.getPlayEntryDao(DBManager.READ_ONLY);
        QueryBuilder qb = dao.queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        List list = qb.where(PlayEntryDao.Properties.Time.ge(beginTime), PlayEntryDao.Properties.Time.le(endTime)).orderAsc(PlayEntryDao.Properties.Time).offset(pageNum * 300).limit(300).list();
        Log.e(TAG, "list size = " + list.size());
        return list;

    }
	
	public Observable<List<PlayEntry>> getPlayEntry1(Date beginTime, Date endTime) {
        DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
        RxDao<PlayEntry, Long> dao = manager.getPlayEntryDao(DBManager.READ_ONLY).rx();
        QueryBuilder<PlayEntry> queryBuilder = dao.getDao().queryBuilder();
        queryBuilder.LOG_SQL = true;
        queryBuilder.LOG_VALUES = true;
        queryBuilder.where(PlayEntryDao.Properties.Time.ge(beginTime), PlayEntryDao.Properties.Time.le(endTime)).orderAsc(PlayEntryDao.Properties.Time);
        RxQuery<PlayEntry> rxQuery = queryBuilder.rx();
        return rxQuery.list();
    }

    public List getUrgentData(){
        DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
        UrgentItemBeanDao urgentItemBeanDao = manager.getUrgentItemBeanDao(DBManager.READ_ONLY);
        QueryBuilder qb = urgentItemBeanDao.queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        List list = qb.where(UrgentItemBeanDao.Properties.IsSelected.eq(true)).list();
        return list;
    }

    public List getInterCutData(){
        DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
        InterCutDataDao interCutDate = manager.getInterCutDataDao(DBManager.READ_ONLY);
        QueryBuilder qb = interCutDate.queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        List list = qb.where(InterCutDataDao.Properties.IsPlay.eq(true)).list();
        return list;
    }

    public List getInterCutData(int ty){
        DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
        InterCutDataDao interCutDate = manager.getInterCutDataDao(DBManager.READ_ONLY);
        QueryBuilder qb = interCutDate.queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        List list = qb.where(InterCutDataDao.Properties.IsPlay.eq(true),InterCutDataDao.Properties.Ty.eq(ty)).orderAsc(InterCutDataDao.Properties.Time).list();
        return list;
    }

    public Boolean getAutoPlayStatus() {
        DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());

        SettingsDao dao = manager.getSettingsDao(DBManager.READ_ONLY);
        if (null == dao) {
            return false;
        }
        List<Settings> list = new ArrayList<>();
        if (null != dao) {
            QueryBuilder qb = dao.queryBuilder();
            list = qb.where(SettingsDao.Properties.SettingsName.eq(Constants.SettingsConstants.AUTO_PLAY)).list();
        }
        if (list == null || list.size() == 0)
            return false;
        else {
            return list.get(0).getSettingsStatus();
        }
    }

    /**
     * 设置插播间隔
     */
    public void setSpaceStatus(long space) {
        DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
        SettingsDao dao = manager.getSettingsDao(DBManager.WRITE_ONLY);
        List<Settings> list = dao.queryBuilder().where(SettingsDao.Properties.SettingsName.eq(Constants.SettingsConstants.SPACE)).list();
        Settings string;
        if (list != null && list.size() > 0) {
            string = list.get(0);
        } else {
            string = new Settings();
            string.setSettingsName(Constants.SettingsConstants.SPACE);
        }
        string.setSettingsValue(space + "");
        manager.insertOrUpdate(string, dao);

    }


    public long getSpaceStatus() {
        DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());

        SettingsDao dao = manager.getSettingsDao(DBManager.READ_ONLY);
        List<Settings> list = new ArrayList<>();
        if (null != dao) {
            QueryBuilder qb = dao.queryBuilder();
            list = qb.where(SettingsDao.Properties.SettingsName.eq(Constants.SettingsConstants.SPACE)).list();
        }
        if (list == null || list.size() == 0)
            return 2000;
        else {
            return Long.parseLong(list.get(0).getSettingsValue());
        }
    }

    /**
     * 设置可自动播放
     */
    public void setAutoPlayStatus() {
        DBManager manager = DBManager.getInstance(MBroadcastApplication.getMyContext());
        SettingsDao dao = manager.getSettingsDao(DBManager.WRITE_ONLY);
        QueryBuilder qb = dao.queryBuilder();
        List<Settings> list = qb.where(SettingsDao.Properties.SettingsName.eq(Constants.SettingsConstants.AUTO_PLAY)).list();
        if (list == null || list.size() == 0) {
            Settings settings = new Settings();
            settings.setSettingsName(Constants.SettingsConstants.AUTO_PLAY);
            settings.setSettingsStatus(true);
            dao.insert(settings);
        } else {
            Settings settings = list.get(0);
            settings.setSettingsStatus(!settings.getSettingsStatus());
            dao.update(settings);
        }
    }
}
