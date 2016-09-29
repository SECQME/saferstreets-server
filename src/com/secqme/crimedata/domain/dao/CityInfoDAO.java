package com.secqme.crimedata.domain.dao;

import com.secqme.crimedata.domain.dao.jpa.JPAParameter;
import com.secqme.crimedata.domain.model.CityInfo;

import java.util.List;

/**
 * User: James Khoo
 * Date: 10/9/14
 * Time: 4:48 PM
 */
public interface CityInfoDAO extends BaseDAO<CityInfo, Long> {
    public List<CityInfo> findAll();
    public CityInfo findByCity(String city);
    public CityInfo findByCountryStateCity(String country, String state, String city);
    public CityInfo findByLocation(double latitude, double longitude);
}
