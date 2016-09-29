package com.secqme.crimedata.rs.v2;

import com.google.gson.JsonArray;
import com.secqme.crimedata.CoreException;
import com.secqme.crimedata.ErrorType;
import com.secqme.crimedata.domain.model.*;
import com.secqme.crimedata.rs.BaseResource;
import com.secqme.crimedata.rs.CommonJSONKey;
import com.secqme.util.cache.CacheKey;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Edmund on 8/4/15.
 */
@Path("/data/v2")
public class CrimeDataResource extends BaseResource{

    private final static Logger myLog = Logger.getLogger(CrimeDataResource.class);
    private static SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public CrimeDataResource() {
    }

    /**
     * Add new SubscriptionRequest
     * @param reqBody
     * @return
     *
     * Changes Made
     * 20150813 - 0838 Edmund
     *      Change API link from subscribenotification to newusersubscriberequest
     */
    @POST
    @Path("/newusersubscriberequest")
    @Produces("application/json")
    public String subscribeNotificationRequest(String reqBody){
        myLog.debug("subscribeNotificationRequest => " + reqBody);
        JSONObject result = new JSONObject();
        JSONObject reqObject = null;
        try {
            reqObject = new JSONObject(validateReqBody(reqBody));
            result.put(CommonJSONKey.JSONOBJECT_USERREQUEST,crimeManager.addNewSubscriptionNotificationRequest(reqObject));
        }catch (JSONException ex){
            ex.printStackTrace();
        }
        return result.toString();
    }

    /**
     * Check for user saferstreets request
     * @param reqBody
     * @return
     *
     * Changes Made
     * 20150813 0840 - Edmund
     *      Change API link from checksupportrequest to checkssrequest
     */
    @POST
    @Path("/checkssrequest")
    @Produces("application/json")
    public String checkUserSSRequest(String reqBody){
        myLog.debug("checkUserSSRequest => " + reqBody);

        JSONObject result = new JSONObject();
        try {
//            initializeValue(reqBody);
            JSONObject requestBody = new JSONObject(validateReqBody(reqBody));
            String email = requestBody.has(CommonJSONKey.USER_EMAIL)?requestBody.getString(CommonJSONKey.USER_EMAIL) : null;
            String userid = requestBody.has(CommonJSONKey.USER_ID)?requestBody.getString(CommonJSONKey.USER_ID) : null;
            String cityName = requestBody.has(CommonJSONKey.STREET_CITY)?requestBody.getString(CommonJSONKey.STREET_CITY) : null;
            String state = requestBody.has(CommonJSONKey.STREET_STATE)?requestBody.getString(CommonJSONKey.STREET_STATE) : null;
            String country = requestBody.has(CommonJSONKey.STREET_COUNTRY)?requestBody.getString(CommonJSONKey.STREET_COUNTRY) : null;
            result.put(CommonJSONKey.JSONOBJECT_USERREQUEST, crimeManager.checkSaferStreetSupportRequest(email,userid,cityName,state,country));
            result.put(CommonJSONKey.JSONOBJECT_REQUESTCOUNT, crimeManager.getTotalUserRequest());
        }catch(JSONException ex){
            ex.printStackTrace();
        }
        return result.toString();
    }

    /*
        Change from saferStreetsSupportRequest to newSSRequest
     */

    /**
     * Add new SaferStreets Support request from user
     * @param reqBody
     * @return
     *
     * Changes Made
     * 20150813 - 0841 Edmund
     *      Change API link from saferstreetsrequest to newssrequest
     */
    @POST
    @Path("/newssrequest")
    @Produces("application/json")
    public String newSSRequest(String reqBody){
        myLog.debug("saferStreetsSupportRequest => " + reqBody);
        JSONObject result = new JSONObject();
        try {
            JSONObject request = new JSONObject(validateReqBody(reqBody));
            result.put(CommonJSONKey.JSONOBJECT_USERREQUEST, crimeManager.addNewSaferStreetSupportRequest(request));
            result.put(CommonJSONKey.JSONOBJECT_REQUESTCOUNT, crimeManager.getTotalUserRequest());
        } catch(JSONException ex) {
            ex.printStackTrace();
        }
        return result.toString();
    }


