package com.secqme.crimedata.manager;

import com.secqme.crimedata.CoreException;
import com.secqme.crimedata.domain.dao.CityInfoDAO;
import com.secqme.crimedata.domain.dao.CrimeDataDAO;
import com.secqme.crimedata.domain.dao.CrimeTypeDAO;
import com.secqme.crimedata.domain.model.*;
import com.secqme.crimedata.rs.CommonJSONKey;
import com.secqme.util.cache.CacheKey;
import com.secqme.util.cache.CacheUtil;
import com.secqme.util.location.LocationUtil;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.Cache;
import java.util.*;

/**
 * Created by Edmund on 7/1/15.
 */
public class DefaultDataManager implements DataManager {

    private final static Logger myLog = Logger.getLogger(DefaultDataManager.class);
    private HashMap<String, CityInfo> cityHashMap;
    private CityInfoDAO cityInfoDAO;
    private CrimeTypeDAO crimeTypeDAO;
    private CrimeDataDAO crimeDataDAO;
    private LocationUtil locationUtil;
    private CacheUtil cacheUtil;

    public DefaultDataManager(CityInfoDAO cityInfoDAO,CrimeTypeDAO crimeTypeDAO, CrimeDataDAO crimeDataDAO,
                              LocationUtil locationUtil,CacheUtil cacheUtil) {
        this.cityInfoDAO = cityInfoDAO;
        this.crimeTypeDAO = crimeTypeDAO;
        this.crimeDataDAO = crimeDataDAO;
        this.locationUtil = locationUtil;
        this.cacheUtil = cacheUtil;
        init();
    }

    @Override
    public void updateCityNeighbour(JSONObject jObj) throws CoreException {
        myLog.debug("updateCityNeighbour");
        try {
            if (jObj.has(CommonJSONKey.STREET_CITY)) {
               CityInfo city = cityHashMap.get(jObj.getString(CommonJSONKey.STREET_CITY));
                if(city != null) {
                    if (jObj.has(CommonJSONKey.JSONOBJECT_CITY_NEIGHBOUR)) {
                        JSONArray cities = jObj.getJSONArray(CommonJSONKey.JSONOBJECT_CITY_NEIGHBOUR);
                        myLog.debug(cities.toString());
                        city.setNeighbour(cities.toString());
                        cityInfoDAO.update(city);
                    }
                }
            }
        }catch(JSONException ex){
            ex.printStackTrace();
        }

    }

    private void init() {
        cityHashMap = new HashMap<String, CityInfo>();
        List<CityInfo> cityList = cityInfoDAO.findAll();
        for (CityInfo cityInfo : cityList) {
            cityHashMap.put(cityInfo.getName(), cityInfo);
        }
        myLog.debug("Total " + cityList.size() + " City Initiated");
    }

    @Override
    public List<CrimeTypeVO> getAllCrimeType() {
        return crimeTypeDAO.findAll();
    }

    @Override
    public JSONObject updateCrimeDayTime() {
        JSONObject result = new JSONObject();
        List<CrimeDataVO> crimeList ;

        try {
            int count = 0;
            int countId = 2138524;
            do {
                crimeList = crimeDataDAO.findAllByBatch(countId, countId+99999);
                myLog.debug("size : " + crimeList.size());
                for (CrimeDataVO crime : crimeList) {
//                    if (crime.getCrimeDayTime() == null) {
                        crime.setCrimeDayTime(locationUtil.getCrimeDayTime(crime.getCrimeDate()));
                        crimeDataDAO.update(crime);
                        count += 1;
//                    }
                }
                countId += 100000;
                myLog.debug("count :" +count);
                myLog.debug("countId :" +countId);
            }while(crimeList.size() > 0);
            result.put("Total Updated: ", count);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return result;
    }
}
