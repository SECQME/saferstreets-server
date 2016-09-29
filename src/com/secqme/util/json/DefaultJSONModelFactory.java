package com.secqme.util.json;

import com.secqme.crimedata.domain.model.*;
import com.secqme.util.spring.DefaultSpringUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * User: James Khoo
 * Date: 9/25/14
 * Time: 3:24 PM
 */
public class DefaultJSONModelFactory implements JSONModelFactory {

    public JSONObject convertCrimeDataToJSON(CrimeDataSimple vo) {
        JSONObject crimeObj = null;
        try {
            crimeObj = new JSONObject();
            crimeObj.put(JSONModelFactory.CRIME_CASE_ID_KEY, vo.getCrimeCaseId())
                    .put(JSONModelFactory.CRIME_TYPE_KEY, vo.getCrimeType())
                    .put(JSONModelFactory.CRIME_DATE_KEY, vo.getOccurredAt())
                    .put(JSONModelFactory.LATITUDE_KEY, vo.getLatitude())
                    .put(JSONModelFactory.LONGITUDE_KEY, vo.getLongitude())
                    .put(JSONModelFactory.CRIME_REPORT_DATE_KEY, vo.getOccurredAt());
        } catch (JSONException je) {

        }
        return crimeObj;
    }

    public JSONObject convertCrimeHotSportListToJSON(List<CrimeHotSpot> crimeHotSpotList) {
        JSONObject hotspotJSON = null;
        try {
            hotspotJSON = new JSONObject();
            JSONArray hotspotArray = new JSONArray();
            for (CrimeHotSpot crimeHotSpot : crimeHotSpotList) {
                JSONObject crimeHotSpotObj = new JSONObject();
                crimeHotSpotObj.put(JSONModelFactory.LATITUDE_KEY, crimeHotSpot.getCrimeCoordinate().getLatitude())
                        .put(JSONModelFactory.LONGITUDE_KEY, crimeHotSpot.getCrimeCoordinate().getLongitude())
                        .put(JSONModelFactory.RADIUS_KEY, crimeHotSpot.getRadius())
                        .put(JSONModelFactory.CRIME_COUNT_KEY, crimeHotSpot.getCrimeList().size());
                JSONArray crimeTypeCountArray = new JSONArray();
                for (CrimeTypeCount crimeTypeCount : crimeHotSpot.getCrimeTypeCountList()) {
                    JSONObject crimeTypeObj = new JSONObject();
                    crimeTypeObj.put(JSONModelFactory.CRIME_TYPE_KEY, crimeTypeCount.getCrimeType());
                    crimeTypeObj.put(JSONModelFactory.CRIME_COUNT_KEY, crimeTypeCount.getCrimeCount());
                    crimeTypeCountArray.put(crimeTypeObj);
                }
//                JSONArray crimeDataArray = new JSONArray();
//                for(CrimeDataVO aCrimeData : crimeHotSpot.getCrimeList()) {
//                    crimeDataArray.put(convertCrimeDataToJSON(aCrimeData));
//                }
//                crimeHotSpotObj.put(JSONModelFactory.CRIME_LIST_KEY, crimeDataArray);
                crimeHotSpotObj.put(JSONModelFactory.CRIME_LIST_KEY, crimeTypeCountArray);
                hotspotArray.put(crimeHotSpotObj);
            }
            hotspotJSON.put(JSONModelFactory.CRIME_HOTSPOT_ARRAY_KEY, hotspotArray);
        } catch (JSONException je) {

        }

        return hotspotJSON;
    }

    public JSONObject convertSafetyRatingToJSON(SafetyReport report) {
        JSONObject reportObj = null;
        try {
            Object[] params = new Object[2];
            params[0] = report.getTotalCrimeCount();
            params[1] = report.getAverageCrimeCount();
            String safetyDescription =
                    DefaultSpringUtil.getInstance().getMessage(report.getSafetyRating().getLangCode(), params);
            reportObj = new JSONObject();
            reportObj.put(JSONModelFactory.LATITUDE_KEY, report.getRowIndex())
                    .put(JSONModelFactory.LONGITUDE_KEY, report.getColIndex())
                    .put(JSONModelFactory.RADIUS_KEY, report.getRadius())
                    .put(JSONModelFactory.CRIME_REPORT_START_DATE_KEY, report.getCrimeReportStartDate().getTime())
                    .put(JSONModelFactory.CRIME_RERPOT_END_DATE_KEY, report.getCrimeRerpotEndDate().getTime())
                    .put(JSONModelFactory.SAFETY_RATING_KEY, report.getSafetyRating().name())
                    .put(JSONModelFactory.SAFETY_RATING_DESC_KEY, safetyDescription)
                    .put(JSONModelFactory.CRIME_COUNT_KEY, report.getTotalCrimeCount())
                    .put(JSONModelFactory.CRIME_AVERAGE_COUNT, report.getAverageCrimeCount());


            Map<String, List<CrimeDataSimple>> crimeMap = report.getCrimeDataHashMapByCrimeType();
            if (crimeMap != null && crimeMap.size() > 0) {
                JSONArray crimeArray = new JSONArray();
                for (String aCrimeType : crimeMap.keySet()) {
                    for (CrimeDataSimple crimeDataVO : crimeMap.get(aCrimeType)) {
                        crimeArray.put(this.convertCrimeDataToJSON(crimeDataVO));
                    }
                }
                reportObj.put(JSONModelFactory.CRIME_LIST_KEY, crimeArray);
            }


        } catch (JSONException je) {
        }

        return reportObj;
    }
}
