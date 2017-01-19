package com.oraro.mbroadcast.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.oraro.mbroadcast.MBroadcastApplication;
import com.oraro.mbroadcast.model.DeviceEntity;
import com.oraro.mbroadcast.model.FlightInfo;
import com.oraro.mbroadcast.model.FlightInfoTemp;
import com.oraro.mbroadcast.model.HistoryFlightTempEdit;
import com.oraro.mbroadcast.model.PlayEntry;
import com.oraro.mbroadcast.utils.LogUtils;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.rx.RxQuery;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import rx.Observable;

/**
 * Created by dongyu on 2016/8/10 0010.
 */
public class DBManager<T> {
    private final static String dbName = "broadcast.db";
    public final static int READ_ONLY = 0;
    public final static int WRITE_ONLY = 1;
    private static DBManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private static DaoSession daoSession;
    private static SQLiteDatabase database;
    private static DaoMaster daoMaster;
    private Context context;

    private DBManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static DBManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DBManager.class) {
                if (mInstance == null) {
                    mInstance = new DBManager(context);
                }
            }
        }
        return mInstance;
    }

//    /**
//     * 只关闭helper就好,看源码就知道helper关闭的时候会关闭数据库
//     */
    public void closeDbConnections() {
        if (openHelper != null) {
            openHelper.close();
            openHelper = null;
        }
        if (daoSession != null) {
            daoSession.clear();
            daoSession = null;
        }
    }


    /**
     * 获取可读数据库
     */
    private SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        database = openHelper.getReadableDatabase();
        return database;
    }

    /**
     * 获取可写数据库
     */
    private SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        database = openHelper.getWritableDatabase();
        return database;
    }

    /**
     * 获取 AirlineCompanyDao
     *
     * @param flag
     */
    public AirlineCompanyDao getAirlineCompanyDao(int flag) {
        switch (flag) {
            case READ_ONLY:
                return getSession(getReadableDatabase()).getAirlineCompanyDao();

            case WRITE_ONLY:
                return getSession(getWritableDatabase()).getAirlineCompanyDao();

            default:
                return null;
        }
    }

    /**
     * 获取FlightInfoDao
     *
     * @param flag
     */
    public FlightInfoDao getFlightInfoDao(int flag) {
        switch (flag) {
            case READ_ONLY:
                return getSession(getReadableDatabase()).getFlightInfoDao();

            case WRITE_ONLY:
                return getSession(getWritableDatabase()).getFlightInfoDao();

            default:
                return null;
        }

    }

    /**
     * 获取getFlightInfoTempDao
     *
     * @param flag
     */
    public FlightInfoTempDao getFlightInfoTempDao(int flag) {
        switch (flag) {
            case READ_ONLY:
                return getSession(getReadableDatabase()).getFlightInfoTempDao();

            case WRITE_ONLY:
                return getSession(getWritableDatabase()).getFlightInfoTempDao();

            default:
                return null;
        }

    }

    /**
     * 获取getFlightInfoTempDao
     *
     * @param flag
     */
    public HistoryFlightTempEditDao getHistoryFlightTempEditDao(int flag) {
        switch (flag) {
            case READ_ONLY:
                return getSession(getReadableDatabase()).getHistoryFlightTempEditDao();

            case WRITE_ONLY:
                return getSession(getWritableDatabase()).getHistoryFlightTempEditDao();

            default:
                return null;
        }

    }

    /**
     * 获取getPlayEntryDao
     *
     * @param flag
     */
    public PlayEntryDao getPlayEntryDao(int flag) {
        switch (flag) {
            case READ_ONLY:
                return getSession(getReadableDatabase()).getPlayEntryDao();

            case WRITE_ONLY:
                return getSession(getWritableDatabase()).getPlayEntryDao();

            default:
                return null;
        }

    }

    /**
     * getUrgentItemBeanDao
     *
     * @param flag
     */
    public UrgentItemBeanDao getUrgentItemBeanDao(int flag) {
        switch (flag) {
            case READ_ONLY:
                return getSession(getReadableDatabase()).getUrgentItemBeanDao();

            case WRITE_ONLY:
                return getSession(getWritableDatabase()).getUrgentItemBeanDao();

            default:
                return null;
        }

    }

    /**
     * getSettingsDao
     *
     * @param flag
     */
    public SettingsDao getSettingsDao(int flag) {
        switch (flag) {
            case READ_ONLY:
                return getSession(getReadableDatabase()).getSettingsDao();

            case WRITE_ONLY:
                return getSession(getWritableDatabase()).getSettingsDao();

            default:
                return null;
        }

    }

    public DeviceEntityDao getDeviceEntityDao(int flag) {
        switch (flag) {
            case READ_ONLY:
                return getSession(getReadableDatabase()).getDeviceEntityDao();

            case WRITE_ONLY:
                return getSession(getWritableDatabase()).getDeviceEntityDao();

            default:
                return null;
        }
    }

    public InterCutDataDao getInterCutDataDao(int flag) {
        switch (flag) {
            case READ_ONLY:
                return getSession(getReadableDatabase()).getInterCutDataDao();

            case WRITE_ONLY:
                return getSession(getWritableDatabase()).getInterCutDataDao();

            default:
                return null;
        }
    }

    private DaoSession getSession(SQLiteDatabase db) {
        daoMaster = new DaoMaster(db);
        return daoMaster.newSession();
    }

    /**
     * 插入一条记录
     *
     * @param entity
     * @param writedao
     */

    public Long insert(T entity, AbstractDao<T, Long> writedao) {

        return writedao.insert(entity);
    }

    /**
     * 插入集合
     *
     * @param entities
     * @param writedao
     */
    public void insertList(Iterable<T> entities, AbstractDao<T, Long> writedao) {

        if (entities == null) {
            return;
        }
        Log.e("dy", "" + entities);
        writedao.insertInTx(entities);
    }

    /**
     * 根据id删除一条记录
     *
     * @param id
     * @param writedao
     */
    public void deleteById(Long id, AbstractDao<T, Long> writedao) {
        writedao.deleteByKey(id);
    }

    /**
     * 根据entity删除一条记录
     *
     * @param entity
     * @param writedao
     */
    public void delete(T entity, AbstractDao<T, Long> writedao) {

        writedao.delete(entity);
    }

    /**
     * 删除一个集合
     *
     * @param entities
     * @param writedao
     */
    public void deleteList(Iterable<T> entities, AbstractDao<T, Long> writedao) {
        writedao.deleteInTx(entities);

    }

    /**
     * 删除所有记录
     *
     * @param writedao
     */

    public void deleteAll(AbstractDao<T, Long> writedao) {
        writedao.deleteAll();

    }

    /**
     * 更新一条记录
     *
     * @param writedao
     * @param entity
     */
    public void update(T entity, AbstractDao<T, Long> writedao) {
        writedao.update(entity);
    }

    /**
     * 更新一个集合
     *
     * @param writedao
     * @param entities
     */
    public void updateList(Iterable<T> entities, AbstractDao<T, Long> writedao) {
        writedao.updateInTx(entities);
    }

    public void rxUpdateList(Iterable<T> entities, AbstractDao<T, Long> writedao) {
        writedao.rx().updateInTx(entities);
    }

    /**
     * 查询所有
     *
     * @param readdao
     */
    public Observable<List<T>> queryAll1(AbstractDao<T, Long> readdao) {
        RxQuery<T> rxQuery = readdao.queryBuilder().rx();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        return rxQuery.list();
    }

    public List<T> queryAll(AbstractDao<T, Long> readdao) {
        QueryBuilder<T> rxQuery = readdao.queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        return rxQuery.list();
    }

    /**
     * 根据Id查询一条记录
     *
     * @param readdao
     */
    public T queryById(Long id, AbstractDao<T, Long> readdao) {
        T entity = readdao.load(id);
        return entity;
    }

    public Observable<T> rxQueryById(Long id, AbstractDao<T, Long> readdao) {
        return readdao.rx().load(id);
    }


    /**
     * 更新或者插入一个实体
     *
     * @param entity
     * @param readdao
     */
    public Long insertOrUpdate(T entity, AbstractDao<T, Long> readdao) {
        return readdao.insertOrReplace(entity);
    }

    public Observable<T> insertOrReplaceByRx(T entity, AbstractDao<T, Long> readdao) {
        return readdao.rx().insertOrReplace(entity);
    }


    /**
     * * 更新或者插入一个实体集合
     *
     * @param entities
     * @param readdao
     */
    public void insertOrUpdateList(Iterable<T> entities, AbstractDao<T, Long> readdao) {
        readdao.insertOrReplaceInTx(entities);
    }

    public List<T> queryBySQL(AbstractDao<T, Long> readdao, String SQL) {
        return readdao.queryRaw(SQL);
    }

    public List queryFlightNumberLike(String value) {
        QueryBuilder qb = getFlightInfoTempDao(READ_ONLY).queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        Date nowDate = new Date();
        Date beginTime = new Date(nowDate.getYear(), nowDate.getMonth(), nowDate.getDate(), 0, 0, 0);
        Calendar calendar = new GregorianCalendar();
        calendar.add(calendar.DATE, 6);//把日期往后增加一天.整数往后推,负数往前移动
        nowDate = calendar.getTime(); //这个时间就是日期往后推一天的结果
        nowDate.setHours(0);
        nowDate.setMinutes(0);
        nowDate.setSeconds(0);
        Date dateToSix = new Date(nowDate.getYear(), nowDate.getMonth(), nowDate.getDate(), 23, 59, 59);
        List list = qb.where(FlightInfoTempDao.Properties.Date.ge(beginTime), FlightInfoTempDao.Properties.Date.lt(dateToSix), FlightInfoTempDao.Properties.FlightNumber.like("%" + value + "%")).list();
        return list;
    }

    public List queryFlightLike(String value) {
        QueryBuilder qb = getFlightInfoTempDao(READ_ONLY).queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        List list = qb.whereOr(FlightInfoTempDao.Properties.FlightNumber.like("%" + value + "%"), FlightInfoTempDao.Properties.Date.like("%" + value + "%"), FlightInfoTempDao.Properties.FlightNumber.like("%" + value + "%")).list();
        Log.e("dy", "list  size = " + list.size());
        return list;
    }

    public List<T> queryInterCutDataByTY(int ty) {
        QueryBuilder qb = getInterCutDataDao(READ_ONLY).queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        List<T> list = qb.where(InterCutDataDao.Properties.Ty.eq(ty)).list();
        return list;
    }

    public List queryFlightTempNumberLike(String value) {
        QueryBuilder qb = getFlightInfoTempDao(READ_ONLY).queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        List list = qb.where(FlightInfoTempDao.Properties.FlightNumber.like("%" + value + "%")).list();
        Log.e("dy", "list  size = " + list.size());
        return list;
    }

    public List queryHisFlightNumberLike(String value) {
        QueryBuilder qb = getFlightInfoTempDao(READ_ONLY).queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        List list = qb.where(HistoryFlightTempEditDao.Properties.FlightInfoTempPid.like("%" + value + "%")).list();
        return list;
    }

    public List queryByHistoryFlightTemp(int pageNum) {
        QueryBuilder qb = getHistoryFlightTempEditDao(READ_ONLY).queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        List list = qb.offset(pageNum * 10).limit(10).orderDesc(HistoryFlightTempEditDao.Properties.EditDate).list();
        return list;
    }

    public HistoryFlightTempEdit queryHisFlightTempByflightInfoTempPid(long flightInfoTempPid) {
        QueryBuilder qb = getHistoryFlightTempEditDao(READ_ONLY).queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        List<HistoryFlightTempEdit> list = qb.where(HistoryFlightTempEditDao.Properties.FlightInfoTempPid.eq(flightInfoTempPid)).list();
        if (list.isEmpty() || list.size() == 0) {
            return null;
        }
        return list.get(0);
    }


    //航班分页
    public List queryFlightTempByPagenum(int pageNum) {
        QueryBuilder qb = getFlightInfoTempDao(READ_ONLY).queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        List list = qb.orderDesc(FlightInfoTempDao.Properties.Date).offset(30 * pageNum).limit(30).list();
        return list;
    }

    public Observable<List<FlightInfoTemp>> queryFlightTempPagenumByRx(int pageNum) {
        QueryBuilder qb = getFlightInfoTempDao(READ_ONLY).queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        qb.orderDesc(FlightInfoTempDao.Properties.Date).offset(30 * pageNum).limit(30).list();
        RxQuery<FlightInfoTemp> rxQuery = qb.rx();

        return rxQuery.list();
    }

    public FlightInfoTemp queryByFlightInfoPid(Long flightInfoPid) {
        QueryBuilder qb = getFlightInfoTempDao(READ_ONLY).queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        List<FlightInfoTemp> list = qb.where(FlightInfoTempDao.Properties.FlightInfoPid.eq(flightInfoPid)).list();
        if (list.isEmpty() || list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

    public void insertOrUpdate(DeviceEntity deviceEntity, long times, int status, boolean isBlack) {
        QueryBuilder qb1 = getDeviceEntityDao(READ_ONLY).queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;

        List<DeviceEntity> list = qb1.where(DeviceEntityDao.Properties.Mac.eq(deviceEntity.getMac())).list();
        if (list.size() >= 1) {
            DeviceEntity entity = list.get(0);
            Log.e("jw", "entity = " + entity.getIsblack());
            entity.setStatus(status);
            entity.setValues(times);
            entity.setIsblack(false);
            getDeviceEntityDao(WRITE_ONLY).update(entity);
        } else {
            deviceEntity.setValues(times);
            deviceEntity.setStatus(status);
            deviceEntity.setIsblack(isBlack);
            getDeviceEntityDao(WRITE_ONLY).insert(deviceEntity);
        }

    }

    public DeviceEntity queryDeviceEntityByMac(String mac) {
        QueryBuilder qb = getDeviceEntityDao(READ_ONLY).queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;

        List<DeviceEntity> list = qb.where(DeviceEntityDao.Properties.Mac.eq(mac)).list();
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    public DeviceEntity queryDeviceEntityByIP(String ip) {
        QueryBuilder qb = getDeviceEntityDao(READ_ONLY).queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;

        List<DeviceEntity> list = qb.where(DeviceEntityDao.Properties.Ip.eq(ip)).list();
        return list.get(0);
    }


    public List<DeviceEntity> queryDeviceEntityByTwo(boolean isBlack, int status) {
        QueryBuilder qb = getDeviceEntityDao(READ_ONLY).queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;

        List<DeviceEntity> list = qb.where(DeviceEntityDao.Properties.Isblack.eq(isBlack), DeviceEntityDao.Properties.Status.eq(status)).list();
        return list;
    }

    public List<DeviceEntity> queryDeviceEntityByStatus(int status) {
        QueryBuilder qb = getDeviceEntityDao(READ_ONLY).queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;

        List<DeviceEntity> list = qb.where(DeviceEntityDao.Properties.Status.eq(status)).list();
        return list;
    }


    public List<DeviceEntity> queryDeviceEntityByBlack(boolean isBlack) {
        QueryBuilder qb = getDeviceEntityDao(READ_ONLY).queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;

        List<DeviceEntity> list = qb.where(DeviceEntityDao.Properties.Isblack.eq(isBlack)).list();
        return list;
    }

    public List<PlayEntry> queryIsTemporary() {
        QueryBuilder qb = getPlayEntryDao(READ_ONLY).queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        List<PlayEntry> list = qb.where(PlayEntryDao.Properties.IsTemporary.eq(true)).list();
        return list;
    }

    public List<FlightInfo> queryFlightInfoByFlightNumberAndDate(Date date, String flightNumber) {
        QueryBuilder qb = getFlightInfoDao(READ_ONLY).queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        List<FlightInfo> list = qb.where(FlightInfoDao.Properties.FlightNumber.eq(flightNumber), FlightInfoDao.Properties.Date.eq(date)).list();
        return list;
    }

    public List<FlightInfoTemp> queryFlightInfoTempByFlightNumberAndDate(Date date, String flightNumber) {
        QueryBuilder qb = getFlightInfoDao(READ_ONLY).queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        List<FlightInfoTemp> list = qb.where(FlightInfoDao.Properties.FlightNumber.eq(flightNumber), FlightInfoDao.Properties.Date.eq(date)).list();
        return list;
    }

    public List queryPlayEntryByFlightInfoTempId(Long flightInfoTempId) {
        QueryBuilder qb = getPlayEntryDao(READ_ONLY).queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        List<PlayEntry> list = qb.where(PlayEntryDao.Properties.PlayEntryId.eq(flightInfoTempId)).list();
        return list;
    }

    public PlayEntry queryPlayEntryByFlightInfoTempId2(Long flightInfoTempId) {
        QueryBuilder qb = getPlayEntryDao(READ_ONLY).queryBuilder();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        List<PlayEntry> list = qb.where(PlayEntryDao.Properties.PlayEntryId.eq(flightInfoTempId)).list();
        if (list.isEmpty() || list.size() < 3) {
            return null;
        }
        return list.get(1);
    }
}