    /**
     * Get Crime Report Based on user's current location
     * @param reqBody required
     * reqBody format
     *                {
     *                  "city":"Chicago"
     *                  "latitude":41.849079,
     *                  "longitude":-87.643922
     *                }
     * @return result contains
     *      - Coordinates of center point of all grids
     *      - Coordinates of Top Right of all grids
     *      - Crime Report of the grid (If we have data)
     *      - Grid Crime Report Start Date & End Date (Based on database)
     *      - Streets Rating of the grid
     *      - Streets Rating Report Start Date & End Date
     *      - Grid Id based on the rowIndex and colIndex
     *
     * Changes Made
     * 20150813 - 0842 Edmund
     *      Change API Link from saferstreetscrimerating to ssgridreport
     *
     *
     */
    @POST
    @Path("/ssgridreport")
    @Produces("application/json")
    public String ssGridReport(String reqBody){
        myLog.debug("ssGridReport v2 => " + reqBody);
//        JSONObject resultObj = new JSONObject();
        JSONArray gridCrimeReport = new JSONArray();

        Integer userRating = 0;
        try {
            JSONObject requestBody = new JSONObject(validateReqBody(reqBody));
            String cityName = trimCityName(requestBody.has(CommonJSONKey.STREET_CITY)?requestBody.getString(CommonJSONKey.STREET_CITY) : null);
            Double latitude = requestBody.has(CommonJSONKey.JSONOBJECT_LATITUDE)?requestBody.getDouble(CommonJSONKey.JSONOBJECT_LATITUDE) : 0.0;
            Double longitude = requestBody.has(CommonJSONKey.JSONOBJECT_LONGITUDE)?requestBody.getDouble(CommonJSONKey.JSONOBJECT_LONGITUDE) : 0.0;
            myLog.debug(latitude + " " + longitude + " " + cityName);
            if(latitude != 0.00 && longitude != 0.00 && cityName != null) {
                myLog.debug("ssGridReport for " + cityName + " at Coordinate (" + latitude + "," + longitude +")");
                CityInfo cityInfo = crimeManager.getCityHashMap().get(cityName);

                if(cityInfo != null) {
                    CityGridFeatures cityGridFeatures = crimeManager.getCityGridFeatures(cityName);
                    if(cityGridFeatures == null){
                        crimeManager.generateCityCrimeMatrix(requestBody.getString(CommonJSONKey.STREET_CITY),false);
                        cityGridFeatures = crimeManager.getCityGridFeatures(cityName);
                    }
                    if (cityGridFeatures != null) {
                        Double crimeCelldistanceInKM = cityGridFeatures.getDistanceBetweenCells();
//                    myLog.debug(crimeCelldistanceInKM);
                        if ( cityGridFeatures.getRowDimension() > 0 && crimeCelldistanceInKM > 0.0 ) {

                            int rowIndex = locationUtil.calculateRowIndex(cityGridFeatures.getRowDimension(), latitude, longitude,
                                    cityInfo.getLowerLeftLat(), longitude, crimeCelldistanceInKM);
                            int colIndex = locationUtil.calculateColumnIndex(latitude, longitude, latitude, cityInfo.getLowerLeftLng(), crimeCelldistanceInKM);

                            rowIndex = crimeManager.minorTweakOnUserLat(rowIndex, colIndex, latitude, longitude, cityName);

//                        myLog.debug("rowIndex : " + rowIndex + " " + " colIndex : " + colIndex);
                            if (rowIndex < cityGridFeatures.getRowDimension() && colIndex < cityGridFeatures.getColDimension() && rowIndex >= 0 && colIndex >= 0) {
//                                myLog.debug("rowIndex : " + rowIndex);
//                                myLog.debug("colIndex : " + colIndex);
                                for (int row = 1; row < 4; row++) {
                                    for (int col = 1; col < 4; col++) {
                                        if ((rowIndex - (2 - row)) < cityGridFeatures.getRowDimension() && (colIndex - (2 - col)) < cityGridFeatures.getColDimension() &&
                                                (rowIndex - (2 - row)) > 0 && (colIndex - (2 - col)) > 0) {
                                            JSONObject jsonObject = new JSONObject();
                                            JSONObject gridObject = new JSONObject();
                                            JSONObject streetsRating = new JSONObject();
//                                            myLog.debug("row : " + row + " col : " + col);
                                            CityGridHolder cityGridHolder = crimeManager.getCityGridHolder(cityName, (rowIndex - (2 - row)), (colIndex - (2 - col)));
                                            CrimeCoordinate coordinate = cityGridHolder.getCrimeCoordinate();

                                            if (coordinate != null && coordinate.isGenerateDetails()) {

                                                jsonObject.put(CommonJSONKey.JSONOBJECT_CENTERPOINT_LAT, coordinate.getCenterLatitude());
                                                jsonObject.put(CommonJSONKey.JSONOBJECT_CENTERPOINT_LNG, coordinate.getCenterLongitude());
                                                jsonObject.put(CommonJSONKey.JSONOBJECT_BTM_LEFT_LATITUDE, coordinate.getBtmRightLatitude());
                                                jsonObject.put(CommonJSONKey.JSONOBJECT_BTM_LEFT_LONGITUDE, coordinate.getTopLeftLongitude());
                                                jsonObject.put(CommonJSONKey.JSONOBJECT_TOP_RIGHT_LATITUDE, coordinate.getTopLeftLatitude());
                                                jsonObject.put(CommonJSONKey.JSONOBJECT_TOP_RIGHT_LONGITUDE, coordinate.getBtmRightLongitude());

                                            /*
                                                Safety Report
                                             */
                                                CrimeDayTime crimeDayTime = locationUtil.checkCityTimeZone(cityInfo.getCityTimeZone());
//                                                SafetyReport safetyReport = crimeManager.getSafetyReport(cityName,(rowIndex - (2 - row)),(colIndex - (2 - col)));
                                                SafetyReport safetyReport;
                                                myLog.debug(cityInfo.getName() + " crime_day_time_report " + cityInfo.isCrimeDayTimeReport());
                                                if(cityInfo.isCrimeDayTimeReport()) {
                                                     safetyReport = crimeDayTime.equals(CrimeDayTime.DAYTIME) ?
                                                            cityGridHolder.getDaytimeSafetyReport() : cityGridHolder.getDarkSafetyReport();
                                                }else{
                                                    safetyReport = cityGridHolder.getDaytimeSafetyReport();
                                                }
                                                if(safetyReport != null) {
                                                    if (safetyReport.getSafetyRating().toString().equals(SafetyRating.LOW_SAFETY.toString())) {
                                                        jsonObject.put(CommonJSONKey.JSONOBJECT_SAFETY_VALUE, -1);
                                                        jsonObject.put(CommonJSONKey.JSONOBJECT_SAFETYRATING, SafetyRating.LOW_SAFETY.toString().replace("_", " "));
                                                    } else if (safetyReport.getSafetyRating().toString().equals(SafetyRating.MODERATE.toString())) {
                                                        jsonObject.put(CommonJSONKey.JSONOBJECT_SAFETY_VALUE, 0);
                                                        jsonObject.put(CommonJSONKey.JSONOBJECT_SAFETYRATING, SafetyRating.MODERATE.toString().replace("_", " "));
                                                    } else {
                                                        jsonObject.put(CommonJSONKey.JSONOBJECT_SAFETY_VALUE, 1);
                                                        jsonObject.put(CommonJSONKey.JSONOBJECT_SAFETYRATING, SafetyRating.MODERATELY_SAFE.toString().replace("_", " "));
                                                    }
                                                }else{
                                                    jsonObject.put(CommonJSONKey.JSONOBJECT_SAFETY_VALUE, 1);
                                                    jsonObject.put(CommonJSONKey.JSONOBJECT_SAFETYRATING, SafetyRating.MODERATELY_SAFE.toString().replace("_", " "));
                                                }
                                                jsonObject.put(CommonJSONKey.JSONOBJECT_STARTDATE, SQL_DATE_FORMAT.format(cityGridFeatures.getCrimeReportStartDate()));
                                                jsonObject.put(CommonJSONKey.JSONOBJECT_ENDDATE, SQL_DATE_FORMAT.format(cityGridFeatures.getCrimeReportEndDate()));

//                                                myLog.debug(crimeDayTime);
                                                JSONArray crimeList = new JSONArray();

                                                List<CrimeTrend> crimeTrends;
                                                if(cityInfo.isCrimeDayTimeReport()) {
                                                    crimeTrends = crimeDayTime.equals(CrimeDayTime.DAYTIME) ?
                                                            cityGridHolder.getDaytimeCrimeTrend() : cityGridHolder.getDarkCrimeTrend();
                                                }else{
                                                    crimeTrends = cityGridHolder.getDaytimeCrimeTrend();
                                                }
                                                if(crimeTrends!=null) {
                                                    if(crimeTrends.size() > 0) {
                                                        for (CrimeTrend crime : crimeTrends) {
                                                            if (crime.getCrimeWeight() > 0.0) {
                                                                JSONObject obj = new JSONObject();
                                                                obj.put(CommonJSONKey.JSONOBJECT_CRIMETYPE, crime.getCrimeType());
                                                                obj.put(CommonJSONKey.JSONOBJECT_CRIMECOUNT, crime.getCrimeCount());
                                                                crimeList.put(obj);
                                                            }
                                                        }
                                                    }else{
                                                        JSONObject obj = new JSONObject();
                                                        obj.put(CommonJSONKey.JSONOBJECT_CRIMETYPE, "-");
                                                        obj.put(CommonJSONKey.JSONOBJECT_CRIMECOUNT, 0);
                                                        crimeList.put(obj);
                                                    }
                                                }else{
                                                    myLog.debug("crimeTrends is  null");
                                                }
                                                jsonObject.put(CommonJSONKey.JSONOBJECT_CRIMETREND, crimeList);


                                            /*
                                                Streets Rating
                                             */
                                                if (cityGridHolder.getUserStreetSafetyRatingVO() != null) {
                                                    userRating = (int) Math.ceil(calculateUserRating(cityGridHolder.getUserStreetSafetyRatingVO()));
                                                }
                                                if (userRating != 0) {
                                                    streetsRating.put(CommonJSONKey.JSONOBJECT_STARTDATE, SQL_DATE_FORMAT.format(
                                                            cityGridFeatures.getUserRatingStartDate()));
                                                    streetsRating.put(CommonJSONKey.JSONOBJECT_ENDDATE, SQL_DATE_FORMAT.format(
                                                            cityGridFeatures.getUserRatingEndDate()));
                                                }
                                                streetsRating.put(CommonJSONKey.JSONOBJECT_RATING, userRating);
                                                jsonObject.put(CommonJSONKey.JSONOBJECT_USERSTREETRATING, streetsRating);
                                            /*
                                                Grid ID
                                             */
                                                gridObject.put(CommonJSONKey.JSONOBJECT_GRIDID, cityName.replace(" ", "")
                                                        + "CrimeGrid" + Integer.toString(rowIndex - (2 - row)) + Integer.toString(colIndex - (2 - col)));
                                                if (row == 2 && col == 2) {
                                                    gridObject.put(CommonJSONKey.JSONOBJECT_CURRENTGRID, true);
                                                }
                                                jsonObject.put(CommonJSONKey.JSONOBJECT_GRID, gridObject);
                                            /*
                                                Add grid report into JSONArray
                                             */
                                                gridCrimeReport.put(jsonObject);
                                            } else {
//                                            myLog.debug("CrimeCoordinate is null or details is not generated");
                                            }
                                        } else {
//                                        myLog.debug("(" + rowIndex + " - ( 2 - " + row +") : " +(rowIndex - (2 - row)) );
//                                        myLog.debug("(" + colIndex + " - ( 2 - " + col +") : " +(colIndex - (2 - col)) );
                                        }
                                    }
                                }
                            } else {
//                            myLog.debug("rowIndex : " + rowIndex + " colIndex : " + colIndex);
                                throw new CoreException(ErrorType.CITY_OUT_OF_BOUND);
                            }
                        } else {
//                            if (cityGridFeatures == null) {
//                                myLog.debug("Crime Matrix is null");
//                            } else if (crimeMatrix.length < 1) {
//                                myLog.debug("Crime Matrix is empty");
//                            } else if (cityCrimeMatrix == null) {
//                                myLog.debug("CityCrimeMatrix is null");
//                            } else {
//                                myLog.debug("Error in CrimeCellDistanceInKM");
//                            }
                            throw new CoreException(ErrorType.CRIME_DATA_NOT_FOUND);
                        }
                    }else{
                        myLog.debug("city crime matrix is null");
                        throw new CoreException(ErrorType.CRIME_DATA_NOT_FOUND);
                    }
                }else{
                    throw new CoreException(ErrorType.CITY_REQUESTED_NOT_FOUND);
                }
//                resultObj.put(CommonJSONKey.JSONOBJECT_RESULT,gridCrimeReport);
            }
        }catch(JSONException ex){
            ex.printStackTrace();
        }
        return gridCrimeReport.toString();

    }

