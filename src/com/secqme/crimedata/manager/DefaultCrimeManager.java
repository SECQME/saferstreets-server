package com.secqme.crimedata.manager;


import com.secqme.crimedata.CoreException;
import com.secqme.crimedata.domain.dao.*;
import com.secqme.crimedata.domain.model.*;
import com.secqme.crimedata.rs.CommonJSONKey;
import com.secqme.crimedata.source.GenerateCrimeMatrix;
import com.secqme.util.cache.CacheKey;
import com.secqme.util.cache.CacheUtil;
import com.secqme.util.location.LocationUtil;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.Cache;
import java.io.IOException;
import java.util.*;

/**
 * Created by Edmund on 9/22/15.
 */
public class DefaultCrimeManager implements CrimeManager {
    private static final Logger myLog = Logger.getLogger(DefaultCrimeManager.class);

    private static List<String> defaultCrimeTypeList;
    private CrimeDataDAO crimeDataDAO;
    private CityInfoDAO cityInfoDAO;
    private CrimeStreetsRatingDAO crimeStreetsRatingDAO;
    private UserStreetSafetyRatingDAO userStreetSafetyRatingDAO;
    private SaferStreetsRequestDAO saferStreetsRequestDAO;
    private SubscriptionNotificationRequestDAO subscriptionNotificationRequestDAO;
    private HashMap<String, CityInfo> cityHashMap = null;
    private Integer crimeReportIntervalInDays = 14;
    private Double crimeCellDistanceInKM = 0.5;
    private CacheUtil cacheUtil = null;
    private LocationUtil locationUtil = null;
    private CrimeTypeDAO crimeTypeDAO;
    private Map<String,Double> crimeTypeWithWeight;
    private ObjectMapper mapper = null;


    public DefaultCrimeManager(CrimeDataDAO dao, CityInfoDAO cityInfoDAO, UserStreetSafetyRatingDAO userStreetSafetyRatingDAO,
                                CrimeStreetsRatingDAO crimeStreetsRatingDAO, SaferStreetsRequestDAO saferStreetsRequestDAO, SubscriptionNotificationRequestDAO subscriptionNotificationRequestDAO,
                                CacheUtil cacheUtil, LocationUtil locUtil,
                                Integer crimeReportInterval, Double crimeCellDistanceInKM,
                                CrimeTypeDAO crimeTypeDAO) {
        this.crimeDataDAO = dao;
        this.cityInfoDAO = cityInfoDAO;
        this.crimeStreetsRatingDAO = crimeStreetsRatingDAO;
        this.userStreetSafetyRatingDAO = userStreetSafetyRatingDAO;
        this.saferStreetsRequestDAO = saferStreetsRequestDAO;
        this.subscriptionNotificationRequestDAO = subscriptionNotificationRequestDAO;
        this.crimeReportIntervalInDays = crimeReportInterval;
        this.crimeCellDistanceInKM = crimeCellDistanceInKM;
        this.cacheUtil = cacheUtil;
        this.locationUtil = locUtil;
        this.crimeTypeDAO = crimeTypeDAO;
        init();
    }
    private void init(){
        cityHashMap = new HashMap<String, CityInfo>();
        List<CityInfo> cityList = cityInfoDAO.findAll();
        for (CityInfo cityInfo : cityList) {
            cityHashMap.put(cityInfo.getName().replace(" ","_").toLowerCase(), cityInfo);
        }
        myLog.debug("Total " + cityList.size() + " City Initiated");

        defaultCrimeTypeList = new ArrayList<String>();
        crimeTypeWithWeight = new HashMap<String, Double>();
        List<CrimeTypeVO> crimeTypeList = crimeTypeDAO.findAll();
        mapper = new ObjectMapper();
        for(CrimeTypeVO crimeType : crimeTypeList){
            defaultCrimeTypeList.add(crimeType.getName());
            crimeTypeWithWeight.put(crimeType.getName(),crimeType.getCrimeWeight());
        }
    }

    @Override
    public List<CrimeDataVO> getDefaultCrimeDataList(String cityName) {
        CrimeDataVO latestCrime = this.getLatestCrimeDataVO(cityName);
        if(latestCrime != null){
            Date reportEndDate = latestCrime.getCrimeDate();
            Date reportStartDate = DateUtils.addDays(reportEndDate, -crimeReportIntervalInDays);
            myLog.debug("latest date:" + latestCrime.getCrimeDate());
            myLog.debug("StartDate: " + reportStartDate);
            return getCrimeDataList(cityName, reportStartDate, reportEndDate, defaultCrimeTypeList);
        }else{
            return null;
        }
    }


