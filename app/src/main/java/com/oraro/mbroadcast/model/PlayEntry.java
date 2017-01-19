package com.oraro.mbroadcast.model;

import com.oraro.mbroadcast.dao.DaoSession;
import com.oraro.mbroadcast.dao.FlightInfoTempDao;
import com.oraro.mbroadcast.dao.PlayEntryDao;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by dongyu on 2016/8/10 0010.
 */
@Entity
public class PlayEntry implements Serializable {
    @Id(autoincrement = true)
    private Long id;
    private Long playEntryId;
    private Date time;
    private int times;
    private int doTimes;
    private int isQueue;
    private String fileName;
    private String fileSuffix;
    private String fileParentPath;
    private String textDesc;
    private int xmlKey;
    private Boolean isTemporary = false;
    private  int  isEmeng = 0;
    @ToOne(joinProperty = "playEntryId")
    private FlightInfoTemp flightInfoTemp;
    @Generated(hash = 473183950)
    private transient Long flightInfoTemp__resolvedKey;
    /** Used for active entity operations. */
    @Generated(hash = 106530353)
    private transient PlayEntryDao myDao;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;


    @Generated(hash = 1142819517)
    public PlayEntry(Long id, Long playEntryId, Date time, int times, int doTimes,
            int isQueue, String fileName, String fileSuffix, String fileParentPath,
            String textDesc, int xmlKey, Boolean isTemporary, int isEmeng) {
        this.id = id;
        this.playEntryId = playEntryId;
        this.time = time;
        this.times = times;
        this.doTimes = doTimes;
        this.isQueue = isQueue;
        this.fileName = fileName;
        this.fileSuffix = fileSuffix;
        this.fileParentPath = fileParentPath;
        this.textDesc = textDesc;
        this.xmlKey = xmlKey;
        this.isTemporary = isTemporary;
        this.isEmeng = isEmeng;
    }


    @Generated(hash = 2004035168)
    public PlayEntry() {
    }


    @Override
    public String toString() {
        return "PlayEntry{" +
                "id=" + id +
                ", playEntryId=" + playEntryId +
                ", time=" + time +
                ", times=" + times +
                ", doTimes=" + doTimes +
                ", isQueue=" + isQueue +
                ", fileName='" + fileName + '\'' +
                ", fileSuffix='" + fileSuffix + '\'' +
                ", fileParentPath='" + fileParentPath + '\'' +
                ", textDesc='" + textDesc + '\'' +
                ", xmlKey=" + xmlKey +
                ", isTemporary=" + isTemporary +
                ", flightInfoTemp=" + flightInfoTemp +
                ", flightInfoTemp__resolvedKey=" + flightInfoTemp__resolvedKey +
                ", myDao=" + myDao +
                ", daoSession=" + daoSession +
                '}';
    }


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
    @Generated(hash = 1762849195)
    public void setFlightInfoTemp(FlightInfoTemp flightInfoTemp) {
        synchronized (this) {
            this.flightInfoTemp = flightInfoTemp;
            playEntryId = flightInfoTemp == null ? null : flightInfoTemp.getId();
            flightInfoTemp__resolvedKey = playEntryId;
        }
    }


    /** To-one relationship, resolved on first access. */
    @Generated(hash = 569395313)
    public FlightInfoTemp getFlightInfoTemp() {
        Long __key = this.playEntryId;
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


    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 494021071)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPlayEntryDao() : null;
    }


    public int getIsEmeng() {
        return this.isEmeng;
    }


    public void setIsEmeng(int isEmeng) {
        this.isEmeng = isEmeng;
    }


    public Boolean getIsTemporary() {
        return this.isTemporary;
    }


    public void setIsTemporary(Boolean isTemporary) {
        this.isTemporary = isTemporary;
    }


    public int getXmlKey() {
        return this.xmlKey;
    }


    public void setXmlKey(int xmlKey) {
        this.xmlKey = xmlKey;
    }


    public String getTextDesc() {
        return this.textDesc;
    }


    public void setTextDesc(String textDesc) {
        this.textDesc = textDesc;
    }


    public String getFileParentPath() {
        return this.fileParentPath;
    }


    public void setFileParentPath(String fileParentPath) {
        this.fileParentPath = fileParentPath;
    }


    public String getFileSuffix() {
        return this.fileSuffix;
    }


    public void setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }


    public String getFileName() {
        return this.fileName;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public int getIsQueue() {
        return this.isQueue;
    }


    public void setIsQueue(int isQueue) {
        this.isQueue = isQueue;
    }


    public int getDoTimes() {
        return this.doTimes;
    }


    public void setDoTimes(int doTimes) {
        this.doTimes = doTimes;
    }


    public int getTimes() {
        return this.times;
    }


    public void setTimes(int times) {
        this.times = times;
    }


    public Date getTime() {
        return this.time;
    }


    public void setTime(Date time) {
        this.time = time;
    }


    public Long getPlayEntryId() {
        return this.playEntryId;
    }


    public void setPlayEntryId(Long playEntryId) {
        this.playEntryId = playEntryId;
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }

}