    /**
     * Return user's current grid crime report
     * @param reqBody
     * @return
     *
     * Changes Made
     * 20150813 - 0844 Edmund
     *      Change API Link from currentcrimereport - gridcrimereport
     */
    @POST
    @Path("/gridcrimereport")
    @Produces("application/json")
    public String currentCrimeReport(String reqBody){
        JSONObject resultObj = new JSONObject();
        JSONArray jsonCrimeList = new JSONArray();
        List<CrimeDataSimple> crimeDataList;
        try {
            JSONObject requestBody = new JSONObject(validateReqBody(reqBody));
            String cityName = trimCityName(requestBody.has(CommonJSONKey.STREET_CITY)?requestBody.getString(CommonJSONKey.STREET_CITY) : null);
            Double latitude = requestBody.has(CommonJSONKey.JSONOBJECT_LATITUDE)?requestBody.getDouble(CommonJSONKey.JSONOBJECT_LATITUDE) : 0.0;
            Double longitude = requestBody.has(CommonJSONKey.JSONOBJECT_LONGITUDE)?requestBody.getDouble(CommonJSONKey.JSONOBJECT_LONGITUDE) : 0.0;
            if (latitude != 0.0 && longitude != 0.0 && cityName != null) {
                CityInfo cityInfo = crimeManager.getCityHashMap().get(cityName);
                if (cityInfo != null) {
                    CityGridFeatures cityGridFeatures = crimeManager.getCityGridFeatures(cityName);
                    if(cityGridFeatures == null){
                        crimeManager.generateCityCrimeMatrix(requestBody.getString(CommonJSONKey.STREET_CITY),false);
                        cityGridFeatures = crimeManager.getCityGridFeatures(cityName);
                    }

                    if(cityGridFeatures != null && cityGridFeatures.getRowDimension() > 0) {
                        Double crimeCellDistanceInKM = cityGridFeatures.getDistanceBetweenCells();
                        int rowIndex = locationUtil.calculateRowIndex(cityGridFeatures.getRowDimension(), latitude, longitude,
                                cityInfo.getLowerLeftLat(), longitude, crimeCellDistanceInKM);
                        int colIndex = locationUtil.calculateColumnIndex(latitude,longitude,latitude,cityInfo.getLowerLeftLng(),crimeCellDistanceInKM);

                        rowIndex = crimeManager.minorTweakOnUserLat(rowIndex,colIndex,latitude,longitude,cityName);
                        myLog.debug("rowIndex : " + rowIndex);
                        myLog.debug("colIndex : " + colIndex);
                        if(rowIndex < cityGridFeatures.getRowDimension() && colIndex < cityGridFeatures.getColDimension() && rowIndex >= 0 && colIndex >= 0){
                            CityGridHolder cityGridHolder = crimeManager.getCityGridHolder(cityName, rowIndex, colIndex);
                            CrimeDayTime crimeDayTime = locationUtil.checkCityTimeZone(cityInfo.getCityTimeZone());
                            if(cityInfo.isCrimeDayTimeReport()) {
                                crimeDataList = crimeDayTime.equals(CrimeDayTime.DAYTIME) ?
                                        cityGridHolder.getDaytimeReport() : cityGridHolder.getDarkReport();
                            }else{
                                crimeDataList = cityGridHolder.getDaytimeReport();
                            }
                            CrimeCoordinate coordinate = cityGridHolder.getCrimeCoordinate();

                            resultObj.put(CommonJSONKey.JSONOBJECT_CENTERPOINT_LAT, coordinate.getCenterLatitude());
                            resultObj.put(CommonJSONKey.JSONOBJECT_CENTERPOINT_LNG, coordinate.getCenterLongitude());
                            resultObj.put(CommonJSONKey.JSONOBJECT_BTM_LEFT_LATITUDE, coordinate.getBtmRightLatitude());
                            resultObj.put(CommonJSONKey.JSONOBJECT_BTM_LEFT_LONGITUDE, coordinate.getTopLeftLongitude());
                            resultObj.put(CommonJSONKey.JSONOBJECT_TOP_RIGHT_LATITUDE, coordinate.getTopLeftLatitude());
                            resultObj.put(CommonJSONKey.JSONOBJECT_TOP_RIGHT_LONGITUDE, coordinate.getBtmRightLongitude());

                            SafetyReport safetyReport;
                            if(cityInfo.isCrimeDayTimeReport()) {
                                safetyReport = crimeDayTime.equals(CrimeDayTime.DAYTIME) ?
                                        cityGridHolder.getDaytimeSafetyReport() :cityGridHolder.getDarkSafetyReport() ;
                            }else{
                                safetyReport = cityGridHolder.getDaytimeSafetyReport();
                            }
                            if(safetyReport != null) {
                                if (safetyReport.getSafetyRating().toString().equals(SafetyRating.LOW_SAFETY.toString())) {
                                    resultObj.put(CommonJSONKey.JSONOBJECT_SAFETY_VALUE, -1);
                                    resultObj.put(CommonJSONKey.JSONOBJECT_SAFETYRATING, SafetyRating.LOW_SAFETY.toString().replace("_", " "));
                                } else if (safetyReport.getSafetyRating().toString().equals(SafetyRating.MODERATE.toString())) {
                                    resultObj.put(CommonJSONKey.JSONOBJECT_SAFETY_VALUE, 0);
                                    resultObj.put(CommonJSONKey.JSONOBJECT_SAFETYRATING, SafetyRating.MODERATE.toString().replace("_", " "));
                                } else {
                                    resultObj.put(CommonJSONKey.JSONOBJECT_SAFETY_VALUE, 1);
                                    resultObj.put(CommonJSONKey.JSONOBJECT_SAFETYRATING, SafetyRating.MODERATELY_SAFE.toString().replace("_", " "));
                                }
                            }else{
                                resultObj.put(CommonJSONKey.JSONOBJECT_SAFETY_VALUE, 1);
                                resultObj.put(CommonJSONKey.JSONOBJECT_SAFETYRATING, SafetyRating.MODERATELY_SAFE.toString().replace("_", " "));
                            }

                            if(crimeDataList != null && crimeDataList.size() > 0){
                                for(CrimeDataSimple crime : crimeDataList){
                                    if(crime.getCrimeWeight() > 0.0) {
                                        JSONObject tempObject = new JSONObject();
                                        tempObject.put(CommonJSONKey.JSONOBJECT_CRIMETYPE, WordUtils.capitalize(crime.getDisplayName() == null ? "" : crime.getDisplayName().toLowerCase()));
                                        tempObject.put(CommonJSONKey.JSONOBJECT_CRIME_DATE, SQL_DATE_FORMAT.format(crime.getOccurredAt()));
                                        tempObject.put(CommonJSONKey.JSONOBJECT_LATITUDE, crime.getLatitude());
                                        tempObject.put(CommonJSONKey.JSONOBJECT_LONGITUDE, crime.getLongitude());
                                        tempObject.put(CommonJSONKey.JSONOBJECT_VIOLENT, crime.isViolent());
                                        jsonCrimeList.put(tempObject);

                                    }
                                }

                            }
                            resultObj.put(CommonJSONKey.JSONOBJECT_STARTDATE, SQL_DATE_FORMAT.format(cityGridFeatures.getCrimeReportStartDate()));
                            resultObj.put(CommonJSONKey.JSONOBJECT_ENDDATE, SQL_DATE_FORMAT.format(cityGridFeatures.getCrimeReportEndDate()));
                            JSONArray crimeList = new JSONArray();
                            List<CrimeTrend> crimeTrends;
                            if(cityInfo.isCrimeDayTimeReport()) {
                                crimeTrends = crimeDayTime.equals(CrimeDayTime.DAYTIME) ?
                                        cityGridHolder.getDaytimeCrimeTrend() : cityGridHolder.getDarkCrimeTrend();
                            }else{
                                crimeTrends = cityGridHolder.getDaytimeCrimeTrend();
                            }
                            if(crimeTrends != null) {
                                for (CrimeTrend crime : crimeTrends) {
                                    JSONObject obj = new JSONObject();
                                    obj.put(CommonJSONKey.JSONOBJECT_CRIMETYPE, crime.getCrimeType());
                                    obj.put(CommonJSONKey.JSONOBJECT_CRIMECOUNT, crime.getCrimeCount());
                                    crimeList.put(obj);
                                }
                            }
                            resultObj.put(CommonJSONKey.JSONOBJECT_CRIMETREND,crimeList);

                            resultObj.put(CommonJSONKey.JSONOBJECT_CRIME_LIST,jsonCrimeList);
                        }
                    }else{
                        myLog.debug("CrimeMatrix is Null / length is less than 1 / cityCrimeMatrix is null");
                        throw new CoreException(ErrorType.CRIME_DATA_NOT_FOUND);
                    }
                }else{
                    throw new CoreException(ErrorType.CITY_REQUESTED_NOT_FOUND);
                }
            }
        }catch(JSONException ex){
            ex.printStackTrace();
        }
        return resultObj.toString();
    }