    @Override
    public List<CrimeDataVO> getCrimeDataList(String cityName, Date startDate, Date endDate, List<String> crimeTypes) {
        if (endDate == null) {
            endDate = new Date();
        }
        return crimeDataDAO.findCrimeFromCityInBetweenDate(cityName, startDate, endDate);
    }

    @Override
    public List<UserStreetSafetyRatingVO> getUserStreetsRating(String cityName) {
        return userStreetSafetyRatingDAO.getUserRatingByCity(cityName);
    }

    @Override
    public void generateCityCrimeMatrix(String cityName ,boolean oldReport) {
        myLog.debug("Generating City Crime Matrix for " + cityName);
//        if(!isCrimeMatrixCache(cityName.replace(" ","_").toLowerCase())){
            CityInfo cityInfo = cityHashMap.get(cityName.replace(" ","_").toLowerCase());
            if(cityInfo != null){
                List<CrimeDataVO> crimeList;
                List<UserStreetSafetyRatingVO> ratingList = this.getUserStreetsRating(cityName);
                if(oldReport){
                    CrimeDataVO latestCrime = this.getLatestCrimeDataVO(cityName);
                    if(latestCrime != null) {
                        Date reportEndDate = DateUtils.addDays(latestCrime.getCrimeDate(), -14);
                        Date reportStartDate = DateUtils.addDays(reportEndDate, -crimeReportIntervalInDays);
                        myLog.debug("latest Date : " + latestCrime.getCrimeDate());
                        myLog.debug("reportStartDate : " + reportStartDate);
                        crimeList =  getCrimeDataList(cityName, reportStartDate, reportEndDate, defaultCrimeTypeList);
                        new GenerateCrimeMatrix(cityInfo, crimeList, ratingList, crimeCellDistanceInKM, locationUtil, cacheUtil,true);
                    }
                }else {
                    crimeList = this.getDefaultCrimeDataList(cityName);
                    if (crimeList != null) {
                        new GenerateCrimeMatrix(cityInfo, crimeList, ratingList, crimeCellDistanceInKM, locationUtil, cacheUtil,false);
                        myLog.debug("CrimeMatrix is generated");
                    } else {
                        myLog.debug("Crime List is null");
                    }
                }
            }else{
                myLog.debug("cityInfo is null");
            }
//        }else{
//            myLog.debug("Crime Matrix feature exist");
//        }
    }

    @Override
    public SafetyReport getSafetyReport(String cityName, Integer rowIndex, Integer colIndex) throws CoreException {
        SafetyRating safetyRating;
        SafetyReport safetyReport;
        List<CrimeDataSimple> crimeWithinTheAreaList = new ArrayList<CrimeDataSimple>();
        CityGridFeatures cityGridFeatures = this.getCityGridFeatures(cityName);
        if(cityGridFeatures == null){
            this.generateCityCrimeMatrix(cityName,false);
            cityGridFeatures = this.getCityGridFeatures(cityName);
        }
        int weightCount = 0;
        CityGridHolder cityGridHolder = this.getCityGridHolder(cityName,rowIndex,colIndex);
        List<CrimeDataSimple> crimeList = this.sortCrimeList(cityGridHolder,cityName);
        if(crimeList != null && crimeList.size() > 0){
            for(CrimeDataSimple crime : crimeList){
                crimeWithinTheAreaList.add(crime);
                weightCount += crime.getCrimeWeight();
            }
            if (weightCount >= cityGridFeatures.getAverageCrimePerCell()) {
                safetyRating = SafetyRating.LOW_SAFETY;
            } else if (weightCount > 0 &&
                    crimeWithinTheAreaList.size() < cityGridFeatures.getAverageCrimePerCell()) {
                safetyRating = SafetyRating.MODERATE;
            } else {
                safetyRating = SafetyRating.MODERATELY_SAFE;
            }
        }else{
//                    myLog.debug("CrimeData List is null / less than 1 therefore set to MODERATELY SAFE");
            safetyRating = SafetyRating.MODERATELY_SAFE;

        }
        safetyReport = new SafetyReport(rowIndex, colIndex, crimeCellDistanceInKM * 1000,
                safetyRating, crimeWithinTheAreaList,
                cityGridFeatures.getCrimeReportStartDate(), cityGridFeatures.getCrimeReportEndDate(),
                cityGridFeatures.getAverageCrimePerCell());


        return safetyReport;
    }

