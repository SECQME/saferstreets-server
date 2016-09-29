package com.secqme.crimedata.rs;

import com.secqme.crimedata.CoreException;
import com.secqme.crimedata.ErrorType;
import com.secqme.crimedata.domain.model.*;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Edmund on 8/4/15.
 */
@Path("/data")
public class CrimeDataResource extends BaseResource{

    private final static Logger myLog = Logger.getLogger(CrimeDataResource.class);
    private static SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public CrimeDataResource() {
    }

    /**
     * Check Users Subscription Request
     * @param reqBody
     * @return
     */
    @POST
    @Path("/checksubscriptionrequest")
    @Produces("application/json")
    public String checkSubscriptionRequest(String reqBody){
        myLog.debug("checkSubscriptionRequest => " + reqBody);

        JSONObject result = new JSONObject();
        try {
            JSONObject requestBody = new JSONObject(validateReqBody(reqBody));
            String email = requestBody.has(CommonJSONKey.USER_EMAIL)?requestBody.getString(CommonJSONKey.USER_EMAIL) : null;
            String cityName = requestBody.has(CommonJSONKey.STREET_CITY)?requestBody.getString(CommonJSONKey.STREET_CITY) : null;
            String state = requestBody.has(CommonJSONKey.STREET_STATE)?requestBody.getString(CommonJSONKey.STREET_STATE) : null;
            String country = requestBody.has(CommonJSONKey.STREET_COUNTRY)?requestBody.getString(CommonJSONKey.STREET_COUNTRY) : null;
            if(email != null) {
                result.put(CommonJSONKey.JSONOBJECT_USERREQUEST,crimeManager.checkSubscriptionNotificationRequest(email, cityName, state, country));
            }else{
                // Error handling
                myLog.debug("Email is null");
            }

        }catch (JSONException ex){
            ex.printStackTrace();
        }
        return result.toString();
    }

