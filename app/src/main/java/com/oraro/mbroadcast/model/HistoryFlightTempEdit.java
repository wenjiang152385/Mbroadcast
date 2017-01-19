package com.oraro.mbroadcast.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;
import com.oraro.mbroadcast.dao.DaoSession;
import com.oraro.mbroadcast.dao.HistoryFlightTempEditDao;
import com.oraro.mbroadcast.dao.FlightInfoTempDao;

/**
 * Created by dongyu on 2016/9/23 0023.
 */
@Entity
public class HistoryFlightTempEdit {
    @Id(autoincrement = true)
    private  Long id;

    private  Long flightInfoTempPid;

    private Date editDate;

    @ToOne(joinProperty = "flightInfoTempPid")
    private FlightInfoTemp flightInfoTemp;

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 344267568)
    public void setFlightInfoTemp(FlightInfoTemp flightInfoTemp) {
        synchronized (this) {
            this.flightInfoTemp = flightInfoTemp;
            flightInfoTempPid = flightInfoTemp == null ? null : flightInfoTemp
                    .getId();
            flightInfoTemp__resolvedKey = flightInfoTempPid;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1217452326)
    public FlightInfoTemp getFlightInfoTemp() {
        Long __key = this.flightInfoTempPid;
        if (flightInfoTemp__resolvedKey == null
                || !flightInfoTemp__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            FlightInfoTempDao targetDao = daoSession.getFlightInfoTempDao();
            FlightInfoTemp flightInfoTempNew = targetDao.load(__key);
            synchronized (this) {
                flightInfoTemp = flightInfoTempNew;
                flightInfoTemp__resolvedKey = __key;
            }
        }
        return flightInfoTemp;
    }

    @Generated(hash = 473183950)
    private transient Long flightInfoTemp__resolvedKey;

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 746463501)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getHistoryFlightTempEditDao()
                : null;
    }

    /** Used for active entity operations. */
    @Generated(hash = 2004233456)
    private transient HistoryFlightTempEditDao myDao;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    public Date getEditDate() {
        return this.editDate;
    }

    public void setEditDate(Date editDate) {
        this.editDate = editDate;
    }

    public Long getFlightInfoTempPid() {
        return this.flightInfoTempPid;
    }

    public void setFlightInfoTempPid(Long flightInfoTempPid) {
        this.flightInfoTempPid = flightInfoTempPid;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 1430195003)
    public HistoryFlightTempEdit(Long id, Long flightInfoTempPid, Date editDate) {
        this.id = id;
        this.flightInfoTempPid = flightInfoTempPid;
        this.editDate = editDate;
    }

    @Generated(hash = 2124305368)
    public HistoryFlightTempEdit() {
    }

}