    @Override
    public SafetyReport getSafetyReportByDayTime(String cityName,Integer rowIndex, Integer colIndex,CrimeDayTime crimeDayTime) {
        SafetyReport safetyReport = null;
        SafetyRating safetyRating = null;
        int weightCount = 0;
        List<CrimeDataSimple> crimeList = null;
        CityGridFeatures cityGridFeatures = this.getCityGridFeatures(cityName);
        if(cityGridFeatures == null){
            this.generateCityCrimeMatrix(cityName,false);
            cityGridFeatures = this.getCityGridFeatures(cityName);
        }
        CityGridHolder cityGridHolder = this.getCityGridHolder(cityName,rowIndex,colIndex);
        if(cityGridHolder != null) {
//            myLog.debug(crimeDayTime);
            if (crimeDayTime.equals(CrimeDayTime.DAYTIME)) {
                crimeList = cityGridHolder.getDaytimeReport();
            }  else {
                crimeList = cityGridHolder.getDarkReport();
            }
            if (crimeList != null) {
                List<CrimeDataSimple> crimeWithinTheAreaList = new ArrayList<CrimeDataSimple>();
                for (CrimeDataSimple crime : crimeList) {
                    crimeWithinTheAreaList.add(crime);
                    weightCount += crime.getCrimeWeight();
                }
                if (weightCount >= cityGridFeatures.getAverageCrimePerCell()) {
                    safetyRating = SafetyRating.LOW_SAFETY;
                } else if (weightCount > 0 &&
                        crimeWithinTheAreaList.size() < cityGridFeatures.getAverageCrimePerCell()) {
                    safetyRating = SafetyRating.MODERATE;
                } else {
                    safetyRating = SafetyRating.MODERATELY_SAFE;
                }
                safetyReport = new SafetyReport(safetyRating,
                        cityGridFeatures.getCrimeReportStartDate(),
                        cityGridFeatures.getUserRatingEndDate(),
                        crimeList);
            } else {
                return null;
            }
        }else{
            return null;
        }
        return safetyReport;
    }

    @Override
    public int minorTweakOnUserLat(int row, int col, double latitude, double longitude, String cityName) {

        CityGridHolder grid1 = this.getCityGridHolder(cityName,row,col);
        CityGridHolder grid2 = this.getCityGridHolder(cityName,row - 1,col);
        if(grid1 != null && grid2 != null) {
            CrimeCoordinate coord1 = grid1.getCrimeCoordinate();
            CrimeCoordinate coord2 = grid2.getCrimeCoordinate();
            double dist1 = locationUtil.distanceBetweenTwoLocation(latitude, longitude, coord1.getCenterLatitude(), coord1.getCenterLongitude());
            double dist2 = locationUtil.distanceBetweenTwoLocation(latitude, longitude, coord2.getCenterLatitude(), coord2.getCenterLongitude());

            if (dist2 < dist1) {
                row -= 1;
            }

        }
        return row;
    }

    @Override
    public boolean checkSubscriptionNotificationRequest(String email, String cityName, String state, String country) {
        return (subscriptionNotificationRequestDAO.findByUserEmailAndLocation(email, cityName, state, country) != null);
    }