    /**
     * Add new user saferstreets Rating
     * @param reqBody
     * @return
     *
     * Changes Made
     * 20150813 - 0846 Edmund
     *      Change API link from userstreetsrating to newuserssrating
     */
    @POST
    @Path("/newuserssrating")
    @Produces("application/json")
    public String newStreetRating(String reqBody){
        myLog.debug("newStreetRating => " + reqBody);
        boolean result = false;
        try{
            result = crimeManager.addNewUserStreetSafetyRating(new JSONObject(validateReqBody(reqBody)));

        }catch(JSONException ex){
            ex.printStackTrace();
        }
        if(result) {
            return OK_STATUS;
        }else{
            return NOT_OK_RESULT;
        }
    }

    /**
     * Check for availability of city in Saferstreets
     * @param reqBody
     * @return
     */
    @POST
    @Path("/checksupportcity")
    @Produces("application/json")
    public String checkSupportCity(String reqBody){
        myLog.debug("checkSupportCity => " + reqBody);
        JSONObject resultObj = new JSONObject();
        try{
            JSONObject requestBody = new JSONObject(validateReqBody(reqBody));

            if (requestBody.has(CommonJSONKey.STREET_CITY)) {
                String cityName = trimCityName(requestBody.has(CommonJSONKey.STREET_CITY) ? requestBody.getString(CommonJSONKey.STREET_CITY) : null);
                String state = requestBody.has(CommonJSONKey.STREET_STATE) ? requestBody.getString(CommonJSONKey.STREET_STATE) : null;
                String country = requestBody.has(CommonJSONKey.STREET_COUNTRY) ? requestBody.getString(CommonJSONKey.STREET_COUNTRY) : null;
                resultObj.put(CommonJSONKey.JSONOBJECT_CITYSUPPORT, crimeManager.checkSupportCity(cityName, state, country));
            } else {
                double latitude = requestBody.getDouble(CommonJSONKey.JSONOBJECT_LATITUDE);
                double longitude = requestBody.getDouble(CommonJSONKey.JSONOBJECT_LONGITUDE);
                resultObj.put(CommonJSONKey.JSONOBJECT_CITYSUPPORT, crimeManager.checkSupportCity(latitude, longitude));
            }
        }catch (JSONException ex){
            ex.printStackTrace();
        }
        return resultObj.toString();
    }


