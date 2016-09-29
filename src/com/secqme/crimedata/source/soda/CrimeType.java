package com.secqme.crimedata.source.soda;

import com.secqme.crimedata.domain.dao.CrimeTypeDAO;
import com.secqme.crimedata.domain.model.CrimeTypeVO;
import com.secqme.util.cache.CacheUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Edmund on 3/23/15.
 */
public class CrimeType {
    private CrimeTypeDAO crimeTypeDAO;
    private Map<String,Double> crimeTypeWeight;
    private Map<String,CrimeTypeVO> crimeTypes;
    private CacheUtil cacheUtil;
    private static final String CRIME_TYPE_WEIGHT_CACHE = "crimeTypeWeight";
    private static final String CRIME_TYPE_CACHE = "crimeType";
    public CrimeType(CrimeTypeDAO crimeTypeDAO, CacheUtil cacheUtil) {
        this.crimeTypeDAO = crimeTypeDAO;
        this.cacheUtil = cacheUtil;
//        initCrimeTypeWeight();
        initCrimeType();
    }

    private void initCrimeTypeWeight(){
        System.out.println("initCrimeTypeWeight");
        crimeTypeWeight = (Map<String,Double>) cacheUtil.getCachedObject(CRIME_TYPE_WEIGHT_CACHE,HashMap.class);
        if(crimeTypes == null || crimeTypes.isEmpty()) {
            List<CrimeTypeVO> crimeTypeVOs = crimeTypeDAO.findAll();
            crimeTypeWeight = new HashMap<String, Double>();
            for (CrimeTypeVO crime : crimeTypeVOs) {
                crimeTypeWeight.put(crime.getName(), crime.getCrimeWeight());
            }
            cacheUtil.storeObjectIntoCache(CRIME_TYPE_WEIGHT_CACHE, crimeTypes);
        }

    }

    public Double getCrimeWeight(String crimeType){
        if(crimeTypeWeight.isEmpty() || crimeTypeWeight == null) {
            initCrimeTypeWeight();
        }
        return crimeTypeWeight.get(crimeType) == null ? 0.0 : crimeTypeWeight.get(crimeType);
    }

    private void initCrimeType(){
        System.out.println("initCrimeType");
        crimeTypes = (Map<String, CrimeTypeVO>) cacheUtil.getCachedObject(CRIME_TYPE_CACHE,HashMap.class);
        if(crimeTypes == null || crimeTypes.isEmpty()){
            List<CrimeTypeVO> crimeTypeVOs = crimeTypeDAO.findAll();
            crimeTypes = new HashMap<String, CrimeTypeVO>();
            for(CrimeTypeVO crimeTypeVO : crimeTypeVOs){
                crimeTypes.put(crimeTypeVO.getName(),crimeTypeVO);
            }
            cacheUtil.storeObjectIntoCache(CRIME_TYPE_CACHE,crimeTypes);
        }else{
            System.out.println("crimeType exist in dynamodb");
        }

    }

    public CrimeTypeVO getCrimeType(String crimeType){
        if(crimeTypes.isEmpty() || crimeTypes == null){
            initCrimeType();
        }
        String tempCrimeType = crimeType.replaceFirst("-"," ").replaceFirst(","," ");
        String arr[] = tempCrimeType.split(" ", 2);
        if(!crimeType.contains("TRAFFIC DR #")) {
            if (crimeTypes.get(arr[0]) != null) {
                return crimeTypes.get(arr[0]);
            } else {
                int distance = 0;
                CrimeTypeVO resultCrimeType = null;
                for (CrimeTypeVO crime : crimeTypes.values()) {
                    if (distance == 0) {
                        if(LevenshteinDistance.computeLevenshteinDistance(crime.getName(), crimeType) < 8) {
                            distance = LevenshteinDistance.computeLevenshteinDistance(crime.getName(), crimeType);
                            resultCrimeType = crime;
                        }
                    } else {
                        int temp = LevenshteinDistance.computeLevenshteinDistance(crime.getName(), crimeType);
                        if (temp < distance && temp < 5) {
                            distance = temp;
                            resultCrimeType = crime;
                        }
                    }

                }

                return resultCrimeType == null ? crimeTypes.get("Remark") : resultCrimeType;
            }
        }else{
            return crimeTypes.get("Remark");
        }
    }


}
