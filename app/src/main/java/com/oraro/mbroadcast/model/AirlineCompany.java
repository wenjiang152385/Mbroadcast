package com.oraro.mbroadcast.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.oraro.mbroadcast.dao.DaoSession;
import com.oraro.mbroadcast.dao.AirlineCompanyDao;
import com.oraro.mbroadcast.dao.FlightInfoDao;


/**
 * Created by dongyu on 2016/8/9 0009.
 */
@Entity
public class AirlineCompany {
    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "name")
    private String airlineCompanyName;
    public String getAirlineCompanyName() {
        return this.airlineCompanyName;
    }
    public void setAirlineCompanyName(String airlineCompanyName) {
        this.airlineCompanyName = airlineCompanyName;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 1398682444)
    public AirlineCompany(Long id, String airlineCompanyName) {
        this.id = id;
        this.airlineCompanyName = airlineCompanyName;
    }
    @Generated(hash = 264543327)
    public AirlineCompany() {
    }

}