    /**
     * Update crime data with crime weight (For internal use)
     * @return
     */
    @POST
    @Path("/updatecrimeweight")
    @Produces("application/json")
    public String updateCrimeWeight(){
        myLog.debug("updatecrimeweight");
        return crimeManager.updateCrimeDataWithCrimeWeight().toString();
    }


    /**
     *  FOR INTERNAL USE - Check Crime Report by Day Time
     * @param reqBody
     * @return
     */
    @POST
    @Path("/checkssgridreport")
    @Produces("application/json")
    public String checkSSGridReport(String reqBody){
        JSONObject result = new JSONObject();
        try {
            JSONObject req = new JSONObject(reqBody);
            if (reqBody != null) {
                String cityName = trimCityName(req.getString(CommonJSONKey.STREET_CITY));
                Double lat = req.getDouble(CommonJSONKey.JSONOBJECT_LATITUDE);
                Double lng = req.getDouble(CommonJSONKey.JSONOBJECT_LONGITUDE);
                myLog.debug(cityName + " lat: " + lat + " lng: " + lng);
                CityGridFeatures cityGridFeatures = crimeManager.getCityGridFeatures(cityName);
                if(cityGridFeatures == null){
                    crimeManager.generateCityCrimeMatrix(req.getString(CommonJSONKey.STREET_CITY),false);
                    cityGridFeatures = crimeManager.getCityGridFeatures(cityName);
                }
                if(cityGridFeatures != null) {
                    Double crimeCelldistanceInKM = cityGridFeatures.getDistanceBetweenCells();

                    CityInfo cityInfo = crimeManager.getCityHashMap().get(cityName);
                    int rowIndex = locationUtil.calculateRowIndex(cityGridFeatures.getRowDimension(), lat, lng,
                            cityInfo.getLowerLeftLat(), lng, crimeCelldistanceInKM);
                    int colIndex = locationUtil.calculateColumnIndex(lat, lng, lat, cityInfo.getLowerLeftLng(), crimeCelldistanceInKM);

                    rowIndex = crimeManager.minorTweakOnUserLat(rowIndex, colIndex, lat, lng, cityName);
                    CityGridHolder cityGridHolder = crimeManager.getCityGridHolder(cityName, rowIndex, colIndex);
                    if (cityGridHolder != null) {
                        for (CrimeDayTime crimeDayTime : CrimeDayTime.values()) {
//                            myLog.debug(crimeDayTime);
                            List<CrimeDataSimple> crimeData = crimeDayTime.equals(CrimeDayTime.DAYTIME) ?
                                    cityGridHolder.getDaytimeReport() : cityGridHolder.getDarkReport();
                                    JSONArray crimeList = getCurrentGridCrimeList(crimeData);
                            SafetyReport safetyReport = crimeDayTime.equals(CrimeDayTime.DAYTIME) ?
                                    cityGridHolder.getDaytimeSafetyReport() : cityGridHolder.getDarkSafetyReport();
                            if(safetyReport != null) {
                                crimeList.put(new JSONObject().put(CommonJSONKey.JSONOBJECT_SAFETYRATING, safetyReport.getSafetyRating().toString()));
                            }else{
                                myLog.debug("safetyReport is null");
                            }
                            result.put(crimeDayTime.toString(), crimeList);
                        }
                        myLog.debug(result.toString());
                    }
                }else{
                    myLog.debug("Null");
                }
            }
        }catch(JSONException ex){
            ex.printStackTrace();
        }
        return result.toString();
    }

