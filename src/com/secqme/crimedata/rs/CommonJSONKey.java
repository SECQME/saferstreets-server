package com.secqme.crimedata.rs;

/**
 * Created by Edmund on 1/8/15.
 */
public interface CommonJSONKey {

    public final static String OK_STATUS = "{\"status\":\"ok\"}";


    public final static String USER_LATITUDE = "latitude";
    public final static String USER_LONGITUDE = "longitude";
    public final static String USER_ID = "userid";
    public final static String USER_EMAIL = "email";
    public final static String USER_NAME = "name";
    public final static String STREET_NAME = "streetName";
    public final static String USER_STREET_RATE = "userRating";
    public final static String STREET_CITY = "city";
    public final static String STREET_STATE = "state";
    public final static String STREET_COUNTRY = "country";
    public final static String STREET_POSTCODE = "postcode";
    public final static String STREET_ACCURACY = "accuracy";

    public final static String JSONOBJECT_LOCATION = "location";
    public final static String JSONOBJECT_ADDRESS = "address";


    public final static String JSONOBJECT_SAFETYRATING = "safetyRating";
    public final static String JSONOBJECT_CRIMEREPORT = "crimeReport";
    public final static String JSONOBJECT_USERSTREETRATING = "userStreetRating";
    public final static String JSONOBJECT_STARTDATE = "startDate";
    public final static String JSONOBJECT_ENDDATE = "endDate";
    public final static String JSONOBJECT_CRIMETREND = "crimeTrend";
    public final static String JSONOBJECT_CRIMETYPE = "crimeType";
    public final static String JSONOBJECT_CRIMECOUNT = "crimeCount";
    public final static String JSONOBJECT_CENTERPOINT_LAT = "centerPointLat";
    public final static String JSONOBJECT_CENTERPOINT_LNG = "centerPointLng";
    public final static String JSONOBJECT_GRID = "grid";
    public final static String JSONOBJECT_GRIDID = "gridId";
    public final static String JSONOBJECT_CURRENTGRID = "currentGrid";
    public final static String JSONOBJECT_RESULT = "gridCrimeReportList";
    public final static String JSONOBJECT_CRIME_LIST = "crimeList";
    public final static String JSONOBJECT_RATING = "rating";
    public final static String JSONOBJECT_COLINDEX = "colIndex";
    public final static String JSONOBJECT_ROWINDEX = "rowIndex";
    public final static String JSONOBJECT_REQUESTCOUNT = "requestCount";
    public final static String JSONOBJECT_USERREQUEST = "userRequested";
    public final static String JSONOBJECT_CITYSUPPORT = "citySupport";
    public final static String JSONOBJECT_CRIME_DATE = "crimeDate";
    public final static String JSONOBJECT_LATITUDE = "latitude";
    public final static String JSONOBJECT_LONGITUDE = "longitude";
    public final static String JSONOBJECT_ID = "id";
    public final static String JSONOBJECT_NAME = "name";
    public final static String JSONOBJECT_VIOLENT = "violent";
    public final static String JSONOBJECT_UPDATE_SINCE = "updated_since";
    public final static String JSONOBJECT_DESCRIPTION = "description";
    public final static String JSONOBJECT_UPDATED_AT = "updated_at";
    public final static String JSONOBJECT_CREATED_AT = "created_at";
    public final static String JSONOBJECT_SUBTYPE_OF = "subtype_of";


    public final static String JSONOBJECT_BTM_LEFT_LATITUDE = "bottomLeftLat";
    public final static String JSONOBJECT_BTM_LEFT_LONGITUDE = "bottomLeftLng";
    public final static String JSONOBJECT_TOP_RIGHT_LATITUDE = "topRightLat";
    public final static String JSONOBJECT_TOP_RIGHT_LONGITUDE = "topRightLng";
    public final static String JSONOBJECT_SAFETY_VALUE= "safetyRatingValue";


    // DefaultDataManager
    public final static String JSONOBJECT_CITY_NEIGHBOUR = "neighbour";
    public final static String JSONOBJECT_ODL_REPORT = "oldReport";

    // Error Message
    public final static String JSON_ERROR_MSG_CRIME_DATA = "Not Enough Crime Data";

    // SafeWalk
    public final static String JSONOBJECT_STEPS ="steps";
    public final static String JSONOBJECT_SAFETY_RATING="safety_rating";





}
