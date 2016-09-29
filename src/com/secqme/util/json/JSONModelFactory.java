package com.secqme.util.json;

import com.secqme.crimedata.domain.model.CrimeDataSimple;
import com.secqme.crimedata.domain.model.CrimeDataVO;
import com.secqme.crimedata.domain.model.CrimeHotSpot;
import com.secqme.crimedata.domain.model.SafetyReport;
import org.json.JSONObject;

import java.util.List;

/**
 * User: James Khoo
 * Date: 9/25/14
 * Time: 2:50 PM
 */
public interface JSONModelFactory {
    // Common JSON Key
    public static final String TIME_ZONE_KEY = "timeZone";
    public static final String LATITUDE_KEY = "latitude";
    public static final String LONGITUDE_KEY = "longitude";
    public static final String ACCURACY_KEY = "accuracy";
    public static final String CITY_KEY = "city";
    public static final String STATE_KEY = "state";
    public static final String COUNTRY_KEY = "country";
    public static final String RADIUS_KEY = "radius";


    // Crime Data JSON Key
    public static final String CRIME_CASE_ID_KEY = "crimeCaseID";
    public static final String CRIME_TYPE_KEY = "crimeType";
    public static final String CRIME_ADDRESS_KEY = "crimeAddress";
    public static final String CRIME_DATE_KEY = "crimeDate";
    public static final String CRIME_NOTE_KEY = "note";
    public static final String CRIME_REPORT_DATE_KEY = "crimeReportDate";
    public static final String CRIME_SOURCE_KEY ="crimeSource";
    public static final String CRIME_PICTURE_URL_KEY = "crimePicture";
    public static final String CRIME_VIDEO_URL_KEY = "crimeVideo";
    public static final String CRIME_SRC_TYPE_KEY = "crimeSrcType";

    public static final String CRIME_LIST_KEY = "crimeList";

    // Safety Report
    public static final String CRIME_REPORT_START_DATE_KEY = "crimeStartDate";
    public static final String CRIME_RERPOT_END_DATE_KEY = "crimeReportDate";
    public static final String SAFETY_RATING_KEY = "safetyRating";
    public static final String SAFETY_RATING_DESC_KEY = "safetyRatingDescription";
    public static final String CRIME_COUNT_KEY = "crimeCount";
    public static final String CRIME_AVERAGE_COUNT = "crimeAverage";

    //
    public static final String CRIME_HOTSPOT_ARRAY_KEY = "crimeHotSpotArray";


    public JSONObject convertCrimeDataToJSON(CrimeDataSimple vo);
    public JSONObject convertSafetyRatingToJSON(SafetyReport report);
    public JSONObject convertCrimeHotSportListToJSON(List<CrimeHotSpot> crimeHotSpotList);

}