    /**
     * Return user's current grid crime report
     * @param reqBody
     * @return
     *
     * Changes Made
     * 20150813 - 0844 Edmund
     *      Change API Link from currentcrimereport - gridcrimereport
     */
    @POST
    @Path("/safewalkreport")
    @Produces("application/json")
    public String safeWalkReport(String reqBody){
        myLog.debug(reqBody);
        JSONObject resultObj = new JSONObject();
        JSONArray routes= new JSONArray();
        int rating = 0;
        try {
            JSONObject jsonObject = new JSONObject(validateReqBody(reqBody));
            String cityName = trimCityName(jsonObject.has(CommonJSONKey.STREET_CITY) ? jsonObject.getString(CommonJSONKey.STREET_CITY) : null);
            JSONArray steps = jsonObject.getJSONArray(CommonJSONKey.JSONOBJECT_STEPS);
            if (cityName != null && steps != null && steps.length() > 0) {
                CityInfo cityInfo = crimeManager.getCityHashMap().get(cityName);
               myLog.debug(cityInfo.getName());
                if (cityInfo != null) {
                    CityGridFeatures cityGridFeatures = crimeManager.getCityGridFeatures(cityName);
                    if(cityGridFeatures == null){
                        crimeManager.generateCityCrimeMatrix(jsonObject.getString(CommonJSONKey.STREET_CITY),false);
                        cityGridFeatures = crimeManager.getCityGridFeatures(cityName);
                    }
                    if(cityGridFeatures != null) {
                        Double crimeCellDistanceInKM = cityGridFeatures.getDistanceBetweenCells();
                        for (int i = 0; i < steps.length(); i++) {
                            double latitude = steps.getJSONObject(i).getDouble(CommonJSONKey.JSONOBJECT_LATITUDE);
                            double longitude = steps.getJSONObject(i).getDouble(CommonJSONKey.JSONOBJECT_LONGITUDE);
                            JSONObject temp = new JSONObject();
                            if ( cityGridFeatures.getRowDimension() > 0 && crimeCellDistanceInKM != 0.0) {
                                int rowIndex = locationUtil.calculateRowIndex(cityGridFeatures.getRowDimension(), latitude, longitude,
                                        cityInfo.getLowerLeftLat(), longitude, crimeCellDistanceInKM);
                                int colIndex = locationUtil.calculateColumnIndex(latitude, longitude, latitude, cityInfo.getLowerLeftLng(), crimeCellDistanceInKM);

                                rowIndex = crimeManager.minorTweakOnUserLat(rowIndex, colIndex, latitude, longitude, cityName);
                                CityGridHolder cityGridHolder = crimeManager.getCityGridHolder(cityName, rowIndex, colIndex);
                                if (rowIndex < cityGridFeatures.getRowDimension() && colIndex < cityGridFeatures.getColDimension() && rowIndex >= 0 && colIndex >= 0) {

                                    // SafetyReport safetyReport = crimeManager.getSafetyReport(cityName, rowIndex, colIndex);
                                    SafetyReport safetyReport;
                                    CrimeDayTime crimeDayTime = locationUtil.checkCityTimeZone(cityInfo.getCityTimeZone());
                                    if(cityInfo.isCrimeDayTimeReport()) {
                                        safetyReport = crimeDayTime.equals(CrimeDayTime.DAYTIME) ?
                                                cityGridHolder.getDaytimeSafetyReport() : cityGridHolder.getDarkSafetyReport();
                                    }else{
                                        safetyReport = cityGridHolder.getDaytimeSafetyReport();
                                    }
                                    if(safetyReport != null) {
                                        if (safetyReport.getSafetyRating().toString().equals(SafetyRating.LOW_SAFETY.toString())) {
                                            temp.put(CommonJSONKey.JSONOBJECT_SAFETY_RATING, -1);
                                            rating += 1;
                                        } else if (safetyReport.getSafetyRating().toString().equals(SafetyRating.MODERATE.toString())) {
                                            temp.put(CommonJSONKey.JSONOBJECT_SAFETY_RATING, 0);
                                        } else {
                                            temp.put(CommonJSONKey.JSONOBJECT_SAFETY_RATING, 1);
                                        }
                                    }else{
                                        temp.put(CommonJSONKey.JSONOBJECT_SAFETY_RATING, 1);
                                    }
                                }
                                temp.put(CommonJSONKey.JSONOBJECT_LATITUDE, latitude);
                                temp.put(CommonJSONKey.JSONOBJECT_LONGITUDE, longitude);
                                routes.put(temp);
                            } else {
                                myLog.debug("CrimeMatrix is Null / length is less than 1 / cityCrimeMatrix is null");
                            }
                        }
                        resultObj.put(CommonJSONKey.JSONOBJECT_STEPS, routes);
                        resultObj.put(CommonJSONKey.JSONOBJECT_RATING, rating);
                    }else{
//                        Return back the same result when city is not supported
                        resultObj.put(CommonJSONKey.JSONOBJECT_STEPS,steps);
                        resultObj.put(CommonJSONKey.JSONOBJECT_RATING,0);
                    }
                }else{
    //                Return back the same result when city is not supported
                    resultObj.put(CommonJSONKey.JSONOBJECT_STEPS,steps);
                    resultObj.put(CommonJSONKey.JSONOBJECT_RATING,0);
                }
            }else{
//                Return back the same result when city is not supported
                resultObj.put(CommonJSONKey.JSONOBJECT_STEPS, steps);
                resultObj.put(CommonJSONKey.JSONOBJECT_RATING,0);
            }
        }
//        catch(CoreException ex){
//            ex.getMessageAsJSON();
//        }
        catch(JSONException ex){
            ex.printStackTrace();
        }
        return resultObj.toString();
    }