    /**
     * Add new SubscriptionRequest
     * @param reqBody
     * @return
     */
    @POST
    @Path("/subscribenotification")
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
     */
    @POST
    @Path("/checksupportrequest")
    @Produces("application/json")
    public String checkUserSSRequest(String reqBody){
        myLog.debug("checkUserSSRequest => " + reqBody);

        JSONObject result = new JSONObject();
        try {
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
     */
    @POST
    @Path("/saferstreetsrequest")
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
     */
    @POST
    @Path("/saferstreetscrimerating")
    @Produces("application/json")
    public String ssGridReport(String reqBody){
        myLog.debug("ssGridReport => " + reqBody);
        JSONObject resultObj = new JSONObject();
        JSONArray gridCrimeReport = new JSONArray();

        Integer userRating = 0;
        try {
            JSONObject requestBody = new JSONObject(validateReqBody(reqBody));
            String cityName = trimCityName(requestBody.has(CommonJSONKey.STREET_CITY)?requestBody.getString(CommonJSONKey.STREET_CITY) : null);
            Double latitude = requestBody.has(CommonJSONKey.JSONOBJECT_LATITUDE)?requestBody.getDouble(CommonJSONKey.JSONOBJECT_LATITUDE) : 0.0;
            Double longitude = requestBody.has(CommonJSONKey.JSONOBJECT_LONGITUDE)?requestBody.getDouble(CommonJSONKey.JSONOBJECT_LONGITUDE) : 0.0;
            myLog.debug(latitude  + " " + longitude + " " + cityName);
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
                        myLog.debug(crimeCelldistanceInKM);
                        if (cityGridFeatures.getRowDimension()> 0 && crimeCelldistanceInKM > 0.0) {

                            int rowIndex = locationUtil.calculateRowIndex(cityGridFeatures.getRowDimension(), latitude, longitude,
                                    cityInfo.getLowerLeftLat(), longitude, crimeCelldistanceInKM);
                            int colIndex = locationUtil.calculateColumnIndex(latitude, longitude, latitude, cityInfo.getLowerLeftLng(), crimeCelldistanceInKM);

                            rowIndex = crimeManager.minorTweakOnUserLat(rowIndex, colIndex, latitude, longitude, cityName);
//                            myLog.debug("rowIndex : " + rowIndex + " " + " colIndex : " + colIndex);
                            if (rowIndex < cityGridFeatures.getRowDimension()&& colIndex < cityGridFeatures.getColDimension() && rowIndex >= 0 && colIndex >= 0) {
                                for (int row = 1; row < 4; row++) {
                                    for (int col = 1; col < 4; col++) {
                                        if ((rowIndex - (2 - row)) < cityGridFeatures.getRowDimension() && (colIndex - (2 - col)) < cityGridFeatures.getColDimension()&&
                                                (rowIndex - (2 - row)) > 0 && (colIndex - (2 - col)) > 0) {
                                            JSONObject jsonObject = new JSONObject();
                                            JSONObject gridObject = new JSONObject();
                                            JSONObject crimeTrend = new JSONObject();
                                            JSONObject streetsRating = new JSONObject();
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
                                                crimeTrend.put(CommonJSONKey.JSONOBJECT_STARTDATE, SQL_DATE_FORMAT.format(cityGridFeatures.getCrimeReportStartDate()));
                                                crimeTrend.put(CommonJSONKey.JSONOBJECT_ENDDATE, SQL_DATE_FORMAT.format(cityGridFeatures.getCrimeReportEndDate()));
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
                                                // For API VERSION 1
                                                if (crimeList.length() < 4 && crimeList.length() > 0) {
                                                    for (int i = crimeList.length(); i < 4; i++) {
                                                        JSONObject jObj = new JSONObject();
                                                        jObj.put(CommonJSONKey.JSONOBJECT_CRIMETYPE, " - ");
                                                        jObj.put(CommonJSONKey.JSONOBJECT_CRIMECOUNT, " - ");
                                                        crimeList.put(jObj);
                                                    }
                                                }
                                                crimeTrend.put(CommonJSONKey.JSONOBJECT_CRIMETREND, crimeList);
                                                jsonObject.put(CommonJSONKey.JSONOBJECT_CRIMEREPORT, crimeTrend);
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
//                                                myLog.debug("CrimeCoordinate is null or details is not generated");
                                            }
                                        } else {
//                                            myLog.debug("(" + rowIndex + " - ( 2 - " + row + ") : " + (rowIndex - (2 - row)));
//                                            myLog.debug("(" + colIndex + " - ( 2 - " + col + ") : " + (colIndex - (2 - col)));
                                        }
                                    }
                                }
                            } else {
                                myLog.debug("rowIndex : " + rowIndex + " colIndex : " + colIndex);
                                throw new CoreException(ErrorType.CITY_OUT_OF_BOUND);
                            }
                        } else {
//                            if (crimeMatrix == null) {
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
                    }
                }else{
                    throw new CoreException(ErrorType.CITY_REQUESTED_NOT_FOUND);
                }
                resultObj.put(CommonJSONKey.JSONOBJECT_RESULT,gridCrimeReport);
            }
        }catch(CoreException ex){
            ex.printStackTrace();
            return ex.getMessageAsJSON().toString();
        }catch(JSONException ex){
            ex.printStackTrace();
        }
        return resultObj.toString();

    }

    /**
     * Return user's current grid crime report
     * @param reqBody
     * @return
     */
    @POST
    @Path("/currentgridsafetyreport")
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

                    if( cityGridFeatures != null && cityGridFeatures.getRowDimension() > 0) {
                        Double crimeCellDistanceInKM = cityGridFeatures.getDistanceBetweenCells();
                        int rowIndex = locationUtil.calculateRowIndex(cityGridFeatures.getRowDimension(),latitude, longitude,
                                cityInfo.getLowerLeftLat(), longitude,crimeCellDistanceInKM);
                        int colIndex = locationUtil.calculateColumnIndex(latitude,longitude,latitude,cityInfo.getLowerLeftLng(),crimeCellDistanceInKM);

                        rowIndex = crimeManager.minorTweakOnUserLat(rowIndex,colIndex,latitude,longitude,cityName);

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
                                        cityGridHolder.getDaytimeSafetyReport() : cityGridHolder.getDarkSafetyReport();
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
                                        tempObject.put(CommonJSONKey.JSONOBJECT_CRIMETYPE, WordUtils.capitalize(crime.getDisplayName().toLowerCase()));
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
                            JSONArray crimeList = getCurrentGridCrimeList(crimeDataList);

                            resultObj.put(CommonJSONKey.JSONOBJECT_CRIMETREND, crimeList);

                            resultObj.put(CommonJSONKey.JSONOBJECT_CRIME_LIST,jsonCrimeList);
                        }
                    }else{
                        myLog.debug("CrimeMatrix is Null / length is less than 1 / cityCrimeMatrix is null");
                        throw new CoreException(ErrorType.CRIME_DATA_NOT_FOUND);
                    }
                }else {
                    throw new CoreException(ErrorType.CITY_REQUESTED_NOT_FOUND);
                }
            }
        }
        catch (CoreException ex){
            ex.getMessageAsJSON();
        }
        catch(JSONException ex){
            ex.printStackTrace();
        }
        return resultObj.toString();
    }

    /**
     * Add new user saferstreets Rating
     * @param reqBody
     * @return
     */
    @POST
    @Path("/userstreetsrating")
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
            String cityName = trimCityName(requestBody.has(CommonJSONKey.STREET_CITY)?requestBody.getString(CommonJSONKey.STREET_CITY) : null);
            String state = requestBody.has(CommonJSONKey.STREET_STATE)?requestBody.getString(CommonJSONKey.STREET_STATE) : null;
            String country = requestBody.has(CommonJSONKey.STREET_COUNTRY)?requestBody.getString(CommonJSONKey.STREET_COUNTRY) : null;
            resultObj.put(CommonJSONKey.JSONOBJECT_CITYSUPPORT, crimeManager.checkSupportCity(cityName,state,country));
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




}
