package com.secqme.crimedata.domain.dao.jpa;

import com.secqme.crimedata.domain.dao.CityInfoDAO;
import com.secqme.crimedata.domain.model.CityInfo;

import java.util.List;

/**
 * User: James Khoo
 * Date: 10/9/14
 * Time: 4:49 PM
 */
public class CityInfoJPADAO extends BaseJPADAO<CityInfo, Long> implements CityInfoDAO {

    public CityInfoJPADAO() {
        super(CityInfo.class);
    }

    public List<CityInfo> findAll() {
        return this.executeQueryWithResultList(CityInfo.QUERY_FIND_ALL);
    }

    @Override
    public CityInfo findByCity(String city) {
        JPAParameter jpaParameter = new JPAParameter()
                .setParameter("city", city);
        return this.executeQueryWithSingleResult(CityInfo.QUERY_FIND_BY_CITY, jpaParameter);
    }

    @Override
    public CityInfo findByCountryStateCity(String country, String state, String city) {
        JPAParameter jpaParameter = new JPAParameter()
                .setParameter("country", country)
                .setParameter("state", state)
                .setParameter("city", city);
        return this.executeQueryWithSingleResult(CityInfo.QUERY_FIND_BY_COUNTRY_STATE_CITY, jpaParameter);
    }

    @Override
    public CityInfo findByLocation(double latitude, double longitude) {
        JPAParameter jpaParameter = new JPAParameter()
                .setParameter("latitude", latitude)
                .setParameter("longitude", longitude);
        List<CityInfo> cities = this.executeQueryWithResultList(CityInfo.QUERY_FIND_BY_LOCATION, jpaParameter);
        if (!cities.isEmpty()) {
            if (cities.size() > 1) {
                myLog.warn(String.format("There are multiple cities in (%f, %f): %s", latitude, longitude, cities));
            }
            return cities.get(0);
        }
        return null;
    }
}