    @POST
    @Path("/crimetype")
    @Produces("application/json")
    public String getCrimeType(String reqBody){
        JSONArray result = new JSONArray();
        try {
            if(reqBody != null) {
                JSONObject request = new JSONObject(validateReqBody(reqBody));
                String date = request.has(CommonJSONKey.JSONOBJECT_UPDATE_SINCE) ? request.getString(CommonJSONKey.JSONOBJECT_UPDATE_SINCE) : null;
                SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd");
                SimpleDateFormat resultFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'",Locale.US);
                resultFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date updateDate = format.parse(date);
                List<CrimeTypeVO> crimeTypeList = crimeManager.getCrimeTypeWithDescription(updateDate);
                for (CrimeTypeVO crimeType : crimeTypeList) {
                    JSONObject obj = new JSONObject();
                    obj.put(CommonJSONKey.JSONOBJECT_ID, crimeType.getId());
                    obj.put(CommonJSONKey.JSONOBJECT_NAME, crimeType.getName());
                    obj.put(CommonJSONKey.JSONOBJECT_SUBTYPE_OF, crimeType.getSubtypeOf());
                    obj.put(CommonJSONKey.JSONOBJECT_DESCRIPTION, crimeType.getDescription());
                    obj.put(CommonJSONKey.JSONOBJECT_VIOLENT,crimeType.isViolent());
                    obj.put(CommonJSONKey.JSONOBJECT_UPDATED_AT, resultFormat.format(crimeType.getUpdatedAt()));
                    obj.put(CommonJSONKey.JSONOBJECT_CREATED_AT, resultFormat.format(crimeType.getCreatedAt()));
                    result.put(obj);
                }
            }else{
                return NOT_OK_RESULT;
            }
        }catch (JSONException ex){
            ex.printStackTrace();
        }catch (ParseException ex){
            ex.printStackTrace();
        }
        return result.toString();
    }

}