    @Override
    public boolean addNewSubscriptionNotificationRequest(JSONObject jsonObject) {
        SubscriptionNotificationRequestVO newRequest = new SubscriptionNotificationRequestVO();
        boolean userRequested = false;
        try {
            if (jsonObject.has(CommonJSONKey.STREET_CITY) && jsonObject.has(CommonJSONKey.STREET_STATE) && jsonObject.has(CommonJSONKey.STREET_COUNTRY)) {
                newRequest.setCity(jsonObject.getString(CommonJSONKey.STREET_CITY));
                newRequest.setState(jsonObject.getString(CommonJSONKey.STREET_STATE));
                newRequest.setCountry(jsonObject.getString(CommonJSONKey.STREET_COUNTRY));
            } else {
//                throw new CoreException(ErrorType.CITY_PARAMETER_NOT_FOUND);
            }
            newRequest.setRequestTime(new Date());



            if (jsonObject.has(CommonJSONKey.USER_EMAIL)) {
                newRequest.setEmail(jsonObject.getString(CommonJSONKey.USER_EMAIL));
                userRequested = (subscriptionNotificationRequestDAO.findByUserEmailAndLocation(newRequest.getEmail(), newRequest.getCity(), newRequest.getState(), newRequest.getCountry()) != null);
                if (!userRequested) {
                    subscriptionNotificationRequestDAO.create(newRequest);
                    userRequested = true;
                }
            } else {
//                throw new CoreException(ErrorType.PARAMETER_USER_EMAIL_NOT_FOUND_EXCEPTION);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return userRequested;
    }

    @Override
    public boolean checkSaferStreetSupportRequest(String email, String userid, String cityName, String state, String country) {
        boolean userrequest = false;
        if(email != null && email.length() >0){
            userrequest = (saferStreetsRequestDAO.findByUserIdAndLocation(email, cityName, state, country) != null);
        }else if(userid != null && userid.length() > 0){
            userrequest = (saferStreetsRequestDAO.findByUserIdAndLocation(userid, cityName, state, country) != null);
        }else{
            myLog.debug("Email : " + email + " userid : " + userid);
        }
        return userrequest;
    }

    @Override
    public boolean addNewSaferStreetSupportRequest(JSONObject jsonObject) {
        SaferStreetsRequestVO newRequest = new SaferStreetsRequestVO();
        boolean userRequested = false;
        try {
            if (jsonObject.has(CommonJSONKey.STREET_CITY) && jsonObject.has(CommonJSONKey.STREET_STATE) && jsonObject.has(CommonJSONKey.STREET_COUNTRY)) {
                newRequest.setCity(jsonObject.getString(CommonJSONKey.STREET_CITY));
                newRequest.setState(jsonObject.getString(CommonJSONKey.STREET_STATE));
                newRequest.setCountry(jsonObject.getString(CommonJSONKey.STREET_COUNTRY));
            } else {
//                throw new CoreException(ErrorType.CITY_PARAMETER_NOT_FOUND);
            }
            newRequest.setPostcode(jsonObject.has(CommonJSONKey.STREET_POSTCODE) ? jsonObject.getString(CommonJSONKey.STREET_POSTCODE) : null);
            newRequest.setRequestTime(new Date());


            if (jsonObject.has(CommonJSONKey.USER_ID)) {
                newRequest.setUserid(jsonObject.has(CommonJSONKey.USER_ID) ? jsonObject.getString(CommonJSONKey.USER_ID) : null);

                userRequested = (saferStreetsRequestDAO.findByUserIdAndLocation(jsonObject.getString(CommonJSONKey.USER_ID), jsonObject.getString(CommonJSONKey.STREET_CITY), jsonObject.getString(CommonJSONKey.STREET_STATE), jsonObject.getString(CommonJSONKey.STREET_COUNTRY)) != null);
                if (!userRequested) {
                    saferStreetsRequestDAO.create(newRequest);
                    userRequested = true;
                }
            } else if (jsonObject.has(CommonJSONKey.USER_EMAIL) && jsonObject.has(CommonJSONKey.USER_NAME)) {
                newRequest.setEmail(jsonObject.has(CommonJSONKey.USER_EMAIL) ? jsonObject.getString(CommonJSONKey.USER_EMAIL) : null);
                newRequest.setName(jsonObject.has(CommonJSONKey.USER_NAME) ? jsonObject.getString(CommonJSONKey.USER_NAME) : null);

                List<SaferStreetsRequestVO> oldRequests = saferStreetsRequestDAO.findByUserEmail(newRequest.getEmail());
                if (oldRequests == null || oldRequests.isEmpty()) {
                    saferStreetsRequestDAO.create(newRequest);
                }

                userRequested = (saferStreetsRequestDAO.findByUserEmailAndLocation(jsonObject.getString(CommonJSONKey.USER_EMAIL), jsonObject.getString(CommonJSONKey.STREET_CITY), jsonObject.getString(CommonJSONKey.STREET_STATE), jsonObject.getString(CommonJSONKey.STREET_COUNTRY)) != null);
            } else {
//                throw new CoreException(ErrorType.PARAMETER_USERID_NOT_FOUND_EXCEPTION);
            }
        }catch(JSONException ex){
            ex.printStackTrace();
        }
        return userRequested;
    }

    @Override
    public boolean addNewUserStreetSafetyRating(JSONObject jsonObject) {
        UserStreetSafetyRatingVO newRating = new UserStreetSafetyRatingVO();
        double latitude = 0.0;
        double longitude = 0.0;
        String cityName,state,country,streetName;
        boolean result = false;
        try {
            JSONObject location = jsonObject.has(CommonJSONKey.JSONOBJECT_LOCATION) ? jsonObject.getJSONObject(CommonJSONKey.JSONOBJECT_LOCATION) : null;
            JSONObject address = jsonObject.has(CommonJSONKey.JSONOBJECT_ADDRESS) ? jsonObject.getJSONObject(CommonJSONKey.JSONOBJECT_ADDRESS) : null;
            if(location != null && address != null){
                if(cityHashMap.get(address.has(CommonJSONKey.STREET_CITY) ? address.getString(CommonJSONKey.STREET_CITY).replace(" ","_").toLowerCase() : null) != null) {

                    latitude = location.has(CommonJSONKey.USER_LATITUDE) ? location.getDouble(CommonJSONKey.USER_LATITUDE) : 0.00;
                    longitude = location.has(CommonJSONKey.USER_LONGITUDE) ? location.getDouble(CommonJSONKey.USER_LONGITUDE) : 0.00;
                    cityName = address.has(CommonJSONKey.STREET_CITY) ? address.getString(CommonJSONKey.STREET_CITY) : null;
                    state = address.has(CommonJSONKey.STREET_STATE) ? address.getString(CommonJSONKey.STREET_STATE) : null;
                    country = address.has(CommonJSONKey.STREET_COUNTRY) ? address.getString(CommonJSONKey.STREET_COUNTRY) : null;
                    streetName = address.has(CommonJSONKey.STREET_NAME) ? address.getString(CommonJSONKey.STREET_NAME) : null;

                    // Get Location
                    newRating.setLatitude(latitude);
                    newRating.setLongitude(longitude);
                    newRating.setAccuracy(location.has(CommonJSONKey.STREET_ACCURACY) ? location.getInt(CommonJSONKey.STREET_ACCURACY) : 0);

                    // Get Street's Details
                    newRating.setCrimeStreetsID(streetName);
                    newRating.setCity(cityName);
                    newRating.setState(state);
                    newRating.setCountry(country);
                    newRating.setPostcode(address.has(CommonJSONKey.STREET_POSTCODE) ? address.getString(CommonJSONKey.STREET_POSTCODE) : null);

                    // User's rating and time
                    newRating.setUserStreetSafetyRating(jsonObject.has(CommonJSONKey.USER_STREET_RATE) ? jsonObject.getInt(CommonJSONKey.USER_STREET_RATE) : 0);
                    newRating.setUserid(jsonObject.has(CommonJSONKey.USER_ID) ? jsonObject.getString(CommonJSONKey.USER_ID) : null);
                    newRating.setUserRatingTime(new Date());
                    userStreetSafetyRatingDAO.create(newRating);
                    CityGridFeatures cityGridFeatures = this.getCityGridFeatures(cityName);
                    if(cityGridFeatures == null){
                        this.generateCityCrimeMatrix(cityName,false);
                        cityGridFeatures = this.getCityGridFeatures(cityName);
                    }
                    CityInfo cityInfo = cityHashMap.get(cityName.replace(" ","_").toLowerCase());
                    if(latitude != 0.0 && longitude != 0.0 && cityGridFeatures != null) {
                        int rowIndex = locationUtil.calculateRowIndex(cityGridFeatures.getRowDimension(), latitude, longitude,
                                cityInfo.getLowerLeftLat(), longitude, crimeCellDistanceInKM);
                        int colIndex = locationUtil.calculateColumnIndex(latitude, longitude, latitude, cityInfo.getLowerLeftLng(), crimeCellDistanceInKM);
                        rowIndex = minorTweakOnUserLat(rowIndex, colIndex, latitude, longitude, cityName.replace(" ","_").toLowerCase());
                        if(rowIndex < cityGridFeatures.getRowDimension() && colIndex < cityGridFeatures.getColDimension() && rowIndex >= 0 && colIndex >= 0){
                            if(streetName != null && streetName.length() > 0){
                                CrimeStreetsRatingVO crimeStreets = crimeStreetsRatingDAO.getCrimeStreetsByName(address.getString(CommonJSONKey.STREET_NAME));
                                if (crimeStreets == null) {
                                    myLog.debug("Creating new record for street : " + newRating.getCrimeStreetsID());
                                    // Create new CrimeStreet Record
                                    CrimeStreetsRatingVO newStreet = new CrimeStreetsRatingVO();
                                    SafetyReport safetyReport = getSafetyReport(newRating.getCity(), rowIndex, colIndex);
                                    JSONArray gridAdress = new JSONArray();
                                    JSONObject jObj = new JSONObject();
                                    jObj.put(CommonJSONKey.JSONOBJECT_COLINDEX, colIndex);
                                    jObj.put(CommonJSONKey.JSONOBJECT_ROWINDEX, rowIndex);
                                    gridAdress.put(jObj);

                                    newStreet.setCrimeStreets(newRating.getCrimeStreetsID());
                                    newStreet.setCity(newRating.getCity());
                                    newStreet.setState(newRating.getState());
                                    newStreet.setCountry(newRating.getCountry());
                                    newStreet.setPostcode(newRating.getPostcode());
                                    newStreet.setAvgUserStreetsSafetyRating(newRating.getUserStreetSafetyRating());
                                    newStreet.setCrimeStreetRating(safetyReport.getSafetyRating().toString());
                                    newStreet.setGridAddress(gridAdress.toString());
                                    newStreet.setCreatedDate(new Date());
                                    newStreet.setUpdatedDate(new Date());
                                    crimeStreetsRatingDAO.create(newStreet);
                                    result = true;
                                }else{
                                    myLog.debug("Updating streets info for : " + crimeStreets.getCrimeStreets());
                                    boolean gridExist = false;
                                    JSONArray gridAdress = new JSONArray(crimeStreets.getGridAddress());
                                    for (int idx = 0; idx < gridAdress.length(); idx++) {
                                        JSONObject temp = gridAdress.getJSONObject(idx);
                                        if (temp.getInt(CommonJSONKey.JSONOBJECT_ROWINDEX) == rowIndex) {
                                            if (temp.getInt(CommonJSONKey.JSONOBJECT_COLINDEX) == colIndex) {
                                                gridExist = true;
                                            }
                                        }
                                    }
                                    if (!gridExist) {
                                        JSONObject jObj = new JSONObject();
                                        jObj.put(CommonJSONKey.JSONOBJECT_COLINDEX, colIndex);
                                        jObj.put(CommonJSONKey.JSONOBJECT_ROWINDEX, rowIndex);
                                        gridAdress.put(jObj);
                                        crimeStreets.setGridAddress(gridAdress.toString());
                                        crimeStreetsRatingDAO.update(crimeStreets);
                                        myLog.debug("GridAddress => " + gridAdress.toString());
                                    }
                                    result = true;

                                }

                            }
                        }else{
                            myLog.debug("Out of boundary");
                        }
                    }
                }
            }

        }catch(JSONException ex){
            ex.printStackTrace();
        }catch (CoreException ex){
            ex.getMessageAsJSON();
        }
        return result;
    }

    @Override
    public long getTotalUserRequest() {
        return saferStreetsRequestDAO.findTotalRequests();
    }

    @Override
    public boolean checkSupportCity(String cityName, String state, String country) {
        boolean result = false;
        if (cityName != null) {
            if (cityHashMap.get(cityName) != null) {
                CityInfo cityInfo = cityHashMap.get(cityName);
                if(state!= null && cityInfo != null) {
                    if(cityInfo.getState().equals(state)) {
                        if(cityInfo.getCountry().equals(country)){
                            result =  true;
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public boolean checkSupportCity(double latitude, double longitude) {
        CityInfo cityInfo = cityInfoDAO.findByLocation(latitude, longitude);
        if (cityInfo != null) {
            myLog.debug("Supported city: " + cityInfo);
            return true;
        }

        myLog.debug(String.format("City is not supported: (%f, %f)", latitude, longitude));
        return false;
    }

    @Override
    public JSONObject updateCrimeDataWithCrimeWeight() {
        JSONObject jObj = new JSONObject();
        myLog.debug("updateCrimeDataWithCrimeWeight");
        try {
            List<CrimeDataVO> crimeDataList = crimeDataDAO.findNullCrimeWeight();
            int updateCounter = 0;
            int totalCrimeData = 0;
            if (crimeDataList != null && crimeDataList.size() > 0) {
                for (CrimeDataVO crime : crimeDataList) {
                    if(crime.getCrimeTypeVO() != null) {
                        Double crimeWeight = crimeTypeWithWeight.get(crime.getCrimeTypeVO().getName());
                        if (crimeWeight != null) {
                            crime.setCrimeWeight(crimeWeight);
                            crimeDataDAO.update(crime);
                            myLog.debug(crime.getCrimeTypeVO().getName() + " " + crimeWeight);
                            updateCounter++;
                        }
                    }
                    totalCrimeData ++;
                }
            }
            jObj.put("CrimeCountUpdated", updateCounter);
            jObj.put("TotalCrimeCount",totalCrimeData);
        }catch (JSONException ex){
            ex.printStackTrace();
        }
        return jObj;
    }

    @Override
    public void updateCrimeTypeDetails(CrimeTypeVO crimeType) {
        if(crimeType != null){
            crimeTypeDAO.update(crimeType);
        }
    }

    @Override
    public HashMap<String, CityInfo> getCityHashMap() {
        if(cityHashMap == null){
            init();
        }
        return cityHashMap;
    }

    @Override
    public CityGridFeatures getCityGridFeatures(String cityName) {
        CityGridFeatures cityGridFeatures = null;
        cityGridFeatures = (CityGridFeatures)cacheUtil
                .getCachedObject(CacheKey.CITY_GRID_FEATSURE_CACHE_PREFIX + cityName.replace(" ", "_").toLowerCase(),
                        CityGridFeatures.class);
        return cityGridFeatures;
    }

    @Override
    public CityGridHolder getCityGridHolder(String cityName, Integer rowIndex, Integer colIndex) {
        CityGridHolder cityGridHolder = null;
        cityGridHolder = (CityGridHolder)cacheUtil
                .getCachedObject(CacheKey.CITY_CRIME_GRID + cityName + "_" + rowIndex + "_" + colIndex, CityGridHolder.class);
        return cityGridHolder;
    }

//    @Override
//    public void generateCrimeMatrix(String cityName) {
//        if(cityHashMap.get(cityName.replace(" ","_").toLowerCase()) != null) {
//            new GenerateCrimeMatrix(
//                    cityHashMap.get(cityName.replace(" ","_").toLowerCase()),
//                    getDefaultCrimeDataList(cityName),
//                    getUserStreetsRating(cityName),
//                    crimeCellDistanceInKM,
//                    locationUtil,
//                    cacheUtil
//            );
//        }
//    }


    /**
     * PRIVATE CLASS
     */
    private boolean isCrimeMatrixCache(String cityName){
        return (cacheUtil.getCachedObject(CacheKey.CITY_GRID_FEATSURE_CACHE_PREFIX+cityName,CityGridFeatures.class) !=null ? true : false);
    }

    private CrimeDataVO getLatestCrimeDataVO(String cityName){
        return crimeDataDAO.findLatestCrimeDataFromCity(cityName);
    }

    private List<CrimeDataSimple> sortCrimeList(CityGridHolder cityGridHolder,String cityName){
        CrimeDayTime dayTime = locationUtil.checkCityTimeZone(cityHashMap.get(cityName).getName());
//        myLog.debug("cityName : "+cityName);
        if(cityGridHolder != null) {
            if (cityHashMap.get(cityName).isCrimeDayTimeReport()) {
                if (dayTime.equals(CrimeDayTime.DAYTIME)) {
                    return cityGridHolder.getDaytimeReport();
                }  else {
                    return cityGridHolder.getDarkReport();
                }
            } else {
                return cityGridHolder.getDaytimeReport();
            }
        }else{
            return null;
        }
    }

    @Override
    public List<CrimeTypeVO> getCrimeTypeWithDescription(Date updatedDate) {
        List<CrimeTypeVO> crimeTypeList = crimeTypeDAO.findAll();
        List<CrimeTypeVO> resultList = new ArrayList<CrimeTypeVO>();
        if(updatedDate != null) {
            for (CrimeTypeVO crimeType : crimeTypeList) {
                if(updatedDate.before(crimeType.getUpdatedAt())){
                    resultList.add(crimeType);
                }
            }
            return resultList;
        }else{
            return crimeTypeList;
        }


    }
}
